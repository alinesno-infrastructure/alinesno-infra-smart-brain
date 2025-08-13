<template>
  <div class="ppt-pager-container">

    <el-container style="height:calc(100vh - 40px );background-color: #fff;">

      <el-aside width="80px" class="ppt-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="ppt-pager-main">

        <!-- 数据 -->
        <slot></slot>

      </el-main>
    </el-container>
 
  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { ElMessage, ElLoading } from 'element-plus';

import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'
import AttachmentSetionPanel from '@/views/base/scene/articleWriting/common/attachmentSection'
import DatasetTemplatePanel from './datasetTemplate' 
import ZshLog from "./ZshLog";
import OutlineGenContainerPanel from './components/OutlineEditor.vue';

import RoleChatPanel from '@/views/base/scene/common/chatPanel';
import FunctionList from './functionList'

import SnowflakeId from "snowflake-id";

import {
  getScene,
  savePagerQuestion,
  addTask ,
  chatPromptContent ,
  importGitProject,
  savePPTOutline
} from '@/api/base/im/scene/productResearch';

const roleSelectPanelRef = ref(null)
const snowflake = new SnowflakeId();

const route = useRoute();
const router = useRouter();

const topics = [
  { text: "写一篇关于产品营销的计划方案" },
  { text: "分析上传的数据报告" },
  { text: "人工智能的发展趋势与未来展望" },
  { text: "公益事业与企业社会责任的结合" },
  { text: "2025在线教育的发展与挑战" },
  { text: "企业数字化执行阻力突破" },
  { text: "年度工作回顾与未来规划" }
];

const outline = ref(null)
const pptId = ref(null)

const logStreamArr = ref([])
const pagerGenContainerPanelRef = ref(null)
const dataAnalysisDisplayRef = ref(null)

// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)

const sceneId = ref(route.query.sceneId)
const streamLoading = ref(null)
// const showArticleOutput = ref(false)

// 表单引用
// const dialogVisible = ref(false);
// const formRef = ref(null)

const channelStreamId = ref(route.query.channelStreamId || snowflake.generate())

// 场景表单信息
const formData = ref({
  pagerType: 'pager',
  sceneId: sceneId.value,
  datasetGroupId: null ,
  channelStreamId: channelStreamId.value,
  pptConfig: {
    writingType: 'general',
    audience: 'general',
    scenario: 'general',
    tone: 'professional'
  },
  attachments: [],
})

// 统计信息
// const totalQuestions = ref(0)
// const totalScore = ref(0)

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

// 生成业务集成大纲
const generaterOutlineText = async () => {
  if (!formData.value.promptText) {
    ElMessage.error('请输入内容');
    return;
  }

  // 打开流容器
  showDebugRunDialog.value = true;
  await nextTick(() => {
    console.log('roleChatPanelRef = ' + roleChatPanelRef)
    console.log('roleChatPanelRef.value = ' + roleChatPanelRef.value)
    roleChatPanelRef.value.openChatBoxWithRole(currentSceneInfo.value.processCollectorEngineer);
  })

  // 开始生成
  streamLoading.value = ElLoading.service({
    lock: true,
    background: 'rgba(255, 255, 255, 0.5)',
    customClass: 'custom-loading'
  });

  
  try {

    let text = 'AI规划正在生成，请稍等...' ; 
    streamLoading.value.setText(text)

    // 使用await等待异步操作完成
    const res = await chatPromptContent(formData.value);
    console.log('res = ' + res.data);

    outline.value = res.data ;
    formData.value.outline = res.data ;

    nextTick(() => {
      pagerGenContainerPanelRef.value.setOutlineJson(res.data)
    })

    ElMessage.success('处理完成');
  } catch (err) {
    ElMessage.error('处理失败');
    // 如果需要在这里跳出函数，可以直接return
    return;
  } finally {
    // 无论成功或失败都关闭loading
    streamLoading.value.close();
    showDebugRunDialog.value = false ;
  }

};

// // 生成PPT
// const generatorPPT = () => {
//   savePPTOutline(sceneId.value,
//     outline.value,
//     pptId.value,
//     formData.value.pptConfig,
//     formData.value.promptText
//   ).then(res => {
//     pptId.value = res.data;
//     window.open(`http://alinesno-infra-smart-aippt-ui.beta.base.infra.linesno.com?pptId=${pptId.value}&editorType=editor`, '_blank');
//   }).catch(error => {
//     ElMessage.error(error.message)
//   })
// }

// // 计算总题目数和总分
// const updateTotalStats = (stats) => {
//   totalQuestions.value = stats.totalQuestions
//   totalScore.value = stats.totalScore
// }


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

    if (!currentSceneInfo.value.progressAnalyzerEngineer || !currentSceneInfo.value.processCollectorEngineer) { // 选择配置角色
      console.log('当前未配置角色')
      roleSelectPanelRef.value.configAgent();
      return;
    }

  })
}

// // 关闭对话框前的处理
// const handleClose = (done) => {
//   // 可以在这里添加关闭前的确认逻辑
//   done()
// }

const handleExampleClick = (item) => {
  formData.value.promptText = item.text;
  // generaterText();
}

// 保存试卷题目信息
// const handleSavePagerQuestion = async () => {

//   try {
//     // 验证表单
//     await formRef.value.validate()
//     console.log('保存文章数据:', formData.value)

//     const questionList = pagerGenContainerPanelRef.value.getQuestionList();

//     const data = {
//       sceneId: sceneId.value,
//       channelStreamId: channelStreamId.value,
//       pagerType: formData.value.pagerType,
//       difficulty: formData.value.difficultyLevel,
//       examStructure: formData.value.examStructure,
//       questionList: questionList,
//     }

//     savePagerQuestion(data).then(res => {
//       console.log('保存成功:', res)
//     })

//     ElMessage.success('试卷保存成功')
//     dialogVisible.value = false

//   } catch (error) {
//     console.error('保存失败:', error)
//     ElMessage.error('请填写完整的试卷信息')
//   }

// }

const generaterText = async () => {
  if (!formData.value.promptText) {
    ElMessage.error('请输入内容');
    return;
  }

  addTask(formData.value).then(res => {

    const taskId = res.data;

    router.push({
       path: '/scene/productResearch/productParser',
       query: {
         sceneId: sceneId.value,
         channelStreamId: snowflake.generate(),
         taskId: taskId,
       }
    })

  })

};

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
      margin-top: 20px;
      margin-bottom: 10px;
      align-items: flex-start;
      flex-direction: column;

      .input-box {
        width: 100%;
        height: 80px;
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
    margin-left: 30px;
    margin-right: 0px;
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

.ppt-pager-aside {
  padding: 0px;
  border-right: 1px solid #f2f3f7;
  background: #fff;
  margin-bottom: 0px;
}

.ppt-pager-main {
  padding: 0px !important;
}

.pager-gen-result-panel {
  margin-bottom: 20px;
  margin-left: 10px;
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