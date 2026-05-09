<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo" @click="goHome">
        <i class="el-icon-s-custom"></i>
        <span v-show="!isCollapsed" class="logo-text">管理后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <i class="el-icon-data-analysis"></i>
          <span slot="title">数据控制台</span>
        </el-menu-item>
        
        <el-submenu index="1">
          <template slot="title">
            <i class="el-icon-user"></i>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/admin/users">用户列表</el-menu-item>
        </el-submenu>
        
        <el-submenu index="2">
          <template slot="title">
            <i class="el-icon-office-building"></i>
            <span>企业管理</span>
          </template>
          <el-menu-item index="/admin/companies">企业审核</el-menu-item>
        </el-submenu>
        
        <el-submenu index="3">
          <template slot="title">
            <i class="el-icon-suitcase"></i>
            <span>岗位管理</span>
          </template>
          <el-menu-item index="/admin/jobs">岗位审核</el-menu-item>
        </el-submenu>
        
        <el-submenu index="4">
          <template slot="title">
            <i class="el-icon-bell"></i>
            <span>公告管理</span>
          </template>
          <el-menu-item index="/admin/announcements">公告列表</el-menu-item>
        </el-submenu>
        
        <el-menu-item index="/admin/settings">
          <i class="el-icon-setting"></i>
          <span slot="title">系统配置</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <header class="header">
        <div class="header-left">
          <i class="el-icon-fold" @click="toggleCollapse"></i>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.name }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="dropdown-trigger">
              <el-avatar :size="36" :src="userAvatar">
                <i class="el-icon-user-solid"></i>
              </el-avatar>
              <span class="user-name">{{ userName }}</span>
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="dashboard">控制台</el-dropdown-item>
              <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区域 -->
      <main class="main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'pinia';
import { useUserStore } from '@/stores/user';

export default {
  name: 'AdminLayout',
  data() {
    return {
      isCollapsed: false
    };
  },
  computed: {
    ...mapGetters(useUserStore, ['getUserName', 'getUserAvatar', 'isAuthenticated']),
    userStore() {
      return useUserStore();
    },
    userName() {
      return this.getUserName || '管理员';
    },
    userAvatar() {
      return this.getUserAvatar;
    },
    activeMenu() {
      return this.$route.path;
    },
    breadcrumbs() {
      // 根据当前路由生成面包屑
      const breadcrumbMap = {
        '/admin/dashboard': [{ name: '数据控制台' }],
        '/admin/users': [{ name: '用户管理' }, { name: '用户列表' }],
        '/admin/companies': [{ name: '企业管理' }, { name: '企业审核' }],
        '/admin/jobs': [{ name: '岗位管理' }, { name: '岗位审核' }],
        '/admin/announcements': [{ name: '公告管理' }, { name: '公告列表' }],
        '/admin/settings': [{ name: '系统配置' }]
      };
      return breadcrumbMap[this.$route.path] || [{ name: '首页' }];
    }
  },
  watch: {
    isAuthenticated(newVal) {
      if (newVal) {
        const loginPath = '/admin/login';
        if (this.$route.path === loginPath) {
          this.$router.push('/admin/dashboard');
        }
      } else {
        this.checkLogin();
      }
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
      const loginPath = '/admin/login';
      if (!this.isAuthenticated && this.$route.path !== loginPath) {
        this.$router.push(loginPath);
      }
    },
    // 切换侧边栏
    toggleCollapse() {
      this.isCollapsed = !this.isCollapsed;
    },
    // 返回首页
    goHome() {
      this.$router.push('/admin/dashboard');
    },
    // 处理下拉菜单命令
    handleCommand(command) {
      switch (command) {
        case 'dashboard':
          this.$router.push('/admin/dashboard');
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
        this.$router.push('/admin/login');
        this.$message.success('已退出登录');
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
}

/* 侧边栏 */
.sidebar {
  width: 210px;
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.sidebar.collapsed {
  width: 64px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4a;
  cursor: pointer;
}

.logo i {
  font-size: 28px;
  color: #409eff;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  margin-left: 10px;
}

.sidebar .el-menu {
  border-right: none;
}

/* 主内容区 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航 */
.header {
  height: 60px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-left .el-icon-fold {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
  color: #666;
}

.header-right {
  display: flex;
  align-items: center;
}

.dropdown-trigger {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-name {
  margin: 0 8px;
  font-size: 14px;
  color: #333;
}

/* 内容区域 */
.main {
  flex: 1;
  padding: 20px;
  background-color: #f0f2f5;
  overflow-y: auto;
}

/* 响应式 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1001;
  }
  
  .sidebar:not(.collapsed) {
    box-shadow: 2px 0 6px rgba(0, 0, 0, 0.15);
  }
  
  .main-container {
    margin-left: 0;
  }
  
  .header {
    padding: 0 15px;
  }
  
  .user-name {
    display: none;
  }
  
  .main {
    padding: 15px;
  }
}
</style>
