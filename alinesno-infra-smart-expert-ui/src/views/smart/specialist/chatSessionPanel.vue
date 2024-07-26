<template>
  <div class="chatChannel-session-list-box">

    <!-- 创建频道 -->
    <div class="chatChannel-builder">
      <el-button type="primary" text bg icon="Link">加入频道</el-button>
      <el-button type="danger" text bg icon="ChatRound">创建频道</el-button>
    </div>

    <div class="chatChannel-session" :class="index == currentChatChannel ? 'select-chatChannel':''" @click="handleSelectChatChannel(item)" v-for="(item , index) in chatChannel" :key="item">
      <div class="chatChannel-icon">
        <img :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (index+15)+ '.png'" 
          style="width:40px;height:40px;border-radius: 50%;" />
      </div>
      <div class="chatChannel-title">
        #{{ item.name }}
      </div>
      <div class="chatChannel-desc">
        {{ item.desc }}
      </div>
    </div>
  </div>
</template>

<script setup name="Role">

import {
  chatMessage
} from '@/api/smart/assistant/robot'

import { getParam } from '@/utils/ruoyi'

const chatChannel = ref([
  { id: '1', name: '公共频道', desc: '这是公共讨论服务频道', icon: '' },
  { id: '2', name: '数据库设计频道', desc: '关于数据库设计的讨论', icon: '' },
  { id: '3', name: '代码模块生成频道', desc: '讨论代码模块生成相关话题', icon: '' },
  { id: '4', name: '数据分析频道', desc: '数据分析技术和方法的讨论', icon: '' },
  { id: '5', name: '考核题目设计频道', desc: '考核题目设计相关讨论', icon: '' },
  { id: '6', name: '文档生成频道', desc: '讨论文档生成工具和最佳实践', icon: '' },
  { id: '7', name: '技术架构设计频道', desc: '关于技术架构设计的讨论', icon: '' },
  { id: '8', name: '解决方案讨论频道', desc: '讨论解决问题的不同方案', icon: '' },
  { id: '9', name: '压力测试频道', desc: '关于系统压力测试的话题', icon: '' },
]);
const currentChatChannel = ref('0') ; 

// 若要动态添加频道并使其 ID 递增，你可以使用类似下面的方法：
let nextId = chatChannel.value.length + 1;

function addChannel(name, desc) {
  chatChannel.value.push({ id: String(nextId), name: name, desc: desc, icon: '' });
  nextId++;
}

const emit = defineEmits(['onSendParams'])

/** 选择当前频道 */
function handleSelectChatChannel(item){
  currentChatChannel.value = item.id - 1 ;
  let businessId = getParam('businessId') ;
  console.log('businessId = ' + businessId) ;

  emit('onSendParams', true)

  chatMessage(businessId).then(response => {
    // messageList.value = response.data ; 
    emit('onSendParams', false)
  })
}


// 例如，添加新的频道并提供描述信息：
// addChannel('新频道名称', '这是新频道的描述');

// 检查更新后的 chatChannel
// console.log(chatChannel.value);

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
  }
  .select-chatChannel{
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
      margin-left: 60px;
      padding-top: 10px;
      float: left;
      line-height: 1rem;
      padding-bottom: 5px;
    }

    .chatChannel-desc {
      font-size: 13px;
      margin-left: 60px;
      float: left;
      color: #a5a5a5;
      line-height: 16px;
    }

    .chatChannel-icon {
      position: absolute;
      margin: 8px;
    }
  }
}
</style>