import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import StudentHome from '@/views/StudentHome.vue'
import AdminHome from '@/views/AdminHome.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    { path: '/student', component: StudentHome, meta: { requiresAuth: true, role: 'student' } },
    { path: '/admin', component: AdminHome, meta: { requiresAuth: true, role: 'admin' } }
  ]
})

router.beforeEach((to, from) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  // 放行注册页面
  if (to.path === '/register') {
    return true
  }

  // 需要认证的路由
  if (to.meta.requiresAuth && !token) {
    return '/login'
  }

  // 角色校验
  if (to.meta.role && to.meta.role !== role) {
    return '/login'
  }

  return true
})

export default router