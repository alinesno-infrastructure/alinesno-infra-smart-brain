<template>
  <div class="cm-container">
    <code-mirror 
      basic 
      :lang="lang" 
      v-model="codeVal" 
      :style="'height:200px'" 
      :theme="theme"
      :extensions="extensions" />
  </div>
</template>

<script setup>

import CodeMirror from 'vue-codemirror6'
import { oneDark } from '@codemirror/theme-one-dark'
import { markdown } from '@codemirror/lang-markdown';

const dialogVisible = ref(false)
const cloneContent = ref('')

const props = defineProps({
  height: {
    type: String,
    default: '600px',
  },
});

// 初始化
let codeVal = ref('');
const editorHeight = ref(props.height);
const lang = markdown() ; 

// 扩展
const extensions = [oneDark];

// 主题样式设置
const theme = {
  "&": {
    fontSize: "10pt",
  }
}

const calculateFirstSectionHeight = () => {
  const windowHeight = window.innerHeight;
  const offset = 280;
  editorHeight.value = `${windowHeight - offset}px`;
  console.log(editorHeight.value); //打印高度
}

const submitDialog = () => {
  codeVal.value = cloneContent.value
  dialogVisible.value = false
}

/**
 * 获取到codeValue
 */
function getRawScript() {
  console.log('codeVal.value = '+ codeVal.value);
  return codeVal.value;
}

/** 设置值  */
function setRawScript(val) {
  console.log('setRawScript = '+ val);
  if(val){
    return codeVal.value = val;
  }
}

onMounted(() => {
  calculateFirstSectionHeight();
  window.addEventListener('resize', calculateFirstSectionHeight);
});

/** 打开编辑器 */
// function openCodemirrorDialog() {
//   cloneContent.value = codeVal.value
//   dialogVisible.value = true
// }

defineExpose({
  getRawScript,
  setRawScript
})

</script>

<style>
/* required! */
.cm-editor {
  height: 100% ;
}

/* .full-eidtor.cm-container {
  height: calc(100vh - 190px);
} */

.cm-container {
  width: 100%;
}

/* .cm-container .function-CodemirrorEditor__footer {
  position: absolute;
  bottom: 5px;
  right: 0px;
} */

</style>
