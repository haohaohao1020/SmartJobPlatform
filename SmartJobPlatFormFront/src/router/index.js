import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

// 公共路由
const routes = [
  // 首页/登录选择
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/Index.vue'),
    meta: { title: '首页' }
  },

  // 求职者端路由
  {
    path: '/jobseeker',
    name: 'JobSeekerLayout',
    component: () => import('@/layouts/JobSeekerLayout.vue'),
    redirect: '/jobseeker/jobs',
    children: [
      // 岗位列表
      {
        path: 'jobs',
        name: 'JobSeekerJobs',
        component: () => import('@/views/jobseeker/Jobs.vue'),
        meta: { title: '岗位列表' }
      },
      // 岗位详情
      {
        path: 'jobs/:id',
        name: 'JobJobDetail',
        component: () => import('@/views/jobseeker/JobDetail.vue'),
        meta: { title: '岗位详情' }
      },
      // 我的投递
      {
        path: 'applications',
        name: 'JobSeekerApplications',
        component: () => import('@/views/jobseeker/Applications.vue'),
        meta: { title: '我的投递' }
      },
      // 我的收藏
      {
        path: 'favorites',
        name: 'JobSeekerFavorites',
        component: () => import('@/views/jobseeker/Favorites.vue'),
        meta: { title: '我的收藏' }
      },
      // 简历管理
      {
        path: 'resume',
        name: 'JobSeekerResume',
        component: () => import('@/views/jobseeker/Resume.vue'),
        meta: { title: '简历管理' }
      },
      // 简历详情/预览
      {
        path: 'resume/preview',
        name: 'JobResumePreview',
        component: () => import('@/views/jobseeker/ResumePreview.vue'),
        meta: { title: '简历预览' }
      },
      // 个人中心
      {
        path: 'profile',
        name: 'JobSeekerProfile',
        component: () => import('@/views/jobseeker/Profile.vue'),
        meta: { title: '个人中心' }
      },
      // 登录
      {
        path: 'login',
        name: 'JobSeekerLogin',
        component: () => import('@/views/jobseeker/Login.vue'),
        meta: { title: '求职者登录' }
      }
    ]
  },

  // 企业HR端路由
  {
    path: '/hr',
    name: 'HRLayout',
    component: () => import('@/layouts/HRLayout.vue'),
    redirect: '/hr/jobs',
    children: [
      // 岗位管理
      {
        path: 'jobs',
        name: 'HRJobs',
        component: () => import('@/views/hr/Jobs.vue'),
        meta: { title: '岗位管理' }
      },
      // 发布岗位
      {
        path: 'jobs/publish',
        name: 'JobPublish',
        component: () => import('@/views/hr/JobPublish.vue'),
        meta: { title: '发布岗位' }
      },
      // 编辑岗位
      {
        path: 'jobs/edit/:id',
        name: 'JobEdit',
        component: () => import('@/views/hr/JobPublish.vue'),
        meta: { title: '编辑岗位' }
      },
      // 简历列表
      {
        path: 'resumes',
        name: 'HRResumes',
        component: () => import('@/views/hr/Resumes.vue'),
        meta: { title: '简历管理' }
      },
      // 简历详情
      {
        path: 'resumes/:id',
        name: 'HRResumeDetail',
        component: () => import('@/views/hr/ResumeDetail.vue'),
        meta: { title: '简历详情' }
      },
      // 企业信息
      {
        path: 'company',
        name: 'HRCompany',
        component: () => import('@/views/hr/Company.vue'),
        meta: { title: '企业信息' }
      },
      // 登录
      {
        path: 'login',
        name: 'HRLogin',
        component: () => import('@/views/hr/Login.vue'),
        meta: { title: 'HR登录' }
      }
    ]
  },

  // 管理员端路由
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      // 数据控制台
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '数据控制台' }
      },
      // 用户管理
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理' }
      },
      // 企业审核
      {
        path: 'companies',
        name: 'AdminCompanies',
        component: () => import('@/views/admin/Companies.vue'),
        meta: { title: '企业审核' }
      },
      // 岗位审核
      {
        path: 'jobs',
        name: 'AdminJobs',
        component: () => import('@/views/admin/Jobs.vue'),
        meta: { title: '岗位审核' }
      },
      // 公告管理
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: () => import('@/views/admin/Announcements.vue'),
        meta: { title: '公告管理' }
      },
      // 系统配置
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('@/views/admin/Settings.vue'),
        meta: { title: '系统配置' }
      },
      // 登录
      {
        path: 'login',
        name: 'AdminLogin',
        component: () => import('@/views/admin/Login.vue'),
        meta: { title: '管理员登录' }
      }
    ]
  },

  // 404页面
  {
    path: '*',
    name: 'NotFound',
    component: () => import('@/views/404.vue'),
    meta: { title: '页面不存在' }
  }
];

const router = new VueRouter({
  mode: 'history',
  base: '/',
  routes
});

// 路由守卫 - 处理页面标题
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title + ' - 智慧招聘平台';
  }
  next();
});

export default router;
