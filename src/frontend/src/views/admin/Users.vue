<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>管理员账户</h2>
      <el-button type="primary" @click="dialogVisible = true">新增管理员</el-button>
    </div>
    <el-table :data="list" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="role" label="角色" />
      <el-table-column prop="lastLogin" label="最后登录" />
      <el-table-column prop="createTime" label="创建时间" />
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增管理员" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role"><el-option label="admin" value="admin" /><el-option label="editor" value="editor" /></el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = reactive({ username: '', password: '', role: 'admin' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/users')
    list.value = res || []
  } catch (e) {}
  loading.value = false
}

const submitForm = async () => {
  try {
    await request.post('/users', { ...form, passwordHash: form.password })
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
