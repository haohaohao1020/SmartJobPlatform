import Mock from 'mockjs';

/**
 * 管理员相关 Mock 数据接口
 */

// 公告数据
const announcementData = Mock.mock({
  'list|15': [
    {
      'id|+1': 1,
      'title': '@ctitle(10, 30)',
      'content': '@cparagraph(10, 20)',
      'type|1': [1, 2, 3], // 1: 系统公告, 2: 活动公告, 3: 其他
      'status|1': [1, 2], // 1: 已发布, 2: 草稿
      'isTop|1': [true, false],
      'viewCount|100-5000': 1,
      'publishTime': '@datetime',
      'createTime': '@datetime',
      'updateTime': '@datetime',
      'adminId': 999,
      'adminName': '系统管理员'
    }
  ]
});

// 系统配置数据
const settingsData = {
  siteName: '智慧招聘平台',
  siteLogo: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=recruitment%20platform%20logo&image_size=square",
  siteDescription: '专业的人才招聘平台，连接人才与企业',
  contactPhone: '400-123-4567',
  contactEmail: 'support@jobplatform.com',
  contactAddress: '北京市朝阳区科技园区A座',
  icpNumber: '京ICP备2023000000号',
  copyright: '© 2023 智慧招聘平台 版权所有',
  enableRegister: true,
  enableJobRecommend: true,
  maxResumeCount: 1,
  maxJobCount: 100,
  uploadMaxSize: 10,
  allowedFileTypes: 'jpg,png,pdf,doc,docx'
};

// 数据控制台统计数据
Mock.mock('/api/admin/statistics', 'get', () => {
  const statistics = {
    // 今日数据
    today: {
      newUsers: Mock.mock('@integer(50, 200)'),
      newCompanies: Mock.mock('@integer(5, 20)'),
      newJobs: Mock.mock('@integer(30, 100)'),
      newApplications: Mock.mock('@integer(100, 500)'),
      revenue: Mock.mock('@float(10000, 50000, 2)')
    },
    // 总计数据
    total: {
      users: Mock.mock('@integer(10000, 50000)'),
      companies: Mock.mock('@integer(1000, 5000)'),
      jobs: Mock.mock('@integer(5000, 20000)'),
      applications: Mock.mock('@integer(50000, 200000)'),
      interviews: Mock.mock('@integer(10000, 50000)'),
      hires: Mock.mock('@integer(5000, 20000)')
    },
    // 待审核数据
    pendingAudit: {
      companies: Mock.mock('@integer(10, 50)'),
      jobs: Mock.mock('@integer(50, 200)'),
      users: Mock.mock('@integer(20, 100)')
    },
    // 用户增长趋势（近7天）
    userGrowth: {
      dates: Mock.mock({
        'list|7': [
          () => {
            const date = new Date();
            date.setDate(date.getDate() - (6 - Mock.mock('@integer(0, 6)')));
            return `${date.getMonth() + 1}-${date.getDate()}`;
          }
        ]
      }).list,
      jobSeekers: Mock.mock({
        'list|7': ['@integer(20, 100)']
      }).list,
      hrs: Mock.mock({
        'list|7': ['@integer(5, 30)']
      }).list
    },
    // 岗位发布趋势（近7天）
    jobTrend: {
      dates: Mock.mock({
        'list|7': [
          () => {
            const date = new Date();
            date.setDate(date.getDate() - (6 - Mock.mock('@integer(0, 6)')));
            return `${date.getMonth() + 1}-${date.getDate()}`;
          }
        ]
      }).list,
      counts: Mock.mock({
        'list|7': ['@integer(10, 100)']
      }).list
    },
    // 行业分布
    industryDistribution: [
      { name: '互联网', value: Mock.mock('@integer(500, 2000)') },
      { name: '金融', value: Mock.mock('@integer(300, 1500)') },
      { name: '教育', value: Mock.mock('@integer(200, 1000)') },
      { name: '医疗', value: Mock.mock('@integer(200, 1000)') },
      { name: '房地产', value: Mock.mock('@integer(100, 800)') },
      { name: '制造业', value: Mock.mock('@integer(100, 800)') },
      { name: '其他', value: Mock.mock('@integer(100, 500)') }
    ],
    // 热门岗位TOP10
    hotJobs: Mock.mock({
      'list|10': [
        {
          'id|+1': 1,
          'title': '@ctitle(4, 10)',
          'companyName': '@ctitle(4, 8)有限公司',
          'applyCount|100-1000': 1,
          'viewCount|500-5000': 1
        }
      ]
    }).list,
    // 热门企业TOP10
    hotCompanies: Mock.mock({
      'list|10': [
        {
          'id|+1': 1,
          'name': '@ctitle(4, 8)有限公司',
          'logo': "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=company%20logo%20icon&image_size=square",
          'jobCount|10-100': 1,
          'viewCount|1000-10000': 1
        }
      ]
    }).list
  };
  
  return {
    code: 200,
    message: '获取成功',
    data: statistics
  };
});

// 获取公告列表
Mock.mock(RegExp('/api/admin/announcements.*'), 'get', (options) => {
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
  const type = params.type || '';
  const status = params.status || '';
  
  // 过滤数据
  let filteredAnnouncements = [...announcementData.list];
  
  if (keyword) {
    filteredAnnouncements = filteredAnnouncements.filter(item => 
      item.title.includes(keyword)
    );
  }
  
  if (type) {
    filteredAnnouncements = filteredAnnouncements.filter(item => 
      item.type === parseInt(type)
    );
  }
  
  if (status) {
    filteredAnnouncements = filteredAnnouncements.filter(item => 
      item.status === parseInt(status)
    );
  }
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedAnnouncements = filteredAnnouncements.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedAnnouncements,
      total: filteredAnnouncements.length,
      page,
      pageSize
    }
  };
});

// 获取公告详情
Mock.mock(RegExp('/api/admin/announcements/\\d+'), 'get', (options) => {
  const id = parseInt(options.url.match(/\/api\/admin\/announcements\/(\d+)/)[1]);
  const announcement = announcementData.list.find(a => a.id === id) || announcementData.list[0];
  
  return {
    code: 200,
    message: '获取成功',
    data: announcement
  };
});

// 创建公告
Mock.mock('/api/admin/announcements', 'post', (options) => {
  const body = JSON.parse(options.body);
  const newAnnouncement = {
    id: announcementData.list.length + 1,
    ...body,
    viewCount: 0,
    createTime: Mock.mock('@datetime'),
    updateTime: Mock.mock('@datetime'),
    adminId: 999,
    adminName: '系统管理员'
  };
  announcementData.list.unshift(newAnnouncement);
  
  return {
    code: 200,
    message: '创建成功',
    data: newAnnouncement
  };
});

// 更新公告
Mock.mock(RegExp('/api/admin/announcements/\\d+'), 'put', (options) => {
  const body = JSON.parse(options.body);
  return {
    code: 200,
    message: '更新成功',
    data: null
  };
});

// 删除公告
Mock.mock(RegExp('/api/admin/announcements/\\d+'), 'delete', (options) => {
  return {
    code: 200,
    message: '删除成功',
    data: null
  };
});

// 发布/下架公告
Mock.mock(RegExp('/api/admin/announcements/\\d+/status'), 'put', (options) => {
  return {
    code: 200,
    message: '操作成功',
    data: null
  };
});

// 获取系统配置
Mock.mock('/api/admin/settings', 'get', () => {
  return {
    code: 200,
    message: '获取成功',
    data: settingsData
  };
});

// 更新系统配置
Mock.mock('/api/admin/settings', 'put', (options) => {
  const body = JSON.parse(options.body);
  Object.assign(settingsData, body);
  
  return {
    code: 200,
    message: '更新成功',
    data: null
  };
});

// 获取登录日志
Mock.mock(RegExp('/api/admin/login-logs.*'), 'get', (options) => {
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
  
  const loginLogs = Mock.mock({
    'list|20': [
      {
        'id|+1': 1,
        'userId|+1': 1,
        'userName': '@cname',
        'role|1': ['jobseeker', 'hr', 'admin'],
        'ip': '@ip',
        'location': '@city',
        'device': '@ctitle(4, 8)',
        'browser': '@ctitle(2, 4)',
        'loginTime': '@datetime',
        'logoutTime': '@datetime',
        'status|1': [1, 2] // 1: 成功, 2: 失败
      }
    ]
  });
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: loginLogs.list,
      total: 100,
      page,
      pageSize
    }
  };
});

// 获取操作日志
Mock.mock(RegExp('/api/admin/operation-logs.*'), 'get', (options) => {
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
  
  const operationLogs = Mock.mock({
    'list|20': [
      {
        'id|+1': 1,
        'userId|+1': 1,
        'userName': '@cname',
        'role|1': ['jobseeker', 'hr', 'admin'],
        'module|1': ['用户管理', '企业管理', '岗位管理', '公告管理', '系统配置'],
        'action|1': ['新增', '修改', '删除', '审核', '登录', '退出'],
        'target': '@ctitle(4, 10)',
        'ip': '@ip',
        'description': '@cparagraph(1, 3)',
        'createTime': '@datetime'
      }
    ]
  });
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: operationLogs.list,
      total: 100,
      page,
      pageSize
    }
  };
});
