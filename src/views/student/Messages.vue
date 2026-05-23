<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Message, Delete, Star, StarFilled, Edit, Send } from '@element-plus/icons-vue';
import * as echarts from 'echarts';

const activeTab = ref('feedback');

// 教师反馈列表
const feedbackMessages = ref([
  {
    id: 1,
    title: '计算机网络作业点评',
    content: '你的TCP/IP协议分析作业完成得很好，逻辑清晰，但需要注意一些网络安全方面的考量。建议你阅读《网络安全基础》第三章相关内容。',
    sender: '张明老师',
    course: '计算机网络',
    time: '2023-09-15 10:30',
    isRead: true,
    isStarred: true,
    replies: [
      {
        id: 101,
        content: '谢谢老师的建议，我会去阅读相关内容。请问有没有其他推荐的资料？',
        sender: '学生',
        time: '2023-09-15 14:20'
      },
      {
        id: 102,
        content: '你可以参考《网络安全实战指南》这本书，图书馆有电子版。',
        sender: '张明老师',
        time: '2023-09-15 16:05'
      }
    ]
  },
  {
    id: 2,
    title: '关于你最近提出的问题',
    content: '关于你提出的OSI七层模型的问题，我已经在课程资料区上传了详细的解释文档，请查看。如有进一步疑问，欢迎继续提问。',
    sender: '李华老师',
    course: '计算机网络',
    time: '2023-09-18 14:45',
    isRead: false,
    isStarred: false,
    replies: []
  },
  {
    id: 3,
    title: '高等数学期中考试评价',
    content: '你在期中考试中表现良好，特别是微积分部分得分很高。但是在级数展开部分还有提升空间，建议多做练习题。',
    sender: '王教授',
    course: '高等数学',
    time: '2023-09-20 16:30',
    isRead: true,
    isStarred: false,
    replies: []
  }
]);

// 系统通知列表
const systemNotices = ref([
  {
    id: 101,
    title: '新课程资料已上传',
    content: '《计算机网络》课程已更新本周学习资料，请及时查看。',
    time: '2023-09-16 08:00',
    isRead: false,
    type: 'info'
  },
  {
    id: 102,
    title: '作业截止日期提醒',
    content: '《操作系统》第三次作业将于明天晚上23:59截止，请及时提交。',
    time: '2023-09-19 09:30',
    isRead: true,
    type: 'warning'
  },
  {
    id: 103,
    title: '期中考试安排',
    content: '《高等数学》期中考试将于下周三上午9:00在教学楼A302举行，请做好准备。',
    time: '2023-09-21 15:00',
    isRead: false,
    type: 'important'
  }
]);

// 选择消息
const selectedMessage = ref(null);

// 回复内容
const replyContent = ref('');

// 获取用户名
const userName = ref('');
onMounted(() => {
  userName.value = localStorage.getItem('userName') || '学生';
});

// 查看消息详情
const viewMessage = (message, type) => {
  selectedMessage.value = message;
  
  // 标记为已读
  if (!message.isRead) {
    message.isRead = true;
  }
};

// 删除消息
const deleteMessage = (message, type, index) => {
  if (type === 'feedback') {
    feedbackMessages.value = feedbackMessages.value.filter(item => item.id !== message.id);
  } else {
    systemNotices.value = systemNotices.value.filter(item => item.id !== message.id);
  }
  
  ElMessage.success('删除成功');
  
  // 如果当前正在查看这条消息，则清空选中
  if (selectedMessage.value && selectedMessage.value.id === message.id) {
    selectedMessage.value = null;
  }
};

// 标记为星标/取消星标
const toggleStar = (message) => {
  message.isStarred = !message.isStarred;
  ElMessage.success(message.isStarred ? '已添加星标' : '已取消星标');
};

// 提交回复
const submitReply = () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容');
    return;
  }
  
  // 确保选中的消息是教师反馈
  if (activeTab.value === 'feedback' && selectedMessage.value) {
    // 添加回复
    selectedMessage.value.replies.push({
      id: Date.now(),
      content: replyContent.value,
      sender: userName.value,
      time: new Date().toLocaleString()
    });
    
    ElMessage.success('回复已发送');
    replyContent.value = '';
  }
};

// 消息类型图标映射
const noticeTypeIcon = {
  info: 'info',
  warning: 'warning',
  important: 'error'
};

// 通知类型文本映射
const noticeTypeText = {
  info: '信息',
  warning: '提醒',
  important: '重要'
};

// 当前选中的课程
const currentCourse = ref({
  id: 1,
  name: '计算机网络',
  teacherName: '张教授',
  progress: 65,
  totalTime: 2580, // 总时长，以分钟为单位
  totalVideos: 12,
  watchedVideos: 8,
  totalExercises: 35,
  completedExercises: 28,
  correctRate: 82,
  studyDays: 18,
  lastStudyTime: '2023-10-15',
  chapters: [
    {
      id: 1,
      name: '第一章 计算机网络基础',
      progress: 100,
      videos: [
        { id: 101, name: '1.1 计算机网络概述', watched: true, duration: 15, watchedTime: 15 },
        { id: 102, name: '1.2 网络拓扑结构', watched: true, duration: 20, watchedTime: 20 }
      ],
      exercises: [
        { id: 201, name: '习题1.1 网络基础概念', completed: true, correct: true },
        { id: 202, name: '习题1.2 网络拓扑结构设计', completed: true, correct: true }
      ]
    },
    {
      id: 2,
      name: '第二章 网络协议',
      progress: 75,
      videos: [
        { id: 103, name: '2.1 OSI七层模型', watched: true, duration: 25, watchedTime: 25 },
        { id: 104, name: '2.2 TCP/IP协议族', watched: true, duration: 30, watchedTime: 22 },
        { id: 105, name: '2.3 IP地址与子网划分', watched: false, duration: 28, watchedTime: 10 }
      ],
      exercises: [
        { id: 203, name: '习题2.1 OSI模型与TCP/IP模型', completed: true, correct: false },
        { id: 204, name: '习题2.2 TCP/UDP协议分析', completed: true, correct: true },
        { id: 205, name: '习题2.3 IP地址计算', completed: false, correct: false }
      ]
    },
    {
      id: 3,
      name: '第三章 网络安全',
      progress: 30,
      videos: [
        { id: 106, name: '3.1 网络攻击类型', watched: true, duration: 22, watchedTime: 22 },
        { id: 107, name: '3.2 加密算法', watched: false, duration: 26, watchedTime: 5 },
        { id: 108, name: '3.3 网络安全防护', watched: false, duration: 24, watchedTime: 0 }
      ],
      exercises: [
        { id: 206, name: '习题3.1 安全威胁分析', completed: true, correct: true },
        { id: 207, name: '习题3.2 加密算法应用', completed: false, correct: false },
        { id: 208, name: '习题3.3 防火墙配置', completed: false, correct: false }
      ]
    },
    {
      id: 4,
      name: '第四章 网络应用',
      progress: 0,
      videos: [
        { id: 109, name: '4.1 Web应用原理', watched: false, duration: 20, watchedTime: 0 },
        { id: 110, name: '4.2 电子邮件系统', watched: false, duration: 18, watchedTime: 0 },
        { id: 111, name: '4.3 DNS系统', watched: false, duration: 22, watchedTime: 0 },
        { id: 112, name: '4.4 P2P应用', watched: false, duration: 25, watchedTime: 0 }
      ],
      exercises: [
        { id: 209, name: '习题4.1 HTTP协议分析', completed: false, correct: false },
        { id: 210, name: '习题4.2 邮件服务器配置', completed: false, correct: false },
        { id: 211, name: '习题4.3 DNS解析过程', completed: false, correct: false }
      ]
    }
  ]
});

// 格式化时间
const formatDuration = (minutes) => {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  if (hours > 0) {
    return `${hours}小时${mins}分钟`;
  } else {
    return `${mins}分钟`;
  }
};

// 折叠面板
const activeChapters = ref(['1']);

// 最近错题
const recentWrongExercises = ref([
  {
    id: 203,
    name: '习题2.1 OSI模型与TCP/IP模型',
    submitTime: '2023-10-12 14:30',
    errorPoints: ['OSI模型层次顺序错误', '传输层协议功能描述不准确'],
    suggestion: '建议重新学习2.1节视频，特别注意OSI七层模型的分层及各层功能'
  },
  {
    id: 205,
    name: '习题2.3 IP地址计算',
    submitTime: '2023-10-14 16:45',
    errorPoints: ['子网掩码计算错误', '可用主机数量计算有误'],
    suggestion: '建议复习子网划分相关知识点，特别是CIDR表示法和子网掩码转换'
  }
]);

// 视图类型
const viewType = ref('progress'); // progress, wrong-exercises

// 初始化图表
let timeChart = null;
let accuracyChart = null;
let weeklyProgressChart = null;

onMounted(() => {
  initTimeDistributionChart();
  initAccuracyChart();
  initWeeklyProgressChart();
});

// 初始化学习时间分布图表
const initTimeDistributionChart = () => {
  const chartDom = document.getElementById('time-distribution-chart');
  timeChart = echarts.init(chartDom);
  
  const option = {
    title: {
      text: '学习时间分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}分钟 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: ['第一章', '第二章', '第三章', '第四章']
    },
    series: [
      {
        type: 'pie',
        radius: '65%',
        center: ['50%', '50%'],
        data: [
          { value: 35, name: '第一章' },
          { value: 57, name: '第二章' },
          { value: 27, name: '第三章' },
          { value: 0, name: '第四章' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  };
  
  timeChart.setOption(option);
};

// 初始化正确率图表
const initAccuracyChart = () => {
  const chartDom = document.getElementById('accuracy-chart');
  accuracyChart = echarts.init(chartDom);
  
  const option = {
    title: {
      text: '章节做题正确率',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: '{b}: {c}%'
    },
    xAxis: {
      type: 'category',
      data: ['第一章', '第二章', '第三章', '第四章'],
      axisLabel: {
        interval: 0,
        rotate: 30
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '正确率',
        type: 'bar',
        data: [100, 67, 33, 0],
        itemStyle: {
          color: function(params) {
            const colorList = ['#91cc75', '#fac858', '#ee6666', '#73c0de'];
            return colorList[params.dataIndex];
          }
        },
        label: {
          show: true,
          position: 'top',
          formatter: '{c}%'
        }
      }
    ]
  };
  
  accuracyChart.setOption(option);
};

// 初始化每周学习进度图表
const initWeeklyProgressChart = () => {
  const chartDom = document.getElementById('weekly-progress-chart');
  weeklyProgressChart = echarts.init(chartDom);
  
  const option = {
    title: {
      text: '每周学习进度',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['第1周', '第2周', '第3周']
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      },
      max: 100
    },
    series: [
      {
        name: '总进度',
        type: 'line',
        stack: '总量',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: [20, 42, 65]
      },
      {
        name: '视频观看',
        type: 'line',
        stack: '总量',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: [25, 48, 67]
      },
      {
        name: '习题完成',
        type: 'line',
        stack: '总量',
        areaStyle: {},
        emphasis: {
          focus: 'series'
        },
        data: [15, 38, 80]
      }
    ]
  };
  
  weeklyProgressChart.setOption(option);
};

// 切换到课程内容详情页
const goToCourseDetail = () => {
  ElMessage.success('跳转到课程内容学习页面');
  // 这里应该是路由跳转到课程详情页
};

// 删除知识点
const deleteKnowledge = (knowledgeId) => {
  ElMessageBox.confirm(
    '确定要删除这个知识点吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 在实际应用中，这里应该调用API删除知识点
    ElMessage.success('知识点删除成功');
  }).catch(() => {
    // 取消删除
  });
};
</script>

<template>
  <div class="messages-container">
    <div class="messages-header">
      <h2>学习反馈与通知</h2>
      <div class="tabs">
        <div 
          class="tab" 
          :class="{ active: activeTab === 'feedback' }" 
          @click="activeTab = 'feedback'"
        >
          教师反馈
        </div>
        <div 
          class="tab" 
          :class="{ active: activeTab === 'system' }" 
          @click="activeTab = 'system'"
        >
          系统通知
        </div>
        <div 
          class="tab" 
          :class="{ active: activeTab === 'progress' }" 
          @click="activeTab = 'progress'"
        >
          学习进度
        </div>
      </div>
    </div>
    
    <div class="messages-main">
      <!-- 教师反馈和系统通知 -->
      <template v-if="activeTab === 'feedback' || activeTab === 'system'">
        <div class="message-list">
          <!-- 教师反馈列表 -->
          <template v-if="activeTab === 'feedback'">
            <div 
              v-for="(message, index) in feedbackMessages" 
              :key="message.id"
              class="message-item"
              :class="{ 'unread': !message.isRead, 'active': selectedMessage && selectedMessage.id === message.id }"
              @click="viewMessage(message, 'feedback')"
            >
              <div class="message-item-header">
                <span class="message-title">{{ message.title }}</span>
                <div class="message-actions">
                  <el-icon 
                    :class="{ 'starred': message.isStarred }"
                    @click.stop="toggleStar(message)"
                  >
                    <component :is="message.isStarred ? StarFilled : Star" />
                  </el-icon>
                  <el-icon @click.stop="deleteMessage(message, 'feedback', index)">
                    <Delete />
                  </el-icon>
                </div>
              </div>
              <div class="message-info">
                <span class="message-sender">{{ message.sender }}</span>
                <span class="message-course">{{ message.course }}</span>
                <span class="message-time">{{ message.time }}</span>
              </div>
              <div class="message-preview">
                {{ message.content.substring(0, 50) }}...
              </div>
              <div v-if="message.replies.length > 0" class="message-replies-count">
                <el-tag size="small" type="info">{{ message.replies.length }}条回复</el-tag>
              </div>
            </div>
          </template>
          
          <!-- 系统通知列表 -->
          <template v-else>
            <div 
              v-for="(notice, index) in systemNotices" 
              :key="notice.id"
              class="message-item"
              :class="{ 'unread': !notice.isRead, 'active': selectedMessage && selectedMessage.id === notice.id }"
              @click="viewMessage(notice, 'system')"
            >
              <div class="message-item-header">
                <el-tag size="small" :type="noticeTypeIcon[notice.type]">
                  {{ noticeTypeText[notice.type] }}
                </el-tag>
                <span class="message-title">{{ notice.title }}</span>
                <div class="message-actions">
                  <el-icon @click.stop="deleteMessage(notice, 'system', index)">
                    <Delete />
                  </el-icon>
                </div>
              </div>
              <div class="message-info">
                <span class="message-time">{{ notice.time }}</span>
              </div>
              <div class="message-preview">
                {{ notice.content }}
              </div>
            </div>
          </template>
        </div>
        
        <div class="message-detail">
          <div v-if="selectedMessage" class="detail-content">
            <div class="detail-header">
              <h3>{{ selectedMessage.title }}</h3>
              <div class="detail-info">
                <template v-if="activeTab === 'feedback'">
                  <span>发送人: {{ selectedMessage.sender }}</span>
                  <span>课程: {{ selectedMessage.course }}</span>
                  <span>时间: {{ selectedMessage.time }}</span>
                </template>
                <template v-else>
                  <el-tag size="small" :type="noticeTypeIcon[selectedMessage.type]">
                    {{ noticeTypeText[selectedMessage.type] }}
                  </el-tag>
                  <span>时间: {{ selectedMessage.time }}</span>
                </template>
              </div>
            </div>
            
            <div class="detail-body">
              <p>{{ selectedMessage.content }}</p>
            </div>
            
            <!-- 回复区域 - 仅教师反馈显示 -->
            <div v-if="activeTab === 'feedback'" class="detail-replies">
              <div v-if="selectedMessage.replies && selectedMessage.replies.length > 0" class="replies-list">
                <h4>回复记录</h4>
                <div 
                  v-for="reply in selectedMessage.replies" 
                  :key="reply.id"
                  class="reply-item"
                  :class="{ 'teacher-reply': reply.sender !== userName }"
                >
                  <div class="reply-header">
                    <span class="reply-sender">{{ reply.sender }}</span>
                    <span class="reply-time">{{ reply.time }}</span>
                  </div>
                  <div class="reply-content">
                    {{ reply.content }}
                  </div>
                </div>
              </div>
              
              <div class="reply-form">
                <h4>回复</h4>
                <div class="reply-input">
                  <el-input
                    v-model="replyContent"
                    type="textarea"
                    :rows="3"
                    placeholder="输入回复内容..."
                  />
                  <el-button 
                    type="primary" 
                    :icon="Send" 
                    @click="submitReply"
                    :disabled="!replyContent.trim()"
                  >
                    发送回复
                  </el-button>
                </div>
              </div>
            </div>
          </div>
          
          <div v-else class="empty-detail">
            <el-icon :size="60" color="#909399"><Message /></el-icon>
            <p>选择一条消息查看详情</p>
          </div>
        </div>
      </template>
      
      <!-- 学习进度视图 -->
      <template v-else-if="activeTab === 'progress'">
        <div class="progress-container">
          <div class="progress-header">
            <h3>{{ currentCourse.name }} - 学习进度</h3>
            <div class="progress-actions">
              <el-radio-group v-model="viewType" size="small">
                <el-radio-button label="progress">学习进度</el-radio-button>
                <el-radio-button label="wrong-exercises">错题分析</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          
          <!-- 学习进度概览 -->
          <div v-if="viewType === 'progress'" class="progress-overview">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-card class="progress-card">
                  <div class="progress-stat">
                    <div class="stat-value">{{ currentCourse.progress }}%</div>
                    <div class="stat-label">总体完成度</div>
                    <el-progress :percentage="currentCourse.progress" :stroke-width="10" />
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="progress-card">
                  <div class="progress-stat">
                    <div class="stat-value">{{ currentCourse.watchedVideos }}/{{ currentCourse.totalVideos }}</div>
                    <div class="stat-label">视频完成数</div>
                    <el-progress 
                      :percentage="Math.round(currentCourse.watchedVideos / currentCourse.totalVideos * 100)" 
                      :stroke-width="10" 
                      status="success" 
                    />
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="progress-card">
                  <div class="progress-stat">
                    <div class="stat-value">{{ currentCourse.correctRate }}%</div>
                    <div class="stat-label">练习正确率</div>
                    <el-progress 
                      :percentage="currentCourse.correctRate" 
                      :stroke-width="10" 
                      status="warning" 
                    />
                  </div>
                </el-card>
              </el-col>
            </el-row>
            
            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="8">
                <el-card class="chart-card">
                  <div class="chart-title">学习时间分布</div>
                  <div id="time-distribution-chart" class="chart"></div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="chart-card">
                  <div class="chart-title">章节做题正确率</div>
                  <div id="accuracy-chart" class="chart"></div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="chart-card">
                  <div class="chart-title">每周学习进度</div>
                  <div id="weekly-progress-chart" class="chart"></div>
                </el-card>
              </el-col>
            </el-row>
            
            <!-- 章节详情 -->
            <el-card style="margin-top: 20px;">
              <template #header>
                <div class="card-header">
                  <span>章节学习详情</span>
                </div>
              </template>
              
              <el-collapse v-model="activeChapters">
                <el-collapse-item 
                  v-for="chapter in currentCourse.chapters" 
                  :key="chapter.id" 
                  :title="chapter.name + ' - ' + chapter.progress + '%'" 
                  :name="chapter.id.toString()"
                >
                  <div class="chapter-content">
                    <div class="chapter-videos">
                      <h4>视频学习</h4>
                      <el-table :data="chapter.videos" style="width: 100%">
                        <el-table-column prop="name" label="视频名称" min-width="180" />
                        <el-table-column label="状态" width="100">
                          <template #default="scope">
                            <el-tag :type="scope.row.watched ? 'success' : 'info'">
                              {{ scope.row.watched ? '已学习' : '未学习' }}
                            </el-tag>
                          </template>
                        </el-table-column>
                        <el-table-column label="时长" width="100">
                          <template #default="scope">
                            {{ scope.row.duration }}分钟
                          </template>
                        </el-table-column>
                        <el-table-column label="已观看" width="100">
                          <template #default="scope">
                            {{ scope.row.watchedTime }}分钟
                          </template>
                        </el-table-column>
                        <el-table-column label="操作" width="100">
                          <template #default="scope">
                            <el-button size="small" type="primary" @click="goToCourseDetail">
                              学习
                            </el-button>
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>
                    
                    <div class="chapter-exercises" style="margin-top: 20px;">
                      <h4>习题练习</h4>
                      <el-table :data="chapter.exercises" style="width: 100%">
                        <el-table-column prop="name" label="习题名称" min-width="180" />
                        <el-table-column label="状态" width="100">
                          <template #default="scope">
                            <el-tag :type="scope.row.completed ? (scope.row.correct ? 'success' : 'danger') : 'info'">
                              {{ scope.row.completed ? (scope.row.correct ? '正确' : '错误') : '未完成' }}
                            </el-tag>
                          </template>
                        </el-table-column>
                        <el-table-column label="操作" width="150">
                          <template #default="scope">
                            <el-button size="small" type="primary" @click="goToCourseDetail">
                              {{ scope.row.completed ? '查看' : '做题' }}
                            </el-button>
                            <el-button 
                              v-if="scope.row.completed && !scope.row.correct" 
                              size="small" 
                              type="danger" 
                              @click="deleteKnowledge(scope.row.id)"
                            >
                              删除
                            </el-button>
                          </template>
                        </el-table-column>
                      </el-table>
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </el-card>
          </div>
          
          <!-- 错题分析 -->
          <div v-else class="wrong-exercises">
            <el-card v-for="exercise in recentWrongExercises" :key="exercise.id" class="wrong-exercise-card">
              <template #header>
                <div class="card-header">
                  <span>{{ exercise.name }}</span>
                  <el-tag size="small" type="danger">错题</el-tag>
                </div>
              </template>
              
              <div class="wrong-exercise-content">
                <div class="wrong-time">提交时间：{{ exercise.submitTime }}</div>
                <div class="wrong-points">
                  <h4>错误点：</h4>
                  <ul>
                    <li v-for="(point, index) in exercise.errorPoints" :key="index">{{ point }}</li>
                  </ul>
                </div>
                <div class="wrong-suggestion">
                  <h4>改进建议：</h4>
                  <p>{{ exercise.suggestion }}</p>
                </div>
                <div class="wrong-actions">
                  <el-button type="primary" size="small" @click="goToCourseDetail">查看详情</el-button>
                  <el-button type="danger" size="small" @click="deleteKnowledge(exercise.id)">删除记录</el-button>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.messages-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 115px);
}

.messages-header {
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

.messages-main {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: 20px;
  height: calc(100vh - 180px);
}

/* 学习进度样式 */
.progress-container {
  grid-column: 1 / -1;
  overflow-y: auto;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.progress-header h3 {
  margin: 0;
}

.progress-card {
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.progress-stat {
  text-align: center;
  width: 100%;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  color: #909399;
  margin-bottom: 10px;
}

.chart-card {
  height: 300px;
}

.chart-title {
  text-align: center;
  margin-bottom: 10px;
  font-weight: bold;
}

.chart {
  height: 250px;
}

.card-header {
  font-weight: bold;
}

.chapter-content {
  padding: 10px 0;
}

/* 错题分析样式 */
.wrong-exercise-card {
  margin-bottom: 20px;
}

.wrong-exercise-content {
  padding: 10px 0;
}

.wrong-time {
  color: #909399;
  margin-bottom: 15px;
}

.wrong-points h4, .wrong-suggestion h4 {
  margin: 10px 0;
  font-size: 15px;
}

.wrong-points ul {
  padding-left: 20px;
  margin: 10px 0;
}

.wrong-actions {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}

/* 消息列表样式 */
.message-list {
  background-color: #fff;
  border-radius: 8px;
  overflow-y: auto;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.message-item {
  padding: 15px;
  border-bottom: 1px solid #EBEEF5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.message-item:hover {
  background-color: #f5f7fa;
}

.message-item.active {
  background-color: #ecf5ff;
}

.message-item.unread {
  border-left: 3px solid #409EFF;
}

.message-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-title {
  font-weight: bold;
  flex: 1;
  margin-right: 10px;
}

.message-actions {
  display: flex;
  gap: 10px;
}

.message-actions .el-icon {
  cursor: pointer;
  font-size: 16px;
  color: #909399;
}

.message-actions .el-icon:hover {
  color: #409EFF;
}

.message-actions .starred {
  color: #E6A23C;
}

.message-info {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
}

.message-sender, .message-course {
  position: relative;
}

.message-sender::after, .message-course::after {
  content: '|';
  position: absolute;
  right: -9px;
  color: #DCDFE6;
}

.message-preview {
  font-size: 14px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-replies-count {
  margin-top: 8px;
}

.message-detail {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.detail-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.detail-header {
  border-bottom: 1px solid #EBEEF5;
  padding-bottom: 15px;
  margin-bottom: 15px;
}

.detail-header h3 {
  margin: 0 0 10px;
}

.detail-info {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: #606266;
}

.detail-body {
  flex: 1;
  overflow-y: auto;
  padding: 10px 0;
  line-height: 1.6;
}

.empty-detail {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.empty-detail p {
  margin-top: 15px;
  font-size: 16px;
}

/* 回复样式 */
.detail-replies {
  margin-top: 20px;
  border-top: 1px solid #EBEEF5;
  padding-top: 20px;
}

.replies-list {
  margin-bottom: 20px;
}

.replies-list h4, .reply-form h4 {
  margin: 0 0 15px;
  font-size: 16px;
  color: #606266;
}

.reply-item {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 10px;
}

.teacher-reply {
  background-color: #ecf5ff;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.reply-sender {
  font-weight: bold;
}

.reply-time {
  font-size: 12px;
  color: #909399;
}

.reply-content {
  line-height: 1.6;
}

.reply-input {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.reply-input .el-button {
  align-self: flex-end;
}
</style> 