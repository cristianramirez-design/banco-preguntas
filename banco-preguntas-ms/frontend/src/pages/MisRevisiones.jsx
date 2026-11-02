import { useCallback, useEffect, useState } from 'react';
import { api } from '../api/cliente.js';
import { Alerta, Cargando, DetallePregunta, EstadoSimple, Vacio, fecha } from '../components/Comunes.jsx';

/** HU-5: el revisor ve sus preguntas asignadas, escribe observaciones y aprueba o rechaza. */
export default function MisRevisiones() {
  const [revisiones, setRevisiones] = useState(null);
  const [seleccion, setSeleccion] = useState(null);
  const [error, setError] = useState('');
  const [aviso, setAviso] = useState('');

  const cargar = useCallback(() => {
    api.misRevisiones().then(setRevisiones).catch((e) => { setError(e.message); setRevisiones([]); });
  }, []);
  useEffect(() => { cargar(); }, [cargar]);

  const porEvaluar = revisiones?.filter((r) => r.estado === 'ASIGNADA').length || 0;

  return (
    <section>
      <div className="titulo-pagina">
        <div>
          <h1>Mis revisiones</h1>
          <p>{revisiones ? `${porEvaluar} pregunta(s) por evaluar.` : 'Preguntas que le asignó el administrador.'}</p>
        </div>
        <button className="btn btn-claro" onClick={cargar}>Actualizar</button>
      </div>
      <Alerta tipo="exito" onCerrar={() => setAviso('')}>{aviso}</Alerta>
      <Alerta onCerrar={() => setError('')}>{error}</Alerta>

      {!revisiones ? <Cargando /> : revisiones.length === 0 ? (
        <Vacio titulo="No tiene preguntas asignadas">Cuando el administrador le asigne una pregunta, le llegará un correo y aparecerá aquí.</Vacio>
      ) : (
        <div className="dos-columnas">
          <ul className="lista-tarjetas">
            {revisiones.map((r) => (
              <li key={r.id}>
                <button className={`tarjeta-item ${seleccion?.id === r.id ? 'activa' : ''}`} onClick={() => setSeleccion(r)}>
                  <div className="obs-cabecera">
                    <b className="codigo">{r.codigoPregunta}</b>
                    <EstadoSimple valor={r.estado} />
                  </div>
                  <span className="celda-enunciado">{r.enunciado}</span>
                  <small className="nota">Asignada {fecha(r.fechaAsignacion)}{r.ronda > 1 ? ` · ronda ${r.ronda}` : ''}</small>
                </button>
              </li>
            ))}
          </ul>
          <div>
            {seleccion ? (
              <PanelEvaluacion key={seleccion.id} revision={seleccion}
                               onEvaluada={(r, texto) => { setAviso(texto); setSeleccion(r); cargar(); }} />
            ) : (
              <Vacio titulo="Seleccione una pregunta">Elija una pregunta de la lista para revisarla.</Vacio>
            )}
          </div>
        </div>
      )}
    </section>
  );
}

function PanelEvaluacion({ revision, onEvaluada }) {
  const [pregunta, setPregunta] = useState(null);
  const [observaciones, setObservaciones] = useState(revision.observaciones || '');
  const [error, setError] = useState('');
  const [enviando, setEnviando] = useState('');
  const evaluada = revision.estado !== 'ASIGNADA';

  useEffect(() => {
    api.pregunta(revision.preguntaId).then(setPregunta).catch((e) => setError(e.message));
  }, [revision.preguntaId]);

  const evaluar = async (decision) => {
    setEnviando(decision); setError('');
    try {
      const r = await api.evaluar(revision.id, decision, observaciones);
      onEvaluada(r, `Pregunta ${revision.codigoPregunta} ${decision === 'APROBAR' ? 'aprobada' : 'rechazada'}. Se notificó a los involucrados cuando todos los revisores terminen.`);
    } catch (e) {
      setError(e.message);
    } finally {
      setEnviando('');
    }
  };

  return (
    <div className="tarjeta">
      {!pregunta ? (error ? <Alerta>{error}</Alerta> : <Cargando />) : <DetallePregunta p={pregunta} />}
      <h4>Sus observaciones</h4>
      {evaluada ? (
        <div className="resultado-evaluacion">
          <EstadoSimple valor={revision.estado} />
          <span className="nota">{fecha(revision.fechaEvaluacion)}</span>
          <p>{revision.observaciones || 'Sin observaciones.'}</p>
        </div>
      ) : (
        <>
          <textarea className={`area-observaciones ${error && !observaciones.trim() ? 'invalido' : ''}`} rows={5}
                    value={observaciones} onChange={(e) => setObservaciones(e.target.value)}
                    placeholder="Escriba sus observaciones para el autor (obligatorias si rechaza)." />
          <Alerta>{error}</Alerta>
          <div className="modal-acciones">
            <button className="btn btn-peligro" disabled={!!enviando} onClick={() => evaluar('RECHAZAR')}>
              {enviando === 'RECHAZAR' ? 'Enviando…' : 'Rechazar'}
            </button>
            <button className="btn btn-exito" disabled={!!enviando} onClick={() => evaluar('APROBAR')}>
              {enviando === 'APROBAR' ? 'Enviando…' : 'Aprobar'}
            </button>
          </div>
        </>
      )}
    </div>
  );
}
