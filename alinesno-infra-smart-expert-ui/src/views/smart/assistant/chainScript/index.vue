<template>
  <div class="app-container">
    <el-row :gutter="20">
       <!--应用数据-->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">

          <el-form-item label="渠道名称" prop="name">
            <el-input v-model="queryParams['condition[name|like]']" placeholder="请输入渠道名称" clearable
                      style="width: 240px" @keyup.enter="handleQuery"/>
          </el-form-item>
          <el-form-item label="渠道类型" prop="toolType" label-width="100px">
            <el-input v-model="queryParams['condition[toolType|like]']" placeholder="请输入渠道类型" clearable
                      style="width: 240px" @keyup.enter="handleQuery"/>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
                type="primary"
                plain
                icon="Plus"
                @click="handleAdd"
            >新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                type="success"
                plain
                icon="Edit"
                :disabled="single"
                @click="handleUpdate"
                v-hasPermi="['system:Application:edit']"
            >修改
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                type="danger"
                plain
                icon="Delete"
                :disabled="multiple"
                @click="handleDelete"
                v-hasPermi="['system:Application:remove']"
            >删除
            </el-button>
          </el-col>

          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="ApplicationList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column label="图标" align="center" width="60px" prop="icon" v-if="columns[0].visible">
            <template #default="scope">
              <div class="role-icon" style="font-size: 30px;color:#1d75b0">
                <i v-if="(scope.$index + 1)%3 == 2" class="fa-brands fa-node-js"></i>
                <i v-if="(scope.$index + 1)%3 == 0" class="fa-brands fa-java"></i>
                <i v-if="(scope.$index + 1)%3 == 1" class="fa-brands fa-python"></i>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="脚本名称" align="left" key="scriptName" prop="scriptName" v-if="columns[5].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div style="font-size: 15px;font-weight: 500;color: #1d75b0;">
                {{ scope.row.scriptName }}
              </div>
              <div style="font-size: 13px;color: #a5a5a5;">
                获取到代码生成器脚本请求
              </div>
            </template>
          </el-table-column>
          <el-table-column label="流程标识" align="left" width="250" key="scriptId" prop="scriptId" v-if="columns[1].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div style="font-size: 15px;font-weight: 500;color: #1d75b0;">
                {{ scope.row.scriptId }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="脚本类型" align="center" key="scriptType" width="200" prop="scriptType" v-if="columns[2].visible" :show-overflow-tooltip="true"/>
          <el-table-column label="角色脚本" align="center" width="150"  key="storagePath" prop="storagePath" v-if="columns[5].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button @click="configScript(scope.row)" type="primary" text bg icon="Paperclip">脚本配置</el-button>
            </template>
          </el-table-column>
          <el-table-column label="脚本语言" align="center" key="scriptLanguage" width="120" prop="scriptLanguage" v-if="columns[3].visible" :show-overflow-tooltip="true"/>
          <el-table-column label="状态" align="center" key="hasStatus" width="80" prop="hasStatus" v-if="columns[4].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" text bg icon="Link" v-if="scope.row.hasStauts === '1'">正常</el-button>
              <el-button type="danger" text bg icon="Link" v-if="scope.row.hasStauts === '0'">异常</el-button>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="addTime" v-if="columns[7].visible" width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.addTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width" v-if="columns[8].visible">
            <template #default="scope">
              <el-tooltip content="修改" placement="top" v-if="scope.row.applicationId !== 1">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                           v-hasPermi="['system:Application:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.applicationId !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                           v-hasPermi="['system:Application:remove']"></el-button>
              </el-tooltip>

            </template>
          </el-table-column>
        </el-table>
        <pagination
            v-show="total > 0"
            :total="total"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            @pagination="getList"
        />
      </el-col>
    </el-row>

    <!-- 添加或修改应用配置对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-form :model="form" :rules="rules" ref="ApplicationRef" label-width="80px">
        <el-row v-if="isEditor">
          <el-col :span="12">
            <el-form-item  label="脚本名称" prop="scriptName">
              <el-input v-model="form.scriptName" placeholder="请输入脚本名称" maxlength="50"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="流程标识" prop="scriptId">
              <el-input v-model="form.scriptId" placeholder="请输入流程标识" maxlength="50"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="isEditor">
          <el-col :span="12">
            <el-form-item label="流程类型" prop="scriptType">
              <el-input v-model="form.scriptType" placeholder="请输入流程类型" maxlength="50"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="脚本语言" prop="scriptLanguage">
              <el-input v-model="form.scriptLanguage" placeholder="请输入脚本语言" maxlength="100"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="脚本内容" prop="scriptData">
              <el-input type="textarea" v-model="form.scriptData" resize="none" :rows="textareaRow" placeholder="请输入脚本内容" maxlength="1024"/>
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
    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
      <el-upload
          ref="uploadRef"
          :limit="1"
          accept=".xlsx, .xls"
          :headers="upload.headers"
          :action="upload.url + '?updateSupport=' + upload.updateSupport"
          :disabled="upload.isUploading"
          :on-progress="handleFileUploadProgress"
          :on-success="handleFileSuccess"
          :auto-upload="false"
          drag
      >
        <el-icon class="el-icon--upload">
          <upload-filled/>
        </el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport"/>
              是否更新已经存在的应用数据
            </div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;"
                     @click="importTemplate">下载模板
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

<script setup name="Application">
import {getToken} from "@/utils/auth";
import {
  listApplication,
  delApplication,
  getApplication,
  updateApplication,
  addApplication,
} from "@/api/smart/assistant/chainScript";
import {reactive} from "vue";

const router = useRouter();
const {proxy} = getCurrentInstance();
// const { sys_normal_disable, sys_Application_sex } = proxy.useDict("sys_normal_disable", "sys_Application_sex");

const ApplicationList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);

const textareaRow = ref(10);
const isEditor = ref(true);

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
  headers: {Authorization: "Bearer " + getToken()},
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/system/Application/importData"
});
// 列显隐信息
const columns = ref([
  {key: 0, label: `图标`, visible: true},
  {key: 1, label: `渠道名称`, visible: true},
  {key: 2, label: `渠道类型`, visible: true},
  {key: 3, label: `使用场景`, visible: true},
  {key: 4, label: `状态`, visible: true},
  {key: 5, label: `渠道描述`, visible: true},
  {key: 6, label: `应用目标`, visible: true},
  {key: 7, label: `创建时间`, visible: true},
  {key: 8, label: `编辑`, visible: true},

]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    ApplicationName: undefined,
    name: undefined,
    toolType: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    applicationId: [{required: true, message: "应用编号不能为空", trigger: "blur"}],
    name: [{required: true, message: "渠道名称不能为空", trigger: "blur"}, {
      min: 2,
      max: 20,
      message: "渠道名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    toolType: [{required: true, message: "渠道类型不能为空", trigger: "blur"}],
    scene: [{required: true, message: "使用场景不能为空", trigger: "blur"}],
    hasStatus: [{required: true, message: "状态不能为空", trigger: "blur"}],
    description: [{required: true, message: "渠道描述不能为空", trigger: "blur"}],
    target: [{required: true, message: "应用目标不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules} = toRefs(data);


/** 查询应用列表 */
function getList() {
  loading.value = true;
  listApplication(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    ApplicationList.value = res.rows;
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
  queryParams.value.toolType = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
  const applicationIds = row.id || ids.value;

  proxy.$modal.confirm('是否确认删除应用编号为"' + applicationIds + '"的数据项？').then(function () {
    return delApplication(applicationIds);
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
    toolType: undefined,
    scene: undefined,
    hasStatus: undefined,
    description: undefined,
    target: undefined,
  };
  proxy.resetForm("ApplicationRef");
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
  title.value = "添加应用";
};

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();

  textareaRow.value = 10 ;
  isEditor.value = true ;

  const applicationId = row.id || ids.value;
  getApplication(applicationId).then(response => {
    form.value = response.data;
    form.value.id = applicationId;
    open.value = true;
    title.value = "修改脚本名称";
  });
};

/** 编辑脚本 */
function configScript(row){
  reset();

  textareaRow.value = 20 ;
  isEditor.value = false ;

  const applicationId = row.id || ids.value;
  getApplication(applicationId).then(response => {
    form.value = response.data;
    form.value.id = applicationId;
    open.value = true;
    title.value = "修改脚本";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ApplicationRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateApplication(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addApplication(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
};

getList();
</script>

<style lang="scss" scoped>
.role-icon {
  img {
    width:45px;
    height:45px;
    border-radius: 50%;
  }
}
</style>