<template>
  <div class="profile-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>个人中心</h2>
          <div class="muted-text">查看并修改昵称、联系方式、头像和密码。</div>
        </div>
        <el-button type="primary" @click="loadProfile">刷新</el-button>
      </div>

      <el-descriptions v-if="profile" :column="2" border>
        <el-descriptions-item label="用户ID">{{ profile.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ profile.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ profile.nickname }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ profile.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ profile.phone }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ profile.contactInfo || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="头像">
          <div v-if="profile.avatarUrl" class="avatar-preview-block">
            <el-image :src="profile.avatarUrl" fit="cover" class="avatar-thumb" />
            <a :href="profile.avatarUrl" target="_blank">原图链接</a>
          </div>
          <span v-else>未设置</span>
        </el-descriptions-item>
        <el-descriptions-item label="角色">{{ profile.role === 1 ? '管理员' : '普通用户' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="page-card">
      <template #header>修改个人信息</template>
      <el-form :model="profileForm" label-width="100px">
        <el-form-item label="昵称">
          <el-input v-model="profileForm.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="profileForm.phone" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="profileForm.contactInfo" />
        </el-form-item>
        <el-form-item label="头像">
          <div class="avatar-upload-row">
            <el-upload
              :show-file-list="false"
              accept="image/jpeg,image/png,image/gif,image/webp"
              :http-request="handleAvatarUpload"
            >
              <el-button type="primary" plain>选择本地图片上传</el-button>
            </el-upload>
            <el-button v-if="profileForm.avatarUrl" link type="danger" @click="profileForm.avatarUrl = ''">清除</el-button>
          </div>
          <div v-if="profileForm.avatarUrl" class="avatar-form-preview">
            <el-image :src="profileForm.avatarUrl" fit="cover" class="avatar-thumb" />
          </div>
          <div v-else class="muted-text">支持 jpg、png、gif、webp，最大 5MB</div>
        </el-form-item>
        <el-button type="primary" @click="submitProfile">保存修改</el-button>
      </el-form>
    </el-card>

    <el-card class="page-card">
      <template #header>修改密码</template>
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-button type="primary" @click="submitPassword">修改密码</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'

import { uploadImage } from '@/api/upload'
import { changePassword, getCurrentUser, updateProfile } from '@/api/user'
import type { UserInfo } from '@/types/api'

const profile = ref<UserInfo | null>(null)

const profileForm = reactive({
  nickname: '',
  phone: '',
  contactInfo: '',
  avatarUrl: '',
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

async function handleAvatarUpload(options: UploadRequestOptions) {
  const file = options.file as File
  if (!file || !(file instanceof Blob)) {
    ElMessage.warning('未选择有效文件')
    options.onError?.(new Error('no file') as never)
    return
  }
  try {
    const url = await uploadImage(file)
    profileForm.avatarUrl = url
    ElMessage.success('头像已上传，请点击保存修改写入资料')
    options.onSuccess?.(url as never)
  } catch (e) {
    options.onError?.(e as Error)
  }
}

async function loadProfile() {
  profile.value = await getCurrentUser()
  profileForm.nickname = profile.value.nickname || ''
  profileForm.phone = profile.value.phone || ''
  profileForm.contactInfo = profile.value.contactInfo || ''
  profileForm.avatarUrl = profile.value.avatarUrl || ''
}

async function submitProfile() {
  await updateProfile(profileForm)
  ElMessage.success('个人信息已更新')
  await loadProfile()
}

async function submitPassword() {
  await changePassword(passwordForm)
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  ElMessage.success('密码修改成功，请记住新密码')
}

onMounted(loadProfile)
</script>

<style scoped>
.profile-layout {
  display: grid;
  gap: 20px;
}

.avatar-upload-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.avatar-form-preview {
  margin-top: 12px;
}

.avatar-preview-block {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.avatar-thumb {
  width: 72px;
  height: 72px;
  border-radius: 8px;
}
</style>
