<template>
  <div class="hr-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="container">
        <div class="logo" @click="goHome">
          <i class="el-icon-office-building"></i>
          <span class="logo-text">HR管理后台</span>
        </div>
        <nav class="nav">
          <router-link 
            v-for="item in navItems" 
            :key="item.path"
            :to="item.path"
            class="nav-item"
            active-class="active"
          >
            <i :class="item.icon"></i>
            <span>{{ item.name }}</span>
          </router-link>
        </nav>
        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="dropdown-trigger">
              <el-avatar :size="36" :src="userAvatar">
                <i class="el-icon-user-solid"></i>
              </el-avatar>
              <span class="user-name">{{ userName }}</span>
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="company">企业信息</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main">
      <div class="container">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script>
import { mapGetters } from 'pinia';
import { useUserStore } from '@/stores/user';

export default {
  name: 'HRLayout',
  data() {
    return {
      // 导航菜单
      navItems: [
        { name: '岗位管理', path: '/hr/jobs', icon: 'el-icon-suitcase' },
        { name: '简历管理', path: '/hr/resumes', icon: 'el-icon-document' },
        { name: '企业信息', path: '/hr/company', icon: 'el-icon-office-building' }
      ]
    };
  },
  computed: {
    ...mapGetters(useUserStore, ['getUserName', 'getUserAvatar', 'isAuthenticated']),
    userStore() {
      return useUserStore();
    },
    userName() {
      return this.getUserName || '未登录';
    },
    userAvatar() {
      return this.getUserAvatar;
    }
  },
  mounted() {
    // 恢复用户信息
    this.userStore.restoreUserInfo();
    
    // 检查是否登录，如果未登录跳转到登录页
    this.checkLogin();
  },
  methods: {
    // 检查登录状态
    checkLogin() {
      const loginPath = '/hr/login';
      if (!this.isAuthenticated && this.$route.path !== loginPath) {
        this.$router.push(loginPath);
      }
    },
    // 返回首页
    goHome() {
      this.$router.push('/hr/jobs');
    },
    // 处理下拉菜单命令
    handleCommand(command) {
      switch (command) {
        case 'company':
          this.$router.push('/hr/company');
          break;
        case 'logout':
          this.logout();
          break;
      }
    },
    // 退出登录
    logout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.userStore.clearUserInfo();
        this.$router.push('/hr/login');
        this.$message.success('已退出登录');
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.hr-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

/* 头部 */
.header {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header .container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.logo i {
  font-size: 28px;
  color: #fff;
  margin-right: 10px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.nav {
  display: flex;
  align-items: center;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.3s;
}

.nav-item:hover,
.nav-item.active {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.nav-item i {
  margin-right: 5px;
}

.user-info {
  display: flex;
  align-items: center;
}

.dropdown-trigger {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #fff;
}

.user-name {
  margin: 0 8px;
  font-size: 14px;
}

/* 主内容 */
.main {
  flex: 1;
}

.main .container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 响应式 */
@media (max-width: 768px) {
  .nav {
    display: none;
  }

  .header .container {
    padding: 0 15px;
  }

  .logo-text {
    font-size: 18px;
  }

  .user-name {
    display: none;
  }

  .main .container {
    padding: 15px;
  }
}
</style>
