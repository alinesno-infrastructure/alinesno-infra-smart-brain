<template>
  <div class="document-editor">
    <TinyMCEEditor  
      :minHeight="editorHeight"  
      @setHtml="handleSetHtml"
      :annotations="annotations" 
      ref="tinyMCEEditorRef"
      v-model="customEditorContent" 
    /> 
 
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import TinyMCEEditor from './TinyMCEEditor';

const tinyMCEEditorRef = ref(null);
const customEditorContent = ref('');
const editorHeight = ref(20); // 初始化为0，将在mounted中计算

const annotations = ref([]);

// 计算编辑器高度
const calculateEditorHeight = () => {
  const vh = window.innerHeight;
  editorHeight.value = vh - 120; // 100vh - 120px
};

// 处理内容变化
const handleSetHtml = (textContent, htmlContent) => { 
  console.log("handleSetHtml =  " + htmlContent)
};

// 暴露 setContent 方法
const setContent = (content) => { 
  customEditorContent.value = content ;  
};

// 暴露 getContent 方法
const getContent = () => {
  return tinyMCEEditorRef.value?.handleGetContent();
}

// 在父组件中调用搜索替换方法
const handleSearchReplace = (searchText , replaceText) => {
  tinyMCEEditorRef.value?.searchAndReplace(searchText , replaceText);  
};

// 组件挂载时计算高度并添加resize监听
onMounted(() => {
  calculateEditorHeight();
  window.addEventListener('resize', calculateEditorHeight);

});

// 组件卸载前移除resize监听
onBeforeUnmount(() => {
  window.removeEventListener('resize', calculateEditorHeight);
});

// 暴露方法给父组件
defineExpose({
  setContent,
  getContent,
  handleSearchReplace
});

</script>

<style lang="scss" scoped>
.document-editor {
  margin-top: 20px;  
  
  /* 确保TinyMCE编辑器填满容器 */
  & ::v-deep {
    .tox-tinymce {
      height: calc(100vh - 140px) !important;
    } 

    /* 使用伪元素实现视觉标记 */
/* 批注标记基础样式 */
.annotation-marker {
  background-color: rgba(255, 229, 143, 0.3);
  border-bottom: 2px dotted #FFB800;
  padding: 0 1px;
  border-radius: 2px;
  transition: all 0.2s;
  position: relative;
}

/* 选中状态样式 */
.annotation-marker.active {
  background-color: rgba(255, 213, 79, 0.5);
  box-shadow: 0 0 0 2px rgba(255, 184, 0, 0.3);
}

/* 批注工具栏样式 */
.annotation-tooltip {
  position: absolute;
  bottom: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%);
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  padding: 12px;
  min-width: 280px;
  z-index: 10000;
  border: 1px solid #EBEEF5;
}

/* 工具栏箭头 */
.annotation-tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border-width: 6px;
  border-style: solid;
  border-color: white transparent transparent transparent;
}

  .annotation-marker {
    background-color: rgba(255, 229, 143, 0.3);
    border-bottom: 2px dotted #FFB800;
    padding: 0 1px;
    border-radius: 2px;
    transition: all 0.2s;
    position: relative;
    cursor: pointer;
  }
  
  .annotation-marker.active {
    background-color: rgba(255, 213, 79, 0.5);
    box-shadow: 0 0 0 2px rgba(255, 184, 0, 0.3);
  }
  
  .annotation-popover {
    position: relative;
    z-index: 10000;
  }

  } 
}
</style>