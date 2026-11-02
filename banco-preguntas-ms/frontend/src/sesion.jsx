import { createContext, useContext, useState } from 'react';

const SesionContexto = createContext(null);

const ETIQUETAS = { AUTOR: 'Autor', REVISOR: 'Revisor', ADMINISTRADOR: 'Administrador' };
export const etiquetaRol = (rol) => ETIQUETAS[rol] || rol;

function leer() {
  try { return JSON.parse(localStorage.getItem('sesion') || 'null'); } catch { return null; }
}

export function ProveedorSesion({ children }) {
  const [sesion, setSesion] = useState(leer);

  const guardar = (s) => {
    try {
      if (s) localStorage.setItem('sesion', JSON.stringify(s)); else localStorage.removeItem('sesion');
    } catch { /* sin almacenamiento: la sesion vive solo en memoria */ }
    setSesion(s);
  };

  const valor = {
    usuario: sesion?.usuario || null,
    rol: sesion?.rol || null,
    iniciar: (usuario, rol) => guardar({ usuario, rol }),
    cambiarRol: (rol) => guardar({ ...sesion, rol }),
    salir: () => guardar(null),
  };
  return <SesionContexto.Provider value={valor}>{children}</SesionContexto.Provider>;
}

export const useSesion = () => useContext(SesionContexto);

/** Pagina de inicio de cada rol. */
export const inicioDeRol = (rol) =>
  ({ AUTOR: '/mis-preguntas', REVISOR: '/mis-revisiones', ADMINISTRADOR: '/asignaciones' }[rol] || '/');
