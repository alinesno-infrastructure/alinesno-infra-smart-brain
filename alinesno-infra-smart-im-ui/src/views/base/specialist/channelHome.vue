<template>
  <el-scrollbar height="100vh">
    <div class="acp-dashboard" style="max-width:1240px;width:80%;margin:auto;margin-bottom: 80px; padding: 0px 10px !important;background: #fff;">

      <div class="flex justify-space-between mb-4 flex-wrap gap-4" style="padding:10px;">
        <!-- <el-button v-for="button in buttons" icon="Search" :key="button.text" :type="button.type" text bg>{{ button.text }}</el-button> -->
      </div>

      <div class="channel-panel-header">
          推荐频道 
          <span style="font-size: 13px;color: #a5a5a5;">这里包含所有需要运营的Agent频道服务列表，你可以理解成一个Agent就是一个员工</span>
      </div>
      <div class="channel-panel-body" v-loading="loading">
        <el-row>
          <el-col class="channel-item-box" :span="3" v-for="(item,index) in recChatChannel" :key="item">
            <div class="img-bannel">
              <img :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (1 + (index+1) * 1)+ '.png'" />
            </div>
            <div class="channel-info-box">
              <div class="title">
                <!-- <img :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (10 + (index+1) * 1)+ '.png'" style="width: 30px;height: 30px;border-radius: 50%;margin-left: -40px;position: absolute;margin-top: 5px;" /> -->
                {{ truncateString(item.channelName,9) }}
              </div>
              <div class="title-desc">{{ truncateString(item.channelDesc,8) }}</div>
            </div>
          </el-col>
        </el-row> 
      </div>
      <div class="channel-panel-header">
          频道市场 
          <span style="font-size: 13px;color: #a5a5a5;">这里包含所有需要运营的Agent服务列表，你可以理解成一个Agent就是一个员工</span>
      </div>
      <div class="channel-panel-body" v-loading="loading">
        <el-row>
          <el-col class="channel-item-box" :span="3" v-for="(item,index) in publicChatChannel" :key="item">
            <div class="img-bannel">
              <img :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (11 + (index+1) * 1)+ '.png'" />
            </div>
            <div class="channel-info-box">
              <div class="title">
                <!-- <img :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (10 + (index+1) * 1)+ '.png'" style="width: 30px;height: 30px;border-radius: 50%;margin-left: -40px;position: absolute;margin-top: 5px;" /> -->
                {{ truncateString(item.channelName,9) }}
              </div>
              <div class="title-desc">{{ truncateString(item.channelDesc,8) }}</div>
            </div>
          </el-col>
        </el-row> 
      </div>
    </div>
  </el-scrollbar>
</template>

<script setup>

import {
  allMyChannel , 
} from '@/api/base/im/channel'

const loading = ref(false)

const publicChatChannel = ref([]);
const recChatChannel = ref([]);

const buttons = ref([
  { type: '', text: '最新' },
  { type: 'primary', text: '需求讨论' },
  { type: 'success', text: '架构设计' },
  { type: 'success', text: '架构设计' },
  { type: 'warning', text: '市场运维' },
  { type: 'danger', text: '项目规范' },
  { type: 'info', text: '市场推广' },
  { type: 'warning', text: '市场运维' },
  { type: 'danger', text: '项目规范' },
])

/** 查询所所有我在参与的频道 */
function handleAllMyChannel() {
  loading.value = true ; 
  allMyChannel().then(response => {
    let items = response.data;

    publicChatChannel.value = items.filter(item => item.channelType === '9');
    recChatChannel.value = items.filter(item => item.channelType === '3');

    loading.value = false; 
  })
}

handleAllMyChannel();

</script>

<style lang="scss" scoped>

.channel-panel-header{
  padding: 10px;
}

.channel-panel-body{

  .channel-info-box {
    margin: 10px;
    line-height: 1.2rem;
    // padding-left: 30px;
  }

  .channel-item-box{
    margin-bottom: 10px;
  }

  .title {
    font-size: 13px;
    font-weight: normal;
  }

  .title-desc {
    font-size: 13px;
    color: #a5a5a5;
  }

  .img-bannel {
    margin: 10px;
    height: 80px;
    overflow: hidden;
    border-radius: 5px;

    img {
      width:100%;
      border-radius: 5px;
    }
  }
}
</style>