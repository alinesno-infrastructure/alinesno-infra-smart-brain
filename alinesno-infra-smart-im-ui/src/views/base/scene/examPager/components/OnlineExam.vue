<template>
  <div class="online-exam-container">

    <el-container>
      <el-aside width="400px" style="background: #fff;border-right: 1px solid #f5f5f5;">
        <!-- 固定顶部导航栏 -->
        <div class="exam-header">
          <div class="header-left">
            <h2 class="exam-title">{{ pagerInfo.title }}
              <el-tag type="success" effect="light">进行中</el-tag>
            </h2>
          </div>

          <div class="header-right">
            <div class="student-info">
              <div class="student-name">
                <font-awesome-icon icon="fa-solid fa-user" class="mr-1" />
                <span>考生：{{ userInfo.name }}</span>
              </div>
              <div class="exam-number">
                <font-awesome-icon icon="fa-solid fa-id-card" class="mr-1" />
                <span>考号：{{ userInfo.examineeId }}</span>
              </div>
            </div>

            <div class="timer-container">
              <font-awesome-icon icon="fa-regular fa-clock" class="timer-icon" />
              <span class="timer-text" :class="{ 
                'time-warning': timeLeft < 15 * 60,
                'time-critical': timeLeft < 5 * 60 
              }">
                {{ formattedTime }}
              </span>
            </div>
          </div>
        </div>

        <!-- 考试信息 -->
        <div class="exam-notice" id="exam-notice" shadow="never" v-if="showNotice">
          <h3 class="notice-title">考试须知</h3>
          <ul class="notice-list">
            <li class="notice-item">
              <font-awesome-icon icon="fa-solid fa-check-circle" class="notice-icon" />
              <span>本场考试共有{{ pagerInfo.questions.length }}道题目</span>
            </li>
            <li class="notice-item">
              <font-awesome-icon icon="fa-solid fa-check-circle" class="notice-icon" />
              <span>考试时间为{{ examDuration }}分钟，请合理安排答题时间</span>
            </li>
            <li class="notice-item">
              <font-awesome-icon icon="fa-solid fa-check-circle" class="notice-icon" />
              <span>考试过程中请勿刷新页面，否则可能导致答案丢失</span>
            </li>
          </ul>
        </div>

        <div class="header-center">
          <div class="exam-controls">
            <el-button class="submit-button" type="danger" size="large" @click="submitExam(false)">
              <font-awesome-icon icon="fa-solid fa-paper-plane" class="mr-1" />
              <span>提交试卷</span>
            </el-button>
          </div>
        </div>
      </el-aside>
      <el-container>
        <el-main>

          <el-scrollbar style="height:calc(100vh - 50px)">
            <!-- 主内容区 -->
            <main class="exam-main">
              <!-- 考试内容区 -->
              <div class="exam-content">
                <!-- 题目区域 - 复用现有组件系统 -->
                <div class="questions-area">
                  <div v-for="(question, index) in pagerInfo.questions" 
                    :key="question.id"
                    :id="'question-' + (index + 1)" 
                    class="question-wrapper">
                    <!-- 动态加载题目组件 -->
                    <component 
                      :is="getQuestionComponent(question.type)" 
                      :question="question" 
                      :isQuestionEdit="false"
                      :currentSelect="false" 
                      @update-handleUpdateQuestion="handleAnswerChange" />
                  </div>
                </div>
              </div>
            </main>
          </el-scrollbar>

        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPagerDetail, updatePagerQuestion } from '@/api/base/im/scene/examPaper'

import {
  validateAccount ,
  saveAccountScore
} from '@/api/base/im/scene/examPaperJob';

import { detail } from '@/api/base/im/scene/examInfoManager'

// 导入所有题目组件
import RadioSelect from '../questions/radioSelect.vue'
import Checkbox from '../questions/checkBox.vue'
import SingleLine from '../questions/singleLine.vue'
import MultiFill from '../questions/multiFill.vue'
import MultiLine from '../questions/multiLine.vue'
import ImageFile from '../questions/imageFile.vue'
import ImageRadio from '../questions/imageRadio.vue'
import ImageCheckbox from '../questions/imageCheckbox.vue'
import Location from '../questions/locationFill.vue'
import Datetime from '../questions/datetimeFill.vue'
import DynamicTable from '../questions/dynamicTable.vue'
import DescriptionPanel from '../questions/descriptionPanel.vue'

const props = defineProps({
  examId: {
    type: String,
    required: true
  },
  accountId: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['submit'])

// 考试相关状态
const route = useRoute()
const pagerInfo = reactive({
  title: '',
  desc: '',
  questions: []
})
const userInfo = reactive({
  name: '',
  examineeId: ''
})
const userAnswers = reactive({})
const markedQuestions = ref([])
const showQuestionModal = ref(false)
const showNotice = ref(true)
const examDuration = 120 // 考试时长(分钟)
const timeLeft = ref(examDuration * 60) // 剩余时间(秒)
const pagerId = ref(null)

// 格式化时间显示
const formattedTime = computed(() => {
  const hours = Math.floor(timeLeft.value / 3600)
  const minutes = Math.floor((timeLeft.value % 3600) / 60)
  const seconds = timeLeft.value % 60
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

// 根据题目类型返回对应组件
const getQuestionComponent = (type) => {
  const componentMap = {
    'radio': RadioSelect,
    'checkbox': Checkbox,
    'single-line': SingleLine,
    'multi-fill': MultiFill,
    'multi-line': MultiLine,
    'image-file': ImageFile,
    'image-radio': ImageRadio,
    'image-checkbox': ImageCheckbox,
    'location': Location,
    'datetime': Datetime,
    'dynamic-table': DynamicTable,
    'description': DescriptionPanel
  }
  return componentMap[type] || null
}

// 获取题目类型名称
const getQuestionTypeName = (type) => {
  const typeNames = {
    'radio': '单选题',
    'checkbox': '多选题',
    'single-line': '填空题',
    'multi-fill': '多行填空题',
    'multi-line': '主观题',
    'image-file': '图片题',
    'image-radio': '图片单选题',
    'image-checkbox': '图片多选题',
    'location': '位置题',
    'datetime': '时间题',
    'dynamic-table': '表格题',
    'description': '描述题'
  }
  return typeNames[type] || '未知题型'
}

// 处理答案变化
const handleAnswerChange = (updatedQuestion) => {
  if (updatedQuestion.type === 'checkbox' || updatedQuestion.type === 'image-checkbox') {
    userAnswers[updatedQuestion.id] = updatedQuestion.answers
      .filter(a => a.selected)
      .map(a => a.label)
  } else if (updatedQuestion.type === 'radio' || updatedQuestion.type === 'image-radio') {
    const selected = updatedQuestion.answers.find(a => a.selected)
    userAnswers[updatedQuestion.id] = selected ? [selected.label] : []
  } else if (updatedQuestion.type === 'single-line') {
    userAnswers[updatedQuestion.id] = updatedQuestion.answer[0] || ''
  } else if (updatedQuestion.type === 'multi-fill' || updatedQuestion.type === 'description') {
    console.log('multi-fill = ' + JSON.stringify(updatedQuestion))
    userAnswers[updatedQuestion.id] = updatedQuestion.answers || []
  } else if (updatedQuestion.type === 'multi-line') {
    userAnswers[updatedQuestion.id] = updatedQuestion.answer[0] || ''
  } else if (updatedQuestion.type === 'datetime') {
    userAnswers[updatedQuestion.id] = updatedQuestion.answer[0] || ''
  } else if (updatedQuestion.type === 'image-file') {
    userAnswers[updatedQuestion.id] = updatedQuestion.answer || []
  } else {
    userAnswers[updatedQuestion.id] = updatedQuestion
  }

  // 2. 更新 pagerInfo.questions（关键修复）
  const questionIndex = pagerInfo.questions.findIndex(q => q.id === updatedQuestion.id);
  if (questionIndex !== -1) {
    pagerInfo.questions[questionIndex] = { ...updatedQuestion }; // 替换整个对象，确保响应式更新
  }

}

// 标记/取消标记题目
const toggleMarkQuestion = (questionId) => {
  const index = markedQuestions.value.indexOf(questionId)
  if (index === -1) {
    markedQuestions.value.push(questionId)
  } else {
    markedQuestions.value.splice(index, 1)
  }
}

// 检查题目是否有答案
const hasAnswer = (questionId) => {
  return userAnswers[questionId] && ((Array.isArray(userAnswers[questionId]) && userAnswers[questionId].length > 0) || (!Array.isArray(userAnswers[questionId]) && userAnswers[questionId] !== ''))
}

// 跳转到指定题目
const scrollToQuestion = (target) => {
  let element = null
  
  if (typeof target === 'number') {
    element = document.getElementById(`question-${target}`)
  } else if (typeof target === 'string') {
    const index = pagerInfo.questions.findIndex(q => q.id === target) + 1
    if (index > 0) {
      element = document.getElementById(`question-${index}`)
    }
  }
  
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' })
    element.classList.add('unanswered-highlight')
    setTimeout(() => {
      element.classList.remove('unanswered-highlight')
    }, 2000)
  }
}

// 检查未答题目的函数
const checkUnansweredQuestions = () => {
  const unansweredQuestions = []

  console.log('userAnswers = ' + JSON.stringify(userAnswers));

  pagerInfo.questions.forEach(question => {
    let hasAnswer = false
    
    if (question.type === 'single-line' || question.type === 'multi-line' || question.type === 'datetime' || question.type === 'description') {
      hasAnswer = userAnswers[question.id] && userAnswers[question.id].trim() !== ''
    } else if (question.type === 'multi-fill') {
      // 优先从userAnswers获取，其次从question.userAnswers，最后从question.answers
      const answers = userAnswers[question.id] || question.userAnswers || question.answers || []
      console.log('Checking multi-fill answers:', JSON.stringify({
        id: question.id,
        userAnswers: userAnswers[question.id],
        questionUserAnswers: question.userAnswers,
        questionAnswers: question.answers,
        finalAnswers: answers
      }))
      
      // 只要有一个非空答案就算已回答
      hasAnswer = Array.isArray(answers) && 
                 answers.some(answer => answer && answer.trim() !== '')

    } else if (question.type === 'image-file') {
      hasAnswer = userAnswers[question.id] && 
                  Array.isArray(userAnswers[question.id]) && 
                  userAnswers[question.id].length > 0
    } else if (Array.isArray(userAnswers[question.id])) {
      hasAnswer = userAnswers[question.id].length > 0
    } else {
      hasAnswer = userAnswers[question.id] !== undefined && 
                  userAnswers[question.id] !== null && 
                  userAnswers[question.id] !== ''
    }
    
    if (!hasAnswer) {
      unansweredQuestions.push({
        id: question.id,
        index: pagerInfo.questions.findIndex(q => q.id === question.id) + 1,
        type: getQuestionTypeName(question.type),
        title: question.title || `第${pagerInfo.questions.findIndex(q => q.id === question.id) + 1}题`
      })
    }
  })

  return unansweredQuestions
}

const submitExam = async (autoSubmit = false) => {
  const unansweredQuestions = checkUnansweredQuestions()
  
  if (!autoSubmit && unansweredQuestions.length > 0) {
    try {
      await ElMessageBox({
        title: '未完成题目提醒',
        message: `
          <div>
            <p>您还有 <strong>${unansweredQuestions.length}</strong> 道题目未作答：</p>
            <ul style="max-height: 200px; overflow-y: auto; margin: 10px 0; padding-left: 20px;">
              ${unansweredQuestions.map(q => 
                `<li style="margin-bottom: 5px; cursor: pointer;" 
                    onclick="document.getElementById('question-${q.index}').scrollIntoView({behavior: 'smooth'})">
                  ${q.index}. ${q.type} - ${q.title.substring(0, 20)}${q.title.length > 20 ? '...' : ''}
                </li>`
              ).join('')}
            </ul>
            <p>确定要提交吗？</p>
          </div>
        `,
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确认提交',
        cancelButtonText: '返回检查',
        type: 'warning'
      })

      await doSubmit(autoSubmit)
    } catch (error) {
      toggleMarkQuestion(unansweredQuestions.map(q => q.id))
      return
    }
  } else {
    await doSubmit(autoSubmit)
  }
}

const doSubmit = async (autoSubmit) => {
  try {
    await ElMessageBox.confirm(
      autoSubmit ? '考试时间已结束，系统将自动提交试卷！' : '确认提交试卷吗？提交后无法修改！',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: autoSubmit ? '' : '取消',
        showCancelButton: !autoSubmit,
        type: 'warning'
      }
    )

    emit('submit', userAnswers, autoSubmit , pagerInfo)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败: ' + error.message)
    }
  }
}

// 开始考试计时器
const startExamTimer = () => {
  const timer = setInterval(() => {
    timeLeft.value--
    
    if (timeLeft.value === 15 * 60) {
      ElMessage.warning('距离考试结束还有15分钟，请抓紧时间答题！')
    }
    
    if (timeLeft.value === 5 * 60) {
      ElMessage.warning('距离考试结束还有5分钟，请尽快完成答题！')
    }
    
    if (timeLeft.value <= 0) {
      clearInterval(timer)
      submitExam(true)
    }
  }, 1000)
}

// 初始化考试数据
const initExamData = async () => {
  const res = await detail(props.examId)
  pagerInfo.title = res.data.examName
  pagerInfo.desc = '期末考试试卷'
  pagerId.value = res.data.pagerId
  
  // 从本地存储恢复用户信息
  const savedUserInfo = localStorage.getItem(`exam_user_${props.accountId}`)

  console.log('props.accountId = ' + props.accountId);
  console.log('savedUserInfo = ' + savedUserInfo);

  if (savedUserInfo) {
    Object.assign(userInfo, JSON.parse(savedUserInfo))
  }
  
  await handlePagerDetail()
  startExamTimer()
  
  // 从本地存储恢复答案
  const savedAnswers = localStorage.getItem(`exam_answers_${props.accountId}`)
  if (savedAnswers) {
    Object.assign(userAnswers, JSON.parse(savedAnswers))
    ElMessage.info('已恢复上次答题进度')
  }
}

// 获取试卷详情
const handlePagerDetail = async () => {
  const res = await getPagerDetail(pagerId.value)
  pagerInfo.id = res.data.id
  pagerInfo.title = res.data.pagerName
  pagerInfo.desc = res.data.pagerDesc
  pagerInfo.pagerType = res.data.pagerType
  pagerInfo.difficulty = res.data.difficulty
  pagerInfo.questions = res.data.questionList
}

// 定时自动保存
const autoSaveInterval = setInterval(() => {
  localStorage.setItem(`exam_answers_${props.accountId}`, JSON.stringify(userAnswers))
  localStorage.setItem(`exam_user_${props.accountId}`, JSON.stringify(userInfo))
}, 30000) // 每30秒自动保存一次

// 页面刷新/关闭处理
const handleBeforeUnload = (e) => {
  e.preventDefault()
  e.returnValue = '考试正在进行中，离开页面将自动提交试卷！'
  return e.returnValue
}

const handleUnload = async () => {
  try {
    await saveAccountScore({
      accountId: props.accountId,
      examId: props.examId,
      answers: userAnswers,
      isAutoSubmit: true,
      isAbnormal: true
    })
  } catch (error) {
    console.error('自动提交失败:', error)
  }
}

onMounted(() => {
  initExamData()
  window.addEventListener('beforeunload', handleBeforeUnload)
  window.addEventListener('unload', handleUnload)
})

onUnmounted(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload)
  window.removeEventListener('unload', handleUnload)
  clearInterval(autoSaveInterval)
})
</script>

<style lang="scss" scoped>
.online-exam-container {
  background-color: #fafafa;
  min-height: 100vh;
  display: flex;
  flex-direction: column;

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 960px;
    margin: 0 auto;
    width: 100%;
    flex-wrap: wrap;
    gap: 1rem;
    padding: 0 1.5rem;

    .header-left {
      display: flex;
      align-items: center;
      gap: 1rem;

      .exam-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1D2129;
        white-space: nowrap;
      }
    }

    .header-center {
      .exam-controls {
        display: flex;
        justify-content: center;
        gap: 1rem;
        flex-wrap: wrap;
      }
    }

  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 1.5rem;
    justify-content: space-between;

    .student-info {
      display: flex;
      flex-direction: column;
      color: #4E5969;
    }

    .timer-container {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      background-color: #F2F3F5;
      padding: 0.5rem 0.8rem;
      border-radius: 0.5rem;

      .timer-text {
        font-weight: 500;

        &.time-warning {
          color: #FF7D00;
          font-weight: bold;
        }
        
        &.time-critical {
          color: #F53F3F;
          font-weight: bold;
          animation: blink 1s infinite;
        }
      }
    }
  }

  .exam-main {
    padding: 1.5rem;
    max-width: 960px;
    margin-left: auto;
    margin-right: auto;
    margin-top: -20px;
    width: 100%;

    .exam-notice {
      margin-bottom: 1.5rem;
      padding: 10px;
    }

    .questions-area {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;

      .question-wrapper {
        background-color: #fff;
        border-radius: 0.75rem;
        padding: 0.5rem;

        .question-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 1rem;
        }

        .question-footer {
          display: flex;
          justify-content: space-between;
          margin: 1.5rem;
        }
      }
    }
  }
}

ul.notice-list {
  list-style: none;
  font-size: 14px;
  line-height: 1.5rem;
  padding: 0px;
}

.unanswered-highlight {
  animation: highlight 2s ease;
  border-left: 4px solid #F53F3F;
  padding-left: 8px;
}

@keyframes highlight {
  0% { background-color: rgba(245, 63, 63, 0.1); }
  100% { background-color: transparent; }
}

@keyframes blink {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}
</style>