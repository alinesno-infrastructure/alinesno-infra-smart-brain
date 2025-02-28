<template>
  <div class="llm-selector-panel">
    <el-select class="llm-selector" clearable v-model="data" placeholder="请选择大模型">
      <el-option
        v-for="model in models"
        :key="model.name"
        :label="model.name"
        :value="model.name"
      >
        <template #default>
          <img :src="model.imageUrl" alt="模型图片" style="width: 20px; height: 20px; margin-right: 10px;">
          {{ model.name }}
        </template>
      </el-option>
    </el-select>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  properties: {
    type: Object,
    default: () => ({})
  },
  nodeModel: {
    type: Object
  }
});

// 定义大模型列表
const models = [
  {
    name: 'Deepseek-R1',
    imageUrl: 'http://data.linesno.com/icons/llm/deepseek.png' // 替换为实际的图片链接
  },
  {
    name: 'GPT-4',
    imageUrl: 'http://data.linesno.com/icons/llm/chatgpt.png' // 替换为实际的图片链接
  },
  {
    name: '文心一言',
    imageUrl: 'http://data.linesno.com/icons/llm/qwen.png' // 替换为实际的图片链接
  }
];

// 定义选中的模型
const selectedModel = ref('');

const emit = defineEmits(['update:modelValue'])
const data = computed({
  set: (value) => {
    console.log('data = ' + JSON.stringify(value))
    selectedModel.value = value
    emit('update:modelValue', value)
  },
  get: () => {
    return props.modelValue
  }
})

</script>

<style scoped lang="scss">
/* 可以在这里添加自定义样式 */
.llm-selector-panel {
    width:100%;
  .llm-selector {
    width: 100%;
  }
}
</style>