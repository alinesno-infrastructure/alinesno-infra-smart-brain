<template>
  <div class="siderbar">
    <el-menu default-active="1" class="el-menu-vertical" :collapse="isCollapse" @open="handleOpen" @close="handleClose">
      <el-menu-item index="1" @click="jumpTo">
        <i class="fa-solid fa-desktop"></i>
      </el-menu-item>

      <el-tooltip effect="dark" :content="item.desc" v-for="item in menuItems" :key="item.id" placement="right">
          <el-menu-item :index="item.id" @click="openServiceList(item.link)">
            <i :class="item.icon"></i>
          </el-menu-item>
      </el-tooltip>

    </el-menu>

  </div>
</template>

<script setup>

const dialogVisible = ref(false)
const router = useRouter();

// 菜单列表
const menuItems = ref([
  {id:'2' , icon:'fa-solid fa-masks-theater' , link:'/smart/assistant/role/index' , desc:'角色管理'},
  {id:'9' , icon:'fa-solid fa-file-pdf' , link:'/smart/assistant/roleCatalog/index' , desc:'角色类型'},
  {id:'3' , icon:'fa-solid fa-feather' , link:'/smart/assistant/chainScript/index' , desc:'节点定义'},
  {id:'4' , icon:'fa-solid fa-paper-plane' , link:'/smart/assistant/apikey/index' , desc:'密钥管理'},
  {id:'5' , icon:'fa-solid fa-box' , link:'/smart/assistant/channel/index' , desc:'插件管理'},
  {id:'7' , icon:'fa-solid fa-pen-nib' , link:'/smart/assistant/workflowRecord/index' , desc:'工作流日志'},
  {id:'6' , icon:'fa-brands fa-drupal' , link:'/smart/assistant/requestRecord/index' , desc:'调用日志'},
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
</style>