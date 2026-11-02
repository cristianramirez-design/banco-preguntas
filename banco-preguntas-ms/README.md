# Banco de Preguntas Saber Pro — versión microservicios (Sprint 2)

Ingeniería de Software II · Universidad del Cauca · 2026.2
Cristian Camilo Ramirez Camacho · Juliana Andrea Salas Tapia · Victor Manuel Ortiz Rojas

La iteración 1 (monolito de escritorio en 3 capas) se refactorizó a **microservicios con arquitectura orientada a eventos**. El frontend (React) y el backend (Spring Boot) están separados y se comunican por una **API REST documentada con Swagger**.

## Arquitectura

```
                      ┌──────────────────────┐
  Navegador ───────►  │ frontend (React) 5173│
                      └──────────┬───────────┘
                                 │ /api/**
                      ┌──────────▼───────────┐
                      │ api-gateway     8080 │  Spring Cloud Gateway + Swagger unificado
                      └─┬───────┬───────┬───┬┘
           ┌────────────┘       │       │   └──────────────┐
  ┌────────▼──────┐ ┌───────────▼──┐ ┌──▼───────────┐ ┌────▼─────────────┐
  │ ms-usuarios   │ │ ms-preguntas │ │ ms-revision  │ │ ms-notificaciones│
  │ 8081  (BD)    │ │ 8082  (BD)   │ │ 8083  (BD)   │ │ 8084  (SMTP)     │
  └───────────────┘ └──────┬───────┘ └──────┬───────┘ └────────▲─────────┘
                           │   RabbitMQ: exchange "banco.eventos"│
                           └────────────────►◄─────────────────────┘
```

| Microservicio | Contexto delimitado | Responsabilidad | Puerto |
|---|---|---|---|
| ms-usuarios | Gestión de Usuarios | Usuarios, roles, inicio de sesión, directorio de revisores | 8081 |
| ms-preguntas | Gestión de Preguntas | Crear, editar, listar/filtrar, validación estructural, ciclo de vida (estados) | 8082 |
| ms-revision | Revisión por Pares | Bandeja del administrador, asignar 1 a 3 revisores, evaluar, decidir resultado | 8083 |
| ms-notificaciones | Notificaciones | Consume eventos y envía correos (Mailtrap o consola) | 8084 |
| api-gateway | — | Punto único de entrada, CORS, Swagger de todos los servicios | 8080 |

Cada microservicio tiene **su propia base de datos** (H2 en archivo por defecto; PostgreSQL con el perfil `postgres`).

### Eventos (RabbitMQ, exchange tipo topic `banco.eventos`)

| Evento | Routing key | Publica | Consume |
|---|---|---|---|
| PreguntaEnviadaARevision | `pregunta.enviada-revision` | ms-preguntas (HU-2) | ms-revision |
| RevisoresAsignados | `revision.revisores-asignados` | ms-revision (HU-4) | ms-preguntas, ms-notificaciones |
| PreguntaEvaluada | `revision.pregunta-evaluada` | ms-revision (HU-5) | ms-preguntas, ms-notificaciones |

Flujo: el autor envía la pregunta → ms-revision la pone en la bandeja del administrador → el administrador asigna revisores → ms-preguntas la pasa a *En revisión* y ms-notificaciones avisa por correo → cada revisor aprueba o rechaza → cuando todos terminan, ms-revision publica el resultado → ms-preguntas la pasa a *Aprobada* o *Rechazada* y se notifica al autor, a los revisores y al administrador.

### Estructura interna de cada microservicio

```
domain/        Modelo, reglas, servicios y puertos (interfaces). Sin Spring: se prueba con JUnit puro.
acceso/        Adaptadores: JPA, RabbitMQ, cliente REST, correo y configuración (composition root).
presentacion/  Controladores REST, DTOs y manejo de errores.
```

## Patrones de diseño GoF

| Tipo | Patrón | Dónde |
|---|---|---|
| Creacional | **Builder** | `ms-preguntas/.../domain/model/Pregunta.java` (`Pregunta.Builder`) |
| Creacional | **Factory Method** | `ValidadorFactory` (ms-preguntas), `PoliticaDecisionFactory` (ms-revision) |
| Estructural | **Adapter** | `*RepositorioJpaAdapter`, `UsuariosRestClient`, `ServicioEmailSmtp` |
| Estructural | **Composite** | `ValidadorEstructural` (reglas) y `FiltroCompuestoY` (filtros) en ms-preguntas |
| Estructural | **Facade** | `NotificacionFacade` (ms-notificaciones) |
| Comportamiento | **State** | `domain/model/estado/*` (Borrador, Pendiente, En revisión, Aprobada, Rechazada) |
| Comportamiento | **Strategy** | Reglas de validación, filtros de búsqueda, `IPoliticaDecision` (Unanimidad / Mayoría) |
| Comportamiento | **Observer** | Publicadores y `@RabbitListener` de los eventos |
| Comportamiento | **Template Method** | `PlantillaNotificacion` y sus subclases (correos) |

## Cómo ejecutarlo

Requisitos: **Java 17**, **Maven 3.9+**, **Docker Desktop**, **Node 18+**.

1. Correo (opcional): copie `ms-notificaciones/correo.properties.example` como `ms-notificaciones/correo.properties` y ponga sus credenciales de Mailtrap. Sin ese archivo los correos se muestran en la consola de ms-notificaciones.
2. Backend:
   - Windows: doble clic en `iniciar-backend.bat` (abre una ventana por servicio).
   - Linux/macOS: `./iniciar-backend.sh`.
   - Manual: `docker compose up -d` y luego `mvn spring-boot:run` dentro de cada carpeta (primero ms-usuarios).
3. Frontend: `cd frontend`, `npm install`, `npm run dev` y abra http://localhost:5173.

| Qué | URL |
|---|---|
| Aplicación | http://localhost:5173 |
| Swagger (todos los servicios) | http://localhost:8080/swagger-ui.html |
| Swagger de un servicio | http://localhost:8081/swagger-ui.html (8082, 8083, 8084) |
| RabbitMQ | http://localhost:15672 (guest / guest) |

Con PostgreSQL: `docker compose --profile postgres up -d` y arranque cada servicio con `mvn spring-boot:run -Dspring-boot.run.profiles=postgres`.

### Usuarios de prueba

| Login | Rol |
|---|---|
| autor1, autor2 | Autor (autor2 también es revisor) |
| revisor1 … revisor4 | Revisor |
| admin | Administrador |

El inicio de sesión es simplificado: se elige el usuario (sin contraseña) y el frontend envía el login en el encabezado `X-Usuario`.

### Recorrido de demostración

1. **autor1** → *Crear pregunta* (3 pasos). Guardar vacío muestra los campos en rojo. Complete y *Guardar y enviar a revisión*.
2. **admin** → *Asignar revisores*. Seleccionar 4 muestra "Puede asignar máximo 3 revisores"; asigne 2.
3. Ver correos en Mailtrap o en *Notificaciones enviadas*. En *Banco de preguntas* la pregunta está *En revisión*.
4. **revisor1** → *Mis revisiones* → Aprobar. **revisor2** → Rechazar sin observaciones muestra el error; escriba una observación y rechace.
5. **autor1** → *Mis preguntas*: la pregunta está *Rechazada*; en *Ver* aparecen las observaciones. Edítela y envíela de nuevo (ronda 2).

## Pruebas

```
mvn test
```

116 pruebas unitarias JUnit 5 sobre las clases de dominio (sin Spring ni base de datos, con dobles en memoria):
ms-usuarios 10 · ms-preguntas 65 · ms-revision 31 · ms-notificaciones 10.
