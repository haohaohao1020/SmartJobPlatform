import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/web/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/web/layouts/MainLayout.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/web/views/Dashboard.vue'),
        meta: { title: '数据控制台', requireAuth: true, roles: ['hr', 'admin'] }
      },
      {
        path: 'jobs',
        name: 'Jobs',
        component: () => import('@/web/views/jobs/Jobs.vue'),
        meta: { title: '岗位管理', requireAuth: true, roles: ['hr', 'admin'] }
      },
      {
        path: 'jobs/publish',
        name: 'JobPublish',
        component: () => import('@/web/views/jobs/JobPublish.vue'),
        meta: { title: '发布岗位', requireAuth: true, roles: ['hr'] }
      },
      {
        path: 'jobs/edit/:id',
        name: 'JobEdit',
        component: () => import('@/web/views/jobs/JobPublish.vue'),
        meta: { title: '编辑岗位', requireAuth: true, roles: ['hr'] }
      },
      {
        path: 'resumes',
        name: 'Resumes',
        component: () => import('@/web/views/resumes/Resumes.vue'),
        meta: { title: '简历管理', requireAuth: true, roles: ['hr'] }
      },
      {
        path: 'resumes/:id',
        name: 'ResumeDetail',
        component: () => import('@/web/views/resumes/ResumeDetail.vue'),
        meta: { title: '简历详情', requireAuth: true, roles: ['hr'] }
      },
      {
        path: 'company',
        name: 'Company',
        component: () => import('@/web/views/company/Company.vue'),
        meta: { title: '企业信息', requireAuth: true, roles: ['hr'] }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/web/views/users/Users.vue'),
        meta: { title: '用户管理', requireAuth: true, roles: ['admin'] }
      },
      {
        path: 'companies',
        name: 'Companies',
        component: () => import('@/web/views/companies/Companies.vue'),
        meta: { title: '企业审核', requireAuth: true, roles: ['admin'] }
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: () => import('@/web/views/announcements/Announcements.vue'),
        meta: { title: '公告管理', requireAuth: true, roles: ['admin'] }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/web/views/settings/Settings.vue'),
        meta: { title: '系统配置', requireAuth: true, roles: ['admin'] }
      }
    ]
  },
  {
    path: '*',
    name: 'NotFound',
    component: () => import('@/web/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
];

const router = new VueRouter({
  mode: 'history',
  base: '/web/',
  routes
});

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title + ' - 智慧招聘管理后台';
  }
  
  const token = localStorage.getItem('web_token');
  const userInfo = localStorage.getItem('web_user_info');
  
  if (to.meta.requireAuth && (!token || !userInfo)) {
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    });
    return;
  }
  
  if (userInfo && to.meta.roles) {
    let userRole = '';
    try {
      const user = JSON.parse(userInfo);
      userRole = user.role;
    } catch (e) {
      console.error('解析用户信息失败:', e);
    }
    
    if (!to.meta.roles.includes(userRole)) {
      next({ path: '/404' });
      return;
    }
  }
  
  if (to.path === '/login' && token && userInfo) {
    next({ path: '/dashboard' });
    return;
  }
  
  next();
});

export default router;
