<template>
  <div class="app-container smart-doc-config">
    <!-- 顶部导航栏 -->
    <el-page-header @back="goBack" class="page-header">
      <template #content>
        <span class="header-title">智能文档配置</span>
      </template>
    </el-page-header>

    <el-tabs v-model="activeTab" :tab-position="'right'" class="config-tabs">
      <el-tab-pane label="排版模板" name="layout" :lazy="true">
        <LayoutConfig />
      </el-tab-pane>
      <el-tab-pane label="审核规则" name="review" :lazy="true">
        <ReviewConfig />
      </el-tab-pane>
      <el-tab-pane label="解析服务" name="parse" :lazy="true">
        <ParseConfig />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const router = useRouter()

import ParseConfig from './contentFormatter/ParseConfig.vue'
import LayoutConfig from './contentFormatter/LayoutConfig.vue'
import ReviewConfig from './contentFormatter/ReviewConfig.vue'
import GroupConfig from './contentFormatter/GroupConfig.vue'

const activeTab = ref('layout')

// 返回
const goBack = () => {
  router.push({ path: "/global/config" })
}

</script>

<style lang="scss" scoped>
.smart-doc-config {
  width: 100%;
  padding: 20px;
  box-sizing: border-box;

  .title {
    color: #333;
    margin-bottom: 24px;
    font-weight: 500;
    font-size: 24px;
  }

  .config-tabs {
    width: 100%;
    margin-top:20px;

    :deep(.el-tabs__content) {
      padding-right: 20px;
      background-color: #fff;
    }

    :deep(.el-tabs__header) {
      margin: 0;
    }

    :deep(.el-tabs__item) {
      font-size: 14px;
      height: 40px;
      line-height: 40px;
    }
  }
}
</style>