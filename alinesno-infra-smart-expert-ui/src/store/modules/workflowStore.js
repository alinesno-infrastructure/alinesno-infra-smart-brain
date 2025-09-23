// src/stores/workflowStore.js
import { defineStore } from 'pinia';

export const useWorkflowStore = defineStore('workflow', {
  state: () => ({
    // 存储 lf 实例
    lf: null,
  }),
  actions: {
    // 设置 lf 实例（在 index.vue 中调用）
    setLF(lfInstance) {
      this.lf = lfInstance;
    },
    // 清除 lf 实例（页面卸载时调用）
    clearLF() {
      this.lf = null;
    },
  },
});