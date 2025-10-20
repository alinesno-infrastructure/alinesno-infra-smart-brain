<template>
  <div class="edit-right-panel">
    <!-- 头部：标题与完整介绍（换行显示） -->
    <div class="edit-right-panel-header">
      <div class="edit-right-panel-title">
        <i class="fa-solid fa-file-pdf"></i> 
        <span class="text-large font-600"> {{ props.currentDatasetGroup?.groupName }} </span>
      </div>
      <div class="edit-right-panel-introduce">
        {{ props.currentDatasetGroup?.groupDescription }}
      </div>
    </div>

    <!-- 知识库标识 -->
    <div class="kb-id">
      <div class="kb-label">知识库标识</div>
      <div class="kb-value">
        <span class="id-text">
          {{ props.currentDatasetGroup?.id }}
        </span>
        <button class="btn small" @click="copyId" aria-label="复制知识库标识">
          复制
        </button>
      </div>
    </div>

    <!-- 模型选择：垂直排列，标签在上 -->
    <div class="models-list">
      <div class="field">
        <div class="field-label">向量模型</div>
        <div class="field-control">
          <LLMSelector :modelType="'vector_model'" :size="'large'" v-model="formData.embeddingModelId" />
        </div>
      </div>

      <div class="field">
        <div class="field-label">OCR模型</div>
        <div class="field-control">
          <LLMSelector :modelType="'ocr_model'" :size="'large'" v-model="formData.ocrModelId" />
        </div>
      </div>

      <div class="field">
        <div class="field-label">文档识别模型</div>
        <div class="field-control">
          <LLMSelector :modelType="'large_language_model'" :size="'large'"
            v-model="formData.documentRecognitionModelId" />
        </div>
      </div>
    </div>

    <div class="foot-note">提示：选择合适的模型能提升检索与解析效果。</div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import LLMSelector from './LLMSelector'

const props = defineProps({
  currentDatasetGroup: {
    type: Object,
    default: null
  }
})

// 使用 ref 创建可响应对象，并确保当 props 变化时同步
const formData = ref({
  embeddingModelId: props.currentDatasetGroup?.embeddingModelId ?? '',
  ocrModelId: props.currentDatasetGroup?.ocrModelId ?? '',
  documentRecognitionModelId: props.currentDatasetGroup?.documentRecognitionModelId ?? ''
})

// 当父组件异步传入 currentDatasetGroup 时更新 formData（立即执行并在变化时同步）
watch(
  () => props.currentDatasetGroup,
  (newGroup) => {
    formData.value.embeddingModelId = newGroup?.embeddingModelId ?? ''
    formData.value.ocrModelId = newGroup?.ocrModelId ?? ''
    formData.value.documentRecognitionModelId = newGroup?.documentRecognitionModelId ?? ''
  },
  { immediate: true }
)

async function copyId() {
  const id = props.currentDatasetGroup?.id ?? ''
  if (!id) {
    // eslint-disable-next-line no-alert
    alert('没有可复制的知识库标识')
    return
  }

  try {
    if (navigator.clipboard?.writeText) {
      await navigator.clipboard.writeText(id)
      // eslint-disable-next-line no-alert
      alert('知识库标识已复制')
    } else {
      // 备用方式（老浏览器）
      const el = document.createElement('textarea')
      el.value = id
      document.body.appendChild(el)
      el.select()
      document.execCommand('copy')
      document.body.removeChild(el)
      // eslint-disable-next-line no-alert
      alert('知识库标识已复制（使用备份方式）')
    }
  } catch (e) {
    // eslint-disable-next-line no-alert
    alert('复制失败，请手动复制：' + id)
  }
}
</script>

<style scoped lang="scss">
.edit-right-panel {
  background: #ffffff;              /* 背景白色 */
  width: 100%;
  padding: 16px;
  border-radius: 8px;
  box-sizing: border-box;
  border: 1px solid rgba(0,0,0,0.04);
  color: #111827;
}

/* 头部 */
.edit-right-panel-header {
  margin-bottom: 14px;
}
.edit-right-panel-title {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 10px;
}
.edit-right-panel-introduce {
  font-size: 13px;
  color: #4b5563;
  line-height: 1.6;
  white-space: normal; /* 多行显示 */
}

/* KB ID */
.kb-id {
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: transparent;
  margin-bottom: 14px;
}
.kb-label {
  font-size: 12px;
  color: #6b7280;
}
.kb-value {
  display: flex;
  gap: 8px;
  align-items: center;
}
.id-text {
  font-size: 13px;
  color: #374151;
  background: rgba(0,0,0,0.03);
  padding: 6px 8px;
  border-radius: 6px;
  word-break: break-all;
}

/* 模型列表：垂直排列，标签在上方 */
.models-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.field {
  display: flex;
  flex-direction: column; /* 垂直排列：标签在上，控件在下 */
  gap: 8px;
  padding: 8px 6px;
  border-radius: 6px;
  background: #fff;
  border: 1px solid rgba(0,0,0,0.03);
}
.field-label {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

/* 确保选择控件占满宽度 */
.field-control {
  width: 100%;
}

/* 按钮样式（复制） */
.btn {
  border: none;
  background: #efefef;
  color: #111827;
  padding: 6px 8px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
}
.btn.small {
  padding: 6px 8px;
}

/* 底部提示 */
.foot-note {
  margin-top: 12px;
  font-size: 12px;
  color: #6b7280;
  background: transparent;
}

/* 在极窄屏下微调 */
@media (max-width: 360px) {
  .edit-right-panel {
    padding: 12px;
  }
  .edit-right-panel-title { font-size: 15px; }
  .field-label { font-size: 12px; }
  .id-text { font-size: 12px; padding: 5px 6px; }
}
</style>