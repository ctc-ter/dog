<template>
  <div class="donate-page">
    <div class="page-banner">
      <h1>爱心捐赠</h1>
      <p>您的每一分爱心都将用于流浪狗的救助和照料</p>
    </div>
    <div class="donate-container">
      <el-card class="donate-card">
        <h2>选择捐赠金额</h2>
        <div class="amount-options">
          <el-button
            v-for="amt in amounts"
            :key="amt"
            :type="selectedAmount === amt ? 'warning' : 'default'"
            size="large"
            @click="selectedAmount = amt"
          >¥{{ amt }}</el-button>
          <el-input v-model="customAmount" placeholder="自定义金额" style="width:120px" size="large" />
        </div>
        <el-form :model="form" label-width="80px" style="margin-top:24px">
          <el-form-item label="捐赠人">
            <el-input v-model="form.donorName" placeholder="可匿名" />
          </el-form-item>
          <el-form-item label="支付方式">
            <el-radio-group v-model="form.paymentMethod">
              <el-radio label="微信支付">微信支付</el-radio>
              <el-radio label="支付宝">支付宝</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <div class="qr-section" v-if="selectedAmount || customAmount">
          <p>请使用{{ form.paymentMethod }}扫描下方二维码完成支付</p>
          <div class="qr-placeholder">
            <p>[ 支付二维码占位 ]</p>
            <p style="font-size:12px;color:#999">实际开发时接入微信支付/支付宝 SDK 生成真实二维码</p>
          </div>
        </div>
        <el-button type="warning" size="large" style="width:100%;margin-top:16px" @click="submitDonate">确认捐赠</el-button>
      </el-card>

      <el-card class="record-card" style="margin-top:20px">
        <h3>近期捐赠记录</h3>
        <el-table :data="donationList" style="width:100%">
          <el-table-column prop="donorName" label="捐赠人" />
          <el-table-column prop="amount" label="金额" />
          <el-table-column prop="paymentMethod" label="方式" />
          <el-table-column prop="donateTime" label="时间" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const amounts = [10, 20, 50, 100, 200, 500]
const selectedAmount = ref(null)
const customAmount = ref('')
const form = reactive({ donorName: '', paymentMethod: '微信支付' })
const donationList = ref([])

const loadData = async () => {
  try {
    const res = await request.get('/donations', { params: { page: 1, size: 5, status: '已完成' } })
    donationList.value = res.list || []
  } catch (e) {}
}

const submitDonate = async () => {
  const amount = customAmount.value ? parseFloat(customAmount.value) : selectedAmount.value
  if (!amount || amount <= 0) {
    ElMessage.warning('请选择或输入捐赠金额')
    return
  }
  try {
    await request.post('/donations', { donorName: form.donorName || '匿名', amount, paymentMethod: form.paymentMethod })
    ElMessage.success('捐赠记录已创建，请完成支付')
    loadData()
  } catch (e) {
    ElMessage.error('提交失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.page-banner {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: #fff;
  padding: 60px 40px;
  text-align: center;
}
.page-banner h1 { font-size: 36px; margin-bottom: 10px; }
.donate-container { max-width: 800px; margin: 30px auto; padding: 0 20px; }
.amount-options { display: flex; gap: 12px; flex-wrap: wrap; align-items: center; margin-top: 16px; }
.qr-section { text-align: center; margin-top: 24px; padding: 20px; background: #f8f9fa; border-radius: 8px; }
.qr-placeholder { width: 200px; height: 200px; margin: 16px auto; background: #e9ecef; display: flex; flex-direction: column; align-items: center; justify-content: center; border-radius: 8px; }
</style>
