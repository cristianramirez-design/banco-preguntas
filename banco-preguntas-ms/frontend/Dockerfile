# ---------- Etapa 1: compilar el frontend React con Vite ----------
FROM node:20-alpine AS construccion
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY . .
RUN npm run build

# ---------- Etapa 2: servir los archivos estaticos con Nginx ----------
# Nginx tambien hace de proxy: /api va al API Gateway y /realms a Keycloak, asi el navegador
# habla con un solo origen (http://localhost:5173) y no hay problemas de CORS.
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=construccion /app/dist /usr/share/nginx/html
EXPOSE 80
