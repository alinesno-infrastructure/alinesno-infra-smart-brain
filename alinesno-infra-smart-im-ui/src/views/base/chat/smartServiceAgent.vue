<template>
  <div class="agent-single-right-panel robot-chat-help-container">

    <!-- 上方统计信息 -->
    <div class="top-stats">
      <span class="stat-item">👍 13</span>
      <span class="stat-item">💬 9K 使用</span>
      <span class="stat-item">🗣️ 60.3K 对话</span>
    </div>
    
    <div class="robot-chat-help-panel">
      <div class="robot-chat-help-title">频道专家Agent列表</div>
      <div class="robot-chat-help-item-list-panel" v-loading="loading">
        <div class="process-panel">
          <el-scrollbar style="max-height: 400px;">
            <ul>
              <li class="item-process" v-for="(item, index) in favouriteList" :key="index">
                <img style="width:35px;height:35px;border-radius: 50%;" :src="imagePathByPath(item.roleAvatar)" />
                <div class="item-process-content" style="white-space: nowrap;overflow: hidden;text-overflow: ellipsis;margin-top: -2px;">
                  {{ item.roleName }}
                </div>
              </li>
            </ul>
          </el-scrollbar>
          <div style="margin-top:10px;text-align: center;">
            <el-button type="primary" text bg size="large" icon="ChatRound" @click="dialogVisible = true">添加Agent到频道</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 中间内容区域 -->
    <div class="content-area">
      <div class="intro">
        {{ channelInfo.channelDesc }}
      </div>
    </div>

    <el-dialog v-model="dialogVisible" v-if="dialogVisible" title="选择专家服务Agent" width="60%" :before-close="handleClose" append-to-body>

      <!-- 打开角色管理 -->
      <RoleAgent :channelId="currentChannelId" />

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleClose()">关闭</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>

import RoleAgent from './roleAgent'

import {
  getChannelAgent
} from '@/api/base/im/robot'
import { getParam } from '@/utils/ruoyi'
import { ref } from 'vue';

// 定义派发事件
const emit = defineEmits(['mentionUser'])

const channelInfo = ref({
  channelName: '频道名称',
  channelDesc: '频道描述'
})
const sc_height = ref('400px') //默认高度
const router = useRouter();
const loading = ref(false)
const dialogVisible = ref(false)
const favouriteList = ref([])
const currentChannelId= ref([])

/** 获取到基础信息 */
function handleGetChannelAgent(){

  const channelId = getParam("channel");
  currentChannelId.value = channelId ;

  loading.value = true ; 

  getChannelAgent(channelId).then(response => {
    favouriteList.value = response.data ;
    emit('mentionUser' , response.data) ; 
    loading.value = false ; 

    // 如果没有数据，则显示弹窗
    // if(favouriteList.value.length == 0){
    //   setTimeout(() => {
    //     dialogVisible.value = true ;
    //   }, 5000)
    // }

  })
}

// 关闭弹窗
function handleClose() {
  handleGetChannelAgent();
  dialogVisible.value = false;
}

const setChannelInfo = (value) => {
  console.log(value)
  channelInfo.value = value ;
}

/** 显示图片 */
// function imagePath(row){
//   let roleAvatar = '1746435800232665090' ; 
//   if(row.avatar){
//     roleAvatar = row.avatar ; 
//   }
//   return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
// }

/** 提问作者 */
function handleSelectAgentToChat(item){
  emit('mentionUser' , item) ; 
}

handleGetChannelAgent() ;

/** 暴露方法 */
defineExpose({
  setChannelInfo
})

/** 监听路由变化 */
watch(() =>  router.currentRoute.value.path,
    (toPath) => {
    //要执行的方法
    handleGetChannelAgent() ;
      
    },{immediate: true,deep: true}
)
  

</script>