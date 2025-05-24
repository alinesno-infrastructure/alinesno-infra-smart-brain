<template>
  <div class="exam-pagercontainer">

    <el-container style="height:calc(100vh - 40px );background-color: #fff;">

      <el-aside width="280px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-main">

        <!-- <RoleSelectPanel :currentSeneInfo="currentSceneInfo" @openExecuteHandle="openExecuteHandle"
          ref="roleSelectPanelRef" /> -->

        <div class="main-content">

          <el-row>
            <el-col :span="11">

              <div class="title-section">
                <span class="title">
                  <i class="fa-solid fa-laptop-file"></i> 智能体组卷管理配置 
                </span>
                <span class="description">依托专业教研智能体，精准聚焦考试痛点，输入科目/阶段，快速定制高效提分方案</span>
              </div>
              <div class="input-button-section pager-prompt">
                <div style="width:100%">

                  <!-- 文本内容 -->
                  <el-input v-model="promptText" class="input-box" size="large" placeholder="请输入您的需求，获取智能体服务"
                    :prefix-icon="Search" />

                </div>
                <!-- 上传附件按键 -->
                <div class="upload-button-section">

                  <div>

                    <el-button type="primary" size="large" class="upload-button" @click="handleUpload" text bg>
                      <i class="fa-solid fa-paperclip"></i> &nbsp; 配置题库 &nbsp;
                      <el-tag effect="dark" size="small" round>
                        5
                      </el-tag>
                    </el-button>

                  </div>

                  <el-button type="primary" size="large" @click="generaterText()" class="send-button">
                    <i class="fa-solid fa-paper-plane" style="font-size:22px"></i>
                  </el-button>
                </div>
              </div>

              <!-- 题型配置的结果，会有默认值-->
              <div class="example-result-section">
                题目难度
                <el-segmented v-model="formData.difficultyLevel" size="large" :options="questionTypeSelect.difficulty_levels" block>
                  <template #default="scope">
                    <div>{{ scope.item.name }}</div>
                  </template>
                </el-segmented>
              </div>

              <!-- 选择题型-->
              <div class="example-select-section">
                <QuestionTypeConfig 
                  v-model="formData.examStructure"
                  :max-question-count="10"
                  :max-score-per-question="30" 
                  @update:total="updateTotalStats"
                  />
              </div>

              <div class="review-footer"> 
               {{ totalQuestions }} 道题目，共计 {{ totalScore }} 分 
              </div>

              <div class="review-footer-message">
                <div class="footer-message">服务生成的所有内容均由人工智能模型生成，其生成内容的准确性和完整性无法保证，不能代表我们的态度和观点。</div>
              </div>

            </el-col>
            <el-col :span="13">
                <div class="review-question-preview-title">
                 <i class="fa-solid fa-file-pdf"></i> 题目生成预览
                </div>
              <!-- 题目生成预览 -->
               <PagerGenContainerPanel />
               
            </el-col>
          </el-row>

        </div>

      </el-main>
    </el-container>

  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'
import AttachmentSetionPanel from '@/views/base/scene/common/attachmentSection'
import QuestionTypeConfig from './questionTypeConfig.vue';
import PagerGenResultPanel from './pagerGenResultPanel.vue';
import PagerGenContainerPanel from './components/PagerGenContainer.vue';

import FunctionList from './functionList'

import {
  getScene,
  updateChapterPromptContent
} from '@/api/base/im/scene/generalAgent';
import SnowflakeId from "snowflake-id";

const roleSelectPanelRef = ref(null)
const snowflake = new SnowflakeId();
const route = useRoute();
const router = useRouter();

const sceneId = ref(route.query.sceneId)
const isBack = ref(route.query.back || false)
const promptText = ref('');

const sizeOptions = ['简单', '中等', '困难']

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
            "name": "简单",
            "description": "基础概念，直接应用",
            "score_weight": 0.3,
            "value": "easy"
        },
        {
            "id": 2,
            "name": "中等",
            "description": "需要一定分析和综合",
            "score_weight": 0.5,
            "value": "medium"
        },
        {
            "id": 3,
            "name": "困难",
            "description": "复杂问题，需深度思考",
            "score_per_question": 0.2,
            "value": "hard"
        }
    ]
});

const currentSceneInfo = ref({
  sceneName: '通用智能体服务',
});

// 场景表单信息
const formData = ref({
  difficultyLevel: 'easy',
  examStructure: [
    {
      id: 'default-radio',
      type: 'radio',
      total_questions: 5,
      score_per_question: 2
    }
  ] // 直接使用组件内置的题型选项
})

// 统计信息
const totalQuestions = ref(0)
const totalScore = ref(0)

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

    if (res.data.genStatus == 1 && !isBack.value) {
      router.push({
        path: '/scene/generalAgent/agentParser',
        query: {
          sceneId: sceneId.value,
          genStatus: true,
          channelStreamId: snowflake.generate()
        }
      })
    }
  })
}

const generaterText = () => {
  if (!promptText.value) {
    ElMessage.error('请输入内容');
    return;
  }
  ElMessage.success('处理完成');

  updateChapterPromptContent({
    sceneId: sceneId.value,
    promptContent: promptText.value
  }).then(res => {
    router.push({
      path: '/scene/generalAgent/agentParser',
      query: {
        sceneId: sceneId.value,
        genStatus: true,
        channelStreamId: snowflake.generate()
      }
    })
  })

}

// const handleExampleClick = (item) => {
//   promptText.value = item.text;
//   // generaterText();
// }

// 计算总数
const calculateTotals = () => {
  const stats = formData.value.examStructure.reduce((acc, item) => {
    acc.totalQuestions += item.total_questions || 0
    acc.totalScore += (item.total_questions || 0) * (item.score_per_question || 0)
    return acc
  }, { totalQuestions: 0, totalScore: 0 })
  
  updateTotalStats(stats)
}

onMounted(() => {
  handleGetScene();
  calculateTotals(); // 初始化统计
})

</script>

<style lang="scss" scoped>
.exam-pagercontainer {
  background: #fff;
  height: calc(100vh - 50px);

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
    padding-top: calc(3vh);
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
    margin-bottom: 10px;
    border-radius: 10px;
    background: #fafafa;
    color: #444;
    font-size: 15px;
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
.exam-pagercontainer .pager-prompt .el-input__wrapper {
  box-shadow: none !important;
  padding: 0px;
  margin-left: -5px;
}
</style>