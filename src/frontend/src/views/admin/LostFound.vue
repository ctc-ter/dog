<template>
  <div class="admin-page">
    <h2>寻狗/招领管理</h2>
    <el-table :data="list" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="location" label="地点" />
      <el-table-column prop="contactName" label="联系人" />
      <el-table-column prop="contactPhone" label="电话" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button size="small" @click="updateStatus(scope.row.id, '已完成')">标记完成</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="prev, pager, next" @change="loadData" style="margin-top:16px" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/lost-found', { params: { page: page.value, size: size.value } })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) {}
  loading.value = false
}

const updateStatus = async (id, status) => {
  try {
    await request.post('/lost-found/' + id + '/status', { status })
    ElMessage.success('状态更新成功')
    loadData()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.admin-page h2 { margin-bottom: 16px; }
</style>
