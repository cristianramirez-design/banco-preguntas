import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/cliente.js';
import { guardarSesion, iniciarSesionKeycloak, rolesDelToken } from '../api/auth.js';
import { Alerta } from '../components/Comunes.jsx';
import { etiquetaRol, inicioDeRol, useSesion } from '../sesion.jsx';

// Usuarios creados en Keycloak (infra/keycloak/realm-banco-preguntas.json). Contrasena de prueba: 1234
const DE_PRUEBA = [
  { login: 'autor1', nombre: 'Ana María Rojas', roles: 'Autor' },
  { login: 'autor2', nombre: 'Carlos Andrés Díaz', roles: 'Autor y Revisor' },
  { login: 'revisor1', nombre: 'Luisa Fernanda Muñoz', roles: 'Revisor' },
  { login: 'revisor2', nombre: 'Jorge Enrique Vallejo', roles: 'Revisor' },
  { login: 'revisor3', nombre: 'Diana Patricia Gómez', roles: 'Revisor' },
  { login: 'admin', nombre: 'Coordinación Saber Pro', roles: 'Administrador' },
];

export default function Login() {
  const [usuario, setUsuario] = useState('');
  const [clave, setClave] = useState('');
  const [error, setError] = useState('');
  const [entrando, setEntrando] = useState(false);
  const [eleccion, setEleccion] = useState(null); // usuario con varios roles: elige con cual trabajar
  const { iniciar } = useSesion();
  const navegar = useNavigate();

  const terminar = (tokens, perfil, roles, rol) => {
    iniciar(tokens, perfil, roles, rol);
    navegar(inicioDeRol(rol));
  };

  const entrar = async (e) => {
    e.preventDefault();
    setError('');
    setEntrando(true);
    try {
      // 1) Keycloak valida usuario y contrasena y entrega el token JWT
      const tokens = await iniciarSesionKeycloak(usuario.trim(), clave);
      const roles = rolesDelToken(tokens.access);
      if (roles.length === 0) throw new Error('El usuario no tiene ningun rol del sistema asignado en Keycloak.');
      // 2) Con el token, el gateway deja consultar el perfil (nombre y correo) en ms-usuarios
      guardarSesion({ tokens });
      const perfil = await api.perfil();
      if (roles.length === 1) terminar(tokens, perfil, roles, roles[0]);
      else setEleccion({ tokens, perfil, roles });
    } catch (err) {
      guardarSesion(null);
      setError(err.message);
    } finally {
      setEntrando(false);
    }
  };

  return (
    <div className="login">
      <div className="login-panel login-angosto">
        <div className="login-cabecera">
          <span className="logo grande">SP</span>
          <h1>Banco de Preguntas Saber Pro</h1>
          <p>Inicie sesión con su usuario institucional.</p>
        </div>
        <Alerta onCerrar={() => setError('')}>{error}</Alerta>

        {eleccion ? (
          <div className="login-eleccion">
            <p>Hola <strong>{eleccion.perfil.nombre}</strong>, ¿con qué rol desea trabajar?</p>
            <div className="login-roles">
              {eleccion.roles.map((r) => (
                <button key={r} className={`btn rol-btn rol-${r.toLowerCase()}`}
                        onClick={() => terminar(eleccion.tokens, eleccion.perfil, eleccion.roles, r)}>
                  Entrar como {etiquetaRol(r)}
                </button>
              ))}
            </div>
          </div>
        ) : (
          <form className="formulario login-form" onSubmit={entrar}>
            <label className="campo">
              <span>Usuario</span>
              <input value={usuario} onChange={(e) => setUsuario(e.target.value)} autoComplete="username"
                     placeholder="p. ej. autor1" autoFocus required />
            </label>
            <label className="campo">
              <span>Contraseña</span>
              <input type="password" value={clave} onChange={(e) => setClave(e.target.value)}
                     autoComplete="current-password" required />
            </label>
            <button className="btn btn-primario" disabled={entrando}>
              {entrando ? 'Verificando…' : 'Iniciar sesión'}
            </button>
          </form>
        )}

        <details className="login-prueba">
          <summary>Usuarios de prueba (contraseña 1234)</summary>
          <div className="login-usuarios">
            {DE_PRUEBA.map((u) => (
              <button key={u.login} type="button" className="login-usuario"
                      onClick={() => { setUsuario(u.login); setClave('1234'); setEleccion(null); }}>
                <span><strong>{u.nombre}</strong><small>{u.login}</small></span>
                <small>{u.roles}</small>
              </button>
            ))}
          </div>
        </details>
        <p className="nota">Autenticación con Keycloak · token JWT validado por el API Gateway</p>
      </div>
    </div>
  );
}
