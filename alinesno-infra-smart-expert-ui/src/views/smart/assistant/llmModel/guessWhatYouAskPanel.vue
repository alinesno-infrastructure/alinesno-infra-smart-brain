
<template>
  <div class="row" style="padding: 20px">
    <el-form :model="formData" :rules="rules" ref="formRef" label-width="90px" :label-position="'left'">

      <el-form-item label="是否启用" prop="enable">
        <el-radio-group size="large" v-model="formData.enable">
          <el-radio :value="true">启用</el-radio>
          <el-radio :value="false">不启用</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 语音模型选择项 -->
      <el-form-item label="大模型" prop="voiceModel">
        <LLMSelector :modelType="'large_language_model'" v-model="formData.voiceModel" />
      </el-form-item>

      <el-form-item label="Prompt" prop="prompt">
         <div style="width:100%">
            <el-input 
                v-model="formData.prompt" 
                type="textarea" 
                :rows="5" 
                :maxlength="3000"
                show-word-limit
                resize="none" 
                placeholder="请输入内容">
            </el-input>
         </div>
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

const emit = defineEmits(['handleGuessWhatYouAskStatusPanelClose'])
// import speakingIcon from '@/assets/icons/speaking.gif';

import LLMSelector from '@/views/smart/assistant/workflow/components/LLMSelector'

// 初始化表单数据
const formData = ref({
  enable: true ,
  voiceModel: '',
  prompt: '- 问题应该与你最后一轮的回复紧密相关，可以引发进一步的讨论。\r\n- 问题不要与上文已经提问或者回答过的内容重复。\r\n- 每句话只包含一个问题，但也可以不是问句而是一句指令。\r\n- 推荐你有能力回答的问题。',
  voiceSpeed: 1,
});

// 定义可用的语音模型数组
const llmModelOptions = ref([]);

// const isSpeaking = ref(false)

// 表单验证规则
const rules = ref({
  enable: [
    { required: true, message: '请确认是否选择模型', trigger: 'bulr' }
  ],
  voiceModel: [
    { required: true, message: '请选择语音模型', trigger: 'change' }
  ],
  prompt: [
    { required: true, message: '请输入自定义Prompt', trigger: 'blur' },
  ]
});

// 获取表单实例
const formRef = ref(null);

// 提交表单的方法
// const submitForm = async () => {
//   const isValid = await formRef.validate();
//   if (isValid) {
//     console.log('提交的表单数据：', formData.value);
//     // 这里可以添加实际的提交逻辑，比如发送请求到后端等
//   }
// };

const setLlmModelOptions = (models) => {
  llmModelOptions.value = models;
}

/** 是否在播放 */
// const listenPlayVoiceOption = () => {
//   isSpeaking.value = !isSpeaking.value
// }

// 重置表单的方法
// const resetForm = () => {
//   formRef.resetFields();
// };

const getFormData = () => {
  return formData.value
}

const setConfigParams = (params) => {
  console.log('params 999999>>> = ' + JSON.stringify(params));
  formData.value = params ;
}

const handleSubmit = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            console.log('handleGuessWhatYouAskStatusPanel params = ' + JSON.stringify(formData.value));
            emit("handleGuessWhatYouAskStatusPanelClose" , formData.value);
            ElMessage.success('提交成功');
        } else {
            ElMessage.error('表单验证失败，请检查输入');
        }
    });
};

onMounted(() => {
  formData.value.isEnable = true ;
})

defineExpose({
  setLlmModelOptions , 
  getFormData ,
  setConfigParams
})


</script>

<style scoped lang="scss"></style>