<template>
  <div class="app-container">
    <el-row :gutter="20">

      <!--类型数据-->
      <el-col :span="5" :xs="24">

          <div class="role-list-panel" style="margin: 0px 0px;">
              <div class="role-list-title">
                <div style="font-size: 15px;margin-bottom: 15px;color: #409EFF;font-weight: bold;">
                  <i class="fa-solid fa-user-secret"></i> 工程师Agent列表
                </div>
              </div>
              <div class="role-list-item">
                <el-scrollbar height="750px">
                  <ul class="item-box" style="list-style: none;padding: 0px;margin: 0px;">
                      <li class="item-detail" style="float: left;width: 100%;background: #fff;padding: 5px;border-radius: 2px;margin-bottom: 10px;" 
                          v-for="(item,index) in roleList" :key="index">

                          <div style="float: left;margin-right: 10px;">
                              <img :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (index +15)+ '.png'" style="width: 45px; height: 45px; border-radius: 5px;">
                          </div>
                          <div style="float: left;">
                              <div style="line-height: 20px;margin-bottom: 5px;font-size: 15px;">
                                {{ item.roleName }}
                              </div>
                              <div style="font-size: 13px; color: rgb(165, 165, 165); cursor: pointer;">
                                {{ item.roleLevel }}
                              </div>
                          </div>
                          <div style="float:right">
                            <el-button type="primary" @click="handleSelectCurrentAgent(item)" text bg>选择</el-button>
                          </div>
                      </li>
                  </ul>
                </el-scrollbar>
              </div>
          </div>

      </el-col>

      <el-col :span="19" :xs="24">
        <el-row :gutter="20">
          <!--流程数据-->
          <el-col :span="24" :xs="24">
            <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">

              <el-form-item label="流程名称" prop="workflowName">
                <el-input v-model="queryParams['condition[workflowName|like]']" placeholder="请输入流程名称" clearable
                  style="width: 240px" @keyup.enter="handleQuery" />
              </el-form-item>
              <el-form-item label="显示名称" prop="usageTimeSeconds" label-width="100px">
                <el-input v-model="queryParams['condition[usageTimeSeconds|like]']" placeholder="请输入显示名称" clearable
                  style="width: 240px" @keyup.enter="handleQuery" />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-form>

            <el-row :gutter="10" class="mb8">
              <el-col :span="1.5">
                <el-button type="primary" plain icon="Plus" @click="handleNodeRecord">新增
                </el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate"
                  v-hasPermi="['system:Application:edit']">修改
                </el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete"
                  v-hasPermi="['system:Application:remove']">删除
                </el-button>
              </el-col>

              <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
            </el-row>

            <el-table v-loading="loading" :data="ApplicationList" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column label="图标" align="center" width="50px" prop="icon" v-if="columns[0].visible">
                <template #default="scope">
                  <div class="role-icon" style="font-size: 30px;color:#3b5998">
                    <i v-if="(scope.$index + 1) % 3 == 2" class="fa-brands fa-node-js"></i>
                    <i v-if="(scope.$index + 1) % 3 == 0" class="fa-brands fa-java"></i>
                    <i v-if="(scope.$index + 1) % 3 == 1" class="fa-brands fa-python"></i>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="流程名称" align="left" key="workflowName" prop="workflowName" v-if="columns[1].visible"
                :show-overflow-tooltip="true">
                <template #default="scope">
                  <div style="font-size: 14px;font-weight: 500;color: #3b5998;">
                    {{ scope.row.workflowName }}#{{ scope.row.buildNumber }}
                  </div>
                  <div style="font-size: 12px;color: #a5a5a5;">
                    执行时间: {{ scope.row.usageTimeSeconds }}
                  </div>
                </template>
              </el-table-column>

              <!-- <el-table-column label="流程描述" align="left" key="stepWithTime" prop="stepWithTime" v-if="columns[3].visible" /> -->

              <el-table-column label="执行状态" align="center" key="workflowStatus" prop="workflowStatus"
                v-if="columns[3].visible" width="100" :show-overflow-tooltip="true">
                <template #default="scope">
                  <el-button v-if="scope.row.workflowStatus === 'success'" type="success" :icon="CircleCheck" text
                    bg>成功</el-button>
                  <el-button v-if="scope.row.workflowStatus === 'fail'" type="warning" :icon="Warning" text
                    bg>失败</el-button>
                </template>
              </el-table-column>
              <el-table-column label="执行记录" align="center" key="id" prop="id" v-if="columns[5].visible"
                :show-overflow-tooltip="true" width="150">
                <template #default="scope">
                  <el-button type="primary" @click="handleNodeRecord(scope.row)" text bg icon="Paperclip">查看记录</el-button>
                </template>
              </el-table-column>

              <el-table-column label="开始时间" align="center" prop="addTime" v-if="columns[7].visible" width="160">
                <template #default="scope">
                  <span>{{ parseTime(scope.row.startTime) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="结束时间" align="center" prop="addTime" v-if="columns[7].visible" width="160">
                <template #default="scope">
                  <span>{{ parseTime(scope.row.endTime) }}</span>
                </template>
              </el-table-column>

              <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width"
                v-if="columns[8].visible">
                <template #default="scope">

                  <el-tooltip content="修改" placement="top" v-if="scope.row.applicationId !== 1">
                    <el-button link type="primary" icon="Edit" @click="handleNodeRecord(scope.row)"
                      v-hasPermi="['system:Application:edit']"></el-button>
                  </el-tooltip>
                  <el-tooltip content="删除" placement="top" v-if="scope.row.applicationId !== 1">
                    <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                      v-hasPermi="['system:Application:remove']"></el-button>
                  </el-tooltip>

                </template>
              </el-table-column>
            </el-table>
            <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
              v-model:limit="queryParams.pageSize" @pagination="getList" />
          </el-col>
        </el-row>
      </el-col>
    </el-row>


    <!-- 添加或修改流程配置对话框 -->
    <el-dialog :title="title" v-if="open" v-model="open" width="1024px" append-to-body>
      <NodeRecord :currentWorkflowId="currentWorkflowId" />
    </el-dialog>

  </div>
</template>

<script setup name="Application">
import { getToken } from "@/utils/auth";
import {
  listApplication,
  delApplication,
  getApplication,
  updateApplication,
  addApplication,
} from "@/api/smart/assistant/workflowExecution";

import {
  listAllRole
} from "@/api/smart/assistant/role";

import { reactive } from "vue";

import NodeRecord from "./nodeRecord"

const router = useRouter();
const { proxy } = getCurrentInstance();
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
const currentWorkflowId = ref("");
const roleList = ref([]);

const deptOptions = ref(undefined);
const initPassword = ref(undefined);
const postOptions = ref([]);
const roleOptions = ref([]);

/*** 流程导入参数 */
const upload = reactive({
  // 是否显示弹出层（流程导入）
  open: false,
  // 弹出层标题（流程导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的流程数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/system/Application/importData"
});
// 列显隐信息
const columns = ref([
  { key: 0, label: `图标`, visible: true },
  { key: 1, label: `流程名称`, visible: true },
  { key: 2, label: `显示名称`, visible: true },
  { key: 3, label: `所属领域`, visible: true },
  { key: 4, label: `域名`, visible: true },
  { key: 5, label: `安全存储路径`, visible: true },
  { key: 6, label: `流程目标`, visible: true },
  { key: 7, label: `创建时间`, visible: true },
  { key: 8, label: `编辑`, visible: true },

]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    workflowName: 'test',
    usageTimeSeconds: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    applicationId: [{ required: true, message: "流程编号不能为空", trigger: "blur" }],
    workflowName: [{ required: true, message: "流程名称不能为空", trigger: "blur" }, {
      min: 2,
      max: 20,
      message: "流程名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    usageTimeSeconds: [{ required: true, message: "显示名称不能为空", trigger: "blur" }],
    workflowStatus: [{ required: true, message: "所属领域不能为空", trigger: "blur" }],
    workflowStatus: [{ required: true, message: "域名不能为空", trigger: "blur" }],
    id: [{ required: true, message: "安全存储路径不能为空", trigger: "blur" }],
    target: [{ required: true, message: "流程目标不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);


/** 查询流程列表 */
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
  queryParams.value.workflowName = undefined;
  queryParams.value.usageTimeSeconds = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
  const applicationIds = row.id || ids.value;

  proxy.$modal.confirm('是否确认删除流程编号为"' + applicationIds + '"的数据项？').then(function () {
    return delApplication(applicationIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
};

/** 选择当前工程师 */
function handleSelectCurrentAgent(item){
  queryParams.value.roleId = item.id ;
  getList() ;
}

/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 重置操作表单 */
function reset() {
  queryParams.value.roleId = undefined ;
  form.value = {
    applicationId: undefined,
    workflowName: undefined,
    usageTimeSeconds: undefined,
    workflowStatus: undefined,
    workflowStatus: undefined,
    id: undefined,
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
function handleNodeRecord(row) {
  open.value = true;
  currentWorkflowId.value = row.id;
  title.value = "流程子节点";
};

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const applicationId = row.id || ids.value;
  getApplication(applicationId).then(response => {
    form.value = response.data;
    form.value.applicationId = applicationId;
    open.value = true;
    title.value = "修改流程";

  });
};

/** 列出所有的角色 */
function handleListAllRole(){
  listAllRole().then(response => {
    console.log('response = ' + response);
    roleList.value = response.data ;
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ApplicationRef"].validate(valid => {
    if (valid) {
      if (form.value.applicationId != undefined) {
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

handleListAllRole() ;
getList();

</script>
