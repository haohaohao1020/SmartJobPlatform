<template>
  <div class="users-page">
    <div class="container">
      <!-- 顶部操作栏 -->
      <div class="action-bar">
        <div class="search-box">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索用户名、手机号"
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
          <el-button type="primary" @click="refreshList">
            <i class="el-icon-refresh"></i> 刷新
          </el-button>
        </div>
      </div>
      
      <!-- 用户列表 -->
      <div class="list-section">
        <el-table 
          v-loading="loading" 
          :data="userList" 
          style="width: 100%"
          stripe
        >
          <el-table-column prop="id" label="ID" width="80">
            <template slot-scope="scope">
              <span class="id-text">{{ scope.row.id }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="name" label="用户名" min-width="150">
            <template slot-scope="scope">
              <div class="user-info">
                <el-avatar :size="36" :src="scope.row.avatar" class="avatar">
                  <i class="el-icon-user-solid"></i>
                </el-avatar>
                <div class="info">
                  <span class="name">{{ scope.row.name }}</span>
                  <el-tag :type="getRoleType(scope.row.role)" size="mini">
                    {{ getRoleName(scope.row.role) }}
                  </el-tag>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="phone" label="手机号" width="130">
            <template slot-scope="scope">
              <span>{{ scope.row.phone }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="email" label="邮箱" min-width="180">
            <template slot-scope="scope">
              <span>{{ scope.row.email || '未填写' }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
                {{ scope.row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createTime" label="注册时间" width="160">
            <template slot-scope="scope">
              <span>{{ formatDate(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="120" fixed="right">
            <template slot-scope="scope">
              <el-button 
                :type="scope.row.status === 1 ? 'danger' : 'success'"
                size="small"
                text
                @click="toggleStatus(scope.row)"
              >
                <i :class="scope.row.status === 1 ? 'el-icon-circle-close' : 'el-icon-circle-check'"></i>
                {{ scope.row.status === 1 ? '禁用' : '启用' }}
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
import { get, put } from '@/utils/request';

export default {
  name: 'AdminUsers',
  data() {
    return {
      loading: false,
      userList: [],
      total: 0,
      searchForm: {
        keyword: '',
        page: 1,
        pageSize: 10
      }
    };
  },
  mounted() {
    this.getUserList();
  },
  methods: {
    // 获取用户列表
    async getUserList() {
      this.loading = true;
      try {
        const res = await get('/admin/users', this.searchForm);
        if (res.code === 200) {
          this.userList = res.data.list;
          this.total = res.data.total;
        }
      } catch (error) {
        console.error('获取用户列表失败:', error);
      } finally {
        this.loading = false;
      }
    },
    // 搜索
    handleSearch() {
      this.searchForm.page = 1;
      this.getUserList();
    },
    // 刷新列表
    refreshList() {
      this.getUserList();
    },
    // 启用/禁用用户
    async toggleStatus(row) {
      const action = row.status === 1 ? '禁用' : '启用';
      this.$confirm(`确定要${action}该用户吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const newStatus = row.status === 1 ? 2 : 1;
          const res = await put(`/admin/users/${row.id}/status`, { status: newStatus });
          if (res.code === 200) {
            this.$message.success(`${action}成功`);
            this.getUserList();
          }
        } catch (error) {
          console.error('操作失败:', error);
        }
      }).catch(() => {});
    },
    // 分页大小变化
    handleSizeChange(val) {
      this.searchForm.pageSize = val;
      this.getUserList();
    },
    // 当前页变化
    handleCurrentChange(val) {
      this.searchForm.page = val;
      this.getUserList();
    },
    // 获取角色类型
    getRoleType(role) {
      const typeMap = {
        jobseeker: 'primary',
        hr: 'success',
        admin: 'warning'
      };
      return typeMap[role] || '';
    },
    // 获取角色名称
    getRoleName(role) {
      const nameMap = {
        jobseeker: '求职者',
        hr: 'HR',
        admin: '管理员'
      };
      return nameMap[role] || '未知';
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
.users-page {
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
