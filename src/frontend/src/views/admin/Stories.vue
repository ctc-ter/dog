<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>救助故事管理</h2>
      <el-button type="primary" @click="openDialog()">发布故事</el-button>
    </div>
    <el-table :data="list" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="封面" width="100">
        <template #default="scope">
          <el-image :src="scope.row.coverImage" style="width:60px;height:60px" fit="cover">
            <template #error><div style="width:60px;height:60px;display:flex;align-items:center;justify-content:center;background:#f5f7fa;color:#c0c4cc;font-size:10px">失败</div></template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="publishTime" label="发布时间" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="prev, pager, next" @change="loadData" style="margin-top:16px" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑故事' : '发布故事'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="作者"><el-input v-model="form.author" /></el-form-item>
        <el-form-item label="封面URL"><el-input v-model="form.coverImage" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" rows="8" /></el-form-item>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const dialogVisible = ref(false)
const form = reactive({ id: null, title: '', author: '', coverImage: '', content: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/stories', { params: { page: page.value, size: size.value } })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) {}
  loading.value = false
}

const openDialog = (row) => {
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: null, title: '', author: '', coverImage: '', content: '' })
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    if (form.id) await request.put('/stories/' + form.id, form)
    else await request.post('/stories', form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' })
    await request.delete('/stories/' + id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
