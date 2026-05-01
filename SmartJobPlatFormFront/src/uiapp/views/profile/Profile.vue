<template>
  <div class="profile-page">
    <div class="profile-header" v-if="userStore.isLoggedIn">
      <div class="user-info" @click="goToEdit">
        <el-avatar :size="60" :src="userStore.avatar" class="avatar">
          <i class="el-icon-user"></i>
        </el-avatar>
        <div class="info-detail">
          <h3>{{ userStore.userName }}</h3>
          <p class="user-desc">完善资料，让HR更快找到你</p>
        </div>
        <i class="el-icon-arrow-right"></i>
      </div>
      
      <div class="user-stats">
        <div class="stat-item" @click="goToApplications">
          <span class="stat-value">{{ stats.applicationCount }}</span>
          <span class="stat-label">投递数</span>
        </div>
        <div class="stat-item" @click="goToFavorites">
          <span class="stat-value">{{ stats.favoriteCount }}</span>
          <span class="stat-label">收藏数</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ stats.interviewCount }}</span>
          <span class="stat-label">面试数</span>
        </div>
        <div class="stat-item" @click="goToResume">
          <span class="stat-value" :class="{ has: hasResume }">
            {{ hasResume ? '已完善' : '待完善' }}
          </span>
          <span class="stat-label">简历</span>
        </div>
      </div>
    </div>
    
    <div class="profile-header login-prompt" v-else>
      <div class="login-prompt-content" @click="goToLogin">
        <div class="login-icon">
          <i class="el-icon-user"></i>
        </div>
        <div class="login-info">
          <h3>点击登录</h3>
          <p>登录后体验完整功能</p>
        </div>
        <i class="el-icon-arrow-right"></i>
      </div>
    </div>
    
    <div class="menu-section">
      <div class="menu-group">
        <div class="menu-item" @click="goToResume">
          <div class="menu-icon resume">
            <i class="el-icon-document"></i>
          </div>
          <span class="menu-label">我的简历</span>
          <i class="el-icon-arrow-right menu-arrow"></i>
        </div>
        
        <div class="menu-item" @click="goToApplications">
          <div class="menu-icon application">
            <i class="el-icon-s-promotion"></i>
          </div>
          <span class="menu-label">我的投递</span>
          <span class="menu-badge" v-if="stats.applicationCount > 0">{{ stats.applicationCount }}</span>
          <i class="el-icon-arrow-right menu-arrow"></i>
        </div>
        
        <div class="menu-item" @click="goToFavorites">
          <div class="menu-icon favorite">
            <i class="el-icon-star-off"></i>
          </div>
          <span class="menu-label">我的收藏</span>
          <span class="menu-badge" v-if="stats.favoriteCount > 0">{{ stats.favoriteCount }}</span>
          <i class="el-icon-arrow-right menu-arrow"></i>
        </div>
      </div>
    </div>
    
    <div class="menu-section">
      <div class="menu-group">
        <div class="menu-item" @click="goToEdit">
          <div class="menu-icon profile">
            <i class="el-icon-edit"></i>
          </div>
          <span class="menu-label">编辑资料</span>
          <i class="el-icon-arrow-right menu-arrow"></i>
        </div>
        
        <div class="menu-item" @click="goToChangePassword">
          <div class="menu-icon password">
            <i class="el-icon-lock"></i>
          </div>
          <span class="menu-label">修改密码</span>
          <i class="el-icon-arrow-right menu-arrow"></i>
        </div>
      </div>
    </div>
    
    <div class="menu-section">
      <div class="menu-group">
        <div class="menu-item" @click="showHelp">
          <div class="menu-icon help">
            <i class="el-icon-question"></i>
          </div>
          <span class="menu-label">帮助中心</span>
          <i class="el-icon-arrow-right menu-arrow"></i>
        </div>
        
        <div class="menu-item" @click="showFeedback">
          <div class="menu-icon feedback">
            <i class="el-icon-edit-outline"></i>
          </div>
          <span class="menu-label">意见反馈</span>
          <i class="el-icon-arrow-right menu-arrow"></i>
        </div>
        
        <div class="menu-item" @click="showAbout">
          <div class="menu-icon about">
            <i class="el-icon-info"></i>
          </div>
          <span class="menu-label">关于我们</span>
          <i class="el-icon-arrow-right menu-arrow"></i>
        </div>
      </div>
    </div>
    
    <div class="logout-section" v-if="userStore.isLoggedIn">
      <div class="logout-btn" @click="handleLogout">
        退出登录
      </div>
    </div>
    
    <div class="version-info">
      <span>版本 1.0.0</span>
    </div>
  </div>
</template>

<script>
import { useUserStore } from '@/uiapp/stores/user';
import { get } from '@/utils/request';

export default {
  name: 'UIProfile',
  data() {
    return {
      hasResume: false,
      stats: {
        applicationCount: 0,
        favoriteCount: 0,
        interviewCount: 0
      }
    };
  },
  computed: {
    userStore() {
      return useUserStore();
    }
  },
  mounted() {
    if (this.userStore.isLoggedIn) {
      this.getUserStats();
      this.checkResume();
    }
  },
  activated() {
    if (this.userStore.isLoggedIn) {
      this.getUserStats();
      this.checkResume();
    }
  },
  methods: {
    async getUserStats() {
      try {
        const res = await get('/user/stats');
        if (res.code === 200) {
          this.stats = res.data;
        }
      } catch (error) {
        console.error('获取用户统计失败:', error);
      }
    },
    async checkResume() {
      try {
        const res = await get('/resume/detail');
        this.hasResume = res.code === 200 && res.data;
      } catch (error) {
        this.hasResume = false;
      }
    },
    goToLogin() {
      this.$router.push({
        path: '/login',
        query: { redirect: '/profile' }
      });
    },
    goToEdit() {
      if (!this.userStore.isLoggedIn) {
        this.goToLogin();
        return;
      }
      this.$router.push('/profile/edit');
    },
    goToResume() {
      if (!this.userStore.isLoggedIn) {
        this.goToLogin();
        return;
      }
      this.$router.push('/resume');
    },
    goToApplications() {
      if (!this.userStore.isLoggedIn) {
        this.goToLogin();
        return;
      }
      this.$router.push('/applications');
    },
    goToFavorites() {
      if (!this.userStore.isLoggedIn) {
        this.goToLogin();
        return;
      }
      this.$router.push('/favorites');
    },
    goToChangePassword() {
      if (!this.userStore.isLoggedIn) {
        this.goToLogin();
        return;
      }
      this.$router.push('/profile/password');
    },
    showHelp() {
      this.$message.info('帮助中心开发中');
    },
    showFeedback() {
      this.$message.info('意见反馈功能开发中');
    },
    showAbout() {
      this.$message.info('关于我们功能开发中');
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.userStore.logout();
        this.$message.success('已退出登录');
        this.$router.replace('/jobs');
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.profile-page {
  min-height: calc(100vh - 50px);
  background: #f5f5f5;
  padding-bottom: 20px;
}

/* 用户头部 */
.profile-header {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  padding: 20px 15px;
}

.login-prompt {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.login-prompt-content {
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
}

.login-icon {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-icon i {
  font-size: 30px;
  color: #fff;
}

.login-info {
  flex: 1;
}

.login-info h3 {
  font-size: 18px;
  color: #fff;
  margin: 0 0 5px 0;
  font-weight: 500;
}

.login-info p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.login-prompt-content .el-icon-arrow-right {
  color: rgba(255, 255, 255, 0.6);
  font-size: 18px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  cursor: pointer;
}

.avatar {
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.info-detail {
  flex: 1;
}

.info-detail h3 {
  font-size: 18px;
  color: #fff;
  margin: 0 0 5px 0;
  font-weight: 500;
}

.user-desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.user-info .el-icon-arrow-right {
  color: rgba(255, 255, 255, 0.6);
  font-size: 18px;
}

/* 用户统计 */
.user-stats {
  display: flex;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  padding: 15px 10px;
}

.stat-item {
  flex: 1;
  text-align: center;
  cursor: pointer;
}

.stat-value {
  display: block;
  font-size: 20px;
  color: #fff;
  font-weight: bold;
}

.stat-value.has {
  color: #67c23a;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

/* 菜单区域 */
.menu-section {
  margin: 10px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.menu-group {
  padding: 0 15px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  position: relative;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.menu-icon.resume {
  background: #ecf5ff;
}

.menu-icon.resume i {
  color: #409eff;
}

.menu-icon.application {
  background: #fdf6ec;
}

.menu-icon.application i {
  color: #e6a23c;
}

.menu-icon.favorite {
  background: #fef0f0;
}

.menu-icon.favorite i {
  color: #f56c6c;
}

.menu-icon.profile {
  background: #f0f9eb;
}

.menu-icon.profile i {
  color: #67c23a;
}

.menu-icon.password {
  background: #f4f4f5;
}

.menu-icon.password i {
  color: #909399;
}

.menu-icon.help {
  background: #f0f9eb;
}

.menu-icon.help i {
  color: #67c23a;
}

.menu-icon.feedback {
  background: #ecf5ff;
}

.menu-icon.feedback i {
  color: #409eff;
}

.menu-icon.about {
  background: #f4f4f5;
}

.menu-icon.about i {
  color: #909399;
}

.menu-icon i {
  font-size: 18px;
}

.menu-label {
  flex: 1;
  font-size: 15px;
  color: #303133;
}

.menu-badge {
  background: #f56c6c;
  color: #fff;
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 10px;
  margin-right: 5px;
  min-width: 16px;
  text-align: center;
}

.menu-arrow {
  color: #c0c4cc;
  font-size: 16px;
}

/* 退出登录 */
.logout-section {
  padding: 20px 15px;
}

.logout-btn {
  height: 44px;
  background: #fff;
  border-radius: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  color: #f56c6c;
  cursor: pointer;
  font-weight: 500;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

/* 版本信息 */
.version-info {
  text-align: center;
  padding: 15px;
  font-size: 12px;
  color: #c0c4cc;
}
</style>
