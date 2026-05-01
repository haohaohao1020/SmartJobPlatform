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
  root: '.',
  base: '/',
  publicDir: 'public',
  build: {
    outDir: 'dist',
    rollupOptions: {
      input: {
        main: resolve(__dirname, 'index.html'),
        uiapp: resolve(__dirname, 'uiapp.html'),
        web: resolve(__dirname, 'web.html')
      }
    }
  },
  server: {
    port: 3000,
    open: false,
    proxy: {
      '/uiapp': {
        target: 'http://127.0.0.1:3000',
        rewrite: (path) => path,
        bypass: (req, res, proxyOptions) => {
          if (req.url.startsWith('/uiapp/') && !req.url.includes('.')) {
            req.url = '/uiapp.html';
          }
        }
      },
      '/web': {
        target: 'http://127.0.0.1:3000',
        rewrite: (path) => path,
        bypass: (req, res, proxyOptions) => {
          if (req.url.startsWith('/web/') && !req.url.includes('.')) {
            req.url = '/web.html';
          }
        }
      }
    }
  }
});
