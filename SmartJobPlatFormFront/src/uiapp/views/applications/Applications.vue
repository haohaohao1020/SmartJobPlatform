<template>
  <div class="applications-page">
    <div class="page-header">
      <span class="title">我的投递</span>
    </div>
    
    <div class="status-tabs">
      <div 
        class="tab-item"
        :class="{ active: currentStatus === '' }"
        @click="switchStatus('')"
      >
        全部
      </div>
      <div 
        v-for="tab in statusTabs" 
        :key="tab.value"
        class="tab-item"
        :class="{ active: currentStatus === tab.value }"
        @click="switchStatus(tab.value)"
      >
        {{ tab.label }}
      </div>
    </div>
    
    <div class="application-list">
      <div 
        v-for="item in applicationList" 
        :key="item.id"
        class="application-card"
        @click="goToDetail(item.jobId)"
      >
        <div class="card-header">
          <div class="job-info">
            <span class="job-title">{{ item.jobTitle }}</span>
            <span class="salary">{{ formatSalary(item.salaryMin, item.salaryMax) }}K/月</span>
          </div>
          <el-tag :type="getStatusType(item.status)" size="small">
            {{ getStatusName(item.status) }}
          </el-tag>
        </div>
        
        <div class="company-info">
          <el-avatar :size="28" :src="item.companyLogo">
            <i class="el-icon-office-building"></i>
          </el-avatar>
          <span class="company-name">{{ item.companyName }}</span>
          <span class="location">{{ item.city }}</span>
        </div>
        
        <div class="card-footer">
          <span class="apply-time">投递时间：{{ formatTime(item.applyTime) }}</span>
          <el-button type="text" size="small" @click.stop="viewResume(item)">
            <i class="el-icon-document"></i> 查看简历
          </el-button>
        </div>
        
        <div class="status-timeline" v-if="item.timeline">
          <div 
            v-for="(node, index) in item.timeline" 
            :key="index"
            class="timeline-node"
            :class="{ active: index < item.currentStep, current: index === item.currentStep }"
          >
            <div class="node-dot"></div>
            <div class="node-info">
              <span class="node-title">{{ node.title }}</span>
              <span class="node-time">{{ node.time }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="load-more" v-if="hasMore" @click="loadMore">
        <el-button type="text" :loading="loading">
          {{ loading ? '加载中...' : '点击加载更多' }}
        </el-button>
      </div>
      
      <div class="empty" v-if="!loading && applicationList.length === 0">
        <div class="empty-icon">
          <i class="el-icon-document-copy"></i>
        </div>
        <p>暂无投递记录</p>
        <el-button type="primary" size="small" @click="goToJobs">去看岗位</el-button>
      </div>
    </div>
    
    <el-dialog
      title="简历预览"
      :visible.sync="resumeDialogVisible"
      width="90%"
      custom-class="resume-preview-dialog"
    >
      <div class="resume-preview-content" v-if="currentResume">
        <div class="resume-header">
          <el-avatar :size="60" :src="currentResume.avatar">
            <i class="el-icon-user"></i>
          </el-avatar>
          <div class="resume-basic">
            <h3>{{ currentResume.name }}</h3>
            <div class="basic-info">
              <span>{{ currentResume.gender }}</span>
              <span>{{ currentResume.age }}岁</span>
              <span>{{ currentResume.education }}</span>
              <span>{{ currentResume.workYears }}</span>
            </div>
            <div class="contact-info">
              <span><i class="el-icon-mobile-phone"></i> {{ currentResume.phone }}</span>
              <span><i class="el-icon-message"></i> {{ currentResume.email }}</span>
            </div>
          </div>
        </div>
        
        <div class="resume-section">
          <div class="section-title">求职意向</div>
          <div class="section-content">
            <div class="intent-item">
              <span class="label">期望职位：</span>
              <span class="value">{{ currentResume.intentPosition }}</span>
            </div>
            <div class="intent-item">
              <span class="label">期望城市：</span>
              <span class="value">{{ currentResume.intentCity }}</span>
            </div>
            <div class="intent-item">
              <span class="label">期望薪资：</span>
              <span class="value">{{ formatSalary(currentResume.intentSalaryMin, currentResume.intentSalaryMax) }}K/月</span>
            </div>
          </div>
        </div>
        
        <div class="resume-section" v-if="currentResume.education?.length">
          <div class="section-title">教育经历</div>
          <div class="section-content">
            <div class="edu-item" v-for="(edu, index) in currentResume.education" :key="index">
              <div class="edu-header">
                <span class="school">{{ edu.school }}</span>
                <span class="time">{{ edu.startDate }} - {{ edu.endDate }}</span>
              </div>
              <div class="edu-detail">
                <span>{{ edu.major }}</span>
                <span>{{ edu.degree }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="resume-section" v-if="currentResume.work?.length">
          <div class="section-title">工作经历</div>
          <div class="section-content">
            <div class="work-item" v-for="(work, index) in currentResume.work" :key="index">
              <div class="work-header">
                <span class="company">{{ work.company }}</span>
                <span class="time">{{ work.startDate }} - {{ work.endDate || '至今' }}</span>
              </div>
              <div class="work-detail">
                <span class="position">{{ work.position }}</span>
              </div>
              <div class="work-description">{{ work.description }}</div>
            </div>
          </div>
        </div>
        
        <div class="resume-section" v-if="currentResume.skills?.length">
          <div class="section-title">技能特长</div>
          <div class="section-content">
            <div class="skills-tags">
              <span v-for="(skill, index) in currentResume.skills" :key="index" class="skill-tag">
                {{ skill }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { get } from '@/utils/request';
import { useUserStore } from '@/uiapp/stores/user';

export default {
  name: 'UIApplications',
  data() {
    return {
      loading: false,
      currentStatus: '',
      page: 1,
      pageSize: 10,
      hasMore: true,
      applicationList: [],
      resumeDialogVisible: false,
      currentResume: null,
      statusTabs: [
        { label: '待处理', value: '1' },
        { label: '面试中', value: '2' },
        { label: '已通过', value: '3' },
        { label: '已拒绝', value: '4' }
      ]
    };
  },
  computed: {
    userStore() {
      return useUserStore();
    }
  },
  mounted() {
    this.getApplicationList();
  },
  activated() {
    if (this.userStore.isLoggedIn) {
      this.getApplicationList();
    }
  },
  methods: {
    async getApplicationList(refresh = true) {
      if (!this.userStore.isLoggedIn) {
        this.applicationList = [];
        return;
      }
      
      if (refresh) {
        this.page = 1;
        this.applicationList = [];
        this.hasMore = true;
      }
      
      if (this.loading || !this.hasMore) return;
      
      this.loading = true;
      try {
        const params = {
          page: this.page,
          pageSize: this.pageSize,
          status: this.currentStatus
        };
        
        const res = await get('/applications/list', params);
        if (res.code === 200) {
          const list = res.data.list || [];
          this.applicationList = refresh ? list : [...this.applicationList, ...list];
          this.hasMore = list.length >= this.pageSize;
          if (list.length >= this.pageSize) {
            this.page++;
          }
        }
      } catch (error) {
        console.error('获取投递列表失败:', error);
      } finally {
        this.loading = false;
      }
    },
    loadMore() {
      this.getApplicationList(false);
    },
    switchStatus(status) {
      if (this.currentStatus !== status) {
        this.currentStatus = status;
        this.getApplicationList(true);
      }
    },
    goToDetail(jobId) {
      this.$router.push(`/jobs/${jobId}`);
    },
    goToJobs() {
      this.$router.push('/jobs');
    },
    viewResume(item) {
      this.currentResume = {
        name: this.userStore.userInfo?.name || '求职者',
        avatar: this.userStore.userInfo?.avatar || '',
        gender: '男',
        age: 28,
        education: '本科',
        workYears: '3-5年',
        phone: '138****0001',
        email: 'user@example.com',
        intentPosition: '前端开发工程师',
        intentCity: '北京',
        intentSalaryMin: 20000,
        intentSalaryMax: 35000,
        education: [
          {
            school: '北京大学',
            major: '计算机科学与技术',
            degree: '本科',
            startDate: '2016-09',
            endDate: '2020-06'
          }
        ],
        work: [
          {
            company: '阿里巴巴',
            position: '高级前端工程师',
            startDate: '2020-07',
            endDate: '',
            description: '负责电商平台的前端架构设计与开发，优化页面性能，提升用户体验。参与组件库建设，提高团队开发效率。'
          }
        ],
        skills: ['Vue', 'React', 'TypeScript', 'Node.js', 'Webpack']
      };
      this.resumeDialogVisible = true;
    },
    getStatusType(status) {
      const typeMap = {
        '1': 'warning',
        '2': 'primary',
        '3': 'success',
        '4': 'danger'
      };
      return typeMap[status] || '';
    },
    getStatusName(status) {
      const nameMap = {
        '1': '待处理',
        '2': '面试中',
        '3': '已通过',
        '4': '已拒绝'
      };
      return nameMap[status] || '未知';
    },
    formatSalary(min, max) {
      return `${(min / 1000).toFixed(0)}-${(max / 1000).toFixed(0)}`;
    },
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    }
  }
};
</script>

<style scoped>
.applications-page {
  min-height: calc(100vh - 50px);
  background: #f5f5f5;
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

/* 状态标签 */
.status-tabs {
  display: flex;
  background: #fff;
  padding: 0 15px;
  border-bottom: 1px solid #f0f0f0;
  overflow-x: auto;
}

.tab-item {
  flex-shrink: 0;
  padding: 12px 15px;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  position: relative;
}

.tab-item.active {
  color: #409eff;
  font-weight: 500;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 24px;
  height: 2px;
  background: #409eff;
  border-radius: 1px;
}

/* 投递列表 */
.application-list {
  padding: 10px;
}

.application-card {
  background: #fff;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.job-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
  flex: 1;
  margin-right: 10px;
}

.job-title {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.salary {
  font-size: 15px;
  color: #fa5555;
  font-weight: bold;
}

.company-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.company-name {
  font-size: 14px;
  color: #606266;
  flex: 1;
}

.location {
  font-size: 13px;
  color: #909399;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #f5f5f5;
}

.apply-time {
  font-size: 12px;
  color: #c0c4cc;
}

/* 时间线 */
.status-timeline {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
  display: flex;
  gap: 10px;
}

.timeline-node {
  display: flex;
  align-items: center;
  gap: 6px;
}

.node-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #e4e7ed;
  flex-shrink: 0;
}

.timeline-node.active .node-dot,
.timeline-node.current .node-dot {
  background: #67c23a;
}

.timeline-node.current .node-dot {
  width: 10px;
  height: 10px;
  box-shadow: 0 0 0 3px rgba(103, 194, 58, 0.2);
}

.node-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.node-title {
  font-size: 12px;
  color: #606266;
}

.timeline-node.active .node-title,
.timeline-node.current .node-title {
  color: #67c23a;
}

.node-time {
  font-size: 10px;
  color: #c0c4cc;
}

/* 加载更多和空状态 */
.load-more, .empty {
  text-align: center;
  padding: 20px;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  padding-top: 60px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  background: #f5f7fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon i {
  font-size: 40px;
  color: #c0c4cc;
}

.empty p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 简历预览弹窗 */
::v-deep .resume-preview-dialog .el-dialog__body {
  padding: 10px 20px 20px;
}

.resume-preview-content {
  background: #fff;
  padding: 15px;
  border-radius: 8px;
}

.resume-header {
  display: flex;
  gap: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f5f5f5;
}

.resume-basic {
  flex: 1;
}

.resume-basic h3 {
  font-size: 18px;
  color: #303133;
  margin: 0 0 8px 0;
}

.basic-info, .contact-info {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 13px;
  color: #606266;
}

.resume-section {
  margin-top: 15px;
}

.section-title {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
  margin-bottom: 10px;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}

.intent-item {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.intent-item .label {
  color: #909399;
  width: 70px;
}

.intent-item .value {
  color: #303133;
}

.edu-item, .work-item {
  padding: 10px 0;
  border-bottom: 1px dashed #f5f5f5;
}

.edu-item:last-child, .work-item:last-child {
  border-bottom: none;
}

.edu-header, .work-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.school, .company {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}

.time {
  font-size: 12px;
  color: #909399;
}

.edu-detail, .work-detail {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #606266;
}

.work-description {
  margin-top: 8px;
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}

.skills-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.skill-tag {
  padding: 4px 12px;
  background: #ecf5ff;
  color: #409eff;
  font-size: 13px;
  border-radius: 4px;
}
</style>
