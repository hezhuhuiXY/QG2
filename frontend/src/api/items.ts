import type { FoundItem, LostItem } from '@/types/api'
import { request } from '@/utils/http'

export interface LostItemPayload {
  itemName: string
  lostLocation: string
  lostTime: string
  description?: string
  imageUrl?: string
  contactInfo?: string
}

export interface FoundItemPayload {
  itemName: string
  foundLocation: string
  foundTime: string
  description?: string
  imageUrl?: string
  contactInfo?: string
}

export interface QueryPayload {
  itemName?: string
  location?: string
}

export function listLostItems(params: QueryPayload) {
  return request<LostItem[]>({
    url: '/lost-items',
    method: 'get',
    params,
  })
}

export function createLostItem(payload: LostItemPayload) {
  return request<void>({
    url: '/lost-items',
    method: 'post',
    data: payload,
  })
}

export function updateLostItem(id: number, payload: LostItemPayload) {
  return request<void>({
    url: `/lost-items/${id}`,
    method: 'put',
    data: payload,
  })
}

export function deleteLostItem(id: number) {
  return request<void>({
    url: `/lost-items/${id}`,
    method: 'delete',
  })
}

export function resolveLostItem(id: number) {
  return request<void>({
    url: `/lost-items/${id}/resolve`,
    method: 'put',
  })
}

export function listFoundItems(params: QueryPayload) {
  return request<FoundItem[]>({
    url: '/found-items',
    method: 'get',
    params,
  })
}

export function createFoundItem(payload: FoundItemPayload) {
  return request<void>({
    url: '/found-items',
    method: 'post',
    data: payload,
  })
}

export function updateFoundItem(id: number, payload: FoundItemPayload) {
  return request<void>({
    url: `/found-items/${id}`,
    method: 'put',
    data: payload,
  })
}

export function deleteFoundItem(id: number) {
  return request<void>({
    url: `/found-items/${id}`,
    method: 'delete',
  })
}

export function claimFoundItem(id: number) {
  return request<void>({
    url: `/found-items/${id}/claim`,
    method: 'put',
  })
}
