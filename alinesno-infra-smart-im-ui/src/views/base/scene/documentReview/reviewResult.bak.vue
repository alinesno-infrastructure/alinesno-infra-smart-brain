<template>
    <div class="review-result-container">
        <div style="display: flex;flex-direction: row;justify-content: space-between;">
            <div>
                <el-button type="primary" text bg @click="handleGenDocxReport">
                    <i class="fa-solid fa-download"></i> &nbsp; 导出审核报告
                </el-button>
                <el-button type="primary" :loading="getMarkLoading" text bg @click="handleGenMarkDocxReport">
                    <i class="fa-solid fa-file-word"></i> &nbsp; 导出标注文档
                </el-button>
            </div>
            <div>
                <!--
                <el-checkbox-group v-model="checkList">
                    <el-checkbox type="danger" v-for="item in checkboxOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-checkbox-group>
                -->
            </div>
        </div>
        <div>
            <div style="margin-top:20px;">
                <div>
                    <el-scrollbar class="scrollable-area" style="height: calc(100vh - 210px);padding-right:0px">
                        <div v-for="(item, index) in checkResultList"
                            :class="'item-alert-' + item.riskLevel"
                            :key="index" class="check-result-item" @click="handleCheckItemClick(item)">
                            <div class="check-result-box" style="display: flex;gap: 5px;align-items: center;">
                                <span>
                                    {{ index + 1 }}
                                </span>
                                <span>
                                    {{ item.ruleName}}
                                </span>
                            </div>
                            <el-tag :type="getTagType(item)" effect="dark">
                                {{ item.auditResultCount }}
                            </el-tag>
                        </div>
                    </el-scrollbar>
                </div>
            </div>
        </div>

        <!-- 规则检查结果列表并操作面板 -->
        <RuleCheckResultPanel ref="ruleCheckResultPanelRef" />

        <!-- 显示pdf文件 -->
        <el-dialog v-model="showPdfDialog" :title="pdfTitle" width="1300px" :before-close="handleClosePdfDialog" :show-close="true">
                <div class="document-wrapper" v-loading="loadingDocument" >
            <el-scrollbar class="scrollable-area" style="height: calc(70vh);margin-top:20px; padding-right:0px">
                <iframe 
                    :src="iframeUrl" 
                    style="position: absolute;width: 100%;border-radius: 5px; height: 100%;border: 0px;padding-bottom: 10px;background: #323639;">
                </iframe>
            </el-scrollbar>
                </div>
                <!-- 显示下载word文档按钮 -->
                 <div class="word-download-button">
                    <el-button type="primary" size="large" :loading="loadingDocument" @click="downloadWordDocument">
                        <i class="el-icon-download"></i> 下载Word文档
                    </el-button>
                </div>
        </el-dialog>

    </div>
</template>

<script setup>

import { saveAs } from 'file-saver'
import { ElMessage }  from 'element-plus'
import { 
    getSceneResultList , 
    downloadMarkDocx
} from '@/api/base/im/scene/documentReview';

import { 
    genDocxReport, 
    getPreviewReportDocx,
    getPreviewUrl
} from '@/api/base/im/scene/documentReviewSceneInfo';

const { proxy } = getCurrentInstance();
const route = useRoute();
const currentSceneInfo = ref(null);
const sceneId = ref(route.query.sceneId)
const taskId = ref(route.query.taskId)

import RuleCheckResultPanel from './ruleCheckResultPanel.vue'

const ruleTitle = ref('');
const contractReviewList = ref([]);
const getMarkLoading = ref(false);
const ruleCheckResultPanelRef = ref(null);

const showPdfDialog = ref(false);
const pdfTitle = ref('')
const iframeUrl = ref('')
const loadingDocument = ref(false);
const currentStoreId = ref(null)

const checkboxOptions = [
    { "label": "高风险", "value": "high" },
    { "label": "中风险", "value": "medium" },
    { "label": "低风险", "value": "low" },
    { "label": "通过", "value": "passed" }
];

const checkList = ref([]);

// const filteredCheckResultList = computed(() => {
//     if (checkList.value.length === 0) {
//         return checkResultList.value;
//     } else {
//         return checkResultList.value.filter(item => checkList.value.includes(item.riskLevel));
//     }
// });

const checkResultList = ref([]);

// 获取标签颜色
const getTagType = (item) => {
    switch (item.riskLevel) {
        case 'low':
            return 'info';
        case 'medium':
            return 'warning';
        case 'high':
            return 'danger';
        default:
            return 'success';
    }
}

// // 通过风险等级做过滤 
// const checkByRiskLevel = (riskLevel) => {
//     const checkList =  checkResult.value.filter(item => item.riskLevel === riskLevel);

// }

// 点击规则
const handleCheckItemClick = (item) => {
    console.log(item.ruleName);
    
    nextTick(() => {
        item.ruleId = item.id ; 
        item.sceneId = sceneId.value ;
        ruleCheckResultPanelRef.value.openDrawerAndSettings(item);
    });
    
}

const handleGetScene = () => {
    getSceneResultList(sceneId.value , taskId.value).then(res => {
        currentSceneInfo.value = res.data;
        checkResultList.value = currentSceneInfo.value.reviewListDtos ;

        // 将所有选项的 ruleName 赋值给 checkList 实现全选
        // checkList.value = contractReviewList.value.map(item => item.id);

        // checkResultList.value = currentSceneInfo.value.reviewListDtos ;
    })
}

// 导出审核报告
const handleGenDocxReport = () => {

    pdfTitle.value = '审核报告预览'
    showPdfDialog.value = true;
    loadingDocument.value = true;

    genDocxReport(sceneId.value , taskId.value).then(res => {
        // window.open(res.data)
        const storegeId = res.data ;
        currentStoreId.value = storegeId ;

        nextTick(async () => {
            try{
                const response = await getPreviewReportDocx(storegeId , taskId.value);
                console.log('response.data:' + response); // 打印数据内容
                iframeUrl.value = window.URL.createObjectURL(response);
                loadingDocument.value = false;
            }catch (error) {
                console.error('Error:', error);
                loadingDocument.value = false;
            }
        })
    })
}

const downloadWordDocument = () => {
    getPreviewUrl(currentStoreId.value).then(res => {
        window.open(res.data);
    }) 
}

// 导出标注文档
const handleGenMarkDocxReport = () => {
    getMarkLoading.value = true;
    try{
        downloadMarkDocx(sceneId.value , taskId.value).then(res => {
            proxy.download(res.data , {} , `${currentSceneInfo.value.documentName}_${new Date().getTime()}.docx`)
            getMarkLoading.value = false;
        }).catch(error => {
            getMarkLoading.value = false;
        })
    } catch (error) {
        console.error('Error:', error);
        ElMessage.error('下载失败');
        getMarkLoading.value = false;
    }
}

onMounted(() => {
    handleGetScene();
})

</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss';

.review-result-container {
    margin-top: 20px;

    .check-result-item {
        padding: 10px;
        background: #f5f5f5;
        border-radius: 5px;
        margin-bottom: 10px;
        font-size: 14px;
        display: flex;
        justify-content: space-between;
    }

}

.check-result-item{
    cursor: pointer;

    &:hover{
        background: #e5e5e5;
        font-weight: bold;
    }
}

.word-download-button {
    margin-top: 20px;
    margin-bottom: 20px;
    text-align: right;
}

</style>
