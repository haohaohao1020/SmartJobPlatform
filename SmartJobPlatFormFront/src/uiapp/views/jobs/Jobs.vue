<template>
  <div class="jobs-page">
    <div class="search-header">
      <div class="search-bar" @click="showSearch = true">
        <i class="el-icon-search"></i>
        <span>{{ searchKeyword || '搜索岗位名称、公司名称' }}</span>
      </div>
      <div class="filter-btn" @click="showFilter = true">
        <i class="el-icon-filter"></i>
      </div>
    </div>
    
    <div class="filter-tags" v-if="activeFilters.length > 0">
      <span 
        v-for="filter in activeFilters" 
        :key="filter.key"
        class="tag"
      >
        {{ filter.value }}
        <i class="el-icon-close" @click="clearFilter(filter.key)"></i>
      </span>
      <span class="clear-all" @click="clearAllFilters">清除全部</span>
    </div>
    
    <div class="sort-bar">
      <div 
        class="sort-item"
        :class="{ active: sortBy === 'default' }"
        @click="setSort('default')"
      >
        综合排序
      </div>
      <div 
        class="sort-item"
        :class="{ active: sortBy === 'salary' }"
        @click="setSort('salary')"
      >
        薪资最高
        <i class="el-icon-arrow-down" v-if="sortBy === 'salary'"></i>
      </div>
      <div 
        class="sort-item"
        :class="{ active: sortBy === 'newest' }"
        @click="setSort('newest')"
      >
        最新发布
      </div>
    </div>
    
    <div class="job-list">
      <div 
        v-for="job in jobList" 
        :key="job.id"
        class="job-card"
        @click="goToDetail(job.id)"
      >
        <div class="job-header">
          <span class="job-title">{{ job.title }}</span>
          <span class="salary">{{ formatSalary(job.salaryMin, job.salaryMax) }}K/月</span>
        </div>
        
        <div class="job-tags">
          <span class="tag-item">{{ job.city }}</span>
          <span class="tag-item">{{ job.workYears }}</span>
          <span class="tag-item">{{ job.education }}</span>
        </div>
        
        <div class="job-company">
          <el-avatar :size="36" :src="job.companyLogo">
            <i class="el-icon-office-building"></i>
          </el-avatar>
          <div class="company-info">
            <span class="company-name">{{ job.companyName }}</span>
            <span class="company-meta">{{ job.industry }} · {{ job.companySize }}</span>
          </div>
        </div>
        
        <div class="job-footer">
          <span class="update-time">{{ formatTime(job.publishTime) }}</span>
          <span class="view-count">{{ job.viewCount }}次浏览</span>
        </div>
      </div>
      
      <div class="load-more" v-if="hasMore" @click="loadMore">
        <el-button type="text" :loading="loading">
          {{ loading ? '加载中...' : '点击加载更多' }}
        </el-button>
      </div>
      
      <div class="no-more" v-else-if="jobList.length > 0">
        已加载全部岗位
      </div>
      
      <div class="empty" v-if="!loading && jobList.length === 0">
        <i class="el-icon-document"></i>
        <p>暂无符合条件的岗位</p>
      </div>
    </div>
    
    <div class="search-popup" v-if="showSearch">
      <div class="search-header-popup">
        <div class="search-box">
          <el-input
            ref="searchInput"
            v-model="searchInput"
            placeholder="搜索岗位名称、公司名称"
            prefix-icon="el-icon-search"
            clearable
            @clear="doSearch"
            @keyup.enter.native="doSearch"
          ></el-input>
        </div>
        <span class="cancel-btn" @click="closeSearch">取消</span>
      </div>
      
      <div class="search-suggestions" v-if="hotKeywords.length > 0">
        <div class="suggestion-title">热门搜索</div>
        <div class="keyword-list">
          <span 
            v-for="keyword in hotKeywords" 
            :key="keyword"
            class="keyword"
            @click="selectKeyword(keyword)"
          >
            {{ keyword }}
          </span>
        </div>
      </div>
    </div>
    
    <div class="filter-popup" v-if="showFilter">
      <div class="filter-header">
        <span class="title">筛选条件</span>
        <span class="reset-btn" @click="resetFilter">重置</span>
      </div>
      
      <div class="filter-content">
        <div class="filter-group">
          <div class="group-title">工作地点</div>
          <div class="group-options">
            <span 
              v-for="city in cities" 
              :key="city"
              class="option"
              :class="{ active: filters.city === city }"
              @click="filters.city = filters.city === city ? '' : city"
            >
              {{ city }}
            </span>
          </div>
        </div>
        
        <div class="filter-group">
          <div class="group-title">薪资范围</div>
          <div class="group-options">
            <span 
              v-for="salary in salaryRanges" 
              :key="salary.value"
              class="option"
              :class="{ active: filters.salaryRange === salary.value }"
              @click="filters.salaryRange = filters.salaryRange === salary.value ? '' : salary.value"
            >
              {{ salary.label }}
            </span>
          </div>
        </div>
        
        <div class="filter-group">
          <div class="group-title">工作经验</div>
          <div class="group-options">
            <span 
              v-for="exp in workExps" 
              :key="exp.value"
              class="option"
              :class="{ active: filters.workYears === exp.value }"
              @click="filters.workYears = filters.workYears === exp.value ? '' : exp.value"
            >
              {{ exp.label }}
            </span>
          </div>
        </div>
        
        <div class="filter-group">
          <div class="group-title">学历要求</div>
          <div class="group-options">
            <span 
              v-for="edu in educations" 
              :key="edu.value"
              class="option"
              :class="{ active: filters.education === edu.value }"
              @click="filters.education = filters.education === edu.value ? '' : edu.value"
            >
              {{ edu.label }}
            </span>
          </div>
        </div>
      </div>
      
      <div class="filter-footer">
        <div class="btn cancel" @click="showFilter = false">取消</div>
        <div class="btn confirm" @click="applyFilter">确定</div>
      </div>
    </div>
    
    <div class="popup-mask" v-if="showSearch || showFilter" @click="closeAllPopups"></div>
  </div>
</template>

<script>
import { get } from '@/utils/request';

export default {
  name: 'UIJobs',
  data() {
    return {
      loading: false,
      jobList: [],
      page: 1,
      pageSize: 10,
      hasMore: true,
      sortBy: 'default',
      
      searchKeyword: '',
      searchInput: '',
      showSearch: false,
      showFilter: false,
      
      filters: {
        city: '',
        salaryRange: '',
        workYears: '',
        education: ''
      },
      
      hotKeywords: ['Java', '前端开发', '产品经理', '运营', '销售', '会计'],
      cities: ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '西安'],
      salaryRanges: [
        { label: '3K以下', value: '1' },
        { label: '3-5K', value: '2' },
        { label: '5-10K', value: '3' },
        { label: '10-20K', value: '4' },
        { label: '20-50K', value: '5' },
        { label: '50K以上', value: '6' }
      ],
      workExps: [
        { label: '不限', value: '不限' },
        { label: '应届生', value: '应届生' },
        { label: '1-3年', value: '1-3年' },
        { label: '3-5年', value: '3-5年' },
        { label: '5-10年', value: '5-10年' },
        { label: '10年以上', value: '10年以上' }
      ],
      educations: [
        { label: '不限', value: '不限' },
        { label: '大专', value: '大专' },
        { label: '本科', value: '本科' },
        { label: '硕士', value: '硕士' },
        { label: '博士', value: '博士' }
      ]
    };
  },
  computed: {
    activeFilters() {
      const filters = [];
      if (this.filters.city) {
        filters.push({ key: 'city', value: this.filters.city });
      }
      if (this.filters.salaryRange) {
        const salary = this.salaryRanges.find(s => s.value === this.filters.salaryRange);
        if (salary) {
          filters.push({ key: 'salaryRange', value: salary.label });
        }
      }
      if (this.filters.workYears) {
        filters.push({ key: 'workYears', value: this.filters.workYears });
      }
      if (this.filters.education) {
        filters.push({ key: 'education', value: this.filters.education });
      }
      return filters;
    }
  },
  mounted() {
    this.getJobList();
  },
  methods: {
    async getJobList(refresh = true) {
      if (refresh) {
        this.page = 1;
        this.jobList = [];
        this.hasMore = true;
      }
      
      if (this.loading || !this.hasMore) return;
      
      this.loading = true;
      try {
        const params = {
          keyword: this.searchKeyword,
          page: this.page,
          pageSize: this.pageSize,
          sortBy: this.sortBy,
          ...this.filters
        };
        
        const res = await get('/jobs', params);
        if (res.code === 200) {
          const list = res.data.list || [];
          this.jobList = refresh ? list : [...this.jobList, ...list];
          this.hasMore = list.length >= this.pageSize;
          if (list.length >= this.pageSize) {
            this.page++;
          }
        }
      } catch (error) {
        console.error('获取岗位列表失败:', error);
      } finally {
        this.loading = false;
      }
    },
    loadMore() {
      this.getJobList(false);
    },
    setSort(sort) {
      if (this.sortBy !== sort) {
        this.sortBy = sort;
        this.getJobList(true);
      }
    },
    goToDetail(id) {
      this.$router.push(`/jobs/${id}`);
    },
    closeSearch() {
      this.showSearch = false;
    },
    closeAllPopups() {
      this.showSearch = false;
      this.showFilter = false;
    },
    selectKeyword(keyword) {
      this.searchInput = keyword;
      this.doSearch();
    },
    doSearch() {
      this.searchKeyword = this.searchInput;
      this.closeSearch();
      this.getJobList(true);
    },
    clearFilter(key) {
      this.filters[key] = '';
      this.getJobList(true);
    },
    clearAllFilters() {
      this.filters = {
        city: '',
        salaryRange: '',
        workYears: '',
        education: ''
      };
      this.searchKeyword = '';
      this.getJobList(true);
    },
    resetFilter() {
      this.filters = {
        city: '',
        salaryRange: '',
        workYears: '',
        education: ''
      };
    },
    applyFilter() {
      this.showFilter = false;
      this.getJobList(true);
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
.jobs-page {
  background: #f5f5f5;
  min-height: calc(100vh - 50px);
}

/* 搜索头部 */
.search-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  padding: 12px 15px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.search-bar {
  flex: 1;
  height: 36px;
  background: #f5f7fa;
  border-radius: 18px;
  display: flex;
  align-items: center;
  padding: 0 15px;
  gap: 8px;
  cursor: pointer;
}

.search-bar i {
  font-size: 16px;
  color: #909399;
}

.search-bar span {
  font-size: 14px;
  color: #909399;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.filter-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 50%;
  cursor: pointer;
}

.filter-btn i {
  font-size: 18px;
  color: #606266;
}

/* 筛选标签 */
.filter-tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 10px 15px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.filter-tags .tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: #ecf5ff;
  color: #409eff;
  font-size: 12px;
  border-radius: 12px;
}

.filter-tags .tag i {
  cursor: pointer;
  font-size: 10px;
}

.filter-tags .clear-all {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
  cursor: pointer;
}

/* 排序栏 */
.sort-bar {
  display: flex;
  background: #fff;
  padding: 10px 15px;
  gap: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.sort-item {
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 2px;
}

.sort-item.active {
  color: #409eff;
  font-weight: 500;
}

/* 岗位列表 */
.job-list {
  padding: 10px;
}

.job-card {
  background: #fff;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.2s;
}

.job-card:active {
  transform: scale(0.99);
  background: #fafafa;
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.job-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  flex: 1;
  margin-right: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.salary {
  font-size: 16px;
  font-weight: bold;
  color: #fa5555;
}

.job-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.tag-item {
  padding: 2px 8px;
  background: #f5f7fa;
  color: #909399;
  font-size: 12px;
  border-radius: 4px;
}

.job-company {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-top: 1px solid #f5f5f5;
}

.company-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  overflow: hidden;
}

.company-name {
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.company-meta {
  font-size: 12px;
  color: #909399;
}

.job-footer {
  display: flex;
  justify-content: space-between;
  padding-top: 10px;
  border-top: 1px solid #f5f5f5;
  font-size: 12px;
  color: #c0c4cc;
}

/* 加载更多 */
.load-more, .no-more, .empty {
  text-align: center;
  padding: 20px;
  font-size: 14px;
  color: #909399;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding-top: 80px;
}

.empty i {
  font-size: 60px;
  color: #e4e7ed;
}

.empty p {
  margin: 0;
}

/* 搜索弹窗 */
.search-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  z-index: 1001;
}

.search-header-popup {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.search-box {
  flex: 1;
}

.search-box ::v-deep .el-input__inner {
  height: 36px;
  border-radius: 18px;
  background: #f5f7fa;
  border: none;
}

.cancel-btn {
  font-size: 14px;
  color: #606266;
  cursor: pointer;
}

.search-suggestions {
  padding: 15px;
}

.suggestion-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.keyword-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.keyword {
  padding: 6px 16px;
  background: #f5f7fa;
  color: #606266;
  font-size: 13px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.keyword:active {
  background: #e4e7ed;
}

/* 筛选弹窗 */
.filter-popup {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: 80%;
  max-width: 360px;
  background: #fff;
  z-index: 1001;
  display: flex;
  flex-direction: column;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.filter-header .title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.filter-header .reset-btn {
  font-size: 14px;
  color: #409eff;
  cursor: pointer;
}

.filter-content {
  flex: 1;
  overflow-y: auto;
  padding: 15px 20px;
}

.filter-group {
  margin-bottom: 20px;
}

.group-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
}

.group-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.group-options .option {
  padding: 6px 16px;
  background: #f5f7fa;
  color: #606266;
  font-size: 13px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.group-options .option.active {
  background: #ecf5ff;
  color: #409eff;
  border: 1px solid #409eff;
}

.filter-footer {
  display: flex;
  border-top: 1px solid #f0f0f0;
}

.filter-footer .btn {
  flex: 1;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  cursor: pointer;
}

.filter-footer .btn.cancel {
  background: #fff;
  color: #606266;
}

.filter-footer .btn.confirm {
  background: #409eff;
  color: #fff;
}

/* 遮罩层 */
.popup-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}
</style>
