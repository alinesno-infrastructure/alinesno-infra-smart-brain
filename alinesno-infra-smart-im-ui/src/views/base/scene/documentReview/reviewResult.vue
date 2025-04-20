<template>
    <div class="review-result-container">
        <div style="display: flex;flex-direction: row;justify-content: space-between;">
            <div>
                <el-button type="primary" text bg>
                    <i class="fa-solid fa-download"></i> &nbsp; 导出审核报告
                </el-button>
                <el-button type="primary" text bg>
                    <i class="fa-solid fa-file-word"></i> &nbsp; 导出标注文档
                </el-button>
            </div>
            <div>
                <el-checkbox-group v-model="checkList">
                    <el-checkbox type="danger" v-for="item in checkboxOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-checkbox-group>
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

    </div>
</template>

<script setup>

import { getSceneResultList } from '@/api/base/im/scene/documentReview';

const route = useRoute();
const currentSceneInfo = ref(null);
const sceneId = ref(route.query.sceneId)

import RuleCheckResultPanel from './ruleCheckResultPanel.vue'

const ruleTitle = ref('');

const contractReviewList = ref([]);
const ruleCheckResultPanelRef = ref(null);

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
    getSceneResultList(sceneId.value).then(res => {
        currentSceneInfo.value = res.data;
        checkResultList.value = currentSceneInfo.value.reviewListDtos ;

        // 将所有选项的 ruleName 赋值给 checkList 实现全选
        // checkList.value = contractReviewList.value.map(item => item.id);

        // checkResultList.value = currentSceneInfo.value.reviewListDtos ;
    })
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
</style>
