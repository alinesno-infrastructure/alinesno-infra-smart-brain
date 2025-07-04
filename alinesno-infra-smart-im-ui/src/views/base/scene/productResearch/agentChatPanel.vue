<template>
  <div class="exam-pagercontainer document-container">

    <el-container style="height:calc(100vh - 40px );background-color: #fff;">

      <el-aside width="80px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-main">
        <!-- 文件知识库列表 -->
        <DatasetList :channelStreamId="channelStreamId" />
      </el-main>
    </el-container>

  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { ElMessage , ElLoading } from 'element-plus';

import FunctionList from './functionList'
import DatasetList from './components/ruleList.vue'
import SnowflakeId from "snowflake-id";

const snowflake = new SnowflakeId();
const route = useRoute();
const router = useRouter();
const channelStreamId= ref(route.query.channelStreamId || snowflake.generate())


</script>

<style lang="scss" scoped>
</style>