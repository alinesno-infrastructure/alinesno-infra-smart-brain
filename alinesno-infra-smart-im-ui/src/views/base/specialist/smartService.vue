<!-- 意见审批  -->
<template>
  <div class="acp-dashboard aip-chat-dashboard-panel">
    <div class="smart-container">
      <el-row>
        <el-col :span="17">
          <div class="robot-chat-windows">
            <div class="robot-chat-header">
              <div class="chat-header-title" style="padding-left: 28px;">
                <img class="aip-chat-box-title-img" :src="imagePath(channelInfo)" />
                {{ channelInfo.channelName }} 
              </div>
              <div class="chat-header-desc">
                ({{ truncateString(channelInfo.channelDesc,30) }}) 
              </div>
              <div class="chat-header-desc" style="float: right;margin-top: -10px;">
                  <el-button type="primary" text bg size="large" @click="taskFlowDialogVisible = true" >
                    <i class="fa-solid fa-truck-fast icon-btn"></i>
                  </el-button>
              </div>
            </div>

            <div class="robot-chat-body" v-loading="loading">
              <!-- 聊天窗口_start -->
              <ChatList ref="chatListRef" 
                @sendMessageToChatBox="sendMessageToChatBox" 
                @handleSelectPreBusinessId="handleSelectPreBusinessId" 
                @handleEditorContent="handleEditorContent" />
              <!-- 聊天窗口_end -->
            </div>

            <div class="robot-chat-footer chat-container" style="float:left;width:100%">

              <el-row :gutter="20">
                <el-col :span="18">
                    <div class="message-input">

                        <el-mention
                            class="input-chat-box"
                            @keydown.ctrl.enter.prevent="keyDown"
                            v-model="message"
                            :options="mentionOptions"
                            :prefix="['@']"
                            placeholder="输入@可指定角色, #可指定输出结果"
                            @select="handleSelect"
                          >
                          <template #label="{ item }">
                            {{ item.label }}
                          </template>
                        </el-mention> 

                    </div>
                </el-col>

                <el-col :span="6" style="text-align: right;">

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

                    <el-tooltip class="box-item" effect="dark" content="上传文档文件" placement="top" >
                      <el-button type="primary" text bg size="large" @click="handleUploadFile" >
                        <i class="fa-solid fa-file-word icon-btn"></i>
                      </el-button>
                    </el-tooltip>

                </el-col>
              </el-row>

            </div>
          </div>
        </el-col>

        <el-col :span="7">
          <SmartServiceAgent @mentionUser="mentionUser" />
        </el-col>
       
      </el-row>
    </div>

    <!-- 任务运行状态 -->
    <el-dialog v-model="taskFlowDialogVisible" title="频道任务运行状态" width="60%" :before-close="handleClose" destroy-on-close append-to-body>
      <br/>
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
    <el-dialog v-model="editDialogVisible" title="任务内容自定义界面" width="60%" destroy-on-close append-to-body>
      <ChatMessageEditor />
    </el-dialog>

  </div>
</template>

<script setup>

import { nextTick, ref } from 'vue'
import ChatList from './chatList'

// --->>> 组件引入 -->>
import ChatUploadFile from './chatUploadFile'
import SmartServiceAgent from './smartServiceAgent';
import AgentTaskFlow from './agentTaskFlow'
import ChatMessageEditor from './chatMessageEditor'

// --->>> 引入方法 -->>
import { chatAssistantContent , updateAssistantContent , chatMessage, sendUserMessage}from '@/api/base/im/robot'
import { getChannel } from "@/api/base/im/channel";
import { getParam } from '@/utils/ruoyi'
import { formatMessage } from '@/utils/chat'
import { openSseConnect , handleCloseAllSse } from "@/api/base/im/chatsse";

// --->>> 定义变量 -->>
const chatListRef = ref();
const router = useRouter();
const {proxy} = getCurrentInstance();

const loading = ref(false)
const businessId  = ref("") ;
const editorLoading = ref(true) ;
const editDialogVisible = ref(false)
const taskFlowDialogVisible = ref(false)
const currentTaskContent = ref("")
const uploadChildComp = ref(null) 
const selectedUsers = ref([]);
const selectedBusinessId = ref([]);
const channelUsers = ref([]);

const message = ref('');
const channelInfo = ref({})
const mentionOptions = ref([]);

const mentionUser = (itemArr) => {
  if(itemArr.length == 0){
    channelUsers.value = [];
    mentionOptions.value = [{}];
    return ;
  }
  channelUsers.value = itemArr ;
  mentionOptions.value = itemArr.map(item => ({
      value: item.roleName,
      label: item.roleName,
      id: item.id
  }));
};

// 选中处理函数
const handleSelect = (value, prefix) => {
  console.log(value, prefix);
  selectedUsers.value.push(value);
};

const handleClose = () => {
  editDialogVisible.value = false ;
  taskFlowDialogVisible.value = false ;
}

/** 发送消息 */
const sendMessage = (type) => {

  if(!message.value){
    proxy.$modal.msgError("请输入消息内容.");
    return ;
  }

  const formattedMessage = formatMessage(message.value, channelUsers.value);

  chatListRef.value.pushMessageList(formattedMessage);
  handleSendUserMessage(message.value, type) ;

  message.value = '';
  selectedUsers.value = [];
  selectedBusinessId.value = [] ;
};

/** 同步消息到后端 */
function handleSendUserMessage(message , type){

  let channelId = getParam("channel");

  // 只返回id列表
  let users = selectedUsers.value.map(item => {
    return item.id ;
  }) 

  sendUserMessage(message , users, selectedBusinessId.value , channelId , type).then(response => {
    console.log("发送消息", response.data);
    response.data.forEach(item => {
      chatListRef.value.pushResponseMessageList(item);
    })
  })
}

/** 发送消息组服务组件 */
function handlePushResponseMessageList(item){
  chatListRef.value.pushResponseMessageList(item);
}

/** 上传文档文件 */
function handleUploadFile(){
  uploadChildComp.value.handleOpenUpload(true);
}

// 发送消息到发送窗口
const sendMessageToChatBox = (msg) => {
  message.value += msg ;
}

/** 选择业务id */
const handleSelectPreBusinessId= (bId) => {
    selectedBusinessId.value.push(bId);
}

/** 获取到会话信息 */
function handleChatMessage(channelId) {
  if(channelId){
    loading.value = true ;
    chatMessage(channelId).then(response => {
      const data = response.data ;
      chatListRef.value.currentResponseMessageList(data); 

      // 处理sse消息内容
      handleSseConnect(channelId);

      loading.value = false;
    })
  }
}

/** 编辑生成内容 */
function handleEditorContent(bId){
  editDialogVisible.value = true ; 
  businessId.value = bId ;

  chatAssistantContent(bId).then(response => {
    editorLoading.value = false ;
  })
}

function keyDown(e) {
  if(!message.value){
    return ;
  }
  sendMessage('send')
  message.value = '' ;
}

/** 查询当前频道 */
function handleGetChannel(channelId){
    if(channelId){
      getChannel(channelId).then(response => {
        channelInfo.value = response.data ;
      })
    }
}

/** 连接sse */
function handleSseConnect(channelId){
  nextTick(() => {
    if(channelId){
      handleCloseAllSse().then(res => {

        let sseSource = openSseConnect(channelId) ;
        // 接收到数据
        sseSource.onmessage = function (event) {
            if (!event.data.includes('[DONE]')) {
              const data = JSON.parse(event.data);

              console.log('--->>> channelId = ' + channelId + ' , data = ' + JSON.stringify(data));

              handlePushResponseMessageList(data);
            }else{
              console.log('消息接收结束.')  
            }
        }

      })
    }
  })
}

/** 监听路由变化 */
watch(() =>  router.currentRoute.value.path,(toPath) => {
    const channelId = getParam("channel");

    handleGetChannel(channelId);
    handleChatMessage(channelId) ;

    },{immediate: true,deep: true}
)
  

</script>
