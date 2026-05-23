<script setup>
import { ref, reactive } from 'vue'
import { Plus, Delete, Edit, View, Download, Refresh, Connection, Setting } from '@element-plus/icons-vue'
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

// 考核内容列表
const assessmentList = ref([
  {
    id: 1,
    title: '计算机网络期中测验',
    type: 'quiz',
    questionCount: 20,
    difficulty: 3,
    createTime: '2023-06-15'
  },
  {
    id: 2,
    title: '网络协议分层模型练习',
    type: 'practice',
    questionCount: 15,
    difficulty: 2,
    createTime: '2023-06-18'
  },
  {
    id: 3,
    title: '计算机网络期末考试',
    type: 'exam',
    questionCount: 30,
    difficulty: 4,
    createTime: '2023-06-20'
  }
])

// 新建考核内容表单
const formVisible = ref(false)
const form = reactive({
  title: '',
  type: 'practice',
  difficulty: 3,
  questionCount: 10,
  questionTypes: [],
  knowledgePoints: [],
  aiModel: 'qwen-turto',
  referenceTeachingContent: null,
  autoSuggestKnowledgePoints: true,
  balanceDifficulty: true,
  generateAnswers: true,
  coverageRate: 80 // 知识点覆盖率(%)
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

// AI模型选项
const aiModels = ref([
  { value: 'qwen-turto', label: '通义千问' },
  { value: 'gpt-4', label: 'GPT-4' },
  { value: 'llama-3', label: 'Llama 3' },
  { value: 'glm-4', label: 'ChatGLM-4' }
])

// 教学内容列表（用于参考）
const teachingContents = ref([
  { id: 1, title: '计算机网络基础概念', courseId: 1 },
  { id: 2, title: '网络协议分层模型', courseId: 1 },
  { id: 3, title: '操作系统导论', courseId: 2 },
  { id: 4, title: '嵌入式Linux基础', courseId: 4 }
])

// 题目类型列表
const questionTypes = ref([
  { value: 'choice', label: '单选题' },
  { value: 'multiple_choice', label: '多选题' },
  { value: 'fill_blank', label: '填空题' },
  { value: 'true_false', label: '判断题' },
  { value: 'short_answer', label: '简答题' },
  { value: 'programming', label: '编程题' }
])

// 生成状态
const generating = ref(false)
const generationProgress = ref(0)
const progressInterval = ref(null)
const suggestingKnowledgePoints = ref(false)

// 预览对话框
const previewVisible = ref(false)
const previewData = ref(null)

// 选择课程
const selectCourse = (course) => {
  selectedCourse.value = course
  // 这里应该根据所选课程加载相应的考核内容
  
  // 模拟加载该课程下的知识点
  if (course.id === 1) {
    // 计算机网络的知识点
    knowledgePoints.value = [
      { id: 1, name: '网络基础概念' },
      { id: 2, name: 'OSI七层模型' },
      { id: 3, name: 'TCP/IP协议族' },
      { id: 4, name: '网络安全' },
      { id: 9, name: 'HTTP协议' },
      { id: 10, name: '网络编程基础' }
    ]
  } else if (course.id === 4) {
    // 嵌入式Linux的知识点
    knowledgePoints.value = [
      { id: 5, name: 'Linux系统基础' },
      { id: 6, name: 'Shell编程' },
      { id: 7, name: '嵌入式开发环境搭建' },
      { id: 8, name: '设备驱动开发' },
      { id: 11, name: '内核编程' },
      { id: 12, name: '嵌入式UI开发' }
    ]
  }
}

// 新建考核内容
const createAssessment = () => {
  formVisible.value = true
  form.title = ''
  form.type = 'practice'
  form.difficulty = 3
  form.questionCount = 10
  form.questionTypes = ['choice', 'multiple_choice']
  form.knowledgePoints = []
  form.referenceTeachingContent = null
  form.autoSuggestKnowledgePoints = true
  form.balanceDifficulty = true
  form.generateAnswers = true
  form.coverageRate = 80
}

// 智能推荐知识点
const suggestKnowledgePoints = () => {
  if (!form.referenceTeachingContent) {
    ElMessage.warning('请先选择参考教学内容')
    return
  }
  
  suggestingKnowledgePoints.value = true
  
  // 模拟推荐过程
  setTimeout(() => {
    // 根据所选教学内容推荐知识点
    if (selectedCourse.value.id === 1) {
      form.knowledgePoints = [1, 2, 3]
    } else if (selectedCourse.value.id === 4) {
      form.knowledgePoints = [5, 6, 7]
    }
    
    suggestingKnowledgePoints.value = false
    ElMessage.success('已智能推荐与教学内容相关的知识点')
  }, 1000)
}

// 获取课程相关的教学内容
const getCourseTeachingContents = (courseId) => {
  return teachingContents.value.filter(item => item.courseId === courseId)
}

// 提交表单
const submitForm = () => {
  if (!form.title) {
    ElMessage.warning('请输入考核内容标题')
    return
  }
  
  if (form.questionTypes.length === 0) {
    ElMessage.warning('请至少选择一种题型')
    return
  }
  
  if (form.knowledgePoints.length === 0) {
    ElMessage.warning('请至少选择一个知识点')
    return
  }
  
  // 开始生成
  generating.value = true
  generationProgress.value = 0
  
  // 模拟生成进度
  progressInterval.value = setInterval(() => {
    generationProgress.value += Math.floor(Math.random() * 8)
    if (generationProgress.value >= 100) {
      clearInterval(progressInterval.value)
      generationProgress.value = 100
      
      // 模拟生成完成后添加到列表
      setTimeout(() => {
        const newAssessment = {
          id: Date.now(),
          title: form.title,
          type: form.type,
          questionCount: form.questionCount,
          difficulty: form.difficulty,
          createTime: new Date().toISOString().split('T')[0]
        }
        
        assessmentList.value.unshift(newAssessment)
        generating.value = false
        formVisible.value = false
        
        // 生成后直接预览
        previewAssessment(newAssessment)
        
        ElMessage.success('考核内容生成成功')
      }, 500)
    }
  }, 200)
}

// 取消表单
const cancelForm = () => {
  formVisible.value = false
}

// 预览考核内容
const previewAssessment = (assessment) => {
  // 根据课程和难度生成模拟试题
  let mockQuestions = []
  
  if (selectedCourse.value.id === 1) { // 计算机网络课程
    mockQuestions = [
      {
        id: 1,
        type: 'choice',
        content: '以下哪项不是OSI七层模型中的层次？',
        options: ['应用层', '表示层', '网络层', '连接层'],
        answer: 3,
        difficulty: 2,
        knowledgePoint: '网络基础概念'
      },
      {
        id: 2,
        type: 'multiple_choice',
        content: '以下哪些协议属于TCP/IP协议族？',
        options: ['HTTP', 'FTP', 'SMTP', 'SNMP'],
        answer: [0, 1, 2],
        difficulty: 3,
        knowledgePoint: 'TCP/IP协议族'
      },
      {
        id: 3,
        type: 'fill_blank',
        content: 'IP地址分为____类。',
        answer: '5',
        difficulty: 1,
        knowledgePoint: '网络基础概念'
      },
      {
        id: 4,
        type: 'true_false',
        content: 'HTTP是一个无状态协议。',
        answer: true,
        difficulty: 2,
        knowledgePoint: 'HTTP协议'
      },
      {
        id: 5,
        type: 'short_answer',
        content: '简述TCP三次握手的过程及其意义。',
        answer: 'TCP三次握手是建立连接的过程，包括：1. 客户端发送SYN包；2. 服务器回应SYN-ACK包；3. 客户端发送ACK确认包。其意义在于确保双方都有发送和接收的能力，防止历史连接请求突然达到服务器导致的错误连接。',
        difficulty: 4,
        knowledgePoint: 'TCP/IP协议族'
      }
    ]
  } else if (selectedCourse.value.id === 4) { // 嵌入式Linux课程
    mockQuestions = [
      {
        id: 1,
        type: 'choice',
        content: '以下哪个命令用于查看Linux系统进程？',
        options: ['ls', 'ps', 'top', 'find'],
        answer: 1,
        difficulty: 2,
        knowledgePoint: 'Linux系统基础'
      },
      {
        id: 2,
        type: 'multiple_choice',
        content: '以下哪些是Linux常用的Shell？',
        options: ['Bash', 'Zsh', 'Fish', 'CMD'],
        answer: [0, 1, 2],
        difficulty: 2,
        knowledgePoint: 'Shell编程'
      },
      {
        id: 3,
        type: 'fill_blank',
        content: 'Linux内核模块的文件扩展名是____。',
        answer: '.ko',
        difficulty: 3,
        knowledgePoint: '设备驱动开发'
      },
      {
        id: 4,
        type: 'programming',
        content: '编写一个简单的Shell脚本，用于统计指定目录中的文件数量。',
        answer: '#!/bin/bash\n\nif [ $# -ne 1 ]; then\n  echo "Usage: $0 directory"\n  exit 1\nfi\n\nif [ ! -d "$1" ]; then\n  echo "Error: $1 is not a directory"\n  exit 2\nfi\n\ncount=$(ls -l "$1" | grep ^- | wc -l)\necho "The directory $1 contains $count files."',
        difficulty: 4,
        knowledgePoint: 'Shell编程'
      }
    ]
  }
  
  // 生成预览数据
  previewData.value = {
    ...assessment,
    questions: mockQuestions.slice(0, assessment.questionCount)
  }
  
  previewVisible.value = true
}

// 编辑考核内容
const editAssessment = (assessment) => {
  ElMessage.info(`编辑考核内容: ${assessment.title}`)
}

// 删除考核内容
const deleteAssessment = (assessment) => {
  ElMessageBox.confirm(
    `确定要删除考核内容"${assessment.title}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    assessmentList.value = assessmentList.value.filter(item => item.id !== assessment.id)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

// 下载考核内容
const downloadAssessment = (assessment) => {
  ElMessage.success(`下载考核内容: ${assessment.title}`)
}

// 关闭预览
const closePreview = () => {
  previewVisible.value = false
  previewData.value = null
}

// 重新生成考核内容
const regenerateAssessment = (assessment) => {
  ElMessage.info('正在重新生成考核内容...')
  previewVisible.value = false
  
  setTimeout(() => {
    previewAssessment(assessment)
  }, 1000)
}

// 考核类型文本映射
const typeMap = {
  practice: '练习',
  quiz: '测验',
  exam: '考试',
  homework: '作业'
}

// 难度等级映射
const difficultyMap = {
  1: '简单',
  2: '较简单',
  3: '中等',
  4: '较难',
  5: '困难'
}

// 题型图标映射
const questionTypeIconMap = {
  choice: 'el-icon-circle-check',
  multiple_choice: 'el-icon-finished',
  fill_blank: 'el-icon-edit',
  true_false: 'el-icon-select',
  short_answer: 'el-icon-notebook-2',
  programming: 'el-icon-terminal'
}
</script>

<template>
  <div class="assessment-container">
    <div class="assessment-header">
      <h2>考核内容生成系统</h2>
      <el-button type="primary" :icon="Plus" @click="createAssessment">新建考核内容</el-button>
    </div>
    
    <div class="assessment-main">
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
        <h3>{{ selectedCourse.name }} - 考核内容列表</h3>
        <el-empty v-if="assessmentList.length === 0" description="暂无考核内容，请点击右上角新建" />
        <el-table v-else :data="assessmentList" style="width: 100%">
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="type" label="类型" width="100">
            <template #default="scope">
              {{ typeMap[scope.row.type] }}
            </template>
          </el-table-column>
          <el-table-column prop="difficulty" label="难度" width="100">
            <template #default="scope">
              <el-rate
                v-model="scope.row.difficulty"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}"
              />
            </template>
          </el-table-column>
          <el-table-column prop="questionCount" label="题目数量" width="100" />
          <el-table-column prop="createTime" label="创建时间" width="120" />
          <el-table-column label="操作" width="220">
            <template #default="scope">
              <el-button :icon="View" size="small" @click="previewAssessment(scope.row)" />
              <el-button :icon="Edit" size="small" @click="editAssessment(scope.row)" />
              <el-button :icon="Download" size="small" type="primary" @click="downloadAssessment(scope.row)" />
              <el-button :icon="Delete" size="small" type="danger" @click="deleteAssessment(scope.row)" />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    
    <!-- 新建考核内容对话框 -->
    <el-dialog
      v-model="formVisible"
      title="新建考核内容"
      width="700px"
      :close-on-click-modal="false"
      :before-close="cancelForm"
    >
      <el-form :model="form" label-width="120px">
        <el-form-item label="课程">
          <el-input v-model="selectedCourse.name" disabled />
        </el-form-item>
        
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="请输入考核内容标题" />
        </el-form-item>
        
        <el-form-item label="考核类型">
          <el-select v-model="form.type" placeholder="请选择考核类型">
            <el-option
              v-for="(value, key) in typeMap"
              :key="key"
              :label="value"
              :value="key"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="难度等级">
          <el-slider
            v-model="form.difficulty"
            :min="1"
            :max="5"
            :format-tooltip="value => difficultyMap[value]"
            :marks="difficultyMap"
          />
        </el-form-item>
        
        <el-form-item label="题目数量">
          <el-input-number v-model="form.questionCount" :min="5" :max="100" />
        </el-form-item>
        
        <el-form-item label="题目类型" required>
          <el-checkbox-group v-model="form.questionTypes">
            <el-checkbox
              v-for="type in questionTypes"
              :key="type.value"
              :label="type.value"
            >
              {{ type.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        
        <el-form-item label="参考教学内容">
          <div class="reference-teaching">
            <el-select 
              v-model="form.referenceTeachingContent" 
              placeholder="选择教学内容以便智能推荐知识点"
              style="width: 80%;"
            >
              <el-option
                v-for="content in getCourseTeachingContents(selectedCourse.id)"
                :key="content.id"
                :label="content.title"
                :value="content.id"
              />
            </el-select>
            <el-button 
              :icon="Connection" 
              type="primary" 
              plain 
              :disabled="!form.referenceTeachingContent"
              :loading="suggestingKnowledgePoints"
              @click="suggestKnowledgePoints"
            >
              智能推荐
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="知识点" required>
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
        
        <el-form-item label="AI模型">
          <el-select v-model="form.aiModel" placeholder="请选择AI模型">
            <el-option
              v-for="model in aiModels"
              :key="model.value"
              :label="model.label"
              :value="model.value"
            />
          </el-select>
        </el-form-item>
        
        <el-divider content-position="left">高级设置</el-divider>
        
        <el-form-item label="知识点覆盖率">
          <el-slider 
            v-model="form.coverageRate" 
            :min="50" 
            :max="100" 
            :format-tooltip="value => `${value}%`"
            :marks="{50: '50%', 75: '75%', 100: '100%'}"
          />
        </el-form-item>
        
        <el-form-item label="其他设置">
          <div class="form-options">
            <el-checkbox v-model="form.balanceDifficulty">题目难度均衡分布</el-checkbox>
            <el-checkbox v-model="form.generateAnswers">生成详细答案解析</el-checkbox>
          </div>
        </el-form-item>
      </el-form>
      
      <div v-if="generating" class="generation-progress">
        <p>正在生成考核内容，请稍候...</p>
        <el-progress :percentage="generationProgress" />
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelForm" :disabled="generating">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="generating">
            {{ generating ? '生成中...' : '生成考核内容' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 预览考核内容对话框 -->
    <el-dialog
      v-model="previewVisible"
      :title="previewData?.title"
      width="800px"
      class="preview-dialog"
    >
      <template v-if="previewData">
        <div class="preview-header">
          <div>
            <span class="label">类型：</span>
            <el-tag>{{ typeMap[previewData.type] }}</el-tag>
          </div>
          <div>
            <span class="label">难度：</span>
            <el-rate
              v-model="previewData.difficulty"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}"
            />
          </div>
          <div>
            <span class="label">题目数量：</span>
            <span>{{ previewData.questionCount }}</span>
          </div>
        </div>
        
        <div class="questions-list">
          <div 
            v-for="(question, index) in previewData.questions" 
            :key="question.id"
            class="question-item"
          >
            <div class="question-header">
              <div class="question-type">
                <el-tag size="small" effect="plain">{{ questionTypes.find(t => t.value === question.type)?.label }}</el-tag>
              </div>
              <div class="question-info">
                <el-tag size="small" type="info" effect="plain">{{ question.knowledgePoint }}</el-tag>
                <el-rate
                  v-model="question.difficulty"
                  disabled
                  size="small"
                  text-color="#ff9900"
                />
              </div>
            </div>
            
            <div class="question-content">
              <div class="question-text">{{ index + 1 }}. {{ question.content }}</div>
              
              <!-- 选择题选项 -->
              <div v-if="question.type === 'choice' || question.type === 'multiple_choice'" class="options">
                <div
                  v-for="(option, optIndex) in question.options"
                  :key="optIndex"
                  class="option"
                >
                  <el-radio
                    v-if="question.type === 'choice'"
                    :model-value="question.answer"
                    :label="optIndex"
                    disabled
                  >
                    {{ ['A', 'B', 'C', 'D'][optIndex] }}. {{ option }}
                  </el-radio>
                  
                  <el-checkbox
                    v-else
                    :model-value="question.answer.includes(optIndex)"
                    disabled
                  >
                    {{ ['A', 'B', 'C', 'D'][optIndex] }}. {{ option }}
                  </el-checkbox>
                </div>
              </div>
              
              <!-- 判断题 -->
              <div v-else-if="question.type === 'true_false'" class="options">
                <el-radio
                  :model-value="question.answer"
                  :label="true"
                  disabled
                >
                  正确
                </el-radio>
                
                <el-radio
                  :model-value="question.answer"
                  :label="false"
                  disabled
                >
                  错误
                </el-radio>
              </div>
              
              <!-- 填空题答案 -->
              <div v-else-if="question.type === 'fill_blank'" class="answer">
                <div class="answer-label">答案：</div>
                <div class="answer-content">{{ question.answer }}</div>
              </div>
              
              <!-- 简答题答案 -->
              <div v-else-if="question.type === 'short_answer'" class="answer">
                <div class="answer-label">参考答案：</div>
                <div class="answer-content">{{ question.answer }}</div>
              </div>
              
              <!-- 编程题答案 -->
              <div v-else-if="question.type === 'programming'" class="answer">
                <div class="answer-label">参考答案：</div>
                <pre class="code-block">{{ question.answer }}</pre>
              </div>
            </div>
          </div>
        </div>
      </template>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closePreview">关闭</el-button>
          <el-button :icon="Download" type="primary">导出考核内容</el-button>
          <el-button :icon="Refresh" @click="regenerateAssessment(previewData)">重新生成</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.assessment-container {
  padding: 20px;
  height: 100%;
}

.assessment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.assessment-main {
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

.reference-teaching {
  display: flex;
  gap: 10px;
}

.form-options {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

/* 预览对话框样式 */
.preview-header {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.label {
  color: #606266;
  margin-right: 5px;
}

.questions-list {
  max-height: 500px;
  overflow-y: auto;
  padding-right: 10px;
}

.question-item {
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #ebeef5;
}

.question-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.question-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.question-content {
  margin-top: 10px;
}

.question-text {
  font-weight: bold;
  margin-bottom: 10px;
}

.options {
  padding-left: 20px;
}

.option {
  margin-bottom: 8px;
}

.answer {
  margin-top: 10px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.answer-label {
  font-weight: bold;
  margin-bottom: 5px;
  color: #409eff;
}

.answer-content {
  white-space: pre-line;
}

.code-block {
  padding: 10px;
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  font-family: monospace;
  white-space: pre;
  overflow-x: auto;
}
</style> 