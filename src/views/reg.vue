<script setup>
import {ref, onMounted} from "vue";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";
import {User, Lock} from "@element-plus/icons-vue";

const form= ref({
  name: '',
  realName: '',
  password: '',
  confirmPassword:'',
  role: 'student' // 默认为学生角色
})

// 页面加载时确保清除登录状态
onMounted(() => {
  // 确保清除登录状态
  localStorage.removeItem('userName');
  localStorage.removeItem('userRole');
  localStorage.removeItem('userRealName');
})

const register = () => {
  // 简单表单验证
  if (!form.value.name || !form.value.password || !form.value.confirmPassword || !form.value.realName) {
    ElMessage.warning('请填写所有必填项');
    return;
  }
  
  if (form.value.password !== form.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致');
    return;
  }
  
  if (form.value.password.length < 6) {
    ElMessage.warning('密码长度不能少于6位');
    return;
  }
  
  // 获取已有用户
  const users = JSON.parse(localStorage.getItem('users') || '[]');
  
  // 检查用户名是否已存在
  if (users.find(u => u.name === form.value.name)) {
    ElMessage.error('用户名已存在');
    return;
  }
  
  // 添加新用户
  users.push({
    name: form.value.name,
    realName: form.value.realName,
    password: form.value.password,
    role: form.value.role, // 使用选择的角色
    avatar: '' // 使用空字符串作为默认头像
  });
  
  // 保存到localStorage
  localStorage.setItem('users', JSON.stringify(users));
  
  ElMessage.success('注册成功，请登录');
  router.push('/login');
}

const goToLogin=()=>{
  // 清除任何可能存在的登录状态
  localStorage.removeItem('userName');
  localStorage.removeItem('userRole');
  localStorage.removeItem('userRealName');
  router.push("/login");
}
</script>

<template>
  <el-row style="min-height: 100vh;" >
    <el-col :span="16" style="display: flex;align-items: center;justify-content: center">
        <div class="img">
        </div>
    </el-col>
    <el-col :span="8" style="background-color: #f8f8f8;display: flex;align-items: center;justify-content: center;-webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  -ms-flex-direction: column;
  -webkit-flex-direction: column;
  flex-direction: column;" >
      <h1 style="color: #333; margin-bottom: 24px;">创建账号</h1>
      <div style="display: flex; align-items: center; width: 80%; margin-bottom: 32px;">
        <span style="flex: 1; height: 1px; background-color: #DCDFE6;"></span> <!-- 左侧灰线 -->
        <span style="padding: 0 12px; color: #999; font-size: 18px;">账号密码注册</span>
        <span style="flex: 1; height: 1px; background-color: #DCDFE6;"></span> <!-- 右侧灰线 -->
      </div>
      <el-form
          :model="form"
          style="width: 80%; max-width: 400px;"
      >
        <!-- 角色选择 -->
        <el-form-item>
          <el-radio-group v-model="form.role" style="width: 100%; display: flex; justify-content: space-around;">
            <el-radio-button label="student">学生</el-radio-button>
            <el-radio-button label="teacher">教师</el-radio-button>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item >
          <el-input
              v-model="form.name"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
          />
        </el-form-item>
        
        <el-form-item >
          <el-input
              v-model="form.realName"
              placeholder="请输入真实姓名"
              :prefix-icon="User"
              size="large"
          />
        </el-form-item>
        
        <el-form-item>
          <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
          />
        </el-form-item>
        <el-form-item >
          <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请确认密码"
              :prefix-icon="Lock"
              size="large"
              show-password
          />
        </el-form-item>
        <el-button
            type="primary"
            size="large"
            style="width: 100%; margin-top: 8px;"
            @click="register"
        >
          立即注册
        </el-button>
        <div style="text-align: center; margin-top: 24px; color: #666;">
          已有账号？
          <el-link type="primary" @click="goToLogin">去登录</el-link>
        </div>
      </el-form>
    </el-col>
  </el-row>
</template>

<style scoped>
.img{
background-image: url('../assets/laptop-2620118_1280.jpg');
width: 100%;
height: 100%;
}
</style>