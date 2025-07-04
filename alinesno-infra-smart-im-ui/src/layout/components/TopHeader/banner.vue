<template>
  <div>
    <router-link class="header-logo-bar" to="/index">
      <div class="header-logo" v-if="enableLogo" @click="enterDomain">
        <img v-if="!saasLogoId" :src="saasLogoUrl" alt="" />
        <img v-else :src="imagePathByPath(saasLogoId)" alt="" />
      </div>
      <a :title="saasTitle" target="_self" class="header-logo-label">
        <span>{{ saasTitle }} </span>
      </a>
    </router-link>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { toggleTheme } from '@/utils/chat'
import { getByOrg } from '@/api/base/im/org';
import AIPLogo from '@/assets/logo/logo.png';

import { useConfigStore } from '@/store/modules/configStore'; 

// 定义响应式数据
const saasTitle = ref('AIP智能体平台');
const enableLogo = ref(true);
const saasUrl = ref('http://portal.infra.linesno.com/');
const saasLogoId = ref(null);
const saasLogoUrl = ref(AIPLogo) ; 
const displayUrl = ref('');
const domainName = ref(null);

// 定义方法
const dashboardHome = () => {
  window.open(saasUrl.value, '_blank');
};

const enterDomain = () => {
  if (domainName.value) {
    window.open(domainName.value);
  }
};

// 初始化数据
onMounted(async () => {
  getByOrg().then(res => {

    console.log('res = ' + res);
    saasTitle.value = res.data.productName;
    saasLogoId.value = res.data.logoImg;

    const studioUrl = res.data.studioUrl;
    const displayService = res.data.displayService;

    // 更新store
    const configStore = useConfigStore();
    configStore.updateConfig(studioUrl, displayService);

    const themeStyle = res.data.themeStyle;
    console.log('themeStyle = ' + themeStyle)
    toggleTheme(themeStyle);
  })
});

</script>    
