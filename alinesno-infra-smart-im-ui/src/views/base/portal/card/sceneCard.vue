<template>
    <div class="scene-card" :class="{ 'selected': isSelected }" @click="toggleSelection">
      <div>
        <span class="scene-banner">
            <img :src="imagePathByPath(scene.sceneBanner)" class="scene-card-image">
        </span>
        <span class="scene-name">
            {{ scene.sceneName || '未命名场景' }}
        </span>
        </div>
      <div v-if="isSelected" class="checkmark">&#10004;</div>
    </div>
  </template>
  
  <script setup>
  import { ref, defineProps, defineEmits } from 'vue';
  
  const props = defineProps({
      scene: {
          type: Object,
          default: {} 
      },
      modelValue: {
          type: [String, Array],
          default: ''
      }
  })
  
  const emits = defineEmits(['update:modelValue']);
  const isSelected = ref(false);
  
  const toggleSelection = () => {
      isSelected.value = !isSelected.value;
      let selectedIds = Array.isArray(props.modelValue) ? [...props.modelValue] : [];
      if (isSelected.value) {
          if (!selectedIds.includes(props.scene.id)) {
              selectedIds.push(props.scene.id);
          }
      } else {
          selectedIds = selectedIds.filter(id => id!== props.scene.id);
      }
      emits('update:modelValue', selectedIds);
  }
  
  // 初始化选中状态
  if (Array.isArray(props.modelValue)) {
      isSelected.value = props.modelValue.includes(props.scene.id);
  }
  </script>
  
  <style lang="scss" scoped>
  .scene-card {
    padding: 10px;
    margin: 5px;
    cursor: pointer;
    position: relative;
    line-height: 1.3rem;
    background: #fafafa;
    border-radius: 8px;
    border: 0px;

    .scene-banner {
        width: 50px;
        
        img {
            width:100%;
        }
    }
    .scene-name {
        width: 100%; /* 你可以根据实际需求调整宽度 */
        display: inline-block;
        white-space: nowrap; /* 不换行 */
        overflow: hidden; /* 超出部分隐藏 */
        text-overflow: ellipsis; /* 显示省略号 */
    }
  }
  
  .scene-card.selected {
      border-color: #145799;
      background-color: #e7f3ff;
  }
  
  .checkmark {
      position: absolute;
      top: 5px;
      right: 5px;
      color: #145799;
  }
  </style>    