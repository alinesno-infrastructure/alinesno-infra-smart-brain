<template>
    <div style="width:100%">
        <div class="cm-container">

            <code-mirror 
                basic 
                :lang="lang" 
                v-model="data" 
                :extensions="extensions"
                style="height: 680px;" 
                :theme="theme"/>

        </div>
    </div>
</template>

<script setup>

import CodeMirror from 'vue-codemirror6'
import { oneDark } from '@codemirror/theme-one-dark'
import { markdown } from '@codemirror/lang-markdown';
import { EditorView } from '@codemirror/view'; // 从 @codemirror/view 导入 EditorView

const lang = markdown();
const extensions = [
  oneDark,
  EditorView.lineWrapping // 使用 EditorView.lineWrapping
];

// 主题样式设
const theme = {
  "&": {
    fontSize: "9.5pt",
  }
}

const props = defineProps({
  businessId: {
    type: String,
    default: null 
  }
})

const data = ref('')

/** 设置数据内容 */
const setData = (content) => {
  data.value = content ?? '未生成内容.'; // 当content为null或undefined时使用空字符串
  console.log('data.value  = ' + data.value )
}

/** 获取数据内容 */
const getData = () => {
  return data.value
}

// 对父类暴露方法
defineExpose({
  setData,
  getData
})

</script>

<style scoped lang="scss">
.submit-button-group {
  margin-top: 20px;
  margin-bottom: 0px;
  width: 100%;
  text-align: right;
}
</style>

<style >
/* required! */
.cm-editor {
  height: 100%;
}

.cm-container{
  width:100%;
}
</style>

