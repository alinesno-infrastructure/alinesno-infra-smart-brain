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
                <span>考号：{{ userInfo.studentId }}</span>
              </div>
            </div>

            <div class="timer-container">
              <font-awesome-icon icon="fa-regular fa-clock" class="timer-icon" />
              <span class="timer-text" :class="{ 'time-warning': timeLeft < 15 * 60 }">
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
import { ref, reactive, computed, onMounted , onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import {
  getPagerDetail,
  updatePagerQuestion,
} from '@/api/base/im/scene/examPaper';

import {
  detail,
} from '@/api/base/im/scene/examInfoManager';

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

// 考试相关状态
const route = useRoute()
const pagerInfo = reactive({
  title: '',
  desc: '',
  questions: []
})
const userInfo = reactive({
  name: '张三',
  studentId: '20250001'
})
const userAnswers = reactive({})
const markedQuestions = ref([])
const showQuestionModal = ref(false)
const showNotice = ref(true)
const examDuration = 120 // 考试时长(分钟)
const timeLeft = ref(examDuration * 60) // 剩余时间(秒)

const pagerId = ref(null)
const examId = ref(route.params.examId)

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
    'multi-line': '主观题', // 多行填空题显示为主观题
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

// 检查题目是否有答案
const hasAnswer = (questionId) => {
  return userAnswers[questionId] && ((Array.isArray(userAnswers[questionId]) && userAnswers[questionId].length > 0) || (!Array.isArray(userAnswers[questionId]) && userAnswers[questionId] !== ''))
}

// // 处理答案变化
// const handleAnswerChange = (updatedQuestion) => {
//   // 根据题目类型提取答案
//   if (updatedQuestion.type === 'radio') {
//     const selected = updatedQuestion.answers.find(a => a.selected)
//     userAnswers[updatedQuestion.id] = selected ? [selected.id] : []
//   } else if (updatedQuestion.type === 'checkbox') {
//     userAnswers[updatedQuestion.id] = updatedQuestion.answers
//       .filter(a => a.selected)
//       .map(a => a.id)
//   } else {
//     // 其他题型直接保存整个question对象
//     userAnswers[updatedQuestion.id] = updatedQuestion
//   }
// }

// 处理答案变化
const handleAnswerChange = (updatedQuestion) => {

  console.log("updateQuestion = " + JSON.stringify(updatedQuestion))

  // 根据题目类型提取答案
  if (updatedQuestion.type === 'checkbox' || updatedQuestion.type === 'image-checkbox' ) {
    // 对于多选题，保存所有选中的选项ID
    userAnswers[updatedQuestion.id] = updatedQuestion.answers
      .filter(a => a.selected)
      .map(a => a.label)
  } else if (updatedQuestion.type === 'radio' || updatedQuestion.type === 'image-radio' ) {
    // 对于单选题，保存选中的选项ID
    const selected = updatedQuestion.answers.find(a => a.selected)
    userAnswers[updatedQuestion.id] = selected ? [selected.label] : []
  } else if (updatedQuestion.type === 'single-line') {
        // 对于单填空题，保存用户输入的答案
        userAnswers[updatedQuestion.id] = updatedQuestion.answer[0] || ''
    } else if (updatedQuestion.type === 'multi-fill' || updatedQuestion.type === 'description') {
        // 对于多填空题，保存用户输入的答案数组
        userAnswers[updatedQuestion.id] = updatedQuestion.answer || []
    } else if (updatedQuestion.type === 'multi-line') {
        // 对于多行填空题，保存用户输入的答案
        userAnswers[updatedQuestion.id] = updatedQuestion.answer[0] || ''
    } else if (updatedQuestion.type === 'datetime') {
        // 对于日期题，保存用户选择的日期
        userAnswers[updatedQuestion.id] = updatedQuestion.answer[0] || ''
    } else if (updatedQuestion.type === 'image-file') {
        // 对于图片上传题，保存图片URL数组
        userAnswers[updatedQuestion.id] = updatedQuestion.answer || []
    } else {
    // 其他题型直接保存整个question对象
    userAnswers[updatedQuestion.id] = updatedQuestion
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

// // 跳转到指定题目
// const scrollToQuestion = (index) => {
//   const element = document.getElementById(`question-${index}`)
//   if (element) {
//     element.scrollIntoView({ behavior: 'smooth', block: 'start' })
//     // 调整滚动位置，考虑固定头部的高度
//     window.scrollBy(0, -80)
//   }
// }

// 切换题目列表模态框
const toggleQuestionModal = () => {
  showQuestionModal.value = !showQuestionModal.value
}

// 开始考试计时器
const startExamTimer = () => {
  const timer = setInterval(() => {
    timeLeft.value--

    if (timeLeft.value <= 0) {
      clearInterval(timer)
      submitExam(true)
    }
  }, 1000)
}

// 提交试卷
// const submitExam = (autoSubmit = false) => {
//   const confirmMessage = autoSubmit
//     ? '考试时间已结束，系统将自动提交试卷！'
//     : '确认提交试卷吗？提交后无法修改！'

//   ElMessageBox.confirm(confirmMessage, '提示', {
//     confirmButtonText: '确定',
//     cancelButtonText: autoSubmit ? '' : '取消',
//     showCancelButton: !autoSubmit,
//     type: 'warning'
//   }).then(() => {
//     // 这里添加提交逻辑
//     console.log('提交的答案:', userAnswers)
//     ElMessage.success('试卷提交成功！')
//     // 跳转到结果页面或其他处理
//   }).catch(() => {
//     if (!autoSubmit) {
//       ElMessage.info('已取消提交')
//     }
//   })
// }

// 初始化考试数据
const initExamData = async () => {
  // 这里应该是从API获取考试数据

  const res = await detail(examId.value);
  pagerInfo.title = res.data.examName
  pagerInfo.desc = '期末考试试卷'

  pagerId.value = res.data.pagerId;
  console.log('pagerId = ' + pagerId.value)

  handlePagerDetail();
  startExamTimer()
}

// 获取试卷详情
const handlePagerDetail = async () => {
  console.log('pagerId = ' + pagerId.value)

  const res = await getPagerDetail(pagerId.value)

  pagerInfo.id = res.data.id
  pagerInfo.title = res.data.pagerName
  pagerInfo.desc = res.data.pagerDesc
  pagerInfo.pagerType = res.data.pagerType
  pagerInfo.difficulty = res.data.difficulty
  pagerInfo.questions = res.data.questionList

}

// 在题目导航组件中
const questionStatus = (questionId) => {
  if (markedQuestions.value.includes(questionId)) {
    return 'marked'
  } else if (hasAnswer(questionId)) {
    return 'answered'
  } else {
    return 'unanswered'
  }
}

// 跳转到指定题目
const scrollToQuestion = (target) => {
  let element = null
  
  // 如果是数字，认为是题目索引
  if (typeof target === 'number') {
    element = document.getElementById(`question-${target}`)
  } 
  // 如果是字符串，认为是题目ID
  else if (typeof target === 'string') {
    const index = pagerInfo.questions.findIndex(q => q.id === target) + 1
    if (index > 0) {
      element = document.getElementById(`question-${index}`)
    }
  }
  
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'start' })
    // 添加高亮效果
    element.classList.add('unanswered-highlight')
    setTimeout(() => {
      element.classList.remove('unanswered-highlight')
    }, 2000)
  }
}

// 检查未答题目的函数
const checkUnansweredQuestions = () => {
  const unansweredQuestions = []

  pagerInfo.questions.forEach(question => {
        // 检查题目是否有答案
        let hasAnswer = false
        
        if (question.type === 'single-line' || question.type === 'multi-line' || question.type === 'datetime' || question.type === 'description') {
            // 单填空题检查是否有非空字符串
            hasAnswer = userAnswers[question.id] && userAnswers[question.id].trim() !== ''
        } else if (question.type === 'multi-fill' ) {
            // 多填空题检查是否所有空都已填写
            hasAnswer = userAnswers[question.id] && 
                        Array.isArray(userAnswers[question.id]) && 
                        userAnswers[question.id].length > 0 &&
                        userAnswers[question.id].every(answer => answer.trim() !== '')
        } else if (question.type === 'image-file') {
            // 图片上传题检查是否有上传文件
            hasAnswer = userAnswers[question.id] && 
                        Array.isArray(userAnswers[question.id]) && 
                        userAnswers[question.id].length > 0
        } else if (Array.isArray(userAnswers[question.id])) {
            // 选择题检查是否有选中项
            hasAnswer = userAnswers[question.id].length > 0
        } else {
            // 其他题型检查是否有非空值
            hasAnswer = userAnswers[question.id] !== undefined && 
                        userAnswers[question.id] !== null && 
                        userAnswers[question.id] !== ''
        }
        
        // 如果没有答案，记录题目信息
        if (!hasAnswer) {
            unansweredQuestions.push({
                id: question.id,
                index: pagerInfo.questions.findIndex(q => q.id === question.id) + 1,
                type: getQuestionTypeName(question.type),
                title: question.title || `第${pagerInfo.questions.findIndex(q => q.id === question.id) + 1}题`
            })
        }
    })

  console.log('unansweredQuestions = ' + JSON.stringify(unansweredQuestions))
  
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
      console.log('用户已取消!')
      // 用户取消，标记未答题目toggleMarkQuestion
      toggleMarkQuestion(unansweredQuestions.map(q => q.id))
      return
    }
  } else {
    // 没有未答题目或自动提交
    await doSubmit(autoSubmit)
  }
}

// 实际提交逻辑
const doSubmit = async (autoSubmit) => {
  console.log('autoSubmit = ' + autoSubmit)
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

    // 提交逻辑.
    console.log('pagerInfo = ' + JSON.stringify(pagerInfo));
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交失败: ' + error.message)
    }
  }
}

// 定时自动保存
const autoSaveInterval = setInterval(() => {
  const unansweredCount = checkUnansweredQuestions().length
  console.log('自动保存中...', `未答题: ${unansweredCount}`)
  // 这里可以调用自动保存API
}, 300000) // 每5分钟自动保存一次

onMounted(() => {
  initExamData()
})

// 组件卸载时清除定时器
onUnmounted(() => {
  clearInterval(autoSaveInterval)
})

</script>

<style lang="scss" scoped>
/* 保留原有的样式，根据需要进行调整 */
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
          color: #F53F3F;
          font-weight: bold;
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

/* 题目列表模态框样式 */
:deep(.question-modal) {
  .el-dialog__body {
    padding: 1.25rem;

    .questions-grid {
      display: grid;
      grid-template-columns: repeat(5, minmax(0, 1fr));
      gap: 0.75rem;

      @media (min-width: 640px) {
        grid-template-columns: repeat(10, minmax(0, 1fr));
      }

      .question-number {
        display: flex;
        flex-direction: column;
        align-items: center;
        cursor: pointer;

        .number-circle {
          width: 2.5rem;
          height: 2.5rem;
          border-radius: 9999px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-weight: 500;

          &.answered {
            background-color: #165DFF;
            color: #FFFFFF;
          }

          &.marked {
            background-color: #FF7D00;
            color: #FFFFFF;
          }

          &.unanswered {
            background-color: #E5E6EB;
            color: #4E5969;
          }
        }
      }
    }

    .legend-section {
      margin-top: 1.5rem;

      .legend-items {
        display: flex;
        flex-wrap: wrap;
        gap: 1rem;
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

/* 未答题高亮效果 */
.unanswered-highlight {
  animation: highlight 2s ease;
  border-left: 4px solid #F53F3F;
  padding-left: 8px;
}

@keyframes highlight {
  0% { background-color: rgba(245, 63, 63, 0.1); }
  100% { background-color: transparent; }
}
</style>