import Mock from 'mockjs';

/**
 * 投递相关 Mock 数据接口
 */

// 生成投递数据
const applicationData = Mock.mock({
  'list|30': [
    {
      'id|+1': 1,
      'userId': 1,
      'jobId|+1': 1,
      'jobTitle': '@ctitle(4, 10)',
      'companyId|+1': 1,
      'companyName': '@ctitle(4, 8)有限公司',
      'companyLogo': "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=company%20logo%20icon&image_size=square",
      'resumeId': 1,
      'salaryMin': '@integer(8000, 15000)',
      'salaryMax': '@integer(15000, 30000)',
      'city': '@city',
      'workYears|1': ['应届毕业生', '1-3年', '3-5年'],
      'education|1': ['大专', '本科', '硕士'],
      'status|1': [1, 2, 3, 4, 5, 6], // 1: 待查看, 2: 待面试, 3: 面试中, 4: 待Offer, 5: 已拒绝, 6: 已录用
      'statusName|1': ['待查看', '待面试', '面试中', '待Offer', '已拒绝', '已录用'],
      'isRead|1': [true, false],
      'hrId|+1': 101,
      'hrName': '@cname',
      'applyTime': '@datetime',
      'updateTime': '@datetime',
      'interviewTime': '@datetime',
      'interviewAddress': '@ctitle(10, 20)',
      'interviewNote': '@cparagraph(1, 2)',
      'rejectReason': ''
    }
  ]
});

// 投递岗位
Mock.mock('/api/applications', 'post', (options) => {
  const body = JSON.parse(options.body);
  const newApplication = {
    id: applicationData.list.length + 1,
    ...body,
    userId: 1,
    status: 1,
    statusName: '待查看',
    isRead: false,
    applyTime: Mock.mock('@datetime'),
    updateTime: Mock.mock('@datetime')
  };
  applicationData.list.unshift(newApplication);
  
  return {
    code: 200,
    message: '投递成功',
    data: newApplication
  };
});

// uiapp 投递岗位
Mock.mock('/api/applications/submit', 'post', (options) => {
  const body = JSON.parse(options.body);
  const newApplication = {
    id: applicationData.list.length + 1,
    ...body,
    userId: 1,
    status: 1,
    statusName: '待查看',
    isRead: false,
    applyTime: Mock.mock('@datetime'),
    updateTime: Mock.mock('@datetime')
  };
  applicationData.list.unshift(newApplication);
  
  return {
    code: 200,
    message: '投递成功',
    data: newApplication
  };
});

// 获取我的投递列表
Mock.mock(RegExp('/api/applications/my.*'), 'get', (options) => {
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
  const status = params.status || '';
  
  // 过滤数据
  let filteredApplications = [...applicationData.list];
  
  if (status) {
    filteredApplications = filteredApplications.filter(app => 
      app.status === parseInt(status)
    );
  }
  
  // 按投递时间倒序
  filteredApplications.sort((a, b) => 
    new Date(b.applyTime) - new Date(a.applyTime)
  );
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedApplications = filteredApplications.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedApplications,
      total: filteredApplications.length,
      page,
      pageSize
    }
  };
});

// uiapp 获取投递列表
Mock.mock(RegExp('/api/applications/list.*'), 'get', (options) => {
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
  const status = params.status || '';
  
  let filteredApplications = [...applicationData.list];
  
  if (status) {
    filteredApplications = filteredApplications.filter(app => 
      app.status === parseInt(status)
    );
  }
  
  filteredApplications.sort((a, b) => 
    new Date(b.applyTime) - new Date(a.applyTime)
  );
  
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedApplications = filteredApplications.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedApplications,
      total: filteredApplications.length,
      page,
      pageSize
    }
  };
});

// 获取投递详情
Mock.mock(RegExp('/api/applications/\\d+'), 'get', (options) => {
  const id = parseInt(options.url.match(/\/api\/applications\/(\d+)/)[1]);
  const application = applicationData.list.find(a => a.id === id) || applicationData.list[0];
  
  return {
    code: 200,
    message: '获取成功',
    data: application
  };
});

// 取消投递
Mock.mock(RegExp('/api/applications/\\d+/cancel'), 'post', (options) => {
  return {
    code: 200,
    message: '取消投递成功',
    data: null
  };
});

// 检查是否已投递
Mock.mock(RegExp('/api/applications/check.*'), 'get', (options) => {
  const url = options.url;
  const params = {};
  const queryString = url.split('?')[1];
  if (queryString) {
    queryString.split('&').forEach(param => {
      const [key, value] = param.split('=');
      params[key] = decodeURIComponent(value);
    });
  }
  
  const jobId = parseInt(params.jobId);
  // 随机返回已投递或未投递
  const hasApplied = Math.random() > 0.5;
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      hasApplied,
      applicationId: hasApplied ? Math.floor(Math.random() * 100) : null
    }
  };
});

// 投递统计
Mock.mock('/api/applications/statistics', 'get', () => {
  const stats = {
    total: applicationData.list.length,
    pending: applicationData.list.filter(a => a.status === 1).length,
    interviewing: applicationData.list.filter(a => a.status === 2 || a.status === 3).length,
    offer: applicationData.list.filter(a => a.status === 4).length,
    hired: applicationData.list.filter(a => a.status === 6).length,
    rejected: applicationData.list.filter(a => a.status === 5).length
  };
  
  return {
    code: 200,
    message: '获取成功',
    data: stats
  };
});

// HR 获取投递列表
Mock.mock(RegExp('/api/hr/applications.*'), 'get', (options) => {
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
  const jobId = params.jobId || '';
  
  // 过滤数据
  let filteredApplications = [...applicationData.list];
  
  if (keyword) {
    filteredApplications = filteredApplications.filter(app => 
      app.jobTitle.includes(keyword)
    );
  }
  
  if (status) {
    filteredApplications = filteredApplications.filter(app => 
      app.status === parseInt(status)
    );
  }
  
  if (jobId) {
    filteredApplications = filteredApplications.filter(app => 
      app.jobId === parseInt(jobId)
    );
  }
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedApplications = filteredApplications.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedApplications,
      total: filteredApplications.length,
      page,
      pageSize
    }
  };
});

// HR 更新投递状态
Mock.mock(RegExp('/api/hr/applications/\\d+/status'), 'put', (options) => {
  const body = JSON.parse(options.body);
  return {
    code: 200,
    message: '状态更新成功',
    data: null
  };
});

// HR 标记为已读
Mock.mock(RegExp('/api/hr/applications/\\d+/read'), 'put', (options) => {
  return {
    code: 200,
    message: '标记成功',
    data: null
  };
});
