<template>
    <div class="contract-review-container">
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
                            <el-radio border value="1" label="甲方立场">甲方立场</el-radio>
                            <el-radio border value="2" label="乙方立场">乙方立场</el-radio>
                            <el-radio border value="9" label="中立立场">中立立场</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="选择审查清单">
                        <el-radio-group v-model="ruleForm.reviewListOption" @change="handleReviewListOptionChange">
                            <el-radio value="aigen" label="AI智能生成" border>
                              <i class="fa-solid fa-rocket"></i> &nbsp; AI智能生成
                            </el-radio>
                            <el-radio value="dataset" label="从知识库选择" border>
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

        <div class="gen-overview-container" style="margin-top: 10px;">
            <el-button style="width:100%" 
                v-if="ruleForm.reviewListOption == 'aigen'" 
                type="primary" 
                size="large" 
                @click="submitForm">
                <i class="fa-solid fa-paper-plane"></i> &nbsp; 生成审查清单
            </el-button>

            <el-button style="width:100%" 
                v-if="ruleForm.reviewListOption == 'dataset'" 
                type="primary" 
                size="large" 
                @click="submitForm">
                <i class="fa-solid fa-paper-plane"></i> &nbsp; 查看审查清单
            </el-button>
        </div>

        <div class="contract-overview-container" v-if="currentSceneInfo.contractOverview">
            <div class="contract-overview-title">合同概览</div>
            <div class="contract-info-container">
                <div class="contract-info-item">
                    <div class="contract-info-label">内容概览</div>
                    <div class="contract-info-value">
                        {{ currentSceneInfo.contractOverview }}
                    </div>
                </div>
            </div>
        </div>


    </div>
</template>

<script setup>

import { ref, reactive } from 'vue';
import { ElLoading } from 'element-plus';
import { getScene } from '@/api/base/im/scene/documentReview';
import { 
    genReviewList , 
    genReviewListByDataset,
    getAuditList 
} from '@/api/base/im/scene/documentReviewSceneInfo';

const route = useRoute();

const channelStreamId = ref(route.query.channelStreamId);
const currentSceneInfo = ref({});
// const formSize = ref('default');
const ruleFormRef = ref();
const sceneId = ref(route.query.sceneId)
const taskId = ref(route.query.taskId)
const streamLoading = ref(null)

const emit = defineEmits(['handleStepClick' , 'genSingleChapterContent'])

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

                streamLoading.value = ElLoading.service({
                    lock: true,
                    background: 'rgba(255, 255, 255, 0.4)',
                    customClass: 'custom-loading'
                });

                if(ruleForm.reviewListOption == 'aigen'){
                    let text = '正在使用AI生成校验规则内容，请稍等.';
                    streamLoading.value.setText(text)
                    emit('genSingleChapterContent', currentSceneInfo.value.analysisAgentEngineer) ; 
                }

                genReviewList(ruleForm).then(res => {
                    console.log('res = ' + res);
                    emit('handleStepClick', 2);
                    streamLoading.value.close();
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

const handleGetScene = () => {
    getScene(sceneId.value , taskId.value).then(res => {
        currentSceneInfo.value = res.data;
        contractTypeList.value = res.data.contractTypes;

        if(currentSceneInfo.value){

            if(currentSceneInfo.value.contractType){
                ruleForm.contractType = currentSceneInfo.value.contractType ; 
            }

            if(currentSceneInfo.value.reviewPosition){
                ruleForm.reviewPosition = currentSceneInfo.value.reviewPosition ;
            }

            if(currentSceneInfo.value.reviewListOption){
                ruleForm.reviewListOption = currentSceneInfo.value.reviewListOption ;
            }

            if(currentSceneInfo.value.auditId){
                ruleForm.auditId = currentSceneInfo.value.auditId ;
            }

            handleReviewListOptionChange();
        }

    })
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

onMounted(() => {
    handleGetScene();
})

</script>

<style lang="scss" scoped>
    .contract-review-container {
        .review-methods-container {
            padding: 10px;
            background: #fff;
            border-radius: 5px;
            margin-top: 10px;
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
                .contract-info-item {
                    padding: 10px;
                    border-radius: 5px;
                    margin-top: 15px;
                    font-size: 14px;
                    display: flex;
                    flex-direction: column;
                    gap: 10px;
                    .contract-info-label {
                        font-weight: bold;
                    }
                }
            }
        }
    }
</style>