<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import * as echarts from 'echarts';

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
  <div class="progress-container">
    <div class="progress-header">
      <h2>学习进度</h2>
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

<style scoped>
.progress-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 115px);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.progress-header h2 {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
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
</style> 