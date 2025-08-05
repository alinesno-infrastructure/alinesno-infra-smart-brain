<template>
  <div class="agent-single-right-panel">
    <!-- 上方统计信息 -->
    <div class="top-stats">
      <span class="stat-item">👍 13</span>
      <span class="stat-item">💬 {{ roleInfo?.chatCount }} 使用</span>
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
      <!-- 解析数组并显示数量 -->
      <span class="stat-item">
        <el-button type="primary" size="small"  text bg>
        {{ parsedKnowledgeBaseIds.length }}个
        </el-button>
        知识库(4分32秒前更新)
      </span>
      <span class="stat-item">420.3K 执行</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const roleInfo = ref({
  responsibilities: '角色描述',
  knowledgeBaseIds: '["1936975012246278146","1952595295703289858"]' // 示例字符串数组
});

// 计算属性：解析knowledgeBaseIds为数组
const parsedKnowledgeBaseIds = computed(() => {
  try {
    // 如果是字符串且不为空，则解析为数组；否则返回空数组
    if (typeof roleInfo.value.knowledgeBaseIds === 'string' && roleInfo.value.knowledgeBaseIds.trim()) {
      return JSON.parse(roleInfo.value.knowledgeBaseIds);
    }
    return [];
  } catch (error) {
    console.error('解析knowledgeBaseIds失败：', error);
    return [];
  }
});

const setRoleInfo = (value) => {
  console.log(value);
  roleInfo.value = value;
};

defineExpose({
  setRoleInfo
});
</script>

<style lang="scss" scoped>
/* 可根据需要添加样式 */
.top-stats, .bottom-stats {
  display: flex;
  gap: 16px;
  padding: 12px;
}

.stat-item {
  color: #666;
  font-size: 14px;
}

.content-area {
  padding: 12px; 
}

.intro {
  margin-bottom: 12px;
  line-height: 1.5;
}

.skill-tip {
  color: #888;
  font-size: 13px;
  margin: 4px 0;
}
</style>