<template>
  <div class="favorites-page">
    <div class="page-header">
      <span class="title">我的收藏</span>
      <span class="count" v-if="total > 0">共 {{ total }} 个</span>
    </div>
    
    <div class="sort-bar">
      <div 
        class="sort-item"
        :class="{ active: sortBy === 'time' }"
        @click="setSort('time')"
      >
        按收藏时间
      </div>
      <div 
        class="sort-item"
        :class="{ active: sortBy === 'salary' }"
        @click="setSort('salary')"
      >
        薪资最高
      </div>
    </div>
    
    <div class="favorite-list">
      <div 
        v-for="item in favoriteList" 
        :key="item.id"
        class="favorite-card"
      >
        <div class="card-main" @click="goToDetail(item.jobId)">
          <div class="job-header">
            <span class="job-title">{{ item.jobTitle }}</span>
            <span class="salary">{{ formatSalary(item.salaryMin, item.salaryMax) }}K/月</span>
          </div>
          
          <div class="job-tags">
            <span class="tag-item">{{ item.city }}</span>
            <span class="tag-item">{{ item.workYears }}</span>
            <span class="tag-item">{{ item.education }}</span>
          </div>
          
          <div class="company-info">
            <el-avatar :size="28" :src="item.companyLogo">
              <i class="el-icon-office-building"></i>
            </el-avatar>
            <div class="company-detail">
              <span class="company-name">{{ item.companyName }}</span>
              <span class="company-meta">{{ item.industry }}</span>
            </div>
          </div>
          
          <div class="card-footer">
            <span class="fav-time">收藏于 {{ formatTime(item.favTime) }}</span>
            <span class="view-count">{{ item.viewCount }}浏览</span>
          </div>
        </div>
        
        <div class="card-action">
          <div class="action-btn" @click.stop="applyJob(item)">
            <i class="el-icon-s-promotion"></i>
            <span>立即投递</span>
          </div>
          <div class="action-btn danger" @click.stop="removeFavorite(item)">
            <i class="el-icon-star-on"></i>
            <span>取消收藏</span>
          </div>
        </div>
      </div>
      
      <div class="load-more" v-if="hasMore" @click="loadMore">
        <el-button type="text" :loading="loading">
          {{ loading ? '加载中...' : '点击加载更多' }}
        </el-button>
      </div>
      
      <div class="empty" v-if="!loading && favoriteList.length === 0">
        <div class="empty-icon">
          <i class="el-icon-star-off"></i>
        </div>
        <p>暂无收藏的岗位</p>
        <p class="sub-text">快去发现心仪的岗位吧</p>
        <el-button type="primary" size="small" @click="goToJobs">去找岗位</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import { get, post } from '@/utils/request';
import { useUserStore } from '@/uiapp/stores/user';

export default {
  name: 'UIFavorites',
  data() {
    return {
      loading: false,
      sortBy: 'time',
      page: 1,
      pageSize: 10,
      hasMore: true,
      total: 0,
      favoriteList: []
    };
  },
  computed: {
    userStore() {
      return useUserStore();
    }
  },
  mounted() {
    this.getFavoriteList();
  },
  activated() {
    if (this.userStore.isLoggedIn) {
      this.getFavoriteList();
    }
  },
  methods: {
    async getFavoriteList(refresh = true) {
      if (!this.userStore.isLoggedIn) {
        this.favoriteList = [];
        return;
      }
      
      if (refresh) {
        this.page = 1;
        this.favoriteList = [];
        this.hasMore = true;
      }
      
      if (this.loading || !this.hasMore) return;
      
      this.loading = true;
      try {
        const params = {
          page: this.page,
          pageSize: this.pageSize,
          sortBy: this.sortBy
        };
        
        const res = await get('/favorites/list', params);
        if (res.code === 200) {
          const list = res.data.list || [];
          this.favoriteList = refresh ? list : [...this.favoriteList, ...list];
          this.total = res.data.total || 0;
          this.hasMore = list.length >= this.pageSize;
          if (list.length >= this.pageSize) {
            this.page++;
          }
        }
      } catch (error) {
        console.error('获取收藏列表失败:', error);
      } finally {
        this.loading = false;
      }
    },
    loadMore() {
      this.getFavoriteList(false);
    },
    setSort(sort) {
      if (this.sortBy !== sort) {
        this.sortBy = sort;
        this.getFavoriteList(true);
      }
    },
    goToDetail(jobId) {
      this.$router.push(`/jobs/${jobId}`);
    },
    goToJobs() {
      this.$router.push('/jobs');
    },
    async applyJob(item) {
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
            jobId: item.jobId
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
    async removeFavorite(item) {
      this.$confirm('确定要取消收藏该岗位吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await post('/favorites/remove', {
            jobId: item.jobId
          });
          
          if (res.code === 200) {
            this.$message.success('已取消收藏');
            this.getFavoriteList(true);
          }
        } catch (error) {
          this.$message.error('操作失败');
        }
      }).catch(() => {});
    },
    formatSalary(min, max) {
      return `${(min / 1000).toFixed(0)}-${(max / 1000).toFixed(0)}`;
    },
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      const now = new Date();
      const diff = now - date;
      const day = Math.floor(diff / (24 * 60 * 60 * 1000));
      
      if (day === 0) {
        const hour = Math.floor(diff / (60 * 60 * 1000));
        if (hour === 0) {
          return '刚刚';
        }
        return `${hour}小时前`;
      } else if (day === 1) {
        return '昨天';
      } else if (day <= 7) {
        return `${day}天前`;
      } else {
        return `${date.getMonth() + 1}月${date.getDate()}日`;
      }
    }
  }
};
</script>

<style scoped>
.favorites-page {
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
  position: relative;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.page-header .title {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

.page-header .count {
  position: absolute;
  right: 15px;
  font-size: 13px;
  color: #909399;
}

/* 排序栏 */
.sort-bar {
  display: flex;
  background: #fff;
  padding: 0 15px;
  border-bottom: 1px solid #f0f0f0;
}

.sort-item {
  padding: 12px 20px;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  position: relative;
}

.sort-item.active {
  color: #409eff;
  font-weight: 500;
}

.sort-item.active::after {
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

/* 收藏列表 */
.favorite-list {
  padding: 10px;
}

.favorite-card {
  background: #fff;
  border-radius: 12px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.card-main {
  padding: 15px;
  cursor: pointer;
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.job-title {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
  flex: 1;
  margin-right: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.salary {
  font-size: 15px;
  color: #fa5555;
  font-weight: bold;
}

.job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.tag-item {
  padding: 3px 10px;
  background: #f5f7fa;
  color: #909399;
  font-size: 12px;
  border-radius: 4px;
}

.company-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.company-detail {
  display: flex;
  flex-direction: column;
  gap: 3px;
  flex: 1;
}

.company-name {
  font-size: 14px;
  color: #303133;
}

.company-meta {
  font-size: 12px;
  color: #909399;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #c0c4cc;
  padding-top: 10px;
  border-top: 1px solid #f5f5f5;
}

/* 操作栏 */
.card-action {
  display: flex;
  border-top: 1px solid #f5f5f5;
}

.action-btn {
  flex: 1;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  font-size: 14px;
  color: #409eff;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:active {
  background: #f5f7fa;
}

.action-btn.danger {
  color: #f56c6c;
  border-left: 1px solid #f5f5f5;
}

.action-btn i {
  font-size: 16px;
}

/* 加载更多和空状态 */
.load-more {
  text-align: center;
  padding: 20px;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding-top: 80px;
}

.empty-icon {
  width: 100px;
  height: 100px;
  background: #f5f7fa;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon i {
  font-size: 50px;
  color: #c0c4cc;
}

.empty p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.empty .sub-text {
  font-size: 13px;
  color: #c0c4cc;
}
</style>
