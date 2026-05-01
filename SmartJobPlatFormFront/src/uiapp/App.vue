<template>
  <div id="uiapp">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'UIApp'
};
</script>

<style>
#uiapp {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #333;
  min-height: 100vh;
  background: #f5f5f5;
}

.uiapp-container {
  max-width: 750px;
  margin: 0 auto;
  min-height: 100vh;
  background: #fff;
  position: relative;
}
</style>
