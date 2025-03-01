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
    <div class="node-settings">
      <!-- 节点设置标题 -->
      <div class="settings-title">节点设置</div>
      <!-- 节点设置表单区域 -->
      <div class="settings-form">
        <el-form :model="form" label-width="auto" label-position="top">
          <!-- 语音识别模型选择项 -->
          <el-form-item label="语音识别模型">
            <LLMSelector :nodeModel="props.nodeModel" v-model="formData.llmModelId" />
          </el-form-item>
          <!-- 选择语音文件选择项 -->
          <el-form-item label="选择内容文件">
            <FlowCascader :nodeModel="props.nodeModel" v-model="formData.contentList" />
          </el-form-item>
          <el-form-item label="返回内容">
            <el-switch v-model="formData.isPrint" size="small"  />
          </el-form-item>
        </el-form>
      </div>
    </div>
    <!-- 输出参数部分 -->
    <div class="node-output">
      <!-- 输出参数标题 -->
      <div class="output-title">输出参数</div>
      <!-- 输出参数内容 -->
      <div class="output-content">
        结果 {result }
      </div>
    </div>
  </div>
</template>

<script setup>
import { set } from 'lodash'
import { ref, reactive } from 'vue'

import FlowCascader from '@/views/smart/assistant/workflow/common/FlowCascader'
import LLMSelector from '@/views/smart/assistant/workflow/components/LLMSelector'

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

// 表单数据对象
const form = reactive({
  llmModelId: '',
  contentList: [],
  isPrint: false
})

const formData = computed({
  get: () => {
    if (props.nodeModel.properties.node_data) {
      return props.nodeModel.properties.node_data
    } else {
      set(props.nodeModel.properties, 'node_data', form)
    }
    return props.nodeModel.properties.node_data
  },
  set: (value) => {
    set(props.nodeModel.properties, 'node_data', value)
  }
})


</script>

<style lang="scss" scoped>
</style>