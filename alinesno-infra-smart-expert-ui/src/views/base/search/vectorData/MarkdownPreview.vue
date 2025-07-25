<!-- components/MarkdownPreview.vue -->
<template>
    <div class="preview-container">
      <div class="markdown-body" v-html="renderedContent"></div>
    </div>
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
  padding: 0px;
  border-radius: 5px;
}

.markdown-body {
  padding: 10px;
}
</style>