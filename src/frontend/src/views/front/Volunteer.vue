<template>
  <div class="volunteer-page">
    <div class="page-banner">
      <h1>志愿者招募</h1>
      <p>我们需要您的力量，一起守护流浪的生命</p>
    </div>
    <div class="content-wrap">
      <el-row :gutter="30">
        <el-col :md="12">
          <el-card>
            <h2>志愿者工作内容</h2>
            <ul class="duty-list">
              <li>协助救助站日常清洁和狗狗照料</li>
              <li>参与流浪狗救助行动</li>
              <li>协助领养审核和回访工作</li>
              <li>参与公益宣传活动</li>
              <li>帮助维护网站和社交媒体</li>
            </ul>
          </el-card>
        </el-col>
        <el-col :md="12">
          <el-card>
            <h2>报名表单</h2>
            <el-form :model="form" label-width="100px" @submit.prevent="handleSubmit">
              <el-form-item label="姓名" required>
                <el-input v-model="form.name" />
              </el-form-item>
              <el-form-item label="电话" required>
                <el-input v-model="form.phone" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="form.email" />
              </el-form-item>
              <el-form-item label="特长技能">
                <el-input v-model="form.skills" placeholder="如：摄影、医疗、驾驶等" />
              </el-form-item>
              <el-form-item label="可参与时间">
                <el-input v-model="form.availableTime" placeholder="如：周末、工作日晚上" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" native-type="submit">提交报名</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const form = reactive({ name: '', phone: '', email: '', skills: '', availableTime: '' })

const handleSubmit = async () => {
  if (!form.name || !form.phone) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    await request.post('/volunteers/apply', form)
    ElMessage.success('报名成功，工作人员会尽快联系您')
    form.name = ''; form.phone = ''; form.email = ''; form.skills = ''; form.availableTime = ''
  } catch (e) {
    ElMessage.error('提交失败')
  }
}
</script>

<style scoped>
.page-banner {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: #fff;
  padding: 60px 40px;
  text-align: center;
}
.page-banner h1 { font-size: 36px; margin-bottom: 10px; }
.content-wrap { max-width: 1200px; margin: 30px auto; padding: 0 20px; }
.duty-list { padding-left: 20px; }
.duty-list li { margin-bottom: 12px; color: #555; line-height: 1.8; }
</style>
