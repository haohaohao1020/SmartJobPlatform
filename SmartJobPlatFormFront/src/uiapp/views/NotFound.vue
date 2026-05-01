<template>
  <div class="not-found-page">
    <div class="content">
      <div class="icon">
        <i class="el-icon-loading"></i>
      </div>
      <h1>404</h1>
      <p>抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UINotFound',
  methods: {
    goHome() {
      this.$router.replace('/jobs');
    }
  }
};
</script>

<style scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.content {
  text-align: center;
  padding: 20px;
}

.icon {
  font-size: 80px;
  color: #e4e7ed;
  margin-bottom: 20px;
}

h1 {
  font-size: 60px;
  color: #909399;
  margin: 0 0 10px 0;
}

p {
  font-size: 14px;
  color: #606266;
  margin: 0 0 30px 0;
}
</style>
