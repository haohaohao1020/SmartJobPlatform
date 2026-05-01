import { defineStore } from 'pinia';

export const useUserStore = defineStore('webUser', {
  state: () => ({
    token: '',
    userInfo: null
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    userName: (state) => state.userInfo?.name || '用户',
    userRole: (state) => state.userInfo?.role || '',
    companyName: (state) => state.userInfo?.companyName || '',
    avatar: (state) => state.userInfo?.avatar || ''
  },
  
  actions: {
    setToken(token) {
      this.token = token;
      localStorage.setItem('web_token', token);
    },
    
    setUserInfo(userInfo) {
      this.userInfo = userInfo;
      localStorage.setItem('web_user_info', JSON.stringify(userInfo));
    },
    
    login(token, userInfo) {
      this.setToken(token);
      this.setUserInfo(userInfo);
    },
    
    logout() {
      this.token = '';
      this.userInfo = null;
      localStorage.removeItem('web_token');
      localStorage.removeItem('web_user_info');
    },
    
    restoreFromStorage() {
      const token = localStorage.getItem('web_token');
      const userInfoStr = localStorage.getItem('web_user_info');
      
      if (token) {
        this.token = token;
      }
      
      if (userInfoStr) {
        try {
          this.userInfo = JSON.parse(userInfoStr);
        } catch (e) {
          console.error('解析用户信息失败:', e);
        }
      }
    },
    
    isHR() {
      return this.userRole === 'hr';
    },
    
    isAdmin() {
      return this.userRole === 'admin';
    }
  }
});
