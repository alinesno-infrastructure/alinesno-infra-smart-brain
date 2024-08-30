<!-- 意见审批  -->
<template>
  <div class="acp-dashboard" style="padding: 0px 10px !important">
    <div class="smart-container">
      <el-row>
        <el-col :span="17">
          <div class="robot-chat-windows">
            <div class="robot-chat-header">
              <div class="chat-header-title" style="padding-left: 28px;">
                <img style="width: 25px;height: 25px;border-radius: 5px;position: absolute;left: 10px;" :src="imagePath(channelInfo)" />
                {{ channelInfo.channelName }} 
              </div>
              <div class="chat-header-desc">
                ({{ channelInfo.channelDesc }}) 
              </div>
              <div class="chat-header-desc" style="float: right;margin-top: -10px;">
                  <el-button type="primary" text bg size="large" @click="taskFlowDialogVisible = true" >
                    <i class="fa-solid fa-truck-fast icon-btn"></i>
                  </el-button>
              </div>
            </div>

            <div class="robot-chat-body" v-loading="loading">
              <!-- 聊天窗口_start -->
              <ChatList ref="chatListRef" @sendMessageToChatBox="sendMessageToChatBox" @handleEditorContent="handleEditorContent" />
              <!-- 聊天窗口_end -->
            </div>

            <div class="robot-chat-footer chat-container" style="float:left;width:100%">

              <el-row :gutter="20">
                <el-col :span="18">
                    <div class="message-input">
                      <textarea
                        class="input-chat-box"
                        v-model="message"
                        @input="handleInput"
                        @keydown="handleKeyDown" 
                        placeholder="请向你Agent,输入你的命令 ..." />

                      <ul v-if="showDropdown" class="mention-dropdown">
                        <li v-for="(user, index) in users" :key="index" @click="mentionUser(user)">
                          <!-- <img style="width:25px;height:25px;border-radius: 5px;position: absolute;" :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (index + 35) + '.png'" /> -->
                          <img style="width:30px;height:30px;border-radius: 5px;position: absolute;" :src="imagePath(user)" />
                          <div style="margin-left: 30px;margin-top: 5px;">
                            {{ user.roleName }}
                          </div>
                        </li>
                      </ul>

                    </div>
                </el-col>

                <el-col :span="6" style="text-align: right;">

                    <el-tooltip class="box-item" effect="dark" content="确认发送指令给Agent，快捷键：Enter+Ctrl" placement="top" >
                      <el-button type="danger" text bg size="large" @click="sendMessage">
                        <i class="fa-solid fa-paper-plane icon-btn"></i>
                      </el-button>
                    </el-tooltip>

                    <el-tooltip class="box-item" effect="dark" content="审批重新生成" placement="top" >
                      <el-button type="warning" text bg size="large" @click="sendMessage('replay')" >
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

    
    <el-dialog v-model="editDialogVisible" title="编辑生成内容" width="60%" :before-close="handleClose" append-to-body>

      <!-- 编辑生成的内容 -->
      <el-form :model="form" ref="ChainRef" v-loading="editorLoading" label-width="0px">
        <el-row>
            <el-col :span="24">
              <el-form-item prop="currentTaskContent">
                  <el-input rows="20" resize="none" type="textarea" v-model="currentTaskContent" placeholder="请输入任务名称" maxlength="50" />
              </el-form-item>
            </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="submitAssistantContentForm">更新</el-button>
          <el-button @click="editDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 任务运行状态 -->
    <el-dialog v-model="taskFlowDialogVisible" title="频道任务运行状态" width="60%" :before-close="handleClose" destroy-on-close append-to-body>
      <AgentTaskFlow />
      <template #footer>
        <span class="dialog-footer">
          <el-button type="warning" @click="taskFlowDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <ChatUploadFile ref="uploadChildComp" @handlePushResponseMessageList="handlePushResponseMessageList" />

  </div>
</template>

<script setup>
import { ref } from 'vue'
import Cookies from 'js-cookie'
import ChatList from './chatList'
import ChatUploadFile from './chatUploadFile'

import {
  chatAssistantContent , 
  updateAssistantContent , 
  chatMessage,
  sendUserMessage,
  getTaskNotice
} from '@/api/base/im/robot'

import {
  getChannel , 
  closeChannelSSE 
} from "@/api/base/im/channel";

import {
  listAllUser,
} from "@/api/base/im/user";

import { getParam } from '@/utils/ruoyi'

import SmartServiceAgent from './smartServiceAgent';
import AgentTaskFlow from './agentTaskFlow'

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

const data = reactive({
   form: {},
   queryParams: {
      pageNum: 1,
      pageSize: 10,
      dbName: undefined,
      dbDesc: undefined
   },
   rules: {
      dbName: [{ required: true, message: "名称不能为空", trigger: "blur" }] , 
      jdbcUrl: [{ required: true, message: "连接不能为空", trigger: "blur" }],
      dbType: [{ required: true, message: "类型不能为空", trigger: "blur" }] , 
      dbUsername: [{ required: true , message: "用户名不能为空", trigger: "blur"}],
      dbPasswd: [{ required: true, message: "密码不能为空", trigger: "blur" }] , 
      dbDesc: [{ required: true, message: "备注不能为空", trigger: "blur" }] 
   }
});

const { queryParams, form, rules } = toRefs(data);

const handleClose = () => {
  // dialogVisible.value = false ; 
  editDialogVisible.value = false ;
  taskFlowDialogVisible.value = false ;
}


const message = ref('');
let users = [] ;

const channelInfo = ref({
  icon: '1746473582053335042' ,
  channelName: '公共频道' , 
  channelDesc : '公共频道' , 
})
const showDropdown = ref(false);
const selectedUsers = ref([]);

const handleInput = () => {
  const lastWord = message.value.split(' ').pop();
  if (lastWord.startsWith('@') && lastWord.length > 1) {
    showDropdown.value = true;
  } else {
    showDropdown.value = false;
  }
};

const mentionUser = (user) => {
  const mentionedText = `@${user.roleName}`;
  message.value = message.value.replace(/@\S*$/, '');
  message.value += mentionedText + ' ';
  selectedUsers.value.push(user);
  showDropdown.value = false;
};

const sendMessage = (type) => {

  // 判断是否有内容
  if(!message.value){
    return ;
  }

  const output = {
    message: message.value,
    mentionedUsers: selectedUsers.value.map((user) => user.roleName),
  };
  const formattedMessage = formatMessage(message.value, selectedUsers.value);

  // 添加到列表中
  chatListRef.value.pushMessageList(formattedMessage);

  // 发送消息到后台
  handleSendUserMessage(formattedMessage, type) ;

  message.value = '';
  selectedUsers.value = [];
};

/** 显示图片 */
function imagePath(row){
  let roleAvatar = '1746435800232665090' ; 
  if(row.icon){
    roleAvatar = row.icon; 
  }
  return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
}

/** 同步消息到后端 */
function handleSendUserMessage(formattedMessage , type){
  const channelId = getParam("channel");

  sendUserMessage(formattedMessage , channelId , type).then(response => {
    chatListRef.value.pushResponseMessageList(response.data);
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

const removeMention = (userId) => {
  const userIndex = selectedUsers.value.findIndex((user) => user.id === userId);
  if (userIndex !== -1) {
    selectedUsers.value.splice(userIndex, 1);
    message.value = message.value.replace(`@${users[userIndex].name}`, '');
  }
};

//子组件使用使用父组件函数
const sendMessageToChatBox = (msg) => {
  message.value += msg ; 
}

const filteredUsers = computed(() => {
  const lastWord = message.value.split(' ').pop().slice(1).toLowerCase();
  return users.filter((user) => user.roleName.toLowerCase().startsWith(lastWord));
});

const formatMessage = (message, selectedUsers) => {
  const words = message.split(' ');
  const formattedMessage = [];
  words.forEach((word) => {

  if (word.startsWith('@')) {
    const user = selectedUsers.find((u) => `@${u.roleName.toLowerCase()}` === word.toLowerCase());
      if (user) {
          formattedMessage.push({ type: 'mention', username: user.roleName, id: user.id });
      } else {
          formattedMessage.push({ type: 'text', text: word });
      }
    } else if (word.startsWith('#')) {
      // Assuming the entire word after # is the order ID
      const orderId = word.slice(1);
      formattedMessage.push({ type: 'business', businessId: orderId });
    } else {
      formattedMessage.push({ type: 'text', text: word });
    }

  });
  return formattedMessage;
};

/** 点击事件 */
function handleKeyDown(event) {
  if (event.key === "Enter" && event.ctrlKey) {
    // 在这里执行你想要的操作
    sendMessage() ;
  }
}

/** 获取到会话信息 */
function handleChatMessage(channelId) {

  // const channelId = getParam("channel");

  if(channelId){
    loading.value = true ;
    chatMessage(channelId).then(response => {
      // messageList.value = response.data;
      // initChatBoxScroll();
      const data = response.data ;
      chatListRef.value.currentResponseMessageList(data); 
      loading.value = false;
    })
  }

}

/** 编辑生成内容 */
function handleEditorContent(bId){
  editDialogVisible.value = true ; 

  console.log('bId = ' + bId) ;
  businessId.value = bId ;

  chatAssistantContent(bId).then(response => {
    currentTaskContent.value = response.data.assistantContent; 
    editorLoading.value = false ;
  })
}

/** 查询当前频道 */
function handleGetChannel(channelId){
    // const channelId = getParam("channel");
    if(channelId){
      getChannel(channelId).then(response => {
        channelInfo.value = response.data ;
      })
    }
}

/** 提交流程按钮 */
function submitAssistantContentForm() {
  proxy.$refs["ChainRef"].validate(valid => {

    if (valid) {
      updateAssistantContent(businessId.value , currentTaskContent.value).then(response => {
        proxy.$modal.msgSuccess("更新成功");
      });
    }
  });
};

/** 获取到所有角色 */
function handlelistAllUser(){
  listAllUser().then(response => {
    users = response.data ; 
  })
}

/** 获取到当前执行中的任务消息 */
function handleGetTaskNotice(){

  const channelId = getParam("channel");

  getTaskNotice().then(response => {

    const data = response.data ;
    if(data && data.length > 0){
      for(let i = 0 ; i < data.length ; i ++){
        const messageChannelId = data[i].channelId ; 

        if(parseInt(channelId) == messageChannelId){
          chatListRef.value.pushResponseMessageList(data[i]);
        }
      }
    }

  })
}

/** 连接sse后台 */
let evtSource = ref(null) ;

const initEventSource = () =>{
  if (typeof (EventSource) !== 'undefined') {

    let ssePath = import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/sseChannelTask/createSseConnect?type=message" ; 
    evtSource = new EventSource(ssePath , { withCredentials: false }) // 后端接口，要配置允许跨域属性

    // 与事件源的连接刚打开时触发
    evtSource.onopen = function(e){
      console.log(e);
    }

    // 当从事件源接收到数据时触发
    evtSource.onmessage = function(e){
      console.log(e.data);

      let channelId = getParam("channel");
      console.log('channelId = ' + channelId) ; 

      if(e.data){
        const data = JSON.parse(e.data) ; 
        if(data && data.length > 0){
          for(let i = 0 ; i < data.length ; i ++){
            const messageChannelId = data[i].channelId ; 

            if(parseInt(channelId) == messageChannelId){
              chatListRef.value.pushResponseMessageList(data[i]);
            }
          }
        }
      }

      // initChatBoxScroll();
    }
    // 与事件源的连接无法打开时触发
    evtSource.onerror = function(e){
      console.log(e);
      evtSource.close(); // 关闭连接
    }
    // 也可以侦听命名事件，即自定义的事件
    evtSource.addEventListener('msg', function(e) {
      console.log(e.data)
    })
  } else {
    console.log('当前浏览器不支持使用EventSource接收服务器推送事件!');
  }
  
}

// onMounted(() => {
//   // 在组件加载完成后执行的代码
//   console.log('Component mounted');
//   initEventSource() ;
// });

// onBeforeUnmount(() => {
//   // 在组件卸载之前执行的代码
//   console.log('Component about to be unmounted');
//   let channelId = getParam("channel");
//   console.log('channelId = ' + channelId) ; 

//   closeChannelSSE(channelId).then(resp => {
//     console.log('close channel = ' + channelId) ;
//   })
// });

/** 获取定时任务服务 */
let timer = null;
onMounted(() => {
   timer = setInterval(() => {
     handleGetTaskNotice() ;
   }, 5*1000);
})

/** 任务实例销毁 */
onBeforeUnmount(() => {
   clearInterval(timer)
   timer = null;
})

/** 监听路由变化 */
watch(() =>  router.currentRoute.value.path,
    (toPath) => {
    //要执行的方法
    const channelId = getParam("channel");

    // initEventSource() ;

    handleGetChannel(channelId);
    handleChatMessage(channelId) ;
      
    },{immediate: true,deep: true}
)
  
handlelistAllUser() ;

</script>

<style lang="scss" scoped>
.icon-btn{
  font-size: 20px;
}

.chat-container {

  padding: 10px;

  .message-input {
    position: relative;
    margin-top: 8px ;
  }

  .input-chat-box {
    width: 100%;
    height: 41px;
    padding: 10px;
    resize: none;
    background: #fafafa;
    font-size: 14px;
    border: 0px solid #ccc;
    border-radius: 5px;
    outline:none;
    outline:0;
  }

  .mention-dropdown {
    max-height: 250px;
    overflow-y: auto;
    position: absolute;
    top: -258px;
    left: 0;
    z-index: 1;
    list-style-type: none;
    padding: 0;
    width: 250px;
    margin: 4px 0;
    background-color: #fff;
    border: 1px solid #e6e6e6 ;
    border-radius: 5px;
  }

  .mention-dropdown li {
    padding: 8px 8px;
    cursor: pointer;
    font-size: 14px;
    color: #333;
    background: #fafafa;
    margin: 5px;
  }

  button {
    margin-top: 8px;
  }

  .message-list {
    margin-top: 20px;
  }

  .message {
    margin-bottom: 8px;
  }

  .mention {
    color: #007bff;
    font-weight: bold;
    margin-right: 5px;
  }
}
</style>
