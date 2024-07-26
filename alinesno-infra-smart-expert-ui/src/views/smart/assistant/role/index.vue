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
                  highlight-current
                  default-expand-all
                  @node-click="handleNodeClick"
               />
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
                <img :src="imagePath(scope.row)" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="角色名称" align="left" width="180" key="roleName" prop="roleName" v-if="columns[1].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div style="font-size: 15px;font-weight: 500;color: #3b5998;">
                {{ scope.row.roleName }}
              </div>
              <div style="font-size: 13px;color: #a5a5a5;">
                 {{ scope.row.roleLevel }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="角色描述" align="left" key="responsibilities" prop="responsibilities" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                {{ scope.row.responsibilities }}
              </div>
              <div style="font-size: 13px;color: #a5a5a5;">
                会话次数: 12734 有效沟通:198312
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所属领域" align="center" width="180" key="domain" prop="domain" v-if="columns[3].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <i class="fa-solid fa-user-astronaut icon-btn"></i> {{ scope.row.industryCatalog }}
            </template>
          </el-table-column>
          <el-table-column label="知识库" align="center" width="120"  key="roleLevel" prop="roleLevel" v-if="columns[4].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" text bg icon="CopyDocument"  @click="handleLangChain(scope.row)" >配置</el-button>
            </template>
          </el-table-column>
          <!--
          <el-table-column label="角色技能" align="center" width="150"  key="storagePath" prop="storagePath" v-if="columns[5].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" text bg icon="Paperclip"  @click="handleLangChain(scope.row)" >配置</el-button>
            </template>
          </el-table-column>
          -->
          <el-table-column label="流程定义" align="center" width="150"  key="target" prop="target" v-if="columns[6].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" text bg icon="Postcard" @click="handleLangChain(scope.row)" >专家链路</el-button>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width" v-if="columns[8].visible">
            <template #default="scope">
              <el-tooltip content="运行对话" placement="top" v-if="scope.row.roleId !== 1">
                <el-button link type="primary" icon="Position" @click="handleRunChain(scope.row)" v-hasPermi="['system:Role:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="对话记录" placement="top" v-if="scope.row.roleId !== 1">
                <el-button link type="primary" icon="ChatLineSquare" @click="handleUpdate(scope.row)" v-hasPermi="['system:Role:edit']"></el-button>
              </el-tooltip>
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

    <!-- 添加或修改角色链路 -->
    <el-dialog :title="chainTitle" v-model="chainOpen" width="900px" append-to-body>
      <el-form :model="chainForm" :rules="chainRules" ref="ChainRef" label-width="80px">
        <el-row>
          <el-col :span="24">
            <el-form-item  label="流程名称" prop="chainName">
              <el-input v-model="chainForm.chainName" placeholder="请输入链路名称" maxlength="50"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item  label="流程描述" prop="chainDescription">
              <el-input v-model="chainForm.chainDescription" placeholder="请输入流程描述" maxlength="50"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="流程数据" prop="elData">
              <el-input v-model="chainForm.elData" placeholder="请输入链路流程" maxlength="120"/>
            </el-form-item>
          </el-col>
        </el-row>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitChainForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加或修改应用配置对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form :model="form" :rules="rules" ref="RoleRef" label-width="80px">
          <el-row>
            <el-col :span="24" class="editor-after-div">
              <el-form-item
                  label="头像"
                  :rules="[{
                      required: true,
                      message: '请上传运行效果',
                      trigger: 'blur',
                    },]"
                >
                  <el-upload
                    :file-list="fileList"
                    :action="upload.url + '?type=img&updateSupport=' + upload.updateSupport"
                    list-type="picture-card"
                    :auto-upload="true"
                    :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload"
                    :headers="upload.headers"
                    :disabled="upload.isUploading"
                    :on-progress="handleFileUploadProgress"
                  >
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
            <el-form-item label="数据源" prop="dataSourceApi">
              <el-input v-model="form.dataSourceApi" placeholder="请输入角色数据来源接口" maxlength="512"/>
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

    <!-- 应用导入对话框 -->
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
              是否更新已经存在的应用数据
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

<script setup name="Role">
import {getToken} from "@/utils/auth";
import {
  listRole,
  delRole,
  getRole,
  updateRole,
  catalogTreeSelect,
  addRole,
  getRoleChainByChainId,
  saveRoleChainInfo,
  runRoleChainByRoleId,
} from "@/api/smart/assistant/role";

import {
  addRoleChain , 
  updateRoleChain,
} from "@/api/smart/assistant/chain"

import { ElMessage } from 'element-plus'
import {reactive} from "vue";

const router = useRouter();
const {proxy} = getCurrentInstance();
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

const chainOpen = ref(false);
const chainTitle = ref("");

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
  headers: {Authorization: "Bearer " + getToken()},
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/role/importData"
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

/** 显示图片 */
function imagePath(row){
  let roleAvatar = '1746435800232665090' ; 
  if(row.roleAvatar){
    roleAvatar = row.roleAvatar ; 
  }
  return import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/role/displayImage/" + roleAvatar ; 
}

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

/** 运行一次专家链路 */
function handleRunChain(row){

  loading.value = true;
  let text = '测试数据' ; 
  runRoleChainByRoleId(row.id , text).then(response => {
    proxy.$modal.msgSuccess("运行成功.");
    loading.value = false;
  })
}

/** 配置用户链路流程 */
function handleLangChain(row){
  const roleId = row.id || ids.value;

  getRoleChainByChainId(roleId).then(response => {
    chainForm.value = response.data;
    chainForm.value.roleId = roleId;

    chainOpen.value = true;
    chainTitle.value = "配置链路";
  });

}

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