import { defineStore } from 'pinia';

/**
 * 用户状态管理
 * 存储用户信息、登录状态等
 */
export const useUserStore = defineStore('user', {
  state: () => ({
    // 用户信息
    userInfo: null,
    // 角色类型: jobseeker | hr | admin
    role: null,
    // 登录状态
    isLoggedIn: false,
    // Token
    token: ''
  }),

  getters: {
    // 获取用户名
    getUserName: (state) => state.userInfo?.name || '未登录',
    // 获取用户头像
    getUserAvatar: (state) => state.userInfo?.avatar || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20avatar%20icon&image_size=square',
    // 检查是否已登录
    isAuthenticated: (state) => state.isLoggedIn && state.token
  },

  actions: {
    /**
     * 设置用户信息
     * @param {Object} userInfo - 用户信息对象
     */
    setUserInfo(userInfo) {
      this.userInfo = userInfo;
      this.role = userInfo.role;
      this.isLoggedIn = true;
      this.token = userInfo.token;
      
      // 保存到本地存储
      localStorage.setItem('userInfo', JSON.stringify(userInfo));
      localStorage.setItem('token', userInfo.token);
    },

    /**
     * 清除用户信息（退出登录）
     */
    clearUserInfo() {
      this.userInfo = null;
      this.role = null;
      this.isLoggedIn = false;
      this.token = '';
      
      // 清除本地存储
      localStorage.removeItem('userInfo');
      localStorage.removeItem('token');
    },

    /**
     * 从本地存储恢复用户信息
     */
    restoreUserInfo() {
      const userInfo = localStorage.getItem('userInfo');
      const token = localStorage.getItem('token');
      
      if (userInfo && token) {
        try {
          const parsedUserInfo = JSON.parse(userInfo);
          this.userInfo = parsedUserInfo;
          this.role = parsedUserInfo.role;
          this.isLoggedIn = true;
          this.token = token;
        } catch (e) {
          console.error('解析用户信息失败:', e);
          this.clearUserInfo();
        }
      }
    }
  }
});
