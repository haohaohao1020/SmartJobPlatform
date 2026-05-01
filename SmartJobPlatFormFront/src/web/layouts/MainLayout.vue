<template>
  <el-container class="main-layout">
    <el-aside :width="isCollapse ? '64px' : '210px'" class="aside">
      <div class="logo-section" @click="goToDashboard">
        <div class="logo">
          <i class="el-icon-s-promotion"></i>
        </div>
        <div class="logo-text" v-show="!isCollapse">
          <h3>智慧招聘</h3>
          <p v-if="userStore.isHR()">企业HR端</p>
          <p v-else>管理员端</p>
        </div>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/dashboard">
          <i class="el-icon-data-analysis"></i>
          <span slot="title">数据控制台</span>
        </el-menu-item>
        
        <el-submenu index="jobs-menu" v-if="userStore.isHR()">
          <template slot="title">
            <i class="el-icon-suitcase"></i>
            <span>岗位管理</span>
          </template>
          <el-menu-item index="/jobs">岗位列表</el-menu-item>
          <el-menu-item index="/jobs/publish">发布岗位</el-menu-item>
        </el-submenu>
        
        <el-menu-item index="/jobs" v-if="userStore.isAdmin()">
          <i class="el-icon-suitcase"></i>
          <span slot="title">岗位审核</span>
        </el-menu-item>
        
        <el-menu-item index="/resumes" v-if="userStore.isHR()">
          <i class="el-icon-document"></i>
          <span slot="title">简历管理</span>
        </el-menu-item>
        
        <el-menu-item index="/company" v-if="userStore.isHR()">
          <i class="el-icon-office-building"></i>
          <span slot="title">企业信息</span>
        </el-menu-item>
        
        <el-submenu index="admin-menu" v-if="userStore.isAdmin()">
          <template slot="title">
            <i class="el-icon-s-grid"></i>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/users">用户管理</el-menu-item>
          <el-menu-item index="/companies">企业审核</el-menu-item>
          <el-menu-item index="/announcements">公告管理</el-menu-item>
          <el-menu-item index="/settings">系统配置</el-menu-item>
        </el-submenu>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <div class="collapse-btn" @click="toggleCollapse">
            <i :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
          </div>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown class="user-dropdown">
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.avatar">
                <i class="el-icon-user"></i>
              </el-avatar>
              <span class="username">{{ userStore.userName }}</span>
              <el-tag v-if="userStore.isHR()" size="mini" type="primary">HR</el-tag>
              <el-tag v-else size="mini" type="danger">管理员</el-tag>
              <i class="el-icon-arrow-down"></i>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="goToProfile">
                <i class="el-icon-user"></i> 个人中心
              </el-dropdown-item>
              <el-dropdown-item @click.native="changePassword">
                <i class="el-icon-lock"></i> 修改密码
              </el-dropdown-item>
              <el-dropdown-item divided @click.native="handleLogout">
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
    
    <el-dialog title="修改密码" :visible.sync="passwordDialogVisible" width="400px">
      <el-form ref="passwordForm" :model="passwordForm" :rules="passwordRules" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPassword" :loading="submitting">确认</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
import { useUserStore } from '@/web/stores/user';
import { post } from '@/utils/request';

export default {
  name: 'WebMainLayout',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };

    return {
      isCollapse: false,
      passwordDialogVisible: false,
      submitting: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    userStore() {
      return useUserStore();
    },
    activeMenu() {
      return this.$route.path;
    },
    breadcrumbs() {
      const matched = this.$route.matched;
      const result = [];
      
      result.push({ path: '/dashboard', title: '首页' });
      
      matched.forEach(item => {
        if (item.meta && item.meta.title && item.meta.title !== '数据控制台') {
          result.push({ path: item.path, title: item.meta.title });
        }
      });
      
      return result;
    }
  },
  mounted() {
    this.userStore.restoreFromStorage();
  },
  methods: {
    toggleCollapse() {
      this.isCollapse = !this.isCollapse;
    },
    goToDashboard() {
      this.$router.push('/dashboard');
    },
    goToProfile() {
      this.$message.info('个人中心开发中');
    },
    changePassword() {
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      };
      this.passwordDialogVisible = true;
    },
    async submitPassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          this.submitting = true;
          try {
            const res = await post('/user/change-password', {
              oldPassword: this.passwordForm.oldPassword,
              newPassword: this.passwordForm.newPassword
            });
            
            if (res.code === 200) {
              this.$message.success('密码修改成功');
              this.passwordDialogVisible = false;
            } else {
              this.$message.error(res.message || '修改失败');
            }
          } catch (error) {
            this.$message.error('修改失败');
          } finally {
            this.submitting = false;
          }
        }
      });
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.userStore.logout();
        this.$message.success('已退出登录');
        this.$router.replace('/login');
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
}

.aside {
  background-color: #304156;
  transition: width 0.3s;
}

.logo-section {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  background-color: #2b3a4a;
  cursor: pointer;
}

.logo {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo i {
  color: #fff;
  font-size: 18px;
}

.logo-text {
  margin-left: 12px;
  flex: 1;
}

.logo-text h3 {
  font-size: 16px;
  color: #fff;
  margin: 0;
  font-weight: 600;
}

.logo-text p {
  font-size: 12px;
  color: #8492a6;
  margin: 2px 0 0 0;
}

.el-menu {
  border-right: none;
}

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
}

.collapse-btn:hover {
  background: #f5f7fa;
}

.collapse-btn i {
  font-size: 18px;
  color: #606266;
}

.el-breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #303133;
}

.user-info .el-icon-arrow-down {
  font-size: 12px;
  color: #909399;
}

.main {
  background: #f0f2f5;
  padding: 20px;
  min-height: calc(100vh - 60px);
}
</style>
