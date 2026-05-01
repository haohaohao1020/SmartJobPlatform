import Mock from 'mockjs';

// 配置 Mock.js
Mock.setup({
  timeout: '200-600'
});

// 统一响应格式
const responseFormat = (data, message = 'success') => {
  return {
    code: 200,
    message,
    data
  };
};

// 导入各个模块的 Mock 数据
import './modules/user';
import './modules/job';
import './modules/resume';
import './modules/company';
import './modules/application';
import './modules/favorite';
import './modules/admin';

console.log('Mock 数据已加载');
