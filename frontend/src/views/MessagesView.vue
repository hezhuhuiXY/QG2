<template>
  <el-card class="page-card">
    <div class="page-header">
      <div>
        <h2>私信会话</h2>
        <div class="muted-text">输入对方用户 ID，即可查看会话并发送消息。</div>
      </div>
      <el-button type="primary" @click="loadConversation">刷新会话</el-button>
    </div>

    <div class="message-top">
      <el-input-number v-model="otherUserId" :min="1" />
      <el-button type="primary" @click="loadConversation">加载会话</el-button>
    </div>

    <el-timeline>
      <el-timeline-item v-for="message in messages" :key="message.id" :timestamp="message.createTime">
        <div>发送者：{{ message.senderId }} -> 接收者：{{ message.receiverId }}</div>
        <div>{{ message.content }}</div>
      </el-timeline-item>
    </el-timeline>

    <el-form class="send-form" @submit.prevent="handleSend">
      <el-input v-model="content" type="textarea" :rows="4" placeholder="输入要发送的消息" />
      <el-button type="primary" @click="handleSend">发送消息</el-button>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

import { listConversation, sendMessage } from '@/api/interaction'
import type { PrivateMessage } from '@/types/api'

const route = useRoute()
const otherUserId = ref(Number(route.query.userId || 1))
const content = ref('')
const messages = ref<PrivateMessage[]>([])

async function loadConversation() {
  if (!otherUserId.value) {
    ElMessage.warning('请先输入对方用户ID')
    return
  }
  messages.value = await listConversation(otherUserId.value)
}

async function handleSend() {
  if (!otherUserId.value || !content.value) {
    ElMessage.warning('请填写用户ID和消息内容')
    return
  }
  await sendMessage({
    receiverId: otherUserId.value,
    content: content.value,
  })
  content.value = ''
  ElMessage.success('发送成功')
  await loadConversation()
}

onMounted(() => {
  if (otherUserId.value) {
    loadConversation()
  }
})
</script>

<style scoped>
.message-top {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.send-form {
  display: grid;
  gap: 12px;
  margin-top: 16px;
}
</style>
