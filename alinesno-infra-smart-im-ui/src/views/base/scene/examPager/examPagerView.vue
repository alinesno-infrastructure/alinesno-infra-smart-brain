<template>
  <div class="exam-pager-container">
    <TopPanel 
      @handleSavePager="handleSavePager"
      :currentPageInfo="currentPageInfo"
      ref="topPanelRef" />

    <el-row>
      <el-col :span="5">
        <LeftPanel 
          @handleAddQuestionCard="handleAddQuestionCard"
          :currentPageInfo="currentPageInfo"
          ref="leftPanelRef" />
      </el-col>
      <el-col :span="14">
        <PagerContainer 
          @setCurrentPageInfo="setCurrentPageInfo"
          ref="pagerContainerRef" />
      </el-col>
      <el-col :span="5">
        <RightPanel 
          :currentPageInfo="currentPageInfo"
          ref="rightPanelRef" />
      </el-col>
    </el-row>

  </div>
</template>

<script setup>

import { ref } from 'vue'

// 引入组件
import TopPanel from './components/TopPanel.vue'
import RightPanel from './components/RightPanel.vue'
import PagerContainer from './components/PagerContainer.vue'
import LeftPanel from './components/LeftPanel.vue'

// 引用组件
const topPanelRef = ref(null)
const pagerContainerRef = ref(null)
const leftPanelRef = ref(null)
const rightPanelRef = ref(null)
const currentPageInfo = ref(null)

// 添加问题
const handleAddQuestionCard = (newQuestionItem) => {
  pagerContainerRef.value.handleAddQuestionCard(newQuestionItem)
}

// 保存试卷
const handleSavePager  = () => {
  pagerContainerRef.value.handleSavePager()
}

const setCurrentPageInfo = (pageInfo) => {
  currentPageInfo.value = pageInfo
}

</script>

<style lang="scss" scoped>

.exam-pager-container {
    background: #f5f5f5;
}

</style>
