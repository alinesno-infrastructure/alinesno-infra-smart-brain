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
                <img :src="'http://data.linesno.com/icons/llm/' + item.providerCode + '.png'" alt="图标" style="width: 25px; height: 25px; border-radius: 50%;">
                {{ item.modelName }}
              </div>
            </template>
          </el-option>
        </el-select>

      </el-form-item>

      <el-form-item label="识别内容" prop="speechRate">
         <div style="width:100%">
          <div style="width: 100%;font-size:14px;color:#a5a5a5;">
            这里显示识别内容
          </div>
         </div>
      </el-form-item> 

      <!-- 提交按钮 -->
      <el-form-item>
        <div style="width: 100%;text-align: right;margin-top:10px">

          <img v-if="isSpeaking" :src="speakingIcon" style="width:35px" />

          <el-button v-if="isSpeaking" type="danger" text bg size="large" @click="stopRecording()"> 
            <i class="fa-regular fa-circle-stop"></i> &nbsp;&nbsp; 停止 
          </el-button>
          <el-button v-if="!isSpeaking" type="primary" text bg size="large" @click="listenPlayVoiceOption()"> 
            <i class="fa-solid fa-microphone-lines"></i> 试讲 
          </el-button>

          <el-button type="primary" @click="handleSubmit" size="large" text bg>确认</el-button>
        </div>
      </el-form-item>

    </el-form>
  </div>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue';
import { ElMessage , ElLoading } from 'element-plus'

import { getInfo, chatRole, recognize } from '@/api/smart/assistant/roleChat'

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

const streamLoading = ref(null)

// 表单验证规则
const rules = ref({
  enable: [
    { required: true, message: '请确认是否选择模型', trigger: 'bulr' }
  ],
  voiceModel: [
    { required: true, message: '请选择语音模型', trigger: 'change' }
  ],
  // speechRate: [
  //   { required: true, message: '请输入语速', trigger: 'blur' },
  //   { type: 'number', message: '语速必须为数字', trigger: 'blur' },
  //   { min: 1, max: 100, message: '语速范围在 1 - 10 之间', trigger: 'blur' }
  // ]
});

// 获取表单实例
const formRef = ref(null);

const setVoiceModelOptions = (models) => {
  voiceModelOptions.value = models;
}

// /** 是否在讲话 */
// const listenPlayVoiceOption = () => {
//   isSpeaking.value = !isSpeaking.value
// }

// 开始录音函数
const listenPlayVoiceOption = async () => {

  isSpeaking.value = !isSpeaking.value

  try {
    if (!('MediaRecorder' in window)) {
      alert('当前浏览器不支持录音功能');
      return;
    }

    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });

    mediaRecorder.value = new MediaRecorder(stream);

    mediaRecorder.value.addEventListener('dataavailable', (event) => {
      if (event.data.size > 0) {
        audioChunks.value.push(event.data);
      }
    });

    mediaRecorder.value.addEventListener('stop', async () => {
      const audioBlob = new Blob(audioChunks.value, { type: 'audio/webm' });
      audioUrl.value = URL.createObjectURL(audioBlob);
      audioChunks.value = [];

      // 调用后端语音识别接口
      await sendAudioToBackend(audioBlob);
    });

    mediaRecorder.value.start();
    isSpeaking.value = true;
  } catch (error) {
    console.error('录音失败:', error);
    alert('录音失败，请检查麦克风权限或设备是否正常');
  }
};

// 停止录音函数
const stopRecording = () => {
  if (mediaRecorder.value && mediaRecorder.value.state === 'recording') {
    mediaRecorder.value.stop();
    isSpeaking.value = false;
  }
};

// 发送音频数据到后端
const sendAudioToBackend = async (audioBlob) => {
  const formData = new FormData();
  formData.append('audio', audioBlob);

  try {

    streamLoading.value = ElLoading.service({
      lock: true,
      text: '语音识别中...',
      background: 'rgba(0, 0, 0, 0.2)',
    })

    const response = await recognize(formData);
    console.log('response = ' + response)
    // message.value = response.data;

    // streamLoading.value.close();
    // sendMessage('send');

  } catch (error) {
    console.error('语音识别请求失败:', error);
    streamLoading.value.close();
  }
};


const setConfigParams = (params) => {
  console.log('params 888>>> = ' + JSON.stringify(params));
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

nextTick(() => {
  // 初始化表单数据
  formData.value.enable = true ;
});

</script>

<style scoped lang="scss"></style>