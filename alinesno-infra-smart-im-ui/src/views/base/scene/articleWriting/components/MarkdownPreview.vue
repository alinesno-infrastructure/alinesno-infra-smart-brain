<!-- components/MarkdownPreview.vue -->
<template>
  <el-dialog 
    v-model="visible" 
    title="内容预览" 
    width="960"
    top="5vh"
    custom-class="markdown-preview-dialog"
  >
    <div class="preview-container">
      <div class="markdown-body" v-html="renderedContent"></div>
    </div>
    
    <template #footer>
      <el-button size="large" text bg @click="handleRegenerate">重新生成</el-button>
      <el-button type="primary" size="large" bg @click="handleConfirm">确认替换</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue';
import MarkdownIt from 'markdown-it';

const props = defineProps({
  content: String
});

const emit = defineEmits(['confirm', 'regenerate']);

const visible = ref(false);
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true
});

const renderedContent = computed(() => md.render(props.content));

const open = () => {
  visible.value = true;
};

const handleConfirm = () => {
  emit('confirm', props.content);
  visible.value = false;
};

const handleRegenerate = () => {
  emit('regenerate');
  visible.value = false;
};

defineExpose({ open });
</script>

<style scoped>
.preview-container {
  max-height: 65vh;
  overflow-y: auto;
  padding: 10px;
}

.markdown-body {
  padding: 0px;
}
</style>