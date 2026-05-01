<template>
  <div class="favorites-page">
    <div class="container">
      <!-- 顶部统计 -->
      <div class="stats-bar">
        <div class="stats-info">
          <span>共收藏 <em>{{ total }}</em> 个岗位</span>
          <span class="divider">|</span>
          <span>招聘中 <em>{{ activeCount }}</em> 个</span>
        </div>
        <div class="actions">
          <el-button 
            type="text" 
            v-if="selectedIds.length > 0"
            @click="batchUnfavorite"
          >
            <i class="el-icon-delete"></i> 批量取消 ({{ selectedIds.length }})
          </el-button>
          <el-button type="text" @click="refreshList">
            <i class="el-icon-refresh"></i> 刷新
          </el-button>
        </div>
      </div>
      
      <!-- 筛选区域 -->
      <div class="filter-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索岗位名称或公司名称"
          prefix-icon="el-icon-search"
          clearable
          style="width: 300px"
          @clear="handleSearch"
          @keyup.enter.native="handleSearch"
        >
          <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>
        <el-button type="primary" @click="clearSearch" style="margin-left: 10px">
          清除筛选
        </el-button>
      </div>
      
      <!-- 收藏列表 -->
      <div class="favorites-list" v-loading="loading">
        <div 
          v-for="item in favoritesList" 
          :key="item.id"
          class="favorite-card"
        >
          <!-- 复选框 -->
          <div class="checkbox-wrapper">
            <el-checkbox 
              v-model="selectedIds" 
              :label="item.id"
            ></el-checkbox>
          </div>
          
          <!-- 岗位信息 -->
          <div class="job-content" @click="goToJobDetail(item.jobId)">
            <div class="job-header">
              <h3 class="job-title">{{ item.jobTitle }}</h3>
              <span class="job-salary">{{ formatSalary(item.salaryMin, item.salaryMax) }}K/月</span>
            </div>
            
            <div class="job-info">
              <el-tag 
                :type="item.jobStatus === 1 ? 'success' : 'danger'" 
                size="small"
                effect="plain"
              >
                {{ item.jobStatus === 1 ? '招聘中' : '已下架' }}
              </el-tag>
              <span class="info-item">
                <i class="el-icon-location"></i> {{ item.city }}
              </span>
              <span class="info-item">
                <i class="el-icon-time"></i> {{ item.workYears }}
              </span>
              <span class="info-item">
                <i class="el-icon-reading"></i> {{ item.education }}
              </span>
            </div>
            
            <div class="company-info">
              <el-avatar :size="36" :src="item.companyLogo" class="company-logo">
                <i class="el-icon-office-building"></i>
              </el-avatar>
              <span class="company-name">{{ item.companyName }}</span>
            </div>
            
            <div class="favorite-time">
              收藏时间：{{ formatDate(item.favoriteTime) }}
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="card-actions">
            <el-button 
              type="danger" 
              size="small"
              text
              @click.stop="unfavorite(item)"
            >
              <i class="el-icon-star-off"></i> 取消收藏
            </el-button>
            <el-button 
              type="primary" 
              size="small"
              text
              @click.stop="goToJobDetail(item.jobId)"
            >
              <i class="el-icon-view"></i> 查看详情
            </el-button>
          </div>
        </div>
        
        <!-- 空状态 -->
        <el-empty v-if="favoritesList.length === 0 && !loading" description="暂无收藏的岗位">
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
          layout="total, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import { get, del } from '@/utils/request';

export default {
  name: 'JobSeekerFavorites',
  data() {
    return {
      loading: false,
      favoritesList: [],
      total: 0,
      activeCount: 0,
      searchKeyword: '',
      selectedIds: [],
      queryParams: {
        keyword: '',
        page: 1,
        pageSize: 10
      }
    };
  },
  mounted() {
    this.getFavoritesList();
  },
  methods: {
    // 获取收藏列表
    async getFavoritesList() {
      this.loading = true;
      try {
        const res = await get('/favorites/my', this.queryParams);
        if (res.code === 200) {
          this.favoritesList = res.data.list;
          this.total = res.data.total;
          // 计算招聘中的数量
          this.activeCount = this.favoritesList.filter(item => item.jobStatus === 1).length;
        }
      } catch (error) {
        console.error('获取收藏列表失败:', error);
      } finally {
        this.loading = false;
      }
    },
    // 搜索
    handleSearch() {
      this.queryParams.keyword = this.searchKeyword;
      this.queryParams.page = 1;
      this.getFavoritesList();
    },
    // 清除搜索
    clearSearch() {
      this.searchKeyword = '';
      this.queryParams.keyword = '';
      this.queryParams.page = 1;
      this.getFavoritesList();
    },
    // 刷新列表
    refreshList() {
      this.getFavoritesList();
    },
    // 取消收藏
    unfavorite(item) {
      this.$confirm('确定要取消收藏该岗位吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await del(`/favorites/${item.id}`);
          if (res.code === 200) {
            this.$message.success('已取消收藏');
            this.getFavoritesList();
          }
        } catch (error) {
          console.error('取消收藏失败:', error);
        }
      }).catch(() => {});
    },
    // 批量取消收藏
    batchUnfavorite() {
      if (this.selectedIds.length === 0) {
        this.$message.warning('请先选择要取消收藏的岗位');
        return;
      }
      
      this.$confirm(`确定要取消收藏选中的 ${this.selectedIds.length} 个岗位吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await del('/favorites/batch', { ids: this.selectedIds.join(',') });
          if (res.code === 200) {
            this.$message.success('批量取消收藏成功');
            this.selectedIds = [];
            this.getFavoritesList();
          }
        } catch (error) {
          console.error('批量取消收藏失败:', error);
        }
      }).catch(() => {});
    },
    // 跳转到岗位详情
    goToJobDetail(jobId) {
      this.$router.push(`/jobseeker/jobs/${jobId}`);
    },
    // 去浏览岗位
    goToJobs() {
      this.$router.push('/jobseeker/jobs');
    },
    // 分页大小变化
    handleSizeChange(val) {
      this.queryParams.pageSize = val;
      this.getFavoritesList();
    },
    // 当前页变化
    handleCurrentChange(val) {
      this.queryParams.page = val;
      this.getFavoritesList();
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
.favorites-page {
  min-height: 100%;
  background-color: #f5f7fa;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

/* 统计栏 */
.stats-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  padding: 15px 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.stats-info {
  font-size: 14px;
  color: #666;
}

.stats-info em {
  font-style: normal;
  color: #409eff;
  font-weight: bold;
  margin: 0 4px;
}

.stats-info .divider {
  margin: 0 15px;
  color: #ddd;
}

.actions {
  display: flex;
  gap: 10px;
}

/* 筛选区域 */
.filter-section {
  background: #fff;
  border-radius: 8px;
  padding: 15px 20px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

/* 收藏列表 */
.favorites-list {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.favorite-card {
  display: flex;
  align-items: flex-start;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  transition: all 0.3s;
}

.favorite-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.1);
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  padding-right: 15px;
}

.job-content {
  flex: 1;
  cursor: pointer;
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
  font-size: 18px;
  color: #fa5555;
  font-weight: bold;
}

.job-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.info-item i {
  margin-right: 4px;
  color: #999;
}

.company-info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.company-logo {
  margin-right: 10px;
}

.company-name {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.favorite-time {
  font-size: 13px;
  color: #999;
}

/* 操作按钮 */
.card-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-left: 15px;
  border-left: 1px solid #f0f0f0;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式 */
@media (max-width: 768px) {
  .container {
    padding: 15px;
  }
  
  .stats-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .favorites-list {
    padding: 15px;
  }
  
  .favorite-card {
    flex-direction: column;
    padding: 15px;
  }
  
  .checkbox-wrapper {
    padding-right: 0;
    padding-bottom: 10px;
  }
  
  .job-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .job-info {
    gap: 8px;
  }
  
  .card-actions {
    flex-direction: row;
    padding-left: 0;
    padding-top: 15px;
    border-left: none;
    border-top: 1px solid #f0f0f0;
    margin-top: 15px;
  }
}
</style>
