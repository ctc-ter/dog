<template>
  <div class="home">
    <!-- 横幅 -->
    <div class="hero">
      <div class="hero-content">
        <h1>每一个生命都值得被温柔以待</h1>
        <p>加入我们，用行动终结流浪，给毛孩子一个温暖的家</p>
        <div class="hero-btns">
          <el-button type="warning" size="large" @click="$router.push('/dogs')">我要领养</el-button>
          <el-button type="primary" size="large" @click="$router.push('/donate')">爱心捐赠</el-button>
        </div>
      </div>
    </div>

    <!-- 统计 -->
    <div class="stats-bar">
      <div class="stat-item">
        <div class="stat-num">128</div>
        <div class="stat-label">已救助狗狗</div>
      </div>
      <div class="stat-item">
        <div class="stat-num">86</div>
        <div class="stat-label">成功领养</div>
      </div>
      <div class="stat-item">
        <div class="stat-num">45</div>
        <div class="stat-label">活跃志愿者</div>
      </div>
      <div class="stat-item">
        <div class="stat-num">¥32,560</div>
        <div class="stat-label">本月捐款</div>
      </div>
    </div>

    <!-- 待领养狗狗 -->
    <div class="section">
      <div class="section-title">
        <h2>等待回家的毛孩子</h2>
        <el-link type="primary" @click="$router.push('/dogs')">查看更多</el-link>
      </div>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="dog in dogList" :key="dog.id">
          <el-card class="dog-card" shadow="hover" @click="goDetail(dog.id)">
            <el-image :src="dog.imageUrls ? dog.imageUrls.split(',')[0] : ''" fit="cover" class="dog-img" />
            <div class="dog-info">
              <h3>{{ dog.name }} <el-tag size="small" :type="dog.gender === '公' ? 'primary' : 'danger'">{{ dog.gender }}</el-tag></h3>
              <p class="dog-breed">{{ dog.breed }} · {{ dog.age }}岁</p>
              <p class="dog-desc">{{ dog.description }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 救助故事 -->
    <div class="section bg-light">
      <div class="section-title">
        <h2>温暖的救助故事</h2>
        <el-link type="primary" @click="$router.push('/stories')">查看更多</el-link>
      </div>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" v-for="story in storyList" :key="story.id">
          <el-card class="story-card" shadow="hover" @click="goStory(story.id)">
            <el-image :src="story.coverImage" fit="cover" class="story-img" />
            <div class="story-info">
              <h3>{{ story.title }}</h3>
              <p>{{ story.content ? story.content.substring(0, 60) + '...' : '' }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/request'

const router = useRouter()
const dogList = ref([])
const storyList = ref([])

const loadData = async () => {
  try {
    const dogs = await request.get('/dogs', { params: { page: 1, size: 4, status: '待领养' } })
    dogList.value = dogs.list || []
    const stories = await request.get('/stories', { params: { page: 1, size: 3 } })
    storyList.value = stories.list || []
  } catch (e) {
    dogList.value = [
      { id: 1, name: '旺财', gender: '公', breed: '中华田园犬', age: 2, description: '性格温顺，喜欢和人亲近', imageUrls: '' },
      { id: 2, name: '花花', gender: '母', breed: '金毛', age: 1, description: '活泼可爱，已完成疫苗接种', imageUrls: '' },
      { id: 3, name: '小黑', gender: '公', breed: '拉布拉多', age: 3, description: '聪明懂事，会基本指令', imageUrls: '' },
      { id: 4, name: '豆豆', gender: '母', breed: '泰迪', age: 1, description: '小巧玲珑，适合家庭饲养', imageUrls: '' }
    ]
    storyList.value = [
      { id: 1, title: '从垃圾桶旁到温暖的家', coverImage: '', content: '那是一个寒冷的冬夜...' },
      { id: 2, title: '三条腿的它也值得被爱', coverImage: '', content: '小白因车祸失去了左后腿...' },
      { id: 3, title: '一群大学生的救助行动', coverImage: '', content: '校园里的流浪狗问题...' }
    ]
  }
}

const goDetail = (id) => router.push('/dog/' + id)
const goStory = (id) => router.push('/story/' + id)

onMounted(loadData)
</script>

<style scoped>
.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 80px 40px;
  text-align: center;
}
.hero h1 {
  font-size: 42px;
  margin-bottom: 16px;
}
.hero p {
  font-size: 18px;
  margin-bottom: 32px;
  opacity: 0.9;
}
.hero-btns {
  display: flex;
  gap: 16px;
  justify-content: center;
}
.stats-bar {
  display: flex;
  justify-content: center;
  gap: 60px;
  padding: 40px;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.stat-item {
  text-align: center;
}
.stat-num {
  font-size: 32px;
  font-weight: bold;
  color: #e6a23c;
}
.stat-label {
  color: #666;
  margin-top: 8px;
}
.section {
  padding: 50px 40px;
}
.bg-light {
  background: #f8f9fa;
}
.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.dog-card {
  cursor: pointer;
  margin-bottom: 20px;
}
.dog-img {
  width: 100%;
  height: 200px;
  border-radius: 4px;
}
.dog-info {
  padding: 12px 0;
}
.dog-info h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.dog-breed {
  color: #666;
  font-size: 14px;
}
.dog-desc {
  color: #999;
  font-size: 13px;
  margin-top: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.story-card {
  cursor: pointer;
  margin-bottom: 20px;
}
.story-img {
  width: 100%;
  height: 180px;
  border-radius: 4px;
}
.story-info {
  padding: 12px 0;
}
.story-info p {
  color: #666;
  font-size: 14px;
  margin-top: 8px;
}
</style>
