<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="container">
        <div class="logo">
          <span class="logo-text">智慧招聘平台</span>
        </div>
        <nav class="nav">
          <el-button type="text" @click="goToJobSeeker">求职者</el-button>
          <el-button type="text" @click="goToHR">企业HR</el-button>
          <el-button type="text" @click="goToAdmin">管理员</el-button>
        </nav>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main">
      <div class="container">
        <!-- 欢迎区域 -->
        <div class="welcome-section">
          <h1 class="welcome-title">智慧招聘平台</h1>
          <p class="welcome-desc">连接人才与企业，实现共赢发展</p>
        </div>

        <!-- 角色选择卡片 -->
        <div class="role-cards">
          <div class="role-card jobseeker-card" @click="goToJobSeeker">
            <div class="card-icon">
              <i class="el-icon-user"></i>
            </div>
            <h3 class="card-title">求职者</h3>
            <p class="card-desc">寻找理想工作，管理个人简历</p>
            <el-button type="primary" class="card-btn">进入求职者端</el-button>
          </div>

          <div class="role-card hr-card" @click="goToHR">
            <div class="card-icon">
              <i class="el-icon-office-building"></i>
            </div>
            <h3 class="card-title">企业HR</h3>
            <p class="card-desc">发布岗位招聘，管理投递简历</p>
            <el-button type="success" class="card-btn">进入HR端</el-button>
          </div>

          <div class="role-card admin-card" @click="goToAdmin">
            <div class="card-icon">
              <i class="el-icon-s-custom"></i>
            </div>
            <h3 class="card-title">平台管理员</h3>
            <p class="card-desc">管理平台运营，审核企业岗位</p>
            <el-button type="warning" class="card-btn">进入管理后台</el-button>
          </div>
        </div>

        <!-- 平台数据展示 -->
        <div class="stats-section">
          <div class="stats-title">平台数据</div>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-number">10000+</div>
              <div class="stat-label">注册用户</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">5000+</div>
              <div class="stat-label">合作企业</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">20000+</div>
              <div class="stat-label">优质岗位</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">50000+</div>
              <div class="stat-label">成功匹配</div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <p>© 2023 智慧招聘平台 版权所有</p>
        <p class="footer-links">
          <span>关于我们</span>
          <span>联系我们</span>
          <span>隐私政策</span>
          <span>服务条款</span>
        </p>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'HomeIndex',
  methods: {
    // 跳转到求职者端
    goToJobSeeker() {
      this.$router.push('/jobseeker/login');
    },
    // 跳转到HR端
    goToHR() {
      this.$router.push('/hr/login');
    },
    // 跳转到管理员端
    goToAdmin() {
      this.$router.push('/admin/login');
    }
  }
};
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 头部 */
.header {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 15px 0;
}

.header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo .logo-text {
  font-size: 24px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav .el-button {
  font-size: 16px;
  margin-left: 20px;
}

/* 主内容 */
.main {
  flex: 1;
  padding: 60px 0;
}

.welcome-section {
  text-align: center;
  margin-bottom: 60px;
}

.welcome-title {
  font-size: 48px;
  color: #fff;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.welcome-desc {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.9);
}

/* 角色卡片 */
.role-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  margin-bottom: 60px;
}

.role-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px 30px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.role-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.card-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
}

.jobseeker-card .card-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.hr-card .card-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: #fff;
}

.admin-card .card-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.card-title {
  font-size: 22px;
  color: #333;
  margin-bottom: 10px;
}

.card-desc {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.card-btn {
  width: 100%;
}

/* 统计区域 */
.stats-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.stats-title {
  font-size: 24px;
  color: #333;
  text-align: center;
  margin-bottom: 30px;
  font-weight: bold;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 36px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 16px;
  color: #666;
}

/* 页脚 */
.footer {
  background: rgba(0, 0, 0, 0.1);
  padding: 30px 0;
  color: rgba(255, 255, 255, 0.9);
  text-align: center;
}

.footer p {
  margin: 0;
  margin-bottom: 10px;
}

.footer-links span {
  margin: 0 15px;
  cursor: pointer;
}

.footer-links span:hover {
  text-decoration: underline;
}

/* 响应式 */
@media (max-width: 992px) {
  .role-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .welcome-title {
    font-size: 32px;
  }

  .role-cards {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }

  .stat-number {
    font-size: 28px;
  }
}
</style>
