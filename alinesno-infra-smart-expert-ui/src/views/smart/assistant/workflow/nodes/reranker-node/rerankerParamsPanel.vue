<template>
    <div class="row">
        <div class="config-container">
            <el-form :model="form" :label-position="'top'" ref="configRef" :inline="true" style="width:100%" label-width="120px">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="引用上限" prop="quoteLimit" class="search-form-item">
                            <el-slider size="large" show-input v-model="form.quoteLimit" :step="1" :max="5000" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="引用分段数" prop="topK" class="search-form-item">
                            <el-slider size="large" show-input v-model="form.topK" :step="1" :min="1" :max="10" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="最低相关度" prop="minRelevance" class="search-form-item">
                            <el-slider size="large" show-input v-model="form.minRelevance" :step="0.1" :min="0.1" :max="1" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <div class="save-button-container">
                    <el-button type="primary" bg text size="large" @click="handleSubmit">
                        <i class="fa-solid fa-file-pdf"></i> &nbsp; 保存配置
                    </el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
// import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['handleSelectDatasetParamsConfigClose'])

// const route = useRoute();
// const { proxy } = getCurrentInstance();

const configRef = ref(null);

const form = reactive({
    // datasetId: route.query.datasetId,
    // searchType: 'vector', // 默认向量检索
    topK: 3 , 
    quoteLimit: 1000, // 默认引用上限
    reorderResults: false, // 默认不重排
    minRelevance: 0.5 // 默认最低相关度
});

// const searchTypes = ref([
//     {
//         code: 'vector',
//         label: '向量检索',
//         desc: '使用向量进行文本相关性查询',
//         enable: true,
//         icon: 'fa-solid fa-magnifying-glass-chart'
//     },
//     {
//         code: 'fulltext',
//         label: '全文检索',
//         desc: '使用传统的全文检索，基于PostgreSql的全文检索',
//         enable: false,
//         icon: 'fa-solid fa-file-pdf'
//     },
//     {
//         code: 'hybrid',
//         label: '混合检索',
//         desc: '使用向量检索与全文检索的综合结果返回。',
//         enable: false,
//         icon: 'fa-solid fa-code-branch'
//     }
// ]);

const setDataset = (dataset) => {
    if (dataset) {
        console.log('props.dataset = ' + JSON.stringify(dataset));
        form.searchType = dataset.searchType;
        form.quoteLimit = dataset.quoteLimit;
        form.reorderResults = dataset.reorderResults;
        form.minRelevance = dataset.minRelevance;
    }
}

// /** 重置按钮操作 */
// function resetQuery() {
//     proxy.resetForm("configRef"); // 重置检索配置表单
//     form.searchType = 'vector'
//     form.quoteLimit = 1000;
//     form.reorderResults = false;
//     form.minRelevance = 0.5;
// }

/** 保存配置操作 */
function saveConfig() {
    console.log('保存的配置:', form);
}

const handleSubmit = () => {
    configRef.value.validate((valid) => {
        if (valid) {
            console.log('form = ' + JSON.stringify(form));
            emit("handleSelectDatasetParamsConfigClose" , form);
            ElMessage.success('提交成功');
        } else {
            ElMessage.error('表单验证失败，请检查输入');
        }
    });
};

// const setDatasetSearchParams = (params) => {
//     console.log('setAgentModelParams modelParams = ' + JSON.stringify(params));
//     form = params
// }

defineExpose({
    setDataset ,
    // setDatasetSearchParams
});
</script>

<style lang="scss" scoped>
.config-container {
    padding: 5px 10px;
    border-radius: 5px;

    .search-type-radio {
        margin-top: 10px;
        width: 100%;
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        padding: 10px;
        height: 63px;
        margin-right: 0px !important;

        &.search-type-radio-active {
            border: 1px solid #5E8FFF;
            background-color: #E1EAFF;
        }

        .radio-content {
            display: flex;
            flex-direction: row;
            align-items: center;
            gap: 10px;
        }

        .radio-text {
            display: flex;
            flex-direction: column;
        }

        .radio-label {
            color: #333;
            height: 25px;
        }

        .radio-desc {
            color: #667085;
            font-size: 13px;
        }
    }

    .search-form-item {
        margin-right: 0px !important;
    }

    .save-button-container {
        margin-top: 10px;
        display: flex;
        justify-content: flex-end;
        width: 100%;
    }
}
</style>