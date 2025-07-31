<!-- 意见审批  -->
<template>
  <div class="acp-dashboard aip-chat-dashboard-panel" 
        v-loading="loading" 
        element-loading-text="Loading..."
        :element-loading-spinner="svg"
        element-loading-svg-view-box="-10, -10, 50, 50"
        element-loading-background="rgba(122, 122, 122, 0.8)"
        style="padding:0px !important;">
    <div class="smart-container inner-smart-container">
      <el-row>
        <el-col :span="24">
          <div class="robot-chat-windows">
            <div class="robot-chat-header">
              <div class="chat-header-title">
                <!-- <img class="aip-chat-box-title-img" :src="imagePathByPath(roleInfo.roleAvatar)" /> -->
                {{ roleInfo.roleName}} 
              </div>
              <div class="chat-header-desc">
                ({{ truncateString(roleInfo.responsibilities,30) }}) 
              </div>
              <div class="chat-header-desc" style="float: right;margin-top: -10px;">
                  <el-button type="primary" text bg size="large" @click="taskFlowDialogVisible = true" >
                    <i class="fa-solid fa-truck-fast icon-btn"></i>
                  </el-button>
              </div>
            </div>

            <div class="robot-chat-body inner-robot-chat-body">
                <!-- 聊天窗口_start -->
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

                                <span style="margin-left:10px" :class="item.showTools?'show-tools':'hide-tools'"> {{ item.dateTime }} </span>
                            </div>

                            <div class="say-message-body markdown-body" v-html="readerHtml(item.chatText)"></div>

                            <div class="chat-ai-say-tools" style="margin-top: 3px;;text-align: right;float:right" :class="item.showTools?'show-tools':'hide-tools'">
                                <el-button type="primary" link icon="EditPen" size="small" @click="handleEditGenContent(item)">复制</el-button>
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
                            placeholder="请输入你的问题."
                            @select="handleSelect"
                          >
                          <template #label="{ item }">
                            {{ item.label }}
                          </template>
                        </el-input> 

                    </div>
                </el-col>

                <el-col :span="8" style="text-align: right;">

                    <el-tooltip class="box-item" effect="dark" content="确认发送指令给Agent，快捷键：Enter+Ctrl" placement="top" >
                      <el-button type="danger" text bg size="large" @click="sendMessage('send')">
                        <svg-icon icon-class="send" class="icon-btn" style="font-size:25px" /> 
                      </el-button>
                    </el-tooltip>

                    <el-tooltip class="box-item" effect="dark" content="执行任务" placement="top" >
                      <el-button type="warning" text bg size="large" @click="sendMessage('function')" >
                        <i class="fa-solid fa-feather icon-btn"></i>
                      </el-button>
                    </el-tooltip>
                </el-col>
              </el-row>

            </div>
          </div>
        </el-col>
       
      </el-row>
    </div>

  </div>
</template>

<script setup>

import MarkdownIt from 'markdown-it';
import mdKatex from '@traptitech/markdown-it-katex';
import hljs from 'highlight.js';
import { ElLoading } from 'element-plus'
import defAva from '@/assets/images/profile.jpg'

import { getInfo, chatRole }from '@/api/base/im/roleChat'
import { getParam } from '@/utils/ruoyi'
import { openSseConnect , handleCloseSse} from "@/api/base/im/chatsse";
import { onMounted } from "vue";

const { proxy } = getCurrentInstance();

const loading = ref(true)
const roleId = ref(null);
const channelId = ref(null);
const roleInfo = ref({})
const message = ref('');

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
    const scrollHeight = element.scrollHeight;

    scrollbarRef.value.setScrollTop(scrollHeight) ; 
  })

}

function highlightBlock(str, lang) {
  return `<pre class="code-block-wrapper"><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
}

function keyDown(e) {
  if(!message.value){
    return ;
  }
  sendMessage('send');
  message.value = '' ;
}

/** 读取html文本 */
function readerHtml(chatText) {
  return mdi.render(chatText);
}

// 推送消息到当前面板
const pushResponseMessageList = (newMessage) => {
  console.log(`--->>> newMessage = ${JSON.stringify(newMessage)}`);

  if(newMessage.llmStream === true){ // 是否为流式输出

    // 查找是否有相同businessId的消息
    const existingIndex = messageList.value.findIndex(item => item.businessId === newMessage.businessId);

    if (existingIndex !== -1) {
      // 如果找到，更新该消息
      messageList.value[existingIndex].chatText += newMessage.chatText;
    } else {
      // 否则，添加新消息
      messageList.value.push(newMessage);
    }
  }else{
      messageList.value.push(newMessage);
  }

  // 调用初始化滚动条的函数
  initChatBoxScroll();
};

/** 连接sse */
function handleSseConnect(channelId){
  nextTick(() => {
    if(channelId){
        let sseSource = openSseConnect(channelId) ;
        // 接收到数据
        sseSource.onmessage = function (event) {
        if (!event.data.includes('[DONE]')) {
          let resData = event.data;

          console.log('sseSource.onmessage = ' + resData);

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
            // if (!event.data.includes('[DONE]')) {
            //   const data = JSON.parse(event.data);
            //   pushResponseMessageList(data);
            // }else{
            //   console.log('消息接收结束.')  
            // }
        }

      // })
    }
  })
}

// 销毁信息
onBeforeUnmount(() => {
  handleCloseSse(channelId.value).then(res => {
    console.log('关闭sse连接成功:' + channelId)
  })
});

/** 获取角色信息 */
function handleGetInfo(roleId){
  getInfo(roleId).then(res => {
    let role = res.role;
    let msg = res.message;

    roleInfo.value = role;
    pushResponseMessageList(msg);

    loading.value = false;
  })
}

/** 发送消息 */
const sendMessage = (type) => {

  if(!message.value){
    proxy.$modal.msgError("请输入消息内容.");
    return ;
  }

  streamLoading.value = ElLoading.service({
    lock: true,
    text: '任务执行中，请勿操作其它界面 ...',
    background: 'rgba(0, 0, 0, 0.2)',
  })


  let formData = {
    channelId: channelId.value,
    message: message.value,
  }
  
  chatRole(formData , roleId.value).then(res => {
    proxy.$modal.msgSuccess("发送成功");
    pushResponseMessageList(res.data);
  })

  message.value = '';
};

function handleDefAva(){
  return defAva ;
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

onMounted(() => {
  initChatBoxScroll();

  roleId.value = getParam('roleId')
  channelId.value = getParam('channelId')

  handleSseConnect(channelId.value)
  handleGetInfo(roleId.value);
})

</script>

<style lang="scss" scoped>
.inner-smart-container {
    max-width: 960px !important;

    .robot-chat-windows{
      border:0px !important;
    }

    // .inner-robot-chat-body {
    //     height: calc(100vh - 100px);
    // }
}
.scroll-panel {
  padding-bottom: 10px;
  float: left;
  width: 100%;
  height: calc(100% - 55px);
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