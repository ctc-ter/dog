<template>
  <div class="admin-page">
    <h2>领养申请管理</h2>
    <el-table :data="list" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="dogId" label="狗狗ID" />
      <el-table-column prop="applicantName" label="申请人" />
      <el-table-column prop="phone" label="电话" />
      <el-table-column prop="address" label="地址" show-overflow-tooltip />
      <el-table-column prop="housingType" label="住房" />
      <el-table-column prop="hasExperience" label="经验"><template #default="s"><el-tag>{{ s.row.hasExperience ? '有' : '无' }}</el-tag></template></el-table-column>
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" type="success" v-if="scope.row.status === '待审核'" @click="audit(scope.row.id, '通过')">通过</el-button>
          <el-button size="small" type="danger" v-if="scope.row.status === '待审核'" @click="audit(scope.row.id, '拒绝')">拒绝</el-button>
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
    const res = await request.get('/adoptions', { params: { page: page.value, size: size.value } })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) {}
  loading.value = false
}

const audit = async (id, status) => {
  try {
    await request.post('/adoptions/' + id + '/audit', { status, remark: '' })
    ElMessage.success('审核完成')
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
