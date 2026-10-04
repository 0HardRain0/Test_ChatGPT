import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // 개발 중 /api 로 시작하는 요청을 Spring Boot(8080)로 넘겨 준다.
    // 덕분에 프론트 코드에서는 fetch('/api/posts') 처럼 상대 경로만 쓰면 된다.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
});
