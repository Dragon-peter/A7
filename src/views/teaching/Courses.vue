<script setup>
import { ref, onMounted, computed } from 'vue'
import { Search, Plus, Document, Edit, Delete } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const activeTab = ref('learning')
const searchText = ref('')
const userRole = ref('student')
const userName = ref('')

// 从localStorage获取用户角色和用户名
onMounted(() => {
  userRole.value = localStorage.getItem('userRole') || 'student'
  userName.value = localStorage.getItem('userName') || '用户'
})

// 模拟教师课程数据
const teacherCourses = ref([
  {
    id: 1,
    title: '计算机网络（计科1-2）',
    cover: 'https://img2.baidu.com/it/u=1395980100,2999804584&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500',
    teacher: '张明',
    students: 45,
    progress: 60
  },
  {
    id: 2,
    title: '操作系统',
    cover: 'https://img1.baidu.com/it/u=1458656822,2078909008&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333',
    teacher: '梁伟',
    students: 38,
    progress: 45
  }
])

// 模拟学生课程数据
const studentCourses = ref([
  {
    id: 3,
    title: '计算机网络',
    cover: 'https://ts1.tc.mm.bing.net/th/id/R-C.27f4737dcab4424fb40f8018a1a56ff4?rik=SsbuS0G70NF2uQ&riu=http%3a%2f%2fwww.tup.tsinghua.edu.cn%2fupload%2fbigbookimg%2f083935-02.jpg&ehk=9dJY6%2fAVaGTocuo3qsVAy8EPmkmOJaN8vffrFNpRWrk%3d&risl=&pid=ImgRaw&r=0',
    teacher: '计算机网络教研组',
    progress: 75
  },
  {
    id: 4,
    title: '体育理论第四讲：奥林匹克运动（24-25大二第四学期）',
    cover: 'https://img0.baidu.com/it/u=1705694933,4002952892&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=281',
    teacher: '体育部教研组',
    progress: 30
  }
])

// 添加课程对话框
const addCourseDialogVisible = ref(false)
const newCourse = ref({
  title: '',
  description: '',
  cover: ''
})

// 添加文件夹对话框
const addFolderDialogVisible = ref(false)
const newFolder = ref({
  name: '',
  description: ''
})

// 根据角色和当前选项卡显示不同的课程
const displayedCourses = computed(() => {
  let courses = []
  
  if (userRole.value === 'teacher') {
    courses = teacherCourses.value
  } else {
    courses = studentCourses.value
  }
  
  // 如果有搜索文本，过滤课程
  if (searchText.value) {
    return courses.filter(course => 
      course.title.toLowerCase().includes(searchText.value.toLowerCase()) || 
      course.teacher.toLowerCase().includes(searchText.value.toLowerCase())
    )
  }
  
  return courses
})

// 进入课程详情
const enterCourse = (courseId) => {
  // 根据用户角色确定跳转路径
  const path = userRole.value === 'teacher' 
    ? `/home/teacher/course/${courseId}` 
    : `/home/student/course/${courseId}`
  
  router.push(path)
}

// 添加课程
const addCourse = () => {
  if (userRole.value !== 'teacher') {
    ElMessage.warning('只有教师才能添加课程')
    return
  }
  addCourseDialogVisible.value = true
}

// 提交新课程
const submitNewCourse = () => {
  if (!newCourse.value.title) {
    ElMessage.warning('请输入课程标题')
    return
  }
  
  // 添加新课程
  teacherCourses.value.push({
    id: Date.now(),
    title: newCourse.value.title,
    description: newCourse.value.description,
    cover: newCourse.value.cover || 'https://img2.baidu.com/it/u=1395980100,2999804584&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500',
    teacher: userName.value,
    students: 0,
    progress: 0
  })
  
  ElMessage.success('课程添加成功')
  addCourseDialogVisible.value = false
  
  // 重置表单
  newCourse.value = {
    title: '',
    description: '',
    cover: ''
  }
}

// 创建文件夹
const createFolder = () => {
  if (userRole.value !== 'teacher') {
    ElMessage.warning('只有教师才能创建文件夹')
    return
  }
  addFolderDialogVisible.value = true
}

// 提交新文件夹
const submitNewFolder = () => {
  if (!newFolder.value.name) {
    ElMessage.warning('请输入文件夹名称')
    return
  }
  
  ElMessage.success(`文件夹"${newFolder.value.name}"创建成功`)
  addFolderDialogVisible.value = false
  
  // 重置表单
  newFolder.value = {
    name: '',
    description: ''
  }
}

// 编辑课程
const editCourse = (course, event) => {
  event.stopPropagation() // 阻止事件冒泡
  ElMessage.info(`编辑课程: ${course.title}`)
}

// 删除课程
const deleteCourse = (course, event) => {
  event.stopPropagation() // 阻止事件冒泡
  
  ElMessageBox.confirm(
    `确定要删除课程"${course.title}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    teacherCourses.value = teacherCourses.value.filter(item => item.id !== course.id)
    ElMessage.success('课程删除成功')
  }).catch(() => {})
}
</script>

<template>
  <div class="courses-container">
    <div class="courses-header">
      <div class="tabs" v-if="userRole === 'student'">
        <div 
          class="tab active"
        >
          我的课程
        </div>
      </div>
      <div class="tabs" v-else>
        <div 
          class="tab active"
        >
          我的课程
        </div>
      </div>
      
      <div class="actions">
        <el-button type="primary" :icon="Plus" @click="addCourse" v-if="userRole === 'teacher'">添加课程</el-button>
        <el-button :icon="Document" @click="createFolder" v-if="userRole === 'teacher'">新建文件夹</el-button>
        <el-input
          v-model="searchText"
          placeholder="搜索课程"
          class="search-input"
          clearable
        >
          <template #suffix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>
    
    <!-- 课程列表 -->
    <div v-if="displayedCourses.length > 0" class="course-list">
      <div 
        v-for="course in displayedCourses" 
        :key="course.id" 
        class="course-card"
        @click="enterCourse(course.id)"
      >
        <div class="course-image">
          <img :src="course.cover" alt="课程封面" />
          <div class="course-actions" v-if="userRole === 'teacher'">
            <el-button type="primary" circle :icon="Edit" size="small" @click="editCourse(course, $event)"></el-button>
            <el-button type="danger" circle :icon="Delete" size="small" @click="deleteCourse(course, $event)"></el-button>
          </div>
        </div>
        <div class="course-title">{{ course.title }}</div>
        <div class="course-info">
          <span class="course-teacher">{{ course.teacher }}</span>
          <span v-if="userRole === 'teacher'" class="course-students">学生: {{ course.students }}</span>
          <el-progress 
            :percentage="course.progress" 
            :stroke-width="5" 
            :text-inside="true" 
            :format="() => `${course.progress}%`"
            :color="course.progress > 80 ? '#67C23A' : course.progress > 50 ? '#409EFF' : '#E6A23C'"
          ></el-progress>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else class="empty-state">
      <el-empty description="暂无课程"></el-empty>
    </div>
    
    <!-- 添加课程对话框 -->
    <el-dialog
      v-model="addCourseDialogVisible"
      title="添加课程"
      width="500px"
    >
      <el-form :model="newCourse" label-width="80px">
        <el-form-item label="课程名称" required>
          <el-input v-model="newCourse.title" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        <el-form-item label="课程简介">
          <el-input 
            v-model="newCourse.description" 
            type="textarea" 
            rows="3" 
            placeholder="请输入课程简介"
          ></el-input>
        </el-form-item>
        <el-form-item label="封面图片">
          <el-input v-model="newCourse.cover" placeholder="请输入封面图片URL"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addCourseDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitNewCourse">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 添加文件夹对话框 -->
    <el-dialog
      v-model="addFolderDialogVisible"
      title="新建文件夹"
      width="500px"
    >
      <el-form :model="newFolder" label-width="80px">
        <el-form-item label="文件夹名" required>
          <el-input v-model="newFolder.name" placeholder="请输入文件夹名称"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input 
            v-model="newFolder.description" 
            type="textarea" 
            rows="3" 
            placeholder="请输入文件夹描述"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addFolderDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitNewFolder">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.courses-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 115px);
}

.courses-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.tabs {
  display: flex;
  gap: 20px;
}

.tab {
  font-size: 16px;
  padding: 8px 0;
  cursor: pointer;
  position: relative;
}

.tab.active {
  font-weight: bold;
  color: #409EFF;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #409EFF;
}

.actions {
  display: flex;
  gap: 10px;
}

.search-input {
  width: 200px;
}

.course-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.course-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-image {
  height: 150px;
  overflow: hidden;
  position: relative;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-actions {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.course-card:hover .course-actions {
  opacity: 1;
}

.course-title {
  padding: 10px;
  font-size: 16px;
  font-weight: bold;
}

.course-info {
  padding: 0 10px 10px;
}

.course-teacher {
  font-size: 14px;
  color: #909399;
  display: block;
  margin-bottom: 5px;
}

.course-students {
  font-size: 12px;
  color: #606266;
  margin-left: 5px;
}

.empty-state {
  margin-top: 50px;
  display: flex;
  justify-content: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 