// Cliente HTTP unico: todas las llamadas pasan por el API Gateway (/api -> http://localhost:8080).
const BASE = import.meta.env.VITE_API_URL || '';

export class ErrorApi extends Error {
  constructor(status, cuerpo) {
    super((cuerpo && cuerpo.mensaje) || `Error ${status}`);
    this.status = status;
    this.errores = (cuerpo && cuerpo.errores) || [];
    this.campos = (cuerpo && cuerpo.campos) || [];
  }
}

function usuarioActual() {
  try {
    const s = JSON.parse(localStorage.getItem('sesion') || 'null');
    return s ? s.usuario.login : null;
  } catch {
    return null;
  }
}

async function pedir(metodo, ruta, cuerpo) {
  const headers = { 'Content-Type': 'application/json' };
  const login = usuarioActual();
  if (login) headers['X-Usuario'] = login;
  let resp;
  try {
    resp = await fetch(BASE + ruta, { method: metodo, headers, body: cuerpo ? JSON.stringify(cuerpo) : undefined });
  } catch {
    throw new ErrorApi(0, { mensaje: 'No hay conexion con el servidor. ¿Esta corriendo el API Gateway (8080)?' });
  }
  const texto = await resp.text();
  let datos = null;
  try { datos = texto ? JSON.parse(texto) : null; } catch { datos = { mensaje: texto }; }
  if (!resp.ok) {
    if (resp.status >= 500 && (!datos || !datos.mensaje || datos.error)) {
      throw new ErrorApi(resp.status, { mensaje: 'El servicio no respondio correctamente. Revise que todos los microservicios esten encendidos.' });
    }
    throw new ErrorApi(resp.status, datos);
  }
  return datos;
}

const qs = (params) => {
  const p = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => { if (v !== undefined && v !== null && v !== '') p.append(k, v); });
  const s = p.toString();
  return s ? `?${s}` : '';
};

export const api = {
  // ms-usuarios
  usuarios: (rol) => pedir('GET', `/api/usuarios${qs({ rol })}`),
  login: (login) => pedir('POST', '/api/auth/login', { login }),
  // ms-preguntas
  catalogos: () => pedir('GET', '/api/catalogos'),
  preguntas: (filtros) => pedir('GET', `/api/preguntas${qs(filtros)}`),
  pregunta: (id) => pedir('GET', `/api/preguntas/${id}`),
  crearPregunta: (datos) => pedir('POST', '/api/preguntas', datos),
  editarPregunta: (id, datos) => pedir('PUT', `/api/preguntas/${id}`, datos),
  enviarARevision: (id) => pedir('PATCH', `/api/preguntas/${id}/enviar-revision`),
  // ms-revision
  pendientes: () => pedir('GET', '/api/revisiones/pendientes'),
  asignar: (preguntaId, revisores, administrador) =>
    pedir('POST', '/api/revisiones/asignaciones', { preguntaId, revisores, administrador }),
  misRevisiones: () => pedir('GET', '/api/revisiones/mias'),
  evaluar: (id, decision, observaciones) => pedir('POST', `/api/revisiones/${id}/evaluacion`, { decision, observaciones }),
  revisionesDePregunta: (preguntaId) => pedir('GET', `/api/revisiones/pregunta/${preguntaId}`),
  // ms-notificaciones
  notificaciones: (destinatario) => pedir('GET', `/api/notificaciones${qs({ destinatario })}`),
};
