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
                <el-checkbox-group v-model="checkList" @change="handleFilterChange">
                                    <el-checkbox
                                        v-for="item in availableCheckboxOptions"
                                        :key="item.value"
                                        :label="item.value"
                                    >
                                        {{ item.label }}
                                    </el-checkbox>
                                </el-checkbox-group>
            </div>
        </div>
        <div>
            <div style="margin-top:20px;">

                <div v-if="checkResultList.length == 0">
                    <el-empty description="审核结果为空，文档未进行审核">
                      <el-button type="primary" text bg size="large" @click="handleGenDocxReport" icon="Plus">返回审核</el-button>
                    </el-empty>
                </div>
 
                <div v-if="checkResultList.length > 0">
                    <el-scrollbar class="scrollable-area" style="height: calc(100vh - 210px);padding-right:0px">
                        <el-collapse v-model="activeNames" accordion>
                            <el-collapse-item
                                v-for="(item, index) in filteredResults"
                                :name="item.id"
                                :key="index"
                                :class="'item-alert-' + item.riskLevel">
                                <template #title>
                                    <div class="check-result-box" style="display: flex;gap: 5px;align-items: center;width: 100%;">
                                        <span style="min-width: 30px;">
                                            {{ index + 1 }}
                                        </span>
                                        <span style="flex: 1;">
                                            {{ item.ruleName}}
                                        </span>
                                        <el-tag :type="getTagType(item)" effect="dark">
                                            {{ item.auditResultCount }}
                                        </el-tag>
                                    </div>
                                </template>
                                <div v-if="item.auditResultCount > 0" style="padding: 10px;">
                                    <div v-for="(result, resultIndex) in item.auditResults" :key="resultIndex" class="result-item">
                                        <div class="result-header" @click="result.showDetails = !result.showDetails">
                                            <el-icon :size="16" style="margin-right: 8px;">
                                                <ArrowRight v-if="!result.showDetails" />
                                                <ArrowDown v-else />
                                            </el-icon>
                                            <strong>{{ result.modificationReason }}</strong>
                                            <el-tag :type="getTagType(item)" effect="dark" style="margin-left: 10px;">
                                                {{ result.riskLevel === 'high' ? '高危' : result.riskLevel === 'medium' ? '中危' : '低危' }}
                                            </el-tag>
                                        </div>

                                        <div v-show="result.showDetails" class="result-details">
                                            <div class="detail-section">
                                                <b>原文内容:</b>
                                                <p>{{ result.originalContent }}</p>
                                            </div>
                                            <div class="detail-section">
                                                <b>建议内容:</b>
                                                <p>{{ result.suggestedContent }}</p>
                                            </div>
                                            <div class="action-buttons">
                                                <el-button type="primary" text bg size="small" @click.stop="handlePointTo(result)">
                                                    <i class="fa-solid fa-hand-pointer"></i>&nbsp;定位
                                                </el-button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div v-else style="padding: 10px; color: #999;">
                                    未发现问题
                                </div>
                            </el-collapse-item>
                        </el-collapse>
                    </el-scrollbar>
                </div>
            </div>
        </div>

        <!-- 显示pdf文件 -->
        <el-dialog v-model="showPdfDialog" :title="pdfTitle" width="1300px" :before-close="handleClosePdfDialog" :show-close="true">
            <div class="document-wrapper" v-loading="loadingDocument">
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
import { ref, onMounted, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { getCurrentInstance } from 'vue';
import { saveAs } from 'file-saver'
import { ElMessage } from 'element-plus'
import { ArrowRight, ArrowDown } from '@element-plus/icons-vue'
import {
    getSceneResultList,
    downloadMarkDocx,
    getAuditResultByRuleId
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

const activeNames = ref([]); // 手风琴当前激活项
const getMarkLoading = ref(false);

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

// 默认选中所有选项
const checkList = ref(checkboxOptions.map(opt => opt.value));
const checkResultList = ref([]);

// 计算可用的复选框选项（只显示有结果的类型）
const availableCheckboxOptions = computed(() => {
    return checkboxOptions.filter(opt =>
        checkResultList.value.some(item =>
            item.riskLevel === opt.value && item.auditResultCount > 0
        )
    );
});

// 计算过滤后的结果
const filteredResults = computed(() => { 
    return checkResultList.value.filter(item =>
       checkList.value.includes(item.riskLevel) && item.auditResultCount > 0
    );
});

// 处理过滤变化
const handleFilterChange = () => {
    // 可以在这里添加额外的逻辑
    console.log('当前选中的过滤条件:', checkList.value);
};


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

// 获取场景数据的方法需要稍作修改
const handleGetScene = async () => {
    try {
        const res = await getSceneResultList(sceneId.value, taskId.value);
        currentSceneInfo.value = res.data;
        checkResultList.value = currentSceneInfo.value.reviewListDtos;

        // 为每个规则项加载审计结果
        for (const item of checkResultList.value) {
            console.log('taskId.id = ' + taskId.id)
            const resultRes = await getAuditResultByRuleId(item.id, sceneId.value , taskId.value);
            item.auditResults = resultRes.data.map(r => ({
                ...r,
                showDetails: false
            }));
        }

        // 更新复选框选项，只保留有结果的类型
        checkList.value = checkList.value.filter(value =>
            availableCheckboxOptions.value.some(opt => opt.value === value)
        );
    } catch (error) {
        console.error('Error fetching scene data:', error);
    }
};



// 导出审核报告
const handleGenDocxReport = () => {
    pdfTitle.value = '审核报告预览'
    showPdfDialog.value = true;
    loadingDocument.value = true;

    genDocxReport(sceneId.value, taskId.value).then(res => {
        const storegeId = res.data;
        currentStoreId.value = storegeId;

        nextTick(async () => {
            try {
                const response = await getPreviewReportDocx(storegeId, taskId.value);
                iframeUrl.value = window.URL.createObjectURL(response);
                loadingDocument.value = false;
            } catch (error) {
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
    try {
        downloadMarkDocx(sceneId.value, taskId.value).then(res => {
            proxy.download(res.data, {}, `${currentSceneInfo.value.documentName}_${new Date().getTime()}.docx`)
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

const handlePointTo = (item) => {
    // 实现定位功能
    console.log('定位到:', item);
}

onMounted(() => {
    handleGetScene();
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss';

.review-result-container {
    margin-top: 20px;
}

:deep(.el-collapse) {
    border: none;
}

:deep(.el-collapse-item__header) {
    padding: 10px;
    background: #f5f5f5;
    border-radius: 5px;
    margin-bottom: 10px;
    font-size: 14px;
    border: none;

    &:hover {
        background: #e5e5e5;
    }
}

:deep(.el-collapse-item__content) {
    padding-bottom: 0;
}

.result-item {
    margin-bottom: 15px;
    border: 1px solid #e6e6e6;
    border-radius: 5px;
    overflow: hidden;

    .result-header {
        padding: 10px 15px;
        background: #fafafa;
        display: flex;
        align-items: center;
        cursor: pointer;

        &:hover {
            background: #f5f5f5;
        }
    }

    .result-details {
        padding: 15px;
        background: #f9f9f9;

        .detail-section {
            margin-bottom: 10px;

            b {
                display: block;
                margin-bottom: 5px;
            }

            p {
                margin: 0;
                padding: 8px;
                background: white;
                border-radius: 4px;
                border: 1px solid #eee;
            }
        }

        .action-buttons {
            text-align: right;
            margin-top: 10px;
        }
    }
}

.word-download-button {
    margin-top: 20px;
    margin-bottom: 20px;
    text-align: right;
}

// 风险等级样式
.item-alert-high {
    :deep(.el-collapse-item__header) {
        border-left: 3px solid #f56c6c;
    }
}

.item-alert-medium {
    :deep(.el-collapse-item__header) {
        border-left: 3px solid #e6a23c;
    }
}

.item-alert-low {
    :deep(.el-collapse-item__header) {
        border-left: 3px solid #909399;
    }
}

.item-alert-passed {
    :deep(.el-collapse-item__header) {
        border-left: 3px solid #67c23a;
    }
}
</style>