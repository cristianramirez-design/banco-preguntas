-- Patron "Database per service": cada microservicio tiene su propia base de datos.
-- Este script solo corre la primera vez, cuando el volumen banco-pgdata esta vacio.
CREATE DATABASE usuarios_db;
CREATE DATABASE preguntas_db;
CREATE DATABASE revision_db;
-- Base de datos de Keycloak (usuarios, contrasenas, roles y sesiones del sistema de autenticacion)
CREATE DATABASE keycloak_db;
