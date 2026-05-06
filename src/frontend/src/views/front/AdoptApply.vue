<template>
  <div class="apply-page">
    <div class="page-banner">
      <h1>领养申请</h1>
      <p>请认真填写以下信息，我们将尽快审核您的申请</p>
    </div>
    <div class="form-wrap">
      <el-card>
        <el-form :model="form" label-width="120px" @submit.prevent="handleSubmit">
          <el-form-item label="申请领养">
            <el-tag size="large" type="warning">狗狗ID: {{ route.params.id }}</el-tag>
          </el-form-item>
          <el-form-item label="申请人姓名" required>
            <el-input v-model="form.applicantName" />
          </el-form-item>
          <el-form-item label="联系电话" required>
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="居住地址" required>
            <el-input v-model="form.address" type="textarea" rows="2" />
          </el-form-item>
          <el-form-item label="住房类型">
            <el-radio-group v-model="form.housingType">
              <el-radio label="自有住房">自有住房</el-radio>
              <el-radio label="租房">租房</el-radio>
              <el-radio label="其他">其他</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="养宠经验">
            <el-radio-group v-model="form.hasExperience">
              <el-radio :label="true">有</el-radio>
              <el-radio :label="false">无</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="申请理由">
            <el-input v-model="form.reason" type="textarea" rows="4" placeholder="请简述您想领养的原因和您的养宠计划" />
          </el-form-item>
          <el-form-item>
            <el-button type="warning" size="large" native-type="submit">提交申请</el-button>
            <el-button @click="$router.back()">返回</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const route = useRoute()
const router = useRouter()
const form = reactive({
  dogId: route.params.id,
  applicantName: '',
  phone: '',
  address: '',
  housingType: '自有住房',
  hasExperience: false,
  reason: ''
})

const handleSubmit = async () => {
  if (!form.applicantName || !form.phone || !form.address) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    await request.post('/adoptions/apply', form)
    ElMessage.success('申请已提交，请耐心等待审核')
    router.push('/dogs')
  } catch (e) {
    ElMessage.error('提交失败')
  }
}
</script>

<style scoped>
.page-banner {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  color: #fff;
  padding: 60px 40px;
  text-align: center;
}
.page-banner h1 { font-size: 36px; margin-bottom: 10px; }
.form-wrap { max-width: 800px; margin: 30px auto; padding: 0 20px; }
</style>
