import Mock from 'mockjs';

/**
 * 企业相关 Mock 数据接口
 */

// 生成企业数据
const companyData = Mock.mock({
  'list|20': [
    {
      'id|+1': 1,
      'name': '@ctitle(4, 8)有限公司',
      'shortName': '@ctitle(2, 4)',
      'logo': "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=company%20logo%20icon&image_size=square",
      'industry|1': ['互联网', '金融', '教育', '医疗', '房地产', '制造业', '服务业', '电子商务', '人工智能'],
      'size|1': ['0-20人', '20-99人', '100-499人', '500-999人', '1000人以上'],
      'type|1': ['民营企业', '国有企业', '外资企业', '合资企业', '上市公司'],
      'description': '@cparagraph(5, 10)',
      'province': '@province',
      'city': '@city',
      'district': '@county',
      'address': '@ctitle(10, 20)',
      'contactName': '@cname',
      'contactPhone': /^1[3456789]\d{9}$/,
      'contactEmail': '@email',
      'businessLicense': "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=business%20license%20document&image_size=square",
      'establishDate': '@date("yyyy-MM-dd")',
      'registeredCapital': '@integer(100, 10000)万元',
      'legalPerson': '@cname',
      'businessScope': '@cparagraph(3, 5)',
      'auditStatus|1': [1, 2, 3], // 1: 已通过, 2: 待审核, 3: 已拒绝
      'auditTime': '@datetime',
      'auditAdminId': 999,
      'auditAdminName': '系统管理员',
      'rejectReason': '',
      'status|1': [1, 2], // 1: 正常, 2: 禁用
      'viewCount|100-5000': 1,
      'jobCount|1-50': 1,
      'createTime': '@datetime',
      'updateTime': '@datetime',
      'hrId|+1': 101,
      'hrName': '@cname'
    }
  ]
});

// 获取企业详情
Mock.mock(RegExp('/api/companies/\\d+'), 'get', (options) => {
  // 从 URL 中提取企业 ID
  const id = parseInt(options.url.match(/\/api\/companies\/(\d+)/)[1]);
  
  // 查找对应企业
  const company = companyData.list.find(c => c.id === id) || companyData.list[0];
  
  return {
    code: 200,
    message: '获取成功',
    data: company
  };
});

// 获取当前 HR 的企业信息
Mock.mock('/api/hr/company', 'get', () => {
  return {
    code: 200,
    message: '获取成功',
    data: companyData.list[0]
  };
});

// 企业入驻（创建企业）
Mock.mock('/api/hr/company', 'post', (options) => {
  const body = JSON.parse(options.body);
  const newCompany = {
    id: companyData.list.length + 1,
    ...body,
    auditStatus: 2, // 待审核
    status: 1,
    viewCount: 0,
    jobCount: 0,
    createTime: Mock.mock('@datetime'),
    updateTime: Mock.mock('@datetime')
  };
  companyData.list.unshift(newCompany);
  
  return {
    code: 200,
    message: '提交成功，等待审核',
    data: newCompany
  };
});

// 更新企业信息
Mock.mock('/api/hr/company', 'put', (options) => {
  const body = JSON.parse(options.body);
  return {
    code: 200,
    message: '更新成功',
    data: body
  };
});

// 上传企业 LOGO
Mock.mock('/api/company/logo', 'post', () => {
  return {
    code: 200,
    message: '上传成功',
    data: {
      url: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=new%20company%20logo&image_size=square"
    }
  };
});

// 上传营业执照
Mock.mock('/api/company/license', 'post', () => {
  return {
    code: 200,
    message: '上传成功',
    data: {
      url: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=business%20license%20image&image_size=square"
    }
  };
});

// 管理员获取企业列表
Mock.mock(RegExp('/api/admin/companies.*'), 'get', (options) => {
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
  const industry = params.industry || '';
  
  // 过滤数据
  let filteredCompanies = [...companyData.list];
  
  if (keyword) {
    filteredCompanies = filteredCompanies.filter(company => 
      company.name.includes(keyword) || 
      company.shortName.includes(keyword)
    );
  }
  
  if (auditStatus) {
    filteredCompanies = filteredCompanies.filter(company => 
      company.auditStatus === parseInt(auditStatus)
    );
  }
  
  if (industry) {
    filteredCompanies = filteredCompanies.filter(company => 
      company.industry === industry
    );
  }
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedCompanies = filteredCompanies.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedCompanies,
      total: filteredCompanies.length,
      page,
      pageSize
    }
  };
});

// 管理员审核企业
Mock.mock(RegExp('/api/admin/companies/\\d+/audit'), 'put', (options) => {
  const body = JSON.parse(options.body);
  const { auditStatus, rejectReason } = body;
  
  const message = auditStatus === 1 ? '审核通过' : (auditStatus === 3 ? '已拒绝' : '操作成功');
  return {
    code: 200,
    message,
    data: null
  };
});

// 管理员禁用/启用企业
Mock.mock(RegExp('/api/admin/companies/\\d+/status'), 'put', (options) => {
  return {
    code: 200,
    message: '操作成功',
    data: null
  };
});

// 热门企业推荐
Mock.mock('/api/companies/hot', 'get', () => {
  // 按浏览量排序取前10个已审核通过的企业
  const hotCompanies = [...companyData.list]
    .filter(company => company.auditStatus === 1 && company.status === 1)
    .sort((a, b) => b.viewCount - a.viewCount)
    .slice(0, 10);
  
  return {
    code: 200,
    message: '获取成功',
    data: hotCompanies
  };
});

// 企业列表（带筛选）
Mock.mock(RegExp('/api/companies.*'), 'get', (options) => {
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
  const industry = params.industry || '';
  const size = params.size || '';
  
  // 过滤数据
  let filteredCompanies = [...companyData.list].filter(
    company => company.auditStatus === 1 && company.status === 1
  );
  
  if (keyword) {
    filteredCompanies = filteredCompanies.filter(company => 
      company.name.includes(keyword) || 
      company.shortName.includes(keyword)
    );
  }
  
  if (industry) {
    filteredCompanies = filteredCompanies.filter(company => 
      company.industry === industry
    );
  }
  
  if (size) {
    filteredCompanies = filteredCompanies.filter(company => 
      company.size === size
    );
  }
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedCompanies = filteredCompanies.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedCompanies,
      total: filteredCompanies.length,
      page,
      pageSize
    }
  };
});
