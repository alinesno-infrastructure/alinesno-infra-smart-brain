<template>
   <div class="app-container">
      <el-row :gutter="20">
         <!--任务数据-->
         <el-col :span="24" :xs="24">
            <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
               <el-form-item label="任务名称" prop="dbName">
                  <el-input v-model="queryParams.dbName" placeholder="请输入任务名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
               </el-form-item>
               <el-form-item label="任务状态" prop="dbName">
                  <!-- <el-input v-model="queryParams['condition[dbName|like]']" placeholder="请输入任务状态" clearable style="width: 240px" @keyup.enter="handleQuery" /> -->
                  <el-select v-model="queryParams.genStatus" placeholder="请选择任务状态" clearable>
                     <el-option
                        v-for="dict in gen_status"
                        :key="dict.value"
                        :label="dict.label"
                        :value="dict.value"
                     />
                  </el-select>
               </el-form-item>
               <el-form-item>
                  <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                  <el-button icon="Refresh" @click="resetQuery">重置</el-button>
               </el-form-item>
            </el-form>

            <el-row :gutter="10" class="mb8">

               <el-col :span="1.5">
                  <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
               </el-col>
               <el-col :span="1.5">
                  <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
               </el-col>
               <el-col :span="1.5">
                  <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
               </el-col>

               <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
            </el-row>

            <el-table v-loading="loading" :data="TaskList" @selection-change="handleSelectionChange">
               <el-table-column type="selection" width="50" align="center" />
               <el-table-column label="图标" align="center" width="50" key="status" v-if="columns[5].visible">
                  <template #default="scope">
                     <div class="role-icon">
                        <img :src="'http://data.linesno.com/icons/circle/Delivery boy-' + ((scope.$index + 1)%5 + 1) + '.png'" />
                     </div>
                  </template>
               </el-table-column>
               <!-- 业务字段-->
               <el-table-column label="GPT角色设定" align="left" key="systemContent" prop="systemContent" v-if="columns[0].visible" :show-overflow-tooltip="true">
                  <template #default="scope">
                     <div>
                        {{ scope.row.taskDesc }}
                     </div>
                     <div style="font-size: 13px;color: #a5a5a5;">
                        业务标识: {{ scope.row.businessId }} 
                     </div>
                  </template>
               </el-table-column>
               <!-- <el-table-column label="业务ID" align="left" width="150" key="businessId" prop="businessId" :show-overflow-tooltip="true">
                  <template #default="scope">
                     {{ scope.row.businessId }} 
                  </template>
               </el-table-column> -->
               <el-table-column label="请求参数" align="center" width="120" key="promptContent" prop="promptContent" v-if="columns[2].visible" :show-overflow-tooltip="true">
                  <template #default="scope">
                     <el-button type="primary" text bg icon="Paperclip" @click="configPrompt(scope.row)">查看</el-button>
                  </template>
               </el-table-column>
               <el-table-column label="生成内容" align="center" width="150" key="assistantContent" prop="assistantContent" v-if="columns[2].visible" :show-overflow-tooltip="true" >
                  <template #default="scope">
                     <el-button type="primary" text bg icon="Paperclip" @click="handleTaskContent(scope.row)">查看</el-button>
                  </template>
               </el-table-column>
               <el-table-column label="重试次数" align="center" key="retryCount" width="100" prop="retryCount" v-if="columns[3].visible" :show-overflow-tooltip="true" />
               <el-table-column label="消耗时间" align="center" key="usageTime" width="100" prop="usageTime" v-if="columns[3].visible" :show-overflow-tooltip="true">
                  <template #default="scope">
                     {{ scope.row.usageTime }} 秒
                  </template>
               </el-table-column>
               <el-table-column label="角色标识" align="center" width="150px" key="systemContent" prop="systemContent" v-if="columns[1].visible" :show-overflow-tooltip="true">
                  <template #default="scope">
                     <el-button type="primary" text bg icon="Paperclip" @click="handleTaskHistory(scope.row)">生成历史</el-button>
                  </template>
               </el-table-column>
               <el-table-column label="任务状态" align="center" width="150" key="taskStatus" v-if="columns[5].visible" >
                  <template #default="scope">
                     <el-button v-if="scope.row.taskStatus == 0" type="info"  text bg icon="Paperclip">排队</el-button>
                     <el-button v-if="scope.row.taskStatus == 2"  type="success" text bg icon="Paperclip">成功</el-button>
                     <el-button v-if="scope.row.taskStatus == 1"  type="primary" loading text bg icon="Loading">运行</el-button>
                     <el-button v-if="scope.row.taskStatus == 9" type="danger" text bg icon="Paperclip">失败</el-button>
                  </template>
               </el-table-column>

               <el-table-column label="最近更新" align="center" prop="finishTime" v-if="columns[6].visible" width="160">
                  <template #default="scope">
                     <span v-if="scope.row.finishTime">{{ parseTime(scope.row.finishTime) }}</span>
                     <span v-else>{{ parseTime(scope.row.updateTime) }}</span>
                  </template>
               </el-table-column>

               <!-- 操作字段  -->
               <el-table-column label="操作" align="center" width="130" class-name="small-padding fixed-width">
                  <template #default="scope">
                     <el-tooltip content="重置" placement="top" v-if="scope.row.TaskId !== 1">
                        <el-button link type="primary" icon="Position" @click="handleResetRetry(scope.row)" v-hasPermi="['system:Task:edit']"></el-button>
                     </el-tooltip>
                     <el-tooltip content="修改" placement="top" v-if="scope.row.TaskId !== 1">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:Task:edit']"></el-button>
                     </el-tooltip>
                     <el-tooltip content="删除" placement="top" v-if="scope.row.TaskId !== 1">
                        <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:Task:remove']"></el-button>
                     </el-tooltip>
                  </template>

               </el-table-column>
            </el-table>
            <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
         </el-col>
      </el-row>

       <!-- 添加或修改指令配置对话框 -->
       <el-dialog :title="promptTitle" v-model="promptOpen" width="900px" destroy-on-close append-to-body>
         <PromptEditor :currentPostId="currentPostId" :currentPromptContent="currentPromptContent" />
      </el-dialog>

       <!-- 添加或修改指令配置对话框 -->
       <el-dialog title="内容生成历史" v-model="promptHistoryOpen" width="900px" destroy-on-close append-to-body>
         <PromptHistoryPanel :currentBusinessId="currentBusinessId" />
      </el-dialog>

      <!-- 添加或修改任务配置对话框 -->
      <el-dialog :title="title" v-model="open" width="900px" :labelPosition="top" append-to-body>
         <el-form :model="form" label-width="0px">
            <el-row>
               <el-col :span="24">
                  <el-form-item prop="currentTaskContent">
                     <el-input rows="20" type="textarea" v-model="currentTaskContent" placeholder="请输入任务名称" maxlength="50" />
                  </el-form-item>
               </el-col>
            </el-row>
         </el-form>
         <template #footer>
            <div class="dialog-footer">
               <el-button @click="cancel">取 消</el-button>
            </div>
         </template>
      </el-dialog>

   </div>
</template>

<script setup name="Task">

import {
   listTask,
   delTask,
   getTask,
   updateTask,
   resetRetry,
   addTask
} from "@/api/smart/brain/task";

import { onMounted, onBeforeUnmount } from 'vue';
import PromptEditor from "./prompt"
import PromptHistoryPanel from "./history"

const router = useRouter();
const { proxy } = getCurrentInstance();
const { gen_status } = proxy.useDict("gen_status");

// 定义变量
const TaskList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const postOptions = ref([]);
const roleOptions = ref([]);
const currentTaskContent = ref("")

const promptTitle = ref("");
const currentPostId = ref("");
const currentBusinessId = ref("");
const currentPromptContent = ref([]);
const promptOpen = ref(false);
const promptHistoryOpen = ref(false);

const customColors = [
  { color: '#f56c6c', percentage: 20 },
  { color: '#e6a23c', percentage: 40 },
  { color: '#5cb87a', percentage: 60 },
  { color: '#1989fa', percentage: 80 },
  { color: '#6f7ad3', percentage: 100 },
]

// 列显隐信息
const columns = ref([
   { key: 0, label: `任务名称`, visible: true },
   { key: 1, label: `任务描述`, visible: true },
   { key: 2, label: `表数据量`, visible: true },
   { key: 3, label: `类型`, visible: true },
   { key: 4, label: `任务地址`, visible: true },
   { key: 5, label: `状态`, visible: true },
   { key: 6, label: `更新时间`, visible: true }
]);

const data = reactive({
   form: {},
   queryParams: {
      pageNum: 1,
      pageSize: 10,
      dbName: undefined,
      genStatus: undefined,
      dbDesc: undefined
   },
   rules: {
      dbName: [{ required: true, message: "名称不能为空", trigger: "blur" }] , 
      jdbcUrl: [{ required: true, message: "连接不能为空", trigger: "blur" }],
      dbType: [{ required: true, message: "类型不能为空", trigger: "blur" }] , 
      dbUsername: [{ required: true , message: "用户名不能为空", trigger: "blur"}],
      dbPasswd: [{ required: true, message: "密码不能为空", trigger: "blur" }] , 
      dbDesc: [{ required: true, message: "备注不能为空", trigger: "blur" }] 
   }
});

const customColorMethod = (percentage) => {
  if (percentage < 30) {
    return '#909399';
  }
  if (percentage < 70) {
    return '#e6a23c';
  }
  return '#67c23a';
};

const { queryParams, form, rules } = toRefs(data);

/** 查询任务列表 */
function getList() {
   loading.value = true;
   listTask(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
      loading.value = false;
      TaskList.value = res.rows;
      total.value = res.total;
   });
};

/** 搜索按钮操作 */
function handleQuery() {
   queryParams.value.pageNum = 1;
   getList();
};

/** 重置按钮操作 */
function resetQuery() {
   dateRange.value = [];
   proxy.resetForm("queryRef");
   queryParams.value.deptId = undefined;
   proxy.$refs.deptTreeRef.setCurrentKey(null);
   handleQuery();
};
/** 删除按钮操作 */
function handleDelete(row) {
   const TaskIds = row.id || ids.value;
   proxy.$modal.confirm('是否确认删除任务编号为"' + TaskIds + '"的数据项？').then(function () {
      return delTask(TaskIds);
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

/** 重置操作表单 */
function reset() {
   form.value = {
      id: undefined,
      deptId: undefined,
      TaskName: undefined,
      nickName: undefined,
      genStatus: undefined,
      phonenumber: undefined,
      status: "0",
      remark: undefined,
   };
   proxy.resetForm("databaseRef");
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
   title.value = "添加任务";
};

/** 修改按钮操作 */
function handleUpdate(row) {
   reset();
   const TaskId = row.id || ids.value;
   getTask(TaskId).then(response => {
      form.value = response.data;
      open.value = true;
      title.value = "修改任务";
   });
};

/** 重置重试试数 */
function handleResetRetry(row){
   resetRetry(row.id).then(response => {
      proxy.$modal.msgSuccess("重置成功.");
      getList() ;
   });
}

/** 查询当前内容 */
function handleTaskContent(row){
   open.value = true;
   title.value = row.taskDesc ;
   currentTaskContent.value = row.assistantContent ;
}

/** 配置Prompt */
function configPrompt(row){
   promptTitle.value = "查询角色参数";
   promptOpen.value = true ;
   currentPostId.value = row.id;

   // if(row.promptContent){
   //    currentPromptContent.value = JSON.parse(row.promptContent);
   // }

}

/** 查询生成历史记录 */
function handleTaskHistory(row){
   promptHistoryOpen.value = true ;
   currentBusinessId.value = row.id;
}

/** 提交按钮 */
function submitForm() {
   proxy.$refs["databaseRef"].validate(valid => {
      if (valid) {
         if (form.value.TaskId != undefined) {
            updateTask(form.value).then(response => {
               proxy.$modal.msgSuccess("修改成功");
               open.value = false;
               getList();
            });
         } else {
            addTask(form.value).then(response => {
               proxy.$modal.msgSuccess("新增成功");
               open.value = false;
               getList();
            });
         }
      }
   });
};

const timer = ref(null);

// onMounted(() => {
//   // 创建定时器
//   timer.value = setInterval(() => {
//     console.log('执行中...');
//     getList() ;
//   }, 5000);
// });

// onBeforeUnmount(() => {
//   // 在组件销毁前清除定时器
//   clearInterval(timer.value);
// });

getList();

</script>

<style lang="scss" scoped>
.role-icon {
  img {
    width:35px;
    height:35px;
  }
}
</style>
