<template>
    <div class="app-container docuemnt-parser-container">
        <el-row>
            <el-col :span="12">
                <div style="font-size: 16px;font-weight: bold;">
                    大罗市应急管理局首都核心区加油站智能视频监控和物联监测系统项目_招投标文件
                </div>
                <div class="document-wrapper" v-loading="loadingDocument" >
                    <el-scrollbar class="scrollable-area" style="height: calc(100vh - 130px);margin-top:20px; padding-right:0px">
                        <div id="fileshow" class="content" style="width: 100%;height: 100vh;border-radius: 5px;" ref="contentRef"></div>
                    </el-scrollbar>
                </div>
            </el-col>
            <el-col :span="12" style="padding-left: 20px;border-left: 1px solid #262727;">
                <div>
                    <el-steps class="mb-4" style="width: 100%;cursor: pointer;" :space="200" :active="currentActive" simple >
                        <el-step @click="handleStepClick(1)" title="合同概览" :icon="Edit" />
                        <el-step @click="handleStepClick(2)" title="审查清单" :icon="UploadFilled" />
                        <el-step @click="handleStepClick(3)" title="审查结果" :icon="Picture" />
                    </el-steps>
                </div>
                <div>
                    <el-collapse-transition>
                        <DocumentOverview v-if="currentActive == 1" />
                        <ReviewCheckList v-if="currentActive == 2" />
                        <ReviewResult v-if="currentActive == 3" />
                    </el-collapse-transition>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>

import DocumentOverview from '@/views/base/scene/documentReview/documentOverview.vue'
import ReviewCheckList  from '@/views/base/scene/documentReview/reviewChecklist.vue'
import ReviewResult  from '@/views/base/scene/documentReview/reviewResult.vue'

import axios from "axios";
import { getPreviewDocx } from '@/api/base/im/scene/documentReview';
import { renderAsync } from 'docx-preview';

const loadingDocument = ref(true)
const contentRef = ref(null)
const currentActive = ref(1)

const handleStepClick = (index) => {
    console.log('index = ' + index);
    currentActive.value = index
}

// const getDocxContent =() => {
//     // 在这里调用getPreviewDocx函数获取docx文件内容，并使用renderAsync函数渲染到页面上

// }

const getDocxContent = async() => {
    try {
        loadingDocument.value = true;

        const response = await getPreviewDocx('123');

        console.log('response.data:' + response); // 打印数据内容
        renderAsync(response, contentRef.value , null  , {
            inWrapper: true 
        });

        loadingDocument.value = false ;

        console.log('文档渲染成功');
    } catch (error) {
        console.error('获取或渲染文档失败', error);
    }
}

nextTick(() => {
    getDocxContent()
})

</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss';
</style>