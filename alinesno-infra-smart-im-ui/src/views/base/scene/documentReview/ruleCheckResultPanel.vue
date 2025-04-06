<template>
    <el-drawer v-model="drawer" :direction="direction" :with-header="false" :before-close="handleClose">
        <div class="drawer-content">
            <div class="drawer-header">
                <el-button :type="getTagType()" text size="default">
                    规则【{{ ruleItem.ruleTitle }}】检查结果
                </el-button>
                <el-button type="primary" text bg size="default" @click="closeDrawer(ruleItem)">关闭</el-button>
            </div>

            <div class="drawer-body">
                <el-scrollbar class="scrollable-area">
                    <div class="rule-result-item" v-for="(item, index) in ruleItem.checkRuleResult">
                        <div class="risk-point">
                            <span>风险点{{ index + 1 }}: {{ item.reason }}</span>
                            <span>
                                <el-button type="primary" text bg size="small">
                                    <i class="fa-solid fa-map-location"></i> &nbsp; 定位到原文
                                </el-button>
                            </span>
                        </div>
                        <div class="contract-original">
                            <div class="contract-original-title"> 文档原文 </div>
                            <div> {{ item.originalText }} </div>
                        </div>
                        <div class="modify-suggestion">
                            <div class="modify-suggestion-title"> 修改建议 </div>
                            <!-- <div> {{ item.suggestedTexts }} </div> -->
                            <div>
                                <div v-for="(i , ix ) in item.suggestedTexts" style="display: flex;justify-content: space-between;align-items: center;margin-bottom: 5px;" >
                                    <span>
                                       {{ ix +1 }}:{{ i.text }}
                                    </span>
                                    <el-button type="primary" text bg size="small">接受</el-button>
                                </div>
                            </div>
                        </div>
                        <!-- 
                        <div class="accept-suggestion">
                            <el-button type="primary" size="default">接受建议并确认(会添加到批注里面)</el-button>
                        </div> 
                        -->
                    </div>
                </el-scrollbar>
            </div>
        </div>
    </el-drawer>
</template>

<script setup>
import { ref } from 'vue';

const drawer = ref(false);
const direction = ref('rtl');
const ruleItem = ref(null);

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
};

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
.drawer-content {
    // padding: 10px;

   .drawer-header {
        font-size: 14px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;
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