import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  // --- ADD THIS SERVER BLOCK ---
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // Points to your local Java Backend
        changeOrigin: true,
        secure: false
      }
    }
  }
})