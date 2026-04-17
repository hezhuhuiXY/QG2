import type { ItemComment, NotificationItem, PrivateMessage } from '@/types/api'
import { request } from '@/utils/http'

export interface CommentPayload {
  itemType: number
  itemId: number
  content: string
  contactInfo?: string
}

export interface MessagePayload {
  receiverId: number
  content: string
}

export interface ReportPayload {
  itemType: number
  itemId: number
  reason: string
}

export interface TopRequestPayload {
  lostItemId: number
  reason: string
  topHours?: number
}

export function listComments(itemType: number, itemId: number) {
  return request<ItemComment[]>({
    url: '/comments',
    method: 'get',
    params: { itemType, itemId },
  })
}

export function addComment(payload: CommentPayload) {
  return request<void>({
    url: '/comments',
    method: 'post',
    data: payload,
  })
}

export function listConversation(otherUserId: number) {
  return request<PrivateMessage[]>({
    url: `/messages/${otherUserId}`,
    method: 'get',
  })
}

export function sendMessage(payload: MessagePayload) {
  return request<void>({
    url: '/messages',
    method: 'post',
    data: payload,
  })
}

export function listNotifications() {
  return request<NotificationItem[]>({
    url: '/notifications',
    method: 'get',
  })
}

export function readNotification(id: number) {
  return request<void>({
    url: `/notifications/${id}/read`,
    method: 'put',
  })
}

export function submitReport(payload: ReportPayload) {
  return request<void>({
    url: '/reports',
    method: 'post',
    data: payload,
  })
}

export function submitTopRequest(payload: TopRequestPayload) {
  return request<void>({
    url: '/top-requests',
    method: 'post',
    data: payload,
  })
}
