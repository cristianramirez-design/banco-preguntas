# Correcciones a los documentos del proyecto

> Estado: las correcciones 2 y 3 ya quedaron aplicadas en el Documento de Arquitectura del primer corte.
> La 1 debe aplicarse al PDF del Taller 2 si se vuelve a entregar.

Tres ajustes de redacción y una errata detectados al cruzar el enunciado del primer
corte, la *Especificación de Requisitos Funcionales* y el *Taller de requisitos
funcionales y no funcionales*.

---

## 1. Incoherencia: aplicación de escritorio vs. aplicación web

**Problema.** El Taller 2 elige aplicación web (SPA React + Spring Boot) y descarta
explícitamente el escritorio, mientras el enunciado del primer corte exige *"java
Desktop con una arquitectura monolítica en 3 capas"*, que es lo que está implementado.
Un evaluador que lea el Taller 2 y luego vea la aplicación Swing encuentra una
contradicción entre documento y código.

**Solución.** Agregar este párrafo al final de la sección 2 del Taller 2, reemplazando
la "Nota sobre la elección del tipo de aplicación":

> **Nota sobre la elección del tipo de aplicación y su vigencia por cortes.** Las
> restricciones del primer corte exigen una implementación en Java Desktop con
> arquitectura monolítica en tres capas, y así se entregó: la aplicación del primer
> corte está construida en Java + Swing, con capas de presentación, lógica de negocio
> y acceso a datos, micro-patrón MVC y persistencia en memoria. Al mismo tiempo, el
> RNF-02 exige acceso desde navegadores web modernos y los RNF-05, RNF-17 y RNF-18
> hablan de 100 a 500 usuarios concurrentes, metas que una aplicación de escritorio no
> puede cumplir. Las decisiones de arquitectura documentadas en esta sección (SPA
> React, Spring Boot, PostgreSQL) corresponden por tanto a la **arquitectura objetivo
> a partir del segundo corte**, cuando el plan del curso exige migrar el monolito
> hacia microservicios y eventos. La migración es viable sin reescribir el dominio
> porque la capa de lógica de negocio del primer corte ya depende únicamente de
> interfaces (`IPreguntaRepository`, `IUsuarioRepository`, `IServicioEmail`,
> `IGeneradorCodigo`) y no de la tecnología de presentación ni de persistencia: es
> exactamente la táctica de "reducir el acoplamiento mediante un intermediario"
> documentada en la sección 4.2.

Con esto la decisión web deja de contradecir la entrega y pasa a ser la justificación
del escenario de calidad de modificabilidad.

---

## 2. Errata en la Especificación de Requisitos Funcionales

En la sección **3.2 HU-2 — Cambiar estado de pregunta (HE-02)** la tabla de encabezado
quedó copiada de la HU-1. Dice:

| Campo | Dice | Debe decir |
|-------|------|------------|
| Épica | HE-01 | **HE-02** |
| Historia de Usuario | HU-1 | **HU-2** |

El rol (*Autor de preguntas*) y el resto de la tabla sí son correctos.

---

## 3. Decisión: cuatro opciones con una marcada como correcta

**Problema.** El enunciado del corte enumera los campos como *"Cuatro distractores,
Respuesta correcta"*, lo que se puede leer como cinco alternativas. El prototipo de la
HU-1 muestra cuatro campos (Distractor 1 a 4) y un radio button *"Respuesta correcta:
○ Distractor 1 ○ Distractor 2 ○ Distractor 3 ○ Distractor 4"*, es decir cuatro
alternativas en total, una de ellas marcada como clave. El criterio de aceptación 2 de
la HU-1 refuerza esta lectura al hablar de *"sin respuesta correcta marcada"*.

**Decisión implementada.** Se siguió la lectura literal del enunciado del profesor,
que es el documento que califica: **cinco opciones de respuesta — cuatro distractores
más la respuesta correcta**. El formulario etiqueta las alternativas como Opción A a E
con un radio para marcar la correcta. Un ítem bien formado de cinco opciones no pierde
puntos frente al prototipo; en cambio, entregar tres distractores donde el enunciado
pidió cuatro sí es un riesgo.

**Pendiente de confirmar con el docente.** Conviene preguntarle si el prototipo (cuatro
alternativas en total) o el enunciado (cuatro distractores más la clave) es el que manda.
La cantidad está en una sola constante:

```java
// ValidadorFactory.java
public static final int OPCIONES_POR_PREGUNTA = 5;   // cambiar a 4 si manda el prototipo
```

Cambiar ese valor ajusta la regla de validación y el número de campos del formulario;
ninguna otra clase se modifica. Esto es, en sí mismo, evidencia del principio
Open/Closed para la rúbrica de modificabilidad.

**Ajuste necesario en los prototipos y en el criterio de aceptación 1 de la HU-1**, para
que el documento propio quede alineado con el enunciado y con la aplicación:

> Cada pregunta debe tener: Contexto, Pregunta directa, cinco opciones de respuesta
> (cuatro distractores y una respuesta correcta, marcada mediante un selector único),
> Justificación de la respuesta, Bibliografía, Competencia, Tema, Subtema y Nivel de
> dificultad.

En el wireframe de la HU-1 esto implica agregar un quinto campo (Opción E) y mover el
selector de respuesta correcta al costado de cada opción, en lugar de listar los cuatro
distractores por separado.

---

## 3 bis. Errata menor en el enunciado del profesor

El requisito funcional 1 cierra con *"el sistema debe aplicar la validación estructural
(ver HU03)"*, pero la HU03 del mismo enunciado es el listado paginado de preguntas, no la
validación. Es un residuo de una versión anterior del documento. La interpretación que se
implementó es la razonable: la validación estructural se aplica al momento de grabar la
pregunta (HU-1). Vale la pena confirmarlo con el docente en la sustentación.

---

## 4. Trazabilidad criterios de aceptación → implementación

| Criterio | Comportamiento implementado |
|----------|------------------------------|
| HU-1 CA1 | Al guardar se aplica la validación estructural sobre los diez campos y las cinco opciones; aparece "Pregunta creada exitosamente" y la pregunta queda en Borrador |
| HU-1 CA2 | Aparece "No se pudo guardar la pregunta", se listan los errores y se **resaltan en rojo** los campos inválidos (`CrearPreguntaView.resaltarCampos`) |
| HU-1 CA3 | Botón CANCELAR: pide confirmación, descarta cambios y regresa al listado del autor |
| HU-2 CA1 | ENVIAR A REVISIÓN pasa la pregunta a Pendiente de revisión con su etiqueta de color |
| HU-2 CA2 | Si la pregunta está incompleta aparece "La pregunta debe estar completa antes de enviarla a revision" y el estado no cambia (`PreguntaIncompletaException`) |
| HU-2 CA3 | Los cinco estados con sus colores: Borrador gris, Pendiente amarillo, En revisión azul, Aprobada verde, Rechazada rojo, más la leyenda en pantalla |
| HU-3 CA1 | Listado paginado de 10 por página con columnas ID, Pregunta directa, Tema, Competencia, Nivel, Estado y Acciones (VER, EDITAR) |
| HU-3 CA2 | Filtros por Estado, Tema, Subtema y Competencia más búsqueda libre por ID o contenido, manteniendo la paginación |
| HU-3 CA3 | Aparece "No se encontraron preguntas con los criterios seleccionados" |
| HU-3 CA4 | EDITAR abre el formulario de la HU-1 con los datos cargados, solo si la pregunta está en Borrador o Rechazada |
| HU-4 CA1 | Modal con casillas por revisor; al asignar pasa a En revisión, envía correo a cada revisor y muestra "Revisor(es) asignado(s) correctamente" |
| HU-4 CA2 | Sin selección aparece "Debe seleccionar al menos un revisor" y el estado no cambia |
| HU-4 CA3 | Bandeja del administrador con columnas ID, Autor, Tema, Competencia, Fecha de envío y acción ASIGNAR |
