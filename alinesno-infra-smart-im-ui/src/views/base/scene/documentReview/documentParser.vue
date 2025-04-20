<template>
    <div class="app-container docuemnt-parser-container">
        <el-row>
            <el-col :span="12">
                <div style="font-size: 16px;font-weight: bold;display: flex;align-items: center;gap:20px; justify-content: flex-start;">
                    <el-button type="primary" text bg @click="onClickLeft()">
                        <i class="fa-solid fa-arrow-left"></i>
                    </el-button>
                    <span>
                        {{ currentSceneInfo?.documentName }}
                    </span>
                </div>
                <div class="document-wrapper" v-loading="loadingDocument" >
                    <el-scrollbar class="scrollable-area" style="height: calc(100vh - 140px);margin-top:20px; padding-right:0px">
                        <!-- <div id="fileshow" class="content" style="width: 100%;height: 100vh;border-radius: 5px;" ref="contentRef"></div> -->
                        <iframe 
                            :src="iframeUrl" 
                            style="position: absolute;width: 100%;border-radius: 5px; height: 100%;border: 0px;padding-bottom: 10px;background: #323639;">
                        </iframe>
                    </el-scrollbar>
                </div>
            </el-col>
            <el-col :span="12" style="padding-left: 20px;">
                <div>
                    <el-steps class="mb-4" style="width: 100%;cursor: pointer;" :space="200" :active="currentActive" simple >
                        <el-step @click="handleStepClick(1)" title="合同概览" :icon="Edit" />
                        <el-step @click="handleStepClick(2)" title="审查清单" :icon="UploadFilled" />
                        <el-step @click="handleStepClick(3)" title="审查结果" :icon="Picture" />
                    </el-steps>
                </div>
                <div>
                    <el-collapse-transition>
                        <DocumentOverview v-if="currentActive == 1" 
                            @handleStepClick="handleStepClick"
                            @genSingleChapterContent="genSingleChapterContent" />

                        <ReviewCheckList v-if="currentActive == 2" 
                            ref="reviewCheckListRef"
                            @handleStepClick="handleStepClick"
                            @genSingleChapterContent="genSingleChapterContent" />

                        <ReviewResult v-if="currentActive == 3" 
                            @handleStepClick="handleStepClick"
                            @genSingleChapterContent="genSingleChapterContent" />
                    </el-collapse-transition>
                </div>
            </el-col>
        </el-row>

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

import { useRouter } from 'vue-router'
const router = useRouter()
const route = useRoute()
const { proxy } = getCurrentInstance();

import DocumentOverview from '@/views/base/scene/documentReview/documentOverview.vue'
import ReviewCheckList  from '@/views/base/scene/documentReview/reviewChecklist.vue'
import ReviewResult  from '@/views/base/scene/documentReview/reviewResult.vue'
import RoleChatPanel from '@/views/base/scene/common/chatPanel';

import axios from "axios";
import { getScene } from '@/api/base/im/scene/documentReview';
import { getPreviewDocx } from '@/api/base/im/scene/documentReview';
import { renderAsync } from 'docx-preview';
import { nextTick } from 'vue'

// 生成文档预览
const iframeUrl = ref(null);
const sceneId = ref(route.query.sceneId)
const currentSceneInfo = ref({});
const loadingDocument = ref(true)
const contentRef = ref(null)
const currentActive = ref(1)

const reviewCheckListRef = ref(null)

// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)

const handleStepClick = (index , auditId) => {
    console.log('index = ' + index);
    currentActive.value = index
    showDebugRunDialog.value = false;
}

const getDocxContent = async() => {
    try {
        loadingDocument.value = true;

        const response = await getPreviewDocx(sceneId.value);

        console.log('response.data:' + response); // 打印数据内容
        // renderAsync(response, contentRef.value , null  , {
        //     inWrapper: true 
        // });

        iframeUrl.value = window.URL.createObjectURL(response);

        loadingDocument.value = false ;

        console.log('文档渲染成功');
    } catch (error) {
        console.error('获取或渲染文档失败', error);
    }
}

// 生成单章
const genSingleChapterContent = (editorRoleId) => {
    showDebugRunDialog.value = true ;
    nextTick(() => {
        roleChatPanelRef.value.openChatBox(editorRoleId) ; 
    })
}

const onClickLeft = () => {
  // 判断历史记录中是否有回退
//   if (history.state?.back) {
//     router.back()
//   } else {
//     router.push('/')
//   }

    proxy.$router.push({
        path: '/scene/documentReview/index' , 
        query: {
            sceneId: sceneId.value,
            back: true
        }
    })
}

const handleGetScene = () => {
    getScene(sceneId.value).then(res => {
        currentSceneInfo.value = res.data;
    })
}

// onMounted(() => {
//     handleGetScene();
//     getDocxContent()
// })

nextTick(() => {
    handleGetScene();
    getDocxContent()
})

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