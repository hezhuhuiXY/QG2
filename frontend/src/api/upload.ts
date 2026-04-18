import { request } from '@/utils/http'

/** 上传图片，返回后端给出的可访问完整 URL（存入 imageUrl / avatarUrl） */
export function uploadImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request<string>({
    url: '/upload/image',
    method: 'post',
    data: formData,
  })
}
