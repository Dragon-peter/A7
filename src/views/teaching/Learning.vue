<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { Search, QuestionFilled, ChatLineRound, Document, ArrowUp, Edit, Plus, Delete, UploadFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 获取用户角色
const userRole = ref('student')
const userName = ref('')

onMounted(() => {
  userRole.value = localStorage.getItem('userRole') || 'student'
  userName.value = localStorage.getItem('userName') || '用户'
})

// 当前选中的课程
const selectedCourse = ref({
  id: 1,
  name: '计算机网络',
  teacher: '张明'
})

// 课程列表
const courseList = ref([
  { id: 1, name: '计算机网络', teacher: '张明' },
  { id: 2, name: '操作系统', teacher: '梁伟' },
  { id: 3, name: '高等数学', teacher: '高等数学教研组' },
  { id: 4, name: '体育理论', teacher: '体育部教研组' }
])

// 知识点列表
const knowledgePoints = ref([
  { 
    id: 1, 
    name: '网络基础概念', 
    mastery: 0.8,
    children: [
      { id: 11, name: '网络定义', mastery: 0.9 },
      { id: 12, name: '网络分类', mastery: 0.7 }
    ]
  },
  { 
    id: 2, 
    name: 'OSI七层模型', 
    mastery: 0.6,
    children: [
      { id: 21, name: '物理层', mastery: 0.8 },
      { id: 22, name: '数据链路层', mastery: 0.7 },
      { id: 23, name: '网络层', mastery: 0.6 },
      { id: 24, name: '传输层', mastery: 0.5 },
      { id: 25, name: '会话层', mastery: 0.4 },
      { id: 26, name: '表示层', mastery: 0.3 },
      { id: 27, name: '应用层', mastery: 0.7 }
    ]
  },
  { 
    id: 3, 
    name: 'TCP/IP协议族', 
    mastery: 0.5,
    children: [
      { id: 31, name: 'IP协议', mastery: 0.6 },
      { id: 32, name: 'TCP协议', mastery: 0.5 },
      { id: 33, name: 'UDP协议', mastery: 0.4 }
    ]
  },
  { 
    id: 4, 
    name: '网络安全', 
    mastery: 0.3,
    children: [
      { id: 41, name: '加密技术', mastery: 0.4 },
      { id: 42, name: '防火墙', mastery: 0.3 },
      { id: 43, name: '入侵检测', mastery: 0.2 }
    ]
  }
])

// 学习资源列表
const learningResources = ref([
  { id: 1, title: '计算机网络基础概念', type: 'document', url: '#', addedBy: '张明' },
  { id: 2, title: 'OSI七层模型详解', type: 'video', url: '#', addedBy: '张明' },
  { id: 3, title: 'TCP/IP协议族学习指南', type: 'document', url: '#', addedBy: '系统' },
  { id: 4, title: '网络安全基础知识', type: 'document', url: '#', addedBy: '梁伟' },
  { id: 5, title: '网络层协议详解', type: 'video', url: '#', addedBy: '系统' }
])

// 问答历史
const chatHistory = ref([
  { 
    id: 1, 
    question: 'OSI七层模型分别是什么？', 
    answer: 'OSI七层模型从下到上分别是：物理层、数据链路层、网络层、传输层、会话层、表示层和应用层。每一层负责不同的网络功能。',
    time: '2023-06-15 10:30',
    relatedPoints: [2]
  },
  { 
    id: 2, 
    question: 'TCP和UDP有什么区别？', 
    answer: 'TCP（传输控制协议）是面向连接的协议，提供可靠的数据传输，具有流量控制和拥塞控制机制；UDP（用户数据报协议）是无连接的协议，不保证数据传输的可靠性，但传输速度快，适用于实时应用。',
    time: '2023-06-16 14:20',
    relatedPoints: [32, 33]
  }
])

// 新问题
const newQuestion = ref('')
const askingQuestion = ref(false)
const showRelatedResources = ref(false)
const relatedResources = ref([])

// 添加资源对话框
const addResourceDialogVisible = ref(false)
const newResource = ref({
  title: '',
  type: 'document',
  url: '',
  chapterId: null,
  source: 'file',
  description: '',
  file: null
})

// 编辑知识点对话框
const editKnowledgeDialogVisible = ref(false)
const editingKnowledge = ref(null)

// 添加知识点对话框
const addKnowledgeDialogVisible = ref(false)
const newKnowledge = ref({
  name: '',
  chapterId: null,
  difficulty: 'medium',
  description: '',
  prerequisites: []
})

// 章节列表
const chapters = ref([
  { id: 1, title: '第一章 网络基础' },
  { id: 2, title: '第二章 OSI模型' },
  { id: 3, title: '第三章 TCP/IP协议' },
  { id: 4, title: '第四章 网络设备' }
])

// 文件上传相关
const uploading = ref(false)
const uploadRef = ref(null)

// 表单验证规则
const knowledgeRules = {
  name: [
    { required: true, message: '请输入知识点名称', trigger: 'blur' }
  ],
  chapterId: [
    { required: true, message: '请选择所属章节', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ]
}

const resourceRules = {
  title: [
    { required: true, message: '请输入资源标题', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择资源类型', trigger: 'change' }
  ],
  chapterId: [
    { required: true, message: '请选择所属章节', trigger: 'change' }
  ]
}

// 页面标题
const pageTitle = computed(() => {
  return userRole.value === 'teacher' ? '学习资源管理' : '学习助手'
})

// 选择课程
const selectCourse = (course) => {
  selectedCourse.value = course
  // 这里应该加载对应课程的知识点和学习资源
}

// 提问
const askQuestion = async () => {
  if (!newQuestion.value.trim()) {
    return
  }
  
  askingQuestion.value = true
  
  // 模拟API请求延迟
  await new Promise(resolve => setTimeout(resolve, 1500))
  
  // 模拟回答
  const answer = '这是一个关于计算机网络的问题的回答。根据网络协议的定义，不同层次的协议有不同的功能和特点。您的问题涉及到网络层和传输层的概念，建议您查看相关的学习资源以深入理解。'
  
  // 添加到历史记录
  chatHistory.value.push({
    id: Date.now(),
    question: newQuestion.value,
    answer: answer,
    time: new Date().toLocaleString(),
    relatedPoints: [2, 3]
  })
  
  newQuestion.value = ''
  askingQuestion.value = false
  
  // 模拟相关资源
  relatedResources.value = [
    { id: 2, title: 'OSI七层模型详解', type: 'video', url: '#' },
    { id: 3, title: 'TCP/IP协议族学习指南', type: 'document', url: '#' }
  ]
  showRelatedResources.value = true
  
  // 滚动到底部
  await nextTick()
  const chatContainer = document.querySelector('.chat-history')
  if (chatContainer) {
    chatContainer.scrollTop = chatContainer.scrollHeight
  }
}

// 获取掌握程度颜色
const getMasteryColor = (mastery) => {
  if (mastery >= 0.8) return '#67C23A'
  if (mastery >= 0.6) return '#E6A23C'
  if (mastery >= 0.4) return '#F56C6C'
  return '#909399'
}

// 获取资源类型图标
const getResourceIcon = (type) => {
  if (type === 'video') return 'VideoPlay'
  if (type === 'document') return Document
  return Document
}

// 获取资源类型文本
const getResourceTypeText = (type) => {
  if (type === 'video') return '视频'
  if (type === 'document') return '文档'
  return '资源'
}

// 打开资源（支持本地文件和外部链接）
const openResource = (resource) => {
  if (resource.fileName) {
    // 本地文件，打开预览或下载
    const previewUrl = `http://localhost:8080/api/file/preview/${resource.fileName}`
    window.open(previewUrl, '_blank')
  } else if (resource.url) {
    // 外部链接
    window.open(resource.url, '_blank')
  } else {
    ElMessage.warning('资源链接无效')
  }
}

// 关闭相关资源提示
const closeRelatedResources = () => {
  showRelatedResources.value = false
}

// 展开/收起知识点
const toggleKnowledgePoint = (point) => {
  point.expanded = !point.expanded
}

// 添加资源
const addResource = () => {
  addResourceDialogVisible.value = true
}

// 提交新资源
const submitNewResource = async () => {
  if (!newResource.value.title) {
    ElMessage.warning('请输入资源标题')
    return
  }

  if (newResource.value.source === 'url' && !newResource.value.url) {
    ElMessage.warning('请输入资源链接')
    return
  }

  if (newResource.value.source === 'file' && !newResource.value.file) {
    ElMessage.warning('请选择要上传的文件')
    return
  }

  uploading.value = true

  try {
    let resourceUrl = newResource.value.url
    let fileName = null
    let originalName = null

    // 如果是文件上传
    if (newResource.value.source === 'file' && newResource.value.file) {
      console.log('准备上传文件:', newResource.value.file)

      const formData = new FormData()
      formData.append('file', newResource.value.file)

      console.log('FormData内容:', formData.get('file'))

      const uploadResponse = await fetch('http://localhost:8080/api/file/upload', {
        method: 'POST',
        body: formData
      })

      console.log('上传响应状态:', uploadResponse.status)

      if (!uploadResponse.ok) {
        const errorText = await uploadResponse.text()
        console.error('上传失败响应:', errorText)
        throw new Error(`文件上传失败: ${uploadResponse.status} ${errorText}`)
      }

      const uploadResult = await uploadResponse.json()
      console.log('上传结果:', uploadResult)

      if (uploadResult.isSuccess) {
        resourceUrl = uploadResult.data.url
        fileName = uploadResult.data.fileName
        originalName = uploadResult.data.originalName
      } else {
        throw new Error(uploadResult.message || '文件上传失败')
      }
    }

    // 如果没有选择章节，使用默认章节ID
    const chapterId = newResource.value.chapterId || chapters.value[0]?.id || 1;

    console.log('提交资源信息:', {
      title: newResource.value.title,
      type: newResource.value.type,
      url: resourceUrl,
      chapterId: chapterId,
      description: newResource.value.description,
      fileName: fileName,
      originalName: originalName
    });

    // 保存资源信息到本地资源服务
    const response = await fetch('http://localhost:8080/api/local/resource', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        title: newResource.value.title,
        type: newResource.value.type,
        url: resourceUrl,
        fileName: fileName,
        originalName: originalName || newResource.value.file?.name,
        description: newResource.value.description,
        size: newResource.value.file?.size
      })
    })

    const result = await response.json()

    if (result.isSuccess) {
      // 添加到本地列表
      learningResources.value.push({
        id: result.data.id,
        title: result.data.title,
        type: result.data.type,
        url: result.data.url,
        fileName: result.data.fileName,
        originalName: result.data.originalName,
        description: result.data.description,
        addedBy: userName.value,
        createTime: result.data.createTime
      })

      ElMessage.success('资源添加成功')
      addResourceDialogVisible.value = false

      // 重置表单
      resetResourceForm()
    } else {
      ElMessage.error(result.message || '添加资源失败')
    }
  } catch (error) {
    console.error('添加资源失败:', error)
    ElMessage.error('添加资源失败：' + error.message)
  } finally {
    uploading.value = false
  }
}

// 删除资源
const deleteResource = (resource, index) => {
  learningResources.value.splice(index, 1)
  ElMessage.success('资源删除成功')
}

// 编辑知识点
const editKnowledge = (point) => {
  editingKnowledge.value = { ...point }
  editKnowledgeDialogVisible.value = true
}

// 删除知识点
const deleteKnowledge = (point) => {
  // 弹出确认对话框
  ElMessageBox.confirm(`确定要删除知识点"${point.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 删除知识点
    knowledgePoints.value = knowledgePoints.value.filter(p => p.id !== point.id)
    ElMessage.success('知识点删除成功')
  }).catch(() => {
    // 取消删除，不做任何操作
  })
}

// 显示添加知识点对话框
const showAddKnowledgeDialog = () => {
  addKnowledgeDialogVisible.value = true
}

// 提交新知识点
const submitNewKnowledge = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/knowledge-point', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        ...newKnowledge.value,
        courseId: selectedCourse.value.id
      })
    })

    const result = await response.json()

    if (result.isSuccess) {
      // 添加到本地列表
      knowledgePoints.value.push({
        id: result.data.id,
        name: newKnowledge.value.name,
        mastery: 0,
        children: []
      })

      ElMessage.success('知识点添加成功')
      addKnowledgeDialogVisible.value = false

      // 重置表单
      newKnowledge.value = {
        name: '',
        chapterId: null,
        difficulty: 'medium',
        description: '',
        prerequisites: []
      }
    } else {
      ElMessage.error(result.message || '添加知识点失败')
    }
  } catch (error) {
    console.error('添加知识点失败:', error)
    ElMessage.error('添加知识点失败')
  }
}

// 关闭添加知识点对话框
const handleAddKnowledgeClose = () => {
  addKnowledgeDialogVisible.value = false
  // 重置表单
  newKnowledge.value = {
    name: '',
    chapterId: null,
    difficulty: 'medium',
    description: '',
    prerequisites: []
  }
}

// 提交知识点编辑
const submitKnowledgeEdit = () => {
  if (!editingKnowledge.value.name) {
    ElMessage.warning('请输入知识点名称')
    return
  }
  
  // 更新知识点
  const index = knowledgePoints.value.findIndex(p => p.id === editingKnowledge.value.id)
  if (index !== -1) {
    knowledgePoints.value[index].name = editingKnowledge.value.name
  }
  
  ElMessage.success('知识点更新成功')
  editKnowledgeDialogVisible.value = false
}

// 文件选择处理
const handleFileChange = (file) => {
  console.log('文件选择:', file)
  newResource.value.file = file.raw
}

// 文件超出限制处理
const handleFileExceed = (files, fileList) => {
  ElMessage.warning('只能上传一个文件，请先删除已选择的文件')
}

// 文件上传前验证
const beforeFileUpload = (file) => {
  console.log('文件上传前验证:', file)

  // 检查文件大小 (50MB)
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过50MB')
    return false
  }

  // 检查文件类型
  const allowedTypes = [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-powerpoint',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    'video/mp4',
    'audio/mpeg',
    'audio/mp3',
    'image/jpeg',
    'image/jpg',
    'image/png',
    'image/gif',
    'text/plain',
    'application/zip',
    'application/x-rar-compressed'
  ]

  const isAllowedType = allowedTypes.includes(file.type) ||
    file.name.toLowerCase().endsWith('.pdf') ||
    file.name.toLowerCase().endsWith('.doc') ||
    file.name.toLowerCase().endsWith('.docx') ||
    file.name.toLowerCase().endsWith('.ppt') ||
    file.name.toLowerCase().endsWith('.pptx')

  if (!isAllowedType) {
    ElMessage.error('不支持的文件格式，请上传PDF、Word、PPT、视频、音频或图片文件')
    return false
  }

  return true
}

// 重置资源表单
const resetResourceForm = () => {
  newResource.value = {
    title: '',
    type: 'document',
    url: '',
    chapterId: null,
    source: 'file',
    description: '',
    file: null
  }
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

// 关闭添加资源对话框
const handleAddResourceClose = () => {
  addResourceDialogVisible.value = false
  resetResourceForm()
}

// 下载资源
const downloadResource = (resource) => {
  if (resource.fileName) {
    const downloadUrl = `http://localhost:8080/api/file/download/${resource.fileName}`
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = resource.originalName || resource.title
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } else {
    ElMessage.warning('该资源不支持下载')
  }
}
</script>

<template>
  <div class="learning-container">
    <div class="learning-header">
      <h2>{{ pageTitle }}</h2>
      <div class="course-selector">
        <span>当前课程：</span>
        <el-select v-model="selectedCourse" value-key="id" style="width: 200px">
          <el-option
            v-for="course in courseList"
            :key="course.id"
            :label="course.name"
            :value="course"
          />
        </el-select>
      </div>
    </div>
    
    <div class="learning-main">
      <!-- 知识点面板 -->
      <div class="knowledge-panel">
        <div class="panel-header">
          <h3>知识点{{ userRole === 'teacher' ? '管理' : '掌握情况' }}</h3>
          <div class="panel-actions">
            <el-input
              placeholder="搜索知识点"
              prefix-icon="Search"
              style="width: 180px"
            />
            <el-button v-if="userRole === 'teacher'" type="primary" :icon="Plus" size="small" @click="showAddKnowledgeDialog">
              添加知识点
            </el-button>
          </div>
        </div>
        
        <div class="knowledge-list">
          <div 
            v-for="point in knowledgePoints" 
            :key="point.id"
            class="knowledge-item"
          >
            <div class="knowledge-header" @click="toggleKnowledgePoint(point)">
              <span class="knowledge-name">{{ point.name }}</span>
              <div class="knowledge-actions">
                <div v-if="userRole === 'student'" class="knowledge-mastery">
                  <el-progress 
                    :percentage="point.mastery * 100" 
                    :color="getMasteryColor(point.mastery)" 
                    :show-text="false"
                    :stroke-width="8"
                  />
                  <span class="mastery-text" :style="{ color: getMasteryColor(point.mastery) }">
                    {{ Math.round(point.mastery * 100) }}%
                  </span>
                </div>
                <div v-else class="teacher-actions">
                  <el-button type="primary" :icon="Edit" circle size="small" @click.stop="editKnowledge(point)"></el-button>
                  <el-button type="danger" :icon="Delete" circle size="small" @click.stop="deleteKnowledge(point)"></el-button>
                </div>
              </div>
            </div>
            
            <div v-if="point.expanded" class="knowledge-children">
              <div 
                v-for="child in point.children" 
                :key="child.id"
                class="knowledge-child"
              >
                <span class="child-name">{{ child.name }}</span>
                <div v-if="userRole === 'student'" class="child-mastery">
                  <el-progress 
                    :percentage="child.mastery * 100" 
                    :color="getMasteryColor(child.mastery)" 
                    :show-text="false"
                    :stroke-width="6"
                  />
                  <span class="mastery-text" :style="{ color: getMasteryColor(child.mastery) }">
                    {{ Math.round(child.mastery * 100) }}%
                  </span>
                </div>
                <div v-else class="child-actions">
                  <el-button type="primary" :icon="Edit" circle size="small"></el-button>
                  <el-button type="danger" :icon="Delete" circle size="small"></el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 资源面板 -->
      <div class="resources-panel">
        <div class="panel-header">
          <h3>学习资源</h3>
          <el-button 
            v-if="userRole === 'teacher'"
            type="primary" 
            :icon="Plus" 
            size="small"
            @click="addResource"
          >
            添加资源
          </el-button>
        </div>
        
        <div class="resources-list">
          <div 
            v-for="(resource, index) in learningResources" 
            :key="resource.id"
            class="resource-item"
            @click="openResource(resource)"
          >
            <el-icon class="resource-icon">
              <component :is="getResourceIcon(resource.type)" />
            </el-icon>
            <div class="resource-info">
              <div class="resource-title">{{ resource.title }}</div>
              <div class="resource-meta">
                <span class="resource-type">{{ getResourceTypeText(resource.type) }}</span>
                <span class="resource-added-by" v-if="userRole === 'teacher'">添加者: {{ resource.addedBy }}</span>
                <span v-if="resource.fileName" class="resource-file-info">本地文件</span>
              </div>
            </div>
            <div class="resource-actions">
              <el-button
                v-if="resource.fileName"
                type="primary"
                :icon="Document"
                circle
                size="small"
                @click.stop="downloadResource(resource)"
                title="下载文件"
              />
              <el-button
                v-if="userRole === 'teacher'"
                type="danger"
                :icon="Delete"
                circle
                size="small"
                @click.stop="deleteResource(resource, index)"
                title="删除资源"
              />
            </div>
          </div>
        </div>
      </div>
      
      <!-- 问答面板 - 仅学生可见 -->
      <div v-if="userRole === 'student'" class="chat-panel">
        <div class="panel-header">
          <h3>在线提问</h3>
        </div>
        
        <div class="chat-history">
          <div 
            v-for="chat in chatHistory" 
            :key="chat.id"
            class="chat-item"
          >
            <div class="question">
              <el-icon><QuestionFilled /></el-icon>
              <div class="question-content">
                <div class="question-text">{{ chat.question }}</div>
                <div class="question-time">{{ chat.time }}</div>
              </div>
            </div>
            
            <div class="answer">
              <el-icon><ChatLineRound /></el-icon>
              <div class="answer-content">
                <div class="answer-text">{{ chat.answer }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="chat-input">
          <el-input
            v-model="newQuestion"
            type="textarea"
            :rows="3"
            placeholder="输入您的问题..."
            :disabled="askingQuestion"
          />
          <el-button 
            type="primary" 
            @click="askQuestion" 
            :loading="askingQuestion"
            :disabled="!newQuestion.trim()"
          >
            提问
          </el-button>
        </div>
        
        <!-- 相关资源提示 -->
        <div v-if="showRelatedResources" class="related-resources">
          <div class="related-header">
            <span>推荐学习资源</span>
            <el-icon @click="closeRelatedResources"><ArrowUp /></el-icon>
          </div>
          <div class="related-list">
            <div 
              v-for="resource in relatedResources" 
              :key="resource.id"
              class="related-item"
              @click="openResource(resource)"
            >
              <el-icon>
                <component :is="getResourceIcon(resource.type)" />
              </el-icon>
              <span>{{ resource.title }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加资源对话框 -->
    <el-dialog
      v-model="addResourceDialogVisible"
      title="添加学习资源"
      width="500px"
    >
      <el-form :model="newResource" label-width="80px">
        <el-form-item label="资源名称" required>
          <el-input v-model="newResource.title" placeholder="请输入资源名称"></el-input>
        </el-form-item>
        <el-form-item label="资源类型">
          <el-select v-model="newResource.type" style="width: 100%">
            <el-option label="文档" value="document"></el-option>
            <el-option label="视频" value="video"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="资源链接" required>
          <el-input v-model="newResource.url" placeholder="请输入资源链接"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addResourceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitNewResource">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 编辑知识点对话框 -->
    <el-dialog
      v-model="editKnowledgeDialogVisible"
      title="编辑知识点"
      width="500px"
    >
      <el-form v-if="editingKnowledge" :model="editingKnowledge" label-width="80px">
        <el-form-item label="知识点名" required>
          <el-input v-model="editingKnowledge.name" placeholder="请输入知识点名称"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editKnowledgeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitKnowledgeEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加知识点对话框 -->
    <el-dialog
      v-model="addKnowledgeDialogVisible"
      title="添加知识点"
      width="600px"
      :before-close="handleAddKnowledgeClose"
    >
      <el-form :model="newKnowledge" :rules="knowledgeRules" ref="knowledgeFormRef" label-width="100px">
        <el-form-item label="知识点名称" prop="name">
          <el-input v-model="newKnowledge.name" placeholder="请输入知识点名称" />
        </el-form-item>

        <el-form-item label="所属章节" prop="chapterId">
          <el-select v-model="newKnowledge.chapterId" placeholder="请选择章节" style="width: 100%">
            <el-option
              v-for="chapter in chapters"
              :key="chapter.id"
              :label="chapter.title"
              :value="chapter.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="难度等级" prop="difficulty">
          <el-select v-model="newKnowledge.difficulty" placeholder="请选择难度等级" style="width: 100%">
            <el-option label="简单" value="easy" />
            <el-option label="中等" value="medium" />
            <el-option label="困难" value="hard" />
          </el-select>
        </el-form-item>

        <el-form-item label="知识点描述" prop="description">
          <el-input
            v-model="newKnowledge.description"
            type="textarea"
            :rows="4"
            placeholder="请输入知识点描述"
          />
        </el-form-item>

        <el-form-item label="前置知识点">
          <el-select
            v-model="newKnowledge.prerequisites"
            multiple
            placeholder="请选择前置知识点"
            style="width: 100%"
          >
            <el-option
              v-for="point in knowledgePoints"
              :key="point.id"
              :label="point.name"
              :value="point.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addKnowledgeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitNewKnowledge">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加学习资源对话框 -->
    <el-dialog
      v-model="addResourceDialogVisible"
      title="添加学习资源"
      width="700px"
      :before-close="handleAddResourceClose"
    >
      <el-form :model="newResource" :rules="resourceRules" ref="resourceFormRef" label-width="100px">
        <el-form-item label="资源标题" prop="title">
          <el-input v-model="newResource.title" placeholder="请输入资源标题" />
        </el-form-item>

        <el-form-item label="资源类型" prop="type">
          <el-select v-model="newResource.type" placeholder="请选择资源类型" style="width: 100%">
            <el-option label="文档" value="document" />
            <el-option label="视频" value="video" />
            <el-option label="音频" value="audio" />
            <el-option label="图片" value="image" />
            <el-option label="链接" value="link" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属章节">
          <el-select v-model="newResource.chapterId" placeholder="可选择章节" style="width: 100%">
            <el-option label="通用资源" value="" />
            <el-option
              v-for="chapter in chapters"
              :key="chapter.id"
              :label="chapter.title"
              :value="chapter.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="资源来源">
          <el-radio-group v-model="newResource.source">
            <el-radio value="file">本地文件</el-radio>
            <el-radio value="url">外部链接</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="newResource.source === 'file'" label="上传文件" prop="file">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-exceed="handleFileExceed"
            :before-upload="beforeFileUpload"
            :limit="1"
            :show-file-list="true"
            accept=".pdf,.doc,.docx,.ppt,.pptx,.mp4,.mp3,.jpg,.jpeg,.png,.gif,.txt,.zip,.rar"
            drag
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 PDF、Word、PPT、视频、音频、图片等格式，文件大小不超过50MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item v-if="newResource.source === 'url'" label="资源链接" prop="url">
          <el-input v-model="newResource.url" placeholder="请输入资源链接" />
        </el-form-item>

        <el-form-item label="资源描述">
          <el-input
            v-model="newResource.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资源描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addResourceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitNewResource" :loading="uploading">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.learning-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 115px);
}

.learning-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.course-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.learning-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

/* 当用户是学生时，使用三列布局 */
.learning-main:has(.chat-panel) {
  grid-template-columns: 1fr 1fr 1fr;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.panel-header h3 {
  margin: 0;
  font-size: 18px;
}

.panel-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.knowledge-panel, .resources-panel, .chat-panel {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.knowledge-list, .resources-list, .chat-history {
  flex: 1;
  overflow-y: auto;
}

.knowledge-item {
  margin-bottom: 15px;
}

.knowledge-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  cursor: pointer;
}

.knowledge-name {
  font-weight: bold;
}

.knowledge-mastery {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 150px;
}

.teacher-actions, .child-actions {
  display: flex;
  gap: 8px;
}

.mastery-text {
  font-size: 14px;
  width: 40px;
  text-align: right;
}

.knowledge-children {
  margin-left: 20px;
  margin-top: 10px;
}

.knowledge-child {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  border-bottom: 1px solid #ebeef5;
}

.child-name {
  font-size: 14px;
}

.child-mastery {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 120px;
}

.resource-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.resource-item:hover {
  background-color: #f5f7fa;
}

.resource-icon {
  font-size: 24px;
  margin-right: 15px;
  color: #409EFF;
}

.resource-info {
  flex: 1;
}

.resource-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.resource-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 10px;
}

.resource-actions {
  opacity: 0;
  transition: opacity 0.3s;
}

.resource-item:hover .resource-actions {
  opacity: 1;
}

.chat-history {
  padding-right: 10px;
}

.chat-item {
  margin-bottom: 20px;
}

.question, .answer {
  display: flex;
  margin-bottom: 10px;
}

.question-content, .answer-content {
  margin-left: 10px;
  padding: 10px;
  border-radius: 4px;
  max-width: 80%;
}

.question-content {
  background-color: #ecf5ff;
}

.answer-content {
  background-color: #f5f7fa;
}

.question-time {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.chat-input {
  margin-top: 15px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.related-resources {
  margin-top: 15px;
  background-color: #ecf5ff;
  border-radius: 4px;
  padding: 10px;
}

.related-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-weight: bold;
  color: #409EFF;
}

.related-header .el-icon {
  cursor: pointer;
}

.related-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.related-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 5px;
  cursor: pointer;
  border-radius: 4px;
}

.related-item:hover {
  background-color: #e6f1fc;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 