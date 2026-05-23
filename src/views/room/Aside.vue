<script setup>
import {computed, ref, onMounted} from "vue";
import { Cpu, HomeFilled, Reading, Document, Collection, SetUp, Message, QuestionFilled, User, Grid, DataAnalysis, Files } from '@element-plus/icons-vue'
import {useRoute} from "vue-router";

const currentRoute = useRoute()
const userRole = ref('student')
const userName = ref('')
const userRealName = ref('')

// 当前激活菜单（根据路由自动高亮）
const activeMenu = computed(() => currentRoute.path)

// 从localStorage获取用户角色和用户名
onMounted(() => {
  userRole.value = localStorage.getItem('userRole') || 'student'
  userName.value = localStorage.getItem('userName') || '用户'
  userRealName.value = localStorage.getItem('userRealName') || userName.value
})

// 教师菜单项配置
const teacherMenus = [
  { path: '/home/teacher/courses', icon: HomeFilled, title: '课程管理' },
  { path: '/home/teacher/teaching', icon: Reading, title: '智能备课' },
  { path: '/home/teacher/assessment', icon: Document, title: '考核内容' },
  { path: '/home/teacher/learning', icon: Collection, title: '学习资源' },
  { path: '/home/ai', icon: Cpu, title: 'AI助手' },
  { path: '/home/teacher/analysis', icon: SetUp, title: '数据分析' }
]

// 学生菜单项配置
const studentMenus = [
  { path: '/home/student/courses', icon: HomeFilled, title: '我的课程' },
  { path: '/home/student/learning', icon: Collection, title: '学习助手' },
  { path: '/home/ai', icon: Cpu, title: 'AI助手' },
  { path: '/home/student/questions', icon: QuestionFilled, title: '在线提问' },
  { path: '/home/student/progress', icon: Message, title: '学习进度' }
]

// 管理员菜单项配置
const adminMenus = [
  { path: '/home/admin/dashboard', icon: Grid, title: '系统概览' },
  { path: '/home/admin/users', icon: User, title: '用户管理' },
  { path: '/home/admin/resources', icon: Files, title: '资源管理' },
  { path: '/home/admin/statistics', icon: DataAnalysis, title: '数据统计' },
  { path: '/home/ai', icon: Cpu, title: 'AI助手' }
]

// 根据角色显示菜单
const menus = computed(() => {
  if (userRole.value === 'admin') {
    return adminMenus
  } else if (userRole.value === 'teacher') {
    return teacherMenus
  } else {
    return studentMenus
  }
})

// 获取角色显示文本
const roleText = computed(() => {
  if (userRole.value === 'admin') return '管理员'
  if (userRole.value === 'teacher') return '教师'
  return '学生'
})
</script>

<template>
  <div class="aside-container">
    <div class="user-info">
      <span class="username">{{ userRealName }} ({{ roleText }})</span>
    </div>
    
    <el-menu
        :default-active="activeMenu"
        active-text-color="#409EFF"
        background-color="#ffffff"
        class="nav-menu"
        text-color="#303133"
        router
    >
      <el-menu-item
          v-for="item in menus"
          :key="item.path"
          :index="item.path"
      >
        <el-icon class="svj" :size="25"><component :is="item.icon" /></el-icon>
        <span>{{ item.title }}</span>
      </el-menu-item>
    </el-menu>
  
  </div>

</template>

<style scoped>
.aside-container {
  display: block;
  height: calc(100vh - 150px);
  width: 255px;
  border-radius: 12px;
  margin-top: 10px;
  margin-left: 10px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  background: #fff;
}
span {
  font-size: 17px;
  margin-left: 10px;
}

.user-info {
  padding: 20px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #fff;
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
  background-color:#409eff ;
}

.username {
  margin-left: 12px;
  font-weight: 600;
  color: #fff;;
  font-size: 20px;
}

.nav-menu {
  border-right: none;
  padding-top: 10px;
}
.svj {
  margin-left: 5px;
}
</style>