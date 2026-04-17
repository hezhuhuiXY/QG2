<template>
  <div class="admin-layout">
    <el-card class="page-card">
      <div class="page-header">
        <div>
          <h2>管理员后台</h2>
          <div class="muted-text">用户管理、内容管理、举报审批、置顶审批、平台统计与 AI 总结。</div>
        </div>
        <el-button type="primary" @click="loadUsers">刷新用户</el-button>
      </div>

      <el-table :data="users" stripe>
        <el-table-column prop="id" label="用户ID" width="90" />
        <el-table-column prop="username" label="用户名" width="130" />
        <el-table-column prop="nickname" label="昵称" width="140" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">{{ row.role === 1 ? '管理员' : '普通用户' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">{{ row.status === 1 ? '已封禁' : '正常' }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="180">
          <template #default="{ row }">
            <el-button size="small" type="warning" plain @click="doBan(row.id)" :disabled="row.role === 1 || row.status === 1">
              封禁
            </el-button>
            <el-button size="small" type="success" plain @click="doUnban(row.id)" :disabled="row.status === 0">
              解封
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="page-card">
      <template #header>平台统计</template>
      <div class="stats-actions">
        <el-input v-model="statsForm.startTime" placeholder="开始时间 yyyy-MM-dd HH:mm:ss" />
        <el-input v-model="statsForm.endTime" placeholder="结束时间 yyyy-MM-dd HH:mm:ss" />
        <el-button type="primary" @click="loadStats">查询统计</el-button>
      </div>
      <el-descriptions v-if="stats" :column="3" border>
        <el-descriptions-item label="发布信息量">{{ stats.publishCount }}</el-descriptions-item>
        <el-descriptions-item label="找回 / 认领量">{{ stats.resolvedCount }}</el-descriptions-item>
        <el-descriptions-item label="活跃用户数">{{ stats.activeUserCount }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="page-card">
      <template #header>内容与审批操作</template>
      <div class="admin-grid">
        <div class="admin-block">
          <h3>删除内容</h3>
          <el-input-number v-model="lostDeleteId" :min="1" />
          <el-button type="danger" plain @click="removeLost">删除失物</el-button>
          <el-input-number v-model="foundDeleteId" :min="1" />
          <el-button type="danger" plain @click="removeFound">删除拾物</el-button>
        </div>

        <div class="admin-block">
          <h3>审核举报</h3>
          <el-input-number v-model="reportForm.id" :min="1" />
          <el-input v-model="reportForm.reviewRemark" placeholder="审核备注" />
          <div class="row-actions">
            <el-button type="primary" @click="handleApproveReport">通过举报</el-button>
            <el-button @click="handleRejectReport">驳回举报</el-button>
          </div>
        </div>

        <div class="admin-block">
          <h3>审核置顶申请</h3>
          <el-input-number v-model="topRequestId" :min="1" />
          <div class="row-actions">
            <el-button type="primary" @click="handleApproveTop">通过置顶</el-button>
            <el-button @click="handleRejectTop">驳回置顶</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="page-card">
      <template #header>AI 管理总结</template>
      <div class="stats-actions">
        <el-input v-model="summaryForm.startTime" placeholder="开始时间 yyyy-MM-dd HH:mm:ss" />
        <el-input v-model="summaryForm.endTime" placeholder="结束时间 yyyy-MM-dd HH:mm:ss" />
        <el-button type="primary" @click="loadSummary">生成总结</el-button>
      </div>
      <el-input v-model="summaryText" type="textarea" :rows="8" readonly />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { generateAdminSummary } from '@/api/ai'
import {
  approveReport,
  approveTopRequest,
  banUser,
  deleteFoundByAdmin,
  deleteLostByAdmin,
  getAdminStats,
  listAdminUsers,
  rejectReport,
  rejectTopRequest,
  unbanUser,
} from '@/api/admin'
import type { AdminStats, AdminUser } from '@/types/api'

const users = ref<AdminUser[]>([])
const stats = ref<AdminStats | null>(null)
const summaryText = ref('')

const statsForm = reactive({
  startTime: '',
  endTime: '',
})

const summaryForm = reactive({
  startTime: '',
  endTime: '',
})

const reportForm = reactive({
  id: 1,
  reviewRemark: '',
})

const topRequestId = ref(1)
const lostDeleteId = ref(1)
const foundDeleteId = ref(1)

async function loadUsers() {
  users.value = await listAdminUsers()
}

async function doBan(id: number) {
  await banUser(id)
  ElMessage.success('封禁成功')
  await loadUsers()
}

async function doUnban(id: number) {
  await unbanUser(id)
  ElMessage.success('解封成功')
  await loadUsers()
}

async function loadStats() {
  stats.value = await getAdminStats(statsForm)
}

async function removeLost() {
  await deleteLostByAdmin(lostDeleteId.value)
  ElMessage.success('已删除失物信息')
}

async function removeFound() {
  await deleteFoundByAdmin(foundDeleteId.value)
  ElMessage.success('已删除拾物信息')
}

async function handleApproveReport() {
  await approveReport(reportForm.id, { reviewRemark: reportForm.reviewRemark })
  ElMessage.success('已通过举报')
}

async function handleRejectReport() {
  await rejectReport(reportForm.id, { reviewRemark: reportForm.reviewRemark })
  ElMessage.success('已驳回举报')
}

async function handleApproveTop() {
  await approveTopRequest(topRequestId.value)
  ElMessage.success('已通过置顶申请')
}

async function handleRejectTop() {
  await rejectTopRequest(topRequestId.value)
  ElMessage.success('已驳回置顶申请')
}

async function loadSummary() {
  summaryText.value = await generateAdminSummary(summaryForm.startTime, summaryForm.endTime)
}

onMounted(async () => {
  await loadUsers()
  await loadStats()
})
</script>

<style scoped>
.admin-layout {
  display: grid;
  gap: 20px;
}

.stats-actions {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 12px;
  margin-bottom: 16px;
}

.admin-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.admin-block {
  display: grid;
  gap: 10px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 14px;
}

.admin-block h3 {
  margin: 0;
}

.row-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

@media (max-width: 1024px) {
  .admin-grid,
  .stats-actions {
    grid-template-columns: 1fr;
  }
}
</style>
