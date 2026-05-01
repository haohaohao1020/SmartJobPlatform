<template>
  <div class="not-found-page">
    <div class="container">
      <div class="content">
        <h1 class="error-code">404</h1>
        <h2 class="error-title">页面不存在</h2>
        <p class="error-desc">抱歉，您访问的页面不存在或已被移除</p>
        <div class="actions">
          <el-button type="primary" @click="goHome">返回首页</el-button>
          <el-button @click="goBack">返回上一页</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFound',
  methods: {
    // 返回首页
    goHome() {
      this.$router.push('/');
    },
    // 返回上一页
    goBack() {
      this.$router.go(-1);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.container {
  text-align: center;
}

.content {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 60px 80px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
  line-height: 1;
}

.error-title {
  font-size: 28px;
  color: #333;
  margin: 20px 0 10px;
}

.error-desc {
  font-size: 16px;
  color: #666;
  margin-bottom: 40px;
}

.actions .el-button {
  margin: 0 10px;
  padding: 12px 30px;
  font-size: 16px;
}

/* 响应式 */
@media (max-width: 768px) {
  .content {
    padding: 40px 30px;
    margin: 20px;
  }

  .error-code {
    font-size: 80px;
  }

  .error-title {
    font-size: 24px;
  }

  .actions .el-button {
    display: block;
    width: 100%;
    margin: 10px 0;
  }
}
</style>
