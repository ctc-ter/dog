<template>
  <div class="recognize-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>AI 狗狗识别</h1>
      <p>上传一张狗狗照片，AI 将识别品种并为您推荐救助站中同品种的待领养狗狗</p>
    </div>

    <!-- 上传区域 -->
    <div class="upload-section">
      <el-upload
        class="upload-area"
        drag
        :auto-upload="false"
        :show-file-list="false"
        accept="image/jpeg,image/png,image/webp"
        :on-change="handleFileChange"
      >
        <div v-if="!previewUrl" class="upload-placeholder">
          <el-icon size="64" color="#c0c4cc"><UploadFilled /></el-icon>
          <p class="upload-text">将狗狗照片拖到此处，或 <em>点击上传</em></p>
          <p class="upload-hint">支持 JPG / PNG / WebP，最大 5MB</p>
        </div>
        <div v-else class="preview-container">
          <img :src="previewUrl" class="preview-image" />
        </div>
      </el-upload>

      <div class="action-btns">
        <el-button
          type="warning"
          size="large"
          :loading="loading"
          :disabled="!selectedFile"
          @click="startRecognition"
        >
          {{ loading ? 'AI 识别中...' : '开始识别' }}
        </el-button>
        <el-button
          v-if="previewUrl"
          size="large"
          @click="resetForm"
        >
          重新选择
        </el-button>
      </div>
    </div>

    <!-- 识别结果 -->
    <div v-if="result" class="result-section">
      <!-- 品种识别结果 -->
      <div class="breed-result">
        <el-icon size="32" :color="isRecognized ? '#67c23a' : '#e6a23c'">
          <component :is="isRecognized ? 'SuccessFilled' : 'WarningFilled'" />
        </el-icon>
        <div class="breed-info">
          <span class="breed-label">识别品种</span>
          <span class="breed-name">{{ result.recognizedBreed }}</span>
        </div>
      </div>

      <!-- 匹配狗狗列表 -->
      <div v-if="result.matchedDogs && result.matchedDogs.length > 0" class="match-section">
        <h3>在救助站中找到 {{ result.matchCount }} 只同品种狗狗</h3>
        <el-row :gutter="16">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="dog in result.matchedDogs" :key="dog.id">
            <el-card class="dog-card" shadow="hover" @click="goDogDetail(dog.id)">
              <el-image
                :src="dog.imageUrls ? dog.imageUrls.split(',')[0] : ''"
                fit="cover"
                class="dog-img"
              />
              <div class="dog-info">
                <h4>{{ dog.name }}</h4>
                <p>{{ dog.breed }} · {{ dog.gender }} · {{ dog.age }}岁</p>
                <el-tag size="small" :type="statusType(dog.status)">{{ dog.status }}</el-tag>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 无匹配 -->
      <div v-else class="no-match">
        <el-empty description="暂无同品种的待领养狗狗，您可以浏览所有狗狗看看是否有喜欢的">
          <el-button type="primary" @click="$router.push('/dogs')">查看所有狗狗</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const router = useRouter()

const selectedFile = ref(null)
const previewUrl = ref('')
const loading = ref(false)
const result = ref(null)

const isRecognized = computed(() => {
  if (!result.value) return false
  const breed = result.value.recognizedBreed || ''
  return !breed.includes('失败') && !breed.includes('无法') && breed !== '混合品种'
})

const handleFileChange = (file) => {
  const rawFile = file.raw
  // 校验类型
  if (!rawFile.type.startsWith('image/')) {
    ElMessage.warning('请上传图片文件')
    return
  }
  // 校验大小
  if (rawFile.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片大小不能超过 5MB')
    return
  }
  selectedFile.value = rawFile
  previewUrl.value = URL.createObjectURL(rawFile)
  result.value = null
}

const startRecognition = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先上传一张狗狗照片')
    return
  }

  loading.value = true
  result.value = null

  const formData = new FormData()
  formData.append('image', selectedFile.value)

  try {
    // 使用 axios 直接发送，避免 request 拦截器处理
    const res = await request.post('/recognition/recognize', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
      timeout: 60000
    })
    result.value = res
    if (isRecognized.value) {
      ElMessage.success('识别成功！品种：' + res.recognizedBreed)
    } else {
      ElMessage.info('识别结果：' + res.recognizedBreed)
    }
  } catch (e) {
    console.error('识别失败:', e)
    ElMessage.error('识别失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  selectedFile.value = null
  previewUrl.value = ''
  result.value = null
}

const goDogDetail = (id) => {
  router.push('/dog/' + id)
}

const statusType = (status) => {
  const map = { '待领养': 'success', '已领养': 'info', '治疗中': 'warning', '隔离观察': 'danger' }
  return map[status] || 'info'
}
</script>

<style scoped>
.recognize-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 20px;
}
.page-header {
  text-align: center;
  margin-bottom: 40px;
}
.page-header h1 {
  font-size: 32px;
  color: #303133;
  margin-bottom: 12px;
}
.page-header p {
  font-size: 16px;
  color: #909399;
}
.upload-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  margin-bottom: 40px;
}
.upload-area {
  width: 420px;
}
.upload-area :deep(.el-upload-dragger) {
  width: 420px;
  min-height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  border: 2px dashed #dcdfe6;
  transition: border-color 0.3s;
}
.upload-area :deep(.el-upload-dragger:hover) {
  border-color: #e6a23c;
}
.upload-placeholder {
  text-align: center;
}
.upload-text {
  font-size: 16px;
  color: #606266;
  margin-top: 16px;
}
.upload-text em {
  color: #e6a23c;
  font-style: normal;
}
.upload-hint {
  font-size: 13px;
  color: #c0c4cc;
  margin-top: 8px;
}
.preview-container {
  padding: 8px;
}
.preview-image {
  max-width: 400px;
  max-height: 260px;
  border-radius: 8px;
  object-fit: contain;
}
.action-btns {
  display: flex;
  gap: 16px;
}

/* 识别结果 */
.result-section {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 30px;
}
.breed-result {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  padding: 20px 24px;
  border-radius: 8px;
  margin-bottom: 30px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.breed-info {
  display: flex;
  flex-direction: column;
}
.breed-label {
  font-size: 13px;
  color: #909399;
}
.breed-name {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-top: 4px;
}

.match-section h3 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 16px;
}
.dog-card {
  cursor: pointer;
  margin-bottom: 16px;
  border-radius: 8px;
}
.dog-img {
  width: 100%;
  height: 160px;
  border-radius: 6px;
}
.dog-info {
  padding: 10px 0 4px;
}
.dog-info h4 {
  margin-bottom: 6px;
  font-size: 16px;
}
.dog-info p {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}
.no-match {
  padding: 20px 0;
}
</style>
