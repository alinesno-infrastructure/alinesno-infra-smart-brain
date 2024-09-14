<!-- 意见审批  -->
<template>
  <div class="acp-dashboard aip-chat-dashboard-panel" style="padding:0px !important;">
    <div class="smart-container inner-smart-container">
      <el-row>
        <el-col :span="24">
          <div class="robot-chat-windows">
            <div class="robot-chat-header">
              <div class="chat-header-title" style="padding-left: 28px;">
                <img class="aip-chat-box-title-img" :src="imagePathByPath(roleInfo.roleAvatar)" />
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

            <div class="robot-chat-body inner-robot-chat-body" v-loading="loading">
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
                              <el-button type="danger" link icon="Promotion" size="small" @click="handleBusinessIdToMessageBox(item)">选择</el-button>
                              <el-button type="primary" link icon="EditPen" size="small" @click="handleEditGenContent(item)">查看</el-button>
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
                        <i class="fa-solid fa-paper-plane icon-btn"></i>
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

import { getInfo, chatRole }from '@/api/base/im/roleChat'
import { getParam } from '@/utils/ruoyi'
import { openSseConnect , handleCloseAllSse } from "@/api/base/im/chatsse";
import { onMounted } from "vue";

const roleId = ref(null);
const roleInfo = ref({})
const message = ref('');

const innerRef = ref(null); // 滚动条的处理_starter
const scrollbarRef = ref(null);
const messageList = ref([{
    "channelId": 0,
    "roleId": "1808349647059738625",
    "accountId": 0,
    "roleType": "agent",
    "icon": "1808349647059738625",
    "name": "业务咨询客服",
    "dateTime": "2024-09-14 23:08:02",
    "chatText": "收到，任务我已经在处理，请稍等1-2分钟 :-)",
    "readerType": "html",
    "status": null,
    "businessId": "0",
    "className": null,
    "loading": false 
}]);

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
  message.value = '' ;
}

/** 读取html文本 */
function readerHtml(chatText) {
  return mdi.render(chatText);
}

// 推送消息到当前面板
const pushResponseMessageList = (message) => {
  console.log('--->>> message = ' + message);
  messageList.value.push(message) ; 

  initChatBoxScroll();
}

/** 连接sse */
function handleSseConnect(roleId){
  nextTick(() => {
    if(roleId){
      handleCloseAllSse().then(res => {

        
        let sseSource = openSseConnect(roleId) ;
        // 接收到数据
        sseSource.onmessage = function (event) {
            let text = '' ; 
            if (!event.data.includes('[DONE]')) {
              const data = JSON.parse(event.data);
              text += data.chatText ;
            }else{
              console.log('消息接收结束.')  
              pushResponseMessageList(data);
            }
        }

      })
    }
  })
}

/** 获取角色信息 */
function handleGetInfo(roleId){
  getInfo(roleId).then(res => {
    roleInfo.value = res.data
    console.log('--->>> roleInfo = ' + JSON.stringify(roleInfo.value))
  })
}

/** 发送消息 */
const sendMessage = (type) => {

  if(!message.value){
    proxy.$modal.msgError("请输入消息内容.");
    return ;
  }

  let formData = {
    roleId: roleId.value,
    content: message.value,
  }
  
  chatRole(formData , roleId.value).then(res => {
    proxy.$modal.msgSuccess("发送成功");
    pushResponseMessageList(res.data);
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

onMounted(() => {
  initChatBoxScroll();

  roleId.value = getParam('roleId')
  handleSseConnect(roleId.value)
  handleGetInfo(roleId.value);
})


// import { nextTick, ref } from 'vue'

// import ChatList from './chatList'

// --->>> 组件引入 -->>
// import ChatUploadFile from './chatUploadFile'
// import SmartServiceAgent from './smartServiceAgent';
// import AgentTaskFlow from './roleTaskFlow'
// import ChatMessageEditor from './chatMessageEditor'

// --->>> 引入方法 -->>
// import { chatAssistantContent , updateAssistantContent , chatMessage, sendUserMessage}from '@/api/base/im/robot'
// import { getChannel } from "@/api/base/im/channel";

// import { getParam } from '@/utils/ruoyi'
// import { formatMessage } from '@/utils/chat'
// import { openSseConnect , handleCloseAllSse } from "@/api/base/im/chatsse";

// --->>> 定义变量 -->>
// const chatListRef = ref();
// const router = useRouter();
// const {proxy} = getCurrentInstance();

// const loading = ref(false)
// const businessId  = ref("") ;
// const editorLoading = ref(true) ;
// const editDialogVisible = ref(false)
// const taskFlowDialogVisible = ref(false)
// const uploadChildComp = ref(null) 
// const selectedUsers = ref([]);
// const selectedBusinessId = ref([]);
// const channelUsers = ref([]);

// const message = ref('');

// const mentionOptions = ref([]);

// const mentionUser = (itemArr) => {
//   if(itemArr.length == 0){
//     channelUsers.value = [];
//     mentionOptions.value = [{}];
//     return ;
//   }
//   channelUsers.value = itemArr ;
//   mentionOptions.value = itemArr.map(item => ({
//       value: item.roleName,
//       label: item.roleName,
//       id: item.id
//   }));
// };

// 选中处理函数
// const handleSelect = (value, prefix) => {
//   console.log(value, prefix);
//   selectedUsers.value.push(value);
// };

// const handleClose = () => {
//   editDialogVisible.value = false ;
//   taskFlowDialogVisible.value = false ;
// }

/** 发送消息 */
// const sendMessage = (type) => {

//   if(!message.value){
//     proxy.$modal.msgError("请输入消息内容.");
//     return ;
//   }

//   const formattedMessage = formatMessage(message.value, channelUsers.value);

//   chatListRef.value.pushMessageList(formattedMessage);
//   handleSendUserMessage(message.value, type) ;

//   message.value = '';
//   selectedUsers.value = [];
//   selectedBusinessId.value = [] ;
// };

/** 同步消息到后端 */
// function handleSendUserMessage(message , type){

//   let channelId = getParam("channel");

//   // 只返回id列表
//   let users = selectedUsers.value.map(item => {
//     return item.id ;
//   }) 

//   sendUserMessage(message , users, selectedBusinessId.value , channelId , type).then(response => {
//     console.log("发送消息", response.data);
//     response.data.forEach(item => {
//       chatListRef.value.pushResponseMessageList(item);
//     })
//   })
// }

/** 发送消息组服务组件 */
// function handlePushResponseMessageList(item){
//   chatListRef.value.pushResponseMessageList(item);
// }

/** 上传文档文件 */
// function handleUploadFile(){
//   uploadChildComp.value.handleOpenUpload(true);
// }

// 发送消息到发送窗口
// const sendMessageToChatBox = (msg) => {
//   message.value += msg ;
// }

/** 选择业务id */
// const handleSelectPreBusinessId= (bId) => {
//     selectedBusinessId.value.push(bId);
// }

/** 获取到会话信息 */
// function handleChatMessage(channelId) {
//   if(channelId){
//     loading.value = true ;
//     chatMessage(channelId).then(response => {
//       const data = response.data ;
//       chatListRef.value.currentResponseMessageList(data); 

//       // 处理sse消息内容
//       handleSseConnect(channelId);

//       loading.value = false;
//     })
//   }
// }

/** 编辑生成内容 */
// function handleEditorContent(bId){
//   editDialogVisible.value = true ; 
//   businessId.value = bId ;

//   chatAssistantContent(bId).then(response => {
//     editorLoading.value = false ;
//   })
// }

// function keyDown(e) {
//   if(!message.value){
//     return ;
//   }
//   sendMessage('send')
//   message.value = '' ;
// }

/** 查询当前频道 */
// function handleGetChannel(channelId){
//     if(channelId){
//       getChannel(channelId).then(response => {
//         channelInfo.value = response.data ;
//       })
//     }
// }

/** 连接sse */
// function handleSseConnect(channelId){
//   nextTick(() => {
//     if(channelId){
//       handleCloseAllSse().then(res => {

//         let sseSource = openSseConnect(channelId) ;
//         // 接收到数据
//         sseSource.onmessage = function (event) {
//             if (!event.data.includes('[DONE]')) {
//               const data = JSON.parse(event.data);

//               console.log('--->>> channelId = ' + channelId + ' , data = ' + JSON.stringify(data));

//               handlePushResponseMessageList(data);
//             }else{
//               console.log('消息接收结束.')  
//             }
//         }

//       })
//     }
//   })
// }

/** 监听路由变化 */
// watch(() =>  router.currentRoute.value.path,(toPath) => {
//     const channelId = getParam("channel");

//     handleGetChannel(channelId);
//     handleChatMessage(channelId) ;

//     },{immediate: true,deep: true}
// )
  

</script>

<style lang="scss" scoped>
.inner-smart-container {
    max-width: 700px !important;

    .robot-chat-windows{
      border:0px !important;
    }

    .inner-robot-chat-body {
        height: calc(100vh - 75px);
    }
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