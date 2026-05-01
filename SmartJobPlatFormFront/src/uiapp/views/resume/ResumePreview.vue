<template>
  <div class="resume-preview-page">
    <div class="page-header">
      <div class="back-btn" @click="$router.back()">
        <i class="el-icon-arrow-left"></i>
      </div>
      <span class="title">简历预览</span>
      <div class="action-btn" @click="editResume">
        <i class="el-icon-edit"></i> 编辑
      </div>
    </div>
    
    <div class="resume-container">
      <div class="resume-header" v-if="resumeData">
        <div class="user-avatar">
          <el-avatar :size="80" :src="resumeData.avatar">
            <i class="el-icon-user"></i>
          </el-avatar>
        </div>
        <div class="user-info">
          <h2>{{ resumeData.name || '求职者' }}</h2>
          <div class="user-meta">
            <span v-if="resumeData.gender">{{ resumeData.gender }}</span>
            <span v-if="resumeData.age">{{ resumeData.age }}岁</span>
            <span v-if="resumeData.workYears">{{ resumeData.workYears }}</span>
            <span v-if="resumeData.highestEducation">{{ resumeData.highestEducation }}</span>
          </div>
          <div class="user-contact">
            <span v-if="resumeData.phone">
              <i class="el-icon-mobile-phone"></i> {{ resumeData.phone }}
            </span>
            <span v-if="resumeData.email">
              <i class="el-icon-message"></i> {{ resumeData.email }}
            </span>
          </div>
        </div>
      </div>
      
      <div class="resume-section" v-if="resumeData?.intentPosition">
        <div class="section-title">
          <i class="el-icon-position"></i> 求职意向
        </div>
        <div class="section-content intent-content">
          <div class="intent-row">
            <span class="label">期望职位：</span>
            <span class="value">{{ resumeData.intentPosition }}</span>
          </div>
          <div class="intent-row" v-if="resumeData.intentCity">
            <span class="label">期望城市：</span>
            <span class="value">{{ resumeData.intentCity }}</span>
          </div>
          <div class="intent-row" v-if="resumeData.intentSalaryMin && resumeData.intentSalaryMax">
            <span class="label">期望薪资：</span>
            <span class="value salary">{{ formatSalary(resumeData.intentSalaryMin, resumeData.intentSalaryMax) }}K/月</span>
          </div>
        </div>
      </div>
      
      <div class="resume-section" v-if="resumeData?.education?.length">
        <div class="section-title">
          <i class="el-icon-college"></i> 教育经历
        </div>
        <div class="section-content">
          <div class="edu-item" v-for="(edu, index) in resumeData.education" :key="index">
            <div class="edu-header">
              <span class="school">{{ edu.school }}</span>
              <span class="time">{{ edu.startDate }} - {{ edu.endDate }}</span>
            </div>
            <div class="edu-detail">
              <span>{{ edu.major }}</span>
              <span>{{ edu.degree }}</span>
            </div>
            <div class="edu-desc" v-if="edu.description">
              {{ edu.description }}
            </div>
          </div>
        </div>
      </div>
      
      <div class="resume-section" v-if="resumeData?.work?.length">
        <div class="section-title">
          <i class="el-icon-office-building"></i> 工作经历
        </div>
        <div class="section-content">
          <div class="work-item" v-for="(work, index) in resumeData.work" :key="index">
            <div class="work-header">
              <div class="company-info">
                <span class="company">{{ work.company }}</span>
                <span class="department" v-if="work.department">{{ work.department }}</span>
              </div>
              <span class="time">{{ work.startDate }} - {{ work.endDate || '至今' }}</span>
            </div>
            <div class="work-position">
              <span>{{ work.position }}</span>
            </div>
            <div class="work-desc" v-if="work.description">
              {{ work.description }}
            </div>
          </div>
        </div>
      </div>
      
      <div class="resume-section" v-if="resumeData?.projects?.length">
        <div class="section-title">
          <i class="el-icon-s-data"></i> 项目经历
        </div>
        <div class="section-content">
          <div class="project-item" v-for="(project, index) in resumeData.projects" :key="index">
            <div class="project-header">
              <span class="name">{{ project.name }}</span>
              <span class="time">{{ project.startDate }} - {{ project.endDate }}</span>
            </div>
            <div class="project-role" v-if="project.role">
              担任角色：{{ project.role }}
            </div>
            <div class="project-desc" v-if="project.description">
              {{ project.description }}
            </div>
          </div>
        </div>
      </div>
      
      <div class="resume-section" v-if="resumeData?.skills?.length">
        <div class="section-title">
          <i class="el-icon-coin"></i> 技能特长
        </div>
        <div class="section-content skills-content">
          <div class="skills-tags">
            <span v-for="(skill, index) in resumeData.skills" :key="index" class="skill-tag">
              {{ skill }}
            </span>
          </div>
        </div>
      </div>
      
      <div class="resume-section" v-if="resumeData?.selfEvaluation">
        <div class="section-title">
          <i class="el-icon-user"></i> 自我评价
        </div>
        <div class="section-content">
          <div class="self-evaluation">
            {{ resumeData.selfEvaluation }}
          </div>
        </div>
      </div>
      
      <div class="resume-empty" v-if="!hasResume">
        <div class="empty-icon">
          <i class="el-icon-document-copy"></i>
        </div>
        <p>您还没有简历</p>
        <el-button type="primary" @click="createResume">
          <i class="el-icon-plus"></i> 创建简历
        </el-button>
      </div>
    </div>
    
    <div class="bottom-actions">
      <el-button type="default" size="large" @click="editResume">
        <i class="el-icon-edit"></i> 编辑简历
      </el-button>
      <el-button type="primary" size="large" @click="shareResume">
        <i class="el-icon-share"></i> 分享简历
      </el-button>
      <el-button type="success" size="large" @click="downloadResume">
        <i class="el-icon-download"></i> 下载简历
      </el-button>
    </div>
  </div>
</template>

<script>
import { get } from '@/utils/request';

export default {
  name: 'UIResumePreview',
  data() {
    return {
      resumeData: null,
      hasResume: false
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
          this.resumeData = res.data;
          this.hasResume = true;
        } else {
          this.hasResume = false;
        }
      } catch (error) {
        console.error('获取简历失败:', error);
        this.hasResume = false;
      }
    },
    editResume() {
      this.$router.push('/resume/edit');
    },
    createResume() {
      this.$router.push('/resume/edit');
    },
    shareResume() {
      this.$message.info('分享功能开发中');
    },
    downloadResume() {
      this.$message.info('下载功能开发中');
    },
    formatSalary(min, max) {
      return `${(min / 1000).toFixed(0)}-${(max / 1000).toFixed(0)}`;
    }
  }
};
</script>

<style scoped>
.resume-preview-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 70px;
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

.page-header .title {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 14px;
  color: #409eff;
  cursor: pointer;
}

/* 简历容器 */
.resume-container {
  max-width: 800px;
  margin: 10px auto;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 简历头部 */
.resume-header {
  display: flex;
  gap: 20px;
  padding-bottom: 20px;
  border-bottom: 2px solid #409eff;
  margin-bottom: 20px;
}

.user-avatar {
  flex-shrink: 0;
}

.user-avatar .el-avatar {
  border: 3px solid #ecf5ff;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-info h2 {
  font-size: 24px;
  color: #303133;
  margin: 0;
  font-weight: 600;
}

.user-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 14px;
  color: #606266;
}

.user-contact {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 14px;
  color: #606266;
}

.user-contact span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.user-contact i {
  color: #409eff;
}

/* 简历章节 */
.resume-section {
  margin-bottom: 25px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.section-title i {
  color: #409eff;
  font-size: 18px;
}

.section-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

/* 求职意向 */
.intent-content {
  background: #fafafa;
  padding: 15px;
  border-radius: 8px;
}

.intent-row {
  display: flex;
  margin-bottom: 8px;
}

.intent-row:last-child {
  margin-bottom: 0;
}

.intent-row .label {
  color: #909399;
  width: 80px;
  flex-shrink: 0;
}

.intent-row .value {
  color: #303133;
  font-weight: 500;
}

.intent-row .salary {
  color: #fa5555;
  font-weight: bold;
}

/* 教育经历 */
.edu-item {
  margin-bottom: 15px;
  padding: 15px;
  background: #fafafa;
  border-radius: 8px;
}

.edu-item:last-child {
  margin-bottom: 0;
}

.edu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.school {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}

.time {
  font-size: 12px;
  color: #909399;
}

.edu-detail {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
}

.edu-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}

/* 工作经历 */
.work-item {
  margin-bottom: 20px;
  padding: 15px;
  background: #fafafa;
  border-radius: 8px;
}

.work-item:last-child {
  margin-bottom: 0;
}

.work-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.company-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.company {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}

.department {
  font-size: 13px;
  color: #909399;
}

.work-position {
  font-size: 14px;
  color: #409eff;
  margin-bottom: 10px;
  font-weight: 500;
}

.work-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
}

/* 项目经历 */
.project-item {
  margin-bottom: 20px;
  padding: 15px;
  background: #fafafa;
  border-radius: 8px;
}

.project-item:last-child {
  margin-bottom: 0;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.name {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}

.project-role {
  font-size: 13px;
  color: #409eff;
  margin-bottom: 8px;
}

.project-desc {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
}

/* 技能特长 */
.skills-content {
  padding: 10px 0;
}

.skills-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.skill-tag {
  padding: 6px 16px;
  background: linear-gradient(135deg, #ecf5ff 0%, #d9ecff 100%);
  color: #409eff;
  font-size: 13px;
  border-radius: 16px;
  border: 1px solid #b3d8ff;
}

/* 自我评价 */
.self-evaluation {
  padding: 15px;
  background: #fafafa;
  border-radius: 8px;
  text-indent: 2em;
  font-size: 14px;
  line-height: 2;
  color: #606266;
}

/* 空状态 */
.resume-empty {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  width: 100px;
  height: 100px;
  background: #ecf5ff;
  border-radius: 50%;
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon i {
  font-size: 50px;
  color: #409eff;
}

.resume-empty p {
  font-size: 15px;
  color: #909399;
  margin: 0 0 25px 0;
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
  max-width: 800px;
  margin: 0 auto;
}

.bottom-actions .el-button {
  flex: 1;
  border-radius: 20px;
  height: 40px;
  font-size: 14px;
}

@media screen and (min-width: 800px) {
  .bottom-actions {
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>
