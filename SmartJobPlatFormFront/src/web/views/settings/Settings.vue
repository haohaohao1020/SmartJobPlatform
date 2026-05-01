<template>
  <div class="settings-page">
    <div class="container">
      <div class="settings-card">
        <div class="card-header">
          <h3><i class="el-icon-setting"></i> 系统配置</h3>
        </div>
        
        <el-form 
          ref="settingsForm" 
          :model="settingsForm" 
          :rules="settingsRules" 
          label-width="120px"
        >
          <div class="form-section">
            <div class="section-title">基础配置</div>
            
            <el-form-item label="平台名称" prop="siteName">
              <el-input 
                v-model="settingsForm.siteName" 
                placeholder="请输入平台名称"
                maxlength="50"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="平台描述" prop="siteDescription">
              <el-input
                v-model="settingsForm.siteDescription"
                type="textarea"
                :rows="2"
                placeholder="请输入平台描述"
                maxlength="200"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="平台LOGO" prop="siteLogo">
              <div class="upload-section">
                <el-upload
                  class="logo-uploader"
                  action="/api/company/logo"
                  :show-file-list="false"
                  :on-success="handleLogoSuccess"
                  :before-upload="beforeLogoUpload"
                >
                  <img v-if="settingsForm.siteLogo" :src="settingsForm.siteLogo" class="logo">
                  <div v-else class="upload-placeholder">
                    <i class="el-icon-plus"></i>
                    <span>上传LOGO</span>
                  </div>
                </el-upload>
                <div class="upload-tip">
                  <p>建议尺寸：200*60 像素</p>
                  <p class="sub-tip">支持 JPG、PNG 格式，大小不超过 2MB</p>
                </div>
              </div>
            </el-form-item>
          </div>
          
          <div class="form-section">
            <div class="section-title">联系信息</div>
            
            <div class="form-row">
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="settingsForm.contactPhone" placeholder="请输入联系电话"></el-input>
              </el-form-item>
              <el-form-item label="联系邮箱" prop="contactEmail">
                <el-input v-model="settingsForm.contactEmail" placeholder="请输入联系邮箱"></el-input>
              </el-form-item>
            </div>
            
            <el-form-item label="联系地址" prop="contactAddress">
              <el-input 
                v-model="settingsForm.contactAddress" 
                placeholder="请输入联系地址"
                maxlength="100"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="ICP备案号" prop="icpNumber">
              <el-input 
                v-model="settingsForm.icpNumber" 
                placeholder="请输入ICP备案号"
                maxlength="50"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="版权信息" prop="copyright">
              <el-input 
                v-model="settingsForm.copyright" 
                placeholder="请输入版权信息"
                maxlength="100"
              ></el-input>
            </el-form-item>
          </div>
          
          <div class="form-section">
            <div class="section-title">功能开关</div>
            
            <el-form-item label="用户注册">
              <el-switch
                v-model="settingsForm.enableRegister"
                active-text="开启"
                inactive-text="关闭"
              ></el-switch>
              <span class="switch-tip">关闭后用户无法注册新账号</span>
            </el-form-item>
            
            <el-form-item label="岗位推荐">
              <el-switch
                v-model="settingsForm.enableJobRecommend"
                active-text="开启"
                inactive-text="关闭"
              ></el-switch>
              <span class="switch-tip">开启后在首页展示热门岗位推荐</span>
            </el-form-item>
          </div>
          
          <div class="form-section">
            <div class="section-title">上传配置</div>
            
            <div class="form-row">
              <el-form-item label="简历上限" prop="maxResumeCount">
                <el-input-number 
                  v-model="settingsForm.maxResumeCount" 
                  :min="1"
                  :max="10"
                  :step="1"
                  style="width: 200px"
                ></el-input-number>
                <span class="unit">份/人</span>
              </el-form-item>
              <el-form-item label="岗位上限" prop="maxJobCount">
                <el-input-number 
                  v-model="settingsForm.maxJobCount" 
                  :min="1"
                  :max="1000"
                  :step="10"
                  style="width: 200px"
                ></el-input-number>
                <span class="unit">个/企业</span>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="上传大小上限" prop="uploadMaxSize">
                <el-input-number 
                  v-model="settingsForm.uploadMaxSize" 
                  :min="1"
                  :max="100"
                  :step="1"
                  style="width: 200px"
                ></el-input-number>
                <span class="unit">MB</span>
              </el-form-item>
            </div>
            
            <el-form-item label="允许的文件类型" prop="allowedFileTypes">
              <el-select
                v-model="fileTypes"
                multiple
                collapse-tags
                placeholder="请选择允许的文件类型"
                style="width: 100%"
                @change="handleFileTypesChange"
              >
                <el-option label="JPG图片" value="jpg"></el-option>
                <el-option label="PNG图片" value="png"></el-option>
                <el-option label="PDF文档" value="pdf"></el-option>
                <el-option label="Word文档" value="doc"></el-option>
                <el-option label="Word文档" value="docx"></el-option>
              </el-select>
              <div class="form-tip">
                <i class="el-icon-info"></i> 选择允许用户上传的文件类型
              </div>
            </el-form-item>
          </div>
          
          <div class="submit-section">
            <el-button type="primary" size="large" :loading="saving" @click="saveSettings">
              <i class="el-icon-check"></i> 保存配置
            </el-button>
            <el-button size="large" @click="resetSettings">
              <i class="el-icon-refresh"></i> 重置
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { get, put } from '@/utils/request';

export default {
  name: 'WebSettings',
  data() {
    return {
      saving: false,
      fileTypes: ['jpg', 'png', 'pdf', 'doc', 'docx'],
      settingsForm: {
        siteName: '',
        siteDescription: '',
        siteLogo: '',
        contactPhone: '',
        contactEmail: '',
        contactAddress: '',
        icpNumber: '',
        copyright: '',
        enableRegister: true,
        enableJobRecommend: true,
        maxResumeCount: 1,
        maxJobCount: 100,
        uploadMaxSize: 10,
        allowedFileTypes: 'jpg,png,pdf,doc,docx'
      },
      settingsRules: {
        siteName: [
          { required: true, message: '请输入平台名称', trigger: 'blur' }
        ],
        contactEmail: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      }
    };
  },
  mounted() {
    this.getSettings();
  },
  methods: {
    async getSettings() {
      try {
        const res = await get('/admin/settings');
        if (res.code === 200 && res.data) {
          this.settingsForm = { ...this.settingsForm, ...res.data };
          if (res.data.allowedFileTypes) {
            this.fileTypes = res.data.allowedFileTypes.split(',');
          }
        }
      } catch (error) {
        console.error('获取配置失败:', error);
      }
    },
    async saveSettings() {
      this.$refs.settingsForm.validate(async (valid) => {
        if (valid) {
          this.saving = true;
          try {
            const res = await put('/admin/settings', this.settingsForm);
            if (res.code === 200) {
              this.$message.success('配置保存成功');
            }
          } catch (error) {
            console.error('保存配置失败:', error);
          } finally {
            this.saving = false;
          }
        }
      });
    },
    resetSettings() {
      this.$confirm('确定要重置所有配置吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.getSettings();
        this.$message.success('已重置为当前配置');
      }).catch(() => {});
    },
    handleFileTypesChange(val) {
      this.settingsForm.allowedFileTypes = val.join(',');
    },
    handleLogoSuccess(response) {
      if (response.code === 200) {
        this.settingsForm.siteLogo = response.data.url;
        this.$message.success('LOGO上传成功');
      }
    },
    beforeLogoUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/jpg';
      const isPNG = file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG && !isPNG) {
        this.$message.error('上传图片只能是 JPG 或 PNG 格式!');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!');
        return false;
      }
      return true;
    }
  }
};
</script>

<style scoped>
.settings-page {
  min-height: 100%;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

.settings-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.card-header {
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid #409eff;
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

.form-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.form-section:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.section-title {
  font-size: 16px;
  color: #333;
  font-weight: 500;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.form-row .el-form-item {
  flex: 1;
  min-width: 300px;
}

.upload-section {
  display: flex;
  align-items: flex-start;
  gap: 30px;
}

.logo-uploader {
  flex-shrink: 0;
}

.logo {
  width: 150px;
  height: 50px;
  border-radius: 4px;
  display: block;
  object-fit: cover;
  border: 2px solid #e4e7ed;
}

.upload-placeholder {
  width: 150px;
  height: 50px;
  border: 2px dashed #d9d9d9;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-placeholder:hover {
  border-color: #409eff;
}

.upload-placeholder i {
  font-size: 20px;
  color: #8c939d;
}

.upload-placeholder span {
  font-size: 14px;
  color: #8c939d;
}

.upload-tip {
  padding-top: 5px;
}

.upload-tip p {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #666;
}

.upload-tip .sub-tip {
  font-size: 12px;
  color: #999;
}

.switch-tip {
  margin-left: 15px;
  font-size: 13px;
  color: #999;
}

.unit {
  margin-left: 10px;
  font-size: 14px;
  color: #666;
}

.form-tip {
  margin-top: 10px;
  font-size: 13px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 5px;
}

.form-tip i {
  color: #409eff;
}

.submit-section {
  display: flex;
  justify-content: flex-start;
  gap: 15px;
  padding-top: 20px;
  margin-top: 20px;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .settings-card {
    padding: 15px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .form-row .el-form-item {
    min-width: auto;
    width: 100%;
  }
  
  .upload-section {
    flex-direction: column;
    gap: 15px;
  }
  
  .submit-section {
    flex-direction: column;
  }
  
  .submit-section .el-button {
    width: 100%;
  }
}
</style>
