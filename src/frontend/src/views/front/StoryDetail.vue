<template>
  <div class="detail-page" v-if="story">
    <div class="detail-container">
      <el-image :src="story.coverImage" fit="cover" class="cover">
        <template #error><div class="img-fallback"><el-icon :size="50"><Picture /></el-icon><span>图片加载失败</span></div></template>
      </el-image>
      <h1>{{ story.title }}</h1>
      <p class="meta">作者：{{ story.author }} · 发布时间：{{ story.publishTime }}</p>
      <div class="content">{{ story.content }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'
import request from '@/api/request'

const route = useRoute()
const story = ref(null)

const loadData = async () => {
  try {
    story.value = await request.get('/stories/' + route.params.id)
  } catch (e) {
    story.value = null
  }
}
onMounted(loadData)
</script>

<style scoped>
.detail-page { padding: 40px; }
.detail-container { max-width: 900px; margin: 0 auto; background: #fff; padding: 40px; border-radius: 8px; }
.cover { width: 100%; height: 400px; border-radius: 8px; margin-bottom: 24px; }
h1 { font-size: 32px; margin-bottom: 12px; }
.meta { color: #999; margin-bottom: 24px; }
.content { line-height: 2; font-size: 16px; color: #444; white-space: pre-wrap; }
.img-fallback { width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; background: #f5f7fa; color: #c0c4cc; gap: 8px; font-size: 14px; }
</style>
