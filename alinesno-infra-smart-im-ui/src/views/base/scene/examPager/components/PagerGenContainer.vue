<template>
  <el-scrollbar class="pager-container" ref="scrollbarRef">

    <div ref="innerRef">

      <!-- 试卷主体区 -->
      <div class="pager-body">

        <div v-if="pagerInfo.questions.length === 0" style="margin-top: 20vh;">
          <el-empty description="当前未生成题目试卷，可以上传相关课件和配置题目，生成试卷" />
        </div>

        <draggable
          v-model="pagerInfo.questions"
          @change="handleSortChange"
          :animation="300"
          class="draggable-container">
          <template #item="{ element }">

              <div class="pager-body-item" 
                v-if="element"
                :class="{ 'pager-body-item-active': getIndex(element.id) === currentIndex }"
                :key="element.id" 
                @click="handleSelectItem(element , getIndex(element.id))">

                <!-- 单选组件 -->
                <RadioSelect 
                  v-if="element.type === 'radio'" 
                  :question="element" 
                  @update-haddleDeleteAnswerItem="haddleDeleteAnswerItem"
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @update-questionIsRequired="handleUpdateQuestionIsRequired"
                  @update-questionTitle="handleUpdateTitle"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) === currentIndex"
                /> 

                <!-- 多选组件 -->
                <Checkbox 
                  v-if="element.type === 'checkbox'" 
                  :question="element" 
                  @update-haddleDeleteAnswerItem="haddleDeleteAnswerItem"
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @update-questionIsRequired="handleUpdateQuestionIsRequired"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) ===  currentIndex"
                /> 

                <!-- 单行文本组件 -->
                <SingleLine 
                  v-if="element.type === 'single-line'" 
                  :question="element" 
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @update-questionIsRequired="handleUpdateQuestionIsRequired"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) ===  currentIndex"
                /> 

                <!-- 多填空组件 -->
                <MultiFill 
                  v-if="element.type === 'multi-fill'" 
                  :question="element" 
                  @update-haddleDeleteAnswerItem="haddleDeleteAnswerItem"
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @update-questionIsRequired="handleUpdateQuestionIsRequired"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) ===  currentIndex"
                /> 

                <!-- 多行文本组件 -->
                <multiLine 
                  v-if="element.type === 'multi-line'" 
                  :question="element" 
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @update-questionIsRequired="handleUpdateQuestionIsRequired"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) ===  currentIndex"
                /> 

                <!-- 图片组件 -->
                <ImageFile 
                  v-if="element.type === 'image-file'" 
                  :question="element" 
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @update-questionIsRequired="handleUpdateQuestionIsRequired"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) ===  currentIndex"
                /> 

                <!-- 图片单选组件 -->
                <ImageRadio 
                  v-if="element.type === 'image-radio'" 
                  :question="element" 
                  @update-handleQuestionImageAnswer="updateHandleQuestionImageAnswer"
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @update-questionIsRequired="handleUpdateQuestionIsRequired"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) ===  currentIndex"
                /> 

                <!-- 图片多选组件 -->
                <ImageCheckbox 
                  v-if="element.type === 'image-checkbox'" 
                  :question="element" 
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) ===  currentIndex"
                /> 

                <!-- 位置组件 -->
                <Location 
                  v-if="element.type === 'location'" 
                  :question="element" 
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @update-questionIsRequired="handleUpdateQuestionIsRequired"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) ===  currentIndex"
                /> 

                <!-- 时间组件 -->
                <Datetime 
                  v-if="element.type === 'datetime'" 
                  :question="element" 
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @update-questionIsRequired="handleUpdateQuestionIsRequired"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) ===  currentIndex"
                /> 

                <!-- 动态表格组件 -->
                <DynamicTable 
                  v-if="element.type === 'dynamic-table'" 
                  :question="element" 
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @update-questionIsRequired="handleUpdateQuestionIsRequired"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) ===  currentIndex"
                /> 

                <!-- 内容分析描述组件 -->
                <DescriptionPanel 
                  v-if="element.type === 'description'" 
                  :question="element" 
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @update-questionIsRequired="handleUpdateQuestionIsRequired"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) ===  currentIndex"
                /> 

              </div>
          </template>
        </draggable>

      </div>

    </div>
  </el-scrollbar>
</template>

<script setup>

import { ref , reactive , defineExpose , nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import draggable from "vuedraggable";

import EditableText from './EditableText.vue';

import RadioSelect from '../questions/radioSelect.vue';
import SingleLine from '../questions/singleLine.vue';
import Checkbox from '../questions/checkBox.vue';
import MultiFill from '../questions/multiFill.vue'
import MultiLine from '../questions/multiLine.vue'
import ImageFile from '../questions/imageFile.vue'
import ImageRadio from '../questions/imageRadio.vue'
import ImageCheckbox from '../questions/imageCheckbox.vue'
import Location from '../questions/locationFill.vue'
import Datetime from '../questions/datetimeFill.vue'
import DynamicTable from '../questions/dynamicTable.vue'; 
import DescriptionPanel from '../questions/descriptionPanel.vue';

const currentIndex = ref(-1)

const innerRef = ref(null); // 滚动条的处理_starter
const scrollbarRef = ref(null);

const props = defineProps({
  showHeader: {
    type: Boolean,
    default: true 
  }
})

// 删除问题
const handleDeleteQuestion = (id) => {
  console.log('delete question = ' + id)
  // 通过id查询到pagerInfo.questions，然后这条id信息
  const index = pagerInfo.questions.findIndex(item => item.id === id)
  if (index !== -1) {
    pagerInfo.questions.splice(index, 1)
  }
}

// 更新问题是否必填
const handleUpdateQuestionIsRequired = (id , val) => {
  console.log('update question = ' + id + ' isRequired = ' + val);
  // 通过id查询到指定的question，然后更新isRequired
  pagerInfo.questions = pagerInfo.questions.map(item => {
    if (item.id === id) {
      item.isRequired = val
    }
    return item
  })
}

// 更新问题
const handleUpdateQuestion = (updatedQuestion) => {

  console.log('updatedQuestion = ' + updatedQuestion.id)

  // 通过id查询到指定的question，然后更新question
  pagerInfo.questions = pagerInfo.questions.map(item => {
    if (item?.id === updatedQuestion.id) {
      item = { ...item, ...updatedQuestion }
    }
    return item ;
  })
}

// 通过 ID 获取问题在数组中的索引
const getIndex = (id) => {
  return pagerInfo.questions.findIndex(item => item.id === id);
};

// 处理拖拽排序后的顺序更新
const handleSortChange = (list) => {
  pagerInfo.questions.forEach((item, index) => {
    item.order = index + 1; // 更新 order 为索引 +1
  });
  console.log('排序后的questions:', pagerInfo.questions);
};

const handleUpdateTitle = (id , newValue) => {
  console.log('update question = ' + id + ' title = ' + newValue);
  // 通过id查询到指定的question，然后更新title
  pagerInfo.questions = pagerInfo.questions.map(item => {
    if (item.id === id) {
      item.title = newValue

      ElMessage.success('修改成功')
    }
    return item
  })
}

const initChatBoxScroll = () => {

  nextTick(() => {
    const element = innerRef.value;  // 获取滚动元素
    const scrollHeight = element.scrollHeight;

    scrollbarRef.value.setScrollTop(scrollHeight);
  })

}

const pagerInfo = reactive({
  'title': '广西中医药大学2022级信息管理与信息系统专业《医学数据挖掘与实践(R或Python)》1学期考试试卷A卷',
  'desc': '欢迎参加《医学数据挖掘与实践(R或Python)》课程的学期考试！本试卷包含选择题、填空题、简答题、计算题和编程题，全面考察你对Python数据挖掘基础知识的掌握程度和应用能力。请认真作答，祝你考试顺利！',
  'questions': []
})

// 点击选中当前index面板
const handleSelectItem = (item , index) => { 
  currentIndex.value = index
}

// 添加新的问题到试卷
const handleAddQuestionCard = (item) => { 
  console.log('PagerConatiner handleAddQuestionCard = ' + item)

  // 设置order
  item.order = pagerInfo.questions.length + 1
  pagerInfo.questions.push(item)

  handleSelectItem(item, pagerInfo.questions.length - 1);

  // 调用初始化滚动条的函数
  initChatBoxScroll();
}

// 切换选中问题
const handleSavePager = () => {
    console.log('pagerInfo = ' + JSON.stringify(pagerInfo))
    ElMessage.success('保存成功')
}

// 删除答案的问题 
const haddleDeleteAnswerItem = (questionId , index , type) => {

  console.log("questionId = " + questionId);
  // 查询出问题，然后删除answers下面的答案
  const question = pagerInfo.questions.find(item => item.id === questionId)
  if (!question) {
    ElMessage.error('问题不存在')
    return
  }

  if(type === 'blank'){
    question.blanks.splice(index , 1)
  }else{
    question.answers.splice(index , 1)
  }

}

// 更新图片类型的答案
const updateHandleQuestionImageAnswer = (questionId , index , newItem) => { 
  console.log("updateHandleQuestionImageAnswer questionId = " + questionId + " index = " + index + " newItem = " + JSON.stringify(newItem))
  // 通过id查询到问题，然后替换掉下标为index的答案
  const question = pagerInfo.questions.find(item => item.id === questionId)
  if (!question) {
    ElMessage.error('问题不存在')
    return
  }
  question.answers[index] = { ...newItem }
}

// 增加pagerInfo.questions的数据，传递问题数组
const addQuestion = (itemArr) => {
  console.log('addQuestion item = ' + JSON.stringify(itemArr))

  if(itemArr && itemArr.length > 0){
    itemArr.forEach(item => {
      item.order = pagerInfo.questions.length + 1
      pagerInfo.questions.push(item)
    })
  }

}

// 获取pagerInfo.questions的数据
const getQuestionList = () => {
  return pagerInfo.questions
}

// 暴露方法给父组件
defineExpose({
  handleAddQuestionCard , 
  handleSavePager, 
  addQuestion,
  getQuestionList,
})

</script>

<style lang="scss" scoped>
.pager-container {
  margin: 10px 10px;
  border-radius: 8px;
  height: calc(100vh - 180px);
  background: #fafafa;
  // box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-left: 20px;
  padding: 5px;
  // border: 1px solid #e8eaf2;
}

.pager-body-item-active {

  .question-container{
    border: 2px solid #409eff;
  }
}

.pager-header {

  padding: 20px;

  .pager-header-title {
    text-align: center;
    font-size: 30px;
    width: 80%;
    margin: auto;
    font-weight: normal;
    word-break: break-word;
    padding: 20px 10px;
    line-height: 2.5rem;
  }

  .pager-header-desc {
    text-align: center;
    width: 80%;
    margin: auto;
    font-weight: normal;
    word-break: break-word;
    padding: 0px 20px;
    line-height: 1.6rem;
  }

}

.draggable-container {
  list-style: none;
  padding: 0;
  padding-bottom: 50px;
}

.pager-body-item {
  /* 添加可拖拽的视觉提示 */
  cursor: grab;
  // margin-bottom: 15px;
  padding: 5px;
  border-radius: 4px;
  background-color: white;
  transition: transform 0.3s ease;
}

.pager-body-item:active {
  cursor: grabbing;
  transform: scale(0.98);
}

</style>
