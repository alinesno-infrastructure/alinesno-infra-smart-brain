<template>
  <div class="left-panel-container">

    <!-- 菜单区 -->
    <div class="menu-panel">
      <div v-for="(item , index) in menuItems" :key="index" class="menu-item" :class="{ 'active': menuType === item.code }" @click="handleItemClick(item)" >
        <span><i :class="item.icon"></i></span>
        <span style="text-align: center;">
          {{ item.label }}
        </span> 
      </div>
    </div>

    <!-- 题型功能列表区 -->
    <div class="select-type-panel" v-if="menuType === 'question-type'">
        <div v-for="(item, index) in questionTypes" :key="index" class="type-panel-box"  >
          <div class="type-title">
            <i :class="item.icon" /> {{ item.label }}
          </div> 
          <div class="type-item-list">
            <div v-for="(i, ii) in item.types" :key="ii" class="item" @click="handleAddQuestionCard(i, ii)">
              <i :class="i.icon" /> {{ i.label }}
            </div>
          </div>
        </div>
    </div>

    <!-- 题库列表 -->
    <div class="select-type-panel" v-if="menuType === 'question-bank'">
      <div v-for="(item, index) in questionBanks" :key="index" class="type-panel-box question-bank-box">
        <div class="type-title" style="margin-bottom:0px;">
          <i :class="item.icon" /> {{ item.label }}
        </div> 
      </div>
    </div>

    <!-- 设置配置 -->
    <div class="select-type-panel" v-if="menuType === 'settings'">
    <div class="type-panel-box">
      <div class="type-title">
        <i class="fa-solid fa-cog" /> 设置
      </div> 
      <div class="type-item-list">
        <div class="item">
          <i class="fa-solid fa-palette"/> 主题
        </div>
      </div>
    </div>
    </div>

  </div>
</template>

<script setup>

import { ref , defineEmits } from 'vue'

import SnowflakeId from "snowflake-id";
const snowflake = new SnowflakeId({
    mid: 42,
    offset: (2019 - 1970) * 31536000 * 1000
});

const emits = defineEmits(['handleAddQuestionCard'])

const menuType = ref('question-type')

const menuItems = ref([
  // { id: 1, code: 'outline', icon: 'fa-solid fa-list', label: '大纲' },
  { id: 2, code: 'question-type', icon: 'fa-solid fa-tags', label: '题型' },
  { id: 3, code: 'question-bank', icon: 'fa-solid fa-database', label: '题库' },
  // { id: 4, code: 'skin', icon: 'fa-solid fa-paint-brush', label: '皮肤' },
  // { id: 5, code: 'settings', icon: 'fa-solid fa-cog', label: '设置' }
])

// 模拟题库
const questionBanks = ref([
    {
        code: "basic-knowledge",
        label: "基础知识库",
        icon: "fa-solid fa-graduation-cap"
    },
    {
        code: "programming",
        label: "编程题库",
        icon: "fa-solid fa-code"
    },
    {
        code: "interview",
        label: "面试题库",
        icon: "fa-solid fa-briefcase"
    },
    {
        code: "certification",
        label: "认证题库",
        icon: "fa-solid fa-certificate"
    },
    {
        code: "aptitude",
        label: "能力倾向测试",
        icon: "fa-solid fa-brain"
    },
    {
        code: "language",
        label: "语言题库",
        icon: "fa-solid fa-language"
    },
    {
        code: "math",
        label: "数学题库",
        icon: "fa-solid fa-calculator"
    },
    {
        code: "general",
        label: "综合题库",
        icon: "fa-solid fa-question-circle"
    }
])

const questionTypes = ([
    {
        code: "select",
        label: "选择",
        icon: "fa-solid fa-check-square",
        types: [
            { code: "radio", label: "单选", icon: "fa-solid fa-dot-circle" },
            { code: "checkbox", label: "多选", icon: "fa-solid fa-check-square" },
            { code: "image-radio", label: "图片单选", icon: "fa-regular fa-image" },
            { code: "image-checkbox", label: "图片多选", icon: "fa-regular fa-images" }
        ]
    },
    {
        code: "text-input",
        label: "文本输入",
        icon: "fa-solid fa-font",
        types: [
            { code: "single-line", label: "单行文本", icon: "fa-solid fa-font" },
            { code: "multi-fill", label: "多项填空", icon: "fa-solid fa-list-alt" },
            { code: "multi-line", label: "多行文本", icon: "fa-solid fa-align-left" }
        ]
    },
    {
        code: "advanced",
        label: "高级题型",
        icon: "fa-solid fa-star",
        types: [
            { code: "image-file", label: "图片/文件", icon: "fa-solid fa-file-image" },
            { code: "datetime", label: "日期/时间", icon: "fa-solid fa-calendar" },
            { code: "location", label: "地理位置", icon: "fa-solid fa-map-marker-alt" },
        ]
    },
    // {
    //     code: "matrix",
    //     label: "矩阵",
    //     icon: "fa-solid fa-table",
    //     types: [
    //         { code: "matrix-radio", label: "矩阵单选", icon: "fa-solid fa-table" },
    //         { code: "matrix-checkbox", label: "矩阵多选", icon: "fa-solid fa-table" },
    //         { code: "matrix-scale", label: "矩阵量表", icon: "fa-solid fa-table" },
    //         { code: "matrix-rating", label: "矩阵打分", icon: "fa-solid fa-table" },
    //         { code: "matrix-fill", label: "矩阵填空", icon: "fa-solid fa-table" },
    //         { code: "dynamic-table", label: "自增表格", icon: "fa-solid fa-table-plus" }
    //     ]
    // },
    {
        code: "note",
        label: "备注说明",
        icon: "fa-solid fa-info-circle",
        types: [
            { code: "description", label: "文本描述", icon: "fa-solid fa-file-lines" }
        ]
    }
]);   

// 点击菜单项
const handleItemClick = (item) => {
  console.log('item = ' + item)
  menuType.value = item.code ;
}

// 添加新的问题到试卷
const handleAddQuestionCard = (item , index) => { 
  console.log('item = ' + item.code + ' , index = ' + index)

  let itemCard ; 
  var id = snowflake.generate();

  if(item.code === 'radio'){ // 单选题
    itemCard = {
      'id': id ,
      'type': 'radio',
      'question': '请输入题目标题' , 
      'isRequired': true,
      'score': 1,
      'order': 1,
      'answers': [
        { 'label': 'A', 'content': '选项', 'isCorrect': true, 'value': '90%' },
        { 'label': 'B', 'content': '选项', 'isCorrect': false, 'value': '10%' },
      ]
    };
  }else if(item.code === 'checkbox'){ // 多选 
    itemCard = {
      'id': id ,
      'type': 'checkbox',
      'question': '请输入题目标题' , 
      'isRequired': true,
      'score': 1,
      'order': 1,
      'answers': [
        { 'label': 'A', 'content': '选项', 'isCorrect': true, 'value': '90%' },
        { 'label': 'B', 'content': '选项', 'isCorrect': false, 'value': '10%' },
      ]
    };
  }else if(item.code === 'image-radio'){ // 图片单选
    itemCard = {
      'id': id,
      "type": "image-radio",
      'question': '请输入题目标题' , 
      "isRequired": true,
      "score": 1,
      "answers": [
        { "label": "A", "content": "选项", "img": "https://picsum.photos/id/237/200/100", "isCorrect": true, "value": "tree" },
        { "label": "B", "content": "选项", "img": "https://picsum.photos/id/238/200/100", "isCorrect": false, "value": "neural" },
      ]
    };
  }else if(item.code === 'image-checkbox'){  // 图片多选
    itemCard = {
      'id': id,
      "type": "image-checkbox",
      'question': '请输入题目标题' , 
      "isRequired": true,
      "score": 1,
      "answers": [
        { "label": "A", "content": "选项", "img": "https://picsum.photos/id/237/200/100", "isCorrect": true, "value": "tree" },
        { "label": "B", "content": "选项", "img": "https://picsum.photos/id/238/200/100", "isCorrect": false, "value": "neural" },
      ]
    };
  }else if(item.code === 'single-line'){  // 单行填空
    itemCard = {
      'id': id ,
      'type': 'single-line',
      'question': '请输入题目标题',
      'isRequired': true,
      'score': 1,
      'placeholder': '请输入',
      'correctAnswer': 'corr()'
    } ;

  }else if(item.code === 'multi-fill'){ // 多行填空

    itemCard = {
      'id': id,
      'type': 'multi-fill',
      'question': '请输入题目标题',
      'isRequired': true,
      'score': 2,
      'blanks': [
        { 'index': 1, 'correctAnswer': 'read_csv' },
        { 'index': 2, 'correctAnswer': 'head' }
      ]
    };

  }else if(item.code === 'multi-line'){ // 多行填空
    itemCard = {
      'id': id ,
      'type': 'multi-line',
      'question': '请输入题目标题',
      'isRequired': true,
      'score': 1,
      'placeholder': '请输入',
      'correctAnswer': 'corr()'
    } ;

  }else if(item.code === 'image-file'){
    itemCard = {
      'id': id ,
      'type': 'image-file',
      'question': '请输入题目标题',
      'isRequired': true,
      'score': 1,
      'placeholder': '请输入',
      'correctAnswer': 'corr()'
    } 
  }else if(item.code === 'datetime'){
    itemCard = {
      'id': id ,
      'type': 'datetime',
      'question': '请输入题目标题',
      'isRequired': true,
      'score': 1,
      'placeholder': '请输入',
      'correctAnswer': 'corr()'
    }
  }else if(item.code === 'location'){
    itemCard = {
      'id': id ,
      'type': 'location',
      'question': '请输入题目标题',
      'isRequired': true,
      'score': 1,
      'placeholder': '请输入',
      'correctAnswer': 'corr()'
    }
  }else if(item.code === 'description'){
    itemCard = {
      'id': id ,
      'type': 'description',
      'question': '请输入题目标题',
      'isRequired': true,
      'score': 1,
      'placeholder': '请输入',
      'correctAnswer': 'corr()'
    }
  }

  console.log('Left panel itemCard = ' , itemCard)

  if(itemCard){
    emits('handleAddQuestionCard' , itemCard)
  }

}

</script>

<style lang="scss" scoped>

.left-panel-container {
  background: #fff;
  height: calc(100vh - 110px);
  border-radius: 5px;
  margin-left: 10px;
  padding: 10px;
  margin-top: 10px;
  margin-bottom: 10px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.menu-panel {
    display: flex;
    flex-direction: column;
    gap: 5px;
    width: 60px;
    align-items: center;

    .menu-item {
        display: flex;
        cursor: pointer;
        margin-top:10px;
        flex-direction: column;
        background: #fafafa;
        width: 40px;
        gap: 2px;
        padding: 10px;
        font-size: 14px;
        border-radius: 5px;
        align-items: center;

        &.active {
          background: #e5e5e5;
        }

        &:hover {
          background: #e5e5e5;
        }
    }
}

.select-type-panel{

  width: 100%;
  text-align: left;
  // margin-left: 10px;
  // padding: 10px;
  font-size: 14px;

  .question-bank-box {
    font-weight: normal;
    background: #fafafa;
    border-radius: 5px;
    cursor: pointer;
    padding-left: 10px;

    &:hover {
      background: #e5e5e5;
    }
  }

  .type-panel-box {
    margin-bottom:10px;

    .type-title {
      font-weight: normal;
    }
  }

  .type-title {
    text-align: left;
    padding: 10px 5px;
    font-weight: bold;
    margin-bottom: 5px;
  }

  .type-item-list {
    column-count: 2;

    .item{
      display: flex;
      align-items: center;
      cursor: pointer;
      gap: 5px;
      padding: 6px;
      background: #f5f5f5;
      margin-bottom: 5px;
      border-radius: 5px;

      &:hover {
        background: #e5e5e5;
      }
    }
  }
}


</style>
