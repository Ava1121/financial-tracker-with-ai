<template>
  <div class="app-container">
    <el-container class="clean-layout">
      <el-aside width="200px" class="aside-menu">
        <div class="logo-container">
          <h1 class="logo-text">个人收支管理系统</h1>
        </div>
        <el-menu 
          :default-active="activeMenu" 
          class="vertical-menu" 
          router
          background-color="#ffffff"
          text-color="#606266"
          active-text-color="#409EFF"
          border-right="none"
        >
          <el-menu-item index="/">
            <el-icon><House /></el-icon>
            <template #title>
              <span>首页</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/records">
            <el-icon><List /></el-icon>
            <template #title>
              <span>收支记录</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/statistics">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>
              <span>数据统计</span>
            </template>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="top-header">
          <div class="user-info">
            <el-avatar :size="40" :src="userAvatar" />
            <div class="user-details">
              <span class="nickname">{{ user.nickname || user.username }}</span>
              <span class="greeting">{{ greeting }}</span>
            </div>
            <el-button type="text" @click="logout" class="logout-btn">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-button>
          </div>
        </el-header>
        <el-main class="main-content">
          <slot></slot>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { 
  House, List, DataAnalysis, SwitchButton 
} from '@element-plus/icons-vue'

export default {
  components: {
    House,
    List,
    DataAnalysis,
    SwitchButton
  },
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user')) || {}
    }
  },
  computed: {
    greeting() {
      const hour = new Date().getHours()
      if (hour < 6) return '凌晨好'
      if (hour < 12) return '早上好'
      if (hour < 14) return '中午好'
      if (hour < 18) return '下午好'
      return '晚上好'
    },
    userAvatar() {
      // 生成随机头像
      const name = this.user.nickname || this.user.username || '用户'
      const firstChar = name.charAt(0).toUpperCase()
      return `https://ui-avatars.com/api/?name=${encodeURIComponent(firstChar)}&background=409EFF&color=fff&size=128`
    },
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    logout() {
      localStorage.removeItem('user')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.app-container {
  height: 100vh;
  background-color: #f5f7fa;
  overflow: hidden;
}

.clean-layout {
  height: 100%;
}

/* 左侧导航栏 */
.aside-menu {
  background-color: white;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  width: 200px !important;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Logo 区域 */
.logo-container {
  background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo-text {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  letter-spacing: 1px;
}

/* 垂直菜单 */
.vertical-menu {
  border-right: none;
  flex: 1;
  overflow: auto;
}

.vertical-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
  margin: 8px 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.vertical-menu .el-menu-item:hover {
  background-color: #ecf5ff;
  color: #409EFF;
}

.vertical-menu .el-menu-item.is-active {
  background-color: #ecf5ff;
  color: #409EFF;
}

.vertical-menu .el-menu-item .el-icon {
  margin-right: 8px;
  font-size: 16px;
}

/* 顶部导航栏 */
.top-header {
  background: white;
  color: #303133;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  height: 60px;
  border-bottom: 1px solid #ebeef5;
}

/* 用户信息区域 */
.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nickname {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.greeting {
  font-size: 12px;
  color: #909399;
}

/* 退出登录按钮 */
.logout-btn {
  color: #606266;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 6px 16px;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background-color: #f56c6c;
  border-color: #f56c6c;
  color: white;
}

/* 主内容区域 */
.main-content {
  background-color: #f5f7fa;
  padding: 24px;
  overflow: auto;
  height: calc(100vh - 60px);
}

/* 确保主内容区域布局正确 */
.el-container.is-vertical {
  height: 100%;
}

.el-container.is-vertical > .el-main {
  height: calc(100vh - 60px);
}
</style>