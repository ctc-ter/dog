<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <span>救助站管理后台</span>
      </div>
      <el-menu :default-active="$route.path" router class="admin-menu" background-color="#304156" text-color="#bfcbd9" active-text-color="#409EFF">
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/admin/dogs">
          <el-icon><Promotion /></el-icon>
          <span>狗狗管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/adoptions">
          <el-icon><Document /></el-icon>
          <span>领养申请</span>
        </el-menu-item>
        <el-menu-item index="/admin/donations">
          <el-icon><Money /></el-icon>
          <span>捐赠记录</span>
        </el-menu-item>
        <el-menu-item index="/admin/stories">
          <el-icon><Reading /></el-icon>
          <span>救助故事</span>
        </el-menu-item>
        <el-menu-item index="/admin/volunteers">
          <el-icon><UserFilled /></el-icon>
          <span>志愿者</span>
        </el-menu-item>
        <el-menu-item index="/admin/lost-found">
          <el-icon><Search /></el-icon>
          <span>寻狗/招领</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><Setting /></el-icon>
          <span>管理员</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              {{ userStore.userInfo?.username || '管理员' }} <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (cmd) => {
  if (cmd === 'logout') {
    userStore.clearToken()
    ElMessage.success('已退出')
    router.push('/admin/login')
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}
.aside {
  background: #304156;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}
.admin-menu {
  border-right: none;
}
.admin-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.user-info {
  cursor: pointer;
  color: #606266;
}
.admin-main {
  background: #f0f2f5;
  padding: 20px;
}
</style>
