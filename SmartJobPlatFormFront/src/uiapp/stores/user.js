import { defineStore } from 'pinia';

export const useUserStore = defineStore('uiappUser', {
  state: () => ({
    token: '',
    userInfo: null
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    userName: (state) => state.userInfo?.name || '求职者',
    avatar: (state) => state.userInfo?.avatar || ''
  },
  
  actions: {
    setToken(token) {
      this.token = token;
      localStorage.setItem('uiapp_token', token);
    },
    
    setUserInfo(userInfo) {
      this.userInfo = userInfo;
      localStorage.setItem('uiapp_user_info', JSON.stringify(userInfo));
    },
    
    login(token, userInfo) {
      this.setToken(token);
      this.setUserInfo(userInfo);
    },
    
    logout() {
      this.token = '';
      this.userInfo = null;
      localStorage.removeItem('uiapp_token');
      localStorage.removeItem('uiapp_user_info');
    },
    
    restoreFromStorage() {
      const token = localStorage.getItem('uiapp_token');
      const userInfoStr = localStorage.getItem('uiapp_user_info');
      
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
    }
  }
});
