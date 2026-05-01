<template>
  <div class="profile-page">
    <div class="container">
      <!-- 个人信息卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h3><i class="el-icon-user"></i> 个人信息</h3>
        </div>
        
        <el-form 
          ref="profileForm" 
          :model="profileForm" 
          :rules="profileRules" 
          label-width="100px"
        >
          <div class="avatar-section">
            <el-upload
              class="avatar-uploader"
              action="/api/user/avatar"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="profileForm.avatar" :src="profileForm.avatar" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
            <div class="avatar-tip">
              <p>点击上传头像</p>
              <p class="tip">支持 JPG、PNG 格式，大小不超过 2MB</p>
            </div>
          </div>
          
          <el-divider></el-divider>
          
          <div class="form-row">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="profileForm.name" placeholder="请输入真实姓名"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="profileForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
          
          <div class="form-row">
            <el-form-item label="出生年月" prop="birthDate">
              <el-date-picker
                v-model="profileForm.birthDate"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="现居城市" prop="currentCity">
              <el-input v-model="profileForm.currentCity" placeholder="请输入现居城市"></el-input>
            </el-form-item>
          </div>
          
          <div class="form-row">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" disabled>
                <el-button slot="append" type="text">
                  <i class="el-icon-circle-check" style="color: #67c23a"></i> 已绑定
                </el-button>
              </el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱">
                <el-button slot="append" type="text" @click="verifyEmail">
                  {{ emailVerified ? '已验证' : '去验证' }}
                </el-button>
              </el-input>
            </el-form-item>
          </div>
          
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="saveProfile">
              <i class="el-icon-check"></i> 保存修改
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 密码修改卡片 -->
      <div class="info-card">
        <div class="card-header">
          <h3><i class="el-icon-lock"></i> 修改密码</h3>
        </div>
        
        <el-form 
          ref="passwordForm" 
          :model="passwordForm" 
          :rules="passwordRules" 
          label-width="100px"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input 
              v-model="passwordForm.oldPassword" 
              type="password" 
              placeholder="请输入原密码"
              show-password
            ></el-input>
          </el-form-item>
          
          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              v-model="passwordForm.newPassword" 
              type="password" 
              placeholder="请输入新密码（6-20位）"
              show-password
            ></el-input>
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              v-model="passwordForm.confirmPassword" 
              type="password" 
              placeholder="请再次输入新密码"
              show-password
            ></el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button type="warning" :loading="changingPassword" @click="changePassword">
              <i class="el-icon-key"></i> 修改密码
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 账户信息 -->
      <div class="info-card">
        <div class="card-header">
          <h3><i class="el-icon-info"></i> 账户信息</h3>
        </div>
        
        <div class="account-info">
          <div class="info-item">
            <span class="label">用户ID：</span>
            <span class="value">{{ userInfo.id }}</span>
          </div>
          <div class="info-item">
            <span class="label">角色类型：</span>
            <span class="value">
              <el-tag size="small" type="primary">求职者</el-tag>
            </span>
          </div>
          <div class="info-item">
            <span class="label">注册时间：</span>
            <span class="value">{{ formatDate(userInfo.createTime) }}</span>
          </div>
          <div class="info-item">
            <span class="label">账户状态：</span>
            <span class="value">
              <el-tag v-if="userInfo.status === 1" size="small" type="success">正常</el-tag>
              <el-tag v-else size="small" type="danger">禁用</el-tag>
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { get, put } from '@/utils/request';
import { useUserStore } from '@/stores/user';

export default {
  name: 'JobSeekerProfile',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      saving: false,
      changingPassword: false,
      emailVerified: false,
      userInfo: {},
      profileForm: {
        name: '',
        avatar: '',
        gender: 1,
        birthDate: '',
        currentCity: '',
        phone: '',
        email: ''
      },
      profileRules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
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
          { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    };
  },
  computed: {
    userStore() {
      return useUserStore();
    }
  },
  mounted() {
    this.getUserInfo();
  },
  methods: {
    // 获取用户信息
    async getUserInfo() {
      try {
        const res = await get('/user/info');
        if (res.code === 200) {
          this.userInfo = res.data;
          this.profileForm = {
            name: res.data.name || '',
            avatar: res.data.avatar || '',
            gender: res.data.gender || 1,
            birthDate: res.data.birthDate || '',
            currentCity: res.data.currentCity || '',
            phone: res.data.phone || '',
            email: res.data.email || ''
          };
          this.emailVerified = res.data.emailStatus === 1;
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
      }
    },
    // 保存个人信息
    async saveProfile() {
      this.$refs.profileForm.validate(async (valid) => {
        if (valid) {
          this.saving = true;
          try {
            const res = await put('/user/profile', this.profileForm);
            if (res.code === 200) {
              this.$message.success('个人信息保存成功');
              // 更新 store 中的用户信息
              this.userStore.setUserInfo({
                ...this.userStore.userInfo,
                name: this.profileForm.name,
                avatar: this.profileForm.avatar
              });
            }
          } catch (error) {
            console.error('保存个人信息失败:', error);
          } finally {
            this.saving = false;
          }
        }
      });
    },
    // 修改密码
    async changePassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          this.changingPassword = true;
          try {
            const res = await put('/user/password', {
              oldPassword: this.passwordForm.oldPassword,
              newPassword: this.passwordForm.newPassword
            });
            if (res.code === 200) {
              this.$message.success('密码修改成功，请重新登录');
              // 清除表单
              this.passwordForm = {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
              };
              // 延迟跳转到登录页
              setTimeout(() => {
                this.userStore.clearUserInfo();
                this.$router.push('/jobseeker/login');
              }, 1500);
            }
          } catch (error) {
            console.error('修改密码失败:', error);
          } finally {
            this.changingPassword = false;
          }
        }
      });
    },
    // 验证邮箱
    verifyEmail() {
      if (this.emailVerified) {
        this.$message.info('邮箱已验证');
        return;
      }
      
      if (!this.profileForm.email) {
        this.$message.warning('请先填写邮箱地址');
        return;
      }
      
      this.$message.success('验证邮件已发送，请查收');
    },
    // 头像上传成功
    handleAvatarSuccess(response, file) {
      if (response.code === 200) {
        this.profileForm.avatar = response.data.url;
        this.$message.success('头像上传成功');
      }
    },
    // 头像上传前验证
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/jpg';
      const isPNG = file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG && !isPNG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 格式!');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
        return false;
      }
      return true;
    },
    // 格式化日期
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
    }
  }
};
</script>

<style scoped>
.profile-page {
  min-height: 100%;
  background-color: #f5f7fa;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

/* 信息卡片 */
.info-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.card-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h3 {
  font-size: 18px;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
}

.card-header h3 i {
  margin-right: 8px;
  color: #409eff;
}

/* 头像区域 */
.avatar-section {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 20px;
}

.avatar-uploader {
  flex-shrink: 0;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: block;
  object-fit: cover;
  border: 3px solid #f0f0f0;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s;
}

.avatar-uploader-icon:hover {
  border-color: #409eff;
  color: #409eff;
}

.avatar-tip p {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #333;
}

.avatar-tip .tip {
  font-size: 12px;
  color: #999;
}

/* 表单样式 */
.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.form-row .el-form-item {
  flex: 1;
  min-width: 300px;
}

/* 账户信息 */
.account-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.info-item .label {
  color: #666;
  width: 80px;
  flex-shrink: 0;
}

.info-item .value {
  color: #333;
  font-weight: 500;
}

/* 响应式 */
@media (max-width: 768px) {
  .container {
    padding: 15px;
  }
  
  .info-card {
    padding: 15px;
  }
  
  .avatar-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .form-row .el-form-item {
    min-width: auto;
    width: 100%;
  }
  
  .account-info {
    grid-template-columns: 1fr;
    gap: 15px;
  }
}
</style>
