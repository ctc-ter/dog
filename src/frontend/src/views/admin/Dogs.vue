<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>狗狗管理</h2>
      <el-button type="primary" @click="openDialog()">新增狗狗</el-button>
    </div>
    <el-table :data="list" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="照片" width="100">
        <template #default="scope">
          <el-image :src="scope.row.imageUrls ? scope.row.imageUrls.split(',')[0] : ''" style="width:60px;height:60px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名字" />
      <el-table-column prop="breed" label="品种" />
      <el-table-column prop="gender" label="性别" />
      <el-table-column prop="age" label="年龄" />
      <el-table-column prop="status" label="状态" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="prev, pager, next" @change="loadData" style="margin-top:16px" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑狗狗' : '新增狗狗'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名字"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="品种"><el-input v-model="form.breed" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender"><el-radio label="公">公</el-radio><el-radio label="母">母</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="0" /></el-form-item>
        <el-form-item label="健康状态"><el-input v-model="form.healthStatus" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status"><el-option label="待领养" value="待领养" /><el-option label="已领养" value="已领养" /><el-option label="治疗中" value="治疗中" /></el-select>
        </el-form-item>
        <el-form-item label="照片URL">
          <el-input v-model="form.imageUrls" placeholder="多个URL用逗号分隔" />
        </el-form-item>
        <el-form-item label="介绍"><el-input v-model="form.description" type="textarea" rows="4" /></el-form-item>
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
const form = reactive({ id: null, name: '', breed: '', gender: '公', age: 1, healthStatus: '', status: '待领养', imageUrls: '', description: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/dogs', { params: { page: page.value, size: size.value } })
    list.value = res.list || []
    total.value = res.total || 0
  } catch (e) {}
  loading.value = false
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, { id: null, name: '', breed: '', gender: '公', age: 1, healthStatus: '', status: '待领养', imageUrls: '', description: '' })
  }
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    if (form.id) {
      await request.put('/dogs/' + form.id, form)
    } else {
      await request.post('/dogs', form)
    }
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
    await request.delete('/dogs/' + id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
