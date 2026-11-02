import { useCallback, useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { api } from '../api/cliente.js';
import { Alerta, Cargando, DetallePregunta, EstadoChip, EstadoSimple, Modal, Vacio, fecha } from '../components/Comunes.jsx';
import { useSesion } from '../sesion.jsx';

const FILTROS_VACIOS = { texto: '', estado: '', competencia: '', tema: '', nivel: '' };
const PUEDE_ENVIAR = ['Borrador', 'Rechazada'];

/** HU-3: lista con filtros y paginacion. Modo "autor" (solo las suyas) o "banco" (administrador, todas). */
export default function ListaPreguntas({ modo = 'autor' }) {
  const { usuario } = useSesion();
  const ubicacion = useLocation();
  const [catalogos, setCatalogos] = useState(null);
  const [filtros, setFiltros] = useState(FILTROS_VACIOS);
  const [aplicados, setAplicados] = useState(FILTROS_VACIOS);
  const [pagina, setPagina] = useState(1);
  const [datos, setDatos] = useState(null);
  const [error, setError] = useState('');
  const [aviso, setAviso] = useState(ubicacion.state?.aviso || '');
  const [detalle, setDetalle] = useState(null);

  useEffect(() => { api.catalogos().then(setCatalogos).catch(() => {}); }, []);

  const cargar = useCallback(() => {
    setError('');
    api.preguntas({ ...aplicados, autor: modo === 'autor' ? usuario.login : undefined, pagina, tamanio: 10 })
      .then(setDatos)
      .catch((e) => { setError(e.message); setDatos({ contenido: [], totalPaginas: 0, totalElementos: 0 }); });
  }, [aplicados, pagina, modo, usuario.login]);

  useEffect(() => { cargar(); }, [cargar]);

  const buscar = (e) => { e.preventDefault(); setPagina(1); setAplicados(filtros); };
  const limpiar = () => { setFiltros(FILTROS_VACIOS); setAplicados(FILTROS_VACIOS); setPagina(1); };
  const cambiar = (campo) => (e) => setFiltros({ ...filtros, [campo]: e.target.value });

  const enviar = async (p) => {
    try {
      await api.enviarARevision(p.id);
      setAviso(`La pregunta ${p.codigo} se envió a revisión. El administrador asignará los revisores.`);
      setDetalle(null);
      cargar();
    } catch (e) {
      setError(e.errores?.length ? `${e.message} ${e.errores.join(' ')}` : e.message);
    }
  };

  return (
    <section>
      <div className="titulo-pagina">
        <div>
          <h1>{modo === 'autor' ? 'Mis preguntas' : 'Banco de preguntas'}</h1>
          <p>{modo === 'autor' ? 'Consulte, edite y envíe a revisión sus preguntas.' : 'Todas las preguntas del banco y su estado.'}</p>
        </div>
        {modo === 'autor' && <Link className="btn btn-primario" to="/preguntas/nueva">+ Crear pregunta</Link>}
      </div>

      <Alerta tipo="exito" onCerrar={() => setAviso('')}>{aviso}</Alerta>
      <Alerta onCerrar={() => setError('')}>{error}</Alerta>

      <form className="filtros tarjeta" onSubmit={buscar}>
        <label className="campo filtro-texto">
          <span>Buscar</span>
          <input value={filtros.texto} onChange={cambiar('texto')} placeholder="Código o texto del enunciado" />
        </label>
        <label className="campo">
          <span>Estado</span>
          <select value={filtros.estado} onChange={cambiar('estado')}>
            <option value="">Todos</option>
            {catalogos?.estados.map((e) => <option key={e.nombre}>{e.nombre}</option>)}
          </select>
        </label>
        <label className="campo">
          <span>Competencia</span>
          <select value={filtros.competencia} onChange={cambiar('competencia')}>
            <option value="">Todas</option>
            {catalogos?.competencias.map((c) => <option key={c}>{c}</option>)}
          </select>
        </label>
        <label className="campo">
          <span>Tema</span>
          <select value={filtros.tema} onChange={cambiar('tema')}>
            <option value="">Todos</option>
            {catalogos?.temas.map((c) => <option key={c}>{c}</option>)}
          </select>
        </label>
        <label className="campo">
          <span>Dificultad</span>
          <select value={filtros.nivel} onChange={cambiar('nivel')}>
            <option value="">Todas</option>
            {catalogos?.niveles.map((n) => <option key={n.valor} value={n.valor}>{n.etiqueta}</option>)}
          </select>
        </label>
        <div className="filtros-acciones">
          <button className="btn btn-primario" type="submit">Buscar</button>
          <button className="btn btn-claro" type="button" onClick={limpiar}>Limpiar</button>
        </div>
      </form>

      {catalogos && (
        <div className="leyenda">
          {catalogos.estados.map((e) => <EstadoChip key={e.nombre} estado={e} />)}
        </div>
      )}

      {!datos ? <Cargando /> : datos.contenido.length === 0 ? (
        <Vacio titulo="No hay preguntas para mostrar">
          {modo === 'autor' ? 'Cree su primera pregunta o cambie los filtros.' : 'Cambie los filtros de búsqueda.'}
        </Vacio>
      ) : (
        <div className="tarjeta tabla-contenedor">
          <table className="tabla">
            <thead>
              <tr>
                <th>Código</th><th>Enunciado</th><th>Competencia</th><th>Dificultad</th>
                {modo === 'banco' && <th>Autor</th>}
                <th>Estado</th><th className="acciones-col">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {datos.contenido.map((p) => (
                <tr key={p.id}>
                  <td className="codigo">{p.codigo}</td>
                  <td><div className="celda-enunciado">{p.enunciado}</div></td>
                  <td>{p.competencia}</td>
                  <td>{p.nivelDificultad}</td>
                  {modo === 'banco' && <td>{p.autorLogin}</td>}
                  <td><EstadoChip estado={p.estado} /></td>
                  <td><div className="acciones">
                    <button className="btn btn-claro btn-sm" onClick={() => setDetalle(p)}>Ver</button>
                    {modo === 'autor' && p.estado.editable && (
                      <Link className="btn btn-claro btn-sm" to={`/preguntas/${p.id}/editar`}>Editar</Link>
                    )}
                    {modo === 'autor' && PUEDE_ENVIAR.includes(p.estado.nombre) && (
                      <button className="btn btn-primario btn-sm" onClick={() => enviar(p)}>Enviar a revisión</button>
                    )}
                  </div></td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="paginacion">
            <span>{datos.totalElementos} pregunta(s) · página {datos.pagina} de {Math.max(datos.totalPaginas, 1)}</span>
            <div>
              <button className="btn btn-claro btn-sm" disabled={pagina <= 1} onClick={() => setPagina(pagina - 1)}>← Anterior</button>
              <button className="btn btn-claro btn-sm" disabled={pagina >= datos.totalPaginas} onClick={() => setPagina(pagina + 1)}>Siguiente →</button>
            </div>
          </div>
        </div>
      )}

      {detalle && (
        <DetalleConRevisiones pregunta={detalle} modo={modo} onCerrar={() => setDetalle(null)} onEnviar={enviar} />
      )}
    </section>
  );
}

function DetalleConRevisiones({ pregunta, modo, onCerrar, onEnviar }) {
  const [revisiones, setRevisiones] = useState(null);
  useEffect(() => {
    api.revisionesDePregunta(pregunta.id).then(setRevisiones).catch(() => setRevisiones([]));
  }, [pregunta.id]);

  return (
    <Modal titulo={`Pregunta ${pregunta.codigo}`} onCerrar={onCerrar} ancho={760}>
      <DetallePregunta p={pregunta} />
      <h4>Revisión</h4>
      {!revisiones ? <Cargando /> : revisiones.length === 0 ? (
        <p className="nota">Aún no tiene revisores asignados.</p>
      ) : (
        <ul className="observaciones">
          {revisiones.map((r) => (
            <li key={r.id}>
              <div className="obs-cabecera">
                <b>{r.revisorLogin}</b>
                <span className="nota">Ronda {r.ronda}</span>
                <EstadoSimple valor={r.estado} />
                <span className="nota">{fecha(r.fechaEvaluacion || r.fechaAsignacion)}</span>
              </div>
              {r.observaciones ? <p>{r.observaciones}</p> : r.estado !== 'ASIGNADA' && <p className="nota">Sin observaciones.</p>}
            </li>
          ))}
        </ul>
      )}
      {modo === 'autor' && (
        <div className="modal-acciones">
          {pregunta.estado.editable && <Link className="btn btn-claro" to={`/preguntas/${pregunta.id}/editar`}>Editar</Link>}
          {PUEDE_ENVIAR.includes(pregunta.estado.nombre) && (
            <button className="btn btn-primario" onClick={() => onEnviar(pregunta)}>Enviar a revisión</button>
          )}
        </div>
      )}
    </Modal>
  );
}
