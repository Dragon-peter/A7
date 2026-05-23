import { createRouter, createWebHistory } from 'vue-router'

import reg from "@/views/reg.vue";
import login from "@/views/login.vue";
import Home from "@/views/Home.vue";
import Start from "@/views/Start.vue";
import AI from "@/views/AI.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: login,
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      name: 'start',
      component: Start,
      meta: { requiresAuth: false }
    },
    {
      path: '/reg',
      name: 'reg',
      component: reg,
      meta: { requiresAuth: false }
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('../views/Home.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: to => {
            const userRole = localStorage.getItem('userRole')
            if (userRole === 'teacher') return '/home/teacher/courses'
            if (userRole === 'admin') return '/home/admin/dashboard'
            return '/home/student/courses'
          },
          meta: { requiresAuth: true }
        },
        {
          path: 'ai',
          component: () => import('../views/AI.vue'),
          meta: { requiresAuth: true }
        },
        // 学生路由
        {
          path: 'student/messages',
          component: () => import('../views/student/Messages.vue'),
          meta: { requiresAuth: true, role: 'student' }
        },
        {
          path: 'student/progress',
          component: () => import('../views/student/Progress.vue'),
          meta: { requiresAuth: true, role: 'student' }
        },
        {
          path: 'student/questions',
          component: () => import('../views/student/Questions.vue'),
          meta: { requiresAuth: true, role: 'student' }
        },
        {
          path: 'student/courses',
          component: () => import('../views/teaching/Courses.vue'),
          meta: { requiresAuth: true, role: 'student' }
        },
        {
          path: 'student/learning',
          component: () => import('../views/teaching/Learning.vue'),
          meta: { requiresAuth: true, role: 'student' }
        },
        {
          path: 'student/course/:id',
          component: () => import('../views/student/CourseDetail.vue'),
          meta: { requiresAuth: true, role: 'student' }
        },
        // 教师路由
        {
          path: 'teacher/courses',
          component: () => import('../views/teaching/Courses.vue'),
          meta: { requiresAuth: true, role: 'teacher' }
        },
        {
          path: 'teacher/teaching',
          component: () => import('../views/teaching/Teaching.vue'),
          meta: { requiresAuth: true, role: 'teacher' }
        },
        {
          path: 'teacher/assessment',
          component: () => import('../views/teaching/Assessment.vue'),
          meta: { requiresAuth: true, role: 'teacher' }
        },
        {
          path: 'teacher/learning',
          component: () => import('../views/teaching/Learning.vue'),
          meta: { requiresAuth: true, role: 'teacher' }
        },
        {
          path: 'teacher/analysis',
          component: () => import('../views/teaching/Analysis.vue'),
          meta: { requiresAuth: true, role: 'teacher' }
        },
        // 管理员路由
        {
          path: 'admin/dashboard',
          component: () => import('../views/admin/Dashboard.vue'),
          meta: { requiresAuth: true, role: 'admin' }
        },
        {
          path: 'admin/users',
          component: () => import('../views/admin/UserManagement.vue'),
          meta: { requiresAuth: true, role: 'admin' }
        },
        {
          path: 'admin/resources',
          component: () => import('../views/admin/ResourceManagement.vue'),
          meta: { requiresAuth: true, role: 'admin' }
        },
        {
          path: 'admin/statistics',
          component: () => import('../views/admin/Statistics.vue'),
          meta: { requiresAuth: true, role: 'admin' }
        },
        {
          path: '/account-settings',
          name: 'AccountSettings',
          component: () => import('../views/AccountSettings.vue'),
          meta: {
            requiresAuth: true
          }
        },
      ]
    },
  ]
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  // 清除可能存在的路由跳转过程中的缓存状态
  if (to.path === '/login' || to.path === '/reg' || to.path === '/') {
    // 当访问登录、注册或首页时，清除登录状态，确保需要重新登录
    localStorage.removeItem('userName');
    localStorage.removeItem('userRole');
  }
  
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth !== false);
  const isLoggedIn = localStorage.getItem('userName');
  const userRole = localStorage.getItem('userRole');
  
  // 处理登录状态
  if (requiresAuth && !isLoggedIn) {
    // 未登录，需要身份验证的页面，重定向到登录
    next('/login');
  } else if (!requiresAuth && isLoggedIn && to.path === '/login') {
    // 已登录，访问登录页面，重定向到首页
    next('/home');
  } else if (requiresAuth && isLoggedIn && to.meta.role && to.meta.role !== userRole) {
    // 角色不匹配，重定向到对应角色的首页
    if (userRole === 'admin') {
      next('/home/admin/dashboard');
    } else if (userRole === 'teacher') {
      next('/home/teacher/courses');
    } else {
      next('/home/student/courses');
    }
  } else {
    // 其他情况正常通过
    next();
  }
});

export default router
