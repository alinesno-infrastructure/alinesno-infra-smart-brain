<template>
  <div class="chat-container" v-loading="loading">
    <div class="main-content">

      <div v-for="(message, index) in messages" :key="index" class="message">
        <div class="message-content">
          <el-input :rows="1" style="width:150px" v-model="message.value" placeholder="输入对话信息" class="message-role-select"></el-input>
          <el-input type="textarea" :rows="1" v-model="message.label" placeholder="输入对话信息" class="message-input-text"></el-input>
        </div>
      </div>
      <br/> 
      <br/> 

    </div>
  </div>
</template>

<script setup>

import {
  getPromptContent , 
} from "@/api/smart/brain/task";

const props = defineProps({
    currentPostId: {
      type: String,
      require: true,
    },
})

const { proxy } = getCurrentInstance();

const loading = ref(true)
const selectedRole = ref("")
const inputText = ref("")
const roles = ref([
  { value: 'system', label: '系统(system)' },
  { value: 'user', label: '用户(user)' },
  { value: 'assistants', label: '助手(assistants)' }
])
const messages = ref([])

function handleRoleChange() {
  // 处理角色切换
}

function addMessage() {
  console.log('current post id = '  + props.currentPostId);

  messages.value.push({ role: selectedRole.value, content: inputText.value });
  // messages = ref([])
  inputText.value = "";
}

function deleteMessage(index) {
  messages.value.splice(index, 1);
}

function saveJson() {
    const json = JSON.stringify(messages.value);
    console.log(json);

    updatePromptContent(json , props.currentPostId).then(response => {
      proxy.$modal.msgSuccess("修改成功");
    });
}

function handlePromptContent(){
  getPromptContent(props.currentPostId).then(response => {
    if(response.data){

      let obj = response.data ;
      // let result = "";

      // for (let key in obj) {
      //   result += key + ": " + obj[key] + "\n";
      // }

      // inputText.value = result ; 

      const result = [];

      for (let key in obj) {
        result.push({ value: key, label: obj[key] });
      }

      console.log(result);
      messages.value = result ;
    }
    loading.value = false ;
  })
}

handlePromptContent() ;

console.log('message.value = ' + messages.value) ;

</script>

<style>
.chat-container {
  display: flex;
  margin: 0 auto;
}

.sidebar {
  flex: 1;
}

.main-content {
  flex: 3;
}

.role-select {
  margin-bottom: 10px;
}

.input-text {
  margin-bottom: 10px;
}

.add-button {
  margin-bottom: 20px;
}

.message-role-select {
  margin-right: 20px;
}

.message {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.message-content {
  flex: 1;
  display: flex;
  align-items: center;
}

.message-input-text {
  flex: 1;
}

.delete-button {
  margin-left: 10px;
}

.save-button {
  margin-top: 20px;
}
</style>
