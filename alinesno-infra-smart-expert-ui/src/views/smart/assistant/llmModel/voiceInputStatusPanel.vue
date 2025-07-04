<template>
  <div class="row" style="padding: 20px">
    <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px" :label-position="'left'">

      <el-form-item label="是否启用" prop="enable">
        <el-radio-group size="large" v-model="formData.enable">
          <el-radio :value="true">启用</el-radio>
          <el-radio :value="false">不启用</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 语音模型选择项 -->
      <el-form-item label="语音识别模型" prop="voiceModel">
        <el-select v-model="formData.voiceModel" placeholder="请选择大模型" size="large" style="width:100%">
          <el-option v-for="item in voiceModelOptions" :key="item.id" :label="item.modelName" :value="item.id">
            <template #default>
              <div>
                <img :src="getLlmIconPath(item.providerCode)" alt="图标" style="width: 25px; height: 25px; border-radius: 50%;">
                {{ item.modelName }}
              </div>
            </template>
          </el-option>
        </el-select>
      </el-form-item>

      <!-- 
      <el-form-item label="识别内容" prop="speechRate">
         <div style="width:100%">
          <div style="width: 100%;font-size:14px;color:#a5a5a5;">
            这里显示识别内容
          </div>
         </div>
      </el-form-item>  
      -->

      <!-- 提交按钮 -->
      <el-form-item>
        <div style="width: 100%;text-align: right;margin-top:10px">

          <img v-if="isSpeaking" :src="speakingIcon" style="width:35px" />

          <!-- 
          <el-button v-if="isSpeaking" type="danger" text bg size="large" @click="stopRecording()"> 
            <i class="fa-regular fa-circle-stop"></i> &nbsp;&nbsp; 停止 
          </el-button>
          <el-button v-if="!isSpeaking" type="primary" text bg size="large" @click="listenPlayVoiceOption()"> 
            <i class="fa-solid fa-microphone-lines"></i> 试讲 
          </el-button> 
          -->

          <el-button type="primary" @click="handleSubmit" size="large" text bg>确认</el-button>
        </div>
      </el-form-item>

    </el-form>
  </div>
</template>

<script setup>

import Recorder from 'js-audio-recorder';

import { nextTick, onMounted, onUnmounted , ref } from 'vue';
import { ElMessage , ElLoading } from 'element-plus'
import { getLlmIconPath } from '@/utils/llmIcons';

import { getInfo, chatRole, recognize , recognizeForm } from '@/api/smart/assistant/roleChat'

import speakingIcon from '@/assets/icons/speaking.gif';

const emit = defineEmits(['handleVoiceInputStatusPanelClose'])

// 初始化表单数据
const formData = ref({
  enable: true ,
  voiceModel: '',
  speechRate: 1 // 默认语速为 1
});

// 定义可用的语音模型数组
const voiceModelOptions = ref([]);
const audioChunks = ref([]);
const mediaRecorder = ref(null);
const isSpeaking = ref(false)
const audioUrl = ref('');

const recorder = new Recorder({
  sampleBits: 16, // 采样位数，支持 8 或 16，默认是16
  sampleRate: 16000, // 采样率，支持 11025、16000、22050、24000、44100、48000，根据浏览器默认值，我的chrome是48000
  numChannels: 1 // 声道，支持 1 或 2， 默认是1
})

const stat = ref({ duration: 0, fileSize: 0, vol: 0 });
const st = ref({ start: 0, isGo: false });

const streamLoading = ref(null)

// 表单验证规则
const rules = ref({
  enable: [
    { required: true, message: '请确认是否选择模型', trigger: 'bulr' }
  ],
  voiceModel: [
    { required: true, message: '请选择语音模型', trigger: 'change' }
  ],
});

recorder.onprogress =  (params) => {
    stat.value = {duration: params.duration , fileSize: params.fileSize, vol: params.vol }
}

// 获取表单实例
const formRef = ref(null);

const setVoiceModelOptions = (models) => {
  voiceModelOptions.value = models;
}

// 开始录音函数
const listenPlayVoiceOption = async () => {

  isSpeaking.value = !isSpeaking.value

    recorder.start().then(() => {
        st.value.start = 1;
        st.value.isGo = true;
    }).catch(e => {
        mlog('录音错误', e);
        ElMessage.error('录音失败')
        // emit('cancel');
    });

};

// 停止录音函数
const stopRecording = () => {

  isSpeaking.value = false;

  st.value.start=0;
  recorder.stop();
  recorder.stopPlay();

  send();
}

const send = async () => {

    console.log('recorder.getWAVBlob() = ' + recorder.getWAVBlob());
    console.log('stat.value = ' + stat.value);

    const blob = recorder.getWAVBlob();
    if (!blob) {
      console.error('获取的音频数据为空');
      return;
    }

    const du = "whisper.wav"; 
    const file = new File([blob], du, { type: "audio/wav" });

    var newbolb = new Blob([blob], { type: 'audio/wav' })
    var fileOfBlob = new File([newbolb], new Date().getTime() + '.wav')

    const micData = {
      act: "gpt.whisper",
      actData: { file:fileOfBlob , prompt: "whisper", duration:  stat.value?.duration },
    }

    var formData = new FormData()
    formData.append('act', "gpt.whisper");
    formData.append('prompt', "whisper");
    formData.append('duration', stat.value?.duration);
    formData.append('file', fileOfBlob);

    await sendAudioFileToBackend(formData);

    stat.value = { duration: 0, fileSize: 0, vol: 0 };
}

// 发送音频文件到后端
const sendAudioFileToBackend = async (audioFormData) => {
  const response = await recognizeForm(audioFormData);
  console.log('response = ' + response)
}

// 发送音频数据到后端
const sendAudioToBackend = async (audioBlob) => {

  try {

    streamLoading.value = ElLoading.service({
      lock: true,
      text: '语音识别中...',
      background: 'rgba(0, 0, 0, 0.2)',
    })

    const response = await recognize(audioBlob);
    console.log('response = ' + response)

  } catch (error) {
    console.error('语音识别请求失败:', error);
    streamLoading.value.close();
  }
};

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
            console.log('handleVoiceInputStatusPanelClose params = ' + JSON.stringify(formData.value));
            emit("handleVoiceInputStatusPanelClose" , formData.value);
            ElMessage.success('提交成功');
        } else {
            ElMessage.error('表单验证失败，请检查输入');
        }
    });
};

/** 是否在播放 */
// const submitForm = () => {
//   isSpeaking.value = !isSpeaking.value
// }

// // 重置表单的方法
// const resetForm = () => {
//   formRef.resetFields();
// };

defineExpose({
  setVoiceModelOptions , 
  getFormData,
  setConfigParams
})

onUnmounted(() => {
  recorder.stop();
  recorder.destroy();
})

nextTick(() => {
  // 初始化表单数据
  formData.value.enable = true ;
});

</script>

<style scoped lang="scss"></style>