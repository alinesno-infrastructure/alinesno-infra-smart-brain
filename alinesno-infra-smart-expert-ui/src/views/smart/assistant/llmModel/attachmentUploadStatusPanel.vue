<template>
  <div class="row" style="padding: 20px">
    <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px" :label-position="'left'">

      <el-form-item label="是否启用" prop="enable">
        <el-radio-group size="large" v-model="formData.enable">
          <el-radio :value="true">启用</el-radio>
          <el-radio :value="false">不启用</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 文件上传数限制 -->
      <el-form-item label="文件上传数" prop="fileUploadLimit">
          <el-slider size="large" show-input v-model="formData.fileUploadLimit" :step="1" :max="20" />
      </el-form-item>

      <!-- 图片上传数限制 -->
      <el-form-item  label="图片上传数" prop="imageUploadLimit" v-if="multiModelOptions.length > 0 || ocrModelOptions.length > 0">
          <el-slider size="large" show-input v-model="formData.imageUploadLimit" :step="1" :max="10" />
      </el-form-item>

      <!-- 识别类型 -->
      <el-form-item label="识别方式" v-if="multiModelOptions.length > 0 || ocrModelOptions.length > 0">
        <el-radio-group v-model="formData.recognitionType" @change="formData.modelId = ''">
          <el-radio value="ocr">OCR模型</el-radio>
          <el-radio value="llm">多态模型</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 多模态模型选择项 -->
      <el-form-item label="识别模型" prop="modelId" v-if="formData.recognitionType === 'llm' && multiModelOptions.length > 0">
        <el-select v-model="formData.modelId" clearable placeholder="请选择多模态大模型" size="large" style="width:100%">
          <el-option v-for="item in multiModelOptions" :key="item.id" :label="item.modelName" :value="item.id">
            <template #default>
              <div>
                <img :src="getLlmIconPath(item.providerCode)" alt="图标" style="width: 25px; height: 25px; border-radius: 50%;">
                {{ item.modelName }}
              </div>
            </template>
          </el-option>
        </el-select> 
      </el-form-item>

      <!-- OCR模型选择项 -->
      <el-form-item label="识别模型" prop="modelId" v-if="formData.recognitionType === 'ocr' && ocrModelOptions.length > 0">
        <el-select v-model="formData.modelId" clearable placeholder="请选择OCR大模型" size="large" style="width:100%">
          <el-option v-for="item in ocrModelOptions" :key="item.id" :label="item.modelName" :value="item.id">
            <template #default>
              <div>
                <img :src="getLlmIconPath(item.providerCode)" alt="图标" style="width: 25px; height: 25px; border-radius: 50%;">
                {{ item.modelName }}
              </div>
            </template>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="字符长度" prop="maxLength">
          <el-slider size="large" show-input v-model="formData.maxLength" :step="100" :max="30000" />
      </el-form-item>

      <!-- 提交按钮 -->
      <el-form-item>
        <div style="width: 100%;text-align: right;margin-top:10px">
          <el-button type="primary" @click="handleSubmit" size="large" text bg>确认</el-button>
        </div>
      </el-form-item>

    </el-form>
  </div>
</template>

<script setup>

import { nextTick, onUnmounted , ref } from 'vue';
import { ElMessage } from 'element-plus'
import LLMSelector from '@/views/smart/assistant/workflow/components/LLMSelector'
import { getLlmIconPath } from '@/utils/llmIcons';

const emit = defineEmits(['handleAttachmentUploadStatusPanelClose'])

// 初始化表单数据
const formData = ref({
  enable: true ,
  recognitionType: 'ocr',
  modelId: '',
  fileUploadLimit: 10,
  imageUploadLimit: 0 // 默认图片上传数为 0
});

// 定义可用的语音模型数组
const multiModelOptions = ref([]);
const ocrModelOptions = ref([]);

// 自定义验证函数
const validateModelId = (rule, value, callback) => {
  if (formData.value.imageUploadLimit > 0 &&!value) {
    callback(new Error('请选择识别模型'));
  } else {
    callback();
  }
};

// 表单验证规则
const rules = ref({
  enable: [
    { required: true, message: '请确认是否选择模型', trigger: 'blur' }
  ],
  fileUploadLimit: [
    { required: true, message: '请输入文件上传数量限制', trigger: 'change' }
  ],
  imageUploadLimit: [
    { required: true, message: '请输入图片上传数量限制', trigger: 'change' }
  ],
  modelId: [
    { validator: validateModelId, trigger: 'change' }
  ]
});

// 获取表单实例
const formRef = ref(null);

const setMultiModalOptions = (models) => {
  multiModelOptions.value = models;
}

// ocr识别模型
const setOcrModalOptions = (models) => {
  ocrModelOptions.value = models;
}

const setConfigParams = (params) => {
  formData.value = params ;
}

/** 获取到表单数据 */
const getFormData = () => {
  return formData.value
}

const handleSubmit = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            console.log('handleAttachmentUploadStatusPanelClose params = ' + JSON.stringify(formData.value));
            emit("handleAttachmentUploadStatusPanelClose" , formData.value);
            ElMessage.success('提交成功');
        } else {
            ElMessage.error('表单验证失败，请检查输入');
        }
    });
};

defineExpose({
  setMultiModalOptions , 
  setOcrModalOptions , 
  getFormData,
  setConfigParams
})

onUnmounted(() => {
})

nextTick(() => {
  // 初始化表单数据
  formData.value.enable = true ;
});

</script>

<style scoped lang="scss"></style>        