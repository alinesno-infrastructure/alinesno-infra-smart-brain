<template>

  <div class="app-container tpl-app">

      <!-- 模板选择 -->
      <div class="template-header">
          <div class="vc-div div_l14lqa17 top-container" v-loading="loadingFilter">
              <div class="vc-div div_l14lqa16">
                  <div style="padding-bottom: 20px;font-weight: 550; border-bottom: var(--card-border-width, 1px) var(--card-border-style, solid) var(--card-border-color, #e3e4e6);margin-bottom: 20px;">
                      <i class="el-icon-link"></i> 应用频道广场
                      <span style="font-size:13px;color:#a5a5a5">
                          应用模板中心会自动上传到你的项目git基线
                      </span>
                  </div>
                  <div class="vc-div div_l14lqa15">
                      <div class="vc-div div_l14lqa0k" style="margin-bottom:10px" v-for="(item, index) in filterRules"
                          :key="index">
                          <div class="vc-div div_l14lqa0h">
                              <div class="vc-div div_l14lqa0e tagMain">
                                  <div class="vc-text" title="">
                                      {{ item.name }}
                                  </div>
                              </div>
                              <div class="vc-div div_l14lqa0g">
                                  <div class="vc-div div_l14lqa0f tagNormal" v-for="(i, ind) in item.items"
                                      :key="ind">
                                      <div class="vc-text" title="">
                                          {{ i.name }}
                                      </div>
                                  </div>
                              </div>
                          </div>
                      </div>

                  </div>
              </div>
          </div>
      </div>

      <div class="gen-template-box" v-if="publicChatChannel.length == 0">
          <el-col :sm="24">
              <el-empty description="还没有创建模板,可以根据提示链接创建自己的工程模板">
                  <el-link type="primary" icon="el-icon-link">如何创建工程模板?</el-link>
              </el-empty>
          </el-col>
      </div>

      <!-- 模板内容 -->
      <div class="vc-div div_l14lqa1k tpl-container" v-loading="loading || loadingFilter">

          <div class="vc-div div_l14lqa1j tpl-item" v-for="(item, index) in publicChatChannel" :key="index">
              <div class="vc-div div_l14lqa19 tpl-item-image">
                  <img class="vc-image--yida image_l14lqa18" 
                    :src="imagePath(item)" 
                    style="height: 90px; object-fit: cover; border-radius: 0px;">
              </div>
              <div class="vc-div div_l14lqa1i" data-spm-anchor-id="0.0.0.i3.72032480dZzISR">
                  <div class="vc-div div_l14lqa1c tpl-item-title">
                      <div class="vc-text text_l14lqa1a" title=""
                          style="display: -webkit-box; -webkit-box-orient: vertical; overflow: hidden; -webkit-line-clamp: 1;">
                          {{ item.channelName }}
                      </div>
                  </div>
                  <div class="vc-div tpl-item-description">
                      <div class="vc-text text_l14lqa1d" :title="item.tempDesc" style="display: -webkit-box; -webkit-box-orient: vertical; overflow: hidden; -webkit-line-clamp: 2;">
                          {{ truncateString(item.channelDesc,35) }}
                      </div>
                  </div>
                  <div class="vc-div div_l14lqa1h tpl-item-footer">
                      <div class="vc-text text_l14lqa1g" :title="item.tempTeam">
                        <el-button @click="handleChannelChat(item)" type="primary" text bg>
                            <i class="fa-solid fa-location-arrow"></i>&nbsp;打开
                        </el-button>
                      </div>
                      <div class="vc-text"  @click="enterChannel(item)" style="cursor:pointer; line-height:35px">
                          <i class="fa-solid fa-fire"></i> 进入
                      </div>
                  </div>
              </div>
          </div>

      </div>

      <div style="margin-left:calc(100vh - 15%);margin-bottom: 60px;">
        <pagination v-show="queryParams.total > 0" :total="queryParams.total" 
            v-model:page="queryParams.pageNum" 
            v-model:limit="queryParams.pageSize" 
            @pagination="getList"/>
      </div>
      
      <!-- 频道聊天 -->
      <el-dialog v-model="dialogVisible" :title="chatTitle" width="80%" :before-close="handleClose">
        <iframe :src="roleChatUri" class="role-chat-iframe"></iframe>
      </el-dialog>

  </div>

</template>

<script setup>

import {
  allMyChannel , 
} from '@/api/base/im/channel'

const router = useRouter();
const loading = ref(false)

const publicChatChannel = ref([]);
const recChatChannel = ref([]);

const chatTitle = ref("")
const dialogVisible = ref(false)
const roleChatUri = ref("")

const filterRules = ref([
    {"name": "场景", "codeValue": "initializr.admin.project.template.screen","items": [
            {"code": "screen_code_1","name": "旅游预订"},
            {"code": "screen_code_2","name": "在线购物"},
            {"code": "screen_code_3","name": "社交媒体"},
            {"code": "screen_code_4","name": "健身健康"},
            {"code": "screen_code_5","name": "在线视频"}
        ]
    },
    {"name": "类型","codeValue": "initializr.admin.project.template.type","items": [
            {"code": "type_code_1","name": "移动应用"},
            {"code": "type_code_2","name": "网页应用"},
            {"code": "type_code_4","name": "社交平台"},
            {"code": "type_code_5","name": "健身应用"}
        ]
    }
]);

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

const {queryParams} = toRefs(data);

function enterChannel(item){
    router.push({
        path: '/chat',
        query: { 'channel': item.id}
    })
}

/** 查询所所有我在参与的频道 */
function handleAllMyChannel() {
  loading.value = true ; 
  allMyChannel().then(response => {
    let items = response.data;

    publicChatChannel.value = items ; // .filter(item => item.channelType === '9');
    // recChatChannel.value = items.filter(item => item.channelType === '3');

    loading.value = false; 
  })
}

/** 与单个频道发信息 */
function handleChannelChat(item){
    roleChatUri.value = "/channelChat?channel=" + item.id;
    chatTitle.value = item.channelName;
    dialogVisible.value = true ;
}

handleAllMyChannel();

</script>


<style scoped lang="scss">

.gen-template-box {
  width: 100%;
  text-align: center;
  float: left;
  margin-top: 100px;
}

.vc-text.text_l14lqa1e.active {
  color: #fff;
  background: #005bd5;
}

</style>


