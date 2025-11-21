import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Navigation from '../views/Navigation.vue'
import UserSettings from '../views/UserSettings.vue'
import PrivateMessage from "@/views/PrivateMessage.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'Login', component: Login},
    { path: '/settings', name: 'UserSettings', component: UserSettings, meta: { requiresAuth: true }},
    { path: '/home', name: 'Home', component: Navigation, meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/home/privatemessage'
      },
      {
        path:'privatemessage',
        name: 'PrivateMessage',
        component: () => import('../views/PrivateMessage.vue'),
        meta: { requiresAuth: true },
        children: [
          {
            path: ':friendId',
            name: 'Message',
            component: () => import('../views/Message.vue'),
            meta: { requiresAuth: true }
          }
        ],
      },
    ]},

  ],
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userInfo = localStorage.getItem('userInfo')

  if (to.meta.requiresAuth && !userInfo) {
    // 需要登录但未登录，跳转到登录页
    next('/login')
  } else if (to.path === '/login' && userInfo) {
    // 已登录访问登录页，跳转到聊天页
    next('/home')
  } else {
    next()
  }
})

export default router
