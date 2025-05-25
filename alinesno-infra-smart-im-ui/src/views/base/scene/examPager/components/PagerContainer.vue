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
  'questions': [
    {
      'id': 1,
      'type': 'radio',
      'question': '假设有100个学生，其中90个学生回答了问卷调查，另外10个学生没有回答问卷调查，那么有效回复率是？',
      "mediaFiles": [
        {
          "name": "5fd34deecbe349ed91bd8cde2af9cfd6~tplv-13w3uml6bg-resize_800_320.png",
          "type": "image/png",
          "url": "https://picsum.photos/id/242/200/100",
          "previewUrl": "blob:http://localhost:8080/f213353c-b84d-4331-ba9a-3f031b94a136"
        },
        {
          "name": "897c06742e8f445796b6be8729af0a84~tplv-13w3uml6bg-resize_800_320.jpg",
          "type": "image/jpeg",
          "url": "https://picsum.photos/id/242/200/100",
          "previewUrl": "blob:http://localhost:8080/13c8d335-20ae-4f14-9c95-8cb254fd4f09"
        }
      ],
      'isRequired': true,
      'score': 1,
      'order': 1,
      'answers': [
        { 'label': 'A', 'content': '90%', 'isCorrect': true, 'value': '90%' },
        { 'label': 'B', 'content': '10%', 'isCorrect': false, 'value': '10%' },
        { 'label': 'C', 'content': '80%', 'isCorrect': false, 'value': '80%' },
        { 'label': 'D', 'content': '100%', 'isCorrect': false, 'value': '100%' }
      ]
    },
    {
      'id': 2,
      'type': 'checkbox',
      'question': '以下哪些属于Python数据挖掘常用库？',
      "mediaFiles": [
        {
          "name": "5fd34deecbe349ed91bd8cde2af9cfd6~tplv-13w3uml6bg-resize_800_320.png",
          "type": "image/png",
          "url": "https://picsum.photos/id/242/200/100",
          "previewUrl": "blob:http://localhost:8080/f213353c-b84d-4331-ba9a-3f031b94a136"
        },
        {
          "name": "897c06742e8f445796b6be8729af0a84~tplv-13w3uml6bg-resize_800_320.jpg",
          "type": "image/jpeg",
          "url": "https://picsum.photos/id/242/200/100",
          "previewUrl": "blob:http://localhost:8080/13c8d335-20ae-4f14-9c95-8cb254fd4f09"
        }
      ],
      'isRequired': false,
      'score': 2,
      'answers': [
        { 'label': 'A', 'content': 'NumPy', 'isCorrect': true, 'value': 'numpy' },
        { 'label': 'B', 'content': 'Pandas', 'isCorrect': true, 'value': 'pandas' },
        { 'label': 'C', 'content': 'Matplotlib', 'isCorrect': true, 'value': 'matplotlib' },
        { 'label': 'D', 'content': 'TensorFlow', 'isCorrect': false, 'value': 'tensorflow' }
      ]
    },
    {
      'id': 3,
      'type': 'radio',
      'question': '假设有100个学生，其中90个学生回答了问卷调查，另外10个学生没有回答问卷调查，那么有效回复率是？',
      'isRequired': true,
      'score': 1,
      'order': 1,
      'answers': [
        { 'label': 'A', 'content': '90%', 'isCorrect': true, 'value': '90%' },
        { 'label': 'B', 'content': '10%', 'isCorrect': false, 'value': '10%' },
        { 'label': 'C', 'content': '80%', 'isCorrect': false, 'value': '80%' },
        { 'label': 'D', 'content': '100%', 'isCorrect': false, 'value': '100%' }
      ]
    },
    {
      'id': 4,
      'type': 'checkbox',
      'question': '以下哪些属于Python数据挖掘常用库？',
      'isRequired': true,
      'score': 2,
      'answers': [
        { 'label': 'A', 'content': 'NumPy', 'isCorrect': true, 'value': 'numpy' },
        { 'label': 'B', 'content': 'Pandas', 'isCorrect': true, 'value': 'pandas' },
        { 'label': 'C', 'content': 'Matplotlib', 'isCorrect': true, 'value': 'matplotlib' },
        { 'label': 'D', 'content': 'TensorFlow', 'isCorrect': false, 'value': 'tensorflow' }
      ]
    },
    {
      'id': 5,
      'type': 'dropdown',
      'question': '在Python中，用于处理缺失值的常用方法是？',
      'isRequired': true,
      'score': 1,
      'answers': [
        { 'label': '', 'content': '请选择', 'isCorrect': false, 'value': '' },
        { 'label': '', 'content': 'dropna()', 'isCorrect': true, 'value': 'dropna' },
        { 'label': '', 'content': 'fillna()', 'isCorrect': false, 'value': 'fillna' },
        { 'label': '', 'content': 'replace()', 'isCorrect': false, 'value': 'replace' },
        { 'label': '', 'content': '以上都是', 'isCorrect': false, 'value': 'all' }
      ]
    },
    {
      'id': 6,
      "type": "image-radio",
      "question": "请选择表示决策树的图形：",
      "isRequired": true,
      "score": 1,
      "answers": [
        { "label": "A", "content": "决策树：一种树状结构模型，通过节点分裂进行特征判断，最终到达叶节点形成分类/回归结果", "img": "https://picsum.photos/id/237/200/100", "isCorrect": true, "value": "tree" },
        { "label": "B", "content": "神经网络：模仿生物神经网络的层级结构，包含输入层、隐藏层和输出层，适合处理复杂非线性关系", "img": "https://picsum.photos/id/238/200/100", "isCorrect": false, "value": "neural" },
        { "label": "C", "content": "聚类分析：无监督学习算法，将相似数据点分组，图中展示的是多维数据在二维平面的投影分布", "img": "https://picsum.photos/id/239/200/100", "isCorrect": false, "value": "cluster" },
        { "label": "D", "content": "回归分析：展示自变量与因变量的线性/非线性关系，图中包含散点图和拟合的趋势线", "img": "https://picsum.photos/id/240/200/100", "isCorrect": false, "value": "regression"}
      ]
    },
    {
      'id': 7,
      "type": "image-checkbox",
      "question": "请选择所有属于监督学习算法的图示：",
      "isRequired": true,
      "score": 2,
      "answers": [
        { "label": "A", "content": "决策树：经典监督学习算法，图示展示树形结构及'if-then'规则的分支过程", "img": "https://picsum.photos/id/241/200/150", "isCorrect": true,"value": "tree" },
        { "label": "B", "content": "K-means：典型无监督聚类算法，图示展示迭代过程中质心移动和簇形成", "img": "https://picsum.photos/id/242/200/150", "isCorrect": false, "value": "kmeans" },
        { "label": "C", "content": "SVM（支持向量机）：监督学习分类器，图示展示最优超平面及边缘支持向量", "img": "https://picsum.photos/id/243/200/150", "isCorrect": true, "value": "svm" },
        { "label": "D", "content": "随机森林：集成学习算法，图示展示多棵决策树的并行训练及投票机制", "img": "https://picsum.photos/id/244/200/150", "isCorrect": true, "value": "randomforest" }
      ]
    },
    {
      'id': 8,
      'type': 'single-line',
      'question': '请写出Python中用于计算相关系数的函数名称：',
      'isRequired': true,
      'score': 1,
      'placeholder': '输入函数名称',
      'correctAnswer': 'corr()'
    },
    {
      'id': 9,
      'type': 'multi-fill',
      'question': '完成以下Python代码，实现读取CSV文件并显示前5行',
      'isRequired': true,
      'score': 2,
      'blanks': [
        { 'index': 1, 'correctAnswer': 'read_csv' },
        { 'index': 2, 'correctAnswer': 'head' }
      ]
    },
    {
      'id': 10,
      'type': 'multi-line',
      'question': '简要描述K近邻(KNN)算法的基本原理和适用场景：',
      'isRequired': true,
      'score': 3,
      'placeholder': '请输入至少50字的回答',
      'wordLimit': 200
    },
    {
      'id': 11,
      'type': 'image-file',
      'question': '请上传你完成的数据分析可视化图表：',
      'isRequired': false,
      'score': 2,
      'fileTypes': 'image/*',
      'maxSize': '5MB'
    },
    {
      'id': 12,
      'type': 'datetime',
      'question': '请输入你预计完成数据分析项目的日期：',
      'isRequired': true,
      'score': 1
    },
    {
      'id': 13,
      'type': 'location',
      'question': '假设你需要收集患者地理位置数据用于疾病传播分析，你会使用哪种地理坐标系统？',
      'isRequired': true,
      'score': 1,
      'answers': [
        { 'label': 'A', 'content': 'WGS 84', 'isCorrect': true, 'value': 'wgs84' },
        { 'label': 'B', 'content': 'GCJ-02', 'isCorrect': false, 'value': 'gcj02' },
        { 'label': 'C', 'content': 'BD-09', 'isCorrect': false, 'value': 'bd09' },
        { 'label': 'D', 'content': 'UTM', 'isCorrect': false, 'value': 'utm' }
      ]
    },
    {
      'id': 19,
      'type': 'description',
      'question': '数据挖掘在医学领域的应用：',
      'content': '<p>数据挖掘技术在医学领域有着广泛的应用前景，包括但不限于以下几个方面：</p><ul><li>疾病预测与诊断辅助</li><li>医疗质量评估与改进</li><li>药物疗效分析与个性化医疗</li><li>医疗资源优化配置</li><li>医学影像分析与识别</li></ul><p>请选择其中一个方向，简要阐述数据挖掘技术在该领域的具体应用方式和价值。</p>',
      'isRequired': false,
      'score': 0
    }
  ]
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
  margin-right: 0px;
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
