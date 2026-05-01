<template>
  <div class="jobs-page">
    <div class="container">
      <!-- 搜索区域 -->
      <div class="search-section">
        <div class="search-box">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索职位、公司名称"
            prefix-icon="el-icon-search"
            class="search-input"
            size="large"
            @keyup.enter.native="handleSearch"
          >
            <el-button
              slot="append"
              type="primary"
              :loading="loading"
              @click="handleSearch"
            >
              搜索
            </el-button>
          </el-input>
        </div>
        
        <!-- 筛选条件 -->
        <div class="filter-section">
          <div class="filter-item">
            <span class="filter-label">城市：</span>
            <el-select 
              v-model="searchForm.city" 
              placeholder="全部城市" 
              clearable
              size="small"
              @change="handleFilter"
            >
              <el-option label="北京" value="北京"></el-option>
              <el-option label="上海" value="上海"></el-option>
              <el-option label="广州" value="广州"></el-option>
              <el-option label="深圳" value="深圳"></el-option>
              <el-option label="杭州" value="杭州"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <span class="filter-label">工作年限：</span>
            <el-select 
              v-model="searchForm.workYears" 
              placeholder="不限" 
              clearable
              size="small"
              @change="handleFilter"
            >
              <el-option label="应届毕业生" value="应届毕业生"></el-option>
              <el-option label="1-3年" value="1-3年"></el-option>
              <el-option label="3-5年" value="3-5年"></el-option>
              <el-option label="5-10年" value="5-10年"></el-option>
              <el-option label="10年以上" value="10年以上"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <span class="filter-label">学历：</span>
            <el-select 
              v-model="searchForm.education" 
              placeholder="不限" 
              clearable
              size="small"
              @change="handleFilter"
            >
              <el-option label="大专" value="大专"></el-option>
              <el-option label="本科" value="本科"></el-option>
              <el-option label="硕士" value="硕士"></el-option>
              <el-option label="博士" value="博士"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item">
            <span class="filter-label">薪资：</span>
            <el-select 
              v-model="searchForm.salary" 
              placeholder="不限" 
              clearable
              size="small"
              @change="handleFilter"
            >
              <el-option label="5k以下" value="0-5000"></el-option>
              <el-option label="5k-10k" value="5000-10000"></el-option>
              <el-option label="10k-15k" value="10000-15000"></el-option>
              <el-option label="15k-20k" value="15000-20000"></el-option>
              <el-option label="20k-30k" value="20000-30000"></el-option>
              <el-option label="30k以上" value="30000"></el-option>
            </el-select>
          </div>
          
          <div class="filter-item sort-item">
            <span class="filter-label">排序：</span>
            <el-radio-group v-model="searchForm.sortBy" size="small" @change="handleFilter">
              <el-radio-button label="publishTime">最新</el-radio-button>
              <el-radio-button label="salary">薪资</el-radio-button>
              <el-radio-button label="viewCount">热度</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </div>
      
      <!-- 岗位列表 -->
      <div class="job-list-section">
        <div class="list-header">
          <span class="list-title">共 <em>{{ total }}</em> 个职位</span>
          <el-button type="text" @click="resetFilter">
            <i class="el-icon-refresh"></i> 重置筛选
          </el-button>
        </div>
        
        <div class="job-list" v-loading="loading">
          <div 
            v-for="job in jobList" 
            :key="job.id"
            class="job-card"
            @click="goToDetail(job.id)"
          >
            <div class="job-main">
              <div class="job-header">
                <h3 class="job-title">{{ job.title }}</h3>
                <span class="job-salary">{{ formatSalary(job.salaryMin, job.salaryMax) }}K/月</span>
              </div>
              <div class="job-info">
                <span class="info-item">
                  <i class="el-icon-location"></i> {{ job.city }}
                </span>
                <span class="info-item">
                  <i class="el-icon-time"></i> {{ job.workYears }}
                </span>
                <span class="info-item">
                  <i class="el-icon-reading"></i> {{ job.education }}
                </span>
                <span class="info-item">
                  <i class="el-icon-bank-card"></i> {{ job.jobType }}
                </span>
              </div>
            </div>
            
            <div class="job-company">
              <div class="company-info">
                <el-avatar :size="48" :src="job.companyLogo" class="company-logo">
                  <i class="el-icon-office-building"></i>
                </el-avatar>
                <div class="company-detail">
                  <div class="company-name">{{ job.companyName }}</div>
                  <div class="company-meta">
                    <span>{{ job.industry }}</span>
                    <span class="divider">|</span>
                    <span>{{ job.companySize }}</span>
                  </div>
                </div>
              </div>
              <div class="job-tags">
                <el-tag v-for="(tag, index) in job.welfare" :key="index" size="mini" effect="plain">
                  {{ tag }}
                </el-tag>
              </div>
            </div>
            
            <div class="job-footer">
              <span class="publish-time">发布于 {{ formatTime(job.publishTime) }}</span>
              <div class="job-stats">
                <span><i class="el-icon-view"></i> {{ job.viewCount }} 浏览</span>
                <span><i class="el-icon-document"></i> {{ job.applyCount }} 投递</span>
              </div>
            </div>
          </div>
          
          <!-- 空状态 -->
          <el-empty v-if="jobList.length === 0 && !loading" description="暂无符合条件的岗位"></el-empty>
        </div>
        
        <!-- 分页 -->
        <div class="pagination-container" v-if="total > 0">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="searchForm.page"
            :page-sizes="[10, 20, 30, 50]"
            :page-size="searchForm.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
          ></el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { get } from '@/utils/request';

export default {
  name: 'JobSeekerJobs',
  data() {
    return {
      loading: false,
      jobList: [],
      total: 0,
      searchForm: {
        keyword: '',
        city: '',
        workYears: '',
        education: '',
        salary: '',
        sortBy: 'publishTime',
        sortOrder: 'desc',
        page: 1,
        pageSize: 10
      }
    };
  },
  mounted() {
    this.getJobList();
  },
  methods: {
    // 获取岗位列表
    async getJobList() {
      this.loading = true;
      try {
        const res = await get('/jobs', this.searchForm);
        if (res.code === 200) {
          this.jobList = res.data.list;
          this.total = res.data.total;
        }
      } catch (error) {
        console.error('获取岗位列表失败:', error);
      } finally {
        this.loading = false;
      }
    },
    // 搜索
    handleSearch() {
      this.searchForm.page = 1;
      this.getJobList();
    },
    // 筛选
    handleFilter() {
      this.searchForm.page = 1;
      this.getJobList();
    },
    // 重置筛选
    resetFilter() {
      this.searchForm = {
        keyword: '',
        city: '',
        workYears: '',
        education: '',
        salary: '',
        sortBy: 'publishTime',
        sortOrder: 'desc',
        page: 1,
        pageSize: 10
      };
      this.getJobList();
    },
    // 分页大小变化
    handleSizeChange(val) {
      this.searchForm.pageSize = val;
      this.getJobList();
    },
    // 当前页变化
    handleCurrentChange(val) {
      this.searchForm.page = val;
      this.getJobList();
    },
    // 跳转到详情
    goToDetail(id) {
      this.$router.push(`/jobseeker/jobs/${id}`);
    },
    // 格式化薪资
    formatSalary(min, max) {
      return `${(min / 1000).toFixed(0)}-${(max / 1000).toFixed(0)}`;
    },
    // 格式化时间
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      const now = new Date();
      const diff = now - date;
      
      // 小于24小时
      if (diff < 24 * 60 * 60 * 1000) {
        const hours = Math.floor(diff / (60 * 60 * 1000));
        return hours <= 0 ? '刚刚' : `${hours}小时前`;
      }
      // 小于30天
      if (diff < 30 * 24 * 60 * 60 * 1000) {
        const days = Math.floor(diff / (24 * 60 * 60 * 1000));
        return `${days}天前`;
      }
      
      return `${date.getMonth() + 1}-${date.getDate()}`;
    }
  }
};
</script>

<style scoped>
.jobs-page {
  min-height: 100%;
  background-color: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 搜索区域 */
.search-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.search-box {
  margin-bottom: 20px;
}

.search-input {
  width: 100%;
  max-width: 600px;
}

.filter-section {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
}

.filter-label {
  font-size: 14px;
  color: #666;
  margin-right: 8px;
}

.sort-item {
  margin-left: auto;
}

/* 岗位列表 */
.job-list-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.list-title {
  font-size: 16px;
  color: #333;
}

.list-title em {
  font-style: normal;
  color: #409eff;
  font-weight: bold;
}

.job-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.job-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.job-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.2);
}

.job-main {
  margin-bottom: 15px;
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.job-title {
  font-size: 18px;
  color: #333;
  margin: 0;
  font-weight: 500;
}

.job-salary {
  font-size: 20px;
  color: #fa5555;
  font-weight: bold;
}

.job-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.info-item i {
  margin-right: 5px;
  color: #999;
}

.job-company {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.company-info {
  display: flex;
  align-items: center;
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

.job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.job-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #f5f5f5;
  font-size: 13px;
  color: #999;
}

.job-stats span {
  margin-left: 15px;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式 */
@media (max-width: 768px) {
  .container {
    padding: 15px;
  }
  
  .search-section {
    padding: 15px;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .sort-item {
    margin-left: 0;
  }
  
  .job-card {
    padding: 15px;
  }
  
  .job-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .job-company {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .job-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .job-stats {
    display: flex;
    gap: 15px;
  }
  
  .job-stats span {
    margin-left: 0;
  }
}
</style>
