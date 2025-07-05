<template>
    <questionContainer :question="question" :isQuestionEdit="props.isQuestionEdit">
        <div class="descriptionPanel-question-wrapper">
            <div class="question-title" v-if="question.title">{{ question.title }}</div>
            <div class="question-desc" v-if="question.desc">{{ question.desc }}</div>
            <el-input 
                type="textarea" 
                :placeholder="placeholderText"
                :rows="8"
                resize="none"
                v-model="answer" 
                size="large"
                @change="handleAnswerChange"
                @input="handleInputChange"
                :disabled="isQuestionEdit"
                class="description-textarea">
            </el-input>
            
            <div class="word-count" v-if="maxLength > 0" :class="{ 'limit-exceeded': isOverLimit }">
                {{ answer.length }}/{{ maxLength }}
            </div>
        </div>
    </questionContainer>
</template>

<script setup>
import { defineProps, defineEmits, ref, computed, watch } from 'vue'
import questionContainer from '@/views/base/scene/examPager/common/questionContainer.vue'

const emit = defineEmits(['update:question'])

const props = defineProps({
    question: {
        type: Object,
        required: true,
        default: () => ({
            id: '',
            type: 'description',
            title: '',
            desc: '',
            answer: '',
            maxLength: 0
        })
    },
    isQuestionEdit: {
        type: Boolean,
        default: false
    }
})

const answer = ref(props.question.answer || '')
const maxLength = computed(() => props.question.maxLength || 0)
const isOverLimit = computed(() => maxLength.value > 0 && answer.value.length > maxLength.value)

const placeholderText = computed(() => {
    return maxLength.value > 0 
        ? `请输入内容 (最多${maxLength.value}字)`
        : '请输入内容'
})

// 处理输入变化（实时）
const handleInputChange = () => {
    if (maxLength.value > 0 && answer.value.length > maxLength.value) {
        answer.value = answer.value.substring(0, maxLength.value)
    }
}

// 处理答案变化（失焦或回车时触发）
const handleAnswerChange = () => {
    const updatedQuestion = {
        ...props.question,
        answer: answer.value
    }
    
    emit('update:question', updatedQuestion)
    // 同时触发父组件可能监听的标准事件
    emit('update-handleUpdateQuestion', updatedQuestion)
}

// 监听答案变化
watch(answer, (newVal) => {
    handleAnswerChange()
})


// // 监听question变化（用于重置答案等场景）
// watch(() => props.question, (newQuestion) => {
//     if (newQuestion.answer !== answer.value) {
//         answer.value = newQuestion.answer || ''
//     }
// }, { deep: true })

</script>

<style lang="scss" scoped>
.descriptionPanel-question-wrapper {
    padding: 16px;
    background: #fff;
    border-radius: 8px;
    
    .question-title {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 12px;
        color: #333;
    }
    
    .question-desc {
        font-size: 14px;
        color: #666;
        margin-bottom: 16px;
        line-height: 1.5;
    }
    
    .description-textarea {
        :deep(.el-textarea__inner) {
            font-size: 14px;
            line-height: 1.5;
            padding: 12px;
            box-shadow: none;
            border: 1px solid #dcdfe6;
            
            &:focus {
                border-color: #409eff;
            }
        }
    }
    
    .word-count {
        text-align: right;
        font-size: 12px;
        color: #999;
        margin-top: 4px;
        
        &.limit-exceeded {
            color: #f56c6c;
        }
    }
}
</style>