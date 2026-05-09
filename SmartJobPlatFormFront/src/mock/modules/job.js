import Mock from 'mockjs';

/**
 * 岗位相关 Mock 数据接口
 */

// 行业分类
const industries = [
  { id: 1, name: '互联网', icon: 'el-icon-monitor' },
  { id: 2, name: '金融', icon: 'el-icon-money' },
  { id: 3, name: '教育', icon: 'el-icon-reading' },
  { id: 4, name: '医疗健康', icon: 'el-icon-first-aid-kit' },
  { id: 5, name: '房地产', icon: 'el-icon-office-building' },
  { id: 6, name: '制造业', icon: 'el-icon-s-operation' },
  { id: 7, name: '服务业', icon: 'el-icon-service' },
  { id: 8, name: '文娱传媒', icon: 'el-icon-picture-outline' },
  { id: 9, name: '物流运输', icon: 'el-icon-truck' },
  { id: 10, name: '能源化工', icon: 'el-icon-connection' }
];

// 岗位类型分类
const jobCategories = [
  { id: 1, name: '技术研发', icon: 'el-icon-cpu', subCategories: ['前端开发', '后端开发', '全栈开发', '移动端开发', '测试开发', '运维/安全', '数据/算法', '架构师'] },
  { id: 2, name: '产品设计', icon: 'el-icon-edit', subCategories: ['产品经理', 'UI设计', 'UX设计', '交互设计', '视觉设计', '用户研究'] },
  { id: 3, name: '市场运营', icon: 'el-icon-data-analysis', subCategories: ['市场营销', '品牌推广', '新媒体运营', '内容运营', '用户运营', '活动运营', 'SEO/SEM'] },
  { id: 4, name: '销售商务', icon: 'el-icon-bank-card', subCategories: ['销售经理', '销售代表', '大客户销售', '渠道销售', '商务拓展', '招商专员'] },
  { id: 5, name: '人事行政', icon: 'el-icon-user', subCategories: ['HR/招聘', 'HRBP', '培训发展', '薪酬福利', '行政专员', '前台接待'] },
  { id: 6, name: '财务法务', icon: 'el-icon-document', subCategories: ['会计', '财务分析', '审计', '税务', '法务', '投资/风控'] },
  { id: 7, name: '采购物流', icon: 'el-icon-goods', subCategories: ['采购专员', '供应链管理', '物流经理', '仓储管理', '关务/商检'] },
  { id: 8, name: '生产制造', icon: 'el-icon-set-up', subCategories: ['生产主管', '质量控制', '工艺工程师', '设备维护', '精益生产'] },
  { id: 9, name: '教育培训', icon: 'el-icon-reading', subCategories: ['教师/讲师', '培训师', '课程顾问', '教研/教学管理'] },
  { id: 10, name: '医疗健康', icon: 'el-icon-first-aid-kit', subCategories: ['医生', '护士', '药师', '医疗器械', '医药代表', '健康管理'] }
];

// 岗位标题模板
const jobTitleTemplates = {
  1: ['高级前端工程师', 'Java后端开发', 'Python全栈开发', 'Android开发工程师', 'iOS开发工程师', '软件测试工程师', '运维工程师', '数据分析师', '算法工程师', '架构师', '前端开发工程师', '后端开发工程师', '全栈开发工程师', '测试开发工程师', '安全工程师'],
  2: ['高级产品经理', '产品经理', 'UI设计师', 'UE交互设计师', '视觉设计师', '产品助理', '用户研究员', '界面设计师'],
  3: ['市场经理', '新媒体运营', '内容运营', '用户运营', '活动运营', '品牌推广', '营销策划', 'SEO优化', 'SEM专员', '数据运营'],
  4: ['销售经理', '销售代表', '大客户销售', '渠道经理', '商务总监', '招商专员', '区域销售', '电话销售', '销售顾问'],
  5: ['HR经理', '招聘专员', 'HRBP', '培训师', '薪酬福利专员', '行政主管', '前台接待', '人事助理', '组织发展'],
  6: ['会计', '财务主管', '出纳', '审计专员', '税务经理', '法务专员', '风控专员', '投资经理', '财务分析'],
  7: ['采购专员', '供应链经理', '物流主管', '仓储经理', '关务专员', '供应商管理', '物料计划'],
  8: ['生产主管', '质量工程师', '工艺工程师', '设备工程师', '生产计划', '质检专员', '精益生产工程师'],
  9: ['语文教师', '数学教师', '英语教师', '课程顾问', '培训讲师', '教研专员', '班主任', 'K12教师'],
  10: ['内科医生', '外科医生', '护士', '药师', '医疗器械销售', '健康管理师', '检验师', '康复治疗师']
};

// 公司名称模板
const companyTemplates = {
  1: ['腾讯科技', '阿里巴巴', '字节跳动', '百度', '美团', '京东', '网易', '小米', '华为', '滴滴出行', '快手', '拼多多', '携程', '小红书', 'B站', '360', '搜狗', '金山软件', '携程旅行', '智联招聘'],
  2: ['中国工商银行', '建设银行', '中国银行', '招商银行', '平安集团', '中信证券', '国泰君安', '华泰证券', '招商证券', '蚂蚁金服', '支付宝', '陆金所', '宜人贷', '人人贷', '拍拍贷'],
  3: ['新东方', '好未来', '学而思', 'VIPKID', '作业帮', '猿辅导', '掌门一对一', '尚德机构', '中公教育', '华图教育', '精锐教育', '昂立教育'],
  4: ['三甲医院', '体检中心', '连锁药店', '医疗器械公司', '制药公司', '生物科技', '健康管理', '医美机构', '心理咨询', '康复中心'],
  5: ['万科地产', '碧桂园', '恒大集团', '融创中国', '保利地产', '中海地产', '龙湖集团', '华润置地', '万达集团', '华夏幸福', '绿地控股'],
  6: ['美的集团', '格力电器', '海尔集团', '比亚迪', '长城汽车', '吉利汽车', '徐工机械', '三一重工', '富士康', '华为终端'],
  7: ['海底捞', '星巴克', '肯德基', '麦当劳', '必胜客', '喜茶', '瑞幸咖啡', '外婆家', '西贝', '呷哺呷哺'],
  8: ['芒果TV', '爱奇艺', '优酷', '腾讯视频', '网易云音乐', 'QQ音乐', '新浪娱乐', '搜狐娱乐', '凤凰传媒', '光线传媒'],
  9: ['顺丰速运', '京东物流', '三通一达', '德邦物流', '中远海运', '中外运', '普洛斯', '嘉里物流'],
  10: ['中石油', '中石化', '国家电网', '南方电网', '中煤能源', '中国神华', '中国石化', '中国化工']
};

// 生成岗位数据
const jobList = [];
for (let i = 1; i <= 200; i++) {
  const categoryId = Mock.mock('@integer(1, 10)');
  const category = jobCategories.find(c => c.id === categoryId);
  const subCategory = Mock.mock({ 'one|1': category.subCategories }).one;
  const titleTemplates = jobTitleTemplates[categoryId];
  const title = Mock.mock({ 'one|1': titleTemplates }).one;
  const companyTemplatesArr = companyTemplates[categoryId];
  const companyName = Mock.mock({ 'one|1': companyTemplatesArr }).one;
  const industry = industries.find(i => i.id === categoryId);
  
  const job = {
    id: i,
    title: title,
    salaryMin: Mock.mock('@integer(5000, 25000)'),
    salaryMax: Mock.mock('@integer(15000, 50000)'),
    city: Mock.mock('@city'),
    workYears: Mock.mock({ 'one|1': ['应届毕业生', '1-3年', '3-5年', '5-10年', '10年以上'] }).one,
    education: Mock.mock({ 'one|1': ['大专', '本科', '硕士', '博士'] }).one,
    jobType: Mock.mock({ 'one|1': ['全职', '兼职', '实习'] }).one,
    description: Mock.mock('@cparagraph(5, 10)'),
    requirements: Mock.mock('@cparagraph(3, 5)'),
    welfare: Mock.mock({ 'list|3-5': ['五险一金', '年终奖', '带薪年假', '弹性工作', '定期团建', '员工旅游', '节日福利', '下午茶', '股票期权', '交通补贴', '餐补', '住房补贴'] }).list,
    publishTime: Mock.mock('@datetime'),
    updateTime: Mock.mock('@datetime'),
    viewCount: Mock.mock('@integer(100, 5000)'),
    applyCount: Mock.mock('@integer(10, 500)'),
    status: Mock.mock('@integer(1, 3)'),
    auditStatus: Mock.mock('@integer(1, 3)'),
    companyId: Mock.mock('@integer(1, 100)'),
    companyName: companyName,
    companyLogo: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=company%20logo%20icon&image_size=square",
    companySize: Mock.mock({ 'one|1': ['0-20人', '20-99人', '100-499人', '500-999人', '1000人以上'] }).one,
    industry: industry.name,
    industryId: industry.id,
    categoryId: categoryId,
    categoryName: category.name,
    subCategory: subCategory,
    hrId: Mock.mock('@integer(101, 200)'),
    hrName: Mock.mock('@cname'),
    hrPosition: Mock.mock('@ctitle(2, 4)')
  };
  jobList.push(job);
}

const jobData = { list: jobList };

// 获取行业分类列表
Mock.mock('/api/jobs/industries', 'get', () => {
  return {
    code: 200,
    message: '获取成功',
    data: industries
  };
});

// 获取岗位分类列表
Mock.mock('/api/jobs/categories', 'get', () => {
  return {
    code: 200,
    message: '获取成功',
    data: jobCategories
  };
});

// 获取岗位列表（求职者端）
Mock.mock(RegExp('/api/jobs.*'), 'get', (options) => {
  // 解析查询参数
  const url = options.url;
  const params = {};
  const queryString = url.split('?')[1];
  if (queryString) {
    queryString.split('&').forEach(param => {
      const [key, value] = param.split('=');
      params[key] = decodeURIComponent(value);
    });
  }
  
  const page = parseInt(params.page) || 1;
  const pageSize = parseInt(params.pageSize) || 10;
  const keyword = params.keyword || '';
  const city = params.city || '';
  const workYears = params.workYears || '';
  const education = params.education || '';
  const salary = params.salary || '';
  const salaryRange = params.salaryRange || '';
  const industryId = params.industryId || '';
  const categoryId = params.categoryId || '';
  const subCategory = params.subCategory || '';
  const sortBy = params.sortBy || 'publishTime';
  const sortOrder = params.sortOrder || 'desc';
  
  // 过滤数据
  let filteredJobs = jobData.list.filter(job => job.status === 1 && job.auditStatus === 1);
  
  // 关键词搜索
  if (keyword) {
    filteredJobs = filteredJobs.filter(job => 
      job.title.includes(keyword) || 
      job.companyName.includes(keyword) ||
      job.subCategory.includes(keyword)
    );
  }
  
  // 城市筛选
  if (city) {
    filteredJobs = filteredJobs.filter(job => job.city.includes(city));
  }
  
  // 工作年限筛选
  if (workYears && workYears !== '不限') {
    filteredJobs = filteredJobs.filter(job => job.workYears === workYears);
  }
  
  // 学历筛选
  if (education && education !== '不限') {
    filteredJobs = filteredJobs.filter(job => job.education === education);
  }
  
  // 薪资筛选
  if (salaryRange) {
    const rangeMap = {
      '1': [0, 3000],
      '2': [3000, 5000],
      '3': [5000, 10000],
      '4': [10000, 20000],
      '5': [20000, 50000],
      '6': [50000, Infinity]
    };
    const range = rangeMap[salaryRange];
    if (range) {
      filteredJobs = filteredJobs.filter(job => {
        if (range[1] === Infinity) {
          return job.salaryMin >= range[0];
        }
        return job.salaryMin <= range[1] && job.salaryMax >= range[0];
      });
    }
  }
  
  if (salary) {
    const [min, max] = salary.split('-').map(s => parseInt(s));
    filteredJobs = filteredJobs.filter(job => {
      if (max) {
        return job.salaryMin <= max && job.salaryMax >= min;
      }
      return job.salaryMin >= min;
    });
  }
  
  // 行业筛选
  if (industryId) {
    filteredJobs = filteredJobs.filter(job => job.industryId === parseInt(industryId));
  }
  
  // 岗位类型筛选
  if (categoryId) {
    filteredJobs = filteredJobs.filter(job => job.categoryId === parseInt(categoryId));
  }
  
  // 子分类筛选
  if (subCategory) {
    filteredJobs = filteredJobs.filter(job => job.subCategory === subCategory);
  }
  
  // 排序
  filteredJobs.sort((a, b) => {
    let valueA, valueB;
    switch (sortBy) {
      case 'salary':
        valueA = a.salaryMax;
        valueB = b.salaryMax;
        break;
      case 'viewCount':
        valueA = a.viewCount;
        valueB = b.viewCount;
        break;
      case 'applyCount':
        valueA = a.applyCount;
        valueB = b.applyCount;
        break;
      default:
        valueA = new Date(a.publishTime).getTime();
        valueB = new Date(b.publishTime).getTime();
    }
    return sortOrder === 'desc' ? valueB - valueA : valueA - valueB;
  });
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedJobs = filteredJobs.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedJobs,
      total: filteredJobs.length,
      page,
      pageSize
    }
  };
});

// 获取岗位详情
Mock.mock(RegExp('/api/jobs/\\d+'), 'get', (options) => {
  // 从 URL 中提取岗位 ID
  const id = parseInt(options.url.match(/\/api\/jobs\/(\d+)/)[1]);
  
  // 查找对应岗位
  const job = jobData.list.find(j => j.id === id) || jobData.list[0];
  
  // 增加浏览量
  job.viewCount++;
  
  return {
    code: 200,
    message: '获取成功',
    data: job
  };
});

// HR 获取岗位列表
Mock.mock(RegExp('/api/hr/jobs.*'), 'get', (options) => {
  // 解析查询参数
  const url = options.url;
  const params = {};
  const queryString = url.split('?')[1];
  if (queryString) {
    queryString.split('&').forEach(param => {
      const [key, value] = param.split('=');
      params[key] = decodeURIComponent(value);
    });
  }
  
  const page = parseInt(params.page) || 1;
  const pageSize = parseInt(params.pageSize) || 10;
  const keyword = params.keyword || '';
  const status = params.status || '';
  
  // 过滤数据
  let filteredJobs = [...jobData.list];
  
  if (keyword) {
    filteredJobs = filteredJobs.filter(job => 
      job.title.includes(keyword)
    );
  }
  
  if (status) {
    filteredJobs = filteredJobs.filter(job => job.status === parseInt(status));
  }
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedJobs = filteredJobs.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedJobs,
      total: filteredJobs.length,
      page,
      pageSize
    }
  };
});

// 发布岗位
Mock.mock('/api/hr/jobs', 'post', (options) => {
  const body = JSON.parse(options.body);
  
  const industry = industries.find(i => i.id == body.industryId);
  const category = jobCategories.find(c => c.id == body.categoryId);
  
  const newJob = {
    id: jobData.list.length + 1,
    ...body,
    industry: industry ? industry.name : '',
    categoryName: category ? category.name : '',
    publishTime: Mock.mock('@datetime'),
    updateTime: Mock.mock('@datetime'),
    viewCount: 0,
    applyCount: 0,
    status: 2,
    auditStatus: 2
  };
  jobData.list.unshift(newJob);
  
  return {
    code: 200,
    message: '发布成功，等待审核',
    data: newJob
  };
});

// 更新岗位
Mock.mock(RegExp('/api/hr/jobs/\\d+'), 'put', (options) => {
  const body = JSON.parse(options.body);
  
  const industry = industries.find(i => i.id == body.industryId);
  const category = jobCategories.find(c => c.id == body.categoryId);
  
  return {
    code: 200,
    message: '更新成功',
    data: {
      ...body,
      industry: industry ? industry.name : '',
      categoryName: category ? category.name : ''
    }
  };
});

// 上下架岗位
Mock.mock(RegExp('/api/hr/jobs/\\d+/status'), 'put', (options) => {
  return {
    code: 200,
    message: '操作成功',
    data: null
  };
});

// 删除岗位
Mock.mock(RegExp('/api/hr/jobs/\\d+'), 'delete', (options) => {
  return {
    code: 200,
    message: '删除成功',
    data: null
  };
});

// 管理员获取岗位列表
Mock.mock(RegExp('/api/admin/jobs.*'), 'get', (options) => {
  // 解析查询参数
  const url = options.url;
  const params = {};
  const queryString = url.split('?')[1];
  if (queryString) {
    queryString.split('&').forEach(param => {
      const [key, value] = param.split('=');
      params[key] = decodeURIComponent(value);
    });
  }
  
  const page = parseInt(params.page) || 1;
  const pageSize = parseInt(params.pageSize) || 10;
  const keyword = params.keyword || '';
  const auditStatus = params.auditStatus || '';
  
  // 过滤数据
  let filteredJobs = [...jobData.list];
  
  if (keyword) {
    filteredJobs = filteredJobs.filter(job => 
      job.title.includes(keyword) || 
      job.companyName.includes(keyword)
    );
  }
  
  if (auditStatus) {
    filteredJobs = filteredJobs.filter(job => job.auditStatus === parseInt(auditStatus));
  }
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedJobs = filteredJobs.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedJobs,
      total: filteredJobs.length,
      page,
      pageSize
    }
  };
});

// 审核岗位
Mock.mock(RegExp('/api/admin/jobs/\\d+/audit'), 'put', (options) => {
  const body = JSON.parse(options.body);
  const { auditStatus, rejectReason } = body;
  
  const message = auditStatus === 1 ? '审核通过' : (auditStatus === 3 ? '已拒绝' : '操作成功');
  return {
    code: 200,
    message,
    data: null
  };
});

// 热门岗位推荐
Mock.mock('/api/jobs/hot', 'get', () => {
  // 按浏览量排序取前10个
  const hotJobs = [...jobData.list]
    .filter(job => job.status === 1 && job.auditStatus === 1)
    .sort((a, b) => b.viewCount - a.viewCount)
    .slice(0, 10);
  
  return {
    code: 200,
    message: '获取成功',
    data: hotJobs
  };
});

// 最新岗位
Mock.mock('/api/jobs/latest', 'get', () => {
  // 按发布时间排序取前10个
  const latestJobs = [...jobData.list]
    .filter(job => job.status === 1 && job.auditStatus === 1)
    .sort((a, b) => new Date(b.publishTime) - new Date(a.publishTime))
    .slice(0, 10);
  
  return {
    code: 200,
    message: '获取成功',
    data: latestJobs
  };
});
