<template>
  <div class="row" style="padding: 20px">
    <el-form :model="formData" :rules="rules" ref="formRef" label-width="90px" :label-position="'left'">

      <!-- 语音模型选择项 -->
      <el-form-item label="语音模型" prop="voiceModel">
        <el-select v-model="formData.voiceModel" placeholder="请选择大模型" size="large" style="width:100%" @change="handleChangeVoiceModelParams">
          <el-option v-for="item in voiceModelOptions" :key="item.id" :label="item.modelName" :value="item.id">
            <template #default>
              <div>
                <img :src="getLlmIconPath(item.providerCode)" alt="图标"
                  style="width: 25px; height: 25px; border-radius: 50%;">
                {{ item.modelName }}
              </div>
            </template>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="播放音色" prop="voiceSpeechModel">
        <el-select v-model="formData.voiceSpeechModel" placeholder="请选择语音人员" size="large" style="width:100%">
          <el-option v-for="item in voiceModelSpeechOptions" :key="item.voice" :label="item.voice" :value="item.voice">
            <template #default>
              <div>
                {{ item.voice }}({{ item.description }})
              </div>
            </template>
          </el-option>
        </el-select>

      </el-form-item>

      <el-form-item label="语速配置" prop="speechRate">
         <div style="width:100%">
          <el-slider size="large" :min="1" :max="10" show-input  v-model="formData.speechRate" :step="1" />
         </div>
      </el-form-item>

      <!-- 提交按钮 -->
      <el-form-item>
        <div style="width: 100%;text-align: right;margin-top:10px">

          <img v-if="isSpeaking" :src="speakingIcon" style="width:35px" />

          <el-button v-if="isSpeaking" type="danger" text bg size="large" @click="listenPlayVoiceOption()">
            <i class="fa-regular fa-circle-stop"></i> &nbsp;&nbsp; 停止
          </el-button>

          <el-button v-if="!isSpeaking" type="primary" text bg size="large" :loading="chatLoading" @click="handleTestSpeechModel()">
            <i class="fa-solid fa-headphones-simple"></i> &nbsp;&nbsp; 试听
          </el-button>

          <el-button type="primary" @click="handleSubmit" size="large" text bg>确认</el-button>

        </div>
      </el-form-item>

    </el-form>
  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { ElMessage } from 'element-plus';

import { getLlmIconPath } from '@/utils/llmIcons';
import speakingIcon from '@/assets/icons/speaking.gif';
import { getVoiceModelSpeech , getSpeechByModelId} from '@/api/smart/assistant/llmModel'

const emit = defineEmits(['handleVoiceConfigParams'])

// 初始化表单数据
const formData = ref({
  voiceModel: '',
  voiceSpeechModel: '' ,
  speechRate: 1 // 默认语速为 1
});

// 定义可用的语音模型数组
const voiceModelOptions = ref([]);
const voiceModelSpeechOptions = ref([]);

const isSpeaking = ref(false)
const chatLoading = ref(false)

// 表单验证规则
const rules = ref({
  // isEnable: [
  //   { required: true, message: '请确认是否选择模型', trigger: 'bulr' }
  // ],
  voiceModel: [
    { required: true, message: '请选择语音模型', trigger: 'change' }
  ],
  speechRate: [
    { required: true, message: '请输入语速', trigger: 'blur' },
    { type: 'number', message: '语速必须为数字', trigger: 'blur' }
  ]
});

// 获取表单实例
const formRef = ref(null);

// 提交表单的方法
const handleSubmit = async () => {

    formRef.value.validate((valid) => {
        if (valid) {
          console.log('提交的表单数据：', formData.value);
          emit('handleVoiceConfigParams', formData.value);
          ElMessage.success('提交成功');
        } else {
          ElMessage.error('表单验证失败，请检查输入');
        }
    });
};



const setVoiceModelOptions = (models) => {
  voiceModelOptions.value = models;
}

const setVoiceModelParams = (params) => {
  formData.value = params;
}

const handleChangeVoiceModelParams = (value) => {
  console.log('handleChangeVoiceModelParams value = ' + value);
  formData.value.voiceSpeechModel = null;
  getVoiceModelSpeech(value).then(res => {
    console.log('res = ' + res);
    voiceModelSpeechOptions.value = res.data.voiceInfos;
  })
}

/** 是否在播放 */
const listenPlayVoiceOption = () => {
  isSpeaking.value = !isSpeaking.value
}

// 验证语音播放
const handleTestSpeechModel = async () => {

// 表单验证通过后，发出语音请求

chatLoading.value = true ;

getSpeechByModelId(formData.value.voiceModel , formData.value.voiceSpeechModel).then(res => {
  const audioBlob = new Blob([res], { type: 'audio/wav' }) // 这按照自己的数据类型来写type的内容
  const audioUrl = URL.createObjectURL(audioBlob) // 生成url
  const audio = new Audio(audioUrl);

  audio.addEventListener('ended', () => {
    console.log('音频播放完成');
    isSpeaking.value = false;
  });

  audio.play();
  chatLoading.value = false ;
  isSpeaking.value = true ;

}).catch(error => {
  chatLoading.value = false;
  console.error('操作失败:', error.message);
});
};

nextTick(() => {
  const modelId = formData.value.voiceModel
  console.log('onMounted : ' + modelId) ;
  if(modelId){
    getVoiceModelSpeech(modelId).then(res => {
      console.log('res = ' + res);
      voiceModelSpeechOptions.value = res.data.voiceInfos;
    })
  }
})

defineExpose({
  setVoiceModelOptions,
  setVoiceModelParams
})

</script>

<style scoped lang="scss"></style>
