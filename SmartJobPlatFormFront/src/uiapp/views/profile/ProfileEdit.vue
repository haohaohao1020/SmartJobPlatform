<template>
  <div class="profile-edit-page">
    <div class="page-header">
      <div class="back-btn" @click="$router.back()">
        <i class="el-icon-arrow-left"></i>
      </div>
      <span class="title">编辑资料</span>
      <div class="save-btn" @click="saveProfile">
        保存
      </div>
    </div>
    
    <div class="form-section">
      <div class="avatar-section">
        <span class="label">头像</span>
        <el-upload
          class="avatar-uploader"
          action="/api/upload/avatar"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
        >
          <el-avatar :size="80" :src="formData.avatar" class="avatar">
            <i class="el-icon-user"></i>
          </el-avatar>
          <div class="upload-tip">
            <i class="el-icon-camera"></i>
          </div>
        </el-upload>
      </div>
      
      <div class="form-item">
        <span class="label">姓名</span>
        <el-input
          v-model="formData.name"
          placeholder="请输入姓名"
          maxlength="20"
          class="input"
        ></el-input>
      </div>
      
      <div class="form-item">
        <span class="label">性别</span>
        <div class="radio-group">
          <el-radio v-model="formData.gender" label="男">男</el-radio>
          <el-radio v-model="formData.gender" label="女">女</el-radio>
        </div>
      </div>
      
      <div class="form-item">
        <span class="label">出生年月</span>
        <el-date-picker
          v-model="formData.birthday"
          type="date"
          placeholder="选择日期"
          format="yyyy-MM-dd"
          value-format="yyyy-MM-dd"
          style="width: 100%"
          class="input"
        ></el-date-picker>
      </div>
      
      <div class="form-item">
        <span class="label">手机号</span>
        <el-input
          v-model="formData.phone"
          placeholder="请输入手机号"
          maxlength="11"
          disabled
          class="input"
        >
          <el-button slot="append" type="text" size="small">已认证</el-button>
        </el-input>
      </div>
      
      <div class="form-item">
        <span class="label">邮箱</span>
        <el-input
          v-model="formData.email"
          placeholder="请输入邮箱"
          maxlength="50"
          class="input"
        ></el-input>
      </div>
      
      <div class="form-item">
        <span class="label">工作年限</span>
        <el-select
          v-model="formData.workYears"
          placeholder="请选择"
          style="width: 100%"
          class="input"
        >
          <el-option label="应届生" value="应届生"></el-option>
          <el-option label="1年以内" value="1年以内"></el-option>
          <el-option label="1-3年" value="1-3年"></el-option>
          <el-option label="3-5年" value="3-5年"></el-option>
          <el-option label="5-10年" value="5-10年"></el-option>
          <el-option label="10年以上" value="10年以上"></el-option>
        </el-select>
      </div>
      
      <div class="form-item">
        <span class="label">最高学历</span>
        <el-select
          v-model="formData.highestEducation"
          placeholder="请选择"
          style="width: 100%"
          class="input"
        >
          <el-option label="大专" value="大专"></el-option>
          <el-option label="本科" value="本科"></el-option>
          <el-option label="硕士" value="硕士"></el-option>
          <el-option label="博士" value="博士"></el-option>
        </el-select>
      </div>
      
      <div class="form-item">
        <span class="label">居住城市</span>
        <el-select
          v-model="formData.city"
          placeholder="请选择"
          filterable
          style="width: 100%"
          class="input"
        >
          <el-option label="北京" value="北京"></el-option>
          <el-option label="上海" value="上海"></el-option>
          <el-option label="广州" value="广州"></el-option>
          <el-option label="深圳" value="深圳"></el-option>
          <el-option label="杭州" value="杭州"></el-option>
          <el-option label="成都" value="成都"></el-option>
          <el-option label="武汉" value="武汉"></el-option>
          <el-option label="西安" value="西安"></el-option>
        </el-select>
      </div>
    </div>
    
    <div class="form-section">
      <div class="section-header">
        <span class="title">求职意向</span>
      </div>
      
      <div class="form-item">
        <span class="label">期望职位</span>
        <el-input
          v-model="formData.intentPosition"
          placeholder="请输入期望职位"
          maxlength="50"
          class="input"
        ></el-input>
      </div>
      
      <div class="form-item">
        <span class="label">期望城市</span>
        <el-select
          v-model="formData.intentCity"
          placeholder="请选择"
          filterable
          style="width: 100%"
          class="input"
        >
          <el-option label="北京" value="北京"></el-option>
          <el-option label="上海" value="上海"></el-option>
          <el-option label="广州" value="广州"></el-option>
          <el-option label="深圳" value="深圳"></el-option>
          <el-option label="杭州" value="杭州"></el-option>
        </el-select>
      </div>
      
      <div class="form-item">
        <span class="label">期望薪资</span>
        <div class="salary-input">
          <el-input-number
            v-model="formData.intentSalaryMin"
            :min="0"
            :max="100000"
            :step="1000"
            size="small"
            style="width: 120px"
          ></el-input-number>
          <span class="range">至</span>
          <el-input-number
            v-model="formData.intentSalaryMax"
            :min="0"
            :max="100000"
            :step="1000"
            size="small"
            style="width: 120px"
          ></el-input-number>
          <span class="unit">K/月</span>
        </div>
      </div>
      
      <div class="form-item">
        <span class="label">求职状态</span>
        <el-radio-group v-model="formData.jobStatus">
          <el-radio label="1">在职，考虑机会</el-radio>
          <el-radio label="2">离职，立即到岗</el-radio>
          <el-radio label="3">应届毕业生</el-radio>
        </el-radio-group>
      </div>
    </div>
    
    <div class="bottom-actions">
      <el-button type="default" size="large" @click="$router.back()">取消</el-button>
      <el-button type="primary" size="large" :loading="saving" @click="saveProfile">保存</el-button>
    </div>
  </div>
</template>

<script>
import { get, post } from '@/utils/request';
import { useUserStore } from '@/uiapp/stores/user';

export default {
  name: 'UIProfileEdit',
  data() {
    return {
      saving: false,
      formData: {
        name: '',
        avatar: '',
        gender: '',
        birthday: '',
        phone: '',
        email: '',
        workYears: '',
        highestEducation: '',
        city: '',
        intentPosition: '',
        intentCity: '',
        intentSalaryMin: 10000,
        intentSalaryMax: 20000,
        jobStatus: '2'
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
    async getUserInfo() {
      try {
        const res = await get('/user/info');
        if (res.code === 200 && res.data) {
          this.formData = { ...this.formData, ...res.data };
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
      }
    },
    async saveProfile() {
      if (!this.formData.name) {
        this.$message.warning('请输入姓名');
        return;
      }
      
      this.saving = true;
      try {
        const res = await post('/user/update', this.formData);
        if (res.code === 200) {
          this.$message.success('保存成功');
          
          if (this.userStore.userInfo) {
            const newUserInfo = { ...this.userStore.userInfo, ...this.formData };
            this.userStore.setUserInfo(newUserInfo);
          }
          
          this.$router.back();
        } else {
          this.$message.error(res.message || '保存失败');
        }
      } catch (error) {
        this.$message.error('保存失败');
      } finally {
        this.saving = false;
      }
    },
    handleAvatarSuccess(response) {
      if (response.code === 200) {
        this.formData.avatar = response.data.url;
      }
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 格式!');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
        return false;
      }
      return true;
    }
  }
};
</script>

<style scoped>
.profile-edit-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 80px;
}

/* 页面头部 */
.page-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
  background: #fff;
  padding: 0 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.back-btn {
  font-size: 18px;
  color: #606266;
  cursor: pointer;
}

.save-btn {
  font-size: 15px;
  color: #409eff;
  font-weight: 500;
  cursor: pointer;
}

.page-header .title {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

/* 表单区域 */
.form-section {
  background: #fff;
  margin: 10px;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.section-header {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f5f5f5;
}

.section-header .title {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}

/* 头像区域 */
.avatar-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  margin-bottom: 10px;
  border-bottom: 1px solid #f5f5f5;
}

.avatar-section .label {
  font-size: 14px;
  color: #606266;
}

.avatar-uploader {
  position: relative;
}

.avatar {
  border: 2px solid #f0f0f0;
}

.upload-tip {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  background: #409eff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
}

.upload-tip i {
  color: #fff;
  font-size: 14px;
}

/* 表单项 */
.form-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.form-item:last-child {
  border-bottom: none;
}

.form-item .label {
  width: 90px;
  font-size: 14px;
  color: #606266;
  flex-shrink: 0;
}

.form-item .input {
  flex: 1;
}

.radio-group {
  flex: 1;
}

/* 薪资输入 */
.salary-input {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.salary-input .range {
  color: #909399;
  font-size: 14px;
}

.salary-input .unit {
  font-size: 13px;
  color: #606266;
}

/* 底部操作 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  padding: 10px 15px;
  background: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  gap: 10px;
  max-width: 750px;
  margin: 0 auto;
}

.bottom-actions .el-button {
  flex: 1;
  border-radius: 22px;
  height: 44px;
  font-size: 15px;
}

@media screen and (min-width: 750px) {
  .bottom-actions {
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>
