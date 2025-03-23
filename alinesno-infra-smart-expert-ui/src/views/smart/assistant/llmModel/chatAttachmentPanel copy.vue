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
            <i :class="getFileIconClass(file.extension)"></i>
          </span>
          <div class="file-info" >
            <div class="file-name">{{ file.name }}</div>
            <div class="file-details">
              <span>{{ file.size }}</span>
              <span>{{ file.wordCount }}</span>
            </div>
          </div>
          <el-button
            :class="{ 'delete-button': true, 'delete-button-visible': isDeleteButtonVisible[index] }"
            @click="removeFile(index)"
            size="default"
            text
          >
            <el-icon><CircleCloseFilled /></el-icon>
          </el-button>
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

  <div style="margin-bottom: 10px;">
    <el-button type="info" size="small" bg text style="background: #f6f6f6;">
      解析这张图片 
      <el-icon><Right /></el-icon>
    </el-button>
    <el-button type="info" size="small" bg text style="background: #f6f6f6;">
      总结这份文档内容 
      <el-icon><Right /></el-icon>
    </el-button>
    <el-button type="info" size="small" bg text style="background: #f6f6f6;">
      说说文档讲了什么 
      <el-icon><Right /></el-icon>
    </el-button>
  </div>

   </div>
</template>

<script setup name="ChatAttachmentPanel">
import { ref, onMounted, watch } from 'vue';

const uploadedFiles = ref([
  {
    name: '企业IT架构转型之道_阿里巴巴中台战略思想与架构实战',
    extension: 'pdf',
    size: 'PDF · 4MB',
    wordCount: '约0.3万字',
    type: 'pdf',
    uploading: false,
    uploadProgress: 0
  },
  {
    name: '项目策划方案',
    extension: 'ppt',
    size: 'PPT · 177KB',
    wordCount: '约0.4万字',
    type: 'ppt',
    uploading: false,
    uploadProgress: 0
  },
  {
    name: '销售数据统计',
    extension: 'xlsx',
    size: 'Excel · 256KB',
    wordCount: '无',
    type: 'excel',
    uploading: false,
    uploadProgress: 0
  },
  {
    name: '年度总结报告',
    extension: 'docx',
    size: 'Word · 320KB',
    wordCount: '约1万字',
    type: 'word',
    uploading: false,
    uploadProgress: 0
  },
  {
    name: '风景图片',
    extension: 'jpg',
    size: '图片 · 800KB',
    wordCount: '无',
    type: 'image',
    uploading: false,
    uploadProgress: 0
  },
  {
    name: '产品宣传视频',
    extension: 'mp4',
    size: '视频 · 12MB',
    wordCount: '无',
    type: 'video',
    uploading: false,
    uploadProgress: 0
  },
  {
    name: '会议录音',
    extension: 'mp3',
    size: '音频 · 2MB',
    wordCount: '无',
    type: 'audio',
    uploading: false,
    uploadProgress: 0
  },
  {
    name: '未知文件.qnrtube',
    extension: 'qnrtube',
    size: '文件 · 512KB',
    wordCount: '无',
    type: 'unknown',
    uploading: false,
    uploadProgress: 0
  }
]);

const containerRef = ref(null);
const canScrollLeft = ref(false);
const canScrollRight = ref(false);
const isDeleteButtonVisible = ref({});

// 根据文件扩展名获取图标类名
const getFileIconClass = (extension) => {
  switch (extension) {
    case 'pdf':
      return 'fa-solid fa-file-pdf';
    case 'ppt':
      return 'fa-solid fa-file-powerpoint';
    case 'xlsx':
      return 'fa-solid fa-file-excel';
    case 'docx':
      return 'fa-solid fa-file-word';
    case 'jpg':
    case 'png':
    case 'gif':
      return 'fa-solid fa-file-image';
    case 'mp4':
      return 'fa-solid fa-file-video';
    case 'mp3':
      return 'fa-solid fa-file-audio';
    default:
      return 'fa-solid fa-file-question';
  }
};

// 删除文件
const removeFile = (index) => {
  uploadedFiles.value.splice(index, 1);
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