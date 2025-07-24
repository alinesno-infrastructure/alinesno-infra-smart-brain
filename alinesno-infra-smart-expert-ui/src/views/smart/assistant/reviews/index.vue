i<template>
  <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
         <el-form-item label="审核清单名称" prop="templateName">
            <el-input v-model="queryParams.templateName" placeholder="请输入审核清单名称" clearable style="width: 240px"
               @keyup.enter="handleQuery" />
         </el-form-item>
         <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
         </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">

         <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd">新增清单</el-button>
         </el-col>

         <el-col :span="1.5">
            <el-button type="warning" plain icon="Plus" @click="handleRuleAdd">审查规则库</el-button>
         </el-col>

         <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
         </el-col>

         <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="ReviewAuditList" @selection-change="handleSelectionChange">
         <el-table-column type="selection" width="50" :align="'center'" />

         <!-- 
         <el-table-column label="图标" align="center" width="50" key="format" prop="format" v-if="columns[5].visible">
            <template #default="scope">
               <span style="font-size: 1.6rem;color:#1d75b0">
                  <i v-if="scope.row.templateType == 'pdf'" class="fa-solid fa-file-pdf"></i>
                  <i v-if="scope.row.templateType == 'xml'" class="fa-regular fa-file-word"></i>
                  <i v-if="scope.row.templateType == 'docx'" class="fa-solid fa-file-word"></i>
                  <i v-if="scope.row.templateType == 'xlsx'" class="fa-solid fa-file-excel"></i>
               </span>
            </template>
         </el-table-column> 
         -->

         <!-- 业务字段-->
         <el-table-column label="审核清单名称" align="left" key="templateName" prop="templateName"
            v-if="columns[0].visible">
            <template #default="scope">
               <div style="font-size:14px">
                  {{ scope.row.auditName }}
               </div>
               <!-- 
               <div style="font-size: 13px;color: #a5a5a5;cursor: pointer;" @click="copyClick(scope.row.templateKey)">
                  标识: {{ scope.row.templateKey }} <el-icon>
                     <CopyDocument />
                  </el-icon>
               </div>  
               -->
            </template>
         </el-table-column>

         <el-table-column label="配置格式" align="center" key="size" width="200" prop="size" v-if="columns[2].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
               <el-button type="primary" text bg @click="displayRules(scope.row)">
                 查看规则 
               </el-button>
            </template>
         </el-table-column> 

         <el-table-column label="数据范围" align="center" key="size" width="200" prop="size" v-if="columns[2].visible"
            :show-overflow-tooltip="true">
            <template #default="scope">
                <i :class="getWorkplaceNameByType(scope.row.dataScope)?.icon" ></i>
                {{ getWorkplaceNameByType(scope.row.dataScope)?.name }}
            </template>
         </el-table-column>

         <el-table-column label="添加时间" align="center" prop="createTime" v-if="columns[6].visible" width="160">
            <template #default="scope">
               <span>{{ parseTime(scope.row.addTime) }}</span>
            </template>
         </el-table-column>

         <!-- 操作字段  -->
         <el-table-column label="操作" align="right" width="350" class-name="small-padding fixed-width">
            <template #default="scope">
                  <el-button link type="primary" icon="EditPen" @click="handleEdit(scope.row)" v-hasPermi="['system:ReviewAudit:edit']">
                     编辑 
                  </el-button>
                  <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:ReviewAudit:remove']">
                     删除
                  </el-button>
            </template>

         </el-table-column>
      </el-table>
      <pagination v-show="total > 0" 
            :total="total" 
            v-model:page="queryParams.pageNum" 
            v-model:limit="queryParams.pageSize" 
            @pagination="getList" />

      <!-- 显示审核规则列表 -->
      <el-dialog v-model="dialogVisible" :title="dialogTitle" style="width:80%;max-width:1400px">
         <RuleSelectDisplay ref="ruleSelectDisplayRef" />
      </el-dialog>

   </div>
</template>

<script setup name="ReviewAudit">

import {
   listReviewAudit,
   delReviewAudit,
   getReviewAudit,
   updateReviewAudit,
   // catalogTreeSelect,
   addReviewAudit
} from "@/api/smart/assistant/reviewAudit";

import { copyClick } from '@/utils/clipboard.js'
import { getToken } from "@/utils/auth";

// import ConfigParamFormatPanel from './configParamFormat.vue'
import { nextTick } from "vue";
import RuleSelectDisplay from './ruleSelectDisplayy.vue'


const router = useRouter();
const { proxy } = getCurrentInstance();

const ruleSelectDisplayRef = ref(null);
const dialogVisible = ref(false);
const dialogTitle = ref('审核规则');

// 定义变量
const ReviewAuditList = ref([]);
const open = ref(false);

const templateDataScopesArr = [
    { "id": "public", "name": "公开", "icon": "fa-solid fa-globe" , "desc":"审核清单对外所有人可用" },
    { "id": "org", "name": "组织", "icon": "fa-solid fa-truck-plane" , "desc":"只对组织内的成员可用" }
];

const promptTitle = ref("");
const currentPostId = ref("");
const currentReviewAuditContent = ref([]);
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
   { key: 0, label: `审核清单名称`, visible: true },
   { key: 1, label: `审核清单描述`, visible: true },
   { key: 2, label: `表数据量`, visible: true },
   { key: 3, label: `类型`, visible: true },
   { key: 4, label: `审核清单地址`, visible: true },
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
      configParamFormatPanelRef.value.initialReviewAuditJson(row);
   })

};


function formatFileSize(bytes) {
   if (bytes === 0) return '0 B';
   const k = 1024;
   const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
   const i = Math.floor(Math.log(bytes) / Math.log(k));
   return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i];
}

/** 查询审核清单列表 */
function getList() {
   loading.value = true;
   listReviewAudit(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
      loading.value = false;
      ReviewAuditList.value = res.rows;
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
   const ReviewAuditIds = row.id || ids.value;
   proxy.$modal.confirm('是否确认删除审核清单编号为"' + ReviewAuditIds + '"的数据项？').then(function () {
      return delReviewAudit(ReviewAuditIds);
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
   // catalogTreeSelect().then(response => {
   //    deptOptions.value = response.data;
   // });
};

/** 配置ReviewAudit */
function configReviewAudit(row) {
   promptTitle.value = "配置角色ReviewAudit";
   promptOpen.value = true;
   currentPostId.value = row.id;

   if (row.contentType) {
      currentReviewAuditContent.value = JSON.parse(row.contentType);
   }
}

// 显示规则
const displayRules = (row) => {
   dialogVisible.value = true;
   dialogTitle.value = row.auditName + ' 规则列表'

   nextTick(() => {
       ruleSelectDisplayRef.value.getSelectRuleIds(row.auditList);
   });
}

/** 重置操作表单 */
function reset() {
   form.value = {
      id: undefined,
      deptId: undefined,
      ReviewAuditName: undefined,
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
  upload.title = "业务审核清单导入";
  upload.open = true;
}

/** 下载审核清单操作 */
function importReviewAudit() {
  proxy.download(
    "system/user/importReviewAudit",
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
            // updateReviewAudit(form.value).then(response => {
            //    proxy.$modal.msgSuccess("修改成功");
            //    open.value = false;
            //    getList();
            // });
         // } else {
            // addReviewAudit(form.value).then(response => {
            //    proxy.$modal.msgSuccess("新增成功");
            //    open.value = false;
            //    getList();
            // });
         // }
      }
   });
}

// 清单编辑
const handleEdit = (row) => {
  router.push({
      path: '/expert/smart/assistant/reviews/addAudit',
      query: {
          id: row.id
      }
  })
};

const handlePreviewReviewAudit = (row) => {
   if(!row.paramFormat){
      proxy.$modal.msgError("请先设置审核清单参数格式");
      return ;
   }

};

/** 新增按钮操作 */
function handleAdd() {
  //  reset();
  //  open.value = true;
  //  title.value = "添加审核清单";

  router.push({
      path: '/expert/smart/assistant/reviews/addAudit'
      // ,
      // query: {
      //     sceneId: sceneId.value
      // }
  })
};

/** 添加规则按钮操作 */
function handleRuleAdd() {
  router.push({
      path: '/expert/smart/assistant/reviews/ruleList'
  })
}

/** 修改按钮操作 */
function handleUpdate(row) {
   reset();
   const ReviewAuditId = row.id || ids.value;
   getReviewAudit(ReviewAuditId).then(response => {
      form.value = response.data;
      open.value = true;
      title.value = "修改审核清单";
   });
};

/** 提交按钮 */
function submitForm() {
   proxy.$refs["databaseRef"].validate(valid => {
      if (valid) {
         if (form.value.id != undefined) {
            updateReviewAudit(form.value).then(response => {
               proxy.$modal.msgSuccess("修改成功");
               open.value = false;
               getList();
            });
         } else {
            addReviewAudit(form.value).then(response => {
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
