<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElLoading } from 'element-plus';
import { Connection, QuestionFilled, ChatRound, Timer, DocumentCopy } from '@element-plus/icons-vue';

const activeTab = ref('ask');

// 学生已选课程数据
const studentCourses = ref([
  {
    id: 1,
    title: '计算机网络',
    chapters: [
      { id: 101, title: '计算机网络概述' },
      { id: 102, title: 'OSI七层模型' },
      { id: 103, title: 'TCP/IP协议' },
      { id: 104, title: '网络安全' }
    ]
  },
  {
    id: 2,
    title: '操作系统',
    chapters: [
      { id: 201, title: '操作系统概述' },
      { id: 202, title: '进程管理' },
      { id: 203, title: '内存管理' },
      { id: 204, title: '文件系统' }
    ]
  },
  {
    id: 3,
    title: '高等数学',
    chapters: [
      { id: 301, title: '极限与连续' },
      { id: 302, title: '导数与微分' },
      { id: 303, title: '定积分' },
      { id: 304, title: '微分方程' }
    ]
  }
]);

// 提问表单
const questionForm = reactive({
  title: '',
  content: '',
  courseId: '',
  chapterId: '',
  attachments: [],
  useAI: true, // 是否使用AI快速回答
  relatedKnowledgePoints: [] // 关联知识点
});

// 问题分类选项
const categories = [
  { label: '课程内容', value: 'course' },
  { label: '作业问题', value: 'homework' },
  { label: '考试相关', value: 'exam' },
  { label: '实践操作', value: 'practice' },
  { label: '其他', value: 'other' }
];

// 历史提问列表
const historyQuestions = ref([
  {
    id: 1,
    title: '关于计算机网络OSI模型的问题',
    content: '请问OSI七层模型中的表示层具体有哪些功能？它与应用层的区别是什么？',
    courseId: 1,
    chapterId: 102,
    courseName: '计算机网络',
    chapterName: 'OSI七层模型',
    createTime: '2023-09-15 14:30',
    status: 'answered',
    answerCount: 2,
    knowledgePoints: ['OSI七层模型', '网络基础'],
    answers: [
      {
        id: 101,
        content: '表示层主要负责数据格式转换、加密解密、压缩解压缩等功能，确保不同系统的应用层可以正确理解交换数据的语法语义。而应用层则直接面向用户，提供各种网络服务和应用程序接口。',
        author: '张明',
        role: 'teacher',
        time: '2023-09-15 15:45'
      },
      {
        id: 102,
        content: '补充一点，表示层处理的是信息的语法语义以及它们的关联，而应用层则关注于应用程序的逻辑。',
        author: 'AI助手',
        role: 'ai',
        time: '2023-09-15 16:00'
      }
    ]
  },
  {
    id: 2,
    title: '操作系统实验遇到的困难',
    content: '在进行进程调度实验时，我的代码出现了死锁问题，请问如何解决？\n\n我的代码如下：\n```c\nwhile(1) {\n  wait(mutex);\n  wait(resource);\n  // 使用资源\n  signal(resource);\n  signal(mutex);\n}\n```',
    courseId: 2,
    chapterId: 202,
    courseName: '操作系统',
    chapterName: '进程管理',
    createTime: '2023-09-18 10:15',
    status: 'pending',
    answerCount: 0,
    knowledgePoints: ['操作系统', '进程调度', '死锁'],
    answers: []
  },
  {
    id: 3,
    title: '关于期中考试范围的咨询',
    content: '请问高等数学期中考试是否包括泰勒展开的内容？',
    courseId: 3,
    chapterId: 302,
    courseName: '高等数学',
    chapterName: '导数与微分',
    createTime: '2023-09-20 16:45',
    status: 'answered',
    answerCount: 1,
    knowledgePoints: ['高等数学', '考试范围'],
    answers: [
      {
        id: 301,
        content: '期中考试范围包括教材第1-6章的内容，泰勒展开在第7章，所以不会包含在期中考试范围内。',
        author: '高等数学教研组',
        role: 'teacher',
        time: '2023-09-20 17:30'
      }
    ]
  }
]);

// 上传文件列表
const fileList = ref([]);

// 问题详情对话框
const detailDialogVisible = ref(false);
const currentQuestion = ref(null);
const replyContent = ref('');

// AI回答相关
const aiAnswering = ref(false);
const aiAnswer = ref('');
const knowledgePointSuggestions = ref([]);
const showKnowledgePointSuggestions = ref(false);

// AI模型选项
const aiModels = [
  { label: '通义千问', value: 'qwen-turto' },
  { label: 'GPT-4', value: 'gpt-4' },
  { label: 'Llama 3', value: 'llama-3' }
];

// 当前选择的AI模型
const selectedAIModel = ref('qwen-turto');

// 知识点列表
const knowledgePoints = ref([
  { id: 1, name: 'OSI七层模型' },
  { id: 2, name: '网络基础' },
  { id: 3, name: 'TCP/IP协议' },
  { id: 4, name: '网络安全' },
  { id: 5, name: '操作系统' },
  { id: 6, name: '进程调度' },
  { id: 7, name: '死锁' },
  { id: 8, name: '内存管理' },
  { id: 9, name: '数据结构' },
  { id: 10, name: '算法设计' },
  { id: 11, name: 'Linux基础' },
  { id: 12, name: 'Shell编程' }
]);

// 获取课程章节
const chapters = computed(() => {
  if (!questionForm.courseId) return [];
  const selectedCourse = studentCourses.value.find(c => c.id == questionForm.courseId);
  return selectedCourse ? selectedCourse.chapters : [];
});

// 检测用户输入并推荐相关知识点
const detectKnowledgePoints = () => {
  if (!questionForm.content || questionForm.content.length < 5) {
    knowledgePointSuggestions.value = [];
    showKnowledgePointSuggestions.value = false;
    return;
  }
  
  showKnowledgePointSuggestions.value = true;
  
  // 模拟基于内容的知识点匹配
  const content = questionForm.content.toLowerCase();
  knowledgePointSuggestions.value = knowledgePoints.value
    .filter(kp => content.includes(kp.name.toLowerCase()))
    .map(kp => kp.id);
    
  // 如果没有匹配到任何知识点，根据课程章节推荐一些相关的
  if (knowledgePointSuggestions.value.length === 0 && questionForm.courseId) {
    const courseId = parseInt(questionForm.courseId);
    if (courseId === 1) {
      knowledgePointSuggestions.value = [1, 2, 3];
    } else if (courseId === 2) {
      knowledgePointSuggestions.value = [5, 6, 7];
    } else if (courseId === 3) {
      knowledgePointSuggestions.value = [9, 10];
    }
  }
};

// 应用推荐的知识点
const applyKnowledgePointSuggestions = () => {
  questionForm.relatedKnowledgePoints = [...knowledgePointSuggestions.value];
  showKnowledgePointSuggestions.value = false;
  ElMessage.success('已应用推荐的知识点');
};

// 获取AI回答
const getAIAnswer = () => {
  if (!questionForm.content) {
    ElMessage.warning('请先输入问题内容');
    return;
  }
  
  aiAnswering.value = true;
  aiAnswer.value = '';
  
  // 模拟AI请求过程
  const loading = ElLoading.service({
    lock: true,
    text: '正在生成回答...',
    background: 'rgba(255, 255, 255, 0.8)',
  });
  
  // 模拟延迟
  setTimeout(() => {
    // 根据问题内容模拟生成回答
    if (questionForm.content.includes('OSI')) {
      aiAnswer.value = `OSI模型（开放系统互连模型）分为七层：物理层、数据链路层、网络层、传输层、会话层、表示层和应用层。其中表示层负责数据格式转换、加密解密、压缩解压缩等功能，确保不同系统可以正确理解交换数据的语法语义。应用层直接面向用户，提供各种网络服务接口。`;
    } else if (questionForm.content.includes('死锁')) {
      aiAnswer.value = `你的代码出现死锁是因为资源请求顺序问题。在第一个线程执行到wait(mutex)后wait(resource)前被切换，第二个线程也执行相同操作，这样两个线程互相等待对方释放资源，形成死锁。\n\n解决方法是保持资源请求的一致顺序：\n\`\`\`c\nwhile(1) {\n  wait(mutex);\n  wait(resource);\n  // 使用资源\n  signal(resource);\n  signal(mutex);\n}\n\`\`\`\n\n或者使用更高级的同步机制如信号量、条件变量等。`;
    } else if (questionForm.content.includes('泰勒')) {
      aiAnswer.value = `根据一般教学安排，高等数学期中考试通常覆盖教材前半部分内容。泰勒展开通常在后半学期讲授，因此很可能不会包含在期中考试范围内。不过建议你向任课教师确认具体考试范围，以免遗漏重要内容。`;
    } else {
      // 根据所选课程和章节生成回答
      const selectedCourse = studentCourses.value.find(c => c.id == questionForm.courseId);
      const selectedChapter = selectedCourse?.chapters.find(ch => ch.id == questionForm.chapterId);
      
      aiAnswer.value = `感谢你的提问。你的问题涉及到${selectedCourse?.title || '课程内容'}中的${selectedChapter?.title || '相关章节'}知识。\n\n为了更准确地回答你的问题，我建议补充一些具体细节，例如你遇到的具体情况、已经尝试的方法等。这样我可以提供更有针对性的帮助。\n\n如果你有代码示例或错误信息，也可以提供给我参考。`;
    }
    
    loading.close();
    aiAnswering.value = false;
    
    // 同时推荐知识点
    detectKnowledgePoints();
  }, 2000);
};

// 提交问题
const submitQuestion = () => {
  if (!questionForm.title || !questionForm.content || !questionForm.courseId || !questionForm.chapterId) {
    ElMessage.warning('请完整填写问题信息');
    return;
  }
  
  // 模拟提交成功
  ElMessage.success('问题提交成功，教师将尽快回复');
  
  // 如果有AI回答，添加到回答列表
  let answers = [];
  if (questionForm.useAI && aiAnswer.value) {
    answers = [{
      id: Date.now(),
      content: aiAnswer.value,
      author: 'AI助手',
      role: 'ai',
      time: new Date().toLocaleString()
    }];
  }
  
  // 获取课程和章节名称
  const selectedCourse = studentCourses.value.find(c => c.id == questionForm.courseId);
  const selectedChapter = selectedCourse?.chapters.find(ch => ch.id == questionForm.chapterId);
  
  // 添加到历史问题列表
  historyQuestions.value.unshift({
    id: Date.now(),
    title: questionForm.title,
    content: questionForm.content,
    courseId: questionForm.courseId,
    chapterId: questionForm.chapterId,
    courseName: selectedCourse?.title || '',
    chapterName: selectedChapter?.title || '',
    createTime: new Date().toLocaleString(),
    status: answers.length > 0 ? 'answered' : 'pending',
    answerCount: answers.length,
    knowledgePoints: questionForm.relatedKnowledgePoints.map(
      id => knowledgePoints.value.find(kp => kp.id === id)?.name || ''
    ).filter(Boolean),
    answers: answers
  });
  
  // 重置表单
  questionForm.title = '';
  questionForm.content = '';
  questionForm.chapterId = '';
  questionForm.relatedKnowledgePoints = [];
  aiAnswer.value = '';
  fileList.value = [];
  
  // 切换到历史记录tab
  activeTab.value = 'history';
};

// 查看问题详情
const viewQuestion = (question) => {
  currentQuestion.value = question;
  detailDialogVisible.value = true;
};

// 提交回复
const submitReply = () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容');
    return;
  }
  
  // 添加回复
  currentQuestion.value.answers.push({
    id: Date.now(),
    content: replyContent.value,
    author: localStorage.getItem('userRealName') || localStorage.getItem('userName') || '学生',
    role: 'student',
    time: new Date().toLocaleString()
  });
  
  // 更新回复数
  currentQuestion.value.answerCount = currentQuestion.value.answers.length;
  
  ElMessage.success('回复已提交');
  replyContent.value = '';
};

// 复制AI回答到问题内容
const copyAIAnswerToContent = () => {
  questionForm.content += '\n\n参考AI回答：\n' + aiAnswer.value;
  ElMessage.success('已复制AI回答到问题内容');
};

// 状态映射
const statusMap = {
  pending: { text: '等待回复', type: 'warning' },
  answered: { text: '已回复', type: 'success' },
  closed: { text: '已关闭', type: 'info' }
};

// 格式化问题内容
const formatContent = (content) => {
  return content.replace(/```([^`]+)```/g, '<pre><code>$1</code></pre>');
};

// 过滤历史问题
const searchKeyword = ref('');
const selectedFilterCourse = ref('');

const filteredQuestions = computed(() => {
  let filtered = historyQuestions.value;
  
  // 按课程筛选
  if (selectedFilterCourse.value) {
    filtered = filtered.filter(q => q.courseId == selectedFilterCourse.value);
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    filtered = filtered.filter(q => 
      q.title.toLowerCase().includes(keyword) || 
      q.content.toLowerCase().includes(keyword) ||
      q.knowledgePoints.some(kp => kp.toLowerCase().includes(keyword))
    );
  }
  
  return filtered;
});
</script>

<template>
  <div class="questions-container">
    <div class="questions-header">
      <h2>在线提问系统</h2>
      <div class="tabs">
        <div 
          class="tab" 
          :class="{ active: activeTab === 'ask' }" 
          @click="activeTab = 'ask'"
        >
          提出问题
        </div>
        <div 
          class="tab" 
          :class="{ active: activeTab === 'history' }" 
          @click="activeTab = 'history'"
        >
          历史提问
        </div>
      </div>
    </div>
    
    <div class="questions-main">
      <!-- 提出问题表单 -->
      <div v-if="activeTab === 'ask'" class="question-form">
        <el-form :model="questionForm" label-width="80px">
          <el-form-item label="课程" required>
            <el-select v-model="questionForm.courseId" placeholder="请选择课程" style="width: 100%">
              <el-option
                v-for="course in studentCourses"
                :key="course.id"
                :label="course.title"
                :value="course.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="章节" required>
            <el-select 
              v-model="questionForm.chapterId" 
              placeholder="请选择章节" 
              style="width: 100%"
              :disabled="!questionForm.courseId"
            >
              <el-option
                v-for="chapter in chapters"
                :key="chapter.id"
                :label="chapter.title"
                :value="chapter.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="标题" required>
            <el-input v-model="questionForm.title" placeholder="请输入问题标题" />
          </el-form-item>
          
          <el-form-item label="内容" required>
            <el-input
              v-model="questionForm.content"
              type="textarea"
              rows="6"
              placeholder="请详细描述您的问题..."
              @input="detectKnowledgePoints"
            />
          </el-form-item>
          
          <el-form-item label="知识点">
            <div class="knowledge-points-selector">
              <el-select
                v-model="questionForm.relatedKnowledgePoints"
                multiple
                placeholder="选择相关知识点"
                style="width: 100%"
              >
                <el-option
                  v-for="point in knowledgePoints"
                  :key="point.id"
                  :label="point.name"
                  :value="point.id"
                />
              </el-select>
              <div v-if="showKnowledgePointSuggestions && knowledgePointSuggestions.length > 0" class="knowledge-suggestions">
                <div class="suggestion-header">
                  <span>检测到相关知识点:</span>
                  <el-button size="small" type="primary" plain @click="applyKnowledgePointSuggestions">应用建议</el-button>
                </div>
                <div class="suggestion-tags">
                  <el-tag 
                    v-for="id in knowledgePointSuggestions" 
                    :key="id" 
                    size="small"
                  >
                    {{ knowledgePoints.find(kp => kp.id === id)?.name }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-form-item>
          
          <el-form-item label="附件">
            <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :file-list="fileList"
            >
              <el-icon><plus /></el-icon>
            </el-upload>
          </el-form-item>
          
          <el-form-item>
            <el-divider content-position="left">AI快速回答</el-divider>
            
            <div class="ai-answer-section">
              <div class="ai-header">
                <el-checkbox v-model="questionForm.useAI">使用AI预先回答</el-checkbox>
                <el-button 
                  type="primary" 
                  :icon="Connection" 
                  size="small" 
                  @click="getAIAnswer"
                  :loading="aiAnswering"
                >获取AI回答</el-button>
              </div>
              
              <div v-if="aiAnswer" class="ai-answer-result">
                <div class="ai-answer-header">
                  <span class="ai-title">AI回答预览</span>
                  <el-button 
                    type="primary" 
                    :icon="DocumentCopy" 
                    size="small" 
                    plain
                    @click="copyAIAnswerToContent"
                  >复制到内容</el-button>
                </div>
                <div class="ai-answer-content">
                  <pre>{{ aiAnswer }}</pre>
                </div>
              </div>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="submitQuestion">提交问题</el-button>
            <el-button @click="activeTab = 'history'">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 历史提问列表 -->
      <div v-if="activeTab === 'history'" class="question-history">
        <div class="history-header">
          <el-button type="primary" @click="activeTab = 'ask'">新建提问</el-button>
          <div class="history-filters">
            <el-select v-model="selectedFilterCourse" placeholder="按课程筛选" clearable style="width: 150px">
              <el-option
                v-for="course in studentCourses"
                :key="course.id"
                :label="course.title"
                :value="course.id"
              />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索问题..."
              prefix-icon="el-icon-search"
              clearable
              style="width: 200px;"
            />
          </div>
        </div>
        
        <el-empty v-if="filteredQuestions.length === 0" description="暂无提问记录" />
        
        <div v-else class="history-list">
          <div 
            v-for="question in filteredQuestions" 
            :key="question.id" 
            class="question-item"
            @click="viewQuestion(question)"
          >
            <div class="question-header">
              <div class="question-title">{{ question.title }}</div>
              <div class="question-meta">
                <el-tag 
                  :type="statusMap[question.status].type" 
                  size="small"
                >
                  {{ statusMap[question.status].text }}
                </el-tag>
              </div>
            </div>
            
            <div class="question-course">
              <span>{{ question.courseName }} - {{ question.chapterName }}</span>
            </div>
            
            <div class="question-brief">{{ question.content.substring(0, 100) + (question.content.length > 100 ? '...' : '') }}</div>
            
            <div class="question-footer">
              <div class="knowledge-tags">
                <el-tag 
                  v-for="(point, index) in question.knowledgePoints" 
                  :key="index"
                  size="small" 
                  effect="plain"
                >
                  {{ point }}
                </el-tag>
              </div>
              
              <div class="question-info">
                <span class="time">
                  <el-icon><Timer /></el-icon>
                  {{ question.createTime }}
                </span>
                <span class="answers">
                  <el-icon><ChatRound /></el-icon>
                  {{ question.answerCount }} 回复
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 问题详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentQuestion?.title"
      width="800px"
    >
      <template v-if="currentQuestion">
        <div class="question-detail">
          <div class="meta-info">
            <div class="meta-item">
              <span class="label">课程-章节：</span>
              <el-tag size="small" type="success">{{ currentQuestion.courseName }} - {{ currentQuestion.chapterName }}</el-tag>
            </div>
            <div class="meta-item">
              <span class="label">状态：</span>
              <el-tag 
                :type="statusMap[currentQuestion.status].type" 
                size="small"
              >
                {{ statusMap[currentQuestion.status].text }}
              </el-tag>
            </div>
            <div class="meta-item">
              <span class="label">时间：</span>
              <span>{{ currentQuestion.createTime }}</span>
            </div>
          </div>
          
          <div v-if="currentQuestion.knowledgePoints && currentQuestion.knowledgePoints.length > 0" class="knowledge-tags detail-tags">
            <span class="label">相关知识点：</span>
            <el-tag 
              v-for="(point, index) in currentQuestion.knowledgePoints" 
              :key="index"
              size="small" 
              effect="plain"
            >
              {{ point }}
            </el-tag>
          </div>
          
          <div class="question-content" v-html="formatContent(currentQuestion.content)"></div>
          
          <el-divider>回复列表 ({{ currentQuestion.answers.length }})</el-divider>
          
          <div v-if="currentQuestion.answers.length === 0" class="no-answer">
            暂无回复
          </div>
          
          <div v-else class="answer-list">
            <div 
              v-for="answer in currentQuestion.answers" 
              :key="answer.id" 
              class="answer-item"
              :class="{ 'ai-answer': answer.role === 'ai', 'teacher-answer': answer.role === 'teacher' }"
            >
              <div class="answer-header">
                <div class="author">
                  <span class="name">{{ answer.author }}</span>
                  <el-tag size="small" effect="plain" :type="answer.role === 'ai' ? 'info' : answer.role === 'teacher' ? 'success' : ''">
                    {{ answer.role === 'ai' ? 'AI助手' : answer.role === 'teacher' ? '教师' : '学生' }}
                  </el-tag>
                </div>
                <div class="time">{{ answer.time }}</div>
              </div>
              
              <div class="answer-content" v-html="formatContent(answer.content)"></div>
            </div>
          </div>
          
          <div class="reply-form">
            <el-divider>回复</el-divider>
            <el-input
              v-model="replyContent"
              type="textarea"
              rows="4"
              placeholder="请输入回复内容..."
            />
            <div class="reply-actions">
              <el-button type="primary" @click="submitReply">提交回复</el-button>
            </div>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.questions-container {
  padding: 20px;
  height: 100%;
}

.questions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.tabs {
  display: flex;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

.tab {
  padding: 10px 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.tab:hover {
  background-color: #e6e8eb;
}

.tab.active {
  background-color: #409eff;
  color: #fff;
  font-weight: bold;
}

.questions-main {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  height: calc(100% - 60px);
  overflow-y: auto;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.history-filters {
  display: flex;
  gap: 10px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.question-item {
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.question-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-color: #d9ecff;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.question-title {
  font-weight: bold;
  font-size: 16px;
}

.question-meta {
  display: flex;
  gap: 10px;
}

.question-course {
  color: #409eff;
  font-size: 14px;
  margin-bottom: 8px;
}

.question-brief {
  color: #606266;
  margin-bottom: 15px;
  white-space: pre-line;
  line-height: 1.5;
}

.question-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #909399;
  font-size: 13px;
}

.knowledge-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.question-info {
  display: flex;
  gap: 15px;
}

.question-info span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.question-detail {
  padding: 10px;
}

.meta-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
}

.label {
  margin-right: 5px;
  color: #606266;
}

.detail-tags {
  margin-bottom: 20px;
}

.question-content {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  white-space: pre-line;
  line-height: 1.6;
  margin-bottom: 20px;
}

.question-content pre {
  background-color: #f0f0f0;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}

.no-answer {
  text-align: center;
  color: #909399;
  padding: 20px;
}

.answer-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.answer-item {
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #fafafa;
}

.answer-item.teacher-answer {
  background-color: #f0f9eb;
  border-color: #e1f3d8;
}

.answer-item.ai-answer {
  background-color: #ecf5ff;
  border-color: #d9ecff;
}

.answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.name {
  font-weight: bold;
}

.answer-content {
  white-space: pre-line;
  line-height: 1.6;
}

.reply-form {
  margin-top: 20px;
}

.reply-actions {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.ai-answer-section {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
}

.ai-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.ai-answer-result {
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 15px;
}

.ai-answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.ai-title {
  font-weight: bold;
  color: #409eff;
}

.ai-answer-content {
  background-color: #fff;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  max-height: 200px;
  overflow-y: auto;
}

.ai-answer-content pre {
  white-space: pre-wrap;
  margin: 0;
  font-family: inherit;
}

.knowledge-points-selector {
  position: relative;
}

.knowledge-suggestions {
  margin-top: 10px;
  padding: 10px;
  background-color: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 4px;
}

.suggestion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.suggestion-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style> 