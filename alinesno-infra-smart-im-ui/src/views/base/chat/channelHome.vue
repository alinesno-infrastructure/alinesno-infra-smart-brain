<template>

  <div class="app-container tpl-app">
    <div class="search-container-panel">
      <el-row>
        <el-col :span="24">
          <div class="feature-header-xqbyQk feature-team-box">
            <div style="gap: 12px;">
              <h1>
                频道市场</h1>
            </div>
            <div class="search-container-weDuEn">
              <el-input v-model="input1" style="width: 400px" size="large" placeholder="搜索频道" :suffix-icon="Search" />
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="header" style="display: flex;margin: 5px 0px;">
        <span style="font-size: 13px;margin-left:10px;color: #a5a5a5;">这里包含所有需要运营的频道能力（Beta）</span>
    </div>

    <div class="channel-container-panel">
      <el-row>

        <el-col :span="6" v-for="(item, index) in publicChatChannel" :key="index" style="padding:8px;">
          <div class="semi-card-container" @click="enterChannel(item)">
            <div class="semi-space cart-head-continer" style="gap: 16px;">
              <div class="cart-head-content">
                <div class="cart-head-content">
                  <span class="semi-avatar semi-avatar-square">
                    <img :src="imagePath(item)" class="">
                  </span>
                </div>
              </div>
              <div class="semi-space info-container" style="gap: 6px;">
                <span class="semi-typography card-title">
                  <span>
                    {{ item.channelName }}
                  </span>
                </span>

                <div class="semi-space container-center" style="gap: 6px;">
                  <div class="semi-space semi-space-align-center semi-space-horizontal" style="display: flex;flex-direction: row;align-items: center;gap: 5px;">
                    <span class="semi-typography text" >
                      <i class="fa-solid fa-user-shield"></i>
                      {{ item.orgName }}
                    </span>
                  </div>
                </div>

                <p class="semi-typography card-desc">
                  <span>
                    {{ truncateString(item.channelDesc, 65) }}
                  </span>
                </p>
                <div class="semi-space card-tag-list" style="gap: 4px;"></div>
              </div>
            </div>
            <div class="semi-divider semi-divider-horizontal"></div>
            <div class="semi-space" style="width: 100%; gap: 8px;display: flex; justify-content: space-between;">

              <div class="semi-space semi-space-align-center card-statics semi-space-horizontal" style="display: flex;align-items: center;gap: 10px;">

                <span class="semi-typography text-h6" v-if="item.channelType == 'public'" ><i class="fa-solid fa-globe"></i> 公共频道</span>
                <span class="semi-typography text-h6" v-if="item.channelType == 'private'"><i class="fa-solid fa-lock"></i> 私人频道</span>
                <span class="semi-typography text-h6" v-if="item.channelType == 'org'" ><i class="fa-solid fa-truck-plane"></i> 组织频道</span>

                <div class="semi-space card-statics" style="gap: 8px;">
                  <span class="semi-typography text-h6"><i class="fa-solid fa-masks-theater"></i> 2.1K</span>
                  <span class="semi-typography text-h6"><i class="fa-solid fa-pen-nib"></i> 45.3K</span>
                </div>
              </div>
              
              <div class="platform-container-YOpW3B">
                <div class="semi-space semi-space-align-center semi-space-horizontal" style="gap: 4px;" v-if="item.knowledgeType">
                  <img v-if="item.knowledgeType.includes('docx')" src="http://data.linesno.com/dataset_icons/docx.webp" class="knowledge-type-icon" />
                  <img v-if="item.knowledgeType.includes('pdf')" src="http://data.linesno.com/dataset_icons/pdf.webp" class="knowledge-type-icon" />
                </div>
              </div>

            </div>
          </div>
        </el-col>
      </el-row>
    </div>

  </div>

</template>

<script setup>

import { ElMessageBox , ElLoading } from 'element-plus'

import {
  allMyChannel,
} from '@/api/base/im/channel'
import { nextTick, ref } from 'vue';
import SnowflakeId from "snowflake-id";

const snowflake = new SnowflakeId();

const router = useRouter();
// const loading = ref(false)

const publicChatChannel = ref([]);
const recommendRole = ref(null);

const data = reactive({
  form: {},
  queryParams: {
    total: 100,
    pageNum: 1,
    pageSize: 8,
    roleName: undefined,
    roleName: undefined,
    responsibilities: undefined,
    status: undefined,
    deptId: undefined
  },
});

const { queryParams } = toRefs(data);

function enterChannel(item) {
  router.push({
    path: '/chat',
    query: { 'channel': item.id }
  })
}

/** 查询所所有我在参与的频道 */
// function handleAllMyChannel() {
//   loading.value = true;
//   allMyChannel().then(response => {
//     let items = response.data;

//     recommendRole.value = response.recommendRole;
//     publicChatChannel.value = items; 

//     loading.value = false;

//     let hasRole = response.hasRole;  // 判断组织是否包含角色
//     if (!hasRole) {
//       // 显示推荐角色
//         ElMessageBox.confirm('你所有当前组织未包含智能体，是否需要到智能体市场选择体验', '系统提示', { confirmButtonText: '进入智能体市场', cancelButtonText: '后期建立', type: 'warning' })
//         .then(() => {
//           router.push({ path: '/agentMarket'})
//         });
//     }

//   })
// }

function handleAllMyChannel() {
  // loading.value = true;

  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.2)',
  })

  allMyChannel().then(response => {
    let items = response.data;

    recommendRole.value = response.recommendRole;
    publicChatChannel.value = items; 

    loading.close()

    let hasRole = response.hasRole;  // 判断组织是否包含角色

    // 检查用户是否已经做出选择
    const userHasChosen = localStorage.getItem('userHasChosenRoles');

    if (!hasRole && !userHasChosen) {
      // 显示推荐角色
      ElMessageBox.confirm(
        '你所有当前组织未包含智能体，是否需要到智能体商店选择体验', 
        '系统提示', { 
          confirmButtonText: '进入智能体商店', 
          cancelButtonText: '后期建立', 
          type: 'warning' 
        }
      ).then(() => {
        router.push({ path: '/store'});
        // 用户选择了进入智能体市场，保存选择状态
        localStorage.setItem('userHasChosenRoles', 'true');
      }).catch(() => {
        // 用户选择了后期建立，也视为做出了选择
        localStorage.setItem('userHasChosenRoles', 'true');
      });
    }

  }).catch(error => {
    // 处理错误
    console.error("获取频道信息失败", error);
    loading.close()
  });
}

nextTick(() => {
  handleAllMyChannel();
});

</script>

<style lang="scss" scoped>

.card-container {
  margin-left: 10px;
  cursor: pointer;
  align-items: flex-start;
  width: 100%;
  height: 270px;
  padding: 16px 0;
  display: inline-flex;
  flex-direction: column;

  .card-header {
    height: 28px;

    img {
      height: 28px;
    }
  }

  h1 {
    line-clamp: 2;
    max-height: 112px;
    margin-bottom: 8px;
    font-size: 40px;
    font-weight: 700;
    line-height: 48px;
    color: #1D1C23;
  }

  p {
    height: 24px;
    margin-bottom: 28px;
    font-size: 16px;
    font-weight: 400;
    line-height: 24px;
    color: rgba(29, 28, 35, 0.6);
  }

}

.knowledge-type-icon{
  width:20px;
  height:20px;
  margin-top:10px
}
</style>