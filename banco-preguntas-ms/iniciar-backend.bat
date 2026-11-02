@echo off
REM Arranca RabbitMQ y los 5 procesos del backend, cada uno en su propia ventana.
REM Requisitos: Docker Desktop abierto, Java 17+ y Maven en el PATH.
REM Cada servicio corre con poca memoria (256 MB) para que funcione en portatiles de 8 GB.
cd /d %~dp0
set OPCIONES=-Xms64m -Xmx256m -XX:MaxMetaspaceSize=192m -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -Xss512k -Dnet.bytebuddy.experimental=true
echo [1/3] Iniciando RabbitMQ...
docker compose up -d rabbitmq
echo [2/3] Compilando (sin pruebas)...
call mvn -q -DskipTests package
if errorlevel 1 (
  echo La compilacion fallo. Revise el error de arriba.
  pause
  exit /b 1
)
echo [3/3] Abriendo los microservicios...
start "ms-usuarios 8081" cmd /k "cd ms-usuarios && java %OPCIONES% -jar target\ms-usuarios-2.0.0.jar"
timeout /t 15 >nul
start "ms-preguntas 8082" cmd /k "cd ms-preguntas && java %OPCIONES% -jar target\ms-preguntas-2.0.0.jar"
start "ms-revision 8083" cmd /k "cd ms-revision && java %OPCIONES% -jar target\ms-revision-2.0.0.jar"
start "ms-notificaciones 8084" cmd /k "cd ms-notificaciones && java %OPCIONES% -jar target\ms-notificaciones-2.0.0.jar"
start "api-gateway 8080" cmd /k "cd api-gateway && java %OPCIONES% -jar target\api-gateway-2.0.0.jar"
echo.
echo Listo. Espere a que cada ventana diga Started ... y abra:
echo   Swagger unificado: http://localhost:8080/swagger-ui.html
echo   RabbitMQ:          http://localhost:15672  (guest/guest)
echo   Frontend:          cd frontend ^&^& npm install ^&^& npm run dev  -^> http://localhost:5173
