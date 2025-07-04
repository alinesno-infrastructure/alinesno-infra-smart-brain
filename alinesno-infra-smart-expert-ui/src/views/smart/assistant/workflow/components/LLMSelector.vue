<template>
  <div class="llm-selector-panel">
    <el-select
      class="llm-selector"
      clearable
      v-model="data"
      placeholder="请选择大模型"
    >
      <template #prefix>
        <!-- 根据选中的值动态显示图标 -->
        <img
          v-if="selectedModelIcon"
          :src="selectedModelIcon"
          alt="模型图片"
          style="width: 20px; height: 20px; margin-right: 0px;"
        />
      </template>
      <el-option
        v-for="model in models"
        :key="model.id"
        :label="model.name"
        :value="model.id"
      >
        <template #default>
          <img
            :src="model.imageUrl"
            alt="模型图片"
            style="width: 20px; height: 20px; margin-right: 10px;"
          />
          {{ model.name }}
        </template>
      </el-option>
    </el-select>
  </div>
</template>

<script setup>

import { ref, computed, defineEmits, nextTick } from 'vue';
import { getLlmIconPath } from '@/utils/llmIcons';

import {
  listAllLlmModel
} from "@/api/smart/assistant/llmModel";

// 定义 props
const props = defineProps({
  properties: {
    type: Object,
    default: () => ({})
  },
  nodeModel: {
    type: Object
  },
  modelValue: {
    type: String,
    default: ''
  },
  modelType: {
    type: String,
    default: ''
  }
});

// 定义大模型列表，添加 id 属性
const models = ref([]);

// 定义选中的模型
const selectedModel = ref('');

// 计算选中模型的图标
const selectedModelIcon = computed(() => {
  const selected = models.value.find((model) => model.id === selectedModel.value);
  return selected ? selected.imageUrl : null;
});

// 定义 emits
const emit = defineEmits(['update:modelValue']);

// 定义 computed 属性
const data = computed({
  set: (value) => {
    console.log('data = ' + JSON.stringify(value));
    selectedModel.value = value;
    emit('update:modelValue', value);
  },
  get: () => {
    return props.modelValue;
  }
});

nextTick(() => {
  listAllLlmModel(props.modelType).then(response => {
    console.log('listAllLlmModel = ' + JSON.stringify(response.data));
    // 转换接口返回的数据结构
    const newModels = response.data.map(item => ({
      id: item.id,
      name: item.modelName,
      imageUrl: getLlmIconPath(item.providerCode) 
    }));
    models.value = newModels;

    // 根据传入的 modelValue 更新 selectedModel
    const initialSelectedModel = models.value.find(model => model.id === props.modelValue);
    if (initialSelectedModel) {
      selectedModel.value = initialSelectedModel.id;
    }
  });
});
</script>

<style scoped lang="scss">
/* 可以在这里添加自定义样式 */
.llm-selector-panel {
  width: 100%;
  .llm-selector {
    width: 100%;
  }
}
</style>