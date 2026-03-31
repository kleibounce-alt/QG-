import request, { type ResultData } from './request'

// ---------- 请求参数类型 ----------
export interface AdminLoginData {
    id: string   // 工号
    password: string
}

export interface AdminRegisterData {
    id: string        // 工号
    password: string
}

export interface UpdateReportData {
    status?: string
    // 可扩展其他字段
}

export interface ChangePasswordData {
    oldPassword: string
    newPassword: string
}

// ---------- API 函数 ----------
// 管理员登录
export const adminLogin = (data: AdminLoginData): Promise<ResultData<string>> => {
    return request.post('/api/admin/login', data)
}

// 管理员注册
export const adminRegister = (data: AdminRegisterData): Promise<ResultData<void>> => {
    return request.post('/api/admin/register', data)
}

// 获取所有报修单（支持状态筛选）
export const getAllReports = (status?: string): Promise<ResultData<any[]>> => {
    return request.get('/api/admin/reports', { params: { status } })
}

// 获取单个报修单详情
export const getOneReport = (reportId: number): Promise<ResultData<any>> => {
    return request.get(`/api/admin/reports/${reportId}`)
}

// 更新报修单
export const updateReport = (reportId: number, data: UpdateReportData): Promise<ResultData<void>> => {
    return request.put(`/api/admin/reports/${reportId}`, data)
}

// 删除报修单
export const deleteReport = (reportId: number): Promise<ResultData<void>> => {
    return request.delete(`/api/admin/reports/${reportId}`)
}

// 修改管理员密码
export const changeAdminPassword = (data: ChangePasswordData): Promise<ResultData<void>> => {
    return request.put('/api/admin/password', data)
}

// 获取某个报修单的所有图片（管理员用）- 修正路径
export const getReportImagesByAdmin = (reportId: number): Promise<ResultData<any[]>> => {
    return request.get(`/api/images/admin/report/${reportId}`)   // 修改为正确路径
}