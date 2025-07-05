<template>
    <questionContainer :question="question" :isQuestionEdit="props.isQuestionEdit">
        <div class="imageCheck-question-wrapper">
            <div class="imageCheck-question-items" 
                 v-for="(item, index) in props.question.answers" 
                 :key="index"
                 :class="{ 'selected': isSelected(index) }"
                 @click="handleItemClick(index)">
                <ImageAnswerCard 
                    :item="item"
                    :isQuestionEdit="props.isQuestionEdit"
                    :checked="isSelected(index)"
                    :checkType="question.selectType || 'radio'"
                    @update:item="updateAnswer(index, $event)"
                    @change="handleAnswerChange(index, $event)"
                /> 
            </div>
        </div>
    </questionContainer>
</template>

<script setup>
import { defineProps, defineEmits, ref, onMounted } from 'vue'
import questionContainer from '@/views/base/scene/examPager/common/questionContainer.vue'
import ImageAnswerCard from '../components/ImageAnswerCard.vue'

const emits = defineEmits(['updateHandleQuestionImageAnswer', 'update-handleUpdateQuestion'])

const props = defineProps({
    question: {
        type: Object,
        required: true
    },
    isQuestionEdit: {
        type: Boolean,
        default: true,
        required: false
    }
})

// 当前选中的答案索引
const selectedIndex = ref(-1)

// 检查是否选中
const isSelected = (index) => {
    return selectedIndex.value === index
}

// 处理选项点击
const handleItemClick = (index) => {
    if (props.isQuestionEdit) return
    
    selectedIndex.value = index
    updateQuestionSelection(index)
}

// 处理答案变化
const handleAnswerChange = (index, checked) => {
    if (checked) {
        selectedIndex.value = index
        updateQuestionSelection(index)
    }
}

// 更新题目选中状态
const updateQuestionSelection = (selectedIndex) => {
    const updatedQuestion = {
        ...props.question,
        answers: props.question.answers.map((item, index) => ({
            ...item,
            selected: index === selectedIndex
        }))
    }
    
    emits('update-handleUpdateQuestion', updatedQuestion)
}

// 更新答案
const updateAnswer = (index, newItem) => {
    emits('updateHandleQuestionImageAnswer', props.question.id, index, newItem)
}

onMounted(() => {
    // 默认选中answers里面的selected:true的选项值
    selectedIndex.value = props.question.answers.findIndex(item => item.selected)
})

</script>

<style lang="scss" scoped>
.imageCheck-question-wrapper {
    display: flex;
    flex-direction: row;
    gap: 10px;
    width: 100%;
    flex-wrap: wrap;

    .imageCheck-question-items {
        display: flex;
        width: calc(25% - 10px);
        flex-direction: row;
        border-radius: 3px;
        font-size: 14px;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &.selected {
            box-shadow: 0 0 0 2px #2378ff;
            border-radius: 8px;
        }

        &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
    }
}

@media (max-width: 768px) {
    .imageCheck-question-items {
        width: calc(50% - 10px) !important;
    }
}
</style>