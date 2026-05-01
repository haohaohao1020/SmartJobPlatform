<template>
  <div class="job-detail-page">
    <div class="nav-header">
      <div class="back-btn" @click="$router.back()">
        <i class="el-icon-arrow-left"></i>
      </div>
      <span class="title">岗位详情</span>
      <div class="share-btn" @click="shareJob">
        <i class="el-icon-share"></i>
      </div>
    </div>
    
    <div class="job-header-card" v-if="jobInfo">
      <div class="job-main">
        <h1 class="job-title">{{ jobInfo.title }}</h1>
        <span class="salary">{{ formatSalary(jobInfo.salaryMin, jobInfo.salaryMax) }}K/月</span>
      </div>
      
      <div class="job-tags">
        <span class="tag-item">{{ jobInfo.city }}</span>
        <span class="tag-item">{{ jobInfo.workYears }}</span>
        <span class="tag-item">{{ jobInfo.education }}</span>
        <span class="tag-item">{{ jobInfo.jobType }}</span>
      </div>
      
      <div class="job-meta">
        <div class="meta-item">
          <i class="el-icon-time"></i>
          <span>发布于 {{ formatTime(jobInfo.publishTime) }}</span>
        </div>
        <div class="meta-item">
          <i class="el-icon-view"></i>
          <span>{{ jobInfo.viewCount }}人浏览</span>
        </div>
        <div class="meta-item">
          <i class="el-icon-document"></i>
          <span>{{ jobInfo.applyCount }}人投递</span>
        </div>
      </div>
    </div>
    
    <div class="company-card" v-if="jobInfo">
      <div class="company-main" @click="goToCompany">
        <el-avatar :size="50" :src="jobInfo.companyLogo">
          <i class="el-icon-office-building"></i>
        </el-avatar>
        <div class="company-info">
          <div class="company-name">{{ jobInfo.companyName }}</div>
          <div class="company-meta">{{ jobInfo.industry }} · {{ jobInfo.companySize }}人</div>
        </div>
        <i class="el-icon-arrow-right"></i>
      </div>
      
      <div class="company-brief" v-if="jobInfo.companyDescription">
        {{ jobInfo.companyDescription }}
      </div>
    </div>
    
    <div class="section-card">
      <div class="section-header">
        <i class="el-icon-info"></i>
        <span>岗位职责</span>
      </div>
      <div class="section-content">
        <ul class="duty-list" v-if="jobInfo?.responsibilities?.length">
          <li v-for="(item, index) in jobInfo.responsibilities" :key="index">
            <span class="index">{{ index + 1 }}.</span>
            <span>{{ item }}</span>
          </li>
        </ul>
        <p v-else class="empty-text">暂无岗位描述</p>
      </div>
    </div>
    
    <div class="section-card">
      <div class="section-header">
        <i class="el-icon-tickets"></i>
        <span>任职要求</span>
      </div>
      <div class="section-content">
        <ul class="req-list" v-if="jobInfo?.requirements?.length">
          <li v-for="(item, index) in jobInfo.requirements" :key="index">
            <span class="dot"></span>
            <span>{{ item }}</span>
          </li>
        </ul>
        <p v-else class="empty-text">暂无任职要求</p>
      </div>
    </div>
    
    <div class="section-card" v-if="jobInfo?.welfare?.length">
      <div class="section-header">
        <i class="el-icon-present"></i>
        <span>福利待遇</span>
      </div>
      <div class="section-content">
        <div class="welfare-tags">
          <span v-for="(item, index) in jobInfo.welfare" :key="index" class="welfare-tag">
            <i class="el-icon-check"></i>
            {{ item }}
          </span>
        </div>
      </div>
    </div>
    
    <div class="section-card" v-if="jobInfo">
      <div class="section-header">
        <i class="el-icon-location"></i>
        <span>工作地点</span>
      </div>
      <div class="section-content">
        <div class="address-info">
          <i class="el-icon-location"></i>
          <span>{{ jobInfo.address }}</span>
        </div>
        <div class="map-placeholder">
          <i class="el-icon-map-location"></i>
          <span>点击查看地图</span>
        </div>
      </div>
    </div>
    
    <div class="bottom-bar">
      <div class="action-left">
        <div class="action-item" @click="toggleFavorite">
          <i :class="isFavorite ? 'el-icon-star-on' : 'el-icon-star-off'" :style="{ color: isFavorite ? '#ffb800' : '' }"></i>
          <span>{{ isFavorite ? '已收藏' : '收藏' }}</span>
        </div>
        <div class="action-item" @click="shareJob">
          <i class="el-icon-share"></i>
          <span>分享</span>
        </div>
      </div>
      <div class="action-right">
        <div class="action-btn primary" @click="applyJob">
          立即投递
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { get, post } from '@/utils/request';
import { useUserStore } from '@/uiapp/stores/user';

export default {
  name: 'UIJobDetail',
  data() {
    return {
      loading: false,
      jobId: null,
      jobInfo: null,
      isFavorite: false
    };
  },
  computed: {
    userStore() {
      return useUserStore();
    }
  },
  beforeRouteEnter(to, from, next) {
    next(vm => {
      vm.jobId = to.params.id;
      vm.getJobDetail();
    });
  },
  methods: {
    async getJobDetail() {
      this.loading = true;
      try {
        const res = await get(`/jobs/${this.jobId}`);
        if (res.code === 200) {
          this.jobInfo = res.data;
          if (res.data.isFavorite !== undefined) {
            this.isFavorite = res.data.isFavorite;
          }
        }
      } catch (error) {
        this.$message.error('获取岗位详情失败');
      } finally {
        this.loading = false;
      }
    },
    async toggleFavorite() {
      if (!this.userStore.isLoggedIn) {
        this.$message.warning('请先登录');
        this.$router.push({
          path: '/login',
          query: { redirect: this.$route.fullPath }
        });
        return;
      }
      
      try {
        const res = await post(`/favorites/${this.isFavorite ? 'remove' : 'add'}`, {
          jobId: this.jobId
        });
        
        if (res.code === 200) {
          this.isFavorite = !this.isFavorite;
          this.$message.success(this.isFavorite ? '收藏成功' : '已取消收藏');
        }
      } catch (error) {
        this.$message.error('操作失败');
      }
    },
    async applyJob() {
      if (!this.userStore.isLoggedIn) {
        this.$message.warning('请先登录');
        this.$router.push({
          path: '/login',
          query: { redirect: this.$route.fullPath }
        });
        return;
      }
      
      this.$confirm('确定要投递该岗位吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await post('/applications/submit', {
            jobId: this.jobId
          });
          
          if (res.code === 200) {
            this.$message.success('投递成功！HR将尽快查看您的简历');
          } else {
            this.$message.error(res.message || '投递失败');
          }
        } catch (error) {
          this.$message.error('投递失败');
        }
      }).catch(() => {});
    },
    goToCompany() {
      this.$message.info('企业详情页开发中');
    },
    shareJob() {
      this.$message.info('分享功能开发中');
    },
    formatSalary(min, max) {
      return `${(min / 1000).toFixed(0)}-${(max / 1000).toFixed(0)}`;
    },
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    }
  }
};
</script>

<style scoped>
.job-detail-page {
  min-height: calc(100vh - 50px);
  padding-bottom: 60px;
  background: #f5f5f5;
}

/* 导航栏 */
.nav-header {
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

.back-btn, .share-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 18px;
  color: #606266;
}

.nav-header .title {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

/* 岗位头部卡片 */
.job-header-card {
  background: #fff;
  padding: 15px;
  margin-bottom: 10px;
}

.job-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.job-title {
  font-size: 20px;
  color: #303133;
  margin: 0;
  flex: 1;
  margin-right: 10px;
  font-weight: 500;
}

.salary {
  font-size: 20px;
  color: #fa5555;
  font-weight: bold;
  white-space: nowrap;
}

.job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.job-tags .tag-item {
  padding: 4px 12px;
  background: #ecf5ff;
  color: #409eff;
  font-size: 13px;
  border-radius: 4px;
}

.job-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #909399;
}

.meta-item i {
  font-size: 14px;
}

/* 企业卡片 */
.company-card {
  background: #fff;
  padding: 15px;
  margin-bottom: 10px;
}

.company-main {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.company-info {
  flex: 1;
  margin-left: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.company-name {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

.company-meta {
  font-size: 13px;
  color: #909399;
}

.company-main .el-icon-arrow-right {
  color: #c0c4cc;
  font-size: 16px;
}

.company-brief {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 章节卡片 */
.section-card {
  background: #fff;
  padding: 15px;
  margin-bottom: 10px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
}

.section-header i {
  font-size: 18px;
  color: #409eff;
}

.section-header span {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

.section-content {
  font-size: 14px;
  color: #606266;
}

/* 职责列表 */
.duty-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.duty-list li {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
  line-height: 1.8;
}

.duty-list li .index {
  color: #409eff;
  flex-shrink: 0;
}

/* 要求列表 */
.req-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.req-list li {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  line-height: 1.8;
}

.req-list li .dot {
  width: 5px;
  height: 5px;
  background: #409eff;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 10px;
}

/* 福利待遇标签 */
.welfare-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.welfare-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 6px 14px;
  background: #f0f9eb;
  color: #67c23a;
  font-size: 13px;
  border-radius: 16px;
}

.welfare-tag i {
  font-size: 12px;
}

/* 地址信息 */
.address-info {
  display: flex;
  align-items: center;
  gap: 8px;
  line-height: 1.6;
}

.address-info i {
  color: #409eff;
  font-size: 16px;
}

.map-placeholder {
  margin-top: 15px;
  height: 120px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #909399;
  cursor: pointer;
}

.map-placeholder i {
  font-size: 32px;
}

.empty-text {
  color: #c0c4cc;
  text-align: center;
  padding: 20px 0;
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 54px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 0 15px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 100;
  max-width: 750px;
  margin: 0 auto;
}

.action-left {
  display: flex;
  gap: 20px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  cursor: pointer;
}

.action-item i {
  font-size: 20px;
  color: #909399;
}

.action-item span {
  font-size: 11px;
  color: #909399;
}

.action-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  min-width: 140px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
}

.action-btn.primary {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

.action-btn.primary:active {
  opacity: 0.8;
}

@media screen and (min-width: 750px) {
  .bottom-bar {
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>
