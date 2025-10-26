<template>
  <div class="row">
    <el-form
      :model="formData"
      :rules="rules"
      ref="formRef"
      label-width="120px"
      :label-position="'top'">

        <el-tabs v-model="activeTab" :tab-position="'top'">
          <el-tab-pane label="规划提示词" name="plan">
            <el-form-item prop="planPrompt" style="margin:0;">
              <div class="prompt-editor-container">
                <code-mirror basic :lang="lang" v-model="formData.planPrompt"
                             :theme="theme" :extensions="extensions" :style="'height:calc(100vh - 220px)'" />
              </div>
            </el-form-item>
          </el-tab-pane>

          <el-tab-pane label="执行提示词" name="worker">
            <el-form-item prop="workerPrompt" style="margin:0;">
              <div class="prompt-editor-container">
                <code-mirror basic :lang="lang" v-model="formData.workerPrompt"
                             :theme="theme" :extensions="extensions" :style="'height:calc(100vh - 220px)'" />
              </div>
            </el-form-item>
          </el-tab-pane>

          <el-tab-pane label="总结提示词" name="summary">
            <el-form-item prop="summaryPrompt" style="margin:0;">
              <div class="prompt-editor-container">
                <code-mirror basic :lang="lang" v-model="formData.summaryPrompt"
                             :theme="theme" :extensions="extensions" :style="'height:calc(100vh - 220px)'" />
              </div>
            </el-form-item>
          </el-tab-pane>
        </el-tabs>

      <!-- 提交按钮 -->
      <el-form-item>
        <div style="width: 100%;text-align: right;margin-top:30px">
          <el-button type="primary" @click="handleSubmit" size="large">确认</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import CodeMirror from 'vue-codemirror6'
import { oneDark } from '@codemirror/theme-one-dark'
import { EditorView } from '@codemirror/view'
import { markdown } from '@codemirror/lang-markdown'

// 事件名与你当前项目一致
const emit = defineEmits(['handleDeepSearchPromptStatusPanelClose'])

// 表单引用
const formRef = ref(null)

// active tab
const activeTab = ref('plan')

// 表单数据，对应后端 DeepSearchPromptData
const formData = ref({
  planPrompt: '',
  workerPrompt: '',
  summaryPrompt: ''
})

// 校验规则（示例设为必填，可按需调整）
const rules = {
  planPrompt: [{ required: true, message: '计划提示不能为空', trigger: 'blur' }],
  workerPrompt: [{ required: true, message: '工作提示不能为空', trigger: 'blur' }],
  summaryPrompt: [{ required: true, message: '摘要提示不能为空', trigger: 'blur' }]
}

// CodeMirror 配置
const lang = markdown()
const extensions = [
  oneDark,
  EditorView.lineWrapping
]
const theme = {
  "&": {
    fontSize: "12pt"
  }
}

// 提交：校验通过后 emit 出 DeepSearchPromptData 对象
const handleSubmit = () => {
  if (!formRef.value) return
  formRef.value.validate((valid) => {
    if (valid) {
      // 发出事件，payload 与 Java 类字段一致
      emit('handleDeepSearchPromptStatusPanelClose', { ...formData.value })
      ElMessage.success('提交成功')
    } else {
      ElMessage.error('表单验证失败，请检查输入')
    }
  })
}

// 外部设置配置参数（例如从后端拿到 DeepSearchPromptData），会直接设置到 v-model，CodeMirror 会同步展示
const setConfigParams = async (params = {}) => {
  formData.value.planPrompt = params.planPrompt ?? ''
  formData.value.workerPrompt = params.workerPrompt ?? ''
  formData.value.summaryPrompt = params.summaryPrompt ?? ''

  // 确保 DOM 更新（可选）
  await nextTick()
}

// 获取当前表单数据
const getFormData = () => {
  return { ...formData.value }
}

defineExpose({
  setConfigParams,
  getFormData
})
</script>

<style>
.prompt-editor-container{
  width: 100%;
}

/* 保证 CodeMirror 编辑器高度展示 */
.prompt-editor-container .cm-editor {
  height: 100% !important;
}
</style>