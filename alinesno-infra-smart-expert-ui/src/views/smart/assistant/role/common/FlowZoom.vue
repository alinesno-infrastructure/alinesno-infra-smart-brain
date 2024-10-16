<template>
  <div class="flow-design-operate">

    <div class="flow-design-zoom">
      <div class="flow-design-zoom-less" @click="onZoomOut"></div>
      <span>{{ zoomValue }}%</span>
      <div class="flow-design-zoom-more" @click="onZoomIn"></div>
    </div>
  </div>
</template>

<script setup>

const zoomValue = ref(100)

</script>
