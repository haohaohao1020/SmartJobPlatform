<template>
  <div class="jobs-page">
    <div class="container">
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
            :class="{ active: !searchForm.industryId }"
            @click="selectIndustry('')"
          >
            全部行业
          </div>
          <div 
            v-for="industry in industries" 
            :key="industry.id"
            class="list-item"
            :class="{ active: searchForm.industryId == industry.id }"
            @click="selectIndustry(industry.id)"
          >
            {{ industry.name }}
          </div>
        </div>
        
        <div class="left-list" v-else>
          <div 
            class="list-item"
            :class="{ active: !searchForm.categoryId }"
            @click="selectCategory('')"
          >
            全部职位
          </div>
          <div 
            v-for="category in jobCategories" 
            :key="category.id"
            class="list-item"
            :class="{ active: searchForm.categoryId == category.id }"
            @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </div>
        </div>
        
        <div class="right-content" v-if="selectedCategory && selectedCategory.subCategories">
          <div class="grid-list">
            <div 
              class="grid-item"
              :class="{ active: !searchForm.subCategory }"
              @click="selectSubCategory('')"
            >
              全部
            </div>
            <div 
              v-for="sub in selectedCategory.subCategories" 
              :key="sub"
              class="grid-item"
              :class="{ active: searchForm.subCategory === sub }"
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
          :class="{ active: searchForm.sortBy === 'publishTime' }"
          @click="setSort('publishTime')"
        >
          综合
        </div>
        <div 
          class="sort-item"
          :class="{ active: searchForm.sortBy === 'salary' }"
          @click="setSort('salary')"
        >
          薪资最高
        </div>
        <div 
          class="sort-item"
          :class="{ active: searchForm.sortBy === 'viewCount' }"
          @click="setSort('viewCount')"
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
        
        <div class="pagination-container" v-if="total > 0">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="searchForm.page"
            :page-sizes="[10, 20, 30]"
            :page-size="searchForm.pageSize"
            layout="prev, pager, next"
            :total="total"
          ></el-pagination>
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
                :class="{ active: searchForm.city === '' }"
                @click="searchForm.city = ''"
              >
                不限
              </span>
              <span 
                v-for="city in cities" 
                :key="city"
                class="option" 
                :class="{ active: searchForm.city === city }"
                @click="searchForm.city = city"
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
                :class="{ active: searchForm.workYears === exp.value }"
                @click="searchForm.workYears = exp.value"
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
                :class="{ active: searchForm.education === edu.value }"
                @click="searchForm.education = edu.value"
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
                :class="{ active: searchForm.salary === '' }"
                @click="searchForm.salary = ''"
              >
                不限
              </span>
              <span 
                v-for="salary in salaryRanges" 
                :key="salary.value"
                class="option" 
                :class="{ active: searchForm.salary === salary.value }"
                @click="searchForm.salary = salary.value"
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
      
      activeTabType: 'industry',
      showPanel: false,
      
      searchInput: '',
      showSearch: false,
      showFilter: false,
      
      industries: [],
      jobCategories: [],
      
      searchForm: {
        keyword: '',
        city: '',
        workYears: '',
        education: '',
        salary: '',
        sortBy: 'publishTime',
        sortOrder: 'desc',
        page: 1,
        pageSize: 10,
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
        { label: '应届生', value: '应届毕业生' },
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
      if (this.searchForm.categoryId && this.jobCategories.length > 0) {
        return this.jobCategories.find(c => c.id == this.searchForm.categoryId);
      }
      return null;
    },
    selectedIndustry() {
      if (this.searchForm.industryId && this.industries.length > 0) {
        return this.industries.find(i => i.id == this.searchForm.industryId);
      }
      return null;
    },
    activeFilters() {
      const filters = [];
      if (this.searchForm.industryId && this.selectedIndustry) {
        filters.push({ key: 'industryId', value: this.selectedIndustry.name });
      }
      if (this.searchForm.categoryId && this.selectedCategory) {
        filters.push({ key: 'categoryId', value: this.selectedCategory.name });
      }
      if (this.searchForm.subCategory) {
        filters.push({ key: 'subCategory', value: this.searchForm.subCategory });
      }
      if (this.searchForm.city) {
        filters.push({ key: 'city', value: this.searchForm.city });
      }
      if (this.searchForm.salary) {
        const salary = this.salaryRanges.find(s => s.value === this.searchForm.salary);
        if (salary) {
          filters.push({ key: 'salary', value: salary.label });
        }
      }
      if (this.searchForm.workYears && this.searchForm.workYears !== '不限') {
        filters.push({ key: 'workYears', value: this.searchForm.workYears });
      }
      if (this.searchForm.education && this.searchForm.education !== '不限') {
        filters.push({ key: 'education', value: this.searchForm.education });
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
      this.searchForm.industryId = id;
      this.searchForm.categoryId = '';
      this.searchForm.subCategory = '';
      this.showPanel = false;
      this.searchForm.page = 1;
      this.getJobList();
    },
    selectCategory(id) {
      this.searchForm.categoryId = id;
      this.searchForm.industryId = '';
      if (!id) {
        this.searchForm.subCategory = '';
        this.showPanel = false;
        this.searchForm.page = 1;
        this.getJobList();
        return;
      }
    },
    selectSubCategory(sub) {
      this.searchForm.subCategory = sub;
      this.showPanel = false;
      this.searchForm.page = 1;
      this.getJobList();
    },
    async getJobList() {
      this.loading = true;
      try {
        const params = {
          ...this.searchForm
        };
        const res = await get('/jobs', params);
        if (res.code === 200) {
          this.jobList = res.data.list || [];
          this.total = res.data.total || 0;
        }
      } catch (error) {
        console.error('获取岗位列表失败:', error);
      } finally {
        this.loading = false;
      }
    },
    setSort(sort) {
      if (this.searchForm.sortBy !== sort) {
        this.searchForm.sortBy = sort;
        this.searchForm.page = 1;
        this.getJobList();
      }
    },
    goToDetail(id) {
      this.$router.push(`/jobseeker/jobs/${id}`);
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
      this.searchForm.keyword = this.searchInput;
      this.closeSearch();
      this.showPanel = false;
      this.searchForm.page = 1;
      this.getJobList();
    },
    clearFilter(key) {
      this.searchForm[key] = '';
      this.searchForm.page = 1;
      this.getJobList();
    },
    clearAllFilters() {
      this.searchForm = {
        keyword: '',
        city: '',
        workYears: '',
        education: '',
        salary: '',
        sortBy: 'publishTime',
        sortOrder: 'desc',
        page: 1,
        pageSize: 10,
        industryId: '',
        categoryId: '',
        subCategory: ''
      };
      this.getJobList();
    },
    resetFilter() {
      this.searchForm = {
        keyword: this.searchForm.keyword,
        city: '',
        workYears: '',
        education: '',
        salary: '',
        sortBy: 'publishTime',
        sortOrder: 'desc',
        page: 1,
        pageSize: 10,
        industryId: this.searchForm.industryId,
        categoryId: this.searchForm.categoryId,
        subCategory: this.searchForm.subCategory
      };
    },
    applyFilter() {
      this.showFilter = false;
      this.searchForm.page = 1;
      this.getJobList();
    },
    handleSizeChange(val) {
      this.searchForm.pageSize = val;
      this.getJobList();
    },
    handleCurrentChange(val) {
      this.searchForm.page = val;
      this.getJobList();
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
  min-height: 100%;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  margin-top: 20px;
  position: relative;
}

/* 顶部分类标签 */
.top-tabs {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  margin: -20px -20px 0 -20px;
  padding: 0 20px;
  border-radius: 8px 8px 0 0;
}

.tab-list {
  display: flex;
  align-items: center;
  padding: 12px 0;
  gap: 24px;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 15px;
  color: #666;
  cursor: pointer;
  padding: 4px 0;
  transition: color 0.2s;
}

.tab-item:hover {
  color: #333;
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
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.filter-tags .tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  background: #12B7F51A;
  color: #12B7F5;
  font-size: 13px;
  border-radius: 14px;
}

.filter-tags .tag i {
  cursor: pointer;
  font-size: 11px;
}

.filter-tags .clear-all {
  font-size: 13px;
  color: #999;
  margin-left: 8px;
  cursor: pointer;
}

.filter-tags .clear-all:hover {
  color: #666;
}

/* 分类面板 */
.category-panel {
  position: absolute;
  top: 65px;
  left: 0;
  right: 0;
  z-index: 200;
  background: #fff;
  display: flex;
  max-height: 320px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 0 0 8px 8px;
}

.left-list {
  width: 140px;
  background: #f8f8f8;
  overflow-y: auto;
  flex-shrink: 0;
}

.list-item {
  padding: 14px 20px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  border-left: 3px solid transparent;
  transition: all 0.2s;
}

.list-item:hover {
  background: #f0f0f0;
}

.list-item.active {
  background: #fff;
  color: #12B7F5;
  border-left-color: #12B7F5;
  font-weight: 500;
}

.right-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.right-content.empty-right {
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-tip {
  color: #999;
  font-size: 14px;
}

.grid-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.grid-item {
  padding: 8px 20px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.2s;
}

.grid-item:hover {
  background: #e8e8e8;
}

.grid-item.active {
  background: #12B7F5;
  color: #fff;
}

/* 排序栏 */
.sort-bar {
  display: flex;
  background: #fff;
  padding: 12px 0;
  gap: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.sort-item {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}

.sort-item:hover {
  color: #333;
}

.sort-item.active {
  color: #12B7F5;
  font-weight: 500;
}

/* 岗位列表 */
.job-list {
  padding: 16px 0;
}

.job-item {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 18px 20px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.job-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-color: #e0e0e0;
}

.job-main {
  padding-bottom: 12px;
  border-bottom: 1px dashed #f0f0f0;
}

.job-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.job-title {
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.job-salary {
  font-size: 18px;
  font-weight: bold;
  color: #FA6041;
}

.job-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.company-name {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.job-tags-item {
  font-size: 13px;
  color: #666;
  padding: 2px 0;
}

.job-tags {
  display: flex;
  gap: 10px;
}

.job-tags .tag {
  font-size: 12px;
  color: #12B7F5;
  background: #12B7F51A;
  padding: 3px 10px;
  border-radius: 3px;
}

.job-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
}

.company-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.company-industry,
.company-size {
  font-size: 13px;
  color: #999;
}

.job-time {
  font-size: 12px;
  color: #ccc;
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 空状态 */
.empty {
  text-align: center;
  padding: 80px 20px;
  font-size: 14px;
  color: #999;
}

.empty i {
  font-size: 60px;
  color: #ddd;
  display: block;
  margin-bottom: 16px;
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
  gap: 16px;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.search-input-box {
  flex: 1;
  height: 44px;
  background: #f5f5f5;
  border-radius: 22px;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.search-input-box input {
  width: 100%;
  border: none;
  background: transparent;
  font-size: 15px;
  outline: none;
}

.search-input-box input::placeholder {
  color: #999;
}

.cancel-btn {
  font-size: 15px;
  color: #666;
  cursor: pointer;
}

.cancel-btn:hover {
  color: #333;
}

.search-body {
  padding: 20px 24px;
}

.hot-section .section-title {
  font-size: 15px;
  color: #333;
  margin-bottom: 16px;
  font-weight: 500;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.hot-tag {
  padding: 8px 20px;
  background: #f5f5f5;
  color: #333;
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.hot-tag:hover {
  background: #e8e8e8;
}

/* 筛选弹窗 */
.filter-popup {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: 400px;
  max-width: 80%;
  background: #fff;
  z-index: 1001;
  display: flex;
  flex-direction: column;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.1);
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.filter-header .title {
  font-size: 17px;
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
  padding: 20px 24px;
}

.filter-section {
  margin-bottom: 30px;
}

.filter-section .section-title {
  font-size: 15px;
  color: #333;
  margin-bottom: 16px;
  font-weight: 500;
}

.option-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.option-list .option {
  padding: 8px 20px;
  background: #f5f5f5;
  color: #333;
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.option-list .option:hover {
  background: #e8e8e8;
}

.option-list .option.active {
  background: #12B7F5;
  color: #fff;
}

.filter-footer {
  border-top: 1px solid #f0f0f0;
}

.filter-footer .btn {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  cursor: pointer;
}

.filter-footer .btn.confirm {
  background: #12B7F5;
  color: #fff;
  transition: background 0.2s;
}

.filter-footer .btn.confirm:hover {
  background: #0da6de;
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
