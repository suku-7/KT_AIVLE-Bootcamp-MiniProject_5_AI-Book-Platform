import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    strictPort: true,
    watch: {
      usePolling: true,
    },
    allowedHosts: ['5173-cherish2pro-thminiprojt-2fbozbr7ku0.ws-us120.gitpod.io'], // ⭐️ 정확히 이 주소 추가
  },
})