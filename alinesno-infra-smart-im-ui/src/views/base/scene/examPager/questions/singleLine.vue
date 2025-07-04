<template>
    <questionContainer :question="question">
        <div class="singleLine-question-wrapper">
            <el-input 
                v-model="answer" 
                size="large"
                placeholder="请输入内容"
                @change="handleAnswerChange"></el-input>
        </div>
    </questionContainer>
</template>

<script setup>

import { defineProps , ref } from 'vue'
import questionContainer from '@/views/base/scene/examPager/common/questionContainer.vue'

const emit = defineEmits(['update-handleUpdateQuestion'])

const answer = ref('')

const props = defineProps({
    question: {
        type: Object,
        required: true
    }
})

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
        answer: [answer.value], // 填空题答案以数组形式存储，即使只有一个空
        userAnswer: answer.value
    }

    console.log("更新后的question: " + JSON.stringify(updatedQuestion))

    console.log("props.question: " + JSON.stringify(props.question))
    emit('update-handleUpdateQuestion', updatedQuestion)
}


</script>

<style lang="scss" scoped>
.singleLine-question-wrapper {
    padding: 10px 0;
}
</style>