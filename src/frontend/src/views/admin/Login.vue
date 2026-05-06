<template>
  <div class="login-page">
    <el-card class="login-box">
      <h2>救助站管理系统</h2>
      <el-form :model="form" @submit.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" style="width:100%">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ username: '', password: '' })

const handleLogin = async () => {
  try {
    const res = await request.post('/auth/login', form)
    userStore.setToken(res.token)
    userStore.userInfo = { username: res.username, role: res.role }
    ElMessage.success('登录成功')
    router.push('/admin/dashboard')
  } catch (e) {
    ElMessage.error('登录失败')
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  width: 400px;
  padding: 20px;
}
.login-box h2 {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
}
</style>
