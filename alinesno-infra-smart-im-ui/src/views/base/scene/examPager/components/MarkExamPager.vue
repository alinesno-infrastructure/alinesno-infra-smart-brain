<template>
    <div class="online-exam-container">

        <!-- 考试内容区 -->
        <div class="exam-content">
            <!-- 题目区域 - 复用现有组件系统 -->
            <div class="questions-area">
                <div v-for="(question, index) in pagerInfo.questions" :key="question.id" :id="'question-' + (index + 1)"
                    class="question-wrapper">
                    <!-- 动态加载题目组件 -->
                    <component :is="getQuestionComponent(question.type)" :question="question" :isQuestionEdit="false"
                        :currentSelect="false" @update-handleUpdateQuestion="handleAnswerChange" />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPagerDetail, updatePagerQuestion } from '@/api/base/im/scene/examPaper'

import {
    validateAccount,
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
        userAnswers[updatedQuestion.id] = updatedQuestion.answer || []
    } else if (updatedQuestion.type === 'multi-line') {
        userAnswers[updatedQuestion.id] = updatedQuestion.answer[0] || ''
    } else if (updatedQuestion.type === 'datetime') {
        userAnswers[updatedQuestion.id] = updatedQuestion.answer[0] || ''
    } else if (updatedQuestion.type === 'image-file') {
        userAnswers[updatedQuestion.id] = updatedQuestion.answer || []
    } else {
        userAnswers[updatedQuestion.id] = updatedQuestion
    }
}

// 获取试卷详情
const handlePagerDetail = async (examInfoData) => {

    console.log('examInfoData=' + JSON.stringify(examInfoData));

    pagerInfo.id = examInfoData.id
    pagerInfo.title = examInfoData.examName
    pagerInfo.desc = examInfoData.examName
    pagerInfo.pagerType = examInfoData.pagerType
    pagerInfo.difficulty = examInfoData.difficulty
    pagerInfo.questions = examInfoData.questions

    userInfo.value.name = examInfoData.student.userName ; 
    userInfo.value.examineeId = examInfoData.student.userCode ; 
}

defineExpose({
    handlePagerDetail
})

</script>

<style lang="scss" scoped>
.online-exam-container {
    background-color: #fafafa;
    border-radius: 10px;
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
    0% {
        background-color: rgba(245, 63, 63, 0.1);
    }

    100% {
        background-color: transparent;
    }
}

@keyframes blink {
    0% {
        opacity: 1;
    }

    50% {
        opacity: 0.5;
    }

    100% {
        opacity: 1;
    }
}
</style>