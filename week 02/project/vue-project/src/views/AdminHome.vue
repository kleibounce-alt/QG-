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
            <!-- ⭐ 修复：筛选下拉也同步修改为只有两种状态 -->
            <el-select v-model="filterStatus" placeholder="全部状态" clearable size="small" style="width: 120px; margin-left: 20px;">
              <el-option label="未完成" value="未完成" />
              <el-option label="已完成" value="已完成" />
            </el-select>
            <el-button type="primary" link @click="fetchReports" style="margin-left: 20px;">筛选</el-button>
            <el-button type="primary" link @click="fetchReports" style="margin-left: 10px;">刷新</el-button>
          </template>
          <el-table :data="reports" stripe>
            <el-table-column prop="id" label="单号" width="80" />
            <el-table-column prop="studentId" label="学生学号" width="120" />
            <el-table-column prop="deviceType" label="设备" />
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="time" label="创建时间" width="160" />
            <!-- ⭐ 修复：状态列修改为只有未完成/已完成 -->
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <!-- ⭐ 关键修复：下拉选项删除"处理中"，只保留两种状态 -->
                <el-select v-model="row.status" size="small" @change="updateStatus(row.id, row.status)">
                  <el-option label="未完成" value="未完成" />
                  <el-option label="已完成" value="已完成" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button type="primary" link @click="viewImages(row.id)">查看图片</el-button>
                <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-main>
    </el-container>

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

    <el-dialog v-model="imageDialogVisible" title="报修单图片" width="50%">
      <div v-loading="imageLoading">
        <div v-if="currentImages.length === 0">暂无图片</div>
        <div v-else class="image-grid">
          <div v-for="img in currentImages" :key="img.id" class="image-item">
            <img
                :src="getImageUrl(img.imagePath)"
                :alt="img.originalName"
                @click="previewImage(getImageUrl(img.imagePath))"
                @error="handleImageError"
            />
            <p>{{ img.originalName }}</p>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="previewDialogVisible" title="图片预览" width="80%">
      <img :src="previewImageUrl" style="width: 100%;" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllReports, updateReport, deleteReport, changeAdminPassword, getReportImagesByAdmin } from '@/api/admin'

const router = useRouter()
const adminId = ref(localStorage.getItem('userId') || '')
const reports = ref<any[]>([])
const pwdForm = ref({ oldPassword: '', newPassword: '' })
const pwdDialogVisible = ref(false)
const filterStatus = ref('')

const imageDialogVisible = ref(false)
const currentImages = ref<any[]>([])
const imageLoading = ref(false)
const previewDialogVisible = ref(false)
const previewImageUrl = ref('')

const getImageUrl = (path: string): string => {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return path.startsWith('/') ? path : '/' + path
}

const handleImageError = () => {
  ElMessage.error('图片加载失败')
}

const fetchReports = async () => {
  try {
    const res = await getAllReports(filterStatus.value || undefined)
    if (res.code === 200) {
      reports.value = res.data
    }
  } catch (err) {
    ElMessage.error('获取报修单失败')
  }
}

const updateStatus = async (id: number, status: string) => {
  try {
    // ⭐ 保险校验：确保只提交有效状态
    if (status !== '未完成' && status !== '已完成') {
      ElMessage.error('无效的状态')
      fetchReports() // 恢复原数据
      return
    }

    const res = await updateReport(id, { status })
    if (res.code === 200) {
      ElMessage.success('状态更新成功')
    } else {
      ElMessage.error(res.message || '更新失败')
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
    }
  } catch (err) {
    if (err !== 'cancel') ElMessage.error('删除失败')
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
    }
  } catch (err) {
    ElMessage.error('修改失败')
  }
}

const viewImages = async (reportId: number) => {
  imageDialogVisible.value = true
  imageLoading.value = true
  currentImages.value = []

  try {
    const res = await getReportImagesByAdmin(reportId)
    if (res.code === 200) {
      currentImages.value = res.data
    } else if (res.code === 401) {
      logout()
    }
  } catch (err: any) {
    if (err.response?.status === 401) logout()
    else ElMessage.error('获取图片失败')
  } finally {
    imageLoading.value = false
  }
}

const previewImage = (url: string) => {
  previewImageUrl.value = url
  previewDialogVisible.value = true
}

const logout = () => {
  localStorage.clear()
  router.push('/login')
}

onMounted(() => {
  fetchReports()
})
</script>

<style scoped>
.admin-home { height: 100vh; }
.el-header {
  background-color: #f5f7fa;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.user-info { display: flex; align-items: center; gap: 15px; }
.image-grid { display: flex; flex-wrap: wrap; gap: 16px; }
.image-item { width: 150px; text-align: center; cursor: pointer; }
.image-item img { width: 100%; height: 120px; object-fit: cover; border-radius: 4px; }
</style>