<template>
  <div class="applications-page">
    <div class="container">
      <!-- 统计卡片 -->
      <div class="stats-section">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-icon all">
              <i class="el-icon-document"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ statistics.total || 0 }}</div>
              <div class="stat-label">全部投递</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon pending">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ statistics.pending || 0 }}</div>
              <div class="stat-label">待查看</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon interviewing">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ statistics.interviewing || 0 }}</div>
              <div class="stat-label">面试中</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon success">
              <i class="el-icon-success"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ statistics.hired || 0 }}</div>
              <div class="stat-label">已录用</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 筛选和列表 -->
      <div class="list-section">
        <!-- 筛选选项卡 -->
        <div class="filter-tabs">
          <el-tabs v-model="activeTab" @tab-click="handleTabChange">
            <el-tab-pane label="全部" name="all"></el-tab-pane>
            <el-tab-pane label="待查看" name="1"></el-tab-pane>
            <el-tab-pane label="面试中" name="interviewing"></el-tab-pane>
            <el-tab-pane label="已录用" name="6"></el-tab-pane>
            <el-tab-pane label="已拒绝" name="5"></el-tab-pane>
          </el-tabs>
        </div>
        
        <!-- 投递列表 -->
        <div class="application-list" v-loading="loading">
          <div 
            v-for="item in applicationList" 
            :key="item.id"
            class="application-card"
          >
            <div class="card-header">
              <div class="job-info" @click="goToJobDetail(item.jobId)">
                <h3 class="job-title">{{ item.jobTitle }}</h3>
                <span class="job-salary">{{ formatSalary(item.salaryMin, item.salaryMax) }}K/月</span>
              </div>
              <div class="status-info">
                <el-tag :type="getStatusType(item.status)" size="small">
                  {{ item.statusName }}
                </el-tag>
              </div>
            </div>
            
            <div class="card-content">
              <div class="company-info" @click="goToCompanyDetail(item.companyId)">
                <el-avatar :size="40" :src="item.companyLogo" class="company-logo">
                  <i class="el-icon-office-building"></i>
                </el-avatar>
                <div class="company-detail">
                  <div class="company-name">{{ item.companyName }}</div>
                  <div class="company-meta">
                    <span>{{ item.city }}</span>
                    <span class="divider">|</span>
                    <span>{{ item.workYears }}</span>
                    <span class="divider">|</span>
                    <span>{{ item.education }}</span>
                  </div>
                </div>
              </div>
              
              <div class="time-info">
                <span>投递时间：{{ formatDate(item.applyTime) }}</span>
              </div>
            </div>
            
            <!-- 操作按钮 -->
            <div class="card-actions">
              <el-button 
                v-if="item.status === 1" 
                type="text" 
                size="small"
                @click="cancelApplication(item)"
              >
                <i class="el-icon-circle-close"></i> 取消投递
              </el-button>
              <el-button 
                type="text" 
                size="small"
                @click="goToJobDetail(item.jobId)"
              >
                <i class="el-icon-view"></i> 查看岗位
              </el-button>
            </div>
          </div>
          
          <!-- 空状态 -->
          <el-empty v-if="applicationList.length === 0 && !loading" description="暂无投递记录">
            <el-button type="primary" @click="goToJobs">去浏览岗位</el-button>
          </el-empty>
        </div>
        
        <!-- 分页 -->
        <div class="pagination-container" v-if="total > 0">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="queryParams.page"
            :page-sizes="[10, 20, 30]"
            :page-size="queryParams.pageSize"
            layout="total, prev, pager, next"
            :total="total"
          ></el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { get, post } from '@/utils/request';

export default {
  name: 'JobSeekerApplications',
  data() {
    return {
      loading: false,
      applicationList: [],
      statistics: {},
      total: 0,
      activeTab: 'all',
      queryParams: {
        status: '',
        page: 1,
        pageSize: 10
      }
    };
  },
  mounted() {
    this.getStatistics();
    this.getApplicationList();
  },
  methods: {
    // 获取统计数据
    async getStatistics() {
      try {
        const res = await get('/applications/statistics');
        if (res.code === 200) {
          this.statistics = res.data;
        }
      } catch (error) {
        console.error('获取统计数据失败:', error);
      }
    },
    // 获取投递列表
    async getApplicationList() {
      this.loading = true;
      try {
        const res = await get('/applications/my', this.queryParams);
        if (res.code === 200) {
          this.applicationList = res.data.list;
          this.total = res.data.total;
        }
      } catch (error) {
        console.error('获取投递列表失败:', error);
      } finally {
        this.loading = false;
      }
    },
    // 选项卡切换
    handleTabChange(tab) {
      if (tab.name === 'all') {
        this.queryParams.status = '';
      } else if (tab.name === 'interviewing') {
        // 面试中包含待面试(2)和面试中(3)
        this.queryParams.status = '2,3';
      } else {
        this.queryParams.status = tab.name;
      }
      this.queryParams.page = 1;
      this.getApplicationList();
    },
    // 取消投递
    cancelApplication(item) {
      this.$confirm('确定要取消投递吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await post(`/applications/${item.id}/cancel`);
          if (res.code === 200) {
            this.$message.success('取消投递成功');
            this.getApplicationList();
            this.getStatistics();
          }
        } catch (error) {
          console.error('取消投递失败:', error);
        }
      }).catch(() => {});
    },
    // 跳转到岗位详情
    goToJobDetail(jobId) {
      this.$router.push(`/jobseeker/jobs/${jobId}`);
    },
    // 跳转到公司详情
    goToCompanyDetail(companyId) {
      this.$message.info('公司详情页开发中');
    },
    // 去浏览岗位
    goToJobs() {
      this.$router.push('/jobseeker/jobs');
    },
    // 分页大小变化
    handleSizeChange(val) {
      this.queryParams.pageSize = val;
      this.getApplicationList();
    },
    // 当前页变化
    handleCurrentChange(val) {
      this.queryParams.page = val;
      this.getApplicationList();
    },
    // 获取状态类型
    getStatusType(status) {
      const typeMap = {
        1: '',      // 待查看
        2: 'warning', // 待面试
        3: 'warning', // 面试中
        4: 'primary', // 待Offer
        5: 'danger',  // 已拒绝
        6: 'success'  // 已录用
      };
      return typeMap[status] || '';
    },
    // 格式化薪资
    formatSalary(min, max) {
      return `${(min / 1000).toFixed(0)}-${(max / 1000).toFixed(0)}`;
    },
    // 格式化日期
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
    }
  }
};
</script>

<style scoped>
.applications-page {
  min-height: 100%;
  background-color: #f5f7fa;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

/* 统计区域 */
.stats-section {
  margin-bottom: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.stat-item {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.all {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.stat-icon.interviewing {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #fff;
}

.stat-icon.success {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: #fff;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

/* 列表区域 */
.list-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.filter-tabs {
  padding: 0 20px;
  border-bottom: 1px solid #f0f0f0;
}

.filter-tabs ::v-deep .el-tabs__header {
  margin-bottom: 0;
}

.filter-tabs ::v-deep .el-tabs__item {
  font-size: 14px;
}

.application-list {
  padding: 20px;
}

.application-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  transition: all 0.3s;
}

.application-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.job-info {
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
}

.job-title {
  font-size: 18px;
  color: #333;
  margin: 0;
  font-weight: 500;
}

.job-salary {
  font-size: 18px;
  color: #fa5555;
  font-weight: bold;
}

.card-content {
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.company-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-bottom: 12px;
}

.company-logo {
  margin-right: 12px;
}

.company-detail {
  display: flex;
  flex-direction: column;
}

.company-name {
  font-size: 15px;
  color: #333;
  font-weight: 500;
  margin-bottom: 4px;
}

.company-meta {
  font-size: 13px;
  color: #999;
}

.company-meta .divider {
  margin: 0 8px;
}

.time-info {
  font-size: 13px;
  color: #999;
}

/* 操作按钮 */
.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #f5f5f5;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式 */
@media (max-width: 768px) {
  .container {
    padding: 15px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  
  .stat-item {
    padding: 15px;
    flex-direction: column;
    text-align: center;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  .filter-tabs {
    padding: 0 15px;
  }
  
  .application-list {
    padding: 15px;
  }
  
  .application-card {
    padding: 15px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 10px;
  }
  
  .job-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .pagination-container {
    padding: 15px;
  }
}
</style>
