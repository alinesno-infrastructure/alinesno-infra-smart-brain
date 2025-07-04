<template>
  <div class="siderbar">
    <el-menu default-active="1" class="el-menu-vertical" :collapse="isCollapse" @open="handleOpen" @close="handleClose">
      <!-- <el-menu-item index="1" @click="jumpTo">
        <i class="fa-solid fa-computer"></i>
      </el-menu-item> -->

      <!-- <el-tooltip effect="dark" :content="item.desc" v-for="item in menuItems" :key="item.id" placement="right"> -->
          <el-menu-item 
              :index="item.id" 
              @click="openServiceList(item.link)" 
              v-for="item in menuItems" 
              :key="item.id" 
              class="aip-menu-item">
            <i :class="item.icon"></i>
            <span>
              {{ item.desc }}
            </span>
          </el-menu-item>
      <!-- </el-tooltip> -->

    </el-menu>

    <el-menu style="" class="el-menu-vertical acp-suggest" :collapse="isCollapse" @open="handleOpen" @close="handleClose">
      <!-- <el-tooltip effect="dark" :content="item.desc" v-for="item in footerMenuItems" :key="item.id" placement="right"> -->
          <el-menu-item 
            :index="item.id" 
            v-for="item in footerMenuItems" 
            :key="item.id" 
            @click="openServiceList(item.link)" 
            class="aip-menu-item">
            <i :class="item.icon"></i>
            <span>
              {{ item.desc }}
            </span>
          </el-menu-item>
      <!-- </el-tooltip> -->
    </el-menu>

  </div>
</template>

<script setup>

const dialogVisible = ref(false)
const router = useRouter();

// 菜单列表
// - 角色模板  /dashboard/smart/assistant/roleTemplate/index
// - 模板管理  /template/smart/assistant/template/index
// - 操作日志  /monitor/smart/assistant/apiRecord/index

const footerMenuItems = ref([
  { id: '13', icon: 'fa-solid fa-computer', link: '/smart/assistant/llmModel/index', desc: '模型'},
  { id: '22', icon: 'fa-brands fa-wordpress',  link: '/monitor/smart/assistant/analyse/index', desc: '监控' },
])

const menuItems = ref([
  { id: '19', icon: 'fa-solid fa-computer', link: '/index', desc: '仪盘表' },
  { id: '9', icon: 'fa-solid fa-ship', link: '/expert/smart/assistant/workplace/index', desc: '工作台' },
  { id: '2', icon: 'fa-solid fa-masks-theater', link: '/expert/smart/assistant/role/index', desc: '智能体' },
  { id: '3', icon: 'fa-solid fa-file-pdf', link: '/expert/smart/assistant/roleCatalog/index', desc: '类型' },
  { id: '4', icon: 'fa-solid fa-box', link: '/prompt/smart/assistant/channel/index', desc: '频道' },
  { id: '5', icon: 'fa-solid fa-feather', link: '/prompt/smart/assistant/scene/index', desc: '场景' },

  { id: '18', icon: 'fa-solid fa-file-pdf', link: '/base/search/vectorData/index', desc: '数据集' },
  { id: '14', icon: 'fa-brands fa-skype', link: '/base/search/catalog/index', desc: '分类' },
  
  { id: '6', icon: 'fa-solid fa-screwdriver-wrench', link: '/template/smart/assistant/plugin/index', desc: '工具' },
  // { id: '7', icon: 'fa-solid fa-paper-plane', link: '/template/smart/assistant/secret/index', desc: '密钥管理' },

]);

// 打开服务市场
function openServiceList(_path) {
  router.push({ path: _path });
}

// 打开客户配置
function jumpToConstomTheme() {
  router.push({ path: "/dashboard/dashboardTheme" });
}

// 打开首页
function jumpTo() {
  router.push({ path: "/index" });
}

// 打开智能客服
function openSmartService() {
  router.push({ path: "/dashboard/smartService" });
}

</script>

<style lang="scss" scoped>
.el-menu-vertical:not(.el-menu--collapse) {
  width: 65px;
}

.acp-suggest {
  bottom: 20px;
  position: absolute;
}

.siderbar {
  float: left;
  height: 100%;
  width: 64px;
  border-right: 1px solid #e6e6e6;
  padding-top: 40px;
  overflow: hidden;
  background-color: #fff;
  position: fixed;
}

.aip-menu-item {

  display: flex;
  flex-direction: column;
  gap: 5px;
  line-height: 1.2rem;
  padding-top: 0px;
  margin: 5px;
  border-radius: 10px;
  justify-content: center;

  span{
    font-size:12px;
    color: #888;
  }
}
</style>