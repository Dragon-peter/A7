<script setup>
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'

// 统计数据
const statistics = reactive({
  totalUsers: 156,
  teacherCount: 32,
  studentCount: 124,
  totalCourses: 45,
  totalResources: 278,
  activeToday: 87
})

// 图表实例
const userChartRef = ref(null)
const activityChartRef = ref(null)
const efficiencyChartRef = ref(null)
const learningChartRef = ref(null)

// 初始化图表
onMounted(() => {
  // 延迟初始化以确保DOM已渲染
  setTimeout(() => {
    initUserChart()
    initActivityChart()
    initEfficiencyChart()
    initLearningChart()
  }, 300)
})

// 初始化用户统计图表
const initUserChart = () => {
  const chartDom = userChartRef.value
  if (!chartDom) return
  
  const chart = echarts.init(chartDom)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 0,
      data: ['教师', '学生']
    },
    series: [
      {
        name: '用户分布',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: statistics.teacherCount, name: '教师' },
          { value: statistics.studentCount, name: '学生' }
        ]
      }
    ]
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化活跃度图表
const initActivityChart = () => {
  const chartDom = activityChartRef.value
  if (!chartDom) return
  
  const chart = echarts.init(chartDom)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['教师', '学生']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '活跃人数'
      }
    ],
    series: [
      {
        name: '教师',
        type: 'bar',
        stack: 'Total',
        emphasis: {
          focus: 'series'
        },
        data: [12, 15, 18, 14, 16, 8, 5]
      },
      {
        name: '学生',
        type: 'bar',
        stack: 'Total',
        emphasis: {
          focus: 'series'
        },
        data: [45, 52, 68, 56, 72, 38, 25]
      }
    ]
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化教学效率指数图表
const initEfficiencyChart = () => {
  const chartDom = efficiencyChartRef.value
  if (!chartDom) return
  
  const chart = echarts.init(chartDom)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['备课耗时', '练习设计耗时', '课程优化']
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
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: {
      type: 'value',
      name: '耗时(分钟)'
    },
    series: [
      {
        name: '备课耗时',
        type: 'line',
        stack: 'Total',
        data: [120, 132, 101, 94, 85, 78],
        smooth: true
      },
      {
        name: '练习设计耗时',
        type: 'line',
        stack: 'Total',
        data: [220, 182, 191, 184, 175, 168],
        smooth: true
      },
      {
        name: '课程优化',
        type: 'line',
        stack: 'Total',
        data: [150, 132, 121, 114, 105, 98],
        smooth: true
      }
    ]
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化学习效果图表
const initLearningChart = () => {
  const chartDom = learningChartRef.value
  if (!chartDom) return
  
  const chart = echarts.init(chartDom)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    },
    legend: {
      data: ['平均正确率', '完成率']
    },
    xAxis: [
      {
        type: 'category',
        data: ['计算机网络', '操作系统', '高等数学', '嵌入式开发', '数据结构'],
        axisPointer: {
          type: 'shadow'
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '百分比',
        min: 0,
        max: 100,
        interval: 20,
        axisLabel: {
          formatter: '{value} %'
        }
      }
    ],
    series: [
      {
        name: '平均正确率',
        type: 'bar',
        data: [78.5, 82.3, 65.8, 76.2, 72.1]
      },
      {
        name: '完成率',
        type: 'line',
        yAxisIndex: 0,
        data: [92.6, 88.4, 75.2, 83.7, 86.9]
      }
    ]
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 高频错误知识点
const errorPoints = [
  { name: '网络协议分层', count: 56, course: '计算机网络' },
  { name: '进程调度算法', count: 48, course: '操作系统' },
  { name: '二叉树遍历', count: 42, course: '数据结构与算法' },
  { name: '设备驱动开发', count: 38, course: '嵌入式开发' },
  { name: '红黑树', count: 35, course: '数据结构' }
]

// 活跃板块
const activeModules = [
  { name: '智能备课', count: 128, trend: 'up' },
  { name: '在线学习助手', count: 245, trend: 'up' },
  { name: '实时练习评测', count: 186, trend: 'down' },
  { name: '考核内容生成', count: 97, trend: 'up' }
]
</script>

<template>
  <div class="dashboard-container">
    <h2>系统概览</h2>
    
    <div class="stat-cards">
      <el-card class="stat-card">
        <template #header>
          <div class="card-header">
            <span>总用户数</span>
          </div>
        </template>
        <div class="stat-value">{{ statistics.totalUsers }}</div>
        <div class="stat-detail">教师: {{ statistics.teacherCount }} | 学生: {{ statistics.studentCount }}</div>
      </el-card>
      
      <el-card class="stat-card">
        <template #header>
          <div class="card-header">
            <span>总课程数</span>
          </div>
        </template>
        <div class="stat-value">{{ statistics.totalCourses }}</div>
        <div class="stat-detail">平均每位教师: {{ (statistics.totalCourses / statistics.teacherCount).toFixed(1) }} 门课程</div>
      </el-card>
      
      <el-card class="stat-card">
        <template #header>
          <div class="card-header">
            <span>教学资源</span>
          </div>
        </template>
        <div class="stat-value">{{ statistics.totalResources }}</div>
        <div class="stat-detail">平均每门课程: {{ (statistics.totalResources / statistics.totalCourses).toFixed(1) }} 个资源</div>
      </el-card>
      
      <el-card class="stat-card">
        <template #header>
          <div class="card-header">
            <span>今日活跃</span>
          </div>
        </template>
        <div class="stat-value">{{ statistics.activeToday }}</div>
        <div class="stat-detail">活跃率: {{ (statistics.activeToday / statistics.totalUsers * 100).toFixed(1) }}%</div>
      </el-card>
    </div>
    
    <div class="chart-row">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>用户分布</span>
          </div>
        </template>
        <div ref="userChartRef" class="chart"></div>
      </el-card>
      
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>本周活跃度</span>
          </div>
        </template>
        <div ref="activityChartRef" class="chart"></div>
      </el-card>
    </div>
    
    <div class="chart-row">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>教学效率指数</span>
          </div>
        </template>
        <div ref="efficiencyChartRef" class="chart"></div>
      </el-card>
      
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>学习效果</span>
          </div>
        </template>
        <div ref="learningChartRef" class="chart"></div>
      </el-card>
    </div>
    
    <div class="table-row">
      <el-card class="table-card">
        <template #header>
          <div class="card-header">
            <span>高频错误知识点</span>
          </div>
        </template>
        <el-table :data="errorPoints" style="width: 100%" :border="true">
          <el-table-column prop="name" label="知识点" />
          <el-table-column prop="course" label="所属课程" />
          <el-table-column prop="count" label="错误次数" width="100" />
        </el-table>
      </el-card>
      
      <el-card class="table-card">
        <template #header>
          <div class="card-header">
            <span>活跃板块 (本周)</span>
          </div>
        </template>
        <el-table :data="activeModules" style="width: 100%" :border="true">
          <el-table-column prop="name" label="功能板块" />
          <el-table-column prop="count" label="使用次数" width="100" />
          <el-table-column label="趋势" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.trend === 'up' ? 'success' : 'danger'">
                {{ scope.row.trend === 'up' ? '上升' : '下降' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin: 10px 0;
}

.stat-detail {
  color: #606266;
  font-size: 14px;
}

.chart-row, .table-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.chart {
  height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-weight: bold;
}
</style> 