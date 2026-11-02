import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/cliente.js';
import { Alerta, Cargando } from '../components/Comunes.jsx';
import { etiquetaRol, inicioDeRol, useSesion } from '../sesion.jsx';

const ORDEN = { AUTOR: 0, REVISOR: 1, ADMINISTRADOR: 2 };

export default function Login() {
  const [usuarios, setUsuarios] = useState(null);
  const [error, setError] = useState('');
  const [entrando, setEntrando] = useState('');
  const { iniciar } = useSesion();
  const navegar = useNavigate();

  useEffect(() => {
    api.usuarios()
      .then((u) => setUsuarios([...u].sort((a, b) => ORDEN[a.roles[0]] - ORDEN[b.roles[0]] || a.login.localeCompare(b.login))))
      .catch((e) => setError(e.message));
  }, []);

  const entrar = async (u, rol) => {
    setEntrando(u.login + rol);
    try {
      const usuario = await api.login(u.login);
      iniciar(usuario, rol);
      navegar(inicioDeRol(rol));
    } catch (e) {
      setError(e.message);
      setEntrando('');
    }
  };

  return (
    <div className="login">
      <div className="login-panel">
        <div className="login-cabecera">
          <span className="logo grande">SP</span>
          <h1>Banco de Preguntas Saber Pro</h1>
          <p>Elija con qué usuario y rol desea ingresar.</p>
        </div>
        <Alerta onCerrar={() => setError('')}>{error}</Alerta>
        {!usuarios && !error && <Cargando texto="Consultando usuarios…" />}
        <div className="login-usuarios">
          {usuarios?.map((u) => (
            <div key={u.login} className="login-usuario">
              <div>
                <strong>{u.nombre}</strong>
                <small>{u.login} · {u.email}</small>
              </div>
              <div className="login-roles">
                {u.roles.map((r) => (
                  <button key={r} className={`btn rol-btn rol-${r.toLowerCase()}`} disabled={!!entrando}
                          onClick={() => entrar(u, r)}>
                    {entrando === u.login + r ? 'Entrando…' : `Entrar como ${etiquetaRol(r)}`}
                  </button>
                ))}
              </div>
            </div>
          ))}
        </div>
        <p className="nota">Inicio de sesión simplificado para el prototipo (sin contraseña).</p>
      </div>
    </div>
  );
}
