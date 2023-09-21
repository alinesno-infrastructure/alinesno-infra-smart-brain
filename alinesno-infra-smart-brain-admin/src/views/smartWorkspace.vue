<template>
  <div class="app-container acp-dashboard">

    <!-- 面板切换-->
    <HeadTypeTab :currentTab="4" />

    <!-- 数据套件和状态 -->
    <SmartWorkspaceService />

    <!-- 运行应用列表 -->
    <SmartWorkspaceApps />
  </div>
</template>

<script setup name="Index">

import HeadTypeTab from './headTypeTab.vue'

import SmartWorkspaceApps from './smart-workspace/apps.vue'
import SmartWorkspaceService from './smart-workspace/service.vue'


</script>
