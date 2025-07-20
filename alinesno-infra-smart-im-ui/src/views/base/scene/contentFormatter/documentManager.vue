<!-- 文档管理界面 -->
<template>
  <ContentFormatterContainer>
    <div class="app-container">
        文档管理界面
    </div>
  </ContentFormatterContainer>  
</template>

<script setup>

import { onBeforeUnmount, ref, shallowRef, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
const route = useRoute()

import ContentFormatterContainer from './common/ContentFormatterContainer';

</script>