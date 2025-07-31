<template>
  <div class="siderbar">
    <el-menu :default-active="activeMenu" class="el-menu-vertical" :collapse="isCollapse" @open="handleOpen" @close="handleClose">

        <el-menu-item :index="item.id" @click="openServiceList(item.link)" v-for="item in menuItems" :key="item.id"  class="aip-menu-item">
          <i :class="item.icon"></i>
          <span>
            {{ item.desc }}
          </span>
        </el-menu-item>
    </el-menu>

    <el-menu style="" class="el-menu-vertical acp-suggest" :collapse="isCollapse" @open="handleOpen" @close="handleClose">

        <el-menu-item index="10" @click="openServiceList('/dataset')" class="aip-menu-item">
              <i class="fa-solid fa-file-pdf"></i>
              <span>
                知识库
              </span>
          </el-menu-item>

        <el-menu-item index="9" @click="addChannel()" class="aip-menu-item">
          <i class="fa-solid fa-marker"></i>
          <span>
            创建频道
          </span>
      </el-menu-item>
        <el-menu-item index="12" @click="dialogVisible = true" class="aip-menu-item">
          <i class="fa-solid fa-message"></i>
          <span>
            建议
          </span>
        </el-menu-item>
    </el-menu>

    <!-- 建议和反馈 -->
    <el-dialog v-model="dialogVisible" title="使用建议和反馈" width="30%" :append-to-body="true" :before-close="handleClose">

      <el-form ref="ruleFormRef" label-position="top" :model="ruleForm" :rules="rules" label-width="120px"
        class="demo-ruleForm" :size="formSize" status-icon>

        <el-form-item label="您对控制台首页满意吗？" prop="name">
          <el-rate v-model="value2" :colors="colors" />
        </el-form-item>

        <el-form-item label="您对控制台首页满意吗？" prop="name">
          <el-input type="textarea" />
        </el-form-item>

        <el-form-item style="margin-top:30px">
          <el-button type="primary" @click="submitForm(ruleFormRef)">保存</el-button>
          <el-button @click="resetForm(ruleFormRef)">重置</el-button>
        </el-form-item>

      </el-form>
    </el-dialog>

    <!-- 创建频道弹窗 -->
    <ChannelGroup ref="createChildComp" />

    <!-- 创建场景弹窗 -->
    <ScreenGroup ref="createScreenComp" />

  </div>
</template>

<script setup>


import ChannelGroup from "@/views/base/chat/channelGroup.vue";
import ScreenGroup from "@/views/base/scene/screenGroup.vue";

const createChildComp = ref(null);
const createScreenComp = ref(null);

const dialogVisible = ref(false)

const router = useRouter();
const route = useRoute();

// 菜单列表
const menuItems = ref([
  { id: '1', icon: 'fa-solid fa-house-user', link: '/index', desc: '工作台' }, 
  { id: '3', icon: 'fa-solid fa-box', link: '/scene', desc: '场景' },
  { id: '4', icon: 'fa-solid fa-sailboat', link: '/store', desc: '智能体' },
  // { id: '6', icon: 'fa-solid fa-file-pdf', link: '/dataset', desc: '知识库' },
  { id: '2', icon: 'fa-solid fa-user-tag', link: '/channel', desc: '频道' },
  { id: '5', icon: 'fa-solid fa-masks-theater', link: '/agentList', desc: '组织' },
]);

// 打开服务市场
function openServiceList(_path) {
  router.push({ path: _path });
}

// 打开客户配置
function jumpToConstomTheme() {
  router.push({ path: "/dashboard/dashboardTheme" });
}

// 添加频道
function addChannel(){
  createChildComp.value.handleOpenChannel(true);
}

// 添加场景
function addScreen(){
  createScreenComp.value.handleOpenChannel(true);
}

// 打开首页
function jumpTo() {
  router.push({ path: "/index" });
}

// 打开智能客服
function openSmartService() {
  router.push({ path: "/dashboard/smartService" });
}

const activeMenu = computed(() => {
  const currentPath = route.path
  const item = menuItems.value.find(item => {
    return currentPath.startsWith(item.link) // 匹配路由前缀
  })
  return item ? item.id : '1'
})

</script>

<style lang="scss" scoped>
.el-menu-vertical:not(.el-menu--collapse) {
  width: 70px;
}

.acp-suggest {
  bottom: 20px;
  position: absolute;
}

.siderbar {
  float: left;
  height: 100%;
  width: 70px;
  border-right: 1px solid #e6e6e6;
  padding-top: 40px;
  overflow: hidden;
  background-color: #fff;
  position: fixed;
  margin-top: 10px;
  margin-bottom: 20px;
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
