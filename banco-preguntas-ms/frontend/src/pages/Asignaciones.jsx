import { useCallback, useEffect, useState } from 'react';
import { api } from '../api/cliente.js';
import { Alerta, Cargando, DetallePregunta, Modal, Vacio, fecha } from '../components/Comunes.jsx';
import { useSesion } from '../sesion.jsx';

const MAXIMO = 3;

/** HU-4: el administrador ve las preguntas enviadas y asigna de 1 a 3 revisores. */
export default function Asignaciones() {
  const { usuario } = useSesion();
  const [pendientes, setPendientes] = useState(null);
  const [revisores, setRevisores] = useState([]);
  const [error, setError] = useState('');
  const [aviso, setAviso] = useState('');
  const [actual, setActual] = useState(null);

  const cargar = useCallback(() => {
    api.pendientes().then(setPendientes).catch((e) => { setError(e.message); setPendientes([]); });
  }, []);

  useEffect(() => {
    cargar();
    api.usuarios('REVISOR').then(setRevisores).catch(() => {});
  }, [cargar]);

  const terminado = (texto) => { setActual(null); setAviso(texto); cargar(); };

  return (
    <section>
      <div className="titulo-pagina">
        <div>
          <h1>Asignar revisores</h1>
          <p>Preguntas enviadas a revisión que todavía no tienen revisores.</p>
        </div>
        <button className="btn btn-claro" onClick={cargar}>Actualizar</button>
      </div>
      <Alerta tipo="exito" onCerrar={() => setAviso('')}>{aviso}</Alerta>
      <Alerta onCerrar={() => setError('')}>{error}</Alerta>

      {!pendientes ? <Cargando /> : pendientes.length === 0 ? (
        <Vacio titulo="No hay preguntas pendientes">Cuando un autor envíe una pregunta a revisión aparecerá aquí.</Vacio>
      ) : (
        <div className="tarjeta tabla-contenedor">
          <table className="tabla">
            <thead>
              <tr><th>Código</th><th>Enunciado</th><th>Competencia</th><th>Autor</th><th>Enviada</th><th>Ronda</th><th /></tr>
            </thead>
            <tbody>
              {pendientes.map((p) => (
                <tr key={p.preguntaId}>
                  <td className="codigo">{p.codigo}</td>
                  <td><div className="celda-enunciado">{p.enunciado}</div></td>
                  <td>{p.competencia}</td>
                  <td>{p.autorLogin}</td>
                  <td>{fecha(p.fechaEnvio)}</td>
                  <td>{p.ronda > 1 ? <span className="chip-ronda">Reenviada · {p.ronda}</span> : 1}</td>
                  <td><div className="acciones">
                    <button className="btn btn-primario btn-sm" onClick={() => setActual(p)}>Asignar revisores</button>
                  </div></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {actual && (
        <ModalAsignar pendiente={actual} revisores={revisores} administrador={usuario.login}
                      onCerrar={() => setActual(null)} onListo={terminado} />
      )}
    </section>
  );
}

function ModalAsignar({ pendiente, revisores, administrador, onCerrar, onListo }) {
  const [seleccion, setSeleccion] = useState([]);
  const [pregunta, setPregunta] = useState(null);
  const [error, setError] = useState('');
  const [enviando, setEnviando] = useState(false);
  const [verPregunta, setVerPregunta] = useState(false);

  useEffect(() => { api.pregunta(pendiente.preguntaId).then(setPregunta).catch(() => {}); }, [pendiente.preguntaId]);

  const candidatos = revisores.filter((r) => r.login !== pendiente.autorLogin);
  const alternar = (login) => {
    setError('');
    setSeleccion((s) => (s.includes(login) ? s.filter((x) => x !== login) : [...s, login]));
  };

  // La regla de 1 a 3 la valida el backend (ms-revision); aqui solo se muestra su mensaje.
  const asignar = async () => {
    setEnviando(true);
    try {
      await api.asignar(pendiente.preguntaId, seleccion, administrador);
      onListo(`Se asignaron ${seleccion.length} revisor(es) a la pregunta ${pendiente.codigo}. Se enviaron los correos de aviso.`);
    } catch (e) {
      setError(e.message);
      setEnviando(false);
    }
  };

  return (
    <Modal titulo={`Asignar revisores · ${pendiente.codigo}`} onCerrar={onCerrar}>
      <p className="celda-enunciado-completo">{pendiente.enunciado}</p>
      <p className="nota">Autor: {pendiente.autorLogin} · {pendiente.competencia} · {pendiente.tema}
        {pregunta && <> · <button className="link" onClick={() => setVerPregunta(!verPregunta)}>{verPregunta ? 'Ocultar' : 'Ver'} pregunta completa</button></>}
      </p>
      {verPregunta && pregunta && <div className="tarjeta interior"><DetallePregunta p={pregunta} /></div>}

      <div className="contador">
        <span>Seleccione de 1 a {MAXIMO} revisores</span>
        <span className={seleccion.length > MAXIMO ? 'excedido' : ''}>{seleccion.length} / {MAXIMO}</span>
      </div>
      <ul className="lista-revisores">
        {candidatos.map((r) => (
          <li key={r.login}>
            <label className={seleccion.includes(r.login) ? 'marcado' : ''}>
              <input type="checkbox" checked={seleccion.includes(r.login)} onChange={() => alternar(r.login)} />
              <span><b>{r.nombre}</b><small>{r.login} · {r.email}</small></span>
            </label>
          </li>
        ))}
      </ul>
      <Alerta>{error}</Alerta>
      <div className="modal-acciones">
        <button className="btn btn-claro" onClick={onCerrar}>Cancelar</button>
        <button className="btn btn-primario" disabled={enviando} onClick={asignar}>
          {enviando ? 'Asignando…' : 'Asignar'}
        </button>
      </div>
    </Modal>
  );
}
