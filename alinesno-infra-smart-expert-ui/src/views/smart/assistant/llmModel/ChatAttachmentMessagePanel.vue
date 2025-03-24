<template>
  <!-- 聊天上传附件面板 -->
   <div>
  <div class="chat-attachment-wrapper">
    <button
      v-if="canScrollLeft"
      @click="scrollLeft"
      class="scroll-button left"
    >
      <el-icon><ArrowLeftBold /></el-icon>
    </button>

    <div ref="containerRef" class="chat-attachment-container">
      <div v-for="(file, index) in uploadedFiles" :key="index" class="file-item" @mouseenter="showDeleteButton(index)" @mouseleave="hideDeleteButton(index)">
        <div class="file-header">
          <span>
            <i :class="getFileIconClass(file.fileType)"></i>
          </span>
          <div class="file-info" >
            <div class="file-name">{{ file.fileName}}</div>
            <div class="file-details">
              <span>{{ formatFileSize(file.length) }}</span>
              <span>{{ file.wordCount }} 字</span>
            </div>
          </div>
          <el-tooltip
        class="box-item"
        effect="dark"
        content="引用文档"
        placement="top"
      >
          <el-button
            :class="{ 'delete-button': true, 'delete-button-visible': isDeleteButtonVisible[index] }"
            @click="linkFile(index)"
            size="default"
            text
          >
            <el-icon><Position /></el-icon>
          </el-button>
    </el-tooltip>
        </div>
        <div v-if="file.uploading" class="progress-bar">
          <div :style="{ width: `${file.uploadProgress}%` }" class="progress"></div>
        </div>
      </div>
    </div>
    <button
      v-if="canScrollRight"
      @click="scrollRight"
      class="scroll-button right"
    >
      <el-icon><ArrowRightBold /></el-icon>
    </button>
  </div>

   </div>
</template>

<script setup name="ChatAttachmentPanel">
import { ref, onMounted, watch } from 'vue';

const emit = defineEmits(['handleFileIdToMessageBox']);

const props = defineProps({
  message: {
    type: String,
    default: ''
  }
});

const uploadedFiles = ref(props.message?.fileAttributeList) ; 

const containerRef = ref(null);
const canScrollLeft = ref(false);
const canScrollRight = ref(false);
const isDeleteButtonVisible = ref({});

// 根据文件扩展名获取图标类名

const getFileIconClass = (extension) => {
    if (extension.includes('pdf')) {
        return 'fa-solid fa-file-pdf';
    } else if (extension.includes('ppt')) {
        return 'fa-solid fa-file-powerpoint';
    } else if (extension.includes('xlsx')) {
        return 'fa-solid fa-file-excel';
    } else if (extension.includes('docx') || extension.includes('doc') ) {
        return 'fa-solid fa-file-word';
    } else if (extension.includes('jpg') || extension.includes('png') || extension.includes('gif')) {
        return 'fa-solid fa-file-image';
    } else if (extension.includes('mp4')) {
        return 'fa-solid fa-file-video';
    } else if (extension.includes('mp3')) {
        return 'fa-solid fa-file-audio';
    } else {
        return 'fa-solid fa-file-question';
    }
};
    
// 文件大小格式化
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 删除文件
const linkFile = (index) => {
  // 查询当前的文件信息
  const file = uploadedFiles.value[index];
  emit('handleFileIdToMessageBox', file);
};

// 显示删除按钮
const showDeleteButton = (index) => {
  isDeleteButtonVisible.value[index] = true;
};

// 隐藏删除按钮
const hideDeleteButton = (index) => {
  isDeleteButtonVisible.value[index] = false;
};

// 滚动到左边
const scrollLeft = () => {
  containerRef.value.scrollBy({
    left: -200,
    behavior: 'smooth'
  });
};

// 滚动到右边
const scrollRight = () => {
  containerRef.value.scrollBy({
    left: 200,
    behavior: 'smooth'
  });
};

// 检查是否可以滚动
const checkScrollability = () => {
  if (containerRef.value) {
    const { scrollLeft, scrollWidth, clientWidth } = containerRef.value;
    canScrollLeft.value = scrollLeft > 0;
    canScrollRight.value = scrollLeft < scrollWidth - clientWidth;
  }
};

onMounted(() => {
  // 初始检查滚动状态
  setTimeout(() => {
    checkScrollability();
  }, 0);
  containerRef.value.addEventListener('scroll', checkScrollability);
});

watch(uploadedFiles, () => {
  // 文件列表变化时检查滚动状态
  setTimeout(() => {
    checkScrollability();
  }, 0);
});
</script>
<style lang="scss" scoped>
.chat-attachment-wrapper {
  position: relative;
  display: flex;
  align-items: center;

  .chat-attachment-container {
    display: flex;
    flex-direction: row;
    padding: 0;
    margin-bottom: 0px;
    align-items: center;
    flex-wrap: nowrap;
    overflow-x: hidden;
    width: 100%;

    .file-item {
      min-width: 180px;
      background: rgba(0, 0, 0, 0.04);
      border-radius: 7px;
      padding: 10px;
      margin-right: 10px;
      margin-bottom: 10px;
      position: relative;
      box-sizing: border-box;

      .file-header {
        display: flex;
        flex-direction: row;
        justify-content: flex-start;
        align-items: center;
        gap: 10px;

        .file-info {
          display: flex;
          flex-direction: column;
          gap: 5px;
          width: 90%;

          .file-name {
            font-size: 14px;
            text-align: left;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            max-width: 130px;
          }

          .file-details {
            font-size: 12px;
            color: #a5a5a5;
            display: flex;
            flex-direction: row;
            justify-content: space-between;
            align-items: center;
          }
        }

        .delete-button {
          position: absolute;
          top: 0px;
          right: 0px;
          display: none; /* 默认隐藏 */

          &.delete-button-visible {
            display: block; /* 显示 */
          }
        }
      }

      .progress-bar {
        height: 10px;
        background-color: #e0e0e0;
        border-radius: 5px;
        margin-top: 5px;
        overflow: hidden;

        .progress {
          height: 100%;
          background-color: #2196f3;
        }
      }
    }
  }

  .scroll-button {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    width: 30px;
    height: 30px;
    background-color: rgba(0, 0, 0, 0.4);
    border: none;
    cursor: pointer;
    border-radius: 50%;
    z-index: 1;

    &.left {
      left: 5px;
    }

    &.right {
      right: 5px;
    }
  }
}
</style>