<!-- 意见审批  -->
<template>
  <div class="acp-dashboard aip-chat-dashboard-panel" v-loading="loading" element-loading-text="Loading..."
    :element-loading-spinner="svg" element-loading-svg-view-box="-10, -10, 50, 50" style="padding:0px !important;">
    <div class="smart-container inner-smart-container">
      <el-row>
        <el-col :span="24">
          <div class="robot-chat-windows">

            <!-- <div class="robot-chat-header">
              <div class="chat-header-title">
                {{ roleInfo.roleName }}
              </div>
              <div class="chat-header-desc">
                ({{ truncateString(roleInfo.responsibilities, 60) }})
              </div>
              <div class="chat-header-desc" style="float: right;margin-top: -10px;">
                <el-button type="primary" text bg size="large" @click="taskFlowDialogVisible = true">
                  <i class="fa-solid fa-truck-fast icon-btn"></i>
                </el-button>
              </div>
            </div> -->

            <div class="robot-chat-body inner-robot-chat-body" style="height: calc(100vh - 240px)">
              <!-- 聊天窗口_start -->
              <el-scrollbar class="scroll-panel" ref="scrollbarRef" loading always wrap-style="padding:10px">

                <div ref="innerRef">

                  <div class="robot-chat-ai-say-box" v-for="(item, index) in messageList" @mouseover="showTools(item)"
                    @mouseleave="hideTools(item)" :key="index">

                    <div class="chat-ai-header" :class="item.roleType == 'person' ? 'say-right-window' : ''">
                      <div class="header-images">
                        <img :src="imagePath(item.icon)" />
                      </div>
                    </div>

                    <div class="chat-ai-say-body" :class="item.roleType == 'person' ? 'say-right-window' : ''"
                      style="max-width:calc(100% - 135px)">
                      <div class="say-message-info" v-if="item.roleType == 'person'">
                        <span style="margin-left:10px" :class="item.showTools ? 'show-tools' : 'hide-tools'"> {{
                          item.dateTime }}</span>
                        {{ item.name }}
                      </div>
                      <div class="say-message-info" v-else>
                        {{ item.name }}

                        <el-button v-if="item.loading" size="default" type="primary" loading text>任务处理中</el-button>
                        <el-button v-if="item.reasoningText && !item.chatText" size="default" type="primary" loading
                          text>推理中</el-button>

                        <span style="margin-left:10px" :class="item.showTools ? 'show-tools' : 'hide-tools'"> {{
                          item.dateTime }} </span>
                      </div>

                      <div class="say-message-body markdown-body chat-reasoning" v-if="item.reasoningText"
                        v-html="readerReasonningHtml(item.reasoningText)"></div>
                      <div class="say-message-body markdown-body" v-if="item.chatText"
                        v-html="readerHtml(item.chatText)"></div>

                      <div class="chat-ai-say-tools" style="margin-top: 3px;;text-align: right;float:right"
                        :class="item.showTools ? 'show-tools' : 'hide-tools'">
                        <el-button type="danger" link icon="Promotion" size="small"
                          @click="handleBusinessIdToMessageBox(item)">选择</el-button>
                        <el-button type="primary" link icon="EditPen" size="small"
                          @click="handleEditGenContent(item)">复制</el-button>
                        <el-button type="primary" v-if="item.businessId && item.roleId" link icon="Position"
                          size="small" @click="handleExecutorMessage(item)">执行</el-button>
                      </div>
                    </div>
                  </div>

                </div>

              </el-scrollbar>
              <!-- 聊天窗口_end -->
            </div>

            <div class="robot-chat-footer chat-container" style="float:left;width:100%">

              <el-row :gutter="20">
                <el-col :span="16">
                  <div class="message-input">

                    <el-input 
                      class="input-chat-box" 
                      @keydown.ctrl.enter.prevent="keyDown" 
                      v-model="message"
                      :options="mentionOptions" 
                      :prefix="['@']" 
                      :placeholder="isRecording?'请说话，我在听':'请输入你的问题.'" 
                      @select="handleSelect">
                      <template #label="{ item }">
                        {{ item.label }}
                      </template>
                    </el-input>

                  </div>
                </el-col>

                <el-col :span="8" style="text-align: right;">

                  <el-tooltip class="box-item" effect="dark" content="语音输入" placement="top">

                    <span style="margin-right:10px;" v-if="isRecording" >
                      <img :src="speakingIcon" style="width:35px" />
                      <el-button type="primary" text bg size="large" @click="stopRecording()">
                        <i class="fa-solid fa-headset icon-btn"></i>
                      </el-button>
                    </span>

                    <el-button v-if="!isRecording" type="primary" text bg size="large" @click="startRecording()">
                      <i class="fa-solid fa-microphone-lines icon-btn"></i>
                    </el-button>
                  </el-tooltip>

                  <el-tooltip class="box-item" effect="dark" content="确认发送指令给Agent，快捷键：Enter+Ctrl" placement="top">
                    <el-button type="danger" text bg size="large" @click="sendMessage('send')">
                      <i class="fa-solid fa-paper-plane icon-btn"></i>
                    </el-button>
                  </el-tooltip>

                  <el-tooltip class="box-item" effect="dark" content="执行任务" placement="top">
                    <el-button type="warning" text bg size="large" @click="sendMessage('function')">
                      <i class="fa-solid fa-feather icon-btn"></i>
                    </el-button>
                  </el-tooltip>

                </el-col>
              </el-row>

            </div>
          </div>
        </el-col>

        <!-- <el-col :span="5">
          <AgentSingleRightPanel ref="agentSingleRightPanelRef" />
        </el-col> -->

      </el-row>
    </div>

  </div>
</template>

<script setup>

import { ElLoading } from 'element-plus'
import MarkdownIt from 'markdown-it';
import mdKatex from '@traptitech/markdown-it-katex';
import hljs from 'highlight.js';

// import AgentSingleRightPanel from './rightPanel.vue'

import { getInfo, chatRole , recognize } from '@/api/smart/assistant/roleChat'
import { openSseConnect, handleCloseSse } from "@/api/smart/assistant/chatsse";

import { getParam } from '@/utils/ruoyi'
import { nextTick, onMounted } from "vue";

import speakingIcon from '@/assets/icons/speaking.gif';

// import { v4 as uuidv4 } from 'uuid'

import SnowflakeId from "snowflake-id";
const snowflake = new SnowflakeId();

const isSpeaking = ref(false)
// 定义响应式变量
const isRecording = ref(false);
const mediaRecorder = ref(null);
const audioChunks = ref([]);
const audioUrl = ref('');
const transcription = ref('');

const { proxy } = getCurrentInstance();

// const agentSingleRightPanelRef = ref(null)

const loading = ref(true)
const roleId = ref(null);
const channelId = ref(null);
const roleInfo = ref({})
const message = ref('');
const businessId = ref(null);

const innerRef = ref(null); // 滚动条的处理_starter
const scrollbarRef = ref(null);
const messageList = ref([]);

const isSpeechSynthesisSupported = 'speechSynthesis' in window;

const streamLoading = ref(null)

const mdi = new MarkdownIt({
  html: true,
  linkify: true,
  highlight(code, language) {
    const validLang = !!(language && hljs.getLanguage(language));
    if (validLang) {
      const lang = language || '';
      return highlightBlock(hljs.highlight(code, { language: lang }).value, lang);
    }
    return highlightBlock(hljs.highlightAuto(code).value, '');
  },
});

mdi.use(mdKatex, { blockClass: 'katexmath-block rounded-md p-[10px]', errorColor: ' #cc0000' });

function initChatBoxScroll() {

  nextTick(() => {
    const element = innerRef.value;  // 获取滚动元素
    const scrollHeight = element.scrollHeight;

    scrollbarRef.value.setScrollTop(scrollHeight);
  })

}

function highlightBlock(str, lang) {
  return `<pre class="code-block-wrapper"><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
}

function keyDown(e) {
  if (!message.value) {
    return;
  }
  sendMessage('send');
  message.value = '';
}

/** 是否语音输入 */
function AudioListener(){
  isSpeaking.value = !isSpeaking.value
}

// 开始录音函数
const startRecording = async () => {
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
    isRecording.value = true;
  } catch (error) {
    console.error('录音失败:', error);
    alert('录音失败，请检查麦克风权限或设备是否正常');
  }
};

// 停止录音函数
const stopRecording = () => {
  if (mediaRecorder.value && mediaRecorder.value.state === 'recording') {
    mediaRecorder.value.stop();
    isRecording.value = false;
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
      background: 'rgba(0, 0, 0, 0.7)',
    })

    const response = await recognize(formData) ; 
    message.value = response.data ;

    streamLoading.value.close();
    sendMessage('send');

  } catch (error) {
    console.error('语音识别请求失败:', error);
  }
};

/** 读取html文本 */
function readerHtml(chatText) {
  return mdi.render(chatText);
}

function readerReasonningHtml(chatText) {
  if (chatText) {
    return mdi.render(chatText);
  }
}

// 推送消息到当前面板
const pushResponseMessageList = (newMessage) => {
  console.log(`--->>> newMessage = ${JSON.stringify(newMessage)}`);

  if (newMessage.llmStream === true) { // 是否为流式输出

    // 查找是否有相同businessId的消息
    const existingIndex = messageList.value.findIndex(item => item.businessId === newMessage.businessId);

    if (existingIndex !== -1) {

      // 如果找到，更新该消息
      messageList.value[existingIndex].reasoningText += newMessage.reasoningText;
      messageList.value[existingIndex].chatText += newMessage.chatText;

      // speakText(newMessage.chatText);

    } else {
      // 否则，添加新消息
      messageList.value.push(newMessage);
    }
  } else {
    messageList.value.push(newMessage);
  }

  // 调用初始化滚动条的函数
  initChatBoxScroll();
};

const speakText = (text) => {
  if (!isSpeechSynthesisSupported) {
    alert('当前浏览器不支持语音合成功能');
    return;
  }

  const utterance = new SpeechSynthesisUtterance();
  utterance.text = text;

  const voices = speechSynthesis.getVoices();
  utterance.voice = voices[0];

  utterance.rate = 1;
  utterance.volume = 1;
  utterance.pitch = 1;

  speechSynthesis.speak(utterance);
};

function handleExecutorMessage(item) {

  // emit('executorMessage' , item) ; 
  // let channelId = getParam("channel");
  // let users = [item.roleId];
  // let bId = [item.businessId];
  // let type = 'function';
  // let message = " #"+item.businessId+" @图片设计专家 " ; 

  // streamLoading.value = ElLoading.service({
  //   lock: true,
  //   text: '任务执行中，请勿操作其它界面 ...',
  //   background: 'rgba(0, 0, 0, 0.7)',
  // })

  // sendUserMessage(message, users, bId , channelId, type).then(response => {
  //   console.log("发送消息", response.data);
  //   response.data.forEach(item => {
  //     chatListRef.value.pushResponseMessageList(item);
  //   })
  // }).catch(error => {
  //   streamLoading.value.close();
  // })

  const businessIdMessage = ' #' + item.businessId + ' ';
  businessId.value = item.businessId;
  message.value += businessIdMessage;

  sendMessage('function');

}

/** 连接sse */
function handleSseConnect(channelId) {
  nextTick(() => {
    if (channelId) {

      let sseSource = openSseConnect(channelId);
      // 接收到数据
      sseSource.onmessage = function (event) {

        if (!event.data.includes('[DONE]')) {
          let resData = event.data;
          if (resData != 'ping') {  // 非心跳消息
            const data = JSON.parse(resData);
            pushResponseMessageList(data);
          }
        } else {
          console.log('消息接收结束.')
          if (streamLoading.value) {
            streamLoading.value.close();
          }
        }

      }
    }
  })
}

function handleBusinessIdToMessageBox(item) {
  const businessIdMessage = ' #' + item.businessId + ' ';
  businessId.value = item.businessId;
  message.value += businessIdMessage;
}

/** 获取角色信息 */
function handleGetInfo(roleId) {
  getInfo(roleId).then(res => {
    let role = res.role;
    let msg = res.message;

    roleInfo.value = role;
    pushResponseMessageList(msg);

    loading.value = false;

    // nextTick(() => {
    //   agentSingleRightPanelRef.value.setRoleInfo(role);
    // })

  })
}

/** 发送消息 */
const sendMessage = (type) => {

  if (!message.value) {
    proxy.$modal.msgError("请输入消息内容.");
    return;
  }

  streamLoading.value = ElLoading.service({
    lock: true,
    text: '任务执行中，请勿操作其它界面 ...',
    background: 'rgba(0, 0, 0, 0.7)',
  })

  let formData = {
    channelId: channelId.value,
    message: message.value,
    businessIds: [businessId.value],
    type: type
  }

  chatRole(formData, roleId.value).then(res => {
    proxy.$modal.msgSuccess("发送成功");
    pushResponseMessageList(res.data);
  }).catch(error => {
    streamLoading.value.close();
  })

  message.value = '';
};

/** 显示工具条 */
function showTools(item) {
  messageList.value.forEach((i) => {
    i.showTools = i === item; // 只有当前项的 showTools 被设置为 true
  });
}

/** 隐藏工具条 */
function hideTools(item) {
  item.showTools = false; // 鼠标移出时隐藏 tools
}

function setRoleInfo(roleInfo) {
  roleInfo.value = roleInfo
}

onMounted(() => {
  initChatBoxScroll();

  roleId.value = getParam('roleId')
  channelId.value = snowflake.generate()
  // channelId.value = getParam('channelId')

  handleSseConnect(channelId.value)
  handleGetInfo(roleId.value);
})


// 销毁信息
// onBeforeUnmount(() => {
//   handleCloseSse(channelId.value).then(res => {
//     console.log('关闭sse连接成功:' + channelId)
//   })
// });

defineExpose({
  setRoleInfo
})

</script>

<style lang="scss" scoped>
.inner-smart-container {
  max-width: 100% ! important;

  .robot-chat-windows {
    border: 0px !important;
  }

  .inner-robot-chat-body {
    height: calc(100vh - 100px);
  }
}

.scroll-panel {
  padding-bottom: 10px;
  float: left;
  width: 100%;
  height: calc(100% - 55px);
  overflow: hidden;
}

.show-tools {
  visibility: visible;
}

.hide-tools {
  visibility: hidden;
}

.robot-chat-ai-say-box {
  float: left;
  width: 100%;

  .say-right-window {
    float: right !important;

    .say-message-info {
      text-align: right !important;
    }
  }

  .chat-ai-header {
    float: left;
    width: 50px;
    margin: 10px;

    .header-images {
      padding: 5px;

      img {
        width: 100%;
        border-radius: 50%;
      }

    }

  }

  .chat-ai-say-body {
    float: left;
    margin-top: 15px;
    font-size: 14px;

    .say-message-info {
      font-size: 13px;
      margin-bottom: 5px;
      color: #999;
    }

    .say-message-body {
      padding: 10px;
      line-height: 1.4rem;
      border-radius: 3px;
      background: #fafafa;

    }

  }

  .message-list {
    margin-top: 20px;
  }

  .message {
    margin-bottom: 8px;
  }

}
</style>