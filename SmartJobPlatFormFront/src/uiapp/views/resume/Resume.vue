<template>
  <div class="resume-page">
    <div class="page-header">
      <span class="title">我的简历</span>
    </div>
    
    <div class="resume-card" v-if="hasResume">
      <div class="resume-header" @click="goToPreview">
        <div class="user-info">
          <el-avatar :size="60" :src="resumeData.avatar">
            <i class="el-icon-user"></i>
          </el-avatar>
          <div class="user-detail">
            <h3>{{ resumeData.name }}</h3>
            <div class="user-meta">
              <span>{{ resumeData.gender }}</span>
              <span>{{ resumeData.age }}岁</span>
              <span>{{ resumeData.workYears }}</span>
            </div>
            <div class="user-contact">
              <span><i class="el-icon-mobile-phone"></i> {{ resumeData.phone }}</span>
            </div>
          </div>
          <i class="el-icon-arrow-right"></i>
        </div>
        
        <div class="intent-info">
          <div class="intent-item">
            <span class="label">求职意向</span>
            <span class="value">{{ resumeData.intentPosition }}</span>
          </div>
          <div class="intent-item">
            <span class="label">期望薪资</span>
            <span class="value salary">{{ formatSalary(resumeData.intentSalaryMin, resumeData.intentSalaryMax) }}K/月</span>
          </div>
        </div>
      </div>
      
      <div class="resume-stats">
        <div class="stat-item">
          <span class="stat-value">{{ resumeData.education?.length || 0 }}</span>
          <span class="stat-label">教育经历</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ resumeData.work?.length || 0 }}</span>
          <span class="stat-label">工作经历</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ resumeData.projects?.length || 0 }}</span>
          <span class="stat-label">项目经历</span>
        </div>
      </div>
      
      <div class="resume-actions">
        <div class="action-btn" @click="goToEdit">
          <i class="el-icon-edit"></i>
          <span>编辑简历</span>
        </div>
        <div class="action-btn" @click="goToPreview">
          <i class="el-icon-view"></i>
          <span>预览简历</span>
        </div>
        <div class="action-btn" @click="shareResume">
          <i class="el-icon-share"></i>
          <span>分享简历</span>
        </div>
      </div>
    </div>
    
    <div class="no-resume-card" v-else>
      <div class="no-resume-icon">
        <i class="el-icon-document-copy"></i>
      </div>
      <h3>您还没有简历</h3>
      <p>创建一份完整的简历，让HR更快找到您</p>
      <el-button type="primary" size="medium" @click="createResume">
        <i class="el-icon-plus"></i> 创建简历
      </el-button>
    </div>
    
    <div class="resume-tips">
      <div class="tips-header">
        <i class="el-icon-warning-outline"></i>
        <span>简历小贴士</span>
      </div>
      <ul class="tips-list">
        <li>完善的简历可以提高 3 倍的面试邀约率</li>
        <li>建议上传真实的个人照片，增加信任度</li>
        <li>详细描述工作经历和项目经验，突出个人能力</li>
        <li>确保联系方式准确无误，避免错过面试机会</li>
      </ul>
    </div>
  </div>
</template>

<script>
import { get } from '@/utils/request';
import { useUserStore } from '@/uiapp/stores/user';

export default {
  name: 'UIResume',
  data() {
    return {
      hasResume: false,
      resumeData: {
        name: '',
        avatar: '',
        gender: '',
        age: 0,
        phone: '',
        email: '',
        workYears: '',
        intentPosition: '',
        intentCity: '',
        intentSalaryMin: 0,
        intentSalaryMax: 0,
        education: [],
        work: [],
        projects: [],
        skills: []
      }
    };
  },
  computed: {
    userStore() {
      return useUserStore();
    }
  },
  mounted() {
    this.getResume();
  },
  activated() {
    if (this.userStore.isLoggedIn) {
      this.getResume();
    }
  },
  methods: {
    async getResume() {
      if (!this.userStore.isLoggedIn) {
        this.hasResume = false;
        return;
      }
      
      try {
        const res = await get('/resume/detail');
        if (res.code === 200 && res.data) {
          this.resumeData = { ...this.resumeData, ...res.data };
          this.hasResume = true;
        } else {
          this.hasResume = false;
        }
      } catch (error) {
        console.error('获取简历失败:', error);
        this.hasResume = false;
      }
    },
    createResume() {
      this.$router.push('/resume/edit');
    },
    goToEdit() {
      this.$router.push('/resume/edit');
    },
    goToPreview() {
      this.$router.push('/resume/preview');
    },
    shareResume() {
      this.$message.info('分享功能开发中');
    },
    formatSalary(min, max) {
      return `${(min / 1000).toFixed(0)}-${(max / 1000).toFixed(0)}`;
    }
  }
};
</script>

<style scoped>
.resume-page {
  min-height: calc(100vh - 50px);
  background: #f5f5f5;
  padding-bottom: 20px;
}

/* 页面头部 */
.page-header {
  height: 44px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.page-header .title {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

/* 简历卡片 */
.resume-card {
  background: #fff;
  margin: 10px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.resume-header {
  padding: 15px;
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.user-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.user-detail h3 {
  font-size: 18px;
  color: #303133;
  margin: 0;
  font-weight: 500;
}

.user-meta {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: #909399;
}

.user-contact {
  font-size: 13px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 5px;
}

.user-contact i {
  color: #409eff;
}

.intent-info {
  display: flex;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.intent-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.intent-item .label {
  font-size: 12px;
  color: #909399;
}

.intent-item .value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.intent-item .salary {
  color: #fa5555;
}

/* 统计数据 */
.resume-stats {
  display: flex;
  padding: 12px 0;
  margin: 0 15px;
  border-top: 1px solid #f5f5f5;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  color: #409eff;
  font-weight: bold;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

/* 操作按钮 */
.resume-actions {
  display: flex;
  border-top: 1px solid #f5f5f5;
}

.action-btn {
  flex: 1;
  height: 48px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:active {
  background: #f5f7fa;
}

.action-btn i {
  font-size: 18px;
  color: #409eff;
}

.action-btn span {
  font-size: 12px;
  color: #606266;
}

/* 无简历状态 */
.no-resume-card {
  background: #fff;
  margin: 10px;
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.no-resume-icon {
  width: 80px;
  height: 80px;
  background: #ecf5ff;
  border-radius: 50%;
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-resume-icon i {
  font-size: 40px;
  color: #409eff;
}

.no-resume-card h3 {
  font-size: 18px;
  color: #303133;
  margin: 0 0 8px 0;
}

.no-resume-card p {
  font-size: 14px;
  color: #909399;
  margin: 0 0 25px 0;
}

/* 简历小贴士 */
.resume-tips {
  background: #fff;
  margin: 10px;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.tips-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.tips-header i {
  font-size: 18px;
  color: #e6a23c;
}

.tips-header span {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
  padding-left: 15px;
  position: relative;
  margin-bottom: 5px;
}

.tips-list li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 8px;
  width: 6px;
  height: 6px;
  background: #e6a23c;
  border-radius: 50%;
}

.tips-list li:last-child {
  margin-bottom: 0;
}
</style>
