<template>
  <el-scrollbar class="pager-container" ref="scrollbarRef"> 

    <div ref="innerRef">

      <!-- 试卷头部 -->
      <div class="pager-header" v-if="props.showHeader">
        <div class="pager-header-title">
          <EditableText v-model="pagerInfo.title" />
        </div>
        <div class="pager-header-desc">
          <EditableText v-model="pagerInfo.desc" />
        </div>
      </div>

      <!-- 试卷主体区 -->
      <div class="pager-body">

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
                  @update-handleQuestionImageAnswer="updateHandleQuestionImageAnswer"
                  @update-handleUpdateQuestion="handleUpdateQuestion"
                  @update-questionIsRequired="handleUpdateQuestionIsRequired"
                  @delete-question="handleDeleteQuestion"
                  :currentSelect="getIndex(element.id) ===  currentIndex"
                /> 

                <!-- 位置组件 -->
                <Location 
                  v-if="element.type === 'location'" 
                  :question="element" 
                  @update-handleQuestionImageAnswer="updateHandleQuestionImageAnswer"
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
import { ElMessage , ElLoading } from 'element-plus'
import draggable from "vuedraggable";

import {
 getPagerDetail ,
 updatePagerQuestion  ,
} from '@/api/base/im/scene/examPaper';

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
const route = useRoute();

const sceneId = ref(route.query.sceneId)
const pagerId = ref(route.query.pagerId)

const loading = ref(true)

const innerRef = ref(null); // 滚动条的处理_starter
const scrollbarRef = ref(null);

const emits = defineEmits(['setCurrentPageInfo']);

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
  'title': '',
  'desc': '',
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

    pagerInfo.questions.forEach((item , index) => {
      // 将question与titile值相等
      item.question = item.title
    })

    const data = {
      id: pagerInfo.id,
      sceneId: sceneId.value,
      pagerType: pagerInfo.pagerType ,
      pagerName: pagerInfo.title , 
      pagerDesc: pagerInfo.desc ,
      difficulty: pagerInfo.difficulty,
      questionList: pagerInfo.questions ,
    }

    const loading = ElLoading.service({
      lock: true,
      text: 'Loading',
      background: 'rgba(0, 0, 0, 0.7)',
    })

    updatePagerQuestion(data).then(res => {
      ElMessage.success('保存成功')
      loading.close() ;
    }).catch(err => {
      loading.close()
    })

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


// 获取试卷详情
const handlePagerDetail = () => {
  getPagerDetail(pagerId.value).then(res => {
      pagerInfo.id = res.data.id
      pagerInfo.title = res.data.pagerName
      pagerInfo.desc = res.data.pagerDesc
      pagerInfo.pagerType = res.data.pagerType
      pagerInfo.difficulty = res.data.difficulty
      pagerInfo.questions = res.data.questionList

      emits('setCurrentPageInfo', pagerInfo)
  })
}

onMounted(() => {
  handlePagerDetail()
})

// 暴露方法给父组件
defineExpose({
  handleAddQuestionCard , 
  handleSavePager
})

</script>

<style lang="scss" scoped>
.pager-container {
  background-color: #fff;
  margin: 10px 10px;
  margin-right: 10px;
  border-radius: 8px;
  height: calc(100vh - 110px);
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
  margin: 5px;
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
