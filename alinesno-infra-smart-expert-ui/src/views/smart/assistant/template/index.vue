<template>
  <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
         <el-form-item label="指令名称" prop="templateName">
            <el-input v-model="queryParams.templateName" placeholder="请输入指令名称" clearable style="width: 240px"
               @keyup.enter="handleQuery" />
         </el-form-item>
         <el-form-item label="指令名称" prop="templateName">
            <el-input v-model="queryParams['condition[templateName|like]']" placeholder="请输入指令名称" clearable
               style="width: 240px" @keyup.enter="handleQuery" />
         </el-form-item>
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">

         <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleImport">新增</el-button>
         </el-col>
         <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
         </el-col>

         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="TemplateList" @selection-change="handleSelectionChange">
         <el-table-column type="selection" width="50" :align="'center'" />

         <el-table-column label="图标" align="center" width="60" key="format" prop="format" v-if="columns[5].visible">
            <template #default="scope">
               <!-- <img :src="'http://data.linesno.com/icons/fileType/' + scope.row.templateType" style="width:35px;" /> -->
               <span style="font-size: 1.6rem;color:#3b5998">
                  <i v-if="scope.row.templateType == 'pdf'" class="fa-solid fa-file-pdf"></i>
                  <i v-if="scope.row.templateType == 'xml'" class="fa-regular fa-file-word"></i>
                  <i v-if="scope.row.templateType == 'docx'" class="fa-solid fa-file-word"></i>
                  <i v-if="scope.row.templateType == 'xlsx'" class="fa-solid fa-file-excel"></i>
               </span>
            </template>
         </el-table-column>

         <!-- 业务字段-->
         <el-table-column label="文件名" align="left" key="templateName" prop="templateName"
            v-if="columns[0].visible">
            <template #default="scope">
               <div>
                  {{ scope.row.templateName }}
               </div>
               <div style="font-size: 13px;color: #a5a5a5;cursor: pointer;" v-copyText="scope.row.id">
                  标识: {{ scope.row.id }} <el-icon>
                     <CopyDocument />
                  </el-icon>
               </div>
            </template>
         </el-table-column>
         <el-table-column label="文件Key" align="center" key="size" prop="size" v-if="columns[2].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
               <div style="font-size: 13px;color: #a5a5a5;cursor: pointer;" v-copyText="scope.row.templateKey">
                  {{ scope.row.templateKey }}
                  <el-icon>
                     <CopyDocument />
                  </el-icon>
               </div>
            </template>
         </el-table-column>

         <el-table-column label="上传时间" align="center" prop="createTime" v-if="columns[6].visible" width="160">
            <template #default="scope">
               <span>{{ parseTime(scope.row.addTime) }}</span>
            </template>
         </el-table-column>

         <!-- 操作字段  -->
         <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
            <template #default="scope">
               <el-tooltip content="下载" placement="top" v-if="scope.row.TemplateId !== 1">
                  <el-button link type="primary" icon="Download" @click="handleOpenLink(scope.row)"
                     v-hasPermi="['system:Template:edit']"></el-button>
               </el-tooltip>
               <el-tooltip content="打开" placement="top" v-if="scope.row.TemplateId !== 1">
                  <el-button link type="primary" icon="Link" @click="handleOpenLink(scope.row)"
                     v-hasPermi="['system:Template:edit']"></el-button>
               </el-tooltip>
               <el-tooltip content="删除" placement="top" v-if="scope.row.TemplateId !== 1">
                  <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['system:Template:remove']"></el-button>
               </el-tooltip>
            </template>

         </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
         v-model:limit="queryParams.pageSize" @pagination="getList" />

      <!-- 添加或修改指令配置对话框 -->
      <el-dialog :title="promptTitle" v-model="promptOpen" width="1024" destroy-on-close append-to-body>

         <TemplateEditor :currentPostId="currentPostId" :currentTemplateContent="currentTemplateContent" />

      </el-dialog>

      <!-- 添加或修改指令配置对话框 -->
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
      <el-upload ref="uploadRef" :limit="1" accept=".xlsx,.xls,.ppt,.docx,.doc,.xml,.pptx,.md" :headers="upload.headers"
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

      <el-dialog :title="title" v-model="open" width="900px" append-to-body>
         <el-form :model="form" :rules="rules" ref="databaseRef" label-width="100px">
            <el-row>
               <el-col :span="24">
                  <el-form-item style="width: 100%;" label="类型" prop="promptType">
                     <el-tree-select v-model="form.promptType" :data="deptOptions"
                        :props="{ value: 'id', label: 'label', children: 'children' }" value-key="id"
                        placeholder="请选择归属类型" check-strictly />
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="24">
                  <el-form-item label="名称" prop="templateName">
                     <el-input v-model="form.templateName" placeholder="请输入指令名称" maxlength="50" />
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="24">
                  <el-form-item label="数据来源" prop="dataSourceApi">
                     <el-input v-model="form.dataSourceApi" placeholder="请输入dataSourceApi数据来源" maxlength="128" />
                  </el-form-item>
               </el-col>
            </el-row>

            <el-row>
               <el-col :span="24">
                  <el-form-item label="备注" prop="promptDesc">
                     <el-input v-model="form.promptDesc" placeholder="请输入指令备注"></el-input>
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

<script setup name="Template">

import {
   listTemplate,
   delTemplate,
   getTemplate,
   updateTemplate,
   catalogTreeSelect,
   addTemplate
} from "@/api/smart/assistant/template";

import { getToken } from "@/utils/auth";

const router = useRouter();
const { proxy } = getCurrentInstance();

// 定义变量
const TemplateList = ref([]);
const open = ref(false);

const promptTitle = ref("");
const currentPostId = ref("");
const currentTemplateContent = ref([]);
const promptOpen = ref(false);

const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const deptOptions = ref(undefined);
const postOptions = ref([]);
const roleOptions = ref([]);

// 列显隐信息
const columns = ref([
   { key: 0, label: `指令名称`, visible: true },
   { key: 1, label: `指令描述`, visible: true },
   { key: 2, label: `表数据量`, visible: true },
   { key: 3, label: `类型`, visible: true },
   { key: 4, label: `指令地址`, visible: true },
   { key: 5, label: `状态`, visible: true },
   { key: 6, label: `更新时间`, visible: true }
]);

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
  url:
    import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/template/importData",
});

const data = reactive({
   form: {},
   queryParams: {
      pageNum: 1,
      pageSize: 10,
      templateName: undefined,
      promptDesc: undefined,
      catalogId: undefined
   },
   rules: {
      templateName: [{ required: true, message: "名称不能为空", trigger: "blur" }],
      dataSourceApi: [{ required: true, message: "连接不能为空", trigger: "blur" }],
      promptType: [{ required: true, message: "类型不能为空", trigger: "blur" }],
      promptDesc: [{ required: true, message: "备注不能为空", trigger: "blur" }]
   }
});

const { queryParams, form, rules } = toRefs(data);

function formatFileSize(bytes) {
   if (bytes === 0) return '0 B';
   const k = 1024;
   const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
   const i = Math.floor(Math.log(bytes) / Math.log(k));
   return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i];
}

/** 查询指令列表 */
function getList() {
   loading.value = true;
   listTemplate(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
      loading.value = false;
      TemplateList.value = res.rows;
      total.value = res.total;
   });
};

// 节点单击事件
function handleNodeClick(data) {
   queryParams.value.catalogId = data.id;
   console.log('data.id = ' + data.id)
   getList();
}

/** 搜索按钮操作 */
function handleQuery() {
   queryParams.value.pageNum = 1;
   getList();
};

/** 重置按钮操作 */
function resetQuery() {
   dateRange.value = [];
   proxy.resetForm("queryRef");

   queryParams.value.catalogId = undefined;

   proxy.$refs.deptTreeRef.setCurrentKey(null);
   handleQuery();
};
/** 删除按钮操作 */
function handleDelete(row) {
   const TemplateIds = row.id || ids.value;
   proxy.$modal.confirm('是否确认删除指令编号为"' + TemplateIds + '"的数据项？').then(function () {
      return delTemplate(TemplateIds);
   }).then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
   }).catch(() => { });
};

/** 选择条数  */
function handleSelectionChange(selection) {
   ids.value = selection.map(item => item.id);
   single.value = selection.length != 1;
   multiple.value = !selection.length;
};

/** 查询类型下拉树结构 */
function getDeptTree() {
   catalogTreeSelect().then(response => {
      deptOptions.value = response.data;
   });
};

/** 配置Template */
function configTemplate(row) {
   promptTitle.value = "配置角色Template";
   promptOpen.value = true;
   currentPostId.value = row.id;

   if (row.contentType) {
      currentTemplateContent.value = JSON.parse(row.contentType);
   }
}

/** 重置操作表单 */
function reset() {
   form.value = {
      id: undefined,
      deptId: undefined,
      TemplateName: undefined,
      nickName: undefined,
      password: undefined,
      phonenumber: undefined,
      status: "0",
      remark: undefined,
   };
   proxy.resetForm("databaseRef");
};
/** 取消按钮 */
function cancel() {
   open.value = false;
   promptOpen.value = false;
   reset();
};

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

/** 新增按钮操作 */
function handleAdd() {
   reset();
   open.value = true;
   title.value = "添加指令";
};

/** 修改按钮操作 */
function handleUpdate(row) {
   reset();
   const TemplateId = row.id || ids.value;
   getTemplate(TemplateId).then(response => {
      form.value = response.data;
      open.value = true;
      title.value = "修改指令";
   });
};

/** 提交按钮 */
function submitForm() {
   proxy.$refs["databaseRef"].validate(valid => {
      if (valid) {
         if (form.value.id != undefined) {
            updateTemplate(form.value).then(response => {
               proxy.$modal.msgSuccess("修改成功");
               open.value = false;
               getList();
            });
         } else {
            addTemplate(form.value).then(response => {
               proxy.$modal.msgSuccess("新增成功");
               open.value = false;
               getList();
            });
         }
      }
   });
};

// getDeptTree();
getList();

</script>
