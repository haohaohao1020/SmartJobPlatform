<template>
  <div class="dashboard-page">
    <div class="container">
      <!-- 今日数据卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="card-icon new-users">
            <i class="el-icon-user"></i>
          </div>
          <div class="card-content">
            <div class="card-value">{{ statistics.today?.newUsers || 0 }}</div>
            <div class="card-label">今日新用户</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="card-icon new-companies">
            <i class="el-icon-office-building"></i>
          </div>
          <div class="card-content">
            <div class="card-value">{{ statistics.today?.newCompanies || 0 }}</div>
            <div class="card-label">今日新企业</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="card-icon new-jobs">
            <i class="el-icon-suitcase"></i>
          </div>
          <div class="card-content">
            <div class="card-value">{{ statistics.today?.newJobs || 0 }}</div>
            <div class="card-label">今日新岗位</div>
          </div>
        </div>
        
        <div class="stat-card">
          <div class="card-icon new-applications">
            <i class="el-icon-document"></i>
          </div>
          <div class="card-content">
            <div class="card-value">{{ statistics.today?.newApplications || 0 }}</div>
            <div class="card-label">今日投递数</div>
          </div>
        </div>
      </div>
      
      <!-- 待审核提示 -->
      <div class="pending-section">
        <el-alert
          title="待审核事项"
          type="warning"
          show-icon
          :closable="false"
        >
          <template slot="default">
            <div class="pending-items">
              <span class="pending-item" @click="goTo('companies')">
                <i class="el-icon-office-building"></i>
                企业审核：<em>{{ statistics.pendingAudit?.companies || 0 }}</em> 家待审核
              </span>
              <span class="pending-item" @click="goTo('jobs')">
                <i class="el-icon-suitcase"></i>
                岗位审核：<em>{{ statistics.pendingAudit?.jobs || 0 }}</em> 个待审核
              </span>
              <span class="pending-item" @click="goTo('users')">
                <i class="el-icon-user"></i>
                用户审核：<em>{{ statistics.pendingAudit?.users || 0 }}</em> 人待审核
              </span>
            </div>
          </template>
        </el-alert>
      </div>
      
      <!-- 总览数据 -->
      <div class="overview-section">
        <div class="section-title">
          <h3><i class="el-icon-data-analysis"></i> 平台总览</h3>
        </div>
        
        <div class="overview-cards">
          <div class="overview-card">
            <div class="overview-icon total-users">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ statistics.total?.users || 0 }}</div>
              <div class="overview-label">总用户数</div>
            </div>
          </div>
          
          <div class="overview-card">
            <div class="overview-icon total-companies">
              <i class="el-icon-office-building"></i>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ statistics.total?.companies || 0 }}</div>
              <div class="overview-label">合作企业数</div>
            </div>
          </div>
          
          <div class="overview-card">
            <div class="overview-icon total-jobs">
              <i class="el-icon-suitcase"></i>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ statistics.total?.jobs || 0 }}</div>
              <div class="overview-label">岗位总数</div>
            </div>
          </div>
          
          <div class="overview-card">
            <div class="overview-icon total-applications">
              <i class="el-icon-document"></i>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ statistics.total?.applications || 0 }}</div>
              <div class="overview-label">投递总数</div>
            </div>
          </div>
          
          <div class="overview-card">
            <div class="overview-icon total-interviews">
              <i class="el-icon-user"></i>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ statistics.total?.interviews || 0 }}</div>
              <div class="overview-label">面试总数</div>
            </div>
          </div>
          
          <div class="overview-card">
            <div class="overview-icon total-hires">
              <i class="el-icon-success"></i>
            </div>
            <div class="overview-info">
              <div class="overview-value">{{ statistics.total?.hires || 0 }}</div>
              <div class="overview-label">成功入职</div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 图表区域 -->
      <div class="charts-section">
        <div class="chart-card">
          <div class="section-title">
            <h3><i class="el-icon-trend-charts"></i> 用户增长趋势</h3>
          </div>
          <div class="chart-container" ref="userChart">
            <div class="chart-placeholder">
              <div class="chart-bars">
                <div 
                  v-for="(item, index) in userGrowthData" 
                  :key="index"
                  class="bar-group"
                >
                  <div class="bars">
                    <div class="bar jobseeker" :style="{ height: getBarHeight(item.jobseeker, 100) + 'px' }">
                      <span class="bar-value">{{ item.jobseeker }}</span>
                    </div>
                    <div class="bar hr" :style="{ height: getBarHeight(item.hr, 100) + 'px' }">
                      <span class="bar-value">{{ item.hr }}</span>
                    </div>
                  </div>
                  <div class="bar-label">{{ item.date }}</div>
                </div>
              </div>
              <div class="chart-legend">
                <span class="legend-item">
                  <span class="legend-color jobseeker"></span>
                  求职者
                </span>
                <span class="legend-item">
                  <span class="legend-color hr"></span>
                  HR
                </span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="chart-card">
          <div class="section-title">
            <h3><i class="el-icon-pie-chart"></i> 行业分布</h3>
          </div>
          <div class="chart-container">
            <div class="pie-placeholder">
              <div class="pie-chart">
                <div 
                  v-for="(item, index) in industryData" 
                  :key="index"
                  class="pie-segment"
                  :style="{ 
                    background: getPieColor(index),
                    transform: getPieTransform(index)
                  }"
                ></div>
              </div>
              <div class="pie-legend">
                <div 
                  v-for="(item, index) in industryData" 
                  :key="index"
                  class="legend-item"
                >
                  <span class="legend-color" :style="{ background: getPieColor(index) }"></span>
                  <span class="legend-name">{{ item.name }}</span>
                  <span class="legend-value">{{ item.value }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 热门排行 -->
      <div class="ranking-section">
        <div class="ranking-card">
          <div class="section-title">
            <h3><i class="el-icon-trophy"></i> 热门岗位 TOP10</h3>
          </div>
          <div class="ranking-list">
            <div 
              v-for="(item, index) in hotJobs" 
              :key="index"
              class="ranking-item"
            >
              <span class="rank-number" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
              <div class="ranking-info">
                <span class="job-title">{{ item.title }}</span>
                <span class="company-name">{{ item.companyName }}</span>
              </div>
              <div class="ranking-stats">
                <span><i class="el-icon-document"></i> {{ item.applyCount }} 投递</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="ranking-card">
          <div class="section-title">
            <h3><i class="el-icon-star-on"></i> 热门企业 TOP10</h3>
          </div>
          <div class="ranking-list">
            <div 
              v-for="(item, index) in hotCompanies" 
              :key="index"
              class="ranking-item"
            >
              <span class="rank-number" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
              <div class="ranking-info">
                <el-avatar :size="32" :src="item.logo" class="company-avatar">
                  <i class="el-icon-office-building"></i>
                </el-avatar>
                <div class="company-info">
                  <span class="company-name">{{ item.name }}</span>
                  <span class="job-count">{{ item.jobCount }} 个岗位</span>
                </div>
              </div>
              <div class="ranking-stats">
                <span><i class="el-icon-view"></i> {{ item.viewCount }} 浏览</span>
              </div>
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
  name: 'AdminDashboard',
  data() {
    return {
      statistics: {
        today: {},
        total: {},
        pendingAudit: {},
        userGrowth: {},
        jobTrend: {},
        industryDistribution: [],
        hotJobs: [],
        hotCompanies: []
      }
    };
  },
  computed: {
    userGrowthData() {
      const dates = this.statistics.userGrowth?.dates || [];
      const jobSeekers = this.statistics.userGrowth?.jobSeekers || [];
      const hrs = this.statistics.userGrowth?.hrs || [];
      
      return dates.map((date, index) => ({
        date,
        jobseeker: jobSeekers[index] || 0,
        hr: hrs[index] || 0
      }));
    },
    industryData() {
      return this.statistics.industryDistribution || [];
    },
    hotJobs() {
      return this.statistics.hotJobs || [];
    },
    hotCompanies() {
      return this.statistics.hotCompanies || [];
    }
  },
  mounted() {
    this.getStatistics();
  },
  methods: {
    // 获取统计数据
    async getStatistics() {
      try {
        const res = await get('/admin/statistics');
        if (res.code === 200) {
          this.statistics = res.data;
        }
      } catch (error) {
        console.error('获取统计数据失败:', error);
      }
    },
    // 跳转到对应页面
    goTo(type) {
      const pathMap = {
        companies: '/admin/companies',
        jobs: '/admin/jobs',
        users: '/admin/users'
      };
      this.$router.push(pathMap[type] || '/admin/dashboard');
    },
    // 获取柱状图高度
    getBarHeight(value, max) {
      if (max === 0) return 0;
      return Math.max((value / max) * 150, 5);
    },
    // 获取饼图颜色
    getPieColor(index) {
      const colors = [
        '#409eff',
        '#67c23a',
        '#e6a23c',
        '#f56c6c',
        '#909399',
        '#00d4ff',
        '#ff6b6b',
        '#845ef7'
      ];
      return colors[index % colors.length];
    },
    // 获取饼图变换
    getPieTransform(index) {
      if (this.industryData.length === 0) return '';
      const total = this.industryData.reduce((sum, item) => sum + item.value, 0);
      if (total === 0) return '';
      
      let startAngle = 0;
      for (let i = 0; i < index; i++) {
        startAngle += (this.industryData[i].value / total) * 360;
      }
      
      const angle = (this.industryData[index].value / total) * 360;
      return `rotate(${startAngle}deg) skewY(${90 - angle}deg)`;
    }
  }
};
</script>

<style scoped>
.dashboard-page {
  min-height: 100%;
}

.container {
  max-width: 100%;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.1);
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.card-icon.new-users {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-icon.new-companies {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.card-icon.new-jobs {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.card-icon.new-applications {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.card-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

/* 待审核区域 */
.pending-section {
  margin-bottom: 20px;
}

.pending-items {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: 10px;
}

.pending-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #e6a23c;
  cursor: pointer;
  transition: color 0.3s;
}

.pending-item:hover {
  color: #c2952d;
}

.pending-item i {
  font-size: 16px;
}

.pending-item em {
  font-style: normal;
  font-weight: bold;
  color: #f56c6c;
}

/* 总览区域 */
.overview-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.section-title {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.section-title h3 {
  font-size: 16px;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
}

.section-title h3 i {
  margin-right: 8px;
  color: #409eff;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 15px;
}

.overview-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s;
}

.overview-card:hover {
  background: #f0f7ff;
}

.overview-icon {
  width: 45px;
  height: 45px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
}

.overview-icon.total-users {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.overview-icon.total-companies {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.overview-icon.total-jobs {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.overview-icon.total-applications {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.overview-icon.total-interviews {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.overview-icon.total-hires {
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
}

.overview-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.overview-label {
  font-size: 12px;
  color: #999;
  margin-top: 3px;
}

/* 图表区域 */
.charts-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 柱状图占位 */
.chart-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 20px 0;
}

.chart-bars {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  flex: 1;
  margin-bottom: 15px;
}

.bar-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.bars {
  display: flex;
  gap: 8px;
  align-items: flex-end;
  height: 180px;
}

.bar {
  width: 25px;
  border-radius: 4px 4px 0 0;
  position: relative;
  transition: all 0.3s;
}

.bar:hover {
  opacity: 0.8;
}

.bar.jobseeker {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
}

.bar.hr {
  background: linear-gradient(180deg, #11998e 0%, #38ef7d 100%);
}

.bar-value {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  color: #666;
}

.bar-label {
  font-size: 12px;
  color: #999;
}

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 30px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 3px;
}

.legend-color.jobseeker {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.legend-color.hr {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

/* 饼图占位 */
.pie-placeholder {
  display: flex;
  align-items: center;
  gap: 30px;
  width: 100%;
  height: 100%;
}

.pie-chart {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  position: relative;
  overflow: hidden;
}

.pie-segment {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transform-origin: 100% 100%;
}

.pie-legend {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.pie-legend .legend-item {
  justify-content: space-between;
  padding: 8px 12px;
  background: #f9f9f9;
  border-radius: 6px;
}

.legend-name {
  flex: 1;
  margin-left: 8px;
}

.legend-value {
  font-weight: bold;
  color: #333;
}

/* 排行区域 */
.ranking-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.ranking-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s;
}

.ranking-item:hover {
  background: #f0f7ff;
}

.rank-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
  flex-shrink: 0;
}

.rank-number.rank-1 {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
}

.rank-number.rank-2 {
  background: linear-gradient(135deg, #c0c0c0 0%, #e8e8e8 100%);
}

.rank-number.rank-3 {
  background: linear-gradient(135deg, #cd7f32 0%, #daa520 100%);
}

.rank-number:not(.rank-1):not(.rank-2):not(.rank-3) {
  background: #999;
}

.ranking-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
}

.ranking-info .job-title {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.ranking-info .company-name {
  font-size: 12px;
  color: #999;
}

.company-avatar {
  flex-shrink: 0;
}

.company-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.company-info .company-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.company-info .job-count {
  font-size: 12px;
  color: #999;
}

.ranking-stats {
  font-size: 13px;
  color: #666;
}

.ranking-stats i {
  margin-right: 3px;
  color: #999;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .overview-cards {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .charts-section,
  .ranking-section {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }
  
  .stat-card {
    padding: 15px;
    flex-direction: column;
    text-align: center;
    gap: 10px;
  }
  
  .card-value {
    font-size: 22px;
  }
  
  .pending-items {
    flex-direction: column;
    gap: 10px;
  }
  
  .overview-cards {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  
  .overview-card {
    flex-direction: column;
    text-align: center;
    padding: 12px;
  }
  
  .chart-placeholder {
    padding: 10px 0;
  }
  
  .bar {
    width: 18px;
  }
  
  .pie-placeholder {
    flex-direction: column;
    gap: 20px;
  }
  
  .pie-chart {
    width: 150px;
    height: 150px;
  }
}
</style>
