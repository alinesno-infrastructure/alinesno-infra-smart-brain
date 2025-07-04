<template>
  <div class="exam-pagercontainer">

    <el-container style="height:calc(100vh - 40px );background-color: #fff;">

      <el-aside width="80px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-main">
        <!-- 文件知识库列表 -->
        <div class="product-parser-container">
          <el-row>
            <el-col :span="8">
              <div class="review-question-preview-title">
                <span>
                  <i class="fa-solid fa-file-pdf"></i> 知识库检索规划预览
                </span>
                <el-button type="warning" text bg :disabled="!outline" @click="executeTaskJob('reGenerate')">
                  <i class="fa-solid fa-paper-plane"></i>&nbsp;开始生成
                </el-button>
              </div>

              <!-- 大纲生成预览 -->
              <div class="pager-gen-result-panel">

                <div v-if="!outline" style="margin-top: 10vh;">
                  <el-empty description="当前未生成项目检索的规划，你可以选择自定义的知识库来进行检索，生成项目检索的规划" />
                </div>

                <el-scrollbar v-if="outline" class="pager-container" ref="scrollbarRef">
                  <!-- 
                  <div style="font-size:13px;text-align: left;margin-bottom: 10px;color:#888">
                      确认下方内容大纲（点击编辑内容，右键添加/删除大纲项），开始选择模板
                  </div> 
                  -->
                  <div ref="innerRef">
                    <OutlineGenContainerPanel ref="pagerGenContainerPanelRef" :value="outline" />
                  </div>
                </el-scrollbar>
              </div>
            </el-col>

            <el-col :span="16" style="padding:0px 1%">

              <!-- 头部显示信息-->
              <div class="article-edit-header">
                <!-- 标题内容 -->
                <EditableTitle v-model:title="articleData.title" class="article-edit-title" />
              </div>

              <!-- 编辑内容 -->
              <div class="article-edit-content-display">
                <AgentContentDisplay v-model:articleData="articleData" @content-change="handleContentChange"
                  ref="contentEditor" />
              </div>
            </el-col>

          </el-row>
        </div>
      </el-main>
    </el-container>

    <!-- 运行抽屉 -->
    <div class="aip-flow-drawer flow-control-panel">
      <el-drawer v-model="showDebugRunDialog" :modal="false" size="40%" style="max-width: 700px;" title="预览与调试"
        :with-header="true">
        <div style="margin-top: 0px;">
          <RoleChatPanel ref="roleChatPanelRef" />
        </div>
      </el-drawer>
    </div>

  </div>
</template>

<script setup>
import { nextTick, onMounted, onUnmounted , ref } from 'vue';
import { ElMessage, ElLoading } from 'element-plus';

import RoleChatPanel from '@/views/base/scene/common/chatPanel';
import FunctionList from './functionList'
// import DatasetList from './components/ruleList.vue'
// import MarkdownPreview from './components/MarkdownPreview.vue'
import OutlineGenContainerPanel from './components/OutlineEditor.vue';

import EditableTitle from './components/EditableTitle.vue'
import AgentContentDisplay from './components/agentContentDisplay'

import {
  getScene,
  getTaskById,
  executeTask
} from '@/api/base/im/scene/productResearch';

import SnowflakeId from "snowflake-id";

const snowflake = new SnowflakeId();
const route = useRoute();
const router = useRouter();
const streamLoading = ref(null)

const taskId = ref(route.query.taskId)
const sceneId = ref(route.query.sceneId)
const channelStreamId = ref(route.query.channelStreamId || snowflake.generate())

// 执行面板
const showDebugRunDialog = ref(false);
const previewDialogRef = ref(null)
const roleChatPanelRef = ref(null)
const pagerGenContainerPanelRef = ref(null)

const outline = ref('')

const currentSceneInfo = ref({
  sceneName: '通用智能体服务',
});

const previewContent = ref('')

const articleData = ref({
  title: '学生管理系统学术论文答辩稿',
  content: previewContent
})

// 轮询定时器ID
const intervalId = ref(null);

// 任务是否正在运行的标志
const isTaskRunning = ref(false);

// 执行任务
const executeTaskJob = async (type) => {
  const data = {
    taskId: taskId.value,
    sceneId: sceneId.value,
    type: type ? type : 'execute' ,
    channelStreamId: channelStreamId.value
  }

  try {
    const res = await executeTask(data);
    console.log('执行结果:', res);
    
    const task = res.data;
    const taskStatus = task.taskStatus;

    // 如果任务状态为0(准备中)，则开始执行
    if (taskStatus == 0 || type == 'reGenerate') {
      isTaskRunning.value = true;
      executeStatus();
      startPolling(); // 开始轮询检查任务状态
    }
  } catch (error) {
    console.error('任务执行错误:', error);
    ElMessage.error('任务执行失败');
  }
}


const executeStatus = () => {

  // 打开流容器
  showDebugRunDialog.value = true;
  nextTick(() => {
    roleChatPanelRef.value.openChatBoxWithRole(currentSceneInfo.value.progressAnalyzerEngineer);
  })

  // 开始生成
  streamLoading.value = ElLoading.service({
    lock: true,
    background: 'rgba(255, 255, 255, 0.5)',
    customClass: 'custom-loading'
  });

  let text = '内容分析正在生成，请稍等...';
  streamLoading.value.setText(text)
}

const handleGetScene = async () => {
  try {
    // 获取场景信息
    const sceneResponse = await getScene(sceneId.value);
    currentSceneInfo.value = sceneResponse.data;

    await executeTaskJob() ; 
    
    // 获取任务信息
    await handleTaskInfo();
  } catch (error) {
    console.error('Error fetching scene or task:', error);
    ElMessage.error('获取场景或任务信息失败');
  }
}

const handleOutlineData = async (outlineStr) => {
  outline.value = JSON.parse(outlineStr);
  await nextTick();
  pagerGenContainerPanelRef.value.setOutlineJson(outline.value);
}

// 开始轮询检查任务状态
const startPolling = () => {
  // 先清除已有的轮询
  stopPolling();
  
  // 每5秒检查一次任务状态
  intervalId.value = setInterval(async () => {
    try {
      await handleTaskInfo();
    } catch (error) {
      console.error('轮询检查错误:', error);
      stopPolling();
    }
  }, 5000);
}

// 停止轮询
const stopPolling = () => {
  if (intervalId.value) {
    clearInterval(intervalId.value);
    intervalId.value = null;
  }
  isTaskRunning.value = false;
}

// 获取任务信息并处理
const handleTaskInfo = async () => {
  try {
    const taskResponse = await getTaskById(taskId.value);
    const taskData = taskResponse.data;
    
    // 更新文章标题
    articleData.value.title = taskData.taskName;
    
    // 处理大纲数据
    if (taskData.outline) {
      await handleOutlineData(taskData.outline);
    }
    
    // 处理详细内容
    if (taskData.detailedContent) {
      articleData.value.content = taskData.detailedContent;
    }
    
    // 检查任务状态
    if (taskData.taskStatus === 2) { // 任务进行中
      if (!isTaskRunning.value) {
        executeStatus();
        isTaskRunning.value = true;
      }
    } else if (taskData.taskStatus === 1) { // 任务已完成
      stopPolling(); // 停止轮询
      if (streamLoading.value) {
        streamLoading.value.close(); // 关闭加载状态
      }
      showDebugRunDialog.value = false ;
    }
  } catch (error) {
    console.error('处理任务信息错误:', error);
    throw error;
  }
}

onMounted(() => {

  // Add this check for channelStreamId in URL
  if (!route.query.channelStreamId) {
    router.replace({
      query: {
        ...route.query,
        channelStreamId: channelStreamId.value
      }
    });
  }

  handleGetScene();
})

// 组件卸载时停止轮询
onUnmounted(() => {
  stopPolling();
});

</script>

<style lang="scss" scoped>
.product-parser-container {
  padding: 10px;

  .product-parser-chat-box {
    margin-left: 20px;
  }

  .review-question-preview-title {
    padding: 10px;
    text-align: left;
    font-weight: bold;
    margin-left: 20px;
    margin-right: 10px;
    margin-bottom: 10px;
    border-radius: 10px;
    background: #fafafa;
    color: #444;
    font-size: 15px;
    display: flex;
    align-content: center;
    margin-top: 10px;
    align-items: center;
    justify-content: space-between;
  }

}

.pager-gen-result-panel {
  margin-bottom: 20px;
  margin-left: 0px;
  margin-right: 10px;
  text-align: left;

  .pager-container {
    background-color: #fafafa;
    margin: 10px 10px;
    margin-right: 0px;
    border-radius: 8px;
    height: calc(100vh - 160px);
    // padding: 10px;
    // padding-left: 10px;
    margin-left: 20px;
    margin-bottom: 0px;
  }
}
</style>