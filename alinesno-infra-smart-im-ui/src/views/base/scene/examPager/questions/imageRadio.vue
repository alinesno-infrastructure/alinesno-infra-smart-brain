<template>
    <questionContainer :question="question">
        <div class="imageCheck-question-wrapper">
            <div class="imageCheck-question-items" v-for="(item, index) in  props.question.answers" :key="index">
                <ImageAnswerCard 
                    :item="item"
                    :checked="isAnswerChecked(index)"
                    :checkType="question.selectType || 'radio'"
                    @update:item="updateAnswer(index, $event)"
                    @change="handleAnswerChange(index, $event)"
                /> 
            </div>
        </div>
    </questionContainer>
</template>

<script setup>

import { defineProps ,defineEmits , ref } from 'vue'

import questionContainer from '@/views/base/scene/examPager/common/questionContainer.vue'
import ImageAnswerCard  from '../components/ImageAnswerCard.vue'

const emits = defineEmits(['updateHandleQuestionImageAnswer'])

const props = defineProps({
    question: {
        type: Object,
        required: true
    }
})

// 初始化选中状态
const selectedAnswers = ref([])

// 检查答案是否被选中
const isAnswerChecked = (index) => {
    return selectedAnswers.value.includes(index)
}

// 处理选中状态变化
const handleAnswerChange = (index, checked) => {
    console.log('选项已改变:', index, checked)

    let selectType  = props.question.selectType
    if(!selectType){
        selectType = 'radio'
    }

    if (selectType === 'radio') {
        // 单选逻辑
        selectedAnswers.value = checked ? [index] : []
    } else {
        // 多选逻辑
        if (checked) {
            if (!selectedAnswers.value.includes(index)) {
                selectedAnswers.value.push(index)
            }
        } else {
            selectedAnswers.value = selectedAnswers.value.filter(i => i !== index)
        }
    }
}

// 添加更新答案的方法
const updateAnswer = (index, newItem) => {
    console.log('updateAnswer index = ' + index + ' newItem = ' + JSON.stringify(newItem))
    // props.question.answers[index] = { ...newItem }
    emits('updateHandleQuestionImageAnswer', props.question.id , index, newItem)
}

</script>

<style lang="scss" scoped>

.imageCheck-question-wrapper {
    display: flex;
    flex-direction: row;
    gap: 10px;
    width: 100%;

    .imageCheck-question-items {
        display: flex;
        width: 25%;
        flex-direction: row;
        // padding: 10px;
        gap: 5px;
        border-radius: 3px;
        font-size: 14px;

        .imageCheck-question-item-content {
            background: #f5f5f5;
            img {
                width:100%;
                border-radius: 5px;;
            }
            .text {
                padding: 5px;
            }
        }
    }
}

</style>