import { useCallback, useEffect, useState } from 'react';
import { api } from '../api/cliente.js';
import { Alerta, Cargando, EstadoSimple, Vacio, fecha } from '../components/Comunes.jsx';
import { useSesion } from '../sesion.jsx';

/** Historial de correos (ms-notificaciones). Administrador: todos; demas roles: solo los propios. */
export default function Notificaciones({ todas = false }) {
  const { usuario } = useSesion();
  const [lista, setLista] = useState(null);
  const [abierta, setAbierta] = useState(null);
  const [error, setError] = useState('');

  const cargar = useCallback(() => {
    api.notificaciones(todas ? undefined : usuario.login)
      .then(setLista).catch((e) => { setError(e.message); setLista([]); });
  }, [todas, usuario.login]);
  useEffect(() => { cargar(); }, [cargar]);

  return (
    <section>
      <div className="titulo-pagina">
        <div>
          <h1>{todas ? 'Notificaciones enviadas' : 'Mis notificaciones'}</h1>
          <p>Correos que envió el sistema al asignar revisores y al terminar una revisión.</p>
        </div>
        <button className="btn btn-claro" onClick={cargar}>Actualizar</button>
      </div>
      <Alerta onCerrar={() => setError('')}>{error}</Alerta>
      {!lista ? <Cargando /> : lista.length === 0 ? (
        <Vacio titulo="Sin notificaciones todavía" />
      ) : (
        <ul className="lista-notificaciones">
          {lista.map((n) => (
            <li key={n.id} className="tarjeta">
              <button className="notif-cabecera" onClick={() => setAbierta(abierta === n.id ? null : n.id)} aria-expanded={abierta === n.id}>
                <div>
                  <b>{n.asunto}</b>
                  <small className="nota">Para {n.destinatarioLogin}{n.destinatarioEmail ? ` <${n.destinatarioEmail}>` : ''} · {fecha(n.fecha)}</small>
                </div>
                <EstadoSimple valor={n.estado} />
              </button>
              {abierta === n.id && (
                <>
                  {n.detalleError && <Alerta>{n.detalleError}</Alerta>}
                  <pre className="correo">{n.cuerpo}</pre>
                </>
              )}
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
