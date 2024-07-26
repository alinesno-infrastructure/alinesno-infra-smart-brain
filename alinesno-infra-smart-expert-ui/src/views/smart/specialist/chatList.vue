<template>
  <el-scrollbar class="scroll-panel" ref="scrollbarRef" loading always wrap-style="padding:10px">

    <div ref="innerRef">
      <div class="robot-chat-ai-say-box" v-for="(item, index) in messageList" :key="index">
        <div class="chat-ai-header" :class="item.roleType == 'person' ? 'say-right-window' : ''">
          <div class="header-images">
            <img src="http://data.linesno.com/icons/sepcialist/dataset_55.png" />
          </div>
        </div>
        <div class="chat-ai-say-body" :class="item.roleType == 'person' ? 'say-right-window' : ''" style="max-width:90%">
          <div class="say-message-info"> {{ item.name }} {{ item.dateTime }}</div>
          <div class="say-message-body markdown-body" v-if="item.readerType === 'html'" v-html="item.chatText"></div>
          <div class="say-message-body markdown-body" v-else v-html="readerHtml(item.chatText)"></div>
        </div>
      </div>
    </div>

  </el-scrollbar>
</template>

<script setup>

import {
  chatMessage
} from '@/api/smart/assistant/robot'

import { getParam } from '@/utils/ruoyi'

import { computed, ref , onMounted, onBeforeMount } from 'vue';
import MarkdownIt from 'markdown-it';
import mdKatex from '@traptitech/markdown-it-katex';
// import mila from 'markdown-it-link-attributes';
import hljs from 'highlight.js';

const messageList = ref([])
const loading = ref(false)

// 滚动条的处理_starter
const innerRef = ref(null);
const scrollbarRef = ref(null);

// 改变组件的中的方法
const pushMessageList = (mess) => {
  console.log('mess = ' + mess);
  console.log('mess = ' + JSON.stringify(mess));

  let chatText = '';
  for (let i = 0; i < mess.length; i++) {
    let content = mess[i];
    if (content.type === 'text') {
      chatText += ('<span class="mention-text">' + (content.text) + '</span>');
    } else if (content.type === 'mention') {
      chatText += ('<span class="mention">' + ('@' + content.username) + '</span>');
    }
  }

  console.log('chatText = ' + chatText);
  messageList.value.push({ roleType: 'person', readerType:'html', name: '软件工程师罗小东', date: '12-10 13:58:21', chatText: chatText });

  initChatBoxScroll();
};

// 推送消息到当前面板
const pushResponseMessageList = (message) => {
  messageList.value.push(message) ; 
  initChatBoxScroll();
}

function initChatBoxScroll() {

  const element = innerRef.value;  // 获取滚动元素
  const scrollHeight = element.scrollHeight;

  // TODO 待处理滚动条没有到底部的问题
  console.log('scrollHeight = ' + scrollHeight);
  scrollbarRef.value.setScrollTop(scrollHeight) ; //scrollHeight);
}

const mdi = new MarkdownIt({
  html: false,
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


function readerHtml(chatText) {
  return mdi.render(chatText);
}

/** 获取到会话信息 */
function handleChatMessage() {

  const businessId = getParam('businessId') == null ? '1733452663532019712' : getParam('businessId');
  console.log('businessId = ' + businessId);

  chatMessage(businessId).then(response => {
    messageList.value = response.data;
    loading.value = false;
    initChatBoxScroll();
  })

}

onMounted(() => {
  handleChatMessage();
})

// 将这个方法暴露出去,这样父组件就可以使用了哈
defineExpose({
  pushMessageList,
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
      background: #fafafa;
      border-radius: 3px;
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