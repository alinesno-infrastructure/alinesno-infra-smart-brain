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
          <el-form-item label="视觉模型">
            <LLMSelector :nodeModel="props.nodeModel" v-model="form.llmModelId" />
          </el-form-item>
          <el-form-item label="提示词">
            <div class="function-CodemirrorEditor mb-8" style="height: 120px;width:100%">
              <ScriptEditorPanel ref="auditEditorRef" :lang="'markdown'" />
              <div class="function-CodemirrorEditor__footer">
                <el-button text @click="openCodemirrorDialog" style="background-color: transparent !important;" class="magnify">
                  <i class="fa-solid fa-up-right-and-down-left-from-center"></i>
                </el-button>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="选择图片">
            <FlowCascader :nodeModel="props.nodeModel" v-model="formData.imageList" />
          </el-form-item>
          <el-form-item label="返回内容">
            <el-switch v-model="form.isPrint" size="small" />
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
        回答内容 {answer}
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="'函数内容（Groovy）'"
      append-to-body
      width="800"
    >
      <template #header>
        <div class="dialog-footer mt-24" style="display: flex;align-items: center; justify-content: space-between; ">
          <div>
            提示词
          </div>
          <div>
            <el-button type="primary" size="large" text bg @click="confirmAndSave"> 确定保存 </el-button>
          </div>
        </div>
      </template>
      <ScriptEditorFullPanel ref="auditFullEditorRef" :lang="'markdown'" />
    </el-dialog>

  </div>
</template>

<script setup>
import { set } from 'lodash'
import { ref, reactive } from 'vue'

import ScriptEditorPanel from '@/views/smart/assistant/workflow/components/ScriptEditor';
import ScriptEditorFullPanel from '@/views/smart/assistant/workflow/components/NodeScriptEditor';

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

// 绑定选择框的值
const auditEditorRef = ref(null)
const auditFullEditorRef = ref(null)

const dialogVisible = ref(false)
const cloneContent = ref('')

// 绑定选择框的值
const chatDataCode = ref('')
const value1 = ref('')

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

const form = {
  llmModelId: '',
  prompt: '',
  dialogue_number: 0,
  isPrint: true,
  imageList: []
}

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

// 确认保存
function confirmAndSave() {
  console.log('confirmAndSave')
  chatDataCode.value = auditFullEditorRef.value.getRawScript()
  auditEditorRef.value.setRawScript(chatDataCode.value)
  console.log('auditFullEditorRef.value.getRawScript() = ' + auditFullEditorRef.value.getRawScript())
  dialogVisible.value = false

  formData.value.prompt = chatDataCode.value
}

// 打开编辑对话框
function openCodemirrorDialog(type) {
  chatDataCode.value = auditEditorRef.value.getRawScript()
  dialogVisible.value = true
  nextTick(() => {
    auditFullEditorRef.value.setRawScript(chatDataCode.value)
  })
}

// function openCodemirrorDialog() {
//   cloneContent.value = chatDataCode.value.code
//   dialogVisible.value = true
// }

// // 选择框的选项列表
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

</script>

<style lang="scss" scoped>

.function-CodemirrorEditor__footer {
  position: absolute;
  bottom: 0px;
  right: 0px;
}

</style>