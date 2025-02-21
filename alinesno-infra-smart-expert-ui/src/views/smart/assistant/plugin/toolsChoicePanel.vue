<template>
  <div>
    <el-row :gutter="20">
      <!-- 应用数据 -->
      <el-col :span="15" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="工具名称" prop="name">
            <el-input v-model="queryParams['condition[name|like]']" placeholder="请输入工具名称" clearable style="width: 240px"
              @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="ToolList" @selection-change="handleSelectionChange" ref="tableRef">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="图标" align="center" width="50px" prop="icon" v-if="columns[0].visible">
            <template #default="scope">
              <div class="role-icon">
                <img :src="imagePath(scope.row.icon)" style="border-radius: 5px;" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="工具名称" align="left" key="name" prop="name" v-if="columns[1].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                {{ scope.row.name }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="插件类型" align="center" key="pluginType" width="150" prop="pluginType"
            v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              {{ getPluginTypeName(scope.row.pluginType) }}
            </template>
          </el-table-column>
          <el-table-column label="更新时间" align="center" prop="addTime" v-if="columns[7].visible" width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.addTime) }}</span>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-col>

      <!-- 已选择插件 -->
      <el-col :span="9" :xs="24">
        <div class="plugin-catalog">
          <div class="catalog-title">
            <i class="fa-solid fa-screwdriver-wrench"></i> 已选择工具
          </div>

                <el-scrollbar style="height:calc(100% - 122px)">
          <div class="catalog-content">
            <div 
              v-for="(item, index) in hasChoisePluginList" 
              :key="index" 
              class="catalog-item"
              style="display: flex;justify-content: space-between;gap: 10px;box-shadow: rgba(16, 24, 40, 0.1) 0px 1px 8px -2px, rgba(16, 24, 40, 0.06) 0px 2px 4px -2px;border: 1px solid rgb(223, 226, 234);">
            <span>
                <img :src="imagePath(item.icon)" style="border-radius: 5px;width:30px;height:30px;" />
              {{ item.label }}
            </span>
              <el-button type="text" icon="Delete" @click="removeSelectedPlugin(index)"></el-button>
            </div>
          </div>
            </el-scrollbar> 
        </div>
      </el-col>

    </el-row>
  </div>
</template>

<script setup name="Tool">
import {
  listTool,
} from "@/api/smart/assistant/tool";
import { reactive, ref, computed, nextTick } from "vue";
// import { useRouter } from "vue-router";
import { getCurrentInstance, toRefs } from 'vue';

const { proxy } = getCurrentInstance();

const ToolList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref([]);

const selectItemList = ref([]);  // 已经选择的项
const tableRef = ref(null); // 表格引用
const hasChoisePluginList = ref([]); // 已选择插件列表

// 列显隐信息
const columns = ref([
  { key: 0, label: `图标`, visible: true },
  { key: 1, label: `工具名称`, visible: true },
  { key: 2, label: `插件类型`, visible: true },
  { key: 3, label: `使用场景`, visible: true },
  { key: 4, label: `状态`, visible: true },
  { key: 5, label: `工具描述`, visible: true },
  { key: 6, label: `应用目标`, visible: true },
  { key: 7, label: `创建时间`, visible: true },
  { key: 8, label: `编辑`, visible: true },
]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    toolName: undefined,
    name: undefined,
    pluginType: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    applicationId: [{ required: true, message: "应用编号不能为空", trigger: "blur" }],
    name: [{ required: true, message: "工具名称不能为空", trigger: "blur" }, {
      min: 2,
      max: 20,
      message: "工具名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    pluginType: [{ required: true, message: "插件类型不能为空", trigger: "blur" }],
    screen: [{ required: true, message: "使用场景不能为空", trigger: "blur" }],
    hasStatus: [{ required: true, message: "状态不能为空", trigger: "blur" }],
    description: [{ required: true, message: "工具描述不能为空", trigger: "blur" }],
    target: [{ required: true, message: "应用目标不能为空", trigger: "blur" }],
  }
});

const { queryParams } = toRefs(data);

// 处理分类项点击事件
// const handleCategoryClick = (category) => {
//   console.log(`点击了分类：${category}`);
// };

/** 查询应用列表 */
function getList() {
  loading.value = true;
  listTool(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    ToolList.value = res.rows;
    total.value = res.total;

    nextTick(() => {
      // 数据加载完成后，回显选中状态
      setTableSelection();
    });

  });
};

/** 获取插件类型名称 */
const getPluginTypeName = computed(() => {
  return (key) => {
    // 这里假设插件类型数据有正确的映射，暂时简化处理
    return key || '未知类型';
  };
});

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
  queryParams.value.pluginType = undefined;
  // 这里假设 proxy.$refs.deptTreeRef 存在
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
    if (!selectItemList.value.includes(item.id)) {
      selectItemList.value.push(item.id);
      hasChoisePluginList.value.push({
        icon : item.icon || 'fa-solid fa-question', // 假设 code 存在，没有则用默认图标
        label: item.name
      });
    }
  });

  console.log('selectItemList = ' + selectItemList.value);
};

// 设置表格的选中状态
function setTableSelection() {
  if (tableRef.value) {
    // 先清空表格的选中状态
    tableRef.value.clearSelection();
    ToolList.value.forEach(item => {
      if (selectItemList.value.includes(item.id)) {
        tableRef.value.toggleRowSelection(item, true);
      }
    });
  }
}

// 删除已选择的插件
function removeSelectedPlugin(index) {
  const removedItem = hasChoisePluginList.value.splice(index, 1)[0];
  const removedId = ToolList.value.find(item => item.name === removedItem.label)?.id;
  if (removedId) {
    const idIndex = selectItemList.value.indexOf(removedId);
    if (idIndex > -1) {
      selectItemList.value.splice(idIndex, 1);
    }
  }
  // 重新设置表格选中状态
  setTableSelection();
}

// 获取 selectItemList 最后的值
function getSelectItemList() {
  return selectItemList.value;
}

getList();

defineExpose({
  getSelectItemList
});

// 假设的 imagePath 和 parseTime 函数
// const imagePath = (icon) => {
//   return icon || 'default-image-url';
// };

const parseTime = (time) => {
  return time || '暂无时间信息';
};

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
  // height: calc(99vh - 100px);
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
</style>