import { useEffect } from 'react';

export function EstadoChip({ estado }) {
  if (!estado) return null;
  const color = estado.color || '#64748b';
  return (
    <span className="chip" style={{ color, background: `${color}1a`, borderColor: `${color}55` }}>
      <span className="chip-punto" style={{ background: color }} />
      {estado.nombre}
    </span>
  );
}

const COLORES_REVISION = { ASIGNADA: '#b45309', APROBADA: '#15803d', RECHAZADA: '#b91c1c', ENVIADA: '#15803d', FALLIDA: '#b91c1c' };
const TEXTOS_REVISION = { ASIGNADA: 'Por evaluar', APROBADA: 'Aprobada', RECHAZADA: 'Rechazada', ENVIADA: 'Enviado', FALLIDA: 'Fallido' };

export function EstadoSimple({ valor }) {
  return <EstadoChip estado={{ nombre: TEXTOS_REVISION[valor] || valor, color: COLORES_REVISION[valor] }} />;
}

export function Alerta({ tipo = 'error', children, onCerrar }) {
  if (!children) return null;
  return (
    <div className={`alerta alerta-${tipo}`} role={tipo === 'error' ? 'alert' : 'status'}>
      <div>{children}</div>
      {onCerrar && <button className="btn-icono" onClick={onCerrar} aria-label="Cerrar">×</button>}
    </div>
  );
}

export function Modal({ titulo, onCerrar, children, ancho = 640 }) {
  useEffect(() => {
    const esc = (e) => e.key === 'Escape' && onCerrar();
    window.addEventListener('keydown', esc);
    return () => window.removeEventListener('keydown', esc);
  }, [onCerrar]);
  return (
    <div className="modal-fondo" onMouseDown={(e) => e.target === e.currentTarget && onCerrar()}>
      <div className="modal" style={{ maxWidth: ancho }} role="dialog" aria-modal="true" aria-label={titulo}>
        <div className="modal-cabecera">
          <h2>{titulo}</h2>
          <button className="btn-icono" onClick={onCerrar} aria-label="Cerrar">×</button>
        </div>
        <div className="modal-cuerpo">{children}</div>
      </div>
    </div>
  );
}

export function Cargando({ texto = 'Cargando…' }) {
  return <div className="cargando"><span className="spinner" />{texto}</div>;
}

export function Vacio({ titulo, children }) {
  return (
    <div className="vacio">
      <strong>{titulo}</strong>
      {children && <p>{children}</p>}
    </div>
  );
}

export const fecha = (iso) => {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleString('es-CO', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
};

const LETRAS = ['A', 'B', 'C', 'D', 'E'];
export const letra = (i) => LETRAS[i] || String(i + 1);

/** Vista de solo lectura de una pregunta completa (la usan autor, revisor y administrador). */
export function DetallePregunta({ p }) {
  return (
    <div className="detalle">
      <div className="detalle-meta">
        <span><b>{p.codigo}</b></span>
        <EstadoChip estado={p.estado} />
        <span>{p.competencia}</span>
        <span>{p.tema}{p.subtema ? ` · ${p.subtema}` : ''}</span>
        <span>Dificultad: {p.nivelDificultad}</span>
      </div>
      {p.contexto && (<><h4>Contexto</h4><p className="texto-largo">{p.contexto}</p></>)}
      <h4>Pregunta</h4>
      <p className="texto-largo enunciado">{p.enunciado}</p>
      <h4>Opciones</h4>
      <ol className="opciones">
        {p.opciones.map((o, i) => (
          <li key={i} className={o.correcta ? 'correcta' : ''}>
            <span className="opcion-letra">{letra(i)}</span>
            <span>{o.texto}</span>
            {o.correcta && <span className="marca-correcta">Respuesta correcta</span>}
          </li>
        ))}
      </ol>
      <h4>Justificación</h4>
      <p className="texto-largo">{p.justificacion}</p>
      <h4>Bibliografía</h4>
      <p className="texto-largo">{p.bibliografia}</p>
    </div>
  );
}
