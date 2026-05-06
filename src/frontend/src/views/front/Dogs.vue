<template>
  <div class="dogs-page">
    <div class="page-banner">
      <h1>等待回家的毛孩子</h1>
      <p>每一只狗狗都在等待一个温暖的家</p>
    </div>
    <div class="filter-bar">
      <el-form :inline="true" :model="query">
        <el-form-item label="品种">
          <el-select v-model="query.breed" placeholder="全部品种" clearable>
            <el-option label="中华田园犬" value="中华田园犬" />
            <el-option label="金毛" value="金毛" />
            <el-option label="拉布拉多" value="拉布拉多" />
            <el-option label="泰迪" value="泰迪" />
            <el-option label="柯基" value="柯基" />
            <el-option label="哈士奇" value="哈士奇" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="query.gender" placeholder="全部" clearable>
            <el-option label="公" value="公" />
            <el-option label="母" value="母" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">筛选</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-row :gutter="20" class="dog-list">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="dog in list" :key="dog.id">
        <el-card shadow="hover" class="dog-card" @click="goDetail(dog.id)">
          <el-image :src="dog.imageUrls ? dog.imageUrls.split(',')[0] : ''" fit="cover" class="dog-img" />
          <div class="dog-info">
            <h3>{{ dog.name }} <el-tag size="small" :type="dog.status === '待领养' ? 'success' : 'info'">{{ dog.status }}</el-tag></h3>
            <p>{{ dog.breed }} · {{ dog.gender }} · {{ dog.age }}岁</p>
            <p class="health">健康状态：{{ dog.healthStatus }}</p>
            <p class="desc">{{ dog.description }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <div class="pagination">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="prev, pager, next"
        @change="loadData"
      />
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/api/request'

const router = useRouter()
const list = ref([])
const total = ref(0)
const query = reactive({ page: 1, size: 12, breed: '', gender: '', status: '待领养' })

const loadData = async () => {
  try {
    const res = await request.get('/dogs', { params: query })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) {
    list.value = []
  }
}

const goDetail = (id) => router.push('/dog/' + id)
onMounted(loadData)
</script>

<style scoped>
.dogs-page { padding-bottom: 40px; }
.page-banner {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
  padding: 60px 40px;
  text-align: center;
}
.page-banner h1 { font-size: 36px; margin-bottom: 10px; }
.filter-bar { padding: 20px 40px; background: #fff; }
.dog-list { padding: 20px 40px; }
.dog-card { cursor: pointer; margin-bottom: 20px; }
.dog-img { width: 100%; height: 220px; border-radius: 4px; }
.dog-info { padding: 12px 0; }
.dog-info h3 { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.health { color: #67c23a; font-size: 13px; margin-top: 6px; }
.desc { color: #999; font-size: 13px; margin-top: 8px; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.pagination { display: flex; justify-content: center; margin-top: 20px; }
</style>
