<template>
  <div>
    <div class="page-header">
      <div>
        <h2>信息广场</h2>
        <div class="muted-text">浏览失物与拾物信息，支持搜索、发布、评论、举报、AI 辅助。</div>
      </div>
      <div class="toolbar">
        <el-button type="primary" @click="openCreateDialog(1)" :disabled="!authStore.isLoggedIn">发布失物</el-button>
        <el-button type="success" @click="openCreateDialog(2)" :disabled="!authStore.isLoggedIn">发布拾物</el-button>
      </div>
    </div>

    <el-card class="page-card">
      <el-tabs v-model="activeTab" @tab-change="loadItems">
        <el-tab-pane label="失物列表" name="lost" />
        <el-tab-pane label="拾物列表" name="found" />
      </el-tabs>

      <div class="filter-row">
        <el-input v-model="filters.itemName" placeholder="按物品名称搜索" clearable />
        <el-input
          v-model="filters.location"
          :placeholder="activeTab === 'lost' ? '按丢失地点筛选' : '按拾取地点筛选'"
          clearable
        />
        <el-button type="primary" @click="loadItems">查询</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </div>

      <div v-loading="loading" class="item-list">
        <ItemCard
          v-for="item in currentItems"
          :key="`${activeTab}-${item.id}`"
          :item="item"
          :item-type="currentItemType"
        >
          <el-button size="small" @click="openCommentDrawer(item)">留言</el-button>
          <el-button
            v-if="authStore.isLoggedIn && item.userId !== authStore.session?.userId"
            size="small"
            @click="goMessage(item.userId)"
          >
            私信
          </el-button>
          <el-button v-if="authStore.isLoggedIn" size="small" @click="openReportDialog(item)">举报</el-button>

          <template v-if="isMine(item)">
            <el-button size="small" type="primary" plain @click="openEditDialog(item)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(item)">删除</el-button>
            <el-button
              v-if="activeTab === 'lost' && (item as LostItem).status === 0"
              size="small"
              type="success"
              plain
              @click="handleResolve(item.id)"
            >
              标记找回
            </el-button>
            <el-button
              v-if="activeTab === 'found' && (item as FoundItem).status === 0"
              size="small"
              type="success"
              plain
              @click="handleClaim(item.id)"
            >
              标记认领
            </el-button>
            <el-button
              v-if="activeTab === 'lost'"
              size="small"
              type="warning"
              plain
              @click="openTopDialog(item.id)"
            >
              申请置顶
            </el-button>
            <el-button size="small" type="info" plain @click="handleGenerateAi(item.id, false)">
              AI 生成描述
            </el-button>
            <el-button size="small" type="info" plain @click="handleGenerateAi(item.id, true)">
              AI 重生成
            </el-button>
          </template>
        </ItemCard>

        <el-empty v-if="!loading && currentItems.length === 0" description="暂无数据" />
      </div>
    </el-card>

    <el-dialog v-model="itemDialogVisible" :title="itemDialogTitle" width="680px">
      <el-form ref="itemFormRef" :model="itemForm" label-width="96px">
        <el-form-item label="物品名称">
          <el-input v-model="itemForm.itemName" />
        </el-form-item>
        <el-form-item :label="currentItemType === 1 ? '丢失地点' : '拾取地点'">
          <el-input v-model="itemForm.location" />
        </el-form-item>
        <el-form-item :label="currentItemType === 1 ? '丢失时间' : '拾取时间'">
          <el-input v-model="itemForm.time" placeholder="yyyy-MM-dd HH:mm:ss" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="itemForm.contactInfo" />
        </el-form-item>
        <el-form-item label="图片地址">
          <el-input v-model="itemForm.imageUrl" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="itemForm.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="itemSubmitting" @click="submitItemForm">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="commentDrawerVisible" title="留言区" size="520px">
      <div class="comment-form">
        <el-input v-model="commentForm.content" type="textarea" :rows="3" placeholder="输入留言内容" />
        <el-input v-model="commentForm.contactInfo" placeholder="可选：留下联系方式" />
        <el-button type="primary" :disabled="!authStore.isLoggedIn" @click="submitComment">发表评论</el-button>
      </div>

      <el-timeline>
        <el-timeline-item v-for="comment in comments" :key="comment.id" :timestamp="comment.createTime">
          <div>用户 {{ comment.userId }}</div>
          <div>{{ comment.content }}</div>
          <div class="muted-text">{{ comment.contactInfo || '未填写联系方式' }}</div>
        </el-timeline-item>
      </el-timeline>
    </el-drawer>

    <el-dialog v-model="reportDialogVisible" title="举报信息" width="520px">
      <el-form :model="reportForm" label-width="90px">
        <el-form-item label="举报理由">
          <el-input v-model="reportForm.reason" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReportForm">提交举报</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="topDialogVisible" title="申请置顶" width="520px">
      <el-form :model="topForm" label-width="90px">
        <el-form-item label="申请原因">
          <el-input v-model="topForm.reason" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="置顶时长">
          <el-input-number v-model="topForm.topHours" :min="1" :max="168" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="topDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTopRequestForm">提交申请</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="aiDialogVisible" title="AI 生成结果" width="620px">
      <el-input v-model="aiResult" type="textarea" :rows="10" readonly />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

import {
  generateFoundDescription,
  generateLostDescription,
  regenerateFoundDescription,
  regenerateLostDescription,
} from '@/api/ai'
import { createFoundItem, createLostItem, deleteFoundItem, deleteLostItem, listFoundItems, listLostItems, updateFoundItem, updateLostItem, resolveLostItem, claimFoundItem } from '@/api/items'
import { addComment, listComments, submitReport, submitTopRequest } from '@/api/interaction'
import ItemCard from '@/components/ItemCard.vue'
import { useAuthStore } from '@/stores/auth'
import type { FoundItem, ItemComment, LostItem } from '@/types/api'

type TabType = 'lost' | 'found'
type GenericItem = LostItem | FoundItem

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref<TabType>('lost')
const filters = reactive({
  itemName: '',
  location: '',
})
const loading = ref(false)
const lostItems = ref<LostItem[]>([])
const foundItems = ref<FoundItem[]>([])

const itemDialogVisible = ref(false)
const itemSubmitting = ref(false)
const editingId = ref<number | null>(null)
const itemForm = reactive({
  itemName: '',
  location: '',
  time: '',
  description: '',
  imageUrl: '',
  contactInfo: '',
})

const commentDrawerVisible = ref(false)
const commentTarget = ref<{ id: number; itemType: 1 | 2 } | null>(null)
const comments = ref<ItemComment[]>([])
const commentForm = reactive({
  content: '',
  contactInfo: '',
})

const reportDialogVisible = ref(false)
const reportTarget = ref<{ id: number; itemType: 1 | 2 } | null>(null)
const reportForm = reactive({
  reason: '',
})

const topDialogVisible = ref(false)
const topTargetId = ref<number | null>(null)
const topForm = reactive({
  reason: '',
  topHours: 24,
})

const aiDialogVisible = ref(false)
const aiResult = ref('')

const currentItems = computed(() => (activeTab.value === 'lost' ? lostItems.value : foundItems.value))
const currentItemType = computed(() => (activeTab.value === 'lost' ? 1 : 2) as 1 | 2)
const itemDialogTitle = computed(() => {
  const prefix = currentItemType.value === 1 ? '失物' : '拾物'
  return editingId.value ? `编辑${prefix}` : `发布${prefix}`
})

function resetFilters() {
  filters.itemName = ''
  filters.location = ''
  loadItems()
}

function isMine(item: GenericItem) {
  return authStore.session?.userId === item.userId
}

async function loadItems() {
  loading.value = true
  try {
    if (activeTab.value === 'lost') {
      lostItems.value = await listLostItems(filters)
    } else {
      foundItems.value = await listFoundItems(filters)
    }
  } finally {
    loading.value = false
  }
}

function resetItemForm() {
  itemForm.itemName = ''
  itemForm.location = ''
  itemForm.time = ''
  itemForm.description = ''
  itemForm.imageUrl = ''
  itemForm.contactInfo = ''
}

function openCreateDialog(type: 1 | 2) {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  activeTab.value = type === 1 ? 'lost' : 'found'
  editingId.value = null
  resetItemForm()
  itemDialogVisible.value = true
}

function openEditDialog(item: GenericItem) {
  editingId.value = item.id
  itemForm.itemName = item.itemName
  itemForm.description = item.description || ''
  itemForm.imageUrl = item.imageUrl || ''
  itemForm.contactInfo = item.contactInfo || ''
  if (activeTab.value === 'lost') {
    itemForm.location = (item as LostItem).lostLocation
    itemForm.time = (item as LostItem).lostTime.replace('T', ' ')
  } else {
    itemForm.location = (item as FoundItem).foundLocation
    itemForm.time = (item as FoundItem).foundTime.replace('T', ' ')
  }
  itemDialogVisible.value = true
}

async function submitItemForm() {
  if (!itemForm.itemName || !itemForm.location || !itemForm.time) {
    ElMessage.warning('请先填写必填字段')
    return
  }

  itemSubmitting.value = true
  try {
    if (currentItemType.value === 1) {
      const payload = {
        itemName: itemForm.itemName,
        lostLocation: itemForm.location,
        lostTime: itemForm.time,
        description: itemForm.description,
        imageUrl: itemForm.imageUrl,
        contactInfo: itemForm.contactInfo,
      }
      if (editingId.value) {
        await updateLostItem(editingId.value, payload)
      } else {
        await createLostItem(payload)
      }
    } else {
      const payload = {
        itemName: itemForm.itemName,
        foundLocation: itemForm.location,
        foundTime: itemForm.time,
        description: itemForm.description,
        imageUrl: itemForm.imageUrl,
        contactInfo: itemForm.contactInfo,
      }
      if (editingId.value) {
        await updateFoundItem(editingId.value, payload)
      } else {
        await createFoundItem(payload)
      }
    }

    ElMessage.success('保存成功')
    itemDialogVisible.value = false
    resetItemForm()
    await loadItems()
  } finally {
    itemSubmitting.value = false
  }
}

async function handleDelete(item: GenericItem) {
  await ElMessageBox.confirm('确定删除这条信息吗？', '提示', { type: 'warning' })
  if (activeTab.value === 'lost') {
    await deleteLostItem(item.id)
  } else {
    await deleteFoundItem(item.id)
  }
  ElMessage.success('删除成功')
  await loadItems()
}

async function handleResolve(id: number) {
  await resolveLostItem(id)
  ElMessage.success('已标记为找回')
  await loadItems()
}

async function handleClaim(id: number) {
  await claimFoundItem(id)
  ElMessage.success('已标记为认领')
  await loadItems()
}

async function openCommentDrawer(item: GenericItem) {
  commentTarget.value = { id: item.id, itemType: currentItemType.value }
  commentDrawerVisible.value = true
  comments.value = await listComments(currentItemType.value, item.id)
}

async function submitComment() {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  if (!commentTarget.value || !commentForm.content) {
    ElMessage.warning('请输入留言内容')
    return
  }
  await addComment({
    itemType: commentTarget.value.itemType,
    itemId: commentTarget.value.id,
    content: commentForm.content,
    contactInfo: commentForm.contactInfo,
  })
  commentForm.content = ''
  commentForm.contactInfo = ''
  comments.value = await listComments(commentTarget.value.itemType, commentTarget.value.id)
  ElMessage.success('留言成功')
}

function openReportDialog(item: GenericItem) {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  reportTarget.value = { id: item.id, itemType: currentItemType.value }
  reportForm.reason = ''
  reportDialogVisible.value = true
}

async function submitReportForm() {
  if (!reportTarget.value || !reportForm.reason) {
    ElMessage.warning('请输入举报理由')
    return
  }
  await submitReport({
    itemType: reportTarget.value.itemType,
    itemId: reportTarget.value.id,
    reason: reportForm.reason,
  })
  reportDialogVisible.value = false
  ElMessage.success('举报已提交')
}

function openTopDialog(id: number) {
  topTargetId.value = id
  topForm.reason = ''
  topForm.topHours = 24
  topDialogVisible.value = true
}

async function submitTopRequestForm() {
  if (!topTargetId.value || !topForm.reason) {
    ElMessage.warning('请填写申请原因')
    return
  }
  await submitTopRequest({
    lostItemId: topTargetId.value,
    reason: topForm.reason,
    topHours: topForm.topHours,
  })
  topDialogVisible.value = false
  ElMessage.success('置顶申请已提交')
}

async function handleGenerateAi(id: number, regenerate: boolean) {
  let text = ''
  if (activeTab.value === 'lost') {
    text = regenerate ? await regenerateLostDescription(id) : await generateLostDescription(id)
  } else {
    text = regenerate ? await regenerateFoundDescription(id) : await generateFoundDescription(id)
  }
  aiResult.value = text
  aiDialogVisible.value = true
}

function goMessage(userId: number) {
  router.push({ name: 'messages', query: { userId: String(userId) } })
}

onMounted(loadItems)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-row {
  display: grid;
  grid-template-columns: 1fr 1fr auto auto;
  gap: 12px;
  margin-bottom: 20px;
}

.item-list {
  display: grid;
  gap: 16px;
}

.comment-form {
  display: grid;
  gap: 12px;
  margin-bottom: 20px;
}

@media (max-width: 900px) {
  .filter-row {
    grid-template-columns: 1fr;
  }
}
</style>
