<template>
  <div class="login-container">
    <el-form :model="loginForm" label-width="80px" class="login-form">
      <h2 class="login-title">个人收支管理系统</h2>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="login" class="login-btn">登录</el-button>
        <el-button @click="register">注册</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      }
    }
  },
  methods: {
    async login() {
      try {
        const res = await request.post('/user/login', this.loginForm)
        if (res.code === 200) {
          this.$message.success('登录成功')
          // 保存用户信息到本地存储
          localStorage.setItem('user', JSON.stringify(res.data))
          this.$router.push('/home')
        } else {
          this.$message.error(res.message)
        }
      } catch (error) {
        this.$message.error('登录失败，请检查网络连接')
      }
    },
    register() {
      this.$router.push('/register')
    }
  }
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-form {
  width: 400px;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}

.login-btn {
  width: 100%;
}
</style>
