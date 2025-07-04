<template>
    <div class="review-checklist-container">
        <div class="header">
            审查清单：智能生成
        </div>
        <div class="rule-section">
            <div class="rule-title">
                全部规则({{ contractReviewList.length }})
            </div>
            <el-scrollbar class="scrollable-area">
                <div class="checkbox-group-container">
                    <el-checkbox-group v-model="checkList" style="display: flex;flex-direction: column;">
                        <el-checkbox v-for="(item , index) in contractReviewList" :key="item.id" :label="item.id" size="large">
                            <template #default>
                                {{index+1}}.{{item.ruleName}}
                            </template>
                        </el-checkbox>
                    </el-checkbox-group>
                </div>
            </el-scrollbar>
            <div class="button-section">
                <el-button type="primary" style="width:100%" size="large" @click="submitAuditForm" >
                    <i class="fa-solid fa-rocket"></i> 发起文档审核
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

const handleGetScene = () => {
    getScene(sceneId.value , taskId.value).then(res => {
        currentSceneInfo.value = res.data;
        contractReviewList.value = res.data.reviewListDtos;

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

onMounted(() => {
    handleGetScene();
});

</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss';
</style>