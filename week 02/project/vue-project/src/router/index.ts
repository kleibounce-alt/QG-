import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'   // 新增注册页面
import StudentHome from '@/views/StudentHome.vue'
import AdminHome from '@/views/AdminHome.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login },
    { path: '/register', component: Register },   // 注册路由，无需认证
    { path: '/student', component: StudentHome, meta: { requiresAuth: true, role: 'student' } },
    { path: '/admin', component: AdminHome, meta: { requiresAuth: true, role: 'admin' } }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  // 放行注册页面
  if (to.path === '/register') {
    next()
    return
  }
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.role && to.meta.role !== role) {
    next('/login')
  } else {
    next()
  }
})

export default router