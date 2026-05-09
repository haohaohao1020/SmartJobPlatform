<template>
  <div class="jobs-page">
    <div class="container">
      <!-- 顶部操作栏 -->
      <div class="action-bar">
        <div class="search-box">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索岗位名称"
            prefix-icon="el-icon-search"
            clearable
            style="width: 300px"
            @clear="handleSearch"
            @keyup.enter.native="handleSearch"
          >
            <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
          </el-input>
        </div>
        <div class="actions">
          <el-select 
            v-model="searchForm.status" 
            placeholder="岗位状态" 
            clearable
            style="width: 150px; margin-right: 10px"
            @change="handleSearch"
          >
            <el-option label="招聘中" :value="1"></el-option>
            <el-option label="已下架" :value="2"></el-option>
            <el-option label="审核中" :value="3"></el-option>
          </el-select>
          <el-button type="primary" @click="goToPublish">
            <i class="el-icon-plus"></i> 发布新岗位
          </el-button>
        </div>
      </div>
      
      <!-- 岗位列表 -->
      <div class="list-section">
        <el-table 
          v-loading="loading" 
          :data="jobList" 
          style="width: 100%"
          stripe
        >
          <el-table-column prop="title" label="岗位名称" min-width="240">
            <template slot-scope="scope">
              <div class="job-info">
                <span class="job-title" @click="goToDetail(scope.row.id)">{{ scope.row.title }}</span>
                <div class="job-meta">
                  <span>{{ scope.row.city }}</span>
                  <span class="divider">|</span>
                  <span>{{ scope.row.workYears }}</span>
                  <span class="divider">|</span>
                  <span>{{ scope.row.education }}</span>
                </div>
                <div class="job-cats">
                  <el-tag size="mini" type="primary" effect="plain">{{ scope.row.industry }}</el-tag>
                  <el-tag size="mini" type="success" effect="plain">{{ scope.row.categoryName }}</el-tag>
                  <el-tag v-if="scope.row.subCategory" size="mini" effect="plain">{{ scope.row.subCategory }}</el-tag>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="salaryMin" label="薪资" width="120">
            <template slot-scope="scope">
              <span class="salary">{{ formatSalary(scope.row.salaryMin, scope.row.salaryMax) }}K/月</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ getStatusName(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="auditStatus" label="审核状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getAuditStatusType(scope.row.auditStatus)" size="small" effect="plain">
                {{ getAuditStatusName(scope.row.auditStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="viewCount" label="浏览量" width="80">
            <template slot-scope="scope">
              <span>{{ scope.row.viewCount }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="applyCount" label="投递数" width="80">
            <template slot-scope="scope">
              <span class="apply-count">{{ scope.row.applyCount }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="publishTime" label="发布时间" width="160">
            <template slot-scope="scope">
              <span>{{ formatDate(scope.row.publishTime) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="goToEdit(scope.row.id)">
                <i class="el-icon-edit"></i> 编辑
              </el-button>
              <el-button 
                v-if="scope.row.status === 1" 
                type="text" 
                size="small" 
                @click="toggleStatus(scope.row, 2)"
              >
                <i class="el-icon-circle-close"></i> 下架
              </el-button>
              <el-button 
                v-else-if="scope.row.status === 2 && scope.row.auditStatus === 1"
                type="text" 
                size="small" 
                @click="toggleStatus(scope.row, 1)"
              >
                <i class="el-icon-circle-check"></i> 上架
              </el-button>
              <el-button 
                type="text" 
                size="small" 
                class="danger-btn"
                @click="handleDelete(scope.row)"
              >
                <i class="el-icon-delete"></i> 删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
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
import { get, put, del } from '@/utils/request';

export default {
  name: 'HRJobs',
  data() {
    return {
      loading: false,
      jobList: [],
      total: 0,
      searchForm: {
        keyword: '',
        status: '',
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
        const res = await get('/hr/jobs', this.searchForm);
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
    // 跳转到发布页
    goToPublish() {
      this.$router.push('/hr/jobs/publish');
    },
    // 跳转到编辑页
    goToEdit(id) {
      this.$router.push(`/hr/jobs/edit/${id}`);
    },
    // 跳转到详情
    goToDetail(id) {
      this.$message.info('岗位详情页开发中');
    },
    // 上下架
    async toggleStatus(row, status) {
      const action = status === 1 ? '上架' : '下架';
      this.$confirm(`确定要${action}该岗位吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await put(`/hr/jobs/${row.id}/status`, { status });
          if (res.code === 200) {
            this.$message.success(`${action}成功`);
            this.getJobList();
          }
        } catch (error) {
          console.error('操作失败:', error);
        }
      }).catch(() => {});
    },
    // 删除
    handleDelete(row) {
      this.$confirm('确定要删除该岗位吗？删除后无法恢复。', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await del(`/hr/jobs/${row.id}`);
          if (res.code === 200) {
            this.$message.success('删除成功');
            this.getJobList();
          }
        } catch (error) {
          console.error('删除失败:', error);
        }
      }).catch(() => {});
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
    // 格式化薪资
    formatSalary(min, max) {
      return `${(min / 1000).toFixed(0)}-${(max / 1000).toFixed(0)}`;
    },
    // 获取状态类型
    getStatusType(status) {
      const typeMap = {
        1: 'success',  // 招聘中
        2: 'info',     // 已下架
        3: 'warning'   // 审核中
      };
      return typeMap[status] || '';
    },
    // 获取状态名称
    getStatusName(status) {
      const nameMap = {
        1: '招聘中',
        2: '已下架',
        3: '审核中'
      };
      return nameMap[status] || '未知';
    },
    // 获取审核状态类型
    getAuditStatusType(status) {
      const typeMap = {
        1: 'success',  // 已通过
        2: 'warning',  // 待审核
        3: 'danger'    // 已拒绝
      };
      return typeMap[status] || '';
    },
    // 获取审核状态名称
    getAuditStatusName(status) {
      const nameMap = {
        1: '已通过',
        2: '待审核',
        3: '已拒绝'
      };
      return nameMap[status] || '未知';
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
.jobs-page {
  min-height: 100%;
  background-color: #f5f7fa;
}

.container {
  max-width: 100%;
}

/* 顶部操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  padding: 15px 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.actions {
  display: flex;
  align-items: center;
}

/* 列表区域 */
.list-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.job-info {
  text-align: left;
}

.job-title {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  cursor: pointer;
  display: block;
  margin-bottom: 5px;
}

.job-title:hover {
  color: #409eff;
}

.job-meta {
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
}

.job-meta .divider {
  margin: 0 5px;
  color: #e4e7ed;
}

.job-cats {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.salary {
  color: #fa5555;
  font-weight: 500;
}

.apply-count {
  color: #409eff;
  font-weight: 500;
}

.danger-btn {
  color: #f56c6c !important;
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
  .action-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }
  
  .search-box {
    width: 100%;
  }
  
  .search-box .el-input {
    width: 100% !important;
  }
  
  .actions {
    justify-content: space-between;
  }
  
  .actions .el-select {
    flex: 1;
    margin-right: 10px;
  }
  
  .list-section {
    padding: 15px;
  }
  
  .pagination-container {
    justify-content: center;
    flex-wrap: wrap;
    gap: 10px;
  }
}
</style>
