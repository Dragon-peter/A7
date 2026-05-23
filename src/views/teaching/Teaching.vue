<script setup>
import { ref, reactive } from 'vue'
import { Plus, Delete, Edit, Download, Connection, RefreshRight, Upload, Document, VideoCamera } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 课程列表
const courseList = ref([
  { id: 1, name: '计算机网络' },
  { id: 2, name: '操作系统' },
  { id: 3, name: '高等数学' },
  { id: 4, name: '嵌入式Linux开发' }
])

// 当前选中的课程
const selectedCourse = ref(courseList.value[0])

// 教学内容列表
const teachingContents = ref([
  {
    id: 1,
    title: '计算机网络基础概念',
    status: 'published',
    createTime: '2023-06-15',
    generatedBy: 'ai'
  },
  {
    id: 2,
    title: '网络协议分层模型',
    status: 'draft',
    createTime: '2023-06-18',
    generatedBy: 'mixed'
  },
  {
    id: 3,
    title: '网络安全基础',
    status: 'published',
    createTime: '2023-06-20',
    generatedBy: 'teacher'
  }
])

// 新建教学内容表单
const formVisible = ref(false)
const form = reactive({
  title: '',
  description: '',
  courseId: '',
  knowledgePoints: [],
  outline: '', // 教学大纲
  includeCases: true, // 是否包含案例
  includeExercises: true, // 是否包含练习
  teachingHours: 2, // 预计教学课时
  // 新增：上传文件列表
  uploadedFiles: []
})

// 知识点列表
const knowledgePoints = ref([
  { id: 1, name: '网络基础概念' },
  { id: 2, name: 'OSI七层模型' },
  { id: 3, name: 'TCP/IP协议族' },
  { id: 4, name: '网络安全' },
  { id: 5, name: 'Linux系统基础' },
  { id: 6, name: 'Shell编程' },
  { id: 7, name: '嵌入式开发环境搭建' },
  { id: 8, name: '设备驱动开发' }
])

// 生成状态
const generating = ref(false)
const generationProgress = ref(0)
const progressInterval = ref(null)
const generatedContent = ref('')
const contentPreviewVisible = ref(false)

// 文件上传相关
const uploadRef = ref(null)
const fileList = ref([])
const uploadVisible = ref(false)
const fileTypeOptions = ref([
  { value: 'outline', label: '课程大纲文件' },
  { value: 'knowledge', label: '知识库文档' },
  { value: 'video', label: '教学视频' },
  { value: 'case', label: '案例文档' },
  { value: 'exercise', label: '练习题库' }
])

// 选择课程
const selectCourse = (course) => {
  selectedCourse.value = course
  // 这里应该根据所选课程加载相应的教学内容和知识点
}

// 新建教学内容
const createContent = () => {
  formVisible.value = true
  form.courseId = selectedCourse.value.id
  form.title = ''
  form.description = ''
  form.outline = ''
  form.knowledgePoints = []
  form.uploadedFiles = []
  fileList.value = []
}

// 处理文件上传
const handleFileUpload = (file) => {
  // 防止自动上传
  return false
}

// 手动添加文件到列表
const addFile = (file) => {
  // 文件类型检查
  const isDocx = file.name.endsWith('.docx') || file.name.endsWith('.doc')
  const isPdf = file.name.endsWith('.pdf')
  const isVideo = file.name.endsWith('.mp4') || file.name.endsWith('.avi') || file.name.endsWith('.mov')
  
  if (!isDocx && !isPdf && !isVideo) {
    ElMessage.warning('请上传文档(docx/pdf)或视频文件(mp4/avi/mov)')
    return false
  }
  
  // 默认文件类型
  let fileType = 'outline'
  if (isVideo) {
    fileType = 'video'
  }
  
  // 添加到文件列表
  fileList.value.push({
    raw: file,
    name: file.name,
    size: file.size,
    type: fileType,
    status: 'ready',
    percentage: 0
  })
  
  return false
}

// 移除文件
const removeFile = (file) => {
  fileList.value = fileList.value.filter(item => item.name !== file.name)
}

// 更新文件类型
const updateFileType = (index, type) => {
  if (fileList.value[index]) {
    fileList.value[index].type = type
  }
}

// 打开文件选择对话框
const openFileUpload = () => {
  uploadVisible.value = true
}

// 确认文件上传
const confirmFileUpload = () => {
  // 检查是否有文件
  if (fileList.value.length === 0) {
    ElMessage.warning('请至少上传一个文件')
    return
  }
  
  // 检查每个文件是否都有类型
  const invalidFile = fileList.value.find(file => !file.type)
  if (invalidFile) {
    ElMessage.warning(`请为文件 ${invalidFile.name} 选择文件类型`)
    return
  }
  
  // 模拟上传过程
  const uploadPromises = fileList.value.map((file, index) => {
    return new Promise((resolve) => {
      let progress = 0
      const interval = setInterval(() => {
        progress += Math.floor(Math.random() * 20)
        if (progress >= 100) {
          clearInterval(interval)
          progress = 100
          file.status = 'success'
          file.percentage = 100
          
          // 将文件信息保存到表单中
          form.uploadedFiles.push({
            name: file.name,
            type: file.type,
            size: file.size
          })
          
          // 如果是大纲文件，模拟提取内容到大纲输入框
          if (file.type === 'outline' && !form.outline) {
            form.outline = `从《${file.name}》中提取的教学大纲：\n\n1. 课程介绍与学习目标\n2. 基础概念与原理\n3. 核心技术与应用场景\n4. 实践案例分析\n5. 综合能力训练`
          }
          
          // 如果是知识库文档，自动添加相关知识点
          if (file.type === 'knowledge' && selectedCourse.value.id === 1) {
            const networkPoints = [1, 2, 3, 4]
            networkPoints.forEach(pointId => {
              if (!form.knowledgePoints.includes(pointId)) {
                form.knowledgePoints.push(pointId)
              }
            })
          } else if (file.type === 'knowledge' && selectedCourse.value.id === 4) {
            const linuxPoints = [5, 6, 7, 8]
            linuxPoints.forEach(pointId => {
              if (!form.knowledgePoints.includes(pointId)) {
                form.knowledgePoints.push(pointId)
              }
            })
          }
          
          resolve()
        } else {
          file.status = 'uploading'
          file.percentage = progress
        }
      }, 300)
    })
  })
  
  Promise.all(uploadPromises).then(() => {
    ElMessage.success('文件上传成功')
    uploadVisible.value = false
  })
}

// 提交表单
const submitForm = async () => {
  if (!form.title) {
    ElMessage.warning('请输入教学内容标题')
    return
  }

  // 检查是否有课程大纲或上传的文件
  if (!form.outline && form.uploadedFiles.length === 0) {
    ElMessage.warning('请输入教学大纲内容或上传相关文件')
    return
  }

  // 开始生成
  generating.value = true
  generationProgress.value = 0

  try {
    // 调用真实的AI API生成教学内容
    const response = await generateAITeachingContent()

    // 模拟进度更新
    const progressInterval = setInterval(() => {
      generationProgress.value += Math.floor(Math.random() * 10) + 5
      if (generationProgress.value >= 100) {
        clearInterval(progressInterval)
        generationProgress.value = 100

        // 设置生成的内容
        generatedContent.value = response

        // 添加到教学内容列表
        teachingContents.value.unshift({
          id: Date.now(),
          title: form.title,
          status: 'draft',
          createTime: new Date().toISOString().split('T')[0],
          generatedBy: form.uploadedFiles.length > 0 ? 'mixed' : 'ai'
        })

        generating.value = false
        contentPreviewVisible.value = true
      }
    }, 200)

  } catch (error) {
    generating.value = false
    generationProgress.value = 0
    ElMessage.error('生成教学内容失败：' + error.message)
  }
}

// 生成模拟的AI教学内容
const generateMockAIContent = () => {
  const courseTitle = selectedCourse.value.name
  const selectedPoints = form.knowledgePoints.map(id => 
    knowledgePoints.value.find(point => point.id === id)?.name
  ).filter(Boolean)
  
  const uploadedFileTypes = form.uploadedFiles.map(f => f.type)
  const hasVideo = uploadedFileTypes.includes('video')
  const hasCase = uploadedFileTypes.includes('case') || form.includeCases
  const hasExercise = uploadedFileTypes.includes('exercise') || form.includeExercises
  
  return `
  # ${form.title}
  
  ## 教学目标
  通过本次课程，学生将掌握${courseTitle}中的关键概念和实践技能，特别是${selectedPoints.join('、')}等知识点。
  
  ## 教学内容
  
  ### 1. 基础概念
  ${form.outline ? form.outline.split('\n').slice(0, 2).join('\n') + '...' : '根据上传的课程大纲文件自动提取的内容'}
  
  ### 2. 核心原理
  - 详细讲解${selectedPoints[0] || '相关'}知识点
  - 阐述${selectedPoints[1] || '重要'}概念的实际应用
  - 展示关键技术的工作原理
  
  ### 3. 案例分析
  ${hasCase ? `
  - 案例一：实际项目中的应用场景
  - 案例二：典型问题的解决方案
  - 案例三：行业最佳实践分享
  ` : '本教学内容未包含案例分析'}
  
  ### 4. 实践练习
  ${hasExercise ? `
  - 练习一：基础概念应用
  - 练习二：问题解决能力训练
  - 练习三：综合实战项目
  ` : '本教学内容未包含实践练习'}
  
  ## 教学计划
  - 总课时：${form.teachingHours}学时
  - 教学方式：理论讲解 ${hasVideo ? '+ 视频教学 ' : ''}${hasCase ? '+ 案例分析 ' : ''}${hasExercise ? '+ 实践练习' : ''}
  - 建议时间分配：理论讲解(${Math.round(form.teachingHours * 0.4)}学时)${hasVideo ? '、视频教学(0.5学时)' : ''}${hasCase ? '、案例分析(' + Math.round(form.teachingHours * 0.3) + '学时)' : ''}${hasExercise ? '、实践练习(' + Math.round(form.teachingHours * 0.3) + '学时)' : ''}
  - 考核方式：${hasExercise ? '实践作业 + 理论测验' : '理论测验'}
  
  ## 教学资源
  ${form.uploadedFiles.length > 0 ? `
  - 已上传资源：${form.uploadedFiles.map(f => `《${f.name}》(${fileTypeOptions.value.find(opt => opt.value === f.type)?.label})`).join('、')}
  ` : ''}
  - 教材：《${courseTitle}教程》
  - 参考资料：行业标准文档、优秀实践案例
  - 在线资源：相关技术社区、教学视频
  
  *本教学内容由智能助手基于上传文件和教学大纲生成，教师可根据实际情况进行调整。*
  `;
}

// 调用真实的AI API生成教学内容
const generateAITeachingContent = async () => {
  try {
    const courseTitle = selectedCourse.value.name
    const selectedPoints = form.knowledgePoints.map(id =>
      knowledgePoints.value.find(point => point.id === id)?.name
    ).filter(Boolean)

    const requestData = {
      title: form.title,
      courseTitle: courseTitle,
      outline: form.outline,
      knowledgePoints: selectedPoints,
      includeCases: form.includeCases,
      includeExercises: form.includeExercises,
      teachingHours: form.teachingHours
    }

    // 创建超时控制器
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 180000) // 2分钟超时

    const response = await fetch('http://localhost:8080/api/teaching-ai/generate-teaching-content', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestData),
      signal: controller.signal
    })

    clearTimeout(timeoutId)

    if (!response.ok) {
      throw new Error('网络请求失败')
    }

    const result = await response.json()

    // 检查后端返回的成功标志
    if (!result.isSuccess) {
      throw new Error(result.message || '生成失败')
    }

    // 检查是否有数据
    if (!result.data) {
      throw new Error('生成的内容为空')
    }

    // 格式化AI生成的内容
    return formatAIContent(result.data)

  } catch (error) {
    console.error('AI生成内容失败:', error)

    // 处理不同类型的错误
    if (error.name === 'AbortError') {
      throw new Error('请求超时，请稍后重试')
    } else if (error.message && error.message.includes('成功')) {
      // 如果错误消息包含"成功"，说明是逻辑错误，直接抛出网络错误
      throw new Error('网络请求异常，请检查网络连接')
    } else {
      throw new Error(error.message || 'AI服务暂时不可用，请稍后重试')
    }
  }
}

// 格式化AI生成的内容
const formatAIContent = (data) => {
  let content = `# ${form.title}\n\n`

  if (data.baseContent) {
    content += `## 基础教学内容\n\n${data.baseContent}\n\n`
  }

  if (data.caseAnalysis && form.includeCases) {
    content += `## 案例分析\n\n${data.caseAnalysis}\n\n`
  }

  if (data.practiceExercises && form.includeExercises) {
    content += `## 实践练习\n\n${data.practiceExercises}\n\n`
  }

  if (data.teachingSuggestions) {
    content += `## 教学建议\n\n${data.teachingSuggestions}\n\n`
  }

  content += `---\n\n*本教学内容由AI智能生成，请根据实际教学需要进行调整。*`

  return content
}

// 保存生成的内容
const saveGeneratedContent = () => {
  contentPreviewVisible.value = false
  formVisible.value = false
  ElMessage.success('教学内容已保存')
  
  // 这里应该调用API将生成的内容保存到数据库
}

// 重新生成内容
const regenerateContent = () => {
  contentPreviewVisible.value = false
  generatedContent.value = ''
  generationProgress.value = 0
  submitForm()
}

// 取消表单
const cancelForm = () => {
  formVisible.value = false
  contentPreviewVisible.value = false
  form.title = ''
  form.description = ''
  form.knowledgePoints = []
  form.outline = ''
  form.uploadedFiles = []
  fileList.value = []
  generatedContent.value = ''
}

// 查看教学内容详情
const viewContent = async (content) => {
  try {
    const response = await fetch(`/api/teaching-ai/teaching-content/${content.id}`)
    const result = await response.json()

    if (result.success) {
      generatedContent.value = formatAIContent(result.data)
      contentPreviewVisible.value = true
    } else {
      ElMessage.error('获取教学内容失败')
    }
  } catch (error) {
    console.error('获取教学内容失败:', error)
    ElMessage.error('获取教学内容失败')
  }
}

// 编辑教学内容
const editContent = (content) => {
  ElMessage.info(`编辑教学内容: ${content.title}`)
}

// 删除教学内容
const deleteContent = (content) => {
  ElMessageBox.confirm(
    `确定要删除教学内容"${content.title}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    teachingContents.value = teachingContents.value.filter(item => item.id !== content.id)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// 下载教学内容
const downloadContent = (content) => {
  ElMessage.success(`下载教学内容: ${content.title}`)
}

// 状态文本映射
const statusMap = {
  draft: '草稿',
  published: '已发布'
}

// 生成方式文本映射
const generatedByMap = {
  ai: 'AI生成',
  teacher: '教师创建',
  mixed: '混合编辑'
}

// 获取文件类型图标
const getFileTypeIcon = (fileType) => {
  switch (fileType) {
    case 'video':
      return VideoCamera
    default:
      return Document
  }
}

// 获取文件大小格式化
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(1) + ' KB'
  } else {
    return (size / 1024 / 1024).toFixed(1) + ' MB'
  }
}
</script>

<template>
  <div class="teaching-container">
    <div class="teaching-header">
      <h2>智能备课系统</h2>
      <el-button type="primary" :icon="Plus" @click="createContent">新建教学内容</el-button>
    </div>
    
    <div class="teaching-main">
      <div class="course-selector">
        <h3>选择课程</h3>
        <div class="course-list">
          <div 
            v-for="course in courseList" 
            :key="course.id" 
            class="course-item"
            :class="{ active: selectedCourse.id === course.id }"
            @click="selectCourse(course)"
          >
            {{ course.name }}
          </div>
        </div>
      </div>
      
      <div class="content-list">
        <h3>{{ selectedCourse.name }} - 教学内容列表</h3>
        <el-empty v-if="teachingContents.length === 0" description="暂无教学内容，请点击右上角新建" />
        <el-table v-else :data="teachingContents" style="width: 100%">
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'published' ? 'success' : 'warning'">
                {{ statusMap[scope.row.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="generatedBy" label="生成方式" width="120">
            <template #default="scope">
              {{ generatedByMap[scope.row.generatedBy] }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="120" />
          <el-table-column label="操作" width="250">
            <template #default="scope">
              <el-button :icon="Document" size="small" @click="viewContent(scope.row)">查看</el-button>
              <el-button :icon="Edit" size="small" @click="editContent(scope.row)">编辑</el-button>
              <el-button :icon="Download" size="small" type="primary" @click="downloadContent(scope.row)">下载</el-button>
              <el-button :icon="Delete" size="small" type="danger" @click="deleteContent(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    
    <!-- 新建教学内容对话框 -->
    <el-dialog
      v-model="formVisible"
      title="新建教学内容"
      width="700px"
      :close-on-click-modal="false"
      :before-close="cancelForm"
    >
      <el-form v-if="!contentPreviewVisible" :model="form" label-width="100px">
        <el-form-item label="课程">
          <el-input v-model="selectedCourse.name" disabled />
        </el-form-item>
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入教学内容标题" />
        </el-form-item>
        
        <!-- 文件上传部分 -->
        <el-form-item label="课程资料">
          <div class="upload-section">
            <el-button type="primary" :icon="Upload" @click="openFileUpload">
              上传课程资料
            </el-button>
            <span class="upload-tip">支持上传课程大纲、知识库文档、教学视频等</span>
          </div>
          
          <!-- 已上传文件列表 -->
          <div v-if="form.uploadedFiles.length > 0" class="uploaded-files">
            <div v-for="(file, index) in form.uploadedFiles" :key="index" class="file-item">
              <el-icon class="file-icon">
                <component :is="getFileTypeIcon(file.type)" />
              </el-icon>
              <div class="file-info">
                <div class="file-name">{{ file.name }}</div>
                <div class="file-meta">
                  <el-tag size="small">
                    {{ fileTypeOptions.find(opt => opt.value === file.type)?.label }}
                  </el-tag>
                  <span class="file-size">{{ formatFileSize(file.size) }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="知识点">
          <el-select
            v-model="form.knowledgePoints"
            multiple
            placeholder="请选择知识点"
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
        <el-form-item label="教学大纲">
          <el-input 
            v-model="form.outline" 
            type="textarea" 
            rows="6" 
            placeholder="请输入教学大纲或教学内容要点，或上传课程大纲文件自动提取，AI将根据此内容生成详细教学内容" 
          />
        </el-form-item>
        <el-form-item label="内容设置">
          <div class="form-options">
            <el-checkbox v-model="form.includeCases">包含案例分析</el-checkbox>
            <el-checkbox v-model="form.includeExercises">包含实践练习</el-checkbox>
            <el-input-number v-model="form.teachingHours" :min="1" :max="10" label="预计课时" />
          </div>
        </el-form-item>
      </el-form>
      
      <!-- 生成的内容预览 -->
      <div v-if="contentPreviewVisible" class="content-preview">
        <h3>生成的教学内容</h3>
        <div class="preview-content">
          <pre>{{ generatedContent }}</pre>
        </div>
      </div>
      
      <div v-if="generating" class="generation-progress">
        <p>正在生成教学内容，请稍候...</p>
        <el-progress :percentage="generationProgress" />
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelForm">取消</el-button>
          <template v-if="!contentPreviewVisible">
            <el-button type="primary" @click="submitForm" :loading="generating">
              {{ generating ? '生成中...' : '生成教学内容' }}
            </el-button>
          </template>
          <template v-else>
            <el-button :icon="RefreshRight" @click="regenerateContent">重新生成</el-button>
            <el-button type="primary" @click="saveGeneratedContent">保存内容</el-button>
          </template>
        </span>
      </template>
    </el-dialog>
    
    <!-- 文件上传对话框 -->
    <el-dialog
      v-model="uploadVisible"
      title="上传课程资料"
      width="600px"
    >
      <el-upload
        ref="uploadRef"
        class="file-uploader"
        drag
        :auto-upload="false"
        :http-request="handleFileUpload"
        :on-change="addFile"
        :on-remove="removeFile"
        multiple
      >
        <el-icon class="el-icon--upload"><Upload /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持上传Word文档(.docx)、PDF文件(.pdf)和视频文件(.mp4/.avi/.mov)
          </div>
        </template>
      </el-upload>
      
      <!-- 文件列表 -->
      <div class="file-list" v-if="fileList.length > 0">
        <h4>待上传文件列表</h4>
        <div v-for="(file, index) in fileList" :key="index" class="file-list-item">
          <div class="file-info">
            <span class="file-name">{{ file.name }}</span>
            <span class="file-size">{{ formatFileSize(file.size) }}</span>
          </div>
          <div class="file-type-selector">
            <el-select 
              v-model="fileList[index].type" 
              placeholder="选择文件类型"
              size="small"
              @change="updateFileType(index, $event)"
            >
              <el-option
                v-for="option in fileTypeOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </div>
          <div class="file-status" v-if="file.status === 'uploading'">
            <el-progress :percentage="file.percentage" :format="p => p + '%'" />
          </div>
          <div class="file-status" v-else-if="file.status === 'success'">
            <el-tag type="success">已上传</el-tag>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmFileUpload">确认上传</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.teaching-container {
  padding: 20px;
  height: 100%;
}

.teaching-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.teaching-main {
  display: flex;
  gap: 20px;
  height: calc(100% - 60px);
}

.course-selector {
  width: 200px;
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.course-list {
  margin-top: 15px;
}

.course-item {
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 8px;
  transition: all 0.3s;
}

.course-item:hover {
  background-color: #f5f7fa;
}

.course-item.active {
  background-color: #ecf5ff;
  color: #409eff;
  font-weight: bold;
}

.content-list {
  flex: 1;
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: auto;
}

.generation-progress {
  margin-top: 20px;
}

.form-options {
  display: flex;
  align-items: center;
  gap: 20px;
}

.content-preview {
  margin-top: 10px;
}

.preview-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 15px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.preview-content pre {
  white-space: pre-wrap;
  font-family: inherit;
}

/* 文件上传样式 */
.upload-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.upload-tip {
  color: #909399;
  font-size: 13px;
}

.file-uploader {
  width: 100%;
}

.file-list {
  margin-top: 15px;
}

.file-list h4 {
  margin-top: 0;
  margin-bottom: 10px;
}

.file-list-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  margin-bottom: 8px;
  border-radius: 4px;
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
}

.file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.file-name {
  font-weight: bold;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-size, .file-meta {
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
}

.file-type-selector {
  width: 140px;
  margin: 0 10px;
}

.file-status {
  width: 80px;
  text-align: right;
}

/* 已上传文件列表样式 */
.uploaded-files {
  margin-top: 10px;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 8px;
  margin-bottom: 6px;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.file-icon {
  font-size: 24px;
  margin-right: 10px;
  color: #409eff;
}

.file-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 4px;
}
</style> 