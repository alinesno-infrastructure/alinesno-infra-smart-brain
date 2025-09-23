<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <FlowContainer :nodeModel="nodeModel" :properties="properties">
    <!-- 节点设置部分 -->
    <div class="node-settings">
      <!-- 节点设置标题：统一样式，增加hover提示 -->
      <div class="settings-title settings-title-panel">
        <span>节点设置</span>
        <el-tooltip content="关联已创建的知识库" placement="top">
          <el-button 
            type="text" 
            size="small" 
            @click="openKnowledgeBaseSelection"
            class="link-btn"
          >
            <i class="fa-solid fa-link ml-1"></i>&nbsp;关联 
          </el-button>
        </el-tooltip>
      </div>

      <!-- 节点设置表单区域：优化布局，增加空状态提示 -->
      <div class="settings-form">
        <el-form :model="formData" label-width="100px" label-position="top" class="form-container">
          <!-- 已关联知识库列表 -->
          <el-form-item label="已关联知识库" class="knowledge-list-item">
            <!-- 空状态：优化提示文案与样式 -->
            <div 
              class="empty-state" 
              v-if="selectionDatasetData.length === 0"
            >
              <i class="fa-solid fa-folder-open text-gray-400 mr-2"></i>
              <span>暂无关联知识库，点击“关联”按钮添加</span>
            </div>

            <!-- 知识库列表：优化hover效果与删除交互 -->
            <div 
              class="select-knowledge-box" 
              v-for="(item, index) in selectionDatasetData" 
              :key="item.id" 
            >
              <div class="select-knowledge-item">
                <span class="knowledge-icon">
                  <i :class="item.icon" class="mr-2"></i> {{ item.name }} 
                </span>
                <el-tooltip content="移除知识库" placement="top">
                  <el-button 
                    class="remove-knowledge-btn" 
                    type="text" 
                    @click="removeKnowledge(index)" 
                    size="small"
                  >
                    <i class="fa-solid fa-trash-can"></i>
                  </el-button>
                </el-tooltip>
              </div>
            </div>
          </el-form-item>

          <!-- 检索问题：增加label提示，优化组件宽度 -->
          <el-form-item label="检索问题" class="question-reference-item">
            <el-tooltip content="选择需要检索的问题字段（从上游节点获取）" placement="top">
              <FlowCascader 
                :nodeModel="props.nodeModel" 
                v-model="formData.questionReference" 
                class="w-full"
              />
            </el-tooltip>
          </el-form-item>
        </el-form>

        <!-- 检索参数区域：优化标题交互，增加参数说明 -->
        <div class="search-params-section">
          <div class="settings-title settings-title-panel">
            <span>检索参数</span>
            <el-tooltip content="配置检索模式、相似度阈值等参数" placement="top">
              <el-button 
                type="text" 
                size="small" 
                @click="handleSearchSettings"
                class="link-btn"
              >
                <i class="fa-solid fa-sliders ml-1"></i>&nbsp;配置 
              </el-button>
            </el-tooltip>
          </div>

          <!-- 参数展示：优化布局，增加参数说明 -->
          <div class="settings-content">
            <el-row class="param-row" :gutter="12">
              <el-col :span="10">
                <span class="settings-label-label">
                  <el-tooltip content="向量检索（embedding）：基于语义相似度；全文检索：基于关键词匹配；混合检索：结合两者" placement="top">
                    检索模式
                  </el-tooltip>
                </span>
              </el-col>
              <el-col :span="14">
                <span class="settings-label-text">{{ getSearchTypeText(formData.datasetSetting.searchType) }}</span>
              </el-col>
            </el-row>
            <el-row class="param-row" :gutter="12">
              <el-col :span="10">
                <span class="settings-label-label">
                  <el-tooltip content="仅保留相似度高于此阈值的检索结果（0-1之间）" placement="top">
                    相似度高于
                  </el-tooltip>
                </span>
              </el-col>
              <el-col :span="14">
                <span class="settings-label-text">{{ formData.datasetSetting.minRelevance }}</span>
              </el-col>
            </el-row>
            <el-row class="param-row" :gutter="12">
              <el-col :span="10">
                <span class="settings-label-label">
                  <el-tooltip content="返回相关性最高的前N段内容" placement="top">
                    引用分段数TOP
                  </el-tooltip>
                </span>
              </el-col>
              <el-col :span="14">
                <span class="settings-label-text">{{ formData.datasetSetting.topK }}</span>
              </el-col>
            </el-row>
            <el-row class="param-row" :gutter="12">
              <el-col :span="10">
                <span class="settings-label-label">
                  <el-tooltip content="检索结果中引用内容的最大字符数限制" placement="top">
                    最多引用字符
                  </el-tooltip>
                </span>
              </el-col>
              <el-col :span="14">
                <span class="settings-label-text">{{ formData.datasetSetting.quoteLimit }}</span>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
    </div>

    <!-- 选择知识库弹窗：增加关闭逻辑优化 -->
    <el-dialog 
      title="选择知识库" 
      v-model="datasetConfigDialogVisible" 
      width="1024px" 
      append-to-body
      :before-close="handleDialogClose" 
    >
      <div class="dialog-content">
        <DatasetChoicePanel 
          @handleSelectDatasetConfigClose="handleSelectDatasetConfigClose" 
          ref="datasetChoicePanelRef" 
        />
      </div>
    </el-dialog>

    <!-- 检索参数配置弹窗：增加数据回显 -->
    <el-dialog 
      title="检索参数配置" 
      v-model="datasetParamsConfigDialogVisible" 
      width="600px" 
      append-to-body
      :before-close="handleDialogClose"
    >
      <div class="dialog-content">
        <DatasetParamsChoicePanel 
          @handleSelectDatasetParamsConfigClose="handleSelectDatasetParamsConfigClose" 
          ref="datasetParamsChoicePanelRef"
          :initialData="formData.datasetSetting" 
        />
      </div>
    </el-dialog>
  </FlowContainer>
</template>

<script setup>
import { set, cloneDeep } from 'lodash'
import { ref, reactive, computed, nextTick, watch } from 'vue'
import FlowContainer from '@/views/smart/assistant/workflow/common/FlowContainer'
import FlowCascader from '@/views/smart/assistant/workflow/common/FlowCascader'
import DatasetParamsChoicePanel from './datasetParamsChoicePanel'
import DatasetChoicePanel from './datasetChoicePanel'

// 接收父组件参数
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
    type: Object,
    required: true  // 节点模型为必填项，增加校验
  }
});

// 弹窗状态管理
const datasetConfigDialogVisible = ref(false)
const datasetParamsConfigDialogVisible = ref(false)

// 组件引用
const datasetParamsChoicePanelRef = ref(null)
const datasetChoicePanelRef = ref(null)

// 已关联知识库列表：与formData.datasetIdList联动
const selectionDatasetData = ref([])

// 表单默认数据（用于初始化）
const DEFAULT_FORM_DATA = {
  datasetIdList: [],
  datasetSetting: {
    topK: 3,
    isReRank: false,
    minRelevance: 0.6,
    quoteLimit: 5000,
    searchType: 'embedding'  // 默认向量检索
  },
  questionReference: []
}

// 表单数据：与nodeModel.properties.node_data双向绑定
const formData = computed({
  get: () => {
    // 若节点无数据，用默认数据初始化
    if (!props.nodeModel.properties.node_data) {
      set(props.nodeModel.properties, 'node_data', cloneDeep(DEFAULT_FORM_DATA))
    }
    return props.nodeModel.properties.node_data
  },
  set: (value) => {
    set(props.nodeModel.properties, 'node_data', value)
  }
})

// 监听formData.datasetIdList变化，同步更新selectionDatasetData
watch(
  () => formData.value.datasetIdList,
  (newVal) => {
    selectionDatasetData.value = newVal || []
  },
  { immediate: true, deep: true }  // 立即执行+深度监听
)

// 关闭弹窗通用方法
const handleDialogClose = () => {
  datasetConfigDialogVisible.value = false
  datasetParamsConfigDialogVisible.value = false
}

// 处理检索参数配置回调：更新datasetSetting
const handleSelectDatasetParamsConfigClose = (paramsData) => {
  if (paramsData && datasetParamsConfigDialogVisible.value) {
    // 深拷贝避免直接修改源数据
    formData.value.datasetSetting = cloneDeep(paramsData)
    datasetParamsConfigDialogVisible.value = false
  }
}

// 移除已关联的知识库：修正索引传递与数据同步
const removeKnowledge = (index) => {
  // 复制数组后删除，避免直接修改响应式数据
  const newDatasetList = [...selectionDatasetData.value]
  newDatasetList.splice(index, 1)
  // 同步更新formData
  formData.value.datasetIdList = newDatasetList
}

// 检索类型文本映射：增加embedding类型
const getSearchTypeText = (searchType) => {
  const typeMap = {
    'embedding': '向量检索',  // 匹配传递的searchType
    'vector': '向量检索',
    'fulltext': '全文检索',
    'hybrid': '混合检索'
  }
  return typeMap[searchType] || '未知检索类型'
}

// 打开知识库选择弹窗：初始化已选列表
const openKnowledgeBaseSelection = async () => {
  datasetConfigDialogVisible.value = true
  // 等待弹窗渲染完成后，传递已选列表
  await nextTick()
  if (datasetChoicePanelRef.value) {
    datasetChoicePanelRef.value.setSelectItemList(selectionDatasetData.value)
  }
}

// 处理知识库选择回调：同步已选列表
const handleSelectDatasetConfigClose = (selectedList) => {
  if (Array.isArray(selectedList) && datasetConfigDialogVisible.value) {
    // 深拷贝避免引用问题
    const newSelectedList = cloneDeep(selectedList)
    selectionDatasetData.value = newSelectedList
    formData.value.datasetIdList = newSelectedList
    datasetConfigDialogVisible.value = false
  }
}

// 打开检索参数配置弹窗：传递现有配置用于回显
const handleSearchSettings = async () => {
  datasetParamsConfigDialogVisible.value = true
  // 等待弹窗渲染完成后，传递当前参数
  await nextTick()
  if (datasetParamsChoicePanelRef.value && formData.value.datasetSetting) {
    datasetParamsChoicePanelRef.value.setInitialData(cloneDeep(formData.value.datasetSetting))
  }
}
</script>
<style lang="scss" scoped>
.select-knowledge-box{
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 5px;
  padding: 3px 10px;
  border-radius: 3px;
  background: #fff;
  box-shadow: 0px 0px 3px rgba(0, 0, 0, .12);

  .select-knowledge-item{
    display: flex;
    color:#444;
    justify-content: space-between;
    align-items: center;
    width: 100%;

    .remove-knowledge-btn{
      // display: none;
        display: block;
    }

    &:hover{
      .remove-knowledge-btn{
        display: block;
      }
    }

  }

}

.settings-title-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

</style>