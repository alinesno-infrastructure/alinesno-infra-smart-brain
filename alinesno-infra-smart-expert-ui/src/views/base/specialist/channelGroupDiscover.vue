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
              <!-- <img class="channel-image" :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (index + 5) + '.png'" /> -->
              <img class="channel-image" :src="imagePath(item)" />
              <div class="channel-text">

                <span class="channel-type">
                    <i v-if="item.channelType === '9'" class="fa-solid fa-users-gear"></i> 
                    <i v-if="item.channelType === '2'" class="fa-solid fa-user-ninja"></i> 
                    <i v-if="item.channelType === '1'" class="fa-solid fa-user-shield"></i> 
                </span>

                #{{ item.channelName }}

              </div>
              <div class="channel-desc">
                {{ item.channelDesc }}
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
        <!-- <el-button type="primary">加入频道</el-button> -->
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
const chatChannelTemplate = ref([
  { id: '1', channelName: '公共频道', channelDesc: '这是公共讨论服务频道', icon: '' },
  { id: '2', channelName: '数据库设计频道', channelDesc: '关于数据库设计的讨论', icon: '' },
  { id: '3', channelName: '代码模块生成频道', channelDesc: '讨论代码模块生成相关话题', icon: '' },
  { id: '4', channelName: '数据分析频道', channelDesc: '数据分析技术和方法的讨论', icon: '' },
  { id: '5', channelName: '考核题目设计频道', channelDesc: '考核题目设计相关讨论', icon: '' },
  { id: '6', channelName: '文档生成频道', channelDesc: '讨论文档生成工具和最佳实践', icon: '' },
  { id: '7', channelName: '技术架构设计频道', channelDesc: '关于技术架构设计的讨论', icon: '' },
  { id: '8', channelName: '解决方案讨论频道', channelDesc: '讨论解决问题的不同方案', icon: '' },
  { id: '9', channelName: '压力测试频道', channelDesc: '关于系统压力测试的话题', icon: '' },
  { id: '10', channelName: '前端开发讨论频道', channelDesc: '关于前端开发的讨论', icon: '' },
  { id: '11', channelName: '后端开发讨论频道', channelDesc: '关于后端开发的讨论', icon: '' },
  { id: '12', channelName: '人工智能与机器学习频道', channelDesc: '人工智能与机器学习技术讨论', icon: '' },
  { id: '13', channelName: '物联网技术频道', channelDesc: '讨论物联网技术和发展', icon: '' },
  { id: '14', channelName: '移动应用开发频道', channelDesc: '关于移动应用开发的话题', icon: '' },
  { id: '15', channelName: '网络安全讨论频道', channelDesc: '关于网络安全的讨论和建议', icon: '' },
  { id: '16', channelName: '云计算与服务频道', channelDesc: '讨论云计算和相关服务', icon: '' },
  { id: '17', channelName: '产品管理讨论频道', channelDesc: '产品管理与策划的话题', icon: '' },
  { id: '18', channelName: '用户体验设计频道', channelDesc: '关于用户体验设计的讨论', icon: '' },
  { id: '19', channelName: '区块链技术讨论频道', channelDesc: '讨论区块链技术和应用', icon: '' },
  { id: '20', channelName: '软件测试与质量控制频道', channelDesc: '软件测试与质量控制的讨论', icon: '' },
  { id: '21', channelName: '项目管理讨论频道', channelDesc: '项目管理方法和实践的讨论', icon: '' },
  { id: '22', channelName: '大数据技术讨论频道', channelDesc: '大数据技术和应用的讨论', icon: '' },
  { id: '23', channelName: '编程语言讨论频道', channelDesc: '各种编程语言的讨论和比较', icon: '' },
  { id: '24', channelName: '自然语言处理频道', channelDesc: '自然语言处理技术讨论', icon: '' },
  { id: '25', channelName: '虚拟现实与增强现实频道', channelDesc: '虚拟现实与增强现实技术讨论', icon: '' },
  { id: '26', channelName: '电子商务技术频道', channelDesc: '电子商务技术和趋势讨论', icon: '' },
  { id: '27', channelName: '微服务架构频道', channelDesc: '微服务架构设计与实践讨论', icon: '' },
  { id: '28', channelName: '人机交互设计频道', channelDesc: '人机交互设计的话题讨论', icon: '' },
  { id: '29', channelName: '敏捷开发方法论频道', channelDesc: '敏捷开发方法论的讨论', icon: '' }
]);

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
  // window.location.reload();
}

function handleOpenChannel(val){
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
    margin-left: 10px;
    font-size: 13px;
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