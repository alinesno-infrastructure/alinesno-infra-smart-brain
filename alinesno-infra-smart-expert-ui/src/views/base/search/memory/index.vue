<template>
   <div class="app-container workflow-container">

    <div class="page-header-container" style="margin-bottom: 20px;">
      <el-page-header @back="goBack">
        <template #content>
          <div style="display: flex;gap: 10px;">
            <span class="text-large font-600 mr-3"> 
               智能体记忆库 
            </span>
            <span style="color: #aaaaaa;font-size: 14px;">保存时间：2025-02-14 23:50:44</span>
          </div>
        </template>
      </el-page-header>
   </div>

      <el-row :gutter="20">
         <!--应用数据-->
         <!-- <el-col :span="8" :xs="24">
            <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">

               <el-form-item label="记忆库名称" prop="name">
                  <el-input v-model="queryParams['condition[name|like]']" placeholder="请输入应用名称" clearable
                     style="width: 240px" @keyup.enter="handleQuery" />
               </el-form-item>

               <el-form-item>
                  <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                  <el-button icon="Refresh" @click="resetQuery">重置</el-button>
               </el-form-item>
            </el-form>

            <el-row :gutter="10" class="mb8">
               <el-col :span="1.5">
                  <el-button type="primary" plain size="mini" icon="Plus" @click="handleAdd">新增
                  </el-button>
               </el-col>
               <el-col :span="1.5">
                  <el-button type="success" plain size="mini" icon="Edit" :disabled="single" @click="handleUpdate"
                     v-hasPermi="['system:Dataset:edit']">修改
                  </el-button>
               </el-col>

            </el-row>

            <el-table v-loading="loading" :data="DatasetList" @selection-change="handleSelectionChange">
               <el-table-column type="selection" width="50" align="center" />

               <el-table-column label="记忆库名称" align="left" key="name" prop="name" v-if="columns[1].visible"
                  :show-overflow-tooltip="true">
                  <template #default="scope">
                     <div>
                        {{ scope.row.name }}
                     </div>
                     <div style="font-size: 13px;color: #a5a5a5;cursor: pointer;" v-copyText="scope.row.id">
                        标识: {{ scope.row.id }} <el-icon>
                           <CopyDocument />
                        </el-icon>
                     </div>
                  </template>
               </el-table-column>
               <el-table-column label="实体总量" align="center" width="130" key="datasetSize" prop="datasetSize"
                  v-if="columns[6].visible" :show-overflow-tooltip="true" />
               <el-table-column label="操作" align="center" width="100" class-name="small-padding fixed-width"
                  v-if="columns[8].visible">
                  <template #default="scope">
                     <el-tooltip content="删除" placement="top" v-if="scope.row.applicationId !== 1">
                        <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                           v-hasPermi="['system:Dataset:remove']"></el-button>
                     </el-tooltip>

                     <el-tooltip content="查看" placement="top" v-if="scope.row.applicationId !== 1">
                        <el-button @click="handleClickBalanced(scope.row)" type="text" bg circle title="请点击选中查看">
                           <el-icon>
                              <Right />
                           </el-icon>
                        </el-button>
                     </el-tooltip>

                  </template>
               </el-table-column>
            </el-table>
            <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
               v-model:limit="queryParams.pageSize" @pagination="getList" />
         </el-col> -->

         <el-col :span="24" :xs="24">
            <DisplayMemory />
         </el-col>
      </el-row>

      <!-- 添加或修改应用配置对话框 -->
      <el-dialog :title="title" v-model="open" width="600px" append-to-body>
         <el-form :model="form" :rules="rules" ref="DatasetRef" label-width="100px">

            <el-row>
               <el-col :span="24">
                  <el-form-item label="记忆库名称" prop="name">
                     <el-input v-model="form.name" placeholder="请输入应用名称" maxlength="50" />
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

<script setup name="Dataset">
import { getToken } from "@/utils/auth";
import {
   listDataset,
   delDataset,
   getDataset,
   updateDataset,
   addDataset,
} from "@/api/base/search/vectorDataset";
import { reactive } from "vue";

import DisplayMemory from "./displayMemory.vue";

const router = useRouter();
const { proxy } = getCurrentInstance();
// const { sys_normal_disable, sys_Dataset_sex } = proxy.useDict("sys_normal_disable", "sys_Dataset_sex");

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
   { key: 1, label: `记忆库名称`, visible: true },
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
      isMemory: 1,
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
      ownerId: [{ required: true, message: "显示名称不能为空", trigger: "blur" }],
      description: [{ required: true, message: "描述信息不能为空", trigger: "blur" }],
      datasetStatus: [{ required: true, message: "域名不能为空", trigger: "blur" }],
      accessPermission: [{ required: true, message: "安全存储路径不能为空", trigger: "blur" }],
      datasetSize: [{ required: true, message: "应用目标不能为空", trigger: "blur" }],
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
   title.value = "添加应用";
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
   upload.title = "记忆库导入";
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

/** 提交按钮 */
function submitForm() {
   proxy.$refs["DatasetRef"].validate(valid => {
      if (valid) {
         form.value.isMemory = 1

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

getList();
</script>

<style lang="scss" scoped>
.role-icon {

   display: flex;
   align-items: center;
   justify-content: space-between;

   img {
      width: 40px;
      height: 40px;
      border-radius: 50%;
   }
}
</style>