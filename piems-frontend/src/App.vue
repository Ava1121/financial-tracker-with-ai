<script setup>
// 应用主组件，使用路由视图
</script>

<template>
  <router-view></router-view>
</template>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: Helvetica Neue, Helvetica, PingFang SC, Hiragino Sans GB, Microsoft YaHei, Arial, sans-serif;
  background-color: #f5f7fa;
  color: #303133;
}

/* 路由过渡动画 */
.router-view {
  transition: all 0.3s ease;
}
</style>
