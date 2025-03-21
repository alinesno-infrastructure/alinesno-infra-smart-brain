<!-- 意见审批  -->
<template>
  <div class="acp-dashboard aip-chat-dashboard-panel">
    <div class="smart-container">
      <el-row>
        <el-col :span="19">
          <div class="robot-chat-windows">
            <div class="robot-chat-header">
              <div class="chat-header-title">
                <img class="aip-chat-box-title-img" :src="imagePath(channelInfo)" />
                {{ channelInfo.channelName }}
              </div>
              <div class="chat-header-desc">
                ({{ truncateString(channelInfo.channelDesc, 30) }})
              </div>
              <div class="chat-header-desc" style="float: right;margin-top: -10px;display: flex;">

                <div v-if="channelInfo.knowledgeType">
                  <img v-if="channelInfo.knowledgeType.includes('docx')"
                    src="http://data.linesno.com/dataset_icons/docx.webp" style="width: 30px;margin: 5px;" />
                  <img v-if="channelInfo.knowledgeType.includes('pdf')"
                    src="http://data.linesno.com/dataset_icons/pdf.webp" style="width: 30px;margin: 5px;" />
                </div>

                <el-button type="primary" text bg size="large" @click="taskFlowDialogVisible = true">
                  <i class="fa-solid fa-truck-fast icon-btn"></i>
                </el-button>
              </div>
            </div>

            <div class="robot-chat-body" v-loading="loading">
              <!-- 聊天窗口_start -->
              <ChatList ref="chatListRef" 
                @sendMessageToChatBox="sendMessageToChatBox"
                @executorMessage="executorMessage"
                @handleSelectPreBusinessId="handleSelectPreBusinessId" 
                @handleEditorContent="handleEditorContent" />
              <!-- 聊天窗口_end -->
            </div>

            <div class="robot-chat-footer chat-container" style="float:left;width:100%">

                <div class="message-input-box">
                  <div class="message-input">

                    <el-mention size="large" class="input-chat-box" v-model="message" :options="mentionOptions" :prefix="['@']"
                      placeholder="输入@可指定角色, #可指定输出结果" @select="handleSelect">
                      <template #label="{ item }">
                        {{ item.label }}
                      </template>
                    </el-mention>

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

                  <el-tooltip class="box-item" effect="dark" content="上传文档文件" placement="top">
                    <el-button type="primary" text bg size="large" @click="handleUploadFile">
                      <i class="fa-solid fa-file-word icon-btn"></i>
                    </el-button>
                  </el-tooltip>

                </div>

            </div>

            <div class="aigen-text-warning">
              内容由第三方 AI 生成，无法确保真实准确，仅供参考
            </div>

          </div>
        </el-col>

        <el-col :span="5" class="chat-agent-right-panel" >
          <SmartServiceAgent ref="smartServiceAgentRef" @mentionUser="mentionUser" />
        </el-col>

      </el-row>
    </div>

    <!-- 任务运行状态 -->
    <el-dialog v-model="taskFlowDialogVisible" title="频道任务运行状态" width="60%" :before-close="handleClose" destroy-on-close
      append-to-body>
      <br />
      <AgentTaskFlow />
      <template #footer>
        <span class="dialog-footer">
          <el-button type="warning" @click="taskFlowDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 材料上传界面 -->
    <ChatUploadFile ref="uploadChildComp" @handlePushResponseMessageList="handlePushResponseMessageList" />

    <!-- 文档编辑界面 -->
    <el-dialog v-model="editDialogVisible" title="任务内容编辑" width="60%" destroy-on-close append-to-body>
      <ChatMessageEditor :businessId="businessId" />
    </el-dialog>

  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue'
import { ElLoading } from 'element-plus'

import ChatList from './chatList'

// --->>> 组件引入 -->>
import ChatUploadFile from './chatUploadFile'
import SmartServiceAgent from './smartServiceAgent';
import AgentTaskFlow from './agentTaskFlow'
import ChatMessageEditor from './chatMessageEditor'

// --->>> 引入方法 -->>
import { chatAssistantContent, chatMessage, sendUserMessage , chanelSayHello } from '@/api/base/im/robot'
import { getChannel } from "@/api/base/im/channel";
import { getParam } from '@/utils/ruoyi'
import { formatMessage } from '@/utils/chat'
import { openSseConnect, handleCloseSse } from "@/api/base/im/chatsse";

const streamLoading = ref(null)

// --->>> 定义变量 -->>
const chatListRef = ref();
const router = useRouter();
const { proxy } = getCurrentInstance();

const loading = ref(false)
const businessId = ref("");
const editorLoading = ref(true);
const editDialogVisible = ref(false)
const taskFlowDialogVisible = ref(false)
const uploadChildComp = ref(null)
const selectedUsers = ref([]);
const selectedBusinessId = ref([]);
const channelUsers = ref([]);
const newChannelUsers = ref([]) // 新添加的角色

const smartServiceAgentRef = ref(null)

const message = ref('');
const channelInfo = ref({})
const mentionOptions = ref([]);

const mentionUser = (itemArr) => {
  
  if (itemArr.length == 0) {
    channelUsers.value = [];
    mentionOptions.value = [{}];
    return;
  }

  // 获取到新添加的角色，itemArr与channelUsers进行对比，将新增的角色放到新的数组newChannelUsers里面
  if(channelUsers.value.length != 0){
    newChannelUsers.value = itemArr.filter(item => !channelUsers.value.some(user => user.id === item.id));
    handleChanelSayHello(newChannelUsers.value);
  }

  channelUsers.value = itemArr;

  mentionOptions.value = itemArr.map(item => ({
    value: item.roleName,
    label: item.roleName,
    id: item.id
  }));

};

// 新添加的角色进入频道打招呼 
const handleChanelSayHello = (newChannelUserArr) =>{

  newChannelUserArr.forEach(item => {  // 消息同步到后台保存

    const formattedMessage = item.greeting?item.greeting:`您好，我是${item.roleName}` ; 
    item.greeting = formattedMessage;

    chanelSayHello(item , channelInfo.value.id).then(res => {

      const msgItem = {
        roleId: item.id,
        icon: item.roleAvatar,
        name: item.roleName,
        chatText: formattedMessage,
        loading: false,
        readerType: 'html',
        businessId: item.id ,
        status: 'completed'
      }

      chatListRef.value.pushResponseMessageList(msgItem);
    })

  }); 

}

// 选中处理函数
const handleSelect = (value, prefix) => {
  console.log(value, prefix);
  selectedUsers.value.push(value);
};

const handleClose = () => {
  editDialogVisible.value = false;
  taskFlowDialogVisible.value = false;
}

/** 发送消息 */
const sendMessage = (type) => {

  if (!message.value) {
    proxy.$modal.msgError("请输入消息内容.");
    return;
  }

  const formattedMessage = formatMessage(message.value, channelUsers.value);

  chatListRef.value.pushMessageList(formattedMessage);
  handleSendUserMessage(message.value, type);

  message.value = '';
  selectedUsers.value = [];
  selectedBusinessId.value = [];
};

/** 同步消息到后端 */
function handleSendUserMessage(message, type) {

  let channelId = getParam("channel");

  // 只返回id列表
  let users = selectedUsers.value.map(item => {
    return item.id;
  })

  streamLoading.value = ElLoading.service({
    lock: true,
    text: '任务执行中，请勿操作其它界面 ...',
    background: 'rgba(0, 0, 0, 0.2)',
  })

  sendUserMessage(message, users, selectedBusinessId.value, channelId, type).then(response => {
    console.log("发送消息", response.data);
    response.data.forEach(item => {
      chatListRef.value.pushResponseMessageList(item);
    })
  }).catch(error => {
    streamLoading.value.close();
  })
}

/** 执行脚本任务 */
function executorMessage(item){

  let channelId = getParam("channel");
  let users = [item.roleId];
  let bId = [item.businessId];
  let type = 'function';
  let message = " #"+item.businessId+" " ; 

  streamLoading.value = ElLoading.service({
    lock: true,
    text: '任务执行中，请勿操作其它界面 ...',
    background: 'rgba(0, 0, 0, 0.2)',
  })

  sendUserMessage(message, users, bId , channelId, type).then(response => {
    console.log("发送消息", response.data);
    response.data.forEach(item => {
      chatListRef.value.pushResponseMessageList(item);
    })
  }).catch(error => {
    streamLoading.value.close();
  })
}

/** 发送消息组服务组件 */
function handlePushResponseMessageList(item) {
  chatListRef.value.pushResponseMessageList(item);

  // 调用初始化滚动条的函数
  // initChatBoxScroll();
}

/** 上传文档文件 */
function handleUploadFile() {
  uploadChildComp.value.handleOpenUpload(true);
}

// 发送消息到发送窗口
const sendMessageToChatBox = (msg) => {
  message.value += msg;
}

/** 选择业务id */
const handleSelectPreBusinessId = (bId) => {
  selectedBusinessId.value.push(bId);
}

/** 获取到会话信息 */
function handleChatMessage(channelId) {
  if (channelId) {
    loading.value = true;
    chatMessage(channelId).then(response => {
      const data = response.data;
      chatListRef.value.currentResponseMessageList(data);

      // 处理sse消息内容
      handleSseConnect(channelId);

      loading.value = false;
    })
  }
}

/** 编辑生成内容 */
function handleEditorContent(bId) {
  editDialogVisible.value = true;
  businessId.value = bId;
}

/** 查询当前频道 */
function handleGetChannel(channelId) {
  if (channelId) {
    getChannel(channelId).then(response => {
      channelInfo.value = response.data;

      nextTick(() => {
        smartServiceAgentRef.value.setChannelInfo(channelInfo.value)
      })

    })
  }
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

          // console.log('sseSource.onmessage = ' + resData);

          if (resData != 'ping') {  // 非心跳消息
            const data = JSON.parse(resData);
            console.log('--->>> channelId = ' + channelId + ' , data = ' + JSON.stringify(data));
            handlePushResponseMessageList(data);
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

/** 快捷键监听事件 */
function keyDown(e) {
  if (!message.value) {
    console.log("空消息,不处理.")
    return;
  }

  if (e.ctrlKey && e.keyCode == 13) { // 生成内容 
    sendMessage('send')
    message.value = '';
  } else if (e.altKey && e.keyCode == 13) {  // 进行文案修改
    sendMessage('modify')
    message.value = '';
  } else if (e.shiftKey && e.keyCode == 13) {  // 调用方法
    sendMessage('function')
    message.value = '';
  }

}

/** 播放生成内容 */
// const handlePlayGenContent = (item) => {
//   // getSpeechLoading.value = true ;

//   if(!roleInfo.value.voicePlayStatus){
//     ElMessage.error('未开启语音播放');
//     return ;
//   }

//   item.getSpeechLoading = true ;

//   playGenContent(item).then(res => {
//     const audioBlob = new Blob([res], { type: 'audio/wav' }) // 这按照自己的数据类型来写type的内容
//     const audioUrl = URL.createObjectURL(audioBlob) // 生成url
//     const audio = new Audio(audioUrl);

//     audio.addEventListener('ended', () => {
//       console.log('音频播放完成');
//       // isPlaySpeaking.value = false 
//       item.isPlaySpeaking = false 
//     });

//     // getSpeechLoading.value = false ;
//     // isPlaySpeaking.value = true 

//     item.getSpeechLoading = false ;
//     item.isPlaySpeaking = true 

//     audio.play(); 
//   }).catch(error => {
//     // getSpeechLoading.value = false ;
//     item.getSpeechLoading = false ;
//     ElMessage.error('播放失败，请确认是否配置语音服务');
//   });
// }


onMounted(() => {
  //绑定监听事件
  window.addEventListener('keydown', keyDown)
});

onBeforeUnmount(() => {
  //销毁事件
  window.removeEventListener('keydown', keyDown, false)

  // 关闭sse连接
  let channelId = channelInfo.value.id;
  handleCloseSse(channelId).then(res => {
    console.log('关闭sse连接成功:' + channelId)
  })
});

/** 监听路由变化 */
watch(() => router.currentRoute.value.path, (toPath) => {
  const channelId = getParam("channel");

  handleGetChannel(channelId);
  handleChatMessage(channelId);

}, { immediate: true, deep: true }
)

</script>
