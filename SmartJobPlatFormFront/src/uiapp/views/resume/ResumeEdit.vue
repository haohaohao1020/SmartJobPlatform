<template>
  <div class="resume-edit-page">
    <div class="page-header">
      <div class="back-btn" @click="$router.back()">
        <i class="el-icon-arrow-left"></i>
      </div>
      <span class="title">编辑简历</span>
      <div class="save-btn" @click="saveResume">
        保存
      </div>
    </div>
    
    <div class="form-section">
      <div class="section-header">
        <span class="title">基本信息</span>
      </div>
      
      <div class="avatar-upload">
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
        <span class="avatar-label">点击上传头像</span>
      </div>
      
      <div class="form-row">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" maxlength="20"></el-input>
        </el-form-item>
      </div>
      
      <div class="form-row">
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
      </div>
      
      <div class="form-row">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" maxlength="11"></el-input>
        </el-form-item>
      </div>
      
      <div class="form-row">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" maxlength="50"></el-input>
        </el-form-item>
      </div>
      
      <div class="form-row">
        <el-form-item label="工作年限" prop="workYears">
          <el-select v-model="formData.workYears" placeholder="请选择" style="width: 100%">
            <el-option label="应届生" value="应届生"></el-option>
            <el-option label="1-3年" value="1-3年"></el-option>
            <el-option label="3-5年" value="3-5年"></el-option>
            <el-option label="5-10年" value="5-10年"></el-option>
            <el-option label="10年以上" value="10年以上"></el-option>
          </el-select>
        </el-form-item>
      </div>
      
      <div class="form-row">
        <el-form-item label="最高学历" prop="education">
          <el-select v-model="formData.highestEducation" placeholder="请选择" style="width: 100%">
            <el-option label="大专" value="大专"></el-option>
            <el-option label="本科" value="本科"></el-option>
            <el-option label="硕士" value="硕士"></el-option>
            <el-option label="博士" value="博士"></el-option>
          </el-select>
        </el-form-item>
      </div>
    </div>
    
    <div class="form-section">
      <div class="section-header">
        <span class="title">求职意向</span>
      </div>
      
      <div class="form-row">
        <el-form-item label="期望职位" prop="intentPosition">
          <el-input v-model="formData.intentPosition" placeholder="请输入期望职位" maxlength="50"></el-input>
        </el-form-item>
      </div>
      
      <div class="form-row">
        <el-form-item label="期望城市" prop="intentCity">
          <el-select v-model="formData.intentCity" placeholder="请选择" style="width: 100%">
            <el-option label="北京" value="北京"></el-option>
            <el-option label="上海" value="上海"></el-option>
            <el-option label="广州" value="广州"></el-option>
            <el-option label="深圳" value="深圳"></el-option>
            <el-option label="杭州" value="杭州"></el-option>
            <el-option label="成都" value="成都"></el-option>
          </el-select>
        </el-form-item>
      </div>
      
      <div class="form-row">
        <el-form-item label="期望薪资" prop="intentSalary">
          <div class="salary-input">
            <el-input-number 
              v-model="formData.intentSalaryMin" 
              :min="0" 
              :max="100000"
              :step="1000"
              size="small"
              style="width: 130px"
            ></el-input-number>
            <span class="range">至</span>
            <el-input-number 
              v-model="formData.intentSalaryMax" 
              :min="0" 
              :max="100000"
              :step="1000"
              size="small"
              style="width: 130px"
            ></el-input-number>
            <span class="unit">元/月</span>
          </div>
        </el-form-item>
      </div>
    </div>
    
    <div class="form-section">
      <div class="section-header">
        <span class="title">教育经历</span>
        <span class="add-btn" @click="addEducation">
          <i class="el-icon-plus"></i> 添加
        </span>
      </div>
      
      <div class="experience-item" v-for="(edu, index) in formData.education" :key="index">
        <div class="item-header">
          <span class="item-title">教育经历 {{ index + 1 }}</span>
          <i class="el-icon-delete" @click="removeEducation(index)"></i>
        </div>
        
        <div class="form-row">
          <el-form-item label="学校名称">
            <el-input v-model="edu.school" placeholder="请输入学校名称" maxlength="50"></el-input>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="所学专业">
            <el-input v-model="edu.major" placeholder="请输入所学专业" maxlength="50"></el-input>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="学历">
            <el-select v-model="edu.degree" placeholder="请选择" style="width: 100%">
              <el-option label="大专" value="大专"></el-option>
              <el-option label="本科" value="本科"></el-option>
              <el-option label="硕士" value="硕士"></el-option>
              <el-option label="博士" value="博士"></el-option>
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row two-col">
          <el-form-item label="开始时间">
            <el-date-picker
              v-model="edu.startDate"
              type="month"
              placeholder="选择开始时间"
              format="yyyy-MM"
              value-format="yyyy-MM"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker
              v-model="edu.endDate"
              type="month"
              placeholder="选择结束时间"
              format="yyyy-MM"
              value-format="yyyy-MM"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="在校描述">
            <el-input
              v-model="edu.description"
              type="textarea"
              :rows="3"
              placeholder="在校经历、获得荣誉等（选填）"
              maxlength="200"
              show-word-limit
            ></el-input>
          </el-form-item>
        </div>
      </div>
      
      <div class="empty-tip" v-if="!formData.education.length">
        暂无教育经历，点击添加
      </div>
    </div>
    
    <div class="form-section">
      <div class="section-header">
        <span class="title">工作经历</span>
        <span class="add-btn" @click="addWork">
          <i class="el-icon-plus"></i> 添加
        </span>
      </div>
      
      <div class="experience-item" v-for="(work, index) in formData.work" :key="index">
        <div class="item-header">
          <span class="item-title">工作经历 {{ index + 1 }}</span>
          <i class="el-icon-delete" @click="removeWork(index)"></i>
        </div>
        
        <div class="form-row">
          <el-form-item label="公司名称">
            <el-input v-model="work.company" placeholder="请输入公司名称" maxlength="50"></el-input>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="所属部门">
            <el-input v-model="work.department" placeholder="请输入所属部门" maxlength="50"></el-input>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="职位名称">
            <el-input v-model="work.position" placeholder="请输入职位名称" maxlength="50"></el-input>
          </el-form-item>
        </div>
        
        <div class="form-row two-col">
          <el-form-item label="开始时间">
            <el-date-picker
              v-model="work.startDate"
              type="month"
              placeholder="选择开始时间"
              format="yyyy-MM"
              value-format="yyyy-MM"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker
              v-model="work.endDate"
              type="month"
              placeholder="选择结束时间（至今可不填）"
              format="yyyy-MM"
              value-format="yyyy-MM"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="工作描述">
            <el-input
              v-model="work.description"
              type="textarea"
              :rows="4"
              placeholder="请详细描述您的工作内容、项目经验等"
              maxlength="500"
              show-word-limit
            ></el-input>
          </el-form-item>
        </div>
      </div>
      
      <div class="empty-tip" v-if="!formData.work.length">
        暂无工作经历，点击添加
      </div>
    </div>
    
    <div class="form-section">
      <div class="section-header">
        <span class="title">项目经历</span>
        <span class="add-btn" @click="addProject">
          <i class="el-icon-plus"></i> 添加
        </span>
      </div>
      
      <div class="experience-item" v-for="(project, index) in formData.projects" :key="index">
        <div class="item-header">
          <span class="item-title">项目经历 {{ index + 1 }}</span>
          <i class="el-icon-delete" @click="removeProject(index)"></i>
        </div>
        
        <div class="form-row">
          <el-form-item label="项目名称">
            <el-input v-model="project.name" placeholder="请输入项目名称" maxlength="50"></el-input>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="担任角色">
            <el-input v-model="project.role" placeholder="请输入您在项目中的角色" maxlength="50"></el-input>
          </el-form-item>
        </div>
        
        <div class="form-row two-col">
          <el-form-item label="开始时间">
            <el-date-picker
              v-model="project.startDate"
              type="month"
              placeholder="选择开始时间"
              format="yyyy-MM"
              value-format="yyyy-MM"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker
              v-model="project.endDate"
              type="month"
              placeholder="选择结束时间"
              format="yyyy-MM"
              value-format="yyyy-MM"
              style="width: 100%"
            ></el-date-picker>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="项目描述">
            <el-input
              v-model="project.description"
              type="textarea"
              :rows="4"
              placeholder="请描述项目背景、您的职责及主要成果"
              maxlength="500"
              show-word-limit
            ></el-input>
          </el-form-item>
        </div>
      </div>
      
      <div class="empty-tip" v-if="!formData.projects.length">
        暂无项目经历，点击添加
      </div>
    </div>
    
    <div class="form-section">
      <div class="section-header">
        <span class="title">技能特长</span>
      </div>
      
      <div class="skills-section">
        <div class="skills-input">
          <el-tag
            v-for="(tag, index) in skillTags"
            :key="index"
            closable
            @close="removeSkillTag(index)"
            class="skill-tag"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-if="showSkillInput"
            v-model="skillInput"
            ref="skillInputRef"
            size="small"
            style="width: 100px"
            @keyup.enter.native="confirmSkillInput"
            @blur="confirmSkillInput"
          ></el-input>
          <span v-else class="add-skill" @click="showSkillInput = true; focusSkillInput">
            <i class="el-icon-plus"></i> 添加技能
          </span>
        </div>
        <div class="skill-hot" v-if="hotSkills.length">
          <span class="label">常用技能：</span>
          <span 
            v-for="skill in hotSkills" 
            :key="skill"
            class="hot-skill"
            @click="addHotSkill(skill)"
            :class="{ disabled: skillTags.includes(skill) }"
          >
            {{ skill }}
          </span>
        </div>
      </div>
    </div>
    
    <div class="form-section">
      <div class="section-header">
        <span class="title">自我评价</span>
      </div>
      
      <div class="form-row">
        <el-form-item>
          <el-input
            v-model="formData.selfEvaluation"
            type="textarea"
            :rows="4"
            placeholder="请简要介绍一下自己，突出您的优势和特长"
            maxlength="200"
            show-word-limit
          ></el-input>
        </el-form-item>
      </div>
    </div>
    
    <div class="bottom-actions">
      <el-button type="default" size="large" @click="$router.back()">取消</el-button>
      <el-button type="primary" size="large" :loading="saving" @click="saveResume">保存简历</el-button>
      <el-button type="success" size="large" :loading="saving" @click="saveAndPreview">保存并预览</el-button>
    </div>
  </div>
</template>

<script>
import { get, post } from '@/utils/request';

export default {
  name: 'UIResumeEdit',
  data() {
    return {
      saving: false,
      showSkillInput: false,
      skillInput: '',
      skillTags: [],
      hotSkills: ['Vue', 'React', 'Angular', 'TypeScript', 'JavaScript', 'Node.js', 'Python', 'Java', 'Go', 'MySQL', 'Redis', 'MongoDB', 'Docker', 'Kubernetes', 'Git'],
      formData: {
        name: '',
        avatar: '',
        gender: '',
        phone: '',
        email: '',
        workYears: '',
        highestEducation: '',
        intentPosition: '',
        intentCity: '',
        intentSalaryMin: 10000,
        intentSalaryMax: 20000,
        education: [],
        work: [],
        projects: [],
        skills: [],
        selfEvaluation: ''
      }
    };
  },
  mounted() {
    this.getResume();
  },
  methods: {
    async getResume() {
      try {
        const res = await get('/resume/detail');
        if (res.code === 200 && res.data) {
          this.formData = { ...this.formData, ...res.data };
          if (res.data.skills) {
            this.skillTags = [...res.data.skills];
          }
        }
      } catch (error) {
        console.error('获取简历失败:', error);
      }
    },
    async saveResume() {
      this.formData.skills = [...this.skillTags];
      
      if (!this.formData.name) {
        this.$message.warning('请输入姓名');
        return;
      }
      if (!this.formData.phone) {
        this.$message.warning('请输入手机号');
        return;
      }
      
      this.saving = true;
      try {
        const res = await post('/resume/save', this.formData);
        if (res.code === 200) {
          this.$message.success('保存成功');
          return true;
        } else {
          this.$message.error(res.message || '保存失败');
          return false;
        }
      } catch (error) {
        this.$message.error('保存失败');
        return false;
      } finally {
        this.saving = false;
      }
    },
    async saveAndPreview() {
      const success = await this.saveResume();
      if (success) {
        this.$router.push('/resume/preview');
      }
    },
    addEducation() {
      this.formData.education.push({
        school: '',
        major: '',
        degree: '',
        startDate: '',
        endDate: '',
        description: ''
      });
    },
    removeEducation(index) {
      this.$confirm('确定要删除这条教育经历吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.formData.education.splice(index, 1);
      }).catch(() => {});
    },
    addWork() {
      this.formData.work.push({
        company: '',
        department: '',
        position: '',
        startDate: '',
        endDate: '',
        description: ''
      });
    },
    removeWork(index) {
      this.$confirm('确定要删除这条工作经历吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.formData.work.splice(index, 1);
      }).catch(() => {});
    },
    addProject() {
      this.formData.projects.push({
        name: '',
        role: '',
        startDate: '',
        endDate: '',
        description: ''
      });
    },
    removeProject(index) {
      this.$confirm('确定要删除这条项目经历吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.formData.projects.splice(index, 1);
      }).catch(() => {});
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
    },
    focusSkillInput() {
      this.$nextTick(() => {
        if (this.$refs.skillInputRef) {
          this.$refs.skillInputRef.focus();
        }
      });
    },
    confirmSkillInput() {
      if (this.skillInput.trim()) {
        if (!this.skillTags.includes(this.skillInput.trim())) {
          this.skillTags.push(this.skillInput.trim());
        }
      }
      this.skillInput = '';
      this.showSkillInput = false;
    },
    removeSkillTag(index) {
      this.skillTags.splice(index, 1);
    },
    addHotSkill(skill) {
      if (!this.skillTags.includes(skill)) {
        this.skillTags.push(skill);
      }
    }
  }
};
</script>

<style scoped>
.resume-edit-page {
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

.back-btn, .save-btn {
  font-size: 16px;
  cursor: pointer;
}

.back-btn {
  color: #606266;
}

.save-btn {
  color: #409eff;
  font-weight: 500;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.add-btn {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 13px;
  color: #409eff;
  cursor: pointer;
}

/* 头像上传 */
.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
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

.avatar-label {
  font-size: 13px;
  color: #909399;
}

/* 表单行 */
.form-row {
  margin-bottom: 12px;
}

.form-row:last-child {
  margin-bottom: 0;
}

.form-row.two-col {
  display: flex;
  gap: 15px;
}

.form-row.two-col .el-form-item {
  flex: 1;
}

.form-row ::v-deep .el-form-item__label {
  color: #606266;
  font-size: 14px;
}

.form-row ::v-deep .el-input__inner {
  border-radius: 8px;
}

/* 薪资输入 */
.salary-input {
  display: flex;
  align-items: center;
  gap: 10px;
}

.salary-input .range {
  color: #909399;
}

.salary-input .unit {
  font-size: 13px;
  color: #606266;
}

/* 经历项 */
.experience-item {
  padding: 15px;
  background: #fafafa;
  border-radius: 8px;
  margin-bottom: 15px;
}

.experience-item:last-child {
  margin-bottom: 0;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.item-title {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.item-header .el-icon-delete {
  color: #f56c6c;
  cursor: pointer;
  font-size: 16px;
}

.empty-tip {
  text-align: center;
  padding: 20px;
  font-size: 13px;
  color: #c0c4cc;
}

/* 技能区域 */
.skills-section {
  padding: 10px 0;
}

.skills-input {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
}

.skill-tag {
  background: #ecf5ff;
  border-color: #b3d8ff;
  color: #409eff;
}

.add-skill {
  display: flex;
  align-items: center;
  gap: 3px;
  padding: 4px 12px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  font-size: 13px;
  color: #909399;
  cursor: pointer;
}

.skill-hot {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.skill-hot .label {
  font-size: 13px;
  color: #909399;
}

.hot-skill {
  padding: 3px 10px;
  background: #f5f7fa;
  color: #606266;
  font-size: 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.hot-skill:active {
  background: #e4e7ed;
}

.hot-skill.disabled {
  color: #c0c4cc;
  cursor: not-allowed;
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
