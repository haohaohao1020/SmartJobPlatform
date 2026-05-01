<template>
  <div class="not-found-page">
    <div class="content">
      <div class="error-code">
        <span>4</span>
        <span>0</span>
        <span>4</span>
      </div>
      <h1>页面不存在</h1>
      <p>抱歉，您访问的页面不存在或已被删除</p>
      <div class="actions">
        <el-button type="primary" @click="goHome">
          <i class="el-icon-house"></i> 返回首页
        </el-button>
        <el-button @click="$router.back()">
          <i class="el-icon-arrow-left"></i> 返回上一页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WebNotFound',
  methods: {
    goHome() {
      this.$router.replace('/dashboard');
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
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.content {
  text-align: center;
  padding: 40px;
}

.error-code {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 30px;
}

.error-code span {
  font-size: 120px;
  font-weight: bold;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

h1 {
  font-size: 28px;
  color: #303133;
  margin: 0 0 15px 0;
}

p {
  font-size: 16px;
  color: #909399;
  margin: 0 0 40px 0;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.actions .el-button {
  height: 44px;
  padding: 0 24px;
  font-size: 15px;
}
</style>
