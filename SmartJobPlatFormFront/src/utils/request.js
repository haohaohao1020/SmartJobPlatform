import axios from 'axios';
import { Message } from 'element-ui';

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么
    // 从 localStorage 获取 token
    const token = localStorage.getItem('token');
    if (token) {
      // 让每个请求携带 token
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    // 对请求错误做些什么
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    
    // 如果返回的状态码不是 200，就判断为错误
    if (res.code !== 200) {
      Message({
        message: res.message || '请求失败',
        type: 'error',
        duration: 3 * 1000
      });

      // 401: 未登录或 token 过期
      if (res.code === 401) {
        // 清除本地存储的用户信息
        localStorage.removeItem('userInfo');
        localStorage.removeItem('token');
        // 跳转到登录页
        setTimeout(() => {
          window.location.href = '/';
        }, 1000);
      }
      return Promise.reject(new Error(res.message || '请求失败'));
    } else {
      return res;
    }
  },
  (error) => {
    console.error('响应错误:', error);
    Message({
      message: error.message || '网络错误，请稍后重试',
      type: 'error',
      duration: 3 * 1000
    });
    return Promise.reject(error);
  }
);

// 封装 get 请求
export function get(url, params) {
  return service({
    url,
    method: 'get',
    params
  });
}

// 封装 post 请求
export function post(url, data) {
  return service({
    url,
    method: 'post',
    data
  });
}

// 封装 put 请求
export function put(url, data) {
  return service({
    url,
    method: 'put',
    data
  });
}

// 封装 delete 请求
export function del(url, params) {
  return service({
    url,
    method: 'delete',
    params
  });
}

export default service;
