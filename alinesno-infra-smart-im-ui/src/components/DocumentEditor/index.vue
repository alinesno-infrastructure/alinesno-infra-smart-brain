<template>
  <div class="document-editor">
    <TinyMCEEditor 
      :minHeight="editorHeight"  
      @setHtml="handleSetHtml"
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

// 计算编辑器高度
const calculateEditorHeight = () => {
  const vh = window.innerHeight;
  editorHeight.value = vh - 120; // 100vh - 120px
};

// 处理内容变化
const handleSetHtml = (textContent, htmlContent) => {
  console.log("纯文本内容:", textContent);
  console.log("HTML 内容:", htmlContent);
};

// 暴露 setContent 方法
const setContent = (content) => { 
  customEditorContent.value = content ;
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
});

</script>

<style lang="scss" scoped>
.document-editor {
  margin-top: 20px;  
  
  /* 确保TinyMCE编辑器填满容器 */
  & ::v-deep .tox-tinymce {
    height: calc(100vh - 140px) !important;
  }
}
</style>