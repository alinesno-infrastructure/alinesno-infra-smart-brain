// stores/configStore.js
import { defineStore } from 'pinia';

export const useConfigStore = defineStore('config', {
  state: () => ({
    studioUrl: '',
    displayService: ''
  }),
  actions: {
    updateConfig(studioUrl, displayService) {
      this.studioUrl = studioUrl;
      this.displayService = displayService;
    }
  }
});