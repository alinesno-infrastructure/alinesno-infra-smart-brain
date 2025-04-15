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
                        <div id="fileshow" class="content" style="width: 100%;height: 100vh;border-radius: 5px;" ref="contentRef"></div>
                    </el-scrollbar>
                </div>
            </el-col>
            <el-col :span="12" style="padding-left: 20px;border-left: 1px solid #262727;">
                <div>
                    <!--
                    <el-steps class="mb-4" style="width: 100%;cursor: pointer;" :space="200" :active="currentActive" simple >
                        <el-step @click="handleStepClick(1)" title="合同概览" :icon="Edit" />
                        <el-step @click="handleStepClick(2)" title="审查清单" :icon="UploadFilled" />
                        <el-step @click="handleStepClick(3)" title="审查结果" :icon="Picture" />
                    </el-steps>
                    -->

                    <!-- <RoleChatPanel ref="roleChatPanelRef" /> -->

                    <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
                        <el-tab-pane name="first">
                            <template #label>
                                <span>
                                    <i class="fa-solid fa-comment-dots"></i>&nbsp;阅读对话
                                </span>
                            </template>
                            <div>
                                <RoleChatPanel ref="roleChatPanelRef" />
                            </div>
                        </el-tab-pane>
                        
                        <!-- 
                        <el-tab-pane name="second">
                            <template #label>
                                <span>
                                    <i class="fa-solid fa-user-pen"></i> &nbsp;阅读笔记
                                </span>
                            </template>
                            阅读笔记
                        </el-tab-pane>
                        <el-tab-pane label="相关案例" name="third">
                            <template #label>
                                <span>
                                    <i class="fa-solid fa-clapperboard"></i>&nbsp;相关案例
                                </span>
                            </template>
                            相关案例
                        </el-tab-pane>
                        <el-tab-pane name="four">
                            <template #label>
                                <span>
                                    <i class="fa-solid fa-file-word"></i>&nbsp;相关文档
                                </span>
                            </template>
                            相关文档
                        </el-tab-pane> 
                        -->

                    </el-tabs> 
                </div>

                <!-- 
                <div>
                    <el-collapse-transition>
                        <DocumentOverview v-if="currentActive == 1" />
                        <ReviewCheckList v-if="currentActive == 2" />
                        <ReviewResult v-if="currentActive == 3" />
                    </el-collapse-transition>
                </div> 
                -->

            </el-col>
        </el-row>
    </div>
</template>

<script setup>

import { useRouter } from 'vue-router'
const router = useRouter()
const route = useRoute()

// import DocumentOverview from '@/views/base/scene/documentReview/documentOverview.vue'
// import ReviewCheckList  from '@/views/base/scene/documentReview/reviewChecklist.vue'
// import ReviewResult  from '@/views/base/scene/documentReview/reviewResult.vue'

import RoleChatPanel from '@/views/base/scene/common/chatPanel';

import axios from "axios";
import { getScene } from '@/api/base/im/scene/documentReader';
import { getPreviewDocx } from '@/api/base/im/scene/documentReader';
import { renderAsync } from 'docx-preview';

const sceneId = ref(route.query.sceneId)
const currentSceneInfo = ref({});
const loadingDocument = ref(true)
const contentRef = ref(null)
const currentActive = ref(1)

const activeName = ref('first')
const roleChatPanelRef = ref(null)

const handleStepClick = (index) => {
    console.log('index = ' + index);
    currentActive.value = index
}

const getDocxContent = async() => {
    try {
        loadingDocument.value = true;

        const response = await getPreviewDocx(sceneId.value);

        console.log('response.data: ' + response); // 打印数据内容
        renderAsync(response, contentRef.value , null  , {
            inWrapper: true 
        });

        loadingDocument.value = false ;

        console.log('文档渲染成功');
    } catch (error) {
        console.error('获取或渲染文档失败', error);
    }
}
 
const onClickLeft = () => {
  // 判断历史记录中是否有回退
  if (history.state?.back) {
    router.back()
  } else {
    router.push('/')
  }
}

const handleGetScene = () => {
    getScene(sceneId.value).then(res => {
        currentSceneInfo.value = res.data;

        nextTick(() => {
            roleChatPanelRef.value.openChatBoxWithRole(currentSceneInfo.value.summaryAgentEngineer) ; 
        })

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