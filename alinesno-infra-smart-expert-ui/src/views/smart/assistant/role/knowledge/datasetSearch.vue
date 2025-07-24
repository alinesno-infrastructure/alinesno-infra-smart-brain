<template>
  <div style="padding-bottom: 30px;">
      <el-row :gutter="20">
          <el-col :span="24">
              <el-form :model="queryParams" ref="queryRef" :rule="rules" :inline="true" v-show="showSearch" label-width="100px">

                  <el-form-item label="搜索条件" prop="searchText">
                      <el-input v-model="queryParams.searchText" placeholder="请输入搜索条件" clearable style="width: 540px" @keyup.enter="handleQuery" />
                  </el-form-item>

                  <el-form-item>
                      <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                      <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                  </el-form-item>
              </el-form>

              <el-table v-loading="loading" :data="datasetList" @selection-change="handleSelectionChange">
                  <el-table-column type="index" width="50" align="center" />

                  <el-table-column label="类型" align="center" width="50px" prop="icon" v-if="columns[0].visible">
                      <template #default="scope">
                          <div class="role-icon" style="font-size: 20px;">
                              <i class="fa-solid fa-file-pdf" />
                          </div>
                      </template>
                  </el-table-column>

                  <el-table-column label="文档名称" align="left" width="250px" prop="icon" v-if="columns[0].visible">
                      <template #default="scope">
                          {{ scope.row.document_title }}
                      </template>
                  </el-table-column>

                  <el-table-column label="查询结果" align="left" key="name" prop="name" v-if="columns[1].visible">
                      <template #default="scope">
                          <div style="font-size: 13px;color: rgb(59, 89, 152)">
                              {{ truncateString(scope.row.document_content,300) }}
                          </div>
                      </template>
                  </el-table-column>
                  <el-table-column label="评分" width="100px" align="center" key="name" prop="name"
                      v-if="columns[1].visible" :show-overflow-tooltip="true">
                      <template #default="scope">
                          <div style="font-size: 15px;font-weight: 500;color: #1d75b0;">
                              {{ scope.row.score }}
                          </div>
                      </template>
                  </el-table-column>

                  <el-table-column label="操作" align="center" width="100" class-name="small-padding fixed-width"
                      v-if="columns[8].visible">
                      <template #default="scope">
                          <el-tooltip content="查看" placement="top" v-if="scope.row.applicationId !== 1">
                              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:Dataset:edit']"></el-button>
                          </el-tooltip>
                          <!-- <el-tooltip content="删除" placement="top" v-if="scope.row.applicationId !== 1">
                              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                                  v-hasPermi="['system:Dataset:remove']"></el-button>
                          </el-tooltip> -->

                      </template>
                  </el-table-column>
              </el-table>
              <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                  v-model:limit="queryParams.pageSize" @pagination="getList" />
          </el-col>

      </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// const textarea = ref('')

// import { getToken } from "@/utils/auth";
import {
  // listDataset,
  // delDataset,
  // getDataset,
  // updateDataset,
  // addDataset,
  getSearchKnowledge
} from "@/api/smart/assistant/roleKnowledge";
import { reactive } from "vue";

// const route = useRoute();
// const router = useRouter();

const { proxy } = getCurrentInstance();
// const { sys_normal_disable, sys_Dataset_sex } = proxy.useDict("sys_normal_disable", "sys_Dataset_sex");

const currentDatasetId = ref("")
const datasetList = ref([]);
// const open = ref(false);
const loading = ref(false);
const showSearch = ref(true);
// const ids = ref([]);
// const single = ref(true);
// const multiple = ref(true);
const total = ref(0);
// const title = ref("");
const dateRange = ref([]);
// const deptName = ref("");
// const deptOptions = ref(undefined);
// const initPassword = ref(undefined);
// const postOptions = ref([]);
// const roleOptions = ref([]);
/*** 应用导入参数 */
// const upload = reactive({
//   // 是否显示弹出层（应用导入）
//   open: false,
//   // 弹出层标题（应用导入）
//   title: "",
//   // 是否禁用上传
//   isUploading: false,
//   // 是否更新已经存在的应用数据
//   updateSupport: 0,
//   // 设置上传的请求头部
//   headers: { Authorization: "Bearer " + getToken() },
//   // 上传的地址
//   url: import.meta.env.VITE_APP_BASE_API + "/system/Dataset/importData"
// });
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
      DatasetName: undefined,
      searchText: undefined,
      topK: 10,
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
      searchText: [{ required: true, message: "显示名称不能为空", trigger: "blur" }],
    //   description: [{ required: true, message: "描述信息不能为空", trigger: "blur" }],
    //   datasetStatus: [{ required: true, message: "域名不能为空", trigger: "blur" }],
    //   accessPermission: [{ required: true, message: "安全存储路径不能为空", trigger: "blur" }],
    //   datasetSize: [{ required: true, message: "应用目标不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);


/** 查询应用列表 */
function getList() {
  loading.value = true;

  console.log("currentDatasetId = " + currentDatasetId.value);
  queryParams.value.datasetId = currentDatasetId.value

  getSearchKnowledge(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
      loading.value = false;
      datasetList.value = res.data;
      total.value = res.total;
  }).catch(() => {
      loading.value = false;
   });

};


/** 搜索按钮操作 */
function handleQuery() {
  console.log(queryParams);
  // queryParams.value.pageNum = 1;

  proxy.$refs["queryRef"].validate(valid => {
      console.log('---> ' + valid)
      if (valid) {
          getList();
      }
  });
};

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  loading.value = false;
  queryParams.value.name = undefined;
  queryParams.value.ownerId = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

// /** 删除按钮操作 */
// function handleDelete(row) {
//   const applicationIds = row.id || ids.value;

//   proxy.$modal.confirm('是否确认删除应用编号为"' + applicationIds + '"的数据项？').then(function () {
//       return delDataset(applicationIds);
//   }).then(() => {
//       getList();
//       proxy.$modal.msgSuccess("删除成功");
//   }).catch(() => {
//   });
// };

// /** 选择条数  */
// function handleSelectionChange(selection) {
//   ids.value = selection.map(item => item.id);
//   single.value = selection.length != 1;
//   multiple.value = !selection.length;
// };

// /** 重置操作表单 */
// function reset() {
//   form.value = {
//       applicationId: undefined,
//       name: undefined,
//       ownerId: undefined,
//       description: undefined,
//       datasetStatus: undefined,
//       accessPermission: undefined,
//       datasetSize: undefined,
//   };
//   proxy.resetForm("DatasetRef");
// };

// /** 取消按钮 */
// function cancel() {
//   open.value = false;
//   reset();
// };

// /** 新增按钮操作 */
// function handleAdd() {
//   reset();
//   open.value = true;
//   title.value = "添加应用";
// };

/** 修改按钮操作 */
// function handleUpdate(row) {
//   reset();
//   const applicationId = row.id || ids.value;
//   getDataset(applicationId).then(response => {
//       form.value = response.data;
//       form.value.applicationId = applicationId;
//       open.value = true;
//       title.value = "修改应用";

//   });
// };

// /** 导入按钮操作 */
// function handleImport() {
//   upload.title = "数据集导入";
//   upload.open = true;
// };

// /** 下载模板操作 */
// function importTemplate() {
//   proxy.download("system/user/importTemplate", {
//   }, `user_template_${new Date().getTime()}.xlsx`);
// };

// /**文件上传中处理 */
// const handleFileUploadProgress = (event, file, fileList) => {
//   upload.isUploading = true;
// };
// /** 文件上传成功处理 */
// const handleFileSuccess = (response, file, fileList) => {
//   upload.open = false;
//   upload.isUploading = false;
//   proxy.$refs["uploadRef"].handleRemove(file);
//   proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", { dangerouslyUseHTMLString: true });
//   getList();
// };
// /** 提交上传文件 */
// function submitFileForm() {
//   proxy.$refs["uploadRef"].submit();
// };

// /** 提交按钮 */
// function submitForm() {
//   proxy.$refs["DatasetRef"].validate(valid => {
//       if (valid) {
//           if (form.value.applicationId != undefined) {
//               updateDataset(form.value).then(response => {
//                   proxy.$modal.msgSuccess("修改成功");
//                   open.value = false;
//                   getList();
//               });
//           } else {
//               addDataset(form.value).then(response => {
//                   proxy.$modal.msgSuccess("新增成功");
//                   open.value = false;
//                   getList();
//               });
//           }
//       }
//   });
// };
// 
// getList();

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

<style lang="scss" scoped>
.input-search-text {
  width: 100%;
  resize: none;
  font-size: 14px;
  border: 0px solid #ccc;
  border-radius: 5px;
  background-color: #fafafa;
  outline: none;

  textarea {
      background-color: #fafafa;
  }
}
</style>