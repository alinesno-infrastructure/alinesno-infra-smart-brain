<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--应用数据-->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
          <el-form-item label="模型名称" prop="modelName">
            <el-input v-model="queryParams['condition[modelName|like]']" placeholder="请输入大模型名称" clearable
              style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="模型类型" prop="providerId">
            <el-input v-model="queryParams['condition[providerId|like]']" placeholder="请输入所属提供商名称" clearable
              style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd">新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
              v-hasPermi="['system:LlmModel:edit']">修改
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
              v-hasPermi="['system:LlmModel:remove']">删除
            </el-button>
          </el-col>

          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="LlmModelList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="名称" align="left" key="icon" prop="icon" width="200px" v-if="columns[0].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <img :src="'http://data.linesno.com/icons/llm/'+scope.row.providerCode+'.png'" alt="图标" style="width: 45px; height: 45px; border-radius: 50%;">
              {{ scope.row.modelName }}
            </template>
          </el-table-column>
          <el-table-column label="模型名称" align="left" key="model" prop="model" v-if="columns[6].visible" :show-overflow-tooltip="true" />

          <el-table-column label="API 地址" align="left" key="apiUrl" prop="apiUrl" v-if="columns[5].visible" :show-overflow-tooltip="true" />
          <el-table-column label="大模型描述" align="center" key="description" prop="description" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              {{ scope.row.modelPermission == 'org' ? '组织' : '私有' }}
            </template>
          </el-table-column>

          <el-table-column label="模型类型" align="center" key="apiKey" prop="apiKey" v-if="columns[4].visible">
            <template #default="scope">
              <el-button type="primary" text bg>
                <i class="fas fa-file-signature"></i> {{ scope.row.modelType }}
              </el-button>
            </template>
          </el-table-column>

          <el-table-column label="操作" align="center" width="100" class-name="small-padding fixed-width"
            v-if="columns[7].visible">
            <template #default="scope">
              <el-tooltip content="查看" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                  v-hasPermi="['system:LlmModel:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                  v-hasPermi="['system:LlmModel:remove']"></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-col>
    </el-row>

    <!-- 添加或修改应用配置对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-form :model="form" :rules="rules" ref="LlmModelRef" label-width="110px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="模型名称" prop="modelName">
              <el-input v-model="form.modelName" placeholder="请输入大模型名称" maxlength="255" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="模型厂商" prop="providerCode">
              <el-radio-group v-model="form.providerCode" @change="selectLLMProvider">
                <el-radio v-for="item in providerOptions" :value="item.code" :label="item.code" :key="item.code">
                  {{ item.displayName }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="模型类型" prop="modelType">
              <el-select v-model="form.modelType" placeholder="请选择模型类型">
                <el-option v-for="item in modelTypeOptions" :label="item.displayName" :value="item.code"
                  :key="item.code">
                  {{ item.displayName }}
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="权限" prop="modelPermission">
              <!-- <el-input v-model="form.description" placeholder="请输入大模型描述" /> -->
              <el-radio-group v-model="form.modelPermission">
                <el-radio :value="'person'" :label="'person'">私有</el-radio>
                <el-radio :value="'org'" :label="'org'">组织</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="模型地址" prop="apiUrl">
              <el-input v-model="form.apiUrl" placeholder="请输入 API 地址" maxlength="255" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="ApiKey" prop="apiKey">
              <el-input v-model="form.apiKey" placeholder="请输入 API 密钥" maxlength="255" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="基础模型" prop="model">
              <el-select 
                v-model="form.model" 
                allow-create
                filterable
                default-first-option
                placeholder="请选择基础模型">

                <el-option v-for="item in baseModelOptions" 
                  :label="item.modelName" 
                  :value="item.modelName"
                  :key="item.modelName">
                  {{ item.modelName }}
                </el-option>

              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup name="LlmModel">
import { getToken } from "@/utils/auth";
import {
  listLlmModel,
  delLlmModel,
  getLlmModel,
  updateLlmModel,
  addLlmModel,
  getAllModelProvidersInfo,
  getAllModelTypesInfo,
} from "@/api/smart/assistant/llmModel";
import { reactive, ref } from "vue";
// import {useRouter, getCurrentInstance} from "vue-router";

const router = useRouter();
const { proxy } = getCurrentInstance();

const LlmModelList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);

const providerOptions = ref(undefined)
const modelTypeOptions = ref(undefined)
const baseModelOptions = ref(undefined)

const deptName = ref("");
const deptOptions = ref(undefined);
const initPassword = ref(undefined);
const postOptions = ref([]);
const roleOptions = ref([]);
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
  url: import.meta.env.VITE_APP_BASE_API + "/system/LlmModel/importData"
});
// 列显隐信息
const columns = ref([
  { key: 0, label: `图标`, visible: true },
  { key: 1, label: `大模型名称`, visible: true },
  { key: 2, label: `大模型描述`, visible: true },
  { key: 3, label: `所属提供商名称`, visible: true },
  { key: 4, label: `API 密钥`, visible: true },
  { key: 5, label: `API 地址`, visible: true },
  { key: 6, label: `模型名称`, visible: true },
  { key: 7, label: `编辑`, visible: true },
]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    modelName: undefined,
    providerId: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    modelName: [{ required: true, message: "大模型名称不能为空", trigger: "blur" }, {
      min: 2,
      max: 255,
      message: "大模型名称长度必须介于 2 和 255 之间",
      trigger: "blur"
    }],
    providerCode: [{ required: true, message: "所属提供商名称不能为空", trigger: "blur" }],
    modelType: [{ required: true, message: "所属模型类型不能为空", trigger: "blur" }],
    apiKey: [{ required: true, message: "API 密钥不能为空", trigger: "blur" }],
    apiUrl: [{ required: true, message: "API 地址不能为空", trigger: "blur" }],
    model: [{ required: true, message: "模型名称不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询应用列表 */
function getList() {
  loading.value = true;
  listLlmModel(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    LlmModelList.value = res.rows;
    total.value = res.total;
  });
};

/** 搜索按钮操作 */
function handleQuery() {
  console.log(queryParams);
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  queryParams.value.modelName = undefined;
  queryParams.value.providerId = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
  const ids = row.id || ids.value;

  proxy.$modal.confirm('是否确认删除大模型编号为"' + ids + '"的数据项？').then(function () {
    return delLlmModel(ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
};

/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 重置操作表单 */
function reset() {
  form.value = {
    id: undefined,
    icon: undefined,
    modelName: undefined,
    description: undefined,
    providerId: undefined,
    apiKey: undefined,
    apiUrl: undefined,
    model: undefined,
  };
  proxy.resetForm("LlmModelRef");
};

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
};

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加大模型";
};

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getLlmModel(id).then(response => {
    form.value = response.data;
    form.value.id = id;
    open.value = true;
    title.value = "修改大模型";
  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["LlmModelRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateLlmModel(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addLlmModel(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
};

/** 获取模型供应商 */
function handleAllModelProvidersInfo() {
  getAllModelProvidersInfo().then(response => {
    providerOptions.value = response.data;
  });

  getAllModelTypesInfo().then(response => {
    modelTypeOptions.value = response.data;
  });
}

/** 选中模型供应商 */
function selectLLMProvider(value) {
  console.log('value = ' + value);

  let selectedItem = null ; 

  for (let i = 0; i < providerOptions.value.length; i++) {
    const item = providerOptions.value[i];
    if (item.code === value) {
      // 这里可以对选中的项进行处理，比如打印信息
      console.log('找到匹配项:', item);
      // 如果想提前结束查找，可添加 break 语句
      selectedItem = item ; 
      break;
    }

  }

  if(selectedItem){  // 查找到角色

    form.value.apiUrl = selectedItem.url;
    baseModelOptions.value = selectedItem.modelList ;

    console.log('form.apiUrl = ' + form.apiUrl + 'baseModelOptions = ' + baseModelOptions);
  }

}

getList();
handleAllModelProvidersInfo();

</script>

<style lang="scss" scoped>
.role-icon {
  img {
    width: 45px;
    height: 45px;
    border-radius: 50%;
  }
}
</style>