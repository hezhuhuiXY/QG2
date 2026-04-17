import axios, { type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

import type { ApiResponse } from '@/types/api'

const http = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('campus-lost-found-token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error.response?.data?.message || error.message || '请求失败'
    ElMessage.error(message)
    return Promise.reject(error)
  },
)

export async function request<T>(config: AxiosRequestConfig): Promise<T> {
  const response = await http.request<ApiResponse<T>>(config)
  const result = response.data

  if (result.code !== 200) {
    const message = result.message || '请求失败'
    ElMessage.error(message)
    throw new Error(message)
  }

  return result.data
}

export default http
