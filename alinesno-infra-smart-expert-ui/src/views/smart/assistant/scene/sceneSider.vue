<template>
  <div class="scene-container">
    <h2 class="scene-title">业务场景分类</h2>
    <div class="scene-list">
      <div 
        class="scene-item" 
        :class="{ 'active': activeSceneId === '' }"
        @click="selectScene('')">
        <div class="scene-content">
          <i class="scene-icon fa-solid fa-layer-group"></i>
          <h3>所有业务场景</h3>
        </div>
      </div>
      
      <div 
        v-for="scene in props.scenes" 
        :key="scene.code" 
        class="scene-item" 
        :class="{ 'active': activeSceneId === scene.code }"
        @click="selectScene(scene.code)">
        <div class="scene-content">
          <i class="scene-icon" :class="scene.icon"></i>
          <div>
            <h3>{{ scene.sceneName }}</h3> 
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const emit = defineEmits(['scene-selected']);  // 定义要触发的事件
const activeSceneId = ref('');

const props = defineProps({
  scenes: {
    type: Array,
    default: () => [],
    required: false
  }
})

const selectScene = (code) => {  
  activeSceneId.value = code ;
  emit('scene-selected', code);  // 触发事件并传递选中的ID
}
</script>

<style lang="scss" scoped>
.scene-container {
  padding: 12px 0;
  
  .scene-title {
    font-size: 15px;
    font-weight: 600;
    color: #333;
    margin: 0 0 12px 12px;
  }
}

.scene-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.scene-item {
  padding: 10px 12px;
  cursor: pointer;
  border-left: 3px solid transparent;
  transition: all 0.2s ease;
  border-radius: 5px;
  
  &.active {
    background-color: #f5f7fa;
    border-left: 3px solid #409eff;
    
    .scene-icon {
      color: #409eff;
    }
    
    h3 {
      color: #409eff;
    }
  }
  
  &:hover:not(.active) {
    background-color: #f5f7fa;
  }
}

.scene-content {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  
  .scene-icon {
    font-size: 16px;
    color: #909399;
    margin-top: 2px;
    flex-shrink: 0;
  }
  
  h3 {
    font-size: 14px;
    font-weight: 500;
    color: #333;
    margin: 0;
    line-height: 1.4;
  }
  
  .scene-description {
    font-size: 13px;
    color: #909399;
    margin: 4px 0 0;
    line-height: 1.4;
  }
}
</style>