<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <div class="workflow-node-container">
    <!-- 节点标题部分，包含图标和名称 -->
    <div class="node-title">
      <div class="node-icon">
        <i class="fa-solid fa-book"></i>
      </div>
      <div class="node-name">知识库检索</div>
    </div>
    <!-- 节点设置部分 -->
    <div class="node-settings">
      <!-- 节点设置标题 -->
        <div class="settings-title" style="display: flex;align-items: center;justify-content: space-between;">
          <span>
            节点设置
          </span>
          <el-button type="text" size="small" @click="openKnowledgeBaseSelection">
           关联 
          </el-button>
      </div>
      <!-- 节点设置表单区域 -->
      <div class="settings-form">
        <el-form :model="formData" label-width="auto" label-position="top">
          <el-form-item>
            <!-- <el-input type="input" resize="none" placeholder="请选择知识库" /> -->

            <div style="font-size: 13px;width: 100%;color: #646a73;" v-if="selectionDatasetData.length === 0">
              关联知识库展示在这里
            </div>

            <div class="select-knowledge-box" v-for="(item , index) in selectionDatasetData" :key="index">
              <div class="select-knowledge-item">
                <span>
                  <i :class="item.icon"></i> {{ item.name }} 
                </span>
                <el-button class="remove-knowledge-btn" type="text" text @click="removeKnowledge">
                  <i class="fa-solid fa-trash-can"></i>
                </el-button>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="检索问题">
            <FlowCascader :nodeModel="props.nodeModel" v-model="formData.questionReference" />
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
              <span class="settings-label-label">检索模式</span>
            </el-col>
            <el-col :span="12">
              <span class="settings-label-text">{{ form.datasetSetting.searchType }}</span>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <span class="settings-label-label">相似度高于</span>
            </el-col>
            <el-col :span="12">
              <span class="settings-label-text">{{ form.datasetSetting.minRelevance }}</span>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <span class="settings-label-label">引用分段数TOP</span>
            </el-col>
            <el-col :span="12">
              <span class="settings-label-text">{{ form.datasetSetting.topK }}</span>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <span class="settings-label-label">最多引用字符</span>
            </el-col>
            <el-col :span="12">
              <span class="settings-label-text">{{ form.datasetSetting.quoteLimit }}</span>
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
        检索结果的分段列表 {paragraph_list}
      </div>
      <div class="output-content">
        满足直接回答的分段列表{is_hit_handling_method_list}
      </div>
      <div class="output-content">
        检索结果{data}
      </div>
      <div class="output-content">
        满足直接回答的分段内容 {directly_return}
      </div>
    </div>

    <el-dialog title="选择知识库" v-model="datasetConfigDialogVisible" width="1024px" append-to-body>
        <div style="margin-bottom:30px">
            <DatasetChoicePanel @handleSelectDatasetConfigClose="handleSelectDatasetConfigClose" ref="datasetChoicePanelRef" />
        </div>
    </el-dialog>

    <el-dialog title="知识库配置" v-model="datasetParamsConfigDialogVisible" width="600px" append-to-body>
        <div style="margin-bottom:30px">
            <DatasetParamsChoicePanel @handleSelectDatasetParamsConfigClose="handleSelectDatasetParamsConfigClose" ref="datasetParamsChoicePanelRef" />
        </div>
    </el-dialog>

  </div>
</template>

<script setup>
import { set } from 'lodash'
import { ref, reactive } from 'vue'

import FlowCascader from '@/views/smart/assistant/workflow/common/FlowCascader'

import DatasetParamsChoicePanel from './datasetParamsChoicePanel'
import DatasetChoicePanel from './datasetChoicePanel'

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
const datasetChoicePanelRef = ref(null)

const selectionDatasetData = ref([])

const selectDataset = ref([
  {icon:'fa-solid fa-truck' , name: '测试知识库', id: '1'} ,
  {icon:'fa-solid fa-ship' , name: '研发知识库', id: '1'} ,
]) // 存储选中的数据集

// 表单数据对象
// const form = reactive({
//   name: '',
//   region: '',
//   date1: '',
//   date2: '',
//   delivery: false,
//   type: [],
//   resource: '',
//   desc: '',
// })

// 表单数据对象
const form = reactive({
  datasetIdList: [],
  datasetSetting: {
    topK: 3,
    isReRank: false,
    minRelevance: 0.6,
    quoteLimit: 5000,
    searchType: 'embedding'
  },
  questionReference: []
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

// 关闭数据集配置窗口
const handleSelectDatasetParamsConfigClose = (formData) => {
    if (datasetParamsConfigDialogVisible.value) {
        datasetParamsConfigDialogVisible.value = false;
        // agentModelConfigForm.value.datasetSearchConfig = formData;

        console.log('handleSelectDatasetParamsConfigClose formData = ' + JSON.stringify(formData))

        form.datasetSetting = formData;
    }
}

// 移除已关联的知识库
const removeKnowledge = (index) => {
  // 根据传入的索引，从 selectionDatasetData 中移除对应的知识库
  selectionDatasetData.value.splice(index, 1);

  formData.value.datasetIdList = selectionDatasetData.value ;
};

// 打开关联知识库选择窗口
function openKnowledgeBaseSelection() {
    console.log('打开关联知识库选择窗口')
    // 这里可以添加打开选择窗口的逻辑，选择后更新knowledgeBaseIds
    datasetConfigDialogVisible.value = true;

    // 设置知识库值
    nextTick(() => {
        datasetChoicePanelRef.value.setSelectItemList(selectionDatasetData.value);
    });

}

// 关闭数据集配置窗口
function handleSelectDatasetConfigClose(selectItem) {

    console.log('关闭数据集配置窗口 = ' + selectItem)
  
    if (datasetConfigDialogVisible.value) {
        datasetConfigDialogVisible.value = false;
        selectionDatasetData.value = selectItem ; // datasetChoicePanelRef.value.getSelectItemList();
        // agentModelConfigForm.value.knowledgeBaseIds = selectionDatasetData.value;

        formData.value.datasetIdList = selectionDatasetData.value ;
    }
}

// 搜索配置
const handleSearchSettings = () => {
  datasetParamsConfigDialogVisible.value = true ;
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
</style>