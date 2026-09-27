# Guion del video de sustentación — Primer corte

**Duración total: 12 minutos exactos** (2 + 1 + 2 + 1 + 1 + 5). El enunciado penaliza con −1 punto pasarse
del tiempo o acelerar el video. Todos los integrantes deben hablar. Ensayen con cronómetro.

Antes de grabar:

* Borren `banco-preguntas.db` para arrancar con los 12 datos de ejemplo.
* Configuren `correo.properties` con Mailtrap y dejen el buzón de Mailtrap abierto en el navegador.
* Tengan abiertos: el documento de arquitectura, el IDE, GitHub, Trello y la terminal.

| # | Bloque | Tiempo | Quién | Qué mostrar |
|---|--------|--------|-------|-------------|
| 1 | Historias, prototipos y usabilidad | 0:00–2:00 | Juliana | Las 4 HU con 1 criterio de aceptación de ejemplo cada una; prototipos de escritorio; resultado del test SUS (promedio y 1 hallazgo que se corrigió) |
| 2 | Atributos de calidad | 2:00–3:00 | Victor | Por qué modificabilidad es prioritaria; tabla del escenario (estímulo → respuesta → medida) |
| 3 | Arquitectura (C4 + UML) | 3:00–5:00 | Victor | Contexto → Contenedores → Componentes (señalar las 3 capas y la inversión de dependencias) → UML de State/Observer |
| 4 | Pruebas unitarias | 5:00–6:00 | Cristian | Ejecutar `mvn clean test` en vivo: 81 pruebas en verde; abrir `PreguntaServiceTest` y explicar una prueba |
| 5 | Git y tablero Scrum | 6:00–7:00 | Cristian | Pestaña *Insights → Contributors* o `git shortlog -sn` mostrando commits de los tres; tablero Trello del Sprint 1 |
| 6 | Software funcionando + código clave | 7:00–12:00 | Los tres | Ver detalle abajo |

## Bloque 6 — demo (5 minutos)

1. **Juliana (1:30)** — Como `autor1`: crear una pregunta dejando la respuesta correcta sin marcar →
   GUARDAR → mensaje de error y campos en rojo (HU-1 CA2). Completar → "Pregunta creada exitosamente".
   Mostrar en código `ValidadorFactory` (Strategy + Composite).
2. **Victor (1:30)** — En *Mis preguntas*: filtrar por tema y estado, paginar (HU-3). Abrir una pregunta y
   ENVIAR A REVISIÓN → etiqueta amarilla (HU-2). Mostrar `EstadoBorrador.enviarARevision()` (State).
3. **Cristian (1:30)** — Cambiar a `admin`: bandeja de pendientes, ASIGNAR sin seleccionar → mensaje (HU-4 CA2).
   Seleccionar dos revisores → "Revisor(es) asignado(s) correctamente" → mostrar los dos correos llegando
   a Mailtrap. Mostrar `AsignacionService` + `NotificadorEmail` (Observer) y `ServicioEmailJakartaMail` (Adapter).
4. **Cristian (0:30)** — Cerrar y volver a abrir la aplicación: la pregunta creada sigue ahí (SQLite).
   Mostrar `Main.java`: la única línea que elige la fábrica de persistencia (Abstract Factory, DIP).
