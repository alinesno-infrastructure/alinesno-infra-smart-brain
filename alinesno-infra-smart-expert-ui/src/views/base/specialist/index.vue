<template>
  <div v-loading="loading">

    <!-- 控制列表 -->
    <el-container style="height: calc(100vh - 56px);">
      <el-aside width="330px" style="background: #fff;height:100%; padding:10px;border-right:1px solid #e6e6e6">
        <ChatSessionPanel @onSendParams="handleGetParams" />
      </el-aside>
      <el-main style="padding:0px;">
        <SmartService @onSendParams="handleGetParams" />
      </el-main>
    </el-container>

  </div>
</template>

<script setup>

import { useRouter } from "vue-router";

import SmartService from './smartService';
import ChatSessionPanel from './chatSessionPanel';

import { ref, reactive } from 'vue'

const router = useRouter()
const textarea = ref('this is a test')

const loading = ref(false)
const labelPosition = ref("top")
const checked1 = ref(false)

// do not use same name with ref
const form = reactive({
  name: '',
  region: '',
  date1: '',
  date2: '',
  delivery: false,
  type: [],
  resource: '',
  desc: '',
})

import { onMounted, onUnmounted } from "vue"

const systemContent = ref("")
const userContent = ref("")

const code = ref(
  `你现在是一名Java计算机面试官，请生成微服务架构相关的，现在需要生成题目做于Java面试题，主要题目类型如下：

- 单选题： single_choice  
- 多选题： multiple_choice  
- 填空题： fill_in_the_blank  

现在请使用yaml生成单选题5题，多选题2题，填写题3题，参考下面的示例生成yaml文件，并返回yaml格式
题目的评分标准按题目的难度来设定分数，总分是100分，请生成10道题目。
`
)
const cmRef = ref(null)
const cmOptions = {
  mode: "text/markdown"
}

const onChange = (val, cm) => {
  console.log(val)
  console.log(cm.getValue())

  userContent.value = cm.getValue()
}

const onInput = (val) => {
  console.log(val)
}

const onReady = (cm) => {
  console.log(cm.focus())
}

function openDashboard() {
  window.open('http://alinesno-infra-plat-console-ui.beta.plat.infra.linesno.com/', '_blank')
}

const handleGetParams = (params) => {
  console.log(params) // 传递给父组件的数据
  loading.value = params;
}

/** 提交按钮 */
function onSubmit() {
  updatePrompt(form.value).then(response => {
    proxy.$modal.msgSuccess("修改成功");
    open.value = false;
  });
};

onMounted(() => {
  setTimeout(() => {
    cmRef.value?.refresh()
  }, 1000)
})

onUnmounted(() => {
  cmRef.value?.destroy()
})

</script>

<style lang="css">
.CodeMirror pre.CodeMirror-line,
.CodeMirror pre.CodeMirror-line-like {
  line-height: 1.3rem !important;
}
</style>

<style lang="scss">
// .el-row {
//   margin-bottom: 20px;
// }

.el-row:last-child {
  margin-bottom: 0;
}

.el-col {
  border-radius: 4px;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}

.head-body {

  .header-avator {
    float: left;
  }

  .header-body-main {
    float: left;
  }

}

</style>