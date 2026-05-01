<template>
  <div class="company-page">
    <div class="container">
      <!-- 顶部状态提示 -->
      <div class="status-bar" v-if="companyInfo">
        <el-alert
          :title="getAuditTitle()"
          :type="getAuditType()"
          show-icon
          :closable="false"
        >
          <template slot="default">
            <span v-if="companyInfo.auditStatus === 2">您的企业信息正在审核中，请耐心等待...</span>
            <span v-else-if="companyInfo.auditStatus === 3">
              拒绝原因：{{ companyInfo.rejectReason || '信息不完整，请补充后重新提交' }}
            </span>
            <span v-else>您的企业信息已通过审核，可以正常发布岗位</span>
          </template>
        </el-alert>
      </div>
      
      <!-- 企业信息表单 -->
      <div class="form-card">
        <div class="card-header">
          <h3><i class="el-icon-office-building"></i> 企业信息</h3>
          <el-tag :type="getAuditType()">
            {{ getAuditStatusName() }}
          </el-tag>
        </div>
        
        <el-form 
          ref="companyForm" 
          :model="companyForm" 
          :rules="companyRules" 
          label-width="120px"
        >
          <!-- 基本信息 -->
          <div class="form-section">
            <div class="section-title">基本信息</div>
            
            <el-form-item label="企业LOGO" prop="logo">
              <div class="upload-section">
                <el-upload
                  class="logo-uploader"
                  action="/api/company/logo"
                  :show-file-list="false"
                  :on-success="handleLogoSuccess"
                  :before-upload="beforeLogoUpload"
                >
                  <img v-if="companyForm.logo" :src="companyForm.logo" class="logo">
                  <div v-else class="upload-placeholder">
                    <i class="el-icon-plus"></i>
                    <span>上传LOGO</span>
                  </div>
                </el-upload>
                <div class="upload-tip">
                  <p>上传企业LOGO，建议尺寸 200*200 像素</p>
                  <p class="sub-tip">支持 JPG、PNG 格式，大小不超过 2MB</p>
                </div>
              </div>
            </el-form-item>
            
            <div class="form-row">
              <el-form-item label="企业名称" prop="name">
                <el-input 
                  v-model="companyForm.name" 
                  placeholder="请输入企业全称"
                  maxlength="50"
                ></el-input>
              </el-form-item>
              <el-form-item label="企业简称" prop="shortName">
                <el-input 
                  v-model="companyForm.shortName" 
                  placeholder="请输入企业简称"
                  maxlength="20"
                ></el-input>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="所属行业" prop="industry">
                <el-select v-model="companyForm.industry" placeholder="请选择所属行业" style="width: 100%">
                  <el-option label="互联网" value="互联网"></el-option>
                  <el-option label="金融" value="金融"></el-option>
                  <el-option label="教育" value="教育"></el-option>
                  <el-option label="医疗" value="医疗"></el-option>
                  <el-option label="房地产" value="房地产"></el-option>
                  <el-option label="制造业" value="制造业"></el-option>
                  <el-option label="服务业" value="服务业"></el-option>
                  <el-option label="电子商务" value="电子商务"></el-option>
                  <el-option label="人工智能" value="人工智能"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="企业规模" prop="size">
                <el-select v-model="companyForm.size" placeholder="请选择企业规模" style="width: 100%">
                  <el-option label="0-20人" value="0-20人"></el-option>
                  <el-option label="20-99人" value="20-99人"></el-option>
                  <el-option label="100-499人" value="100-499人"></el-option>
                  <el-option label="500-999人" value="500-999人"></el-option>
                  <el-option label="1000人以上" value="1000人以上"></el-option>
                </el-select>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="企业类型" prop="type">
                <el-select v-model="companyForm.type" placeholder="请选择企业类型" style="width: 100%">
                  <el-option label="民营企业" value="民营企业"></el-option>
                  <el-option label="国有企业" value="国有企业"></el-option>
                  <el-option label="外资企业" value="外资企业"></el-option>
                  <el-option label="合资企业" value="合资企业"></el-option>
                  <el-option label="上市公司" value="上市公司"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="成立日期" prop="establishDate">
                <el-date-picker
                  v-model="companyForm.establishDate"
                  type="date"
                  placeholder="请选择成立日期"
                  value-format="yyyy-MM-dd"
                  style="width: 100%"
                ></el-date-picker>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="注册资本" prop="registeredCapital">
                <el-input v-model="companyForm.registeredCapital" placeholder="请输入注册资本">
                  <template slot="append">万元</template>
                </el-input>
              </el-form-item>
              <el-form-item label="法人代表" prop="legalPerson">
                <el-input v-model="companyForm.legalPerson" placeholder="请输入法人代表姓名"></el-input>
              </el-form-item>
            </div>
          </div>
          
          <!-- 联系方式 -->
          <div class="form-section">
            <div class="section-title">联系方式</div>
            
            <div class="form-row">
              <el-form-item label="所在省份" prop="province">
                <el-input v-model="companyForm.province" placeholder="请输入省份"></el-input>
              </el-form-item>
              <el-form-item label="所在城市" prop="city">
                <el-input v-model="companyForm.city" placeholder="请输入城市"></el-input>
              </el-form-item>
              <el-form-item label="所在区县" prop="district">
                <el-input v-model="companyForm.district" placeholder="请输入区县"></el-input>
              </el-form-item>
            </div>
            
            <el-form-item label="详细地址" prop="address">
              <el-input 
                v-model="companyForm.address" 
                placeholder="请输入详细地址"
                maxlength="100"
                show-word-limit
              ></el-input>
            </el-form-item>
            
            <div class="form-row">
              <el-form-item label="联系人" prop="contactName">
                <el-input v-model="companyForm.contactName" placeholder="请输入联系人姓名"></el-input>
              </el-form-item>
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="companyForm.contactPhone" placeholder="请输入联系电话"></el-input>
              </el-form-item>
              <el-form-item label="联系邮箱" prop="contactEmail">
                <el-input v-model="companyForm.contactEmail" placeholder="请输入联系邮箱"></el-input>
              </el-form-item>
            </div>
          </div>
          
          <!-- 企业介绍 -->
          <div class="form-section">
            <div class="section-title">企业介绍</div>
            
            <el-form-item label="企业简介" prop="description">
              <el-input
                v-model="companyForm.description"
                type="textarea"
                :rows="5"
                placeholder="请详细介绍企业的发展历程、主营业务、企业文化等信息"
                maxlength="2000"
                show-word-limit
              ></el-input>
            </el-form-item>
            
            <el-form-item label="经营范围" prop="businessScope">
              <el-input
                v-model="companyForm.businessScope"
                type="textarea"
                :rows="3"
                placeholder="请输入企业经营范围"
                maxlength="500"
                show-word-limit
              ></el-input>
            </el-form-item>
          </div>
          
          <!-- 营业执照 -->
          <div class="form-section">
            <div class="section-title">
              营业执照
              <span class="required-tag">* 必传</span>
            </div>
            
            <el-form-item label="营业执照" prop="businessLicense">
              <div class="upload-section">
                <el-upload
                  class="license-uploader"
                  action="/api/company/license"
                  :show-file-list="false"
                  :on-success="handleLicenseSuccess"
                  :before-upload="beforeLicenseUpload"
                >
                  <img v-if="companyForm.businessLicense" :src="companyForm.businessLicense" class="license">
                  <div v-else class="upload-placeholder">
                    <i class="el-icon-plus"></i>
                    <span>上传营业执照</span>
                  </div>
                </el-upload>
                <div class="upload-tip">
                  <p>请上传清晰的营业执照照片</p>
                  <p class="sub-tip">支持 JPG、PNG 格式，大小不超过 5MB</p>
                </div>
              </div>
            </el-form-item>
          </div>
          
          <!-- 提交按钮 -->
          <div class="submit-section">
            <el-button type="primary" size="large" :loading="submitting" @click="submitForm">
              <i class="el-icon-check"></i>
              {{ companyInfo ? '重新提交审核' : '提交入驻申请' }}
            </el-button>
            <el-button size="large" @click="resetForm">
              <i class="el-icon-refresh"></i> 重置
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { get, post, put } from '@/utils/request';

export default {
  name: 'HRCompany',
  data() {
    return {
      companyInfo: null,
      submitting: false,
      companyForm: {
        id: null,
        name: '',
        shortName: '',
        logo: '',
        industry: '',
        size: '',
        type: '',
        establishDate: '',
        registeredCapital: '',
        legalPerson: '',
        province: '',
        city: '',
        district: '',
        address: '',
        contactName: '',
        contactPhone: '',
        contactEmail: '',
        description: '',
        businessScope: '',
        businessLicense: ''
      },
      companyRules: {
        name: [
          { required: true, message: '请输入企业名称', trigger: 'blur' }
        ],
        industry: [
          { required: true, message: '请选择所属行业', trigger: 'change' }
        ],
        size: [
          { required: true, message: '请选择企业规模', trigger: 'change' }
        ],
        city: [
          { required: true, message: '请输入城市', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入详细地址', trigger: 'blur' }
        ],
        contactName: [
          { required: true, message: '请输入联系人姓名', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' }
        ],
        contactEmail: [
          { required: true, message: '请输入联系邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入企业简介', trigger: 'blur' },
          { min: 50, message: '企业简介不能少于50个字符', trigger: 'blur' }
        ],
        businessLicense: [
          { required: true, message: '请上传营业执照', trigger: 'change' }
        ]
      }
    };
  },
  mounted() {
    this.getCompanyInfo();
  },
  methods: {
    // 获取企业信息
    async getCompanyInfo() {
      try {
        const res = await get('/hr/company');
        if (res.code === 200 && res.data) {
          this.companyInfo = res.data;
          // 填充表单数据
          this.companyForm = { ...this.companyForm, ...res.data };
        }
      } catch (error) {
        console.error('获取企业信息失败:', error);
      }
    },
    // 提交表单
    async submitForm() {
      this.$refs.companyForm.validate(async (valid) => {
        if (valid) {
          this.submitting = true;
          try {
            let res;
            if (this.companyInfo) {
              // 重新提交审核
              res = await put('/hr/company', this.companyForm);
            } else {
              // 首次提交
              res = await post('/hr/company', this.companyForm);
            }
            
            if (res.code === 200) {
              this.$message.success('提交成功，等待审核');
              // 刷新企业信息
              this.getCompanyInfo();
            }
          } catch (error) {
            console.error('提交失败:', error);
          } finally {
            this.submitting = false;
          }
        }
      });
    },
    // 重置表单
    resetForm() {
      this.$refs.companyForm.resetFields();
    },
    // LOGO上传成功
    handleLogoSuccess(response) {
      if (response.code === 200) {
        this.companyForm.logo = response.data.url;
        this.$message.success('LOGO上传成功');
      }
    },
    // LOGO上传前验证
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
    },
    // 营业执照上传成功
    handleLicenseSuccess(response) {
      if (response.code === 200) {
        this.companyForm.businessLicense = response.data.url;
        this.$message.success('营业执照上传成功');
      }
    },
    // 营业执照上传前验证
    beforeLicenseUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/jpg';
      const isPNG = file.type === 'image/png';
      const isLt5M = file.size / 1024 / 1024 < 5;

      if (!isJPG && !isPNG) {
        this.$message.error('上传图片只能是 JPG 或 PNG 格式!');
        return false;
      }
      if (!isLt5M) {
        this.$message.error('上传图片大小不能超过 5MB!');
        return false;
      }
      return true;
    },
    // 获取审核状态名称
    getAuditStatusName() {
      if (!this.companyInfo) return '未入驻';
      const nameMap = {
        1: '已通过',
        2: '待审核',
        3: '已拒绝'
      };
      return nameMap[this.companyInfo.auditStatus] || '未知';
    },
    // 获取审核类型
    getAuditType() {
      if (!this.companyInfo) return 'info';
      const typeMap = {
        1: 'success',
        2: 'warning',
        3: 'error'
      };
      return typeMap[this.companyInfo.auditStatus] || 'info';
    },
    // 获取审核标题
    getAuditTitle() {
      if (!this.companyInfo) return '';
      const titleMap = {
        1: '企业信息审核通过',
        2: '企业信息审核中',
        3: '企业信息审核未通过'
      };
      return titleMap[this.companyInfo.auditStatus] || '';
    }
  }
};
</script>

<style scoped>
.company-page {
  min-height: 100%;
  background-color: #f5f7fa;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

/* 状态栏 */
.status-bar {
  margin-bottom: 20px;
}

/* 表单卡片 */
.form-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid #67c23a;
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
  color: #67c23a;
}

/* 表单章节 */
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
  border-left: 3px solid #67c23a;
  display: flex;
  align-items: center;
  gap: 8px;
}

.required-tag {
  font-size: 12px;
  color: #f56c6c;
  font-weight: normal;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.form-row .el-form-item {
  flex: 1;
  min-width: 250px;
}

/* 上传区域 */
.upload-section {
  display: flex;
  align-items: flex-start;
  gap: 30px;
}

.logo-uploader,
.license-uploader {
  flex-shrink: 0;
}

.logo {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  display: block;
  object-fit: cover;
  border: 2px solid #e4e7ed;
}

.license {
  width: 300px;
  height: 200px;
  border-radius: 8px;
  display: block;
  object-fit: cover;
  border: 2px solid #e4e7ed;
}

.upload-placeholder {
  width: 120px;
  height: 120px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.license-uploader .upload-placeholder {
  width: 300px;
  height: 200px;
}

.upload-placeholder:hover {
  border-color: #67c23a;
}

.upload-placeholder i {
  font-size: 36px;
  color: #8c939d;
  margin-bottom: 5px;
}

.upload-placeholder span {
  font-size: 14px;
  color: #8c939d;
}

.upload-tip {
  padding-top: 10px;
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

/* 提交区域 */
.submit-section {
  display: flex;
  justify-content: flex-start;
  gap: 15px;
  padding-top: 20px;
  margin-top: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式 */
@media (max-width: 768px) {
  .container {
    padding: 15px;
  }
  
  .form-card {
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
  
  .license {
    width: 100%;
    max-width: 300px;
    height: auto;
  }
  
  .license-uploader .upload-placeholder {
    width: 100%;
    max-width: 300px;
    height: 180px;
  }
  
  .submit-section {
    flex-direction: column;
  }
  
  .submit-section .el-button {
    width: 100%;
  }
}
</style>
