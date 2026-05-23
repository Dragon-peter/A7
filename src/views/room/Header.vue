<script setup>
import { Setting } from "@element-plus/icons-vue";
import router from "@/router/index.js";
import { ref, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';

const userName = ref('');
const userRole = ref('');
const userRealName = ref('');
const userAvatar = ref(''); // 用户头像
const defaultAvatar = '/default-avatar.png'; // 默认头像路径

// 获取用户信息
onMounted(() => {
  userName.value = localStorage.getItem('userName') || '用户';
  userRole.value = localStorage.getItem('userRole') || 'student';
  userRealName.value = localStorage.getItem('userRealName') || userName.value;
  userAvatar.value = localStorage.getItem('userAvatar') || defaultAvatar; // 获取头像或使用默认头像
  
  // 监听storage事件，当其他页面修改了localStorage时更新头像
  window.addEventListener('storage', handleStorageChange);
  
  // 监听自定义的头像更新事件
  window.addEventListener('avatarUpdated', updateAvatar);
});

// 更新头像
const updateAvatar = () => {
  userAvatar.value = localStorage.getItem('userAvatar') || defaultAvatar;
};

// 处理localStorage变化
const handleStorageChange = (e) => {
  if (e.key === 'userAvatar') {
    userAvatar.value = e.newValue || defaultAvatar;
  } else if (e.key === 'userRealName') {
    userRealName.value = e.newValue || userName.value;
  }
};

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('storage', handleStorageChange);
  window.removeEventListener('avatarUpdated', updateAvatar);
});

const logout = () => {
  // 清除本地存储的用户信息
  localStorage.removeItem('userName');
  localStorage.removeItem('userRole');
  localStorage.removeItem('userRealName');
  localStorage.removeItem('userAvatar');
  
  ElMessage.success('退出登录成功');
  router.push("/");
};

const change = () => {
  router.push('/account-settings');
};
</script>

<template>
  <div class="toolbar" >
    <div class="title">
      智慧教学系统
    </div>
    <!-- 将 el-dropdown 包裹在图标外层 -->
    <div class="user-area">
      <el-dropdown trigger="click">
        <div class="user-profile">
          <el-avatar :size="36" :src="userAvatar" />
          <span class="user-name">{{ userRealName }}</span>
        </div>
        <!-- 下拉菜单 -->
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="change">账号管理</el-dropdown-item>
            <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style scoped>
.toolbar {
  width: 100%;
  margin: 0;
  padding: 0 20px;
  background-color: #409eff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 8px;
  color: #fff;
  
}

/* 原有flex布局保留 */
.title{
  font-weight: bold;
  font-size: 25px;
}
.user-area {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
}

.user-profile {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-name {
  margin-left: 8px;
  font-size: 16px;
}
</style>