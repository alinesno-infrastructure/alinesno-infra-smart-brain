<template>
    <div class="content-parser-container exam-pagercontainer">


    <el-container style="height:calc(100vh - 60px );background-color: #fff;">

      <el-aside width="280px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-main">

        <el-row style="margin-left:20px;">

            <!-- 左侧内容编辑 -->
            <el-col :span="12">
                <div style="font-size: 16px;font-weight: bold;display: flex;align-items: center;gap:5px; justify-content: flex-start;">
                    <el-button type="primary" text bg @click="onClickLeft()">
                        <i class="fa-solid fa-arrow-left"></i>
                    </el-button>
                    <span>
                        {{ currentSceneInfo?.sceneName }}
                    </span>
                </div>
                <div class="document-wrapper" v-loading="loadingDocument" >
                    <el-scrollbar class="scrollable-area" style="height: calc(100vh - 120px);margin-top:10px; padding-right:0px">
                        <ChapterEditor ref="chapterEditorRef" />
                    </el-scrollbar>
                </div>
            </el-col>

            <!-- 右侧内容编辑 -->
            <el-col :span="12">
                <div style="padding: 20px;padding-top: 0px;">
                    <el-steps class="mb-4" style="width: 100%;cursor: pointer;" :space="200" :active="currentActive" simple >
                        <el-step @click="handleStepClick(1)" title="文本概览" :icon="Edit" />
                        <el-step @click="handleStepClick(2)" title="内容检查" :icon="Edit" />
                    </el-steps> 

                    <DocumentOverview 
                        v-if="currentActive == 1" 
                        :currentSceneInfo="currentSceneInfo"
                        @getContentPromptContent="getContentPromptContent"
                        @handleStepClick="handleStepClick"
                        @displayDocumentReviewList="displayDocumentReviewList"
                        @genSingleChapterContent="genSingleChapterContent" />

                    <ResultResult 
                        v-if="currentActive == 2" 
                        ref="resultResultRef"
                        :currentSceneInfo="currentSceneInfo"
                        @getContentPromptContent="getContentPromptContent"
                        @handlePointTo="handlePointTo"
                        @handleStepClick="handleStepClick"
                        @genSingleChapterContent="genSingleChapterContent" />

                </div>
            </el-col>
        </el-row>

        </el-main>
        </el-container>

         <!-- 运行抽屉 -->
        <div class="aip-flow-drawer">
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

import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()
const route = useRoute()

import RoleChatPanel from '@/views/base/scene/common/chatPanel';
import FunctionList from './functionList'

import { getScene } from '@/api/base/im/scene/contentFormatter';

import ChapterEditor from './chapterEditor'
import DocumentOverview from './overview.vue'
import ResultResult from './reviewResult.vue'

const currentActive = ref(1)
const chapterEditorRef = ref(null)
const resultResultRef = ref(null)
const showDebugRunDialog = ref(false);

// 执行面板
const roleChatPanelRef = ref(null)

const sceneId = ref(route.query.sceneId)
const channelStreamId = ref(route.query.channelStreamId);

const currentSceneInfo = ref({
    sceneName: '政务公文内容排版 业务开发平台' 
})

const handleStepClick = (index) => {
    console.log('index = ' + index);
    currentActive.value = index
    showDebugRunDialog.value = false;
}

const displayDocumentReviewList = (reviewList ) => {
    currentActive.value = 2  
    showDebugRunDialog.value = false;
    nextTick(() => {
        console.log('reviewList = ' + reviewList);
        console.log('resultResultRef = ' + resultResultRef.value);
        resultResultRef.value.setRuleContent(reviewList);
    })
}

const handleGetScene = () => {
    getScene(sceneId.value).then(res => {
        currentSceneInfo.value = res.data;
        chapterEditorRef.value.setData(currentSceneInfo.value.contentPromptContent);
    })
}

// 生成单章
const genSingleChapterContent = (editorRoleId) => {
    console.log('--->> editorRoleId = ' + editorRoleId);
    showDebugRunDialog.value = true ;
    nextTick(() => {
        roleChatPanelRef.value.openChatBox(editorRoleId) ; 
    })
}

// 获取生成内容
// const getContentPromptContent = () => {
//     return chapterEditorRef.value.getData() ;
// }

const getContentPromptContent = (callback) => {
  const response = chapterEditorRef.value.getData() ;
  callback(response);
};

const handlePointTo = (item) => {
    console.log('originalContent = ' + item.originalContent)
    chapterEditorRef.value.locateAfterText(item.originalContent);
}

const onClickLeft = () => {
    router.back();
}

onMounted(() => {
    console.log('chapterEditorRef = ', chapterEditorRef.value)
    handleGetScene() ;
})

</script>

<style lang="scss" scoped>

.content-parser-container {
    padding: 10px;
    background: #fff;
}

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