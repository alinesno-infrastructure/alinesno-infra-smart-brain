<template>
  <div>
      <!--
      <el-page-header @back="goBack">
          <template #content>
              <span class="text-large font-600 mr-3" style="font-size: 16px;"> 配置知识库数据集</span>
          </template>
      </el-page-header>
      -->

      <div>
          <el-tabs :tab-position="tabPosition" style="height: calc(100vh - 150px)" class="demo-tabs">
              <el-tab-pane style="height: 100%;" label="数据源管理">
                  <DatasetList ref="datasetListRef" />
              </el-tab-pane>
              <el-tab-pane label="向量性测试">
                  <DatasetSearch ref="datasetSearchRef" />
              </el-tab-pane>
          </el-tabs>
      </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

import DatasetList from './datasetList';
import DatasetSearch from './datasetSearch';

const tabPosition = ref('top')
const router = useRouter();

const datasetListRef = ref(null)
const datasetSearchRef = ref(null)

/** 初始化角色信息 */
function initRoleData(role){
    console.log('parse Dataet roleId = ' + JSON.stringify(role))
    datasetListRef.value.initRoleData(role);
    datasetSearchRef.value.initRoleData(role);
}

defineExpose({
    initRoleData
})

</script>

<style lang="scss">

.demo-tabs{
  .el-tabs__nav-wrap::after{
      height:0px !important;
  }
}

</style>