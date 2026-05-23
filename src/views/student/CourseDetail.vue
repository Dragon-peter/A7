<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { VideoPlay, Document, QuestionFilled, ChatLineRound, DocumentCopy, ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const courseId = ref(null)

// 当前选中的章节和视频
const currentChapter = ref(null)
const currentVideo = ref(null)

// 当前显示的视图（video, exercise, discussion）
const currentView = ref('video')

// 视频播放状态
const videoPlaying = ref(false)
const videoProgress = ref(0)
const videoTimer = ref(null)

// 课程详情
const courseDetail = ref({
  id: 1,
  title: '计算机网络',
  cover: 'https://img2.baidu.com/it/u=1395980100,2999804584&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500',
  teacher: '张明',
  description: '本课程介绍计算机网络的基本概念、体系结构和关键技术，包括物理层、数据链路层、网络层、传输层和应用层的详细讲解。通过理论学习和实践案例，帮助学生掌握网络协议分析、网络应用开发和网络安全基本技能。',
  chapters: [
    {
      id: 1,
      title: '第一章 计算机网络基础',
      description: '介绍计算机网络的基本概念和发展历史',
      videos: [
        { id: 101, title: '1.1 计算机网络概述', duration: 15, watched: true, progress: 100, url: 'https://www.example.com/video1.mp4' },
        { id: 102, title: '1.2 网络拓扑结构', duration: 20, watched: true, progress: 100, url: 'https://www.example.com/video2.mp4' }
      ],
      exercises: [
        { 
          id: 201, 
          title: '习题1.1 网络基础概念', 
          type: 'choice',
          content: '下列关于计算机网络特点的描述中，错误的是：',
          options: ['共享性是计算机网络的基本特征', '网络通信必须遵循共同的规则', '计算机网络一定是由通信线路连接的', '计算机网络可以连接不同类型的设备'],
          answer: 2,
          explanation: '计算机网络不一定是由通信线路连接的，现在也有很多无线网络技术。'
        },
        { 
          id: 202, 
          title: '习题1.2 网络拓扑结构设计', 
          type: 'text',
          content: '请简述星型拓扑结构的优缺点。',
          answer: '优点：结构简单清晰，易于管理和维护；某个节点故障不会影响其他节点。缺点：中心节点负担重，若中心节点故障则整个网络瘫痪。',
          keywords: ['中心节点', '管理', '故障', '瘫痪']
        }
      ],
      resources: [
        { id: 301, title: '计算机网络基础知识.pdf', type: 'pdf', size: '2.5MB', url: '#' },
        { id: 302, title: '网络拓扑结构图例.zip', type: 'zip', size: '5.1MB', url: '#' }
      ]
    },
    {
      id: 2,
      title: '第二章 网络协议',
      description: '详细讲解OSI七层模型和TCP/IP协议族',
      videos: [
        { id: 103, title: '2.1 OSI七层模型', duration: 25, watched: true, progress: 100, url: 'https://www.example.com/video3.mp4' },
        { id: 104, title: '2.2 TCP/IP协议族', duration: 30, watched: true, progress: 73, url: 'https://www.example.com/video4.mp4' },
        { id: 105, title: '2.3 IP地址与子网划分', duration: 28, watched: false, progress: 35, url: 'https://www.example.com/video5.mp4' }
      ],
      exercises: [
        { 
          id: 203, 
          title: '习题2.1 OSI模型与TCP/IP模型', 
          type: 'choice',
          content: '在OSI七层模型中，负责将比特流转换为帧的是哪一层？',
          options: ['物理层', '数据链路层', '网络层', '传输层'],
          answer: 1,
          explanation: '数据链路层负责将物理层的比特流组装成帧，并进行差错控制。'
        },
        { 
          id: 204, 
          title: '习题2.2 TCP/UDP协议分析', 
          type: 'multisect',
          content: '下列关于TCP协议特点的说法，正确的有：（多选）',
          options: ['面向连接', '可靠传输', '无连接', '传输效率高于UDP'],
          answer: [0, 1],
          explanation: 'TCP是面向连接的可靠传输协议，而UDP是无连接的不可靠传输协议。TCP的传输效率低于UDP。'
        },
        { 
          id: 205, 
          title: '习题2.3 IP地址计算', 
          type: 'text',
          content: '某C类IP地址192.168.1.0，子网掩码为255.255.255.224，请计算该子网的可用主机数量。',
          answer: '30台',
          keywords: ['30', '32-2', '2^5-2']
        }
      ],
      resources: [
        { id: 303, title: 'TCP/IP详解.pdf', type: 'pdf', size: '4.2MB', url: '#' },
        { id: 304, title: 'Wireshark抓包实验.doc', type: 'doc', size: '1.7MB', url: '#' }
      ]
    },
    {
      id: 3,
      title: '第三章 网络安全',
      description: '介绍网络安全威胁与防护技术',
      videos: [
        { id: 106, title: '3.1 网络攻击类型', duration: 22, watched: true, progress: 100, url: 'https://www.example.com/video6.mp4' },
        { id: 107, title: '3.2 加密算法', duration: 26, watched: false, progress: 19, url: 'https://www.example.com/video7.mp4' },
        { id: 108, title: '3.3 网络安全防护', duration: 24, watched: false, progress: 0, url: 'https://www.example.com/video8.mp4' }
      ],
      exercises: [
        { 
          id: 206, 
          title: '习题3.1 安全威胁分析', 
          type: 'choice',
          content: '下列哪种攻击类型是通过发送大量请求使服务器资源耗尽的？',
          options: ['钓鱼攻击', 'SQL注入', 'DDoS攻击', '跨站脚本攻击(XSS)'],
          answer: 2,
          explanation: 'DDoS(分布式拒绝服务)攻击是通过大量请求耗尽服务器资源，导致合法用户无法访问。'
        }
      ],
      resources: [
        { id: 305, title: '网络安全防护指南.pdf', type: 'pdf', size: '3.8MB', url: '#' }
      ]
    }
  ],
  discussionPosts: [
    { 
      id: 1, 
      author: '李同学', 
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      content: '老师，我对IP地址子网划分这部分内容有些不理解，能否再详细解释一下如何计算子网掩码？', 
      time: '2023-09-15 10:30',
      replies: [
        { 
          id: 101, 
          author: '张明', 
          avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
          content: '你可以这样理解：子网掩码实际上是用来确定IP地址中哪些位是网络ID，哪些位是主机ID。比如255.255.255.0就是前24位是网络ID，后8位是主机ID。',
          time: '2023-09-15 11:15'
        },
        { 
          id: 102, 
          author: '李同学', 
          avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
          content: '谢谢老师，我大概理解了。那如果是255.255.255.128这样的子网掩码，就是前25位是网络ID，后7位是主机ID，对吗？',
          time: '2023-09-15 13:40'
        }
      ]
    },
    { 
      id: 2, 
      author: '王同学', 
      avatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9cpng.png',
      content: '有没有同学知道第二章习题2.2中的TCP和UDP的区别？我有点混淆了。', 
      time: '2023-09-20 16:45',
      replies: [
        { 
          id: 103, 
          author: '赵同学', 
          avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
          content: 'TCP是面向连接的，提供可靠传输，有流量控制和拥塞控制，但传输效率相对较低。UDP是无连接的，传输不可靠但效率高。',
          time: '2023-09-20 17:30'
        }
      ]
    }
  ]
})

// 讨论区新帖子
const newPost = ref('')

// 当前选中习题
const currentExercise = ref(null)
const exerciseAnswer = ref('')

// 初始化
onMounted(() => {
  // 获取路由参数中的课程ID
  courseId.value = route.params.id || 1
  
  // 模拟加载课程数据
  // 实际应该从API获取数据
  
  // 默认选中第一个章节和第一个视频
  if (courseDetail.value.chapters.length > 0) {
    currentChapter.value = courseDetail.value.chapters[0]
    if (currentChapter.value.videos.length > 0) {
      currentVideo.value = currentChapter.value.videos[0]
    }
  }
})

// 计算视频总数和已观看数量
const videoStats = computed(() => {
  let total = 0
  let watched = 0
  
  courseDetail.value.chapters.forEach(chapter => {
    total += chapter.videos.length
    chapter.videos.forEach(video => {
      if (video.watched) watched++
    })
  })
  
  return { total, watched }
})

// 计算习题总数和已完成数量
const exerciseStats = computed(() => {
  let total = 0
  let completed = 0
  
  courseDetail.value.chapters.forEach(chapter => {
    if (chapter.exercises) {
      total += chapter.exercises.length
      // 这里需要后端提供习题完成状态
      // 目前简化处理
    }
  })
  
  return { total, completed }
})

// 选择章节
const selectChapter = (chapter) => {
  currentChapter.value = chapter
  if (chapter.videos.length > 0) {
    currentVideo.value = chapter.videos[0]
  }
}

// 选择视频
const selectVideo = (video) => {
  if (videoTimer.value) {
    clearInterval(videoTimer.value)
    videoTimer.value = null
  }
  
  videoPlaying.value = false
  videoProgress.value = video.progress
  currentVideo.value = video
}

// 播放/暂停视频
const toggleVideo = () => {
  videoPlaying.value = !videoPlaying.value
  
  if (videoPlaying.value) {
    // 模拟视频播放进度
    videoTimer.value = setInterval(() => {
      if (videoProgress.value < 100) {
        videoProgress.value += 1
        // 更新当前视频的进度
        if (currentVideo.value) {
          currentVideo.value.progress = videoProgress.value
          if (videoProgress.value >= 100) {
            currentVideo.value.watched = true
          }
        }
      } else {
        clearInterval(videoTimer.value)
        videoTimer.value = null
        videoPlaying.value = false
      }
    }, 300) // 每300ms更新一次，仅用于演示
  } else {
    if (videoTimer.value) {
      clearInterval(videoTimer.value)
      videoTimer.value = null
    }
  }
}

// 切换视图
const switchView = (view) => {
  currentView.value = view
  if (view === 'exercise' && currentChapter.value && currentChapter.value.exercises && currentChapter.value.exercises.length > 0) {
    currentExercise.value = currentChapter.value.exercises[0]
    exerciseAnswer.value = ''
  }
}

// 选择习题
const selectExercise = (exercise) => {
  currentExercise.value = exercise
  exerciseAnswer.value = ''
}

// 提交习题答案
const submitAnswer = () => {
  if (!currentExercise.value) return
  
  if (currentExercise.value.type === 'text') {
    // 文本题评估
    const keywords = currentExercise.value.keywords || []
    let matched = 0
    keywords.forEach(keyword => {
      if (exerciseAnswer.value.toLowerCase().includes(keyword.toLowerCase())) {
        matched++
      }
    })
    
    const accuracy = keywords.length > 0 ? matched / keywords.length : 0
    
    if (accuracy >= 0.7) {
      ElMessage.success('回答正确！')
    } else if (accuracy >= 0.3) {
      ElMessage.warning('回答部分正确，可以更完善。')
    } else {
      ElMessage.error('回答有误，请查看解析并重新作答。')
    }
  } else if (currentExercise.value.type === 'choice') {
    // 单选题评估
    if (parseInt(exerciseAnswer.value) === currentExercise.value.answer) {
      ElMessage.success('回答正确！')
    } else {
      ElMessage.error('回答错误，请查看解析并重新作答。')
    }
  } else if (currentExercise.value.type === 'multisect') {
    // 多选题评估
    const selectedAnswers = exerciseAnswer.value.split(',').map(Number)
    const correctAnswers = currentExercise.value.answer
    
    // 检查答案是否完全匹配
    const isCorrect = 
      selectedAnswers.length === correctAnswers.length && 
      selectedAnswers.every(answer => correctAnswers.includes(answer))
    
    if (isCorrect) {
      ElMessage.success('回答正确！')
    } else {
      ElMessage.error('回答错误，请查看解析并重新作答。')
    }
  }
}

// 发布讨论
const submitPost = () => {
  if (!newPost.value.trim()) {
    ElMessage.warning('请输入讨论内容')
    return
  }
  
  // 添加新帖子
  courseDetail.value.discussionPosts.unshift({
    id: Date.now(),
    author: '我',
    avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
    content: newPost.value,
    time: new Date().toLocaleString(),
    replies: []
  })
  
  ElMessage.success('发布成功')
  newPost.value = ''
}

// 返回课程列表
const backToCourseList = () => {
  router.push('/home/student/courses')
}
</script>

<template>
  <div class="course-detail-container">
    <!-- 课程头部信息 -->
    <div class="course-header">
      <div class="back-button" @click="backToCourseList">
        <el-button :icon="ArrowLeft" circle></el-button>
      </div>
      <div class="course-info">
        <div class="course-title">{{ courseDetail.title }}</div>
        <div class="course-meta">
          <span>教师: {{ courseDetail.teacher }}</span>
          <span class="separator">|</span>
          <span>视频: {{ videoStats.watched }}/{{ videoStats.total }}</span>
          <span class="separator">|</span>
          <span>习题: {{ exerciseStats.completed }}/{{ exerciseStats.total }}</span>
        </div>
        <div class="course-description">{{ courseDetail.description }}</div>
      </div>
    </div>
    
    <div class="course-content">
      <!-- 左侧章节导航 -->
      <div class="chapters-sidebar">
        <div class="sidebar-header">课程大纲</div>
        <div class="chapter-list">
          <div 
            v-for="chapter in courseDetail.chapters" 
            :key="chapter.id" 
            class="chapter-item"
            :class="{ active: currentChapter && chapter.id === currentChapter.id }"
            @click="selectChapter(chapter)"
          >
            <div class="chapter-title">{{ chapter.title }}</div>
            <div class="chapter-videos">
              <div 
                v-for="video in chapter.videos" 
                :key="video.id" 
                class="video-item"
                :class="{ active: currentVideo && video.id === currentVideo.id, 'is-watched': video.watched }"
                @click.stop="selectVideo(video)"
              >
                <el-icon><VideoPlay /></el-icon>
                <span>{{ video.title }}</span>
                <span class="video-duration">({{ video.duration }}分钟)</span>
                <el-progress 
                  class="video-progress" 
                  :percentage="video.progress" 
                  :stroke-width="3" 
                  :show-text="false"
                ></el-progress>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧内容区域 -->
      <div class="content-area">
        <div class="view-tabs">
          <div 
            class="view-tab" 
            :class="{ active: currentView === 'video' }"
            @click="switchView('video')"
          >
            <el-icon><VideoPlay /></el-icon> 视频
          </div>
          <div 
            class="view-tab" 
            :class="{ active: currentView === 'exercise' }"
            @click="switchView('exercise')"
          >
            <el-icon><QuestionFilled /></el-icon> 习题
          </div>
          <div 
            class="view-tab" 
            :class="{ active: currentView === 'discussion' }"
            @click="switchView('discussion')"
          >
            <el-icon><ChatLineRound /></el-icon> 讨论区
          </div>
          <div 
            class="view-tab" 
            :class="{ active: currentView === 'resources' }"
            @click="switchView('resources')"
          >
            <el-icon><DocumentCopy /></el-icon> 资源
          </div>
        </div>
        
        <!-- 视频播放区域 -->
        <div v-if="currentView === 'video'" class="video-container">
          <div v-if="currentVideo" class="video-player">
            <div class="video-placeholder">
              <div class="video-title">{{ currentVideo.title }}</div>
              <div class="video-controls">
                <el-button 
                  type="primary" 
                  circle 
                  @click="toggleVideo"
                  :icon="videoPlaying ? 'Pause' : 'VideoPlay'"
                ></el-button>
              </div>
              <div class="video-progress-bar">
                <el-progress 
                  :percentage="videoProgress" 
                  :format="() => `${videoProgress}%`"
                  :stroke-width="10"
                ></el-progress>
              </div>
            </div>
          </div>
          <div v-else class="no-content">
            请选择一个视频进行播放
          </div>
        </div>
        
        <!-- 习题区域 -->
        <div v-else-if="currentView === 'exercise'" class="exercise-container">
          <div class="exercise-sidebar">
            <div v-if="currentChapter && currentChapter.exercises">
              <div 
                v-for="exercise in currentChapter.exercises" 
                :key="exercise.id" 
                class="exercise-item"
                :class="{ active: currentExercise && exercise.id === currentExercise.id }"
                @click="selectExercise(exercise)"
              >
                {{ exercise.title }}
              </div>
            </div>
            <div v-else class="no-content">
              当前章节暂无习题
            </div>
          </div>
          
          <div class="exercise-content">
            <div v-if="currentExercise" class="exercise-detail">
              <div class="exercise-question">{{ currentExercise.content }}</div>
              
              <!-- 选择题 -->
              <div v-if="currentExercise.type === 'choice'" class="exercise-options">
                <el-radio-group v-model="exerciseAnswer">
                  <div 
                    v-for="(option, index) in currentExercise.options" 
                    :key="index" 
                    class="option-item"
                  >
                    <el-radio :label="index.toString()">{{ option }}</el-radio>
                  </div>
                </el-radio-group>
              </div>
              
              <!-- 多选题 -->
              <div v-else-if="currentExercise.type === 'multisect'" class="exercise-options">
                <el-checkbox-group v-model="exerciseAnswer">
                  <div 
                    v-for="(option, index) in currentExercise.options" 
                    :key="index" 
                    class="option-item"
                  >
                    <el-checkbox :label="index">{{ option }}</el-checkbox>
                  </div>
                </el-checkbox-group>
              </div>
              
              <!-- 文本题 -->
              <div v-else-if="currentExercise.type === 'text'" class="exercise-text">
                <el-input
                  v-model="exerciseAnswer"
                  type="textarea"
                  rows="6"
                  placeholder="请输入您的答案"
                ></el-input>
              </div>
              
              <div class="exercise-submit">
                <el-button type="primary" @click="submitAnswer">提交答案</el-button>
              </div>
            </div>
            <div v-else class="no-content">
              请选择一道习题
            </div>
          </div>
        </div>
        
        <!-- 讨论区域 -->
        <div v-else-if="currentView === 'discussion'" class="discussion-container">
          <div class="post-form">
            <el-input
              v-model="newPost"
              type="textarea"
              rows="4"
              placeholder="发表您的讨论、问题或见解..."
            ></el-input>
            <div class="post-actions">
              <el-button type="primary" @click="submitPost">发布</el-button>
            </div>
          </div>
          
          <div class="discussion-list">
            <div v-for="post in courseDetail.discussionPosts" :key="post.id" class="discussion-post">
              <div class="post-header">
                <div class="post-author">
                  <el-avatar :size="36" :src="post.avatar"></el-avatar>
                  <span>{{ post.author }}</span>
                </div>
                <div class="post-time">{{ post.time }}</div>
              </div>
              <div class="post-content">{{ post.content }}</div>
              
              <!-- 回复列表 -->
              <div class="post-replies">
                <div v-for="reply in post.replies" :key="reply.id" class="reply-item">
                  <div class="reply-header">
                    <div class="reply-author">
                      <el-avatar :size="28" :src="reply.avatar"></el-avatar>
                      <span>{{ reply.author }}</span>
                    </div>
                    <div class="reply-time">{{ reply.time }}</div>
                  </div>
                  <div class="reply-content">{{ reply.content }}</div>
                </div>
              </div>
              
              <div class="reply-form">
                <el-input placeholder="回复此讨论..."></el-input>
                <el-button size="small">回复</el-button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 资源区域 -->
        <div v-else-if="currentView === 'resources'" class="resources-container">
          <div v-if="currentChapter && currentChapter.resources && currentChapter.resources.length > 0">
            <div class="resource-list">
              <div v-for="resource in currentChapter.resources" :key="resource.id" class="resource-item">
                <el-icon><Document /></el-icon>
                <div class="resource-info">
                  <div class="resource-title">{{ resource.title }}</div>
                  <div class="resource-meta">
                    <span class="resource-type">{{ resource.type.toUpperCase() }}</span>
                    <span class="resource-size">{{ resource.size }}</span>
                  </div>
                </div>
                <el-button size="small" type="primary">下载</el-button>
              </div>
            </div>
          </div>
          <div v-else class="no-content">
            当前章节暂无资源
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.course-detail-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.course-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.back-button {
  margin-right: 15px;
  margin-top: 5px;
}

.course-info {
  flex: 1;
}

.course-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 10px;
}

.course-meta {
  color: #606266;
  margin-bottom: 15px;
}

.separator {
  margin: 0 10px;
  color: #dcdfe6;
}

.course-description {
  color: #606266;
  line-height: 1.6;
}

.course-content {
  display: flex;
  gap: 20px;
  height: calc(100vh - 200px);
}

.chapters-sidebar {
  width: 280px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 15px;
  font-weight: bold;
  border-bottom: 1px solid #ebeef5;
}

.chapter-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.chapter-item {
  margin-bottom: 15px;
  cursor: pointer;
}

.chapter-title {
  font-weight: bold;
  padding: 8px;
  border-radius: 4px;
}

.chapter-item.active .chapter-title {
  background-color: #ecf5ff;
  color: #409eff;
}

.chapter-videos {
  padding-left: 20px;
}

.video-item {
  display: flex;
  align-items: center;
  padding: 8px;
  font-size: 14px;
  border-radius: 4px;
  margin: 5px 0;
  position: relative;
}

.video-item:hover {
  background-color: #f5f7fa;
}

.video-item.active {
  background-color: #ecf5ff;
  color: #409eff;
}

.video-item.is-watched {
  color: #67c23a;
}

.video-item .el-icon {
  margin-right: 5px;
}

.video-duration {
  margin-left: 5px;
  color: #909399;
  font-size: 12px;
}

.video-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
}

.content-area {
  flex: 1;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.view-tabs {
  display: flex;
  border-bottom: 1px solid #ebeef5;
}

.view-tab {
  padding: 15px 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.view-tab.active {
  color: #409eff;
  border-bottom: 2px solid #409eff;
}

.video-container, .exercise-container, .discussion-container, .resources-container {
  flex: 1;
  overflow: auto;
  padding: 20px;
}

.video-player {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.video-placeholder {
  flex: 1;
  background-color: #000;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #fff;
  border-radius: 8px;
  position: relative;
}

.video-title {
  font-size: 20px;
  margin-bottom: 20px;
}

.video-controls {
  margin-bottom: 20px;
}

.video-progress-bar {
  width: 80%;
  position: absolute;
  bottom: 20px;
}

.no-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
}

.exercise-container {
  display: flex;
  gap: 20px;
}

.exercise-sidebar {
  width: 220px;
  border-right: 1px solid #ebeef5;
  padding-right: 15px;
  overflow-y: auto;
}

.exercise-item {
  padding: 10px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
}

.exercise-item:hover {
  background-color: #f5f7fa;
}

.exercise-item.active {
  background-color: #ecf5ff;
  color: #409eff;
}

.exercise-content {
  flex: 1;
  overflow-y: auto;
  padding-left: 15px;
}

.exercise-detail {
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.exercise-question {
  font-size: 16px;
  margin-bottom: 20px;
  line-height: 1.6;
}

.exercise-options {
  margin-bottom: 20px;
}

.option-item {
  margin-bottom: 10px;
}

.exercise-text {
  margin-bottom: 20px;
}

.exercise-submit {
  text-align: right;
}

.post-form {
  margin-bottom: 20px;
}

.post-actions {
  margin-top: 10px;
  text-align: right;
}

.discussion-post {
  border-bottom: 1px solid #ebeef5;
  padding: 15px 0;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.post-time {
  color: #909399;
  font-size: 12px;
}

.post-content {
  line-height: 1.6;
  margin-bottom: 15px;
}

.post-replies {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 10px;
}

.reply-item {
  padding: 10px 0;
  border-bottom: 1px dashed #ebeef5;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.reply-author {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
}

.reply-time {
  color: #909399;
  font-size: 12px;
}

.reply-content {
  line-height: 1.6;
  font-size: 14px;
}

.reply-form {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.resource-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.resource-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.resource-item:hover {
  background-color: #f5f7fa;
}

.resource-item .el-icon {
  font-size: 24px;
  margin-right: 15px;
  color: #909399;
}

.resource-info {
  flex: 1;
}

.resource-title {
  margin-bottom: 5px;
}

.resource-meta {
  display: flex;
  gap: 10px;
  font-size: 12px;
  color: #909399;
}

.resource-type {
  background-color: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
}
</style> 