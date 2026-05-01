<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <div class="logo">
          <i class="el-icon-suitcase"></i>
          <span class="logo-text">智慧招聘平台</span>
        </div>
        <h1 class="title">求职者登录</h1>
        <p class="subtitle">登录后可以浏览岗位、投递简历</p>
      </div>
      
      <el-form 
        ref="loginForm" 
        :model="loginForm" 
        :rules="loginRules" 
        class="login-form"
      >
        <el-form-item prop="phone">
          <el-input 
            v-model="loginForm.phone" 
            placeholder="请输入手机号" 
            prefix-icon="el-icon-mobile-phone"
            size="large"
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码" 
            prefix-icon="el-icon-lock"
            size="large"
            show-password
            @keyup.enter.native="handleLogin"
          ></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            :loading="loading" 
            class="login-btn"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <p>测试账号：任意手机号 + 任意密码</p>
        <p>还没有账号？<a href="javascript:;" @click="goToRegister">立即注册</a></p>
      </div>
    </div>
  </div>
</template>

<script>
import { post } from '@/utils/request';
import { useUserStore } from '@/stores/user';

export default {
  name: 'JobSeekerLogin',
  data() {
    return {
      loginForm: {
        phone: '13800138000',
        password: '123456',
        rememberMe: false
      },
      loginRules: {
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
        ]
      },
      loading: false
    };
  },
  methods: {
    // 处理登录
    async handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true;
          try {
            const res = await post('/jobseeker/login', this.loginForm);
            if (res.code === 200) {
              // 保存用户信息到 store
              const userStore = useUserStore();
              userStore.setUserInfo(res.data);
              
              this.$message.success('登录成功');
              // 跳转到岗位列表页
              this.$router.push('/jobseeker/jobs');
            }
          } catch (error) {
            console.error('登录失败:', error);
          } finally {
            this.loading = false;
          }
        }
      });
    },
    // 跳转到注册
    goToRegister() {
      this.$message.info('注册功能开发中');
    }
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.logo i {
  font-size: 36px;
  color: #409eff;
  margin-right: 10px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.login-form {
  margin-top: 30px;
}

.login-btn {
  width: 100%;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.login-btn:hover {
  background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
}

.login-footer {
  margin-top: 30px;
  text-align: center;
}

.login-footer p {
  font-size: 13px;
  color: #999;
  margin: 10px 0;
}

.login-footer a {
  color: #409eff;
  text-decoration: none;
}

.login-footer a:hover {
  text-decoration: underline;
}

/* 响应式 */
@media (max-width: 480px) {
  .login-container {
    padding: 30px 20px;
  }
  
  .logo-text {
    font-size: 20px;
  }
  
  .title {
    font-size: 20px;
  }
}
</style>
