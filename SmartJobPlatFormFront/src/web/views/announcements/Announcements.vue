<template>
  <div class="announcements-page">
    <div class="container">
      <div class="action-bar">
        <div class="search-box">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索公告标题"
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
            v-model="searchForm.type" 
            placeholder="公告类型" 
            clearable
            style="width: 120px; margin-right: 10px"
            @change="handleSearch"
          >
            <el-option label="系统公告" :value="1"></el-option>
            <el-option label="活动公告" :value="2"></el-option>
            <el-option label="其他" :value="3"></el-option>
          </el-select>
          <el-select 
            v-model="searchForm.status" 
            placeholder="状态" 
            clearable
            style="width: 100px; margin-right: 10px"
            @change="handleSearch"
          >
            <el-option label="已发布" :value="1"></el-option>
            <el-option label="草稿" :value="2"></el-option>
          </el-select>
          <el-button type="primary" @click="openAddDialog">
            <i class="el-icon-plus"></i> 新建公告
          </el-button>
        </div>
      </div>
      
      <div class="list-section">
        <el-table 
          v-loading="loading" 
          :data="announcementList" 
          style="width: 100%"
          stripe
        >
          <el-table-column prop="id" label="ID" width="80">
            <template slot-scope="scope">
              <span class="id-text">{{ scope.row.id }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="title" label="公告标题" min-width="250">
            <template slot-scope="scope">
              <div class="title-info">
                <span class="title">{{ scope.row.title }}</span>
                <el-tag 
                  v-if="scope.row.isTop" 
                  type="danger" 
                  size="mini"
                  effect="plain"
                >
                  置顶
                </el-tag>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="type" label="类型" width="100">
            <template slot-scope="scope">
              <el-tag :type="getTypeType(scope.row.type)" size="small">
                {{ getTypeName(scope.row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
                {{ scope.row.status === 1 ? '已发布' : '草稿' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="viewCount" label="浏览量" width="80">
            <template slot-scope="scope">
              <span>{{ scope.row.viewCount }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="publishTime" label="发布时间" width="160">
            <template slot-scope="scope">
              <span>{{ formatDate(scope.row.publishTime) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="viewDetail(scope.row)">
                <i class="el-icon-view"></i> 查看
              </el-button>
              <el-button type="text" size="small" @click="editAnnouncement(scope.row)">
                <i class="el-icon-edit"></i> 编辑
              </el-button>
              <el-dropdown @command="(command) => handleAction(scope.row, command)" size="small">
                <el-button type="text" size="small">
                  更多 <i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item :command="scope.row.status === 1 ? 'offline' : 'publish'">
                    <i :class="scope.row.status === 1 ? 'el-icon-circle-close' : 'el-icon-circle-check'"></i>
                    {{ scope.row.status === 1 ? '下架公告' : '发布公告' }}
                  </el-dropdown-item>
                  <el-dropdown-item 
                    :command="scope.row.isTop ? 'untop' : 'top'"
                    divided
                  >
                    <i :class="scope.row.isTop ? 'el-icon-arrow-down' : 'el-icon-arrow-up'"></i>
                    {{ scope.row.isTop ? '取消置顶' : '置顶公告' }}
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    <i class="el-icon-delete" style="color: #f56c6c"></i> 删除公告
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
      
      <el-dialog 
        :title="isEdit ? '编辑公告' : '新建公告'" 
        :visible.sync="dialogVisible" 
        width="700px"
        :close-on-click-modal="false"
      >
        <el-form 
          ref="announcementForm" 
          :model="announcementForm" 
          :rules="announcementRules" 
          label-width="100px"
        >
          <el-form-item label="公告标题" prop="title">
            <el-input 
              v-model="announcementForm.title" 
              placeholder="请输入公告标题"
              maxlength="100"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item label="公告类型" prop="type">
            <el-radio-group v-model="announcementForm.type">
              <el-radio :label="1">系统公告</el-radio>
              <el-radio :label="2">活动公告</el-radio>
              <el-radio :label="3">其他</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="公告内容" prop="content">
            <el-input
              v-model="announcementForm.content"
              type="textarea"
              :rows="8"
              placeholder="请输入公告内容"
              maxlength="2000"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item label="是否置顶" prop="isTop">
            <el-switch
              v-model="announcementForm.isTop"
              active-text="是"
              inactive-text="否"
            ></el-switch>
          </el-form-item>
          
          <el-form-item label="发布状态" prop="status">
            <el-radio-group v-model="announcementForm.status">
              <el-radio :label="1">立即发布</el-radio>
              <el-radio :label="2">保存草稿</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAnnouncement" :loading="submitting">
            确认提交
          </el-button>
        </span>
      </el-dialog>
      
      <el-dialog 
        title="公告详情" 
        :visible.sync="detailDialogVisible" 
        width="600px"
      >
        <div class="announcement-detail" v-if="currentAnnouncement">
          <div class="detail-header">
            <h3 class="detail-title">{{ currentAnnouncement.title }}</h3>
            <div class="detail-meta">
              <el-tag :type="getTypeType(currentAnnouncement.type)" size="small">
                {{ getTypeName(currentAnnouncement.type) }}
              </el-tag>
              <span class="meta-info">
                <i class="el-icon-view"></i> {{ currentAnnouncement.viewCount }} 浏览
              </span>
              <span class="meta-info">
                <i class="el-icon-time"></i> {{ formatDate(currentAnnouncement.publishTime) }}
              </span>
            </div>
          </div>
          <div class="detail-content">
            <p>{{ currentAnnouncement.content }}</p>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { get, post, put, del } from '@/utils/request';

export default {
  name: 'WebAnnouncements',
  data() {
    return {
      loading: false,
      announcementList: [],
      total: 0,
      searchForm: {
        keyword: '',
        type: '',
        status: '',
        page: 1,
        pageSize: 10
      },
      isEdit: false,
      submitting: false,
      dialogVisible: false,
      detailDialogVisible: false,
      currentAnnouncement: null,
      announcementForm: {
        id: null,
        title: '',
        type: 1,
        content: '',
        isTop: false,
        status: 1
      },
      announcementRules: {
        title: [
          { required: true, message: '请输入公告标题', trigger: 'blur' },
          { min: 2, max: 100, message: '标题长度为2-100个字符', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择公告类型', trigger: 'change' }
        ],
        content: [
          { required: true, message: '请输入公告内容', trigger: 'blur' },
          { min: 10, message: '公告内容不能少于10个字符', trigger: 'blur' }
        ]
      }
    };
  },
  mounted() {
    this.getAnnouncementList();
  },
  methods: {
    async getAnnouncementList() {
      this.loading = true;
      try {
        const res = await get('/admin/announcements', this.searchForm);
        if (res.code === 200) {
          this.announcementList = res.data.list;
          this.total = res.data.total;
        }
      } catch (error) {
        console.error('获取公告列表失败:', error);
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.searchForm.page = 1;
      this.getAnnouncementList();
    },
    openAddDialog() {
      this.isEdit = false;
      this.announcementForm = {
        id: null,
        title: '',
        type: 1,
        content: '',
        isTop: false,
        status: 1
      };
      this.dialogVisible = true;
    },
    editAnnouncement(row) {
      this.isEdit = true;
      this.announcementForm = {
        id: row.id,
        title: row.title,
        type: row.type,
        content: row.content,
        isTop: row.isTop,
        status: row.status
      };
      this.dialogVisible = true;
    },
    viewDetail(row) {
      this.currentAnnouncement = row;
      this.detailDialogVisible = true;
    },
    async handleAction(row, command) {
      switch (command) {
        case 'publish':
        case 'offline':
          await this.toggleStatus(row);
          break;
        case 'top':
        case 'untop':
          await this.toggleTop(row);
          break;
        case 'delete':
          await this.deleteAnnouncement(row);
          break;
      }
    },
    async toggleStatus(row) {
      const action = row.status === 1 ? '下架' : '发布';
      this.$confirm(`确定要${action}该公告吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const newStatus = row.status === 1 ? 2 : 1;
          const res = await put(`/admin/announcements/${row.id}/status`, { status: newStatus });
          if (res.code === 200) {
            this.$message.success(`${action}成功`);
            this.getAnnouncementList();
          }
        } catch (error) {
          console.error('操作失败:', error);
        }
      }).catch(() => {});
    },
    async toggleTop(row) {
      const action = row.isTop ? '取消置顶' : '置顶';
      this.$confirm(`确定要${action}该公告吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const isTop = !row.isTop;
          const res = await put(`/admin/announcements/${row.id}/status`, { isTop });
          if (res.code === 200) {
            this.$message.success(`${action}成功`);
            this.getAnnouncementList();
          }
        } catch (error) {
          console.error('操作失败:', error);
        }
      }).catch(() => {});
    },
    async deleteAnnouncement(row) {
      this.$confirm('确定要删除该公告吗？删除后无法恢复。', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await del(`/admin/announcements/${row.id}`);
          if (res.code === 200) {
            this.$message.success('删除成功');
            this.getAnnouncementList();
          }
        } catch (error) {
          console.error('删除失败:', error);
        }
      }).catch(() => {});
    },
    async submitAnnouncement() {
      this.$refs.announcementForm.validate(async (valid) => {
        if (valid) {
          this.submitting = true;
          try {
            let res;
            if (this.isEdit) {
              res = await put(`/admin/announcements/${this.announcementForm.id}`, this.announcementForm);
            } else {
              res = await post('/admin/announcements', this.announcementForm);
            }
            
            if (res.code === 200) {
              this.$message.success(this.isEdit ? '编辑成功' : '发布成功');
              this.dialogVisible = false;
              this.getAnnouncementList();
            }
          } catch (error) {
            console.error('提交失败:', error);
          } finally {
            this.submitting = false;
          }
        }
      });
    },
    handleSizeChange(val) {
      this.searchForm.pageSize = val;
      this.getAnnouncementList();
    },
    handleCurrentChange(val) {
      this.searchForm.page = val;
      this.getAnnouncementList();
    },
    getTypeType(type) {
      const typeMap = {
        1: 'primary',
        2: 'success',
        3: 'info'
      };
      return typeMap[type] || '';
    },
    getTypeName(type) {
      const nameMap = {
        1: '系统公告',
        2: '活动公告',
        3: '其他'
      };
      return nameMap[type] || '未知';
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
.announcements-page {
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

.actions {
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

.title-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.announcement-detail {
  padding: 10px;
}

.detail-header {
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.detail-title {
  font-size: 20px;
  color: #333;
  margin: 0 0 15px 0;
  font-weight: 600;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #999;
}

.meta-info i {
  color: #999;
}

.detail-content {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
}

.detail-content p {
  margin: 0;
  text-indent: 2em;
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
  
  .actions {
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .actions .el-select {
    flex: 1;
    min-width: 100px;
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
