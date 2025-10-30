<template>
  <div>
    <div class="header-logo-bar">
      <div class="header-logo" v-if="enableLogo" @click="enterDomain">
        <img :src="saasLogoUrl" alt="" />
      </div>
      <a :title="saasTitle" target="_self" class="header-logo-label">
        <span>{{ saasTitle }} </span>
      </a>
      <div v-if="saasUrl" class="dashboard-home" @click="dashboardHome()">
        <i class="fa-solid fa-house"></i> 工作台
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AIPLogo from '@/assets/logo/logo.png'

const saasTitle = ref('AIP智能设施')
const enableLogo = ref(true)
const saasUrl = ref('https://alinesno-infra-plat-console-ui.linesno.com')
const saasLogoUrl = ref(AIPLogo)
const displayUrl = ref('')
const domainName = ref(null)

const dashboardHome = () => {
  window.location.href = saasUrl.value
}

const enterDomain = () => {
  if (domainName.value) {
    window.open(domainName.value)
  }
}
</script>