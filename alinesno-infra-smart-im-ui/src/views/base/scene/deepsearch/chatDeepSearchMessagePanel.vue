<template>
  <div class="deepsearch-app-container">


    <div class="deepsearch-message-item" v-if="deepSearchFlow.plan">
      <!-- 任务规划_start -->
      <div class="step-title">
        <i class="fa-solid fa-compass-drafting"></i> {{ deepSearchFlow.plan.name }} 
      </div>
      <div class="task-plan-panel">

        <div class="think-panel" v-for="(item , index) in mergedPlanActions" :key="index">
          <div class="think-title">
            <i :class="item.icon"></i> {{ item.actionName + '('+ item.actionType +')'}} 
          </div>
          <div class="think-loading" v-if="item.status === 'doing'">
            <el-button class="loading-btn" type="primary" text size="large" loading>正在生成中 ...</el-button>
          </div>
          <!-- 
          <div class="say-message-body markdown-body think-content" v-if="item.think" v-html="readerHtml(item.think)"></div>
          <div class="say-message-body markdown-body output-content" v-if="item.result" v-html="readerHtml(item.result)"></div> 
           -->
        </div>

      </div>
      <!-- 任务规划_end -->

      <div v-if="deepSearchFlow.steps">
        <!-- 任务执行_start -->
        <div class="step-title">
          <i class="fa-solid fa-signature"></i> 执行步骤
        </div>

        <div v-for="(item, index) in mergedSteps" :key="index" class="task-item">
          <div class="task-step-title">
            <i class="fa-solid fa-arrow-right"></i> 当前步骤: 
            <span class="task-step-name"> Step{{ index + 1 }} </span> 任务名称: 
            {{ item.name }}
          </div>
          <div>
            <div class="step-desc">
              {{ item.description }}
            </div>
            <div class="think-loop">
              <div class="think-panel" v-for="(actionItem, actionIndex) in item.actions" :key="actionIndex">
                <div class="think-title">
                  <i :class="actionItem.icon"></i> {{ actionItem.actionName + '('+ actionItem.actionType +')'}} 
                </div>
                <div class="think-loading" v-if="actionItem.status === 'doing'">
                  <el-button class="loading-btn" type="primary" text size="large" loading>正在生成中 ...</el-button>
                </div>
                <!-- <div class="say-message-body markdown-body think-content" v-if="actionItem.think" v-html="readerHtml(actionItem.think)"></div>
                <div class="say-message-body markdown-body output-content" v-if="actionItem.result" v-html="readerHtml(actionItem.result)"></div> -->
              </div>

            </div>

          </div>
        </div>
        <!-- 任务执行_end -->
      </div>

      <!-- 任务结果输出_start -->
      <div v-if="deepSearchFlow.output">
        <div class="step-title">
          <i class="fa-solid fa-paper-plane"></i> {{ deepSearchFlow.output.name }} 
        </div>
        <div class="task-result">

          <!-- 内容结果输出_start -->
          <div class="task-content-panel" v-for="(item, index) in mergedOutputActions" :key="index">
            <div class="think-title" >
              <i :class="item.icon"></i> {{ item.actionName + '('+ item.actionType +')'}} 
            </div>
            <div class="think-loading" v-if="item.status === 'doing'">
              <el-button class="loading-btn" type="primary" text size="large" loading>正在生成中 ...</el-button>
            </div>
            <!-- <div class="say-message-body markdown-body think-content" v-if="item.think" v-html="readerHtml(item.think)"></div>
            <div class="say-message-body markdown-body output-content" v-if="item.result" v-html="readerHtml(item.result)"></div>  -->
          </div>
          <!-- 内容结果输出_end -->

          <!-- 文件结果输出_start -->
          <div class="task-file-panel" v-if="deepSearchFlow.output?.attachments && deepSearchFlow.output?.attachments.length > 0">
            <div class="think-title">
              <i class="fa-solid fa-file-word"></i> 文件列表
            </div>

            <div class="file-list-content">
              <div v-for="(item, index) in deepSearchFlow.output?.attachments" class="file-item" :key="index" @click="handleDisplayContent(item)" >
                <div class="file-item-content">
                  <div class="icon">
                    <i :class="item.icon"></i>
                  </div>
                  <div class="file-content">
                    <div class="file-name">
                      {{ item.name }}
                    </div>
                    <div class="file-link">
                      {{ truncateString(item.desc , 10) }}
                    </div>
                  </div>
                </div>
                <div class="download-link">
                  <el-button type="info" size="small" text @click="handleDownload(item)">
                    <i class="fa-solid fa-arrow-down"></i>
                  </el-button>
                </div>
              </div>
            </div>

          </div>
          <!-- 文件结果输出_end -->

        </div>
        <!-- 任务结果输出_start -->
      </div>

    </div>

    <!-- 参数配置窗口 
    <div class="deepsearch-flow-drawer">
        <el-drawer 
            v-model="showTracePanelDialog" 
            v-if="currentTraceContent"
            :modal="false" size="50%" 
            style="max-width: 700px;" 
            title="运行任务动态追踪"
            :with-header="true">

            <div style="margin-top: 0px;padding:0px !important" class="agent-chat-box  agent-inference-container">
                <div class="say-message-body markdown-body output-content" 
                    v-html="readerHtml(currentTraceContent)">
                </div>
            </div> 

        </el-drawer>
    </div>
     -->

  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  deepSearchFlow: {
    type: Object,
    required: true 
  },
  mdi: {
    type: Object,
    required: true 
  }
});

const showTracePanelDialog = ref(true);
const currentTraceContent = ref('');

const thinkContent = ref(null)

const emits = defineEmits(['update:message' , "handleDisplayContent"]);

/** 读取html文本 */
const readerHtml = (chatText) => {
  if (chatText) {
    return props.mdi.render(chatText);
  }
}

/** 读取html文本 */
const readerTraceHtml = (chatText) => {
  if (chatText) {
    // return props.mdi.render(chatText);
    currentTraceContent.value = chatText;
  }
}

const handleDisplayContent = (item) => {
  emits("handleDisplayContent", item);
}

// 计算属性：合并规划动作（按actionId拼接think和result）
const mergedPlanActions = computed(() => {
  const actionMap = new Map();
  
  (props.deepSearchFlow.plan?.actions || []).forEach(newAction => {
    const actionId = newAction.actionId;
    const existingAction = actionMap.get(actionId);

    if (existingAction) {
      // 拼接think和result（保留历史内容+新增内容）
      actionMap.set(actionId, {
        ...existingAction,  // 保留历史属性（如icon、actionType等）
        ...newAction,       // 用新动作覆盖非文本属性（如status、errorMsg）
        think: `${existingAction.think || ''}${newAction.think || ''}`,  // 关键拼接逻辑
        result: `${existingAction.result || ''}${newAction.result || ''}` // 关键拼接逻辑
      });
    } else {
      // 新动作直接存入（初始化）
      actionMap.set(actionId, { ...newAction });
    }
  });

  return Array.from(actionMap.values());
});

// 计算属性：合并步骤（包含步骤内动作的think/result拼接）
const mergedSteps = computed(() => {
  const stepMap = new Map();

  (props.deepSearchFlow.steps || []).forEach(newStep => {
    const stepId = newStep.id;
    const existingStep = stepMap.get(stepId);

    // 1. 合并步骤内的动作（按actionId拼接think/result）
    const mergedActions = newStep.actions.reduce((acc, newAction) => {
      const actionId = newAction.actionId;
      const existingAction = acc.get(actionId);

      if (existingAction) {
        acc.set(actionId, {
          ...existingAction,
          ...newAction,
          think: `${existingAction.think || ''}${newAction.think || ''}`,
          result: `${existingAction.result || ''}${newAction.result || ''}`
        });
      } else {
        acc.set(actionId, { ...newAction });
      }
      return acc;
    }, new Map());

    // 2. 合并步骤本身（保留历史步骤数据+新增步骤数据）
    if (existingStep) {
      stepMap.set(stepId, {
        ...existingStep,          // 保留历史步骤属性（如name、description）
        ...newStep,               // 用新步骤覆盖状态类属性（如status）
        actions: [                // 合并后的动作列表：历史动作+新增动作（去重）
          ...existingStep.actions.filter(a => !mergedActions.has(a.actionId)), // 保留未重复的历史动作
          ...Array.from(mergedActions.values())                               // 添加合并后的新动作
        ]
      });
    } else {
      stepMap.set(stepId, {
        ...newStep,
        actions: Array.from(mergedActions.values())
      });
    }
  });

  return Array.from(stepMap.values());
});

// 计算属性：合并规划动作（按actionId拼接think和result）
const outputActionMap = new Map();
const mergedOutputActions = computed(() => {
  
  (props.deepSearchFlow.output?.actions || []).forEach(newAction => {
    const actionId = newAction.actionId;
    const existingAction = outputActionMap.get(actionId);

    if (existingAction) {
      // 拼接think和result（保留历史内容+新增内容）
      outputActionMap.set(actionId, {
        ...existingAction,  // 保留历史属性（如icon、actionType等）
        ...newAction,       // 用新动作覆盖非文本属性（如status、errorMsg）
        think: existingAction.think += newAction.think , // `${existingAction.think || ''}${newAction.think || ''}`,  // 关键拼接逻辑
        result: existingAction.result += newAction.result , // `${existingAction.result || ''}${newAction.result || ''}` // 关键拼接逻辑
      });
    } else {
      // 新动作直接存入（初始化）
      outputActionMap.set(actionId, { ...newAction });
    }
  });

  return Array.from(outputActionMap.values());
});

const updateDeepSearchFlow= () => {
  const newDeepSearchFlow= { ...props.deepSearchFlow, text: '更新后的消息' };
  emits('update:deepSearchFlow', newDeepSearchFlow);
};

</script>

<style lang="scss" scoped>
.deepsearch-app-container {

  .step-title {
    font-size: 15px;
    background-color: #f5f5f5;
    font-weight: bold;
    margin-bottom: 10px;
    padding: 10px;
    padding-left: 10px;
    border-radius: 5px;
    margin-top: 10px;
    color: #444;
  }

  .task-item {
    font-size: 14px;
    line-height: 1.5rem;
    padding-left: 10px;

    .task-step-title {
      // display: flex;
      align-items: center;
      gap: 5px;
      margin-top: 10px;
      margin-bottom: 10px;

      .task-step-name {
        color: #444;
        font-weight: bold;
        background-color: #f5f5f5;
        padding: 5px;
        border-radius: 3px;
        line-height: 1rem;
      }
    }
  }

  .task-plan-panel {
    padding-left: 10px;
    display: flex;
    flex-direction: column;
    gap: 5px;
  }

  .think-content {
    color: #aaa;
    border-left: 2px solid #ddd;
    padding-left: 10px;
    font-size: 14px;
    line-height: 1.3rem;
    border-radius: 5px;
    margin-top: 5px;
    margin-bottom: 5px;
    margin-left:10px;
  }

  .output-content {
    color: #444;
    margin-top: 5px;
    font-size: 14px;
    line-height: 1.3rem;
    margin-bottom: 5px;
    margin-left:10px;
  }

  .think-title {
    font-size: 14px;
    background-color: #f5f5f5;
    color: #444;
    padding: 10px;
    margin-bottom: 10px;
    border-radius: 5px;
  }

  .task-result {
    padding-left: 10px;
  }

  .tool-panel {
    .tool-title {
      background-color: #f5f5f5;
      color: #444;
      padding: 5px;
      border-radius: 5px;
    }

    .tool-content {
      margin: 10px;
      padding: 5px;
      background: #f5f5f5;
      border-radius: 7px;
      font-size: 14px;
      color: #444;
    }
  }

  .summary-panel {
    display: flex;
    gap: 8px;
    flex-direction: column;
  }

  .think-panel {
    display: flex;
    gap: 8px;
    flex-direction: column;
  }



  .task-result{

    .task-content-panel {
        display: flex;
        gap: 10px;
        flex-direction: column;
        margin-bottom: 10px;
    }

    .task-file-panel {
        margin-top: 10px;
        margin-bottom: 20px;
    }

    .file-list-content{

      display: flex;
      margin-top: 10px;
      flex-wrap: wrap;
      gap: 10px;

      .icon {
        font-size: 20px;
        width: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .file-content {
        font-size: 14px;
      }

      .file-item-content {
        display: flex;
        flex-direction: row;
        align-content: center;
        align-items: center;
      }

      .file-item {
        width: calc(50% - 10px);
        float: left;
        cursor: pointer;
        color: #555;
        background: #f5f5f5;
        padding: 10px;
        border-radius: 8px;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;

        &:hover {
          .download-link {
            display: block;
          }
        }

        .file-link {
          font-size: 13px;
          color:#777;
        }

        .download-link {
          font-size: 16px;
          color: #777;
          // margin-right: 10px;
          display: none;
          cursor: pointer;
        }
      }

    }
  }

  .step-desc {
    background: #f5f5f5;
    margin-bottom: 10px;
    border-radius: 10px;
    font-size: 13px;
    color: #999;
    padding: 10px;
  }

}

.loading-btn{
  margin-left: 0px;
  padding-left: 0px;
}
</style>

<style>
.deepsearch-flow-drawer .el-drawer.ltr,
.deepsearch-flow-drawer .el-drawer.rtl {
    /* height: 80%;
    bottom: 10px; */
    top: auto;
    /* right: 10px; */
    border-radius: 8px;
}

.deepsearch-flow-drawer .el-drawer__header {
    margin-bottom: 0px;
}
</style>