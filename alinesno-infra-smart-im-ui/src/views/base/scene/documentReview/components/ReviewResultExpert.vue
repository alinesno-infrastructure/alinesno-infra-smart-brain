<template>
	<el-button type="primary" text bg @click="handleGenDocxReport">
        <i class="fa-solid fa-download"></i> &nbsp; 导出审核报告
    </el-button>

    <!-- 显示pdf文件 -->
        <el-dialog v-model="showPdfDialog" :title="pdfTitle" width="1300px" :before-close="handleClosePdfDialog" :show-close="true">
            <div class="document-wrapper" v-loading="loadingDocument">
                <el-scrollbar class="scrollable-area" style="margin-bottom:20px; padding-right:0px">
                    <ChapterEditor 
                        ref="chapterEditorRef"
                        v-model:articleData="articleData"
                        :outlineEnable="true"
                        :abcHeight="350"
                        autoFocus="true" 
                    />
                </el-scrollbar>
            </div>
            <!-- 显示下载word文档按钮 -->
            <div class="word-download-button">
                <el-button type="primary" size="large" :loading="loadingDocument" @click="downloadWordDocument">
                    <i class="el-icon-download"></i> 下载Word文档
                </el-button>
            </div>
        </el-dialog>

</template>

<script setup>

import ChapterEditor from './chapterEditor'

import {
    genDocxReport 
} from '@/api/base/im/scene/documentReviewSceneInfo';

const { proxy } = getCurrentInstance();
const route = useRoute();

const sceneId = ref(route.query.sceneId)
const taskId = ref(route.query.taskId)

const props = defineProps({ 
  currentTaskInfo: {
    type: Object, 
    required: false 
  }
})  

const showPdfDialog = ref(false);
const pdfTitle = ref('')
const iframeUrl = ref('')
const loadingDocument = ref(false);

const articleData = ref({
    content: "" , 
})

const downloadWordDocument = () => { 
 
 articleData.value.fileName = props.currentTaskInfo?.taskName ;
    proxy.download(
      "/api/infra/smart/assistant/scene/longTextTask/exportWord",
      { ...articleData.value },
      `${props.currentTaskInfo?.taskName + ' 审核报告' }_${new Date().getTime()}.docx`  // 如：任务名称_1712345678901.docx
    );
}; 

// 导出审核报告
const handleGenDocxReport = () => {
    pdfTitle.value = props.currentTaskInfo?.taskName + ' 审核报告预览'
    showPdfDialog.value = true;
    loadingDocument.value = true;

    genDocxReport(sceneId.value, taskId.value).then(res => { 
        nextTick(async () => {
            try { 
                articleData.value.content = res.data ;
                loadingDocument.value = false;
            } catch (error) {
                console.error('Error:', error);
                loadingDocument.value = false;
            }
        })
    })
}

</script>

<style lang="scss" scoped>

.word-download-button {
    margin-top: 20px; 
    text-align: right;
}
</style>