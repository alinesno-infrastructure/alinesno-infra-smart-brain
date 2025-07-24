<template>
   <div>
      <el-row :gutter="20">
         <!--类型数据-->
         <el-col :span="24" :xs="24">

            <el-table v-loading="loading" :data="TaskHistoryList" @selection-change="handleSelectionChange">
               <el-table-column type="selection" width="50" align="center" />

               <el-table-column label="图标" align="center" width="80px" prop="icon" v-if="columns[0].visible">
                  <template #default="scope">
                  <div class="role-icon">
                     <img :src="'http://data.linesno.com/icons/dataset/dataset_' + (scope.$index + 8) + '.png'" style="width:45px;height:45px;border-radius: 50%;" />
                  </div>
                  </template>
               </el-table-column>

               <!-- 业务字段-->
               <el-table-column label="类型名称" align="left" width="200" key="name" prop="name" v-if="columns[0].visible">
                  <template #default="scope">
                  <div style="font-size: 15px;font-weight: 500;color: #1d75b0;">
                     {{ scope.row.name }}
                  </div>
                  <div style="font-size: 13px;color: #a5a5a5;">
                     {{ scope.row.name }}
                  </div>
                  </template>
               </el-table-column>
               <el-table-column label="类型描述" align="left" key="description" prop="description" v-if="columns[1].visible" :show-overflow-tooltip="true">
                  <template #default="scope">
                     <div>
                        {{ scope.row.description }}
                     </div>
                     <div style="font-size: 13px;color: #a5a5a5;">
                        会话次数: 12734 有效沟通:198312
                     </div>
                  </template>
               </el-table-column>
               <el-table-column label="生成式数量" align="center" width="150" key="promptCount" prop="promptCount" v-if="columns[2].visible" :show-overflow-tooltip="true" />
               <el-table-column label="状态" align="center" width="100" key="hasStatus" v-if="columns[5].visible" />

               <el-table-column label="添加时间" align="center" prop="addTime" v-if="columns[6].visible" width="160">
                  <template #default="scope">
                     <span>{{ parseTime(scope.row.addTime) }}</span>
                  </template>
               </el-table-column>

               <!-- 操作字段  -->
               <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
                  <template #default="scope">
                     <el-tooltip content="修改" placement="top" v-if="scope.row.TaskHistoryId !== 1">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                           v-hasPermi="['system:TaskHistory:edit']"></el-button>
                     </el-tooltip>
                     <el-tooltip content="删除" placement="top" v-if="scope.row.TaskHistoryId !== 1">
                        <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                           v-hasPermi="['system:TaskHistory:remove']"></el-button>
                     </el-tooltip>
                  </template>

               </el-table-column>
            </el-table>
            <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
         </el-col>
      </el-row>

      <!-- 添加或修改类型配置对话框 -->
      <el-dialog :title="title" v-model="open" width="700px" append-to-body>
         <el-form :model="form" :rules="rules" ref="databaseRef" label-width="100px">
            <el-row>
               <el-col :span="24">
                  <el-form-item label="父类" prop="name">
                     <el-select clearable v-model="form.parentId" style="width:100%" placeholder="可多选" :multiple-limit="10">
                        <el-option v-for="item in 10" :key="item" :label="item" :value="item" />
                     </el-select>
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="24">
                  <el-form-item label="名称" prop="name">
                     <el-input v-model="form.name" placeholder="请输入类型名称" maxlength="50" />
                  </el-form-item>
               </el-col>
            </el-row>
            <el-row>
               <el-col :span="24">
                  <el-form-item label="描述" prop="description">
                     <el-input v-model="form.description" placeholder="请输入jdbcUrl连接地址" maxlength="128" />
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

<script setup name="TaskHistory">

import {
   listTaskHistory,
   delTaskHistory,
   getTaskHistory,
   updateTaskHistory,
   addTaskHistory
} from "@/api/smart/brain/taskHistory";

const props = defineProps({
  currentBusinessId: {
      type: String,
      require: true,
    },
})

const router = useRouter();
const { proxy } = getCurrentInstance();

// 定义变量
const checkboxList = ref([])
const TaskHistoryList = ref([]);
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

// 列显隐信息
const columns = ref([
   { key: 0, label: `类型名称`, visible: true },
   { key: 1, label: `类型描述`, visible: true },
   { key: 2, label: `表数据量`, visible: true },
   { key: 3, label: `类型`, visible: true },
   { key: 4, label: `类型地址`, visible: true },
   { key: 5, label: `状态`, visible: true },
   { key: 6, label: `更新时间`, visible: true }
]);

const data = reactive({
   form: {},
   queryParams: {
      pageNum: 1,
      pageSize: 10,
      name: undefined,
      description: undefined
   },
   rules: {
      name: [{ required: true, message: "名称不能为空", trigger: "blur" }] , 
      description: [{ required: true, message: "连接不能为空", trigger: "blur" }],
      promptCount: [{ required: true, message: "类型不能为空", trigger: "blur" }] , 
      parentId: [{ required: true , message: "用户名不能为空", trigger: "blur"}],
   }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询类型列表 */
function getList() {
   loading.value = true;
   listTaskHistory(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
      loading.value = false;
      TaskHistoryList.value = res.rows;
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
   const TaskHistoryIds = row.id || ids.value;
   proxy.$modal.confirm('是否确认删除类型编号为"' + TaskHistoryIds + '"的数据项？').then(function () {
      return delTaskHistory(TaskHistoryIds);
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
      TaskHistoryName: undefined,
      promptCount: undefined,
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
   reset();
};

/** 新增按钮操作 */
function handleAdd() {
   reset();
   open.value = true;
   title.value = "添加类型";
};

/** 修改按钮操作 */
function handleUpdate(row) {
   reset();
   const TaskHistoryId = row.id || ids.value;
   getTaskHistory(TaskHistoryId).then(response => {
      form.value = response.data;
      open.value = true;
      title.value = "修改类型";
   });
};

/** 提交按钮 */
function submitForm() {
   proxy.$refs["databaseRef"].validate(valid => {
      if (valid) {
         if (form.value.TaskHistoryId != undefined) {
            updateTaskHistory(form.value).then(response => {
               proxy.$modal.msgSuccess("修改成功");
               open.value = false;
               getList();
            });
         } else {
            addTaskHistory(form.value).then(response => {
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
