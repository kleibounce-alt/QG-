<template>
  <div class="register-container">
    <h2>注册账号</h2>
    <div class="role-switch">
      <label>
        <input type="radio" value="student" v-model="role" /> 学生
      </label>
      <label>
        <input type="radio" value="admin" v-model="role" /> 管理员
      </label>
    </div>
    <input
        type="text"
        v-model="id"
        :placeholder="role === 'student' ? '学号' : '工号'"
    />
    <input
        type="password"
        v-model="password"
        placeholder="密码"
    />
    <input
        type="password"
        v-model="confirmPassword"
        placeholder="确认密码"
        @keyup.enter="register"
    />
    <button @click="register" :disabled="loading">注册</button>
    <p class="login-link">
      已有账号？<router-link to="/login">去登录</router-link>
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { studentRegister } from '@/api/student'
import { adminRegister } from '@/api/admin'

type Role = 'student' | 'admin'

const role = ref<Role>('student')
const id = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const router = useRouter()

const register = async () => {
  // 校验非空
  if (!id.value.trim() || !password.value.trim() || !confirmPassword.value.trim()) {
    ElMessage.warning('请填写完整信息')
    return
  }

  // 校验学号/工号是否为数字
  const idNum = Number(id.value)
  if (isNaN(idNum)) {
    ElMessage.warning(role.value === 'student' ? '学号必须是数字' : '工号必须是数字')
    return
  }

  // 校验密码长度（可根据后端要求调整）
  if (password.value.length < 6) {
    ElMessage.warning('密码长度至少6位')
    return
  }

  // 校验两次密码是否一致
  if (password.value !== confirmPassword.value) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  loading.value = true
  try {
    let res
    // Register.vue 中的 register 函数
    if (role.value === 'student') {
      res = await studentRegister({ id: id.value, password: password.value });
    } else {
      res = await adminRegister({ id: id.value, password: password.value });
    }

    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (err: any) {
    console.error(err)
    ElMessage.error(err.message || '注册失败，请检查网络或后端服务')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
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
.login-link {
  margin-top: 15px;
  font-size: 14px;
}
</style>