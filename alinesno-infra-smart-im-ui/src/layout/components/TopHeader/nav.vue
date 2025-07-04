<template>
  <nav class=" header-text">
    <div class="acp-header-item ">
      <div class="header-label-text" >
        <div style="cursor: pointer;display: flex;flex-direction: row;align-items: center;gap: 3px;" @click="navToggleTheme()">
            <svg-icon v-if="themeType!== 'dark'" icon-class="dark-theme" style="font-size:16px" /> 
            <svg-icon v-else icon-class="light-theme" style="font-size:16px" /> 主题 
        </div>
      </div>
    </div> 
    
    <div class="acp-header-item ">
      <a class="header-label-text" @click="setHomePage()">
        <i class="fa-solid fa-screwdriver-wrench"></i> 设首页 
      </a>
    </div> 

    <div class="acp-header-item ">
      <router-link class="header-label-text" to="/dashboard/learnPanel">
        <i class="fa-regular fa-file-word"></i> 手册
      </router-link>
    </div>
     
    <div class="acp-header-item " v-if="displayService !== 'none'">
      <router-link class="header-label-text" to="/dashboard/suportTechnique">
        <i class="fa-solid fa-user-tag"></i> 服务
      </router-link>
    </div>

    <div class="acp-header-item ">
      <a class="header-label-text" target="_blank" :href="studioUrl? studioUrl : 'http://alinesno-infra-plat-console-ui.beta.base.infra.linesno.com'">
        <i class="fa-solid fa-feather"></i> 工作区(Studio)
      </a>
    </div>

    <el-dropdown size="mini">
      <div class="acp-header-item " style="display: flex">
        <a class="header-label-text">
          <i class="fa-solid fa-shield-halved"></i>
          {{ userStore.nickName }}
          {{ account }}
          <el-icon>
            <ArrowDownBold />
          </el-icon>
        </a>
        <a class="header-label-text" target="_blank">
          <img :src="userStore.avatar" class="su70ez-0 CB-gLgKdv" alt="" />
        </a>
      </div>

      <template #dropdown>
        <el-dropdown-menu style="width: 350px">

          <el-container style="margin-bottom: 15px">
            <el-header class="bg-color-base info-h" style="">
              <p class="color-text-secondary f-e-s">登陆名: {{ userStore.name }}
                <span class="copy-user-id">
                  <i class="fa-solid fa-clone"></i> 
                </span>
              </p>
              <p class="color-text-primary f-e-s" v-if="userStore.org">组织: {{ userStore.org.orgName }}</p>
              <p class="color-text-secondary f-e-s" v-if="userStore.org">角色: {{ userStore.org.roleName }}</p>
            </el-header>
          </el-container>

          <router-link :to="{ path: '/user/profile' }" replace>
            <el-dropdown-item>
              <i class="fa-solid fa-user-pen"></i> 用户中心
            </el-dropdown-item>
          </router-link>

          <router-link :to="{ path: '/dashboard/dashboardTheme' }" replace>
          <el-dropdown-item>
            <i class="fa-solid fa-desktop"></i> 机构配置
          </el-dropdown-item>
          </router-link>

          <router-link :to="{ path: '/dashboard/dashboardTheme' }" replace>
            <el-dropdown-item>
              <i class="fa-solid fa-user-group"></i> 锁屏
            </el-dropdown-item>
          </router-link>

          <el-container>
            <el-main>
              <el-button type="primary" style="width: 100%" @click="logout">退出登陆</el-button>
            </el-main>
          </el-container>

        </el-dropdown-menu>
      </template>
    </el-dropdown>

  </nav>
</template>

<script setup>
import { ElMessageBox , ElMessage } from 'element-plus'
import useUserStore from '@/store/modules/user'
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { toggleTheme } from '@/utils/chat'

import { useConfigStore } from '@/store/modules/configStore';
const configStore = useConfigStore();

const userStore = useUserStore()
// 获取路由实例
const router = useRouter()
// 获取当前路由信息
const route = useRoute()

// 使用计算属性获取数据
const studioUrl = computed(() => configStore.studioUrl);

const displayService = computed(() => configStore.displayService);

// 初始化为 localStorage 中存储的主题类型，如果没有则默认为空字符串
const themeType = ref(localStorage.getItem('themeType') || '')

function logout() {
  ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.ssoLogOut().then(() => {
      location.href = '/index'
    })
  }).catch(() => {})
}

/** 判断用户是否已经存在组织 */
function handleInOrg() {
  let org = userStore.org
  if (!org) { 
    router.push('/createOrg')
  }
}

function navToggleTheme() {
  // 切换主题类型
  themeType.value = themeType.value === 'dark' ? '' : 'dark'
  toggleTheme(themeType.value);
}

// 设置主页
function setHomePage() {
  // 获取当前路径（去掉域名部分）
  const currentPath = window.location.href.replace(window.location.origin, '')
  // 去掉开头的斜杠
  const homePage = currentPath.startsWith('/') ? currentPath.substring(1) : currentPath
  
  ElMessageBox.confirm(
    `您确定要将当前页面设置为主页吗？`,
    '设置默认主页',
    {
      confirmButtonText: '设置',
      cancelButtonText: '默认主页',
      distinguishCancelAndClose: true,
      showClose: true, // 隐藏右上角关闭按钮
      closeOnClickModal: false, // 点击遮罩层不关闭
      closeOnPressEscape: false, // 按ESC不关闭
      cancelButtonClass: 'set-default-btn', // 用于自定义取消按钮样式
      beforeClose: (action, instance, done) => {
        if (action === 'cancel') {
          // 用户点击了"设为默认"
          const data = {
            homePage: 'index',
            type: 'default'
          }
          userStore.setHomePage(data).then(() => {
            done()
            ElMessage.success('已设置为默认主页(index)')
          })
        } else if (action === 'confirm') {
          // 用户确认设置当前页面为主页
          const data = {
            homePage: homePage,
            type: 'settings'
          }
          userStore.setHomePage(data).then(() => {
            done()
            ElMessage.success('主页设置成功')
          })
        } else {
          done()
        }
      }
    }
  ).catch(() => {
    // 用户点击了取消或关闭
    // done()
  })
}

// 进入默认主页
const enterHomePage = async () => {
  // 获取当前路径（去掉域名部分）
  const currentPath = window.location.pathname;
  
  // 如果当前已经是首页(index)，则检查是否有自定义主页设置
  if (currentPath === '/index' || currentPath === '/') {
    try {
      // 从用户存储中获取主页配置
      const homePageConfig = await userStore.getHomePage();
      
      // 如果有自定义主页配置，并且不是默认index页面，则跳转
      if (homePageConfig && homePageConfig.homePage && homePageConfig.homePage !== 'index') {
        // 确保路径以斜杠开头
        const targetPath = homePageConfig.homePage.startsWith('/')  ? homePageConfig.homePage  : `/${homePageConfig.homePage}`;
        console.log('homePageConfig.homePage', homePageConfig.homePage);
        
        // 避免重复跳转
        if (targetPath !== currentPath) {
          router.push(targetPath);
        }
      }
    } catch (error) {
      console.error('获取主页配置失败:', error);
      // 可以在这里添加错误处理，比如跳转到默认页
      router.push('/index');
    }
  }
};

onMounted(() => {
  handleInOrg(); 
  nextTick(() => {
    enterHomePage(); 
  });
})

// 监听路由变化
watch(() => route.path, (newPath) => {
  // 如果当前是首页路径，则检查是否需要跳转到自定义主页
  if (newPath === '/' || newPath === '/index') {
    enterHomePage()
  }
})

</script>

<style lang="scss" scoped>
.color-text-secondary , .color-text-primary{
  font-size: var(--el-font-size-base);

  .copy-user-id {
    margin-left: 10px;
    cursor: pointer;
  }
}
</style>