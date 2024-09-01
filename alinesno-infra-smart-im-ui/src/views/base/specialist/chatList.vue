<template>
  <el-scrollbar class="scroll-panel" ref="scrollbarRef" loading always wrap-style="padding:10px">

    <div ref="innerRef">

      <div class="robot-chat-ai-say-box" v-for="(item, index) in messageList" 
          @mouseover="showTools(item)"
          @mouseleave="hideTools(item)"
          :key="index">

        <div class="chat-ai-header" :class="item.roleType == 'person' ? 'say-right-window' : ''">
          <div class="header-images">
            <!-- <img :src="item.icon" /> -->
            <img :src="imagePath(item)" />
          </div>
        </div>

        <div class="chat-ai-say-body" :class="item.roleType == 'person' ? 'say-right-window' : ''" style="max-width:calc(100% - 135px)">
          <div class="say-message-info" v-if="item.roleType == 'person'"> 
            <span style="margin-left:10px" :class="item.showTools?'show-tools':'hide-tools'"> {{ item.dateTime }}</span> 
            {{ item.name }} 
          </div>
          <div class="say-message-info" v-else> 

            <!-- <div v-if="item.roleType != 'person'" style="float:left;text-align: right;">
              <el-button v-if="index%2==0" type="primary" link loading size="default"></el-button>
              <el-button v-if="index%2==1" type="primary" link size="default"><el-icon><CircleCheck /></el-icon></el-button>
            </div> -->

            {{ item.name }}  
            <span style="margin-left:10px" :class="item.showTools?'show-tools':'hide-tools'"> {{ item.dateTime }} </span>
          </div>

          <div class="say-message-body markdown-body" v-html="readerHtml(item.chatText)"></div>


          <div class="chat-ai-say-tools" style="margin-top: 3px;;text-align: right;float:right" :class="item.showTools?'show-tools':'hide-tools'">
              <el-button type="danger" link icon="Promotion" size="small" @click="handleBusinessIdToMessageBox(item)">选择</el-button>
              <el-button type="primary" link icon="EditPen" size="small" @click="handleEditGenContent(item)">查看</el-button>
              <!-- <el-button type="primary" link icon="Refresh" size="small" @click="handleRecyleGenContent(item)">审批</el-button> -->
          </div>

        </div>
      </div>

    </div>

  </el-scrollbar>
</template>

<script setup>

import { getParam } from '@/utils/ruoyi'

import { nextTick } from 'vue'

import { computed, ref , onMounted,  defineEmits} from 'vue';
import MarkdownIt from 'markdown-it';
import mdKatex from '@traptitech/markdown-it-katex';
import hljs from 'highlight.js';

const loading = ref(false)

// 滚动条的处理_starter
const innerRef = ref(null);
const scrollbarRef = ref(null);
const messageList = ref([]);

// 定义派发事件
const emit = defineEmits(['sendMessageToChatBox' , 'handleEditorContent'])

// 改变组件的中的方法
const pushMessageList = (mess) => {

  let chatText = '';
  for (let i = 0; i < mess.length; i++) {
    let content = mess[i];
    if (content.type === 'text') {
      chatText += ('<span class="mention-text">' + (content.text) + '</span>');
    } else if (content.type === 'mention') {
      chatText += ('<span class="mention">' + ('@' + content.username) + '</span>');
    } else if (content.type === 'business') {
      chatText += ('<span class="mention-business">' + ('#' + content.businessId) + '</span>');
    }
  }

  // TODO 从本地store中获取到当前用户信息
  messageList.value.push({ 
    roleType: 'person', 
    businessId: '1733452663532019712' ,  
    dateTime: '2023-12-11 16:32:10' ,  
    readerType:'html', 
    icon:'1746465675916124161' , 
    name: '软件工程师罗小东', 
    date: '12-10 13:58:21', 
    chatText: chatText });

  initChatBoxScroll();
};

// 推送消息到当前面板
const currentResponseMessageList = (message) => {
  messageList.value = message ; 

  initChatBoxScroll();
}

// 推送消息到当前面板
const pushResponseMessageList = (message) => {
  messageList.value.push(message) ; 
  initChatBoxScroll();
}

function initChatBoxScroll() {

  nextTick(() => {
    const element = innerRef.value;  // 获取滚动元素
    const scrollHeight = element.scrollHeight;

    scrollbarRef.value.setScrollTop(scrollHeight) ; 
  })

}

const mdi = new MarkdownIt({
  html: true,
  linkify: true,
  highlight(code, language) {
    const validLang = !!(language && hljs.getLanguage(language));
    if (validLang) {
      const lang = language || '';
      return highlightBlock(hljs.highlight(code, { language: lang }).value, lang);
    }
    return highlightBlock(hljs.highlightAuto(code).value, '');
  },
});

// mdi.use(mila, { attrs: { target: '_blank', rel: 'noopener' } });
mdi.use(mdKatex, { blockClass: 'katexmath-block rounded-md p-[10px]', errorColor: ' #cc0000' });

function highlightBlock(str, lang) {
  return `<pre class="code-block-wrapper"><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
}

/** 显示图片 */
function imagePath(row){
  let roleAvatar = '1746435800232665090' ; 
  if(row.icon){
    roleAvatar = row.icon ; 
  }
  return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
}

function readerHtml(chatText) {
  return mdi.render(chatText);
}

/** 发送业务代码到消息框中 */
function handleBusinessIdToMessageBox(item){
  const businessIdMessage = '#' + item.businessId + ' ' ;
  emit('sendMessageToChatBox' , businessIdMessage) ; 
}

/** 编辑生成内容和更新生成内容 */
function handleEditGenContent(item){
  const businessId = item.businessId + '' ;
  console.log('businessId = ' + businessId) ;
  emit('handleEditorContent' , businessId) ; 
}

/** 重新生成内容 */
const handleRecyleGenContent = (item) => {
  console.log('handleRecyleGenContent item = ' + item) ;

  const businessIdMessage = '#' + item.businessId + ' ' ;
  emit('sendMessageToChatBox' , businessIdMessage) ; 
}

function showTools(item) {
    messageList.value.forEach((i) => {
      i.showTools = i === item; // 只有当前项的 showTools 被设置为 true
    });
}

function hideTools(item) {
  item.showTools = false; // 鼠标移出时隐藏 tools
}

// onMounted(() => {
//   handleChatMessage();
// })

// 将这个方法暴露出去,这样父组件就可以使用了哈
defineExpose({
  pushMessageList,
  currentResponseMessageList,
  pushResponseMessageList
});

</script>

<style lang="scss" scoped>
.scroll-panel {
  padding-bottom: 10px;
  float: left;
  width: 100%;
  height: calc(100% - 55px);
  overflow: hidden;
}

.show-tools{
  visibility: visible;
}

.hide-tools{
  visibility: hidden;
}

.robot-chat-ai-say-box {
  float: left;
  width: 100%;

  .say-right-window {
    float: right !important;

    .say-message-info {
      text-align: right !important;
    }
  }

  .chat-ai-header {
    float: left;
    width: 50px;
    margin: 10px;

    .header-images {
      padding: 5px;

      img {
        width: 100%;
        border-radius: 50%;
      }

    }

  }

  .chat-ai-say-body {
    float: left;
    margin-top: 15px;
    font-size: 14px;

    .say-message-info {
      font-size: 13px;
      margin-bottom: 5px;
      color: #999;
    }

    .say-message-body {
      padding: 10px;
      line-height: 1.4rem;
      border-radius: 3px;
      background: #fafafa;
    }

  }

  .message-list {
    margin-top: 20px;
  }

  .message {
    margin-bottom: 8px;
  }

}

</style>