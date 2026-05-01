<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <div class="logo">
          <i class="el-icon-s-promotion"></i>
        </div>
        <div class="title-section">
          <h1>智慧招聘平台</h1>
          <p>企业管理后台</p>
        </div>
      </div>
      
      <div class="login-tabs">
        <div 
          class="tab-item"
          :class="{ active: loginRole === 'hr' }"
          @click="switchRole('hr')"
        >
          <i class="el-icon-office-building"></i>
          <span>企业HR登录</span>
        </div>
        <div 
          class="tab-item"
          :class="{ active: loginRole === 'admin' }"
          @click="switchRole('admin')"
        >
          <i class="el-icon-s-tools"></i>
          <span>平台管理员登录</span>
        </div>
      </div>
      
      <div class="login-form">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              :placeholder="loginRole === 'hr' ? '请输入企业账号' : '请输入管理员账号'"
              prefix-icon="el-icon-user"
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              show-password
              @keyup.enter.native="handleLogin"
            ></el-input>
          </el-form-item>
          
          <el-form-item prop="captcha" v-if="showCaptcha">
            <div class="captcha-row">
              <el-input
                v-model="loginForm.captcha"
                placeholder="请输入验证码"
                prefix-icon="el-icon-key"
                maxlength="4"
                style="width: 200px"
              ></el-input>
              <div class="captcha-img" @click="refreshCaptcha">
                <span>{{ captchaCode }}</span>
              </div>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              :loading="loading"
              @click="handleLogin"
              style="width: 100%; height: 44px; font-size: 16px"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <div class="login-footer">
        <div class="quick-login">
          <span class="label">测试账号：</span>
          <el-button type="text" @click="quickLogin('hr')">HR: hr@company.com</el-button>
          <el-button type="text" @click="quickLogin('admin')">管理员: admin@platform.com</el-button>
        </div>
      </div>
    </div>
    
    <div class="login-decoration">
      <div class="decoration-bg"></div>
      <div class="decoration-content">
        <h2>智慧招聘，连接人才与机会</h2>
        <p>高效的招聘管理平台，助力企业人才战略</p>
        <div class="features">
          <div class="feature-item">
            <i class="el-icon-user-solid"></i>
            <span>高效人才管理</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-document-checked"></i>
            <span>智能简历筛选</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-s-data"></i>
            <span>数据可视化</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { post } from '@/utils/request';
import { useUserStore } from '@/web/stores/user';

export default {
  name: 'WebLogin',
  data() {
    return {
      loginRole: 'hr',
      loading: false,
      showCaptcha: false,
      rememberMe: false,
      captchaCode: 'A8X3',
      loginForm: {
        username: '',
        password: '',
        captcha: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码不能少于6位', trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    userStore() {
      return useUserStore();
    }
  },
  methods: {
    switchRole(role) {
      this.loginRole = role;
      this.loginForm = {
        username: '',
        password: '',
        captcha: ''
      };
    },
    refreshCaptcha() {
      const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
      let result = '';
      for (let i = 0; i < 4; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
      }
      this.captchaCode = result;
    },
    quickLogin(role) {
      this.loginRole = role;
      if (role === 'hr') {
        this.loginForm.username = 'hr@company.com';
        this.loginForm.password = '123456';
      } else {
        this.loginForm.username = 'admin@platform.com';
        this.loginForm.password = '123456';
      }
    },
    async handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true;
          try {
            const url = this.loginRole === 'hr' ? '/company/login' : '/admin/login';
            const res = await post(url, this.loginForm);
            
            if (res.code === 200) {
              const userInfo = {
                ...res.data.userInfo,
                role: this.loginRole
              };
              
              this.userStore.login(res.data.token, userInfo);
              this.$message.success('登录成功');
              
              const redirect = this.$route.query.redirect || '/dashboard';
              this.$router.replace(redirect);
            } else {
              this.$message.error(res.message || '登录失败');
            }
          } catch (error) {
            this.$message.error('登录失败，请重试');
          } finally {
            this.loading = false;
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
}

.login-container {
  width: 420px;
  min-height: 100vh;
  background: #fff;
  padding: 40px 40px;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.05);
  z-index: 10;
  position: relative;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border-radius: 12px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.logo i {
  font-size: 32px;
  color: #fff;
}

.title-section h1 {
  font-size: 24px;
  color: #303133;
  margin: 0 0 6px 0;
  font-weight: 600;
}

.title-section p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-tabs {
  display: flex;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 30px;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 0;
  cursor: pointer;
  position: relative;
  transition: all 0.3s;
}

.tab-item i {
  font-size: 20px;
  color: #909399;
  transition: all 0.3s;
}

.tab-item span {
  font-size: 14px;
  color: #909399;
  transition: all 0.3s;
}

.tab-item.active i,
.tab-item.active span {
  color: #409eff;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 2px;
  background: #409eff;
  border-radius: 1px;
}

.login-form {
  padding: 0 10px;
}

.captcha-row {
  display: flex;
  align-items: center;
  gap: 15px;
}

.captcha-img {
  width: 120px;
  height: 40px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: 1px solid #dcdfe6;
}

.captcha-img span {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  letter-spacing: 4px;
  font-style: italic;
}

.login-footer {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f5f5f5;
}

.quick-login {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.quick-login .label {
  font-size: 13px;
  color: #909399;
}

.quick-login .el-button {
  font-size: 13px;
  padding: 5px 12px;
}

.login-decoration {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.decoration-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 50%, #409eff 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.decoration-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
  padding: 0 80px;
}

.decoration-content h2 {
  font-size: 32px;
  margin: 0 0 16px 0;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.decoration-content p {
  font-size: 16px;
  margin: 0 0 40px 0;
  opacity: 0.9;
}

.features {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.feature-item i {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.feature-item span {
  font-size: 14px;
  opacity: 0.9;
}

@media (max-width: 1200px) {
  .login-decoration {
    display: none;
  }
  
  .login-container {
    width: 100%;
    max-width: 420px;
    margin: 0 auto;
  }
}
</style>
