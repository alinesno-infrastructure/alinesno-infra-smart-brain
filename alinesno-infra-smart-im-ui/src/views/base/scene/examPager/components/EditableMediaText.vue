<template>
  <div class="editable-container" @mouseleave="handleContainerLeave">

    <div 
      v-if="!isEditing" 
      class="editable-display"
      @click="startEditing"
    >
      <div v-if="displayValue || !mediaFiles.length" class="content-text">
        {{ displayValue || placeholder }}
      </div>
      <div v-if="mediaFiles.length > 0" class="content-media">
        <div v-for="(file, index) in mediaFiles" :key="index" class="media-item">
          <img v-if="file.type.startsWith('image/')" :src="file.url" alt="上传的图片" class="media-image">
          <video v-else-if="file.type.startsWith('video/')" controls class="media-video">
            <source :src="file.url" :type="file.type">
          </video>
          <button v-if="isEditing" @click.stop="removeMedia(index)" class="remove-media-btn">
            <i class="fa fa-times-circle"></i>
          </button>
        </div>
      </div>
    </div>
    
    <div v-else class="editable-input-wrapper">
      <el-input
        v-model="tempValue"
        ref="inputRef"
        type="textarea"
        size="large"
        resize="none"
        class="editable-input"
        @keyup.enter="save"
        @keyup.esc="cancel"
        :autosize="{ minRows: 3, maxRows: 10 }"
      />

        <!-- @blur="handleBlur" -->
      
      <div class="upload-area">
        <div class="upload-buttons">
          <div class="upload-btn" @click.stop="triggerFileInput('image')">
            <i class="fa fa-image"></i> 上传图片
          </div>
          <input 
            type="file" 
            ref="imageInput" 
            accept="image/*" 
            @change="handleImageUpload" 
            class="hidden"
          >
          
          <div class="upload-btn" @click.stop="triggerFileInput('video')">
            <i class="fa fa-video"></i> 上传视频
          </div>
          <input 
            type="file" 
            ref="videoInput" 
            accept="video/*" 
            @change="handleVideoUpload" 
            class="hidden"
          >
        </div>
        
        <div v-if="uploadingFiles.length" class="uploading-files">
          <div v-for="(file, index) in uploadingFiles" :key="index" class="uploading-file">
            <div class="file-name">{{ file.name }}</div>
            <div class="progress-bar">
              <div class="progress" :style="{ width: file.progress + '%' }"></div>
            </div>
            <div class="progress-text">{{ file.progress }}%</div>
          </div>
        </div>
        
        <div v-if="tempMediaFiles.length" class="uploaded-media">
          <div v-for="(file, index) in tempMediaFiles" :key="index" class="media-preview">
            <img v-if="file.type.startsWith('image/')" :src="file.previewUrl" class="preview-image">
            <video v-else-if="file.type.startsWith('video/')" controls class="preview-video">
              <source :src="file.previewUrl" :type="file.type">
            </video>
            <button @click.stop="removeTempMedia(index)" class="remove-preview-btn">
              <i class="fa fa-times-circle"></i>
            </button>
          </div>
        </div>
      </div>
      
      <div class="editable-actions">
        <button class="action-btn save-btn" @click.stop="save">
          <i class="fa fa-check"></i>
        </button>
        <button class="action-btn cancel-btn" @click.stop="cancel">
          <i class="fa fa-times"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits, nextTick, onMounted } from 'vue';
import { ElMessage } from 'element-plus';

const props = defineProps({
  modelValue: String,
  placeholder: {
    type: String,
    default: '点击编辑...'
  },
  saveDebounce: {
    type: Number,
    default: 300
  },
  isQuestionEdit: {
    type: Boolean,
    default: true,
    required: false 
  },
  mediaFiles: {
    type: Array,
    default: () => []
  },
  uploadUrl: {
    type: String,
    required: true
  }
});

const emits = defineEmits([
  'update:modelValue', 
  'save', 
  'cancel', 
  'edit',
  'upload-media',
  'remove-media'
]);

const inputRef = ref(null);
const imageInput = ref(null);
const videoInput = ref(null);
const isEditing = ref(false);
const tempValue = ref(props.modelValue);
const displayValue = ref(props.modelValue);
const debounceTimer = ref(null);
const tempMediaFiles = ref([...props.mediaFiles]);
const uploadingFiles = ref([]);

watch(() => props.modelValue, (newVal) => {
  if (!isEditing.value) {
    tempValue.value = newVal;
    displayValue.value = newVal;
  }
});

watch(() => props.mediaFiles, (newFiles) => {
  if (!isEditing.value) {
    tempMediaFiles.value = [...newFiles];
  }
});

const triggerFileInput = (type) => {
  if (type === 'image') {
    imageInput.value.click();
  } else {
    videoInput.value.click();
  }
};

const startEditing = () => {

  // 如果是不可编辑状态
  if(!props.isQuestionEdit){
    return;
  }

  if (isEditing.value) return;
  
  isEditing.value = true;
  tempValue.value = props.modelValue;
  tempMediaFiles.value = [...props.mediaFiles];
  
  nextTick(() => {
    inputRef.value.focus();
  });
  
  emits('edit');
};

// const handleContainerLeave = (e) => {
//   if (!e.relatedTarget || !e.currentTarget.contains(e.relatedTarget)) {
//     saveIfEditing();
//   }
// };

const handleBlur = (e) => {
  if (!e.relatedTarget || !e.currentTarget.closest('.editable-container').contains(e.relatedTarget)) {
    save();
  }
};

const handleImageUpload = async (event) => {
  // 确保获取到有效的文件列表
  const files = event.target.files;
  console.log('Selected files:', files); // 调试日志

  if (!files || files.length === 0) {
    console.warn('No files selected or dialog cancelled');
    event.target.value = ''; // 重置input
    return;
  }

  try {
    await uploadFiles(files, 'image');
  } catch (error) {
    console.error('Upload failed:', error);
    ElMessage.error('上传失败: ' + (error.message || '未知错误'));
  } finally {
    event.target.value = ''; // 无论成功失败都重置input
  }
};

const handleVideoUpload = async (event) => {
  event.target.value = '';
  const files = event.target.files;
  if (!files.length) return;
  await uploadFiles(files, 'video');
};

const uploadFiles = async (files, type) => {
  for (const file of files) {
    if ((type === 'image' && !file.type.startsWith('image/')) || 
        (type === 'video' && !file.type.startsWith('video/'))) {
      ElMessage.error(`请上传${type === 'image' ? '图片' : '视频'}文件`);
      continue;
    }
    
    const previewUrl = URL.createObjectURL(file);
    const uploadId = Date.now() + '-' + Math.random().toString(36).substring(2);

    console.log('previewUrl = ' + previewUrl)
    
    uploadingFiles.value.push({
      id: uploadId,
      name: file.name,
      type: file.type,
      progress: 0,
      previewUrl
    });
    
    let progress = 0;
    const interval = setInterval(() => {
      progress += Math.random() * 20;
      if (progress > 100) progress = 100;
      
      const index = uploadingFiles.value.findIndex(f => f.id === uploadId);
      if (index !== -1) {
        uploadingFiles.value[index].progress = progress;
      }
      
      if (progress === 100) {
        clearInterval(interval);
      }
    }, 200);
    
    try {
      await new Promise(resolve => setTimeout(resolve, 2000));
      
        const uploadItem = {
          name: file.name,
          type: file.type,
          url: `https://picsum.photos/id/242/200/100`,
          previewUrl
        }
      tempMediaFiles.value.push(uploadItem);
      
      uploadingFiles.value = uploadingFiles.value.filter(f => f.id !== uploadId);
      ElMessage.success(`${file.name} 上传成功`);

      emits('upload-media', uploadItem);

       //
      // emits('upload-media', {
      //   uploadItem,
      //   file,
      //   type: file.type.startsWith('image/') ? 'image' : 'video'
      // });
      
    } catch (error) {
      uploadingFiles.value = uploadingFiles.value.filter(f => f.id !== uploadId);
      ElMessage.error(`${file.name} 上传失败: ${error.message || '未知错误'}`);
    }
  }
};

const removeTempMedia = (index) => {
  tempMediaFiles.value.splice(index, 1);
};

const removeMedia = (index) => {
  emits('remove-media', index);
};

const save = () => {
  if (!isEditing.value) return;
  
  clearTimeout(debounceTimer.value);
  
  const hasChanges = tempValue.value !== props.modelValue || 
                    JSON.stringify(tempMediaFiles.value) !== JSON.stringify(props.mediaFiles);
  
  if (hasChanges) {
    emits('update:modelValue', tempValue.value);
    emits('save', {
      text: tempValue.value,
      mediaFiles: tempMediaFiles.value
    });
    displayValue.value = tempValue.value;
  }
  
  isEditing.value = false;
};

const cancel = () => {
  if (!isEditing.value) return;
  
  tempValue.value = props.modelValue;
  tempMediaFiles.value = [...props.mediaFiles];
  isEditing.value = false;
  emits('cancel');
};

const saveIfEditing = () => {
  if (isEditing.value) {
    clearTimeout(debounceTimer.value);
    debounceTimer.value = setTimeout(() => {
      if (isEditing.value) {
        save();
      }
    }, props.saveDebounce);
  }
};

onMounted(() => {
  const releaseUrls = () => {
    tempMediaFiles.value.forEach(file => {
      if (file.previewUrl) {
        URL.revokeObjectURL(file.previewUrl);
      }
    });
  };
  
  watch(isEditing, (newVal) => {
    if (!newVal) {
      releaseUrls();
    }
  });
});
</script>

<style scoped>
.editable-container {
  position: relative;
  min-width: 200px;
  transition: all 0.3s ease;
}

.editable-display {
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid transparent;
  transition: all 0.2s ease;
  line-height: 1.5;
  cursor: pointer;
}

.editable-display:hover {
  background-color: rgba(0, 0, 0, 0.03);
  border-color: rgba(0, 0, 0, 0.1);
}

.content-media {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.media-item {
  position: relative;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.media-image, .media-video {
  max-width: 100%;
  max-height: 200px;
  display: block;
  object-fit: cover;
}

.remove-media-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.media-item:hover .remove-media-btn {
  opacity: 1;
}

.editable-input-wrapper {
  position: relative;
}

.editable-input {
  width: calc(100% - 50px);
  padding: 8px 36px 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  outline: none;
  transition: border-color 0.2s ease;
  line-height: 1.5;
}

.editable-input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3);
}

.upload-area {
  margin-top: 10px;
  padding: 10px;
  border: 1px dashed #d1d5db;
  border-radius: 4px;
  background-color: #f9fafb;
}

.upload-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.upload-btn {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  background-color: #f3f4f6;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  color: #4b5563;
  font-size: 14px;
  user-select: none;
}

.upload-btn:hover {
  background-color: #e5e7eb;
}

.upload-btn i {
  margin-right: 5px;
}

.hidden {
  display: none;
}

.uploading-files {
  margin-bottom: 10px;
}

.uploading-file {
  margin-bottom: 5px;
}

.file-name {
  font-size: 14px;
  margin-bottom: 2px;
}

.progress-bar {
  height: 8px;
  background-color: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background-color: #3b82f6;
  transition: width 0.2s ease;
}

.progress-text {
  font-size: 12px;
  color: #6b7280;
  margin-top: 2px;
}

.uploaded-media {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.media-preview {
  position: relative;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.preview-image, .preview-video {
  max-width: 150px;
  max-height: 100px;
  display: block;
  object-fit: cover;
}

.remove-preview-btn {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.media-preview:hover .remove-preview-btn {
  opacity: 1;
}

.editable-actions {
  position: absolute;
  right: 6px;
  top: 50px;
  transform: translateY(-50%);
  display: flex;
  gap: 4px;
  opacity: 1;
  /* visibility: hidden; */
  transition: all 0.2s ease;
}

.editable-input-wrapper:hover .editable-actions,
.editable-input:focus + .editable-actions {
  opacity: 1;
  visibility: visible;
}

.action-btn {
  width: 24px;
  height: 24px;
  border: none;
  background-color: transparent;
  cursor: pointer;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease;
}

.action-btn:hover {
  background-color: rgba(0, 0, 0, 0.08);
}

.save-btn {
  color: #10b981;
}

.cancel-btn {
  color: #ef4444;
}
</style>