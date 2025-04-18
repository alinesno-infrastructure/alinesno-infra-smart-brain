<template>
    <div class="review-result-container">
        <div style="display: flex;flex-direction: row;justify-content: space-between;">
            <div>
                <el-button type="primary" text bg @click="handleDownloadExcel()">
                    <i class="fa-solid fa-download"></i> &nbsp; 导出审核报告
                </el-button>
            </div>
            <div>
                <el-checkbox-group v-model="checkList">
                    <el-checkbox type="danger" v-for="item in checkboxOptions" :key="item.value" :label="item.label"
                        :value="item.value" />
                </el-checkbox-group>
            </div>
        </div>
        <div>
            <div style="margin-top:20px;">
                <div>
                    <el-scrollbar class="scrollable-area" style="height: calc(100vh - 210px);padding-right:10px">
                        <div v-for="(item, index) in filteredCheckResultList" :key="index">
                            <div class="check-result-item" :class="'item-alert-' + item.riskLevel"
                                @click="handleCheckItemClick(item)">
                                <div class="check-result-box" style="display: flex;gap: 5px;align-items: center;">
                                    <span>
                                        <el-button type="text" size="small" style="width:35px;">
                                            <i class="fa-solid fa-chevron-down" v-if="item.showItemContent"></i>
                                            <i class="fa-solid fa-chevron-right" v-if="!item.showItemContent" ></i>
                                        </el-button>
                                    </span>
                                    <span>
                                        <strong>
                                           {{ item.modificationReason }}
                                        </strong>
                                    </span>
                                    <el-tag :type="getTagType(item)" effect="dark">
                                        {{ item.riskLevel === 'high' ? '高危' : item.riskLevel === 'medium' ? '中危' : '低危' }}
                                    </el-tag>
                                </div>


                            </div>

                                <div class="check-result-item-content" v-if="item.showItemContent">
                                    <div class="content-panel">
                                        <div>
                                           <b>原文内容:</b> 
                                           <p>
                                            {{ item.originalContent }}
                                           </p> 
                                        </div>
                                        <div>
                                            <b>建议内容:</b> 
                                            <p>
                                            {{ item.suggestedContent }}
                                            </p>
                                        </div>
                                    </div>
                                    <div>
                                        <el-button type="primary" text bg size="default" @click="handlePointTo(item)">
                                            <i class="fa-solid fa-hand-pointer"></i>&nbsp;定位
                                        </el-button>
                                    </div>
                                </div>

                        </div>

                            <div v-if="filteredCheckResultList.length == 0">
                                <el-empty description="还没有校验结果">
                                    <el-button type="primary" text bg size="large" icon="Back" @click="onClickBack"> 返回概览</el-button>
                                </el-empty>
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

import { getScene , reviewCheckExpert } from '@/api/base/im/scene/contentFormatter';
import { ElLoading } from 'element-plus';

const emit = defineEmits(['handlePointTo' , 'handleStepClick']);

const route = useRoute();
const currentSceneInfo = ref(null);
const sceneId = ref(route.query.sceneId)

// import RuleCheckResultPanel from './ruleCheckResultPanel.vue'

const activeName = ref('1')
const ruleTitle = ref('');

const contractReviewList = ref([]);
const ruleCheckResultPanelRef = ref(null);

const checkboxOptions = [
    { "label": "高风险", "value": "high" },
    { "label": "中风险", "value": "medium" },
    { "label": "低风险", "value": "low" },
];

const checkList = ref([]);

const filteredCheckResultList = computed(() => {
    if (checkList.value.length === 0) {
        return checkResultList.value;
    } else {
        return checkResultList.value.filter(item => checkList.value.includes(item.riskLevel));
    }
});

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

    let toggleShow = item.showItemContent

    if (!item.showItemContent) {
        item.showItemContent = true;
        return;
    } else {
        toggleShow = !toggleShow;
    }

    item.showItemContent = toggleShow;

}

const handleGetScene = () => {
    getScene(sceneId.value).then(res => {
        currentSceneInfo.value = res.data;
        // checkResultList.value = res.data.checkResultListDtos ;

        // 将所有选项的 ruleName 赋值给 checkList 实现全选
        // checkList.value = contractReviewList.value.map(item => item.id);
    })
}

// 定位到对应位置
const handlePointTo = (item) => {
    emit('handlePointTo' , item)
}

const onClickBack = () => {
    emit('handleStepClick' , 1)
}

onMounted(() => {

//     handleGetScene();

    if(checkResultList.value.length == 0){
        if (localStorage.getItem('ruleContent')) {
            checkResultList.value = JSON.parse(localStorage.getItem('ruleContent'));
        }
    }
})
/** 下载模板操作 */
const handleDownloadExcel =() => {

    // 开始生成
    const streamLoading = ElLoading.service({
        lock: true,
        background: 'rgba(255, 255, 255, 0.4)',
        customClass: 'custom-loading'
    });

    let text = '正在生成导出内容.';
    streamLoading.setText(text)

    reviewCheckExpert(checkResultList.value).then(res => {
        streamLoading.close();
        if (res.code === 200) {
            window.open(res.data);
        }
    })
};

// 提供出方法，设置规则内容
const setRuleContent = (ruleContent) => {
    console.log('ruleContent = ' + ruleContent)
    checkResultList.value = ruleContent; 

    // 保存到localStorage
    localStorage.setItem('ruleContent' , JSON.stringify(ruleContent))
}

defineExpose({
    setRuleContent
})


</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss';

.review-result-container {
    margin-top: 20px;

    .check-result-item {
        padding: 15px;
        background: #fafafa;
        border-radius: 5px;
        margin-top: 10px;
        font-size: 14px;
        display: flex;
        justify-content: space-between;
        border: 1px solid #e6e6e6;
        align-items: center;
    }

}

.check-result-item-content {

    padding: 10px 15px;
    font-size: 14px;
    background: #f5f5f5;
    margin-top: 10px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .content-panel {
        display: flex;
        gap: 5px;
        flex-direction: column;
    }
}

.check-result-item {
    cursor: pointer;

    &:hover {
        background: #f5f5f5;
    }

}

.item-alert-low {
    color: #155724;
    background-color: #d4edda !important;
    border-color: #c3e6cb !important;
}

.item-alert-high {
    color: #721c24;
    background-color: #f8d7da !important;
    border-color: #f5c6cb !important;
}

.item-alert-medium {
    color: #856404;
    background-color: #fff3cd !important;
    border-color: #ffeeba !important;
}
</style>
