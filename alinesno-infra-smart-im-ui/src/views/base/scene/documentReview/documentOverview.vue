<template>
    <div >

        <el-scrollbar class="contract-review-container" style="height:calc(100vh - 190px)" >
        <div class="review-methods-container">
            <div class="review-methods-title">审查方式</div> 
            <div class="form-container">
                <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" label-width="120px" :label-position="'top'" size="large">
                    <el-form-item label="合同类型" prop="contractType">
                        <el-select v-model="ruleForm.contractType">
                            <el-option v-for="item in contractTypeList" :key="item.scenarioId" :label="item.title"
                                :value="item.scenarioId"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="选择审查立场" prop="reviewPosition">
                        <el-radio-group v-model="ruleForm.reviewPosition">
                            <el-radio  value="1" label="甲方立场">甲方立场</el-radio>
                            <el-radio  value="2" label="乙方立场">乙方立场</el-radio>
                            <el-radio  value="9" label="中立立场">中立立场</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="选择审查清单">
                        <el-radio-group v-model="ruleForm.reviewListOption" @change="handleReviewListOptionChange">
                            <el-radio value="aigen" label="AI智能生成" >
                              <i class="fa-solid fa-rocket"></i> &nbsp; AI智能生成
                            </el-radio>
                            <el-radio value="dataset" label="从知识库选择" >
                               <i class="fa-solid fa-train-subway"></i> &nbsp; 从知识库选择
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="选择审查清单" prop="reviewListOption" >
                        <el-select placeholder="选择审查清单" v-model="ruleForm.auditId" :disabled="ruleForm.reviewListOption!== 'dataset'">
                            <el-option v-for="item in scenarioList" 
                                :key="item.id" 
                                :label="item.auditName"
                                :value="item.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
            </div>
        </div>
 
        <div class="contract-overview-container" >
            <div class="contract-overview-title">合同概览</div>
            <div class="contract-info-container">
                <div 
                    class="contract-info-item" 
                    v-for="(item, index) in contractMetadataMap" 
                    :key="index">
                    <div class="contract-info-label">{{ getLabel(item) }}</div>
                    <div class="contract-info-value">
                    {{ getValue(item) }}
                    </div>
                </div>
            </div>
        </div>

        </el-scrollbar>

        <div class="gen-overview-container" style="margin-top: 10px;"> 
            <el-button style="width:100%" 
                v-if="ruleForm.reviewListOption == 'aigen' && reviewListDtos.length == 0" 
                type="primary" 
                size="large" 
                @click="submitForm">
                <i class="fa-solid fa-robot"></i> &nbsp; AI生成审查清单
            </el-button>

            <el-button style="width:100%" 
                v-if="ruleForm.reviewListOption == 'aigen' && reviewListDtos.length > 0" 
                type="primary" 
                size="large" 
                @click="enterReviewList">
                <i class="fa-solid fa-robot"></i> &nbsp; 查看AI生成的审查清单
            </el-button>

            <el-button style="width:100%" 
                v-if="ruleForm.reviewListOption == 'dataset'" 
                type="primary" 
                size="large" 
                @click="submitForm">
                <i class="fa-solid fa-paper-plane"></i> &nbsp; 查看审查清单
            </el-button>
        </div>

    </div>
</template>

<script setup>

import { ref, reactive } from 'vue';
import { ElLoading } from 'element-plus';
import { getScene } from '@/api/base/im/scene/documentReview';


import {  
    genReviewListByDataset,
    getAuditList 
} from '@/api/base/im/scene/documentReviewSceneInfo';

import {
  genReviewList , 
  getReviewTask 
} from '@/api/base/im/scene/documentReviewTask';

const route = useRoute();

const channelStreamId = ref(route.query.channelStreamId);
const currentSceneInfo = ref({});

const ruleFormRef = ref();
const sceneId = ref(route.query.sceneId)
const taskId = ref(route.query.taskId)
const streamLoading = ref(null)

const contractMetadataMap = ref([])
const reviewListDtos = ref([]);
const currentTaskInfo = ref(null);
 
const emit = defineEmits([
    'handleStepClick', 
    'genSingleChapterContent', 
    'closeGeneratorStatus',
    'generatorStatus'])

// 定义轮询相关变量
const pollingInterval = ref(null); // 轮询定时器引用
const pollingIntervalTime = 10000; // 轮询间隔时间设为10秒

// 定义表单数据
const ruleForm = reactive({
    contractType: '73',
    reviewPosition: '1',
    reviewListOption: 'aigen',
    reviewListKnowledgeBase: '',
});

// 知识库选择列表数据
const contractTypeList = ref([]);

// 知识库选择列表数据，优化场景名称
const scenarioList = ref([]);

// 定义表单验证规则
const rules = reactive({
    contractType: [
        { required: true, message: '请输入合同类型', trigger: 'blur' }
    ],
    reviewPosition: [
        { required: true, message: '请选择审查立场', trigger: 'change' }
    ],
    reviewListOption: [
        { required: true, message: '请选择审查清单选项', trigger: 'change' },
        {
            validator: (rule, value, callback) => {
                if (value === '从知识库选择' && !ruleForm.selectedScenario) {
                    callback(new Error('从知识库选择时，请选择具体的列表项'));
                } else {
                    callback();
                }
            },
            trigger: 'change'
        }
    ],
    selectedScenario: [
        {
            validator: (rule, value, callback) => {
                if (ruleForm.reviewListOption === '从知识库选择' && !value) {
                    callback(new Error('请选择具体的场景'));
                } else {
                    callback();
                }
            },
            trigger: 'change'
        }
    ] , 
    auditId: [
        { required: true, message: '请选择知识库', trigger: 'change' }
    ]
});

// 进入AI生成的列表界面
const enterReviewList = () => {
    emit('handleStepClick', 2);
}

// 提交表单方法
const submitForm = async () => {
    const formEl = ruleFormRef.value;
    if (!formEl) return;
    await formEl.validate((valid) => {
        if (valid) {
            console.log('submit!', ruleForm);

            // 如果是数据集
            if(ruleForm.reviewListOption == 'dataset'){

                ruleForm.sceneId = currentSceneInfo.value.id ;
                ruleForm.taskId = taskId.value ; // currentSceneInfo.value.id ;
                ruleForm.channelStreamId = channelStreamId.value ;

                genReviewListByDataset(ruleForm).then(res => {
                    emit('handleStepClick', 2);
                })

            }else if(ruleForm.reviewListOption == 'aigen'){

                ruleForm.sceneId = currentSceneInfo.value.id ;
                ruleForm.taskId = taskId.value ; // currentSceneInfo.value.id ;
                ruleForm.channelStreamId = channelStreamId.value ;
 
                if(ruleForm.reviewListOption == 'aigen'){
                    let text = '正在使用AI生成校验规则内容，请稍等.'; 
                    emit('genSingleChapterContent', currentSceneInfo.value.analysisAgentEngineer) ; 
                    emit('generatorStatus' , text);
                }

                genReviewList(ruleForm).then(res => {
                    console.log('res = ' + res.data);

                    // 生成成功，开始轮询检查状态
                    startPolling();

                    // if(res.data != 'success'){
                    //     ElMessage.warning("未生成审核清单.")
                    //     return ;
                    // }
                    // emit('handleStepClick', 2);
                    // generatingStatusRef.value.close();
                })
            }

        } else {
            console.log('error submit!');
        }
    });
};

// 重置表单方法
const resetForm = () => {
    const formEl = ruleFormRef.value;
    if (!formEl) return;
    formEl.resetFields();
};

// 提取重复逻辑到一个单独的函数
const fetchSceneData = async () => {
  const res = await getScene(sceneId.value, taskId.value);
  currentSceneInfo.value = res.data;
  contractTypeList.value = res.data.contractTypes; 
  reviewListDtos.value = res.data.reviewListDtos || [];
  
  if (currentSceneInfo.value) { 
    if (currentSceneInfo.value.contractType) {
      ruleForm.contractType = currentSceneInfo.value.contractType; 
    } 
    if (currentSceneInfo.value.reviewPosition) {
      ruleForm.reviewPosition = currentSceneInfo.value.reviewPosition;
    } 
    if (currentSceneInfo.value.reviewListOption) {
      ruleForm.reviewListOption = currentSceneInfo.value.reviewListOption;
    } 
    if (currentSceneInfo.value.auditId) {
      ruleForm.auditId = currentSceneInfo.value.auditId;
    } 
    
    handleReviewListOptionChange();
  }
  return res;
};

const handleGetScene = async () => {
    await fetchSceneData();
    startPolling();
}

const handleGetAuditList = () => {
    getAuditList(sceneId.value).then(res => {
        console.log('res = ' + res);
        scenarioList.value = res.data;
    })
}

const handleReviewListOptionChange = () => {
    if (ruleForm.reviewListOption === 'dataset') {
        handleGetAuditList();
    }
}

// 获取显示标签
const getLabel = (item) => {
  return Object.keys(item).find(key => key !== 'key');
};

// 获取显示值
const getValue = (item) => {
  const key = Object.keys(item).find(key => key !== 'key');
  return item[key];
};

// 获取审查任务详情，并检查AI生成状态
const handleGetReviewTask = async() => {
    try {
        // 调用API获取任务详情
        const res = await getReviewTask(taskId.value);
        currentTaskInfo.value = res.data;
        contractMetadataMap.value = res.data.contractMetadataMap || [];
        
        // 检查生成状态是否已完成
        if (currentTaskInfo.value?.reviewGenStatus === 'success') {
            stopPolling(); // 停止轮询
            // emit('handleStepClick', 2); // 跳转到下一步   
            emit('closeGeneratorStatus')
            await fetchSceneData(); // 使用提取的函数
        }else if(currentTaskInfo.value?.reviewGenStatus === 'generating'){ // 生成中
            let text = '正在使用AI生成校验规则内容，请稍等.' ; //  + currentSceneInfo.value.analysisAgentEngineer; 
            emit('genSingleChapterContent', currentSceneInfo.value.analysisAgentEngineer) ; 
            emit('generatorStatus' , text);
        }
    } catch (error) {
        console.error('获取审查任务出错:', error);
        stopPolling(); // 出错时也停止轮询
    }
};

// 开始轮询函数
const startPolling = () => {
    // 先清除可能存在的旧定时器
    stopPolling();
    
    // 设置新的定时器，每隔10秒检查一次状态
    pollingInterval.value = setInterval(handleGetReviewTask, pollingIntervalTime);
    
    // 立即执行一次检查，不用等待第一个间隔
    handleGetReviewTask();
};

// 停止轮询函数
const stopPolling = () => {
    if (pollingInterval.value) {
        clearInterval(pollingInterval.value); // 清除定时器
        pollingInterval.value = null; // 重置引用
    }
};

onMounted(() => {
    handleGetScene(); 
});

// 组件卸载时清除定时器
onUnmounted(() => {
    stopPolling(); // 确保组件销毁时清理定时器
});

 
</script>

<style lang="scss" scoped>
    .contract-review-container {

        padding: 10px;
        background-color: #f5f5f5;
        margin-top: 10px;
        border-radius: 5px;

        .review-methods-container {
            padding: 10px;
            background: #fff;
            border-radius: 5px; 
            .review-methods-title {
                font-size: 14px;
                background: #f5f5f5;
                border-radius: 5px;
                padding: 10px;
            }
            .form-container {
                padding: 10px;
                border-radius: 5px;
            }
        }
        .contract-overview-container {
            padding: 10px;
            background: #fff;
            border-radius: 5px;
            margin-top: 10px;
            .contract-overview-title {
                font-size: 14px;
                background: #f5f5f5;
                border-radius: 5px;
                padding: 10px;
            }
            .contract-info-container {
                border-radius: 5px;
                .contract-info-item {
                    padding: 10px;
                    border-radius: 5px; 
                    font-size: 14px;
                    display: flex;
                    flex-direction: column;
                    gap: 10px;
                    .contract-info-label {
                        font-weight: bold;
                        color:#444;
                    }
                }
            }
        }

        .contract-info-value{
            color: #555 ;
            padding: 5px 10px;
            line-height: 1.6rem;
                background: #f5f5f5;
    border-radius: 5px;
        }
    }
</style>