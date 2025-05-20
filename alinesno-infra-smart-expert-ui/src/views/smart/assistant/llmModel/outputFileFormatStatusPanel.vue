
<template>
  <div class="row" style="padding: 20px">
    <el-form :model="formData" 
        :rules="rules" 
        ref="formRef" 
        label-width="120px" 
        size="large"
        :label-position="'top'">

      <el-form-item label="是否启用" prop="enable">
        <el-radio-group size="large" v-model="formData.enable">
          <el-radio :value="true">启用</el-radio>
          <el-radio :value="false">不启用</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 语音模型选择项 -->
      <el-form-item label="大模型" prop="llmModel">
        <LLMSelector :modelType="'large_language_model'" v-model="formData.llmModel" />
      </el-form-item>

      <el-form-item label="文件格式化" prop="fileFormats">
        <el-checkbox-group
          v-model="formData.fileFormats"
          size="large"
          :min="1"
          :max="5"
          style="width: 100%; display: flex; flex-wrap: wrap; gap: 12px;">
          <el-checkbox
            v-for="format in fileFormatOptions"
            :key="format.value"
            :label="format.value"
            class="flex items-center gap-2">
            <i :class="format.icon" class="text-lg"></i> &nbsp;<span>{{ format.label }}</span>
            <!-- <span class="text-gray-500 text-sm ml-1">{{ format.desc }}</span> -->
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <el-form-item style="margin-top: 20px;">
          <div style="display: flex;justify-content: flex-end;width: 100%;">
              <el-button type="primary" @click="handleSubmit" size="large" text bg>确认</el-button>
          </div>
      </el-form-item> 

    </el-form>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';

const emit = defineEmits(['handleOutputFileFormatStatusPanelPanelClose'])

import LLMSelector from '@/views/smart/assistant/workflow/components/LLMSelector'

// 初始化表单数据
const formData = ref({
  enable: true ,
  llmModel: '',
  fileFormats: ['html'] 
});

// 定义可用的语音模型数组
const llmModelOptions = ref([]);

// 在script部分添加文件格式配置数据
const fileFormatOptions = [
  { value: 'docx', label: 'DOCX', icon: 'fa-solid fa-file-word text-blue-600', desc: 'Word文档'},
  { value: 'pdf', label: 'PDF', icon: 'fa-solid fa-file-pdf text-red-600', desc: '可打印文档'},
  { value: 'excel', label: 'Excel', icon: 'fa-solid fa-file-excel text-green-600', desc: '电子表格'},
  { value: 'md', label: 'Markdown', icon: 'fa-brands fa-markdown text-gray-700', desc: '轻量文本' },
  { value: 'html', label: 'HTML', icon: 'fa-brands fa-html5 text-orange-500', desc: '网页格式'}
];

// 表单验证规则
const rules = ref({
  enable: [
    { required: true, message: '请确认是否选择模型', trigger: 'bulr' }
  ],
  llmModel: [
    { required: true, message: '请选择大模型', trigger: 'change' }
  ],
  fileFormats: [
    { required: true, message: '请至少选择一种文件格式', trigger: 'change' }
  ]
});

// 获取表单实例
const formRef = ref(null);

const setLlmModelOptions = (models) => {
  llmModelOptions.value = models;
}

const getFormData = () => {
  return formData.value
}

const setConfigParams = (params) => {
  formData.value = params ;
}

const handleSubmit = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            emit("handleOutputFileFormatStatusPanelPanelClose" , formData.value);
            ElMessage.success('提交成功');
        } else {
            ElMessage.error('表单验证失败，请检查输入');
        }
    });
};

onMounted(() => {
  formData.value.enable = true ;
})

defineExpose({
  setLlmModelOptions , 
  getFormData ,
  setConfigParams
})


</script>

<style scoped lang="scss">
</style>