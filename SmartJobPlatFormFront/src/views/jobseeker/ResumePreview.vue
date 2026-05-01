<template>
  <div class="resume-preview-page">
    <div class="container">
      <!-- 顶部操作栏 -->
      <div class="action-bar">
        <el-button type="primary" @click="goBack">
          <i class="el-icon-arrow-left"></i> 返回编辑
        </el-button>
        <div class="title">简历预览</div>
        <el-button type="success" @click="downloadResume">
          <i class="el-icon-download"></i> 下载简历
        </el-button>
      </div>
      
      <!-- 简历内容 -->
      <div class="resume-card" ref="resumeContent">
        <!-- 头部信息 -->
        <div class="resume-header">
          <div class="avatar-section">
            <el-avatar :size="120" :src="resumeData.avatar" class="avatar">
              <i class="el-icon-user-solid"></i>
            </el-avatar>
          </div>
          <div class="info-section">
            <h1 class="name">{{ resumeData.userName || '未填写' }}</h1>
            <div class="basic-info">
              <span v-if="resumeData.gender">
                <i class="el-icon-user"></i>
                {{ resumeData.gender === 1 ? '男' : '女' }}
              </span>
              <span v-if="resumeData.birthDate">
                <i class="el-icon-date"></i>
                {{ resumeData.birthDate }}
              </span>
              <span v-if="resumeData.currentCity">
                <i class="el-icon-location"></i>
                {{ resumeData.currentCity }}
              </span>
            </div>
            <div class="contact-info">
              <span v-if="resumeData.phone">
                <i class="el-icon-phone"></i>
                {{ resumeData.phone }}
              </span>
              <span v-if="resumeData.email">
                <i class="el-icon-message"></i>
                {{ resumeData.email }}
              </span>
            </div>
          </div>
        </div>
        
        <!-- 求职意向 -->
        <div class="resume-section" v-if="resumeData.jobIntention">
          <div class="section-title">
            <i class="el-icon-suitcase"></i>
            <span>求职意向</span>
          </div>
          <div class="section-content">
            <div class="intention-grid">
              <div class="intention-item">
                <span class="label">期望职位：</span>
                <span class="value">{{ resumeData.jobIntention }}</span>
              </div>
              <div class="intention-item">
                <span class="label">期望薪资：</span>
                <span class="value">{{ resumeData.expectedSalaryMin ? (resumeData.expectedSalaryMin / 1000) : '面议' }} - {{ resumeData.expectedSalaryMax ? (resumeData.expectedSalaryMax / 1000) : '面议' }}K/月</span>
              </div>
              <div class="intention-item">
                <span class="label">期望城市：</span>
                <span class="value">{{ resumeData.expectedCity || '不限' }}</span>
              </div>
              <div class="intention-item">
                <span class="label">工作状态：</span>
                <span class="value">{{ resumeData.workStatus || '未填写' }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 自我评价 -->
        <div class="resume-section" v-if="resumeData.selfIntroduction">
          <div class="section-title">
            <i class="el-icon-edit"></i>
            <span>自我评价</span>
          </div>
          <div class="section-content">
            <p class="self-intro">{{ resumeData.selfIntroduction }}</p>
          </div>
        </div>
        
        <!-- 工作经历 -->
        <div class="resume-section" v-if="resumeData.workExperiences && resumeData.workExperiences.length > 0">
          <div class="section-title">
            <i class="el-icon-office-building"></i>
            <span>工作经历</span>
          </div>
          <div class="section-content">
            <div 
              v-for="(work, index) in resumeData.workExperiences" 
              :key="work.id || index"
              class="experience-item"
            >
              <div class="experience-header">
                <div class="company-info">
                  <span class="company-name">{{ work.companyName }}</span>
                  <span class="position">{{ work.position }}</span>
                </div>
                <div class="time-info">
                  <span class="time">{{ work.startDate }} - {{ work.isCurrent ? '至今' : work.endDate }}</span>
                  <span class="salary" v-if="work.salaryMin && work.salaryMax">
                    {{ work.salaryMin / 1000 }}-{{ work.salaryMax / 1000 }}K/月
                  </span>
                </div>
              </div>
              <div class="experience-content">
                <div class="content-block" v-if="work.workDescription">
                  <h4>工作描述：</h4>
                  <p>{{ work.workDescription }}</p>
                </div>
                <div class="content-block" v-if="work.achievements">
                  <h4>工作业绩：</h4>
                  <p>{{ work.achievements }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 项目经历 -->
        <div class="resume-section" v-if="resumeData.projectExperiences && resumeData.projectExperiences.length > 0">
          <div class="section-title">
            <i class="el-icon-s-data"></i>
            <span>项目经历</span>
          </div>
          <div class="section-content">
            <div 
              v-for="(project, index) in resumeData.projectExperiences" 
              :key="project.id || index"
              class="experience-item"
            >
              <div class="experience-header">
                <div class="project-info">
                  <span class="project-name">{{ project.projectName }}</span>
                  <span class="role">{{ project.role }}</span>
                </div>
                <div class="time-info">
                  <span class="time">{{ project.startDate }} - {{ project.endDate }}</span>
                </div>
              </div>
              <div class="experience-content">
                <div class="content-block" v-if="project.projectDescription">
                  <h4>项目描述：</h4>
                  <p>{{ project.projectDescription }}</p>
                </div>
                <div class="content-block" v-if="project.responsibilities">
                  <h4>工作职责：</h4>
                  <p>{{ project.responsibilities }}</p>
                </div>
                <div class="content-block" v-if="project.achievements">
                  <h4>项目成果：</h4>
                  <p>{{ project.achievements }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 教育经历 -->
        <div class="resume-section" v-if="resumeData.educations && resumeData.educations.length > 0">
          <div class="section-title">
            <i class="el-icon-reading"></i>
            <span>教育经历</span>
          </div>
          <div class="section-content">
            <div 
              v-for="(edu, index) in resumeData.educations" 
              :key="edu.id || index"
              class="education-item"
            >
              <div class="education-header">
                <span class="school-name">{{ edu.schoolName }}</span>
                <span class="time">{{ edu.startDate }} - {{ edu.endDate }}</span>
              </div>
              <div class="education-detail">
                <span class="major">{{ edu.major }}</span>
                <span class="education">{{ edu.education }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 专业技能 -->
        <div class="resume-section" v-if="resumeData.skills && resumeData.skills.length > 0">
          <div class="section-title">
            <i class="el-icon-coin"></i>
            <span>专业技能</span>
          </div>
          <div class="section-content">
            <div class="skills-container">
              <el-tag 
                v-for="(skill, index) in resumeData.skills" 
                :key="skill.id || index"
                size="medium"
                effect="plain"
                class="skill-tag"
              >
                {{ skill.skillName }} ({{ skill.proficiency }})
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { get } from '@/utils/request';

export default {
  name: 'ResumePreview',
  data() {
    return {
      resumeData: {
        userName: '',
        avatar: '',
        gender: null,
        birthDate: '',
        phone: '',
        email: '',
        currentCity: '',
        jobIntention: '',
        expectedSalaryMin: null,
        expectedSalaryMax: null,
        expectedCity: '',
        workStatus: '',
        selfIntroduction: '',
        educations: [],
        workExperiences: [],
        projectExperiences: [],
        skills: []
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
          this.resumeData = { ...this.resumeData, ...res.data };
        }
      } catch (error) {
        console.error('获取简历失败:', error);
      }
    },
    // 返回编辑页
    goBack() {
      this.$router.push('/jobseeker/resume');
    },
    // 下载简历（模拟）
    downloadResume() {
      this.$message.success('简历下载功能开发中');
    }
  }
};
</script>

<style scoped>
.resume-preview-page {
  min-height: 100%;
  background-color: #f5f7fa;
  padding: 20px;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

/* 顶部操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  padding: 15px 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.action-bar .title {
  font-size: 18px;
  color: #333;
  font-weight: 500;
}

/* 简历卡片 */
.resume-card {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

/* 头部信息 */
.resume-header {
  display: flex;
  align-items: flex-start;
  gap: 30px;
  padding-bottom: 30px;
  border-bottom: 2px solid #409eff;
  margin-bottom: 30px;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar {
  border: 3px solid #f0f0f0;
}

.info-section {
  flex: 1;
}

.name {
  font-size: 32px;
  color: #333;
  margin: 0 0 15px 0;
  font-weight: 600;
}

.basic-info,
.contact-info {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.basic-info span,
.contact-info span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.basic-info i,
.contact-info i {
  color: #999;
}

/* 章节样式 */
.resume-section {
  margin-bottom: 30px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  color: #333;
  font-weight: 600;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 15px;
}

.section-title i {
  color: #409eff;
}

.section-content {
  padding: 0 10px;
}

/* 求职意向 */
.intention-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.intention-item {
  display: flex;
  align-items: center;
  font-size: 15px;
}

.intention-item .label {
  color: #666;
  width: 80px;
  flex-shrink: 0;
}

.intention-item .value {
  color: #333;
  font-weight: 500;
}

/* 自我评价 */
.self-intro {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
  margin: 0;
  text-indent: 2em;
}

/* 工作经历/项目经历 */
.experience-item {
  margin-bottom: 25px;
  padding-bottom: 25px;
  border-bottom: 1px dashed #e4e7ed;
}

.experience-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.experience-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.company-info,
.project-info {
  display: flex;
  align-items: baseline;
  gap: 15px;
}

.company-name,
.project-name {
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.position,
.role {
  font-size: 15px;
  color: #409eff;
  font-weight: 500;
}

.time-info {
  text-align: right;
}

.time-info .time {
  display: block;
  font-size: 14px;
  color: #999;
}

.time-info .salary {
  display: block;
  font-size: 14px;
  color: #fa5555;
  font-weight: 500;
  margin-top: 5px;
}

.experience-content {
  font-size: 14px;
  line-height: 1.8;
  color: #333;
}

.content-block {
  margin-bottom: 10px;
}

.content-block:last-child {
  margin-bottom: 0;
}

.content-block h4 {
  font-size: 14px;
  color: #666;
  margin: 0 0 5px 0;
  font-weight: 500;
}

.content-block p {
  margin: 0;
  text-indent: 2em;
}

/* 教育经历 */
.education-item {
  margin-bottom: 15px;
}

.education-item:last-child {
  margin-bottom: 0;
}

.education-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.school-name {
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.education-header .time {
  font-size: 14px;
  color: #999;
}

.education-detail {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

/* 专业技能 */
.skills-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.skill-tag {
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 768px) {
  .resume-preview-page {
    padding: 15px;
  }
  
  .action-bar {
    padding: 12px 15px;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .action-bar .title {
    order: -1;
    width: 100%;
    text-align: center;
    margin-bottom: 5px;
  }
  
  .resume-card {
    padding: 20px;
  }
  
  .resume-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 20px;
  }
  
  .basic-info,
  .contact-info {
    justify-content: center;
    gap: 15px;
  }
  
  .intention-grid {
    grid-template-columns: 1fr;
  }
  
  .experience-header {
    flex-direction: column;
    gap: 8px;
  }
  
  .time-info {
    text-align: left;
  }
  
  .company-info,
  .project-info {
    flex-direction: column;
    gap: 5px;
  }
  
  .education-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .education-detail {
    flex-direction: column;
    gap: 5px;
  }
}
</style>
