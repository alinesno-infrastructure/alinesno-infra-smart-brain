<template>
  <div id="flow-design-map" class="flow-design-map">
    <!-- <img :src="$store.state.flow.mapImg" /> -->
    <div id="flow-design-map-mask" class="map-mask" :style="mapMask" @mousedown="handleMouseDown" @mouseup="handleMouseUp" @mouseleave="handleMouseUp"></div>
  </div>
</template>

<script setup>

// import { flowMixin } from './flowMixin'

</script>
