import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { etiquetaRol, inicioDeRol, useSesion } from '../sesion.jsx';

const MENU = {
  AUTOR: [
    { a: '/mis-preguntas', t: 'Mis preguntas' },
    { a: '/preguntas/nueva', t: 'Crear pregunta' },
    { a: '/mis-notificaciones', t: 'Mis notificaciones' },
  ],
  REVISOR: [
    { a: '/mis-revisiones', t: 'Mis revisiones' },
    { a: '/mis-notificaciones', t: 'Mis notificaciones' },
  ],
  ADMINISTRADOR: [
    { a: '/asignaciones', t: 'Asignar revisores' },
    { a: '/banco', t: 'Banco de preguntas' },
    { a: '/notificaciones', t: 'Notificaciones enviadas' },
  ],
};

export default function Layout() {
  const { usuario, rol, salir, cambiarRol } = useSesion();
  const navegar = useNavigate();

  const cambiarUsuario = () => { salir(); navegar('/login'); };
  const otroRol = (r) => { cambiarRol(r); navegar(inicioDeRol(r)); };

  return (
    <div className="app">
      <header className="barra">
        <div className="barra-marca">
          <span className="logo">SP</span>
          <div>
            <strong>Banco de Preguntas Saber Pro</strong>
            <small>Universidad del Cauca</small>
          </div>
        </div>
        <div className="barra-usuario">
          <span className={`rol-badge rol-${rol?.toLowerCase()}`} title="Rol con el que está trabajando">
            {etiquetaRol(rol)}
          </span>
          <span className="barra-nombre">{usuario?.nombre}</span>
          {usuario?.roles?.length > 1 && (
            <select aria-label="Cambiar de rol" value={rol} onChange={(e) => otroRol(e.target.value)}>
              {usuario.roles.map((r) => <option key={r} value={r}>Trabajar como {etiquetaRol(r)}</option>)}
            </select>
          )}
          <button className="btn btn-claro" onClick={cambiarUsuario}>Cambiar usuario</button>
        </div>
      </header>
      <nav className="menu">
        {(MENU[rol] || []).map((m) => (
          <NavLink key={m.a} to={m.a} end>{m.t}</NavLink>
        ))}
      </nav>
      <main className="contenido">
        <Outlet />
      </main>
    </div>
  );
}
