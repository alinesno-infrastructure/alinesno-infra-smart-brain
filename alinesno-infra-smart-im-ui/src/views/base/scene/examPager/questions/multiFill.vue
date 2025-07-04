<template>
    <questionContainer :question="question" :isQuestionEdit="props.isQuestionEdit">
        <div class="multiFill-question-wrapper" v-for="(blank, index) in props.question.blanks" :key="index">
            <el-input 
                class="multiFill-question-item" 
                v-model="userAnswers[index]" 
                size="large"
                :placeholder="`请输入第${blank.index}空内容`"
                @change="handleAnswerChange(index)">
            </el-input>

            <el-button 
                v-if="selectedValue === index && props.isQuestionEdit" 
                type="text" 
                @click="haddleDeleteAnswerItem(index)"
                class="delete-button">
                删除
            </el-button>
        </div>
    </questionContainer>
</template>

<script setup>
import { defineProps, ref, watch } from 'vue'
import { ElMessageBox } from 'element-plus'
import questionContainer from '@/views/base/scene/examPager/common/questionContainer.vue'

const emit = defineEmits(['update-handleUpdateQuestion', 'updateHaddleDeleteAnswerItem'])

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

const selectedValue = ref(-1)
const userAnswers = ref([])

// 初始化用户答案
const initializeAnswers = () => {
    // 从question.answers恢复已有答案，或初始化为空字符串
    userAnswers.value = props.question.blanks.map((blank, index) => {
        return props.question.answers && props.question.answers[index] 
            ? props.question.answers[index] 
            : ''
    })
}

// 初始化
initializeAnswers()

// 监听question变化，当数据更新时重新初始化
watch(() => props.question, initializeAnswers, { deep: true })

const handleSelectedValue = (index) => {
    selectedValue.value = index
}

// 处理答案变化
const handleAnswerChange = (index) => {
    const updatedQuestion = {
        ...props.question,
        answers: [...userAnswers.value], // 更新所有填空的答案
        userAnswers: [...userAnswers.value] // 同时保存到userAnswers字段
    }
    emit('update-handleUpdateQuestion', updatedQuestion)
}

// 删除问题指定答案
const haddleDeleteAnswerItem = async (index) => {
    try {
        await ElMessageBox.confirm(
            '确定要删除这个回答吗？',
            '删除确认',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )
        // 用户确认删除
        userAnswers.value[index] = '' // 清空该填空的答案
        handleAnswerChange(index) // 触发更新
        emit('updateHaddleDeleteAnswerItem', props.question.id, index, 'blank')
    } catch (error) {
        // 用户取消删除
        console.log('用户取消了删除操作')
    }
}
</script>

<style lang="scss" scoped>
.multiFill-question-wrapper {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    margin-bottom: 10px;

    .multiFill-question-item {
        width: calc(100% - 40px);
        margin-right: 5px;
    }

    .delete-button {
        width: 30px;
    }
}
</style>