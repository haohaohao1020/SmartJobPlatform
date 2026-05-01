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
  root: '.',
  base: './',
  publicDir: 'public',
  build: {
    outDir: 'dist-uiapp',
    rollupOptions: {
      input: {
        uiapp: resolve(__dirname, 'uiapp.html')
      },
      output: {
        entryFileNames: 'assets/[name].[hash].js',
        chunkFileNames: 'assets/[name].[hash].js',
        assetFileNames: 'assets/[name].[hash].[ext]'
      }
    }
  },
  server: {
    port: 8080,
    open: false
  }
});
