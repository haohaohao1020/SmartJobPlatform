import Mock from 'mockjs';

/**
 * 岗位相关 Mock 数据接口
 */

// 生成岗位数据
const jobData = Mock.mock({
  'list|50': [
    {
      'id|+1': 1,
      'title': '@ctitle(4, 10)',
      'salaryMin': '@integer(5000, 15000)',
      'salaryMax': '@integer(15000, 30000)',
      'city': '@city',
      'workYears|1': ['应届毕业生', '1-3年', '3-5年', '5-10年', '10年以上'],
      'education|1': ['大专', '本科', '硕士', '博士'],
      'jobType|1': ['全职', '兼职', '实习'],
      'description': '@cparagraph(5, 10)',
      'requirements': '@cparagraph(3, 5)',
      'welfare|3-5': ['五险一金', '年终奖', '带薪年假', '弹性工作', '定期团建', '员工旅游', '节日福利', '下午茶'],
      'publishTime': '@datetime',
      'updateTime': '@datetime',
      'viewCount|100-1000': 1,
      'applyCount|10-100': 1,
      'status|1': [1, 2, 3], // 1: 招聘中, 2: 已下架, 3: 审核中
      'auditStatus|1': [1, 2, 3], // 1: 已通过, 2: 待审核, 3: 已拒绝
      'companyId|+1': 1,
      'companyName': '@ctitle(4, 8)有限公司',
      'companyLogo': "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=company%20logo%20icon&image_size=square",
      'companySize|1': ['0-20人', '20-99人', '100-499人', '500-999人', '1000人以上'],
      'companyIndustry|1': ['互联网', '金融', '教育', '医疗', '房地产', '制造业', '服务业'],
      'hrId|+1': 101,
      'hrName': '@cname',
      'hrPosition': '@ctitle(2, 4)'
    }
  ]
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
  const sortBy = params.sortBy || 'publishTime';
  const sortOrder = params.sortOrder || 'desc';
  
  // 过滤数据
  let filteredJobs = jobData.list.filter(job => job.status === 1 && job.auditStatus === 1);
  
  // 关键词搜索
  if (keyword) {
    filteredJobs = filteredJobs.filter(job => 
      job.title.includes(keyword) || 
      job.companyName.includes(keyword)
    );
  }
  
  // 城市筛选
  if (city) {
    filteredJobs = filteredJobs.filter(job => job.city.includes(city));
  }
  
  // 工作年限筛选
  if (workYears) {
    filteredJobs = filteredJobs.filter(job => job.workYears === workYears);
  }
  
  // 学历筛选
  if (education) {
    filteredJobs = filteredJobs.filter(job => job.education === education);
  }
  
  // 薪资筛选
  if (salary) {
    const [min, max] = salary.split('-').map(s => parseInt(s));
    filteredJobs = filteredJobs.filter(job => {
      if (max) {
        return job.salaryMin <= max && job.salaryMax >= min;
      }
      return job.salaryMin >= min;
    });
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
  const newJob = {
    id: jobData.list.length + 1,
    ...body,
    publishTime: Mock.mock('@datetime'),
    updateTime: Mock.mock('@datetime'),
    viewCount: 0,
    applyCount: 0,
    status: 2, // 刚发布默认为已下架，需要上架
    auditStatus: 2 // 待审核
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
  return {
    code: 200,
    message: '更新成功',
    data: body
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
