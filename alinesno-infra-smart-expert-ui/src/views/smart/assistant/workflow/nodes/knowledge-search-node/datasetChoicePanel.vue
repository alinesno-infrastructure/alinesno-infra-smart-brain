<template>
  <div>
    <el-row :gutter="20">
      <!-- 应用数据 -->
      <el-col :span="15" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
          <el-form-item label="数据集名称" prop="name">
            <el-input v-model="queryParams['condition[name|like]']" placeholder="请输入应用名称" clearable style="width: 240px"
              @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="DatasetList" @selection-change="handleSelectionChange" ref="tableRef">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="数据集名称" align="left" key="name" prop="name" v-if="columns[1].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                <i :class="scope.row.icon"></i>  {{ scope.row.name }}
              </div>
              <div style="font-size: 13px;color: #a5a5a5;cursor: pointer;" v-copyText="scope.row.id">
                {{ scope.row.description }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="数据总量" align="center" key="datasetSize" prop="datasetSize"
            v-if="columns[6].visible" :show-overflow-tooltip="true" />
          <el-table-column label="最后更新时间" align="center" prop="addTime" v-if="columns[7].visible" width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.addTime) }}</span>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-col>

      <!-- 已选择数据集 -->
      <el-col :span="9" :xs="24">
        <div class="plugin-catalog">
          <div class="catalog-title">
            <i class="fa-solid fa-file-pdf"></i> 已选择数据集 
          </div>
          <div class="catalog-content">
            <div 
              v-for="(item, index) in selectedDatasetList" 
              :key="index" 
              class="catalog-item"
              style="display: flex;justify-content: space-between;gap: 10px;box-shadow: rgba(16, 24, 40, 0.1) 0px 1px 8px -2px, rgba(16, 24, 40, 0.06) 0px 2px 4px -2px;border: 1px solid rgb(223, 226, 234);">
            <span>
              <i :class="item.icon"></i> {{ item.name }}
            </span>
              <el-button type="text" icon="Delete" @click="removeSelectedDataset(index)"></el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <div style="display: flex;justify-content: flex-end;width: 100%;">
        <el-button type="primary" @click="handleSubmit" size="large" text bg>确认保存</el-button>
    </div>

  </div>
</template>

<script setup name="Dataset">
import {
  listDataset,
  catalogManifestTreeSelect,
} from "@/api/base/search/vectorDataset";

import { reactive, ref, nextTick } from "vue";
import { getCurrentInstance, toRefs } from 'vue';
import { ElMessage } from 'element-plus';

const emit = defineEmits(['handleSelectDatasetConfigClose'])

const { proxy } = getCurrentInstance();

const DatasetList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref([]);
const deptOptions = ref(undefined);

const selectItemList = ref([]);  // 已经选择的项
const tableRef = ref(null); // 表格引用
const selectedDatasetList = ref([]); // 已选择数据集列表

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
    isMemory: 0,
    DatasetName: undefined,
    name: undefined,
    ownerId: undefined,
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
    ownerId: [{ required: true, message: "显示名称不能为空", trigger: "blur" }],
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
  console.log(proxy);
  // listDataset(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
  listDataset(queryParams.value).then(res => {
    loading.value = false;
    DatasetList.value = res.rows;
    total.value = res.total;

    nextTick(() => {
      // 数据加载完成后，回显选中状态
      // setTableSelection();
    });
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

/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;

  selection.forEach(item => {

    // if (!selectItemList.value.includes(item.id)) {
    const exists = selectedDatasetList.value.some(selectedItem => selectedItem.id === item.id);
      // selectItemList.value.push(item.id);
    if (!exists) {
      selectedDatasetList.value.push({
        id: item.id,
        icon: item.icon ? 'fa-solid fa-file-pdf' : item.icon ,
        name: item.name
      });
    }
  });
};

// 设置表格的选中状态
// function setTableSelection() {
//   if (tableRef.value) {
//     // 先清空表格的选中状态
//     tableRef.value.clearSelection();
//     DatasetList.value.forEach(item => {
//       if (selectItemList.value.includes(item.id)) {
//         tableRef.value.toggleRowSelection(item, true);

//         // 如果没有 则添加到已选择列表中
//         if (!selectedDatasetList.value.some(i => i.id === item.id)) {
//           selectedDatasetList.value.push({
//             id: item.id,
//             icon: item.icon ? 'fa-solid fa-file-pdf' : item.icon ,
//             name: item.name
//           });
//         }

//       }
//     });
//   }
// }

// 设置表格的选中状态
function setTableSelection() {
  if (tableRef.value) {
    // 先清空表格的选中状态
    tableRef.value.clearSelection();
    DatasetList.value.forEach(item => {
      // 检查 selectedDatasetList 中是否已经存在具有相同 id 的元素
      // const exists = selectedDatasetList.value.some(selectedItem => selectedItem.id === item.id);
      // if (exists) {
      //   // 如果存在，则选中该行
      //   tableRef.value.toggleRowSelection(item, true);
      // } else {
      //   // 如果不存在，则添加到已选择列表中
      //   selectedDatasetList.value.push({
      //     id: item.id,
      //     icon: item.icon ? 'fa-solid fa-file-pdf' : item.icon,
      //     name: item.name
      //   });
      //   // 同时选中该行
      //   tableRef.value.toggleRowSelection(item, true);
      // }
    });
  }
}

// 删除已选择的数据集
function removeSelectedDataset(index) {
  const removedItem = selectedDatasetList.value.splice(index, 1)[0];
  const removedId = DatasetList.value.find(item => item.name === removedItem.name)?.id;
  if (removedId) {
    const idIndex = selectItemList.value.indexOf(removedId);
    if (idIndex > -1) {
      selectItemList.value.splice(idIndex, 1);
    }
  }
  // 重新设置表格选中状态
  setTableSelection();
}

const handleSubmit = () => {
  emit("handleSelectDatasetConfigClose" , getSelectItemList());
  ElMessage.success('提交成功');
};

// 获取 selectItemList 最后的值
function getSelectItemList() {
  // return selectItemList.value;
  return selectedDatasetList.value ; 
}

/** 查询类型下拉树结构 */
function getDeptTree() {
  catalogManifestTreeSelect().then(response => {
    deptOptions.value = response.data;
  });
}

/** 设置选中知识库值 */
function setSelectItemList(items){
  console.log('setSelectItemList = ' + items)
  selectedDatasetList.value = items;

  setTableSelection();
}

getDeptTree();
getList();

defineExpose({
  getSelectItemList ,
  setSelectItemList
});

// 假设的 parseTime 函数
const parseTime = (time) => {
  return time || '暂无时间信息';
};

</script>

<style lang="scss" scoped>
.role-icon {
  display: flex;
  align-items: center;
  justify-content: space-between;

  img {
    width: 30px;
    height: 30px;
    border-radius: 50%;
  }
}

.dataset-description {
  width: 100%;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
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
      padding: 8px 10px;
      color: #443;
      cursor: pointer;
      margin-bottom: 4px;
      border-radius: 4px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      &:hover {
        background: #e9eaf3;
      }
    }
  }
}
</style>