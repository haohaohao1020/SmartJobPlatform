<template>
  <div class="resume-page">
    <div class="container">
      <!-- 顶部操作栏 -->
      <div class="action-bar">
        <div class="page-title">
          <h2>我的简历</h2>
          <span class="tip">完善简历可提高投递成功率</span>
        </div>
        <div class="actions">
          <el-button type="primary" @click="previewResume">
            <i class="el-icon-view"></i> 预览简历
          </el-button>
        </div>
      </div>
      
      <!-- 简历表单 -->
      <el-form 
        ref="resumeForm" 
        :model="resumeForm" 
        :rules="resumeRules" 
        label-width="100px"
        class="resume-form"
      >
        <!-- 基本信息 -->
        <div class="form-section">
          <div class="section-header">
            <h3><i class="el-icon-user"></i> 基本信息</h3>
          </div>
          
          <div class="form-row">
            <el-form-item label="头像" prop="avatar">
              <el-upload
                class="avatar-uploader"
                action="/api/user/avatar"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <img v-if="resumeForm.avatar" :src="resumeForm.avatar" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
              <div class="upload-tip">点击上传头像，支持JPG、PNG格式</div>
            </el-form-item>
          </div>
          
          <div class="form-row">
            <el-form-item label="姓名" prop="userName">
              <el-input v-model="resumeForm.userName" placeholder="请输入真实姓名"></el-input>
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="resumeForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="出生年月" prop="birthDate">
              <el-date-picker
                v-model="resumeForm.birthDate"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
              ></el-date-picker>
            </el-form-item>
          </div>
          
          <div class="form-row">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="resumeForm.phone" placeholder="请输入手机号"></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="resumeForm.email" placeholder="请输入邮箱"></el-input>
            </el-form-item>
            <el-form-item label="现居城市" prop="currentCity">
              <el-input v-model="resumeForm.currentCity" placeholder="请输入现居城市"></el-input>
            </el-form-item>
          </div>
        </div>
        
        <!-- 求职意向 -->
        <div class="form-section">
          <div class="section-header">
            <h3><i class="el-icon-suitcase"></i> 求职意向</h3>
          </div>
          
          <div class="form-row">
            <el-form-item label="期望职位" prop="jobIntention">
              <el-input v-model="resumeForm.jobIntention" placeholder="请输入期望职位"></el-input>
            </el-form-item>
            <el-form-item label="期望薪资" prop="expectedSalaryMin">
              <el-input-number 
                v-model="resumeForm.expectedSalaryMin" 
                :min="0"
                :max="100000"
                placeholder="最低"
              ></el-input-number>
              <span class="divider">-</span>
              <el-input-number 
                v-model="resumeForm.expectedSalaryMax" 
                :min="0"
                :max="100000"
                placeholder="最高"
              ></el-input-number>
              <span class="unit">K/月</span>
            </el-form-item>
          </div>
          
          <div class="form-row">
            <el-form-item label="期望城市" prop="expectedCity">
              <el-input v-model="resumeForm.expectedCity" placeholder="请输入期望城市"></el-input>
            </el-form-item>
            <el-form-item label="工作状态" prop="workStatus">
              <el-select v-model="resumeForm.workStatus" placeholder="请选择工作状态">
                <el-option label="在职，考虑机会" value="在职，考虑机会"></el-option>
                <el-option label="在职，暂不考虑" value="在职，暂不考虑"></el-option>
                <el-option label="离职，随时到岗" value="离职，随时到岗"></el-option>
              </el-select>
            </el-form-item>
          </div>
        </div>
        
        <!-- 教育经历 -->
        <div class="form-section">
          <div class="section-header">
            <h3><i class="el-icon-reading"></i> 教育经历</h3>
            <el-button type="primary" size="small" icon="el-icon-plus" @click="addEducation">
              添加教育经历
            </el-button>
          </div>
          
          <div 
            v-for="(edu, index) in resumeForm.educations" 
            :key="edu.id || index"
            class="experience-card"
          >
            <div class="card-header">
              <span class="card-title">教育经历 {{ index + 1 }}</span>
              <el-button 
                type="text" 
                size="small" 
                @click="removeEducation(index)"
              >
                <i class="el-icon-delete"></i> 删除
              </el-button>
            </div>
            
            <el-form-item label="学校名称" :prop="'educations.' + index + '.schoolName'" :rules="{ required: true, message: '请输入学校名称', trigger: 'blur' }">
              <el-input v-model="edu.schoolName" placeholder="请输入学校名称"></el-input>
            </el-form-item>
            
            <el-form-item label="专业" :prop="'educations.' + index + '.major'" :rules="{ required: true, message: '请输入专业', trigger: 'blur' }">
              <el-input v-model="edu.major" placeholder="请输入专业"></el-input>
            </el-form-item>
            
            <div class="form-row">
              <el-form-item label="学历" :prop="'educations.' + index + '.education'" :rules="{ required: true, message: '请选择学历', trigger: 'change' }">
                <el-select v-model="edu.education" placeholder="请选择学历">
                  <el-option label="大专" value="大专"></el-option>
                  <el-option label="本科" value="本科"></el-option>
                  <el-option label="硕士" value="硕士"></el-option>
                  <el-option label="博士" value="博士"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="开始时间" :prop="'educations.' + index + '.startDate'" :rules="{ required: true, message: '请选择开始时间', trigger: 'change' }">
                <el-date-picker
                  v-model="edu.startDate"
                  type="month"
                  placeholder="选择月份"
                  value-format="yyyy-MM"
                ></el-date-picker>
              </el-form-item>
              <el-form-item label="结束时间" :prop="'educations.' + index + '.endDate'" :rules="{ required: true, message: '请选择结束时间', trigger: 'change' }">
                <el-date-picker
                  v-model="edu.endDate"
                  type="month"
                  placeholder="选择月份"
                  value-format="yyyy-MM"
                ></el-date-picker>
              </el-form-item>
            </div>
          </div>
          
          <el-empty v-if="resumeForm.educations.length === 0" description="暂无教育经历" :image-size="60"></el-empty>
        </div>
        
        <!-- 工作经历 -->
        <div class="form-section">
          <div class="section-header">
            <h3><i class="el-icon-office-building"></i> 工作经历</h3>
            <el-button type="primary" size="small" icon="el-icon-plus" @click="addWorkExperience">
              添加工作经历
            </el-button>
          </div>
          
          <div 
            v-for="(work, index) in resumeForm.workExperiences" 
            :key="work.id || index"
            class="experience-card"
          >
            <div class="card-header">
              <span class="card-title">工作经历 {{ index + 1 }}</span>
              <el-button 
                type="text" 
                size="small" 
                @click="removeWorkExperience(index)"
              >
                <i class="el-icon-delete"></i> 删除
              </el-button>
            </div>
            
            <el-form-item label="公司名称" :prop="'workExperiences.' + index + '.companyName'" :rules="{ required: true, message: '请输入公司名称', trigger: 'blur' }">
              <el-input v-model="work.companyName" placeholder="请输入公司名称"></el-input>
            </el-form-item>
            
            <div class="form-row">
              <el-form-item label="职位" :prop="'workExperiences.' + index + '.position'" :rules="{ required: true, message: '请输入职位', trigger: 'blur' }">
                <el-input v-model="work.position" placeholder="请输入职位"></el-input>
              </el-form-item>
              <el-form-item label="是否在职">
                <el-switch v-model="work.isCurrent"></el-switch>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="开始时间" :prop="'workExperiences.' + index + '.startDate'" :rules="{ required: true, message: '请选择开始时间', trigger: 'change' }">
                <el-date-picker
                  v-model="work.startDate"
                  type="month"
                  placeholder="选择月份"
                  value-format="yyyy-MM"
                ></el-date-picker>
              </el-form-item>
              <el-form-item label="结束时间" :prop="'workExperiences.' + index + '.endDate'" :rules="work.isCurrent ? [] : [{ required: true, message: '请选择结束时间', trigger: 'change' }]">
                <el-date-picker
                  v-model="work.endDate"
                  type="month"
                  placeholder="选择月份"
                  value-format="yyyy-MM"
                  :disabled="work.isCurrent"
                ></el-date-picker>
              </el-form-item>
              <el-form-item label="薪资范围">
                <el-input-number v-model="work.salaryMin" :min="0" :max="100000" placeholder="最低"></el-input-number>
                <span class="divider">-</span>
                <el-input-number v-model="work.salaryMax" :min="0" :max="100000" placeholder="最高"></el-input-number>
                <span class="unit">K/月</span>
              </el-form-item>
            </div>
            
            <el-form-item label="工作描述">
              <el-input
                v-model="work.workDescription"
                type="textarea"
                :rows="4"
                placeholder="请详细描述您的工作职责和工作内容"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="工作业绩">
              <el-input
                v-model="work.achievements"
                type="textarea"
                :rows="3"
                placeholder="请描述您在该岗位上取得的主要业绩"
              ></el-input>
            </el-form-item>
          </div>
          
          <el-empty v-if="resumeForm.workExperiences.length === 0" description="暂无工作经历" :image-size="60"></el-empty>
        </div>
        
        <!-- 项目经历 -->
        <div class="form-section">
          <div class="section-header">
            <h3><i class="el-icon-s-data"></i> 项目经历</h3>
            <el-button type="primary" size="small" icon="el-icon-plus" @click="addProjectExperience">
              添加项目经历
            </el-button>
          </div>
          
          <div 
            v-for="(project, index) in resumeForm.projectExperiences" 
            :key="project.id || index"
            class="experience-card"
          >
            <div class="card-header">
              <span class="card-title">项目经历 {{ index + 1 }}</span>
              <el-button 
                type="text" 
                size="small" 
                @click="removeProjectExperience(index)"
              >
                <i class="el-icon-delete"></i> 删除
              </el-button>
            </div>
            
            <div class="form-row">
              <el-form-item label="项目名称" :prop="'projectExperiences.' + index + '.projectName'" :rules="{ required: true, message: '请输入项目名称', trigger: 'blur' }">
                <el-input v-model="project.projectName" placeholder="请输入项目名称"></el-input>
              </el-form-item>
              <el-form-item label="担任角色" :prop="'projectExperiences.' + index + '.role'" :rules="{ required: true, message: '请输入担任角色', trigger: 'blur' }">
                <el-input v-model="project.role" placeholder="请输入担任角色"></el-input>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item label="开始时间" :prop="'projectExperiences.' + index + '.startDate'" :rules="{ required: true, message: '请选择开始时间', trigger: 'change' }">
                <el-date-picker
                  v-model="project.startDate"
                  type="month"
                  placeholder="选择月份"
                  value-format="yyyy-MM"
                ></el-date-picker>
              </el-form-item>
              <el-form-item label="结束时间" :prop="'projectExperiences.' + index + '.endDate'" :rules="{ required: true, message: '请选择结束时间', trigger: 'change' }">
                <el-date-picker
                  v-model="project.endDate"
                  type="month"
                  placeholder="选择月份"
                  value-format="yyyy-MM"
                ></el-date-picker>
              </el-form-item>
            </div>
            
            <el-form-item label="项目描述">
              <el-input
                v-model="project.projectDescription"
                type="textarea"
                :rows="3"
                placeholder="请描述项目的背景、目标和范围"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="工作职责">
              <el-input
                v-model="project.responsibilities"
                type="textarea"
                :rows="3"
                placeholder="请描述您在项目中的主要职责"
              ></el-input>
            </el-form-item>
            
            <el-form-item label="项目成果">
              <el-input
                v-model="project.achievements"
                type="textarea"
                :rows="2"
                placeholder="请描述您在项目中取得的主要成果"
              ></el-input>
            </el-form-item>
          </div>
          
          <el-empty v-if="resumeForm.projectExperiences.length === 0" description="暂无项目经历" :image-size="60"></el-empty>
        </div>
        
        <!-- 专业技能 -->
        <div class="form-section">
          <div class="section-header">
            <h3><i class="el-icon-coin"></i> 专业技能</h3>
            <el-button type="primary" size="small" icon="el-icon-plus" @click="addSkill">
              添加技能
            </el-button>
          </div>
          
          <div class="skills-list">
            <div 
              v-for="(skill, index) in resumeForm.skills" 
              :key="skill.id || index"
              class="skill-item"
            >
              <el-form-item 
                :prop="'skills.' + index + '.skillName'" 
                :rules="{ required: true, message: '请输入技能名称', trigger: 'blur' }"
              >
                <el-input v-model="skill.skillName" placeholder="技能名称" style="width: 150px"></el-input>
              </el-form-item>
              <el-form-item 
                :prop="'skills.' + index + '.proficiency'" 
                :rules="{ required: true, message: '请选择熟练程度', trigger: 'change' }"
              >
                <el-select v-model="skill.proficiency" placeholder="熟练程度" style="width: 120px">
                  <el-option label="了解" value="了解"></el-option>
                  <el-option label="掌握" value="掌握"></el-option>
                  <el-option label="熟练" value="熟练"></el-option>
                </el-select>
              </el-form-item>
              <el-button type="text" @click="removeSkill(index)">
                <i class="el-icon-delete"></i>
              </el-button>
            </div>
          </div>
          
          <el-empty v-if="resumeForm.skills.length === 0" description="暂无专业技能" :image-size="60"></el-empty>
        </div>
        
        <!-- 自我评价 -->
        <div class="form-section">
          <div class="section-header">
            <h3><i class="el-icon-edit"></i> 自我评价</h3>
          </div>
          
          <el-form-item>
            <el-input
              v-model="resumeForm.selfIntroduction"
              type="textarea"
              :rows="5"
              placeholder="请简要介绍您的职业目标、专业特长、工作风格等，突出您的优势和亮点"
              show-word-limit
              :maxlength="500"
            ></el-input>
          </el-form-item>
        </div>
        
        <!-- 提交按钮 -->
        <div class="submit-section">
          <el-button type="primary" size="large" :loading="saving" @click="saveResume">
            <i class="el-icon-check"></i> 保存简历
          </el-button>
          <el-button size="large" @click="resetForm">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { get, post } from '@/utils/request';

export default {
  name: 'JobSeekerResume',
  data() {
    return {
      saving: false,
      resumeForm: {
        id: null,
        userId: 1,
        userName: '',
        avatar: '',
        gender: 1,
        birthDate: '',
        phone: '',
        email: '',
        currentCity: '',
        jobIntention: '',
        expectedSalaryMin: 10000,
        expectedSalaryMax: 20000,
        expectedCity: '',
        workStatus: '在职，考虑机会',
        selfIntroduction: '',
        educations: [],
        workExperiences: [],
        projectExperiences: [],
        skills: []
      },
      resumeRules: {
        userName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        currentCity: [
          { required: true, message: '请输入现居城市', trigger: 'blur' }
        ],
        jobIntention: [
          { required: true, message: '请输入期望职位', trigger: 'blur' }
        ]
      }
    };
  },
  mounted() {
    this.getResume();
  },
  methods: {
    // 获取简历信息
    async getResume() {
      try {
        const res = await get('/resumes/my');
        if (res.code === 200 && res.data) {
          this.resumeForm = { ...this.resumeForm, ...res.data };
        }
      } catch (error) {
        console.error('获取简历失败:', error);
      }
    },
    // 保存简历
    async saveResume() {
      this.$refs.resumeForm.validate(async (valid) => {
        if (valid) {
          this.saving = true;
          try {
            const res = await post('/resumes', this.resumeForm);
            if (res.code === 200) {
              this.$message.success('简历保存成功');
              this.resumeForm.id = res.data.id;
            }
          } catch (error) {
            console.error('保存简历失败:', error);
          } finally {
            this.saving = false;
          }
        }
      });
    },
    // 重置表单
    resetForm() {
      this.$refs.resumeForm.resetFields();
    },
    // 预览简历
    previewResume() {
      this.$router.push('/jobseeker/resume/preview');
    },
    // 头像上传成功
    handleAvatarSuccess(response, file) {
      if (response.code === 200) {
        this.resumeForm.avatar = response.data.url;
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
    // 添加教育经历
    addEducation() {
      this.resumeForm.educations.push({
        id: null,
        schoolName: '',
        major: '',
        education: '',
        startDate: '',
        endDate: '',
        description: ''
      });
    },
    // 删除教育经历
    removeEducation(index) {
      this.resumeForm.educations.splice(index, 1);
    },
    // 添加工作经历
    addWorkExperience() {
      this.resumeForm.workExperiences.push({
        id: null,
        companyName: '',
        position: '',
        startDate: '',
        endDate: '',
        isCurrent: false,
        salaryMin: null,
        salaryMax: null,
        workDescription: '',
        achievements: ''
      });
    },
    // 删除工作经历
    removeWorkExperience(index) {
      this.resumeForm.workExperiences.splice(index, 1);
    },
    // 添加项目经历
    addProjectExperience() {
      this.resumeForm.projectExperiences.push({
        id: null,
        projectName: '',
        role: '',
        startDate: '',
        endDate: '',
        projectDescription: '',
        responsibilities: '',
        achievements: ''
      });
    },
    // 删除项目经历
    removeProjectExperience(index) {
      this.resumeForm.projectExperiences.splice(index, 1);
    },
    // 添加技能
    addSkill() {
      this.resumeForm.skills.push({
        id: null,
        skillName: '',
        proficiency: '掌握'
      });
    },
    // 删除技能
    removeSkill(index) {
      this.resumeForm.skills.splice(index, 1);
    }
  }
};
</script>

<style scoped>
.resume-page {
  min-height: 100%;
  background-color: #f5f7fa;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

/* 顶部操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.page-title h2 {
  font-size: 20px;
  color: #333;
  margin: 0 0 5px 0;
}

.page-title .tip {
  font-size: 13px;
  color: #999;
}

/* 表单样式 */
.resume-form {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
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

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.section-header h3 {
  font-size: 16px;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
}

.section-header h3 i {
  margin-right: 8px;
  color: #409eff;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.form-row .el-form-item {
  flex: 1;
  min-width: 200px;
}

/* 头像上传 */
.avatar-uploader {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: block;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
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

.upload-tip {
  font-size: 12px;
  color: #999;
}

/* 经历卡片 */
.experience-card {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.card-title {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

/* 薪资范围 */
.divider {
  margin: 0 10px;
  color: #999;
}

.unit {
  margin-left: 10px;
  color: #666;
  font-size: 14px;
}

/* 技能列表 */
.skills-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.skill-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 提交区域 */
.submit-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding-top: 20px;
  margin-top: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式 */
@media (max-width: 768px) {
  .container {
    padding: 15px;
  }
  
  .action-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
    padding: 15px;
  }
  
  .resume-form {
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
  
  .experience-card {
    padding: 15px;
  }
  
  .skills-list {
    flex-direction: column;
  }
  
  .skill-item {
    width: 100%;
    justify-content: space-between;
  }
  
  .submit-section {
    flex-direction: column;
  }
  
  .submit-section .el-button {
    width: 100%;
  }
}
</style>
