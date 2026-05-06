<template>
  <div class="lostfound-page">
    <div class="page-banner">
      <h1>寻狗 / 招领</h1>
      <p>帮助走失的狗狗回家，或发布您发现的流浪狗信息</p>
    </div>
    <div class="content-wrap">
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="寻狗启示" name="lost">
          <el-button type="primary" @click="showDialog('lost')">发布寻狗</el-button>
          <el-table :data="lostList" style="margin-top:16px">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="location" label="走失地点" />
            <el-table-column prop="eventTime" label="时间" />
            <el-table-column prop="contactPhone" label="联系电话" />
            <el-table-column prop="status" label="状态" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="招领启示" name="found">
          <el-button type="primary" @click="showDialog('found')">发布招领</el-button>
          <el-table :data="foundList" style="margin-top:16px">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="location" label="发现地点" />
            <el-table-column prop="eventTime" label="时间" />
            <el-table-column prop="contactPhone" label="联系电话" />
            <el-table-column prop="status" label="状态" />
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
        <el-form :model="form" label-width="100px">
          <el-form-item label="标题">
            <el-input v-model="form.title" />
          </el-form-item>
          <el-form-item label="地点">
            <el-input v-model="form.location" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="form.description" type="textarea" rows="3" />
          </el-form-item>
          <el-form-item label="联系人">
            <el-input v-model="form.contactName" />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="form.contactPhone" />
          </el-form-item>
          <el-form-item label="发生时间">
            <el-date-picker v-model="form.eventTime" type="datetime" placeholder="选择时间" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">提交</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const activeTab = ref('lost')
const list = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({ type: '', title: '', location: '', description: '', contactName: '', contactPhone: '', eventTime: '' })

const lostList = computed(() => list.value.filter(i => i.type === '寻狗'))
const foundList = computed(() => list.value.filter(i => i.type === '招领'))

const loadData = async () => {
  try {
    const res = await request.get('/lost-found', { params: { page: 1, size: 50 } })
    list.value = res.list || []
  } catch (e) {}
}

const showDialog = (type) => {
  form.value = { type: type === 'lost' ? '寻狗' : '招领', title: '', location: '', description: '', contactName: '', contactPhone: '', eventTime: '' }
  dialogTitle.value = type === 'lost' ? '发布寻狗启示' : '发布招领启示'
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    await request.post('/lost-found', form.value)
    ElMessage.success('发布成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('发布失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-banner {
  background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);
  color: #fff;
  padding: 60px 40px;
  text-align: center;
}
.page-banner h1 { font-size: 36px; margin-bottom: 10px; }
.content-wrap { max-width: 1200px; margin: 30px auto; padding: 0 20px; }
</style>
