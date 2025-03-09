<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <FlowContainer :nodeModel="nodeModel" :properties="properties">

    <!-- 节点设置部分 -->
    <div class="node-settings">
      <!-- 节点设置标题 -->
      <div class="settings-title" style="display: flex;align-items: center;justify-content: space-between;">
        <span>
          节点设置 
        </span>
          <el-select v-model="formData.replayType" size="small" placeholder="回复方式" style="margin-right:8px;width:100px">
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
      </div>
      <!-- 节点设置表单区域 -->
      <div class="settings-form">
        <el-form :model="formData" label-width="auto" label-position="top">

          <el-form-item label="输入参数" v-if="formData.replayType === 'link'">
            <FlowCascader :nodeModel="props.nodeModel" v-model="formData.replayParams" />
          </el-form-item>

          <el-form-item label="指定回答内容" v-if="formData.replayType === 'text'">
            <!-- <el-input type="textarea" :rows="5" resize="none" placeholder="角色设置" /> -->
            <div class="function-CodemirrorEditor mb-8" style="height: 120px;width:100%">
              <ScriptEditorPanel ref="auditEditorRef" :lang="'markdown'" />
              <div class="function-CodemirrorEditor__footer">
                <el-button text @click="openCodemirrorDialog" style="background-color: transparent !important;" class="magnify">
                  <i class="fa-solid fa-up-right-and-down-left-from-center"></i>
                </el-button>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="返回内容">
            <el-switch v-model="formData.isPrint" size="small" />
          </el-form-item>
        </el-form>
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
            请输入指定回答内容
          </div>
          <div>
            <el-button type="primary" size="large" text bg @click="confirmAndSave"> 确定保存 </el-button>
          </div>
        </div>
      </template>
      <ScriptEditorFullPanel ref="auditFullEditorRef" :lang="'markdown'" />
    </el-dialog>

  </FlowContainer>
</template>

<script setup>
import { set } from 'lodash'
import { ref, reactive } from 'vue'

import FlowContainer from '@/views/smart/assistant/workflow/common/FlowContainer'
import ScriptEditorPanel from '@/views/smart/assistant/workflow/components/ScriptEditor';
import ScriptEditorFullPanel from '@/views/smart/assistant/workflow/components/NodeScriptEditor';

import FlowCascader from '@/views/smart/assistant/workflow/common/FlowCascader'

// 定义组件接收的属性
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

const auditEditorRef = ref(null)
const auditFullEditorRef = ref(null)

// 绑定选择框的值
const dialogVisible = ref(false)
const cloneContent = ref('')

// 绑定选择框的值
const chatDataCode = ref('')
const value1 = ref('')

const replayType = ref('text')

// 表单数据对象
const form = reactive({
  replayType: 'text',  // 回复方式 
  replayParams:[],
  replayContent: '',
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

// 确认保存
function confirmAndSave() {
  console.log('confirmAndSave')

  chatDataCode.value = auditFullEditorRef.value.getRawScript()
  auditEditorRef.value.setRawScript(chatDataCode.value)

  console.log('auditFullEditorRef.value.getRawScript() = ' + auditFullEditorRef.value.getRawScript())
  dialogVisible.value = false

  // 配置
  formData.value.replayContent = chatDataCode.value
}

function openCodemirrorDialog() {

  console.log('auditEditorRef.value.getRawScript() = ' + auditEditorRef.value.getRawScript());

  chatDataCode.value = auditEditorRef.value.getRawScript();
  console.log('chatDatacode = ' + chatDataCode.value)
  dialogVisible.value = true

  nextTick(() => {
    auditFullEditorRef.value.setRawScript(chatDataCode.value)
  })
}


// 选择框的选项列表
const options = [
  { value: 'link',label: '引用' },
  { value: 'text', label: '文本' }
]

onMounted(() => {
  if(formData.value.replayContent){
    auditEditorRef.value.setRawScript(formData.value.replayContent)
  }
})

</script>

<style lang="scss" scoped>

.function-CodemirrorEditor__footer {
  position: absolute;
  bottom: 0px;
  right: 0px;
}

</style>