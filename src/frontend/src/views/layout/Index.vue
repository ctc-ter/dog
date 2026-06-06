<template>
  <div class="layout">
    <el-header class="header">
      <div class="logo">
        <el-icon size="32" color="#e6a23c"><Cherry /></el-icon>
        <span>流浪狗救助站</span>
      </div>
      <el-menu mode="horizontal" :router="true" :default-active="$route.path" class="nav-menu">
        <el-menu-item index="/home">首页</el-menu-item>
        <el-menu-item index="/dogs">待领养狗狗</el-menu-item>
        <el-menu-item index="/stories">救助故事</el-menu-item>
        <el-menu-item index="/donate">爱心捐赠</el-menu-item>
        <el-menu-item index="/volunteer">志愿者招募</el-menu-item>
        <el-menu-item index="/lost-found">寻狗/招领</el-menu-item>
        <el-menu-item index="/recognize">AI识别</el-menu-item>
      </el-menu>
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>
    <el-footer class="footer">
      <div class="footer-content">
        <p>流浪狗救助站 - 用爱终止流浪</p>
        <p>联系电话：400-xxx-xxxx | 邮箱：help@dogrescue.org</p>
        <p style="font-size:12px;color:#999;margin-top:8px;">本网站为公益性质，所有信息真实有效</p>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 0 40px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
  color: #e6a23c;
}
.nav-menu {
  border-bottom: none;
}
.main {
  flex: 1;
  padding: 0;
}
.footer {
  background: #2c3e50;
  color: #fff;
  text-align: center;
  padding: 30px 0;
}
.footer-content p {
  margin: 6px 0;
}
</style>
