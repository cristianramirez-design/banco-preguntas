import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// El frontend solo conoce al API Gateway (8080) y a Keycloak (8180). En desarrollo, Vite reenvia
// /api al gateway y /realms a Keycloak, para que el navegador hable con un solo origen.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': { target: process.env.VITE_GATEWAY || 'http://localhost:8080', changeOrigin: true },
      // Keycloak (tokens JWT). En Docker lo hace Nginx; en desarrollo, Vite.
      '/realms': { target: process.env.VITE_KEYCLOAK || 'http://localhost:8180', changeOrigin: true },
    },
  },
});
