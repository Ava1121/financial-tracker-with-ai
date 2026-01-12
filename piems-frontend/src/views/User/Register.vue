<template>
  <div class="register-container">
    <el-form :model="registerForm" label-width="80px" class="register-form">
      <h2 class="register-title">用户注册</h2>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="registerForm.password" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="registerForm.nickname" placeholder="请输入昵称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="register" class="register-btn">注册</el-button>
        <el-button @click="login">返回登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  data() {
    return {
      registerForm: {
        username: '',
        password: '',
        nickname: ''
      }
    }
  },
  methods: {
    async register() {
      try {
        const res = await request.post('/user/register', this.registerForm)
        if (res.code === 200) {
          this.$message.success('注册成功')
          this.$router.push('/login')
        } else {
          this.$message.error(res.message)
        }
      } catch (error) {
        this.$message.error('注册失败，请检查网络连接')
      }
    },
    login() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.register-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.register-form {
  width: 400px;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}

.register-btn {
  width: 100%;
}
</style>
