<template>
  <el-card class="item-card" shadow="hover">
    <div class="item-header">
      <div>
        <div class="item-title-row">
          <h3>{{ item.itemName }}</h3>
          <el-tag v-if="itemType === 1 && (item as LostItem).isTop === 1" type="danger">置顶</el-tag>
        </div>
        <p class="muted-text">
          发布人ID：{{ item.userId }} | 发布时间：{{ item.createTime?.replace('T', ' ') }}
        </p>
      </div>
      <el-tag :type="statusTag.type">{{ statusTag.text }}</el-tag>
    </div>

    <div class="item-body">
      <p><strong>{{ itemType === 1 ? '丢失地点' : '拾取地点' }}：</strong>{{ locationText }}</p>
      <p><strong>{{ itemType === 1 ? '丢失时间' : '拾取时间' }}：</strong>{{ timeText }}</p>
      <p><strong>联系方式：</strong>{{ item.contactInfo || '未填写' }}</p>
      <p><strong>描述：</strong>{{ item.description || '暂无描述' }}</p>
      <div v-if="item.imageUrl" class="item-image-block">
        <strong>图片：</strong>
        <el-image :src="item.imageUrl" fit="cover" class="item-image-thumb" :preview-src-list="[item.imageUrl]" />
        <a :href="item.imageUrl" target="_blank">原图链接</a>
      </div>
    </div>

    <div class="item-actions">
      <slot />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'

import type { FoundItem, LostItem } from '@/types/api'

const props = defineProps<{
  item: LostItem | FoundItem
  itemType: 1 | 2
}>()

const locationText = computed(() =>
  props.itemType === 1
    ? (props.item as LostItem).lostLocation
    : (props.item as FoundItem).foundLocation,
)

const timeText = computed(() =>
  props.itemType === 1
    ? (props.item as LostItem).lostTime
    : (props.item as FoundItem).foundTime,
)

const statusTag = computed(() => {
  if (props.itemType === 1) {
    const status = (props.item as LostItem).status
    if (status === 1) return { text: '已找回', type: 'success' as const }
    if (status === 2) return { text: '已删除', type: 'info' as const }
    return { text: '未找回', type: 'warning' as const }
  }

  const status = (props.item as FoundItem).status
  if (status === 1) return { text: '已认领', type: 'success' as const }
  if (status === 2) return { text: '已删除', type: 'info' as const }
  return { text: '未认领', type: 'warning' as const }
})
</script>

<style scoped>
.item-card {
  border-radius: 18px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.item-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-title-row h3 {
  margin: 0;
}

.item-body {
  margin-top: 12px;
}

.item-body p {
  margin: 8px 0;
}

.item-image-block {
  margin: 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.item-image-thumb {
  width: 120px;
  height: 90px;
  border-radius: 8px;
}

.item-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}
</style>
