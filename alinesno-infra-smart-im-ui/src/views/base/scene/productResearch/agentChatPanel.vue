<template>
  <div class="exam-pagercontainer">

    <el-container style="height:calc(100vh - 40px );background-color: #fff;">

      <el-aside width="280px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-main">
        <AgentSingleChatPanel />
      </el-main>
    </el-container>

  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { ElMessage , ElLoading } from 'element-plus';

import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'
import AttachmentSetionPanel from '@/views/base/scene/examPager/common/attachmentSection'
// import QuestionTypeConfig from './questionTypeConfig.vue';
// import PagerGenResultPanel from './pagerGenResultPanel.vue';
// import PagerGenContainerPanel from './components/PagerGenContainer.vue';
import RoleChatPanel from '@/views/base/scene/common/chatPanel';

import AgentSingleChatPanel from '@/views/base/agent/agentSingleChatPanel.vue'

import FunctionList from './functionList'

import {
  getScene,
  savePagerQuestion,
  chatPromptContent
} from '@/api/base/im/scene/examPaper';

import SnowflakeId from "snowflake-id";

const roleSelectPanelRef = ref(null)
const snowflake = new SnowflakeId();
const route = useRoute();
const router = useRouter();

const pagerGenContainerPanelRef = ref(null)

// 执行面板
const showDebugRunDialog = ref(true);
const roleChatPanelRef = ref(null)

const sceneId = ref(route.query.sceneId)

// const isBack = ref(route.query.back || false)
// const promptText = ref('');
// const questionGenerator = ref(null);

const streamLoading = ref(null)

// 表单引用
const dialogVisible = ref(false);
const formRef = ref(null)

const channelStreamId= ref(route.query.channelStreamId || snowflake.generate())

const greetingQuestionList = ref([
  { text: "制定一份市场营销策划方案" },
  { text: "分析当前热门短视频运营策略" },
  { text: "规划一场线上产品发布会流程" },
  { text: "设计一套员工培训课程体系" }
]);

const questionTypeSelect = ref({
    difficulty_levels: [
        {
            "id": 1,
            "name": "简 单",
            "description": "基础概念，直接应用",
            "score_weight": 0.3,
            "value": "easy"
        },
        {
            "id": 2,
            "name": "中 等",
            "description": "需要一定分析和综合",
            "score_weight": 0.5,
            "value": "medium"
        },
        {
            "id": 3,
            "name": "困 难",
            "description": "复杂问题，需深度思考",
            "scorePerQuestion": 0.2,
            "value": "hard"
        }
    ]
});

// 场景表单信息
const formData = ref({
  pagerType: 'pager',
  pagerName: '',
  pagerDesc: '',
  sceneId: sceneId.value,
  difficultyLevel: 'easy',
  channelStreamId: channelStreamId.value ,
  examStructureItem: null ,
  examStructure: [
    {
      id: 'default-radio',
      type: 'radio',
      typeName: '单选题',
      typeDesc: '单选题，从多个选项中选择一个正确答案',
      totalQuestion: 5,
      scorePerQuestion: 2
    }
  ], // 直接使用组件内置的题型选项
  attachments:[],
})

// 表单验证规则
const rules = {
  pagerName: [
    { required: true, message: '请输入试卷名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  pagerDesc: [
    { required: true, message: '请输入试卷描述', trigger: 'blur' },
    { min: 5, max: 200, message: '长度在 5 到 200 个字符', trigger: 'blur' }
  ],
  difficultyLevel: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ]
}

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

const openDialogVisible = () => {
  dialogVisible.value = true;
  if(!formData.value.pagerName){
    formData.value.pagerName = formData.value.promptText ;
  }
}

// 计算总题目数和总分
const updateTotalStats = (stats) => {
  totalQuestions.value = stats.totalQuestions
  totalScore.value = stats.totalScore
}

const handleGetScene = () => {
  getScene(sceneId.value).then(res => {
    currentSceneInfo.value = res.data;
    // handleRoleBySceneIdAndAgentType();

    if (res.data.greetingQuestion && res.data.greetingQuestion.length > 0) {
      greetingQuestionList.value = [];
      res.data.greetingQuestion.forEach(item => {
        greetingQuestionList.value.push({
          text: item
        });
      });
    }

    if (!currentSceneInfo.value.businessProcessorEngineer || !currentSceneInfo.value.dataViewerEngineer) { // 选择配置角色
      // roleSelectPanelRef.value.configAgent();
      return;
    }

    // if (res.data.genStatus == 1 && !isBack.value) {
    //   router.push({
    //     path: '/scene/generalAgent/agentParser',
    //     query: {
    //       sceneId: sceneId.value,
    //       genStatus: true,
    //       channelStreamId: snowflake.generate()
    //     }
    //   })
    // }

  })
}

// 关闭对话框前的处理
const handleClose = (done) => {
  // 可以在这里添加关闭前的确认逻辑
  done()
}

// 保存试卷题目信息
const handleSavePagerQuestion = async () => {

  try {
    // 验证表单
    await formRef.value.validate()
    console.log('保存试卷数据:', formData.value)

    const questionList = pagerGenContainerPanelRef.value.getQuestionList() ;

    const data = {
      sceneId: sceneId.value,
      channelStreamId: channelStreamId.value,
      pagerType: formData.value.pagerType,
      pagerName: formData.value.pagerName,
      pagerDesc: formData.value.pagerName,
      difficulty: formData.value.difficultyLevel,
      examStructure: formData.value.examStructure,
      questionList: questionList,
    }

    savePagerQuestion(data).then(res => {
      console.log('保存成功:', res)
    })
    
    ElMessage.success('试卷保存成功')
    dialogVisible.value = false

  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('请填写完整的试卷信息')
  }

}

const generaterText = async () => {
  if (!formData.value.promptText) {
    ElMessage.error('请输入内容');
    return;
  }

  // 打开流容器
  showDebugRunDialog.value = true;
  await nextTick(() => {
    console.log('roleChatPanelRef = ' + roleChatPanelRef)
    console.log('roleChatPanelRef.value = ' + roleChatPanelRef.value)
    roleChatPanelRef.value.openChatBoxWithRole(currentSceneInfo.value.questionGeneratorEngineer);
  })

  // 开始生成
  streamLoading.value = ElLoading.service({
    lock: true,
    background: 'rgba(255, 255, 255, 0.5)',
    customClass: 'custom-loading'
  });

  const totalNodes = formData.value.examStructure.length;
  
  try {
    for (let i = 0; i < formData.value.examStructure.length; i++) {
      const item = formData.value.examStructure[i];
      const text = '任务正在生成题目【' + item.typeName + '】生成中，还有【' + (totalNodes - i) + '】篇';
      streamLoading.value.setText(text);
      
      formData.value.examStructureItem = item;
      
      // 使用await等待异步操作完成
      const res = await chatPromptContent(formData.value);
      console.log('res = ' + res);

      pagerGenContainerPanelRef.value.addQuestion(res.data);

    }
    
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

// 计算总数
const calculateTotals = () => {
  const stats = formData.value.examStructure.reduce((acc, item) => {
    acc.totalQuestions += item.totalQuestion || 0
    acc.totalScore += (item.totalQuestion || 0) * (item.scorePerQuestion || 0)
    return acc
  }, { totalQuestions: 0, totalScore: 0 })
  
  updateTotalStats(stats)
}

onMounted(() => {
  handleGetScene();
  calculateTotals(); // 初始化统计

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
.exam-pagercontainer {
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
    padding-top: calc(2vh);
    text-align: center;
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

    .upload-button-section {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      width: 100%;
      padding: 0px;
    }

    .input-button-section {
      display: flex;
      gap: 10px;
      position: relative;
      box-sizing: border-box;
      width: 100%;
      border-radius: 5px;
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

    .example-section {
      padding: 0px 0px;

      .example-title {
        color: rgb(44, 44, 54);
        font-size: 14px;
        text-align: left;
        margin-left: 5px;
        margin-top: 15px;
        margin-bottom: 15px;
      }

      .example-list {
        display: flex;
        flex-wrap: wrap;

        .example-item {
          position: relative;
          display: flex;
          gap: 8px;
          align-items: center;
          height: 40px;
          background: rgb(242, 243, 247);
          border-radius: 8px;
          cursor: pointer;
          transition: 0.25s cubic-bezier(0.4, 0, 0.2, 1);
          will-change: opacity, transform;
          width: calc(50% - 10px);
          box-sizing: border-box;
          padding: 10px;
          margin: 5px 5px 8px 5px;

          &:hover {
            background: rgb(232 233 235);

            .example-icon {
              display: block;
            }
          }

          .example-icon {
            display: none;
            color: #585a73;
            font-size: 12px;
          }

          .example-text {
            flex: 1 1;
            overflow: hidden;
            color: #585a73;
            font-size: 14px;
            white-space: nowrap;
            text-align: left;
            text-overflow: ellipsis;
            transition: padding-right .2s ease-out;
          }
        }
      }
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
</style>


<style>
.exam-pagercontainer .input-button-section .el-input__wrapper {
  box-shadow: none !important;
  padding: 5px;
  margin-left: 0px;
}
</style>