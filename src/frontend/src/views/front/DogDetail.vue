<template>
  <div class="detail-page" v-if="dog">
    <div class="detail-container">
      <el-row :gutter="30">
        <el-col :md="12">
          <el-carousel height="400px" v-if="images.length">
            <el-carousel-item v-for="(img, idx) in images" :key="idx">
              <el-image :src="img" fit="cover" style="width:100%;height:100%" />
            </el-carousel-item>
          </el-carousel>
        </el-col>
        <el-col :md="12">
          <h1>{{ dog.name }}</h1>
          <div class="info-row">
            <span class="label">品种：</span>{{ dog.breed }}
          </div>
          <div class="info-row">
            <span class="label">性别：</span>{{ dog.gender }}
          </div>
          <div class="info-row">
            <span class="label">年龄：</span>{{ dog.age }}岁
          </div>
          <div class="info-row">
            <span class="label">健康状态：</span>{{ dog.healthStatus }}
          </div>
          <div class="info-row">
            <span class="label">当前状态：</span><el-tag>{{ dog.status }}</el-tag>
          </div>
          <div class="info-row desc">
            <span class="label">介绍：</span>
            <p>{{ dog.description }}</p>
          </div>
          <el-button type="warning" size="large" v-if="dog.status === '待领养'" @click="goApply">申请领养</el-button>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/api/request'

const route = useRoute()
const router = useRouter()
const dog = ref(null)

const images = computed(() => {
  return dog.value?.imageUrls ? dog.value.imageUrls.split(',') : []
})

const loadData = async () => {
  try {
    dog.value = await request.get('/dogs/' + route.params.id)
  } catch (e) {
    dog.value = null
  }
}

const goApply = () => router.push('/adopt-apply/' + route.params.id)
onMounted(loadData)
</script>

<style scoped>
.detail-page { padding: 40px; }
.detail-container { max-width: 1200px; margin: 0 auto; background: #fff; padding: 30px; border-radius: 8px; }
h1 { font-size: 28px; margin-bottom: 20px; }
.info-row { margin-bottom: 14px; font-size: 16px; }
.label { color: #666; font-weight: 500; }
.desc p { margin-top: 8px; line-height: 1.8; color: #555; }
</style>
