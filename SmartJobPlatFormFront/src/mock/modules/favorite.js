import Mock from 'mockjs';

/**
 * 收藏相关 Mock 数据接口
 */

// 生成收藏数据
const favoriteData = Mock.mock({
  'list|20': [
    {
      'id|+1': 1,
      'userId': 1,
      'jobId|+1': 1,
      'jobTitle': '@ctitle(4, 10)',
      'companyId|+1': 1,
      'companyName': '@ctitle(4, 8)有限公司',
      'companyLogo': "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=company%20logo%20icon&image_size=square",
      'salaryMin': '@integer(8000, 15000)',
      'salaryMax': '@integer(15000, 30000)',
      'city': '@city',
      'workYears|1': ['应届毕业生', '1-3年', '3-5年'],
      'education|1': ['大专', '本科', '硕士'],
      'jobStatus|1': [1, 2], // 1: 招聘中, 2: 已下架
      'favoriteTime': '@datetime'
    }
  ]
});

// 收藏岗位
Mock.mock('/api/favorites', 'post', (options) => {
  const body = JSON.parse(options.body);
  const newFavorite = {
    id: favoriteData.list.length + 1,
    ...body,
    userId: 1,
    favoriteTime: Mock.mock('@datetime')
  };
  favoriteData.list.unshift(newFavorite);
  
  return {
    code: 200,
    message: '收藏成功',
    data: newFavorite
  };
});

// uiapp 收藏岗位
Mock.mock('/api/favorites/add', 'post', (options) => {
  const body = JSON.parse(options.body);
  const newFavorite = {
    id: favoriteData.list.length + 1,
    jobId: body.jobId,
    userId: 1,
    favoriteTime: Mock.mock('@datetime')
  };
  favoriteData.list.unshift(newFavorite);
  
  return {
    code: 200,
    message: '收藏成功',
    data: newFavorite
  };
});

// 取消收藏
Mock.mock(RegExp('/api/favorites/\\d+'), 'delete', (options) => {
  return {
    code: 200,
    message: '取消收藏成功',
    data: null
  };
});

// 批量取消收藏
Mock.mock('/api/favorites/batch', 'delete', (options) => {
  return {
    code: 200,
    message: '批量取消收藏成功',
    data: null
  };
});

// uiapp 取消收藏
Mock.mock('/api/favorites/remove', 'post', (options) => {
  return {
    code: 200,
    message: '取消收藏成功',
    data: null
  };
});

// 获取我的收藏列表
Mock.mock(RegExp('/api/favorites/my.*'), 'get', (options) => {
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
  
  // 过滤数据
  let filteredFavorites = [...favoriteData.list];
  
  if (keyword) {
    filteredFavorites = filteredFavorites.filter(fav => 
      fav.jobTitle.includes(keyword) || 
      fav.companyName.includes(keyword)
    );
  }
  
  // 按收藏时间倒序
  filteredFavorites.sort((a, b) => 
    new Date(b.favoriteTime) - new Date(a.favoriteTime)
  );
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedFavorites = filteredFavorites.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedFavorites,
      total: filteredFavorites.length,
      page,
      pageSize
    }
  };
});

// uiapp 获取收藏列表
Mock.mock(RegExp('/api/favorites/list.*'), 'get', (options) => {
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
  
  let filteredFavorites = [...favoriteData.list];
  
  filteredFavorites.sort((a, b) => 
    new Date(b.favoriteTime) - new Date(a.favoriteTime)
  );
  
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedFavorites = filteredFavorites.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedFavorites,
      total: filteredFavorites.length,
      page,
      pageSize
    }
  };
});

// 检查是否已收藏
Mock.mock(RegExp('/api/favorites/check.*'), 'get', (options) => {
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
  // 随机返回已收藏或未收藏
  const hasFavorited = Math.random() > 0.5;
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      hasFavorited,
      favoriteId: hasFavorited ? Math.floor(Math.random() * 100) : null
    }
  };
});

// 收藏统计
Mock.mock('/api/favorites/statistics', 'get', () => {
  const stats = {
    total: favoriteData.list.length,
    active: favoriteData.list.filter(f => f.jobStatus === 1).length,
    expired: favoriteData.list.filter(f => f.jobStatus === 2).length
  };
  
  return {
    code: 200,
    message: '获取成功',
    data: stats
  };
});

// 收藏岗位详情（用于收藏列表显示）
Mock.mock(RegExp('/api/favorites/\\d+/job'), 'get', (options) => {
  // 从 URL 中提取收藏 ID
  const id = parseInt(options.url.match(/\/api\/favorites\/(\d+)\/job/)[1]);
  const favorite = favoriteData.list.find(f => f.id === id) || favoriteData.list[0];
  
  const jobDetail = {
    ...favorite,
    description: Mock.mock('@cparagraph(5, 10)'),
    requirements: Mock.mock('@cparagraph(3, 5)'),
    welfare: Mock.mock('@cword(3, 5)'),
    publishTime: Mock.mock('@datetime'),
    viewCount: Mock.mock('@integer(100, 1000)'),
    applyCount: Mock.mock('@integer(10, 100)'),
    hrId: Mock.mock('@integer(101, 200)'),
    hrName: Mock.mock('@cname'),
    hrPosition: Mock.mock('@ctitle(2, 4)'),
    companyId: favorite.companyId,
    companyName: favorite.companyName,
    companyLogo: favorite.companyLogo,
    companySize: Mock.mock('@integer(20, 500)人'),
    companyIndustry: Mock.mock('@ctitle(2, 4)'),
    companyDescription: Mock.mock('@cparagraph(5, 10)')
  };
  
  return {
    code: 200,
    message: '获取成功',
    data: jobDetail
  };
});
