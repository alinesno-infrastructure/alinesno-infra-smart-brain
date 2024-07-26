<!-- 意见审批  -->
<template>
  <div class="acp-dashboard" style="padding: 0px 10px !important">
    <div class="smart-container">
      <el-row>
        <el-col :span="18">
          <div class="robot-chat-windows">
            <div class="robot-chat-header">
              <div class="chat-header-title">
                数据库高级工程师 Agent
              </div>
              <div class="chat-header-desc">
                请确认生成的内容是否正确，请确认是否进入下一步 
              </div>
            </div>
            <div class="robot-chat-body">
              <!-- 聊天窗口_start -->
              <ChatList ref="chatListRef" />
              <!-- 聊天窗口_end -->
            </div>
            <div class="robot-chat-footer chat-container" style="float:left;width:100%">

              <el-row :gutter="20">
                <el-col :span="18">
                    <div class="message-input">
                      <input
                        v-model="message"
                        @input="handleInput"
                        @keydown="handleKeyDown" 
                        placeholder="请向你Agent,输入你的命令 ..." />

                      <ul v-if="showDropdown" class="mention-dropdown">
                        <li v-for="(user, index) in users" :key="index" @click="mentionUser(user)">
                          <img style="width:25px;height:25px;border-radius: 50%;position: absolute;" :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (index + 35) + '.png'" />
                          <div style="margin-left: 30px;margin-top: 5px;">
                            {{ user.roleName }}
                          </div>
                        </li>
                      </ul>

                    </div>
                </el-col>
                <el-col :span="6">

                    <el-tooltip class="box-item" effect="dark" content="回退重新生成" placement="top" >
                      <el-button type="danger" text bg size="large" @click="sendMessage">
                        <i class="fa-solid fa-paper-plane icon-btn"></i>
                      </el-button>
                    </el-tooltip>

                    <el-tooltip class="box-item" effect="dark" content="编辑生成内容" placement="top" >
                      <el-button type="warning" text bg size="large" @click="handleEditorContent()" >
                        <i class="fa-solid fa-pen-nib icon-btn"></i>
                      </el-button>
                    </el-tooltip>

                    <el-tooltip class="box-item" effect="dark" content="提交任务给Agent执行" placement="top" >
                      <el-button type="primary" text bg size="large" @click="dialogVisible = true" >
                        <i class="fa-solid fa-truck-fast icon-btn"></i>
                      </el-button>
                    </el-tooltip>

                </el-col>
              </el-row>

            </div>
          </div>
        </el-col>

        <el-col :span="6">
          <SmartServiceAgent />
        </el-col>
       
      </el-row>
    </div>

    <el-dialog v-model="dialogVisible" title="选择专家服务Agent" width="75%" :before-close="handleClose" append-to-body>

      <!-- 打开角色管理 -->
      <RoleAgent :businessId="businessId" />

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

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

  </div>
</template>

<script setup>
import { ref } from 'vue'
import ChatList from './chatList'

import {
  chatAssistantContent , 
  updateAssistantContent , 
  sendUserMessage
} from '@/api/smart/assistant/robot'

import {
  listAllRole,
} from "@/api/smart/assistant/role";

import { getParam } from '@/utils/ruoyi'

import SmartServiceAgent from './smartServiceAgent';
import RoleAgent from './agent/roleAgent'

const chatListRef = ref();
const router = useRouter();
const {proxy} = getCurrentInstance();

const businessId  = ref("1733452663532019712") ;
const editorLoading = ref(true) ;
const dialogVisible = ref(false)
const editDialogVisible = ref(false)
const currentTaskContent = ref("")

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
  dialogVisible.value = false ; 
  editDialogVisible.value = false ;
}


const message = ref('');
let users = [] ;

const showDropdown = ref(false);
const selectedUsers = ref([]);
const messageList = ref([]);

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

const sendMessage = () => {
  const output = {
    message: message.value,
    mentionedUsers: selectedUsers.value.map((user) => user.roleName),
  };
  const formattedMessage = formatMessage(message.value, selectedUsers.value);

  // 添加到列表中
  chatListRef.value.pushMessageList(formattedMessage);

  // 发送消息到后台
  handleSendUserMessage(formattedMessage) ;
  console.log(output); // Replace with your desired handling of the output

  message.value = '';
  selectedUsers.value = [];
};

/** 同步消息到后端 */
function handleSendUserMessage(formattedMessage){
  console.log('formattedMessage = ' + formattedMessage) ;
  sendUserMessage(formattedMessage).then(response => {
    chatListRef.value.pushResponseMessageList(response.data);
  })
}

const removeMention = (userId) => {
  const userIndex = selectedUsers.value.findIndex((user) => user.id === userId);
  if (userIndex !== -1) {
    selectedUsers.value.splice(userIndex, 1);
    message.value = message.value.replace(`@${users[userIndex].name}`, '');
  }
};

const filteredUsers = computed(() => {
  const lastWord = message.value.split(' ').pop().slice(1).toLowerCase();
  return users.filter((user) => user.roleName.toLowerCase().startsWith(lastWord));
});

const formatMessage = (message, selectedUsers) => {
  const words = message.split(' ');
  const formattedMessage = [];
  words.forEach((word) => {
    const user = selectedUsers.find((u) => `@${u.roleName.toLowerCase()}` === word.toLowerCase());
    if (user) {
      formattedMessage.push({ type: 'mention', username: user.roleName, id: user.id });
    } else {
      formattedMessage.push({ type: 'text', text: word });
    }
  });
  return formattedMessage;
};

// return {
//   message,
//   users,
//   showDropdown,
//   selectedUsers,
//   messageList,
//   handleInput,
//   mentionUser,
//   sendMessage,
//   removeMention,
//   filteredUsers,
// };

function handleKeyDown(event) {
  if (event.key === "Enter" && event.ctrlKey) {
    // 在这里执行你想要的操作
    console.log("Enter+Ctrl 被按下");
    sendMessage() ;
  }
}

/** 编辑生成内容 */
function handleEditorContent(){
  editDialogVisible.value = true ; 

  chatAssistantContent(businessId.value).then(response => {
    currentTaskContent.value = response.data.genContent ; 
    editorLoading.value = false ;
  })
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
function handleListAllRole(){
  listAllRole().then(response => {
    users = response.data ; 
  })
}
  
businessId.value = getParam('businessId') == null ? '1733452663532019712' : getParam('businessId') ;
console.log('businessId = ' + businessId) ;

handleListAllRole() ;

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

  input{
    width: 100%;
    height: 41px;
    padding: 10px;
    resize: none;
    background: #fafafa;
    font-size: 14px;
    border: 0px solid #ccc;
    border-radius: 5px;
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