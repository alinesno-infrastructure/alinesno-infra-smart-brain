<template>
  <!-- 工作流节点容器，包含节点标题、设置和输出参数等内容 -->
  <FlowContainer :nodeModel="nodeModel" :properties="properties">

    <div class="node-settings">
      <!-- 节点设置标题 -->
      <div class="settings-title" style="display: flex; align-items: center; justify-content: space-between;">
        <span>
          请求配置
        </span>
      </div>

      <!-- 节点设置表单区域 -->
      <div class="settings-form">
        <el-form :model="formData" label-width="auto" label-position="top">

          <el-form-item label="API接口">
            <el-input v-model="formData.url" placeholder="请输入请求链接">
              <template #prepend>
                <el-select v-model="formData.method" style="width:80px">
                  <el-option label="GET" value="GET"></el-option>
                  <el-option label="POST" value="POST"></el-option>
                  <el-option label="PUT" value="PUT"></el-option>
                  <el-option label="DELETE" value="DELETE"></el-option>
                </el-select>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <div style="display: flex;justify-content: space-between;width: 100%;font-size: 13px;color: #606266;">
              <span>请求头(Header)</span>
              <span>
                <el-button @click="openAddHeaderDialog" type="text" size="small">添加</el-button>
              </span>
            </div>
            <el-table :data="formData.headers" stripe :key="formData.headers.length">
              <el-table-column prop="key" width="70" label="键" :show-overflow-tooltip="true"/>
              <el-table-column prop="value" label="值">
                <template #default="scope">
                  <el-input v-model="scope.row.value" />
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="60">
                <template #default="scope">
                  <div style="display: flex;justify-content: space-between;align-items: center;gap:10px;margin-right:10px;">
                      <span @click="editHeader(scope.$index)">
                        <i class="fa-solid fa-feather" />
                      </span>
                      <span @click="removeHeader(scope.$index)">
                        <i class="fa-solid fa-trash"></i>
                      </span>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item>
            <div style="display: flex;justify-content: space-between;width: 100%;font-size: 13px;color: #606266;">
              <span>查询参数(Params)</span>
              <span>
                <el-button @click="openAddParamDialog" type="text" size="small">添加</el-button>
              </span>
            </div>
            <el-table :data="formData.params" stripe :key="formData.params.length">
              <el-table-column prop="key" width="70" label="键" :show-overflow-tooltip="true"/>
              <el-table-column prop="value" label="值">
                <template #default="scope">
                  <el-input v-model="scope.row.value" />
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="60">
                <template #default="scope">
                  <div style="display: flex;justify-content: space-between;align-items: center;gap:10px;margin-right:10px;">
                      <span @click="editParam(scope.$index)">
                        <i class="fa-solid fa-feather" />
                      </span>
                      <span @click="removeParam(scope.$index)">
                        <i class="fa-solid fa-trash"></i>
                      </span>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item label="请求体（Body）类型">
            <el-radio-group v-model="formData.bodyType">
              <el-radio label="json">JSON</el-radio>
              <el-radio label="text">Text</el-radio>
              <el-radio label="raw text">Raw Text</el-radio>
            </el-radio-group>
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

    <el-dialog v-model="addHeaderDialogVisible" title="添加请求头" append-to-body width="500">
      <el-form :model="newHeader" label-width="80px">
        <el-form-item label="键">
          <el-input v-model="newHeader.key" />
        </el-form-item>
        <el-form-item label="值">
          <el-input v-model="newHeader.value" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="addHeaderDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addHeader">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="editHeaderDialogVisible" title="编辑请求头" append-to-body width="500">
      <el-form :model="editedHeader" label-width="80px">
        <el-form-item label="键">
          <el-input v-model="editedHeader.key" />
        </el-form-item>
        <el-form-item label="值">
          <el-input v-model="editedHeader.value" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="editHeaderDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveEditedHeader">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="addParamDialogVisible" title="添加查询参数" append-to-body width="500">
      <el-form :model="newParam" label-width="80px">
        <el-form-item label="键">
          <el-input v-model="newParam.key" />
        </el-form-item>
        <el-form-item label="值">
          <el-input v-model="newParam.value" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="addParamDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addParam">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="editParamDialogVisible" title="编辑查询参数" append-to-body width="500">
      <el-form :model="editedParam" label-width="80px">
        <el-form-item label="键">
          <el-input v-model="editedParam.key" />
        </el-form-item>
        <el-form-item label="值">
          <el-input v-model="editedParam.value" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="editParamDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveEditedParam">确定</el-button>
        </span>
      </template>
    </el-dialog>

  </FlowContainer>
</template>

<script setup>
import { set } from 'lodash'
import { ref, reactive, onMounted, nextTick, computed, watch } from 'vue'

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
const addHeaderDialogVisible = ref(false)
const editHeaderDialogVisible = ref(false)
const addParamDialogVisible = ref(false)
const editParamDialogVisible = ref(false)
const newHeader = ref({ key: '', value: '' })
const editedHeader = ref({ key: '', value: '' })
const newParam = ref({ key: '', value: '' })
const editedParam = ref({ key: '', value: '' })
const showDeleteBtn = ref(-1)
let editingHeaderIndex = -1
let editingParamIndex = -1

// Java 常见类型列表
const javaTypes = ref(['int', 'String', 'boolean', 'double', 'long', 'Array', 'List', 'Map'])

// 表单数据对象
const form = reactive({
  params: [],
  headers: [],
  isPrint: false
})

const formData = computed({
  get: () => {
    if (props.nodeModel.properties.node_data) {
      if (!props.nodeModel.properties.node_data.headers) {
        props.nodeModel.properties.node_data.headers = [];
      }
      if (!props.nodeModel.properties.node_data.params) {
        props.nodeModel.properties.node_data.params = [];
      }
      return props.nodeModel.properties.node_data;
    } else {
      set(props.nodeModel.properties, 'node_data', form);
    }
    return props.nodeModel.properties.node_data;
  },
  set: (value) => {
    set(props.nodeModel.properties, 'node_data', value);
  }
});

// 确认保存
function confirmAndSave() {
  console.log('confirmAndSave')
  chatDataCode.value = auditFullEditorRef.value.getRawScript()
  auditEditorRef.value.setRawScript(chatDataCode.value)
  console.log('auditFullEditorRef.value.getRawScript() = ' + auditFullEditorRef.value.getRawScript())
  dialogVisible.value = false

  formData.value.rawScript = chatDataCode.value
}

const openAddHeaderDialog = () => {
  newHeader.value = { key: '', value: '' }
  addHeaderDialogVisible.value = true
}

const addHeader = () => {
  if (newHeader.value.key && newHeader.value.value) {
    formData.value.headers.push({ ...newHeader.value })
    addHeaderDialogVisible.value = false
  }
}

const removeHeader = (index) => {
  formData.value.headers.splice(index, 1)
}

const editHeader = (index) => {
  editingHeaderIndex = index
  editedHeader.value = { ...formData.value.headers[index] }
  editHeaderDialogVisible.value = true
}

const saveEditedHeader = () => {
  if (editedHeader.value.key && editedHeader.value.value) {
    // 更新 headers 数组中的对应项
    formData.value.headers.splice(editingHeaderIndex, 1, { ...editedHeader.value });
    editHeaderDialogVisible.value = false
  }
}

const openAddParamDialog = () => {
  newParam.value = { key: '', value: '' }
  addParamDialogVisible.value = true
}

const addParam = () => {
  if (newParam.value.key && newParam.value.value) {
    formData.value.params.push({ ...newParam.value })
    addParamDialogVisible.value = false
  }
}

const removeParam = (index) => {
  formData.value.params.splice(index, 1)
}

const editParam = (index) => {
  editingParamIndex = index
  editedParam.value = { ...formData.value.params[index] }
  editParamDialogVisible.value = true
}

const saveEditedParam = () => {
  if (editedParam.value.key && editedParam.value.value) {
    // 更新 params 数组中的对应项
    formData.value.params.splice(editingParamIndex, 1, { ...editedParam.value });
    editParamDialogVisible.value = false
  }
}

// 打开编辑器对话框
function openCodemirrorDialog() {
  chatDataCode.value = auditEditorRef.value.getRawScript()
  dialogVisible.value = true
  nextTick(() => {
    auditFullEditorRef.value.setRawScript(chatDataCode.value)
  })
}

// 试运行
function submitDialog() {
  console.log('submitDialog')
}

// 初始化 formData
onMounted(() => {
  formData.value;
});

// 监听 props.nodeModel 变化
watch(() => props.nodeModel, () => {
  formData.value;
}, { deep: true });
</script>

<style lang="scss" scoped>
.workflow-node-container {
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #fff;

  .node-title {
    display: flex;
    align-items: center;
    margin-bottom: 16px;

    .node-icon {
      margin-right: 8px;
    }

    .node-name {
      font-size: 16px;
      font-weight: 600;
    }
  }

  .node-settings {
    margin-bottom: 16px;

    .settings-title {
      margin-bottom: 8px;
    }

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

    .settings-form {
      .form-params-item {
        margin-bottom: 0px;
        border-radius: 4px;
        padding: 8px 0px;
        transition: border-color 0.3s;

        &:hover {
          border-color: #409eff;
        }
      }
    }
  }

  .node-output {
    .output-title {
      font-size: 14px;
      font-weight: 600;
      margin-bottom: 8px;
    }

    .output-content {
      font-size: 13px;
      color: #606266;
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