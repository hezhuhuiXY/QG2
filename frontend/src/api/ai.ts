import { request } from '@/utils/http'

export function generateLostDescription(id: number) {
  return request<string>({
    url: `/ai/lost-items/${id}/generate-description`,
    method: 'post',
  })
}

export function regenerateLostDescription(id: number) {
  return request<string>({
    url: `/ai/lost-items/${id}/regenerate-description`,
    method: 'put',
  })
}

export function generateFoundDescription(id: number) {
  return request<string>({
    url: `/ai/found-items/${id}/generate-description`,
    method: 'post',
  })
}

export function regenerateFoundDescription(id: number) {
  return request<string>({
    url: `/ai/found-items/${id}/regenerate-description`,
    method: 'put',
  })
}

export function generateAdminSummary(startTime?: string, endTime?: string) {
  return request<string>({
    url: '/ai/admin-summary',
    method: 'get',
    params: {
      startTime: startTime || undefined,
      endTime: endTime || undefined,
    },
  })
}
