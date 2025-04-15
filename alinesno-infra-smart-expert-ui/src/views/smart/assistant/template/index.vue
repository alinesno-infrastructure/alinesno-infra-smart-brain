<template>
  <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
         <el-form-item label="模板名称" prop="templateName">
            <el-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable style="width: 240px"
               @keyup.enter="handleQuery" />
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

         <el-table-column label="图标" align="center" width="50" key="format" prop="format" v-if="columns[5].visible">
            <template #default="scope">
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
               <div style="font-size:14px">
                  {{ scope.row.templateName }}
               </div>
               <div style="font-size: 13px;color: #a5a5a5;cursor: pointer;" @click="copyClick(scope.row.templateKey)">
                  标识: {{ scope.row.templateKey }} <el-icon>
                     <CopyDocument />
                  </el-icon>
               </div> 
            </template>
         </el-table-column>

         <el-table-column label="配置格式" align="center" key="size" width="200" prop="size" v-if="columns[2].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
               <el-button v-if="scope.row.paramFormat" type="primary" text bg @click="configParamFormat(scope.row)">
                  已配置
               </el-button>
               <el-button v-else type="warning" text bg @click="configParamFormat(scope.row)">
                  未配置
               </el-button>
            </template>
         </el-table-column>

         <el-table-column label="数据范围" align="center" key="size" width="200" prop="size" v-if="columns[2].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
                <i :class="getWorkplaceNameByType(scope.row.templateDataScope)?.icon" ></i>
                {{ getWorkplaceNameByType(scope.row.templateDataScope)?.name }}
            </template>
         </el-table-column>

         <el-table-column label="上传时间" align="center" prop="createTime" v-if="columns[6].visible" width="160">
            <template #default="scope">
               <span>{{ parseTime(scope.row.addTime) }}</span>
            </template>
         </el-table-column>

         <!-- 操作字段  -->
         <el-table-column label="操作" align="right" width="250" class-name="small-padding fixed-width">
            <template #default="scope">
               <el-tooltip content="预览" placement="top" v-if="scope.row.TemplateId !== 1">
                  <el-button link type="primary" icon="View" @click="handlePreviewTemplate(scope.row)" v-hasPermi="['system:Template:edit']">
                     预览
                  </el-button>
               </el-tooltip> 

               <el-tooltip content="下载" placement="top" v-if="scope.row.TemplateId !== 1">
                  <el-button link type="primary" icon="Download" @click="handleDownload(scope.row)" v-hasPermi="['system:Template:edit']">
                     下载
                  </el-button>
               </el-tooltip>


               <el-tooltip content="删除" placement="top" v-if="scope.row.TemplateId !== 1">
                  <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:Template:remove']">
                     删除
                  </el-button>
               </el-tooltip>
            </template>

         </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
         v-model:limit="queryParams.pageSize" @pagination="getList" />

      <!-- 添加或修改模板配置对话框 
      <el-dialog :title="promptTitle" v-model="promptOpen" width="1024" destroy-on-close append-to-body>
         <TemplateEditor :currentPostId="currentPostId" :currentTemplateContent="currentTemplateContent" />
      </el-dialog>
       -->

      <!-- 添加或修改模板配置对话框 -->
       <!-- 应用导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="800px"  :before-close="handleCloseUploadDialog" append-to-body>

      <el-form ref="databaseRef" :model="form" :rules="rules" :label-position="'top'" label-width="80px">
         <el-form-item label="模板名称" prop="templateName">
            <el-input type="input" v-model="form.templateName" size="large" placeholder="请输入模板名称" />
         </el-form-item>

          <el-row>
          <el-col :span="24">
            <el-form-item label="数据范围" prop="templateDataScope">
              <el-radio-group v-model="form.templateDataScope" :disabled="form.id">
                <el-radio v-for="item in templateDataScopesArr" 
                  style="margin-top: 10px;margin-bottom: 10px;" 
                  :key="item.id" 
                  :value="item.id"
                  :label="item.id" size="large">
                  <div style="padding:10px; display: flex;flex-direction: column;line-height: 1.5rem;">
                    <span style="font-size:15px;font-weight: bold;">
                      <i :class="item.icon"></i> {{ item.name }}
                    </span>
                    <span style="color:#a5a5a5">
                      {{ item.desc }}
                    </span>
                  </div>
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row> 
           
         <el-form-item label="模板内容" prop="templateContent">
            <el-upload v-model="form.templateContent" ref="uploadRef" 
               :limit="1" 
               accept=".xlsx,.xls,.ppt,.docx,.doc,.xml,.pptx" 
               style="width:100%" 
               :headers="upload.headers"
               :action="upload.url + '?templateName=' + form.templateName + '&templateDataScope=' + form.templateDataScope + '&sceneId=' + sceneId" 
               :disabled="upload.isUploading" 
               :on-progress="handleFileUploadProgress" 
               :on-success="handleFileSuccess"
               :auto-upload="false" 
               drag>
               <el-icon class="el-icon--upload">
                  <upload-filled />
               </el-icon>
               <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
               <template #tip>
                  <div class="el-upload__tip text-center">
                     <span>支持 .xml, .doc, .docx, .excel , .md 文件，导入文件不能超过10M</span>
                  </div>
               </template>
            </el-upload>
         </el-form-item>

      </el-form>
            <template #footer>
            <div class="dialog-footer">
               <el-button type="primary" :loading="upload.isUploading" size="large" @click="submitFileForm">上 传</el-button>
               <el-button size="large" @click="upload.open = false">取 消</el-button>
            </div>
            </template>

    </el-dialog>

    <el-dialog :title="configParamFormatTitle" v-model="configParamFormatVisible" width="900px" append-to-body>
      <ConfigParamFormatPanel ref="configParamFormatPanelRef" @getList="getList" />
    </el-dialog>

    <!-- 
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
                     <el-input v-model="form.templateName" placeholder="请输入模板名称" maxlength="50" />
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
                     <el-input v-model="form.promptDesc" placeholder="请输入模板备注"></el-input>
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
      -->

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

import { copyClick } from '@/utils/clipboard.js'
import { getToken } from "@/utils/auth";

import ConfigParamFormatPanel from './configParamFormat.vue'
import { nextTick } from "vue";

const router = useRouter();
const { proxy } = getCurrentInstance();

// 定义变量
const TemplateList = ref([]);
const open = ref(false);

const templateDataScopesArr = [
    { "id": "public", "name": "公开工作台", "icon": "fa-solid fa-globe" , "desc":"模板对外所有人可用" },
    { "id": "org", "name": "组织工作台", "icon": "fa-solid fa-truck-plane" , "desc":"只对组织内的成员可用" }
];

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

const configParamFormatTitle = ref("") ; 
const configParamFormatPanelRef = ref(null)
const configParamFormatVisible = ref(false) ;

// 列显隐信息
const columns = ref([
   { key: 0, label: `模板名称`, visible: true },
   { key: 1, label: `模板描述`, visible: true },
   { key: 2, label: `表数据量`, visible: true },
   { key: 3, label: `类型`, visible: true },
   { key: 4, label: `模板地址`, visible: true },
   { key: 5, label: `状态`, visible: true },
   { key: 6, label: `更新时间`, visible: true }
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
   form: {
      templateDataScope: 'org'
   },
   queryParams: {
      pageNum: 1,
      pageSize: 10,
      templateName: undefined,
      promptDesc: undefined,
      catalogId: undefined
   },
   rules: {
      templateName: [{ required: true, message: "名称不能为空", trigger: "blur" }],
      // templateContent: [{ required: true, message: "内容不能为空", trigger: "blur" }],
   }
});

const { queryParams, form, rules } = toRefs(data);

const configParamFormat = (row) => {
   configParamFormatTitle.value = row.templateName + "配置参数格式";
   configParamFormatVisible.value = true;

   nextTick(() => {
      configParamFormatPanelRef.value.initialTemplateJson(row);
   })

};

function formatFileSize(bytes) {
   if (bytes === 0) return '0 B';
   const k = 1024;
   const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
   const i = Math.floor(Math.log(bytes) / Math.log(k));
   return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i];
}

/** 查询模板列表 */
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

/** 关闭上传窗口 */
const handleCloseUploadDialog = () => {
   if(upload.isUploading){
      proxy.$modal.msgError("正在上传中，请稍后再试");
      return ;
   }
   uploadDialogVisible.value = false;
};

const getWorkplaceNameByType = (type) => {
  for (let i = 0; i < templateDataScopesArr.length; i++) {
    let item = templateDataScopesArr[i] ; 
    if (item.id == type) {
      return item ; 
    }
  }
  return null ;
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
   proxy.$modal.confirm('是否确认删除模板编号为"' + TemplateIds + '"的数据项？').then(function () {
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
  upload.title = "业务模板导入";
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
    "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 0px 0;'>" +
      response.msg +
    "</div>",
    "导入结果",
    { dangerouslyUseHTMLString: true }
  );
  getList();
};
/** 提交上传文件 */
function submitFileForm() {
//    // 表单提交校验
//    formRef.value.validate((valid) => {
//     if (valid) {
//       // 如果校验通过，执行提交逻辑
//       proxy.$refs["uploadRef"].submit();
//     } else {
//       console.log('校验失败');
//       return false;
//     }
//   });

   proxy.$refs["databaseRef"].validate(valid => {
      if (valid) {
         proxy.$refs["uploadRef"].submit();
         // if (form.value.id != undefined) {
            // updateTemplate(form.value).then(response => {
            //    proxy.$modal.msgSuccess("修改成功");
            //    open.value = false;
            //    getList();
            // });
         // } else {
            // addTemplate(form.value).then(response => {
            //    proxy.$modal.msgSuccess("新增成功");
            //    open.value = false;
            //    getList();
            // });
         // }
      }
   });
}

const handlePreviewTemplate = (row) => {
   if(!row.paramFormat){
      proxy.$modal.msgError("请先设置模板参数格式");
      return ;
   }

};

/** 新增按钮操作 */
function handleAdd() {
   reset();
   open.value = true;
   title.value = "添加模板";
};

/** 修改按钮操作 */
function handleUpdate(row) {
   reset();
   const TemplateId = row.id || ids.value;
   getTemplate(TemplateId).then(response => {
      form.value = response.data;
      open.value = true;
      title.value = "修改模板";
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
