<template>
  <div class="jobs-page">
    <div class="container">
      <!-- 顶部操作栏 -->
      <div class="action-bar">
        <div class="search-box">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索岗位名称、公司名称"
            prefix-icon="el-icon-search"
            clearable
            style="width: 300px"
            @clear="handleSearch"
            @keyup.enter.native="handleSearch"
          >
            <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
          </el-input>
        </div>
        <div class="filters">
          <el-select 
            v-model="searchForm.auditStatus" 
            placeholder="审核状态" 
            clearable
            style="width: 150px; margin-right: 10px"
            @change="handleSearch"
          >
            <el-option label="待审核" :value="2"></el-option>
            <el-option label="已通过" :value="1"></el-option>
            <el-option label="已拒绝" :value="3"></el-option>
          </el-select>
          <el-button type="primary" @click="refreshList">
            <i class="el-icon-refresh"></i> 刷新
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
          <el-table-column prop="id" label="ID" width="80">
            <template slot-scope="scope">
              <span class="id-text">{{ scope.row.id }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="title" label="岗位信息" min-width="220">
            <template slot-scope="scope">
              <div class="job-info">
                <div class="job-main">
                  <span class="job-title">{{ scope.row.title }}</span>
                  <span class="salary">{{ formatSalary(scope.row.salaryMin, scope.row.salaryMax) }}K/月</span>
                </div>
                <div class="job-meta">
                  <span>{{ scope.row.city }}</span>
                  <span class="divider">|</span>
                  <span>{{ scope.row.workYears }}</span>
                  <span class="divider">|</span>
                  <span>{{ scope.row.education }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="companyName" label="企业信息" min-width="180">
            <template slot-scope="scope">
              <div class="company-info">
                <el-avatar :size="36" :src="scope.row.companyLogo" class="logo">
                  <i class="el-icon-office-building"></i>
                </el-avatar>
                <div class="info">
                  <span class="name">{{ scope.row.companyName }}</span>
                  <div class="meta">
                    <span>{{ scope.row.industry }}</span>
                    <span class="divider">|</span>
                    <span>{{ scope.row.companySize }}</span>
                  </div>
                </div>
              </div>
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
          
          <el-table-column prop="auditStatus" label="审核状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getAuditStatusType(scope.row.auditStatus)" size="small">
                {{ getAuditStatusName(scope.row.auditStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="岗位状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="getJobStatusType(scope.row.status)" size="small" effect="plain">
                {{ getJobStatusName(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="publishTime" label="发布时间" width="160">
            <template slot-scope="scope">
              <span>{{ formatDate(scope.row.publishTime) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="220" fixed="right">
            <template slot-scope="scope">
              <el-dropdown @command="(command) => handleAction(scope.row, command)" size="small">
                <el-button type="text" size="small">
                  操作 <i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="view">
                    <i class="el-icon-view"></i> 查看详情
                  </el-dropdown-item>
                  <el-dropdown-item 
                    v-if="scope.row.auditStatus === 2"
                    command="pass"
                    divided
                  >
                    <i class="el-icon-circle-check" style="color: #67c23a"></i> 审核通过
                  </el-dropdown-item>
                  <el-dropdown-item 
                    v-if="scope.row.auditStatus === 2"
                    command="reject"
                  >
                    <i class="el-icon-circle-close" style="color: #f56c6c"></i> 审核拒绝
                  </el-dropdown-item>
                  <el-dropdown-item 
                    :command="scope.row.status === 1 ? 'disable' : 'enable'"
                    divided
                  >
                    <i 
                      :class="scope.row.status === 1 ? 'el-icon-circle-close' : 'el-icon-circle-check'"
                      :style="{ color: scope.row.status === 1 ? '#f56c6c' : '#67c23a' }"
                    ></i> 
                    {{ scope.row.status === 1 ? '下架岗位' : '上架岗位' }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
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
      
      <!-- 审核拒绝弹窗 -->
      <el-dialog title="填写拒绝原因" :visible.sync="rejectDialogVisible" width="500px">
        <el-form :model="rejectForm" label-width="100px">
          <el-form-item label="拒绝原因" prop="rejectReason">
            <el-input
              v-model="rejectForm.rejectReason"
              type="textarea"
              :rows="4"
              placeholder="请填写拒绝原因"
              maxlength="200"
              show-word-limit
            ></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReject" :loading="rejectLoading">确认拒绝</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { get, put } from '@/utils/request';

export default {
  name: 'AdminJobs',
  data() {
    return {
      loading: false,
      jobList: [],
      total: 0,
      searchForm: {
        keyword: '',
        auditStatus: '',
        page: 1,
        pageSize: 10
      },
      rejectDialogVisible: false,
      rejectLoading: false,
      rejectForm: {
        rejectReason: '',
        jobId: null
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
        const res = await get('/admin/jobs', this.searchForm);
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
    // 刷新列表
    refreshList() {
      this.getJobList();
    },
    // 操作处理
    handleAction(row, command) {
      switch (command) {
        case 'view':
          this.viewDetail(row);
          break;
        case 'pass':
          this.auditPass(row);
          break;
        case 'reject':
          this.openRejectDialog(row);
          break;
        case 'disable':
        case 'enable':
          this.toggleStatus(row);
          break;
      }
    },
    // 查看详情
    viewDetail(row) {
      this.$message.info('岗位详情页开发中');
    },
    // 审核通过
    async auditPass(row) {
      this.$confirm('确定要审核通过该岗位吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await put(`/admin/jobs/${row.id}/audit`, {
            auditStatus: 1
          });
          if (res.code === 200) {
            this.$message.success('审核通过');
            this.getJobList();
          }
        } catch (error) {
          console.error('审核失败:', error);
        }
      }).catch(() => {});
    },
    // 打开拒绝弹窗
    openRejectDialog(row) {
      this.rejectForm.jobId = row.id;
      this.rejectForm.rejectReason = '';
      this.rejectDialogVisible = true;
    },
    // 提交拒绝
    async submitReject() {
      if (!this.rejectForm.rejectReason.trim()) {
        this.$message.warning('请填写拒绝原因');
        return;
      }
      
      this.rejectLoading = true;
      try {
        const res = await put(`/admin/jobs/${this.rejectForm.jobId}/audit`, {
          auditStatus: 3,
          rejectReason: this.rejectForm.rejectReason
        });
        if (res.code === 200) {
          this.$message.success('已拒绝');
          this.rejectDialogVisible = false;
          this.getJobList();
        }
      } catch (error) {
        console.error('审核拒绝失败:', error);
      } finally {
        this.rejectLoading = false;
      }
    },
    // 上架/下架岗位
    async toggleStatus(row) {
      const action = row.status === 1 ? '下架' : '上架';
      this.$confirm(`确定要${action}该岗位吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const newStatus = row.status === 1 ? 2 : 1;
          const res = await put(`/admin/jobs/${row.id}/audit`, {
            auditStatus: row.auditStatus,
            status: newStatus
          });
          if (res.code === 200) {
            this.$message.success(`${action}成功`);
            this.getJobList();
          }
        } catch (error) {
          console.error('操作失败:', error);
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
    // 获取岗位状态类型
    getJobStatusType(status) {
      const typeMap = {
        1: 'success',  // 招聘中
        2: 'info',     // 已下架
        3: 'warning'   // 审核中
      };
      return typeMap[status] || '';
    },
    // 获取岗位状态名称
    getJobStatusName(status) {
      const nameMap = {
        1: '招聘中',
        2: '已下架',
        3: '审核中'
      };
      return nameMap[status] || '未知';
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
.jobs-page {
  min-height: 100%;
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

.filters {
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

.id-text {
  color: #999;
  font-family: monospace;
}

/* 岗位信息 */
.job-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.job-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.job-title {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.salary {
  font-size: 14px;
  color: #fa5555;
  font-weight: bold;
}

.job-meta {
  font-size: 12px;
  color: #666;
}

.job-meta .divider {
  margin: 0 8px;
  color: #e4e7ed;
}

/* 企业信息 */
.company-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo {
  border: 2px solid #f0f0f0;
}

.info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.name {
  font-size: 13px;
  color: #333;
  font-weight: 500;
}

.meta {
  font-size: 12px;
  color: #666;
}

.meta .divider {
  margin: 0 8px;
  color: #e4e7ed;
}

.apply-count {
  color: #409eff;
  font-weight: 500;
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
  
  .filters {
    justify-content: space-between;
  }
  
  .filters .el-select {
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
