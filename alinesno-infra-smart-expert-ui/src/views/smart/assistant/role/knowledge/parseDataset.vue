<template>
  <div class="app-container" >
        <el-page-header @back="goBack">
          <template #content>
              <span class="text-large font-600 mr-3" style="font-size: 16px;"> 配置知识库数据集</span>
          </template>
      </el-page-header>

      <div style="margin-top:20px">
          <el-tabs :tab-position="tabPosition" style="height: calc(100vh - 150px)" class="demo-tabs">
              <el-tab-pane style="height: 100%;" label="数据源管理">
                  <DatasetList />
              </el-tab-pane>
              <el-tab-pane label="向量性测试">
                  <DatasetSearch />
              </el-tab-pane>
          </el-tabs>
      </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

import DatasetList from './datasetList';
import DatasetSearch from './datasetSearch';

const tabPosition = ref('left')
const router = useRouter();

const goBack = () => {
  console.log('go back')
  let path = '/expert/smart/assistant/role/index';
  router.push(path)
}
</script>

<style lang="scss">

.demo-tabs{
  .el-tabs__nav-wrap::after{
      height:0px !important;
  }
}

</style>