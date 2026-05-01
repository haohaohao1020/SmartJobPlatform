<template>
  <div class="login-page">
    <div class="login-header">
      <div class="logo">
        <i class="el-icon-s-promotion"></i>
      </div>
      <h1>智慧招聘</h1>
      <p>求职者端</p>
    </div>
    
    <div class="login-form">
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
        <el-form-item prop="phone">
          <el-input
            v-model="loginForm.phone"
            placeholder="请输入手机号"
            prefix-icon="el-icon-mobile-phone"
            maxlength="11"
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
        
        <el-form-item>
          <el-button 
            type="primary" 
            size="medium" 
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <router-link to="/register">还没有账号？去注册</router-link>
      </div>
      
      <div class="quick-login">
        <p>快速体验（测试账号）</p>
        <div class="test-accounts">
          <div class="account-item" @click="quickLogin('13800138001', '123456')">
            <span>求职者: 13800138001</span>
            <span class="label">点击登录</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { post } from '@/utils/request';
import { useUserStore } from '@/uiapp/stores/user';

export default {
  name: 'UILogin',
  data() {
    return {
      loading: false,
      loginForm: {
        phone: '',
        password: ''
      },
      loginRules: {
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码不能少于6位', trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    async handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true;
          try {
            const res = await post('/user/login', this.loginForm);
            if (res.code === 200) {
              const userStore = useUserStore();
              userStore.login(res.data.token, res.data.userInfo);
              this.$message.success('登录成功');
              
              const redirect = this.$route.query.redirect || '/jobs';
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
    },
    quickLogin(phone, password) {
      this.loginForm.phone = phone;
      this.loginForm.password = password;
      this.handleLogin();
    }
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  display: flex;
  flex-direction: column;
  padding: 40px 20px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header .logo {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-header .logo i {
  font-size: 40px;
  color: #fff;
}

.login-header h1 {
  font-size: 28px;
  color: #fff;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.login-header p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

.login-form {
  background: #fff;
  border-radius: 16px;
  padding: 30px 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-form .el-input {
  height: 48px;
}

.login-form .el-input ::v-deep .el-input__inner {
  height: 48px;
  border-radius: 24px;
  padding-left: 45px;
  background: #f5f7fa;
  border: none;
}

.login-form .el-input ::v-deep .el-input__prefix {
  left: 15px;
}

.login-form .el-input ::v-deep .el-input__prefix i {
  color: #909399;
  font-size: 18px;
}

.login-form .el-button--primary {
  width: 100%;
  height: 48px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 500;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
}

.login-footer a {
  color: #409eff;
  font-size: 14px;
}

.quick-login {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.quick-login p {
  text-align: center;
  font-size: 13px;
  color: #909399;
  margin-bottom: 15px;
}

.test-accounts {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.account-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.account-item:active {
  background: #e4e7ed;
  transform: scale(0.98);
}

.account-item span:first-child {
  font-size: 14px;
  color: #606266;
}

.account-item .label {
  font-size: 12px;
  color: #409eff;
}
</style>
