<template>
  <el-tooltip class="box-item" effect="dark" content="语音输入" placement="top">
    <span style="margin-right:10px;display: flex;" v-if="isRecording">
      <img :src="speakingIcon" style="width:35px" />
      <el-button type="primary" text bg size="large" @click="stopRecording">
        <i class="fa-solid fa-headset icon-btn"></i>
      </el-button>
    </span>
    <el-button v-if="!isRecording" type="primary" size="large" text bg @click="listenPlayVoiceOption" :loading="chatLoading">
      <i class="fa-solid fa-microphone-lines icon-btn"></i>
    </el-button>
  </el-tooltip>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';
import { recognizeForm } from '@/api/base/im/roleChat';
import speakingIcon from '@/assets/icons/speaking.gif';
import Recorder from 'js-audio-recorder';

const chatLoading = ref(false)

// 接收父类传递的props
const props = defineProps({
  roleId: {
    type: Number,
    default: 0,
  },
  role: {
    type: Object,
  },
});

const isRecording = ref(false);
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

  formData.append('roleId', props.role.id);

  formData.append('act', "gpt.whisper");
  formData.append('prompt', "whisper");
  formData.append('duration', stat.value?.duration);
  formData.append('file', fileOfBlob);

  await sendAudioFileToBackend(formData);

  stat.value = { duration: 0, fileSize: 0, vol: 0 };
};

// 发送音频文件到后端
const sendAudioFileToBackend = async (audioFormData) => {
  chatLoading.value = true;
  const response = await recognizeForm(audioFormData);
  chatLoading.value = false ;
  console.log('response = ' + response.data);
  if(response.data){
    emits("sendAudioToBackend" , response.data);
  }else{
    ElMessage.error('录音识别失败，请重试或者确认麦克风正常.');
  }
};

// 按下 Ctrl + R 开始录音
const onKeyDown = (event) => {
    if (event.ctrlKey && event.key === 'b' && !isRecording.value) {
        listenPlayVoiceOption();
    }
};

// 抬起 Ctrl + R 停止录音
const onKeyUp = (event) => {
    if (event.ctrlKey && event.key === 'b' && isRecording.value) {
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