<template>
  <div class="app-container acp-dashboard">

    <!-- 面板服务 -->
    <DashboardService />

    <!-- 通知服务 -->
    <DashboardNotices />

  </div>
</template>

<script setup name="Index">

import DashboardService from './dashboard/service.vue'
import DashboardNotices from './dashboard/notices.vue'

</script>
