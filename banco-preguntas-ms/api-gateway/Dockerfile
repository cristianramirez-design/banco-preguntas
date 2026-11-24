# ---------- Etapa 1: compilar api-gateway con Maven (no hace falta tener Maven instalado) ----------
# El contexto de construccion es la carpeta raiz (docker-compose.yml) porque el pom padre esta alli.
FROM maven:3.9-eclipse-temurin-17 AS construccion
WORKDIR /src
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/
COPY ms-usuarios/pom.xml ms-usuarios/
COPY ms-preguntas/pom.xml ms-preguntas/
COPY ms-revision/pom.xml ms-revision/
COPY ms-notificaciones/pom.xml ms-notificaciones/
COPY api-gateway/src api-gateway/src
RUN --mount=type=cache,target=/root/.m2 mvn -q -B -pl api-gateway -am -DskipTests package

# ---------- Etapa 2: imagen liviana solo con el JRE y el .jar ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=construccion /src/api-gateway/target/api-gateway-*.jar app.jar
# Memoria acotada para poder correr todo el sistema en un portatil
ENV JAVA_OPTS="-Xms64m -Xmx192m -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -Xss512k"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
