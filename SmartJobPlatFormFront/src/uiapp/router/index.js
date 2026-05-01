import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    redirect: '/jobs'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/uiapp/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/uiapp/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/uiapp/layouts/MainLayout.vue'),
    children: [
      {
        path: 'jobs',
        name: 'Jobs',
        component: () => import('@/uiapp/views/jobs/Jobs.vue'),
        meta: { title: '首页', requireAuth: false }
      },
      {
        path: 'applications',
        name: 'Applications',
        component: () => import('@/uiapp/views/applications/Applications.vue'),
        meta: { title: '我的投递', requireAuth: true }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('@/uiapp/views/favorites/Favorites.vue'),
        meta: { title: '我的收藏', requireAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/uiapp/views/profile/Profile.vue'),
        meta: { title: '我的', requireAuth: true }
      }
    ]
  },
  {
    path: '/jobs/:id',
    name: 'JobDetail',
    component: () => import('@/uiapp/views/jobs/JobDetail.vue'),
    meta: { title: '岗位详情', requireAuth: false }
  },
  {
    path: '/resume',
    name: 'Resume',
    component: () => import('@/uiapp/views/resume/Resume.vue'),
    meta: { title: '简历管理', requireAuth: true }
  },
  {
    path: '/resume/edit',
    name: 'ResumeEdit',
    component: () => import('@/uiapp/views/resume/ResumeEdit.vue'),
    meta: { title: '编辑简历', requireAuth: true }
  },
  {
    path: '/resume/preview',
    name: 'ResumePreview',
    component: () => import('@/uiapp/views/resume/ResumePreview.vue'),
    meta: { title: '简历预览', requireAuth: true }
  },
  {
    path: '/profile/edit',
    name: 'ProfileEdit',
    component: () => import('@/uiapp/views/profile/ProfileEdit.vue'),
    meta: { title: '编辑资料', requireAuth: true }
  },
  {
    path: '/profile/password',
    name: 'ChangePassword',
    component: () => import('@/uiapp/views/profile/ChangePassword.vue'),
    meta: { title: '修改密码', requireAuth: true }
  },
  {
    path: '*',
    name: 'NotFound',
    component: () => import('@/uiapp/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
];

const isStandalone = typeof process !== 'undefined' && process.env && process.env.UIAPP_STANDALONE === 'true';
const routerBase = isStandalone ? '/' : '/uiapp/';

const router = new VueRouter({
  mode: 'history',
  base: routerBase,
  routes
});

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title + ' - 智慧招聘';
  }
  
  const token = localStorage.getItem('uiapp_token');
  const userInfo = localStorage.getItem('uiapp_user_info');
  
  if (to.meta.requireAuth && (!token || !userInfo)) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    });
  } else {
    next();
  }
});

export default router;
