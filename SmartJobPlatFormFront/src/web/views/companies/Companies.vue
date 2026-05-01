<template>
  <div class="companies-page">
    <div class="container">
      <div class="action-bar">
        <div class="search-box">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索企业名称"
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
      
      <div class="list-section">
        <el-table 
          v-loading="loading" 
          :data="companyList" 
          style="width: 100%"
          stripe
        >
          <el-table-column prop="id" label="ID" width="80">
            <template slot-scope="scope">
              <span class="id-text">{{ scope.row.id }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="name" label="企业信息" min-width="220">
            <template slot-scope="scope">
              <div class="company-info">
                <el-avatar :size="48" :src="scope.row.logo" class="logo">
                  <i class="el-icon-office-building"></i>
                </el-avatar>
                <div class="info">
                  <span class="name">{{ scope.row.name }}</span>
                  <span class="short-name" v-if="scope.row.shortName">{{ scope.row.shortName }}</span>
                  <div class="meta">
                    <span>{{ scope.row.industry }}</span>
                    <span class="divider">|</span>
                    <span>{{ scope.row.size }}</span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="contactName" label="联系人" width="120">
            <template slot-scope="scope">
              <div class="contact-info">
                <span class="name">{{ scope.row.contactName }}</span>
                <span class="phone">{{ scope.row.contactPhone }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="viewCount" label="浏览量" width="80">
            <template slot-scope="scope">
              <span>{{ scope.row.viewCount }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="jobCount" label="岗位数" width="80">
            <template slot-scope="scope">
              <span class="job-count">{{ scope.row.jobCount }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="auditStatus" label="审核状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getAuditStatusType(scope.row.auditStatus)" size="small">
                {{ getAuditStatusName(scope.row.auditStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="企业状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small" effect="plain">
                {{ scope.row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createTime" label="入驻时间" width="160">
            <template slot-scope="scope">
              <span>{{ formatDate(scope.row.createTime) }}</span>
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
                    {{ scope.row.status === 1 ? '禁用企业' : '启用企业' }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
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
import { get, put } from '@/utils/request';

export default {
  name: 'WebCompanies',
  data() {
    return {
      loading: false,
      companyList: [],
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
        companyId: null
      }
    };
  },
  mounted() {
    this.getCompanyList();
  },
  methods: {
    async getCompanyList() {
      this.loading = true;
      try {
        const res = await get('/admin/companies', this.searchForm);
        if (res.code === 200) {
          this.companyList = res.data.list;
          this.total = res.data.total;
        }
      } catch (error) {
        console.error('获取企业列表失败:', error);
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.searchForm.page = 1;
      this.getCompanyList();
    },
    refreshList() {
      this.getCompanyList();
    },
    handleAction(row, command) {
      switch (command) {
        case 'view':
          this.$message.info('企业详情页开发中');
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
    async auditPass(row) {
      this.$confirm('确定要审核通过该企业吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await put(`/admin/companies/${row.id}/audit`, {
            auditStatus: 1
          });
          if (res.code === 200) {
            this.$message.success('审核通过');
            this.getCompanyList();
          }
        } catch (error) {
          console.error('审核失败:', error);
        }
      }).catch(() => {});
    },
    openRejectDialog(row) {
      this.rejectForm.companyId = row.id;
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
        const res = await put(`/admin/companies/${this.rejectForm.companyId}/audit`, {
          auditStatus: 3,
          rejectReason: this.rejectForm.rejectReason
        });
        if (res.code === 200) {
          this.$message.success('已拒绝');
          this.rejectDialogVisible = false;
          this.getCompanyList();
        }
      } catch (error) {
        console.error('审核拒绝失败:', error);
      } finally {
        this.rejectLoading = false;
      }
    },
    async toggleStatus(row) {
      const action = row.status === 1 ? '禁用' : '启用';
      this.$confirm(`确定要${action}该企业吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const newStatus = row.status === 1 ? 2 : 1;
          const res = await put(`/admin/companies/${row.id}/status`, { status: newStatus });
          if (res.code === 200) {
            this.$message.success(`${action}成功`);
            this.getCompanyList();
          }
        } catch (error) {
          console.error('操作失败:', error);
        }
      }).catch(() => {});
    },
    handleSizeChange(val) {
      this.searchForm.pageSize = val;
      this.getCompanyList();
    },
    handleCurrentChange(val) {
      this.searchForm.page = val;
      this.getCompanyList();
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
.companies-page {
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

.company-info {
  display: flex;
  align-items: center;
  gap: 12px;
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
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.short-name {
  font-size: 12px;
  color: #999;
}

.meta {
  font-size: 12px;
  color: #666;
}

.meta .divider {
  margin: 0 8px;
  color: #e4e7ed;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.contact-info .name {
  font-size: 13px;
  color: #333;
}

.contact-info .phone {
  font-size: 12px;
  color: #666;
}

.job-count {
  color: #409eff;
  font-weight: 500;
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
