<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

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

// 图表实例
const activityChart = ref(null)
const masteryChart = ref(null)
const questionChart = ref(null)
const progressChart = ref(null)

// 图表容器引用
const activityChartRef = ref(null)
const masteryChartRef = ref(null)
const questionChartRef = ref(null)
const progressChartRef = ref(null)

// 统计数据
const stats = ref({
  totalStudents: 120,
  activeStudents: 98,
  avgScore: 85.6,
  completionRate: 78.3,
  questionCount: 156
})

// 选择课程
const selectCourse = (course) => {
  selectedCourse.value = course
  // 重新加载图表数据
  initCharts()
}

// 初始化图表
const initCharts = () => {
  // 学生活跃度图表
  if (activityChartRef.value) {
    activityChart.value = echarts.init(activityChartRef.value)
    activityChart.value.setOption({
      title: {
        text: '学生活跃度',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          data: [120, 132, 101, 134, 90, 30, 20],
          type: 'line',
          smooth: true,
          areaStyle: {}
        }
      ]
    })
  }
  
  // 知识点掌握分布图表
  if (masteryChartRef.value) {
    masteryChart.value = echarts.init(masteryChartRef.value)
    masteryChart.value.setOption({
      title: {
        text: '知识点掌握分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          type: 'pie',
          radius: '50%',
          data: [
            { value: 35, name: '优秀(90-100)' },
            { value: 30, name: '良好(80-89)' },
            { value: 20, name: '中等(70-79)' },
            { value: 10, name: '及格(60-69)' },
            { value: 5, name: '不及格(<60)' }
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
    })
  }
  
  // 问题类型分布图表
  if (questionChartRef.value) {
    questionChart.value = echarts.init(questionChartRef.value)
    questionChart.value.setOption({
      title: {
        text: '问题类型分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'category',
        data: ['概念理解', '应用问题', '实验操作', '编程问题', '其他']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          data: [45, 32, 25, 40, 14],
          type: 'bar',
          showBackground: true,
          backgroundStyle: {
            color: 'rgba(180, 180, 180, 0.2)'
          }
        }
      ]
    })
  }
  
  // 学习进度图表
  if (progressChartRef.value) {
    progressChart.value = echarts.init(progressChartRef.value)
    progressChart.value.setOption({
      title: {
        text: '学习进度分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      radar: {
        indicator: [
          { name: '网络基础概念', max: 100 },
          { name: 'OSI七层模型', max: 100 },
          { name: 'TCP/IP协议族', max: 100 },
          { name: '网络安全', max: 100 },
          { name: '网络应用', max: 100 }
        ]
      },
      series: [
        {
          type: 'radar',
          data: [
            {
              value: [85, 70, 65, 55, 75],
              name: '班级平均'
            },
            {
              value: [95, 80, 75, 65, 85],
              name: '优秀学生'
            }
          ]
        }
      ]
    })
  }
}

// 监听窗口大小变化，重新调整图表大小
const handleResize = () => {
  activityChart.value?.resize()
  masteryChart.value?.resize()
  questionChart.value?.resize()
  progressChart.value?.resize()
}

// 导出数据
const exportData = () => {
  console.log('导出数据')
}

// 组件挂载后初始化图表
onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})
</script>

<template>
  <div class="analysis-container">
    <div class="analysis-header">
      <h2>数据分析</h2>
      <div class="header-actions">
        <div class="course-selector">
          <span>当前课程：</span>
          <el-select v-model="selectedCourse" value-key="id" style="width: 200px" @change="selectCourse">
            <el-option
              v-for="course in courseList"
              :key="course.id"
              :label="course.name"
              :value="course"
            />
          </el-select>
        </div>
        <el-button type="primary" @click="exportData">导出数据</el-button>
      </div>
    </div>
    
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-value">{{ stats.totalStudents }}</div>
        <div class="stat-label">学生总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.activeStudents }}</div>
        <div class="stat-label">活跃学生</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.avgScore.toFixed(1) }}</div>
        <div class="stat-label">平均分数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.completionRate.toFixed(1) }}%</div>
        <div class="stat-label">完成率</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.questionCount }}</div>
        <div class="stat-label">问题总数</div>
      </div>
    </div>
    
    <div class="charts-container">
      <div class="chart-row">
        <div class="chart-card">
          <div ref="activityChartRef" class="chart"></div>
        </div>
        <div class="chart-card">
          <div ref="masteryChartRef" class="chart"></div>
        </div>
      </div>
      <div class="chart-row">
        <div class="chart-card">
          <div ref="questionChartRef" class="chart"></div>
        </div>
        <div class="chart-card">
          <div ref="progressChartRef" class="chart"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.analysis-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 115px);
}

.analysis-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 20px;
  align-items: center;
}

.course-selector {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stats-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  flex: 1;
  text-align: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  color: #606266;
}

.charts-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-row {
  display: flex;
  gap: 20px;
}

.chart-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  flex: 1;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.chart {
  height: 300px;
  width: 100%;
}
</style> 