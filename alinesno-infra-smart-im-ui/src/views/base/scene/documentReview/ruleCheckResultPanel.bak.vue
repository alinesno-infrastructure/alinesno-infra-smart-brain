<template>
    <el-drawer v-model="drawer" :direction="direction" size="40%" :with-header="false" :before-close="handleClose">
        <div class="drawer-content">
            <div class="drawer-header">
                规则【{{ ruleItem.ruleName || '未命名' }}】检查结果
                <el-button type="text" size="large" @click="closeDrawer(ruleItem)">关闭</el-button>
            </div>

            <div class="drawer-body">
                <el-scrollbar class="scrollable-area">
                    <div v-for="(item, index) in auditResultList" :key="index">
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
                </el-scrollbar>
            </div>
        </div>
    </el-drawer>
</template>

<script setup>
import { ref } from 'vue';

import { getAuditResultByRuleId } from '@/api/base/im/scene/documentReview';

const drawer = ref(false);
const direction = ref('rtl');
const ruleItem = ref(null);
const checkRuleResult = ref([]);
const auditResultList = ref([]);
const closeDrawer = (item) => {
    console.log('closeDrawer', item);
    drawer.value = false;
};

const handleClose = () => {
    console.log('handleClose');
    drawer.value = false;
};

const openDrawerAndSettings = (item) => {
    console.log('openDrawerAndSettings', item);
    drawer.value = true;
    ruleItem.value = item;

    getAuditResultByRuleId(item.ruleId, item.sceneId).then((res) => {
        console.log('getAuditResultByRuleId', res);
        auditResultList.value = res.data;
    });

};

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

// 获取标签颜色
const getTagType = () => {
    switch (ruleItem.value?.riskLevel) {
        case 'low':
            return 'info';
        case'medium':
            return 'warning';
        case 'high':
            return 'danger';
        default:
            return'success';
    }
};

defineExpose({
    openDrawerAndSettings
});
</script>

<style lang="scss" scoped>

@import '@/assets/styles/document-review.scss';

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

.drawer-content {
    // padding: 10px;
    height: calc(90vh);

   .drawer-header {
        font-size: 15px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-bottom: 10px;
        font-weight: bold;
        background-color: #fafafa ; 
        border-radius: 5px;;
        padding: 5px 10px;
    }
   .drawer-body {
       .scrollable-area {
            height: calc(100vh - 80px);
            padding-right: 0px;
           .rule-result-item {
                margin-top: 10px;
                background: #393838;
                padding: 10px;
                border-radius: 3px;
               .risk-point {
                    background: rgb(38, 39, 39);
                    padding: 10px;
                    border-radius: 5px;
                    display: flex;
                    font-size: 14px;
                    justify-content: space-between;
                    align-items: center;
                }
               .contract-original {
                    margin-top: 10px;
                    padding: 10px;
                    font-size: 14px;
                    background: #262727;
                    border-radius: 5px;
                    display: flex;
                    gap: 10px;
                    flex-direction: column;
                   .contract-original-title {
                        font-size: 15px;
                        font-weight: bold;
                    }
                }
               .modify-suggestion {
                    margin-top: 10px;
                    padding: 10px;
                    background: #262727;
                    border-radius: 5px;
                    font-size: 14px;
                    gap: 10px;
                    display: flex;
                    flex-direction: column;
                   .modify-suggestion-title {
                        font-size: 15px;
                        font-weight: bold;
                    }
                }
               .accept-suggestion {
                    margin-top: 10px;
                    text-align: left;
                }
            }
        }
    }
}
</style>