<template>
  <el-scrollbar class="scroll-panel" ref="scrollbarRef" loading always wrap-style="padding:10px">

    <div ref="innerRef">

      <div class="robot-chat-ai-say-box" v-for="(item, index) in messageList" 
          @mouseover="showTools(item)"
          @mouseleave="hideTools(item)"
          :key="index">

        <div class="chat-ai-header" :class="item.roleType == 'person' ? 'say-right-window' : ''">
          <div class="header-images">
            <img :src="imagePath(item)" />
          </div>
        </div>

        <div class="chat-ai-say-body" :class="item.roleType == 'person' ? 'say-right-window' : ''" style="max-width:calc(100% - 135px)">
          <div class="say-message-info" v-if="item.roleType == 'person'"> 
            <span style="margin-left:10px" :class="item.showTools?'show-tools':'hide-tools'"> {{ item.dateTime }}</span> 
            {{ item.name }}
          </div>
          <div class="say-message-info" v-else> 
            {{ item.name }}

            <el-button v-if="item.loading" size="default" type="primary" loading text>任务处理中</el-button>
            <el-button v-if="item.reasoningText && !item.chatText" size="default" type="primary" loading text>推理中</el-button>

            <span style="margin-left:10px" :class="item.showTools?'show-tools':'hide-tools'"> {{ item.dateTime }} </span>
          </div>

          <!-- 流程输出调试信息_start -->
          <div class="chat-debugger-box" 
            @click="handleShowDebuggerContent(index, flowStepIndex)"
            v-for="(flowStepItem, flowStepIndex) in item.flowStepArr" 
            :key="flowStepIndex"
            v-if="item.roleType != 'person'">

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


          <div class="say-message-body markdown-body chat-reasoning" v-if="item.reasoningText" v-html="readerReasonningHtml(item.reasoningText)"></div>
          <div class="say-message-body markdown-body" v-if="item.chatText" v-html="readerHtml(item.chatText)"></div>

          <!-- <div class="say-message-body markdown-body" v-html="readerHtml(item.chatText)"></div> -->

          <div class="chat-ai-say-tools" style="margin-top: 3px;;text-align: right;float:right" :class="item.showTools?'show-tools':'hide-tools'">
              <el-button type="danger" link icon="Promotion" size="small" @click="handleBusinessIdToMessageBox(item)">引用</el-button>
              <el-button type="primary" link icon="CopyDocument" size="small" @click="handleCopyGenContent(item)">复制</el-button>
              <el-button type="primary" link icon="EditPen" size="small" @click="handleEditGenContent(item)">查看</el-button>
              <el-button type="primary" v-if="item.businessId && item.roleId" link icon="Position" size="small" @click="handleExecutorMessage(item)">执行</el-button>
          </div>

        </div>
      </div>

    </div>

  </el-scrollbar>
</template>

<script setup>

import { ElMessage } from 'element-plus';
import { nextTick , ref , defineEmits} from 'vue';

import MarkdownIt from 'markdown-it';
import mdKatex from '@traptitech/markdown-it-katex';
import hljs from 'highlight.js';


import { handleCopyGenContent } from '@/utils/ruoyi'
import useUserStore from '@/store/modules/user'
const userStore = useUserStore()

const loading = ref(false)

// 滚动条的处理_starter
const innerRef = ref(null);
const scrollbarRef = ref(null);
const messageList = ref([]);

// 定义派发事件
const emit = defineEmits(['sendMessageToChatBox' , 'handleEditorContent' , 'executorMessage'])

// 改变组件的中的方法
const pushMessageList = (mess) => {

  let chatText = '';
  for (let i = 0; i < mess.length; i++) {
    let content = mess[i];
    if (content.type === 'text') {
      chatText += ('<span class="mention-text">' + (content.text) + '</span>');
    } else if (content.type === 'mention') {
      chatText += ('<span class="mention">' + ('@' + content.username) + '</span>');
    } else if (content.type === 'business') {
      chatText += ('<span class="mention-business">' + ('#' + content.businessId) + '</span>');
    }
  }

  // TODO 从本地store中获取到当前用户信息
  messageList.value.push({ 
    roleType: 'person', 
    businessId: '1733452663532019712' ,  
    dateTime: '2023-12-11 16:32:10' ,  
    readerType:'html', 
    icon: userStore.avatar , 
    name: userStore.nickName , 
    date: '12-10 13:58:21', 
    chatText: chatText 
  });

  initChatBoxScroll();
};

// 推送消息到当前面板
const currentResponseMessageList = (message) => {
  messageList.value = message ; 
  initChatBoxScroll();
}

// 推送消息到当前面板
const pushResponseMessageList = (newMessage) => {

  // console.log('--->>> message = ' + message);
  // messageList.value.push(message) ; 
  // console.log(`--->>> message = ${JSON.stringify(newMessage)}`);

  if(newMessage.llmStream === true){ // 是否为流式输出
    // 查找是否有相同businessId的消息
    const existingIndex = messageList.value.findIndex(item => item.businessId === newMessage.businessId);

    if (existingIndex !== -1) {
      // 如果找到，更新该消息
      // messageList.value[existingIndex].chatText += message.chatText;
      messageList.value[existingIndex].reasoningText += newMessage.reasoningText; 
      messageList.value[existingIndex].chatText += newMessage.chatText;

      const findMessage = messageList.value[existingIndex] ; 

      if(newMessage.flowStep){
        const existingStepIdIndex = findMessage.flowStepArr.findIndex(item => item.stepId === newMessage.flowStep.stepId);

        console.log('existingStepIdIndex = ' + existingStepIdIndex);

        if(existingStepIdIndex !== -1){
          messageList.value[existingIndex].flowStepArr[existingStepIdIndex].status = newMessage.flowStep.status;
          messageList.value[existingIndex].flowStepArr[existingStepIdIndex].isPrint = newMessage.flowStep.print;
          messageList.value[existingIndex].flowStepArr[existingStepIdIndex].flowChatText += newMessage.flowStep.flowChatText;
          messageList.value[existingIndex].flowStepArr[existingStepIdIndex].flowReasoningText += newMessage.flowStep.flowReasoningText;
          console.log('flow chat text = ' + messageList.value[existingIndex].flowStepArr[existingStepIdIndex].flowChatText) ; 
        }else{
          messageList.value[existingIndex].flowStepArr.push(newMessage.flowStep) ; 
        }
      }

    } else {
      // 否则，添加新消息
      if(newMessage.flowStep){
        newMessage.flowStepArr.push(newMessage.flowStep) ; 
      }
      messageList.value.push(newMessage);
    }
  }else{
    messageList.value.push(newMessage);
  }

  messageList.value.forEach(item => {
    console.log('--->>> item = ' + item);
    if(item.loading && newMessage.roleId == item.roleId && newMessage.status === 'completed'){
      item.loading = false;
    }
  });

  initChatBoxScroll();
}

function initChatBoxScroll() {

  nextTick(() => {
    const element = innerRef.value;  // 获取滚动元素
    const scrollHeight = element.scrollHeight;

    scrollbarRef.value.setScrollTop(scrollHeight) ; 
  })

}

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

// mdi.use(mila, { attrs: { target: '_blank', rel: 'noopener' } });
mdi.use(mdKatex, { blockClass: 'katexmath-block rounded-md p-[10px]', errorColor: ' #cc0000' });

function highlightBlock(str, lang) {
  return `<pre class="code-block-wrapper"><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
}

/** 显示图片 */
// function imagePath(row){
//   let roleAvatar = '1746435800232665090' ; 
//   if(row.icon){
//     roleAvatar = row.icon ; 
//   }
//   return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
// }

function readerHtml(chatText) {
  return mdi.render(chatText);
}

// 是否显示调试内容
const handleShowDebuggerContent = (messageIndex, stepIndex) => {
  console.log('handleShowDebuggerContent messageIndex = ' + messageIndex + ' , stepIndex = ' + stepIndex);

  // 对 showContent 的值取反
  messageList.value[messageIndex].flowStepArr[stepIndex].isPrint = !messageList.value[messageIndex].flowStepArr[stepIndex].isPrint;
}

function readerReasonningHtml(chatText) {
  if(chatText){
    return mdi.render(chatText);
  }
}

/** 发送业务代码到消息框中 */
function handleBusinessIdToMessageBox(item){
  const businessIdMessage = ' #' + item.businessId + ' ' ;
  emit('sendMessageToChatBox' , businessIdMessage) ; 
  emit('handleSelectPreBusinessId' , item.businessId) ; 
}

/** 编辑生成内容和更新生成内容 */
function handleEditGenContent(item){
  const businessId = item.businessId + '' ;
  console.log('businessId = ' + businessId) ;
  emit('handleEditorContent' , businessId) ; 
}

// 定义 handleCopyGenContent 函数
// const handleCopyGenContent = async (item) => {
//   try {
//     const text = item.reasoningText? item.reasoningText + item.chatText : item.chatText ;
//     await navigator.clipboard.writeText(text);
//     ElMessage.success('复制成功！');
//   } catch (error) {
//     console.error('复制失败:', error);
//     ElMessage.error('复制失败，请稍后重试。');
//   }
// };

function handleExecutorMessage(item){
  emit('executorMessage' , item) ; 
}

/** 重新生成内容 */
// const handleRecyleGenContent = (item) => {
//   console.log('handleRecyleGenContent item = ' + item) ;
//   const businessIdMessage = ' #' + item.businessId + ' ' ;
//   emit('sendMessageToChatBox' , businessIdMessage) ; 
// }

function showTools(item) {
    messageList.value.forEach((i) => {
      i.showTools = i === item; // 只有当前项的 showTools 被设置为 true
    });
}

function hideTools(item) {
  item.showTools = false; // 鼠标移出时隐藏 tools
}

// onMounted(() => {
//   handleChatMessage();
// })

// 将这个方法暴露出去,这样父组件就可以使用了哈
defineExpose({
  pushMessageList,
  currentResponseMessageList,
  pushResponseMessageList
});

</script>

<style lang="scss" scoped>
.scroll-panel {
  padding-bottom: 10px;
  float: left;
  width: 100%;
  height: calc(100% - 95px);
  overflow: hidden;
}

.show-tools{
  visibility: visible;
}

.hide-tools{
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
      border-radius: 5px;
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