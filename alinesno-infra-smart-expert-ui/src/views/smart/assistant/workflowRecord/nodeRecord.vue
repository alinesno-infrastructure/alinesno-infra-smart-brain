<template>
  <div>
    <el-row :gutter="20">
       <!--节点数据-->
      <el-col :span="24" :xs="24">

        <el-table v-loading="loading" :data="NodeExcectionList" @selection-change="handleSelectionChange">
          <el-table-column type="index" width="50" label="序号" align="center"/>

          <el-table-column label="节点名称" align="left" key="nodeName" prop="nodeName" v-if="columns[1].visible" :show-overflow-tooltip="true"/>
          <el-table-column label="执行时间" align="left" width="150" key="timeSpent" prop="timeSpent" v-if="columns[4].visible">
            <template #default="scope">
              {{ handleFormatTime(scope.row.timeSpent) }}
            </template>
          </el-table-column>
          <el-table-column label="执行状态" align="center" key="status" prop="workflowStatus" v-if="columns[3].visible" width="100" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button v-if="scope.row.status === 'success'" type="success" :icon="CircleCheck" text bg>成功</el-button>
              <el-button v-if="scope.row.status === 'fail'"  type="warning" :icon="Warning" text bg>失败</el-button>
            </template>
          </el-table-column>

          <el-table-column label="结束时间" align="center" prop="addTime" v-if="columns[7].visible" width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.endTime ) }}</span>
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

    <!-- 添加或修改节点配置对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-form :model="form" :rules="rules" ref="NodeExcectionRef" label-width="80px">
        <el-row>
          <el-col :span="24">
            <el-form-item  label="节点名称" prop="nodeName">
              <el-input v-model="form.nodeName" placeholder="请输入节点名称" maxlength="50"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="显示名称" prop="showName">
              <el-input v-model="form.showName" placeholder="请输入显示名称" maxlength="50"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="域名" prop="timeSpent">
              <el-input v-model="form.timeSpent" placeholder="请输入域名" maxlength="100"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="所属领域" prop="domain">
              <el-input v-model="form.domain" placeholder="请输入所属领域" maxlength="100"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="安全存储路径" prop="storagePath" label-width="107px">
              <el-input v-model="form.storagePath" placeholder="请输入安全存储路径" maxlength="200"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="执行状态" prop="target">
              <el-input v-model="form.target" placeholder="请输入执行状态" maxlength="20"/>
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

    <!-- 节点导入对话框 -->
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
              是否更新已经存在的节点数据
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

<script setup name="NodeExcection">
import {getToken} from "@/utils/auth";
import {
  listNodeExcection,
  delNodeExcection,
  getNodeExcection,
  updateNodeExcection,
  addNodeExcection,
  getNodeList ,
} from "@/api/smart/assistant/workflowNodeExecution";
import {reactive} from "vue";

const router = useRouter();
const {proxy} = getCurrentInstance();
// const { sys_normal_disable, sys_NodeExcection_sex } = proxy.useDict("sys_normal_disable", "sys_NodeExcection_sex");

//接收父组件传过来的值,需要注意defineProps只能在setup语法糖里面使用，不需要import引入
const parentParams= defineProps({
	currentWorkflowId:{
		type: String,
  }
})

const NodeExcectionList = ref([]);
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
/*** 节点导入参数 */
const upload = reactive({
  // 是否显示弹出层（节点导入）
  open: false,
  // 弹出层标题（节点导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的节点数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: {Authorization: "Bearer " + getToken()},
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/system/NodeExcection/importData"
});
// 列显隐信息
const columns = ref([
  {key: 0, label: `图标`, visible: true},
  {key: 1, label: `节点名称`, visible: true},
  {key: 2, label: `显示名称`, visible: true},
  {key: 3, label: `所属领域`, visible: true},
  {key: 4, label: `域名`, visible: true},
  {key: 5, label: `安全存储路径`, visible: true},
  {key: 6, label: `执行状态`, visible: true},
  {key: 7, label: `创建时间`, visible: true},
  {key: 8, label: `编辑`, visible: true},

]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    currentWorkflowId: parentParams.currentWorkflowId ,
    nodeName: undefined,
    showName: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    NodeExcectionId: [{required: true, message: "节点编号不能为空", trigger: "blur"}],
    nodeName: [{required: true, message: "节点名称不能为空", trigger: "blur"}, {
      min: 2,
      max: 20,
      message: "节点名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    showName: [{required: true, message: "显示名称不能为空", trigger: "blur"}],
    domain: [{required: true, message: "所属领域不能为空", trigger: "blur"}],
    timeSpent: [{required: true, message: "域名不能为空", trigger: "blur"}],
    storagePath: [{required: true, message: "安全存储路径不能为空", trigger: "blur"}],
    target: [{required: true, message: "执行状态不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules} = toRefs(data);


/** 查询节点列表 */
function getList() {
  loading.value = true;

  listNodeExcection(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    NodeExcectionList.value = res.rows;
    total.value = res.total;
  });

};

/** 时间格式化 */
function handleFormatTime(mTime) {

  const seconds = mTime/60 ; 
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const remainingSeconds = Math.floor(seconds % 60);
    
  let formattedTime = '';
    
  if (hours > 0) {
      formattedTime += `${hours}时`;
  }
  if (minutes > 0 || hours > 0) {
      formattedTime += `${minutes}分`;
  }
  formattedTime += `${remainingSeconds}秒`;
    
  return formattedTime;
}

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
  queryParams.value.nodeName = undefined;
  queryParams.value.showName = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
  const NodeExcectionIds = row.id || ids.value;

  proxy.$modal.confirm('是否确认删除节点编号为"' + NodeExcectionIds + '"的数据项？').then(function () {
    return delNodeExcection(NodeExcectionIds);
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
    NodeExcectionId: undefined,
    nodeName: undefined,
    showName: undefined,
    domain: undefined,
    timeSpent: undefined,
    storagePath: undefined,
    target: undefined,
  };
  proxy.resetForm("NodeExcectionRef");
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
  title.value = "添加节点";
};

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const NodeExcectionId = row.id || ids.value;
  getNodeExcection(NodeExcectionId).then(response => {
    form.value = response.data;
    form.value.NodeExcectionId = NodeExcectionId;
    open.value = true;
    title.value = "修改节点";

  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["NodeExcectionRef"].validate(valid => {
    if (valid) {
      if (form.value.NodeExcectionId != undefined) {
        updateNodeExcection(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addNodeExcection(form.value).then(response => {
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