<script setup>
import {User, Lock} from "@element-plus/icons-vue";
import {ref, onMounted} from "vue";
import router from "@/router/index.js";
import { ElMessage } from "element-plus";

const form= ref({
  name: '',
  password: ''
})

// 添加用户角色选择
const userRole = ref('student'); // 默认为学生角色
const roleOptions = [
  { value: 'student', label: '学生' },
  { value: 'teacher', label: '教师' },
  { value: 'admin', label: '管理员' }
]

// 页面加载时确保清除登录状态
onMounted(() => {
  // 确保清除登录状态，防止自动登录
  localStorage.removeItem('userName');
  localStorage.removeItem('userRole');
})

const goReg=()=>{
  router.push("/reg")
}

const login=()=>{
  // 检查是否为空
  if (!form.value.name || !form.value.password) {
    ElMessage.warning('请输入用户名和密码');
    return;
  }

  // 管理员账号判断
  if (form.value.name === 'admin' && userRole.value === 'admin') {
    // 检查是否有修改过的密码
    const adminPassword = localStorage.getItem('adminPassword') || 'admin';
    
    if (form.value.password === adminPassword) {
      localStorage.setItem('userRole', 'admin');
      localStorage.setItem('userName', 'admin');
      localStorage.setItem('userRealName', '系统管理员'); // 设置管理员真实姓名
      // 设置默认头像
      if (!localStorage.getItem('userAvatar')) {
        localStorage.setItem('userAvatar', '');
      }
      ElMessage.success('管理员登录成功');
      router.push("/home/admin/dashboard");
      return;
    }
  }

  // 默认教师账号判断
  if (form.value.name === 'teacher' && userRole.value === 'teacher') {
    // 检查是否有修改过的密码
    const teacherPassword = localStorage.getItem('teacherPassword') || 'teacher';
    
    if (form.value.password === teacherPassword) {
      localStorage.setItem('userRole', 'teacher');
      localStorage.setItem('userName', 'teacher');
      localStorage.setItem('userRealName', '张明'); // 设置教师真实姓名
      // 设置默认头像
      if (!localStorage.getItem('userAvatar')) {
        localStorage.setItem('userAvatar', '');
      }
      ElMessage.success('教师登录成功');
      router.push("/home/teacher/courses");
      return;
    }
  }
  
  // 检查是否是注册用户
  const users = JSON.parse(localStorage.getItem('users') || '[]');
  const user = users.find(u => u.name === form.value.name && u.password === form.value.password);
  
  if (user) {
    // 检查角色是否匹配
    if (user.role === userRole.value) {
      localStorage.setItem('userRole', user.role);
      localStorage.setItem('userName', form.value.name);
      localStorage.setItem('userRealName', user.realName || form.value.name); // 保存真实姓名，如果没有则使用用户名
      
      // 设置头像，如果用户有自定义头像则使用，否则使用默认头像
      localStorage.setItem('userAvatar', user.avatar || '');
      
      ElMessage.success(`${user.role === 'teacher' ? '教师' : '学生'}登录成功`);
      
      // 根据角色跳转到不同页面
      if (user.role === 'teacher') {
        router.push("/home/teacher/courses");
      } else {
        router.push("/home/student/courses");
      }
      return;
    }
  }
  
  ElMessage.error('用户名或密码错误，或选择的角色不匹配');
}
</script>

<template>
  <el-row style="min-height: 100vh;">
    <el-col :span="16" style="display: flex;align-items: center;justify-content: center">
      <div class="img">
      </div>
    </el-col>
    <el-col :span="8" style="background-color: #f8f8f8;display: flex;align-items: center;justify-content: center;-webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  -ms-flex-direction: column;
  -webkit-flex-direction: column;
  flex-direction: column;" >
      <h1 style="color: #333; margin-bottom: 24px;">欢迎回来</h1>
      <div style="display: flex; align-items: center; width: 80%; margin-bottom: 32px;">
        <span style="flex: 1; height: 1px; background-color: #DCDFE6;"></span> <!-- 左侧灰线 -->
        <span style="padding: 0 12px; color: #999; font-size: 18px;">账号密码登录</span>
        <span style="flex: 1; height: 1px; background-color: #DCDFE6;"></span> <!-- 右侧灰线 -->
      </div>
      <el-form
          :model="form"
          style="width: 80%; max-width: 400px;"
      >
        <!-- 角色选择 -->
        <el-form-item>
          <el-radio-group v-model="userRole" size="large" style="width: 100%; display: flex; justify-content: space-around;">
            <el-radio-button v-for="option in roleOptions" :key="option.value" :label="option.value">
              {{ option.label }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-input
              v-model="form.name"
              placeholder="请输入用户名"
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

        <el-button
            type="primary"
            size="large"
            style="width: 100%; margin-top: 8px;"
            @click="login"
        >
          登录
        </el-button>
        <div style="text-align: center; margin-top: 24px; color: #666;">
          未有账号？
          <el-link type="primary" @click="goReg" v-if="userRole !== 'admin'">注册</el-link>
          <span v-else>请联系系统管理员</span>
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