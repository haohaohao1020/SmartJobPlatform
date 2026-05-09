<template>
  <div class="jobs-page">
    <div class="top-tabs">
      <div class="tab-list">
        <div 
          class="tab-item"
          :class="{ active: activeTabType === 'industry' }"
          @click="toggleCategoryTab('industry')"
        >
          <span>行业</span>
          <i :class="activeTabType === 'industry' && showPanel ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
        </div>
        <div 
          class="tab-item"
          :class="{ active: activeTabType === 'category' }"
          @click="toggleCategoryTab('category')"
        >
          <span>职位</span>
          <i :class="activeTabType === 'category' && showPanel ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
        </div>
        <div class="tab-item" @click="showSearch = true">
          <i class="el-icon-search"></i>
          <span>搜索</span>
        </div>
        <div class="tab-item" @click="showFilter = true">
          <i class="el-icon-filter"></i>
          <span>筛选</span>
        </div>
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
    
    <div class="category-panel" v-if="showPanel">
      <div class="left-list" v-if="activeTabType === 'industry'">
        <div 
          class="list-item"
          :class="{ active: !filters.industryId }"
          @click="selectIndustry('')"
        >
          全部行业
        </div>
        <div 
          v-for="industry in industries" 
          :key="industry.id"
          class="list-item"
          :class="{ active: filters.industryId == industry.id }"
          @click="selectIndustry(industry.id)"
        >
          {{ industry.name }}
        </div>
      </div>
      
      <div class="left-list" v-else>
        <div 
          class="list-item"
          :class="{ active: !filters.categoryId }"
          @click="selectCategory('')"
        >
          全部职位
        </div>
        <div 
          v-for="category in jobCategories" 
          :key="category.id"
          class="list-item"
          :class="{ active: filters.categoryId == category.id }"
          @click="selectCategory(category.id)"
        >
          {{ category.name }}
        </div>
      </div>
      
      <div class="right-content" v-if="selectedCategory && selectedCategory.subCategories">
        <div class="grid-list">
          <div 
            class="grid-item"
            :class="{ active: !filters.subCategory }"
            @click="selectSubCategory('')"
          >
            全部
          </div>
          <div 
            v-for="sub in selectedCategory.subCategories" 
            :key="sub"
            class="grid-item"
            :class="{ active: filters.subCategory === sub }"
            @click="selectSubCategory(sub)"
          >
            {{ sub }}
          </div>
        </div>
      </div>
      <div class="right-content empty-right" v-else-if="activeTabType === 'category'">
        <div class="empty-tip">请选择左侧职位类型</div>
      </div>
    </div>
    
    <div class="sort-bar">
      <div 
        class="sort-item"
        :class="{ active: sortBy === 'default' }"
        @click="setSort('default')"
      >
        综合
      </div>
      <div 
        class="sort-item"
        :class="{ active: sortBy === 'salary' }"
        @click="setSort('salary')"
      >
        薪资最高
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
        class="job-item"
        @click="goToDetail(job.id)"
      >
        <div class="job-main">
          <div class="job-info">
            <span class="job-title">{{ job.title }}</span>
            <span class="job-salary">{{ formatSalary(job.salaryMin, job.salaryMax) }}K</span>
          </div>
          <div class="job-meta">
            <span class="company-name">{{ job.companyName }}</span>
            <span class="job-tags-item">{{ job.city }}</span>
            <span class="job-tags-item">{{ job.workYears }}</span>
            <span class="job-tags-item">{{ job.education }}</span>
          </div>
          <div class="job-tags">
            <span class="tag" v-if="job.subCategory">{{ job.subCategory }}</span>
            <span class="tag">{{ job.jobType }}</span>
          </div>
        </div>
        <div class="job-footer">
          <div class="company-info">
            <span class="company-industry">{{ job.industry }}</span>
            <span class="company-size">{{ job.companySize }}</span>
          </div>
          <div class="job-time">{{ formatTime(job.publishTime) }}</div>
        </div>
      </div>
      
      <div class="load-more" v-if="hasMore" @click="loadMore">
        <span v-if="loading">加载中...</span>
        <span v-else>加载更多</span>
      </div>
      
      <div class="no-more" v-else-if="jobList.length > 0">
        没有更多了
      </div>
      
      <div class="empty" v-if="!loading && jobList.length === 0">
        <i class="el-icon-document"></i>
        <p>暂无符合条件的岗位</p>
      </div>
    </div>
    
    <div class="search-popup" v-if="showSearch">
      <div class="search-header">
        <div class="search-input-box">
          <input 
            v-model="searchInput" 
            placeholder="搜索职位、公司" 
            @keyup.enter="doSearch"
          />
        </div>
        <span class="cancel-btn" @click="closeSearch">取消</span>
      </div>
      <div class="search-body">
        <div class="hot-section">
          <div class="section-title">热门搜索</div>
          <div class="hot-tags">
            <span 
              v-for="keyword in hotKeywords" 
              :key="keyword"
              class="hot-tag"
              @click="selectKeyword(keyword)"
            >
              {{ keyword }}
            </span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="filter-popup" v-if="showFilter">
      <div class="filter-header">
        <span class="title">筛选</span>
        <span class="reset" @click="resetFilter">重置</span>
      </div>
      <div class="filter-body">
        <div class="filter-section">
          <div class="section-title">工作城市</div>
          <div class="option-list">
            <span 
              class="option" 
              :class="{ active: filters.city === '' }"
              @click="filters.city = ''"
            >
              不限
            </span>
            <span 
              v-for="city in cities" 
              :key="city"
              class="option" 
              :class="{ active: filters.city === city }"
              @click="filters.city = city"
            >
              {{ city }}
            </span>
          </div>
        </div>
        
        <div class="filter-section">
          <div class="section-title">工作经验</div>
          <div class="option-list">
            <span 
              v-for="exp in workExps" 
              :key="exp.value"
              class="option" 
              :class="{ active: filters.workYears === exp.value }"
              @click="filters.workYears = exp.value"
            >
              {{ exp.label }}
            </span>
          </div>
        </div>
        
        <div class="filter-section">
          <div class="section-title">学历要求</div>
          <div class="option-list">
            <span 
              v-for="edu in educations" 
              :key="edu.value"
              class="option" 
              :class="{ active: filters.education === edu.value }"
              @click="filters.education = edu.value"
            >
              {{ edu.label }}
            </span>
          </div>
        </div>
        
        <div class="filter-section">
          <div class="section-title">薪资范围</div>
          <div class="option-list">
            <span 
              class="option" 
              :class="{ active: filters.salaryRange === '' }"
              @click="filters.salaryRange = ''"
            >
              不限
            </span>
            <span 
              v-for="salary in salaryRanges" 
              :key="salary.value"
              class="option" 
              :class="{ active: filters.salaryRange === salary.value }"
              @click="filters.salaryRange = salary.value"
            >
              {{ salary.label }}
            </span>
          </div>
        </div>
      </div>
      <div class="filter-footer">
        <div class="btn confirm" @click="applyFilter">确定</div>
      </div>
    </div>
    
    <div class="mask" v-if="showPanel || showFilter || showSearch" @click="closeAll"></div>
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
      
      activeTabType: 'industry',
      showPanel: false,
      
      searchKeyword: '',
      searchInput: '',
      showSearch: false,
      showFilter: false,
      
      industries: [],
      jobCategories: [],
      
      filters: {
        city: '',
        salaryRange: '',
        workYears: '',
        education: '',
        industryId: '',
        categoryId: '',
        subCategory: ''
      },
      
      hotKeywords: ['Java', '前端开发', '产品经理', '运营', '销售', '会计', 'UI设计', '数据分析师', '项目经理', 'HR'],
      cities: ['北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '西安', '南京', '重庆', '苏州', '天津'],
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
    selectedCategory() {
      if (this.filters.categoryId && this.jobCategories.length > 0) {
        return this.jobCategories.find(c => c.id == this.filters.categoryId);
      }
      return null;
    },
    selectedIndustry() {
      if (this.filters.industryId && this.industries.length > 0) {
        return this.industries.find(i => i.id == this.filters.industryId);
      }
      return null;
    },
    activeFilters() {
      const filters = [];
      if (this.filters.industryId && this.selectedIndustry) {
        filters.push({ key: 'industryId', value: this.selectedIndustry.name });
      }
      if (this.filters.categoryId && this.selectedCategory) {
        filters.push({ key: 'categoryId', value: this.selectedCategory.name });
      }
      if (this.filters.subCategory) {
        filters.push({ key: 'subCategory', value: this.filters.subCategory });
      }
      if (this.filters.city) {
        filters.push({ key: 'city', value: this.filters.city });
      }
      if (this.filters.salaryRange) {
        const salary = this.salaryRanges.find(s => s.value === this.filters.salaryRange);
        if (salary) {
          filters.push({ key: 'salaryRange', value: salary.label });
        }
      }
      if (this.filters.workYears && this.filters.workYears !== '不限') {
        filters.push({ key: 'workYears', value: this.filters.workYears });
      }
      if (this.filters.education && this.filters.education !== '不限') {
        filters.push({ key: 'education', value: this.filters.education });
      }
      return filters;
    }
  },
  async mounted() {
    await this.loadCategories();
    this.getJobList();
  },
  methods: {
    async loadCategories() {
      try {
        const [industryRes, categoryRes] = await Promise.all([
          get('/jobs/industries'),
          get('/jobs/categories')
        ]);
        if (industryRes.code === 200) {
          this.industries = industryRes.data;
        }
        if (categoryRes.code === 200) {
          this.jobCategories = categoryRes.data;
        }
      } catch (error) {
        console.error('加载分类数据失败:', error);
      }
    },
    toggleCategoryTab(type) {
      if (this.activeTabType === type) {
        this.showPanel = !this.showPanel;
      } else {
        this.activeTabType = type;
        this.showPanel = true;
      }
    },
    selectIndustry(id) {
      this.filters.industryId = id;
      this.filters.categoryId = '';
      this.filters.subCategory = '';
      this.showPanel = false;
      this.getJobList(true);
    },
    selectCategory(id) {
      this.filters.categoryId = id;
      this.filters.industryId = '';
      if (!id) {
        this.filters.subCategory = '';
        this.showPanel = false;
        this.getJobList(true);
        return;
      }
    },
    selectSubCategory(sub) {
      this.filters.subCategory = sub;
      this.showPanel = false;
      this.getJobList(true);
    },
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
    closeAll() {
      this.showSearch = false;
      this.showFilter = false;
      this.showPanel = false;
    },
    selectKeyword(keyword) {
      this.searchInput = keyword;
      this.doSearch();
    },
    doSearch() {
      this.searchKeyword = this.searchInput;
      this.closeSearch();
      this.showPanel = false;
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
        education: '',
        industryId: '',
        categoryId: '',
        subCategory: ''
      };
      this.searchKeyword = '';
      this.getJobList(true);
    },
    resetFilter() {
      this.filters = {
        city: '',
        salaryRange: '',
        workYears: '',
        education: '',
        industryId: this.filters.industryId,
        categoryId: this.filters.categoryId,
        subCategory: this.filters.subCategory
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
* {
  box-sizing: border-box;
}

.jobs-page {
  background: #f5f5f5;
  min-height: calc(100vh - 50px);
}

/* 顶部分类标签 */
.top-tabs {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
}

.tab-list {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  gap: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  padding: 4px 0;
}

.tab-item.active {
  color: #12B7F5;
  font-weight: 500;
}

.tab-item i {
  font-size: 12px;
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
  padding: 3px 10px;
  background: #12B7F51A;
  color: #12B7F5;
  font-size: 12px;
  border-radius: 12px;
}

.filter-tags .tag i {
  cursor: pointer;
  font-size: 10px;
}

.filter-tags .clear-all {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
  cursor: pointer;
}

/* 分类面板 */
.category-panel {
  position: absolute;
  top: 45px;
  left: 0;
  right: 0;
  z-index: 200;
  background: #fff;
  display: flex;
  max-height: 300px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.left-list {
  width: 110px;
  background: #f5f5f5;
  overflow-y: auto;
  flex-shrink: 0;
}

.list-item {
  padding: 12px 15px;
  font-size: 13px;
  color: #333;
  cursor: pointer;
  border-left: 3px solid transparent;
}

.list-item.active {
  background: #fff;
  color: #12B7F5;
  border-left-color: #12B7F5;
  font-weight: 500;
}

.right-content {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
}

.right-content.empty-right {
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-tip {
  color: #999;
  font-size: 13px;
}

.grid-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.grid-item {
  padding: 6px 16px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 13px;
  color: #333;
  cursor: pointer;
}

.grid-item.active {
  background: #12B7F5;
  color: #fff;
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
  font-size: 13px;
  color: #666;
  cursor: pointer;
}

.sort-item.active {
  color: #12B7F5;
  font-weight: 500;
}

/* 岗位列表 */
.job-list {
  padding: 8px 12px;
}

.job-item {
  background: #fff;
  border-radius: 8px;
  padding: 14px 16px;
  margin-bottom: 8px;
  cursor: pointer;
}

.job-main {
  padding-bottom: 10px;
  border-bottom: 1px dashed #f0f0f0;
}

.job-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.job-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.job-salary {
  font-size: 16px;
  font-weight: bold;
  color: #FA6041;
}

.job-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.company-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.job-tags-item {
  font-size: 12px;
  color: #666;
  padding: 2px 0;
}

.job-tags {
  display: flex;
  gap: 8px;
}

.job-tags .tag {
  font-size: 11px;
  color: #12B7F5;
  background: #12B7F51A;
  padding: 2px 8px;
  border-radius: 2px;
}

.job-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
}

.company-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.company-industry,
.company-size {
  font-size: 12px;
  color: #999;
}

.job-time {
  font-size: 11px;
  color: #ccc;
}

/* 加载更多 */
.load-more, .no-more, .empty {
  text-align: center;
  padding: 20px;
  font-size: 13px;
  color: #999;
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding-top: 60px;
}

.empty i {
  font-size: 50px;
  color: #ddd;
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

.search-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.search-input-box {
  flex: 1;
  height: 36px;
  background: #f5f5f5;
  border-radius: 18px;
  display: flex;
  align-items: center;
  padding: 0 15px;
}

.search-input-box input {
  width: 100%;
  border: none;
  background: transparent;
  font-size: 14px;
  outline: none;
}

.search-input-box input::placeholder {
  color: #999;
}

.cancel-btn {
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.search-body {
  padding: 15px;
}

.hot-section .section-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 12px;
  font-weight: 500;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.hot-tag {
  padding: 6px 16px;
  background: #f5f5f5;
  color: #333;
  font-size: 13px;
  border-radius: 4px;
  cursor: pointer;
}

/* 筛选弹窗 */
.filter-popup {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: 85%;
  max-width: 320px;
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
  color: #333;
}

.filter-header .reset {
  font-size: 14px;
  color: #12B7F5;
  cursor: pointer;
}

.filter-body {
  flex: 1;
  overflow-y: auto;
  padding: 15px 20px;
}

.filter-section {
  margin-bottom: 25px;
}

.filter-section .section-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 12px;
  font-weight: 500;
}

.option-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-list .option {
  padding: 6px 16px;
  background: #f5f5f5;
  color: #333;
  font-size: 13px;
  border-radius: 4px;
  cursor: pointer;
}

.option-list .option.active {
  background: #12B7F5;
  color: #fff;
}

.filter-footer {
  border-top: 1px solid #f0f0f0;
}

.filter-footer .btn {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  cursor: pointer;
}

.filter-footer .btn.confirm {
  background: #12B7F5;
  color: #fff;
}

/* 遮罩层 */
.mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 99;
}
</style>
