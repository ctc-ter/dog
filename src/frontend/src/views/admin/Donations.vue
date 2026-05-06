<template>
  <div class="admin-page">
    <h2>捐赠记录</h2>
    <el-table :data="list" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="donorName" label="捐赠人" />
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="paymentMethod" label="支付方式" />
      <el-table-column prop="transactionId" label="交易号" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="donateTime" label="时间" />
    </el-table>
    <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="prev, pager, next" @change="loadData" style="margin-top:16px" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/donations', { params: { page: page.value, size: size.value } })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) {}
  loading.value = false
}

onMounted(loadData)
</script>

<style scoped>
.admin-page h2 { margin-bottom: 16px; }
</style>
