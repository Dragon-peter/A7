<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'

// 用户列表
const userList = ref([])

// 搜索关键词
const searchKeyword = ref('')

// 表格加载状态
const tableLoading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const isEdit = ref(false)

// 表单数据
const form = reactive({
  id: null,
  name: '',
  password: '',
  role: 'student',
  email: '',
  phone: '',
  createTime: '',
  lastLoginTime: ''
})

// 表单规则
const rules = {
  name: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 表单引用
const formRef = ref(null)

// 分页配置
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 初始化数据
onMounted(() => {
  loadUserData()
})

// 加载用户数据
const loadUserData = () => {
  tableLoading.value = true
  
  // 从localStorage获取用户数据
  const storedUsers = JSON.parse(localStorage.getItem('users') || '[]')
  
  // 添加默认系统用户
  const systemUsers = [
    {
      id: 1,
      name: 'admin',
      password: 'admin',
      role: 'admin',
      email: 'admin@example.com',
      phone: '13800000000',
      createTime: '2023-01-01 00:00:00',
      lastLoginTime: '2023-06-15 08:30:00'
    },
    {
      id: 2,
      name: 'teacher',
      password: 'teacher',
      role: 'teacher',
      email: 'teacher@example.com',
      phone: '13900000000',
      createTime: '2023-01-02 00:00:00',
      lastLoginTime: '2023-06-14 10:15:00'
    }
  ]
  
  // 合并系统用户和注册用户
  const allUsers = [...systemUsers]
  
  // 为注册用户添加ID和其他信息
  storedUsers.forEach((user, index) => {
    allUsers.push({
      id: index + 3, // 从3开始，避免与系统用户ID冲突
      name: user.name,
      password: user.password,
      role: user.role || 'student',
      email: user.email || `${user.name}@example.com`,
      phone: user.phone || '',
      createTime: user.createTime || new Date().toLocaleString(),
      lastLoginTime: user.lastLoginTime || '-'
    })
  })
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    userList.value = allUsers.filter(user => 
      user.name.toLowerCase().includes(keyword) || 
      user.email.toLowerCase().includes(keyword) ||
      user.role.toLowerCase().includes(keyword)
    )
  } else {
    userList.value = allUsers
  }
  
  pagination.total = userList.value.length
  
  setTimeout(() => {
    tableLoading.value = false
  }, 300)
}

// 搜索用户
const searchUsers = () => {
  pagination.currentPage = 1
  loadUserData()
}

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = ''
  pagination.currentPage = 1
  loadUserData()
}

// 打开添加用户对话框
const openAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '添加用户'
  resetForm()
  dialogVisible.value = true
}

// 打开编辑用户对话框
const openEditDialog = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  
  // 复制用户数据到表单
  Object.keys(form).forEach(key => {
    if (key in row) {
      form[key] = row[key]
    }
  })
  
  dialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      if (isEdit.value) {
        // 编辑用户
        updateUser()
      } else {
        // 添加用户
        addUser()
      }
    }
  })
}

// 添加用户
const addUser = () => {
  // 检查用户名是否已存在
  const existingUser = userList.value.find(u => u.name === form.name)
  if (existingUser) {
    ElMessage.error('用户名已存在')
    return
  }
  
  // 创建新用户
  const newUser = {
    id: userList.value.length > 0 ? Math.max(...userList.value.map(u => u.id)) + 1 : 1,
    name: form.name,
    role: form.role,
    email: form.email || `${form.name}@example.com`,
    phone: form.phone || '',
    createTime: new Date().toLocaleString(),
    lastLoginTime: '-'
  }
  
  // 更新本地存储
  const storedUsers = JSON.parse(localStorage.getItem('users') || '[]')
  storedUsers.push({
    name: form.name,
    password: form.password,
    role: form.role
  })
  localStorage.setItem('users', JSON.stringify(storedUsers))
  
  // 更新列表
  userList.value.push(newUser)
  pagination.total = userList.value.length
  
  ElMessage.success('用户添加成功')
  dialogVisible.value = false
}

// 更新用户
const updateUser = () => {
  // 检查用户名是否与其他用户重复
  const duplicateUser = userList.value.find(u => u.name === form.name && u.id !== form.id)
  if (duplicateUser) {
    ElMessage.error('用户名已存在')
    return
  }
  
  // 更新用户列表
  const index = userList.value.findIndex(u => u.id === form.id)
  if (index !== -1) {
    userList.value[index] = {
      ...userList.value[index],
      name: form.name,
      role: form.role,
      email: form.email,
      phone: form.phone
    }
    
    // 更新本地存储
    if (form.id > 2) { // 只更新非系统用户
      const storedUsers = JSON.parse(localStorage.getItem('users') || '[]')
      const userIndex = storedUsers.findIndex(u => u.name === userList.value[index].name)
      if (userIndex !== -1) {
        storedUsers[userIndex] = {
          ...storedUsers[userIndex],
          name: form.name,
          role: form.role,
          ...(form.password ? { password: form.password } : {})
        }
        localStorage.setItem('users', JSON.stringify(storedUsers))
      }
    }
    
    ElMessage.success('用户更新成功')
    dialogVisible.value = false
  }
}

// 删除用户
const deleteUser = (row) => {
  // 系统用户不可删除
  if (row.id <= 2) {
    ElMessage.warning('系统用户不可删除')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除用户 "${row.name}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 从列表中删除
    userList.value = userList.value.filter(u => u.id !== row.id)
    pagination.total = userList.value.length
    
    // 从本地存储中删除
    const storedUsers = JSON.parse(localStorage.getItem('users') || '[]')
    const filteredUsers = storedUsers.filter(u => u.name !== row.name)
    localStorage.setItem('users', JSON.stringify(filteredUsers))
    
    ElMessage.success('用户删除成功')
  }).catch(() => {})
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  
  form.id = null
  form.name = ''
  form.password = ''
  form.role = 'student'
  form.email = ''
  form.phone = ''
  form.createTime = ''
  form.lastLoginTime = ''
}

// 获取角色标签类型
const getRoleTagType = (role) => {
  switch (role) {
    case 'admin':
      return 'danger'
    case 'teacher':
      return 'success'
    default:
      return 'info'
  }
}

// 获取角色名称
const getRoleName = (role) => {
  switch (role) {
    case 'admin':
      return '管理员'
    case 'teacher':
      return '教师'
    default:
      return '学生'
  }
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
  <div class="user-management-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" :icon="Plus" @click="openAddDialog">添加用户</el-button>
    </div>
    
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户名、邮箱或角色"
        class="search-input"
        :prefix-icon="Search"
        @keyup.enter="searchUsers"
      />
      <el-button type="primary" @click="searchUsers">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>
    
    <el-table
      :data="userList.slice((pagination.currentPage - 1) * pagination.pageSize, pagination.currentPage * pagination.pageSize)"
      style="width: 100%"
      v-loading="tableLoading"
      border
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="用户名" min-width="120" />
      <el-table-column prop="password" label="密码" min-width="120" />
      <el-table-column prop="role" label="角色" width="120">
        <template #default="scope">
          <el-tag :type="getRoleTagType(scope.row.role)">
            {{ getRoleName(scope.row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" min-width="180" />
      <el-table-column prop="phone" label="电话" width="150" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column prop="lastLoginTime" label="最后登录" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <el-button :icon="Edit" size="small" @click="openEditDialog(scope.row)" />
          <el-button 
            :icon="Delete" 
            type="danger" 
            size="small" 
            @click="deleteUser(scope.row)" 
            :disabled="scope.row.id <= 2"
          />
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
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="name">
          <el-input v-model="form.name" placeholder="请输入用户名" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        
        <el-form-item label="新密码" v-else>
          <el-input v-model="form.password" type="password" placeholder="不修改请留空" show-password />
        </el-form-item>
        
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="学生" value="student" />
            <el-option label="教师" value="teacher" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-management-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  margin-bottom: 20px;
  gap: 10px;
}

.search-input {
  width: 300px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style> 