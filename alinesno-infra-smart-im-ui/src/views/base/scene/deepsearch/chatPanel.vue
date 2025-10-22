<template>
  <div class="acp-dashboard aip-chat-dashboard-panel" v-loading="loading" element-loading-text="Loading..."
    :element-loading-spinner="svg" element-loading-svg-view-box="-10, -10, 50, 50" style="padding:0px !important;">
    <div class="smart-container inner-smart-container">
      <el-row>
        <el-col :span="12">
          <div class="robot-chat-windows">
            <div class="robot-chat-header">
              <div class="chat-header-title">

                  <div class="chat-top-ai-header">
                    <div class="header-top-image">
                      <img :src="imagePathByPath(roleInfo?.roleAvatar)" />
                    </div>
                  </div>

                <!-- 标题内容 -->
                <EditableTitle 
                  v-model:title="taskTitle" 
                  class="article-edit-title" 
                />
              </div>
            </div>

            <div class="robot-chat-body inner-robot-chat-body" :style="'height:calc(100vh - ' +heightDiff+ 'px)'">
              <!-- 聊天窗口_start -->
              <el-scrollbar class="scroll-panel" ref="scrollbarRef" loading always wrap-style="padding:10px">

                <div ref="innerRef">

                  <div class="robot-chat-ai-say-box" v-for="(item, index) in messageList" @mouseover="showTools(item)" @mouseleave="hideTools(item)" :key="index">

                    <div class="chat-ai-say-body" :class="item.roleType == 'person' ? 'say-right-window' : ''" style="max-width:calc(100% - 135px)">
                      <div class="say-message-info" v-if="item.roleType == 'person'">
                        <span style="margin-left:10px" :class="item.showTools ? 'show-tools' : 'hide-tools'"> {{ item.dateTime }}</span>
                        {{ item.name }}
                      </div>
                      <div class="say-message-info" v-else>
                        {{ item.name }}

                        <el-button v-if="item.loading" size="default" type="primary" loading text>任务处理中</el-button>
                        <el-button v-if="item.reasoningText && !item.chatText" size="default" type="primary" loading text>推理中</el-button>

                        <span style="margin-left:10px" :class="item.showTools ? 'show-tools' : 'hide-tools'"> {{ item.dateTime }} </span>
                      </div>

                       <!-- 文件输出列表__start -->
                       <ChatAttachmentMessagePanel :message="item" @handleFileIdToMessageBox="handleFileIdToMessageBox" />
                       <!-- 文件输出列表__end -->
                      
                      <!-- 流程输出调试信息_start -->
                      <div class="chat-debugger-box" 
                        @click="handleShowDebuggerContent(index, flowStepIndex)"
                        v-for="(flowStepItem, flowStepIndex) in item.flowStepArr" 
                        :key="flowStepIndex">
                        <div class="chat-debugger">
                          <div class="chat-debugger-item">
                            <el-icon v-if="flowStepItem?.status === 'start'" class="is-loading">
                              <Loading />
                            </el-icon>
                            <el-icon v-if="flowStepItem?.status === 'process'" class="is-loading">
                              <Loading />
                            </el-icon>
                            <el-icon v-if="flowStepItem?.status === 'finish'">
                              <CircleCheck />
                            </el-icon>
                            {{ flowStepItem.message }}

                            <el-collapse-transition>
                              <el-icon v-if="!flowStepItem.isPrint && flowStepItem?.flowChatText"><ArrowRightBold /></el-icon>
                              <el-icon v-if="flowStepItem.isPrint"><ArrowDownBold/></el-icon>
                            </el-collapse-transition>
                          </div>

                          <el-collapse-transition>
                            <div class="chat-debugger-content" v-if="flowStepItem.isPrint">
                              <div class="say-message-body markdown-body chat-reasoning" v-if="flowStepItem.flowReasoningText" v-html="readerReasonningHtml(flowStepItem.flowReasoningText)"></div>
                              <div class="say-message-body markdown-body" v-if="flowStepItem.flowChatText" v-html="readerHtml(flowStepItem.flowChatText)"></div>
                            </div>
                          </el-collapse-transition>
                        </div>

                      </div>
                      <!-- 流程输出调试信息_end -->

                      <!-- 输出DeepSearch内容规划_start -->
                      <ChatDeepSearchMessagePanel 
                        :mdi="mdi"
                        v-if="item.deepSearchFlow" 
                        :deepSearchFlow="item.deepSearchFlow"
                        @handleDisplayContent="handleDisplayContent"
                         />
                      <!-- 输出DeepSearch内容规划_end -->

                      <div class="say-message-body markdown-body chat-reasoning" v-if="item.reasoningText" v-html="readerReasonningHtml(item.reasoningText)"></div>
                      <div class="say-message-body markdown-body" v-if="item.chatText" v-html="readerHtml(item.chatText)"></div>

                      <div class="chat-ai-say-tools" :class="item.roleType == 'agent' && item.chatText && (item.showTools || item.isPlaySpeaking || item.getSpeechLoading) ? 'show-tools' : 'hide-tools'">
                        <img :src="speakingIcon" v-if="item.isPlaySpeaking" style="width:25px;margin-right:10px;cursor: pointer;"  />
                        <el-button type="info" v-if="!item.isPlaySpeaking && roleInfo.voicePlayStatus" link icon="Headset" size="small" @click="handlePlayGenContent(item)" :loading="item.getSpeechLoading">播放</el-button>
                        <el-button type="info" link icon="Position" size="small" @click="handleBusinessIdToMessageBox(item)">引用</el-button>
                        <el-button type="info" link icon="CopyDocument" size="small" @click="handleCopyGenContent(item)">复制</el-button>
                        <el-button type="info" v-if="item.messageId && item.roleId && roleInfo.functionCallbackScript" size="small" link icon="Promotion" @click="handleExecutorMessage(item)">执行</el-button>
                      </div>

                      <!-- 用户问题建议_start ，只要AI最后一条消息下面输出 -->
                      <UserQuestionSuggestions 
                        ref="userQuestionSuggestionsRef" 
                        v-if="item.roleType == 'agent' && index == messageList.length -1"
                        @handleUserQuestionSuggestionsClick="sendAttachmentActions"
                        @initChatBoxScroll="initChatBoxScroll"
                        :roleId="item.roleId" 
                        :channelId="channelId" 
                        :greetingQuestion="item.greetingQuestion"
                        :chatStreamLoading="chatStreamLoading"
                      />
                      <!-- 用户问题建议_end -->

                    </div>
                  </div>

                </div>

              </el-scrollbar>
              <!-- 聊天窗口_end -->
            </div>

            <div class="robot-chat-footer chat-container robot-chat-attachement-panel" >

                <ChatAttachmentPanel @updateChatWindowHeight="updateChatWindowHeight" 
                  @sendAttachmentActions="sendAttachmentActions"  
                  ref="attachmentPanelRef" />

                  <div class="message-chat-container">

                    <div class="message-input-box">
                      <div class="message-input">

                        <el-input class="input-chat-box" @keydown.ctrl.enter.prevent="keyDown" v-model="message"
                          :options="mentionOptions" :prefix="['@']" placeholder="请输入你的问题." @select="handleSelect">
                          <template #label="{ item }">
                            {{ item.label }}
                          </template>
                        </el-input>

                      </div>
                    </div>

                    <div class="message-btn-box">

                      <AIVoiceInput @sendAudioToBackend="sendAudioToBackend" :role="roleInfo" v-if="roleInfo.voiceInputStatus"/>

                      <el-tooltip class="box-item" effect="dark" content="确认发送指令给Agent，快捷键：Enter+Ctrl" placement="top">
                        <el-button type="primary"  :loading="chatStreamLoading"  text bg size="large" @click="sendMessage('send')">
                          <svg-icon icon-class="send" class="icon-btn" style="font-size:25px" /> 
                        </el-button>
                      </el-tooltip>

                      <el-tooltip class="box-item" effect="dark" content="上传文档文件" placement="top" v-if="roleInfo.uploadStatus">
                        <el-button type="primary" text bg size="large" @click="handleUploadFile">
                          <i class="fa-solid fa-file-word icon-btn"></i>
                        </el-button>
                      </el-tooltip>

                    </div>
                </div>

            </div>
            <div class="aigen-text-warning">
              内容由第三方 AI 生成，无法确保真实准确，仅供参考
            </div>

          </div>
        </el-col>

        <el-col :span="12">
          <TracePanel ref="agentSingleRightPanelRef" />
        </el-col>

      </el-row>
    </div>

  </div>
</template>

<script setup>

import { ElLoading , ElMessage } from 'element-plus'
import MarkdownIt from 'markdown-it';
import mdKatex from '@traptitech/markdown-it-katex';
import hljs from 'highlight.js';

import ChatDeepSearchMessagePanel from './chatDeepSearchMessagePanel.vue'
import TracePanel from './tracePanel.vue'
import AIVoiceInput from '@/components/aiVoiceInput'
import ChatAttachmentPanel from '@/components/ChatAttachment/chatAttachmentPanel'
import ChatAttachmentMessagePanel from '@/components/ChatAttachment/chatAttachmentMessagePanel'
import UserQuestionSuggestions from '@/components/ChatAttachment/userQuestionSuggestionsPanel'
import EditableTitle from './components/EditableTitle'

import { getInfo, chatRole , playGenContent  } from '@/api/base/im/roleChat'
import { getParam , handleCopyGenContent } from '@/utils/ruoyi'
import { openSseConnect, handleCloseSse } from "@/api/base/im/chatsse";
import { getDeepsearchTaskById } from '@/api/base/im/scene/deepSearch';
import { submitDeepSearchTask , getTaskRecords } from '@/api/base/im/scene/deepSearchTask';

import { nextTick, onMounted } from "vue";

import speakingIcon from '@/assets/icons/speaking.gif';

import SnowflakeId from "snowflake-id";

const snowflake = new SnowflakeId();
const channelStreamId = ref(null);
const { proxy } = getCurrentInstance();
const route = useRoute();

const agentSingleRightPanelRef = ref(null)

const userQuestionSuggestionsRef = ref(null);
const attachmentPanelRef = ref(null);
const heightDiff = ref(228);

const loading = ref(false)
const roleId = ref(null);
const channelId = ref(null);
const sceneId = ref(route.query.sceneId)
const taskId = ref(route.query.taskId)
const currentTask = ref({})
const taskTitle = ref('深度搜索任务')
const taskStatus = ref(null)

const roleInfo = ref({
  roleName: '深度搜索业务角色',
  responsibilities: '深度搜索业务角色',
})

const message = ref('');
const businessId = ref(null);
const editDialogVisible = ref(false)
const innerRef = ref(null); // 滚动条的处理_starter
const scrollbarRef = ref(null);
const messageList = ref([]);
const currentBusinessId = ref(null)
const streamLoading = ref(null)
const chatStreamLoading = ref(false); // 聊天加载

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

function sendAttachmentActions(actionTxt) {
  message.value = actionTxt ;
  sendMessage('send');
  message.value = '';
}

/** 读取html文本 */
function readerHtml(chatText) {
  return mdi.render(chatText);
}

const handleUploadFile = () => {
  let uploadDataJson = {} ;
  if(roleInfo.value.uploadData){
    uploadDataJson = JSON.parse(roleInfo.value.uploadData) ;
  }
  attachmentPanelRef.value.openFileSelector(uploadDataJson) ; 
};

function readerReasonningHtml(chatText) {
  if(chatText){
    return mdi.render(chatText);
  }
}

/** 编辑生成内容和更新生成内容 */
function handleEditGenContent(item){
  currentBusinessId.value = item.businessId + '' ;
  editDialogVisible.value = true;
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
      messageList.value[existingIndex].messageId = newMessage.messageId;
      if(newMessage.usage){
        messageList.value[existingIndex].usage = newMessage.usage;
      }

      // DeepSearch 处理结果
      messageList.value[existingIndex].deepSearchFlow = newMessage.deepSearchFlow;
      pushDeepsearchFlowTracePanel(newMessage.deepSearchFlow) // 推送到消息跟踪面板

      const findMessage = messageList.value[existingIndex];

      if (newMessage.flowStep) {
        const existingStepIdIndex = findMessage.flowStepArr.findIndex(item => item.stepId === newMessage.flowStep.stepId);

        console.log('existingStepIdIndex = ' + existingStepIdIndex);

        if (existingStepIdIndex !== -1) {
          messageList.value[existingIndex].flowStepArr[existingStepIdIndex].status = newMessage.flowStep.status;
          messageList.value[existingIndex].flowStepArr[existingStepIdIndex].isPrint = newMessage.flowStep.print;
          messageList.value[existingIndex].flowStepArr[existingStepIdIndex].flowChatText += newMessage.flowStep.flowChatText;
          messageList.value[existingIndex].flowStepArr[existingStepIdIndex].flowReasoningText += newMessage.flowStep.flowReasoningText;
          console.log('flow chat text = ' + messageList.value[existingIndex].flowStepArr[existingStepIdIndex].flowChatText);
        } else {
          messageList.value[existingIndex].flowStepArr.push(newMessage.flowStep);
        }
      } 

    } else {
      // 否则，添加新消息
      if(newMessage.flowStep){
        newMessage.flowStepArr.push(newMessage.flowStep) ; 
      }
      messageList.value.push(newMessage);
    }
  } else {
    messageList.value.push(newMessage);
  }

  // 调用初始化滚动条的函数
  initChatBoxScroll();
};

// 推送到deepsearchFlowTracePanel
const pushDeepsearchFlowTracePanel = (item) =>{
  nextTick(() => { 
    if(agentSingleRightPanelRef.value){
      agentSingleRightPanelRef.value.pushDeepsearchFlowTracePanel(item)
    }
  })
}

function handleExecutorMessage(item){

  const businessIdMessage = ' #' + item.messageId + ' ';
  businessId.value = item.messageId;
  message.value += businessIdMessage;

  sendMessage('function');

}

// 是否显示调试内容
const handleShowDebuggerContent = (messageIndex, stepIndex) => {
  console.log('handleShowDebuggerContent messageIndex = ' + messageIndex + ' , stepIndex = ' + stepIndex);

  // 对 showContent 的值取反
  messageList.value[messageIndex].flowStepArr[stepIndex].isPrint = !messageList.value[messageIndex].flowStepArr[stepIndex].isPrint;
}

/** 连接sse */
function handleSseConnect(channelStreamId) {
  nextTick(() => {
    if (channelStreamId) {

      let sseSource = openSseConnect(channelStreamId);
      // 接收到数据
      sseSource.onmessage = function (event) {

        if (!event.data.includes('[DONE]')) {
          let resData = event.data;
          if (resData != 'ping') {  // 非心跳消息
            const data = JSON.parse(resData);
            pushResponseMessageList(data);
          }
      } else if(event.data.includes('[DONE]')) {
          // console.log('消息接收结束.')
          // chatStreamLoading.value = false ; // 关闭流式结束
        }

      }
    }
  })
}

function handleBusinessIdToMessageBox(item) {
  const businessIdMessage = ' #' + item.messageId + ' ';
  businessId.value = item.messageId;
  message.value += businessIdMessage;
}

/** 文件引用 */
function handleFileIdToMessageBox(file) {

const attFile = {
  id: file.fileId,
  name: file.fileName , 
  extension: file.fileType
}
attachmentPanelRef.value.setReferenceFile(attFile)
}

/** 获取角色信息 */
const handleGetInfo = async(roleId) => {
  getInfo(roleId).then(res => {
    let role = res.role;
    let msg = res.message;

    roleInfo.value = role;

    console.log('taskStatus = ' + taskStatus.value) ;
    console.log('handle getInfo msg = ' + JSON.stringify(msg)) ;

   // 如果是运行中的任务，则不需要添加
   if (taskStatus.value === 'completed' || taskStatus.value === 'not_run') {
     pushResponseMessageList(msg);
   }

    loading.value = false;
  })
}

// 动态更新高度
const updateChatWindowHeight = (heightVal) => {
  console.log('heightVal = ' + heightVal);
  if(heightVal > 0){
    heightDiff.value = heightVal - 32;
  }else {
    heightDiff.value = 260 - 32;
  }
  console.log('heightDiff.value = ' + heightDiff.value);
};

/** 播放生成内容 */
const handlePlayGenContent = (item) => {
  // getSpeechLoading.value = true ;

  if(!roleInfo.value.voicePlayStatus){
    ElMessage.error('未开启语音播放');
    return ;
  }

  item.getSpeechLoading = true ;

  playGenContent(item).then(res => {
    const audioBlob = new Blob([res], { type: 'audio/wav' }) // 这按照自己的数据类型来写type的内容
    const audioUrl = URL.createObjectURL(audioBlob) // 生成url
    const audio = new Audio(audioUrl);

    audio.addEventListener('ended', () => {
      console.log('音频播放完成');
      // isPlaySpeaking.value = false 
      item.isPlaySpeaking = false 
    });

    // getSpeechLoading.value = false ;
    // isPlaySpeaking.value = true 

    item.getSpeechLoading = false ;
    item.isPlaySpeaking = true 

    audio.play(); 
  }).catch(error => {
    // getSpeechLoading.value = false ;
    item.getSpeechLoading = false ;
    ElMessage.error('播放失败，请确认是否配置语音服务');
  });
}

/** 发送消息 */
const sendMessage = (type) => {

  // 获取到上传的文件列表
  const uploadFiles = attachmentPanelRef.value.handleGetUploadFiles();
  console.log('handleGetUploadFiles = ' + uploadFiles);

  if (!message.value) {
    proxy.$modal.msgError("请输入消息内容.");
    return;
  }

  chatStreamLoading.value = true ;

  let formData = {
    taskId: taskId.value,
    sceneId: sceneId.value,
    roleId: roleId.value,
    channelId: channelId.value,
    channelStreamId: channelStreamId.value,
    message: message.value,
    businessIds: [businessId.value],
    type: type , 
    fileIds: uploadFiles // [...uploadFiles , ...refreshFieldId.value] 
  }

  submitDeepSearchTask(formData, roleId.value).then(res => {
    proxy.$modal.msgSuccess("发送成功");
    pushResponseMessageList(res.data);
  }).catch(error => {
    streamLoading.value.close();
    chatStreamLoading.value = false;
  })

  message.value = '';
};

// 发送音频数据到后端
const sendAudioToBackend = async (voiceMessage) => {
  try {

    chatStreamLoading.value = true ;
    
    message.value = voiceMessage ; 

    streamLoading.value.close();
    sendMessage('send');

  } catch (error) {
    console.error('语音识别请求失败:', error);
    // streamLoading.value.close();
    chatStreamLoading.value = false ;
  }
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

// 显示DeepSearch的内容
function handleDisplayContent(item){
  // emits("handleDisplayContent", item);
  if(agentSingleRightPanelRef.value){
    agentSingleRightPanelRef.value.handleDisplayContent(item)
  }
}

// 新增方法：加载任务历史记录
const loadTaskHistoryRecords = async () => {
  try {
    const res = await getTaskRecords(taskId.value);
    const deepSearchFlow = res.data;
    const username = res.username;

    if (deepSearchFlow) {
      // 重建消息列表
      rebuildMessageListFromFlow(deepSearchFlow , username);
    }
  } catch (error) {
    console.error('加载任务历史记录失败:', error);
    ElMessage.error('加载历史记录失败');
  }
};

// 重建消息列表
const rebuildMessageListFromFlow = (deepSearchFlow , username) => {
  // 清空当前消息列表
  // messageList.value = [];

  // 添加用户初始消息
  const userMessage = {
    roleType: 'person',
    name: username || '用户',
    dateTime: new Date().toLocaleString(),
    chatText: currentTask.value.promptContent,
    businessId: snowflake.generate(),
    showTools: false
  };

  messageList.value.push(userMessage);

  // 添加 AI 响应消息（包含完整的 DeepSearch 流程）
  const aiMessage = {
    roleType: 'agent',
    name: roleInfo.value.roleName || 'AI助手',
    dateTime: new Date().toLocaleString(),
    businessId: snowflake.generate(),
    showTools: false,
    deepSearchFlow: deepSearchFlow,
    chatText: deepSearchFlow.output?.actions?.[0]?.result || '',
    loading: false
  };
  messageList.value.push(aiMessage);

  // 初始化滚动条
  initChatBoxScroll();
};

// 销毁信息
onBeforeUnmount(() => {
  if(channelStreamId.value) {
      handleCloseSse(channelStreamId.value).then(res => {
        console.log('关闭sse连接成功:' + channelId)
      })
  }
});

onMounted(async() => { 

  nextTick(async() => {
    const taskRes = await getDeepsearchTaskById(taskId.value) ;

    let data = taskRes.data ;
    currentTask.value = data ; 

    taskTitle.value = data.taskName ;
    taskStatus.value = data.taskStatus ;

    // messageList.value = [];
    roleId.value =  data.searchPlannerEngineer ;
    channelId.value = data.id ;
    channelStreamId.value = data.channelStreamId;

    handleSseConnect(channelStreamId.value)
    await handleGetInfo(roleId.value);

    // 新增：如果任务已完成或运行中，加载历史记录
    if (taskStatus.value === 'completed') {
      await loadTaskHistoryRecords();
    }

    // 如果没有运行，则提交消息任务
    if(taskStatus.value === 'not_run'){
      message.value = data.promptContent ;
      sendMessage('send');
      return ;
    }else if(taskStatus.value === 'running') { // 运行状态
      chatStreamLoading.value = true ;
    }
  })

});

</script>

<style lang="scss" scoped>
.inner-smart-container {
  max-width: 100% ! important;

  .robot-chat-windows {
    border: 0px !important;
    padding: 0 1%;
  }
}

.scroll-panel {
  padding-bottom: 10px;
  float: left;
  width: 100%;
  height: calc(100%);
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
      width: 50px;
      height: 50px;

      img {
        border-radius: 50%;
        width: 100%;
        height: 100%;
        object-fit: cover;
        object-position: center;
      }

    }

  }

  .chat-ai-say-tools{
    margin-top: 3px;
    text-align: right;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    height: 30px;
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
      border-radius: 5px;
      background: #f5f5f5;
    }

  }

  .message-list {
    margin-top: 20px;
  }

  .message {
    margin-bottom: 8px;
  }


}

  .robot-chat-header {
    display: flex;
    align-items: center;
    padding: 5px !important;
}

.chat-header-title {
    align-items: center;
    gap: 10px;
}
.chat-top-ai-header{
  .header-top-image{ 
    img {
      width: 40px;
      height: 40px;
      border-radius: 50%;
    }
  }
}
</style>