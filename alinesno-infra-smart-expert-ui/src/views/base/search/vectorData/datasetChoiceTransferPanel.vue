<template>
  <div>
    <el-row :gutter="20">
      <!-- 应用数据 -->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
          <el-form-item label="数据集名称" prop="name">
            <el-input v-model="queryParams['condition[name|like]']" placeholder="请输入数据集名称" clearable style="width: 240px"
               />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <div class="dataset-el-transfer-panel">
          <el-transfer
            v-model="selectedDatasetIds"
            filterable
            style="text-align: left; width:100%; display: inline-block"
            :titles="['所有数据集', '已选择数据集']"
            :format="{
              noChecked: '${total}',
              hasChecked: '${checked}/${total}',
            }"
            :filter-method="filterDataset"
            filter-placeholder="搜索数据集"
            :data="DatasetList"
            :props="{
              key: 'id',
              label: 'name'
            }"
          >
            <!-- 自定义源列表项 -->
            <template #default="{ option }">
              <div class="aip-el-transfer-panel__item">
                <i :class="option.icon" style="margin-right: 10px;"></i>
                {{ option.name }}
              </div>
            </template>
          </el-transfer>
        </div>

        <pagination v-show="total > 0" :total="total" :pageSizes="[1000]" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

      </el-col>
    </el-row>

    <div style="display: flex; justify-content: flex-end; width: 100%;">
      <el-button type="primary" @click="handleSubmit" size="large" text bg>确认保存</el-button>
    </div>
  </div>
</template>

<script setup name="Dataset">
import {
  toolSelection,
  catalogManifestTreeSelect,
} from "@/api/base/search/vectorDataset";

import { reactive, ref, computed, nextTick } from "vue";
import { ElMessage } from 'element-plus';
import { ElTransfer } from 'element-plus';
import { getCurrentInstance, toRefs } from 'vue';

const emit = defineEmits(['handleSelectDatasetConfigClose']);

const { proxy } = getCurrentInstance();

const DatasetList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const dateRange = ref([]);
const deptOptions = ref(undefined);

const selectedDatasetIds = ref([]); // 已选择的数据集ID
const defaultSelectedDatasetIds = ref([]); // 用于存储默认的已选择数据集ID

// 列显隐信息（这里不再使用，因为Transfer组件有自己的渲染逻辑）
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
    pageSize: 1000,
    isMemory: 0,
    DatasetName: undefined,
    name: undefined,
    ownerId: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    applicationId: [{ required: true, message: "应用编号不能为空", trigger: "blur" }],
    name: [{ required: true, message: "数据集名称不能为空", trigger: "blur" }, {
      min: 2,
      max: 20,
      message: "数据集名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    ownerId: [{ required: true, message: "显示名称不能为空", trigger: "blur" }],
    description: [{ required: true, message: "描述信息不能为空", trigger: "blur" }],
    datasetStatus: [{ required: true, message: "域名不能为空", trigger: "blur" }],
    accessPermission: [{ required: true, message: "安全存储路径不能为空", trigger: "blur" }],
    datasetSize: [{ required: true, message: "应用目标不能为空", trigger: "blur" }],
  }
});

const { queryParams } = toRefs(data);

/** 查询数据集列表 */
function getList() {
  const savedSelectedDatasetIds = [...selectedDatasetIds.value]; // 保存当前已选数据集的ID
  loading.value = true;

  toolSelection(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    DatasetList.value = res.rows;
    total.value = res.total;

    // 结合默认的selectedDatasetIds和从后端获取的数据来更新selectedDatasetIds
    const newSelectedDatasetIds = DatasetList.value.filter(item => {
      return defaultSelectedDatasetIds.value.includes(item.id) || savedSelectedDatasetIds.includes(item.id);
    }).map(item => item.id);
    selectedDatasetIds.value = newSelectedDatasetIds;
  });
};

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  queryParams.value.name = undefined;
  queryParams.value.ownerId = undefined;
  if (proxy.$refs.deptTreeRef) {
    proxy.$refs.deptTreeRef.setCurrentKey(null);
  }
  handleQuery();
};

/** 过滤数据集 */
function filterDataset(value, item) {
  return item.name.toLowerCase().includes(value.toLowerCase());
}

/** 穿梭框选择变化 */
function handleTransferChange(value) {
  selectedDatasetIds.value = value;
}

const handleSubmit = () => {
  emit("handleSelectDatasetConfigClose", selectedDatasetIds.value);
  ElMessage.success('提交成功');
};

// 设置 selectItemList
function setSelectItemList(items) {
  defaultSelectedDatasetIds.value = items;
  selectedDatasetIds.value = items;
}

getList();

// 假设的 parseTime 函数
const parseTime = (time) => {
  return time || '暂无时间信息';
};

defineExpose({
  setSelectItemList
});
</script>

<style lang="scss" scoped>
.role-icon {
  img {
    width: 35px;
    height: 35px;
    border-radius: 50%;
  }
}

.plugin-catalog {
  background: #fafafa;
  border-radius: 2px;
  padding: 9px;

  .catalog-title {
    padding: 10px;
    font-size: 14px;
    font-weight: bold;
    color: #444;
    margin-bottom: 5px;
  }

  .catalog-content {
    .catalog-item {
      padding: 12px 10px;
      color: #443;
      cursor: pointer;
      margin-bottom: 4px;
      border-radius: 4px;
      font-size: 13px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      &:hover {
        background: #e9eaf3;
      }
    }
  }
}

.aip-el-transfer-panel__item {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 0;
}
</style>

<style>
.dataset-el-transfer-panel .el-transfer-panel__item {
  margin-bottom: 10px;
  display: flex !important;
  align-items: center;
}
</style>