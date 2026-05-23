<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ArrowLeft, Lock, Upload, User } from '@element-plus/icons-vue';
import router from '@/router';

// 用户信息
const userInfo = reactive({
  name: '',
  realName: '',
  role: '',
  avatar: ''
});

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 头像上传相关
const avatarUrl = ref('');
const defaultAvatar = ''; // 使用空字符串，让Element Plus使用默认头像
const avatarUploadRef = ref(null);

// 页面加载时获取用户信息
onMounted(() => {
  // 从localStorage获取用户信息
  userInfo.name = localStorage.getItem('userName') || '';
  userInfo.realName = localStorage.getItem('userRealName') || userInfo.name;
  userInfo.role = localStorage.getItem('userRole') || '';
  
  // 获取头像
  const storedAvatar = localStorage.getItem('userAvatar');
  userInfo.avatar = storedAvatar || defaultAvatar;
  avatarUrl.value = userInfo.avatar;
  
  // 如果用户未登录，跳转到登录页
  if (!userInfo.name) {
    router.push('/login');
  }
});

// 获取角色显示名称
const getRoleName = (role) => {
  switch (role) {
    case 'admin':
      return '管理员';
    case 'teacher':
      return '教师';
    case 'student':
      return '学生';
    default:
      return '未知';
  }
};

// 修改密码
const changePassword = () => {
  // 表单验证
  if (!passwordForm.oldPassword) {
    ElMessage.warning('请输入原密码');
    return;
  }
  
  if (!passwordForm.newPassword) {
    ElMessage.warning('请输入新密码');
    return;
  }
  
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致');
    return;
  }
  
  if (passwordForm.newPassword.length < 6) {
    ElMessage.warning('新密码长度不能少于6位');
    return;
  }
  
  // 验证原密码是否正确
  const isAdmin = userInfo.name === 'admin' && userInfo.role === 'admin';
  const isDefaultTeacher = userInfo.name === 'teacher' && userInfo.role === 'teacher';
  
  // 处理系统默认账户
  if (isAdmin) {
    if (passwordForm.oldPassword !== 'admin') {
      ElMessage.error('原密码不正确');
      return;
    }
    
    // 更新管理员密码（实际项目中应该调用API）
    localStorage.setItem('adminPassword', passwordForm.newPassword);
    ElMessage.success('密码修改成功');
    resetPasswordForm();
    return;
  }
  
  if (isDefaultTeacher) {
    if (passwordForm.oldPassword !== 'teacher') {
      ElMessage.error('原密码不正确');
      return;
    }
    
    // 更新教师密码（实际项目中应该调用API）
    localStorage.setItem('teacherPassword', passwordForm.newPassword);
    ElMessage.success('密码修改成功');
    resetPasswordForm();
    return;
  }
  
  // 处理注册用户
  const users = JSON.parse(localStorage.getItem('users') || '[]');
  const userIndex = users.findIndex(u => u.name === userInfo.name);
  
  if (userIndex !== -1) {
    if (users[userIndex].password !== passwordForm.oldPassword) {
      ElMessage.error('原密码不正确');
      return;
    }
    
    // 更新密码
    users[userIndex].password = passwordForm.newPassword;
    localStorage.setItem('users', JSON.stringify(users));
    
    ElMessage.success('密码修改成功');
    resetPasswordForm();
  } else {
    ElMessage.error('用户不存在');
  }
};

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
};

// 上传头像前的处理
const beforeAvatarUpload = (file) => {
  // 检查文件类型
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;
  
  if (!isImage) {
    ElMessage.error('头像必须是图片格式!');
    return false;
  }
  
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!');
    return false;
  }
  
  // 使用FileReader读取图片并转为base64
  const reader = new FileReader();
  reader.readAsDataURL(file);
  reader.onload = (e) => {
    avatarUrl.value = e.target.result;
    saveAvatar(e.target.result);
  };
  
  // 阻止自动上传
  return false;
};

// 保存头像
const saveAvatar = (avatarData) => {
  // 保存到localStorage
  localStorage.setItem('userAvatar', avatarData);
  userInfo.avatar = avatarData;
  
  // 如果是注册用户，更新用户数据
  if (userInfo.role !== 'admin' && !(userInfo.role === 'teacher' && userInfo.name === 'teacher')) {
    const users = JSON.parse(localStorage.getItem('users') || '[]');
    const userIndex = users.findIndex(u => u.name === userInfo.name);
    
    if (userIndex !== -1) {
      users[userIndex].avatar = avatarData;
      localStorage.setItem('users', JSON.stringify(users));
    }
  }
  
  // 更新头像URL
  avatarUrl.value = avatarData;
  
  // 触发一个自定义事件，通知其他组件头像已更新
  const event = new Event('avatarUpdated');
  window.dispatchEvent(event);
  
  ElMessage.success('头像更新成功');
};

// 返回上一页
const goBack = () => {
  router.go(-1);
};
</script>

<template>
  <div class="account-settings">
    <div class="page-header">
      <el-button :icon="ArrowLeft" @click="goBack">返回</el-button>
      <h2>账号管理</h2>
    </div>
    
    <el-card class="account-card">
      <div class="user-info">
        <div class="avatar-container">
          <el-avatar :size="100" :src="avatarUrl" />
          <div class="upload-btn">
            <el-upload
              ref="avatarUploadRef"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="beforeAvatarUpload"
              action="#"
            >
              <el-button type="primary" size="small">更换头像</el-button>
            </el-upload>
          </div>
        </div>
        
        <div class="info-details">
          <div class="info-item">
            <span class="label">用户名:</span>
            <span class="value">{{ userInfo.name }}</span>
          </div>
          <div class="info-item">
            <span class="label">真实姓名:</span>
            <span class="value">{{ userInfo.realName }}</span>
          </div>
          <div class="info-item">
            <span class="label">角色:</span>
            <el-tag :type="userInfo.role === 'admin' ? 'danger' : userInfo.role === 'teacher' ? 'success' : 'info'">
              {{ getRoleName(userInfo.role) }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-card>
    
    <el-card class="password-card">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>
      
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="原密码" required>
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            placeholder="请输入原密码" 
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>
        
        <el-form-item label="新密码" required>
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            placeholder="请输入新密码" 
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>
        
        <el-form-item label="确认新密码" required>
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码" 
            show-password
            :prefix-icon="Lock"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="changePassword">修改密码</el-button>
          <el-button @click="resetPasswordForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.account-settings {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin-left: 15px;
  margin-bottom: 0;
}

.account-card,
.password-card {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: flex-start;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 40px;
}

.upload-btn {
  margin-top: 15px;
}

.info-details {
  flex: 1;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.label {
  font-weight: bold;
  width: 100px;
}

.value {
  color: #606266;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}
</style> 