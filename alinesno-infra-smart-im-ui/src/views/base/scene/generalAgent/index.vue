<template>
  <GeneralAgentContainer>

        <RoleSelectPanel :currentSeneInfo="currentSceneInfo" @openExecuteHandle="openExecuteHandle"
          ref="roleSelectPanelRef" />

        <div class="main-content">
          <el-row>
            <el-col :span="12">

              <div class="title-section">
                <span class="title">
                  <i class="fa-solid fa-feather-pointed"></i> 通用智能体任务 
                </span>
                <span class="description">
                  AI针对于某一个场景设计出智能体任务，并结合需求进行输出
                </span>
              </div>
 
              <div class="input-button-section">
                <div style="width:100%">
                  <!-- 附件内容-->
                  <AttachmentSetionPanel 
                    @upload-success="handleUploadSuccess"
                    ref="attachmentPanelRef"
                    style="padding: 0px 0px;margin-bottom:0px;" />

                  <!-- 文本内容 -->
                  <el-input v-model="formData.promptText" 
                    class="input-box" 
                    size="large" 
                    placeholder="请输入您的需求，获取智能体服务"
                    :prefix-icon="Search" />

                </div>

                <!-- 上传附件按键 -->
                <div class="upload-button-section">
                  <div>
                    <el-button type="primary" size="large" class="upload-button" @click="handleUpload" text bg>
                      <i class="fa-solid fa-paperclip"></i> 上传附件
                    </el-button>
                  </div>

                  <el-button type="primary" size="large" @click="generaterText()" class="send-button">
                    <i class="fa-solid fa-paper-plane" style="font-size:22px"></i>
                  </el-button>
                </div>
              </div>

              <!-- 选择题型-->
              <div class="example-select-section">
                <ArticleTypeConfig v-model="formData.pptConfig"/>
              </div>

              <div class="example-section">
                <div class="example-title">你可以这样提问</div>
                <div class="example-list">
                  <div v-for="(item, index) in greetingQuestionList" :key="index" class="example-item" @click="handleExampleClick(item)">
                    <span class="example-text">{{ item.text }}</span>
                    <i class="fa-solid fa-paper-plane example-icon"></i>
                  </div> 
                </div>
              </div>

              <div class="review-footer-message">
                <div class="footer-message">服务生成的所有内容均由人工智能模型生成，其生成内容的准确性和完整性无法保证，不能代表我们的态度和观点。</div>
              </div>

            </el-col>
            <el-col :span="12">
              <div class="review-question-preview-title">
                <span>
                  <i class="fa-solid fa-file-pdf"></i> 它可以帮你完成这些事情
                </span> 
              </div>
 
              <!-- 大纲生成预览 -->
              <div class="pager-gen-result-panel"> 
                <ArticleTemplatePanel v-model="formData.selectedTemplateId" :seceneTemplates="seceneTemplates" />  
              </div>
            </el-col>
          </el-row>

        </div>
 
    <!-- 运行抽屉 -->
    <div class="aip-flow-drawer flow-control-panel">
        <el-drawer v-model="showDebugRunDialog" :modal="false" size="40%" style="max-width: 700px;" title="预览与调试"
            :with-header="true">
            <div style="margin-top: 0px;">
                <RoleChatPanel ref="roleChatPanelRef" />
            </div>
        </el-drawer>
    </div>

  </GeneralAgentContainer>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { ElMessage , ElLoading } from 'element-plus';

import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'
import AttachmentSetionPanel from '@/views/base/scene/articleWriting/common/attachmentSection'
import ArticleTemplatePanel  from './articleTemplate'
import ArticleTypeConfig from './articleTypeConfig.vue';

import GeneralAgentContainer from './generalAgentContainer'
import RoleChatPanel from '@/views/base/scene/common/chatPanel';
import FunctionList from './functionList'

import {
  getScene,
  addTask , 
} from '@/api/base/im/scene/generalAgent';

import SnowflakeId from "snowflake-id";

const roleSelectPanelRef = ref(null)
const snowflake = new SnowflakeId();

const route = useRoute();
const router = useRouter();

const seceneTemplates = ref([])

const greetingQuestionList = ref([
    { text: "写一篇关于产品营销的计划方案" },
    { text: "分析上传的数据报告" },
    { text: "人工智能的发展趋势与未来展望" },
    { text: "公益事业与企业社会责任的结合" },
    { text: "2025在线教育的发展与挑战" },
    { text: "企业数字化执行阻力突破" },
    { text: "年度工作回顾与未来规划" }
]);


const outline = ref(null)
const pptId = ref(null)

const pagerGenContainerPanelRef = ref(null)
const dataAnalysisDisplayRef = ref(null)

// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)

const sceneId = ref(route.query.sceneId)
const streamLoading = ref(null)
const showArticleOutput = ref(false)

// 表单引用
const dialogVisible = ref(false);
const formRef = ref(null)

const channelStreamId= ref(route.query.channelStreamId || snowflake.generate())

// 场景表单信息
const formData = ref({
  pagerType: 'pager',
  sceneId: sceneId.value,
  difficultyLevel: 'easy',
  channelStreamId: channelStreamId.value ,
  pptConfig: {
    writingType: 'general',
    audience: 'general',
    scenario: 'general',
    tone: 'professional'
  },
  attachments:[],
})

// 统计信息
const totalQuestions = ref(0)
const totalScore = ref(0)

const currentSceneInfo = ref({
  sceneName: '通用智能体服务',
});

// 添加attachmentPanelRef引用
const attachmentPanelRef = ref(null);

// 处理上传成功
const handleUploadSuccess = (fileInfo) => {
  console.log('文件上传成功:', fileInfo);
  // 这里可以保存storageId到表单数据中
  // 例如: formData.value.attachments.push(fileInfo.storageId);
  formData.value.attachments.push(fileInfo.storageId);
};

// 修改handleUpload方法，直接调用子组件的上传
const handleUpload = () => {
  if (attachmentPanelRef.value) {
    attachmentPanelRef.value.$el.querySelector('.el-upload__input').click();
  }
};
 

// 计算总题目数和总分
const updateTotalStats = (stats) => {
  totalQuestions.value = stats.totalQuestions
  totalScore.value = stats.totalScore
}

const handleGetScene = () => {
  getScene(sceneId.value).then(res => {
  
    currentSceneInfo.value = res.data;   
    seceneTemplates.value = currentSceneInfo.value.templates 

    if (res.data.greetingQuestion && res.data.greetingQuestion.length > 0) { 
      res.data.greetingQuestion.forEach(item => {
        greetingQuestionList.value.push({
          text: item
        });
      });
    }
 
    if (!currentSceneInfo.value.businessProcessorEngineer || !currentSceneInfo.value.dataViewerEngineer) { // 选择配置角色
      roleSelectPanelRef.value.configAgent();
      return;
    }

  })
}

// 关闭对话框前的处理
const handleClose = (done) => {
  // 可以在这里添加关闭前的确认逻辑
  done()
}

const handleExampleClick = (item) => {
  formData.value.promptText = item.text; 
}

const generaterText = async () => {

  if (!formData.value.promptText) {
    ElMessage.error('请输入内容');
    return;
  }

  // 检查是否选择了模板
  if (!formData.value.selectedTemplateId) {
    ElMessage.error('请在右边选择一个模板');
    return;
  }

  addTask(formData.value).then(res => { 
    const taskId = res.data ; 

    router.push({
      path: '/scene/generalAgent/agentParser',
      query: {
        sceneId: sceneId.value,
        genStatus: true,
        channelStreamId: snowflake.generate(),
        taskId: taskId , 
      }
    })

  })

};

onMounted(() => {
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
.ppt-pager-container {
  background: #fff;
  height: calc(100vh - 60px);

  .review-footer {
    padding: 10px;
    font-size: 14px;
    background: #fafafa;
    border-radius: 8px;
    text-align: left;
    color: #555;
    margin-top: 10px;
}

  .main-content {
    display: flex;
    flex-direction: column;
    padding-top: calc(1vh);
    // text-align: center;
    // max-width: 90%;
    margin: auto;
    padding-left: 20px;
    padding-right: 20px;

    .example-result-section {
      padding: 12px;
      border-radius: 10px;
      font-size: 14px;
      text-align: left;
      color: #585a73;
      display: flex;
      flex-direction: row;
      width: 100%;
      align-items: center;
      gap: 10px;
    }

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

    .input-button-section {
      display: flex;
      gap: 10px;
      position: relative;
      box-sizing: border-box;
      width: 100%;
      border-radius: 15px;
      // box-shadow: rgba(54, 54, 73, 0.06) 0px 12px 24px -16px, rgba(74, 80, 96, 0.12) 0px 12px 40px, rgba(44, 44, 54, 0.02) 0px 0px 1px;
      transition: 0.3s;
      background: rgb(255, 255, 255);
      padding: 10px !important;
      border: 1px solid rgb(232, 234, 242);
      margin-top: 30px;
      margin-bottom: 10px;
      align-items: flex-start;
      flex-direction: column;

      .input-box {
        width: 100%;
        height: 50px;
        border: 0px !important;
        margin-bottom: 0px;
      }
    }

  }

  .review-footer-message {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 10px;
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
    margin-right: 10px;
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

  .review-question-preview {
    height: calc(100vh - 170px);
    margin-left: 20px;
    border-radius: 8px;
    background: #fafafa;
    border: 1px solid #e8eaf2;
    overflow: hidden;
  }

}

.ppt-pager-aside{
  padding: 0px;
  border-right: 1px solid #f2f3f7;
  background: #fff;
  margin-bottom: 0px;
}

.ppt-pager-main{
  padding: 0px !important;
}

.pager-gen-result-panel{
  margin-bottom:20px;
  margin-left:20px;
  text-align: left;

  .pager-container {
    background-color: #fafafa;
    margin: 10px 10px;
    margin-right: 0px;
    border-radius: 8px;
    height: calc(100vh - 190px);
    padding: 10px;
    padding-left: 10px;
    margin-left: 20px;
    margin-bottom: 0px;
  }
}
</style>


<style>
.ppt-pager-container .input-button-section .el-input__wrapper {
  box-shadow: none !important;
  padding: 5px;
  margin-left: 0px;
}
</style>