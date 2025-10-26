<template>
  <div class="prompt-editor-container" >
    <div style="
    display: flex;
    justify-content: space-between;
    align-items: center;">
      <span style="font-size:14px;font-weight: bold;"> 人设与回复逻辑 </span>
    </div>

    <div style="margin-top:10px;" class="prompt-editor-content">
      <code-mirror basic :lang="lang" v-model="codeVal" :style="'height:700px'" :theme="theme" :extensions="extensions" />
    </div>

    <div style="display: flex; justify-content: flex-end;">
      <el-button type="primary" text bg size="large" @click="syncPromptContent()">确认</el-button>
    </div>


  </div>
</template>

<script setup>

import CodeMirror from 'vue-codemirror6'
import { oneDark } from '@codemirror/theme-one-dark'
import { EditorView } from '@codemirror/view';
import { markdown } from '@codemirror/lang-markdown';

const { proxy } = getCurrentInstance();

// 初始化
let codeVal = ref('');

// const editorHeight = ref(props.height);

const lang = markdown() ; 

// 扩展
const extensions = [
  oneDark,
  EditorView.lineWrapping,
];

// 主题样式设置
const theme = {
  "&": {
    fontSize: "10pt",
  }
}

/** 设置Prompt */
const setPromptContent = (promptContent) => {
  codeVal.value = promptContent ;
}

/** 保存 Prompt */
const syncPromptContent = () => {
  console.log('codeValue = ' + codeVal.value)
  proxy.$emit('syncPromptContent', codeVal.value)
}

defineExpose({
  setPromptContent
})

</script>

<style>
/* required! */
.prompt-editor-content .cm-editor {
  height: calc(100% - 20px);
}

</style>