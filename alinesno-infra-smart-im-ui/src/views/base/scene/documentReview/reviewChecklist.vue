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

                <el-button v-if="props.currentTaskInfo?.resultGenStatus != 'success'" type="primary" size="large" @click="submitAuditForm('check')">
                    <i class="fa-solid fa-rocket"></i> 发起文档审核 
                    <span style="font-weight:bold;" v-if="checkList.length > 0">  
                        （选择审核规则{{checkList.length}}条）
                    </span>
                </el-button>
            
                <el-button v-if="props.currentTaskInfo?.resultGenStatus == 'success'" type="primary" size="large" @click="submitAuditForm('reCheck')">
                    <i class="fa-solid fa-repeat"></i> 重新发起审核
                    <span style="font-weight:bold;" v-if="checkList.length > 0">  
                        （选择审核规则{{checkList.length}}条）
                    </span>
                </el-button>

                <el-button :disabled="props.currentTaskInfo?.resultGenStatus != 'success'" type="success" size="large" @click="showResult()">
                    <i class="fa-brands fa-phoenix-framework"></i> 查看审核结果
                </el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
import {  
    genAuditResult 
} from '@/api/base/im/scene/documentReview';

import { useRoute } from 'vue-router';
import { ElLoading, ElMessage } from 'element-plus';
import { ref, onMounted } from 'vue';

const emit = defineEmits([
    'handleStepClick', 
    'genSingleChapterContent', 
    'closeGeneratorStatus',
    'generatorStatus'])
  
const props = defineProps({
  currentSceneInfo: {
    type: Object, 
    required: false 
  },
  currentTaskInfo: {
    type: Object, 
    required: false 
  }
})    

const streamLoading = ref(null);
const route = useRoute();

const currentSceneInfo = ref(null);
const sceneId = ref(route.query.sceneId);
const taskId = ref(route.query.taskId);
const channelStreamId = ref(route.query.channelStreamId);
const auditId = ref(null)
const currentTaskInfo = ref(null);

const contractReviewList = ref([]);
const checkList = ref([]);

// 定义轮询相关变量
const pollingInterval = ref(null); // 轮询定时器引用
const pollingIntervalTime = 10000; // 轮询间隔时间设为10秒

const selectAll = () => { 
  checkList.value = contractReviewList.value.map(item => item.id);
  console.log('checkList.value = ' + checkList.value)
};

const cancelSelection = () => { 
  checkList.value = [];
};
 
const submitAuditForm = async () => {

    if(checkList.value.length == 0){
        ElMessage.warning("发起审核之前请先选择审核规则")
        return ; 
    }

    // 获取所有选择的id
    const selectedIds = checkList.value;
    const totalCount = selectedIds.length;
  
        const data = {
            sceneId: sceneId.value,
            channelStreamId: channelStreamId.value,
            taskId: taskId.value,
            ruleIds: selectedIds,
            auditId: auditId.value,
            roleId: props.currentSceneInfo.logicReviewerEngineer
        };

        try {
            const res = await genAuditResult(data);
            if(res.code != 200){
                ElMessage.error(res.msg);
                return ;
            }

            emit('genSingleChapterContent', props.currentSceneInfo.logicReviewerEngineer);
            emit('generatorStatus' , '正在进行检查中.'); 

            console.log('res = ' + res);
        } catch (error) {
            console.error(`处理规则时出错:`, error); 
        }

};
 
const showResult = () => {
    emit('handleStepClick', 3);
}

watch(
  () => props.currentSceneInfo,
  (newValue, oldValue) => { 
    if (newValue) {  
      contractReviewList.value = newValue.reviewListDtos || [] ;

      if(newValue.reviewListOption === 'aigen'){
        auditId.value = 0 ; 
      }else if(newValue.reviewListOption === 'dataset'){
        auditId.value = newValue.auditId ;
      }
    }
  },
  { deep: true, immediate: true }
);
 
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
    width: 49%;
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