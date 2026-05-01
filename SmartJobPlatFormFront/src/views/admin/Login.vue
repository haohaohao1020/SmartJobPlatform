<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <div class="logo">
          <i class="el-icon-s-custom"></i>
          <span class="logo-text">管理后台</span>
        </div>
        <h1 class="title">管理员登录</h1>
        <p class="subtitle">登录后管理平台运营</p>
      </div>
      
      <el-form 
        ref="loginForm" 
        :model="loginForm" 
        :rules="loginRules" 
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名" 
            prefix-icon="el-icon-user"
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
        <p>测试账号：admin / admin123</p>
      </div>
    </div>
  </div>
</template>

<script>
import { post } from '@/utils/request';
import { useUserStore } from '@/stores/user';

export default {
  name: 'AdminLogin',
  data() {
    return {
      loginForm: {
        username: 'admin',
        password: 'admin123',
        rememberMe: false
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
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
            const res = await post('/admin/login', this.loginForm);
            if (res.code === 200) {
              // 保存用户信息到 store
              const userStore = useUserStore();
              userStore.setUserInfo(res.data);
              
              this.$message.success('登录成功');
              // 跳转到数据控制台
              this.$router.push('/admin/dashboard');
            }
          } catch (error) {
            console.error('登录失败:', error);
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
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2c3e50 0%, #4ca1af 100%);
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
  color: #4ca1af;
  margin-right: 10px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  background: linear-gradient(135deg, #2c3e50 0%, #4ca1af 100%);
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
  background: linear-gradient(135deg, #2c3e50 0%, #4ca1af 100%);
  border: none;
}

.login-btn:hover {
  background: linear-gradient(135deg, #233140 0%, #3d8a96 100%);
}

.login-footer {
  margin-top: 30px;
  text-align: center;
}

.login-footer p {
  font-size: 13px;
  color: #999;
  margin: 0;
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
