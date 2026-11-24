// Autenticacion con Keycloak (OpenID Connect). El navegador pide el token a /realms/... y Vite (en
// desarrollo) o Nginx (en Docker) lo reenvian a Keycloak. El token JWT se manda luego al API Gateway.
const OIDC = '/realms/banco-preguntas/protocol/openid-connect';
const CLIENTE = 'banco-frontend';
const CLAVE = 'sesion';

export const ROLES_SISTEMA = ['AUTOR', 'REVISOR', 'ADMINISTRADOR'];

/** Lee el contenido (payload) de un JWT sin validarlo: la validacion la hace el gateway. */
export function decodificar(token) {
  try {
    const parte = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const json = decodeURIComponent(atob(parte).split('').map((c) => '%' + c.charCodeAt(0).toString(16).padStart(2, '0')).join(''));
    return JSON.parse(json);
  } catch {
    return {};
  }
}

/** Roles del sistema que trae el token (claim realm_access.roles). */
export function rolesDelToken(token) {
  const roles = decodificar(token)?.realm_access?.roles || [];
  return ROLES_SISTEMA.filter((r) => roles.includes(r));
}

function aTokens(datos) {
  const ahora = Date.now();
  return {
    access: datos.access_token,
    refresh: datos.refresh_token,
    expira: ahora + (datos.expires_in - 15) * 1000,
    expiraRefresh: ahora + ((datos.refresh_expires_in || 1800) - 15) * 1000,
  };
}

async function pedirToken(parametros) {
  let resp;
  try {
    resp = await fetch(`${OIDC}/token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ client_id: CLIENTE, ...parametros }),
    });
  } catch {
    throw new Error('No hay conexion con Keycloak. ¿Esta encendido (puerto 8180)?');
  }
  const datos = await resp.json().catch(() => ({}));
  if (!resp.ok) {
    if (datos.error === 'invalid_grant') throw new Error('Usuario o contraseña incorrectos.');
    throw new Error(datos.error_description || `Keycloak respondio ${resp.status}`);
  }
  return aTokens(datos);
}

/** Inicio de sesion con usuario y contrasena (Resource Owner Password Credentials). */
export const iniciarSesionKeycloak = (usuario, clave) =>
  pedirToken({ grant_type: 'password', username: usuario, password: clave, scope: 'openid' });

// ---------------- Sesion guardada en el navegador ----------------

export function leerSesion() {
  try { return JSON.parse(sessionStorage.getItem(CLAVE) || 'null'); } catch { return null; }
}

export function guardarSesion(sesion) {
  try {
    if (sesion) sessionStorage.setItem(CLAVE, JSON.stringify(sesion)); else sessionStorage.removeItem(CLAVE);
  } catch { /* sin almacenamiento disponible */ }
  window.dispatchEvent(new Event('sesion-cambio'));
}

let renovando = null;

/** Devuelve un access token vigente; si esta por vencer lo renueva con el refresh token. */
export async function tokenVigente() {
  const sesion = leerSesion();
  if (!sesion?.tokens) return null;
  if (Date.now() < sesion.tokens.expira) return sesion.tokens.access;
  if (Date.now() >= sesion.tokens.expiraRefresh) {
    guardarSesion(null);
    return null;
  }
  if (!renovando) {
    renovando = pedirToken({ grant_type: 'refresh_token', refresh_token: sesion.tokens.refresh })
      .then((tokens) => { guardarSesion({ ...leerSesion(), tokens }); return tokens.access; })
      .catch(() => { guardarSesion(null); return null; })
      .finally(() => { renovando = null; });
  }
  return renovando;
}

/** Cierra la sesion tambien en Keycloak (revoca el refresh token). */
export async function cerrarSesionKeycloak() {
  const sesion = leerSesion();
  guardarSesion(null);
  if (!sesion?.tokens?.refresh) return;
  try {
    await fetch(`${OIDC}/logout`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ client_id: CLIENTE, refresh_token: sesion.tokens.refresh }),
    });
  } catch { /* si Keycloak no responde, la sesion local ya se borro */ }
}
