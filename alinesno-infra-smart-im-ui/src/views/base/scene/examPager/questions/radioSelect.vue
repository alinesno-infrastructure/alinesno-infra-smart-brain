<template>
    <questionContainer :question="question" :isQuestionEdit="props.isQuestionEdit">
        <div class="radio-question-wrapper"> 

            <div v-for="(item, index) in props.question.answers" 
                :key="index" 
                :class="{ 'selected': selectedValue === index}"
                @click="haddleSelectItem(item , index)"
                class="radio-item">
                    <input type="radio" 
                        :value="item" 
                        :name="`radio-${props.question.id}`"
                        :checked="item.selected" 
                        @click.stop
                        class="radio-item-input"> 
                    <div class="radio-item-content">
                        <label>{{ item.label }}</label>
                        <EditableText 
                            style="width:100%" 
                            :isQuestionEdit="props.isQuestionEdit"
                            v-model="item.content" />
                    </div>
                    <el-button v-if="selectedValue === index && props.isQuestionEdit" type="text" @click="haddleDeleteAnswerItem(index)" class="delete-button">
                        删除
                    </el-button>
            </div>

        </div>
    </questionContainer>
</template>

<script setup>

import { defineProps , ref , defineEmits } from 'vue'
import { ElMessageBox } from 'element-plus'
import questionContainer from '@/views/base/scene/examPager/common/questionContainer.vue'
import EditableText from '../components/EditableText.vue'

const emit = defineEmits(['updateHaddleDeleteAnswerItem'])

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

// 使用v-model绑定选中值
const selectedValue = ref(-1)

// 处理单选变化
const handleRadioChange = (selectedItem) => {
    // 更新所有选项的选中状态
    props.question.answers.forEach(item => {
        item.selected = (item.id === selectedItem.id)
    })
    
    emitUpdatedQuestion()
}

// 处理选择项
const haddleSelectItem = (selectedItem  , index) => {
    selectedValue.value = index 

     // 需要在非编辑状态下选择才会生效
     if(!props.isQuestionEdit){
        // 更新所有选项的选中状态
        props.question.answers.forEach(item => {
            console.log('item = ' + JSON.stringify(item))
            console.log('selectItem = ' + JSON.stringify(selectedItem))
            item.selected = (item.label === selectedItem.label)
        })
        
        emitUpdatedQuestion()
     }

}

// 发出更新事件
const emitUpdatedQuestion = () => {
    // 准备要更新的问题数据
    const updatedQuestion = {
        ...props.question,
        answers: [...props.question.answers]
    }
    
    // 触发更新事件
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
    emit('updateHaddleDeleteAnswerItem', props.question.id, index)
  } catch (error) {
    // 用户取消删除
    console.log('用户取消了删除操作')
  }
}

</script>

<style lang="scss" scoped>

.radio-item-input{
    margin-top: 0px;
}

.radio-item {
    background-color: #fafafa ;
    border-color: rgba(0, 0, 0, 0.06);
    min-height: 36px;
    font-size: 14px;
    box-sizing: border-box;
    cursor: pointer;
    width: calc(100% - 0px);
    display: flex;
    align-items: center;
    border: 1px solid #fafafa ;
    gap: 5px;
    margin-bottom: 10px;
    padding: 5px 10px;
    border-radius: 9px;


    .radio-item-content {
        display: flex;
        flex-direction: row;
        gap: 5px;
        width: 100%;
        align-items: center;
    }

    &.selected { 
        // border: 1px solid #2378ff;
        // font-weight: bold;
        border-color: #2378ff;
        background-color: #f0f7ff;
        box-shadow: 0 0 0 1px #2378ff;
    }

    &:hover { 
        // border: 1px solid #2378ff;
        // box-shadow: 0px 4px 16px rgba(0, 0, 0, 0.07);
        border-color: #2378ff;
        box-shadow: 0px 4px 16px rgba(0, 0, 0, 0.07);
    }
}

</style>