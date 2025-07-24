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
          <!-- 修改为分段内容列表 -->
          <div class="segment-list">
            <div v-for="(segment, index) in datasetList" :key="index" class="segment-item">
              <div class="segment-header">
                <span class="segment-index"># 分段 {{ index + 1 }}（ID:{{segment.id}}）</span>
                <span class="segment-length">长度: {{ segment.document_content?.length || 0 }} 字符</span>
              </div>
              <div 
                class="segment-content" 
                @click="showFullContent(segment.document_content)"
              >
                {{ truncateString(segment.document_content, 300) }}
              </div>
            </div>
          </div>
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
    </el-row>

    <!-- 添加弹窗显示完整内容 -->
    <el-dialog
      v-model="dialogVisible"
      title="分段内容详情"
      width="60%"
      top="5vh"
    >
      <div class="full-content">
        <pre>{{ currentContent }}</pre>
      </div>
      <template #footer>
        <el-button type="primary" @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
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
const total = ref(0);
const dateRange = ref([]);

// 添加弹窗相关状态
const dialogVisible = ref(false);
const currentContent = ref('');

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

// 显示完整内容
const showFullContent = (content) => {
  currentContent.value = content;
  dialogVisible.value = true;
};

// 截断字符串
const truncateString = (str, num) => {
  if (!str) return '';
  if (str.length <= num) {
    return str;
  }
  return str.slice(0, num) + '...';
};

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

const goBack = () => {
  router.back();
}

onMounted(() => {
  handleDatasetKnowledge();
  handleQueryDocumentPage();
})
</script>

<style lang="scss" scoped>
.app-container {
  .segment-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
    padding: 8px;
    margin: 0px 20px;
  }

  .segment-item {
    background: #f8f8f8;
    border-radius: 4px;
    padding: 12px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: #f0f0f0;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }
  }

  .segment-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
    font-size: 14px;
    color: #666;
  }

  .segment-content {
    white-space: pre-wrap;
    word-break: break-word;
    line-height: 1.6;
    font-size: 14px;
    color: #333;
  }

  .full-content {
    max-height: 70vh;
    overflow-y: auto;
    padding: 10px;
    background: #f9f9f9;
    border-radius: 4px;
    line-height: 1.6;
    pre {
      white-space: pre-wrap;
      word-break: break-word;
      font-family: inherit;
      margin: 0;
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
}
</style>