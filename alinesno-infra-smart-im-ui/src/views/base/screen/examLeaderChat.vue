<!-- 意见审批  -->
<template>
  <div class="acp-dashboard aip-chat-dashboard-panel" 
        v-loading="loading" 
        element-loading-text="Loading..."
        :element-loading-spinner="svg" 
        element-loading-svg-view-box="-10, -10, 50, 50" style="padding:0px !important;">

    <div class="smart-container inner-smart-container">
      <el-row>
        <el-col :span="24">
          <div class="robot-chat-windows">
            <div class="robot-chat-header" style="padding:15px 0px !important">
              <div class="chat-header-title">
                {{ roleInfo.roleName }}
              </div>
              <div class="chat-header-desc">
                ({{ truncateString(roleInfo.responsibilities, 60) }})
              </div>
              <!-- 
              <div class="chat-header-desc" style="float: right;margin-top: -10px;">
                <el-button type="primary" text bg size="large" @click="taskFlowDialogVisible = true">
                  <i class="fa-solid fa-truck-fast icon-btn"></i>
                </el-button>
              </div> 
              -->
            </div>

            <div class="robot-chat-body inner-robot-chat-body" style="height: calc(100vh - 290px)">
              <!-- 聊天窗口_start -->
              <el-scrollbar class="scroll-panel" ref="scrollbarRef" loading always wrap-style="padding:10px">

                <div ref="innerRef">

                  <div class="robot-chat-ai-say-box" v-for="(item, index) in messageList" @mouseover="showTools(item)"
                    @mouseleave="hideTools(item)" :key="index">

                    <div class="chat-ai-header" :class="item.roleType == 'person' ? 'say-right-window' : ''">
                      <div class="header-images">
                        <img :src="imagePath(item)" />
                      </div>
                    </div>

                    <div class="chat-ai-say-body" :class="item.roleType == 'person' ? 'say-right-window' : ''" style="max-width:calc(100% - 335px)">
                      <div class="say-message-info" v-if="item.roleType == 'person'">
                        <span style="margin-left:10px" :class="item.showTools ? 'show-tools' : 'hide-tools'"> {{ item.dateTime }}</span>
                        {{ item.name }}
                      </div>
                      <div class="say-message-info" v-else>
                        {{ item.name }}

                        <el-button v-if="item.loading" size="default" type="primary" loading text>任务处理中</el-button>
                        <span style="margin-left:10px" :class="item.showTools ? 'show-tools' : 'hide-tools'"> {{ item.dateTime }} </span>
                      </div>

                      <div class="say-message-body markdown-body" v-html="readerHtml(item.chatText)"></div>

                    </div>
                  </div>

                </div>

              </el-scrollbar>
              <!-- 聊天窗口_end -->
            </div>

            <!-- <div class="robot-chat-footer chat-container" style="float:left;width:100%">

                <div class="message-input-box">
                  <div class="message-input">

                    <el-input class="input-chat-box" @keydown.ctrl.enter.prevent="keyDown" v-model="message"
                      :options="mentionOptions" :prefix="['@']" placeholder="请输入你的问题." @select="handleSelect">
                      <template #label="{ item }">
                        {{ item.label }}
                      </template>
                    </el-input>

                </div>

                <div class="message-btn-box">

                  <el-tooltip class="box-item" effect="dark" content="确认发送指令给Agent，快捷键：Enter+Ctrl" placement="top">
                    <el-button type="danger" text bg size="large" @click="sendMessage('send')">
                      <svg-icon icon-class="send" class="icon-btn" style="font-size:25px" /> 
                    </el-button>
                  </el-tooltip>

                  <el-tooltip class="box-item" effect="dark" content="执行任务" placement="top">
                    <el-button type="warning" text bg size="large" @click="sendMessage('function')">
                      <i class="fa-solid fa-feather icon-btn"></i>
                    </el-button>
                  </el-tooltip>

                </div>
                </div>

            </div> -->

            <div class="robot-chat-footer chat-container" style="float:left;width:100%">

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

                  <el-tooltip class="box-item" effect="dark" content="确认发送指令给Agent，快捷键：Enter+Ctrl" placement="top">
                    <el-button type="danger" text bg size="large" @click="sendMessage('send')">
                      <svg-icon icon-class="send" class="icon-btn" style="font-size:25px" /> 
                    </el-button>
                  </el-tooltip>

                  <el-tooltip class="box-item" effect="dark" content="执行任务" placement="top">
                    <el-button type="warning" text bg size="large" @click="sendMessage('function')">
                      <i class="fa-solid fa-feather icon-btn"></i>
                    </el-button>
                  </el-tooltip>

                </div>

            </div>

            <div class="aigen-text-warning">
              内容由第三方 AI 生成，无法确保真实准确，仅供参考
            </div>

            
          </div>
        </el-col>

      </el-row>
    </div>

  </div>
</template>

<script setup>

import { ElLoading } from 'element-plus'
import MarkdownIt from 'markdown-it';
import mdKatex from '@traptitech/markdown-it-katex';
import hljs from 'highlight.js';

import { getInfo } from '@/api/base/im/roleChat'
import { getParam } from '@/utils/ruoyi'
import { openSseConnect, handleCloseSse } from "@/api/base/im/chatsse";
import { nextTick, onMounted } from "vue";
import SnowflakeId from "snowflake-id";

import { 
  chatMessage
} from '@/api/base/im/robot'

import {
    executeScreenTask ,
    leaderPlan ,
} from '@/api/base/im/screen'

const { proxy } = getCurrentInstance();

const loading = ref(false)
const roleId = ref(null);
const screenId = ref(null);
const roleInfo = ref({
    roleName:'考试培训助教' , 
    responsibilities:"1. 帮助用户完成任务，2. 帮助用户完成任务"
})
const message = ref('');
const businessId = ref(null);
const snowflake = new SnowflakeId();

const innerRef = ref(null); // 滚动条的处理_starter
const scrollbarRef = ref(null);
const messageList = ref([]);

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
    if(element){
      const scrollHeight = element.scrollHeight;

      if(scrollHeight){
        scrollbarRef.value.setScrollTop(scrollHeight);
      }
    }
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

/** 读取html文本 */
function readerHtml(chatText) {
  return mdi.render(chatText);
}

// 推送消息到当前面板
const pushResponseMessageList = (newMessage) => {
  console.log(`--->>> newMessage = ${JSON.stringify(newMessage)}`);

  if (newMessage.llmStream === true) { // 是否为流式输出

    // 查找是否有相同businessId的消息
    const existingIndex = messageList.value.findIndex(item => item.businessId === newMessage.businessId);

    if (existingIndex !== -1) {
      // 如果找到，更新该消息
      messageList.value[existingIndex].chatText += newMessage.chatText;
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

/** 连接sse */
function handleSseConnect(screenId) {
  nextTick(() => {
    if (screenId) {

      let sseSource = openSseConnect(screenId);
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
        }

      }
    }
  })
}

// function handleBusinessIdToMessageBox(item) {
//   const businessIdMessage = ' #' + item.businessId + ' ';
//   businessId.value = item.businessId;
//   message.value += businessIdMessage;
// }

/** 获取角色信息 */
function handleGetInfo(roleId) {

  if(!roleId){
    return ;
  }

  loading.value = true ;

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
    background: 'rgba(255, 255, 255, 0.5)',
    customClass: 'custom-loading'
  })

  let formData = {
    screenId: screenId.value,
    message: message.value
  }

  leaderPlan(formData, roleId.value).then(res => {
    proxy.$modal.msgSuccess("发送成功");
    // pushResponseMessageList(res.data);
    // streamLoading.value.close();

    // 循环执行任务
    let taskList = res.data
    handleExecuteScreenTask(taskList)

  }).catch(e => {
    console.info("请求异常" + e)
    streamLoading.value.close();
  })

  message.value = '';
};

const handleExecuteScreenTask = async (taskList) => {
    let uId = snowflake.generate()
    try{
        for(let i = 0; i < taskList.length; i++){
            let item = taskList[i]

            let text = item.roleName + ' 角色开始执行，请耐心等待 ...';
            streamLoading.value.setText(text)
            const result = await executeScreenTask(item , uId) ;

            if(result.code == 200){
                text = '任务执行完成，请等待下一次任务 ...';
                streamLoading.value.setText(text)
            }
        }
    } catch (error) {
        console.error('Error fetching data:', error);
        proxy.$modal.msgError("生成失败");
        streamLoading.value.close();
    }
    streamLoading.value.close();
}


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

/** 获取频道的最近100条消息 */ 
function handleScreenMessage(){

  chatMessage(screenId.value).then(res => {
    let msgList = res.data 
    if(msgList){
      messageList.value = msgList ; 
      initChatBoxScroll();
    }
  })

}

onMounted(() => {
  screenId.value = getParam('screenId')

  handleSseConnect(screenId.value)
  handleScreenMessage()
})


// 销毁信息
onBeforeUnmount(() => {
  handleCloseSse(screenId.value).then(res => {
    console.log('关闭sse连接成功:' + screenId)
  })
});

// function runTask() {
//   streamLoading.value = ElLoading.service({
//     lock: true,
//     text: '任务执行中，请勿操作其它界面 ...',
//     background: 'rgba(0, 0, 0, 0.2)',
//   })
// }

defineExpose({
  handleGetInfo 
})

</script>

<style lang="scss" scoped>
.inner-smart-container {
  max-width: 100% ! important;

  .robot-chat-windows {
    border: 0px !important;
    padding: 0px !important;
  }

  // .inner-robot-chat-body {
  //   height: calc(100vh - 100px);
  // }
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