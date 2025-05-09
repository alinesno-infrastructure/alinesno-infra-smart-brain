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
            <el-button type="primary" text size="large" loading>正在生成中 ...</el-button>
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
                  <el-button type="primary" text size="large" loading>正在生成中 ...</el-button>
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
          <i class="fa-solid fa-paper-plane"></i> 任务结果
        </div>
        <div class="task-result">

          <!-- 内容结果输出_start -->
          <div class="task-content-panel">
            <div class="think-title">
              思考
            </div>
            <div class="think-content">
              用户需要读取并解读指定论文的重点内容，已知论文的URL，根据工具列表，“link_reader”工具可以获取网页、pdf等链接下的标题和内容，正好满足需求，所以选择该工具，参数url_list为包含该论文URL的列表。
            </div>
            <div class="output-content">
              机器学习领域
              重要学者：周志华（国内机器学习领域权威学者）。
              相关文献：
              《机器学习及其应用2007》（周志华著，2007年，清华大学出版社）：结合理论与实际案例，探讨机器学习技术的落地应用[3]。
              《机器学习》（机械工业出版社，2013年，豆瓣评分7.4）：计算机科学与人工智能领域经典教材，覆盖主流算法与理论[6]。
              《Generative Adversarial Networks》（生成式对抗网络）：提出GAN架构及训练理论，证明生成器数据分布的全局最优性与算法收敛性[1]。
              总结
              《Attention Is All You
              Need》通过Transformer模型推动了NLP领域从传统RNN/CNN向注意力机制的范式转移，其成果与计算语言学、机器学习领域的基础理论（如翁富良等学者的著作）及技术方法如周志华的机器学习教材、GAN模型）密切相关，共同构成了现代深度学习与自然语言处理的研究基石。
            </div>
          </div>
          <!-- 内容结果输出_end -->

          <!-- 文件结果输出_start -->
          <div class="task-file-panel">
            <div class="think-title">
              <i class="fa-solid fa-file-word"></i> 文件列表
            </div>
            <div class="file-list-content">
              <div v-for="(item, index) in fileList" class="file-item" :key="index">
                <div class="file-item-content">
                  <div class="icon">
                    <i :class="item.icon"></i>
                  </div>
                  <div class="file-content">
                    <div class="file-name">
                      {{ item.name }}
                    </div>
                    <div class="file-link">
                      {{ item.downloadLink }}
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

const emits = defineEmits(['update:message']);

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

const fileList = ref([
  {
    name: '员工工资表.xlsx',
    icon: 'fa-solid fa-file-excel',
    downloadLink: 'employee_salary.xlsx',
    type: 'excel'
  },
  {
    name: '项目计划书.docx',
    icon: 'fa-solid fa-file-word',
    downloadLink: 'project_plan.docx',
    type: 'word'
  },
  {
    name: '会议记录.txt',
    icon: 'fa-solid fa-file',
    downloadLink: 'meeting_notes.txt',
    type: 'txt'
  },
  {
    name: '产品宣传视频.mp4',
    icon: 'fa-solid fa-file-video',
    downloadLink: 'product_promotion.mp4',
    type: 'video'
  },
  {
    name: '培训课程音频.mp3',
    icon: 'fa-solid fa-file-audio',
    downloadLink: 'training_course.mp3',
    type: 'audio'
  }
]);

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

const updateDeepSearchFlow= () => {
  const newDeepSearchFlow= { ...props.deepSearchFlow, text: '更新后的消息' };
  emits('update:deepSearchFlow', newDeepSearchFlow);
};

</script>

<style lang="scss" scoped>
.deepsearch-app-container {

  .step-title {
    font-size: 15px;
    background-color: #fafafa;
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
      display: flex;
      align-items: center;
      gap: 5px;
      margin-top: 10px;
      margin-bottom: 10px;

      .task-step-name {
        color: #444;
        font-weight: bold;
        background-color: #fafafa;
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
    background-color: #fafafa;
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
      background-color: #fafafa;
      color: #444;
      padding: 5px;
      border-radius: 5px;
    }

    .tool-content {
      margin: 10px;
      padding: 5px;
      background: #fafafa;
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
        width: calc(33% - 10px);
        float: left;
        color: #555;
        background: #fafafa;
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
    background: #fafafa;
    margin-bottom: 10px;
    border-radius: 10px;
    font-size: 13px;
    color: #999;
    padding: 10px;
}

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