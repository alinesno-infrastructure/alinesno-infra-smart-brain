<template>
  <div style="padding-bottom: 30px;">
    <el-row :gutter="20">
      <!--应用数据-->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
          <el-form-item label="数据集名称" prop="name">
            <el-input v-model="queryParams.documentName" placeholder="请输入应用名称" clearable style="width: 240px"
               />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <router-link :to="'/base/search/vectorData/dataUpload?datasetId=' + currentDatasetId" >
              <el-button type="primary" plain icon="Upload">数据集上传</el-button>
            </router-link>
          </el-col>
          <el-col :span="1.5">
            <router-link :to="'/base/search/vectorData/websiteImport?datasetId=' + currentDatasetId" >
              <el-button type="warning" plain icon="Link">Web站点同步</el-button>
            </router-link>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="DatasetKnowledgeList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />

          <el-table-column label="文档名称" align="left" key="name" prop="name" v-if="columns[1].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
              <div style="white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                <i class="fa-solid fa-file-pdf" style="font-size:20px" />
                <router-link :to="'/base/search/vectorData/knowledgeDetail?id=' + scope.row.id + '&datasetId=' + currentDatasetId" >
                  {{ scope.row.documentName }}
                </router-link>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="测试" align="center" width="300" key="datasetStatus" prop="datasetStatus" v-if="columns[4].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" @click="handleTestSearch(scope.row.id)" text icon="Search">向量测试</el-button>
            </template>
          </el-table-column>

          <el-table-column label="分割模式" align="center" key="description" prop="description" v-if="columns[3].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-if="scope.row.processingMethod == 'direct_segmentation'">
                直接分段
              </div>
            </template>
          </el-table-column>
          <el-table-column label="数据数量" align="center" width="130" key="documentCount" prop="documentCount"
            v-if="columns[2].visible" :show-overflow-tooltip="true" />
          <el-table-column label="创建/更新时间" align="center" prop="addTime" v-if="columns[7].visible" width="200">
            <template #default="scope">
              <div style="display: flex;flex-direction: column;">
                <span>{{ parseTime(scope.row.addTime) }}</span>
                <span>{{ parseTime(scope.row.updateTime) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" width="300" key="datasetStatus" prop="datasetStatus" v-if="columns[4].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button v-if="scope.row.status === 'import_in_progress'" type="warning" text bg>
                  <i class="fa-solid fa-spinner fa-spin" aria-hidden="true"></i>
                  <span class="status-text">导入中</span>
              </el-button>
              <el-button v-else-if="scope.row.status === 'import_not_completed'" type="info" text bg>
                  <i class="fa-solid fa-circle-xmark" aria-hidden="true"></i>
                  <span class="status-text">未导入</span>
              </el-button>
              <el-button v-else-if="scope.row.status === 'import_completed'" type="success" text bg>
                  <i class="fa-solid fa-check-circle" aria-hidden="true"></i>
                  <span class="status-text">导入完成</span>
              </el-button>
              <el-button v-else-if="scope.row.status === 'import_failed'" type="danger" text bg>
                  <i class="fa-solid fa-exclamation-triangle" aria-hidden="true"></i>
                  <span class="status-text">导入失败</span>
              </el-button>
              <el-button v-else type="default" text bg>
                  <span class="status-text">{{ scope.row.status }}</span>
              </el-button>
          </template>
          </el-table-column>

          <el-table-column label="开启" align="center" width="100" key="hasStatus" prop="hasStatus" v-if="columns[1].visible" :show-overflow-tooltip="true" >
            <template #default="scope">
                <el-switch
                  v-model="scope.row.hasStatus"
                  :active-value="0"
                  :inactive-value="1"
                  @change="handleChangStatusField('hasStatus' , scope.row.hasStatus, scope.row.id)"
                />
            </template>
          </el-table-column>

          <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width"
            v-if="columns[8].visible">
            <template #default="scope">
              <el-tooltip content="修改" placement="top" v-if="scope.row.datasetKnowledgeId !== 1">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                  v-hasPermi="['system:DatasetKnowledge:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.datasetKnowledgeId !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                  v-hasPermi="['system:DatasetKnowledge:remove']"></el-button>
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
      <el-form :model="form" :rules="rules" ref="DatasetKnowledgeRef" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="文档名称" prop="documentName">
              <el-input v-model="form.documentName" placeholder="请输入文档名称" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="数据集数量" prop="ownerId">
              <el-input v-model="form.ownerId" placeholder="请输入数据集数据" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="状态" prop="datasetStatus">
              <el-input v-model="form.datasetStatus" placeholder="请输入域名" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="描述信息" prop="description">
              <el-input v-model="form.description" placeholder="请输入描述信息" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="访问权限" prop="accessPermission">
              <el-input v-model="form.accessPermission" placeholder="请输入安全存储路径" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="数据总量" prop="datasetSize">
              <el-input v-model="form.datasetSize" placeholder="请输入应用目标" maxlength="20" />
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

    <!-- 应用导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="800px" append-to-body>
      <el-row>
        <el-col :span="24">
          <div style="margin-bottom: 20px">
            <el-radio-group v-model="splitterType">
              <el-radio 
                :label="item.code" 
                :value="item.code"
                size="large" 
                v-for="(item, index) in splitterTextType" 
                :key="index" 
                border>
                <div>
                  <div style="display: flex;align-items: center;gap: 3px;">
                    <i :class="item.icon"></i>{{ item.name }} <el-icon><QuestionFilled /></el-icon>
                  </div>
                </div>
              </el-radio>
            </el-radio-group>
          </div>
        </el-col>
      </el-row>
      <el-upload ref="uploadRef" :limit="1" accept=".xlsx,.xls,.ppt,.docx,.doc,.pdf,.pptx,.md" :headers="upload.headers"
        :action="upload.url +
          '?updateSupport=' +
          upload.updateSupport +
          '&datasetId=' +
          currentDatasetId
          " :disabled="upload.isUploading" :on-progress="handleFileUploadProgress" :on-success="handleFileSuccess"
        :auto-upload="false" drag>
        <el-icon class="el-icon--upload">
          <upload-filled />
        </el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport" />
              <span> 是否更新已经存在的应用数据 </span>
            </div>
            <span>支持 .txt, .doc, .docx, .pdf, .md 文件。</span>
            <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline"
              @click="importTemplate">自定义文件内容模板下载
            </el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog :title="'向量测试'" v-model="vectorTestDialog" width="80%" append-to-body>
      <DatasetSearch ref="datasetSearchRef" />
    </el-dialog>

  </div>
</template>

<script setup name="DatasetKnowledge">
import { getToken } from "@/utils/auth";
import {
  listDatasetKnowledge,
  delDatasetKnowledge,
  getDatasetKnowledge,
  updateDatasetKnowledge,
  addDatasetKnowledge,
  changStatusField,
} from "@/api/base/search/datasetKnowledge";

import { reactive } from "vue";
import { getParam } from "@/utils/ruoyi";

import DatasetSearch from './datasetSearch';

const router = useRouter();
const { proxy } = getCurrentInstance();
// const { sys_normal_disable, sys_DatasetKnowledge_sex } = proxy.useDict("sys_normal_disable", "sys_DatasetKnowledge_sex");

const currentDatasetId = getParam("datasetId");
const DatasetKnowledgeList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);

const splitterType = ref(null);
const datasetSearchRef = ref(null);

const deptName = ref("");
const deptOptions = ref(undefined);
const initPassword = ref(undefined);
const postOptions = ref([]);
const roleOptions = ref([]);

const vectorTestDialog = ref(false);

const splitterTextType = ref([
  {"code": "direct_segmentation", "name": "直接分段"},
  {"code": "enhanced_processing", "name": "增强处理"},
  {"code": "qa_split", "name": "问答拆分"}
]);

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
  url:
    import.meta.env.VITE_APP_BASE_API +
    "/api/infra/base/search/datasetKnowledge/importData",
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
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    datasetId: getParam("datasetId"),
    DatasetKnowledgeName: undefined,
    name: undefined,
    ownerId: undefined,
    status: undefined,
    deptId: undefined,
  },
  rules: {
    datasetKnowledgeId: [
      { required: true, message: "应用编号不能为空", trigger: "blur" },
    ],
    name: [
      { required: true, message: "应用名称不能为空", trigger: "blur" },
      {
        min: 2,
        max: 20,
        message: "应用名称长度必须介于 2 和 20 之间",
        trigger: "blur",
      },
    ],
    ownerId: [{ required: true, message: "显示名称不能为空", trigger: "blur" }],
    description: [
      { required: true, message: "描述信息不能为空", trigger: "blur" },
    ],
    datasetStatus: [
      { required: true, message: "域名不能为空", trigger: "blur" },
    ],
    accessPermission: [
      { required: true, message: "安全存储路径不能为空", trigger: "blur" },
    ],
    datasetSize: [
      { required: true, message: "应用目标不能为空", trigger: "blur" },
    ],
  },
});

const { queryParams, form, rules } = toRefs(data);

/** 查询应用列表 */
function getList() {
  console.log("currentDatasetId = " + currentDatasetId);

  loading.value = true;
  listDatasetKnowledge(
    proxy.addDateRange(queryParams.value, dateRange.value)
  ).then((res) => {
    loading.value = false;
    DatasetKnowledgeList.value = res.rows;
    total.value = res.total;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  console.log(queryParams);
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  queryParams.value.name = undefined;
  queryParams.value.ownerId = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
}

/** 删除按钮操作 */
function handleDelete(row) {
  const datasetKnowledgeIds = row.id || ids.value;

  proxy.$modal
    .confirm('是否确认删除应用编号为"' + datasetKnowledgeIds + '"的数据项？')
    .then(function () {
      return delDatasetKnowledge(datasetKnowledgeIds);
    })
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => { });
}

/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 重置操作表单 */
function reset() {
  form.value = {
    datasetKnowledgeId: undefined,
    name: undefined,
    ownerId: undefined,
    description: undefined,
    datasetStatus: undefined,
    accessPermission: undefined,
    datasetSize: undefined,
  };
  proxy.resetForm("DatasetKnowledgeRef");
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 新增按钮操作 */
// function handleAdd() {
//   reset();
//   open.value = true;
//   title.value = "添加应用";
// }

 /** 修改状态 */
 const handleChangStatusField = async(field , value , id) => {
    // 判断tags值 这样就不会进页面时调用了
      const res = await changStatusField({
         field: field,
         value: value?1:0,
         id: id
      }).catch(() => { })
      if (res && res.code == 200) {
         // 刷新表格
         getList()
      }
 }

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const datasetKnowledgeId = row.id || ids.value;
  getDatasetKnowledge(datasetKnowledgeId).then((response) => {
    form.value = response.data;
    form.value.datasetKnowledgeId = datasetKnowledgeId;
    open.value = true;
    title.value = "修改应用";
  });
}

/** 导入按钮操作 */
// function handleImport() {
//   // upload.title = "数据集导入";
//   // upload.open = true;
// }

/** 下载模板操作 */
function importTemplate() {
  proxy.download(
    "system/user/importTemplate",
    {},
    `user_template_${new Date().getTime()}.xlsx`
  );
}

/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true;
};
/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].handleRemove(file);
  proxy.$alert(
    "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" +
    response.msg +
    "</div>",
    "导入结果",
    { dangerouslyUseHTMLString: true }
  );
  getList();
};
/** 提交上传文件 */
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["DatasetKnowledgeRef"].validate((valid) => {
    if (valid) {
      if (form.value.datasetKnowledgeId != undefined) {
        updateDatasetKnowledge(form.value).then((response) => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addDatasetKnowledge(form.value).then((response) => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 向量测试 */
function handleTestSearch(item) {
  vectorTestDialog.value = true ;
}

getList();
</script>

<style lang="scss" scoped>
.role-icon {
  img {
    width: 45px;
    height: 45px;
    border-radius: 50%;
  }
}

.status-text {
    display: inline-block;
    width: 60px; /* 可按需调整宽度 */
    text-align: center;
}
</style>