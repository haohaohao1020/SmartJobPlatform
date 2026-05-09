import { defineConfig } from 'vite';
import vue2 from '@vitejs/plugin-vue2';
import { resolve } from 'path';

export default defineConfig({
  plugins: [vue2()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  define: {
    'process.env.UIAPP_STANDALONE': '"true"'
  },
  root: resolve(__dirname, 'src/uiapp'),
  base: './',
  publicDir: resolve(__dirname, 'public'),
  build: {
    outDir: resolve(__dirname, 'dist-uiapp'),
    emptyOutDir: true
  },
  server: {
    port: 8080,
    open: false
  }
});
