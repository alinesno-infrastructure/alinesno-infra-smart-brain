<template> 
    <DocumentReviewContainer> 
                <div class="app-container docuemnt-parser-container">
                    <el-row>
                        <el-col :span="14">
                            <div style="font-size: 16px;font-weight: bold;display: flex;align-items: center;gap:20px; justify-content: flex-start;">
                                <!-- 
                                <el-button type="primary" text bg @click="onClickLeft()">
                                    <i class="fa-solid fa-arrow-left"></i>
                                </el-button> 
                                <span>
                                    {{ currentSceneInfo?.documentName }}
                                </span> 
                                -->

                                <!-- 标题内容 -->
                                <EditableTitle 
                                    v-if="currentTaskInfo"    
                                    v-model:title="currentTaskInfo.taskName" 
                                    class="article-edit-title" 
                                />

                            </div>
                            <div class="document-wrapper" v-loading="loadingDocument">
                                <el-scrollbar class="scrollable-area"  >
                                    <TinyMCEDocumentEditor ref="tinyMceEditorRef"  />
                                </el-scrollbar>
                            </div>
                        </el-col>
                        <el-col :span="10" style="padding-left: 20px;">
                            <div>  
                                <el-steps class="mb-4"  finish-status="success" style="width: 100%;cursor: pointer;" :space="200"
                                    :active="currentActive" simple>
                                    <el-step @click="handleStepClick(1)" title="合同概览" :icon="Edit" />
                                    <el-step @click="handleStepClick(2)" title="审查清单" :icon="UploadFilled" />
                                    <el-step @click="handleStepClick(3)" title="审查结果" :icon="Picture" />
                                </el-steps>
                            </div>
                            <div>
                                <el-collapse-transition>
                                    <DocumentOverview v-if="currentActive == 1" 
                                        ref="documentOverviewRef"
                                        :currentSceneInfo="currentSceneInfo"
                                        :currentTaskInfo="currentTaskInfo"
                                        @handleStepClick="handleStepClick"
                                        @closeGeneratorStatus="closeGeneratorStatus"
                                        @generatorStatus="generatorStatus"
                                        @genSingleChapterContent="genSingleChapterContent" />

                                    <ReviewCheckList v-if="currentActive == 2" 
                                        ref="reviewCheckListRef"
                                        :currentSceneInfo="currentSceneInfo"
                                        :currentTaskInfo="currentTaskInfo"
                                        @handleStepClick="handleStepClick"
                                        @closeGeneratorStatus="closeGeneratorStatus"
                                        @generatorStatus="generatorStatus"
                                        @genSingleChapterContent="genSingleChapterContent" />

                                    <ReviewResult v-if="currentActive == 3" @handleStepClick="handleStepClick"
                                        @genSingleChapterContent="genSingleChapterContent" />
                                </el-collapse-transition>
                            </div>
                        </el-col>
                    </el-row>

                    <!-- 运行抽屉 -->
                    <div class="aip-flow-drawer">
                        <el-drawer  v-model="showDebugRunDialog" :modal="false" size="40%" style="max-width: 700px;"
                            :close-on-press-escape="false"
                            title="预览与调试" :with-header="true">
                            <div style="margin-top: 0px;">
                                <RoleChatPanel ref="roleChatPanelRef" :showDebugRunDialog="showDebugRunDialog"/>
                            </div>
                        </el-drawer>
                    </div>
                </div>
 
                <!-- AI生成状态 -->
                <AIGeneratingStatus 
                    ref="generatingStatusRef" 
                    :back-to-path="'/scene/documentReview/reviewManager'"
                    :route-params="{ sceneId: sceneId }" 
                    :takeOverEnable="takeOverEnable"
                    @takeOver="handleTakeOver"
                /> 

           </DocumentReviewContainer>
</template>

<script setup>

import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
const router = useRouter()
const route = useRoute()
const { proxy } = getCurrentInstance();

import DocumentReviewContainer from "./DocumentReviewContainer"
import AIGeneratingStatus from '@/components/GeneratingStatus/index.vue'

import TinyMCEDocumentEditor from '@/components/DocumentEditor/index'
import EditableTitle from './components/EditableTitle.vue'
import DocumentOverview from '@/views/base/scene/documentReview/documentOverview.vue'
import ReviewCheckList from '@/views/base/scene/documentReview/reviewChecklist.vue'
import ReviewResult from '@/views/base/scene/documentReview/reviewResult.vue'
import RoleChatPanel from '@/views/base/scene/common/chatPanel';
import FunctionList from './functionList'

import axios from "axios";
import { 
    getScene,
    getSceneResultList,
    getPreviewDocx
} from '@/api/base/im/scene/documentReview';

import {
  getReviewTask,
  pagerListByPage
} from '@/api/base/im/scene/documentReviewTask';

import { nextTick } from 'vue'

// 生成文档预览
const iframeUrl = ref(null);
const sceneId = ref(route.query.sceneId)
const taskId = ref(route.query.taskId)
const currentSceneInfo = ref({});
const currentTaskInfo = ref({
    taskName: '审核文档'
});

const generatingStatusRef = ref(null)

const loadingDocument = ref(true)
const contentRef = ref(null)
const currentActive = ref(1)

const checkResultList = ref([])

const reviewCheckListRef = ref(null)
const documentOverviewRef = ref(null)

const tinyMceEditorRef = ref(null);

// 定义轮询相关变量
const pollingInterval = ref(null); // 轮询定时器引用
const pollingIntervalTime = 10000; // 轮询间隔时间设为10秒

// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)

const handleStepClick = (index, auditId) => {

    console.log('index = ' + index);

    if(index == 3){
        if(currentTaskInfo.value.resultGenStatus != 'success'){
            ElMessage.warning('当前文档未审核，请对文档做审核！');
            return ;
        }
    }

    if(index == 2){
        if(currentTaskInfo.value.reviewGenStatus != 'success'){
            ElMessage.warning('当前审核规则为空，请选择审核规则！');
            return ; 
        }
    }

    console.log('index = ' + index);
    currentActive.value = index
    showDebugRunDialog.value = false;

}

const getDocxContent = async () => {
    try {
        loadingDocument.value = true; 
        const response = await getPreviewDocx(taskId.value);  
        tinyMceEditorRef.value.setContent(response.data);
        loadingDocument.value = false; 
        console.log('文档渲染成功');
    } catch (error) {
        console.error('获取或渲染文档失败', error);
    }
}

// 生成单章
const genSingleChapterContent = (editorRoleId) => {

    if(showDebugRunDialog.value){
        return ;
    }

    showDebugRunDialog.value = true;
    nextTick(() => {
        roleChatPanelRef.value.openChatBox(editorRoleId);
    })
}

// 开启运行状态
const generatorStatus = (text) => {
    generatingStatusRef.value?.loading()
    if(text){
        generatingStatusRef.value?.setText(text)
    }
}

const closeGeneratorStatus = () => {
    showDebugRunDialog.value = false;
    generatingStatusRef.value?.close(); 
}



// 提取重复逻辑到一个单独的函数
const fetchSceneData = async () => {
    const res = await getScene(sceneId.value, taskId.value);
    currentSceneInfo.value = res.data; 
};
 
// 获取审查任务详情，并检查AI生成状态
const handleGetReviewTask = async() => {
    try {
        // 调用API获取任务详情
        const res = await getReviewTask(taskId.value);
        currentTaskInfo.value = res.data; 

        if(currentTaskInfo.value?.reviewGenStatus === 'success' 
            && (currentTaskInfo.value?.resultGenStatus === 'success' 
                || currentTaskInfo.value?.resultGenStatus === 'failed_no_content')) {
            stopPolling(); // 出错时也停止轮询
            return ;
        }
        
        // 检查生成状态是否已完成 
        if(currentTaskInfo.value?.reviewGenStatus === 'generating'){ // 生成中
            let text = '正在使用AI生成校验规则内容，请稍等.' ;  
            genSingleChapterContent(currentSceneInfo.value.analysisAgentEngineer) ; 
            generatorStatus(text);
        } 

        // 规则清单生成状态是否已完成 
        else if(currentTaskInfo.value?.reviewGenStatus === 'success'){ // 生成成功

            fetchSceneData();
          
            // 检查生成状态是否已完成 
            if(currentTaskInfo.value?.resultGenStatus === 'generating'){ // 生成中
                let text = currentTaskInfo.value?.currentStepInfo ; 
                genSingleChapterContent(currentSceneInfo.value.logicReviewerEngineer) ; 
                generatorStatus(text);
            } 

            // 检查生成状态是否已完成 
            else if(currentTaskInfo.value?.resultGenStatus === 'success'){ // 生成成功
                closeGeneratorStatus();
            } else if(currentTaskInfo.value?.resultGenStatus === 'failed_no_content'){ // 生成成功
                closeGeneratorStatus();
            } else {
                closeGeneratorStatus(); 
            }
        } 

        


    } catch (error) {
        console.error('获取审查任务出错:', error);
        stopPolling(); // 出错时也停止轮询
    }
};

// 开始轮询函数
const startPolling = () => {
    // 先清除可能存在的旧定时器
    stopPolling();
    
    // 设置新的定时器，每隔10秒检查一次状态
    pollingInterval.value = setInterval(handleGetReviewTask, pollingIntervalTime);
    
    // 立即执行一次检查，不用等待第一个间隔
    handleGetReviewTask();
};

// 停止轮询函数
const stopPolling = () => {
    if (pollingInterval.value) {
        clearInterval(pollingInterval.value); // 清除定时器
        pollingInterval.value = null; // 重置引用
    }
};

nextTick(async() => {
    await fetchSceneData();
    startPolling();
    getDocxContent()
})

// 组件卸载时清除定时器
onUnmounted(() => {
    stopPolling(); // 确保组件销毁时清理定时器
});


</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss'; 
</style>

<style>
.flow-control-panel .el-card__body {
    padding: 13px !important
}

.aip-flow-drawer .el-drawer.ltr,
.aip-flow-drawer .el-drawer.rtl {
    height: 93%;
    bottom: 10px;
    top: auto;
    right: 10px;
    border-radius: 8px;
}

.aip-flow-drawer .el-drawer__header {
    margin-bottom: 0px;
}
</style>