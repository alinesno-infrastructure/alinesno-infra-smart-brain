<template>
  <div class="markdown-content" v-html="renderedMarkdown" />
</template>

<script>
import { defineComponent, computed } from 'vue';
import MarkdownIt from 'markdown-it';

export default defineComponent({
  name: 'MarkdownRenderer',
  props: {
    content: {
      type: String,
      required: true,
      default: ''
    },
    options: {
      type: Object,
      default: () => ({})
    }
  },
  setup(props) {
    // 初始化 markdown-it 实例
    const md = new MarkdownIt({
      html: true,         // 允许 HTML 标签
      linkify: true,      // 自动转换链接
      typographer: true,  // 美化排版
      ...props.options    // 合并传入的选项
    });

    // 计算属性，将 markdown 转换为 HTML
    const renderedMarkdown = computed(() => {
      return md.render(props.content);
    });

    return {
      renderedMarkdown
    };
  }
});
</script>