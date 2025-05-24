<template>
    <questionContainer :question="question">

        <div class="multiFill-question-wrapper" v-for="(item, index) in props.question.blanks" :key="index">

            <el-input 
                @click="handleSelectedValue(index)" 
                class="multiFill-question-item" 
                v-model="answer" 
                size="large"
                placeholder="请输入内容">
            </el-input>

            <el-button v-if="selectedValue === index" type="text" @click="haddleDeleteAnswerItem(index)"
                class="delete-button">
                删除
            </el-button>
        </div>
    </questionContainer>
</template>

<script setup>

import { defineProps, ref,defineEmits } from 'vue'
import { ElMessageBox } from 'element-plus'
import questionContainer from '@/views/base/scene/examPager/common/questionContainer.vue'

const emit = defineEmits(['updateHaddleDeleteAnswerItem'])
const answer = ref('')

const props = defineProps({
    question: {
        type: Object,
        required: true
    }
})

const selectedValue = ref(-1)

const handleSelectedValue = (index) => {
    selectedValue.value = index
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
    emit('updateHaddleDeleteAnswerItem', props.question.id, index , 'blank')
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

    .multiFill-question-item {
        width: calc(100% - 40px);
        margin-bottom: 10px;
        margin-right: 5px;
    }

}
</style>