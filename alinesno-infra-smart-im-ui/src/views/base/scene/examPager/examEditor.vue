<template>
  <div class="exam-pagercontainer">

    <el-container style="height:calc(100vh - 40px );background-color: #fff;">

      <el-aside width="280px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-main">
        <ExamPagerView />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>

import FunctionList from './functionList'
import ExamPagerView from './examPagerView'

</script>

<style scoped lang="scss">
</style>