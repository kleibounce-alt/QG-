<template>
  <div class="login-container">
    <h2>宿舍报修管理系统</h2>
    <div class="role-switch">
      <label><input type="radio" value="student" v-model="role" /> 学生</label>
      <label><input type="radio" value="admin" v-model="role" /> 管理员</label>
    </div>
    <input type="text" v-model="username" :placeholder="role === 'student' ? '学号' : '工号'" />
    <input type="password" v-model="password" placeholder="密码" @keyup.enter="login" />
    <button @click="login" :disabled="loading">登录</button>
    <p class="register-link">还没有账号？<router-link to="/register">立即注册</router-link></p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { studentLogin } from '@/api/student'
import { adminLogin } from '@/api/admin'

type Role = 'student' | 'admin'

const role = ref<Role>('student')
const username = ref('')
const password = ref('')
const loading = ref(false)
const router = useRouter()

// 手动解析 JWT 的 payload（不依赖额外库）
const parseJwt = (token: string) => {
  try {
    let replace: {
      (searchValue: { [Symbol.replace](string: string, replaceValue: string): string }, replaceValue: string): string;
      (searchValue: {
        [Symbol.replace](string: string, replacer: (substring: string, ...args: any[]) => string): string
      }, replacer: (substring: string, ...args: any[]) => string): string;
      (searchValue: (string | RegExp), replaceValue: string): string;
      (searchValue: (string | RegExp), replacer: (substring: string, ...args: any[]) => string): string
    };
    ({replace: replace} = token.split('.')[1]);
    const base64 = replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
};

const login = async () => {
  if (!username.value.trim() || !password.value.trim()) {
    alert('请填写完整信息')
    return
  }

  if (role.value === 'admin') {
    const jobNumber = Number(username.value)
    if (isNaN(jobNumber)) {
      alert('工号必须是数字')
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
      localStorage.setItem('role', role.value)
      // 解析 token 获取用户ID（带前导零的字符串）
      const decoded = parseJwt(token)
      if (decoded && decoded.sub) {
        localStorage.setItem('userId', decoded.sub)
      }
      router.push(role.value === 'student' ? '/student' : '/admin')
    } else {
      alert(res.message || '登录失败')
    }
  } catch (err: any) {
    console.error(err)
    alert(err.message || '请求失败，请检查网络或后端服务')
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
input, button {
  display: block;
  width: 100%;
  margin: 10px 0;
  padding: 8px;
  box-sizing: border-box;
}
.role-switch {
  margin: 15px 0;
}
.register-link {
  margin-top: 15px;
  font-size: 14px;
}
</style>