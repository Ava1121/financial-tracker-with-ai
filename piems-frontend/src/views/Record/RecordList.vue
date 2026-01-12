<template>
  <Layout>
    <!-- 页面标题和操作区 -->
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <el-icon><List /></el-icon>
          收支记录
        </h2>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item><a href="/">首页</a></el-breadcrumb-item>
          <el-breadcrumb-item>收支记录</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="header-right">
        <el-button 
          type="primary" 
          @click="openAddDialog" 
          class="add-btn"
          size="large"
          icon="Plus"
        >
          添加记录
        </el-button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview">
      <h3 class="section-title">统计概览</h3>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="stat-card income-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-label">总记录数</div>
              <div class="stat-value">{{ records.length }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-label">本月收入</div>
              <div class="stat-value income">{{ totalIncome }} 元</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-label">本月支出</div>
              <div class="stat-value expense">{{ totalExpense }} 元</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="hover">
      <el-form :inline="true" class="filter-form" label-position="top">
        <el-form-item label="收支类型">
          <el-select 
            v-model="filterForm.type" 
            placeholder="全部" 
            clearable
            class="w-100"
          >
            <el-option label="收入" value="1"></el-option>
            <el-option label="支出" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            class="w-100"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="操作">
          <div class="filter-actions">
            <el-button type="primary" @click="getRecords">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="resetFilter">
              <el-icon><RefreshRight /></el-icon>
              重置
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 记录表格 -->
    <el-card class="table-card" shadow="hover">
      <el-table 
        :data="records" 
        style="width: 100%"
        stripe
        border
        :row-style="{ cursor: 'pointer' }"
        @row-click="handleRowClick"
      >
        <el-table-column 
          prop="recordTime" 
          label="记录时间" 
          width="180"
          align="center"
        >
          <template #default="scope">
            <span class="time-text">{{ formatDate(scope.row.recordTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag 
              :type="scope.row.type === 1 ? 'success' : 'danger'"
              size="medium"
              effect="light"
            >
              {{ scope.row.type === 1 ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="140" align="right">
          <template #default="scope">
            <span 
              :class="{
                'amount-text': true,
                'income-amount': scope.row.type === 1,
                'expense-amount': scope.row.type === 2
              }"
            >
              {{ scope.row.type === 1 ? '+' : '-' }}¥{{ scope.row.amount.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" align="center">
          <template #default="scope">
            <el-tag type="info" size="medium" effect="light">
              {{ scope.row.categoryName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200">
          <template #default="scope">
            <span class="remark-text" :title="scope.row.remark">
              {{ scope.row.remark || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column 
          label="操作" 
          width="160" 
          align="center"
          fixed="right"
        >
          <template #default="scope">
            <div class="operation-buttons">
              <el-button 
                type="primary" 
                size="small" 
                @click="editRecord(scope.row)"
                icon="Edit"
              >
                编辑
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="deleteRecord(scope.row.id)"
                icon="Delete"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-if="total > 0"
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          v-model:current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
        <div v-else class="empty-state">
          <el-empty description="暂无收支记录" :image-size="100"></el-empty>
        </div>
      </div>
    </el-card>
    
    <!-- 添加/编辑记录对话框 -->
    <AddRecord ref="addRecordRef" @success="onAddSuccess"></AddRecord>
    
    <!-- AI交互区 -->
    <div class="ai-chat-container">
      <div class="ai-chat-box" v-show="aiChatVisible">
        <div class="ai-chat-header">
          <h3>AI理财助手</h3>
          <button class="ai-close-btn" @click="closeAIChat">×</button>
        </div>
        <div class="ai-chat-messages">
          <!-- 欢迎消息 -->
          <div class="ai-message">
            <p>您好，我是您的AI理财助手，有什么可以帮您的？</p>
            <span class="ai-time">{{ new Date().toLocaleTimeString() }}</span>
          </div>
          <!-- 聊天消息列表 -->
          <div 
            v-for="(msg, index) in chatMessages" 
            :key="index" 
            :class="msg.type === 'user' ? 'user-message' : 'ai-message'"
          >
            <p>{{ msg.text }}</p>
            <span class="ai-time">{{ msg.time }}</span>
          </div>
        </div>
        <div class="ai-chat-input">
          <el-input
            v-model="aiInput"
            placeholder="请输入您的问题..."
            @keyup.enter="sendAIMessage"
          ></el-input>
          <el-button type="primary" @click="sendAIMessage">发送</el-button>
        </div>
      </div>
      <button class="ai-toggle-btn" @click="toggleAIChat">
        AI助手
      </button>
    </div>
  </Layout>
</template>

<script>
import request from '@/utils/request'
import AddRecord from './AddRecord.vue'
import Layout from '@/components/Layout.vue'
import { List, Plus, Edit, Delete, Search, RefreshRight } from '@element-plus/icons-vue'

export default {
  components: {
    Layout,
    AddRecord,
    List,
    Plus,
    Edit,
    Delete,
    Search,
    RefreshRight
  },
  data() {
    return {
      records: [],
      total: 0,
      currentPage: 1,
      pageSize: 20,
      filterForm: {
        type: '',
        startTime: '',
        endTime: ''
      },
      dateRange: [],
      totalIncome: 0,
      totalExpense: 0,
      loading: false,
      // AI聊天相关
      aiInput: '',
      chatMessages: [], // 统一存储所有聊天消息
      aiChatVisible: true
    }
  },
  computed: {
    userAvatar() {
      const user = JSON.parse(localStorage.getItem('user')) || {}
      const name = user.nickname || user.username || '用户'
      const firstChar = name.charAt(0).toUpperCase()
      return `https://ui-avatars.com/api/?name=${encodeURIComponent(firstChar)}&background=409EFF&color=fff&size=128`
    }
  },
  mounted() {
    this.getRecords()
  },
  methods: {
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    
    // 获取记录列表
    async getRecords() {
      try {
        // 显示加载提示
        this.loading = true
        
        // 处理用户信息
        const userInfo = localStorage.getItem('user')
        if (!userInfo) {
          this.loading = false
          this.$router.push('/login')
          return
        }
        
        const user = JSON.parse(userInfo)
        const userId = user.id
        
        // 发送请求获取记录
        const params = {
          userId
        }
        
        // 只添加有值的参数，注意将type转换为数字类型
        console.log('当前filterForm.type:', this.filterForm.type)
        if (this.filterForm.type) {
          params.type = Number(this.filterForm.type)
        }
        
        console.log('发送请求到 /record/list，参数:', params)
        const res = await request.get('/record/list', {
          params
        })
        
        console.log('完整响应数据:', res)
        
        // 更新数据，根据响应拦截器，res已经是Result对象，记录列表在res.data中
        this.records = res.data || []
        this.total = this.records.length || 0
        
        console.log('更新后的记录数:', this.records.length)
        console.log('更新后的记录:', this.records)
        
        // 计算统计数据
        this.calculateStats()
        
        // 关闭加载提示
        this.loading = false
      } catch (error) {
        this.loading = false
        console.error('获取记录失败:', error)
        let errorMsg = '获取记录失败，请稍后重试'
        if (error.response) {
          // 服务器返回了错误响应
          errorMsg = `获取记录失败: ${error.response.status} ${error.response.statusText}`
          if (error.response.data && error.response.data.msg) {
            errorMsg += ` - ${error.response.data.msg}`
          }
        } else if (error.request) {
          // 请求发送成功但没有收到响应
          errorMsg = '获取记录失败: 服务器无响应，请检查网络连接'
        } else {
          // 请求配置错误
          errorMsg = `获取记录失败: ${error.message}`
        }
        this.$message.error(errorMsg)
      }
    },
    
    // 计算统计数据
    calculateStats() {
      const now = new Date()
      const currentMonth = now.getMonth() + 1
      const currentYear = now.getFullYear()
      
      // 计算当前月份的记录
      const thisMonthRecords = this.records.filter(record => {
        const recordDate = new Date(record.recordTime)
        return recordDate.getMonth() + 1 === currentMonth && recordDate.getFullYear() === currentYear
      })
      
      // 计算总收入
      this.totalIncome = thisMonthRecords
        .filter(r => r.type === 1)
        .reduce((sum, r) => sum + r.amount, 0)
        .toFixed(2)
      
      // 计算总支出
      this.totalExpense = thisMonthRecords
        .filter(r => r.type === 2)
        .reduce((sum, r) => sum + r.amount, 0)
        .toFixed(2)
    },
    
    // 打开添加对话框
    openAddDialog() {
      this.$refs.addRecordRef.open()
    },
    
    // 添加成功回调
    onAddSuccess() {
      this.$message.success('添加记录成功')
      this.getRecords()
    },
    
    // 编辑记录
    editRecord(record) {
      this.$refs.addRecordRef.openEdit(record)
    },
    
    // 删除记录
    async deleteRecord(id) {
      try {
        await this.$confirm('确定要删除这条记录吗？', '删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const userId = JSON.parse(localStorage.getItem('user')).id
        await request.delete(`/record/${id}`, {
          params: { userId }
        })
        
        this.$message.success('删除成功')
        this.getRecords()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    
    // 行点击事件
    handleRowClick(row) {
      this.$message.info('点击了记录行：' + row.id)
    },
    
    // 重置筛选条件
    resetFilter() {
      this.filterForm = {
        type: '',
        startTime: '',
        endTime: ''
      }
      this.dateRange = []
      this.getRecords()
    },
    
    // 分页大小变化
    handleSizeChange(size) {
      this.pageSize = size
      this.currentPage = 1
      this.getRecords()
    },
    
    // 当前页变化
    handleCurrentChange(current) {
      this.currentPage = current
      this.getRecords()
    },
    
    // AI聊天相关方法
    toggleAIChat() {
      this.aiChatVisible = !this.aiChatVisible
    },
    
    closeAIChat() {
      this.aiChatVisible = false
    },
    
    async sendAIMessage() {
      if (!this.aiInput.trim()) return
      
      // 添加用户消息到聊天记录
      const userMsg = {
        type: 'user',
        text: this.aiInput.trim(),
        time: new Date().toLocaleTimeString()
      }
      this.chatMessages.push(userMsg)
      
      const userInput = this.aiInput.trim()
      this.aiInput = ''
      
      try {
        // 根据用户输入内容生成不同的响应
        const greetingPatterns = /^(你好|您好|hi|hello|嗨|早上好|下午好|晚上好)$/i
        const financeKeywords = ['理财', '储蓄', '支出', '收入', '消费', '建议', '分析', '财务', '状况', '情况', '报告', '基金', '投资', '股票', '债券', '资产', '配置']
        
        if (greetingPatterns.test(userInput)) {
          // 问候语回应
          const greetings = [
            '您好！很高兴为您服务，有什么可以帮助您的？',
            '你好！欢迎使用AI理财助手，请问有什么可以帮您的？',
            '嗨！我是您的AI理财助手，有什么可以为您解答的？'
          ]
          const randomGreeting = greetings[Math.floor(Math.random() * greetings.length)]
          const aiMsg = {
            type: 'ai',
            text: randomGreeting,
            time: new Date().toLocaleTimeString()
          }
          this.chatMessages.push(aiMsg)
        } else {
          // 检查是否包含财务相关关键词
          const containsFinanceKeyword = financeKeywords.some(keyword => userInput.includes(keyword))
          
          if (containsFinanceKeyword) {
            // 财务相关问题，调用AI分析API
            const user = JSON.parse(localStorage.getItem('user'))
            if (!user) {
              throw new Error('用户未登录')
            }
            const res = await request.get('/record/ai-analysis', {
              params: { 
                userId: user.id,
                question: userInput
              }
            })
            
            // 添加AI回复到聊天记录
            const aiMsg = {
              type: 'ai',
              text: res.data.response,
              time: new Date().toLocaleTimeString()
            }
            this.chatMessages.push(aiMsg)
          } else {
            // 非财务问题，仍调用AI分析API，让AI自己判断
            const user = JSON.parse(localStorage.getItem('user'))
            if (!user) {
              throw new Error('用户未登录')
            }
            const res = await request.get('/record/ai-analysis', {
              params: { 
                userId: user.id,
                question: userInput
              }
            })
            
            // 添加AI回复到聊天记录
            const aiMsg = {
              type: 'ai',
              text: res.data.response,
              time: new Date().toLocaleTimeString()
            }
            this.chatMessages.push(aiMsg)
          }
        }
      } catch (error) {
        console.error('获取AI分析失败:', error)
        const errorMsg = {
          type: 'ai',
          text: '抱歉，我暂时无法为您提供分析，请稍后再试。',
          time: new Date().toLocaleTimeString()
        }
        this.chatMessages.push(errorMsg)
      }
    },
    
    // 格式化时间
    formatTime(date) {
      return date.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },
    
    // 发送AI消息
    async sendAiMessage() {
      if (!this.aiInputMessage.trim()) return
      
      // 添加用户消息
      const userMsg = {
        text: this.aiInputMessage.trim(),
        time: this.formatTime(new Date())
      }
      this.chatMessages.push(userMsg)
      this.aiInputMessage = ''
      
      // 模拟AI思考过程
      this.isAiTyping = true
      
      try {
        // 调用AI分析API
        const userId = JSON.parse(localStorage.getItem('user')).id
        const res = await request.get('/record/ai-analysis', {
          params: { userId }
        })
        
        // 模拟AI回复
        setTimeout(() => {
          const aiMsg = {
            text: res.data.suggestions.join('\n'),
            time: this.formatTime(new Date())
          }
          this.aiMessages.push(aiMsg)
          this.isAiTyping = false
          // 滚动到底部
          this.$nextTick(() => {
            const chatContainer = document.querySelector('.chat-messages')
            if (chatContainer) {
              chatContainer.scrollTop = chatContainer.scrollHeight
            }
          })
        }, 1000)
      } catch (error) {
        console.error('获取AI分析失败:', error)
        const aiMsg = {
          text: '很抱歉，我暂时无法为您提供分析，请稍后再试。',
          time: this.formatTime(new Date())
        }
        this.aiMessages.push(aiMsg)
        this.isAiTyping = false
      }
    },
    
    // 切换AI面板折叠状态
    toggleAiPanel() {
      this.isAiCollapsed = !this.isAiCollapsed
    },
    
    // 清空聊天历史
    clearChatHistory() {
      this.chatMessages = []
      this.aiMessages = []
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
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.stat-content {
  text-align: center;
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

/* 筛选卡片 */
.filter-card {
  margin-bottom: 24px;
  border-radius: 8px;
  border: none;
}

.filter-form {
  width: 100%;
}

.filter-actions {
  display: flex;
  gap: 12px;
}

/* 表格卡片 */
.table-card {
  border-radius: 8px;
  border: none;
}

/* 表格样式 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper) {
  border-radius: 8px 8px 0 0;
  overflow: hidden;
}

:deep(.el-table__header tr th) {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  color: #303133;
  font-weight: 600;
  padding: 12px 0;
}

:deep(.el-table__body tr:hover td) {
  background-color: #f0f9ff !important;
}

/* 金额样式 */
.amount-text {
  font-size: 16px;
  font-weight: 600;
}

.income-amount {
  color: #67c23a;
}

.expense-amount {
  color: #f56c6c;
}

/* 时间文本 */
.time-text {
  color: #606266;
  font-size: 14px;
}

/* 备注文本 */
.remark-text {
  color: #606266;
  line-height: 1.5;
}

/* 操作按钮组 */
.operation-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

/* 分页容器 */
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background: #fafafa;
  border-top: 1px solid #ebeef5;
}

.empty-state {
  display: flex;
  justify-content: center;
  padding: 40px;
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
  
  .add-btn {
    width: 100%;
  }
  
  :deep(.el-table .el-table__fixed-right) {
    right: 0;
  }
}

/* AI聊天样式 */
.ai-chat-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
}

.ai-chat-box {
  width: 350px;
  max-height: 500px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: all 0.3s ease;
}

.ai-chat-header {
  background: #409eff;
  color: white;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.ai-chat-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.ai-close-btn {
  background: transparent;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-chat-messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ai-message {
  align-self: flex-start;
  background: #f0f9ff;
  color: #303133;
  padding: 10px 14px;
  border-radius: 16px 16px 16px 4px;
  max-width: 80%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.user-message {
  align-self: flex-end;
  background: #ecf5ff;
  color: #409eff;
  padding: 10px 14px;
  border-radius: 16px 16px 4px 16px;
  max-width: 80%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.ai-message p,
.user-message p {
  margin: 0 0 4px 0;
  line-height: 1.4;
}

.ai-time {
  font-size: 11px;
  color: #909399;
  display: block;
  text-align: right;
}

.ai-chat-input {
  padding: 12px 16px;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 8px;
  background: #fafafa;
}

.ai-chat-input .el-input {
  flex: 1;
}

.ai-toggle-btn {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #409eff;
  color: white;
  border: none;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: vertical-rl;
}

.ai-toggle-btn:hover {
  background: #66b1ff;
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.5);
}

/* 滚动条样式 */
.ai-chat-messages::-webkit-scrollbar {
  width: 6px;
}

.ai-chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.ai-chat-messages::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.ai-chat-messages::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
