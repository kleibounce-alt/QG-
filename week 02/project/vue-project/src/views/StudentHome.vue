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
                  <el-input v-model="reportForm.description" type="textarea" :rows="3" />
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
                <el-table-column label="图片" width="120">
                  <template #default="{ row }">
                    <el-button type="primary" link @click="openUploadDialog(row.id)">上传</el-button>
                    <el-button type="success" link @click="viewImages(row.id)">查看</el-button>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="{ row }">
                    <el-button v-if="row.status === '未完成'" type="danger" link @click="cancelReport(row.id)">取消</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>

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

    <el-dialog v-model="uploadDialogVisible" title="上传图片" width="30%">
      <input type="file" accept="image/jpeg,image/png" @change="handleFileChange" ref="fileInput" />
      <div style="margin-top: 10px;">
        <el-button type="primary" @click="submitUpload" :loading="uploading">上传</el-button>
      </div>
    </el-dialog>

    <el-dialog v-model="imageDialogVisible" title="报修单图片" width="50%">
      <div v-loading="imageLoading">
        <div v-if="imageList.length === 0">暂无图片</div>
        <div v-else class="image-grid">
          <div v-for="img in imageList" :key="img.id" class="image-item">
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
import { ElMessage } from 'element-plus'
import {
  getStudentReports, createReport, bindDormitory, cancelReport,
  changeStudentPassword, uploadImage, getReportImagesByStudent
} from '@/api/student'

const router = useRouter()
const studentId = ref(localStorage.getItem('userId') || '')
const reports = ref<any[]>([])
const bindForm = ref({ dormitory: '', roomId: '' })
const reportForm = ref({ deviceType: '', description: '' })
const pwdForm = ref({ oldPassword: '', newPassword: '' })
const bindDialogVisible = ref(false)
const pwdDialogVisible = ref(false)
const uploadDialogVisible = ref(false)
const currentReportId = ref<number | null>(null)
const selectedFile = ref<File | null>(null)
const uploading = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)
const imageDialogVisible = ref(false)
const imageList = ref<any[]>([])
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
    const res = await getStudentReports()
    if (res.code === 200) reports.value = res.data
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
    }
  } catch (err) {
    ElMessage.error('创建失败')
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
    }
  } catch (err) {
    ElMessage.error('修改失败')
  }
}

const openUploadDialog = (reportId: number) => {
  currentReportId.value = reportId
  selectedFile.value = null
  if (fileInput.value) fileInput.value.value = ''
  uploadDialogVisible.value = true
}

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) {
    selectedFile.value = null
    return
  }
  const ext = file.name.split('.').pop()?.toLowerCase()
  if (ext !== 'jpg' && ext !== 'png') {
    ElMessage.error('只支持 jpg 或 png 格式')
    selectedFile.value = null
    return
  }
  selectedFile.value = file
}

// ⭐ 关键修复：添加详细错误处理
const submitUpload = async () => {
  if (!currentReportId.value || !selectedFile.value) {
    ElMessage.warning('请选择图片')
    return
  }
  uploading.value = true

  try {
    const res = await uploadImage(currentReportId.value, selectedFile.value)
    if (res.code === 200) {
      ElMessage.success('上传成功')
      uploadDialogVisible.value = false
      selectedFile.value = null
      if (fileInput.value) fileInput.value.value = ''
    } else {
      // ⭐ 添加：显示后端返回的具体错误信息
      ElMessage.error(res.message || '上传失败')
    }
  } catch (err: any) {
    console.error('上传错误:', err)
    // ⭐ 添加：详细错误处理，显示后端返回的权限错误或其他信息
    if (err.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      logout()
    } else if (err.response?.status === 403) {
      ElMessage.error('权限不足，无法上传图片')
    } else if (err.response?.data?.message) {
      ElMessage.error(err.response.data.message)
    } else {
      ElMessage.error('上传失败，请检查网络或文件大小')
    }
  } finally {
    uploading.value = false
  }
}

const viewImages = async (reportId: number) => {
  imageDialogVisible.value = true
  imageLoading.value = true
  imageList.value = []

  try {
    const res = await getReportImagesByStudent(reportId)
    if (res.code === 200) {
      imageList.value = res.data
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
.student-home { height: 100vh; }
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