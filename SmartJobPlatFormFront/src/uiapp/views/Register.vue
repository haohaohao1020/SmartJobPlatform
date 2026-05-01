<template>
  <div class="register-page">
    <div class="register-header">
      <div class="back-btn" @click="$router.back()">
        <i class="el-icon-arrow-left"></i>
      </div>
      <h2>注册账号</h2>
    </div>
    
    <div class="register-form">
      <el-form ref="registerForm" :model="registerForm" :rules="registerRules" label-width="0">
        <el-form-item prop="phone">
          <div class="form-row">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              prefix-icon="el-icon-mobile-phone"
              maxlength="11"
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item prop="code">
          <div class="form-row">
            <el-input
              v-model="registerForm.code"
              placeholder="请输入验证码"
              prefix-icon="el-icon-key"
              maxlength="6"
              style="flex: 1; margin-right: 10px"
            ></el-input>
            <el-button 
              type="primary" 
              size="small"
              :disabled="counting || !registerForm.phone"
              @click="sendCode"
            >
              {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item prop="name">
          <div class="form-row">
            <el-input
              v-model="registerForm.name"
              placeholder="请输入姓名"
              prefix-icon="el-icon-user"
              maxlength="20"
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item prop="password">
          <div class="form-row">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码（6-20位）"
              prefix-icon="el-icon-lock"
              show-password
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <div class="form-row">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              prefix-icon="el-icon-lock"
              show-password
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            :loading="loading"
            @click="handleRegister"
            style="width: 100%; height: 44px; border-radius: 22px"
          >
            注 册
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="agreement">
        <el-checkbox v-model="agreed">
          <span class="agreement-text">我已阅读并同意</span>
          <span class="link">《用户协议》</span>
          <span class="agreement-text">和</span>
          <span class="link">《隐私政策》</span>
        </el-checkbox>
      </div>
    </div>
  </div>
</template>

<script>
import { post } from '@/utils/request';

export default {
  name: 'UIRegister',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };

    return {
      loading: false,
      agreed: false,
      counting: false,
      countdown: 0,
      registerForm: {
        phone: '',
        code: '',
        name: '',
        password: '',
        confirmPassword: ''
      },
      registerRules: {
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { len: 6, message: '验证码为6位数字', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, message: '姓名至少2个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    sendCode() {
      if (!/^1[3-9]\d{9}$/.test(this.registerForm.phone)) {
        this.$message.warning('请输入正确的手机号');
        return;
      }
      
      this.counting = true;
      this.countdown = 60;
      
      const timer = setInterval(() => {
        this.countdown--;
        if (this.countdown <= 0) {
          clearInterval(timer);
          this.counting = false;
        }
      }, 1000);
      
      this.$message.success('验证码已发送（测试验证码：123456）');
    },
    async handleRegister() {
      if (!this.agreed) {
        this.$message.warning('请先阅读并同意用户协议和隐私政策');
        return;
      }
      
      this.$refs.registerForm.validate(async (valid) => {
        if (valid) {
          this.loading = true;
          try {
            const res = await post('/user/register', this.registerForm);
            if (res.code === 200) {
              this.$message.success('注册成功，请登录');
              this.$router.replace('/login');
            } else {
              this.$message.error(res.message || '注册失败');
            }
          } catch (error) {
            this.$message.error('注册失败，请重试');
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
.register-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.register-header {
  position: relative;
  height: 44px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.back-btn {
  position: absolute;
  left: 15px;
  font-size: 18px;
  color: #606266;
  cursor: pointer;
}

.register-header h2 {
  font-size: 16px;
  color: #303133;
  margin: 0;
  font-weight: 500;
}

.register-form {
  padding: 20px;
}

.form-row {
  display: flex;
  align-items: center;
}

.el-form-item {
  margin-bottom: 15px;
}

.el-input ::v-deep .el-input__inner {
  height: 44px;
  border-radius: 22px;
  padding-left: 42px;
  background: #fff;
  border: 1px solid #e4e7ed;
}

.el-input ::v-deep .el-input__prefix {
  left: 12px;
}

.el-input ::v-deep .el-input__prefix i {
  color: #909399;
  font-size: 16px;
}

.agreement {
  margin-top: 15px;
  text-align: center;
}

.agreement ::v-deep .el-checkbox__label {
  font-size: 12px;
}

.agreement-text {
  color: #606266;
  font-size: 12px;
}

.link {
  color: #409eff;
  font-size: 12px;
}
</style>
