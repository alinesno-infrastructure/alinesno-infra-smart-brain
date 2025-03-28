<template>
  <div>
    <router-link tag="div" class="header-logo-bar" to="/index">
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

// 定义响应式数据
const saasTitle = ref('AIP智能体平台');
const enableLogo = ref(true);
const saasUrl = ref('http://portal.infra.linesno.com/');
const saasLogoId = ref(null);
const saasLogoUrl = ref('http://data.linesno.com/logo_2.png');
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

    const themeStyle = res.data.themeStyle;
    console.log('themeStyle = ' + themeStyle)
    toggleTheme(themeStyle);
  })
});

</script>    

<!-- <script>

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
    let saasLogoUrl = 'http://data.linesno.com/logo_2.png' ;
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
</script> -->
