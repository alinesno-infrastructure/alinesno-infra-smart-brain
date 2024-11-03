<template>
  <div class="agent-single-right-panel">
    <!-- 上方统计信息 -->
    <div class="top-stats">
      <span class="stat-item">👍 13</span>
      <span class="stat-item">💬 9K 使用</span>
      <span class="stat-item">🗣️ 60.3K 对话</span>
    </div>

    <!-- 中间内容区域 -->
    <div class="content-area">
      <div class="intro">
        {{ roleInfo.responsibilities }}
      </div>
      <p><strong>任务周期</strong></p>
      <p class="skill-tip">技能：生成文档、更新审批</p>
      <p class="skill-tip">生成时长:1分32秒32</p>
    </div>

    <!-- 底部统计信息 -->
    <div class="bottom-stats">
      <span class="stat-item">142.31K 知识库(4分32秒前更新)</span>
      <span class="stat-item">420.3K 执行</span>
    </div>
  </div>
</template>

<script setup>
// 导入Vue3的ref
import { ref } from 'vue';

const roleInfo = ref({
  responsibilities: '角色描述'
});

const setRoleInfo = (value) => {
  console.log(value)
  roleInfo.value = value ;
}

/** 暴露方法 */
defineExpose({
  setRoleInfo
})
</script>

<style lang="scss" scoped>

</style>