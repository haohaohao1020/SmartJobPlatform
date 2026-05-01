<template>
  <div class="password-page">
    <div class="page-header">
      <div class="back-btn" @click="$router.back()">
        <i class="el-icon-arrow-left"></i>
      </div>
      <span class="title">修改密码</span>
    </div>
    
    <div class="form-card">
      <div class="form-desc">
        <i class="el-icon-warning-outline"></i>
        <span>为保障您的账户安全，建议定期更换密码</span>
      </div>
      
      <el-form ref="passwordForm" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
            maxlength="20"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码（6-20位）"
            show-password
            maxlength="20"
          ></el-input>
          <div class="strength-bar" v-if="passwordForm.newPassword">
            <div 
              class="strength-item"
              :class="{ active: strength >= 1 }"
            ></div>
            <div 
              class="strength-item"
              :class="{ active: strength >= 2 }"
            ></div>
            <div 
              class="strength-item"
              :class="{ active: strength >= 3 }"
            ></div>
            <span class="strength-text">{{ strengthText }}</span>
          </div>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
            maxlength="20"
          ></el-input>
        </el-form-item>
      </el-form>
      
      <div class="tips-section">
        <div class="tips-title">
          <i class="el-icon-info"></i> 密码规则
        </div>
        <ul class="tips-list">
          <li>密码长度为 6-20 位字符</li>
          <li>建议包含大小写字母、数字和特殊字符</li>
          <li>避免使用连续相同的字符</li>
          <li>不要使用与用户名相同的密码</li>
        </ul>
      </div>
    </div>
    
    <div class="bottom-action">
      <el-button type="primary" size="large" :loading="submitting" @click="submitForm">
        确认修改
      </el-button>
    </div>
  </div>
</template>

<script>
import { post } from '@/utils/request';

export default {
  name: 'UIChangePassword',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };

    return {
      submitting: false,
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' },
          { 
            validator: (rule, value, callback) => {
              if (value === this.passwordForm.oldPassword) {
                callback(new Error('新密码不能与原密码相同'));
              } else {
                callback();
              }
            }, 
            trigger: 'blur' 
          }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    strength() {
      const pwd = this.passwordForm.newPassword;
      if (!pwd) return 0;
      
      let level = 0;
      
      if (pwd.length >= 6) level++;
      if (/[A-Z]/.test(pwd) && /[a-z]/.test(pwd)) level++;
      if (/\d/.test(pwd)) level++;
      if (/[!@#$%^&*(),.?":{}|<>]/.test(pwd)) level++;
      
      if (level <= 1) return 1;
      if (level <= 2) return 2;
      return 3;
    },
    strengthText() {
      const map = {
        0: '',
        1: '弱',
        2: '中',
        3: '强'
      };
      return map[this.strength] || '';
    }
  },
  methods: {
    submitForm() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          this.submitting = true;
          try {
            const res = await post('/user/change-password', {
              oldPassword: this.passwordForm.oldPassword,
              newPassword: this.passwordForm.newPassword
            });
            
            if (res.code === 200) {
              this.$message.success('密码修改成功，请重新登录');
              localStorage.removeItem('uiapp_token');
              localStorage.removeItem('uiapp_user_info');
              setTimeout(() => {
                this.$router.replace('/login');
              }, 1500);
            } else {
              this.$message.error(res.message || '修改失败');
            }
          } catch (error) {
            this.$message.error('修改失败，请重试');
          } finally {
            this.submitting = false;
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.password-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 80px;
}

/* 页面头部 */
.page-header {
  display: flex;
  align-items: center;
  height: 44px;
  background: #fff;
  padding: 0 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  position: relative;
}

.back-btn {
  position: absolute;
  left: 15px;
  font-size: 18px;
  color: #606266;
  cursor: pointer;
}

.page-header .title {
  flex: 1;
  text-align: center;
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

/* 表单卡片 */
.form-card {
  background: #fff;
  margin: 10px;
  border-radius: 12px;
  padding: 20px 15px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.form-desc {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background: #ecf5ff;
  border-radius: 8px;
  margin-bottom: 20px;
}

.form-desc i {
  font-size: 16px;
  color: #409eff;
  flex-shrink: 0;
  margin-top: 2px;
}

.form-desc span {
  font-size: 13px;
  color: #409eff;
  line-height: 1.6;
}

/* 强度条 */
.strength-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
}

.strength-item {
  width: 60px;
  height: 4px;
  background: #e4e7ed;
  border-radius: 2px;
  transition: all 0.3s;
}

.strength-item.active:nth-child(1) {
  background: #f56c6c;
}

.strength-item.active:nth-child(2) {
  background: #e6a23c;
}

.strength-item.active:nth-child(3) {
  background: #67c23a;
}

.strength-text {
  font-size: 12px;
  color: #909399;
  margin-left: 5px;
}

/* 提示区域 */
.tips-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f5f5f5;
}

.tips-title {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.tips-title i {
  color: #409eff;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  font-size: 12px;
  color: #909399;
  line-height: 1.8;
  padding-left: 15px;
  position: relative;
}

.tips-list li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  width: 4px;
  height: 4px;
  background: #c0c4cc;
  border-radius: 50%;
}

/* 底部操作 */
.bottom-action {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 10px 15px;
  background: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  max-width: 750px;
  margin: 0 auto;
}

.bottom-action .el-button {
  width: 100%;
  border-radius: 22px;
  height: 44px;
  font-size: 15px;
}

@media screen and (min-width: 750px) {
  .bottom-action {
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>
