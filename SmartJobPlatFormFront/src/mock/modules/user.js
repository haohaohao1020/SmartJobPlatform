import Mock from 'mockjs';

/**
 * 用户相关 Mock 数据接口
 */

// 生成求职者用户数据
const jobSeekerUsers = Mock.mock({
  'list|10': [
    {
      'id|+1': 1,
      'name': '@cname',
      'phone': /^1[3456789]\d{9}$/,
      'email': '@email',
      'avatar': "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20portrait%20avatar&image_size=square",
      'role': 'jobseeker',
      'status|1': [1, 2], // 1: 正常, 2: 禁用
      'createTime': '@datetime'
    }
  ]
});

// 生成 HR 用户数据
const hrUsers = Mock.mock({
  'list|5': [
    {
      'id|+1': 101,
      'name': '@cname',
      'phone': /^1[3456789]\d{9}$/,
      'email': '@email',
      'avatar': "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=business%20professional%20avatar&image_size=square",
      'role': 'hr',
      'companyId': '@integer(1, 10)',
      'companyName': '@ctitle(4, 8)有限公司',
      'status|1': [1, 2],
      'createTime': '@datetime'
    }
  ]
});

// 生成管理员数据
const adminUsers = [
  {
    id: 999,
    name: 'admin',
    phone: '13800138000',
    email: 'admin@jobplatform.com',
    avatar: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=administrator%20avatar%20icon&image_size=square",
    role: 'admin',
    status: 1,
    createTime: '2023-01-01 00:00:00'
  }
];

// 求职者登录
Mock.mock('/api/jobseeker/login', 'post', (options) => {
  const body = JSON.parse(options.body);
  const { phone, password } = body;
  
  // 模拟验证
  if (phone && password) {
    const user = {
      id: 1,
      name: '张三',
      phone: phone,
      email: 'zhangsan@example.com',
      avatar: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20portrait%20avatar&image_size=square",
      role: 'jobseeker',
      token: 'jobseeker_token_' + Date.now()
    };
    return {
      code: 200,
      message: '登录成功',
      data: user
    };
  }
  return {
    code: 400,
    message: '手机号或密码错误',
    data: null
  };
});

// HR 登录
Mock.mock('/api/hr/login', 'post', (options) => {
  const body = JSON.parse(options.body);
  const { phone, password } = body;
  
  if (phone && password) {
    const user = {
      id: 101,
      name: '李经理',
      phone: phone,
      email: 'hr@company.com',
      avatar: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=business%20professional%20avatar&image_size=square",
      role: 'hr',
      companyId: 1,
      companyName: '智联科技有限公司',
      token: 'hr_token_' + Date.now()
    };
    return {
      code: 200,
      message: '登录成功',
      data: user
    };
  }
  return {
    code: 400,
    message: '手机号或密码错误',
    data: null
  };
});

// 管理员登录
Mock.mock('/api/admin/login', 'post', (options) => {
  const body = JSON.parse(options.body);
  const { username, password } = body;
  
  if (username === 'admin' && password === 'admin123') {
    const user = {
      id: 999,
      name: '系统管理员',
      username: 'admin',
      avatar: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=administrator%20avatar%20icon&image_size=square",
      role: 'admin',
      token: 'admin_token_' + Date.now()
    };
    return {
      code: 200,
      message: '登录成功',
      data: user
    };
  }
  return {
    code: 400,
    message: '用户名或密码错误',
    data: null
  };
});

// 获取用户信息
Mock.mock(RegExp('/api/user/info.*'), 'get', (options) => {
  // 从 URL 中获取角色类型
  const url = options.url;
  let role = 'jobseeker';
  if (url.includes('hr')) role = 'hr';
  if (url.includes('admin')) role = 'admin';
  
  let userInfo = {};
  switch (role) {
    case 'jobseeker':
      userInfo = {
        id: 1,
        name: '张三',
        phone: '13800138000',
        email: 'zhangsan@example.com',
        avatar: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20portrait%20avatar&image_size=square",
        gender: 1,
        birthDate: '1995-05-15',
        workYears: 3,
        highestEducation: '本科',
        currentCity: '北京市',
        phoneStatus: 1,
        emailStatus: 1,
        createTime: '2023-01-15 10:30:00'
      };
      break;
    case 'hr':
      userInfo = {
        id: 101,
        name: '李经理',
        phone: '13900139000',
        email: 'hr@company.com',
        avatar: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=business%20professional%20avatar&image_size=square",
        companyId: 1,
        companyName: '智联科技有限公司',
        position: '人力资源经理',
        phoneStatus: 1,
        emailStatus: 1,
        createTime: '2022-06-20 09:00:00'
      };
      break;
    case 'admin':
      userInfo = {
        id: 999,
        name: '系统管理员',
        username: 'admin',
        avatar: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=administrator%20avatar%20icon&image_size=square",
        role: 'admin',
        lastLoginTime: '2023-10-01 08:30:00'
      };
      break;
  }
  
  return {
    code: 200,
    message: '获取成功',
    data: userInfo
  };
});

// 更新用户信息
Mock.mock('/api/user/profile', 'put', (options) => {
  const body = JSON.parse(options.body);
  return {
    code: 200,
    message: '更新成功',
    data: body
  };
});

// 修改密码
Mock.mock('/api/user/password', 'put', (options) => {
  const body = JSON.parse(options.body);
  const { oldPassword, newPassword } = body;
  
  // 模拟验证旧密码
  if (oldPassword) {
    return {
      code: 200,
      message: '密码修改成功',
      data: null
    };
  }
  return {
    code: 400,
    message: '原密码错误',
    data: null
  };
});

// 上传头像
Mock.mock('/api/user/avatar', 'post', () => {
  return {
    code: 200,
    message: '上传成功',
    data: {
      url: "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=new%20avatar%20portrait&image_size=square"
    }
  };
});

// 管理员获取用户列表
Mock.mock(RegExp('/api/admin/users.*'), 'get', (options) => {
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
  let filteredUsers = [...jobSeekerUsers.list, ...hrUsers.list];
  if (keyword) {
    filteredUsers = filteredUsers.filter(user => 
      user.name.includes(keyword) || 
      user.phone.includes(keyword)
    );
  }
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedUsers = filteredUsers.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedUsers,
      total: filteredUsers.length,
      page,
      pageSize
    }
  };
});

// 禁用/启用用户
Mock.mock(RegExp('/api/admin/users/\\d+/status'), 'put', (options) => {
  return {
    code: 200,
    message: '操作成功',
    data: null
  };
});
