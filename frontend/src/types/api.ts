export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface LoginResult {
  token: string
  userId: number
  username: string
  nickname: string
  role: number
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatarUrl: string | null
  contactInfo: string | null
  role: number
  status: number
}

export interface LostItem {
  id: number
  userId: number
  itemName: string
  lostLocation: string
  lostTime: string
  description: string | null
  imageUrl: string | null
  contactInfo: string | null
  status: number
  isTop: number
  topExpireTime: string | null
  createTime: string
  updateTime: string
}

export interface FoundItem {
  id: number
  userId: number
  itemName: string
  foundLocation: string
  foundTime: string
  description: string | null
  imageUrl: string | null
  contactInfo: string | null
  status: number
  createTime: string
  updateTime: string
}

export interface ItemComment {
  id: number
  itemType: number
  itemId: number
  userId: number
  content: string
  contactInfo: string | null
  isRead: number
  createTime: string
}

export interface PrivateMessage {
  id: number
  senderId: number
  receiverId: number
  content: string
  isRead: number
  createTime: string
}

export interface NotificationItem {
  id: number
  userId: number
  bizType: string
  bizId: number
  title: string
  content: string
  isRead: number
  createTime: string
}

export interface AdminUser {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatarUrl: string | null
  contactInfo: string | null
  role: number
  status: number
  createTime: string
  updateTime: string
}

export interface AdminStats {
  publishCount: number
  resolvedCount: number
  activeUserCount: number
}
