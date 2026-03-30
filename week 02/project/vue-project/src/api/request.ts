import axios, {type AxiosRequestConfig, type AxiosResponse } from 'axios'

// 后端统一响应结构
export interface ResultData<T = any> {
    code: number
    message: string
    data: T
}

// 创建 axios 实例
const instance = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000,
})

// 请求拦截器：自动添加 token
instance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    (error) => Promise.reject(error)
)

// 响应拦截器：直接返回 response.data（后端 Result 结构）



instance.interceptors.response.use(
    // @ts-ignore
    (response: AxiosResponse) => {
        return response.data as ResultData
    },
    (error) => {
        if (error.response) {
            const status = error.response.status
            if (status === 401) {
                // token 失效，清除本地信息并跳转登录页
                localStorage.removeItem('token')
                localStorage.removeItem('role')
                window.location.href = '/login'
            }
            const message = error.response.data?.message || '请求失败'
            console.error(message)
        } else {
            console.error('网络错误：', error.message)
        }
        return Promise.reject(error)
    }
)

// 定义返回类型为 Promise<ResultData<T>> 的请求方法
const request = {
    get<T = any>(url: string, config?: AxiosRequestConfig): Promise<ResultData<T>> {
        return instance.get(url, config)
    },
    post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ResultData<T>> {
        return instance.post(url, data, config)
    },
    put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ResultData<T>> {
        return instance.put(url, data, config)
    },
    delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<ResultData<T>> {
        return instance.delete(url, config)
    },
}

export default request