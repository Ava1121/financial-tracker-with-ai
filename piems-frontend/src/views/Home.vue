<template>
  <Layout>
    <!-- 页面标题和操作区 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon><House /></el-icon>
          首页
        </h2>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item><a href="/">首页</a></el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="header-right">
        <!-- 可以添加操作按钮 -->
      </div>
    </div>

    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="hover">
      <template #header>
        <div class="welcome-header">
          <h2>欢迎回来，{{ user.nickname || user.username }}！</h2>
          <el-tag type="info">{{ currentDate }}</el-tag>
        </div>
      </template>
      <div class="welcome-content">
        <p class="welcome-desc">轻松管理您的收入和支出，让理财变得简单</p>
        <div class="quick-links">
          <el-button type="primary" @click="goToAddRecord" icon="Plus">
            立即添加记录
          </el-button>
          <el-button type="success" @click="goToStatistics" icon="DataAnalysis">
            查看统计分析
          </el-button>
        </div>
      </div>
    </el-card>
    
    <!-- 数据概览卡片 -->
    <div class="stats-section">
      <h3 class="section-title">数据概览</h3>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="stat-card income-card" shadow="hover">
            <div class="stat-icon income">
              <el-icon><ArrowUp /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">当月收入</div>
              <div class="stat-value">¥{{ monthlySummary.income || 0 }}</div>
              <div class="stat-change positive">+{{ incomeChange }}%</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card expense-card" shadow="hover">
            <div class="stat-icon expense">
              <el-icon><ArrowDown /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">当月支出</div>
              <div class="stat-value">¥{{ monthlySummary.expense || 0 }}</div>
              <div class="stat-change negative">-{{ expenseChange }}%</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card balance-card" shadow="hover">
            <div class="stat-icon balance">
              <el-icon><Wallet /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-label">当月结余</div>
              <div class="stat-value">¥{{ monthlySummary.balance || 0 }}</div>
              <div class="stat-change positive">+{{ balanceChange }}%</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 最近记录 -->
    <div class="recent-records">
      <h3 class="section-title">最近记录</h3>
      <el-card shadow="hover">
        <el-table :data="recentRecords" border style="width: 100%">
          <el-table-column prop="recordTime" label="时间" width="180" />
          <el-table-column prop="type" label="类型" width="100" align="center">
            <template #default="scope">
              <el-tag type="success" v-if="scope.row.type === 1">收入</el-tag>
              <el-tag type="danger" v-else>支出</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="categoryName" label="分类" width="120" align="center" />
          <el-table-column prop="amount" label="金额" width="140" align="right">
            <template #default="scope">
              <span :class="{'income-amount': scope.row.type === 1, 'expense-amount': scope.row.type === 2}">
                {{ scope.row.type === 1 ? '+' : '-' }}¥{{ scope.row.amount }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="200" />
        </el-table>
        <div class="view-all">
          <el-button type="text" @click="goToRecords" icon="Right">查看全部记录</el-button>
        </div>
      </el-card>
    </div>
  </Layout>
</template>

<script>
import { 
  ArrowUp, ArrowDown, Wallet, Plus, House, DataAnalysis 
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import Layout from '@/components/Layout.vue'

export default {
  components: {
    Layout,
    ArrowUp,
    ArrowDown,
    Wallet,
    Plus,
    House,
    DataAnalysis
  },
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user')) || {},
      monthlySummary: {},
      recentRecords: [],
      incomeChange: 0,
      expenseChange: 0,
      balanceChange: 0
    }
  },
  computed: {
    currentDate() {
      const now = new Date()
      return now.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      })
    }
  },
  mounted() {
    this.getMonthlySummary()
    this.getRecentRecords()
  },
  methods: {
    async getMonthlySummary() {
      try {
        const userId = this.user.id
        const now = new Date()
        const res = await request.get('/record/monthly-summary', {
          params: {
            userId,
            year: now.getFullYear(),
            month: now.getMonth() + 1
          }
        })
        this.monthlySummary = res.data
      } catch (error) {
        console.error('获取月度汇总失败:', error)
      }
    },
    async getRecentRecords() {
      try {
        const userId = this.user.id
        const res = await request.get('/record/list', {
          params: {
            userId,
            limit: 5
          }
        })
        this.recentRecords = res.data.slice(0, 5)
      } catch (error) {
        console.error('获取最近记录失败:', error)
      }
    },
    goToAddRecord() {
      this.$router.push('/records')
    },
    goToStatistics() {
      this.$router.push('/statistics')
    },
    goToRecords() {
      this.$router.push('/records')
    }
  }
}
</script>

<style scoped>
/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 欢迎卡片 */
.welcome-card {
  margin-bottom: 24px;
  border-radius: 8px;
  background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
  color: #333;
  border: none;
}

.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #333;
}

.welcome-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #000;
}

.welcome-header .el-tag {
  background-color: rgba(0, 0, 0, 0.1);
  border-color: rgba(0, 0, 0, 0.2);
  color: #333;
}

.welcome-content {
  padding: 20px 0;
}

.welcome-desc {
  font-size: 16px;
  margin-bottom: 20px;
  color: #666;
}

.quick-links {
  display: flex;
  gap: 12px;
}

.quick-links .el-button {
  background-color: white;
  color: #333;
  border: 1px solid #ddd;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.quick-links .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #999;
}

.quick-links .el-button:nth-child(2) {
  background-color: #f5f5f5;
  color: #333;
  border: 1px solid #ddd;
}

/* 数据概览 */
.stats-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-left: 4px;
  border-left: 4px solid #409EFF;
}

.stat-card {
  border-radius: 8px;
  border: none;
  transition: all 0.3s ease;
  cursor: pointer;
  overflow: hidden;
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.income-card {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
}

.expense-card {
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
}

.balance-card {
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
}

.stat-icon {
  font-size: 28px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-icon.income {
  background-color: rgba(103, 194, 58, 0.2);
  color: #67c23a;
}

.stat-icon.expense {
  background-color: rgba(245, 108, 108, 0.2);
  color: #f56c6c;
}

.stat-icon.balance {
  background-color: rgba(64, 158, 255, 0.2);
  color: #409EFF;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-change {
  font-size: 12px;
  font-weight: 500;
}

.stat-change.positive {
  color: #67c23a;
}

.stat-change.negative {
  color: #f56c6c;
}

/* 最近记录 */
.recent-records {
  margin-bottom: 24px;
}

.recent-records .el-card {
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.recent-records .el-table {
  border: none;
  margin-bottom: 16px;
}

.income-amount {
  color: #67c23a;
  font-weight: 500;
}

.expense-amount {
  color: #f56c6c;
  font-weight: 500;
}

.view-all {
  text-align: right;
  padding: 0 20px 16px 0;
}

.view-all .el-button {
  color: #409EFF;
  padding: 0;
}
</style>
