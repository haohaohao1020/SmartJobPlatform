<template>
  <div class="job-publish-page">
    <div class="container">
      <!-- 顶部操作栏 -->
      <div class="action-bar">
        <div class="page-title">
          <h2>{{ isEdit ? '编辑岗位' : '发布新岗位' }}</h2>
          <span class="tip">完善岗位信息，吸引更多优秀人才</span>
        </div>
        <div class="actions">
          <el-button @click="goBack">
            <i class="el-icon-arrow-left"></i> 返回列表
          </el-button>
        </div>
      </div>
      
      <!-- 岗位表单 -->
      <el-form 
        ref="jobForm" 
        :model="jobForm" 
        :rules="jobRules" 
        label-width="120px"
        class="job-form"
      >
        <!-- 基本信息 -->
        <div class="form-section">
          <div class="section-header">
            <h3><i class="el-icon-info"></i> 基本信息</h3>
          </div>
          
          <el-form-item label="岗位名称" prop="title">
            <el-input 
              v-model="jobForm.title" 
              placeholder="请输入岗位名称，如：Java开发工程师"
              maxlength="50"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <div class="form-row">
            <el-form-item label="工作城市" prop="city">
              <el-select v-model="jobForm.city" placeholder="请选择工作城市" style="width: 100%">
                <el-option label="北京" value="北京"></el-option>
                <el-option label="上海" value="上海"></el-option>
                <el-option label="广州" value="广州"></el-option>
                <el-option label="深圳" value="深圳"></el-option>
                <el-option label="杭州" value="杭州"></el-option>
                <el-option label="南京" value="南京"></el-option>
                <el-option label="成都" value="成都"></el-option>
                <el-option label="武汉" value="武汉"></el-option>
                <el-option label="西安" value="西安"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="工作类型" prop="jobType">
              <el-select v-model="jobForm.jobType" placeholder="请选择工作类型" style="width: 100%">
                <el-option label="全职" value="全职"></el-option>
                <el-option label="兼职" value="兼职"></el-option>
                <el-option label="实习" value="实习"></el-option>
              </el-select>
            </el-form-item>
          </div>
          
          <div class="form-row">
            <el-form-item label="工作经验" prop="workYears">
              <el-select v-model="jobForm.workYears" placeholder="请选择工作经验要求" style="width: 100%">
                <el-option label="应届毕业生" value="应届毕业生"></el-option>
                <el-option label="1-3年" value="1-3年"></el-option>
                <el-option label="3-5年" value="3-5年"></el-option>
                <el-option label="5-10年" value="5-10年"></el-option>
                <el-option label="10年以上" value="10年以上"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="学历要求" prop="education">
              <el-select v-model="jobForm.education" placeholder="请选择学历要求" style="width: 100%">
                <el-option label="不限" value="不限"></el-option>
                <el-option label="大专" value="大专"></el-option>
                <el-option label="本科" value="本科"></el-option>
                <el-option label="硕士" value="硕士"></el-option>
                <el-option label="博士" value="博士"></el-option>
              </el-select>
            </el-form-item>
          </div>
          
          <div class="form-row">
            <el-form-item label="薪资范围" prop="salaryMin">
              <div class="salary-input">
                <el-input-number 
                  v-model="jobForm.salaryMin" 
                  :min="0"
                  :max="100000"
                  :step="1000"
                  placeholder="最低薪资"
                ></el-input-number>
                <span class="divider">-</span>
                <el-input-number 
                  v-model="jobForm.salaryMax" 
                  :min="0"
                  :max="100000"
                  :step="1000"
                  placeholder="最高薪资"
                ></el-input-number>
                <span class="unit">K/月</span>
              </div>
            </el-form-item>
          </div>
        </div>
        
        <!-- 岗位描述 -->
        <div class="form-section">
          <div class="section-header">
            <h3><i class="el-icon-document"></i> 岗位描述</h3>
          </div>
          
          <el-form-item label="岗位职责" prop="description">
            <el-input
              v-model="jobForm.description"
              type="textarea"
              :rows="6"
              placeholder="请详细描述岗位职责，例如：&#10;1、负责公司产品的开发与维护&#10;2、参与需求分析与技术方案设计&#10;3、与产品、测试等团队协作完成项目交付"
              maxlength="2000"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item label="任职要求" prop="requirements">
            <el-input
              v-model="jobForm.requirements"
              type="textarea"
              :rows="5"
              placeholder="请详细描述任职要求，例如：&#10;1、本科及以上学历，计算机相关专业&#10;2、3年以上相关工作经验&#10;3、熟悉Java语言，熟悉Spring Boot等框架"
              maxlength="2000"
              show-word-limit
            ></el-input>
          </el-form-item>
        </div>
        
        <!-- 福利待遇 -->
        <div class="form-section">
          <div class="section-header">
            <h3><i class="el-icon-present"></i> 福利待遇</h3>
          </div>
          
          <el-form-item label="福利待遇">
            <el-select 
              v-model="jobForm.welfare" 
              multiple 
              collapse-tags
              placeholder="请选择公司提供的福利待遇（可多选）"
              style="width: 100%"
            >
              <el-option label="五险一金" value="五险一金"></el-option>
              <el-option label="年终奖" value="年终奖"></el-option>
              <el-option label="带薪年假" value="带薪年假"></el-option>
              <el-option label="弹性工作" value="弹性工作"></el-option>
              <el-option label="定期团建" value="定期团建"></el-option>
              <el-option label="员工旅游" value="员工旅游"></el-option>
              <el-option label="节日福利" value="节日福利"></el-option>
              <el-option label="下午茶" value="下午茶"></el-option>
              <el-option label="零食下午茶" value="零食下午茶"></el-option>
              <el-option label="股票期权" value="股票期权"></el-option>
              <el-option label="绩效奖金" value="绩效奖金"></el-option>
              <el-option label="加班补助" value="加班补助"></el-option>
              <el-option label="通讯补贴" value="通讯补贴"></el-option>
              <el-option label="交通补贴" value="交通补贴"></el-option>
              <el-option label="餐补" value="餐补"></el-option>
            </el-select>
            <div class="form-tip">
              <i class="el-icon-info"></i>
              选择有吸引力的福利待遇，有助于吸引更多优秀人才
            </div>
          </el-form-item>
        </div>
        
        <!-- 提交按钮 -->
        <div class="submit-section">
          <el-button type="primary" size="large" :loading="publishing" @click="submitForm">
            <i class="el-icon-check"></i>
            {{ isEdit ? '保存修改' : '发布岗位' }}
          </el-button>
          <el-button size="large" @click="resetForm">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
          <el-button size="large" @click="goBack">
            <i class="el-icon-arrow-left"></i> 取消
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { get, post, put } from '@/utils/request';

export default {
  name: 'JobPublish',
  data() {
    return {
      isEdit: false,
      jobId: null,
      publishing: false,
      jobForm: {
        title: '',
        city: '',
        jobType: '全职',
        workYears: '',
        education: '',
        salaryMin: 10000,
        salaryMax: 20000,
        description: '',
        requirements: '',
        welfare: []
      },
      jobRules: {
        title: [
          { required: true, message: '请输入岗位名称', trigger: 'blur' },
          { min: 2, max: 50, message: '岗位名称长度为2-50个字符', trigger: 'blur' }
        ],
        city: [
          { required: true, message: '请选择工作城市', trigger: 'change' }
        ],
        jobType: [
          { required: true, message: '请选择工作类型', trigger: 'change' }
        ],
        workYears: [
          { required: true, message: '请选择工作经验要求', trigger: 'change' }
        ],
        education: [
          { required: true, message: '请选择学历要求', trigger: 'change' }
        ],
        description: [
          { required: true, message: '请输入岗位职责', trigger: 'blur' },
          { min: 20, message: '岗位职责描述不能少于20个字符', trigger: 'blur' }
        ],
        requirements: [
          { required: true, message: '请输入任职要求', trigger: 'blur' },
          { min: 20, message: '任职要求描述不能少于20个字符', trigger: 'blur' }
        ]
      }
    };
  },
  mounted() {
    // 检查是否是编辑模式
    const id = this.$route.params.id;
    if (id) {
      this.isEdit = true;
      this.jobId = id;
      this.getJobDetail(id);
    }
  },
  methods: {
    // 获取岗位详情
    async getJobDetail(id) {
      try {
        const res = await get(`/jobs/${id}`);
        if (res.code === 200) {
          // 填充表单数据
          this.jobForm = {
            title: res.data.title || '',
            city: res.data.city || '',
            jobType: res.data.jobType || '全职',
            workYears: res.data.workYears || '',
            education: res.data.education || '',
            salaryMin: res.data.salaryMin || 10000,
            salaryMax: res.data.salaryMax || 20000,
            description: res.data.description || '',
            requirements: res.data.requirements || '',
            welfare: res.data.welfare || []
          };
        }
      } catch (error) {
        console.error('获取岗位详情失败:', error);
      }
    },
    // 提交表单
    async submitForm() {
      this.$refs.jobForm.validate(async (valid) => {
        if (valid) {
          this.publishing = true;
          try {
            let res;
            if (this.isEdit) {
              // 编辑模式
              res = await put(`/hr/jobs/${this.jobId}`, this.jobForm);
            } else {
              // 新增模式
              res = await post('/hr/jobs', this.jobForm);
            }
            
            if (res.code === 200) {
              this.$message.success(this.isEdit ? '修改成功' : '发布成功，等待审核');
              // 返回列表页
              setTimeout(() => {
                this.$router.push('/hr/jobs');
              }, 1000);
            }
          } catch (error) {
            console.error('操作失败:', error);
          } finally {
            this.publishing = false;
          }
        }
      });
    },
    // 重置表单
    resetForm() {
      this.$refs.jobForm.resetFields();
    },
    // 返回列表
    goBack() {
      this.$router.push('/hr/jobs');
    }
  }
};
</script>

<style scoped>
.job-publish-page {
  min-height: 100%;
  background-color: #f5f7fa;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

/* 顶部操作栏 */
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.page-title h2 {
  font-size: 20px;
  color: #333;
  margin: 0 0 5px 0;
}

.page-title .tip {
  font-size: 13px;
  color: #999;
}

/* 表单样式 */
.job-form {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.form-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.form-section:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.section-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.section-header h3 {
  font-size: 16px;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
}

.section-header h3 i {
  margin-right: 8px;
  color: #409eff;
}

.form-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.form-row .el-form-item {
  flex: 1;
  min-width: 300px;
}

/* 薪资输入 */
.salary-input {
  display: flex;
  align-items: center;
  gap: 10px;
}

.salary-input .divider {
  color: #999;
  font-size: 16px;
}

.salary-input .unit {
  color: #666;
  font-size: 14px;
}

/* 表单提示 */
.form-tip {
  margin-top: 10px;
  font-size: 13px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 5px;
}

.form-tip i {
  color: #409eff;
}

/* 提交区域 */
.submit-section {
  display: flex;
  justify-content: flex-start;
  gap: 15px;
  padding-top: 20px;
  margin-top: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式 */
@media (max-width: 768px) {
  .container {
    padding: 15px;
  }
  
  .action-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
    padding: 15px;
  }
  
  .job-form {
    padding: 15px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .form-row .el-form-item {
    min-width: auto;
    width: 100%;
  }
  
  .salary-input {
    flex-wrap: wrap;
  }
  
  .submit-section {
    flex-direction: column;
  }
  
  .submit-section .el-button {
    width: 100%;
  }
}
</style>
