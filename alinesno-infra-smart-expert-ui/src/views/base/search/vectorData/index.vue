<template>
  <div class="app-container">
    <el-row :gutter="20">

      <!--类型数据-->
      <el-col :span="4" :xs="24">
        <div class="head-container">
          <el-input v-model="deptName" placeholder="请输入类型名称" clearable prefix-icon="Search"
            style="margin-bottom: 20px" />
        </div>
        <div class="head-container">
          <el-tree :data="deptOptions" :props="{ label: 'label', children: 'children' }" :expand-on-click-node="false"
            :filter-node-method="filterNode" ref="deptTreeRef" node-key="id" highlight-current
            @node-click="handleNodeClick" />
        </div>
      </el-col>

      <!--应用数据-->
      <el-col :span="20" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">

          <el-form-item label="数据集名称" prop="name">
            <el-input v-model="queryParams['condition[name|like]']" placeholder="请输入应用名称" clearable style="width: 240px"
               />
          </el-form-item>

          <el-form-item label="访问权限" prop="accessPermission" label-width="100px">
            <el-radio-group v-model="queryParams.accessPermission" label="数据范围" label-width="100px" @change="handleQuery">
              <el-radio v-for="item in dataScopeOptions" 
                :key="item.value" 
                :label="item.value">

                {{ item.text }}

              </el-radio>
            </el-radio-group>
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
              v-hasPermi="['system:Dataset:edit']">修改
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
              v-hasPermi="['system:Dataset:remove']">删除
            </el-button>
          </el-col>

          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="DatasetList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />

          <el-table-column width="70px" align="center" label="图标" v-if="columns[0].visible">
            <template #default="scope">
              <div class="doc-icon">
                <i v-if="scope.row.icon" :class="scope.row.icon"></i>   
                <i v-else class="fa-solid fa-file-word"></i> 
              </div>
            </template>
          </el-table-column>

          <el-table-column label="数据集名称" align="left" key="name" prop="name" v-if="columns[1].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                <router-link style="font-size:15px;font-weight: bold;" :to="'/base/search/vectorData/parseDataset?datasetId=' + scope.row.id">
                {{ scope.row.name }}
                </router-link>
              </div>
              <div style="font-size: 13px;color: #a5a5a5;cursor: pointer;" v-copyText="scope.row.id">
                {{ scope.row.description }}
              </div>
            </template>
          </el-table-column>
  
          <el-table-column label="访问权限" align="center" width="130" key="accessPermission" prop="accessPermission"
            v-if="columns[5].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <!-- <el-button type="danger" text bg icon="Link">{{ scope.row.accessPermission }}</el-button> -->
                <el-button text bg type="primary">
                  <span v-if="scope.row.accessPermission == 'person'">私有</span>
                  <span v-if="scope.row.accessPermission == 'org'">组织</span>
                  <span v-if="scope.row.accessPermission == 'public'">公开</span>
                </el-button>
            </template>
          </el-table-column>


          <el-table-column label="数据总量" align="center" width="130" key="datasetSize" prop="datasetSize"  v-if="columns[6].visible" :show-overflow-tooltip="true" />
          <el-table-column label="创建时间" align="center" prop="addTime" v-if="columns[7].visible" width="160">

            <template #default="scope">
              <span>{{ parseTime(scope.row.addTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width"
            v-if="columns[8].visible">
            <template #default="scope">
              <el-tooltip content="修改" placement="top" v-if="scope.row.applicationId !== 1">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                  v-hasPermi="['system:Dataset:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.applicationId !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                  v-hasPermi="['system:Dataset:remove']"></el-button>
              </el-tooltip>

            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-col>
    </el-row>

    <!-- 添加或修改应用配置对话框 -->
    <el-dialog :title="title" v-model="open" width="980px" append-to-body :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" :label-position="labelPosition" ref="DatasetRef" label-width="100px" size="large">

                  <el-row>
          <el-col :span="24">
            <el-form-item label="配置模式" prop="configType">
              <el-radio-group v-model="form.configType" size="large" >
                <el-radio v-for="option in configTypeOptions" :key="option.code" :value="option.code" :label="option.label" border class="custom-radio" >
              

                               <template #default>
            <div class="radio-content"> 
              <div class="radio-info">
                <div class="radio-title">{{ option.text }}</div>
                <div class="radio-desc">{{ option.desc }}</div>
              </div>
            </div>
          </template>

                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="图标" prop="icon">
              <el-radio-group v-model="form.icon">
                <el-radio v-for="item in icons"
                  :value="item.icon"
                  :key="item.icon"
                  :label="item.icon"
                  >
                  <i :class="item.icon"></i>
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入应用名称" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="访问权限" prop="accessPermission">
              <el-radio-group v-model="form.accessPermission" size="large">
                <el-radio v-for="option in dataScopeOptions" :key="option.code" :value="option.code" :label="option.label" border
          class="custom-radio">

                <template #default>
            <div class="radio-content"> 
              <div class="radio-info">
                <div class="radio-title">{{ option.text }}</div>
                <div class="radio-desc">{{ option.desc }}</div>
              </div>
            </div>
          </template>
               
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>


 
         <!-- 向量模型模板选择项 -->
            <el-row v-if="form.configType == 'constom'">
          <el-col :span="17">
        <el-form-item label="向量模型" prop="embeddingModelId">
          <LLMSelector :modelType="'vector_model'" v-model="form.embeddingModelId" />
        </el-form-item>
        </el-col>
      

        <!-- 文档识别模板选择项 -->
        
          <el-col :span="17">
        <el-form-item label="文档识别" prop="documentRecognitionModelId">
          <LLMSelector :modelType="'large_language_model'" v-model="form.documentRecognitionModelId" />
        </el-form-item>
        </el-col>
    <!-- OCR模型选择项 -->
          <el-col :span="17">

        <el-form-item label="图片识别" prop="ocrModelId">
          <LLMSelector :modelType="'ocr_model'" v-model="form.ocrModelId" />
        </el-form-item> 
        </el-col> 

     
        
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="描述信息" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" resize="none" placeholder="请输入描述信息" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" size="large" @click="submitForm">确 定</el-button>
          <el-button size="large"  @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup name="Dataset">
import { getToken } from "@/utils/auth";
import {
  listDataset,
  delDataset,
  getDataset,
  updateDataset,
  catalogManifestTreeSelect,
  addDataset,
} from "@/api/base/search/vectorDataset";
import { reactive } from "vue";

import LLMSelector from '@/views/smart/assistant/workflow/components/LLMSelector'
import { getLlmIconPath } from '@/utils/llmIcons';

const router = useRouter();
const { proxy } = getCurrentInstance();
// const { sys_normal_disable, sys_Dataset_sex } = proxy.useDict("sys_normal_disable", "sys_Dataset_sex");

const dataSplitting = ref([
  { "code": "direct_segmentation", "name": "直接分段" },
  { "code": "enhanced_processing", "name": "增强处理" },
  { "code": "qa_split", "name": "问答拆分" }
]);

const DatasetList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const deptName = ref("");
const deptOptions = ref(undefined);
const labelPosition = ref("right")

const dataScopeOptions = ref([
  { value: 'person', label: 'person', text: '私有', desc: '此数据仅你个人可见和使用' },
  { value: 'org', label: 'org', text: '组织', desc: '此数据可在组织内部共享和使用' },
  { value: 'public', label: 'public', text: '公开', desc: '此数据可被所有人访问和查看' }
]);
 
const configTypeOptions = ref([
  { value: 'default', label: 'default', text: '默认', desc: '将使用系统默认的模型配置' },
  { value: 'constom', label: 'constom', text: '高级', desc: '通过高级配置可以自定义识别模型' }, 
]);

const icons = ref([
  { id: 1, icon: 'fa-solid fa-file-word'} ,
  { id: 1, icon: 'fa-solid fa-truck'} ,
  { id: 2, icon: 'fa-solid fa-paper-plane'} ,
  { id: 2, icon: 'fa-solid fa-ship'} ,
  { id: 3, icon: 'fa-solid fa-chart-column'},
  { id: 4, icon: 'fa-solid fa-server'}, 
  { id: 5, icon: 'fa-solid fa-box-open'}, 
  { id: 8, icon: 'fa-solid fa-file-invoice-dollar'}, 
  { id: 9, icon: 'fa-solid fa-user-tie'},
]);

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
  url: import.meta.env.VITE_APP_BASE_API + "/system/Dataset/importData"
});
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
  form: {
    configType: 'default',
  },
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
    icon: [{ required: true, message: "图标不能为空", trigger: "blur" }],
    ownerId: [{ required: true, message: "显示名称不能为空", trigger: "blur" }],
    description: [{ required: true, message: "描述信息不能为空", trigger: "blur" }],
    datasetStatus: [{ required: true, message: "域名不能为空", trigger: "blur" }],
    accessPermission: [{ required: true, message: "数据范围不能为空", trigger: "blur" }],
    datasetSize: [{ required: true, message: "应用目标不能为空", trigger: "blur" }],

    configType: [{ required: true, message: "配置模式不能为空", trigger: "blur" }],
    embeddingModelId: [{ required: true, message: "向量模型不能为空", trigger: "blur" }],
    documentRecognitionModelId: [{ required: true, message: "文档识别不能为空", trigger: "blur" }],
    ocrModelId: [{ required: true, message: "图片识别不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);


/** 查询应用列表 */
function getList() {
  loading.value = true;
  listDataset(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    DatasetList.value = res.rows;
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
  queryParams.value.name = undefined;
  queryParams.value.ownerId = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
  const applicationIds = row.id || ids.value;

  proxy.$modal.confirm('是否确认删除应用编号为"' + applicationIds + '"的数据项？').then(function () {
    return delDataset(applicationIds);
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
    applicationId: undefined,
    name: undefined,
    configType: 'default',
    ownerId: undefined,
    description: undefined,
    datasetStatus: undefined,
    accessPermission: undefined,
    datasetSize: undefined,
  };
  proxy.resetForm("DatasetRef");
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
  title.value = "添加数据集";
};

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const applicationId = row.id || ids.value;
  getDataset(applicationId).then(response => {
    form.value = response.data;
    form.value.applicationId = applicationId;
    open.value = true;
    title.value = "修改应用";

  });
};

/** 导入按钮操作 */
function handleImport() {
  upload.title = "数据集导入";
  upload.open = true;
};

/** 下载模板操作 */
function importTemplate() {
  proxy.download("system/user/importTemplate", {
  }, `user_template_${new Date().getTime()}.xlsx`);
};

/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true;
};
/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].handleRemove(file);
  proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
  getList();
};
/** 提交上传文件 */
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
};

/** 查询类型下拉树结构 */
function getDeptTree() {
  catalogManifestTreeSelect().then(response => {
    deptOptions.value = response.data;
  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["DatasetRef"].validate(valid => {
    if (valid) {
      if (form.value.applicationId != undefined) {
        updateDataset(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addDataset(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
};

getDeptTree();
getList();

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

.doc-icon {
  font-size: 22px;
  background: #1d75b0;
  color: #fff;
  border-radius: 25%;
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0px 0px 12px rgba(0, 0, 0, .12);
}

.dataset-description {
  width: 100%;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}

.custom-radio {
    display: block;
    margin-bottom: 12px;
    padding: 5px !important;
    height: auto;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    width: 250px ; 
    margin-right: 10px !important; 
}

  .radio-content {
    display: flex; 
    align-items: center;
    width:230px;
    line-height: 1.5rem;
  
    .radio-info {
      flex-grow: 1;
      min-width: 0;
      
      .radio-title {
        font-weight: 500;
        margin-bottom: 4px; 
        display: flex;
        align-items: center;
        gap: 10px;
      }
      
      .radio-desc { 
        line-height: 1.5;
        max-width: 800px;
        white-space: normal; 
        display: -webkit-box;
        -webkit-line-clamp: 2; // 最多显示2行
        -webkit-box-orient: vertical;
        overflow: hidden; // 超出部分隐藏
        text-overflow: ellipsis; // 显示省略号
      }
    }
  }
</style>