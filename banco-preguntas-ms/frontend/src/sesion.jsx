import { createContext, useContext, useEffect, useState } from 'react';
import { cerrarSesionKeycloak, guardarSesion, leerSesion } from './api/auth.js';

const SesionContexto = createContext(null);

const ETIQUETAS = { AUTOR: 'Autor', REVISOR: 'Revisor', ADMINISTRADOR: 'Administrador' };
export const etiquetaRol = (rol) => ETIQUETAS[rol] || rol;

/**
 * Sesion del usuario: tokens de Keycloak, perfil (nombre y correo de ms-usuarios), roles que vienen
 * en el token y el rol con el que esta trabajando ahora.
 */
export function ProveedorSesion({ children }) {
  const [sesion, setSesion] = useState(leerSesion);

  // Si el token se renueva o vence (api/auth.js), la pantalla se entera.
  useEffect(() => {
    const actualizar = () => setSesion(leerSesion());
    window.addEventListener('sesion-cambio', actualizar);
    return () => window.removeEventListener('sesion-cambio', actualizar);
  }, []);

  const valor = {
    usuario: sesion?.usuario || null,
    roles: sesion?.roles || [],
    rol: sesion?.rol || null,
    iniciar: (tokens, usuario, roles, rol) => guardarSesion({ tokens, usuario, roles, rol }),
    cambiarRol: (rol) => guardarSesion({ ...leerSesion(), rol }),
    salir: () => cerrarSesionKeycloak(),
  };
  return <SesionContexto.Provider value={valor}>{children}</SesionContexto.Provider>;
}

export const useSesion = () => useContext(SesionContexto);

/** Pagina de inicio de cada rol. */
export const inicioDeRol = (rol) =>
  ({ AUTOR: '/mis-preguntas', REVISOR: '/mis-revisiones', ADMINISTRADOR: '/asignaciones' }[rol] || '/');
