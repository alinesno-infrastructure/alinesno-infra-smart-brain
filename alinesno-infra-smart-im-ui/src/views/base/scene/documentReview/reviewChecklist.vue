<template>
    <div class="review-checklist-container">
        <div class="review-checklist-header">
            审查清单：智能生成
        </div>
        <div class="review-checklist-rule-section">
            <div class="review-checklist-rule-title">
                全部规则({{ contractReviewList.length }})
                <div class="check-actions">
                    <el-button size="small" type="primary"
                        @click="selectAll"
                        :disabled="checkList.length === contractReviewList.length"
                    ><i class="fa-solid fa-list-check"
                        
                    ></i> &nbsp; 全选</el-button>
                    <el-button size="small" type="warning"
                        @click="cancelSelection"
                        :disabled="checkList.length === 0"
                    > <i class="fa-solid fa-bars"></i> &nbsp;  取消</el-button>
                </div>
            </div>
            <el-scrollbar class="scrollable-area review-checklist-scrollable-area" >
                <div class="review-checklist-checkbox-group">
                    <el-checkbox-group v-model="checkList" size="large">
                        <el-checkbox v-for="(item , index) in contractReviewList" :key="item.id" :label="item.id" size="large">
                            <div class="review-checklist-item">
                                {{index+1 < 10?'0' + (index +1): index}}
                                <div class="review-checklist-item-content">
                                    <span class="review-checklist-item-title">
                                        {{item.ruleName}}
                                    </span>
                                    <span class="review-checklist-item-desc">
                                        {{ truncateString(item.ruleContent,38)}}
                                    </span>
                                </div>
                            </div>
                        </el-checkbox>
                    </el-checkbox-group>
                </div>
            </el-scrollbar>
            <div class="review-checklist-button-section">
                <el-button type="primary" size="large" @click="submitAuditForm">
                    <i class="fa-solid fa-rocket"></i> 发起文档审核 
                    <span style="font-weight:bold;" v-if="checkList.length > 0">  
                        （选择审核规则{{checkList.length}}条）
                    </span>
                </el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { 
    getScene, 
    genAuditResult 
} from '@/api/base/im/scene/documentReview';

import { useRoute } from 'vue-router';
import { ElLoading, ElMessage } from 'element-plus';
import { ref, onMounted } from 'vue';

const emit = defineEmits(['handleStepClick' , 'genSingleChapterContent'])

const streamLoading = ref(null);
const route = useRoute();

const currentSceneInfo = ref(null);
const sceneId = ref(route.query.sceneId);
const taskId = ref(route.query.taskId);
const channelStreamId = ref(route.query.channelStreamId);
const auditId = ref(null)

const contractReviewList = ref([]);
const checkList = ref([]);

const selectAll = () => { 
  checkList.value = contractReviewList.value.map(item => item.id);
  console.log('checkList.value = ' + checkList.value)
};

const cancelSelection = () => { 
  checkList.value = [];
};

const handleGetScene = () => {
    getScene(sceneId.value , taskId.value).then(res => {
        currentSceneInfo.value = res.data; 
        contractReviewList.value = res.data.reviewListDtos || [] ;

        if(currentSceneInfo.value.reviewListOption === 'aigen'){
            auditId.value = 0 ; 
        }else if(currentSceneInfo.value.reviewListOption === 'dataset'){
            auditId.value = currentSceneInfo.value.auditId ;
        }

        // 将所有选项的 ruleName 赋值给 checkList 实现全选
        // checkList.value = contractReviewList.value.map(item => item.id);
    });
};

const submitAuditForm = async () => {

    if(checkList.value.length == 0){
        ElMessage.warning("发起审核之前请先选择审核规则")
        return ; 
    }

    // 获取所有选择的id
    const selectedIds = checkList.value;
    const totalCount = selectedIds.length;

    // 开始生成
    streamLoading.value = ElLoading.service({
        lock: true,
        background: 'rgba(255, 255, 255, 0.4)',
        customClass: 'custom-loading'
    });

    emit('genSingleChapterContent', currentSceneInfo.value.logicReviewerEngineer);

    // 遍历选择的id列表，依次调用genAuditResult方法
    for (let index = 0; index < selectedIds.length; index++) {
        const id = selectedIds[index];
        const ruleName = contractReviewList.value.find(item => item.id === id).ruleName;
        console.log('ruleName = ' + ruleName);

        const currentCount = index + 1;
        const remainingCount = totalCount - currentCount;
        const text = `正在进行[${ruleName}]检查，当前是第${currentCount}条，还剩${remainingCount}条`;
        streamLoading.value.setText(text);

        const data = {
            sceneId: sceneId.value,
            channelStreamId: channelStreamId.value,
            taskId: taskId.value,
            ruleId: id,
            auditId: auditId.value,
            roleId: currentSceneInfo.value.logicReviewerEngineer
        };

        try {
            const res = await genAuditResult(data);
            console.log('res = ' + res);
        } catch (error) {
            console.error(`处理规则 ${ruleName} (ID: ${id}) 时出错:`, error);
            // 可以选择继续处理下一个，或者根据错误类型决定是否继续
            // 如果需要特定错误才忽略，可以在这里添加条件判断
            continue;
        }
    }

    streamLoading.value.close();

    emit('handleStepClick', 3);
};

const setCurrentTaskInfo = (info) => {
    currentSceneInfo.value = info; 
        contractReviewList.value = info.reviewListDtos || [] ;

        if(currentSceneInfo.value.reviewListOption === 'aigen'){
            auditId.value = 0 ; 
        }else if(currentSceneInfo.value.reviewListOption === 'dataset'){
            auditId.value = currentSceneInfo.value.auditId ;
        }
}

onMounted(() => {
    handleGetScene();
});

defineExpose({
  setCurrentTaskInfo,
  handleGetScene
})

</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss';

.review-checklist-container {
    display: flex;
    flex-direction: column;
    height: 100%;
}

.review-checklist-header {
    font-size: 15px;
    font-weight: bold;
    margin-bottom: 10px;
    margin-top: 10px;
}

.review-checklist-rule-section {
    display: flex;
    flex-direction: column;
    flex: 1;
}

.review-checklist-rule-title {
margin-bottom: 15px;
    padding: 10px 10px;
    border-radius: 5px;
    background: #f5f5f5;
    font-size: 14px;

        display: flex;
    justify-content: space-between;
    align-items: center;
}

.review-checklist-scrollable-area { 
    margin-bottom: 20px;
    height:calc(100vh - 290px);
}

.review-checklist-checkbox-group {
    display: flex;
    flex-direction: column;
    
    .el-checkbox-group {
        display: flex;
        flex-direction: column;
    }
    
    .el-checkbox {
        height: auto;
        margin-bottom: 10px;
    }
}

.review-checklist-item {
    display: flex;
    gap: 10px;
    align-items: center;
    width: 100%;
    flex-direction: row;
}

.review-checklist-item-content {
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding: 10px 15px 10px;
    border-radius: 5px;
    background: #f5f5f5;
}

.review-checklist-item-title {
    font-size: 15px;
}

.review-checklist-item-desc {
    font-size: 13px;
    color: #a5a5a5;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: inline-block;
    max-width: 100%;
}

.review-checklist-button-section .el-button {
    width: 100%;
}

.check-actions {
    display: flex;
    gap: 8px;
    
    .el-button {
        padding: 6px 12px;
        font-size: 12px;
        
        i {
            margin-right: 4px;
        }
    }
}

</style>