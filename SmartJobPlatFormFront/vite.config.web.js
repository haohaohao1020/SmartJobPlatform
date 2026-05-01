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
  base: './',
  publicDir: 'public',
  build: {
    outDir: 'dist-web',
    rollupOptions: {
      input: {
        web: resolve(__dirname, 'web.html')
      },
      output: {
        entryFileNames: 'assets/[name].[hash].js',
        chunkFileNames: 'assets/[name].[hash].js',
        assetFileNames: 'assets/[name].[hash].[ext]'
      }
    }
  },
  server: {
    port: 8081,
    open: false
  }
});
