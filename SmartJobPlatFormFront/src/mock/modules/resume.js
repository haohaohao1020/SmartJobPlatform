import Mock from 'mockjs';

/**
 * 简历相关 Mock 数据接口
 */

// 生成简历数据
const resumeData = Mock.mock({
  'id': 1,
  'userId': 1,
  'userName': '@cname',
  'avatar': "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20portrait%20avatar&image_size=square",
  'gender|1': [1, 2],
  'birthDate': '@date("yyyy-MM-dd")',
  'phone': /^1[3456789]\d{9}$/,
  'email': '@email',
  'currentCity': '@city',
  'jobIntention': '@ctitle(4, 8)',
  'expectedSalaryMin': '@integer(8000, 15000)',
  'expectedSalaryMax': '@integer(15000, 30000)',
  'expectedCity': '@city',
  'workStatus|1': ['在职，考虑机会', '在职，暂不考虑', '离职，随时到岗'],
  'highestEducation|1': ['大专', '本科', '硕士', '博士'],
  'graduateSchool': '@ctitle(4, 10)大学',
  'major': '@ctitle(4, 8)',
  'graduateDate': '@date("yyyy-MM")',
  'workYears|1-10': 1,
  'selfIntroduction': '@cparagraph(3, 5)',
  'createTime': '@datetime',
  'updateTime': '@datetime',
  'educations|1-3': [
    {
      'id|+1': 1,
      'schoolName': '@ctitle(4, 10)大学',
      'major': '@ctitle(4, 8)',
      'education|1': ['大专', '本科', '硕士', '博士'],
      'startDate': '@date("yyyy-MM")',
      'endDate': '@date("yyyy-MM")',
      'description': '@cparagraph(1, 2)'
    }
  ],
  'workExperiences|1-3': [
    {
      'id|+1': 1,
      'companyName': '@ctitle(4, 8)有限公司',
      'position': '@ctitle(2, 4)',
      'startDate': '@date("yyyy-MM")',
      'endDate': '@date("yyyy-MM")',
      'isCurrent|1': [true, false],
      'salaryMin': '@integer(8000, 15000)',
      'salaryMax': '@integer(15000, 30000)',
      'workDescription': '@cparagraph(3, 5)',
      'achievements': '@cparagraph(2, 3)'
    }
  ],
  'projectExperiences|0-2': [
    {
      'id|+1': 1,
      'projectName': '@ctitle(4, 10)',
      'role': '@ctitle(2, 4)',
      'startDate': '@date("yyyy-MM")',
      'endDate': '@date("yyyy-MM")',
      'projectDescription': '@cparagraph(2, 4)',
      'responsibilities': '@cparagraph(2, 3)',
      'achievements': '@cparagraph(1, 2)'
    }
  ],
  'skills|3-8': [
    {
      'id|+1': 1,
      'skillName': '@ctitle(2, 4)',
      'proficiency|1': ['熟练', '掌握', '了解']
    }
  ],
  'certificates|0-3': [
    {
      'id|+1': 1,
      'certificateName': '@ctitle(4, 8)证书',
      'issuingAuthority': '@ctitle(4, 8)',
      'issueDate': '@date("yyyy-MM")',
      'certificateNo': /^[A-Z0-9]{8,16}$/
    }
  ]
});

// HR 端简历列表数据
const hrResumeList = Mock.mock({
  'list|30': [
    {
      'id|+1': 1,
      'resumeId|+1': 1,
      'userId|+1': 1,
      'userName': '@cname',
      'avatar': "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20portrait%20avatar&image_size=square",
      'gender|1': [1, 2],
      'age|22-40': 1,
      'phone': /^1[3456789]\d{9}$/,
      'email': '@email',
      'workYears|1-10': 1,
      'highestEducation|1': ['大专', '本科', '硕士', '博士'],
      'graduateSchool': '@ctitle(4, 10)大学',
      'jobIntention': '@ctitle(4, 8)',
      'expectedSalaryMin': '@integer(8000, 15000)',
      'expectedSalaryMax': '@integer(15000, 30000)',
      'currentCity': '@city',
      'jobId|+1': 1,
      'jobTitle': '@ctitle(4, 10)',
      'companyId': 1,
      'companyName': '智联科技有限公司',
      'applyTime': '@datetime',
      'interviewStatus|1': [1, 2, 3, 4, 5],
      'interviewStatusName|1': ['待查看', '待面试', '面试中', '待Offer', '已拒绝', '已录用'],
      'isRead|1': [true, false],
      'createTime': '@datetime'
    }
  ]
});

// 获取简历详情
Mock.mock(RegExp('/api/resumes/\\d+'), 'get', (options) => {
  return {
    code: 200,
    message: '获取成功',
    data: resumeData
  };
});

// 获取当前用户简历
Mock.mock('/api/resumes/my', 'get', () => {
  return {
    code: 200,
    message: '获取成功',
    data: resumeData
  };
});

// 创建/更新简历
Mock.mock('/api/resumes', 'post', (options) => {
  const body = JSON.parse(options.body);
  return {
    code: 200,
    message: '保存成功',
    data: { ...resumeData, ...body, updateTime: Mock.mock('@datetime') }
  };
});

// 更新简历基本信息
Mock.mock('/api/resumes/basic', 'put', (options) => {
  const body = JSON.parse(options.body);
  return {
    code: 200,
    message: '更新成功',
    data: null
  };
});

// 添加教育经历
Mock.mock('/api/resumes/educations', 'post', (options) => {
  const body = JSON.parse(options.body);
  const newEducation = {
    id: resumeData.educations.length + 1,
    ...body
  };
  resumeData.educations.push(newEducation);
  return {
    code: 200,
    message: '添加成功',
    data: newEducation
  };
});

// 更新教育经历
Mock.mock(RegExp('/api/resumes/educations/\\d+'), 'put', (options) => {
  return {
    code: 200,
    message: '更新成功',
    data: null
  };
});

// 删除教育经历
Mock.mock(RegExp('/api/resumes/educations/\\d+'), 'delete', (options) => {
  return {
    code: 200,
    message: '删除成功',
    data: null
  };
});

// 添加工作经历
Mock.mock('/api/resumes/work-experiences', 'post', (options) => {
  const body = JSON.parse(options.body);
  const newExperience = {
    id: resumeData.workExperiences.length + 1,
    ...body
  };
  resumeData.workExperiences.push(newExperience);
  return {
    code: 200,
    message: '添加成功',
    data: newExperience
  };
});

// 更新工作经历
Mock.mock(RegExp('/api/resumes/work-experiences/\\d+'), 'put', (options) => {
  return {
    code: 200,
    message: '更新成功',
    data: null
  };
});

// 删除工作经历
Mock.mock(RegExp('/api/resumes/work-experiences/\\d+'), 'delete', (options) => {
  return {
    code: 200,
    message: '删除成功',
    data: null
  };
});

// 添加项目经历
Mock.mock('/api/resumes/project-experiences', 'post', (options) => {
  const body = JSON.parse(options.body);
  const newProject = {
    id: resumeData.projectExperiences.length + 1,
    ...body
  };
  resumeData.projectExperiences.push(newProject);
  return {
    code: 200,
    message: '添加成功',
    data: newProject
  };
});

// 更新项目经历
Mock.mock(RegExp('/api/resumes/project-experiences/\\d+'), 'put', (options) => {
  return {
    code: 200,
    message: '更新成功',
    data: null
  };
});

// 删除项目经历
Mock.mock(RegExp('/api/resumes/project-experiences/\\d+'), 'delete', (options) => {
  return {
    code: 200,
    message: '删除成功',
    data: null
  };
});

// HR 获取简历列表
Mock.mock(RegExp('/api/hr/resumes.*'), 'get', (options) => {
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
  const interviewStatus = params.interviewStatus || '';
  const jobId = params.jobId || '';
  
  // 过滤数据
  let filteredResumes = [...hrResumeList.list];
  
  if (keyword) {
    filteredResumes = filteredResumes.filter(resume => 
      resume.userName.includes(keyword) || 
      resume.phone.includes(keyword)
    );
  }
  
  if (interviewStatus) {
    filteredResumes = filteredResumes.filter(resume => 
      resume.interviewStatus === parseInt(interviewStatus)
    );
  }
  
  if (jobId) {
    filteredResumes = filteredResumes.filter(resume => resume.jobId === parseInt(jobId));
  }
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedResumes = filteredResumes.slice(startIndex, endIndex);
  
  return {
    code: 200,
    message: '获取成功',
    data: {
      list: paginatedResumes,
      total: filteredResumes.length,
      page,
      pageSize
    }
  };
});

// HR 更新面试状态
Mock.mock(RegExp('/api/hr/resumes/\\d+/status'), 'put', (options) => {
  const body = JSON.parse(options.body);
  return {
    code: 200,
    message: '状态更新成功',
    data: null
  };
});

// HR 标记简历为已读
Mock.mock(RegExp('/api/hr/resumes/\\d+/read'), 'put', (options) => {
  return {
    code: 200,
    message: '标记成功',
    data: null
  };
});
