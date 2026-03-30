import request, { type ResultData } from './request'

// ---------- 请求参数类型 ----------
export interface StudentLoginData {
    id: string      // 学号
    password: string
}

export interface StudentRegisterData {
    id: string      // 学号
    password: string
}

export interface DormitoryBindingData {
    dormitory: string
    roomId: string
}

export interface CreateReportData {
    deviceType: string
    description: string
}

export interface ChangePasswordData {
    oldPassword: string
    newPassword: string
}

// ---------- API 函数 ----------
// 学生登录
export const studentLogin = (data: StudentLoginData): Promise<ResultData<string>> => {
    return request.post('/api/student/login', data)
}

// 学生注册
export const studentRegister = (data: StudentRegisterData): Promise<ResultData<void>> => {
    return request.post('/api/student/register', data)
}

// 绑定/修改宿舍
export const bindDormitory = (data: DormitoryBindingData): Promise<ResultData<void>> => {
    return request.put('/api/student/dormitory', data)
}

// 创建报修单
export const createReport = (data: CreateReportData): Promise<ResultData<void>> => {
    return request.post('/api/student/reports', data)
}

// 获取当前学生的所有报修单
export const getStudentReports = (): Promise<ResultData<any[]>> => {
    return request.get('/api/student/reports')
}

// 取消报修单
export const cancelReport = (reportId: number): Promise<ResultData<void>> => {
    return request.delete(`/api/student/reports/${reportId}`)
}

// 修改学生密码
export const changeStudentPassword = (data: ChangePasswordData): Promise<ResultData<void>> => {
    return request.put('/api/student/password', data)
}