<template>
    <questionContainer :question="question">
        <div class="dateTime-question-wrapper">
            <el-date-picker
                v-model="answer"
                type="date"
                placeholder="选择日期"
                size="large"
                value-format="YYYY-MM-DD"
                @change="handleAnswerChange"
            />
        </div>
    </questionContainer>
</template>

<script setup>
import { defineProps, ref, watch } from 'vue'
import questionContainer from '@/views/base/scene/examPager/common/questionContainer.vue'

const props = defineProps({
    question: {
        type: Object,
        required: true
    }
})

const emit = defineEmits(['update-handleUpdateQuestion'])

const answer = ref('')

// 初始化答案
if (props.question.answer && props.question.answer.length > 0) {
    answer.value = props.question.answer[0] || ''
}

// 监听答案变化
watch(answer, (newVal) => {
    handleAnswerChange()
})

// 处理答案变化
const handleAnswerChange = () => {
    const updatedQuestion = {
        ...props.question,
        answer: [answer.value], // 日期答案以数组形式存储
        userAnswer: answer.value
    }
    emit('update-handleUpdateQuestion', updatedQuestion)
}
</script>

<style lang="scss" scoped>
.dateTime-question-wrapper {
    padding: 10px 0;
    
    .el-date-editor {
        width: 100%;
    }
}
</style>