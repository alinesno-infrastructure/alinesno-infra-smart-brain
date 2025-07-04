<template>
  <div>
    <el-row :gutter="20">
      <!-- 应用数据 -->
      <el-col :span="24" :xs="24">
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

        <div class="tool-el-transfer-panel">
          <el-transfer
            v-model="selectedToolIds"
            filterable
            style="text-align: left; width:100%; display: inline-block"
            :titles="['所有工具', '已选择工具']"
            :format="{
              noChecked: '${total}',
              hasChecked: '${checked}/${total}',
            }"
            :filter-method="filterTool"
            filter-placeholder="搜索工具"
            :data="ToolList"
            :props="{
              key: 'id',
              label: 'name'
            }"
          >
            <!-- 自定义源列表项 -->
            <template #default="{ option }">
              <div class="aip-el-transfer-panel__item">

                <img :src="imagePath(option.icon)" 
                  @error="handleImageError" 
                  style="border-radius: 50%; width: 30px; height: 30px;" />
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

<script setup name="Tool">
import {
  listTool,
  toolSelection ,
} from "@/api/smart/assistant/tool";
import { reactive, ref, computed, nextTick } from "vue";
import { ElMessage } from 'element-plus';
import { ElTransfer } from 'element-plus';
import { getCurrentInstance, toRefs } from 'vue';
import defaultImage from '@/assets/default-avatar.png' // 导入默认图片

const emit = defineEmits(['handleSelectToolsConfigClose']);

const { proxy } = getCurrentInstance();

const ToolList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const dateRange = ref([]);

const selectedToolIds = ref([]); // 已选择的工具ID
const defaultSelectedToolIds = ref([]); // 用于存储默认的已选择工具ID

const fallbackImage = ref(defaultImage)

// 列显隐信息（这里不再使用，因为Transfer组件有自己的渲染逻辑）
const columns = ref([
  { key: 0, label: `图标`, visible: true },
  { key: 1, label: `工具名称`, visible: true },
  { key: 2, label: `工具类型`, visible: true },
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
    pageSize: 1000,
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
    pluginType: [{ required: true, message: "工具类型不能为空", trigger: "blur" }],
    scene: [{ required: true, message: "使用场景不能为空", trigger: "blur" }],
    hasStatus: [{ required: true, message: "状态不能为空", trigger: "blur" }],
    description: [{ required: true, message: "工具描述不能为空", trigger: "blur" }],
    target: [{ required: true, message: "应用目标不能为空", trigger: "blur" }],
  }
});

const { queryParams } = toRefs(data);

/** 查询应用列表 */
function getList() {
  console.log('.selectedToolIds.value = ' + JSON.stringify(selectedToolIds.value))
  const savedSelectedToolIds = [...selectedToolIds.value]; // 保存当前已选工具的ID
  loading.value = true;

  toolSelection(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {

    loading.value = false;
    ToolList.value = res.rows;
    total.value = res.total;
    console.log('ToolList:', ToolList.value); // 打印获取到的工具列表

    // 结合默认的selectedToolIds和从后端获取的数据来更新selectedToolIds
    const newSelectedToolIds = ToolList.value.filter(item => {
      return defaultSelectedToolIds.value.includes(item.id) || savedSelectedToolIds.includes(item.id);
    }).map(item => item.id);
    selectedToolIds.value = newSelectedToolIds;
    console.log('重新标记后的 selectedToolIds:', selectedToolIds.value);
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
  queryParams.value.pluginType = undefined;
  // 这里假设 proxy.$refs.deptTreeRef 存在
  if (proxy.$refs.deptTreeRef) {
    proxy.$refs.deptTreeRef.setCurrentKey(null);
  }
  handleQuery();
};

/** 过滤工具 */
function filterTool(value, item) {
  if(item){
    return item?.name?.toLowerCase().includes(value.toLowerCase());
  }
}

// 图片加载错误处理
const handleImageError = (event) => {
  event.target.src = fallbackImage.value
}

/** 穿梭框选择变化 */
function handleTransferChange(value) {
  selectedToolIds.value = value;
  console.log('Selected Tool Ids:', selectedToolIds.value); // 打印选中的工具ID
}

const handleSubmit = () => {
  emit("handleSelectToolsConfigClose", selectedToolIds.value);
  ElMessage.success('提交成功');
};

// 设置 selectItemList
function setSelectItemList(items) {
  console.log('Setting selectItemList:', JSON.stringify(items));
  defaultSelectedToolIds.value = items;
  selectedToolIds.value = items;
  console.log('Default selected tool ids:', JSON.stringify(selectedToolIds.value ));
}

getList();

// 假设的 imagePath 和 parseTime 函数
// const imagePath = (icon) => {
//   return icon || 'default-image-url';
// };

// const parseTime = (time) => {
//   return time || '暂无时间信息';
// };

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

.aip-el-transfer-panel__item {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 0;
}
</style>

<style>
.tool-el-transfer-panel .el-transfer-panel__item {
  margin-bottom: 10px;
  display: flex !important;
  align-items: center;
}

</style>