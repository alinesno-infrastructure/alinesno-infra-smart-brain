<template>
  <div class="app-container">
    <el-page-header @back="goBack">
      <template #content>
        <span class="text-large font-600 mr-3" style="font-size: 16px;"> {{ documentName }}</span>
      </template>
    </el-page-header>
    <el-row :gutter="20" style="margin-top:20px;">
      <el-col :span="19">
        <el-form :model="queryParams" size="large" ref="queryRef" :rule="rules" :inline="true" v-show="showSearch"
          label-width="100px">

          <el-form-item label="搜索条件" prop="searchText">
            <el-input v-model="queryParams.searchText" placeholder="请输入搜索条件" clearable style="width: 540px" />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-scrollbar style="height:calc(100vh - 200px)">
          <el-table v-loading="loading" :data="datasetList" @selection-change="handleSelectionChange">
            <el-table-column type="index" width="50" label="序号" align="center" />
            <el-table-column label="切片数据" align="left" key="name" prop="name" v-if="columns[1].visible">
              <template #default="scope">
                <div
                  style="background: rgb(250, 250, 250);border-radius: 5px;padding:10px;display: flex;flex-direction: column;gap: 5px;">
                  <div class="role-icon" style="font-size: 14px;">
                    评分:<i class="fa-solid fa-file-pdf" />
                    {{ scope.row.score }}
                  </div>
                  <div style="padding: 5px;">
                    {{ truncateString(scope.row.document_content, 300) }}
                  </div>
                  <div class="role-icon" style="font-size: 14px;">
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

      <el-col :span="5">
        <div class="metadata-container">
          <div style="font-size: 14px;margin-bottom: 15px;font-weight: bold;">
            元数据
          </div>
          <div class="metadata-item" v-for="(item, index) in metadataList" :key="index">
            <div class="metadata-label">{{ item.metaDataItem }}</div>
            <div class="metadata-value">{{ item.detail }}</div>
          </div>
        </div>
      </el-col>

      <!-- 
      <el-col :span="5">
        <div style="font-size: 14px;margin: 10px;font-weight: bold;">
          元数据
        </div>
        <el-table :data="metadataList" stripe>
          <el-table-column prop="metaDataItem" width="100" label="数据项"></el-table-column>
          <el-table-column prop="detail" label="详情"></el-table-column>
        </el-table>
      </el-col> 
      -->

    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'

import {
  getSearch,
} from "@/api/base/search/vectorDataset";

import {
  knowledgeDetail,
  queryDocumentPage
} from "@/api/base/search/datasetKnowledge";

import { reactive } from "vue";

const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

const documentName = ref('文档名称')
const knowledgeId = ref(route.query.id)
const datasetList = ref([]);
const loading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref([]);

const metadataList = ref([
  { metaDataItem: '集合 ID', detail: '67b1618119984de00881fb9d' },
  { metaDataItem: '数据来源', detail: '文件' },
  { metaDataItem: '来源名', detail: '01_我对智能体基础平台的产品设计.md' },
  { metaDataItem: '来源大小', detail: '1.85 KB' },
  { metaDataItem: '创建时间', detail: '2025 - 02 - 16 11:54' },
  { metaDataItem: '更新时间', detail: '2025 - 02 - 16 11:54' },
  { metaDataItem: '原文长度', detail: '694' },
  { metaDataItem: '训练模式', detail: '直接分段' },
  { metaDataItem: '分割大小', detail: '512' }
])

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

const goBack = () => {
  router.back();
}

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    DatasetName: undefined,
    searchText: undefined,
    topK: 10,
    status: undefined,
    deptId: undefined
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


/** 查询应用列表 */
function getList() {
  loading.value = true;

  let datasetId = route.query.datasetId;
  console.log('datasetId = ' + datasetId);
  queryParams.value.datasetId = datasetId;

  getSearch(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    datasetList.value = res.data;
    total.value = res.total;
  }).catch(() => {
    loading.value = false;
  });

};


/** 搜索按钮操作 */
function handleQuery() {
  proxy.$refs["queryRef"].validate(valid => {
    console.log('---> ' + valid)
    if (valid) {
      getList();
    }
  });
};

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  loading.value = false;
  queryParams.value.name = undefined;
  queryParams.value.ownerId = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

// 查询数据集详情
const handleDatasetKnowledge = () => {
  knowledgeDetail(knowledgeId.value).then(res => {
    const knowledgeData = res.data;

    documentName.value = knowledgeData.documentName;

    metadataList.value = [
      { metaDataItem: '集合 ID', detail: knowledgeData.id || 'N/A' },
      { metaDataItem: '数据来源', detail: knowledgeData.fileType ? '文件' : 'N/A' },
      { metaDataItem: '来源名', detail: knowledgeData.documentName || 'N/A' },
      { metaDataItem: '来源大小', detail: knowledgeData.fileSize ? `${knowledgeData.fileSize} KB` : 'N/A' },
      { metaDataItem: '创建时间', detail: knowledgeData.addTime || 'N/A' },
      { metaDataItem: '更新时间', detail: knowledgeData.updateTime || 'N/A' },
      { metaDataItem: '文档数量', detail: knowledgeData.documentCount?.toString() || 'N/A' },
      {
        metaDataItem: '训练模式', detail:
          knowledgeData.processingMethod === 'direct_segmentation' ? '直接分段' :
            (knowledgeData.processingMethod || 'N/A')
      },
      { metaDataItem: '分割大小', detail: knowledgeData.idealChunk?.toString() || 'N/A' }
    ];
  })
}

// 查询文档列表
const handleQueryDocumentPage = () => {

  const params = {
    id: knowledgeId.value,
    pageNum: 1,
    pageSize: 10
  };
  queryDocumentPage(params).then(res => {
    console.log('res = ' + res);
    datasetList.value = res.rows;
    total.value = res.total;
  })
}

onMounted(() => {
  handleDatasetKnowledge();
  handleQueryDocumentPage();
})

</script>

<style lang="scss" scoped>
.role-icon {
  img {
    width: 45px;
    height: 45px;
    border-radius: 50%;
  }
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

.metadata-container {
  padding: 15px;
  background: #f8f8f8;
  border-radius: 4px;
}

.metadata-item {
  margin-bottom: 12px;
  line-height: 1.2rem;
}

.metadata-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.metadata-value {
  font-size: 14px;
  color: #333;
  word-break: break-word;
}

.role-icon {
  img {
    width: 45px;
    height: 45px;
    border-radius: 50%;
  }
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
</style>