<template>
  <div class="recent-documents">
    <h2 class="title">最近</h2>
    <el-table
      :data="files"
      style="width: 100%"
      :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      :cell-style="{ padding: '12px 0' }"
      @row-click="handleRowClick"
    > 
      <el-table-column prop="filename" label="文件名称" min-width="200">
        <template #default="{ row }">
          <div class="filename" @click.stop="handleShare(row)">
            <i :class="getFileIcon(row.filename)" class="file-icon"></i> {{ row.filename }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="modifiedTime" label="修改日期" width="220" align="center">
        <template #default="{ row }">
          {{ formatDate(row.modifiedTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="size" label="大小" width="100" align="center" />
      <el-table-column width="120" label="" align="center">
        <template #default="{ row }">
          <el-button 
            text 
            bg 
            type="primary"
            class="share-button"
            @click.stop="handleShare(row)"
          > 
            <i class="fa-solid fa-paper-plane"></i>&nbsp;查看
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const router = useRouter()
const route = useRoute()

const sceneId = ref(route.query.sceneId)
const channelStreamId = ref(route.query.channelStreamId);

const files = ref([
  {
    filename: "AI智能文档功能设计.docx",
    storage: "My Cloud Documents",
    owner: "Me",
    modifiedTime: "2023-07-17 06:18:00", // 今天
    size: "13 KB"
  },
  {
    filename: "党群口纪要发文稿纸（红头模板）.doc",
    storage: "My Cloud Documents",
    owner: "Me",
    modifiedTime: "2023-07-16 14:39:00", // 昨天
    size: "48 KB"
  },
  {
    filename: "党委办公室-文件（红头模板）.doc",
    storage: "My Cloud Documents",
    owner: "Me",
    modifiedTime: "2023-07-15 14:39:00", // 前天
    size: "32 KB"
  },
  {
    filename: "党委工作部部门发文稿纸--电子.doc",
    storage: "My Cloud Documents",
    owner: "Me",
    modifiedTime: "2023-07-12 14:39:00", // 较早日期
    size: "37 KB"
  },
  {
    filename: "党工部文件-纸质.doc",
    storage: "My Cloud Documents",
    owner: "Me",
    modifiedTime: "2023-07-11 14:39:00",
    size: "31 KB"
  },
  {
    filename: "党工部文件-电子.doc",
    storage: "My Cloud Documents",
    owner: "Me",
    modifiedTime: "2023-07-10 14:39:00",
    size: "32 KB"
  },
  {
    filename: "办公室函件-纸质.doc",
    storage: "My Cloud Documents",
    owner: "Me",
    modifiedTime: "2023-07-09 14:39:00",
    size: "32 KB"
  },
  {
    filename: "办公室函件-电子.doc",
    storage: "My Cloud Documents",
    owner: "Me",
    modifiedTime: "2023-07-08 14:39:00",
    size: "34 KB"
  },
  {
    filename: "办公室部门发文稿纸--电子.doc",
    storage: "My Cloud Documents",
    owner: "Me",
    modifiedTime: "2023-07-07 14:39:00",
    size: "45 KB"
  },
  {
    filename: "党委办公室-函件（红头模板）.doc",
    storage: "My Cloud Documents",
    owner: "Me",
    modifiedTime: "2023-07-06 14:39:00",
    size: "29 KB"
  },
  {
    filename: "党群口文件发文稿纸（红头模板）.doc",
    storage: "My Cloud Documents",
    owner: "Me",
    modifiedTime: "2023-07-05 14:39:00",
    size: "49 KB"
  }
]);

// 改进后的人性化日期格式化函数
const formatDate = (dateString) => {
  const now = new Date();
  const date = new Date(dateString);
  
  // 获取年月日，忽略时分秒
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const yesterday = new Date(today);
  yesterday.setDate(yesterday.getDate() - 1);
  const dayBeforeYesterday = new Date(yesterday);
  dayBeforeYesterday.setDate(dayBeforeYesterday.getDate() - 1);
  
  const targetDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  
  // 时间部分格式化
  const timeStr = date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit',
    hour12: false // 使用24小时制
  }).replace(/^24:/, '00:');
  
  if (targetDate.getTime() === today.getTime()) {
    return `今天 ${timeStr}`;
  } else if (targetDate.getTime() === yesterday.getTime()) {
    return `昨天 ${timeStr}`;
  } else if (targetDate.getTime() === dayBeforeYesterday.getTime()) {
    return `前天 ${timeStr}`;
  } else if ((today - targetDate) / (1000 * 60 * 60 * 24) <= 7) {
    // 一周内显示星期几
    const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
    return `${weekdays[date.getDay()]} ${timeStr}`;
  } else {
    // 超过一周显示具体日期
    const month = date.getMonth() + 1;
    const day = date.getDate();
    return `${month}月${day}日 ${timeStr}`;
  }
};

const getFileIcon = (fileName) => {
  const extension = fileName.split('.').pop().toLowerCase();
  switch(extension) {
    case 'doc':
    case 'docx':
      return 'fa-solid fa-file-word text-blue-600';
    case 'ppt':
    case 'pptx':
      return 'fa-regular fa-file-powerpoint text-orange-600';
    case 'pdf':
      return 'fa-regular fa-file-pdf text-red-600';
    default:
      return 'fa-regular fa-file text-gray-500';
  }
};

const handleRowClick = (row) => {
  console.log('File clicked:', row.filename);
  // Add your file open logic here
};

const handleShare = (row) => {
  console.log('Share file:', row.filename);
  // Add your share logic here
  router.push({
    path: '/scene/contentFormatter/contentParser',
    query: {
      sceneId: sceneId.value,
      channelStreamId: channelStreamId.value 
    }
  })
};
</script>

<style lang="scss" scoped>
.recent-documents {
  max-width: 100%;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  
  .title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 1px solid #eee;
  }
  
  .file-icon {
    font-size: 24px;
    margin-left: 8px;
    color: #1d75b0;
  }
  
  .filename {
    font-size: 14px;
    font-weight: 500;
    color: #333;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: flex;
    gap:10px;
    align-items: center;
  }
  
  .storage-location {
    font-size: 12px;
    color: #909399;
    margin-top: 2px;
  }
  
  .share-button {
    &:hover {
      color: #0d5fd9;
      background-color: #f0f7ff;
    }
  }
  
  :deep(.el-table__row) {
    cursor: pointer;
    
    &:hover {
      background-color: #f5f7fa;
    }
  }
  
  :deep(.el-table__cell) {
    .cell {
      padding-left: 8px;
      padding-right: 8px;
    }
  }
}

@media (max-width: 768px) {
  .recent-documents {
    padding: 12px;
    
    :deep(.el-table__cell) {
      padding: 8px 0;
      
      .cell {
        padding-left: 4px;
        padding-right: 4px;
      }
    }
  }
}
</style>