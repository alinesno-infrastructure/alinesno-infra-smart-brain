<template>
  <div>
    <div id="vditor" @contextmenu.prevent="showContextMenu"></div>

    <div v-if="contextMenu.visible" class="context-menu"
      :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }" @click.stop>
      <div class="input-section">
        <input v-model="promptText" type="text" placeholder="告诉 AI 下一步应该如何？比如：帮我翻译成英语" @keyup.enter="handleSendPrompt" @keyup.esc="closeContextMenu" ref="promptInput" />
        <button @click="handleSendPrompt">
          <i class="fa-solid fa-paper-plane"></i>发送
        </button>
      </div>
      <div class="menu-item" @click="addContinuation">
        <i class="fa-solid fa-plus"></i> 继写
      </div>
      <div class="menu-item" @click="improveWriting">
        <i class="fa-solid fa-pen-fancy"></i> 改进写作
      </div>
      <div class="menu-item" @click="simplifyContent">
        <i class="fa-solid fa-scissors"></i> 简化内容
      </div>
      <div class="menu-item" @click="translateContent">
        <i class="fa-solid fa-language"></i> 翻译
      </div>
      <div class="menu-item" @click="summarizeContent">
        <i class="fa-solid fa-file-contract"></i> 总结
      </div>
    </div>

    <!-- AI 输出面板 -->
    <div v-if="showAIPanel" class="ai-output-panel" :style="aiPanelPosition">
      <div class="ai-output-header">
        <span>AI 处理结果</span>
        <el-button class="close-button" type="text" @click="closeAIPanel">
          <i class="fa-solid fa-xmark"></i>
        </el-button>
      </div>
      <div class="ai-output-content" ref="aiOutputContent">
        {{ aiOutput }}
      </div>
      <div class="ai-output-actions">
        <el-button type="primary" :loading="chatStreamLoading" @click="replaceSelectedText">
          <i class="fas fa-exchange-alt"></i> 替换
        </el-button>
        <el-button :loading="chatStreamLoading" @click="insertAfterSelection">
          <i class="fas fa-plus"></i> 插入
        </el-button>
        <el-button @click="copyToClipboard">
          <i class="fas fa-copy"></i> 复制
        </el-button>
      </div>
    </div>

  </div>
</template>

<script setup>
import { onMounted, ref, reactive, nextTick, onUnmounted } from 'vue';
import Vditor from 'vditor';
import 'vditor/dist/index.css';
import { getToken } from "@/utils/auth";

import { ElMessage } from 'element-plus';
import { openSseConnect, handleCloseSse } from "@/api/base/im/chatsse";
import SnowflakeId from "snowflake-id";

import {
  chatEditorRole
} from '@/api/base/im/scene/articleWriting';

// 定义组件可能触发的事件
const emit = defineEmits([
  'update:articleData',  // 用于双向绑定
  'content-change',      // 内容变化事件
  'save'                 // 保存事件
]);

const route = useRoute();
const chatStreamLoading = ref(false);
const snowflake = new SnowflakeId();
const channelStreamId = ref(snowflake.generate());
const articleId = ref(route.query.articleId)
const sceneId = ref(route.query.sceneId)

const contentEditor = ref(null);
const promptInput = ref(null);
const promptText = ref('');
const contextMenu = reactive({
  visible: false,
  x: 0,
  y: 0,
  selectedText: '',
  selectionRange: null
});

const props = defineProps({
  // 文章数据
  articleData: {
    type: Object,
    required: true,
    default: () => ({
      title: '',
      content: ''
    })
  },
  // 是否自动聚焦编辑器
  autoFocus: {
    type: Boolean,
    default: false
  }
});

// Store original selection style
let originalStyles = {};

// AI 输出面板状态
const showAIPanel = ref(false);
const aiReasoningOutput = ref('');
const aiOutput = ref('');
const aiOutputContent = ref(null);
const aiPanelPosition = ref({});
let aiStreamInterval = null;

const defaultMd = `
# 第一章 绪论
## 1.1 课题背景
[这是一段正文内容]
>引用他人的论点论据，用来佐证本文的背景及意义。
>
>——引文出处，适当增加尾注[^1]

[这是一段正文内容]
## 1.2 信息化管理
## 1.3 系统设计目的和内容
[^1]: 绪论尾注 1 的内容。
`;

// 在 script setup 顶部添加 debounce 函数
const debounce = (func, wait) => {
  let timeout;
  return function(...args) {
    clearTimeout(timeout);
    timeout = setTimeout(() => {
      func.apply(this, args);
    }, wait);
  };
};

const highlightSelection = () => {
  // Remove previous highlight if exists
  removeHighlight();

  const selection = window.getSelection();
  if (selection.rangeCount === 0 || selection.toString().trim() === '') return;

  const range = selection.getRangeAt(0);
  const span = document.createElement('span');
  span.style.backgroundColor = '#ffeb3b'; // Yellow highlight
  span.style.borderRadius = '2px';
  span.style.padding = '0 2px';

  try {
    range.surroundContents(span);
    // Store original styles for restoration
    originalStyles = {
      backgroundColor: span.style.backgroundColor,
      borderRadius: span.style.borderRadius,
      padding: span.style.padding
    };
  } catch (e) {
    // In case the selection can't be surrounded (crosses node boundaries)
    console.warn("Couldn't highlight selection:", e);
  }
};

const removeHighlight = () => {
  const highlights = document.querySelectorAll('#vditor span[style*="background-color"]');
  highlights.forEach(span => {
    span.style.backgroundColor = originalStyles.backgroundColor || '';
    span.style.borderRadius = originalStyles.borderRadius || '';
    span.style.padding = originalStyles.padding || '';
    // Remove the span but keep its contents
    const parent = span.parentNode;
    while (span.firstChild) {
      parent.insertBefore(span.firstChild, span);
    }
    parent.removeChild(span);
  });
};

const showContextMenu = (e) => {
  contextMenu.x = e.clientX;
  contextMenu.y = e.clientY;
  contextMenu.visible = true;
  promptText.value = '';

  // Get selected text and range
  const selection = window.getSelection();
  contextMenu.selectedText = selection.toString();
  if (selection.rangeCount > 0) {
    contextMenu.selectionRange = selection.getRangeAt(0);
    highlightSelection();
  }

  nextTick(() => {
    if (promptInput.value) {
      promptInput.value.focus();
    }
  });
};

const closeContextMenu = () => {
  removeHighlight();
  contextMenu.visible = false;
};

const handleKeyDown = (e) => {
  if (e.key === 'Escape') {
    closeContextMenu();
    closeAIPanel();
  }
};

// document.addEventListener('click', closeContextMenu);
document.addEventListener('keydown', handleKeyDown);

const replaceSelectedText = () => {
  if (!contentEditor.value || !contextMenu.selectionRange || !aiOutput.value) return;

  const vditorInstance = contentEditor.value.vditor;
  if (!vditorInstance) return;

  // Remove highlight first
  removeHighlight();

  // Create new range from saved selection
  const range = document.createRange();
  range.setStart(contextMenu.selectionRange.startContainer, contextMenu.selectionRange.startOffset);
  range.setEnd(contextMenu.selectionRange.endContainer, contextMenu.selectionRange.endOffset);

  // Delete selected content
  range.deleteContents();

  // Insert new text
  range.insertNode(document.createTextNode(aiOutput.value));

  closeContextMenu();
  closeAIPanel();

  // Update Vditor state
  // vditorInstance.wysiwyg.popover.hide();
  vditorInstance.wysiwyg.element.normalize();

    // 触发内容变化事件
  triggerContentChange();
};

const insertAfterSelection = () => {
  if (!contentEditor.value || !contextMenu.selectionRange || !aiOutput.value) return;

  const vditorInstance = contentEditor.value.vditor;
  if (!vditorInstance) return;

  // Remove highlight first
  removeHighlight();

  // Create new range at the end of the selection
  const range = document.createRange();
  range.setStart(contextMenu.selectionRange.endContainer, contextMenu.selectionRange.endOffset);
  range.collapse(true); // Collapse to end point

  // Insert new text with a space before if needed
  const textToInsert = aiOutput.value;
  const textNode = document.createTextNode(textToInsert);
  range.insertNode(textNode);

  // Add space after if the inserted text doesn't end with whitespace
  if (!/\s$/.test(textToInsert)) {
    range.setStartAfter(textNode);
    range.insertNode(document.createTextNode(' '));
  }

  // Update Vditor state
  // vditorInstance.wysiwyg.popover.hide();
  vditorInstance.wysiwyg.element.normalize();

  closeContextMenu();
  closeAIPanel();

    // 触发内容变化事件
  triggerContentChange();

  // Move cursor to after the inserted text
  const newRange = document.createRange();
  newRange.setStartAfter(textNode);
  newRange.collapse(true);
  const selection = window.getSelection();
  // selection.removeAllRanges();
  selection.addRange(newRange);

};

// 关闭AI面板
const closeAIPanel = () => {

  showAIPanel.value = false;
};

// 菜单项功能
const improveWriting = () => {
  promptText.value = "请改进文本的写作质量";
  nextTick(() => {
    promptInput.value?.focus();
  });
};
// 继写
const addContinuation = () => {
  promptText.value = "请续写文本";
  nextTick(() => {
    promptInput.value?.focus();
  });
};

const checkSpelling = () => {
  promptText.value = "请检查文本的拼写和语法";
  nextTick(() => {
    promptInput.value?.focus();
  });
};

const simplifyContent = () => {
  promptText.value = "请简化内容";
  nextTick(() => {
    promptInput.value?.focus();
  });
};

const translateContent = () => {
  promptText.value = "请将内容翻译为中文";
  nextTick(() => {
    promptInput.value?.focus();
  });
};

const summarizeContent = () => {
  promptText.value = "请总结内容";
  nextTick(() => {
    promptInput.value?.focus();
  });
};

// 复制到剪贴板
const copyToClipboard = async () => {
  try {
    await navigator.clipboard.writeText(aiOutput.value);
    ElMessage.success('已复制到剪贴板');
  } catch (err) {
    ElMessage.error('复制失败');
  }
};

// 新增方法：触发内容变化事件
const triggerContentChange = () => {
  if (!contentEditor.value) return;
  
  const newContent = contentEditor.value.getValue() ; 
  emit('content-change', newContent);
  emit('update:articleData', {
    ...props.articleData,
    content: newContent
  });
};

// 处理发送prompt
const handleSendPrompt = async () => {
  if (!promptText.value.trim()) return;

  // closeContextMenu();
  contextMenu.visible = false;
  showAIPanel.value = true;
  aiOutput.value = '';
  chatStreamLoading.value = true;

  let formData = {
    articleId: articleId.value,
    sceneId: sceneId.value,
    modifyText: contextMenu.selectedText || '', // Use selected text or empty string 
    articleContent: props.articleData.content,
    channelStreamId: channelStreamId.value,
    message: promptText.value
  }

  chatEditorRole(formData).then(res => {
    proxy.$modal.msgSuccess("发送成功");
  }).catch(error => {
    chatStreamLoading.value = false
  })

  // 设置AI面板位置
  aiPanelPosition.value = {
    left: `${contextMenu.x}px`,
    top: `${contextMenu.y + 5}px`
  };


};

onMounted(() => {

  contentEditor.value = new Vditor('vditor', {
    height: 'calc(100vh - 130px)',
    width: '100%',
    mode: "wysiwyg",
    cdn: '/vditor',
    toolbarConfig: {
      pin: true,
    },
    cache: {
      enable: false,
    },
    upload: {
      accept: 'image/*',
      headers: { Authorization: "Bearer " + getToken() },
      fieldName: 'file',
      multiple: false,
      url: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/scene/articleAttachment/upload",
      linkToImgUrl: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/scene/articleAttachment/display/",
      filename (name) {
        return name.replace(/[^(a-zA-Z0-9\u4e00-\u9fa5\.)]/g, '').
          replace(/[\?\\/:|<>\*\[\]\(\)\$%\{\}@~]/g, '').
          replace('/\\s/g', '')
      },
      //上传图片回显处理
      format(files, resText){
        const res = JSON.parse(resText)
        if (res.code === 200) {
            let filename = res.fileName ; // res.filename ; 
            let filepath = import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + res.data ;
            return '{"msg": "","code": 0,"data": {"errFiles": [],"succMap": {"' + filename + '": "' + filepath + '"}}}';
        } else {
            return '{"msg": "上传失败","code": -1,"data": {"errFiles": [],"succMap": {}}}';
        }
      }
    },
    outline: {
      "enable": true,
      "position": "left"
    },
    input: debounce(() => {
      triggerContentChange();
    }, 300),
    after: () => {

      nextTick(() => {
        contentEditor.value.setValue(props.articleData.content || '');
        if (props.autoFocus) {
          contentEditor.value.focus();
        }
      });
    }
  });

  nextTick(() => {
    handleSseConnect(channelStreamId.value);
  });

});

onUnmounted(() => {
  document.removeEventListener('click', closeContextMenu);
  document.removeEventListener('keydown', handleKeyDown);
});

// 销毁信息
onBeforeUnmount(() => {
  handleCloseSse(channelStreamId.value).then(res => {
    console.log('关闭sse连接成功:' + channelStreamId.value)
  })
});

/** 连接sse */
function handleSseConnect(channelStreamId) {
  nextTick(() => {
    if (channelStreamId) {

      let sseSource = openSseConnect(channelStreamId);
      // 接收到数据
      sseSource.onmessage = function (event) {

        if (!event.data.includes('[DONE]')) {
          let resData = event.data;
          if (resData != 'ping') {  // 非心跳消息
            const data = JSON.parse(resData);
            // pushResponseMessageList(data);
            simulateSSEStream(data)
          }
        } else if (event.data.includes('[DONE]')) {
          console.log('消息接收结束.')
          chatStreamLoading.value = false; // 关闭流式结束
        }

      }
    }
  })
}

// 监听 articleData 变化
watch(() => props.articleData, (newVal) => {
  try{
    if (contentEditor.value && newVal.content !== contentEditor.value.getValue()) {
        contentEditor.value.setValue(newVal.content || '');
    }
  }catch(e){
    console.log(e);
  }
}, { deep: true });

const simulateSSEStream = (response) => {
  aiReasoningOutput.value += response.reasoning;
  aiOutput.value += response.chatText;

  // Use nextTick to ensure DOM is updated
  nextTick(() => {
    const contentEl = aiOutputContent.value;
    if (contentEl) {
      // Use smooth scrolling
      contentEl.scrollTo({
        top: contentEl.scrollHeight,
        behavior: 'smooth'
      });
    }
  });

  // Panel positioning logic
  nextTick(() => {
    const panelEl = document.querySelector('.ai-output-panel');
    if (panelEl) {
      const panelHeight = panelEl.offsetHeight;
      const viewportHeight = window.innerHeight;
      const menuTop = contextMenu.y + 5;

      if (menuTop + panelHeight > viewportHeight) {
        aiPanelPosition.value.top = `${viewportHeight - panelHeight - 5}px`;
      }
    }
  });
};

// 暴露方法给父组件
defineExpose({
  // setData,
  // setPlanItem,
  // setPlanItemContentData,
  focusEditor: () => contentEditor.value?.focus()
});

</script>

<style scoped lang="scss">
.context-menu {

  position: fixed;
  background: white;
  border: 1px solid #e9e9e9;
  box-shadow: 0 0 7px #00000026; // 2px 2px 5px rgba(0, 0, 0, 0.2);
  z-index: 1000;
  min-width: 500px;
  border-radius: 7px;
  padding: 10px;
  font-size: 14px;

  $border-color: #ddd;
  $primary-color: #409eff;
  $hover-color: #66b1ff;
  $menu-hover-bg: #f0f0f0;
  $section-border: #eee;

  .input-section {
    display: flex;
    margin-bottom: 5px;
    padding-bottom: 5px;

    input {
      flex: 1;
      border: 0px solid #ddd;
      margin-right: 5px;
      padding: 10px;
      border-radius: 5px;
      height: 50px;
      background: #f5f5f5;
      line-height: 50px;

      &:focus {
        outline: none;
        border-color: $primary-color;
      }
    }

    button {
      padding: 10px 10px;
      background: $primary-color;
      color: white;
      border: none;
      border-radius: 3px;
      cursor: pointer;
      transition: background-color 0.2s ease;

      &:hover {
        background: $hover-color;
      }

      &:active {
        transform: scale(0.98);
      }
    }
  }

  .menu-item {
    padding: 8px 12px;
    cursor: pointer;
    transition: all 0.2s ease;
    border-radius: 3px;

    &:hover {
      background-color: $menu-hover-bg;
    }

    &:not(:last-child) {
      margin-bottom: 2px;
    }

    // &::before {
    //   content: "->";
    //   color: $primary-color;
    //   margin-right: 8px;
    // }
  }
}

.ai-output-panel {
  position: fixed;
  width: 500px;
  max-height: 60vh;
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 1001;
  display: flex;
  flex-direction: column;

  .ai-output-header {
    padding: 12px 16px;
    border-bottom: 1px solid #eee;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
    background: #f9f9f9;
    border-radius: 8px 8px 0 0;

    .close-button {
      padding: 0;
      min-height: auto;
    }
  }

  .ai-output-content {
    padding: 10px;
    flex: 1;
    overflow-y: auto; // This enables scrolling
    white-space: pre-wrap;
    font-size: 14px;
    line-height: 1.6;
    max-height: 40vh; // Add a max-height to ensure scrolling works
  }

  .ai-output-actions {
    padding: 12px 16px;
    border-top: 1px solid #eee;
    display: flex;
    gap: 10px;
    justify-content: flex-end;
    background: #f9f9f9;
    border-radius: 0 0 8px 8px;
  }
}
</style>
