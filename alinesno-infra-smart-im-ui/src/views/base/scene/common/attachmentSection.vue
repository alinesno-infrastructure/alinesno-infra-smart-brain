<template>
  <div class="attachment-section">
    <div 
      v-for="(item, index) in attachments" 
      :key="index" 
      class="attachment-item">
      <!-- 附件图标区域 -->
      <div class="attachment-item-icon">
        <i 
          :class="['fa-solid', item.iconClass]" 
          :title="item.fileType"
        ></i>
      </div>

      <!-- 附件名称及大小区域 -->
      <div class="attachment-item-info">
        <div class="attachment-item-name">
          <span class="attachment-item-name-text">
            {{ item.fileName }}
          </span>
          <span class="attachment-size">
            {{ item.fileSize }}M
          </span>
        </div>
      </div>

      <!-- 删除按钮区域（默认隐藏，悬停显示） -->
      <div  class="attachment-item-delete"  @click="handleDelete(index)">
        <i class="fa-solid fa-xmark" title="删除附件"></i>
      </div>
    </div>
  </div>
</template>

<script setup>
// 模拟生产环境数据（增加fileSize字段）
const attachments = ref([
  {
    fileType: 'PDF文件',
    fileName: '年度报告.pdf',
    fileSize: 23.5,
    iconClass: 'fa-file-pdf' // PDF图标
  },
  {
    fileType: 'Excel表格',
    fileName: '销售数据.xlsx',
    fileSize: 23.5,
    iconClass: 'fa-file-excel' // Excel图标
  },
  {
    fileType: 'Word文档',
    fileSize: 23.5,
    fileName: '会议纪要.docx',
    iconClass: 'fa-file-word' // Word图标
  }
]);

// 鼠标状态管理
const mouseEnterIndex = ref(-1);
const isDeleteVisible = (index) => index === mouseEnterIndex.value;

// 删除附件方法
const handleDelete = (index) => {
  attachments.value.splice(index, 1);
  console.log(`已删除第 ${index + 1} 个附件`);
};
</script>

<style lang="scss" scoped>
.attachment-section {
  display: flex;
  gap: 12px;
  border-radius: 8px;
  flex-wrap: wrap; // 允许容器内元素换行
  
  .attachment-item {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 8px;
    padding: 12px 16px;
    background: #fafafa;
    border-radius: 10px;
    transition: transform 0.2s ease;
    min-width: 180px;
    max-width: calc(25% - 10px);
    width: 100%;
    
    // 鼠标悬停效果
    &:hover {
      cursor: pointer;
      // transform: translateX(4px);
      // box-shadow: 0 2px 8px rgba(0, 123, 255, 0.1);
      
      .attachment-item-delete {
        display: block; // 悬停时显示删除按钮
      }
    }
    
    // 附件图标区域
    .attachment-item-icon {
      font-size: 1.25rem;
      // color: #007bff;
    }
    
    // 附件信息区域
    .attachment-item-info {
      width: 100%;
      
      .attachment-item-name {
        display: flex;
        justify-content: center;
        align-items: flex-start;
        width: 100%;
        flex-direction: column;
        
        .attachment-item-name-text {
          font-size: 0.9rem;
          color: #333;
          max-width: calc(100% - 0px);
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: normal;
          line-height: 1.4;
        }
        
        .attachment-size {
          font-size: 0.8rem;
          color: #6c757d;
        }
      }
    }
    
    // 删除按钮区域
    .attachment-item-delete {
      font-size: 1.1rem;
      color: #dc3545;
      cursor: pointer;
      transition: color 0.2s ease;
      display: none; // 默认隐藏
      
      &:hover {
        color: #bd2130;
      }
    }
  }
}
</style>