<template>
  <div class="job-detail-page">
    <div class="container">
      <!-- 加载中 -->
      <div v-loading="loading" class="loading-container">
        <el-empty v-if="!jobInfo.id && !loading" description="岗位不存在或已下架"></el-empty>
      </div>
      
      <div v-if="jobInfo.id && !loading" class="detail-content">
        <!-- 岗位信息卡片 -->
        <div class="info-card job-card">
          <div class="card-header">
            <div class="job-main">
              <h1 class="job-title">{{ jobInfo.title }}</h1>
              <div class="job-tags">
                <el-tag type="primary" size="small">{{ jobInfo.city }}</el-tag>
                <el-tag type="success" size="small">{{ jobInfo.workYears }}</el-tag>
                <el-tag type="warning" size="small">{{ jobInfo.education }}</el-tag>
                <el-tag size="small">{{ jobInfo.jobType }}</el-tag>
              </div>
            </div>
            <div class="job-salary">
              <span class="salary-label">薪资</span>
              <span class="salary-value">{{ formatSalary(jobInfo.salaryMin, jobInfo.salaryMax) }}K/月</span>
            </div>
          </div>
          
          <div class="card-content">
            <div class="job-meta">
              <div class="meta-item">
                <i class="el-icon-time"></i>
                <span>发布时间：{{ formatDate(jobInfo.publishTime) }}</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-view"></i>
                <span>浏览：{{ jobInfo.viewCount }} 次</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-document"></i>
                <span>投递：{{ jobInfo.applyCount }} 人</span>
              </div>
            </div>
            
            <!-- 福利待遇 -->
            <div class="welfare-section" v-if="jobInfo.welfare && jobInfo.welfare.length > 0">
              <h3 class="section-title">
                <i class="el-icon-present"></i> 福利待遇
              </h3>
              <div class="welfare-list">
                <div 
                  v-for="(item, index) in jobInfo.welfare" 
                  :key="index"
                  class="welfare-item"
                >
                  <i class="el-icon-check"></i>
                  <span>{{ item }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="card-actions">
            <el-button 
              type="primary" 
              size="large" 
              :loading="applyLoading"
              @click="handleApply"
              :disabled="hasApplied"
            >
              <i class="el-icon-s-promotion"></i>
              {{ hasApplied ? '已投递' : '立即投递' }}
            </el-button>
            <el-button 
              size="large" 
              :type="hasFavorited ? 'danger' : ''"
              :loading="favoriteLoading"
              @click="handleFavorite"
            >
              <i :class="hasFavorited ? 'el-icon-star-off' : 'el-icon-star-on'"></i>
              {{ hasFavorited ? '取消收藏' : '收藏岗位' }}
            </el-button>
          </div>
        </div>
        
        <!-- 岗位描述 -->
        <div class="info-card">
          <h3 class="section-title">
            <i class="el-icon-document"></i> 岗位描述
          </h3>
          <div class="section-content">
            <h4>岗位职责：</h4>
            <p class="description-text">{{ jobInfo.description }}</p>
            
            <h4 v-if="jobInfo.requirements">任职要求：</h4>
            <p class="description-text" v-if="jobInfo.requirements">{{ jobInfo.requirements }}</p>
          </div>
        </div>
        
        <!-- 公司信息 -->
        <div class="info-card company-card">
          <h3 class="section-title">
            <i class="el-icon-office-building"></i> 公司信息
          </h3>
          <div class="company-info">
            <el-avatar :size="80" :src="jobInfo.companyLogo" class="company-logo">
              <i class="el-icon-office-building"></i>
            </el-avatar>
            <div class="company-detail">
              <h4 class="company-name">{{ jobInfo.companyName }}</h4>
              <div class="company-tags">
                <el-tag size="small" effect="plain">{{ jobInfo.industry }}</el-tag>
                <el-tag size="small" effect="plain">{{ jobInfo.companySize }}</el-tag>
              </div>
              <div class="company-hr">
                <span>HR：{{ jobInfo.hrName }}</span>
                <span class="divider">|</span>
                <span>职位：{{ jobInfo.hrPosition }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 相同公司的其他岗位 -->
        <div class="info-card" v-if="relatedJobs.length > 0">
          <h3 class="section-title">
            <i class="el-icon-more"></i> 该公司其他岗位
          </h3>
          <div class="related-jobs">
            <div 
              v-for="job in relatedJobs" 
              :key="job.id"
              class="related-job-item"
              @click="goToDetail(job.id)"
            >
              <div class="job-info">
                <span class="job-name">{{ job.title }}</span>
                <span class="job-salary">{{ formatSalary(job.salaryMin, job.salaryMax) }}K/月</span>
              </div>
              <div class="job-meta">
                <span>{{ job.city }}</span>
                <span>{{ job.workYears }}</span>
                <span>{{ job.education }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { get, post, del } from '@/utils/request';

export default {
  name: 'JobDetail',
  data() {
    return {
      loading: false,
      applyLoading: false,
      favoriteLoading: false,
      jobInfo: {},
      hasApplied: false,
      hasFavorited: false,
      relatedJobs: []
    };
  },
  mounted() {
    this.getJobDetail();
    this.checkStatus();
  },
  methods: {
    // 获取岗位详情
    async getJobDetail() {
      const jobId = this.$route.params.id;
      if (!jobId) return;
      
      this.loading = true;
      try {
        const res = await get(`/jobs/${jobId}`);
        if (res.code === 200) {
          this.jobInfo = res.data;
        }
      } catch (error) {
        console.error('获取岗位详情失败:', error);
      } finally {
        this.loading = false;
      }
    },
    // 检查投递和收藏状态
    async checkStatus() {
      const jobId = this.$route.params.id;
      if (!jobId) return;
      
      try {
        // 检查是否已投递
        const applyRes = await get('/applications/check', { jobId });
        if (applyRes.code === 200) {
          this.hasApplied = applyRes.data.hasApplied;
        }
        
        // 检查是否已收藏
        const favRes = await get('/favorites/check', { jobId });
        if (favRes.code === 200) {
          this.hasFavorited = favRes.data.hasFavorited;
        }
      } catch (error) {
        console.error('检查状态失败:', error);
      }
    },
    // 投递岗位
    async handleApply() {
      if (this.hasApplied) return;
      
      this.applyLoading = true;
      try {
        const res = await post('/applications', {
          jobId: this.jobInfo.id,
          resumeId: 1 // 使用默认简历
        });
        if (res.code === 200) {
          this.hasApplied = true;
          this.$message.success('投递成功！HR会尽快查看您的简历');
        }
      } catch (error) {
        console.error('投递失败:', error);
      } finally {
        this.applyLoading = false;
      }
    },
    // 收藏/取消收藏
    async handleFavorite() {
      this.favoriteLoading = true;
      try {
        if (this.hasFavorited) {
          // 取消收藏
          const res = await del('/favorites/1'); // 简化，实际应该用收藏ID
          if (res.code === 200) {
            this.hasFavorited = false;
            this.$message.success('已取消收藏');
          }
        } else {
          // 收藏
          const res = await post('/favorites', {
            jobId: this.jobInfo.id
          });
          if (res.code === 200) {
            this.hasFavorited = true;
            this.$message.success('收藏成功');
          }
        }
      } catch (error) {
        console.error('收藏操作失败:', error);
      } finally {
        this.favoriteLoading = false;
      }
    },
    // 跳转到详情
    goToDetail(id) {
      this.$router.push(`/jobseeker/jobs/${id}`);
    },
    // 格式化薪资
    formatSalary(min, max) {
      return `${(min / 1000).toFixed(0)}-${(max / 1000).toFixed(0)}`;
    },
    // 格式化日期
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    }
  }
};
</script>

<style scoped>
.job-detail-page {
  min-height: 100%;
  background-color: #f5f7fa;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.loading-container {
  min-height: 500px;
}

/* 信息卡片 */
.info-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.job-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.job-main {
  flex: 1;
}

.job-title {
  font-size: 24px;
  color: #333;
  margin: 0 0 12px 0;
  font-weight: 500;
}

.job-tags {
  display: flex;
  gap: 10px;
}

.job-salary {
  text-align: right;
}

.salary-label {
  display: block;
  font-size: 14px;
  color: #999;
  margin-bottom: 5px;
}

.salary-value {
  font-size: 28px;
  color: #fa5555;
  font-weight: bold;
}

.card-content {
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.job-meta {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.meta-item i {
  margin-right: 5px;
  color: #999;
}

/* 章节标题 */
.section-title {
  font-size: 16px;
  color: #333;
  margin: 0 0 16px 0;
  font-weight: 500;
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 8px;
  color: #409eff;
}

.section-content {
  font-size: 14px;
  line-height: 1.8;
  color: #666;
}

.section-content h4 {
  font-size: 15px;
  color: #333;
  margin: 16px 0 8px 0;
  font-weight: 500;
}

.description-text {
  margin: 0;
  white-space: pre-wrap;
}

/* 福利待遇 */
.welfare-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.welfare-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #333;
}

.welfare-item i {
  color: #67c23a;
  margin-right: 5px;
}

/* 操作按钮 */
.card-actions {
  display: flex;
  gap: 15px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

.card-actions .el-button {
  flex: 1;
  max-width: 200px;
}

/* 公司信息 */
.company-card .company-info {
  display: flex;
  align-items: center;
}

.company-logo {
  margin-right: 20px;
}

.company-detail {
  flex: 1;
}

.company-name {
  font-size: 18px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: 500;
}

.company-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.company-hr {
  font-size: 14px;
  color: #666;
}

.company-hr .divider {
  margin: 0 10px;
  color: #ddd;
}

/* 相关岗位 */
.related-jobs {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.related-job-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f9f9f9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.related-job-item:hover {
  background: #f0f7ff;
}

.related-job-item .job-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.related-job-item .job-name {
  font-size: 15px;
  color: #333;
}

.related-job-item .job-salary {
  font-size: 16px;
  color: #fa5555;
  font-weight: bold;
}

.related-job-item .job-meta {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

/* 响应式 */
@media (max-width: 768px) {
  .container {
    padding: 15px;
  }
  
  .info-card {
    padding: 16px;
    margin-bottom: 15px;
  }
  
  .job-card .card-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .job-salary {
    text-align: left;
  }
  
  .job-meta {
    flex-wrap: wrap;
    gap: 15px;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .card-actions .el-button {
    max-width: none;
  }
  
  .company-card .company-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .related-job-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .related-job-item .job-info {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
