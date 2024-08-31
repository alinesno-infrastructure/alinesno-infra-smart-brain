<template>
  <div class="robot-chat-help-container">

    <div class="robot-chat-help-panel">
      <div class="robot-chat-help-title">频道专家Agent列表</div>
      <div class="robot-chat-help-item-list-panel" v-loading="loading">
        <div class="process-panel">
          <ul>
            <li class="item-process" v-for="(item, index) in favouriteList" :key="index">
              <!-- <img style="width:30px;height:30px;border-radius: 5px;position: absolute;" :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (index + 40) + '.png'" /> -->
              <img style="width:30px;height:30px;border-radius: 5px;position: absolute;" :src="imagePath(item)" />
              <div style="margin-left: 40px;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;margin-top: -2px;color: #2c3e50;">
                {{ item.roleName }}
                <el-button type="primary" style="float:right;position: absolute;right:25px" icon="Link" text bg @click="handleSelectAgentToChat(item)" >选择</el-button>
              </div>
            </li>
            <li class="item-process" style="background-color: #fff;text-align: center;">
              <el-button type="primary" text bg icon="ChatRound" @click="dialogVisible = true">添加Agent到频道</el-button>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" v-if="dialogVisible" title="选择专家服务Agent" width="60%" :before-close="handleClose" append-to-body>

      <!-- 打开角色管理 -->
      <RoleAgent :channelId="currentChannelId" />

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>

import RoleAgent from './agent/roleAgent'

import {
  getChannelAgent
} from '@/api/base/im/robot'
import { getParam } from '@/utils/ruoyi'

// 定义派发事件
const emit = defineEmits(['mentionUser'])

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
    loading.value = false ; 
  })
}

/** 显示图片 */
function imagePath(row){
  let roleAvatar = '1746435800232665090' ; 
  if(row.avatar){
    roleAvatar = row.avatar ; 
  }
  return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
}

/** 提问作者 */
function handleSelectAgentToChat(item){
  emit('mentionUser' , item) ; 
}

handleGetChannelAgent() ;

/** 监听路由变化 */
watch(() =>  router.currentRoute.value.path,
    (toPath) => {
    //要执行的方法
    handleGetChannelAgent() ;
      
    },{immediate: true,deep: true}
)
  

</script>