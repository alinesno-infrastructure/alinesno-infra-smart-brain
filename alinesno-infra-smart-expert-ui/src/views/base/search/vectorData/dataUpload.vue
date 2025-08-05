<template>
    <div class="app-container upload-dataset-container">
        <el-page-header @back="goBack">
            <template #content>
                <span class="text-large font-600 mr-3" style="font-size: 16px;"> [测试数据集] 上传</span>
            </template>
        </el-page-header>
        <div class="step-container">
            <el-steps :active="activeStep" simple finish-status="success" class="step-container-panel">
                <el-step title="选择文件"></el-step>
                <el-step title="数据处理"></el-step>
                <el-step title="确认上传"></el-step>
            </el-steps>
        </div>
        <div v-if="activeStep === 1" class="step-content">
            <el-upload ref="uploadRef" multiple :limit="10" accept=".xlsx,.xls,.ppt,.docx,.doc,.pdf,.pptx,.md"
                :headers="upload.headers" :action="upload.url +
                    '?updateSupport=' +
                    upload.updateSupport +
                    '&datasetId=' +
                    currentDatasetId
                    " :disabled="upload.isUploading" :on-progress="handleFileUploadProgress"
                :on-success="handleFileSuccess" :auto-upload="true" drag>
                <el-icon class="el-icon--upload">
                    <upload-filled />
                </el-icon>
                <div class="el-upload__text">
                    将文件拖到此处，或<em>点击上传</em>
                    <div class="el-upload__tip text-center">
                        <span>
                            支持.txt,.docx,.csv,.xlsx,.pdf,.md,.html,.pptx 类型文件 <br />
                            最多支持 10 个文件单个文件最大 50 MB
                        </span>
                    </div>
                </div>
            </el-upload>
            <div class="file-list">
                <el-table :data="tableData" style="width: 100%">
                    <el-table-column label="文件名">
                        <template #default="{ row }">
                            <i :class="getFileIconClass(row.fileType)" style="margin-right: 5px;"></i>
                            {{ row.documentName }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="uploadProgress" width="300px" align="center" label="文件上传进度">
                        <template #default="scope">
                            <el-progress status="success" v-if="scope.row.hasUploaded === 'uploaded'" :percentage="100" :stroke-width="20" :text-inside="true"
                            striped-flow />
                            <el-progress v-else :percentage="scope.row.uploadProgress" striped striped-flow
                                :stroke-width="10" status="primary" :duration="10" />
                        </template>
                    </el-table-column>
                    <el-table-column prop="fileSize" label="文件大小" align="center" width="280" />
                    <el-table-column label="操作" align="center" width="200">
                        <template #default="{ row }">
                            <el-button size="mini" @click="previewFile(row)" icon="View">预览</el-button>
                            <el-button size="mini" @click="handleDelete(row)" icon="DeleteFilled">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <el-button @click="nextStep" type="primary" size="large" icon="Right" class="next-button">下一步</el-button>
        </div>
        <div v-if="activeStep === 2" class="step-content">
            <el-row>
                <el-col :span="10">
                    <div class="step-content-title">数据处理方式</div>
                    <el-form :model="formData" ref="formRef" label-width="150" :label-position="labelPosition"
                        :rules="rules">
                        <el-form-item prop="processingMethod" label="数据处理方式">
                            <el-radio-group v-model="formData.processingMethod">
                                <el-radio :label="item.code" :value="item.code" size="large"
                                    v-for="(item, index) in splitterTextType" :key="index" border>
                                    <div>
                                        <div style="display: flex;align-items: center;gap: 3px;">
                                            <i :class="item.icon"></i>{{ item.name }} <el-icon>
                                                <QuestionFilled />
                                            </el-icon>
                                        </div>
                                    </div>
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item prop="processingParam" label="处理参数">
                            <el-radio-group v-model="formData.processingParam">
                                <el-radio v-for="item in radioOptions" size="large" :key="item.label"
                                    :label="item.label" border>
                                    <div style="display: flex;align-items: flex-start;gap: 3px;flex-direction: column;">
                                        <div>
                                            <i :class="item.icon"></i>{{ item.label }}
                                        </div>
                                    </div>
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item prop="idealChunkLength" label="理想分块长度">
                            <el-input-number v-model="formData.idealChunkLength" size="large" :min="1" :step="1"
                                :max="5000" controls-position="right" placeholder="理想分块长度" style="width:30%">
                            </el-input-number>
                        </el-form-item>
                        <el-form-item prop="customSplitSymbol" label="自定义分割符号">
                            <el-input v-model="formData.customSplitSymbol" size="large" placeholder="自定义分割符号"
                                style="width:70%"></el-input>
                        </el-form-item>
                    </el-form>
                </el-col>
                <el-col :span="14">
                    <div class="step-content-title">上传文件列表</div>
                    <div class="file-list">
                        <el-table :data="tableData" style="width: 100%">
                            <el-table-column label="文件名">
                                <template #default="{ row }">
                                    <i :class="getFileIconClass(row.fileType)" style="margin-right: 5px;"></i>
                                    {{ row.documentName }}
                                </template>
                            </el-table-column>
                            <el-table-column prop="fileSize" label="文件大小" align="center" width="280" />
                            <el-table-column label="操作" align="center" width="200">
                                <template #default="{ row }">
                                    <el-button size="mini" @click="previewFile(row)" icon="View">预览</el-button>
                                    <el-button size="mini" @click="handleDelete(row)" icon="DeleteFilled">删除</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                </el-col>
            </el-row>
            <el-button @click="prevStep" type="danger" size="large" icon="Back" class="prev-button">上一步</el-button>
            <el-button @click="validateAndNext" type="primary" size="large" icon="Right"
                class="next-button">下一步</el-button>
        </div>
        <div v-if="activeStep === 3" class="step-content">
            <div class="file-list">
                <el-table :data="tableData" style="width: 100%">
                    <el-table-column label="文件名">
                        <template #default="{ row }">
                            <i :class="getFileIconClass(row.fileType)" style="margin-right: 5px;"></i>
                            {{ row.documentName }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" align="center" label="状态">
                        <template #default="scope">
                            <el-button v-if="scope.row.status === '未解析'" text bg loading type="primary">解析中</el-button>
                            <span v-else>{{ scope.row.status }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="fileSize" label="文件大小" align="center" width="280" />
                    <el-table-column label="操作" align="center" width="200">
                        <template #default="{ row }">
                            <el-button size="mini" @click="handleDelete(row)"  icon="DeleteFilled">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <el-progress v-if="uploading" :percentage="uploadProgress" class="progress-bar"></el-progress>
            <div>
                <el-button @click="prevStep" type="danger" size="large" icon="Back" class="prev-button">上一步</el-button>
                <el-button @click="uploadFiles" :loading="uploading" icon="Upload" type="primary" size="large" class="upload-button">
                    {{ uploading ? '上传中...' : '上传' }}
                </el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { getToken } from "@/utils/auth";
import { nextTick, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getParam } from "@/utils/ruoyi";
import { 
    queryTmpFileByDatasetId,
    delDatasetKnowledge,
    uploadTmpFileByDatasetId,
 } from '@/api/base/search/datasetKnowledge';
import axios from 'axios';

const { proxy } = getCurrentInstance();

// 获取路由实例
const router = useRouter();
const currentDatasetId = getParam("datasetId");
const labelPosition = ref('right');

// 当前步骤
const activeStep = ref(1);
// 文件列表
const fileList = ref([]);
// 表单数据
const formData = ref({
    processingMethod: 'direct_segmentation',
    processingParam: '自动',
    idealChunkLength: '512',
    customSplitSymbol: '\n;======;==SPLIT=='
});
// 表单引用
const formRef = ref(null);
const uploadRef = ref(null);
// 上传状态
const uploading = ref(false);
// 上传进度
const uploadProgress = ref(0);

const splitterTextType = ref([
    { "code": "direct_segmentation", "name": "直接分段", "icon": "" },
    { "code": "enhanced_processing", "name": "增强处理", "icon": "" },
    { "code": "qa_split", "name": "问答拆分", "icon": "" }
]);

// 定义单选框选项的数组
const radioOptions = ref([
    {
        label: '自动',
        description: '自动设置分割和预处理规则'
    },
    {
        label: '自定义',
        description: '自定义设置数据处理规则'
    }
]);

/*** 应用导入参数 */
const upload = reactive({
    // 是否显示弹出层（应用导入）
    open: false,
    // 弹出层标题（应用导入）
    title: "",
    // 是否禁用上传
    isUploading: false,
    // 是否更新已经存在的应用数据
    updateSupport: 0,
    // 设置上传的请求头部
    headers: { Authorization: "Bearer " + getToken() },
    // 上传的地址
    url:
        import.meta.env.VITE_APP_BASE_API + "/api/infra/base/search/datasetKnowledge/upload"
});

// 表单校验规则
const rules = ref({
    processingMethod: [
        { required: true, message: '请选择数据处理方式', trigger: 'change' }
    ],
    processingParam: [
        { required: true, message: '请选择处理参数', trigger: 'change' }
    ],
    idealChunkLength: [
        { required: true, message: '请输入理想分块长度', trigger: 'blur' }
    ],
    customSplitSymbol: [
        { required: true, message: '请输入自定义分割符号', trigger: 'blur' }
    ]
});

// 返回上一页
const goBack = () => {
    router.back();
};

// 处理文件选择
const handleFileChange = (file, fileList) => {
    const newFile = {
        documentName: file.name,
        fileType: file.name.split('.').pop().toLowerCase(),
        fileSize: file.size,
        uploadProgress: 0,
        status: '未解析'
    };
    tableData.value.push(newFile);
    fileList.value = fileList;
};

// 下一步
const nextStep = () => {
    if(activeStep.value === 1){
        if(tableData.value.length === 0){
            proxy.$message({
                message: '请选择文件',
                type: 'warning'
            });
            return ;
        }
    }
    activeStep.value++;
};

// 上一步
const prevStep = () => {
    if (activeStep.value > 1) {
        activeStep.value--;
    }
};

/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
    const percentCompleted = Math.round((event.loaded * 100) / event.total);
    const index = tableData.value.findIndex(f => f.documentName === file.name);
    if (index > -1) {
        tableData.value[index].uploadProgress = percentCompleted;
    }
    upload.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {

    upload.open = false;
    upload.isUploading = false;

    console.log('response = ' + response.code)
    if(response.code != 200){ 
        ElMessage.error(response.msg?response.msg:'文件上传异常，请重新进入再次上传。');
        return ; 
    }

    
    proxy.$refs["uploadRef"].handleRemove(file);
    handleQueryTmpFileByDatasetId();
};

const submitUpload = () => {
    uploadRef.value.submit();
};

// 定义表格数据
const tableData = ref([]);

// 根据文件名获取对应的图标类名
const getFileIconClass = (fileType) => {
    switch (fileType) {
        case 'pdf':
            return 'fa-solid fa-file-pdf';
        case 'docx':
            return 'fa-solid fa-file-word';
        case 'pptx':
            return 'fa-solid fa-file-powerpoint';
        case 'xlsx':
            return 'fa-solid fa-file-excel';
        case 'csv':
            return 'fa-solid fa-file-csv';
        case 'txt':
            return 'fa-solid fa-file-lines';
        case'md':
            return 'fa-solid fa-file-code';
        default:
            return 'fa-solid fa-file';
    }
};

// 处理删除文件操作
const handleDelete = (row) => {
    delDatasetKnowledge(row.id).then(res => {
        proxy.$message({
            message: '删除成功',
            type: 'success'
        });
        handleQueryTmpFileByDatasetId();
    });
};

// 预览文件
const previewFile = (row) => {
    const ext = row.fileName.split('.').pop().toLowerCase();
    if (ext === 'pdf' && row.fileUrl) {
        window.open(row.fileUrl, '_blank');
    } else {
        console.log('暂不支持该文件类型的预览');
    }
};

// 上传文件
const uploadFiles = () => {
    uploading.value = true;
    try {
        formData.value.datasetId = currentDatasetId;
        uploadTmpFileByDatasetId(formData.value).then(response => {
            proxy.$message({
                message: '解析成功',
                type: 'success'
            });
            uploading.value = false;

            const path = '/base/search/vectorData/parseDataset?datasetId=' + currentDatasetId ;
            router.push(path)
        });
    } catch (error) {
        console.error('提交数据处理方式时出错', error);
        proxy.$message.error('提交数据处理方式失败');
    }
};

// 验证表单并下一步
const validateAndNext = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            nextStep();
        } else {
            console.log('表单校验不通过');
        }
    });
};

/** 查询未导入数据集 */
const handleQueryTmpFileByDatasetId = () => {
    queryTmpFileByDatasetId(currentDatasetId).then(response => {
        console.log('response.data ='+ JSON.stringify(response.data))
        tableData.value = response.data; 
    });
};

nextTick(() => {
    console.log('handleQueryTmpFileByDatasetId ='+ currentDatasetId)
    handleQueryTmpFileByDatasetId();
});
</script>
 

<style scoped lang="scss">

.upload-dataset-container{
    margin: auto;
    max-width:1400px;
}
.step-container {
    background: #F7F8FA;
    padding: 5px;
    margin-top: 20px;
    border-radius: 10px;

 .step-container-panel {
        max-width: 900px;
        margin: auto;
    }
}

.step-content {
    background-color: #fff;
    padding: 20px 0px;
    border-radius: 8px;
}

.upload-container {
    margin-bottom: 15px;
}

.file-list {
    list-style-type: none;
    padding: 0;
    margin-top: 20px;
    margin-bottom: 20px;

    li {
        display: flex;
        align-items: center;
        margin-bottom: 8px;
        font-size: 14px;
        color: #333;

        i {
            margin-right: 8px;
            color: #666;
        }
    }
}

.next-button,
.upload-button,
.prev-button {
    margin-top: 15px;
    margin-right: 15px;
}

.section-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 10px;
    color: #333;
}

.radio-group {
    display: flex;
    flex-direction: column;
    margin-bottom: 20px;

 .el-radio {
        margin-bottom: 8px;
    }
}

.step-content-title {
    font-size: 14px;
    padding: 10px 0px;
}

.custom-inputs {
    display: flex;
    flex-direction: column;

 .el-input {
        margin-bottom: 15px;
    }
}

.progress-bar {
    margin-top: 15px;
}
</style>