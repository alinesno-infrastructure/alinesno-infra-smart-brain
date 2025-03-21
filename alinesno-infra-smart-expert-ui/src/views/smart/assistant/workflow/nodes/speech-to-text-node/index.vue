<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <FlowContainer :nodeModel="nodeModel" :properties="properties">

    <!-- 节点设置部分 -->
    <div class="node-settings">
      <!-- 节点设置标题 -->
      <div class="settings-title">节点设置</div>
      <!-- 节点设置表单区域 -->
      <div class="settings-form">
        <el-form :model="formData" label-width="auto" label-position="top">
          <!-- 语音识别模型选择项 -->
          <el-form-item label="语音识别模型">
            <LLMSelector :nodeModel="props.nodeModel" v-model="formData.llmModelId" :modelType="'speech_recognition'" />
          </el-form-item>
          <!-- 选择语音文件选择项 -->
          <el-form-item label="选择语音文件">
            <FlowCascader :nodeModel="props.nodeModel" :properties="props.properties" v-model="formData.audioList" />
          </el-form-item>
          <el-form-item label="返回内容">
            <el-switch v-model="formData.isPrint" size="small" />
          </el-form-item>
        </el-form>
      </div>
    </div>

  </FlowContainer>
</template>

<script setup>
import { set } from 'lodash'
import { ref, reactive } from 'vue'

import FlowContainer from '@/views/smart/assistant/workflow/common/FlowContainer'
import LLMSelector from '@/views/smart/assistant/workflow/components/LLMSelector'
import FlowCascader from '@/views/smart/assistant/workflow/common/FlowCascader'
import { ModelType } from '@logicflow/core'

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
  audioList: [],
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