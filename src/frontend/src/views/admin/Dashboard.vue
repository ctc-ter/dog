<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card><div class="stat-title">待领养狗狗</div><div class="stat-value">{{ stats.dogCount }}</div></el-card>
      </el-col>
      <el-col :span="6">
        <el-card><div class="stat-title">待审核申请</div><div class="stat-value">{{ stats.adoptionCount }}</div></el-card>
      </el-col>
      <el-col :span="6">
        <el-card><div class="stat-title">本月捐款</div><div class="stat-value">¥{{ stats.donationAmount }}</div></el-card>
      </el-col>
      <el-col :span="6">
        <el-card><div class="stat-title">志愿者人数</div><div class="stat-value">{{ stats.volunteerCount }}</div></el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card><h3>近期领养申请</h3>
          <el-table :data="recentAdoptions" size="small">
            <el-table-column prop="applicantName" label="申请人" />
            <el-table-column prop="phone" label="电话" />
            <el-table-column prop="status" label="状态" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card><h3>近期捐赠</h3>
          <el-table :data="recentDonations" size="small">
            <el-table-column prop="donorName" label="捐赠人" />
            <el-table-column prop="amount" label="金额" />
            <el-table-column prop="status" label="状态" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const stats = ref({ dogCount: 0, adoptionCount: 0, donationAmount: 0, volunteerCount: 0 })
const recentAdoptions = ref([])
const recentDonations = ref([])

const loadData = async () => {
  try {
    const dogs = await request.get('/dogs', { params: { page: 1, size: 1, status: '待领养' } })
    stats.value.dogCount = dogs.total || 0
    const adoptions = await request.get('/adoptions', { params: { page: 1, size: 1, status: '待审核' } })
    stats.value.adoptionCount = adoptions.total || 0
    const donations = await request.get('/donations', { params: { page: 1, size: 1 } })
    stats.value.donationAmount = donations.list?.reduce((sum, d) => sum + (d.amount || 0), 0) || 0
    const volunteers = await request.get('/volunteers', { params: { page: 1, size: 1, status: '已通过' } })
    stats.value.volunteerCount = volunteers.total || 0

    const recentA = await request.get('/adoptions', { params: { page: 1, size: 5 } })
    recentAdoptions.value = recentA.list || []
    const recentD = await request.get('/donations', { params: { page: 1, size: 5 } })
    recentDonations.value = recentD.list || []
  } catch (e) {}
}

onMounted(loadData)
</script>

<style scoped>
.stat-title { color: #666; font-size: 14px; }
.stat-value { font-size: 32px; font-weight: bold; color: #409EFF; margin-top: 8px; }
h3 { margin-bottom: 16px; }
</style>
