<template>
  <div class="iframe-container">
    <iframe
      ref="iframeRef"
      :src="iframeUrl"
      frameborder="0"
      allowfullscreen
    ></iframe>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  pptId: {
    type: String,
    required: true
  },
  editorType: {
    type: String,
    default: 'editor'
  }
})

const iframeRef = ref(null)
const baseUrl = 'http://alinesno-infra-smart-aippt-ui.beta.base.infra.linesno.com'

const iframeUrl = computed(() => {
  return `${baseUrl}?pptId=${props.pptId}&editorType=${props.editorType}`
})

// Handle iframe resizing
const handleResize = () => {
  if (iframeRef.value) {
    iframeRef.value.style.height = `${window.innerHeight}px`
    iframeRef.value.style.width = '100%'
  }
}

onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
.iframe-container {
  width: 100%;
  height: calc(100vh - 60px) ;
  
  iframe {
    width: 100%;
    height: 100%;
    display: block;
  }
}
</style>