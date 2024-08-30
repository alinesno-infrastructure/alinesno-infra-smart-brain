<template>
  <div class="chatChannel-session-list-box">

    <!-- 创建频道 -->
    <div class="chatChannel-builder">
      <el-button type="primary" text bg icon="Link" @click="addChannel">加入频道</el-button>
      <el-button type="danger" text bg icon="ChatRound" @click="createChannel">创建频道</el-button>
    </div>

    <el-scrollbar style="float:left;height:calc(100vh - 200px)" v-loading="loading">
      <div class="chatChannel-session" 
          @mouseover="showTools(item)"
          @mouseleave="hideTools(item)"
          :class="item.id == currentChatChannel ? 'select-chatChannel' : ''" v-for="item in chatChannel" :key="item">
        <el-row>
          <el-col :span="5" @click="handleSelectChatChannel(item)" >
            <div class="chatChannel-icon">
              <img v-if="item.channelType !== '1'" :src="imagePath(item)"  style="width:40px;height:40px;border-radius: 5px;" />
              <img v-if="item.channelType === '1'" src="http://data.linesno.com/icons/sepcialist/dataset_56.png"  style="width:40px;height:40px;border-radius: 5px;" />
            </div>
          </el-col>
          <el-col :span="19">
            <div class="chatChannel-title" @click="handleSelectChatChannel(item)" >
              #{{ item.channelName }}
            </div>
            <div class="chatChannel-desc">
              {{ item.channelDesc }}
            </div>
            <el-button type="danger" 
                :class="item.showTools && item.channelType !== '1' ?'show-tools':'hide-tools'"  
                link
                style="position: absolute;top: 5px;right: 10px;" 
                icon="Close" 
                size="small"
                @click="handelDeleteChannel(item)">删除</el-button>
          </el-col>
          <span class="channel-type">
              <i v-if="item.channelType === '9'" class="fa-solid fa-users-gear"></i> 
              <i v-if="item.channelType === '2'" class="fa-solid fa-user-ninja"></i> 
              <i v-if="item.channelType === '1'" class="fa-solid fa-user-shield"></i> 
          </span>
        </el-row>
      </div>
    </el-scrollbar>

    <!-- 频道弹窗 -->
    <ChannelGroup ref="createChildComp" />

    <!-- 频道弹窗 -->
    <ChannelGroupDiscover ref="addChildComp" />

  </div>
</template>

<script setup name="Role">

import {
  chatMessage
} from '@/api/base/im/robot'

import {
  allMyChannel , 
  delChannel , 
  removeChannel
} from '@/api/base/im/channel'

import Cookies from 'js-cookie'
import { getParam } from '@/utils/ruoyi'
import ChannelGroup from "./channelGroup";
import ChannelGroupDiscover from "./channelGroupDiscover";

const loading = ref(false)
const router = useRouter();
const createChildComp = ref(null);
const addChildComp = ref(null);

const chatChannel = ref([
  { id: '1', channelName: '公共频道', channelDesc: '这是公共讨论服务频道', icon: '' },
]);
const currentChatChannel = ref('0');

const emit = defineEmits(['onSendParams'])

/** 选择当前频道 */
function handleSelectChatChannel(item) {

  const channelId = item.id ;
  router.push({
      path: '/chat',
      query: { 'channel': channelId }
  })

}

/** 创建频道 */
function createChannel() {
  createChildComp.value.handleOpenChannel(true);
}

// 例如，添加新的频道并提供描述信息：
// addChannel('新频道名称', '这是新频道的描述');
/** 加入频道 */
function addChannel() {
  addChildComp.value.handleOpenChannel(true);
}

/** 删除路径 */
function handelDeleteChannel(item){
  removeChannel(item.id).then(response => {
    handleAllMyChannel();
  })
}

/** 查询所所有我在参与的频道 */
function handleAllMyChannel() {

  const channelId = getParam("channel");
  currentChatChannel.value = channelId ; 

  // loading.value = true ; 
  allMyChannel().then(response => {
    chatChannel.value = response.data;
    loading.value = false; 
  })
}

function showTools(item) {
    chatChannel.value.forEach((i) => {
      i.showTools = i === item; // 只有当前项的 showTools 被设置为 true
    });
}

/** 显示图片 */
function imagePath(row){
  let roleAvatar = '1746435800232665090' ; 
  if(row.icon){
    roleAvatar = row.icon ; 
  }
  return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
}

function hideTools(item) {
  item.showTools = false; // 鼠标移出时隐藏 tools
}

handleAllMyChannel();

/** 监听路由变化 */
watch(() =>  router.currentRoute.value.path,
    (toPath) => {
    //要执行的方法
    // const channelId = router.currentRoute.value.channelId;

    handleAllMyChannel();
      
    },{immediate: true,deep: true}
)
  

</script>

<style lang="scss" scoped>
.chatChannel-builder {
  float: left;
  width: 100%;
  padding: 10px;
  text-align: center;
  margin-bottom: 10px;
}

.chatChannel-session-list-box {

  .chatChannel-session:hover {
    color: #409EFF;
    background: #fafafa;

    .channel-type{
      color: #409EFF !important;
    }
  }

  .select-chatChannel {
    color: #409EFF;
    background: #fafafa;
  }

  .chatChannel-session {
    margin-bottom: 10px;
    border-radius: 5px;
    cursor: pointer;
    float: left;
    width: 100%;
    height: 60px;

    .chatChannel-title {
      font-size: 14px;
      font-weight: bold;
      padding-top: 10px;
      width: calc(100% - 0px);
      float: left;
      line-height: 1rem;
      padding-bottom: 5px;
    }

    .chatChannel-desc {
      font-size: 13px;
      float: left;
      width: calc(100% - 0px);
      color: #a5a5a5;
      line-height: 16px;
      margin-top: 3px;
    }

    .chatChannel-icon {
      // position: absolute;
      margin: 8px;
      text-align: center;
    }

    .channel-type{
      position: absolute;
      bottom: 8px;
      right: 15px;
      color: #a5a5a5;
    }
  }

.show-tools{
  visibility: visible;
}

.hide-tools{
  visibility: hidden;
}
}
</style>