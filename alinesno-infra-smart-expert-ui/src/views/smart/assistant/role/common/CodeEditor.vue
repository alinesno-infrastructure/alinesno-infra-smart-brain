<template>
    <div class="cm-container">
      <code-mirror 
        basic 
        :lang="lang" 
        v-model="codeVal" 
        :style="'height:' + props.height " 
        :theme="theme"
        :extensions="extensions" />
    </div>
</template>

<script setup>

import CodeMirror from 'vue-codemirror6'
import { oneDark } from '@codemirror/theme-one-dark'
import { python } from '@codemirror/lang-python';

import { json } from '@codemirror/lang-json';
import { sql } from '@codemirror/lang-sql';
import { yaml } from '@codemirror/lang-yaml';
import { markdown } from '@codemirror/lang-markdown';

const router = useRouter();
const { proxy } = getCurrentInstance();

const props = defineProps({
  lang: {
    type: String,
    default: 'python' , 
  },
  aa: {
    type: String,
    default: 'python' , 
  },
  height: {
    type: String,
    default: '400px',
  },
});

// 初始化
let codeVal = ref('');

const lang = props.lang == 'python'? python(): 
  props.lang == 'json'?json():
  props.lang == 'yaml'?yaml():
  props.lang == 'sql'?sql():
  props.lang == 'markdown'?markdown():
  python() ;

console.log('aa = ' + props.aa +  ' , props.lang  = ' + props.lang  + ' , lang = ' + lang);

// 扩展
const extensions = [oneDark];

// 主题样式设置
const theme = {
  "&": {
    fontSize: "9.5pt",
  }
}

/**
 * 获取到codeValue
 */
function getRawScript(){
  return codeVal.value ;
}

/** 设置值  */
function setRawScript(val){
  return codeVal.value = val ;
}

defineExpose({
  getRawScript , 
  setRawScript
})

</script>

<style >
/* required! */
.cm-editor {
  height: 100%;
}

.cm-container{
  width:100%;
}
</style>