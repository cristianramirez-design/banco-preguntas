# Guía para el repositorio Git

La rúbrica y el video exigen **commits de todos los integrantes**. Cada uno debe hacer commits desde su
propia cuenta de GitHub; no sirve que una sola persona suba todo.

## 1. Crear el repositorio (lo hace una persona)

```bash
cd banco-preguntas
git init
git branch -M main
git add .gitignore pom.xml README.md
git commit -m "Configura proyecto Maven y README"
git remote add origin https://github.com/USUARIO/banco-preguntas.git
git push -u origin main
```

En GitHub: *Settings → Collaborators* → invitar a los otros dos integrantes.

## 2. Cada integrante configura su identidad (una vez)

```bash
git config --global user.name "Nombre Apellido"
git config --global user.email "correo-de-su-cuenta-github@..."
```

El correo debe ser el de su cuenta de GitHub; si no, los commits no aparecen a su nombre.

## 3. Reparto sugerido (cada uno sube sus partes con commits pequeños)

| Integrante | Partes | Ejemplos de mensajes |
|------------|--------|----------------------|
| Cristian | `domain/servicio`, `domain/notificacion`, `acceso/email`, `acceso/sqlite`, pruebas de servicios y acceso | "Implementa AsignacionService con Observer", "Agrega persistencia SQLite" |
| Juliana | `domain/model`, `domain/validacion`, `presentacion/crear`, pruebas de modelo y validación | "Modela Pregunta con Builder", "Agrega reglas de validación estructural" |
| Victor | `domain/busqueda`, `domain/model/estado`, `presentacion/listar`, `presentacion/asignar`, `docs/arquitectura` | "Implementa patrón State", "Agrega filtros y paginación", "Agrega diagramas C4" |

Flujo de cada persona:

```bash
git pull
git add src/main/java/co/edu/unicauca/bancopreguntas/domain/model
git commit -m "Modela Pregunta con Builder"
git push
```

Hagan `git pull` antes de empezar para evitar conflictos, y usen mensajes que expliquen el cambio.

## 4. Verificar antes del video

```bash
git shortlog -sn      # commits por autor
```

En GitHub también se ve en *Insights → Contributors*.

## Qué no subir

`correo.properties` (contraseña), `banco-preguntas.db`, `target/` y `out/` ya están en `.gitignore`.
El `banco-preguntas.jar` pesa unos 14 MB; es mejor adjuntarlo en *Releases* de GitHub que en el repositorio.
