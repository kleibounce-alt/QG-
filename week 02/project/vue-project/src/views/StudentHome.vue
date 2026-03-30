<template>
  <div class="student-home">
    <el-container>
      <el-header>
        <h2>学生报修管理系统</h2>
        <div class="user-info">
          学号：{{ studentId }}
          <el-button type="primary" link @click="openBindDialog">绑定/修改宿舍</el-button>
          <el-button type="primary" link @click="openPwdDialog">修改密码</el-button>
          <el-button type="danger" link @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <template #header>创建报修单</template>
              <el-form :model="reportForm" label-width="80px">
                <el-form-item label="设备类型">
                  <el-input v-model="reportForm.deviceType" placeholder="例如：空调、热水器" />
                </el-form-item>
                <el-form-item label="故障描述">
                  <el-input v-model="reportForm.description" type="textarea" rows="3" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="submitReport">提交</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>我的报修单</span>
                <el-button type="primary" link @click="fetchReports" style="margin-left: 10px;">刷新</el-button>
              </template>
              <el-table :data="reports" stripe>
                <el-table-column prop="id" label="单号" width="80" />
                <el-table-column prop="deviceType" label="设备" />
                <el-table-column prop="description" label="描述" />
                <el-table-column prop="status" label="状态" />
                <el-table-column label="操作" width="100">
                  <template #default="{ row }">
                    <el-button
                        v-if="row.status === '未完成'"
                        type="danger"
                        link
                        @click="cancelReportApi(row.id)"
                    >取消</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>

    <!-- 绑定/修改宿舍对话框 -->
    <el-dialog v-model="bindDialogVisible" title="绑定/修改宿舍" width="30%">
      <el-form :model="bindForm">
        <el-form-item label="宿舍楼栋">
          <el-input v-model="bindForm.dormitory" placeholder="例如：西1" />
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="bindForm.roomId" placeholder="例如：325室" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bindDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBind">确定</el-button>
      </template>
    </el-dialog>

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
import { ElMessage } from 'element-plus'
import {
  getStudentReports,
  createReport,
  bindDormitory,
  cancelReport as cancelReportApi,
  changeStudentPassword
} from '@/api/student'

const router = useRouter()
const studentId = ref(localStorage.getItem('userId') || '')
const reports = ref<any[]>([])
const bindForm = ref({ dormitory: '', roomId: '' })
const reportForm = ref({ deviceType: '', description: '' })
const pwdForm = ref({ oldPassword: '', newPassword: '' })
const bindDialogVisible = ref(false)
const pwdDialogVisible = ref(false)

const fetchReports = async () => {
  try {
    const res = await getStudentReports()
    if (res.code === 200) {
      reports.value = res.data
    } else {
      ElMessage.error(res.message)
    }
  } catch (err) {
    ElMessage.error('获取报修单失败')
  }
}

const submitReport = async () => {
  if (!reportForm.value.deviceType || !reportForm.value.description) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const res = await createReport(reportForm.value)
    if (res.code === 200) {
      ElMessage.success('报修单创建成功')
      reportForm.value = { deviceType: '', description: '' }
      fetchReports()
    } else {
      ElMessage.error(res.message)
    }
  } catch (err) {
    ElMessage.error('创建失败')
  }
}

const cancelReport = async (id: number) => {
  try {
    const res = await cancelReportApi(id)
    if (res.code === 200) {
      ElMessage.success('取消成功')
      fetchReports()
    } else {
      ElMessage.error(res.message)
    }
  } catch (err) {
    ElMessage.error('取消失败')
  }
}

const openBindDialog = () => {
  bindForm.value = { dormitory: '', roomId: '' }
  bindDialogVisible.value = true
}
const submitBind = async () => {
  if (!bindForm.value.dormitory || !bindForm.value.roomId) {
    ElMessage.warning('请填写完整')
    return
  }
  try {
    const res = await bindDormitory(bindForm.value)
    if (res.code === 200) {
      ElMessage.success('绑定成功')
      bindDialogVisible.value = false
    } else {
      ElMessage.error(res.message)
    }
  } catch (err) {
    ElMessage.error('绑定失败')
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
    const res = await changeStudentPassword(pwdForm.value)
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
.student-home {
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