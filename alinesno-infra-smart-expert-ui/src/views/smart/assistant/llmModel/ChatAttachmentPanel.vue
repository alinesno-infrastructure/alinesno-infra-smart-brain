<template>
  <!-- 聊天上传附件面板 -->
   <div >

     <!-- 添加文件输入框 -->
    <input
      ref="fileInput"
      type="file"
      multiple
      accept="image/*, application/pdf, application/vnd.ms-powerpoint, application/vnd.openxmlformats-officedocument.presentationml.presentation, application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/msword, application/vnd.openxmlformats-officedocument.wordprocessingml.document"
      @change="handleFileSelect"
      style="display: none;"
    />

  <div class="chat-attachment-wrapper" v-if="uploadedFiles.length != 0">
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
            <i :class="getFileIconClass(file.extension)" v-if="!file.uploading"></i>
            <el-button type="text" text :loading="file.uploading"></el-button>
          </span>
          <div class="file-info" >
            <div class="file-name">{{ file.name }}</div>
            <div class="file-details">
              <span>{{ file.size }}</span>
              <span>{{ file.wordCount }} 字</span>
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

  <div style="margin-bottom: 10px;" v-if="uploadedFiles.length != 0">
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
import { ref, onMounted, watch, computed, reactive, toRefs } from 'vue';
import { ElMessage } from 'element-plus';
import { uploadFile } from '@/api/smart/assistant/roleChat';

// emits updateChatWindowHeight
const emit = defineEmits(['updateChatWindowHeight']);

// 文件输入框引用
const fileInput = ref(null);

const uploadedFiles = ref([]);

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
  checkScrollability();
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

    if(uploadedFiles.value.length > 0){
      emit('updateChatWindowHeight', 320) ;
    }else{
      emit('updateChatWindowHeight', 0);
    }

};

// onMounted(() => {
//   // 初始检查滚动状态
//   setTimeout(() => {
//     checkScrollability();
//   }, 0);
//   containerRef.value.addEventListener('scroll', checkScrollability);
// });

// 文件上传处理
const handleFileSelect = (e) => {
  const files = e.target.files;
  if (!files || files.length === 0) return;

  const newFiles = [];
  for (const file of files) {
    if (uploadedFiles.value.length >= 10) {
      ElMessage.warning('最多只能上传10个文件');
      return;
    }

    const extension = file.name.split('.').pop().toLowerCase();
    const fileSize = formatFileSize(file.size);

    newFiles.push(reactive({
      id: -1, // 初始化 id 为 -1
      name: file.name,
      extension,
      size: fileSize,
      wordCount: '无',
      type: getFileType(extension),
      uploading: false,
      uploadProgress: 0,
      file: file // 保存原始File对象
    }));
  }

  uploadedFiles.value.push(...newFiles);
  checkScrollability();
  newFiles.forEach(file => startUpload(file));
};

// 开始上传文件
const startUpload = (fileItem) => {
  fileItem.uploading = true;
  fileItem.uploadProgress = 0;

  const formData = new FormData();
  formData.append('file', fileItem.file);

  uploadFile(formData).then((response) => {
    const data = response.data;
    fileItem.id = data.id; // 更新文件 id
    fileItem.wordCount = data.wordCount || '无';
    fileItem.uploading = false;
    ElMessage.success('文件上传成功');

  })
  .catch((error) => {
    fileItem.uploading = false;
    ElMessage.error(`文件上传失败：${error.message}`);
  });
};

// 文件类型格式化
const getFileType = (extension) => {
  switch (extension) {
    case 'pdf': return 'pdf';
    case 'ppt': case 'pptx': return 'ppt';
    case 'xlsx': case 'xls': return 'excel';
    case 'docx': case 'doc': return 'word';
    case 'jpg': case 'jpeg': case 'png': case 'gif': return 'image';
    case 'mp4': return 'video';
    case 'mp3': return 'audio';
    default: return 'unknown';
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

// 暴露给父组件的方法
const openFileSelector = () => {
  fileInput.value.click();
};

watch(uploadedFiles, () => {
  // 文件列表变化时检查滚动状态
  setTimeout(() => {
    checkScrollability();
  }, 0);
});

/** 获取到正常上传的文件列表，只需要文件的id */
const handleGetUploadFiles = () => {
  const filterArr =  uploadedFiles.value.map(file => file.id);

  // 清空uploadedFiles数组
  uploadedFiles.value = [];
  checkScrollability();

  return filterArr ;
};

defineExpose({
  openFileSelector,
  handleGetUploadFiles
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