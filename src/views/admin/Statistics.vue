<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { Warning, TrendCharts, Timer } from '@element-plus/icons-vue'

// 图表引用
const teacherActivityChartRef = ref(null)
const studentActivityChartRef = ref(null)
const efficiencyChartRef = ref(null)
const learningEffectChartRef = ref(null)
const errorKnowledgeChartRef = ref(null)
const timeDistributionChartRef = ref(null)

// 时间范围
const timeRange = ref('week')

// 初始化图表
onMounted(() => {
  // 延迟初始化以确保DOM已渲染
  setTimeout(() => {
    initTeacherActivityChart()
    initStudentActivityChart()
    initEfficiencyChart()
    initLearningEffectChart()
    initErrorKnowledgeChart()
    initTimeDistributionChart()
  }, 300)
})

// 切换时间范围
const changeTimeRange = (range) => {
  timeRange.value = range
  
  // 重新初始化所有图表
  initTeacherActivityChart()
  initStudentActivityChart()
  initEfficiencyChart()
  initLearningEffectChart()
  initErrorKnowledgeChart()
  initTimeDistributionChart()
}

// 初始化教师活跃度图表
const initTeacherActivityChart = () => {
  const chartDom = teacherActivityChartRef.value
  if (!chartDom) return
  
  const chart = echarts.init(chartDom)
  
  // 根据时间范围获取不同数据
  let xAxisData = []
  let seriesData = []
  
  if (timeRange.value === 'day') {
    xAxisData = ['00:00', '03:00', '06:00', '09:00', '12:00', '15:00', '18:00', '21:00']
    seriesData = [2, 1, 0, 15, 12, 18, 10, 5]
  } else if (timeRange.value === 'week') {
    xAxisData = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    seriesData = [25, 32, 28, 35, 29, 15, 8]
  } else if (timeRange.value === 'month') {
    xAxisData = Array.from({length: 30}, (_, i) => `${i+1}日`)
    seriesData = [
      25, 32, 28, 35, 29, 15, 8, 18, 22, 30,
      27, 33, 35, 28, 20, 15, 10, 22, 28, 32,
      35, 38, 30, 25, 22, 18, 15, 20, 25, 30
    ]
  }
  
  const option = {
    title: {
      text: '教师使用次数统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
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
        data: xAxisData,
        axisTick: {
          alignWithLabel: true
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '使用次数'
      }
    ],
    series: [
      {
        name: '使用次数',
        type: 'bar',
        barWidth: '60%',
        data: seriesData,
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化学生活跃度图表
const initStudentActivityChart = () => {
  const chartDom = studentActivityChartRef.value
  if (!chartDom) return
  
  const chart = echarts.init(chartDom)
  
  // 根据时间范围获取不同数据
  let xAxisData = []
  let seriesData = []
  
  if (timeRange.value === 'day') {
    xAxisData = ['00:00', '03:00', '06:00', '09:00', '12:00', '15:00', '18:00', '21:00']
    seriesData = [5, 2, 0, 45, 68, 75, 82, 65]
  } else if (timeRange.value === 'week') {
    xAxisData = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    seriesData = [85, 92, 88, 95, 78, 105, 65]
  } else if (timeRange.value === 'month') {
    xAxisData = Array.from({length: 30}, (_, i) => `${i+1}日`)
    seriesData = [
      85, 92, 88, 95, 78, 105, 65, 88, 92, 98,
      87, 93, 95, 88, 80, 75, 70, 82, 88, 92,
      95, 98, 90, 85, 82, 78, 75, 80, 85, 90
    ]
  }
  
  const option = {
    title: {
      text: '学生使用次数统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
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
        data: xAxisData,
        axisTick: {
          alignWithLabel: true
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '使用次数'
      }
    ],
    series: [
      {
        name: '使用次数',
        type: 'bar',
        barWidth: '60%',
        data: seriesData,
        itemStyle: {
          color: '#67C23A'
        }
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
    title: {
      text: '教学效率指数',
      left: 'center'
    },
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
      data: ['备课耗时', '练习设计耗时', '课程优化耗时'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
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
        name: '耗时(分钟)',
        min: 0,
        max: 120,
        interval: 20
      }
    ],
    series: [
      {
        name: '备课耗时',
        type: 'bar',
        data: [85, 92, 78, 95, 88]
      },
      {
        name: '练习设计耗时',
        type: 'bar',
        data: [65, 72, 85, 68, 75]
      },
      {
        name: '课程优化耗时',
        type: 'bar',
        data: [45, 52, 38, 48, 42]
      }
    ]
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化学习效果图表
const initLearningEffectChart = () => {
  const chartDom = learningEffectChartRef.value
  if (!chartDom) return
  
  const chart = echarts.init(chartDom)
  const option = {
    title: {
      text: '学生学习效果',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['平均正确率', '完成率'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: {
      type: 'value',
      name: '百分比',
      min: 50,
      max: 100,
      interval: 10,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '平均正确率',
        type: 'line',
        data: [68, 72, 75, 78, 82, 85],
        smooth: true,
        lineStyle: {
          width: 3
        }
      },
      {
        name: '完成率',
        type: 'line',
        data: [82, 85, 88, 86, 90, 92],
        smooth: true,
        lineStyle: {
          width: 3
        }
      }
    ]
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化错误知识点图表
const initErrorKnowledgeChart = () => {
  const chartDom = errorKnowledgeChartRef.value
  if (!chartDom) return
  
  const chart = echarts.init(chartDom)
  const option = {
    title: {
      text: '高频错误知识点',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle'
    },
    series: [
      {
        name: '错误次数',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 56, name: '网络协议分层' },
          { value: 48, name: '进程调度算法' },
          { value: 42, name: '二叉树遍历' },
          { value: 38, name: '设备驱动开发' },
          { value: 35, name: '红黑树' }
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
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化学习时间分布图表
const initTimeDistributionChart = () => {
  const chartDom = timeDistributionChartRef.value
  if (!chartDom) return
  
  const chart = echarts.init(chartDom)
  const option = {
    title: {
      text: '学习时间分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: '{b}: {c} 小时'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['计算机网络', '操作系统', '高等数学', '嵌入式开发', '数据结构'],
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value',
      name: '累计学习时间(小时)'
    },
    series: [
      {
        name: '累计学习时间',
        type: 'bar',
        barWidth: '60%',
        data: [45, 38, 52, 35, 42],
        itemStyle: {
          color: function(params) {
            const colorList = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399'];
            return colorList[params.dataIndex % colorList.length];
          }
        }
      }
    ]
  }
  
  chart.setOption(option)
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 活跃板块数据
const activeModules = [
  { name: '智能备课', count: 128, trend: 'up' },
  { name: '在线学习助手', count: 245, trend: 'up' },
  { name: '实时练习评测', count: 186, trend: 'down' },
  { name: '考核内容生成', count: 97, trend: 'up' }
]
</script>

<template>
  <div class="statistics-container">
    <div class="page-header">
      <h2>数据统计</h2>
      <div class="time-range-selector">
        <el-radio-group v-model="timeRange" @change="changeTimeRange">
          <el-radio-button label="day">今日</el-radio-button>
          <el-radio-button label="week">本周</el-radio-button>
          <el-radio-button label="month">本月</el-radio-button>
        </el-radio-group>
      </div>
    </div>
    
    <div class="chart-grid">
      <!-- 教师使用次数统计 -->
      <el-card class="chart-card">
        <div ref="teacherActivityChartRef" class="chart"></div>
      </el-card>
      
      <!-- 学生使用次数统计 -->
      <el-card class="chart-card">
        <div ref="studentActivityChartRef" class="chart"></div>
      </el-card>
      
      <!-- 教学效率指数 -->
      <el-card class="chart-card">
        <div ref="efficiencyChartRef" class="chart"></div>
      </el-card>
      
      <!-- 学生学习效果 -->
      <el-card class="chart-card">
        <div ref="learningEffectChartRef" class="chart"></div>
      </el-card>
      
      <!-- 高频错误知识点 -->
      <el-card class="chart-card">
        <div ref="errorKnowledgeChartRef" class="chart"></div>
      </el-card>
      
      <!-- 学习时间分布 -->
      <el-card class="chart-card">
        <div ref="timeDistributionChartRef" class="chart"></div>
      </el-card>
    </div>
    
    <!-- 活跃板块统计 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>活跃板块统计 ({{ timeRange === 'day' ? '今日' : timeRange === 'week' ? '本周' : '本月' }})</span>
        </div>
      </template>
      <el-table :data="activeModules" style="width: 100%" border>
        <el-table-column prop="name" label="功能板块" />
        <el-table-column prop="count" label="使用次数" width="120" sortable />
        <el-table-column label="趋势" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.trend === 'up' ? 'success' : 'danger'">
              {{ scope.row.trend === 'up' ? '上升' : '下降' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="占比" width="200">
          <template #default="scope">
            <el-progress 
              :percentage="Math.round(scope.row.count / activeModules.reduce((sum, item) => sum + item.count, 0) * 100)" 
              :color="scope.row.trend === 'up' ? '#67C23A' : '#F56C6C'"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 系统建议 -->
    <el-card class="suggestion-card">
      <template #header>
        <div class="card-header">
          <span>系统优化建议</span>
        </div>
      </template>
      <div class="suggestions">
        <div class="suggestion-item">
          <el-icon class="suggestion-icon"><Warning /></el-icon>
          <div class="suggestion-content">
            <h4>课程优化方向</h4>
            <p>计算机网络课程中"网络协议分层"知识点错误率较高，建议教师增加相关案例讲解和练习。</p>
          </div>
        </div>
        <div class="suggestion-item">
          <el-icon class="suggestion-icon"><TrendCharts /></el-icon>
          <div class="suggestion-content">
            <h4>学习效果提升</h4>
            <p>高等数学课程完成率偏低(75.2%)，建议调整练习难度或增加辅导资源。</p>
          </div>
        </div>
        <div class="suggestion-item">
          <el-icon class="suggestion-icon"><Timer /></el-icon>
          <div class="suggestion-content">
            <h4>教学效率提升</h4>
            <p>备课平均耗时较高，建议利用智能备课系统提供的模板功能，可减少约25%的备课时间。</p>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.statistics-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  min-height: 400px;
}

.chart {
  height: 350px;
}

.table-card, .suggestion-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-weight: bold;
}

.suggestions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 15px;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.suggestion-icon {
  font-size: 24px;
  color: #409EFF;
}

.suggestion-content h4 {
  margin-top: 0;
  margin-bottom: 8px;
  color: #303133;
}

.suggestion-content p {
  margin: 0;
  color: #606266;
  line-height: 1.5;
}
</style> 