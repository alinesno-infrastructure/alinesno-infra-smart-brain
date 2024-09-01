<template>
  <div>
    <router-link tag="div" class="header-logo-bar" to="/index">
      <div class="header-logo" v-if="enableLogo" @click="enterDomain">
        <img :src="saasLogoUrl" alt="" />
      </div>
      <a :title="saasTitle" target="_self" class="header-logo-label">
        <span>{{ saasTitle }} </span>
      </a>
      <div v-if="saasUrl" class="dashboard-home" @click="dashboardHome()">
        <i class="fa-solid fa-house"></i> 工作台
      </div>
    </router-link>
  </div>
</template>

<script>

export default {
  name: 'TopHeader',
  components: {
  },
  computed: {
  },
  data() {

    let saasTitle = 'AIP智能体平台'
    let enableLogo = true;
    let saasUrl = 'http://portal.infra.linesno.com/' ; // http://alinesno-infra-plat-console-ui.beta.plat.infra.linesno.com' ;
    let saasLogoUrl = 'http://portal.infra.linesno.com/logo.png' ;
    let displayUrl = ''; 


    return {
      saasTitle,
      saasUrl,
      saasLogoUrl,
      enableLogo,
      displayUrl,
      domainName: null,
    }
  },
  created() {
  },
  methods: {
    dashboardHome() {
      window.open(this.saasUrl, '_blank')
    },
    // 进入企业官网
    enterDomain() {
      if (this.domainName) { // 跳转进入官网
        window.open(this.domainName)
      }
    }
  }
}
</script>
