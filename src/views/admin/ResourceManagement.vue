<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Delete, Download, Filter, Share, Document, VideoCamera, Picture, Folder } from '@element-plus/icons-vue'

// 资源列表
const resourceList = ref([
  {
    id: 1,
    name: '计算机网络基础概念',
    type: 'document',
    subject: '计算机网络',
    creator: '张明',
    size: 1024 * 1024 * 2.5, // 2.5MB
    createTime: '2023-06-15 10:30:00',
    downloadCount: 45
  },
  {
    id: 2,
    name: 'OSI七层模型详解',
    type: 'video',
    subject: '计算机网络',
    creator: '张明',
    size: 1024 * 1024 * 75, // 75MB
    createTime: '2023-06-16 14:20:00',
    downloadCount: 78
  },
  {
    id: 3,
    name: '进程调度算法',
    type: 'document',
    subject: '操作系统',
    creator: '梁伟',
    size: 1024 * 1024 * 1.8, // 1.8MB
    createTime: '2023-06-17 09:15:00',
    downloadCount: 56
  },
  {
    id: 4,
    name: '内存管理详解',
    type: 'video',
    subject: '操作系统',
    creator: '梁伟',
    size: 1024 * 1024 * 120, // 120MB
    createTime: '2023-06-18 16:40:00',
    downloadCount: 62
  },
  {
    id: 5,
    name: '微积分基础练习题',
    type: 'exercise',
    subject: '高等数学',
    creator: '高等数学教研组',
    size: 1024 * 1024 * 0.8, // 0.8MB
    createTime: '2023-06-19 11:25:00',
    downloadCount: 124
  },
  {
    id: 6,
    name: '极限与连续性',
    type: 'document',
    subject: '高等数学',
    creator: '高等数学教研组',
    size: 1024 * 1024 * 3.2, // 3.2MB
    createTime: '2023-06-20 13:10:00',
    downloadCount: 98
  },
  {
    id: 7,
    name: '嵌入式Linux开发环境搭建',
    type: 'document',
    subject: '嵌入式Linux开发',
    creator: '李强',
    size: 1024 * 1024 * 5.5, // 5.5MB
    createTime: '2023-06-21 15:30:00',
    downloadCount: 42
  },
  {
    id: 8,
    name: '设备驱动开发实例',
    type: 'video',
    subject: '嵌入式Linux开发',
    creator: '李强',
    size: 1024 * 1024 * 180, // 180MB
    createTime: '2023-06-22 10:45:00',
    downloadCount: 36
  }
])

// 搜索关键词
const searchKeyword = ref('')

// 表格加载状态
const tableLoading = ref(false)

// 筛选条件
const filters = reactive({
  subject: '',
  type: '',
  creator: ''
})

// 分页配置
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 学科列表
const subjects = computed(() => {
  const subjectSet = new Set(resourceList.value.map(item => item.subject))
  return Array.from(subjectSet)
})

// 创建者列表
const creators = computed(() => {
  const creatorSet = new Set(resourceList.value.map(item => item.creator))
  return Array.from(creatorSet)
})

// 资源类型列表
const resourceTypes = [
  { value: 'document', label: '文档' },
  { value: 'video', label: '视频' },
  { value: 'exercise', label: '练习' },
  { value: 'image', label: '图片' }
]

// 初始化数据
onMounted(() => {
  loadResourceData()
})

// 加载资源数据
const loadResourceData = () => {
  tableLoading.value = true
  
  // 应用筛选
  let filteredList = [...resourceList.value]
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filteredList = filteredList.filter(item => 
      item.name.toLowerCase().includes(keyword) || 
      item.subject.toLowerCase().includes(keyword) ||
      item.creator.toLowerCase().includes(keyword)
    )
  }
  
  // 按学科筛选
  if (filters.subject) {
    filteredList = filteredList.filter(item => item.subject === filters.subject)
  }
  
  // 按类型筛选
  if (filters.type) {
    filteredList = filteredList.filter(item => item.type === filters.type)
  }
  
  // 按创建者筛选
  if (filters.creator) {
    filteredList = filteredList.filter(item => item.creator === filters.creator)
  }
  
  // 更新分页
  pagination.total = filteredList.length
  
  // 模拟API延迟
  setTimeout(() => {
    resourceList.value = filteredList
    tableLoading.value = false
  }, 300)
}

// 搜索资源
const searchResources = () => {
  pagination.currentPage = 1
  loadResourceData()
}

// 重置搜索和筛选
const resetFilters = () => {
  searchKeyword.value = ''
  filters.subject = ''
  filters.type = ''
  filters.creator = ''
  pagination.currentPage = 1
  
  // 恢复原始数据列表
  resourceList.value = [
    {
      id: 1,
      name: '计算机网络基础概念',
      type: 'document',
      subject: '计算机网络',
      creator: '张明',
      size: 1024 * 1024 * 2.5, // 2.5MB
      createTime: '2023-06-15 10:30:00',
      downloadCount: 45
    },
    {
      id: 2,
      name: 'OSI七层模型详解',
      type: 'video',
      subject: '计算机网络',
      creator: '张明',
      size: 1024 * 1024 * 75, // 75MB
      createTime: '2023-06-16 14:20:00',
      downloadCount: 78
    },
    {
      id: 3,
      name: '进程调度算法',
      type: 'document',
      subject: '操作系统',
      creator: '梁伟',
      size: 1024 * 1024 * 1.8, // 1.8MB
      createTime: '2023-06-17 09:15:00',
      downloadCount: 56
    },
    {
      id: 4,
      name: '内存管理详解',
      type: 'video',
      subject: '操作系统',
      creator: '梁伟',
      size: 1024 * 1024 * 120, // 120MB
      createTime: '2023-06-18 16:40:00',
      downloadCount: 62
    },
    {
      id: 5,
      name: '微积分基础练习题',
      type: 'exercise',
      subject: '高等数学',
      creator: '高等数学教研组',
      size: 1024 * 1024 * 0.8, // 0.8MB
      createTime: '2023-06-19 11:25:00',
      downloadCount: 124
    },
    {
      id: 6,
      name: '极限与连续性',
      type: 'document',
      subject: '高等数学',
      creator: '高等数学教研组',
      size: 1024 * 1024 * 3.2, // 3.2MB
      createTime: '2023-06-20 13:10:00',
      downloadCount: 98
    },
    {
      id: 7,
      name: '嵌入式Linux开发环境搭建',
      type: 'document',
      subject: '嵌入式Linux开发',
      creator: '李强',
      size: 1024 * 1024 * 5.5, // 5.5MB
      createTime: '2023-06-21 15:30:00',
      downloadCount: 42
    },
    {
      id: 8,
      name: '设备驱动开发实例',
      type: 'video',
      subject: '嵌入式Linux开发',
      creator: '李强',
      size: 1024 * 1024 * 180, // 180MB
      createTime: '2023-06-22 10:45:00',
      downloadCount: 36
    }
  ]
  
  // 更新分页
  pagination.total = resourceList.value.length
  tableLoading.value = false
}

// 删除资源
const deleteResource = (row) => {
  ElMessageBox.confirm(
    `确定要删除资源 "${row.name}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    resourceList.value = resourceList.value.filter(item => item.id !== row.id)
    pagination.total = resourceList.value.length
    ElMessage.success('资源删除成功')
  }).catch(() => {})
}

// 下载资源
const downloadResource = (row) => {
  ElMessage.success(`开始下载: ${row.name}`)
  // 实际应用中应该调用API下载文件
  
  // 更新下载次数
  const index = resourceList.value.findIndex(item => item.id === row.id)
  if (index !== -1) {
    resourceList.value[index].downloadCount++
  }
}

// 批量导出
const exportResources = () => {
  const selectedSubject = filters.subject || '全部'
  ElMessage.success(`正在导出${selectedSubject}学科的资源`)
  // 实际应用中应该调用API导出资源
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(1) + ' KB'
  } else if (size < 1024 * 1024 * 1024) {
    return (size / 1024 / 1024).toFixed(1) + ' MB'
  } else {
    return (size / 1024 / 1024 / 1024).toFixed(1) + ' GB'
  }
}

// 获取资源类型图标
const getResourceIcon = (type) => {
  switch (type) {
    case 'document':
      return Document
    case 'video':
      return VideoCamera
    case 'image':
      return Picture
    case 'exercise':
      return Folder
    default:
      return Document
  }
}

// 获取资源类型文本
const getResourceTypeText = (type) => {
  const resourceType = resourceTypes.find(item => item.value === type)
  return resourceType ? resourceType.label : '未知'
}

// 分页变化
const handleCurrentChange = (val) => {
  pagination.currentPage = val
}

// 每页条数变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.currentPage = 1
}
</script>

<template>
  <div class="resource-management-container">
    <div class="page-header">
      <h2>课件资源管理</h2>
      <div class="header-actions">
        <el-button type="primary" :icon="Download" @click="exportResources">批量导出</el-button>
      </div>
    </div>
    
    <div class="filter-section">
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索资源名称、学科或创建者"
          class="search-input"
          :prefix-icon="Search"
          @keyup.enter="searchResources"
        />
        <el-button type="primary" @click="searchResources">搜索</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </div>
      
      <div class="filters">
        <el-select v-model="filters.subject" placeholder="按学科筛选" clearable @change="searchResources">
          <el-option
            v-for="subject in subjects"
            :key="subject"
            :label="subject"
            :value="subject"
          />
        </el-select>
        
        <el-select v-model="filters.type" placeholder="按类型筛选" clearable @change="searchResources">
          <el-option
            v-for="type in resourceTypes"
            :key="type.value"
            :label="type.label"
            :value="type.value"
          />
        </el-select>
        
        <el-select v-model="filters.creator" placeholder="按创建者筛选" clearable @change="searchResources">
          <el-option
            v-for="creator in creators"
            :key="creator"
            :label="creator"
            :value="creator"
          />
        </el-select>
      </div>
    </div>
    
    <el-table
      :data="resourceList.slice((pagination.currentPage - 1) * pagination.pageSize, pagination.currentPage * pagination.pageSize)"
      style="width: 100%"
      v-loading="tableLoading"
      border
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="资源名称" min-width="200">
        <template #default="scope">
          <div class="resource-name">
            <el-icon class="resource-icon">
              <component :is="getResourceIcon(scope.row.type)" />
            </el-icon>
            <span>{{ scope.row.name }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="100">
        <template #default="scope">
          {{ getResourceTypeText(scope.row.type) }}
        </template>
      </el-table-column>
      <el-table-column prop="subject" label="所属学科" width="150" />
      <el-table-column prop="creator" label="创建者" width="120" />
      <el-table-column label="大小" width="100">
        <template #default="scope">
          {{ formatFileSize(scope.row.size) }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column prop="downloadCount" label="下载次数" width="100" sortable />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button :icon="Download" size="small" type="primary" @click="downloadResource(scope.row)" />
          <el-button :icon="Delete" type="danger" size="small" @click="deleteResource(scope.row)" />
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped>
.resource-management-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.filter-section {
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  margin-bottom: 15px;
  gap: 10px;
}

.search-input {
  width: 300px;
}

.filters {
  display: flex;
  gap: 15px;
}

.filters .el-select {
  width: 180px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.resource-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.resource-icon {
  font-size: 18px;
  color: #409EFF;
}
</style> 