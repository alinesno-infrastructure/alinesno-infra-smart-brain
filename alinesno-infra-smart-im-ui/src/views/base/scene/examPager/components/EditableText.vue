<template>
  <div class="editable-container" @mouseleave="saveIfEditing">
    <div 
      v-if="!isEditing" 
      class="editable-display"
      @click="startEditing"
    >
      {{ displayValue || placeholder }}
    </div>
    <div v-else class="editable-input-wrapper">
      <el-input
        v-model="tempValue"
        ref="inputRef"
        type="text"
        size="large"
        class="editable-input"
        @blur="save"
        @keyup.enter="save"
        @keyup.esc="cancel"
      />
      <div class="editable-actions">
        <button class="action-btn save-btn" @click="save">
          <i class="fa fa-check"></i>
        </button>
        <button class="action-btn cancel-btn" @click="cancel">
          <i class="fa fa-times"></i>
        </button>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, watch, defineProps, defineEmits, nextTick } from 'vue';

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '点击编辑...'
  },
  isQuestionEdit: {
    type: Boolean,
    default: true,
    required: false
  },
  saveDebounce: {
    type: Number,
    default: 300
  }
});

const emits = defineEmits(['update:modelValue', 'save', 'cancel', 'edit']);

const inputRef = ref(null);
const isEditing = ref(false);
const tempValue = ref(props.modelValue);
const displayValue = ref(props.modelValue);
const debounceTimer = ref(null);

// 监听外部值变化
watch(() => props.modelValue, (newVal) => {
  if (!isEditing.value) {
    tempValue.value = newVal;
    displayValue.value = newVal;
  }
});

// 开始编辑
const startEditing = () => {

  if(!props.isQuestionEdit){
    return;
  }

  if (isEditing.value) return;
  
  isEditing.value = true;
  tempValue.value = props.modelValue;
  
  nextTick(() => {
    inputRef.value.focus();
    inputRef.value.select();
  });
  
  emits('edit');
};

// 保存编辑
const save = () => {
  if (!isEditing.value) return;
  
  clearTimeout(debounceTimer.value);
  
  if (tempValue.value !== props.modelValue) {
    emits('update:modelValue', tempValue.value);
    emits('save', tempValue.value);
    displayValue.value = tempValue.value;
  }
  
  isEditing.value = false;
};

// 取消编辑
const cancel = () => {
  if (!isEditing.value) return;
  
  tempValue.value = props.modelValue;
  isEditing.value = false;
  emits('cancel');
};

// 鼠标离开时保存
const saveIfEditing = () => {
  if (isEditing.value) {
    clearTimeout(debounceTimer.value);
    debounceTimer.value = setTimeout(save, props.saveDebounce);
  }
};
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
  /* min-height: 36px; */
  line-height: 1.5;
  cursor: pointer;
}

.editable-display:hover {
  background-color: rgba(0, 0, 0, 0.03);
  border-color: rgba(0, 0, 0, 0.1);
}

.editable-input-wrapper {
  position: relative;
}

.editable-input {
  width: 100%;
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

.editable-actions {
  position: absolute;
  right: 6px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  gap: 4px;
  opacity: 0;
  visibility: hidden;
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