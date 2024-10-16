<template>
  <div class="app-container">
    <el-row :gutter="20">

            <!--类型数据-->
      <el-col :span="4" :xs="24">
            <div class="head-container">
               <el-input
                  v-model="deptName"
                  placeholder="请输入类型名称"
                  clearable
                  prefix-icon="Search"
                  style="margin-bottom: 20px"
               />
            </div>
            <div class="head-container">
               <el-tree
                  :data="deptOptions"
                  :props="{ label: 'label', children: 'children' }"
                  :expand-on-click-node="false"
                  :filter-node-method="filterNode"
                  ref="deptTreeRef"
                  node-key="id"
                  default-expand-all 
                  highlight-current
                  @node-click="handleNodeClick"
               />
                  <!-- default-expand-all -->
            </div>
         </el-col>

       <!--应用数据-->
      <el-col :span="20" :xs="20">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">

          <el-form-item label="角色名称" prop="roleName">
            <el-input v-model="queryParams['condition[roleName|like]']" placeholder="请输入角色名称" clearable style="width: 240px" @keyup.enter="handleQuery"/>
          </el-form-item>
          <el-form-item label="角色描述" prop="responsibilities" label-width="100px">
            <el-input v-model="queryParams['condition[responsibilities|like]']" placeholder="请输入角色描述" clearable style="width: 240px" @keyup.enter="handleQuery"/>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
                type="primary"
                plain
                icon="Plus"
                @click="handleAdd"
            >新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                type="success"
                plain
                icon="Edit"
                :disabled="single"
                @click="handleUpdate"
                v-hasPermi="['system:Role:edit']"
            >修改
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
                type="danger"
                plain
                icon="Delete"
                :disabled="multiple"
                @click="handleDelete"
                v-hasPermi="['system:Role:remove']"
            >删除
            </el-button>
          </el-col>

          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="RoleList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column label="图标" align="center" width="60px" prop="icon" v-if="columns[0].visible">
            <template #default="scope">
              <div class="role-icon">
                <img :src="imagePath(scope.row.roleAvatar)" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="角色名称" align="left" width="180" key="roleName" prop="roleName" v-if="columns[1].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div style="font-size: 15px;font-weight: 500;color: #3b5998;">
                {{ truncateString(scope.row.roleName , 10) }}
              </div>
                     <div style="font-size: 13px;color: #a5a5a5;cursor: pointer;" v-copyText="scope.row.chainId">
                代码:{{ truncateString(scope.row.chainId,20) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="角色描述" align="left" key="responsibilities" prop="responsibilities" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                {{ scope.row.responsibilities }}
              </div>
              <div style="font-size: 13px;color: #a5a5a5;">
                会话: {{ scope.row.chatCount }} 
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所属类型" align="center" width="130" key="domain" prop="domain" v-if="columns[3].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <i class="fa-solid fa-user-astronaut icon-btn"></i> {{ scope.row.industryCatalog }}
            </template>
          </el-table-column>
          <el-table-column label="配置Prompt" align="center" width="120"  key="target" prop="target" v-if="columns[6].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" text icon="Paperclip" @click="configPrompt(scope.row)">配置</el-button>
            </template>
          </el-table-column>
          <el-table-column label="知识库" align="center" width="120"  key="target" prop="target" v-if="columns[6].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" text icon="Promotion" @click="configKnowledge(scope.row)">知识库</el-button>
            </template>
          </el-table-column>
          <el-table-column label="脚本" align="center" width="110"  key="target" prop="target" v-if="columns[6].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" text icon="Promotion" @click="configExecuteScript(scope.row)">脚本</el-button>
            </template>
          </el-table-column>
          <el-table-column label="执行流程" align="center" width="110"  key="target" prop="target" v-if="columns[6].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" text icon="Promotion" @click="configExecuteFlow(scope.row)">配置</el-button>
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" width="100" key="hasStatus" prop="hasStatus" v-if="columns[1].visible" :show-overflow-tooltip="true" >
              <template #default="scope">
                <el-switch
                    v-model="scope.row.hasStatus"
                    :active-value="0"
                    :inactive-value="1"
                    @change="handleChangStatusField('hasStatus' , scope.row.hasStatus, scope.row.id)"
                />
              </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="100" class-name="small-padding fixed-width" v-if="columns[8].visible">
            <template #default="scope">
              <el-tooltip content="更新" placement="top" v-if="scope.row.roleId !== 1">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:Role:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.roleId !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:Role:remove']"></el-button>
              </el-tooltip>

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

      <!-- 添加或修改指令配置对话框 -->
      <el-dialog :title="promptTitle" v-model="promptOpen" width="980px" :before-close="handleClosePrompt" destroy-on-close append-to-body>
         <PromptEditor :currentPrompt="currentPrompt" />
      </el-dialog>

    <!-- 添加或修改应用配置对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form :model="form" :rules="rules" ref="RoleRef" label-width="80px">
          <el-row>
            <el-col :span="24" class="editor-after-div">
              <el-form-item
                  label="头像"
                  :rules="[{ required: true, message: '请上传头像', trigger: 'blur',},]">
                  <el-upload
                    :file-list="fileList"
                    :action="upload.url + '?type=img&updateSupport=' + upload.updateSupport"
                    list-type="picture-card"
                    :auto-upload="true"
                    :limit="1"
                    :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                    :headers="upload.headers"
                    :disabled="upload.isUploading"
                    :on-progress="handleFileUploadProgress"
                  >
                    <!-- <img v-if="form.roleAvatar" style="width:100%;height:100%" :src="imagePath(form.roleAvatar)" /> -->
                    <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                  </el-upload>
                </el-form-item>
            </el-col>
          </el-row>
          <el-row>
              <el-col :span="24">
                <el-form-item style="width: 100%;" label="类型" prop="industryCatalog">
                    <el-tree-select
                      v-model="form.industryCatalog"
                      :data="deptOptions"
                      :props="{ value: 'id', label: 'label', children: 'children' }"
                      value-key="id"
                      placeholder="请选择归属类型"
                      check-strictly
                    />
                </el-form-item>
              </el-col>
          </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item  label="名称" prop="roleName">
              <el-input v-model="form.roleName" placeholder="请输入角色名称" maxlength="50"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="描述" prop="responsibilities">
              <el-input v-model="form.responsibilities" placeholder="请输入角色描述" maxlength="50"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="级别" prop="roleLevel">
              <el-input v-model="form.roleLevel" placeholder="请输入角色级别" maxlength="100"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="知识库" prop="knowledgeId">
              <el-input v-model="form.knowledgeId" disabled="disabled" placeholder="请输入知识库ID" maxlength="512"/>
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

    <!-- 知识库配置 -->
    <el-drawer
      v-model="knowDrawerDialog"
      size="50%"
      with-header="false"
      :title="knowTitle"
      :direction="right"
      :before-close="handleCloseKnowDrawer">
      <template #header>
        <div class="role-icon">
          <img :src="imagePath(knowRoleAvatar)" style="float:left" /> 
          <div style="float: left;padding-top: 5px;margin-left: 10px;">
            {{ knowTitle }}
          </div> 
        </div>
      </template>
      <template #default>
        <KnowledgeDataset ref="knowledgeDatasetRef" />
      </template>
    </el-drawer>
    
  </div>
</template>

<script setup name="Role">
import {getToken} from "@/utils/auth";
import {
  listRole,
  delRole,
  getRole,
  updateRole,
  catalogTreeSelect,
  addRole,
  saveRoleChainInfo,
} from "@/api/smart/assistant/role";

import KnowledgeDataset from '@/views/smart/assistant/role/knowledge/parseDataset'
import ExecuteScriptPanel from '@/views/smart/assistant/role/executeScriptPanel'

// import {
//   addRoleChain , 
//   updateRoleChain,
// } from "@/api/smart/assistant/chain"

import PromptEditor from "./editor.vue"
import { ElMessage } from 'element-plus'
import {reactive} from "vue";

const router = useRouter();
const { proxy } = getCurrentInstance();
// const { sys_normal_disable, sys_Role_sex } = proxy.useDict("sys_normal_disable", "sys_Role_sex");

const RoleList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const imageUrl = ref('')

const knowledgeDatasetRef = ref(null)
const knowDrawerDialog = ref(false)
const knowTitle = ref("")
const knowRoleAvatar = ref("")

// 执行脚本
const executeScriptRef = ref(null)
const executeScriptDialog = ref(false)
const executeScriptTitle = ref("")

const chainOpen = ref(false);
// const chainTitle = ref("");
const promptTitle = ref("");
const currentPrompt = ref("");
// const currentPromptContent = ref([]);
const promptOpen = ref(false);

const deptName = ref("");
const deptOptions = ref(undefined);

// const initPassword = ref(undefined);
// const postOptions = ref([]);
// const roleOptions = ref([]);

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
  headers: {Authorization: "Bearer " + getToken()},
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/importData"
});
// 列显隐信息
const columns = ref([
  {key: 0, label: `图标`, visible: true},
  {key: 1, label: `角色名称`, visible: true},
  {key: 2, label: `角色描述`, visible: true},
  {key: 3, label: `所属领域`, visible: true},
  {key: 4, label: `角色级别`, visible: true},
  {key: 5, label: `安全存储路径`, visible: true},
  {key: 6, label: `应用目标`, visible: true},
  {key: 7, label: `创建时间`, visible: true},
  {key: 8, label: `编辑`, visible: true},

]);

const data = reactive({
  form: {
    roleAvatar: null
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    roleName: undefined,
    roleName: undefined,
    responsibilities: undefined,
    status: undefined,
    industryCatalog: undefined
  },
  rules: {
    roleId: [{required: true, message: "应用编号不能为空", trigger: "blur"}],
    roleName: [{required: true, message: "角色名称不能为空", trigger: "blur"}, {
      min: 2,
      max: 20,
      message: "角色名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    responsibilities: [{required: true, message: "角色描述不能为空", trigger: "blur"}],
    domain: [{required: true, message: "所属领域不能为空", trigger: "blur"}],
    roleLevel: [{required: true, message: "角色级别不能为空", trigger: "blur"}],
    storagePath: [{required: true, message: "安全存储路径不能为空", trigger: "blur"}],
    target: [{required: true, message: "应用目标不能为空", trigger: "blur"}],
  },
  chainForm: {
    roleId: undefined,
  },
  chainRules: {
    chainName: [{required: true, message: "链路名称不能为空", trigger: "blur"}],
    elData: [{required: true, message: "链路流程不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules , chainForm , chainRules} = toRefs(data);

/** 查询应用列表 */
function getList() {
  loading.value = true;
  listRole(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    RoleList.value = res.rows;
    total.value = res.total;
  });
};

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
  imageUrl.value = URL.createObjectURL(uploadFile.raw);
  form.value.roleAvatar = response.data ;
  console.log('form.roleAvatar = ' + form.roleAvatar);
};

/** 图片上传之前 */
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!');
    return false;
  }
  return true;
};

/** 关闭对话框 */
function handleClosePrompt(){
   promptOpen.value = false;
  getList();
}

// 节点单击事件
function handleNodeClick(data) {
   queryParams.value.industryCatalog = data.id;
   console.log('data.id = ' + data.id)
   getList();
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
  queryParams.value.roleName = undefined;
  queryParams.value.responsibilities = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
  const roleIds = row.id || ids.value;

  proxy.$modal.confirm('是否确认删除应用编号为"' + roleIds + '"的数据项？').then(function () {
    return delRole(roleIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
};

/** 配置知识库 */
function configKnowledge(row){

  knowDrawerDialog.value = true
  knowTitle.value = '[' + row.roleName + ']知识库配置'
  knowRoleAvatar.value = row.roleAvatar

  nextTick(() => {
    knowledgeDatasetRef.value.initRoleData(row);
  });
  
}

/** 配置执行流程 */
function configExecuteFlow(row){
  router.push({path:"/smart/assistant/role/createDefinition" , query: {roleId: row.id}});
}

/** 配置执行脚本 */
function configExecuteScript(row){
  router.push({path:"/expert/smart/assistant/role/script" , query: {roleId: row.id}});
}

/** 配置Prompt */
function configPrompt(row){
   promptTitle.value = "配置角色Prompt";
   promptOpen.value = true ;
   currentPrompt.value = row ;

  //  if(row.promptContent){
  //     currentPromptContent.value = JSON.parse(row.promptContent);
  //  }
}

/** 关闭弹窗 */
function handleCloseKnowledge(){
   knowDrawerDialog.value = false
}

/** 运行一次专家链路 */
// function handleRunChain(row){

//   loading.value = true;
//   let text = '测试数据' ; 
//   runRoleChainByRoleId(row.id , text).then(response => {
//     proxy.$modal.msgSuccess("运行成功.");
//     loading.value = false;
//   })
// }

// /** 配置用户链路流程 */
// function handleLangChain(row){
//   const roleId = row.id || ids.value;

//   getRoleChainByChainId(roleId).then(response => {
//     chainForm.value = response.data;
//     chainForm.value.roleId = roleId;

//     chainOpen.value = true;
//     chainTitle.value = "配置链路";
//   });
// }

/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 重置操作表单 */
function reset() {
  form.value = {
    roleId: undefined,
    roleName: undefined,
    responsibilities: undefined,
    domain: undefined,
    roleLevel: undefined,
    storagePath: undefined,
    target: undefined,
  };
  proxy.resetForm("RoleRef");
};

/** 取消按钮 */
function cancel() {
  open.value = false;
  chainOpen.value = false;
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
  const roleId = row.id || ids.value;
  getRole(roleId).then(response => {
    form.value = response.data;
    form.value.roleId = roleId;
    open.value = true;
    title.value = "修改应用";
  });
};

/** 提交流程按钮 */
function submitChainForm() {
  proxy.$refs["ChainRef"].validate(valid => {

    if (valid) {
      if (chainForm.value.id != undefined) {
        updateRoleChain(chainForm.value).then(response => {
          proxy.$modal.msgSuccess("配置成功");
          chainOpen.value = false;
          getList();
        });
      } else {
        saveRoleChainInfo(chainForm.value , chainForm.value.roleId).then(response => {
          proxy.$modal.msgSuccess("配置成功");
          chainOpen.value = false;
          getList();
        });
      }
    }
  });
};


/** 提交按钮 */
function submitForm() {
  proxy.$refs["RoleRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateRole(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addRole(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
};

/** 查询类型下拉树结构 */
function getDeptTree() {
  catalogTreeSelect().then(response => {
    deptOptions.value = response.data;
  });
};

getDeptTree();
getList();

</script>

<style lang="scss" scoped>
.role-icon {
  img {
    width:40px;
    height:40px;
    border-radius: 5px;
  }
}

.editor-after-div {
  .el-upload{
      widtH:56px;
      height: 56px;
      text-align: center;
      line-height: 56px;
  }
  .el-upload-list__item-thumbnail{
      width: 56px;
      height: 56px;
  }
  .el-upload-list__item{
      width: 56px;
      height: 56px;
  }
}

</style>