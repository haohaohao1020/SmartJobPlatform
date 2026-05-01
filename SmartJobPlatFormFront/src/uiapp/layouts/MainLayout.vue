<template>
  <div class="main-layout">
    <router-view />
    <div class="tab-bar">
      <div 
        v-for="item in tabItems" 
        :key="item.path"
        class="tab-item"
        :class="{ active: isActive(item.path) }"
        @click="goTo(item.path)"
      >
        <div class="tab-icon">
          <i :class="item.icon"></i>
        </div>
        <div class="tab-text">{{ item.name }}</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MainLayout',
  data() {
    return {
      tabItems: [
        { path: '/jobs', name: '首页', icon: 'el-icon-house' },
        { path: '/applications', name: '投递', icon: 'el-icon-document' },
        { path: '/favorites', name: '收藏', icon: 'el-icon-star-off' },
        { path: '/profile', name: '我的', icon: 'el-icon-user' }
      ]
    };
  },
  methods: {
    isActive(path) {
      return this.$route.path === path;
    },
    goTo(path) {
      this.$router.push(path);
    }
  }
};
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  padding-bottom: 60px;
  background: #f5f5f5;
}

.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50px;
  background: #fff;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  z-index: 999;
  max-width: 750px;
  margin: 0 auto;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  height: 100%;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-item.active {
  color: #409eff;
}

.tab-item:not(.active) {
  color: #999;
}

.tab-icon {
  font-size: 20px;
  margin-bottom: 2px;
}

.tab-text {
  font-size: 11px;
}

@media screen and (min-width: 750px) {
  .tab-bar {
    left: 50%;
    transform: translateX(-50%);
  }
}
</style>
