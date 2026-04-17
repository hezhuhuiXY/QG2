<template>
  <div class="auth-page">
    <el-card class="auth-card page-card">
      <template #header>
        <div class="auth-header">
          <h2>登录校园失物招领平台</h2>
          <span class="muted-text">支持用户名、手机号或邮箱登录</span>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="账号" prop="account">
          <el-input v-model="form.account" placeholder="请输入用户名 / 手机号 / 邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" class="full-btn" :loading="submitting" @click="handleLogin">
          登录
        </el-button>
      </el-form>

      <div class="auth-footer">
        <span class="muted-text">还没有账号？</span>
        <el-button link type="primary" @click="router.push('/register')">去注册</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'

import { login } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = reactive({
  account: '',
  password: '',
})

const rules: FormRules<typeof form> = {
  account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const data = await login(form)
    authStore.setSession({
      token: data.token,
      userId: data.userId,
      username: data.username,
      nickname: data.nickname,
      role: data.role,
    })
    ElMessage.success('登录成功')
    router.push((route.query.redirect as string) || '/')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
}

.auth-card {
  width: min(100%, 460px);
}

.auth-header h2 {
  margin: 0 0 6px;
}

.full-btn {
  width: 100%;
}

.auth-footer {
  margin-top: 18px;
  text-align: center;
}
</style>
