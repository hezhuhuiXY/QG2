import type { LoginResult } from '@/types/api'
import { request } from '@/utils/http'

export interface RegisterPayload {
  username: string
  email: string
  phone: string
  password: string
  confirmPassword: string
}

export interface LoginPayload {
  account: string
  password: string
}

export function register(payload: RegisterPayload) {
  return request<void>({
    url: '/auth/register',
    method: 'post',
    data: payload,
  })
}

export function login(payload: LoginPayload) {
  return request<LoginResult>({
    url: '/auth/login',
    method: 'post',
    data: payload,
  })
}
