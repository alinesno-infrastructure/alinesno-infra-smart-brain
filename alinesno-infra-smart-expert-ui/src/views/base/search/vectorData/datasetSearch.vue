<template>
    <div>
        <el-row :gutter="20">
            <el-col :span="17">
                <el-form :model="queryParams" ref="queryRef" :rules="rules" :inline="true" v-show="showSearch" label-width="100px">
                    <el-form-item label="搜索条件" prop="searchText">
                        <el-input v-model="queryParams.searchText" placeholder="请输入搜索条件" clearable style="width: 540px"
                        @keyup.enter.native="handleQuery"
                             />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                    </el-form-item>
                </el-form>
                <el-scrollbar class="result-scrollbar">
                    <el-table v-loading="loading" :data="datasetList" @selection-change="handleSelectionChange">
                        <el-table-column type="index" width="50" label="序号" align="center" />
                        <el-table-column label="查询结果" align="left" key="name" prop="name" v-if="columns[1].visible">
                            <template #default="scope">
                                <div class="result-item">
                                    <div class="role-icon">
                                        评分:<i class="fa-solid fa-file-pdf" />
                                        {{ scope.row.score }}
                                    </div>
                                    <div class="result-content">
                                        {{ truncateString(scope.row.document_content, 300) }}
                                    </div>
                                    <div class="role-icon">
                                        引用: <i class="fa-solid fa-file-pdf" />
                                        {{ scope.row.document_title }}
                                    </div>
                                </div>
                            </template>
                        </el-table-column>
                    </el-table>
                    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                        v-model:limit="queryParams.pageSize" @pagination="getList" />
                </el-scrollbar>
            </el-col>
            <el-col :span="7">
                <div class="config-container">
                    <div class="config-title">查询配置</div>
                    <el-form :model="queryParams" :label-position="'top'" ref="configRef" :inline="true" style="width:100%" label-width="120px">
                        <el-row>
                            <el-col :span="24">
                                <el-form-item label="检索类型">
                                    <el-radio-group v-model="queryParams.searchType">
                                        <el-radio v-for="item in searchTypes" :key="item.code" :label="item.code"
                                            style="height:60px" size="large" class="search-type-radio" :class="item.code === queryParams.searchType ? 'search-type-radio-active' : 'search-type-radio-normal'">
                                            <template #default>
                                                <div
                                                    style="display: flex;flex-direction: row;contain-intrinsic-block-size: auto 100px;line-height: 20px;align-items: center;gap: 10px;">
                                                    <span>
                                                        <i :class="item.icon"></i>
                                                    </span>
                                                    <div style="display: flex;flex-direction: column;">
                                                        <span style="color:#333">
                                                            {{ item.label }}
                                                        </span>
                                                        <span style="color: #667085;font-size:13px">{{ item.desc }}</span>
                                                    </div>
                                                </div>
                                            </template>
                                        </el-radio>
                                    </el-radio-group>
                                </el-form-item>
                            </el-col>
                            <el-col :span="24">
                                <el-form-item label="引用上限" prop="quoteLimit">
                                    <el-input v-model="queryParams.quoteLimit" placeholder="请输入上限字符数" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="24">
                                <el-form-item label="是否结果重排" prop="reorderResults">
                                    <el-switch v-model="queryParams.reorderResults" 
                                        :active-value="1"
                                        :inactive-value="0"
                                        active-text="是" 
                                        inactive-text="否" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="24">
                                <el-form-item label="最低相关度" prop="minRelevance"
                                    v-if="queryParams.searchType === 'vector'">
                                    <el-input v-model="queryParams.minRelevance" placeholder="请输入最低相关度" />
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <div style="margin-top:10px;text-align: right;">
                            <el-button type="primary" bg text size="large" @click="saveConfig">
                                <i class="fa-solid fa-file-pdf"></i> &nbsp; 保存配置
                            </el-button>
                        </div>
                    </el-form>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getSearch, updateDatasetConfig } from "@/api/base/search/vectorDataset";

const route = useRoute();
const { proxy } = getCurrentInstance();

const datasetList = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref([]);
// 列显隐信息
const columns = ref([
    { key: 0, label: `图标`, visible: true },
    { key: 1, label: `数据集名称`, visible: true },
    { key: 2, label: `所有者`, visible: true },
    { key: 3, label: `描述信息`, visible: true },
    { key: 4, label: `状态`, visible: true },
    { key: 5, label: `访问权限`, visible: true },
    { key: 6, label: `数据总量`, visible: true },
    { key: 7, label: `创建时间`, visible: true },
    { key: 8, label: `编辑`, visible: true },
]);

const data = reactive({
    form: {},
    queryParams: {
        pageNum: 1,
        pageSize: 10,
        DatasetName: undefined,
        searchText: undefined,
        topK: 10,
        status: undefined,
        deptId: undefined,
        searchType: 'vector', // 默认向量检索
        quoteLimit: 1000, // 默认引用上限
        reorderResults: false, // 默认不重排
        minRelevance: 0.5 // 默认最低相关度
    },
    rules: {
        applicationId: [{ required: true, message: "应用编号不能为空", trigger: "blur" }],
        name: [{ required: true, message: "应用名称不能为空", trigger: "blur" }, {
            min: 2,
            max: 20,
            message: "应用名称长度必须介于 2 和 20 之间",
            trigger: "blur"
        }],
        searchText: [{ required: true, message: "显示名称不能为空", trigger: "blur" }],
        description: [{ required: true, message: "描述信息不能为空", trigger: "blur" }],
        datasetStatus: [{ required: true, message: "域名不能为空", trigger: "blur" }],
        accessPermission: [{ required: true, message: "安全存储路径不能为空", trigger: "blur" }],
        datasetSize: [{ required: true, message: "应用目标不能为空", trigger: "blur" }],
    }
});

const { queryParams, form, rules } = toRefs(data);

const searchTypes = ref([
    { 
        code: 'vector', 
        label: '向量检索', 
        desc: '使用向量进行文本相关性查询', 
        enable: true,
        icon: 'fa-solid fa-magnifying-glass-chart'
    },
    { 
        code: 'fulltext', 
        label: '全文检索', 
        desc: '使用传统的全文检索，基于PostgreSql的全文检索', 
        enable: false,
        icon: 'fa-solid fa-file-pdf'
    },
    { 
        code: 'hybrid', 
        label: '混合检索', 
        desc: '使用向量检索与全文检索的综合结果返回。', 
        enable: false,
        icon: 'fa-solid fa-code-branch' 
    }
]);

const setDataset = (dataset) => {
    if (dataset) {
        console.log('props.dataset = ' + JSON.stringify(dataset));

        queryParams.value.searchType = dataset.searchType; 
        queryParams.value.quoteLimit = dataset.quoteLimit; 
        queryParams.value.reorderResults = dataset.reorderResults; 
        queryParams.value.minRelevance = dataset.minRelevance; 
    }
}

/** 查询应用列表 */
function getList() {
    loading.value = true;
    let datasetId = route.query.datasetId;
    queryParams.value.datasetId = datasetId;
    getSearch(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
        loading.value = false;
        datasetList.value = res.data;
        total.value = res.total;
    }).catch(() => {
        loading.value = false;
    });
}

/** 搜索按钮操作 */
function handleQuery() {
    proxy.$refs["queryRef"].validate(valid => {
        if (valid) {
            getList();
        }
    });
}

/** 重置按钮操作 */
function resetQuery() {
    dateRange.value = [];
    proxy.resetForm("queryRef");
    proxy.resetForm("configRef"); // 重置检索配置表单
    loading.value = false;
    queryParams.value.DatasetName = undefined;
    queryParams.value.searchText = undefined;
    queryParams.value.topK = 10;
    queryParams.value.status = undefined;
    queryParams.value.deptId = undefined;
    queryParams.value.searchType = 'vector';
    queryParams.value.quoteLimit = 1000;
    queryParams.value.reorderResults = false;
    queryParams.value.minRelevance = 0.5;
    handleQuery();
}

/** 选择条数  */
function handleSelectionChange(selection) {
    ids.value = selection.map(item => item.id);
    single.value = selection.length != 1;
    multiple.value = !selection.length;
}

/** 保存配置操作 */
function saveConfig() {
    queryParams.value.datasetId = route.query.datasetId;
    console.log('保存的配置:', queryParams.value);
    updateDatasetConfig(queryParams.value).then(res => {
        proxy.$modal.msgSuccess("修改成功");
    }).catch(() => {
        proxy.$modal.msgError("修改失败");
    });
}

// 截断字符串函数示例
function truncateString(str, length) {
    return str.length > length ? str.slice(0, length) + '...' : str;
}

defineExpose({
    setDataset
});


</script>

<style lang="scss" scoped>
.role-icon {
    img {
        width: 45px;
        height: 45px;
        border-radius: 50%;
    }

    font-size: 14px;
}

.input-search-text {
    width: 100%;
    resize: none;
    font-size: 14px;
    border: 0px solid #ccc;
    border-radius: 5px;
    background-color: #fafafa;
    outline: none;

    textarea {
        background-color: #fafafa;
    }
}

.result-scrollbar {
    height: calc(100vh - 250px);
}

.result-item {
    background: rgb(250, 250, 250);
    border-radius: 5px;
    padding: 10px;
    display: flex;
    flex-direction: column;
    gap: 5px;
}

.result-content {
    padding: 5px;
}

.config-container {
    // background: #fafafa;
    padding: 5px 10px;
    border-radius: 5px;

    .search-type-radio-active{
            border: 1px solid #5E8FFF;
    background-color: #E1EAFF;
    }
}

.config-title {
    font-size: 14px;
    margin-bottom: 10px;
    font-weight: bold;
}

.search-type-radio {
    margin-top: 10px;
    width: 100%;
    margin-right: 10px !important;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    padding: 10px;
}
</style>