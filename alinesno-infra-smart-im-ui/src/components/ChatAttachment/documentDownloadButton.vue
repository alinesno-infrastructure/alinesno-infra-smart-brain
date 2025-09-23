<template>
  <el-tooltip effect="dark" content="导出成word" placement="top">
    <el-button
      class="chat-download-attachment-button"
      @click="handleExportWord"
      :loading="loading"
      link
      size="small">
      <i v-if="!loading" class="fa-solid fa-file-word"></i>
    </el-button>
  </el-tooltip>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import {
  exportWord
} from "@/api/base/im/messageRerence";

const props = defineProps({
  message: {
    type: Object,
    default: () => ({})
  },
});

const loading = ref(false);

const pad = (n) => String(n).padStart(2, '0');

// 可读时间戳，例如 20250923_140811
function formatTimestamp(date = new Date()) {
  const y = date.getFullYear();
  const m = pad(date.getMonth() + 1);
  const d = pad(date.getDate());
  const hh = pad(date.getHours());
  const mm = pad(date.getMinutes());
  const ss = pad(date.getSeconds());
  return `${y}${m}${d}_${hh}${mm}${ss}`;
}

// 或者使用 epoch 毫秒：Date.now()

// 简单文件名净化，替换非法字符并把空格改下划线，截断到合理长度
function sanitizeFileName(name, maxLen = 200) {
  if (!name) return '';
  // 替换 Windows/Unix 不允许的字符以及其他特殊符号
  const cleaned = name.replace(/[/\\?%*:|"<>]/g, '-').replace(/\s+/g, '_');
  return cleaned.substring(0, maxLen);
}

const handleExportWord = async () => {
  console.log('exportWord', props.message);

  // 基础名：优先用 message.name，否则退回到 messageId 或时间戳
  let baseName = props.message?.name?.trim() || (props.message?.messageId ? `message_${props.message.messageId}` : `message_${Date.now()}`);
  baseName = sanitizeFileName(baseName);

  // 选择时间戳方式：可读格式
  const ts = formatTimestamp(new Date());
  // 或者使用 epoch： const ts = Date.now();

  let documentName = `${baseName}_${ts}`;

  // 确保有 .docx 后缀
  if (!documentName.toLowerCase().endsWith('.docx')) {
    documentName += '.docx';
  }

  loading.value = true;
  try {
    const blob = await exportWord(props.message.chatText, props.message.messageId, documentName);
    const blobData = (blob && blob.data) ? blob.data : blob;
    if (!blobData) throw new Error('未返回文件数据');

    const file = new Blob([blobData], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
    const url = window.URL.createObjectURL(file);
    const a = document.createElement('a');
    a.href = url;
    a.download = documentName;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(url);
  } catch (err) {
    console.error('exportWord err', err);
    // 提示用户失败
  } finally {
    loading.value = false;
  }
};

nextTick(() => {
  console.log('document download props.message', JSON.stringify(props.message));
});
</script>

<style lang="scss" scoped>
.chat-download-attachment-button {
  margin-left: 12px !important;
}
</style>