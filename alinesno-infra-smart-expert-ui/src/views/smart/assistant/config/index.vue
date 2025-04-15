<template>
  <div class="aip-config-container">
    <el-tabs :tab-position="tabPosition" v-model="activeTab" @tab-click="handleTabClick">
      <!-- 全局配置管理 -->
      <el-tab-pane label="全局配置" name="globalConfig" value="globalConfig">
        <template #label>
          <span class="custom-tabs-label">
            <i class="fa-solid fa-globe" />
            <span>系统全局配置</span>
          </span>
        </template>
        <GlobalConfigPanel />
      </el-tab-pane>
      <!-- 密钥配置管理 -->
      <el-tab-pane label="密钥配置" name="secretConfig" value="secretConfig">
        <template #label>
          <span class="custom-tabs-label">
            <i class="fa-solid fa-paper-plane" />
            <span>全局密钥配置</span>
          </span>
        </template>
        <GlobalSecretPanel />
      </el-tab-pane>
      <el-tab-pane label="模板配置" name="templateConfig" value="templateConfig">
        <template #label>
          <span class="custom-tabs-label">
            <i class="fa-solid fa-file-pdf" />
            <span>导出模板配置</span>
          </span>
        </template>
        <TemplateConfigPanel />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import GlobalConfigPanel from './globalConfig.vue';
import GlobalSecretPanel from '@/views/smart/assistant/secret/index';
import TemplateConfigPanel from '@/views/smart/assistant/template/index';

const tabPosition = ref('right');
const activeTab = ref('globalConfig');

// 从 localStorage 读取选中的 tab
const loadSelectedTab = () => {
  const storedTab = localStorage.getItem('selectedTab');
  activeTab.value = storedTab || 'globalConfig';
};

// 处理 tab 点击事件，保存选中的 tab 到 localStorage
const handleTabClick = (tab) => {
  console.log('点击了标签：', tab);
  console.log('activeTab.value', activeTab.value)
  localStorage.setItem('selectedTab', activeTab.value);
};

onMounted(() => {
  loadSelectedTab();
});
</script>

<style lang="scss" scoped>
.aip-config-container {
  padding: 10px;

  .tabs-container {
    height: calc(100vh - 60px);
  }

  span.custom-tabs-label {
    display: flex;
    align-items: center;
    gap: 3px;
  }
}
</style>    