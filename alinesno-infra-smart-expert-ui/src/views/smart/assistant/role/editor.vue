<template>
  <div>
    <el-row>
      <el-col :span="8">
        <el-card shadow="never">
          <el-scrollbar style="height: calc(100vh - 450px)">
            <el-list v-for="(item, index) in prompts" :key="index" size="large">
              <el-list-item>
                <el-row style="margin-bottom:10px">
                  <el-col :span="14">
                    <el-select v-model="item.role" placeholder="Type" class="ml-2">
                      <el-option label="System" value="system"></el-option>
                      <el-option label="User" value="user"></el-option>
                      <el-option label="Assistant" value="assistant"></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="10" style="text-align: right;">
                    <el-button v-if="index == currentPromptIndex" type="danger" text bg @click="editPrompt(index)">
                      <i class="fa-solid fa-user-pen"></i>
                    </el-button>
                    <el-button v-else type="primary" text bg @click="editPrompt(index)">
                      <i class="fa-solid fa-file-pen"></i>
                    </el-button>
                    <el-button type="danger" text bg @click="removePrompt(index)">
                      <i class="fa-solid fa-trash-can"></i>
                    </el-button>
                  </el-col>
                </el-row>

              </el-list-item>
            </el-list>
          </el-scrollbar>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card shadow="never">
          <el-scrollbar>
            <el-input type="textarea" :rows="23" 
              placeholder="在这里输入你的Prompt ..."
              resize="none"
              v-model="currentPromptContent"></el-input>
          </el-scrollbar>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="never" style="text-align: right;">
      <el-button @click="addPrompt" icon="Plus">添加Prompt</el-button>
      <el-button type="success" @click="savePrompt">临时保存</el-button>
      <el-button type="primary" @click="saveToServerPrompt">更新</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

import {
  updatePromptContent
} from "@/api/smart/assistant/role";

const props = defineProps({
  currentPrompt: {
    type: Object,
    require: true,
  },
})

const {proxy} = getCurrentInstance();

const prompts = ref([
  {
    role: 'system',
    content: ''
  }
]);

// 当前选中的Prompt内容
const currentPromptIndex = ref(0);
const currentPromptContent = ref(prompts.value[0].content);

// 添加新的Prompt
function addPrompt() {
  const newPrompt = {
    role: 'system',
    content: ''
  };
  prompts.value.push(newPrompt);
  editPrompt(prompts.value.length - 1);
}

// 移除Prompt
function removePrompt(index) {
  prompts.value.splice(index, 1);
  if (currentPromptIndex.value >= prompts.value.length) {
    currentPromptIndex.value = Math.max(0, prompts.value.length - 1);
  }
  editPrompt(currentPromptIndex.value);
}

// 编辑Prompt
function editPrompt(index) {
  currentPromptIndex.value = index;
  currentPromptContent.value = prompts.value[index].content;
}

// 保存Prompt
function savePrompt() {
  prompts.value[currentPromptIndex.value].content = currentPromptContent.value;
}

// 保存Prompt
function saveToServerPrompt() {

  savePrompt();

  // 清除掉content为空的prompt
  prompts.value = prompts.value.filter(item => item.content !== '');
  console.log(JSON.stringify(prompts.value, null, 2));

  updatePromptContent(prompts.value , props.currentPrompt.id).then(res => {
    proxy.$modal.msgSuccess("保存成功");
  })
}

// 监听当前编辑的Prompt变化
watch(currentPromptIndex, newIndex => {
  editPrompt(newIndex);
});

function handlePromptContent() {
  if(props.currentPrompt && props.currentPrompt.promptContent){
    prompts.value = JSON.parse(props.currentPrompt.promptContent);
    editPrompt(0)
  }
}

handlePromptContent();
</script>

<style scoped lang="scss">
.el-card {
  border-width: 0px;
}
</style>