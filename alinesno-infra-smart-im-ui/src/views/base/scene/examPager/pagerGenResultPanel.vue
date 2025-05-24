<template>
  <div class="pager-gen-result-panel">
    <PagerContainer 
      :showHeader="false"
      ref="pagerContainerRef" />
  </div>
</template>

<script setup>

import PagerContainer from './components/PagerContainer.vue'

const pagerContainerRef = ref(null)

</script>

<style lang="scss" scoped>
.pager-gen-result-panel{
  margin-bottom:20px;
}
</style>