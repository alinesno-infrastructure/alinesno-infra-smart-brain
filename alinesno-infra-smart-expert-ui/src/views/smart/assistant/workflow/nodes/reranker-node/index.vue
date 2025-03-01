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
        <el-form :model="formData" label-width="auto" label-position="top">
          <el-form-item label="重排内容">
            <FlowCascader :nodeModel="props.nodeModel" v-model="formData.rerankerReference" />
          </el-form-item>
          <el-form-item label="检索问题">
            <FlowCascader :nodeModel="props.nodeModel" v-model="formData.questionReference" />
          </el-form-item>
          <el-form-item label="重排模型">
            <LLMSelector :nodeModel="props.nodeModel" v-model="formData.rerankerModelId" />
          </el-form-item>
        </el-form>
        <div class="settings-title" style="display: flex;align-items: center;justify-content: space-between;">
          <span>
            检索参数
          </span>
          <el-button type="text" size="small" @click="handleSearchSettings">
            配置
          </el-button>
        </div>
        <div class="settings-content">
          <el-row>
            <el-col :span="12">
              <span class="settings-label-label">相似度高于</span>
            </el-col>
            <el-col :span="12">
              <span class="settings-label-text">{{ form.rerankerSetting.minRelevance }}</span>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <span class="settings-label-label">引用分段数TOP</span>
            </el-col>
            <el-col :span="12">
              <span class="settings-label-text">{{ form.rerankerSetting.topK }}</span>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <span class="settings-label-label">最多引用字符</span>
            </el-col>
            <el-col :span="12">
              <span class="settings-label-text">{{ form.rerankerSetting.quoteLimit }}</span>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
    <!-- 输出参数部分 -->
    <div class="node-output">
      <!-- 输出参数标题 -->
      <div class="output-title">输出参数</div>
      <!-- 输出参数内容 -->
      <div class="output-content">
        重排结果列表 {result_list}
      </div>
      <div class="output-content">
        重排结果{result}
      </div>
    </div>

    <el-dialog title="重排配置" v-model="datasetParamsConfigDialogVisible" width="600px" append-to-body>
        <div style="margin-bottom:30px">
            <rerankerParamsPanel @handleSelectDatasetParamsConfigClose="handleSelectDatasetParamsConfigClose" ref="datasetParamsChoicePanelRef" />
        </div>
    </el-dialog>

  </div>
</template>

<script setup>
import { set } from 'lodash'
import { ref, reactive } from 'vue'

import LLMSelector from '@/views/smart/assistant/workflow/components/LLMSelector'
import FlowCascader from '@/views/smart/assistant/workflow/common/FlowCascader'

import rerankerParamsPanel from './rerankerParamsPanel.vue'

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

const datasetConfigDialogVisible = ref(false)
const datasetParamsConfigDialogVisible = ref(false)

const datasetParamsChoicePanelRef= ref(null)
// const datasetChoicePanelRef = ref(null)

// 绑定选择框的值
// const value = ref('')
// const value1 = ref('')

// 表单数据对象
const form = reactive({
  rerankerReference : [],
  rerankerModelId: '',
  questionReference : [],
  rerankerSetting: {
    topK: 3,
    minRelevance: 0.3,
    quoteLimit: 5000
  }
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

// 选择框的选项列表
// const options = [
//   {
//     value: 'Option1',
//     label: 'Option1',
//   },
//   {
//     value: 'Option2',
//     label: 'Option2',
//   },
//   {
//     value: 'Option3',
//     label: 'Option3',
//   },
//   {
//     value: 'Option4',
//     label: 'Option4',
//   },
//   {
//     value: 'Option5',
//     label: 'Option5',
//   },
// ]

// 关闭数据集配置窗口
const handleSelectDatasetParamsConfigClose = (formData) => {
    if (datasetParamsConfigDialogVisible.value) {
        datasetParamsConfigDialogVisible.value = false;
        // agentModelConfigForm.value.datasetSearchConfig = formData;

        form.rerankerSetting = formData;
    }
}

// 关闭数据集配置窗口
function handleSelectDatasetConfigClose(selectItem) {
    if (datasetConfigDialogVisible.value) {
        datasetConfigDialogVisible.value = false;
        selectionDatasetData.value = selectItem ; // datasetChoicePanelRef.value.getSelectItemList();
        // agentModelConfigForm.value.knowledgeBaseIds = selectionDatasetData.value;
    }
}

// 搜索配置
const handleSearchSettings = () => {
  datasetParamsConfigDialogVisible.value = true ;
}
</script>

<style lang="scss" scoped>
</style>