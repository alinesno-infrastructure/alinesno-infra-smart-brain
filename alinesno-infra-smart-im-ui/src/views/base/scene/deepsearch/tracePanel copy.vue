<template>
  <div class="task-trace-panel">
    <div class="title" style="padding-bottom:10px;">
      DeepSearch运行的工作空间
      <span>
        <el-button type="primary" bg text size="large" @click="handleClose">
          <el-icon><Close /></el-icon>
        </el-button>
      </span>
    </div>
    <div class="follow-step-panel">
      <div class="step-title">
        <span>
          <i :class="currentStepAction.icon"></i>
        </span>
         &nbsp; 正在使用
        <span class="trace-tag">{{ currentStepAction.actionName }}</span>
        <!-- 显示
        <span class="trace-tag">团队人员提交代码比例</span> -->
      </div>
      <el-scrollbar class="step-body-panel" ref="scrollbarRef" >
        <div class="step-body" ref="innerRef">

          <div class="say-message-body markdown-body think-content" v-if="currentStepAction?.think" v-html="readerHtml(currentStepAction.think)"></div>
          <div class="say-message-body markdown-body output-content" v-if="currentStepAction?.result" v-html="readerHtml(currentStepAction.result)"></div> 

        </div>
      </el-scrollbar>
    </div>
    <div class="follow-btn-panel">
      <el-row style="width:100%;align-items: center;">
        <el-col :span="8">
            <div class="step-btn-panel">
              <el-button type="primary" bg text size="large" @click="previewStepAction">
                <el-icon><ArrowLeftBold /></el-icon>
              </el-button>
              <el-button type="primary" bg text size="large" @click="nextStepAction">
                <el-icon><ArrowRightBold /></el-icon>
              </el-button>
            </div>
        </el-col>
        <el-col :span="16">
            <div class="step-progress-panel">
              <el-progress :percentage="percentage" :color="customColor" />
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

import MarkdownIt from 'markdown-it';
import mdKatex from '@traptitech/markdown-it-katex';
import hljs from 'highlight.js';

const innerRef = ref(null); // 滚动条的处理_starter
const scrollbarRef = ref(null);

const percentage = ref(20)
const customColor = ref('#909399')

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

    const planActins = flowData.plan.actions;

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

    currentStepActionFilter();
};

const handleClose = () => {
  // 关闭操作的逻辑，这里暂时为空
};


function initChatBoxScroll() {
  nextTick(() => {
    const element = innerRef.value;  // 获取滚动元素
    const scrollHeight = element.scrollHeight;

    scrollbarRef.value.setScrollTop(scrollHeight);
  })
}

// 更新deepsearchFlow并处理数据
const pushDeepsearchFlowTracePanel = (deepsearchFlowVal) => {
    deepsearchFlow.value = deepsearchFlowVal;
    processDeepsearchFlow(deepsearchFlow.value);
};

const setRoleInfo = (roleInfoVal) => {
    console.log('roleInfo = ' + roleInfoVal);
};

defineExpose({
  pushDeepsearchFlowTracePanel,
  setRoleInfo
});
</script>

<style lang="scss" scoped>
/* 样式部分保持不变 */
.task-trace-panel {
  background: #fff;
  border-radius: 8px;
  border: 1px solid var(--line-color-border-2, #eaedf1);
  padding: 10px;
  height: calc(100% - 110px);
  margin-top: 110px;
  box-shadow: rgba(0, 0, 0, 0.12) 0px -2px 12px 0px ;
  margin-right: 10px;
  
  .title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #444;
    border-bottom: 1px solid #eaedf1;
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
      align-items: center;
      color: #333;
      background: #f5f5f5;
      padding: 8px;
      border: 4px;
      border-radius: 5px;
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
    font-size: 14px;
    line-height: 1.3rem;
    border-radius: 5px;
    margin-bottom: 20px;
    // margin-left:10px;
  }

  .output-content {
    color: #444;
    margin-top: 5px;
    font-size: 14px;
    line-height: 1.3rem;
    margin-bottom: 5px;
    // margin-left:10px;
  }
}

.step-body-panel {
  // height: calc(100vh - 280px);
  // border: 1px solid #dedede;
  height: calc(100vh - 370px);
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
</style>