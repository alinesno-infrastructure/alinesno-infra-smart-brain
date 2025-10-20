<template>
  <div class="llm-display-panel" :class="{ 'disabled': props.disabled }">
    <!-- 仅当模型数据存在时，显示图标和名称 -->
    <div v-if="displayModel" class="llm-display-content" style="display: flex; align-items: center;">
      <img
        :src="displayModel.imageUrl"
        alt="模型图标"
        style="width: 15px; height: 15px; margin-right: 5px;"
      />
      <span class="llm-display-name">{{ displayModel.name }}</span>
    </div>
    <!-- 无匹配模型或未传id时，显示占位文本 -->
    <div v-else class="llm-display-placeholder" style="color: #909399;">
      {{ props.placeholder || "未选择大模型" }}
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue';
import { getLlmIconPath } from '@/utils/llmIcons';
import { listAllLlmModel } from '@/api/base/im/scene/productResearchDataset';

// 定义Props：接收模型ID、类型、禁用状态、占位符
const props = defineProps({
  // 需显示的模型ID（核心入参）
  modelId: {
    type: String,
    default: ''
  },
  // 模型类型（用于接口筛选）
  modelType: {
    type: String,
    default: ''
  },
  // 禁用状态（仅样式变化）
  disabled: {
    type: Boolean,
    default: false
  },
  // 无数据时的占位文本
  placeholder: {
    type: String,
    default: ''
  }
});

// 模型列表数据源
const models = ref([]);
// 最终要显示的模型数据（匹配modelId的结果）
const displayModel = ref(null);

// 加载模型列表并匹配显示模型
const loadAndMatchModel = async () => {
  try {
    const response = await listAllLlmModel(props.modelType);
    // 转换接口数据结构（与选择器组件保持一致）
    models.value = response.data.map(item => ({
      id: item.id,
      name: item.modelName,
      imageUrl: getLlmIconPath(item.providerCode)
    }));
    // 根据modelId匹配目标模型
    matchDisplayModel();
  } catch (error) {
    console.error('加载大模型列表失败：', error);
    models.value = [];
    displayModel.value = null;
  }
};

// 根据当前modelId匹配显示模型
const matchDisplayModel = () => {
  if (!props.modelId) {
    displayModel.value = null;
    return;
  }
  // 从模型列表中找到id匹配的项
  displayModel.value = models.value.find(model => model.id === props.modelId) || null;
};

// 初始化加载
nextTick(() => {
  loadAndMatchModel();
});

// 监听modelId变化：当外部传入的id改变时，重新匹配显示
watch(
  () => props.modelId,
  () => {
    matchDisplayModel();
  },
  { immediate: true }
);

// 监听modelType变化：当模型类型改变时，重新加载列表并匹配
watch(
  () => props.modelType,
  () => {
    loadAndMatchModel();
  }
);
</script>

<style scoped lang="scss">
.llm-display-panel {
  width: 100%;
  padding: 0px 0; /* 保持与选择器组件相近的垂直间距 */
  font-size: 12px;

  &.disabled {
    .llm-display-name,
    .llm-display-placeholder {
      color: #c0c4cc; /* 禁用状态文本色 */
    }
  }

  .llm-display-content {
    color: #777; /* 正常文本色 */
  }
}
</style>