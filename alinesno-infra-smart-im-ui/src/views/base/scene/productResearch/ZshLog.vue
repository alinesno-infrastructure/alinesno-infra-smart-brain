<template>
  <div class="zsh-log-container" :class="theme">
    <div
      v-if="logs.length === 0"
      class="empty-state"
    >
      <el-empty description="当前无日志记录" />
    </div>

    <div
      v-else
      class="log-list"
    >
      <div
        v-for="(log, index) in logs"
        :key="index"
        class="log-item"
        :style="{ '--level-color': getLevelColor(log.level) }"
      >
        <!-- 时间戳 -->
        <span v-if="showTime" class="log-time">
          [{{ log.logTime.slice(11, 19) }}]
        </span>

        <!-- 日志级别 & 来源 -->
        <span class="log-meta">
          <span class="level">{{ log.level }}</span>
          <span class="source">:: {{ log.sourceClass }}</span>
        </span>

        <!-- 日志消息 -->
        <span class="log-message">
          {{ log.message }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

//  props 定义
const props = defineProps({
  logs: {
    type: Array,
    default: () => []
  },
  showTime: {
    type: Boolean,
    default: true
  },
  highlightLevel: {
    type: Boolean,
    default: true
  },
  theme: {
    type: String,
    default: 'zsh-dark',
    validator: val => ['zsh-dark', 'zsh-light'].includes(val)
  }
});

//  颜色映射（OhMyZsh 主题色）
const levelColorMap = {
  DEBUG: '#89b4fa',   // 蓝色（调试）
  INFO: '#a7e08b',    // 绿色（信息）
  WARN: '#f9e2af',    // 黄色（警告）
  ERROR: '#f38ba8',   // 红色（错误）
  DEFAULT: '#d8dee9'  // 中性色（默认）
};

//  计算属性：获取日志级别颜色
const getLevelColor = (level) => {
  return props.highlightLevel 
    ? levelColorMap[level.toUpperCase()] || levelColorMap.DEFAULT
    : 'inherit';
};
</script>

<style scoped lang="scss">
.zsh-log-container {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.9rem;
  line-height: 1.4;
  padding-left: 1rem;
  border-radius: 8px;
  overflow-x: auto;
  white-space: pre-wrap;

  .empty-state {
    padding: 2rem;
    color: #6c757d;
  }

  .log-list {
    gap: 0.5rem;
    display: flex;
    flex-direction: column;
  }

  .log-item {
    position: relative;
    padding-left: 1.5rem;
    border-left: 3px solid var(--level-color);
    background: rgba(0, 0, 0, 0.03);
    border-radius: 4px;
    // padding-top: 2px;
    // padding-bottom: 2px;
    box-shadow: 0 1px 2px rgba(0,0,0,0.05);

    &:before {
      content: '→';
      position: absolute;
      left: 0;
      color: var(--level-color);
      font-weight: bold;
    }
  }

  .log-time {
    color: #6c757d;
    margin-right: 0.5rem;
  }

  .log-meta {
    color: #5865f2; /* OhMyZsh 主题标志性紫色 */
    font-weight: 500;

    .level {
      text-transform: uppercase;
      font-size: 0.85rem;
      padding: 0 0.3rem;
      border-radius: 2px;
      font-weight: 600;
    }

    .source {
      color: #3b3b3b;
      font-weight: 400;
      margin-left: 0.5rem;
    }
  }

  .log-message {
    color: #24292e;
    word-break: break-all;
  }
}

/* 浅色主题变体 */
.zsh-log-container.zsh-light {
  background: #fafbfc;
  border: 1px solid #d1d5db;

  .log-item {
    background: #ffffff;
    border-color: var(--level-color);
    box-shadow: 0 1px 3px rgba(0,0,0,0.08);
  }

  .log-time {
    color: #71767b;
  }

  .log-meta {
    color: #1f3d7a; /* 浅色主题紫色 */

    .source {
      color: #555;
    }
  }

  .log-message {
    color: #181818;
  }
}
</style>