<template>
  <div class="stories-page">
    <div class="page-banner">
      <h1>温暖的救助故事</h1>
      <p>每一个故事都是一次生命的重生</p>
    </div>
    <div class="story-list">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="story in list" :key="story.id">
          <el-card shadow="hover" class="story-card" @click="goDetail(story.id)">
            <el-image :src="story.coverImage" fit="cover" class="story-img">
              <template #error><div class="img-fallback"><el-icon :size="40"><Picture /></el-icon><span>图片加载失败</span></div></template>
            </el-image>
            <div class="story-info">
              <h3>{{ story.title }}</h3>
              <p class="author">作者：{{ story.author }} · {{ story.publishTime }}</p>
              <p class="preview">{{ story.content ? story.content.substring(0, 80) + '...' : '' }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <div class="pagination">
        <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="prev, pager, next" @change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'
import request from '@/api/request'

const router = useRouter()
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(9)

const loadData = async () => {
  try {
    const res = await request.get('/stories', { params: { page: page.value, size: size.value } })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) {
    list.value = []
  }
}

const goDetail = (id) => router.push('/story/' + id)
onMounted(loadData)
</script>

<style scoped>
.page-banner {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #fff;
  padding: 60px 40px;
  text-align: center;
}
.page-banner h1 { font-size: 36px; margin-bottom: 10px; }
.story-list { padding: 30px 40px; }
.story-card { cursor: pointer; margin-bottom: 20px; }
.story-img { width: 100%; height: 200px; border-radius: 4px; }
.story-info { padding: 12px 0; }
.author { color: #999; font-size: 13px; margin-top: 8px; }
.preview { color: #666; font-size: 14px; margin-top: 8px; }
.pagination { display: flex; justify-content: center; margin-top: 20px; }
.img-fallback { width: 100%; height: 100%; display: flex; flex-direction: column; align-items: center; justify-content: center; background: #f5f7fa; color: #c0c4cc; gap: 8px; font-size: 12px; }
</style>
