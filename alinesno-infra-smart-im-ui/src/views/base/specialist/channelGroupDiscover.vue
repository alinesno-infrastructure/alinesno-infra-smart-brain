<template>

  <el-dialog v-model="centerDialogVisible" title="加入频道" width="70%" align-center>

    <div class="channel-box-panel">

      <div style="font-size: 13px;margin-bottom: 10px;width: 100%;">
        社区频道列表，参与其它人更有意思的角色定义和频道学习
      </div>
      
      <el-scrollbar height="500px">
        <ul style="margin: 0;padding: 0px;list-style: none;">
          <li v-for="(item, index) in chatChannelTemplate" :key="index" class="channel-item-li">
            <div class="channel-item">
              <img class="channel-image" :src="imagePath(item)" />
              <div class="channel-text">
                <span class="channel-type">
                    <i v-if="item.channelType === '9'" class="fa-solid fa-users-gear"></i> 
                    <i v-if="item.channelType === '2'" class="fa-solid fa-user-ninja"></i> 
                    <i v-if="item.channelType === '1'" class="fa-solid fa-user-shield"></i> 
                </span>

                #{{ item.channelName }}
                <div class="channel-desc">
                  {{ truncateString(item.channelDesc,25) }}
                </div> 
              </div>
              <el-button type="primary" @click="handleSetChannelId(item.id)" style="position: absolute;right: 10px;" text bg>
                <i class="fa-solid fa-user-tag icon-btn"></i> 加入 
              </el-button>
            </div>
          </li>
        </ul>
      </el-scrollbar>
    </div>

    <template #footer>
      <span class="dialog-footer">
      </span>
    </template>

  </el-dialog>

</template>

<script setup>

let { proxy } = getCurrentInstance();

import {
  allPublicChannel , 
  joinChannel
} from '@/api/base/im/channel'

import { onMounted } from 'vue';
import { getParam } from '@/utils/ruoyi'

const router = useRouter();
const centerDialogVisible = ref(false)
const chatChannelTemplate = ref([]);

/** 设置ChannelId */
function handleSetChannelId(channelId){
  console.log('handleSetChannelId channelId = ' + channelId) ;

  joinChannel(channelId).then(response => {
    router.push({
        path: '/chat',
        query: { 'channel': channelId }
    })
  })

  centerDialogVisible.value = false ;
}

function handleOpenChannel(val){
    handleAllPublicChannel();
    centerDialogVisible.value = val;
}

function handleAllPublicChannel(){
  allPublicChannel().then(response => {
    chatChannelTemplate.value = response.data ;
  })
}

/** 显示图片 */
function imagePath(row){
  let roleAvatar = '1746435800232665090' ; 
  if(row.icon){
    roleAvatar = row.icon ; 
  }
  return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
}


handleAllPublicChannel();

defineExpose({
  handleOpenChannel,
})

</script>

<style lang="scss" scoped>
.channel-box-panel {

  .channel-item {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    padding: 10px;
    position: relative;
    background: #fafafa;
    border-radius: 5px;
  }

  .channel-item-li{
    width: calc(50% - 20px);
    float: left;
    margin-right: 10px;
    margin-left: 10px;
  }

  .channel-image {
    width: 35px;
    height: 35px;
    border-radius: 5px;
    margin-right: 10px;
  }

  .channel-text {
    font-size: 14px;
    font-weight: bold;
    color: #333 ;
  }

  .channel-desc {
    font-size: 12px;
    font-weight: normal;
    color: #a5a5a5;
    line-height: 1rem;
  }

  .icon-btn{
    margin-right: 10px;
  }

  .channel-type i{
    font-size: 16px;
    color: #409EFF;
  }

}

</style>