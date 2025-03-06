<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <FlowContainer :nodeModel="nodeModel" :properties="properties">

    <!-- 节点设置部分 -->
    <div class="node-settings">
      <!-- 节点设置标题 -->
      <div class="settings-title" style="display: flex; align-items: center; justify-content: space-between;">
        <span>
          参数设置
        </span>
        <el-button type="text" size="small" @click="openAddParamDialog">
          添加
        </el-button>
      </div>
      <!-- 节点设置表单区域 -->
      <div class="settings-form">
        <el-form :model="formData" label-width="auto" label-position="top">
          <div>
            <div v-if="formData.params.length == 0" class="empty-params">
              参数在这里显示
            </div>
            <template v-for="(param, index) in formData.params" :key="index">
              <div style="width: 100%" class="form-params-item" @mouseenter="showDeleteBtn = index"
                @mouseleave="showDeleteBtn = -1">
                <div class="params-item-label">
                  <span>
                    {{ param.name }} <el-tag>{{ param.type }}</el-tag>
                  </span>
                  <span v-if="showDeleteBtn === index" style="margin-right: 10px">
                    <span @click="deleteNode(index)">
                      <i class="fa-solid fa-trash-can"></i>
                    </span>
                  </span>
                </div>
                <div>
                  <FlowCascader :nodeModel="props.nodeModel" v-model="param.inputData" />
                </div>
              </div>
            </template>
          </div>

        </el-form>
      </div>

    </div>

    <div class="node-settings">
      <!-- 节点设置标题 -->
      <div class="settings-title" style="display: flex; align-items: center; justify-content: space-between;">
        <span>
          函数内容(Groovy)
        </span>
      </div>

      <!-- 节点设置表单区域 -->
      <div class="settings-form">
        <el-form :model="formData" label-width="auto" label-position="top">

          <el-form-item>
            <div class="function-CodemirrorEditor mb-8" style="height: 120px; width: 100%">
              <ScriptEditorPanel ref="auditEditorRef" :lang="'java'" />
              <div class="function-CodemirrorEditor__footer">
                <el-button text @click="openCodemirrorDialog" style="background-color: transparent !important;"
                  class="magnify">
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

    <el-dialog v-model="dialogVisible" :title="'函数内容（Groovy）'" append-to-body fullscreen>
      <template #header>
        <div class="dialog-footer mt-24" style="display: flex; align-items: center; justify-content: space-between; ">
          <div>
            函数内容(Groovy)
          </div>
          <div>
            <el-button type="danger" size="large" text bg @click="submitDialog"> 试运行 </el-button>
            <el-button type="primary" size="large" text bg @click="confirmAndSave()"> 确定保存 </el-button>
          </div>
        </div>
      </template>
      <ScriptEditorFullPanel ref="auditFullEditorRef" :lang="'java'" />
    </el-dialog>

    <el-dialog v-model="addParamDialogVisible" title="添加参数" append-to-body width="500">
      <el-form :model="newParam" label-width="80px">
        <el-form-item label="参数名称">
          <el-input v-model="newParam.name" />
        </el-form-item>

        <el-form-item label="参数类型">
          <el-select v-model="newParam.type" placeholder="请选择参数类型">
            <el-option v-for="t in javaTypes" :key="t" :label="t" :value="t"></el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <template #footer>
        <span>
          <el-button @click="addParamDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addParam">确定</el-button>
        </span>
      </template>
    </el-dialog>

  </FlowContainer>
</template>

<script setup>
import { set } from 'lodash'
import { ref, reactive, onMounted, nextTick, computed } from 'vue'

import FlowContainer from '@/views/smart/assistant/workflow/common/FlowContainer'
import ScriptEditorPanel from '@/views/smart/assistant/workflow/components/ScriptEditor'
import ScriptEditorFullPanel from '@/views/smart/assistant/workflow/components/ScriptEditorFull'
import FlowCascader from '@/views/smart/assistant/workflow/common/FlowCascader'

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
})

// 绑定选择框的值
const auditEditorRef = ref(null)
const auditFullEditorRef = ref(null)
const value = ref('')
const chatDataCode = ref('')
const value1 = ref('')
const dialogVisible = ref(false)
const cloneContent = ref('')
const addParamDialogVisible = ref(false)
const newParam = ref({ name: '', type: '' })
const showDeleteBtn = ref(-1)

// Java 常见类型列表
const javaTypes = ref(['int', 'String', 'boolean', 'double', 'long', 'Array', 'List', 'Map'])

// 表单数据对象
const form = reactive({
  params: [],
  isPrint: false
})

const formData = computed({
  get: () => {
    if (props.nodeModel.properties.node_data) {
      console.log('rawScript = ' + props.nodeModel.properties.node_data.rawScript)
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

  formData.value.rawScript = chatDataCode.value
}

// 打开编辑器对话框
function openCodemirrorDialog() {
  chatDataCode.value = auditEditorRef.value.getRawScript()
  dialogVisible.value = true
  nextTick(() => {
    auditFullEditorRef.value.setRawScript(chatDataCode.value)
  })
}

// 打开添加参数对话框
function openAddParamDialog() {
  newParam.value = { name: '', type: '' }
  addParamDialogVisible.value = true
}

// 添加参数
function addParam() {
  if (newParam.value.name && newParam.value.type) {
    formData.value.params.push({ ...newParam.value, inputData: '' })
    addParamDialogVisible.value = false
  }
}

// 删除参数
function deleteNode(index) {
  formData.value.params.splice(index, 1)
}

// 试运行
function submitDialog() {
  console.log('submitDialog')
}

onMounted(() => {
  auditEditorRef.value.setRawScript(formData.value.rawScript)
})

</script>

<style lang="scss" scoped>
.workflow-node-container {

  .node-settings {
    margin-bottom: 10px;

    .empty-params {
      font-size: 13px;
      border-radius: 3px;
      padding: 12px 10px;
      color: #606266;
    }

    .params-item-label {
      display: flex;
      justify-content: space-between;
      color: rgb(96, 98, 102);
      font-size: 13px;
      align-items: center;
      margin-bottom: 3px;
    }

  }

  .function-CodemirrorEditor {
    position: relative;

    &__footer {
      position: absolute;
      bottom: 0px;
      right: 0px;
    }
  }
}
</style>