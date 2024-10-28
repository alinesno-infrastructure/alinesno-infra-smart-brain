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
      <div class="intro">4 道 AI 测试题，Get 你在未来世界的隐藏身份！ 完成 4 道题目, 即可解锁奖品，iPhone 16 Pro Max、Apple Watch S9、AirPods
        4、扣子周边等500份奖品你来拿！</div>
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
</script>

<style lang="scss" scoped>

.agent-single-right-panel {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 40px);
  border-left: 1px solid #e6e5e9;

  .top-stats,
  .bottom-stats {
    padding: 10px;
    margin-top: 10px;
  }

  .stat-item {
    font-size: 14px;
    color: #333;
    margin-right: 10px;
  }

  .content-area {
    flex: 1; // 让内容区域扩展以填充剩余空间
    padding: 20px;
    background-color: #fff;
    border-radius: 5px;
    text-align: left;

    .intro {
      font-size: 15px;
      margin-bottom: 10px;
      margin-bottom: 20px;
    }

    .skill-tip {
      color: #555;
    }

    p {
      font-size: 14px;
      margin-bottom: 5px;
      margin-top: 10px;
    }
  }

  .bottom-stats {
    margin-top: auto;
    margin-bottom: 30px;
  }
}
</style>