<template>
  <div class="exam-pagercontainer product-research-container">
    <el-container style="height:calc(100vh - 40px );background-color: #fff;">
      <el-aside width="280px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-main">
        <RoleSelectPanel :currentSeneInfo="currentSceneInfo" @openExecuteHandle="openExecuteHandle"
          ref="roleSelectPanelRef" />

        <div class="main-content">
          <el-row>
            <el-col :span="11">
              <div class="title-section">
                <span class="title">
                  <i class="fa-solid fa-file-word"></i> 基于AI项目情况分析
                </span>
                <span class="description">依托专业AI智能体，精准聚焦项目痛点，快速定制获取研发进度汇报</span>
              </div>
              <div class="input-button-section2">
                <div style="width:100%">
                  <AttachmentSetionPanel @upload-success="handleUploadSuccess" ref="attachmentPanelRef"
                    style="padding: 0px 0px;margin-bottom:0px;" />

                  <el-form ref="formRef" :model="formData" label-position="top" :rules="rules">

                    <!-- 项目名称 -->
                    <el-form-item prop="projectName" label="项目名称">
                      <el-input v-model="formData.projectName" size="large" placeholder="请输入项目名称"
                        :prefix-icon="Search">
                        <template #prefix>
                          <div>项目名</div>
                        </template>
                      </el-input>
                    </el-form-item>

                    <el-form-item prop="gitUrl" label="项目仓库地址">
                      <el-input v-model="formData.gitUrl" size="large" placeholder="项目仓库信息"
                        :prefix-icon="Search">
                        <template #prefix>
                          <div>Git地址</div>
                        </template>
                      </el-input>
                    </el-form-item>

                    <!-- 使用el-switch开关是否使用认证-->
                    <el-form-item prop="isAuth" label="是否使用认证">  
                      <el-switch v-model="formData.isAuth" 
                        class="switch-box" 
                        size="small" />
                    </el-form-item>

                    <el-form-item v-if="formData.isAuth" prop="gitUsername" label="认证用户名">  
                      <el-input v-model="formData.gitUsername" size="large" placeholder="仓库认证用户名"
                        :prefix-icon="Search">
                        <template #prefix>
                          <div>Git用户名</div>
                        </template>
                      </el-input>
                    </el-form-item>
                    <el-form-item v-if="formData.isAuth" prop="gitPassword" label="认证密钥">
                      <el-input v-model="formData.gitPassword" size="large" placeholder="仓库认证密钥"
                        :prefix-icon="Search">
                        <template #prefix>
                          <div>Git密码</div>
                        </template>
                      </el-input>
                    </el-form-item>
                  </el-form>
                </div>
              </div>
              <div class="upload-button-section">
                <el-button type="primary" text bg size="large" @click="submitGitProject()"  v-loading="chatStreamLoading" class="send-button">
                  <i class="fa-solid fa-paper-plane" style="font-size:22px"></i> &nbsp; 导入项目
                </el-button>
              </div>

              <div class="review-footer-message">
                <div class="footer-message">服务生成的所有内容均由人工智能模型生成，其生成内容的准确性和完整性无法保证，不能代表我们的态度和观点。</div>
              </div>
            </el-col>

            <el-col :span="13">
              <div class="review-question-preview-title">
                <span>
                  <i class="fa-solid fa-file-pdf"></i> 导入进度详情
                </span>
              </div>
              <div style="margin-top: 10vh;" v-if="logStreamArr.length == 0">
                <el-empty description="当前未导入项目，这里会实时显示导入的进度情况" />
              </div>
              <div v-if="logStreamArr.length > 0">
                  <el-scrollbar class="scroll-panel" ref="scrollbarRef" loading always wrap-style="padding:10px" style=" height: calc(100vh - 180px);">
                    <ZshLog ref="innerRef" 
                        :logs="logStreamArr" 
                        :show-time="true" 
                        theme="zsh-dark" 
                        highlight-level 
                      />
                      <div v-if="chatStreamLoading" class="loading-indicator">
                        <el-button type="primary" text bg :loading="chatStreamLoading">数据加载中 ....</el-button>
                      </div>
                  </el-scrollbar>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-main>
    </el-container>

    <div class="aip-flow-drawer flow-control-panel">
      <el-drawer v-model="showDebugRunDialog" :modal="false" size="40%" style="max-width: 700px;" title="预览与调试"
        :with-header="true">
        <div style="margin-top: 0px;">
          <RoleChatPanel ref="roleChatPanelRef" />
        </div>
      </el-drawer>
    </div>
  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { ElMessage, ElLoading } from 'element-plus';

import { openSseConnectChannel , handleCloseSse } from "@/api/base/im/chatsse";

import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'
import AttachmentSetionPanel from '@/views/base/scene/examPager/common/attachmentSection'
import RoleChatPanel from '@/views/base/scene/common/chatPanel';
import FunctionList from './functionList'
import  ZshLog from "./ZshLog";

import SnowflakeId from "snowflake-id";

import {
  getScene,
  savePagerQuestion,
  importGitProject,
  savePPTOutline
} from '@/api/base/im/scene/productResearch';

const roleSelectPanelRef = ref(null)
const snowflake = new SnowflakeId();
const route = useRoute();
const router = useRouter();
const pagerGenContainerPanelRef = ref(null)
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)
const sceneId = ref(route.query.sceneId)
const streamLoading = ref(null)

const channelStreamId = ref(route.query.channelStreamId || snowflake.generate())
const logChannelStreamId = ref(snowflake.generate());

const innerRef = ref(null); // 滚动条的处理_starter
const scrollbarRef = ref(null);

const formRef = ref(null)
const formData = ref({
  projectName: '',
   gitUrl: '',
   sceneId: sceneId.value,
   isAuth: false,
   channelStreamId: channelStreamId.value ,
   logChannelStreamId: logChannelStreamId.value ,
   gitUsername: '',
   gitPassword: ''
})

const chatStreamLoading = ref(false);
const logStreamArr =  ref([])

const currentSceneInfo = ref({
  sceneName: '通用智能体服务',
});

// 表单验证规则
const rules = {
  projectName:  [
    { required: true, message: '请输入项目名', trigger: 'blur' },
  ],
  gitUrl: [
    { required: true, message: '请输入项目仓库地址', trigger: 'blur' },
  ],
  gitUsername: [
    { required: true, message: '请输入认证用户名', trigger: 'blur' },
  ],
  gitPassword: [
    { required: true, message: '请输入认证密钥', trigger: 'blur' },
  ]
}


const attachmentPanelRef = ref(null);

const handleUploadSuccess = (fileInfo) => {
  formData.value.attachments.push(fileInfo.storageId);
};

const submitGitProject = async () => {

  if(chatStreamLoading.value){
    ElMessage.warning('正在导入项目，请稍候...');
    return ; 
  }

  // 表单验证
  formRef.value.validate(async (valid) => {
    if (valid) {

      chatStreamLoading.value = true ;
      logStreamArr.value = [] ;// 清空日志

      importGitProject(formData.value).then(res => {
        console.log('res = ' + res);
        // streamLoading.value.close();

      }).catch( error => {
      chatStreamLoading.value = false;
      })
    }
  });
};


const handleGetScene = () => {
  getScene(sceneId.value).then(res => {
    currentSceneInfo.value = res.data;

    if (res.data.greetingQuestion && res.data.greetingQuestion.length > 0) {
      greetingQuestionList.value = [];
      res.data.greetingQuestion.forEach(item => {
        greetingQuestionList.value.push({
          text: item
        });
      });
    }

    if (!currentSceneInfo.value.progressAnalyzerEngineer|| !currentSceneInfo.value.processCollectorEngineer) { // 选择配置角色
      roleSelectPanelRef.value.configAgent();
      return;
    }

  })
}

function initChatBoxScroll() {

  nextTick(() => {
    const element = innerRef.value;  // 获取滚动元素
    const scrollHeight = element.scrollHeight;

    scrollbarRef.value.setScrollTop(scrollHeight);
  })

}

const pushResponseMessageList = (data) => {
  console.log('data = ' + data);
  logStreamArr.value.push(data)

  if(data.level === 'FINISH'){
      showDebugRunDialog.value = false ;  
      streamLoading.value.close() ;
  }

  // 最大不能超过100条
   if (logStreamArr.value.length >= 30) {
    logStreamArr.value.splice(0, 1);
  }

  initChatBoxScroll();

}

/** 连接sse */
function handleSseConnect(channelStreamId) {
  nextTick(() => {
    if (channelStreamId) {

      let sseSource = openSseConnectChannel(channelStreamId);
      // 接收到数据
      sseSource.onmessage = function (event) {
        if (!event.data.includes('[DONE]')) {
          let resData = event.data;

          if (resData != 'ping') {  // 非心跳消息
            const data = JSON.parse(resData);
            pushResponseMessageList(data);
          }
        } else if(event.data.includes('[DONE]')) {
          console.log('消息接收结束.')
          chatStreamLoading.value = false ;
          openRoleChat() ;
        }

      }
    }
  })
}

const openRoleChat = () => {
  // 打开流容器
  showDebugRunDialog.value = true;
    // 开始生成
    streamLoading.value = ElLoading.service({
      lock: true,
      text: '正在解析内容，请勿刷新，请耐心等待...',
      customClass: 'project-custom-loading',
      background: 'rgba(0, 0, 0, 0.2)',
    });

  nextTick(() => {
    console.log('roleChatPanelRef = ' + roleChatPanelRef)
    console.log('roleChatPanelRef.value = ' + roleChatPanelRef.value)
    roleChatPanelRef.value.openChatBoxWithRole(currentSceneInfo.value.processCollectorEngineer);
  })
}

// 销毁信息
onBeforeUnmount(() => {
  handleCloseSse(logChannelStreamId.value).then(res => {
    console.log('关闭sse连接成功:' + logChannelStreamId.value)
  })
});


onMounted(() => {

  handleSseConnect(logChannelStreamId.value);
  handleGetScene();

  if (!route.query.channelStreamId) {
    router.replace({
      query: {
        ...route.query,
        channelStreamId: channelStreamId.value
      }
    });
  }
})
</script>

<style lang="scss" scoped>

.exam-pagercontainer {
  background: #fff;
  height: calc(100vh - 60px);

  .main-content {
    display: flex;
    flex-direction: column;
    padding-top: calc(2vh);
    margin: auto;
    padding-left: 20px;
    padding-right: 20px;

    .title-section {
      display: flex;
      flex-direction: column;
      text-align: center;
      align-items: flex-start;

      .title {
        color: #2C2C36;
        font-weight: 600;
        font-size: 28px;
        margin-bottom: 10px;
        line-height: 40px;
      }

      .description {
        margin-top: 10px;
        color: #8F91A8;
        font-weight: 400;
        font-size: 16px;
        line-height: 24px;
      }
    }

    .input-button-section2 {
      display: flex;
      gap: 10px;
      position: relative;
      box-sizing: border-box;
      width: 100%;
      border-radius: 5px;
      background: #fafafa ;// rgb(255, 255, 255);
      padding: 10px !important;
      border: 1px solid #f5f5f5 ; // rgb(232, 234, 242);
      margin-top: 20px;
      margin-bottom: 20px;
      align-items: flex-start;
      flex-direction: column;
    }
  }

  .review-footer-message {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 50px;
    height: 36px;
    padding: 12px 0px;
    text-align: center;

    .footer-message {
      margin-bottom: 4px;
      color: #C8CAD9;
      font-size: 12px;
      line-height: 12px;
    }
  }

  .review-question-preview-title {
    padding: 10px;
    text-align: left;
    font-weight: bold;
    margin-left: 20px;
    margin-bottom: 10px;
    border-radius: 10px;
    background: #fafafa;
    color: #444;
    font-size: 15px;
    display: flex;
    align-content: center;
    align-items: center;
    justify-content: space-between;
  }

  .loading-indicator {
    margin: 10px;
  }

}

</style>

<style scoped>
.product-research-container .input-button-section2 .el-input__wrapper{
  box-shadow: 0 0 0 1px var(--el-input-border-color,var(--el-border-color)) inset !important;
}

</style>