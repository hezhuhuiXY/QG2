<template>
  <div class="layout">
    <header class="topbar">
      <div class="brand" @click="router.push('/')">校园失物招领平台</div>
      <nav class="nav">
        <RouterLink to="/">首页</RouterLink>
        <RouterLink v-if="authStore.isLoggedIn" to="/profile">个人中心</RouterLink>
        <RouterLink v-if="authStore.isLoggedIn" to="/messages">私信</RouterLink>
        <RouterLink v-if="authStore.isLoggedIn" to="/notifications">通知</RouterLink>
        <RouterLink v-if="authStore.isAdmin" to="/admin">管理员</RouterLink>
      </nav>
      <div class="actions">
        <span v-if="authStore.session" class="welcome">
          {{ authStore.session.nickname || authStore.session.username }}
        </span>
        <template v-if="authStore.isLoggedIn">
          <el-button type="primary" plain @click="router.push('/profile')">我的账号</el-button>
          <el-button @click="logout">退出登录</el-button>
        </template>
        <template v-else>
          <el-button type="primary" plain @click="router.push('/login')">登录</el-button>
          <el-button type="primary" @click="router.push('/register')">注册</el-button>
        </template>
      </div>
    </header>

    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { RouterLink, RouterView, useRouter } from 'vue-router'

import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const router = useRouter()

function logout() {
  authStore.clearSession()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 24px;
  background: #111827;
  color: #fff;
  position: sticky;
  top: 0;
  z-index: 10;
}

.brand {
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
}

.nav {
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
}

.nav a {
  color: #d1d5db;
}

.nav a.router-link-active {
  color: #fff;
  font-weight: 700;
}

.actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.welcome {
  color: #d1d5db;
}

.content {
  max-width: 1320px;
  margin: 0 auto;
  padding: 24px;
}

@media (max-width: 900px) {
  .topbar {
    flex-direction: column;
    align-items: stretch;
  }

  .actions {
    justify-content: flex-end;
  }
}
</style>
