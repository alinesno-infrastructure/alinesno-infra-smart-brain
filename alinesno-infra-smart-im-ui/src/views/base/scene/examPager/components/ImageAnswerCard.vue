<template>
  <div 
    class="editable-card"
    :class="{ 
      'correct': isCorrect, 
      'editing': isEditing,
      'checked': isCheck
    }"
    @mouseleave="handleMouseLeave">

    <!-- Display mode -->
    <div v-if="!isEditing" class="editable-card-content" @click="handleClick">
      <div class="image-wrapper">
        <img v-if="item.img" 
            :src="getFullImagePath(item.img)" 
            @error="handleImageError"
            class="image-preview" />
        <div v-else class="image-placeholder">
          <el-icon><Picture /></el-icon>
        </div>
      </div>
      <div class="content-wrapper">
        <div class="label">{{ item.label }}</div>
        <div class="text">{{ item.content }}</div>
      </div>
    </div>

    <!-- Edit mode -->
    <div v-else class="editable-card-edit">
      
      <el-upload
        class="image-uploader"
        :action="upload.url"
        :headers="upload.headers"
        :data="{ type: 'img', updateSupport: upload.updateSupport }"
        :show-file-list="false"
        :auto-upload="true"
        :on-success="handleAvatarSuccess" 
        :on-error="handleUploadError"
        :on-change="handleImageChange"
        :on-progress="handleUploadProgress"
        accept="image/*">
        <template #trigger>
          <div class="upload-area">
            <div v-if="uploadProgress > 0 && uploadProgress < 100" class="upload-progress">
              <el-progress 
                type="circle" 
                :percentage="uploadProgress" 
                :width="80"
                :stroke-width="8"
              />
            </div>
            <!-- <img v-else-if="editData.img" :src="getFullImagePath(editData.img)" class="uploaded-image" @error="handleImageError"/> -->

            <img v-else-if="previewImage || editData.img" :src="previewImage || getFullImagePath(editData.img)" class="uploaded-image" @error="handleImageError"/>
            <div v-else class="upload-placeholder">
              <el-icon><Plus /></el-icon>
              <div class="upload-text">点击上传图片</div>
            </div>
          </div>
        </template>
      </el-upload>
      
      <div class="edit-fields">
        <el-input 
          v-model="editData.label" 
          placeholder="Enter label" 
          class="edit-input"
        />
        <el-input
          v-model="editData.content"
          placeholder="Enter content"
          type="textarea"
          rows="3"
          class="edit-input"
        />
        
        <div class="edit-actions">
          <el-button type="primary" size="small" @click="saveChanges">确认</el-button>
          <el-button size="small" @click="cancelEditing">取消</el-button>
        </div>
      </div>
    </div>

    <!-- Checkbox/Radio footer -->
    <div class="editable-card-footer">
      <el-radio 
        v-if="checkType === 'radio'" 
        :model-value="checked"
        @update:model-value="handleSelectionChange($event)"
        :label="true" 
        size="large"
        @click.stop>选择</el-radio>
      <el-checkbox 
        v-if="checkType === 'checkbox'" 
        :model-value="checked"
        @update:model-value="handleSelectionChange($event)"
        size="large"
        @click.stop
      >选择</el-checkbox>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, defineProps, defineEmits } from 'vue';
import { Picture, Plus } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { getToken } from "@/utils/auth";

import errorImage from '@/assets/images/scene-error.png';

const emit = defineEmits(['update:item', 'change']);

// 添加一个预览图的ref
const previewImage = ref('');
const props = defineProps({
  item: {
    type: Object,
    required: true,
    default: () => ({
      img: '',
      label: '',
      content: ''
    })
  },
  checkType: {
    type: String,
    default: 'radio',
    validator: (value) => ['radio', 'checkbox'].includes(value)
  },
  checked: {
    type: Boolean,
    default: false
  },
  isQuestionEdit: {
      type: Boolean,
      default: true,
      required: false
  },
  isCorrect: {
    type: Boolean,
    default: false
  }
});

const upload = reactive({
  open: false,
  title: "",
  isUploading: false,
  updateSupport: 0,
  headers: { Authorization: "Bearer " + getToken() },
  url: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/scene/examImage/uploadImage",
  display: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/"
});

const isCheck = ref(false);
const isEditing = ref(false);
const editData = ref({
  img: '',
  label: '',
  content: ''
});
const uploadProgress = ref(0);

// Helper function to get full image path
const getFullImagePath = (imgPath) => {
  if (!imgPath) return '';
  if (imgPath.startsWith('http') || imgPath.startsWith('blob:')) {
    return imgPath;
  }
  return upload.display + imgPath;
};

// 处理选中状态变化
const handleSelectionChange = (value) => {
  emit('change', value);
};

// Initialize edit data with current item values
const initEditData = () => {
  editData.value = {
    img: props.item.img,
    label: props.item.label,
    content: props.item.content
  };
};

const handleClick = () => {
  if(!props.isQuestionEdit){
    return;
  }
  if (isEditing.value) return;
  initEditData();
  isEditing.value = true;
};

const handleMouseLeave = () => {
  if (isEditing.value && !editData.value.img && !editData.value.label && !editData.value.content) {
    isEditing.value = false;
  }
};

const handleImageChange = (file) => {
  // 检查文件类型
  const isImage = file.raw.type.startsWith('image/');
  if (!isImage) {
    ElMessage.error('只能上传图片文件');
    return;
  }
  
  // 检查文件大小 (例如限制5MB)
  const isLt5M = file.raw.size / 1024 / 1024 < 5;
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB');
    return;
  }

  // 创建预览图（仅用于临时显示，不会保存到editData）
  const reader = new FileReader();
  reader.onload = (e) => {
    // 这里不再赋值给editData.value.img
    // 只是临时显示预览（如果需要显示预览，可以通过另一个变量）
    previewImage.value = e.target.result;
  };
  reader.readAsDataURL(file.raw);
};

/** 图片上传成功 */
const handleAvatarSuccess = (response) => {
  uploadProgress.value = 0;
  if (response.code === 200) {
    // 关键修改：直接使用服务器返回的路径，而不是base64预览
    editData.value.img = response.data;
    ElMessage.success('上传成功');
  } else {
    ElMessage.error(response.msg || '上传失败');
    // 上传失败时清除预览
    editData.value.img = '';
  }
};

// 处理上传错误
const handleUploadError = (error) => {
  uploadProgress.value = 0;
  ElMessage.error('上传失败: ' + (error.message || '未知错误'));
};

// 处理上传进度事件
const handleUploadProgress = (event) => {
  uploadProgress.value = Math.round((event.loaded / event.total) * 100);
};

const saveChanges = () => {
  console.log('保存修改 = ' + JSON.stringify(editData.value))
  emit('update:item', { ...editData.value });
  isEditing.value = false;
};

const cancelEditing = () => {
  isEditing.value = false;
};

const handleImageError = (event) => {
  event.target.src = errorImage;
  event.target.onerror = null; // Prevent infinite loop if fallback image fails
};

// Watch for checkbox/radio changes
watch(isCheck, (newVal) => {
  emit('change', newVal);
});
</script>

<style lang="scss" scoped>
.editable-card {
  position: relative;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s ease;
  background-color: #fff;
  cursor: pointer;
  
  &:hover {
    box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  }
  
  &.editing {
    border-color: #409eff;
    box-shadow: 0 0 8px rgba(64, 158, 255, 0.3);
  }
  
  &.checked {
    border-color: #67c23a;
    background-color: rgba(103, 194, 58, 0.05);
  }
  
  &.correct {
    border-color: #67c23a;
    background-color: rgba(103, 194, 58, 0.1);
  }
}

.editable-card-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.content-wrapper {
    padding: 0px 10px;
}

.image-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
  
  .image-preview {
    width: 100%;
    max-height: 100%;
    object-fit: contain;
  }
  
  .image-placeholder {
    color: #c0c4cc;
    font-size: 24px;
  }
}

.content-wrapper {
  .label {
    font-weight: bold;
    margin-bottom: 4px;
    color: #303133;
  }
  
  .text {
    color: #606266;
    font-size: 14px;
    line-height: 1.5;
  }
}

.editable-card-edit {
  display: flex;
  gap: 16px;
  flex-direction: column;
}

.image-uploader {
  width: calc(100% - 0px) ; 
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  
  &:hover {
    border-color: #409eff;
  }
  
  .uploaded-image {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
  
  .uploader-icon {
    font-size: 28px;
    color: #8c939d;
  }
}

.edit-fields {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 0px 10px;
}

.edit-input {
  width: 100%;
}

.edit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

.editable-card-footer {
  display: flex;
  justify-content: center;
  margin-top: 12px;
  border-top: 1px solid #ebeef5;
}

.upload-area {
  width: 100%;
  height: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #8c939d;
  
  .el-icon {
    font-size: 28px;
    margin-bottom: 8px;
  }
  
  .upload-text {
    font-size: 12px;
  color: #8c939d;
  }
}

.upload-progress {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.7);
}
</style>