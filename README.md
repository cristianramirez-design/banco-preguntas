# Banco de Preguntas Saber Pro — Primer corte 2026.2

Aplicación Java Desktop (Swing), arquitectura **monolítica en 3 capas** con micro‑patrón **MVC**,
principios **SOLID** y **patrones de diseño GoF**, para el proyecto de Ingeniería de Software II /
Laboratorio de Ingeniería de Software II — Universidad del Cauca.

Integrantes: Cristian Camilo Ramirez Camacho · Juliana Andrea Salas Tapia · Victor Manuel Ortiz Rojas

## Cómo ejecutar

Requiere JDK 17 o superior.

```bash
mvn clean test        # ejecuta las 81 pruebas unitarias
mvn package           # genera target/banco-preguntas-1.0.0.jar con todas las dependencias
mvn compile exec:java # compila y levanta la aplicación de escritorio
```

Sin Maven, con el ejecutable ya construido en la raíz del repositorio:

```bash
java -jar banco-preguntas.jar
```

### Persistencia

Por defecto los datos se guardan en **SQLite**, en el archivo `banco-preguntas.db` (se crea solo, junto al
lugar desde donde se ejecuta). La primera vez se cargan 12 preguntas de ejemplo; las siguientes veces se
conservan los datos. Para empezar de cero basta con borrar el archivo `.db`.

Para usar la persistencia en memoria (sin archivo):

```bash
java -Dpersistencia=MEMORIA -jar banco-preguntas.jar
```

### Correo electrónico (HU-4)

* **Sin configuración**: el correo se simula e imprime en la consola.
* **Correo real**: copie `correo.properties.example` como `correo.properties` (junto al `.jar`) y complete
  los datos SMTP. Para la sustentación se recomienda **Mailtrap** (plan gratuito): captura todos los correos
  en un buzón de prueba aunque los revisores tengan direcciones ficticias. `correo.properties` está en
  `.gitignore` para no subir la contraseña.

  **Importante:** `correo.properties` debe estar en la carpeta desde donde se ejecuta el programa, la misma
  donde aparece `banco-preguntas.db`. Si al arrancar la consola dice *"Sin correo.properties"*, el archivo
  está en otra carpeta.

El envío se hace en segundo plano (patrón Decorator), así que la ventana no se congela mientras se conecta al servidor.

## Usuarios de prueba

La barra superior permite cambiar de usuario para demostrar los distintos roles:

| Login      | Rol(es)            |
|------------|--------------------|
| autor1     | Autor              |
| autor2     | Autor + Revisor    |
| revisor1‑3 | Revisor            |
| admin      | Administrador      |

## Arquitectura en 3 capas

```
presentacion/   →   domain/   →   (interfaces)   ←   acceso/
   Swing            Entidades, servicios,             SQLite (JDBC), memoria,
   MVC              validación, notificación          correo (Jakarta Mail)
```

* **Capa de presentación** (`presentacion`): vistas Swing y controladores. No contiene reglas de negocio.
* **Capa de lógica de negocio** (`domain`): entidades, servicios, validación estructural, eventos.
  Define las **interfaces** de repositorio y de correo; no conoce ninguna clase de la capa de datos.
* **Capa de acceso a datos** (`acceso`): repositorios SQLite y en memoria, fábricas y servicios de correo.

La dependencia hacia la capa de datos está **invertida**: `acceso` depende de `domain`, no al revés.
Pasar de memoria a SQLite no modificó ninguna clase de `domain` ni de `presentacion`: solo se agregó
una familia nueva a la Abstract Factory.

Los diagramas C4 (contexto, contenedores, componentes) y el UML de clases están en
`docs/arquitectura/` (fuentes PlantUML `.puml` e imágenes en `png/`).

## Requisitos funcionales del primer corte

| HU | Requisito | Dónde está |
|----|-----------|------------|
| HU-1 | Crear preguntas de selección múltiple con única respuesta (ECD) | `CrearPreguntaView/Controller`, `Pregunta.Builder`, `PreguntaService.crear` |
| HU-2 | Cambiar estado *Borrador* → *Pendiente de revisión*, con colores | Patrón State en `domain/model/estado`, `EstadoCellRenderer`, `PreguntaService.enviarARevision` |
| HU-3 | Listar, filtrar, paginar y editar mis preguntas | `PreguntaService.listar/actualizar`, `CriterioBusqueda`, `PaginaResultado` |
| HU-4 | Asignar al menos un revisor y notificar por email | `AsignacionService`, Observer + Adapter + Decorator de correo |

La correspondencia criterio por criterio está en `docs/correcciones-documentos.md`, sección 4.

### Ciclo de vida de la pregunta (HU-2, CA3)

| Estado | Color | Editable | Transiciones |
|--------|-------|----------|--------------|
| Borrador | gris `#9E9E9E` | sí | → Pendiente de revisión |
| Pendiente de revisión | amarillo `#F5A623` | no | → En revisión |
| En revisión | azul `#42A5F5` | no | → Aprobada / Rechazada |
| Aprobada | verde `#66BB6A` | no | terminal |
| Rechazada | rojo `#EF5350` | sí | → Pendiente de revisión |

## Patrones de diseño GoF aplicados

| Patrón | Clase(s) | Problema que resuelve |
|--------|----------|-----------------------|
| **Builder** | `Pregunta.Builder` | La pregunta tiene más de diez atributos; `aBuilder()` produce la copia editable (HU-3, CA4). |
| **State** | `IEstadoPregunta` y los cinco estados | Cada estado decide transiciones legales, si es editable y su color. |
| **Strategy** | `IReglaValidacion` (6 reglas), `IFiltroPregunta` (7 filtros) | Algoritmos de validación y de filtrado intercambiables. |
| **Composite** | `ValidadorEstructural`, `FiltroCompuestoY` | Un conjunto de reglas/filtros se trata como uno solo. |
| **Factory Method** | `ValidadorFactory.porDefecto()` | Arma el validador y fija `OPCIONES_POR_PREGUNTA`. |
| **Abstract Factory** | `RepositoryFactory`, `SqliteRepositoryFactory`, `MemoriaRepositoryFactory` | Familias coherentes de repositorios + generador de códigos. |
| **Singleton** | Fábricas, `SesionUsuario`, estados | Instancia única compartida. |
| **Observer** | `IObservadorAsignacion`, `NotificadorEmail`, `NotificadorBitacora` | El servicio publica la asignación sin saber quién reacciona. |
| **Adapter** | `ServicioEmailJakartaMail`, `ServicioEmailSmtpAdapter` | Adaptan Jakarta Mail y el gateway simulado a `IServicioEmail`. |
| **Decorator** | `ServicioEmailAsincrono` | Envía el correo en otro hilo sin cambiar el servicio decorado. |
| **MVC (micro‑patrón)** | `View` + `Controller` + `TableModel` por historia | Separa widgets, eventos y datos presentados. |

## Principios SOLID

* **SRP** — `Pregunta.Builder` construye, `ValidadorEstructural` valida, el repositorio persiste, `PreguntaService` orquesta.
* **OCP** — una regla, un filtro o una familia de persistencia nueva es una clase nueva + una línea en su fábrica.
* **LSP** — `PreguntaRepositorySqlite` y `PreguntaRepositoryMemoria` son intercambiables.
* **ISP** — interfaces pequeñas: `IServicioEmail`, `IReglaValidacion`, `IFiltroPregunta`, `IGeneradorCodigo`.
* **DIP** — las interfaces viven en el dominio; `Main` es el único *composition root*.

## Escenario de calidad: modificabilidad

| Elemento | Descripción |
|----------|-------------|
| Contexto | Tiempo de desarrollo, al inicio de un sprint; fuente: equipo de desarrollo a petición del cliente |
| Estímulo | Nueva regla de validación estructural / cambiar el almacenamiento en memoria por una base de datos |
| Artefacto | `domain.validacion` y `acceso` |
| Respuesta | Clase nueva que implementa `IReglaValidacion` / `IPreguntaRepository`, registrada en su fábrica |
| Medición | Archivos existentes modificados, tiempo, pruebas rotas |
| Resultado esperado | Regla: 1 archivo nuevo + 1 línea, < 1 h, 0 pruebas rotas. Persistencia: 0 cambios en `domain` y `presentacion` |

## Pruebas unitarias

81 pruebas JUnit 5 (todas pasan):

| Clase de prueba | Pruebas | Cubre |
|-----------------|---------|-------|
| `PreguntaServiceTest` | 16 | HU-1, HU-2, HU-3 (paginación, filtros, edición) |
| `PreguntaTest` | 14 | Builder, copia editable, transiciones State, colores |
| `ValidadorEstructuralTest` | 12 | Las 6 reglas estructurales |
| `AsignacionServiceTest` | 9 | HU-4 y notificación con doble de prueba |
| `FiltrosBusquedaTest` | 7 | Los 7 filtros, Composite y `CriterioBusqueda` |
| `PreguntaRepositorySqliteTest` | 6 | Integración con SQLite |
| `PaginaResultadoTest` | 5 | Paginación |
| `UsuarioTest` | 4 | Roles y repositorio de usuarios |
| `OpcionTest` | 3 | Opciones de respuesta |
| `NotificacionTest` | 3 | Evento de asignación y observadores |
| `ServicioEmailAsincronoTest` | 2 | Decorator de correo |

## Estructura del proyecto

```
src/main/java/co/edu/unicauca/bancopreguntas/
├── Main.java                 # composition root
├── DatosDemo.java
├── presentacion/             # CAPA 1
│   ├── VentanaPrincipal.java
│   ├── comun/  crear/  listar/  asignar/
├── domain/                   # CAPA 2
│   ├── model/ + model/estado/
│   ├── validacion/ + validacion/reglas/
│   ├── busqueda/  servicio/  notificacion/
│   ├── repositorio/          # interfaces (puertos)
│   └── excepcion/
└── acceso/                   # CAPA 3
    ├── sqlite/               # JDBC: repositorios, conexión, generador de códigos
    ├── memoria/
    ├── fabrica/              # Abstract Factory
    └── email/                # Jakarta Mail (Adapter), asíncrono (Decorator), simulado
docs/
├── arquitectura/             # C4 + UML (PlantUML y PNG)
├── guion-video.md            # protocolo de sustentación, 12 minutos
├── guia-git.md               # commits de los tres integrantes
└── ...                       # backlog, Trello, burndown, correcciones
```

## Enlaces

* Video de sustentación: *(pendiente)*
* Documento de arquitectura: *(pendiente)*
