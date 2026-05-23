<script setup>
import router from "@/router/index.js";

const login=()=>{
  router.push("/login")
}
const reg=()=>{
  router.push("/reg")
}

</script>

<template>
  <div class="img">
  </div>

  <!-- 雾层覆盖 -->
  <div class="fog-layer"></div>
  <!-- 系统名称 -->
  <div class="system-name">
    <h2 class="brand-name">教习π</h2>
    <h1>智慧教学系统</h1>
    <p>高效·智能·一体化</p>
  </div>
<div class="button">
  <el-button style="margin: 20px;width: 80px " size="large" round @click="login">登录</el-button>
  <el-button style="margin: 20px;width: 80px" size="large" round @click="reg">注册</el-button>
</div>
</template>

<style scoped>
.img {
  background-image: url('../assets/laptop-2620118_1280.jpg');
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  background-size: cover;
  background-position: center;
}
.button{
  position: absolute;
  z-index: 2;
  top: 68%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}
/* 雾层效果 */
.fog-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
      rgba(255, 255, 255, 0.2),
      rgba(245, 245, 245, 0.4),
      rgba(240, 240, 240, 0.6)
  );
  backdrop-filter: blur(1px);
  z-index: 1;
}

/* 系统名称 */
.system-name {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #333;
  z-index: 2;
  padding: 40px 50px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.brand-name {
  font-size: 4.5rem;
  font-weight: 900;
  font-family: 'Microsoft YaHei', 'PingFang SC', 'Hiragino Sans GB', sans-serif;
  margin-bottom: 20px;
  letter-spacing: 8px;
  position: relative;
  background: linear-gradient(45deg, #667eea 0%, #764ba2 25%, #f093fb 50%, #f5576c 75%, #4facfe 100%);
  background-size: 400% 400%;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradientShift 4s ease-in-out infinite;
  text-shadow: 0 0 30px rgba(102, 126, 234, 0.5);
  filter: drop-shadow(0 8px 16px rgba(0, 0, 0, 0.2));
  transform: perspective(500px) rotateX(15deg);
  transition: all 0.3s ease;
}

.brand-name:hover {
  transform: perspective(500px) rotateX(0deg) scale(1.05);
  filter: drop-shadow(0 12px 24px rgba(0, 0, 0, 0.3));
}

@keyframes gradientShift {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.system-name h1 {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 15px;
  letter-spacing: 2px;
  color: #2c3e50;
  opacity: 0.8;
  margin-top: 10px;
}

.system-name p {
  font-size: 1.1rem;
  letter-spacing: 2px;
  color: #333;
}
</style>