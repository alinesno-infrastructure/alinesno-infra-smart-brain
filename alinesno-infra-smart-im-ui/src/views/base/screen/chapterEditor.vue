<template>
    <div style="width:100%">

        <div class="cm-container">
          <!-- <code-mirror 
            :lang="lang" 
            :theme="theme" 
            :style="'height:600px;'" 
            :extensions="extensions"
            v-model="data" /> -->

            <code-mirror 
                basic 
                :lang="lang" 
                v-model="data" 
                :extensions="extensions"
                style="height: 630px;" 
                :theme="theme"/>

        </div>


        <!--
        <div>
          <el-input type="textarea" rows="30" resize="none" v-model="data" />
        </div>

        <div v-if="!isCode">
          <el-input type="textarea" rows="20" resize="none" v-model="data" />
        </div>
        <div v-if="isCode">
          <JsonEditorVue class="editor" 
            :show-btns="true"
            :language="zh-CN"
            :expandedOnStart="true"
            :currentMode="'tree'"
            :modeList="['tree', 'form', 'view']"
            @blur="validate"
            @change="onJsonChange"
            v-model="data" />
        </div>
        <div class="submit-button-group">
          <el-button type="primary" size="large" @click="handleUpdateContent()">
            更新 
          </el-button>
        </div>
        -->
        
    </div>
</template>

<script setup>

// import JsonEditorVue from 'json-editor-vue3'
import { getMessage , updateContent } from '@/api/base/im/workflow'

import CodeMirror from 'vue-codemirror6'
import { oneDark } from '@codemirror/theme-one-dark'
import { markdown } from '@codemirror/lang-markdown';
import { nextTick } from 'vue';

const lang = markdown();
const extensions = [oneDark];
// 主题样式设置
const theme = {
  "&": {
    fontSize: "9.5pt",
  }
}

const { proxy } = getCurrentInstance();
const isCode = ref(true)

const props = defineProps({
  businessId: {
    type: String,
    default: null 
  }
})

const data = ref('')

// const validate = async (editor) => {
//   const res = await editor.validate();
//   console.log("blur validate", res);
// };

// function onJsonChange (value) {
//   console.log('value:', value)
//   data.value = value
// }

// 获取到业务消息
const handleGetMessage = () => {
  getMessage(props.businessId).then(res => {
    let respData = res.data ;

    // if(respData.coding){
    //   data.value = JSON.parse(respData.codeContent[0].content);
    // }else{
    //   isCode.value = false ;
    //   data.value = respData.genContent ;
    // }

    data.value = respData.content ;
  })
}

const handleUpdateContent = () => {
  let formData = {
    code: false ,
    content: data.value,
    businessId: props.businessId
  }
  updateContent(formData).then(res => {
    proxy.$modal.msgSuccess("修改成功");
  })
}

nextTick(() => {
  handleGetMessage();
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

