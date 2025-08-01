<template>
  <div class="message-log-container">
    <div class="filter-container">
      <el-input
        v-model="searchQuery"
        placeholder="搜索会话ID/用户ID/消息内容"
        style="width: 250px;"
        class="filter-item"
        clearable
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      />
      <el-select
        v-model="messageType"
        placeholder="消息类型"
        style="width: 120px; margin-left: 10px;"
        class="filter-item"
        clearable
      >
        <el-option label="文本" value="text" />
        <el-option label="图片" value="image" />
        <el-option label="视频" value="video" />
        <el-option label="文件" value="file" />
      </el-select>
      <el-select
        v-model="statusFilter"
        placeholder="状态"
        style="width: 120px; margin-left: 10px;"
        class="filter-item"
        clearable
      >
        <el-option label="成功" value="success" />
        <el-option label="失败" value="failed" />
        <el-option label="待发送" value="pending" />
      </el-select>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        style="width: 300px; margin-left: 10px;"
        class="filter-item"
        value-format="YYYY-MM-DD"
      />
      <el-button 
        class="filter-item" 
        type="primary" 
        style="margin-left: 10px;"
        @click="handleSearch"
      >
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
      <el-button 
        class="filter-item" 
        style="margin-left: 10px;"
        @click="resetSearch"
      >
        <el-icon><Refresh /></el-icon>
        重置
      </el-button>
    </div>
    
    <el-table
      :data="filteredMessageLogs"
      style="width: 100%; margin-top: 20px;" 
      highlight-current-row
      v-loading="loading"
      @sort-change="handleSortChange"
    >
      <el-table-column prop="sessionId" label="会话ID" width="180" sortable />
      <el-table-column prop="userId" label="用户ID" width="120" sortable />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="messageType" label="消息类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getMessageTypeTag(row.messageType)">
            {{ formatMessageType(row.messageType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="消息内容" min-width="200">
        <template #default="{ row }">
          <div class="message-content">
            {{ truncateContent(row.content) }}
            <el-popover
              v-if="row.content.length > 50"
              placement="top-start"
              width="300"
              trigger="hover"
              :content="row.content"
            >
              <template #reference>
                <el-link type="primary" :underline="false">查看全部</el-link>
              </template>
            </el-popover>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="timestamp" label="时间" width="180" sortable>
        <template #default="{ row }">
          {{ formatDateTime(row.timestamp) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status)">
            {{ formatStatus(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="text" size="small" @click="viewDetails(row)">
            详情
          </el-button>
          <el-button 
            v-if="row.status === 'failed'" 
            type="text" 
            size="small" 
            @click="retrySend(row)"
          >
            重发
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 20px; text-align: right;"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
    />
    
    <!-- 消息详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="消息详情" width="50%">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="会话ID">{{ currentDetail.sessionId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentDetail.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentDetail.username }}</el-descriptions-item>
        <el-descriptions-item label="消息类型">
          <el-tag :type="getMessageTypeTag(currentDetail.messageType)">
            {{ formatMessageType(currentDetail.messageType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="消息内容">
          <div style="word-break: break-all;">{{ currentDetail.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ formatDateTime(currentDetail.timestamp) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(currentDetail.status)">
            {{ formatStatus(currentDetail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentDetail.errorMsg" label="错误信息">
          <el-alert :title="currentDetail.errorMsg" type="error" :closable="false" />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

// 搜索和过滤条件
const searchQuery = ref('')
const messageType = ref('')
const statusFilter = ref('')
const dateRange = ref([])
const sortProp = ref('timestamp')
const sortOrder = ref('descending')

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 对话框控制
const detailDialogVisible = ref(false)
const currentDetail = reactive({
  sessionId: '',
  userId: '',
  username: '',
  messageType: '',
  content: '',
  timestamp: '',
  status: '',
  errorMsg: ''
})

// 示例数据
const messageLogs = reactive([
  {
    sessionId: 'sess_001_20230815',
    userId: 'user_001',
    username: '张三',
    messageType: 'text',
    content: '你好，我想咨询一下产品的价格信息，能否提供详细的价格表？',
    timestamp: '2023-08-15T09:12:34Z',
    status: 'success'
  },
  {
    sessionId: 'sess_002_20230815',
    userId: 'user_002',
    username: '李四',
    messageType: 'image',
    content: 'product_image_001.jpg',
    timestamp: '2023-08-15T10:23:45Z',
    status: 'success'
  },
  {
    sessionId: 'sess_003_20230815',
    userId: 'user_003',
    username: '王五',
    messageType: 'text',
    content: '我的订单号是ORD20230815001，为什么还没有发货？',
    timestamp: '2023-08-15T11:34:56Z',
    status: 'success'
  },
  {
    sessionId: 'sess_004_20230815',
    userId: 'user_004',
    username: '赵六',
    messageType: 'file',
    content: 'contract_draft_v2.pdf',
    timestamp: '2023-08-15T14:45:12Z',
    status: 'failed',
    errorMsg: '文件大小超过限制'
  },
  {
    sessionId: 'sess_005_20230816',
    userId: 'user_005',
    username: '钱七',
    messageType: 'video',
    content: 'product_demo.mp4',
    timestamp: '2023-08-16T09:15:30Z',
    status: 'pending'
  },
  {
    sessionId: 'sess_006_20230816',
    userId: 'user_006',
    username: '孙八',
    messageType: 'text',
    content: '感谢你们的服务，问题已经解决了！',
    timestamp: '2023-08-16T13:20:45Z',
    status: 'success'
  },
  {
    sessionId: 'sess_007_20230816',
    userId: 'user_007',
    username: '周九',
    messageType: 'text',
    content: '我需要取消我的订阅，请问如何操作？',
    timestamp: '2023-08-16T15:30:10Z',
    status: 'success'
  },
  {
    sessionId: 'sess_008_20230817',
    userId: 'user_008',
    username: '吴十',
    messageType: 'image',
    content: 'error_screenshot.png',
    timestamp: '2023-08-17T10:05:20Z',
    status: 'success'
  },
  {
    sessionId: 'sess_009_20230817',
    userId: 'user_009',
    username: '郑十一',
    messageType: 'text',
    content: '我想预约下周二的演示会议，上午10点可以吗？',
    timestamp: '2023-08-17T11:40:35Z',
    status: 'failed',
    errorMsg: '网络连接超时'
  },
  {
    sessionId: 'sess_010_20230817',
    userId: 'user_010',
    username: '王十二',
    messageType: 'file',
    content: 'requirements.docx',
    timestamp: '2023-08-17T16:25:50Z',
    status: 'success'
  }
])

// 计算属性 - 过滤后的数据
const filteredMessageLogs = computed(() => {
  let result = [...messageLogs]
  
  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(item => 
      item.sessionId.toLowerCase().includes(query) ||
      item.userId.toLowerCase().includes(query) ||
      item.username.toLowerCase().includes(query) ||
      item.content.toLowerCase().includes(query)
    )
  }
  
  // 消息类型过滤
  if (messageType.value) {
    result = result.filter(item => item.messageType === messageType.value)
  }
  
  // 状态过滤
  if (statusFilter.value) {
    result = result.filter(item => item.status === statusFilter.value)
  }
  
  // 日期范围过滤
  if (dateRange.value && dateRange.value.length === 2) {
    const start = new Date(dateRange.value[0])
    const end = new Date(dateRange.value[1])
    end.setHours(23, 59, 59, 999) // 包含结束日期的全天
    
    result = result.filter(item => {
      const itemDate = new Date(item.timestamp)
      return itemDate >= start && itemDate <= end
    })
  }
  
  // 排序
  if (sortProp.value) {
    result.sort((a, b) => {
      const valA = a[sortProp.value]
      const valB = b[sortProp.value]
      
      if (valA < valB) {
        return sortOrder.value === 'ascending' ? -1 : 1
      }
      if (valA > valB) {
        return sortOrder.value === 'ascending' ? 1 : -1
      }
      return 0
    })
  }
  
  // 更新总数
  total.value = result.length
  
  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  return result.slice(start, start + pageSize.value)
})

// 格式化日期时间
const formatDateTime = (timestamp) => {
  return dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss')
}

// 格式化消息类型
const formatMessageType = (type) => {
  const types = {
    text: '文本',
    image: '图片',
    video: '视频',
    file: '文件'
  }
  return types[type] || type
}

// 格式化状态
const formatStatus = (status) => {
  const statusMap = {
    success: '成功',
    failed: '失败',
    pending: '待发送'
  }
  return statusMap[status] || status
}

// 获取消息类型标签样式
const getMessageTypeTag = (type) => {
  const typeMap = {
    text: '',
    image: 'success',
    video: 'warning',
    file: 'info'
  }
  return typeMap[type] || ''
}

// 获取状态标签样式
const getStatusTag = (status) => {
  const statusMap = {
    success: 'success',
    failed: 'danger',
    pending: 'warning'
  }
  return statusMap[status] || ''
}

// 截断长内容
const truncateContent = (content) => {
  return content.length > 50 ? content.substring(0, 50) + '...' : content
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  // 这里实际项目中应该调用API获取数据
  // fetchMessageLogs()
}

// 重置搜索
const resetSearch = () => {
  searchQuery.value = ''
  messageType.value = ''
  statusFilter.value = ''
  dateRange.value = []
  currentPage.value = 1
  sortProp.value = 'timestamp'
  sortOrder.value = 'descending'
  // fetchMessageLogs()
}

// 分页变化
const handlePageChange = (val) => {
  currentPage.value = val
  // fetchMessageLogs()
}

// 每页条数变化
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  // fetchMessageLogs()
}

// 排序变化
const handleSortChange = ({ prop, order }) => {
  sortProp.value = prop
  sortOrder.value = order
  // fetchMessageLogs()
}

// 查看详情
const viewDetails = (row) => {
  Object.assign(currentDetail, row)
  detailDialogVisible.value = true
}

// 重发消息
const retrySend = (row) => {
  ElMessageBox.confirm('确定要重新发送这条消息吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 实际项目中调用API重发
    loading.value = true
    setTimeout(() => {
      row.status = 'success'
      row.errorMsg = ''
      loading.value = false
      ElMessage.success('消息重发成功')
    }, 1000)
  }).catch(() => {
    // 取消操作
  })
}

// 模拟API获取数据
const fetchMessageLogs = () => {
  loading.value = true
  // 实际项目中这里应该是API调用
  setTimeout(() => {
    loading.value = false
  }, 500)
}

onMounted(() => {
  // 初始化加载数据
  // fetchMessageLogs()
  total.value = messageLogs.length
})
</script>

<style scoped>
.message-log-container {
  padding: 20px;
}

.filter-container {
  margin-bottom: 20px;
}

.filter-item {
  margin-right: 10px;
}

.message-content {
  display: flex;
  align-items: center;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>