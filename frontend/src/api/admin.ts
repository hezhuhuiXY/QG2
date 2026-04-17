import type { AdminStats, AdminUser } from '@/types/api'
import { request } from '@/utils/http'

export interface AuditPayload {
  reviewRemark: string
}

export interface DateRangePayload {
  startTime?: string
  endTime?: string
}

export function listAdminUsers() {
  return request<AdminUser[]>({
    url: '/admin/users',
    method: 'get',
  })
}

export function banUser(id: number) {
  return request<void>({
    url: `/admin/users/${id}/ban`,
    method: 'put',
  })
}

export function unbanUser(id: number) {
  return request<void>({
    url: `/admin/users/${id}/unban`,
    method: 'put',
  })
}

export function deleteLostByAdmin(id: number) {
  return request<void>({
    url: `/admin/lost-items/${id}`,
    method: 'delete',
  })
}

export function deleteFoundByAdmin(id: number) {
  return request<void>({
    url: `/admin/found-items/${id}`,
    method: 'delete',
  })
}

export function getAdminStats(params: DateRangePayload) {
  return request<AdminStats>({
    url: '/admin/stats',
    method: 'get',
    params: {
      startTime: params.startTime || undefined,
      endTime: params.endTime || undefined,
    },
  })
}

export function approveReport(id: number, payload: AuditPayload) {
  return request<void>({
    url: `/admin/reports/${id}/approve`,
    method: 'put',
    data: payload,
  })
}

export function rejectReport(id: number, payload: AuditPayload) {
  return request<void>({
    url: `/admin/reports/${id}/reject`,
    method: 'put',
    data: payload,
  })
}

export function approveTopRequest(id: number) {
  return request<void>({
    url: `/admin/top-requests/${id}/approve`,
    method: 'put',
  })
}

export function rejectTopRequest(id: number) {
  return request<void>({
    url: `/admin/top-requests/${id}/reject`,
    method: 'put',
  })
}
