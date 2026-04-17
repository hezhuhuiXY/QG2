import type { UserInfo } from '@/types/api'
import { request } from '@/utils/http'

export interface UpdateProfilePayload {
  nickname?: string
  avatarUrl?: string
  contactInfo?: string
  phone?: string
}

export interface ChangePasswordPayload {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

export function getCurrentUser() {
  return request<UserInfo>({
    url: '/users/me',
    method: 'get',
  })
}

export function updateProfile(payload: UpdateProfilePayload) {
  return request<void>({
    url: '/users/profile',
    method: 'put',
    data: payload,
  })
}

export function changePassword(payload: ChangePasswordPayload) {
  return request<void>({
    url: '/users/password',
    method: 'put',
    data: payload,
  })
}
