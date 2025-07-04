<template>
  <div class="app-container review-page-contaier">

        <el-page-header @back="goBack" class="review-page-header">
            <template #content>
                <span class="text-large font-600 mr-3"> 文章排版模板管理 </span>
            </template>
        </el-page-header>

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

      <el-row :gutter="10" class="mb8">
         <el-col :span="24">
            <div class="aip-template-type" >
               <span class="template-type-title" >类型</span>
               <div class="template-type-list" >
                  <span v-for="(item , index) in templateTypeOptions" 
                     :key="index"  
                     class="template-type-item" 
                     :class="item.code == selectTemplateType ? 'active' : ''"
                     @click="handleTemplateTypeChange(item)">
                     {{ item.name }}
                  </span>
               </div>
            </div>
         </el-col>
      </el-row>

      <el-row :gutter="24" class="mb8" >
         <el-col :span="4" v-for="(item , index) in templateList" :key="index">
            <TemplateCardPanel :templateItem="item" :index="index" @configParamFormat="configParamFormat" />
         </el-col>
      </el-row>

      <pagination v-show="total > 0" 
         :total="total" 
         v-model:page="queryParams.pageNum"
         v-model:limit="queryParams.pageSize" 
         @pagination="getList" />

      <!-- 添加或修改模板配置对话框 -->
      <el-dialog :title="promptTitle" v-model="promptOpen" width="1024" destroy-on-close append-to-body>
         <TemplateEditor :currentPostId="currentPostId" :currentTemplateContent="currentTemplateContent" />
      </el-dialog>

      <!-- 添加或修改模板配置对话框 -->
       <!-- 应用导入对话框 -->
       <el-dialog :title="upload.title" v-model="upload.open" width="800px" style="padding:20px;"  :before-close="handleCloseUploadDialog" append-to-body>

      <el-form ref="databaseRef" :model="form" :rules="rules" :label-position="'top'" label-width="80px">
          <el-row>
            <el-col :span="13">
               <el-form-item label="模板名称" prop="templateName">
                  <el-input type="input" v-model="form.templateName" size="large" placeholder="请输入模板名称" />
               </el-form-item>
               </el-col>
            <el-col :span="11">
               <el-form-item label="模板图片" prop="templateImage" style="float:right;margin-right:30px">

                  <el-upload 
                  :file-list="imageUrl" 
                  accept=".png,.jpeg,.jpg" 
                  :action="upload.url + '?type=image'"
                  list-type="picture-card" 
                  :auto-upload="true" 
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload" 
                  :headers="upload.headers" 
                  :disabled="upload.isUploading"
                  :on-progress="handleFileUploadProgress">
                  <el-icon class="avatar-uploader-icon">
                     <Plus />
                  </el-icon>
               </el-upload>

               </el-form-item>
               </el-col>
            </el-row>

         <el-form-item label="模板类型" prop="templateType">
            <el-radio-group v-model="form.templateType" size="large">
                  <el-radio v-for="item in templateTypeOptions" 
                     style="margin-top: 0px;margin-bottom: 0px;" 
                     :key="item.code" 
                     :value="item.code"
                     :label="item.name" size="large">
                     <div style="padding:0px; display: flex;flex-direction: column;line-height: 1.5rem;">
                     <span style="font-size:15px;font-weight: bold;">
                        <i :class="item.icon"></i> {{ item.name }}
                     </span>
                     <!-- <span style="color:#a5a5a5">
                        {{ truncateString(item.desc , 10) }}
                     </span> -->
                     </div>
                  </el-radio>
            </el-radio-group>
         </el-form-item>

         <el-form-item label="解析引擎" prop="templateEngine">
            <el-radio-group v-model="form.templateEngine" size="large">
               <el-radio v-for="(item, index) in templateEngineOptions" 
                  :key="index"
                  :value="item.key"
                  :label="item.key">
                  {{ item.label }}
               </el-radio>
            </el-radio-group>
         </el-form-item>

                <!-- + '&templateDataScope=' + form.templateDataScope + '&sceneId=' + sceneId + '&templateEngine=' + form.templateEngine + '&templaeType=' + form.templateType" -->

         <el-form-item label="模板内容" prop="storageFileId">
            <el-upload v-model="form.storageFileId" ref="uploadRef" 
               :limit="1" 
               accept=".ppt,.docx,.doc,.xml" 
               class="upload-file-box"
               :headers="upload.headers"
               :action="upload.url + '?templateName=' + form.templateName" 
               :disabled="upload.isUploading" 
               :on-progress="handleFileUploadProgress" 
               :on-success="handleFileSuccess"
               :auto-upload="true" 
               >
               <el-icon class="el-icon--upload">
                  <upload-filled />
               </el-icon>
               <div class="el-upload__text">点击此处选择上传的模板</div>
               <template #tip>
                  <div class="el-upload__tip text-center">
                     <span>支持 .xml, .doc, .docx 文件，导入文件不能超过10M</span>
                  </div>
               </template>
            </el-upload>
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
           

      </el-form>
            <template #footer>
            <div class="dialog-footer">
               <el-button type="primary" :loading="upload.isUploading" size="large" @click="submitFileForm">提 交</el-button>
               <el-button size="large" @click="upload.open = false">取 消</el-button>
            </div>
            </template>

    </el-dialog>

    <el-dialog :title="configParamFormatTitle" v-model="configParamFormatVisible" width="900px" append-to-body>
      <ConfigParamFormatPanel ref="configParamFormatPanelRef" @getList="getList" />
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
   getTemplateType,
   addTemplate
} from "@/api/smart/assistant/template";

import { copyClick } from '@/utils/clipboard.js'
import { getToken } from "@/utils/auth";

import TemplateCardPanel from './templateCardPanel.vue'

import ConfigParamFormatPanel from './configParamFormat.vue'
import { nextTick } from "vue";

const router = useRouter();
const { proxy } = getCurrentInstance();

// 定义变量
const templateList = ref([]);
const open = ref(false);

const templateDataScopesArr = [
    { "id": "public", "name": "公开工作台", "icon": "fa-solid fa-globe" , "desc":"模板对外所有人可用" },
    { "id": "org", "name": "组织工作台", "icon": "fa-solid fa-truck-plane" , "desc":"只对组织内的成员可用" }
];

// 解析方式(简单/复杂)
const templateEngineOptions = [
   { key: "simple", label: "简单" },
   { key: "complex", label: "复杂" }
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
const imageUrl = ref([])

const postOptions = ref([]);
const roleOptions = ref([]);

const templateTypeOptions = ref([]);
const selectTemplateType = ref(undefined);

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

const databaseRef = ref(null);

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
  url: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/template/importData",
  // 显示地址
  display: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/"
});

const data = reactive({
   form: {
      templateDataScope: 'org' ,
      templateEngine: 'simple'
   },
   queryParams: {
      pageNum: 1,
      pageSize: 10,
   },
   rules: {
      templateImage: [{ required: true, message: "图片不能为空", trigger: "blur" }],
      templateName: [{ required: true, message: "名称不能为空", trigger: "blur" }],
      storageFileId: [{ required: true, message: "模板内容不能为空", trigger: "blur" }],
      templateType: [{ required: true, message: "类型不能为空", trigger: "blur" }],
      templateEngine: [{ required: true, message: "解析方式不能为空", trigger: "blur" }]
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

// 返回
const goBack = () => {
  router.push({ path: "/global/config" });
};

/** 查询模板列表 */
function getList() {
   loading.value = true;
   listTemplate(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
      loading.value = false;
      templateList.value = res.rows;
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
   upload.open = false;
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

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
  imageUrl.value = response.data ? response.data.split(',').map(url => { return { url: upload.display + url } }) : [];
  form.value.templateImage = response.data;
  upload.isUploading = false;
  // console.log('form.roleAvatar = ' + form.roleAvatar);
};

/** 图片上传之前 */
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!');
    return false;
  }
  return true;
};


/** 重置按钮操作 */
function resetQuery() {
   dateRange.value = [];
   proxy.resetForm("queryRef");

   // queryParams.value.catalogId = undefined;
   // proxy.$refs.deptTreeRef.setCurrentKey(null);

   reset();
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
   selectTemplateType.value = null;
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
  console.log('response = ' + response);
//   upload.open = false;
  upload.isUploading = false;
  form.value.storageFileId = response.data ;
  form.value.originalFilename = response.originalFilename;
  form.value.fileType = response.fileType;

//   proxy.$refs["uploadRef"].handleRemove(file);
//   proxy.$alert(
//     "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 0px 0;'>" +
//       response.msg +
//     "</div>",
//     "导入结果",
//     { dangerouslyUseHTMLString: true }
//   );
//   getList();
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
         // proxy.$refs["uploadRef"].submit();
         if (form.value.id != undefined) {
            updateTemplate(form.value).then(response => {
               proxy.$modal.msgSuccess("修改成功");
               upload.open = false;
               getList();
            });
         } else {
            addTemplate(form.value).then(response => {
               proxy.$modal.msgSuccess("新增成功");
               upload.open = false;
               getList();
            });
         }
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

const handleGetTemplateType = () => {
   getTemplateType().then(response => {
      templateTypeOptions.value = response.data;
   });
};

const handleTemplateTypeChange = (item) => {
   console.log('value = ' + JSON.stringify(item) + ' code = ' + item.code)
   queryParams.value.templateType = item.code;
   selectTemplateType.value = item.code;
   getList();
};

// getDeptTree();
getList();
handleGetTemplateType();

</script>

<style lang="scss" scoped>

.upload-file-box{
   width: 100%;
   text-align: center;
   color: #444;
   border-radius: 8px;
   background: #f5f5f5;
   font-size: 15px;
   padding-top: 15px;
}
</style>
