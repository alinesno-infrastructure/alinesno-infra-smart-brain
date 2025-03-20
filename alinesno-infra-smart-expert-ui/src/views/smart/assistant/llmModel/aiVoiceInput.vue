<template>
  <el-tooltip class="box-item" effect="dark" content="语音输入" placement="top">
    <span style="margin-right:10px;" v-if="isRecording">
      <img :src="speakingIcon" style="width:35px" />
      <el-button type="primary" text bg size="large" @click="stopRecording">
        <i class="fa-solid fa-headset icon-btn"></i>
      </el-button>
    </span>
    <el-button v-if="!isRecording" type="primary" size="large" text bg @click="listenPlayVoiceOption">
      <i class="fa-solid fa-microphone-lines icon-btn"></i>
    </el-button>
  </el-tooltip>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { recognizeForm } from '@/api/smart/assistant/roleChat';
import speakingIcon from '@/assets/icons/speaking.gif';
import Recorder from 'js-audio-recorder';

const isRecording = ref(false);
const mediaRecorder = ref(null);
const audioChunks = ref([]);
const audioUrl = ref('');

const streamLoading = ref(null);

const recorder = new Recorder({
  sampleBits: 16, // 采样位数，支持 8 或 16，默认是16
  sampleRate: 16000, // 采样率，支持 11025、16000、22050、24000、44100、48000，根据浏览器默认值，我的chrome是48000
  numChannels: 1 // 声道，支持 1 或 2， 默认是1
});

// 监听录音状态
const stat = ref({ duration: 0, fileSize: 0, vol: 0 });
const st = ref({ start: 0, isGo: false });

const emits = defineEmits(['sendAudioToBackend']);

recorder.onprogress = (params) => {
  stat.value = { duration: params.duration, fileSize: params.fileSize, vol: params.vol };
};

// 开始录音函数
const listenPlayVoiceOption = async () => {
  isRecording.value = !isRecording.value;
  recorder.start().then(() => {
    st.value.start = 1;
    st.value.isGo = true;
  }).catch((e) => {
    console.log('录音错误', e);
    ElMessage.error('录音失败');
    // emit('cancel');
  });
};

// 停止录音函数
const stopRecording = () => {
  isRecording.value = false;
  st.value.start = 0;
  recorder.stop();
  recorder.stopPlay();
  send();
};

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

  var newbolb = new Blob([blob], { type: 'audio/wav' });
  var fileOfBlob = new File([newbolb], new Date().getTime() + '.wav');

  const micData = {
    act: "gpt.whisper",
    actData: { file: fileOfBlob, prompt: "whisper", duration: stat.value?.duration },
  };

  var formData = new FormData();
  formData.append('act', "gpt.whisper");
  formData.append('prompt', "whisper");
  formData.append('duration', stat.value?.duration);
  formData.append('file', fileOfBlob);

  await sendAudioFileToBackend(formData);

  stat.value = { duration: 0, fileSize: 0, vol: 0 };
};

// 发送音频文件到后端
const sendAudioFileToBackend = async (audioFormData) => {
  const response = await recognizeForm(audioFormData);
  console.log('response = ' + response);
  emits("sendAudioToBackend" , response);
};

// 按下空格键开始录音
const onKeyDown = (event) => {
  if (event.key === ' ' && !isRecording.value) {
    listenPlayVoiceOption();
  }
};

// 抬起空格键停止录音
const onKeyUp = (event) => {
  if (event.key === ' ' && isRecording.value) {
    stopRecording();
  }
};

onMounted(() => {
  window.addEventListener('keydown', onKeyDown);
  window.addEventListener('keyup', onKeyUp);
});

onUnmounted(() => {
  window.removeEventListener('keydown', onKeyDown);
  window.removeEventListener('keyup', onKeyUp);
});
</script>

<style scoped>
.icon-btn {
  margin-right: 5px;
}
</style>    