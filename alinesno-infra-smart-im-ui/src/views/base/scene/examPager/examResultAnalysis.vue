<template>
  <!-- 加载遮罩层 -->
  <div v-if="loading" class="loading-overlay">
    <div class="loading-content">
      <div class="ai-avatar">
        <img :src="aiAvatar" alt="AI阅卷助手" class="avatar-img">
        <div class="ai-name">
          {{ answerCheckerEngineer?.roleName }}
        </div>
        <div class="ai-speech-bubble">
          <p>{{ currentAIMessage }}</p>
        </div>
      </div>
      <div class="loading-steps">
        <div class="step" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
          <div class="step-icon">
            <i v-if="currentStep > 1" class="fa fa-check"></i>
            <span v-else>1</span>
          </div>
          <div class="step-text">AI正在阅卷</div>
          <div v-if="currentStep === 1" class="step-progress">
            <div class="progress-bar" :style="{ width: stepProgress + '%' }"></div>
          </div>
        </div>
        <div class="step" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
          <div class="step-icon">
            <i v-if="currentStep > 2" class="fa fa-check"></i>
            <span v-else>2</span>
          </div>
          <div class="step-text">AI正在评分</div>
          <div v-if="currentStep === 2" class="step-progress">
            <div class="progress-bar" :style="{ width: stepProgress + '%' }"></div>
          </div>
        </div>
        <div class="step" :class="{ active: currentStep >= 3, completed: currentStep > 3 }">
          <div class="step-icon">
            <i v-if="currentStep > 3" class="fa fa-check"></i>
            <span v-else>3</span>
          </div>
          <div class="step-text">AI正在生成分析报告</div>
          <div v-if="currentStep === 3" class="step-progress">
            <div class="progress-bar" :style="{ width: stepProgress + '%' }"></div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 主内容区 -->
  <div class="exam-result-container" :class="{ blur: loading || reSubmit }">
    <!-- 主要内容区 -->
    <main class="container" id="exam-result-id">

      <!-- 考试总结卡片 -->
      <section class="exam-summary-section">
        <div class="exam-summary-card">
          <div class="summary-header">
            <div>

              <h2>{{ examInfo.examName }}</h2>
              <div class="exam-meta">
                <span>考试日期: 2025年6月15日</span>
                <span>考试时长: 60分钟</span>
                <span>总分: {{ examInfo.totalScore }}分</span>
                <span>你的得分: {{ examData.score }}分</span>
              </div>
            </div>


            <!-- 新增的分数盖章效果 -->
            <div class="score-stamp" :class="getScoreStampClass(examData.score, examInfo.totalScore)">
              <div class="score-value">{{ examData.score }}</div>
              <div class="score-label">{{ getScoreLabel(examData.score, examInfo.totalScore) }}</div>
              <div class="stamp-overlay"></div>
            </div>

          </div>

          <div class="summary-content-wrapper">
            <MarkdownRenderer :content="analysisResult.overall" />
          </div>

          <div class="summary-footer">
            <div class="ai-signature">
              <img :src="aiAvatar" alt="AI阅卷助手" class="avatar-sm">
              <span>AI智能阅卷助手</span>
            </div>
            <div class="summary-date">分析时间: {{ currentDate }}</div>
          </div>
        </div>
      </section>

      <!-- 考试信息卡片 -->
      <section class="exam-info-section">
        <div class="exam-info-card">
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-icon bg-primary-light">
                <i class="fa fa-line-chart"></i>
              </div>
              <div>
                <p class="stat-label">班级排名</p>
                <p class="stat-value">第 {{ examData.rank }} 名</p>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon bg-secondary-light">
                <i class="fa fa-trophy"></i>
              </div>
              <div>
                <p class="stat-label">正确率</p>
                <p class="stat-value">{{ examData.accuracy }}%</p>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon bg-accent-light">
                <i class="fa fa-clock-o"></i>
              </div>
              <div>
                <p class="stat-label">用时</p>
                <p class="stat-value">{{ examData.timeUsed }} 分钟</p>
              </div>
            </div>
          </div>
        </div>
      </section>
 
      <!-- 答题详情 -->
<section class="answer-details-section">
  <div class="answer-details-card">
    <div class="answer-details-header">
      <h3>答题详情</h3>
      <div class="filter-buttons">
        <el-button type="primary" text bg>全部</el-button>
        <el-button text bg>正确</el-button>
        <el-button text bg>错误</el-button>
        <el-button text bg>未答</el-button>
      </div>
    </div>

    <div class="questions-list">
      <div 
        v-for="(result, index) in analysisResult.examResults" 
        :key="result.id" 
        class="question-item"
      >
        <div class="question-header">
          <span class="question-number">{{ index + 1 }}.</span>
          <span class="question-score" :class="{
            'zero-score': result.score === 0,
            'full-score': result.score === result.maxScore,
            'partial-score': result.score > 0 && result.score < result.maxScore 
          }">
            ({{ result.score }}/{{ result.maxScore }})
          </span>
        </div>
        
        <div class="question-content">
          <h4 class="question-title">题目：</h4>
          <p>{{ getQuestionById(result.id)?.question }}</p>
        </div>
        
        <div class="question-analysis">
          <h4 class="analysis-title">考核分析：</h4>
          <p>{{ getQuestionById(result.id)?.answerAnalysis }}</p>
        </div>
        
        <div class="question-comment">
          <h4 class="comment-title">教师点评：</h4>
          <p>{{ result.comment || "暂无点评" }}</p>
        </div>
      </div>
    </div>
  </div>
</section>


      <!-- 薄弱知识点分析 -->
      <section class="weak-knowledge-section">
        <div class="weak-knowledge-card">
          <h3>薄弱知识点分析</h3>
          <div class="knowledge-grid">
            <div class="knowledge-item" v-for="knowledge in weakAreas" :key="knowledge.id">
              <h4>{{ knowledge.topic }}</h4>
              <p style="font-size:14px;">{{ knowledge.issue }}</p>
              <p>{{ knowledge.suggestion }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 学习建议 -->
      <section class="suggestions-section">
        <div class="suggestions-card">
          <h3>学习建议</h3>
          <div class="suggestions-list">
            <div class="suggestion-item" v-for="(suggestion, index) in improvementSuggestions" :key="index">
              <div class="suggestion-icon">
                <i class="fa fa-check-circle"></i>
              </div>
              <div class="suggestion-content">
                <h4>{{ suggestion.topic }}</h4>
                <p>{{ suggestion.text }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>


      <section>
        <div class="ai-label">此为AI阅卷结果，打分和评语仅供参数，酌情使用</div>
      </section>
    </main>

    <div class="floating-button">
      <el-button size="large" type="primary" @click="downloadReport">
        <i class="fa fa-download"></i> 导出
      </el-button>
    </div>

    <!-- 模态框 - 题目详情 -->
    <div v-if="showModal" class="question-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>题目详情</h3>
          <el-button @click="closeModal" class="close-btn" text>
            <i class="fa fa-times"></i>
          </el-button>
        </div>
        <div class="modal-body">
          <div class="question-section">
            <h4>题目：</h4>
            <p>{{ currentQuestion.content }}</p>
            <p v-for="(part, index) in currentQuestion.parts" :key="index">{{ part }}</p>
          </div>

          <div class="answer-section">
            <h4>正确答案：</h4>
            <p v-for="(answer, index) in currentQuestion.correctAnswers" :key="'correct' + index">{{ answer }}</p>
          </div>

          <div class="user-answer-section">
            <h4>你的答案：</h4>
            <p v-for="(answer, index) in currentQuestion.userAnswers" :key="'user' + index">{{ answer }}</p>
          </div>

          <div class="error-analysis-section">
            <h4>错误分析：</h4>
            <p v-for="(analysis, index) in currentQuestion.errorAnalysis" :key="'analysis' + index">{{ analysis }}</p>
          </div>

          <div class="knowledge-points-section">
            <h4>知识点：</h4>
            <p>{{ currentQuestion.knowledge }}</p>
          </div>
        </div>
        <div class="modal-footer">
          <el-button @click="closeModal">关闭</el-button>
          <el-button type="primary">添加到错题集</el-button>
        </div>
      </div>
    </div>

    <!-- 运行抽屉 -->
    <div class="aip-flow-drawer">
      <el-drawer v-model="showDebugRunDialog" 
        :modal="false" 
        :size="isMobile?'calc(100% - 20px)':'40%'" 
        title="预览与调试"
        :with-header="true">
        <div style="margin-top: 0px;">
          <RoleChatPanel ref="roleChatPanelRef" :showDebugRunDialog="showDebugRunDialog" />
        </div>
      </el-drawer>
    </div>

  </div>
</template>

<script setup>

import { ref, onMounted, computed } from 'vue';
import { ElMessageBox } from 'element-plus';
import RoleChatPanel from '@/views/base/scene/common/chatPanel';
import MarkdownRenderer from "./components/markResultSummay";
import { htmlToPDF } from '@/utils/htmlToPDF.js';
import AIPLogo from "@/assets/logo/logo.png";

import {
  checkStatus,
  updateExamStatus,
  examAnalysis,
} from '@/api/base/im/scene/examPaperJob';

const displayImage = import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/";

// 加载状态
const reSubmit = ref(false);
const loading = ref(true);
const currentStep = ref(1);
const stepProgress = ref(0);

const allQuestions = ref([]);

// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)

// AI头像
const aiAvatar = ref(AIPLogo);

// AI对话消息
const aiMessages = ref(["正在扫描您的试卷，识别答题内容...", "检测到您的选择题答题卡填涂规范，开始批阅...",]);
const currentAIMessage = ref(aiMessages.value[0]);
const currentMessageIndex = ref(0);

const answerCheckerEngineer = ref({
  roleName: "AI阅卷专员"
});

const { examId, examineeId } = useRoute().params;
const channelStreamId = ref(null)

// 考试数据
const examData = ref({
  score: 0,
  rank: 0,
  accuracy: 0,
  timeUsed: 0,
  scoreDistribution: [],
  knowledgePoints: [],
  questions: [],
  weakPoints: [],
  suggestions: []
});

const examInfo = ref({})

const analysisResult = ref({});
const improvementSuggestions = ref([])
const weakAreas = ref([])
// const allSuggestions= ref([])

// 当前查看的题目
const showModal = ref(false);
const currentQuestion = ref({
  content: "",
  parts: [],
  correctAnswers: [],
  userAnswers: [],
  errorAnalysis: [],
  knowledge: ""
});

// 当前日期
const currentDate = computed(() => {
  const now = new Date();
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 ${now.getHours()}:${now.getMinutes().toString().padStart(2, '0')}`;
});


// 打开题目详情
const openQuestionModal = (question) => {
  currentQuestion.value = {
    content: "已知函数 f(x) = sin²x + √3sinxcosx + 2cos²x，x ∈ R。",
    parts: [
      "(1) 求函数 f(x) 的最小正周期；",
      "(2) 求函数 f(x) 的最大值和最小值，并写出取得最大值和最小值时的 x 的集合。"
    ],
    correctAnswers: [
      "(1) 函数 f(x) 的最小正周期为 π；",
      "(2) 函数 f(x) 的最大值为 5/2，此时 x 的集合为 {x | x = kπ + π/6, k ∈ Z}；最小值为 1/2，此时 x 的集合为 {x | x = kπ + 2π/3, k ∈ Z}。"
    ],
    userAnswers: [
      "(1) 函数 f(x) 的最小正周期为 2π；",
      "(2) 函数 f(x) 的最大值为 3，此时 x 的集合为 {x | x = kπ + π/4, k ∈ Z}；最小值为 0，此时 x 的集合为 {x | x = kπ + 3π/4, k ∈ Z}。"
    ],
    errorAnalysis: [
      "(1) 错误原因：在化简函数时，对三角函数的降幂公式和辅助角公式使用错误，导致周期计算错误。",
      "(2) 错误原因：由于函数化简错误，导致最大值和最小值计算错误，以及对应的 x 的集合也错误。"
    ],
    knowledge: "三角函数的化简、周期、最值"
  };
  showModal.value = true;
};

const getQuestionById = (id) => {
  let q = allQuestions.value.find(question => question.id === id);
  console.log('getQuestionById  = ' + q + ' , id = ' + id);
  return q;
}

// 计算分数等级
const getScoreLabel = (score, totalScore) => {
  const percentage = (score / totalScore) * 100;
  if (percentage >= 90) return '优秀';
  if (percentage >= 80) return '良好';
  if (percentage >= 70) return '中等';
  if (percentage >= 60) return '及格';
  return '不及格';
};

// 获取分数对应的样式类
const getScoreStampClass = (score, totalScore) => {
  const percentage = (score / totalScore) * 100;
  if (percentage >= 90) return 'excellent';
  if (percentage >= 80) return 'good';
  if (percentage >= 70) return 'medium';
  if (percentage >= 60) return 'pass';
  return 'fail';
};

// 关闭模态框
const closeModal = () => {
  showModal.value = false;
};

// 下载报告
const downloadReport = () => {
  // 模拟下载功能
  // alert("考试结果分析报告下载开始...");
  const title = examInfo.value.examName + " - " + currentDate.value;
  const content = document.getElementById('exam-result-id');

  loading.value = true
  htmlToPDF(title, content);
  loading.value = false
};

const isMobile = ref(false)

// 检测是否为移动端
const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

const openChatBox = (roleId) => {

  if (showDebugRunDialog.value) {
    return;
  }

  showDebugRunDialog.value = true;

  nextTick(() => {
    roleChatPanelRef.value.openChatBoxWithRole(roleId);
  })

}

// 添加一个标志位防止重复打开聊天框
let chatBoxOpened = false

// 检查状态并处理逻辑
const handleExamStatus = async (examId, examineeId) => {
  try {
    const status = await checkExamStatus(examId, examineeId)

    if (status === 'review_end') {  // 阅卷结束
      loading.value = false
      showDebugRunDialog.value = false
      return false // 停止轮询
    } else if (status === 'review') {  // 阅卷中
      if (!chatBoxOpened) {  // 防止重复打开聊天框
        openChatBox(answerCheckerEngineer.value.id)
        chatBoxOpened = true
      }
    } else if (status === 'examination_end') {  // 考生完成考试开始进行阅卷
      openChatBox(answerCheckerEngineer.value.id)
      chatBoxOpened = true

      examAnalysis(examId, examineeId).then(res => {  // 提交阅卷考试
        console.log('阅卷完成')
        currentAIMessage.value = res.msg
      })
    } else if (status === 'canceled') {  // 考生阅卷取异常而取消
      loading.value = false
      reSubmit.value = true

      // 提示考生需要重新提交阅卷
      ElMessageBox.confirm(
        '阅卷过程出现异常，请新提交阅卷？',
        '提示',
        {
          confirmButtonText: '重新提交',
          type: 'warning'
        }
      ).then(() => {
        // 用户点击"重新提交"的处理逻辑
        console.log('重新提交阅卷')
        loading.value = true
        reSubmit.value = false

        // 更新试卷状态为待阅卷
        updateExamStatus(examId, examineeId, 'examination_end').then(res => {
          console.log('更新试卷状态成功')

          // 启动状态轮询
          startStatusPolling(examId, examineeId)
        })

        return false // 继续轮询
      }).catch(() => {
        // 用户点击"取消"的处理逻辑
        console.log('取消重新提交')
        return false // 停止轮询
      })


      return false // 停止轮询
    }
    return true // 继续轮询
  } catch (error) {
    console.error('状态检查失败:', error)
    return true // 出错时继续轮询（可根据需要修改）
  }
}

// 启动状态轮询
const startStatusPolling = (examId, examineeId) => {
  // 立即执行第一次检查
  handleExamStatus(examId, examineeId).then(shouldContinue => {
    if (!shouldContinue) return

    // 设置定时器进行后续检查
    const pollingInterval = setInterval(async () => {
      const shouldContinue = await handleExamStatus(examId, examineeId)
      if (!shouldContinue) {
        clearInterval(pollingInterval)
      }
    }, 5000) // 每5秒检查一次
  })
}

// 单独提取的状态检查方法
const checkExamStatus = async (examId, examineeId) => {
  const res = await checkStatus(examId, examineeId)

  channelStreamId.value = res.data.result.id
  answerCheckerEngineer.value = res.data.answerCheckerEngineer
  aiAvatar.value = displayImage + res.data.answerCheckerEngineer.roleAvatar
  examInfo.value = res.data.examInfo
  allQuestions.value = res.data.result.questions

  examData.value.score = res.data.result.score

  // 模拟考试结果分析
  if (res.data.result.analysisResult) {
    analysisResult.value = res.data.result.analysisResult

    // 如果你需要分开保存
    improvementSuggestions.value = res.data.result.analysisResult.examResults.flatMap(
      item => item.improvementSuggestions || []
    )

    weakAreas.value = res.data.result.analysisResult.examResults.flatMap(
      item => item.weakAreas || []
    )
  }

  // 路径如果缺少channelStreamId参数，则添加channelStreamId
  if (channelStreamId.value && !window.location.href.includes('channelStreamId=' + channelStreamId.value)) {
    window.location.href = window.location.href + '&channelStreamId=' + channelStreamId.value
  }

  const examStatus = res.data.status
  console.log('examStatus = ' + examStatus)
  console.log('channelStreamId = ' + channelStreamId.value)

  return examStatus
}

// 组件挂载时开始模拟AI处理
onMounted(() => {
  console.log('examId = ' + examId)
  console.log('examineeId = ' + examineeId)

  checkMobile();

  // 启动状态检查轮询
  startStatusPolling(examId, examineeId)
})

</script>

<style lang="scss" scoped>
@import "@/assets/styles/scene/exam-result-analysis.scss";

.step-text {
  font-size: 16px;
  color: #333;

  .completed {
    color: #d5d5d5;
  }
}

.ai-name {
  color: #333;
  margin-top: 10px;
  font-size: 14px;
}

.answer-details-section {
  margin-top: 20px;
  
  .answer-details-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .answer-details-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px; 
    background-color: #f9f9f9 ;
    padding: 10px;
    border-radius: 5px;
    border-bottom: 0px;
    
    h3 {
      margin: 0;
      font-size: 18px;
      color: #333;
    }
  }
  
  .questions-list {
    display: flex;
    flex-direction: column;
    gap: 25px;
  }
  
  .question-item {
    padding: 15px;
    border-radius: 6px;
    background-color: #f9f9f9;
    transition: all 0.3s ease;
    
    &:hover {
      background-color: #f0f0f0;
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
    }
  }
  
  .question-header {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    font-weight: bold;
    
    .question-number {
      font-size: 16px;
      color: #333;
      margin-right: 8px;
    }
    
    .question-score {
      font-size: 14px;
      padding: 2px 8px;
      border-radius: 4px;
    }
  }
  
  .question-content,
  .question-analysis,
  .question-comment {
    margin-bottom: 15px;
    
    h4 {
      margin: 0 0 8px 0;
      font-size: 15px;
      font-weight: bold;
      color: #555;
    }
    
    p {
      margin: 0;
      font-size: 15px;

      line-height: 1.6;
      color: #666;
    }
  }
  
  .question-comment {
    p {
      font-style: italic;
      color: #888;
    }
  }
  
  // 分数样式
  .zero-score {
    color: #ff4d4f;
    background-color: rgba(255, 77, 79, 0.1);
  }
  
  .full-score {
    color: #52c41a;
    background-color: rgba(82, 196, 26, 0.1);
  }
  
  .partial-score {
    color: #faad14;
    background-color: rgba(250, 173, 20, 0.1);
  }
  
  .filter-buttons {
    display: flex;
    gap: 10px;
  }
}

$primary-color: #2c3e50;
$secondary-color: #555;
$light-gray: #f9f9f9;
$border-color: #eee;

.question-container {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.6;
  color: #333;
  padding: 15px;
  margin-left: -12px;
  margin-right: -12px;
  border-radius: 8px;
  background-color: $light-gray;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

  .title {
    color: $primary-color;
    margin-bottom: 8px;
    font-size: 16px;
    font-weight: 600;
  }

  .content {
    // margin-left: 20px;
    // margin-bottom: 15px;
    color: $secondary-color;
    font-size: 16px;
  }

  .question-section,
  .analysis-section,
  .comment-section {
    // margin-bottom: 15px;
    // padding-bottom: 15px;
    border-bottom: 1px solid $border-color;

    &:last-child {
      border-bottom: none;
      padding-bottom: 0;
    }
  }

  .comment-section {
    .content {
      font-style: italic;
      color: darken($secondary-color, 10%);
      font-size: 16px;
    }
  }


}

.zero-score {
  color: #ff4d4f;
  /* Red color for 0 score */
  font-weight: bold;
}

.full-score {
  color: #52c41a;
  /* Green color for full score */
  font-weight: bold;
}

.partial-score {
  color: #faad14;
  /* Orange color for partial score */
  font-weight: bold;
}

.question-container {
  padding: 10px;
}

.title {
  margin-bottom: 5px;
  color: #333;
  font-size: 14px;
}

.floating-button {
  display: flex;
  position: fixed;
  bottom: 20px;
  right: 20px;
  top: 20px;
  z-index: 9999;
}

// .content {
//     margin-top: 0;
//     color: #666;
//     font-size: 13px;
// }

.exam-summary-card {
  position: relative;
  overflow: hidden;
}

.summary-content-wrapper {
  position: relative;
  min-height: 180px;
}

.score-stamp {
  right: 30px;
  transform: translateY(-0%) rotate(15deg);
  width: 100px;
  margin-right: 20px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    border-radius: 50%;
    opacity: 0.1;
    z-index: -1;
  }

  .score-value {
    font-size: 36px;
    line-height: 1;
    margin-bottom: 5px;
  }

  .score-label {
    font-size: 20px;
    letter-spacing: 2px;
    text-align: center;
  }

  .stamp-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border-radius: 50%;
    // background: radial-gradient(circle at center, transparent 40%, rgba(0, 0, 0, 0.1) 100%);
    z-index: -1;
  }
}

// 不同分数等级的样式
.score-stamp.excellent {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  color: #2c3e50;

  &::before {
    background: radial-gradient(circle at 30% 30%, #ffd700 0%, transparent 70%);
  }

  .score-value {
    color: #d4af37; // 金色
  }
}

.score-stamp.good {
  background: linear-gradient(135deg, #e0f7fa 0%, #80deea 100%);
  color: #006064;

  &::before {
    background: radial-gradient(circle at 30% 30%, #4dd0e1 0%, transparent 70%);
  }
}

.score-stamp.medium {
  background: linear-gradient(135deg, #e8f5e9 0%, #a5d6a7 100%);
  color: #2e7d32;

  &::before {
    background: radial-gradient(circle at 30% 30%, #81c784 0%, transparent 70%);
  }
}

.score-stamp.pass {
  background: linear-gradient(135deg, #fff3e0 0%, #ffcc80 100%);
  color: #e65100;

  &::before {
    background: radial-gradient(circle at 30% 30%, #ffb74d 0%, transparent 70%);
  }
}

.score-stamp.fail {
  background: linear-gradient(135deg, #ffebee 0%, #ef9a9a 100%);
  color: #c62828;

  &::before {
    background: radial-gradient(circle at 30% 30%, #ef5350 0%, transparent 70%);
  }

  .score-value {
    text-decoration: line-through;
    opacity: 0.8;
  }
}

.ai-label {
    font-size: 15px;
    text-align: center;
    color: #777;
}

// 定义手机样式
@media (max-width: 768px) { 
}
  .aip-flow-drawer-content{
    width: calc(100% - 20px) !important ;
    max-width: 100% !important;
  }

</style>