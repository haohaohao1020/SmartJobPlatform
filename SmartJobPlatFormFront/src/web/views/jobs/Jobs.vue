<template>
  <div class="jobs-page">
    <div class="container">
      <div class="action-bar">
        <div class="search-box">
          <el-input
            v-model="searchForm.keyword"
            :placeholder="userStore.isAdmin() ? '搜索岗位名称、公司名称' : '搜索岗位名称'"
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
            v-if="userStore.isAdmin()"
            v-model="searchForm.auditStatus" 
            placeholder="审核状态" 
            clearable
            style="width: 120px; margin-right: 10px"
            @change="handleSearch"
          >
            <el-option label="待审核" :value="2"></el-option>
            <el-option label="已通过" :value="1"></el-option>
            <el-option label="已拒绝" :value="3"></el-option>
          </el-select>
          <el-select 
            v-if="userStore.isHR()"
            v-model="searchForm.status" 
            placeholder="岗位状态" 
            clearable
            style="width: 120px; margin-right: 10px"
            @change="handleSearch"
          >
            <el-option label="招聘中" :value="1"></el-option>
            <el-option label="已下架" :value="2"></el-option>
            <el-option label="审核中" :value="3"></el-option>
          </el-select>
          <el-button 
            v-if="userStore.isHR()"
            type="primary" 
            @click="goToPublish"
          >
            <i class="el-icon-plus"></i> 发布新岗位
          </el-button>
          <el-button type="primary" @click="refreshList">
            <i class="el-icon-refresh"></i> 刷新
          </el-button>
        </div>
      </div>
      
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
          
          <el-table-column prop="companyName" label="企业信息" min-width="180" v-if="userStore.isAdmin()">
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
          
          <el-table-column prop="status" label="岗位状态" width="100" v-if="userStore.isHR()">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ getStatusName(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="auditStatus" label="审核状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getAuditStatusType(scope.row.auditStatus)" size="small">
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
          
          <el-table-column label="操作" width="220" fixed="right">
            <template slot-scope="scope">
              <template v-if="userStore.isHR()">
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
              <template v-else>
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
            </template>
          </el-table-column>
        </el-table>
        
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
import { get, put, del } from '@/utils/request';
import { useUserStore } from '@/web/stores/user';

export default {
  name: 'WebJobs',
  data() {
    return {
      loading: false,
      jobList: [],
      total: 0,
      searchForm: {
        keyword: '',
        status: '',
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
  computed: {
    userStore() {
      return useUserStore();
    }
  },
  mounted() {
    this.getJobList();
  },
  methods: {
    async getJobList() {
      this.loading = true;
      try {
        const url = this.userStore.isAdmin() ? '/admin/jobs' : '/hr/jobs';
        const res = await get(url, this.searchForm);
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
    handleSearch() {
      this.searchForm.page = 1;
      this.getJobList();
    },
    refreshList() {
      this.getJobList();
    },
    goToPublish() {
      this.$router.push('/jobs/publish');
    },
    goToEdit(id) {
      this.$router.push(`/jobs/edit/${id}`);
    },
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
    handleAction(row, command) {
      switch (command) {
        case 'view':
          this.$message.info('岗位详情页开发中');
          break;
        case 'pass':
          this.auditPass(row);
          break;
        case 'reject':
          this.openRejectDialog(row);
          break;
        case 'disable':
        case 'enable':
          this.toggleJobStatus(row);
          break;
      }
    },
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
    openRejectDialog(row) {
      this.rejectForm.jobId = row.id;
      this.rejectForm.rejectReason = '';
      this.rejectDialogVisible = true;
    },
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
    async toggleJobStatus(row) {
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
    getStatusType(status) {
      const typeMap = {
        1: 'success',
        2: 'info',
        3: 'warning'
      };
      return typeMap[status] || '';
    },
    getStatusName(status) {
      const nameMap = {
        1: '招聘中',
        2: '已下架',
        3: '审核中'
      };
      return nameMap[status] || '未知';
    },
    getAuditStatusType(status) {
      const typeMap = {
        1: 'success',
        2: 'warning',
        3: 'danger'
      };
      return typeMap[status] || '';
    },
    getAuditStatusName(status) {
      const nameMap = {
        1: '已通过',
        2: '待审核',
        3: '已拒绝'
      };
      return nameMap[status] || '未知';
    },
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
  gap: 10px;
}

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

.danger-btn {
  color: #f56c6c !important;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

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
    flex-wrap: wrap;
  }
  
  .filters .el-select {
    flex: 1;
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
