<template>
  <el-card class="page-card">
    <div class="page-header">
      <div>
        <h2>通知中心</h2>
        <div class="muted-text">查看评论、私信等提醒，并支持一键标记已读。</div>
      </div>
      <el-button type="primary" @click="loadNotifications">刷新</el-button>
    </div>

    <el-table :data="notifications" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="content" label="内容" min-width="260" />
      <el-table-column prop="bizType" label="业务类型" width="120" />
      <el-table-column prop="createTime" label="时间" min-width="180" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.isRead === 1 ? 'success' : 'warning'">
            {{ row.isRead === 1 ? '已读' : '未读' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" type="primary" plain :disabled="row.isRead === 1" @click="markRead(row.id)">
            已读
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { listNotifications, readNotification } from '@/api/interaction'
import type { NotificationItem } from '@/types/api'

const notifications = ref<NotificationItem[]>([])

async function loadNotifications() {
  notifications.value = await listNotifications()
}

async function markRead(id: number) {
  await readNotification(id)
  ElMessage.success('已标记为已读')
  await loadNotifications()
}

onMounted(loadNotifications)
</script>
