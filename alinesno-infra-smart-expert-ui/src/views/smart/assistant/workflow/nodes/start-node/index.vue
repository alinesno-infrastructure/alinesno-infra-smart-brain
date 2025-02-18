<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <div class="workflow-node-container">
    <!-- 节点标题部分，包含图标和名称 -->
    <div class="node-title">
      <div class="node-icon">
        <i :class="props.properties.icon"></i>
      </div>
      <div class="node-name">
        {{ props.properties.stepName }}
      </div>
    </div>
    <!-- 节点设置部分 -->
    <div class="node-output">
      <!-- 输出参数标题 -->
      <div class="output-title">全局变量</div>
      <!-- 输出参数内容 -->
      <div class="output-content">
        当前时间 {time}
      </div>
      <div class="output-content">
        上节点内容{pre_content}
      </div>
      <div class="output-content">
        频道号{channeId}
      </div>
      <div class="output-content">
        历史对话{history_content}
      </div>
    </div>
    <!-- 输出参数部分 -->
    <div class="node-output">
      <!-- 输出参数标题 -->
      <div class="output-title">输出参数</div>
      <!-- 输出参数内容 -->
      <div class="output-content">
        用户消息{message}
      </div>
      <div class="output-content">
        文档{document}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const props = defineProps({
  properties: {
    type: Object,
    default: () => ({})
  },
  isSelected: {
    type: Boolean,
    default: false
  },
  nodeModel: {
    type: Object
  }
});

// 绑定选择框的值
const value = ref('')
const value1 = ref('')
const num = ref('1')

// 表单数据对象
const form = reactive({
  name: '',
  region: '',
  date1: '',
  date2: '',
  delivery: false,
  type: [],
  resource: '',
  desc: '',
})

// 选择框的选项列表
const options = [
  {
    value: 'Option1',
    label: 'Option1',
  },
  {
    value: 'Option2',
    label: 'Option2',
  },
  {
    value: 'Option3',
    label: 'Option3',
  },
  {
    value: 'Option4',
    label: 'Option4',
  },
  {
    value: 'Option5',
    label: 'Option5',
  },
]
</script>

<style lang="scss" scoped>
</style>