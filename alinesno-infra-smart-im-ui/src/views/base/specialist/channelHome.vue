<template>

  <div class="app-container tpl-app">
    <div class="search-container-panel">
      <el-row>
        <el-col :span="24">
          <div class="feature-header-xqbyQk feature-team-box">
            <div style="gap: 12px;">
              <h1
                style="font-size: 20px; font-weight: 500; font-style: normal; line-height: 32px; color: rgba(var(--coze-fg-4), var(--coze-fg-4-alpha)); margin: 0px 0px 0px 10px; float: left;">
                频道市场</h1>
            </div>
            <div class="search-container-weDuEn">
              <el-input v-model="input1" style="width: 400px" size="large" placeholder="搜索频道" :suffix-icon="Search" />
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="banner-container-panel" v-if="recommendRole">
      <el-row>
        <el-col :span="17">

          <div class="card-container">
            <div>
              <div class="card-header">
                <img
                  src="https://lf-coze-web-cdn.coze.cn/obj/coze-web-cn/mf/marketplace/static/image/brand-daily-rec.48709716.png"
                  alt="Brand Logo">
              </div>
              <h1><span>🪡深度结合工作细节场景，精准服务，多场景应用</span></h1>
              <p><span>{{ truncateString(recommendRole.responsibilities , 20) }}</span></p>
            </div>
            <el-button type="primary" bg text size="large" @click="handleRoleChat()">
              <span class="semi-button-content">立即聊聊</span>
            </el-button>
          </div>


        </el-col>
        <el-col :span="7">

          <div class="right-container">
            <img src="http://data.linesno.com/banner/agent_bg.png" class="bot-banner-bg" alt="Banner Background Image">

            <div class="banner-info">
              <span class="avatar">
                <img :src="imagePathByPath(recommendRole.roleAvatar)"  alt="Avatar Image">
              </span>
              <div class="info-text">
                <p class="category">{{ recommendRole.roleName }}</p>
                <h1 class="title">{{ recommendRole.roleName }}</h1>
                <div class="author-info">
                  <!-- <div class="semi-image avatar-oDHtb3">
                    <img :src="imagePathByPath(recommendRole.roleAvatar)"  class="semi-image-img" width="14" height="14">
                  </div> -->
                  <div class="author-name"><span>罗文</span></div>
                  <div class="at-name"><span>@LUOWEN</span></div>
                </div>
              </div>
            </div>
          </div>

        </el-col>
      </el-row>
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
                  <div class="semi-image avatar-oDHtb3" style="width: 14px; height: 14px;">
                    <img
                      src="http://data.linesno.com/switch_header.png"
                      class="semi-image-img" width="14" height="14">
                  </div>
                  <div class="semi-space semi-space-align-center semi-space-horizontal" style="gap: 2px;">
                    <span class="semi-typography text" style="max-width: 150px;"><span>Easton</span></span>
                  </div>
                </div>
                <p class="semi-typography card-desc" style="-webkit-line-clamp: 3;">
                  <span>
                    {{ truncateString(item.channelDesc, 65) }}
                  </span>
                </p>
                <div class="semi-space card-tag-list" style="gap: 4px;"></div>
              </div>
            </div>
            <div class="semi-divider semi-divider-horizontal"></div>
            <div class="semi-space" style="width: 100%; gap: 8px;display: flex; justify-content: space-between;">
              <div class="semi-space semi-space-align-center semi-space-horizontal" x-semi-prop="children"
                style="display: inline-flex;">
                <div class="semi-space card-statics" style="gap: 8px;">
                  <span class="semi-typography text-h6"><i class="fa-solid fa-user-ninja"></i> 1.2K</span>
                  <span class="semi-typography text-h6"><i class="fa-solid fa-link"></i> 2.1K</span>
                  <span class="semi-typography text-h6"><i class="fa-solid fa-pen-nib"></i> 45.3K</span>
                </div>
              </div>
              
              <div class="platform-container-YOpW3B">
                <div class="semi-space semi-space-align-center semi-space-horizontal" style="gap: 4px;" v-if="item.knowledgeType">

                  <!-- 包含则显示-->
                  <img v-if="item.knowledgeType.includes('docx')" src="http://data.linesno.com/dataset_icons/docx.webp" class="knowledge-type-icon" />
                  <img v-if="item.knowledgeType.includes('pdf')" src="http://data.linesno.com/dataset_icons/pdf.webp" class="knowledge-type-icon" />
                </div>
              </div>

            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 频道聊天 
    <el-dialog v-model="dialogVisible" :title="chatTitle" width="80%">
      <iframe :src="roleChatUri" class="role-chat-iframe"></iframe>
    </el-dialog>
     -->

  </div>

</template>

<script setup>

import {
  allMyChannel,
} from '@/api/base/im/channel'
import { nextTick, ref } from 'vue';

import SnowflakeId from "snowflake-id";

const snowflake = new SnowflakeId();

const router = useRouter();
const loading = ref(false)

const publicChatChannel = ref([]);
const recommendRole = ref(null);
// const recChatChannel = ref([]);

const chatTitle = ref("")
const dialogVisible = ref(false)
const roleChatUri = ref("")

// const filterRules = ref([
//   {
//     "name": "场景", "codeValue": "initializr.admin.project.template.screen", "items": [
//       { "code": "screen_code_1", "name": "旅游预订" },
//       { "code": "screen_code_2", "name": "在线购物" },
//       { "code": "screen_code_3", "name": "社交媒体" },
//       { "code": "screen_code_4", "name": "健身健康" },
//       { "code": "screen_code_5", "name": "在线视频" }
//     ]
//   },
//   {
//     "name": "类型", "codeValue": "initializr.admin.project.template.type", "items": [
//       { "code": "type_code_1", "name": "移动应用" },
//       { "code": "type_code_2", "name": "网页应用" },
//       { "code": "type_code_4", "name": "社交平台" },
//       { "code": "type_code_5", "name": "健身应用" }
//     ]
//   }
// ]);

const demeChannel = ref([])

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

/** 与单个Role发信息 */
function handleRoleChat() {

  let id = recommendRole.value.id 
  router.push({
      path: '/single/agentChat',
      query: { 'roleId': id, 'channelId': snowflake.generate() }
  })

// roleChatUri.value = "/agentChat?roleId=" + item.id;
// chatTitle.value = item.roleName;
// dialogVisible.value = true;
}

/** 查询所所有我在参与的频道 */
function handleAllMyChannel() {
  loading.value = true;
  allMyChannel().then(response => {
    let items = response.data;

    recommendRole.value = response.recommendRole;
    publicChatChannel.value = items; // .filter(item => item.channelType === '9');
    // recChatChannel.value = items.filter(item => item.channelType === '3');

    // 添加DemoChannel频道
    // publicChatChannel.value = publicChatChannel.value.concat(demeChannel.value);

    loading.value = false;
  })
}

/** 与单个频道发信息 */
// function handleChannelChat(item) {
//   roleChatUri.value = "/channelChat?channel=" + item.id;
//   chatTitle.value = item.channelName;
//   dialogVisible.value = true;
// }

nextTick(() => {
  handleAllMyChannel();
});

</script>

<style lang="scss" scoped>
.right-container {
  position: relative; // 确保子元素可以绝对定位在容器内

  .bot-banner-bg {
    width: 100%;
    height: 100%;
    box-shadow: 0 4px 24px 0 rgba(0, 0, 0, 0.12);
  }

  .banner-info {
    display: flex;
    align-items: center;
    gap: 16px;
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    padding: 5px;
    background: linear-gradient(0deg, rgba(0, 0, 0, 0.8) 0%, rgba(0, 0, 0, 0) 100%);
    flex-direction: row;

    .avatar {
      width: 64px;
      height: 64px;
      background: #f0f0f5;
      border-radius: 8px;

      img {
        display: block;
        height: 100%;
        object-fit: cover;
        width: 100%;
        border-radius: 8px;
      }
    }

    .info-text {
      overflow: hidden;
      flex: 1;
      align-items: flex-start;
      display: flex;
      flex-direction: column;

      .category {
        font-size: 10px;
        font-weight: 400;
        line-height: 12px;
        color: #FFF;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin: 0;
      }

      .title {
        font-size: 18px;
        font-weight: 600;
        line-height: 24px;
        color: #FFF;
        margin: 0;
      }

      .author-info {
        display: flex;
        align-items: center;
        gap: 4px;
        flex-shrink: 0;
        max-width: 100%;
        height: 18px;
        color: rgba(255, 255, 255, 0.39);

        .semi-image {
          width: 14px;
          height: 14px;
          overflow: hidden;
          border-radius: 12px;

          img {
            width: 14px;
            height: 14px;
          }
        }

        .author-name,
        .at-name {
          font-size: 12px;
          font-weight: 400;
          line-height: 18px;
          flex: 1;
        }
      }
    }
  }
}

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
    -webkit-line-clamp: 2;
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