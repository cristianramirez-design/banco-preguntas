import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// El frontend solo conoce al API Gateway (8080). En desarrollo, Vite reenvia /api al gateway.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': { target: process.env.VITE_GATEWAY || 'http://localhost:8080', changeOrigin: true },
    },
  },
});
