<template>
  <el-scrollbar height="100vh">
    <div class="acp-dashboard" style="max-width:1240px;width:80%;margin:auto;margin-bottom: 80px; padding: 0px 10px !important;background: #fff;">

      <div class="flex justify-space-between mb-4 flex-wrap gap-4" style="padding:10px;">
        <el-button v-for="button in buttons" icon="Search" :key="button.text" :type="button.type" text bg>{{ button.text }}</el-button>
      </div>

      <div class="channel-panel-header">
          自动化Agent频道列表
          <span style="font-size: 13px;color: #a5a5a5;">这里包含所有需要运营的Agent频道服务列表，你可以理解成一个Agent就是一个员工</span>
      </div>
      <div class="channel-panel-body" v-loading="loading">
        <el-row>
          <el-col class="channel-item-box" :span="4" v-for="(item,index) in chatChannel" :key="item">
            <div class="img-bannel">
              <img :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (1 + (index+1) * 1)+ '.png'" />
            </div>
            <div class="channel-info-box">
              <img :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (10 + (index+1) * 1)+ '.png'" style="width: 30px;height: 30px;border-radius: 50%;margin-left: -40px;position: absolute;margin-top: 5px;" />
              <div class="title">{{ item.channelName }}</div>
              <div class="title-desc">{{ item.channelDesc }}</div>
            </div>
          </el-col>
        </el-row> 
      </div>
      <div class="channel-panel-header">
          自动化Agent列表
          <span style="font-size: 13px;color: #a5a5a5;">这里包含所有需要运营的Agent服务列表，你可以理解成一个Agent就是一个员工</span>
      </div>
      <div class="channel-panel-body" v-loading="loading">
        <el-row>
          <el-col class="channel-item-box" :span="4" v-for="(item,index) in chatChannel" :key="item">
            <div class="img-bannel">
              <img :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (11 + (index+1) * 1)+ '.png'" />
            </div>
            <div class="channel-info-box">
              <img :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (10 + (index+1) * 1)+ '.png'" style="width: 30px;height: 30px;border-radius: 50%;margin-left: -40px;position: absolute;margin-top: 5px;" />
              <div class="title">{{ item.channelName }}</div>
              <div class="title-desc">{{ item.channelDesc }}</div>
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
const chatChannel = ref([
  { id: '1', channelName: '公共频道', channelDesc: '这是公共讨论服务频道', icon: '' },
]);
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
    chatChannel.value = response.data;
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
    line-height: 1.5rem;
    padding-left: 45px;
  }

  .channel-item-box{
    margin-bottom: 10px;
  }

  .title {
    font-size: 15px;
    font-weight: bold;
  }

  .title-desc {
    font-size: 13px;
    color: #a5a5a5;
  }

  .img-bannel {
    margin: 10px;
    height: 130px;
    overflow: hidden;
    border-radius: 5px;

    img {
      width:100%;
      border-radius: 5px;
    }
  }
}
</style>