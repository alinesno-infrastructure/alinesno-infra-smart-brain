<template>
  <div class="message-referece-panel">
    <el-drawer
      v-model="drawer"
      title="引用内容"
      :direction="direction"
      :modal="false"
      :before-close="handleClose"
      size="25%"
    >
      <el-scrollbar class="drawer-scrollbar">
        <div class="reference-content">
          <div 
            v-for="(item, index) in references" 
            :key="index" 
            class="reference-item"
            :class="{ 'is-expanded': expandedItems[index] }"
          >
            <div class="reference-header" @click="toggleExpand(index)">
              <el-tag :type="getTagType(item.type)" size="default">{{ item.type }}</el-tag>
              <span class="reference-title">{{ item.name }}</span>
              <span class="reference-source">{{ item.source }}</span>
              <el-icon class="expand-icon">
                <ArrowDown v-if="!expandedItems[index]" />
                <ArrowUp v-else />
              </el-icon>
            </div>
            <el-collapse-transition>
              <div v-show="expandedItems[index]" class="reference-body">
                <div class="content-preview">
                  {{ item.content }}
                </div>
                <div class="reference-meta">
                  <span>文本长度: {{ item.textLength }}</span>
                  <span>排序: {{ item.sort }}</span>
                </div>
              </div>
            </el-collapse-transition>
          </div>
        </div>

        <div v-if="references.length == 0" style="
          display: flex;
          align-items: center;
          justify-content: center;
          height: 500px;">
          <el-empty image-size="120" description="消息引用内容(知识库)为空，主要由大模型生成内容。" />
        </div>
      </el-scrollbar>
    </el-drawer>
  </div>
</template>


<script setup>
import { ref } from 'vue'

import {
	getByMessageId
} from "@/api/base/im/messageRerence";

const drawer = ref(false)
const direction = 'rtl'
const expandedItems = ref([]) // 控制每个项目的展开状态

// 示例数据
const references = ref([])

const openReferece = (item) => {
  console.log('openRefereceItem = ', item.messageId)
  drawer.value = true
  handleGetByMessageId(item.messageId)
}

const handleClose = (done) => {
  done()
}

const getTagType = (type) => {
  const map = {
    '知识库': 'success',
    '链接': 'primary',
    '记忆': 'warning',
    '附件': 'info'
  }
  return map[type] || ''
}


// 初始化所有项目为折叠状态
references.value.forEach((_, index) => {
  expandedItems.value[index] = true
})

const toggleExpand = (index) => {
  expandedItems.value[index] = !expandedItems.value[index]
}

const handleGetByMessageId = (messageId) =>{
	getByMessageId(messageId).then(res => {
		console.log('res = ' + res);
		references.value = res.data ;

		references.value.forEach((_, index) => {
		  expandedItems.value[index] = true
		})
	})
}

onMounted(() => { 
})

defineExpose({
  openReferece
})
</script>

<style lang="scss" scoped>
.message-referece-panel {
  :deep(.el-drawer) {
    height: 85vh !important;
    position: fixed;
    bottom: 20px !important;
    right: 20px !important;
    top: auto !important;
    left: auto !important;
    border-radius: 10px;
    // box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
    
    .el-drawer__header {
      margin-bottom: 0;
      padding: 16px;
      border-bottom: 1px solid #f0f0f0;
      border-radius:5px;
      font-weight: bold;
    }
    
    .el-drawer__body {
      height: calc(100% - 60px);
      padding: 0;
    }
  }

  .drawer-scrollbar {
    height: 100%;
    
    :deep(.el-scrollbar__wrap) {
      padding-bottom: 20px; // 留出滚动空间
    }
  }

  .reference-content {
    padding: 6px;
  }

  .reference-item {
    border: 0px solid #ebeef5;
    border-radius: 4px;
    overflow: hidden;
    transition: all 0.3s;
    background: #fafafa;
    margin-bottom: 16px;

    &:hover {
      // box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }

    &.is-expanded {
      .reference-header {
        // border-bottom: 1px solid #ebeef5;
      }
    }
  }

  .reference-header {
    display: flex;
    align-items: center;
    padding: 12px;
    background-color: #f5f7fa;
    cursor: pointer;
    user-select: none;
    font-size: 14px;
    transition: all 0.3s;
    
    &:hover {
      background-color: #ebedf0;
    }
    
    .el-tag {
      margin-right: 8px;
      flex-shrink: 0;
    }
    
    .reference-title {
      flex: 1;
      font-weight: 500;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-right: 8px;
      font-size: 14px;
    }
    
    .reference-source {
      color: #909399;
      font-size: 12px;
      flex-shrink: 0;
      margin-right: 8px;
    }
    
    .expand-icon {
      color: #909399;
      transition: transform 0.3s;
    }
  }

  .reference-body {
    padding: 12px;
    
    .content-preview {
      color: #606266;
      line-height: 1.6;
      font-size: 14px;
      white-space: pre-wrap;
    }
    
    .reference-meta {
      display: flex;
      justify-content: space-between;
      color: #909399;
      font-size: 12px;
      margin-top: 8px;
    }
  }
}
</style>