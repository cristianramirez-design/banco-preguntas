#!/usr/bin/env bash
# Arranca RabbitMQ y los 5 procesos del backend (Linux/macOS). Ctrl+C detiene todo.
set -e
cd "$(dirname "$0")"
docker compose up -d rabbitmq
mvn -q -DskipTests install
pids=()
(cd ms-usuarios && mvn -q spring-boot:run) & pids+=($!)
sleep 8
for m in ms-preguntas ms-revision ms-notificaciones api-gateway; do
  (cd "$m" && mvn -q spring-boot:run) & pids+=($!)
done
trap 'kill ${pids[@]} 2>/dev/null' INT TERM
echo "Swagger: http://localhost:8080/swagger-ui.html"
wait
