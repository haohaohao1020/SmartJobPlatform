<template>
  <div class="resumes-page">
    <div class="container">
      <!-- 顶部操作栏 -->
      <div class="action-bar">
        <div class="search-box">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索求职者姓名、手机号"
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
            v-model="searchForm.interviewStatus" 
            placeholder="面试状态" 
            clearable
            style="width: 150px; margin-right: 10px"
            @change="handleSearch"
          >
            <el-option label="待查看" :value="1"></el-option>
            <el-option label="待面试" :value="2"></el-option>
            <el-option label="面试中" :value="3"></el-option>
            <el-option label="待Offer" :value="4"></el-option>
            <el-option label="已拒绝" :value="5"></el-option>
            <el-option label="已录用" :value="6"></el-option>
          </el-select>
          <el-button type="primary" @click="refreshList">
            <i class="el-icon-refresh"></i> 刷新
          </el-button>
        </div>
      </div>
      
      <!-- 简历列表 -->
      <div class="list-section">
        <el-table 
          v-loading="loading" 
          :data="resumeList" 
          style="width: 100%"
          stripe
          @row-click="handleRowClick"
        >
          <el-table-column prop="userName" label="求职者" width="180">
            <template slot-scope="scope">
              <div class="user-info">
                <el-avatar :size="40" :src="scope.row.avatar" class="avatar">
                  <i class="el-icon-user-solid"></i>
                </el-avatar>
                <div class="info">
                  <span class="name">{{ scope.row.userName }}</span>
                  <span class="meta">
                    {{ scope.row.gender === 1 ? '男' : '女' }} | 
                    {{ scope.row.age }}岁 | 
                    {{ scope.row.highestEducation }}
                  </span>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="jobTitle" label="应聘岗位" min-width="180">
            <template slot-scope="scope">
              <div class="job-info">
                <span class="job-title">{{ scope.row.jobTitle }}</span>
                <span class="salary">{{ formatSalary(scope.row) }}</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="phone" label="联系方式" width="140">
            <template slot-scope="scope">
              <div class="contact-info">
                <div class="phone">
                  <i class="el-icon-mobile-phone"></i> {{ scope.row.phone }}
                </div>
                <div class="email" v-if="scope.row.email">
                  <i class="el-icon-message"></i> {{ scope.row.email }}
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="interviewStatus" label="面试状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.interviewStatus)" size="small">
                {{ scope.row.interviewStatusName }}
              </el-tag>
              <el-tag 
                v-if="!scope.row.isRead" 
                type="danger" 
                size="mini" 
                effect="plain"
                style="margin-left: 5px"
              >
                新
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="applyTime" label="投递时间" width="160">
            <template slot-scope="scope">
              <span>{{ formatDate(scope.row.applyTime) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="220" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click.stop="goToDetail(scope.row)">
                <i class="el-icon-view"></i> 查看简历
              </el-button>
              <el-dropdown @command="(command) => handleStatusChange(scope.row, command)" size="small">
                <el-button type="text" size="small">
                  操作 <i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item :command="2" v-if="scope.row.interviewStatus === 1">
                    标记为待面试
                  </el-dropdown-item>
                  <el-dropdown-item :command="3" v-if="scope.row.interviewStatus === 2">
                    标记为面试中
                  </el-dropdown-item>
                  <el-dropdown-item :command="4" v-if="scope.row.interviewStatus === 3">
                    标记为待Offer
                  </el-dropdown-item>
                  <el-dropdown-item :command="6" v-if="scope.row.interviewStatus === 4">
                    标记为已录用
                  </el-dropdown-item>
                  <el-dropdown-item :command="5" divided>
                    标记为已拒绝
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
    </div>
  </div>
</template>

<script>
import { get, put } from '@/utils/request';

export default {
  name: 'HRResumes',
  data() {
    return {
      loading: false,
      resumeList: [],
      total: 0,
      searchForm: {
        keyword: '',
        interviewStatus: '',
        page: 1,
        pageSize: 10
      }
    };
  },
  mounted() {
    this.getResumeList();
  },
  methods: {
    // 获取简历列表
    async getResumeList() {
      this.loading = true;
      try {
        const res = await get('/hr/resumes', this.searchForm);
        if (res.code === 200) {
          this.resumeList = res.data.list;
          this.total = res.data.total;
        }
      } catch (error) {
        console.error('获取简历列表失败:', error);
      } finally {
        this.loading = false;
      }
    },
    // 搜索
    handleSearch() {
      this.searchForm.page = 1;
      this.getResumeList();
    },
    // 刷新列表
    refreshList() {
      this.getResumeList();
    },
    // 行点击
    handleRowClick(row) {
      // 标记为已读
      if (!row.isRead) {
        this.markAsRead(row);
      }
    },
    // 标记为已读
    async markAsRead(row) {
      try {
        await put(`/hr/resumes/${row.id}/read`);
        row.isRead = true;
      } catch (error) {
        console.error('标记已读失败:', error);
      }
    },
    // 查看简历详情
    goToDetail(row) {
      this.$router.push(`/hr/resumes/${row.resumeId}`);
    },
    // 状态变更
    async handleStatusChange(row, status) {
      const statusMap = {
        2: '待面试',
        3: '面试中',
        4: '待Offer',
        5: '已拒绝',
        6: '已录用'
      };
      
      this.$confirm(`确定要将该简历状态改为「${statusMap[status]}」吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await put(`/hr/resumes/${row.id}/status`, { interviewStatus: status });
          if (res.code === 200) {
            this.$message.success('状态更新成功');
            this.getResumeList();
          }
        } catch (error) {
          console.error('状态更新失败:', error);
        }
      }).catch(() => {});
    },
    // 分页大小变化
    handleSizeChange(val) {
      this.searchForm.pageSize = val;
      this.getResumeList();
    },
    // 当前页变化
    handleCurrentChange(val) {
      this.searchForm.page = val;
      this.getResumeList();
    },
    // 获取状态类型
    getStatusType(status) {
      const typeMap = {
        1: '',      // 待查看
        2: 'warning', // 待面试
        3: 'warning', // 面试中
        4: 'primary', // 待Offer
        5: 'danger',  // 已拒绝
        6: 'success'  // 已录用
      };
      return typeMap[status] || '';
    },
    // 格式化薪资
    formatSalary(row) {
      if (!row.salaryMin || !row.salaryMax) return '面议';
      return `${(row.salaryMin / 1000).toFixed(0)}-${(row.salaryMax / 1000).toFixed(0)}K/月`;
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
.resumes-page {
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

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  border: 2px solid #f0f0f0;
}

.info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.meta {
  font-size: 12px;
  color: #999;
}

/* 岗位信息 */
.job-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  text-align: left;
}

.job-title {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.salary {
  font-size: 12px;
  color: #fa5555;
  font-weight: 500;
}

/* 联系方式 */
.contact-info {
  text-align: left;
}

.contact-info .phone,
.contact-info .email {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #666;
  margin-bottom: 2px;
}

.contact-info i {
  color: #999;
  font-size: 14px;
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
