import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api } from '../api/cliente.js';
import { Alerta, Cargando, letra } from '../components/Comunes.jsx';

const VACIA = {
  competencia: '', tema: '', subtema: '', nivelDificultad: '',
  contexto: '', enunciado: '', opciones: ['', '', '', '', ''], indiceCorrecta: null,
  justificacion: '', bibliografia: '',
};

// Cada campo que puede marcar el backend (CampoPregunta) y el paso del asistente donde esta.
const PASO_DE_CAMPO = {
  COMPETENCIA: 0, TEMA: 0, SUBTEMA: 0, NIVEL_DIFICULTAD: 0,
  CONTEXTO: 1, ENUNCIADO: 1, OPCIONES: 1, RESPUESTA_CORRECTA: 1,
  JUSTIFICACION: 2, BIBLIOGRAFIA: 2,
};
const PASOS = ['Clasificación', 'Pregunta y opciones', 'Justificación'];

/** HU-1 (crear) y HU-3 CA4 (editar en Borrador o Rechazada). Formulario en 3 pasos. */
export default function FormularioPregunta() {
  const { id } = useParams();
  const navegar = useNavigate();
  const [catalogos, setCatalogos] = useState(null);
  const [p, setP] = useState(id ? null : VACIA);
  const [codigo, setCodigo] = useState('');
  const [paso, setPaso] = useState(0);
  const [errores, setErrores] = useState([]);
  const [campos, setCampos] = useState([]);
  const [mensaje, setMensaje] = useState('');
  const [guardando, setGuardando] = useState(false);

  useEffect(() => { api.catalogos().then(setCatalogos).catch((e) => setMensaje(e.message)); }, []);

  useEffect(() => {
    if (!id) return;
    api.pregunta(id).then((q) => {
      if (!q.estado.editable) {
        setMensaje(`La pregunta ${q.codigo} está "${q.estado.nombre}" y no se puede editar.`);
      }
      setCodigo(q.codigo);
      setP({
        competencia: q.competencia || '', tema: q.tema || '', subtema: q.subtema || '',
        nivelDificultad: q.nivelDificultad || '', contexto: q.contexto || '', enunciado: q.enunciado || '',
        opciones: q.opciones.map((o) => o.texto).concat(['', '', '', '', '']).slice(0, 5),
        indiceCorrecta: Math.max(0, q.opciones.findIndex((o) => o.correcta)),
        justificacion: q.justificacion || '', bibliografia: q.bibliografia || '',
      });
    }).catch((e) => setMensaje(e.message));
  }, [id]);

  if (!p) return mensaje ? <Alerta>{mensaje}</Alerta> : <Cargando />;

  const invalido = (campo) => campos.includes(campo);
  const set = (campo) => (e) => {
    setP({ ...p, [campo]: e.target.value });
  };
  const setOpcion = (i, v) => setP({ ...p, opciones: p.opciones.map((o, j) => (j === i ? v : o)) });

  const guardar = async (enviar) => {
    setGuardando(true);
    setErrores([]); setCampos([]); setMensaje('');
    const datos = { ...p, opciones: p.opciones.map((o) => o.trim()) };
    try {
      const guardada = id ? await api.editarPregunta(id, datos) : await api.crearPregunta(datos);
      let aviso = `Pregunta ${guardada.codigo} guardada como "${guardada.estado.nombre}".`;
      if (enviar) {
        await api.enviarARevision(guardada.id);
        aviso = `Pregunta ${guardada.codigo} guardada y enviada a revisión.`;
      }
      navegar('/mis-preguntas', { state: { aviso } });
    } catch (e) {
      setMensaje(e.message);
      setErrores(e.errores || []);
      setCampos(e.campos || []);
      const pasos = (e.campos || []).map((c) => PASO_DE_CAMPO[c]).filter((x) => x !== undefined);
      if (pasos.length) setPaso(Math.min(...pasos));
      setGuardando(false);
    }
  };

  const erroresDelPaso = (n) => (campos || []).some((c) => PASO_DE_CAMPO[c] === n);

  return (
    <section>
      <div className="titulo-pagina">
        <div>
          <h1>{id ? `Editar pregunta ${codigo}` : 'Crear pregunta'}</h1>
          <p>Complete los tres pasos. Los campos marcados con * son obligatorios.</p>
        </div>
      </div>

      <ol className="pasos">
        {PASOS.map((t, i) => (
          <li key={t} className={`${i === paso ? 'activo' : ''} ${i < paso ? 'hecho' : ''} ${erroresDelPaso(i) ? 'con-error' : ''}`}>
            <button type="button" onClick={() => setPaso(i)}>
              <span className="paso-num">{i + 1}</span>{t}
            </button>
          </li>
        ))}
      </ol>

      {mensaje && (
        <Alerta onCerrar={() => setMensaje('')}>
          <strong>{mensaje}</strong>
          {errores.length > 0 && <ul className="lista-errores">{errores.map((er) => <li key={er}>{er}</li>)}</ul>}
        </Alerta>
      )}

      <div className="tarjeta formulario">
        {paso === 0 && (
          <div className="rejilla-2">
            <Campo etiqueta="Competencia *" error={invalido('COMPETENCIA')}>
              <input list="lista-competencias" value={p.competencia} onChange={set('competencia')} placeholder="Ej. Razonamiento Cuantitativo" />
              <datalist id="lista-competencias">{catalogos?.competencias.map((c) => <option key={c} value={c} />)}</datalist>
            </Campo>
            <Campo etiqueta="Nivel de dificultad *" error={invalido('NIVEL_DIFICULTAD')}>
              <select value={p.nivelDificultad} onChange={set('nivelDificultad')}>
                <option value="">Seleccione…</option>
                {catalogos?.niveles.map((n) => <option key={n.valor} value={n.valor}>{n.etiqueta}</option>)}
              </select>
            </Campo>
            <Campo etiqueta="Tema *" error={invalido('TEMA')}>
              <input list="lista-temas" value={p.tema} onChange={set('tema')} placeholder="Ej. Probabilidad" />
              <datalist id="lista-temas">{catalogos?.temas.map((c) => <option key={c} value={c} />)}</datalist>
            </Campo>
            <Campo etiqueta="Subtema *" error={invalido('SUBTEMA')}>
              <input list="lista-subtemas" value={p.subtema} onChange={set('subtema')} placeholder="Ej. Eventos independientes" />
              <datalist id="lista-subtemas">{catalogos?.subtemas.map((c) => <option key={c} value={c} />)}</datalist>
            </Campo>
          </div>
        )}

        {paso === 1 && (
          <>
            <Campo etiqueta="Contexto *" ayuda="Situación o texto base que el estudiante debe leer." error={invalido('CONTEXTO')}>
              <textarea rows={4} value={p.contexto} onChange={set('contexto')} />
            </Campo>
            <Campo etiqueta="Pregunta directa *" error={invalido('ENUNCIADO')}>
              <textarea rows={2} value={p.enunciado} onChange={set('enunciado')} />
            </Campo>
            <fieldset className={`opciones-form ${invalido('OPCIONES') || invalido('RESPUESTA_CORRECTA') ? 'invalido' : ''}`}>
              <legend>Opciones de respuesta * <small>(marque la correcta)</small></legend>
              {p.opciones.map((o, i) => (
                <div key={i} className={`opcion-fila ${p.indiceCorrecta === i ? 'es-correcta' : ''}`}>
                  <label className="opcion-radio" title="Marcar como respuesta correcta">
                    <input type="radio" name="correcta" checked={p.indiceCorrecta === i}
                           onChange={() => setP({ ...p, indiceCorrecta: i })} />
                    <span className="opcion-letra">{letra(i)}</span>
                  </label>
                  <input value={o} onChange={(e) => setOpcion(i, e.target.value)}
                         placeholder={`Opción ${letra(i)}`} aria-label={`Opción ${letra(i)}`} />
                </div>
              ))}
            </fieldset>
          </>
        )}

        {paso === 2 && (
          <>
            <Campo etiqueta="Justificación de la respuesta *" ayuda="Explique por qué la opción correcta lo es y por qué las demás no." error={invalido('JUSTIFICACION')}>
              <textarea rows={5} value={p.justificacion} onChange={set('justificacion')} />
            </Campo>
            <Campo etiqueta="Bibliografía *" error={invalido('BIBLIOGRAFIA')}>
              <textarea rows={3} value={p.bibliografia} onChange={set('bibliografia')} />
            </Campo>
          </>
        )}

        <div className="formulario-acciones">
          <button className="btn btn-claro" type="button" onClick={() => navegar('/mis-preguntas')}>Cancelar</button>
          <div className="separador" />
          {paso > 0 && <button className="btn btn-claro" type="button" onClick={() => setPaso(paso - 1)}>← Anterior</button>}
          {paso < 2 && <button className="btn btn-primario" type="button" onClick={() => setPaso(paso + 1)}>Siguiente →</button>}
          {paso === 2 && (
            <>
              <button className="btn btn-claro" disabled={guardando} onClick={() => guardar(false)}>Guardar borrador</button>
              <button className="btn btn-primario" disabled={guardando} onClick={() => guardar(true)}>
                {guardando ? 'Guardando…' : 'Guardar y enviar a revisión'}
              </button>
            </>
          )}
        </div>
      </div>
    </section>
  );
}

function Campo({ etiqueta, ayuda, error, children }) {
  return (
    <label className={`campo ${error ? 'invalido' : ''}`}>
      <span>{etiqueta}</span>
      {children}
      {ayuda && <small>{ayuda}</small>}
      {error && <small className="texto-error">Revise este campo.</small>}
    </label>
  );
}
