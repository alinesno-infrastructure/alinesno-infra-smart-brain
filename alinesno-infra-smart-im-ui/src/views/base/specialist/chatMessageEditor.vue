<template>
    <div>

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
          <el-button type="primary" @click="handleUpdateContent()">
            更新 
          </el-button>
        </div>
    </div>
</template>

<script setup>

import JsonEditorVue from 'json-editor-vue3'
import { getMessage , updateContent } from '@/api/base/im/workflow'

const { proxy } = getCurrentInstance();
const isCode = ref(true)

const props = defineProps({
  businessId: {
    type: String,
    default: null 
  }
})

const data = ref([])

const validate = async (editor) => {
  const res = await editor.validate();
  console.log("blur validate", res);
};

function onJsonChange (value) {
  console.log('value:', value)
  data.value = value
}

// 获取到业务消息
const handleGetMessage = () => {
  getMessage(props.businessId).then(res => {
    let respData = res.data ;

    if(respData.coding){
      data.value = JSON.parse(respData.codeContent[0].content);
    }else{
      isCode.value = false ;
      data.value = respData.genContent ;
    }
  })
}

const handleUpdateContent = () => {
  let formData = {
    code: isCode.value,
    content: data.value,
    businessId: props.businessId
  }
  updateContent(formData).then(res => {
    proxy.$modal.msgSuccess("修改成功");
  })
}

handleGetMessage();

</script>

<style scoped lang="scss">
.editor {
  height: 500px;
  width: 100%;
}
.submit-button-group {
  margin-top: 40px;
  margin-bottom: 20px;
  width: 100%;
  text-align: right;
}
</style>


