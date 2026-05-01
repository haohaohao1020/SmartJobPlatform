<template>
  <div class="jobseeker-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="container">
        <div class="logo" @click="goHome">
          <i class="el-icon-suitcase"></i>
          <span class="logo-text">智慧招聘</span>
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
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="resume">简历管理</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main">
      <router-view />
    </main>

    <!-- 底部导航栏（移动端） -->
    <nav class="mobile-nav" v-if="isMobile">
      <router-link 
        v-for="item in mobileNavItems" 
        :key="item.path"
        :to="item.path"
        class="mobile-nav-item"
        active-class="active"
      >
        <i :class="item.icon"></i>
        <span>{{ item.name }}</span>
      </router-link>
    </nav>
  </div>
</template>

<script>
import { mapGetters } from 'pinia';
import { useUserStore } from '@/stores/user';

export default {
  name: 'JobSeekerLayout',
  data() {
    return {
      // 导航菜单
      navItems: [
        { name: '岗位列表', path: '/jobseeker/jobs', icon: 'el-icon-search' },
        { name: '我的投递', path: '/jobseeker/applications', icon: 'el-icon-document' },
        { name: '我的收藏', path: '/jobseeker/favorites', icon: 'el-icon-star-on' },
        { name: '简历管理', path: '/jobseeker/resume', icon: 'el-icon-edit' }
      ],
      // 移动端导航菜单
      mobileNavItems: [
        { name: '首页', path: '/jobseeker/jobs', icon: 'el-icon-home' },
        { name: '投递', path: '/jobseeker/applications', icon: 'el-icon-document' },
        { name: '收藏', path: '/jobseeker/favorites', icon: 'el-icon-star-on' },
        { name: '我的', path: '/jobseeker/profile', icon: 'el-icon-user' }
      ],
      isMobile: false
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
    this.checkIsMobile();
    window.addEventListener('resize', this.checkIsMobile);
    
    // 恢复用户信息
    this.userStore.restoreUserInfo();
    
    // 检查是否登录，如果未登录跳转到登录页
    this.checkLogin();
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkIsMobile);
  },
  methods: {
    // 检查是否是移动端
    checkIsMobile() {
      this.isMobile = window.innerWidth <= 768;
    },
    // 检查登录状态
    checkLogin() {
      const loginPath = '/jobseeker/login';
      if (!this.isAuthenticated && this.$route.path !== loginPath) {
        this.$router.push(loginPath);
      }
    },
    // 返回首页
    goHome() {
      this.$router.push('/jobseeker/jobs');
    },
    // 处理下拉菜单命令
    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$router.push('/jobseeker/profile');
          break;
        case 'resume':
          this.$router.push('/jobseeker/resume');
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
        this.$router.push('/jobseeker/login');
        this.$message.success('已退出登录');
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.jobseeker-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

/* 头部 */
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  padding-bottom: 60px;
}

/* 移动端导航 */
.mobile-nav {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50px;
  background: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-around;
  align-items: center;
  z-index: 1000;
}

.mobile-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  height: 50px;
  color: #666;
  text-decoration: none;
  font-size: 12px;
}

.mobile-nav-item i {
  font-size: 20px;
  margin-bottom: 2px;
}

.mobile-nav-item.active {
  color: #409eff;
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
}
</style>
