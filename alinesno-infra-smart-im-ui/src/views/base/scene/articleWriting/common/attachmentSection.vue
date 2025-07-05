<template>

  <div>
    <!-- 隐藏的上传控件 -->
    <el-upload ref="uploadRef" class="hidden-upload" :action="uploadUrl" :headers="headers"
      :on-success="handleUploadSuccess" :on-error="handleUploadError" :before-upload="beforeUpload"
      :show-file-list="false" :multiple="true" :limit="5" :on-exceed="handleExceed" :file-list="fileList"
      :on-progress="handleUploadProgress" />

    <div class="attachment-section" v-if="attachments.length > 0">

      <div v-for="(item, index) in attachments" :key="index" class="attachment-item">

        <!-- 上传进度显示 -->
        <div v-if="item.uploading" class="upload-progress">
          <el-progress :percentage="item.progress" :stroke-width="4" :show-text="false" />
        </div>

        <!-- 附件图标区域 -->
        <div class="attachment-item-icon">
          <i :class="['fa-solid', item.iconClass]" :title="item.fileType"></i>
        </div>

        <!-- 附件名称及大小区域 -->
        <div class="attachment-item-info">
          <div class="attachment-item-name">
            <span class="attachment-item-name-text">
              {{ truncateString(item.fileName, 5) }}
            </span>
            <span class="attachment-size">
              {{ item.fileSize }}M
            </span>
          </div>
        </div>

        <!-- 删除按钮区域（默认隐藏，悬停显示） -->
        <div class="attachment-item-delete" @click="handleDelete(index)">
          <i class="fa-solid fa-xmark" title="删除附件"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>

import { ElMessage } from 'element-plus';
import { getToken } from "@/utils/auth";

const emit = defineEmits(['upload-success']);

// 模拟生产环境数据（增加fileSize字段）
const attachments = ref([
  // {
  //   fileType: 'PDF文件',
  //   fileName: '年度报告.pdf',
  //   fileSize: 23.5,
  //   iconClass: 'fa-file-pdf' // PDF图标
  // },
  // {
  //   fileType: 'Excel表格',
  //   fileName: '销售数据.xlsx',
  //   fileSize: 23.5,
  //   iconClass: 'fa-file-excel' // Excel图标
  // },
  // {
  //   fileType: 'Word文档',
  //   fileSize: 23.5,
  //   fileName: '会议纪要.docx',
  //   iconClass: 'fa-file-word' // Word图标
  // }
]);

// 鼠标状态管理
const mouseEnterIndex = ref(-1);
const isDeleteVisible = (index) => index === mouseEnterIndex.value;

// 上传配置
const uploadUrl = import.meta.env.VITE_APP_BASE_API + '/api/infra/smart/assistant/scene/examPaper/uploadAttachment';
const headers = computed(() => ({
  Authorization: "Bearer " + getToken()
}));

// 上传状态
const uploading = ref(false);
const uploadRef = ref(null);

// 文件列表
const fileList = ref([]);

// 上传前处理
const beforeUpload = (file) => {
  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB');
    return false;
  }

  // 添加到上传列表
  attachments.value.unshift({
    fileName: file.name,
    fileSize: (file.size / 1024 / 1024).toFixed(2),
    fileType: getFileType(file.name),
    iconClass: getFileIcon(file.name),
    uploading: true,
    progress: 0
  });

  uploading.value = true;
  return true;
};

// 上传进度处理
const handleUploadProgress = (event, file, fileList) => {
  const index = attachments.value.findIndex(item => item.fileName === file.name);
  if (index !== -1) {
    attachments.value[index].progress = Math.floor(event.percent);
  }
};

// 上传成功处理
const handleUploadSuccess = (response, file, fileList) => {
  const index = attachments.value.findIndex(item => item.fileName === file.name);
  if (index !== -1) {
    attachments.value[index].uploading = false;
    attachments.value[index].storageId = response.data;

    // 通知父组件
    emit('upload-success', {
      fileName: file.name,
      storageId: response.data,
      fileSize: (file.size / 1024 / 1024).toFixed(2)
    });
  }

  if (fileList.every(f => f.status === 'success')) {
    uploading.value = false;
  }
};

// 上传失败处理
const handleUploadError = (error, file, fileList) => {
  const index = attachments.value.findIndex(item => item.fileName === file.name);
  if (index !== -1) {
    attachments.value.splice(index, 1);
    ElMessage.error(`${file.name} 上传失败: ${error.message}`);
  }
};

// 文件数量超出限制
const handleExceed = () => {
  ElMessage.warning('最多只能上传5个文件');
};

// 删除附件
const handleDelete = (index) => {
  attachments.value.splice(index, 1);
};

// 根据文件名获取文件类型
const getFileType = (fileName) => {
  const ext = fileName.split('.').pop().toLowerCase();
  const types = {
    pdf: 'PDF文件',
    doc: 'Word文档',
    docx: 'Word文档',
    xls: 'Excel表格',
    xlsx: 'Excel表格',
    ppt: 'PPT演示文稿',
    pptx: 'PPT演示文稿',
    txt: '文本文件',
    jpg: '图片',
    jpeg: '图片',
    png: '图片',
    gif: '图片'
  };
  return types[ext] || '未知文件';
};

// 根据文件名获取图标
const getFileIcon = (fileName) => {
  const ext = fileName.split('.').pop().toLowerCase();
  const icons = {
    pdf: 'fa-file-pdf',
    doc: 'fa-file-word',
    docx: 'fa-file-word',
    xls: 'fa-file-excel',
    xlsx: 'fa-file-excel',
    ppt: 'fa-file-powerpoint',
    pptx: 'fa-file-powerpoint',
    txt: 'fa-file-lines',
    jpg: 'fa-file-image',
    jpeg: 'fa-file-image',
    png: 'fa-file-image',
    gif: 'fa-file-image',
    zip: 'fa-file-zipper',
    rar: 'fa-file-zipper'
  };
  return icons[ext] || 'fa-file';
};

// 暴露方法给父组件
defineExpose({
  getAttachments: () => attachments.value
});

// // 删除附件方法
// const handleDelete = (index) => {
//   attachments.value.splice(index, 1);
//   console.log(`已删除第 ${index + 1} 个附件`);
// };

</script>

<style lang="scss" scoped>
// /* 为所有子元素设置右边距，除了最后一个 */
// .attachment-section>* {
//   margin-right: 12px;
// }

// /* 去除最后一个子元素的右边距 */
// .attachment-section>*:last-child {
//   margin-right: 0;
// }

// /* 确保第一个子元素左边没有间距 */
.attachment-section>attachment-item:first-child {
  margin-left: 0;
}

.hidden-upload {
  height: 0px;
}

.attachment-section {
  display: flex;
  // gap: 12px;
  border-radius: 8px;
  flex-wrap: wrap; // 允许容器内元素换行


  .attachment-item {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin-right: 12px;
    margin-bottom: 10px;
    gap: 8px;
    padding: 12px 16px;
    background: #fafafa;
    border-radius: 10px;
    transition: transform 0.2s ease;
    min-width: 180px;
    max-width: calc(25% - 20px);
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
      // color: #145799;
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