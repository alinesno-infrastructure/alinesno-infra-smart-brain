<template>
  <div class="task-trace-panel">
    <div class="title" style="padding-bottom:10px;">
      深度搜索运行的工作空间
    </div>

    <!-- 执行步骤显示 -->
    <div class="follow-step-panel" v-if="!displayContentVisible">

      <div class="step-title">
        <i :class="currentStepAction?.icon"></i> &nbsp; 正在使用
        <span class="trace-tag">{{ currentStepAction?.actionName || '任务未运行' }}</span>
      </div>

      <el-scrollbar class="step-body-panel" ref="scrollbarRef">

      <div v-if="!currentStepAction?.actionName" style="margin-top: 10vh">
          <el-empty description="当前还没有执行步骤，任务状态显示为空，还未没有执行步骤" />
      </div>

      <div class="step-body" ref="innerRef">
          <div class="say-message-body markdown-body think-content" v-if="currentStepAction?.think" v-html="readerHtml(currentStepAction.think)"></div>
          <div class="say-message-body markdown-body output-content" v-if="currentStepAction?.result" v-html="readerHtml(currentStepAction.result)"></div> 
      </div>

      </el-scrollbar>
    </div>

    <!-- 内容显示 -->
    <div class="follow-step-panel display-output-content" v-if="displayContentVisible">
      <div class="step-title" style="justify-content: space-between;">
        <span>
          <i class="fa-brands fa-html5"></i>&nbsp;文件内容预览({{ displayContentType }})
        </span>
        <span style="margin-right: 20px;display: flex;gap: 10px;flex-direction: row;">
          <el-button type="text" @click="hadnleOpenLink()" v-if="displayContentType === 'html'" >
            <i class="fa-solid fa-paper-plane"></i>
          </el-button>
          <el-button type="text" @click="handleCopyContent()">
            <i class="fa-solid fa-clone"></i>
          </el-button>
          <el-button type="text" @click="handleDownloadContent()">
            <i class="fa-solid fa-file-arrow-down"></i>
          </el-button>
        </span>
      </div>

       <div v-loading="iframeLoading" element-loading-text="页面加载中 ..." :element-loading-spinner="svg" element-loading-svg-view-box="-10, -10, 50, 50">
        <iframe :src="displayContentHtmlUrl" v-if="displayContentType === 'html'" />
       </div>
      <ChapterEditor v-model:articleData="articleData" ref="chapterEditorRef" v-if="displayContentType === 'md'" />

    </div>

    <div class="follow-btn-panel">
      <el-row style="width:100%;align-items: center;">
        <el-col :span="4">
            <div class="step-btn-panel">
              <el-button type="primary" bg text size="large" @click="previewStepAction">
                <el-icon><ArrowLeftBold /></el-icon>
              </el-button>
              <el-button type="primary" bg text size="large" @click="nextStepAction">
                <el-icon><ArrowRightBold /></el-icon>
              </el-button>
            </div>
        </el-col>
        <el-col :span="20">
            <div class="step-progress-panel">
              <el-progress :percentage="percentage" />
            </div>
        </el-col>
      </el-row>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Close } from '@element-plus/icons-vue';
import { ElLoading, ElMessage } from 'element-plus'

import ChapterEditor from './chapterEditor'
import MarkdownIt from 'markdown-it';
import mdKatex from '@traptitech/markdown-it-katex';
import hljs from 'highlight.js';

import { 
  getOutputPreviewUrl ,
  getOutputMarkdownContent
} from '@/api/base/im/scene/deepSearch';

const innerRef = ref(null); // 滚动条的处理_starter
const scrollbarRef = ref(null);

const displayContentVisible = ref(false)
const displayContentType = ref('html')
const displayContentHtmlUrl = ref('http://data.linesno.com/index3.html')
const displayContentMd = ref('')
let abortController = null;

const chapterEditorRef = ref(null)
const articleData = ref({
    content: "" , 
})

const percentage = ref(0)
const iframeLoading = ref(false);

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

mdi.use(mdKatex, { blockClass: 'katexmath-block rounded-md p-[10px]', errorColor: ' #cc0000' });

function highlightBlock(str, lang) {
  return `<pre class="code-block-wrapper"><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
}


/** 读取html文本 */
function readerHtml(chatText) {
  if (chatText) {
    return mdi.render(chatText);
  }
}

// 深度搜索流程数据
const deepsearchFlow = ref(null);

// 步骤操作列表
const stepActionList = ref([]);

// 当前执行的任务列表
const currentStepAction= ref([]);

const currentStepActionFilter = () => {
    // 查询出status为doing的actionList 
    currentStepAction.value =  stepActionList.value.find((item) => item.status === 'doing');
    initChatBoxScroll();
}

// 处理deepsearchFlow数据并更新stepActionList
const processDeepsearchFlow = (flowData) => {

    const planActins = flowData.plan?.actions;

    if(planActins){
        for (const action of planActins) {
            const existingIndex = stepActionList.value.findIndex(item => item.actionId === action.actionId);

            if(existingIndex !== -1){
                stepActionList.value[existingIndex].status = action.status;
                stepActionList.value[existingIndex].think += action.think;
                stepActionList.value[existingIndex].result += action.result;
            }else {
                stepActionList.value.push(action);
            }
        }
    }

    const steps = flowData.steps;
    if(steps){
        steps.forEach((step, stepIndex) => {
            const stepActions = step.actions;  

            if (stepActions) {
                stepActions.forEach((action, actionIndex) => {
                    // 先查询stepActionList.value是否存在该action
                    const existingIndex = stepActionList.value.findIndex(item => item.actionId === action.actionId);

                    if (existingIndex !== -1) {
                        // 如果存在，则更新该action
                        stepActionList.value[existingIndex].status = action.status;
                        stepActionList.value[existingIndex].think += action.think;
                        stepActionList.value[existingIndex].result += action.result;
                    } else {
                        // 如果不存在，则添加该action
                        stepActionList.value.push(action);
                    }

                });
            }
        });
    }

    const outputActions = flowData.output?.actions;
    if (outputActions) {
      for (const action of outputActions) {
            const existingIndex = stepActionList.value.findIndex(item => item.actionId === action.actionId);

            if(existingIndex !== -1){
                stepActionList.value[existingIndex].status = action.status;
                stepActionList.value[existingIndex].think += action.think;
                stepActionList.value[existingIndex].result += action.result;
            }else {
                stepActionList.value.push(action);
            }

        }
    }

    // 附件
    const attachments = flowData.output?.attachments;
    if(attachments){
        if (attachments && attachments.length > 0) {
            const htmlAttachment = attachments.find(attachment => attachment.type === 'html');

            if (htmlAttachment) {
                displayContentType.value = 'html';
                displayContentHtmlUrl.value = getOutputPreviewUrl(htmlAttachment.storageId);
            }
        }
    }

    // 判断是否所有的actions状态都为done
    if (stepActionList.value.every(action => action.status === 'done')) {
        percentage.value = 100;
    } else {
        percentage.value = Math.round((stepActionList.value.filter(action => action.status === 'done').length / stepActionList.value.length) * 100);
    }

    currentStepActionFilter();
};

const handleClose = () => {
  // 关闭操作的逻辑，这里暂时为空
};


function initChatBoxScroll() {
  nextTick(() => {

    if (!innerRef.value) return;

    const element = innerRef.value;  // 获取滚动元素
    const scrollHeight = element.scrollHeight;

    if (scrollbarRef.value) {
      scrollbarRef.value.setScrollTop(scrollHeight);
    }
  })
}

// 更新deepsearchFlow并处理数据
const pushDeepsearchFlowTracePanel = (deepsearchFlowVal) => {
    deepsearchFlow.value = deepsearchFlowVal;
    processDeepsearchFlow(deepsearchFlow.value);
};

const hadnleOpenLink = () => {
  window.open(displayContentHtmlUrl.value);
};


// 显示内容
const handleDisplayContent = (item) => {
  console.log('item = ' + JSON.stringify(item));

  displayContentVisible.value = true ;
  displayContentType.value = item.type ;

  // 2. 终止上一次未完成的请求（关键：避免重复请求）
    if (abortController) {
      abortController.abort();
      abortController = null;
    }

  if(item.type === 'html'){

    ElMessage.info("正在获取生成内容.")

    // 3. 创建新的取消控制器
    abortController = new AbortController();

    getOutputPreviewUrl(item.storageId , { signal: abortController.signal }).then(res => {
      displayContentHtmlUrl.value = res.data ;
      abortController = null; // 请求成功后重置
    }).catch(err => {
        // 忽略取消请求的错误
        if (err.name !== 'AbortError') {
          ElMessage.error("获取HTML内容失败");
        }
        abortController = null;
    });

  }else if(item.type === 'md'){

    ElMessage.info("正在获取生成内容.")
    // 3. 创建新的取消控制器
    abortController = new AbortController();

    getOutputMarkdownContent(item.storageId , { signal: abortController.signal }).then(res => {
      displayContentMd.value = res.data ; 
      articleData.value.content = res.data ;
    }).catch(err => {
      if (err.name !== 'AbortError') {
        ElMessage.error("获取MD内容失败");
      }
      abortController = null;
    });
  }else{
    ElMessage.warning("其它格式暂时不支持显示，可直接下载.")
  }
}

// 组件卸载时终止未完成请求（可选，避免内存泄漏）
onUnmounted(() => {
  if (abortController) {
    abortController.abort();
  }
});

defineExpose({
  pushDeepsearchFlowTracePanel,
  handleDisplayContent
});
</script>

<style lang="scss" scoped>
/* 样式部分保持不变 */
.task-trace-panel {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--line-color-border-2, #eaedf1);
  padding: 10px;
  margin-top:20px;
  height: calc(100vh - 80px);
  
  .title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #222;
    border-bottom: 1px solid var(--line-color-border-2, #eaedf1);
    margin-bottom: 10px;
    font-size: 18px;
    font-weight: bold;
  }
  
  .follow-step-panel {

    display: flex;
    flex-direction: column;

    .step-title {
      font-size: 14px;
      margin-bottom: 15px;
      display: flex;
      color: #555;
      align-items: center;
    }
    
    .trace-tag {
      margin: 0 4px;
      display: flex;
      padding: 3px 4px;
      align-items: center;
      gap: 4px;
      border-radius: 4px;
      background: #f1f3f5;
      color: #42464e;
      font-size: 14px;
      font-style: normal;
      font-weight: 600;
    }
    
    .action-item {
      margin-bottom: 10px;
      border: 1px solid #eaedf1;
      border-radius: 4px;
      padding: 10px;
      
      &.active {
        border-color: #42b983;
      }
      
      .action-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 5px;
        
        .action-status {
          color: #999;
        }
      }
      
      .action-content {
        .action-section {
          margin-bottom: 5px;
          
          .section-title {
            font-weight: bold;
            margin-right: 5px;
          }
        }
      }
    }
  }
}

.step-body {
  margin-top: 10px;
  padding-right: 20px;
  padding-left: 10px;



  .think-content {
    color: #aaa;
    border-left: 2px solid #ddd;
    padding-left: 10px;
    border-radius: 5px;
    margin-bottom: 20px;
  }

  .output-content {
    color: #444;
    margin-top: 5px;
    margin-bottom: 5px;
  }
}

.step-body-panel {
  height: calc(100vh - 240px);
  border-radius: 7px;
  padding-bottom: 20px;
}

.follow-btn-panel{
  display: flex;
  flex-direction: row;
  align-items: center;
}

.link-item {
  margin: 7px 0px;
  font-size: 14px;
  background: #f5f5f5;
  border-radius: 5px;
  padding: 7px;
  padding-left: 10px;
  color: #666;
}

.display-output-content {
  iframe {
    width: 100%;
    height: calc(100vh - 260px);
    border: 0px;
    margin-bottom:15px;
    border-radius: 8px;
  }
}

</style>