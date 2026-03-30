<template>
  <div class="admin-home">
    <el-container>
      <el-header>
        <h2>管理员报修管理系统</h2>
        <div class="user-info">
          工号：{{ adminId }}
          <el-button type="primary" link @click="openPwdDialog">修改密码</el-button>
          <el-button type="danger" link @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <el-card>
          <template #header>
            <span>所有报修单</span>
            <el-button type="primary" link @click="fetchReports" style="margin-left: 20px;">刷新</el-button>
          </template>
          <el-table :data="reports" stripe>
            <el-table-column prop="id" label="单号" width="80" />
            <el-table-column prop="studentId" label="学生学号" width="120" />
            <el-table-column prop="deviceType" label="设备" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="time" label="创建时间" width="160" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-select v-model="row.status" size="small" @change="updateStatus(row.id, row.status)">
                  <el-option label="未完成" value="未完成" />
                  <el-option label="处理中" value="处理中" />
                  <el-option label="已完成" value="已完成" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-main>
    </el-container>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="30%">
      <el-form :model="pwdForm">
        <el-form-item label="旧密码">
          <el-input v-model="pwdForm.oldPassword" type="password" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitChangePwd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllReports, updateReport, deleteReport, changeAdminPassword } from '@/api/admin'

const router = useRouter()
const adminId = ref(localStorage.getItem('userId') || '')
const reports = ref<any[]>([])
const pwdForm = ref({ oldPassword: '', newPassword: '' })
const pwdDialogVisible = ref(false)

const fetchReports = async () => {
  try {
    const res = await getAllReports()
    if (res.code === 200) {
      reports.value = res.data
    } else {
      ElMessage.error(res.message)
    }
  } catch (err) {
    ElMessage.error('获取报修单失败')
  }
}

const updateStatus = async (id: number, status: string) => {
  try {
    const res = await updateReport(id, { status })
    if (res.code === 200) {
      ElMessage.success('状态更新成功')
    } else {
      ElMessage.error(res.message)
      fetchReports() // 恢复原数据
    }
  } catch (err) {
    ElMessage.error('状态更新失败')
    fetchReports()
  }
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定删除该报修单吗？', '提示', { type: 'warning' })
    const res = await deleteReport(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchReports()
    } else {
      ElMessage.error(res.message)
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const openPwdDialog = () => {
  pwdForm.value = { oldPassword: '', newPassword: '' }
  pwdDialogVisible.value = true
}
const submitChangePwd = async () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    ElMessage.warning('请填写完整')
    return
  }
  try {
    const res = await changeAdminPassword(pwdForm.value)
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      logout()
    } else {
      ElMessage.error(res.message)
    }
  } catch (err) {
    ElMessage.error('修改失败')
  }
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  localStorage.removeItem('userId')
  router.push('/login')
}

onMounted(() => {
  fetchReports()
})
</script>

<style scoped>
.admin-home {
  height: 100vh;
}
.el-header {
  background-color: #f5f7fa;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}
</style>