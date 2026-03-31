<template>
  <div class="login-container">
    <h2>宿舍报修管理系统</h2>
    <div class="role-switch">
      <label><input type="radio" value="student" v-model="role" /> 学生</label>
      <label><input type="radio" value="admin" v-model="role" /> 管理员</label>
    </div>
    <el-input
        v-model="username"
        :placeholder="role === 'student' ? '学号' : '工号'"
        clearable
    />
    <el-input
        v-model="password"
        type="password"
        placeholder="密码"
        show-password
        @keyup.enter="login"
    />
    <el-button type="primary" @click="login" :loading="loading" style="width: 100%; margin-top: 10px;">
      登录
    </el-button>
    <p class="register-link">还没有账号？<router-link to="/register">立即注册</router-link></p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { studentLogin } from '@/api/student'
import { adminLogin } from '@/api/admin'

type Role = 'student' | 'admin'

const role = ref<Role>('student')
const username = ref('')
const password = ref('')
const loading = ref(false)
const router = useRouter()

// 解析 JWT payload
const parseJwt = (token: string) => {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
    return JSON.parse(jsonPayload)
  } catch (e) {
    console.error('JWT解析失败', e)
    return null
  }
}

const login = async () => {
  if (!username.value.trim() || !password.value.trim()) {
    ElMessage.warning('请填写完整信息')
    return
  }

  if (role.value === 'admin') {
    const jobNumber = Number(username.value)
    if (isNaN(jobNumber)) {
      ElMessage.warning('工号必须是数字')
      return
    }
  }

  loading.value = true
  try {
    let res: any
    if (role.value === 'student') {
      res = await studentLogin({ id: username.value, password: password.value })
    } else {
      res = await adminLogin({ id: username.value, password: password.value })
    }

    if (res.code === 200) {
      const token = res.data
      localStorage.setItem('token', token)
      // 关键修复：存储的 role 值必须与路由 meta 中的 role 一致（'student' 或 'admin'）
      localStorage.setItem('role', role.value)   // 直接存储 'student' 或 'admin'

      // 解析 token 获取用户ID
      const decoded = parseJwt(token)
      if (decoded && decoded.sub) {
        localStorage.setItem('userId', decoded.sub)
      } else {
        localStorage.setItem('userId', username.value)
      }

      // 跳转前可打印日志确认
      const target = role.value === 'student' ? '/student' : '/admin'
      console.log('准备跳转到:', target)
      await router.push(target)
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (err: any) {
    console.error(err)
    ElMessage.error(err.message || '请求失败，请检查网络或后端服务')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  max-width: 300px;
  margin: 100px auto;
  text-align: center;
}
.role-switch {
  margin: 15px 0;
}
.register-link {
  margin-top: 15px;
  font-size: 14px;
}
</style>