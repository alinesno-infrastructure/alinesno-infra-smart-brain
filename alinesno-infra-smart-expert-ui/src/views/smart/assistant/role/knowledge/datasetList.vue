<template>
  <div style="padding-bottom: 30px;">
    <el-row :gutter="20">
      <!--应用数据-->
      <el-col :span="24" :xs="24">
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

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Upload" @click="handleImport"
              v-hasPermi="['system:user:import']">导入</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="RoleKnowledgeList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />

          <el-table-column label="类型" align="center" width="80px" prop="icon" v-if="columns[0].visible">
            <template #default="scope">
              <div class="role-icon" style="font-size: 20px">
                <i class="fa-solid fa-file-pdf" />
              </div>
            </template>
          </el-table-column>

          <el-table-column label="文档名称" align="left" width="300" key="name" prop="name" v-if="columns[1].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
              <div style="font-size: 15px; font-weight: 500; white-space: nowrap;overflow: hidden;text-overflow: ellipsis; ; color: #1d75b0">
                {{ scope.row.documentName }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="文档描述" align="left" key="description" prop="description" v-if="columns[3].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                {{ scope.row.documentDesc }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="数据数量" align="center" width="130" key="documentCount" prop="documentCount"
            v-if="columns[2].visible" :show-overflow-tooltip="true" />
          <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width"
            v-if="columns[8].visible">
            <template #default="scope">
              <el-tooltip content="修改" placement="top" v-if="scope.row.datasetKnowledgeId !== 1">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                  v-hasPermi="['system:RoleKnowledge:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.datasetKnowledgeId !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                  v-hasPermi="['system:RoleKnowledge:remove']"></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-col>
    </el-row>

    <!-- 应用导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="800px" append-to-body>
      <el-row>
        <el-col :span="24">
          <div style="margin-bottom: 20px">
            <el-radio-group v-model="radio1">
              <el-radio :label="item.type" size="large" v-for="(item, index) in splitterTextType" :key="index">
                <div style="padding: 10px">
                  <div><i :class="item.icon"></i> {{ item.name }}</div>
                </div>
              </el-radio>
            </el-radio-group>
          </div>
        </el-col>
      </el-row>
      <el-upload ref="uploadRef" :limit="1" accept=".xlsx,.xls,.ppt,.docx,.doc,.pdf,.pptx,.md" :headers="upload.headers"
        :action="upload.url +'?updateSupport=' +upload.updateSupport +'&datasetId=' +currentDatasetId" 
        :disabled="upload.isUploading" 
        :on-progress="handleFileUploadProgress" 
        :on-success="handleFileSuccess"
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
  </div>
</template>

<script setup name="RoleKnowledge">
import { getToken } from "@/utils/auth";
import {
  listRoleKnowledge,
  delRoleKnowledge,
  addRoleKnowledge,
} from "@/api/smart/assistant/roleKnowledge";

import { reactive } from "vue";
import { getParam } from "@/utils/ruoyi";

const router = useRouter();
const { proxy } = getCurrentInstance();
// const { sys_normal_disable, sys_RoleKnowledge_sex } = proxy.useDict("sys_normal_disable", "sys_RoleKnowledge_sex");

const currentDatasetId = ref("")
const RoleKnowledgeList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);

// const deptName = ref("");
// const deptOptions = ref(undefined);
// const initPassword = ref(undefined);
// const postOptions = ref([]);
// const roleOptions = ref([]);

const splitterTextType = ref([
  {
    id: "1",
    name: "数据集直接分段",
    icon: "fa-solid fa-file-word",
    type: "sp",
  },
  { id: "2", name: "数据集QA拆分", icon: "fa-brands fa-wordpress", type: "qa" },
  {
    id: "3",
    name: "CSV直接导入",
    icon: "fa-solid fa-file-import",
    type: "cvs",
  },
  { id: "4", name: "数据接口导入", icon: "fa-solid fa-file-word", type: "api" },
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
  url: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/roleKnowledge/importData"
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
    datasetId: "",
    // RoleKnowledgeName: undefined,
    // name: undefined,
    // ownerId: undefined,
    // status: undefined,
    // deptId: undefined,
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
  console.log("currentDatasetId = " + currentDatasetId.value);
  queryParams.value.datasetId = currentDatasetId.value

  loading.value = true;
  listRoleKnowledge(
    proxy.addDateRange(queryParams.value, dateRange.value)
  ).then((res) => {
    loading.value = false;
    RoleKnowledgeList.value = res.rows;
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
      return delRoleKnowledge(datasetKnowledgeIds);
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
  proxy.resetForm("RoleKnowledgeRef");
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加应用";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const datasetKnowledgeId = row.id || ids.value;
  getRoleKnowledge(datasetKnowledgeId).then((response) => {
    form.value = response.data;
    form.value.datasetKnowledgeId = datasetKnowledgeId;
    open.value = true;
    title.value = "修改应用";
  });
}

/** 导入按钮操作 */
function handleImport() {
  upload.title = "数据集导入";
  upload.open = true;
}

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
  proxy.$refs["RoleKnowledgeRef"].validate((valid) => {
    if (valid) {
      if (form.value.datasetKnowledgeId != undefined) {
        updateRoleKnowledge(form.value).then((response) => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addRoleKnowledge(form.value).then((response) => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 初始化角色信息 */
function initRoleData(role){
  currentDatasetId.value = role.knowledgeId
  getList();
}

defineExpose({
    initRoleData
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
</style>