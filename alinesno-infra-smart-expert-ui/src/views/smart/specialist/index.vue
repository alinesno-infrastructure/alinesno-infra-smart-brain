<template>
  <div v-loading="loading">
    <el-row>
      <el-col :span="24" style="border-bottom: 1px solid rgb(229, 229, 229);height: 55px;">
        <div class="flex items-center" style="padding: 9px;border-radius: 5px;color: #242e42;">

          <div class="head-body" style="margin-top: 5px;margin:auto;width:100%">

            <div class="header-avatar" style="float: left;margin-right: 10px;">
              <span class="el-avatar el-avatar--square mr-4" style="--el-avatar-size: 32px;margin-left:10px;float: left;">
                <img src="http://portal.infra.linesno.com/logo.png" style="object-fit: cover;background-color: #fff;">
              </span>
              <div class="header-body-main" style="margin-left:10px;margin-top: 5px;float: left;font-weight: bold;">
                <span class="text-sm mr-2" style="color: var(--el-text-color-regular);">AIP智能体专家</span>
              </div>
              <router-link target="_blank" to="/dashboard">
                <el-button type="primary" icon="House" text bg size="default" style="margin-top: 2px;margin-left: 20px;">管理台</el-button>
              </router-link>
            </div>

            <div class="header-avatar" style="float: right;margin-right: 10px;">
              <span class="el-avatar el-avatar--circle mr-3" style="--el-avatar-size: 32px;margin-left:10px;">
                <img src="https://foruda.gitee.com/avatar/1676897721015308137/41655_landonniao_1656075872.png"
                  style="object-fit: cover;">
              </span>
              <div class="header-body-main" style="float: left;margin-left:10px;margin-top: 10px;font-size: 13px;">
                <span class="text-sm mr-2" style="color: var(--el-text-color-regular);">罗小东(数据库高级工程师)</span>
              </div>
            </div>

            <div class="header-body-main" style="float: right;margin-top: 10px;font-size: 13px;">
              <span class="text-sm mr-2" style="color: var(--el-text-color-regular);">业务标识 : 1732055435930882069 <el-icon>
                  <CopyDocument />
                </el-icon> 
              </span>
              <span class="text-sm mr-2" style="margin-left:10px;color: var(--el-text-color-regular);">角色标识 : uvaWo9Es
                <el-icon>
                  <CopyDocument />
                </el-icon> 
              </span>
            </div>

          </div>

        </div>
      </el-col>
    </el-row>

    <el-container>
      <el-aside width="300px" style="background: #fff;padding:10px;">
        <ChatSessionPanel @onSendParams="handleGetParams" />
      </el-aside>
      <el-main style="padding:0px;">
        <SmartService @onSendParams="handleGetParams" />
      </el-main>
    </el-container>


  </div>
</template>

<script setup>
// import TheWelcome from '../components/TheWelcome.vue'

import { useRouter } from "vue-router";
import SmartService from './smartService';
import ChatSessionPanel from './chatSessionPanel';

import { ref, reactive } from 'vue'

const router = useRouter()
const textarea = ref('this is a test')

const loading = ref(false)
const labelPosition = ref("top")
const checked1 = ref(false)
const engineers = ref([{
  "icon": "",
  "name": "张三",
  "id": "",
  "title": "技术负责人"
},
{
  "icon": "",
  "name": "李大四",
  "id": "",
  "title": "需求工程师"
},
{
  "icon": "",
  "name": "王五",
  "id": "",
  "title": "软件工程师"
},
{
  "icon": "",
  "name": "赵六",
  "id": "",
  "title": "系统架构师"
},
{
  "icon": "",
  "name": "刘小七",
  "id": "",
  "title": "测试工程师"
},
{
  "icon": "",
  "name": "陈一八",
  "id": "",
  "title": "数据工程师"
},
{
  "icon": "",
  "name": "钱九",
  "id": "",
  "title": "网络工程师"
},
{
  "icon": "",
  "name": "孙十",
  "id": "",
  "title": "安全工程师"
},
{
  "icon": "",
  "name": "周一",
  "id": "",
  "title": "前端工程师"
},
]);

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
// import "codemirror/mode/markdown/markdown.js"
// import Codemirror from "codemirror-editor-vue3"

// import type { CmComponentRef } from "codemirror-editor-vue3"
// import type { Editor, EditorConfiguration } from "codemirror"

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

function openDashboard(){
  // router.push('/dashboard')
  window.open(href.href + '/dashboard', '_blank')
}

const handleGetParams = (params) => {
  console.log(params) // 传递给父组件的数据
  loading.value = params ; 
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

  //  setTimeout(() => {
  //    cmRef.value?.resize(300, 200)
  //  }, 2000)

  //  setTimeout(() => {
  //    cmRef.value?.cminstance.isClean()
  //  }, 3000)

})

onUnmounted(() => {
  cmRef.value?.destroy()
})

// const onSubmit = () => {
//   console.log('submit!')
// }

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

}</style>