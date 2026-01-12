<template>
  <Layout>
    <!-- 页面标题和操作区 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon><DataAnalysis /></el-icon>
          数据统计
        </h2>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item><a href="/">首页</a></el-breadcrumb-item>
          <el-breadcrumb-item>数据统计</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="header-right">
        <!-- 可以添加操作按钮 -->
      </div>
    </div>

    <!-- 统计概览卡片 -->
    <div class="stats-overview">
      <h3 class="section-title">月度概览</h3>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="stat-card income-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-label">当月收入</div>
              <div class="stat-value income">¥{{ monthlySummary.income || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card expense-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-label">当月支出</div>
              <div class="stat-value expense">¥{{ monthlySummary.expense || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card balance-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-label">当月结余</div>
              <div class="stat-value balance">¥{{ monthlySummary.balance || 0 }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 收支趋势图表 -->
    <div class="chart-section">
      <h3 class="section-title">收支趋势</h3>
      <el-card class="chart-card" shadow="hover">
        <div ref="chartRef" style="width: 100%; height: 450px;"></div>
      </el-card>
    </div>

    <!-- AI财务分析 -->
    <div class="ai-analysis-section">
      <h3 class="section-title">AI财务分析</h3>
      <div class="ai-content">
        <h4>分析概览</h4>
        <p>3个月总收入: ¥{{ aiAnalysis.totalIncome || 0 }}</p>
        <p>3个月总支出: ¥{{ aiAnalysis.totalExpense || 0 }}</p>
        <p>3个月结余: ¥{{ aiAnalysis.savings || 0 }}</p>
        <p>总储蓄率: {{ aiAnalysis.savingsRate ? aiAnalysis.savingsRate.toFixed(1) : 0 }}%</p>
        
        <h4>AI理财建议</h4>
        <ul>
          <li v-for="(suggestion, index) in aiAnalysis.suggestions" :key="index">
            {{ suggestion }}
          </li>
        </ul>
      </div>
    </div>
  </Layout>
</template>

<script>
import request from '@/utils/request'
import * as echarts from 'echarts'
import Layout from '@/components/Layout.vue'
import { ArrowUp, ArrowDown, Wallet, DataAnalysis } from '@element-plus/icons-vue'
import { ElTimeline, ElTimelineItem, ElTag } from 'element-plus'

export default {
  components: {
    Layout,
    ArrowUp,
    ArrowDown,
    Wallet,
    DataAnalysis,
    ElTimeline,
    ElTimelineItem,
    ElTag
  },
  data() {
    return {
      monthlySummary: {},
      annualSummary: {},
      aiAnalysis: {},
      chart: null
    }
  },
  mounted() {
    this.initChart()
    this.getMonthlySummary()
    this.getAnnualSummary()
    this.getAIAnalysis()
  },
  beforeUnmount() {
    // 销毁图表实例，释放资源
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
  },
  methods: {
    // 初始化图表
    initChart() {
      // 确保DOM元素已渲染
      this.$nextTick(() => {
        this.chart = echarts.init(this.$refs.chartRef)
        
        // 设置图表选项
        const option = {
          title: {
            text: '年度收支趋势',
            left: 'center'
          },
          tooltip: {
            trigger: 'axis',
            formatter: function(params) {
              let result = params[0].axisValue + '<br/>'
              params.forEach(param => {
                result += `${param.marker} ${param.seriesName}: ${param.value}元<br/>`
              })
              return result
            }
          },
          legend: {
            data: ['收入', '支出'],
            top: '10%'
          },
          grid: {
            left: '3%',
            right: '3%',
            bottom: '3%',
            top: '15%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              formatter: '{value} 元'
            }
          },
          series: [
            {
              name: '收入',
              type: 'line',
              data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
              smooth: true,
              lineStyle: {
                color: '#67c23a'
              },
              itemStyle: {
                color: '#67c23a'
              },
              areaStyle: {
                color: {
                  type: 'linear',
                  x: 0,
                  y: 0,
                  x2: 0,
                  y2: 1,
                  colorStops: [{
                    offset: 0, color: 'rgba(103, 194, 58, 0.3)'
                  }, {
                    offset: 1, color: 'rgba(103, 194, 58, 0.1)'
                  }]
                }
              }
            },
            {
              name: '支出',
              type: 'line',
              data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
              smooth: true,
              lineStyle: {
                color: '#f56c6c'
              },
              itemStyle: {
                color: '#f56c6c'
              },
              areaStyle: {
                color: {
                  type: 'linear',
                  x: 0,
                  y: 0,
                  x2: 0,
                  y2: 1,
                  colorStops: [{
                    offset: 0, color: 'rgba(245, 108, 108, 0.3)'
                  }, {
                    offset: 1, color: 'rgba(245, 108, 108, 0.1)'
                  }]
                }
              }
            }
          ]
        }
        
        this.chart.setOption(option)
        
        // 监听窗口大小变化，自适应图表大小
        window.addEventListener('resize', () => {
          if (this.chart) {
            this.chart.resize()
          }
        })
      })
    },
    
    // 更新图表数据
    updateChart() {
      if (!this.chart) return
      
      const option = this.chart.getOption()
      option.series[0].data = this.annualSummary.monthlyIncome || [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
      option.series[1].data = this.annualSummary.monthlyExpense || [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
      
      this.chart.setOption(option)
    },
    
    // 获取月度汇总
    async getMonthlySummary() {
      try {
        const userId = JSON.parse(localStorage.getItem('user')).id
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
        this.$message.error('获取月度汇总失败')
      }
    },
    
    // 获取年度汇总
    async getAnnualSummary() {
      try {
        const userId = JSON.parse(localStorage.getItem('user')).id
        const now = new Date()
        const res = await request.get('/record/annual-summary', {
          params: { 
            userId,
            year: now.getFullYear()
          }
        })
        this.annualSummary = res.data
        this.updateChart()
      } catch (error) {
        this.$message.error('获取年度汇总失败')
      }
    },
    
    // 获取AI财务分析
    async getAIAnalysis() {
      console.log('开始获取AI分析数据')
      try {
        const user = localStorage.getItem('user')
        console.log('用户信息:', user)
        const userId = JSON.parse(user).id
        console.log('userId:', userId)
        const res = await request.get('/record/ai-analysis', {
          params: { userId }
        })
        console.log('AI分析响应:', res)
        this.aiAnalysis = res.data
        console.log('AI分析数据:', this.aiAnalysis)
      } catch (error) {
        console.error('AI分析请求失败:', error)
        this.$message.error('获取AI分析失败')
      }
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

/* 统计概览 */
.stats-overview {
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
  padding: 20px;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.stat-content {
  text-align: left;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.stat-value.income {
  color: #67c23a;
}

.stat-value.expense {
  color: #f56c6c;
}

.stat-value.balance {
  color: #409eff;
}

/* 图表区域 */
.chart-section {
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 8px;
  border: none;
  transition: all 0.3s ease;
  padding: 20px;
}

.chart-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

/* AI分析模块样式 */
.ai-analysis-section {
  margin-bottom: 24px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.ai-content {
  margin-top: 16px;
}

.ai-content h4 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 16px 0 8px 0;
}

.ai-content p {
  font-size: 14px;
  color: #606266;
  margin: 8px 0;
}

.ai-content ul {
  list-style-type: disc;
  padding-left: 20px;
}

.ai-content li {
  font-size: 14px;
  color: #606266;
  margin: 8px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .header-right {
    width: 100%;
  }
  
  .ai-stats {
    grid-template-columns: 1fr;
  }
}
</style>
