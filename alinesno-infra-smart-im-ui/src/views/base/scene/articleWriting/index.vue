<template>
  <ArticleWriterContainer>

        <RoleSelectPanel :currentSeneInfo="currentSceneInfo" @openExecuteHandle="openExecuteHandle"
          ref="roleSelectPanelRef" />

        <div class="main-content">
          <el-row>
            <el-col :span="11">

              <div class="title-section">
                <span class="title">
                  <i class="fa-solid fa-feather-pointed"></i> 智能写作助手助力高效创作
                </span>
                <span class="description">
                  依托前沿语言模型，深度解析创作痛点，输入指令 / 要求，迅速输出高水准文章
                </span>
              </div>

              <div class="input-button-section">
                <div style="width:100%">
                  <!-- 附件内容-->
                  <AttachmentSetionPanel 
                    @upload-success="handleUploadSuccess"
                    ref="attachmentPanelRef"
                    style="padding: 0px 0px;margin-bottom:0px;" />

                   <DynamicPromptEditor 
                      ref="promptEditorRef"
                      v-model="formData.promptText"
                      :promptText="currentPromptTemplate"
                    />
    
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
                <ArticleTypeConfig v-model="formData.articleConfig" :artcleTemplate="formData.selectedTemplate" />
              </div>

              <div class="example-section">
                <div class="example-title">你可以在这里编写</div>
                <div class="example-list">
                  <div v-for="(item, index) in topics" :key="index" class="example-item" @click="handleExampleClick(item)">
                    <span class="example-text">{{ item.text }}</span>
                    <i class="fa-solid fa-paper-plane example-icon"></i>
                  </div> 
                </div>
              </div>

              <div class="review-footer-message">
                <div class="footer-message">服务生成的所有内容均由人工智能模型生成，其生成内容的准确性和完整性无法保证，不能代表我们的态度和观点。</div>
              </div>

            </el-col>
            <el-col :span="13">
              <div class="review-question-preview-title">
                <span>
                  <i class="fa-solid fa-file-pdf"></i> 文章模板 
                </span>
              </div>

              <!-- 文章模板 -->
              <div class="pager-gen-result-panel">

                <el-scrollbar class="pager-container" ref="scrollbarRef"> 
                  <div ref="innerRef">

                    <ArticleTemplatePanel 
                      @selectTemplate="selectTemplate"
                      v-model="formData.selectedTemplate"
                    />

                  </div>
                </el-scrollbar>
              </div>
            </el-col>
          </el-row>

        </div>

<!-- 运行抽屉 -->
    <div class="aip-flow-drawer flow-control-panel">
        <el-drawer v-model="showDebugRunDialog" :modal="false" size="40%" style="max-width: 700px;" title="预览与调试"
            :with-header="true">
            <div style="margin-top: 0px;">
                <RoleChatPanel ref="roleChatPanelRef" :showDebugRunDialog="showDebugRunDialog" />
            </div>
        </el-drawer>
    </div>

            <!-- AI生成状态 -->
        <AIGeneratingStatus 
            ref="generatingStatusRef"  
            :close-enable="false" 
        />

  </ArticleWriterContainer>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { ElMessage , ElLoading } from 'element-plus';

import AIGeneratingStatus from '@/components/GeneratingStatus/index.vue'
import ArticleWriterContainer from "./articleWriterContainer"
import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'
import AttachmentSetionPanel from '@/views/base/scene/articleWriting/common/attachmentSection'
import ArticleTemplatePanel  from './articleTemplate'

import ArticleTypeConfig from './articleTypeConfig.vue';

// import DataAnalysisDisplay from './agentContentDisplay.vue'
// import OutlineGenContainerPanel from './components/OutlineEditor.vue';

import RoleChatPanel from '@/views/base/scene/common/chatPanel';
import FunctionList from './functionList'
// import DynamicTemplateEditor from './components/dynamicTemplateEditor'
import DynamicPromptEditor from './components/DynamicPromptEditor.vue';

import {
  getScene,
  // savePagerQuestion,
  chatPromptContent ,
  // savePPTOutline
} from '@/api/base/im/scene/articleWriting';

import SnowflakeId from "snowflake-id";

const promptEditorRef = ref(null); // 获取组件引用
const roleSelectPanelRef = ref(null)
const snowflake = new SnowflakeId();

const route = useRoute();
const router = useRouter();

const topics = [
    { text: "健康生活方式的趋势" },
    { text: "虚拟货币市场的发展历程与未来展望" },
    { text: "新能源汽车市场现状与发展预测" },
    { text: "人工智能的发展趋势与未来展望" },
    { text: "公益事业与企业社会责任的结合" },
    { text: "2025在线教育的发展与挑战" },
    { text: "企业数字化执行阻力突破" },
    { text: "年度工作回顾与未来规划" }
];

const generatingStatusRef = ref(null)

const outline = ref(null)
 
// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)
const finalText = ref('')
const currentPromptTemplate = ref('帮我写一篇文章')
const sceneId = ref(route.query.sceneId)
const streamLoading = ref(null)
const showArticleOutput = ref(false)
  
const channelStreamId= ref(route.query.channelStreamId || snowflake.generate())

// 场景表单信息
const formData = ref({
  pagerType: 'pager',
  sceneId: sceneId.value,
  difficultyLevel: 'easy',
  channelStreamId: channelStreamId.value ,
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

      topics.value = greetingQuestionList.value;
    }

    if (!currentSceneInfo.value.articleWriterEngineer || !currentSceneInfo.value.articleLayoutDesignerEngineer) { // 选择配置角色
      roleSelectPanelRef.value.configAgent();
      return;
    }

  })
}

 
const handleExampleClick = (item) => {
  formData.value.promptText = item.text; 
} 

const generaterText = async () => {

  // 验证占位符是否全部填写
  if (!promptEditorRef.value.validatePlaceholders()) {
      // 如果有未填写的占位符，方法会自动聚焦到第一个空占位符
      // 这里可以添加额外的提示逻辑
      ElMessage.warning('请填写所有提示词变量');
      return;
  }

  if (!formData.value.promptText) {
    ElMessage.error('请输入内容');
    return;
  }

  // 判断是否选择模板
  if (!formData.value.selectedTemplate) {
    ElMessage.error('请选择文章生成模板');
    return;
  }
  formData.value.selectedTemplateId = formData.value.selectedTemplate.id;

  // 打开流容器
  showDebugRunDialog.value = true;
  await nextTick(() => {
    console.log('roleChatPanelRef = ' + roleChatPanelRef)
    console.log('roleChatPanelRef.value = ' + roleChatPanelRef.value)
    roleChatPanelRef.value.openChatBoxWithRole(currentSceneInfo.value.articleWriterEngineer);
  })

  // 开始生成
 
  let text = '文案正在生成，请稍等...' ;  

  generatingStatusRef.value?.loading()
  generatingStatusRef.value?.setText(text) ;
  
  try {

    // 使用await等待异步操作完成
    const res = await chatPromptContent(formData.value);
    console.log('res = ' + res.data);

    outline.value = res.data ;
    showArticleOutput.value = true ;
    const articleId = res.data ;

    nextTick(() => {
      const path = '/scene/articleWriting/articleEditPager';
      router.push({
        path: path,
        query: { 
          'articleId': articleId ,
          'sceneId': sceneId.value , 
          'channelStreamId': snowflake.generate() 
        }
      })
    });
    
    ElMessage.success('处理完成');
  } catch (err) {
    ElMessage.error('处理失败');
    // 如果需要在这里跳出函数，可以直接return
    return;
  } finally {
    // 无论成功或失败都关闭loading
    generatingStatusRef.value?.close();
    showDebugRunDialog.value = false ;
  }

};

// 判断是否为string，是则转换为对象
const getJsonObject = (jsonString) => {
  if (typeof jsonString === 'string') {
    return JSON.parse(jsonString);
  } else {
    return jsonString;
  }
}

const selectTemplate = (item) => {
  console.log('item = ' + JSON.stringify(item))
  // formData.value.promptText = item.prompt;
  currentPromptTemplate.value = item.prompt ;
};

// // watcher监听selectTemplate是否为空，不为空则赋值给formData.value.promptText（如果为空的情况下）
// watch(() => formData.value.selectedTemplate, (newValue) => { 
//   console.log('newValue = ' + newValue.promptText)
//   if (!newValue) {
//     return;
//   }
//   if (!formData.value.promptText) {
//     formData.value.promptText = newValue.promptText;
//   }
// });

onMounted(() => {
  handleGetScene();
  // calculateTotals(); // 初始化统计

  // Add this check for channelStreamId in URL
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
        // height: 50px;
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
.ppt-pager-container .input-button-section .el-textarea__inner{
  box-shadow: none !important;
  padding: 5px;
  margin-left: 0px;
}
</style>