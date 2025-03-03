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
          <el-form-item label="选择文档内容">
            <FlowCascader :nodeModel="props.nodeModel" v-model="formData.documentList" />
          </el-form-item>
          <el-form-item label="返回内容">
            <el-switch v-model="form.isPrint" size="small" />
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
import FlowCascader from '@/views/smart/assistant/workflow/common/FlowCascader'

// 绑定选择框的值
const value = ref('')
const value1 = ref('')

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
  documentList: [],
  isPrint:true 
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