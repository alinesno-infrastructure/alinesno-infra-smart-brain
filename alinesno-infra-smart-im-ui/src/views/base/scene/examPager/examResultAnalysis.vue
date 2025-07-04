<template>
  <!-- 加载遮罩层 -->
  <div v-if="loading" class="loading-overlay">
    <div class="loading-content">
      <div class="ai-avatar">
        <img :src="aiAvatar" alt="AI阅卷助手" class="avatar-img">
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
  <div class="exam-result-container" :class="{ blur: loading }">
    <!-- 主要内容区 -->
    <main class="container">
      <!-- 考试总结卡片 -->
      <section class="exam-summary-section">
        <div class="exam-summary-card">
          <div class="summary-header">
            <h2>高三数学模拟考试分析报告</h2>
            <div class="exam-meta">
              <span>考试日期: 2025年6月15日</span>
              <span>考试时长: 120分钟</span>
              <span>总分: 150分</span>
              <span>你的得分: {{ examData.score }}分</span>
            </div>
          </div>
          <div class="summary-content">
            <p>亲爱的同学，本次模拟考试你取得了{{ examData.score }}分的成绩（满分150分），在班级排名第{{ examData.rank }}名。整体表现{{ getPerformanceLevel()
              }}，正确率达到{{ examData.accuracy }}%。</p>

            <p>
              从试卷分析来看，你在<strong>集合与简易逻辑</strong>、<strong>函数与导数</strong>等基础知识点上掌握扎实，得分率超过85%，展现了良好的基本功。但在<strong>三角函数</strong>和<strong>立体几何</strong>部分表现相对薄弱，特别是三角函数的综合应用题得分率仅为40%，这反映出你在复杂问题的公式转换和计算准确性上还需加强。
            </p>

            <p>值得肯定的是，你的解题思路清晰，步骤完整，在解答题部分获得了较高的过程分。时间管理方面，你用时{{ examData.timeUsed }}分钟完成全部试题，{{ getTimeUsageComment()
              }}。</p>

            <p>基于本次考试表现，建议你：1) 每天安排30分钟专项练习三角函数综合题；2) 整理错题本，重点分析几何证明题的解题思路；3)
              定期进行限时训练，提高解题速度。相信通过有针对性的训练，你在下次考试中一定能取得更大进步！</p>
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

      <!-- 成绩分析文本展示 -->
      <section class="text-display-section">
        <div class="text-display-card">
          <h3>得分分布</h3>
          <div class="text-display-content">
            <p v-for="(item, index) in examData.scoreDistribution" :key="index">
              <strong>{{ item.type }}：</strong>得分率{{ item.rate }}%
            </p>
          </div>
        </div>
        <div class="text-display-card">
          <h3>知识点掌握情况</h3>
          <div class="text-display-content">
            <p v-for="(item, index) in examData.knowledgePoints" :key="index">
              <strong>{{ item.name }}：</strong>掌握程度{{ item.mastery }}%
            </p>
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

          <div class="table-container">
            <table>
              <thead>
                <tr>
                  <th>题号</th>
                  <th>题型</th>
                  <th>知识点</th>
                  <th>难度</th>
                  <th>分数</th>
                  <th>得分</th>
                  <th>状态</th>
                  <th align="center">详情</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in examData.questions" :key="item.id">
                  <td>{{ item.id }}</td>
                  <td>{{ item.type }}</td>
                  <td>{{ item.knowledge }}</td>
                  <td>
                    <span :class="['difficulty-tag', item.difficultyClass]">
                      {{ item.difficulty }}
                    </span>
                  </td>
                  <td>{{ item.totalScore }}</td>
                  <td>{{ item.score }}</td>
                  <td>
                    <span :class="['status-tag', item.statusClass]">
                      {{ item.status }}
                    </span>
                  </td>
                  <td>
                    <el-button type="primary" text @click="openQuestionModal(item)">查看</el-button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>

      <!-- 薄弱知识点分析 -->
      <section class="weak-knowledge-section">
        <div class="weak-knowledge-card">
          <h3>薄弱知识点分析</h3>
          <div class="knowledge-grid">
            <div class="knowledge-item" v-for="knowledge in examData.weakPoints" :key="knowledge.id">
              <h4>{{ knowledge.title }}</h4>
              <p>{{ knowledge.description }}</p>
              <div class="progress-info">
                <span>得分率</span>
                <span>{{ knowledge.scoreRate }}%</span>
              </div>
              <div class="progress-bar">
                <div :style="{ width: knowledge.scoreRate + '%', 'background-color': knowledge.color }"></div>
              </div>
              <div class="action-link">
                <el-button type="primary" text>查看练习推荐</el-button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 学习建议 -->
      <section class="suggestions-section">
        <div class="suggestions-card">
          <h3>学习建议</h3>
          <div class="suggestions-list">
            <div class="suggestion-item" v-for="(suggestion, index) in examData.suggestions" :key="index">
              <div class="suggestion-icon">
                <i class="fa fa-check-circle"></i>
              </div>
              <div class="suggestion-content">
                <h4>{{ suggestion.title }}</h4>
                <p>{{ suggestion.content }}</p>
              </div>
            </div>
          </div>
          <div class="suggestion-action">
            <el-button size="large" type="primary" @click="downloadReport">下载考试结果分析</el-button>
          </div>
        </div>
      </section>
    </main>

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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';

// 加载状态
const loading = ref(true);
const currentStep = ref(1);
const stepProgress = ref(0);

// AI头像
const aiAvatar = ref('http://localhost:30304/v1/api/infra/base/im/chat/displayImage/1934609190684893186');

// AI对话消息
const aiMessages = ref([
  "正在扫描您的试卷，识别答题内容...",
  "检测到您的选择题答题卡填涂规范，开始批阅...",
  "发现第3题三角函数部分可能有计算错误，正在复核...",
  "您的解答题步骤完整，正在给予过程分...",
  "已完成客观题批改，开始主观题评分...",
  "正在分析您的答题模式和学习薄弱点...",
  "生成个性化学习建议中，请稍候..."
]);
const currentAIMessage = ref(aiMessages.value[0]);
const currentMessageIndex = ref(0);

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

// 性能等级评价
const getPerformanceLevel = () => {
  const score = examData.value.score;
  if (score >= 135) return "非常优秀";
  if (score >= 120) return "良好";
  if (score >= 90) return "中等";
  return "有待提高";
};

// 时间使用评价
const getTimeUsageComment = () => {
  const timeUsed = examData.value.timeUsed;
  if (timeUsed < 90) return "答题速度很快，但要注意检查准确率";
  if (timeUsed < 110) return "时间分配合理，保持了良好节奏";
  return "部分题目耗时较长，建议加强时间管理";
};

// 模拟AI处理过程
const simulateAIProcessing = () => {
  // 切换AI消息
  const messageInterval = setInterval(() => {
    currentMessageIndex.value = (currentMessageIndex.value + 1) % aiMessages.value.length;
    currentAIMessage.value = aiMessages.value[currentMessageIndex.value];
  }, 2500);

  // 第一步：阅卷
  const step1Interval = setInterval(() => {
    stepProgress.value += Math.random() * 5;
    if (stepProgress.value >= 100) {
      clearInterval(step1Interval);
      currentStep.value = 2;
      stepProgress.value = 0;

      // 第二步：评分
      const step2Interval = setInterval(() => {
        stepProgress.value += Math.random() * 5;
        if (stepProgress.value >= 100) {
          clearInterval(step2Interval);
          currentStep.value = 3;
          stepProgress.value = 0;

          // 第三步：分析
          const step3Interval = setInterval(() => {
            stepProgress.value += Math.random() * 5;
            if (stepProgress.value >= 100) {
              clearInterval(step3Interval);
              clearInterval(messageInterval);
              loading.value = false;
              loadExamData();
            }
          }, 100);
        }
      }, 100);
    }
  }, 100);
};

// 加载考试数据
const loadExamData = () => {
  examData.value = {
    score: 128,
    rank: 5,
    accuracy: 85.3,
    timeUsed: 105,
    scoreDistribution: [
      { type: "选择题", rate: 90 },
      { type: "填空题", rate: 75 },
      { type: "解答题", rate: 65 },
      { type: "证明题", rate: 80 },
      { type: "应用题", rate: 70 }
    ],
    knowledgePoints: [
      { name: "集合与简易逻辑", mastery: 90 },
      { name: "函数与导数", mastery: 85 },
      { name: "三角函数", mastery: 40 },
      { name: "数列", mastery: 75 },
      { name: "立体几何", mastery: 60 },
      { name: "解析几何", mastery: 70 },
      { name: "概率统计", mastery: 80 }
    ],
    questions: [
      {
        id: 1,
        type: "选择题",
        knowledge: "集合与简易逻辑",
        difficulty: "简单",
        difficultyClass: "easy",
        totalScore: 5,
        score: 5,
        status: "正确",
        statusClass: "correct"
      },
      {
        id: 2,
        type: "选择题",
        knowledge: "函数与导数",
        difficulty: "中等",
        difficultyClass: "medium",
        totalScore: 5,
        score: 5,
        status: "正确",
        statusClass: "correct"
      },
      {
        id: 3,
        type: "选择题",
        knowledge: "三角函数",
        difficulty: "困难",
        difficultyClass: "hard",
        totalScore: 5,
        score: 0,
        status: "错误",
        statusClass: "wrong"
      },
      {
        id: 4,
        type: "填空题",
        knowledge: "数列",
        difficulty: "中等",
        difficultyClass: "medium",
        totalScore: 5,
        score: 3,
        status: "部分正确",
        statusClass: "partial"
      },
      {
        id: 5,
        type: "解答题",
        knowledge: "立体几何",
        difficulty: "困难",
        difficultyClass: "hard",
        totalScore: 12,
        score: 8,
        status: "部分正确",
        statusClass: "partial"
      }
    ],
    weakPoints: [
      {
        id: 1,
        title: "三角函数",
        description: "本次考试中，三角函数部分得分率较低，建议加强对基本公式的记忆和应用练习。",
        scoreRate: 40,
        color: "#ef4444"
      },
      {
        id: 2,
        title: "立体几何",
        description: "空间想象能力和几何证明能力需要提高，建议多做一些空间几何图形的练习。",
        scoreRate: 60,
        color: "#eab308"
      }
    ],
    suggestions: [
      {
        title: "加强三角函数练习",
        content: "针对三角函数部分的薄弱环节，建议每天进行30分钟的专项练习，重点掌握三角函数的图像与性质、三角恒等变换等内容。"
      },
      {
        title: "提高解题速度",
        content: "在保证答题正确率的前提下，需要提高解题速度。建议进行限时训练，逐步提高解题效率。"
      },
      {
        title: "整理错题集",
        content: "将本次考试中的错题整理到错题集中，分析错误原因，总结解题方法和技巧，定期进行复习。"
      }
    ]
  };
};

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

// 关闭模态框
const closeModal = () => {
  showModal.value = false;
};

// 下载报告
const downloadReport = () => {
  // 模拟下载功能
  alert("考试结果分析报告下载开始...");
};

// 组件挂载时开始模拟AI处理
onMounted(() => {
  simulateAIProcessing();
});
</script>

<style lang="scss" scoped>
@import "@/assets/styles/scene/exam-result-analysis.scss";
</style>