<template>
    <div class="contract-review-container">
        <div class="review-methods-container">
            <div class="review-methods-title">排版方式</div>
            <div class="form-container">
                <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" label-width="120px" :label-position="'top'" size="large">
                     <el-form-item label="选择文书类型" prop="documentType">
                        <el-select
                            v-model="ruleForm.documentType"
                            multiple
                            placeholder="请选择文书类型"
                            style="width: 100%"
                            >
                            <el-option
                                v-for="option in documentOptions" 
                                :key="option.value" 
                                :border="false" 
                                :value="option.value" 
                                :label="option.label"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="选择业务场景" prop="businessScenario">
                        <el-select
                            v-model="ruleForm.businessScenario"
                            multiple
                            placeholder="请选择业务场景"
                            style="width: 100%"
                            >
                            <el-option
                                v-for="option in businessOptions" 
                                :key="option.value" 
                                :border="false" 
                                :value="option.value" 
                                :label="option.label"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="选择排版清单">
                        <el-radio-group v-model="ruleForm.reviewListOption">
                            <el-radio value="aigen" label="AI智能生成" border :disabled="true">
                              <i class="fa-solid fa-rocket"></i> &nbsp; AI智能生成
                            </el-radio>
                            <el-radio value="dataset" label="从模板库选择" border>
                               <i class="fa-solid fa-train-subway"></i> &nbsp; 从模板库选择
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="选择排版模板" prop="templateId" >
                        <el-select placeholder="选择排版模板" 
                            v-model="ruleForm.templateId" 
                            :disabled="ruleForm.reviewListOption!== 'dataset'">
                            <el-option v-for="item in templateList" 
                                :key="item.id" 
                                :label="item.templateName"
                                :value="item.id">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-form>
            </div>
        </div>

        <div class="gen-overview-container" style="margin-top: 10px;">
            <el-button style="width:100%" 
                type="primary" 
                size="large" 
                @click="submitForm">
                <i class="fa-solid fa-paper-plane"></i> &nbsp; 开始内容解析 
            </el-button>
        </div>

        <div class="contract-overview-container">
            <div class="contract-overview-title">文档概览</div>
            <div class="contract-info-container">
                <div class="contract-info-item">
                    <div class="contract-info-label">内容概览</div>
                    <div class="contract-info-value">
                        {{ currentSceneInfo.contractOverview }}
                    </div>
                </div>
            </div>
        </div>

        <!-- 显示生成的word文档内容 -->
        <el-dialog v-model="showWordDialog" title="文档预览" width="60%" destroy-on-close append-to-body>
                <div class="document-wrapper" v-loading="loadingDocument" >
                    <el-scrollbar class="scrollable-area" style="height: calc(75vh);margin-top:0px; padding-right:0px">
                        <iframe :src="iframeUrl" style="position:absolute;width:100%;height:100%;border:0px;"></iframe>
                    </el-scrollbar>
                </div>
                <div class="document-footer">
                    <el-button type="primary" size="large" @click="handleDownloadDocx()">
                       <i class="fa-solid fa-sailboat"></i> 下载
                    </el-button>
                </div>
        </el-dialog>

    </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { ElLoading } from 'element-plus';
import { renderAsync } from 'docx-preview';

// import VueOfficeDocx from '@vue-office/docx'

import { 
    getTemplates , 
    getPreviewDocxPreviewUrl, 
    getPreviewDocx , 
    chatRoleSync 
} from '@/api/base/im/scene/contentFormatter';

const route = useRoute();

const props = defineProps({
  currentSceneInfo: {
    type: Object,
    default: () => {}
  }
})

const channelStreamId = ref(route.query.channelStreamId);
const sceneId = ref(route.query.sceneId)
const currentSceneInfo = ref({});
const ruleFormRef = ref();
const streamLoading = ref(null)
const docxFile = ref(null);

// 生成文档预览
const iframeUrl = ref(null);
const showWordDialog = ref(false);

const businessOptions = ref([
  { value: 'government', label: '政务' },
  { value: 'commerce', label: '商务' },
  { value: 'ecommerce', label: '电商' },
  { value: 'campus', label: '校园' },
  { value: 'medical', label: '医疗' },
  { value: 'education', label: '教育' },
  { value: 'finance', label: '金融' },
  { value: 'manufacturing', label: '制造' },
  { value: 'media', label: '媒体' },
  { value: 'entertainment', label: '娱乐' },
  { value: 'transportation', label: '交通' },
  { value: 'agriculture', label: '农业' },
  { value: 'construction', label: '建筑' },
  { value: 'energy', label: '能源' },
  { value: 'tourism', label: '旅游' },
  { value: 'service', label: '服务' }
]);

// 文书类型
const documentOptions = ref([
  { value: 'notice', label: '通知' },
  { value: 'government_letter', label: '政务函' },
  { value: 'red_head_document', label: '红头文件' },
  { value: 'report', label: '报告' },
  { value: 'memo', label: '备忘录' },
  { value: 'contract', label: '合同' },
  { value: 'agreement', label: '协议' },
  { value: 'proposal', label: '提案' },
  { value: 'announcement', label: '公告' },
  { value: 'circular', label: '通报' },
  { value: 'invitation', label: '邀请函' },
  { value: 'declaration', label: '声明' },
  { value: 'application', label: '申请书' },
  { value: 'resume', label: '简历' }
]);

const loadingDocument = ref(true)
const contentRef = ref(null)
const storageId = ref(null)

const emit = defineEmits(['handleStepClick' , 'getContentPromptContent' , 'genSingleChapterContent'])

// 定义表单数据
const ruleForm = reactive({
    reviewListOption: 'dataset',
    documentType: [],
    businessScenario: [],
    templateId: ''
});

const templateList = ref([]);

// 定义表单验证规则
const rules = reactive({
    documentType: [
        { required: true, message: '请选择文稿类型', trigger: 'change' }
    ],
    businessScenario: [
        { required: true, message: '请选择业务场景', trigger: 'change' }
    ], 
    templateId: [
        { required: true, message: '请选择排版模板', trigger: 'change' }
    ],
    reviewListOption: [
        { required: true, message: '请选择排版清单选项', trigger: 'change' },
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
    ]
});

// 提交表单方法
const submitForm = async () => {
    const formEl = ruleFormRef.value;
    if (!formEl) {
        return;
    }

    await formEl.validate((valid) => {
        if (valid) {
            console.log('submit!', ruleForm);

            ruleForm.sceneId = props.currentSceneInfo.id ;
            ruleForm.channelStreamId = channelStreamId.value ;

            // 开始生成
            streamLoading.value = ElLoading.service({
                lock: true,
                background: 'rgba(255, 255, 255, 0.4)',
                customClass: 'custom-loading'
            });

            let text = '正在使用模板生成业务内容，请稍等.';
            streamLoading.value.setText(text)

            emit('getContentPromptContent',(result) => {

                const roleId = props.currentSceneInfo.templateExtractorEngineer ;
                emit('genSingleChapterContent', roleId) ; 

                ruleForm.roleId = roleId ; 
                ruleForm.contentPromptContent = result ; 

                chatRoleSync(ruleForm).then(res => {
                    console.log('res = ' + res);
                    streamLoading.value.close();

                    emit('handleStepClick', 1);

                    storageId.value = res.data ;
                    getDocxContent(res.data);
                })
            }) ; 

        } else {
            console.log('error submit!');
        }
    });
};

const handleDownloadDocx = () => {
    getPreviewDocxPreviewUrl(storageId.value).then(res => {
        window.open(res.data)
    })
};

const getDocxContent = async(storageId) => {
    try {
        showWordDialog.value = true ;
        loadingDocument.value = true;

        nextTick(() => {
            getPreviewDocx(storageId).then(response => {

                console.log('response.data:' + response); // 打印数据内容

                // renderAsync(response, contentRef.value , null  , {
                //     inWrapper: true 
                // });

                iframeUrl.value = window.URL.createObjectURL(response);

                // iframeUrl.value = URL.createObjectURL(new Blob([resonse], { type: "application/pdf" }))+'#toolbar=0&view=FitH';

                loadingDocument.value = false ;

                console.log('文档渲染成功');
            });

        });

    } catch (error) {
        console.error('获取或渲染文档失败', error);
    }
}

// 重置表单方法
const resetForm = () => {
    const formEl = ruleFormRef.value;
    if (!formEl) return;
    formEl.resetFields();
};

const handleGetTemplates = () => {
    getTemplates().then(res => {
        // contractTypeList.value = res.data;
        templateList.value = res.data;
    })
}

// 存储表单数据到本地缓存
const saveFormDataToLocalStorage = () => {
    localStorage.setItem('ruleForm', JSON.stringify({
        documentType: ruleForm.documentType,
        businessScenario: ruleForm.businessScenario,
        templateId: ruleForm.templateId,
        reviewListOption: ruleForm.reviewListOption
    }));
};

// 从本地缓存读取表单数据
const loadFormDataFromLocalStorage = () => {
    const storedData = localStorage.getItem('ruleForm');
    if (storedData) {
        const data = JSON.parse(storedData);
        ruleForm.documentType = data.documentType;
        ruleForm.businessScenario = data.businessScenario;
        ruleForm.templateId = data.templateId;
        ruleForm.reviewListOption = data.reviewListOption;
    }
};

watch([() => ruleForm.documentType, () => ruleForm.businessScenario, () => ruleForm.templateId], () => {
    saveFormDataToLocalStorage();
});

onMounted(() => {
    handleGetTemplates() ;
    loadFormDataFromLocalStorage();
});

</script>

<style lang="scss" scoped>

.contract-review-container {
    .review-methods-container {
        padding-top: 10px;
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

.document-footer {
    margin-top: 10px;
    text-align: right;
}


</style>