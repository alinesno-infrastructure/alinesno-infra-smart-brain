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
          <el-form-item label="AI模型">
            <!-- 
            <el-select v-model="value" placeholder="请选择语音识别模型" style="width: 240px">
              <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
            </el-select> 
            -->
            <LLMSelector :nodeModel="props.nodeModel" />
          </el-form-item>
          <el-form-item label="系统角色">
            <!-- <el-input type="textarea" :rows="3" resize="none" placeholder="角色设置" /> -->

            <div class="function-CodemirrorEditor mb-8" style="height: 120px;width:100%">
              <ScriptEditorPanel ref="auditEditorRef" :lang="'java'" />
              <div class="function-CodemirrorEditor__footer">
                <el-button text @click="openCodemirrorDialog('system')" style="background-color: transparent !important;" class="magnify">
                  <i class="fa-solid fa-up-right-and-down-left-from-center"></i>
                </el-button>
              </div>
            </div>

          </el-form-item>
          <el-form-item label="提示词">
            <!-- <el-input type="textarea" :rows="4" resize="none" placeholder="角色设置" /> -->
            <div class="function-CodemirrorEditor mb-8" style="height: 120px;width:100%">
              <ScriptEditorPanel ref="auditEditorRef" :lang="'java'" />
              <div class="function-CodemirrorEditor__footer">
                <el-button text @click="openCodemirrorDialog" style="background-color: transparent !important;" class="magnify">
                  <i class="fa-solid fa-up-right-and-down-left-from-center"></i>
                </el-button>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="返回内容">
            <el-switch v-model="value1" size="small" />
          </el-form-item>
          <el-form-item label="输出思考">
            <el-switch v-model="value1" size="small" />
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
      <div class="output-content">
        思考过程 {reasoning_content}
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="'函数内容（Groovy）'"
      width="800"
      append-to-body
    >
      <template #header>
        <div class="dialog-footer mt-24" style="display: flex;align-items: center; justify-content: space-between; ">
          <div>
            {{ dialogTitle }} 
          </div>
          <div>
            <el-button type="primary" size="large" text bg @click="submitDialog"> 确认</el-button>
          </div>
        </div>
      </template>
      <ScriptEditorFullPanel ref="auditEditorRef" :lang="'java'" />
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

import ScriptEditorPanel from '@/views/smart/assistant/workflow/components/ScriptEditor';
import ScriptEditorFullPanel from '@/views/smart/assistant/workflow/components/NodeScriptEditor';

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

const dialogVisible = ref(false)
const cloneContent = ref('')

// 绑定选择框的值
const chatDataCode = ref('')
const value1 = ref('')
const dialogTitle = ref('')

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

function openCodemirrorDialog(type) {
  dialogTitle.value = type == 'system' ?'系统角色': '提示词'
  cloneContent.value = chatDataCode.value.code
  dialogVisible.value = true
}

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

.function-CodemirrorEditor__footer {
  position: absolute;
  bottom: 0px;
  right: 0px;
}

</style>