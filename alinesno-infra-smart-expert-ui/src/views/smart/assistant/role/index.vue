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
              <div style="font-size: 13px;color: #a5a5a5;">
                会话: {{ scope.row.chatCount }} 
              </div>
            </template>
          </el-table-column>
          <el-table-column label="角色描述" align="left" key="responsibilities" prop="responsibilities" v-if="columns[2].visible">
            <template #default="scope">
              <div style="display: -webkit-box; -webkit-box-orient: vertical; overflow: hidden; -webkit-line-clamp: 2;">
                {{ scope.row.responsibilities }}
              </div>
            </template>
          </el-table-column>
 
          <el-table-column label="推送" align="center" width="120"  key="target" prop="target" v-if="columns[6].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" :disabled="scope.row.saleFromRoleId" text @click="pushOrg(scope.row)">
                <i class="fa-solid fa-truck-fast"></i> 推送
              </el-button>
            </template>
          </el-table-column> 

          <el-table-column label="知识库" align="center" width="120"  key="target" prop="target" v-if="columns[6].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" text @click="configKnowledge(scope.row)">
                <i class="fa-solid fa-file-word"></i>知识库
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="脚本" align="center" width="110"  key="target" prop="target" v-if="columns[6].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button type="primary" text @click="configExecuteScript(scope.row)">
                <i class="fa-solid fa-code"></i>脚本
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="推荐" align="center" width="100" key="hasRecommended" prop="hasRecommended" v-if="columns[1].visible" :show-overflow-tooltip="true" >
              <template #default="scope">
                <el-switch
                    v-model="scope.row.hasRecommended"
                    :active-value="true"
                    :inactive-value="false"
                    @change="handleRecommended(scope.row.id)"
                />
              </template>
          </el-table-column>
          <el-table-column label="分享" align="center" width="100" key="hasStatus" prop="hasStatus" v-if="columns[1].visible" :show-overflow-tooltip="true" >
              <template #default="scope">
                <el-switch
                    v-model="scope.row.hasSale"
                    :active-value="1"
                    :inactive-value="0"
                    :disabled="scope.row.saleFromRoleId"
                    @change="handleChangeSaleField('hasSale' , scope.row.hasSale, scope.row.id)"
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

    <!-- 添加或修改应用配置对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form :model="form" :rules="rules" ref="RoleRef" label-width="80px">
          <el-row>
          <el-col :span="24" class="editor-after-div">
            <el-form-item
                label="头像"
                :rules="[{ required: true, message: '请上传头像', trigger: 'blur',},]">
                <el-upload
                  :file-list="imageUrl"
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
                  <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
              </el-form-item>
          </el-col>
        </el-row>
        <el-col :span="24">
          <el-form-item label="角色类型" prop="roleType">
            <el-radio-group v-model="form.roleType">
              <el-radio v-for="item in roleTypes" :key="item.key" :label="item.key">{{ item.name }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
          <el-row>
              <el-col :span="24">
                <el-form-item style="width: 100%;" label="业务分类" prop="industryCatalog">
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
            <el-form-item label="开场白" prop="greeting">
                <el-input v-model="form.greeting" :placeholder="'你好，我是' + form.roleName + '🎉 '" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="执行类型" prop="scriptType">
              <el-radio-group v-model="form.scriptType">
                <el-radio :value="script" label="script">脚本</el-radio>
                <el-radio :value="flow" label="flow">流程</el-radio>
                <el-radio :value="react" label="react">推理</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row v-if="form.knowledgeId">
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

    <el-dialog v-model="pushOrgDialogFormVisible" title="请输入推送组织号" width="500">
      <div style="padding: 10px 20px;display: flex;align-items: center;margin-bottom:20px; line-height: 1.1rem; gap: 10px;margin-top: -20px;">
        <div>
          <img :src="imagePath(pushRoleInfo.roleAvatar)" style="width:70px;height:70px;border-radius:50%;" />
        </div>
        <div style="display: flex;flex-direction: column;gap: 5px;">
          <span style="font-size:16px;font-weight: 500;">
            <b>角色</b>:{{ pushRoleInfo.roleName }}
          </span>
          <span style="font-size: 13px;color: #a5a5a5;">
            {{ pushRoleInfo.responsibilities}}
          </span>
        </div>
      </div>  
      <el-form :model="form">
        <el-form-item label="组织ID" label-width="70px">
          <el-input v-model="pushOrgId" autocomplete="off" placeholder="请输入推送组织号">
            <template #append>
              <el-button @click="handleFindOrg" icon="Search" />
            </template>
          </el-input> 
        </el-form-item>
      </el-form>
      <div style="padding:10px 20px;" v-if="pushOrgInfo.id">
        <div>
          <p><i class="fa-solid fa-paper-plane"></i> 组织ID: {{ pushOrgInfo.id }}</p>
          <p><i class="fa-brands fa-slack"></i> 组织名称: {{ pushOrgInfo.orgName }}</p>
          <p><i class="fa-solid fa-file-word"></i> 备注: {{ pushOrgInfo.remark }}</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="pushOrgDialogFormVisible = false">取消</el-button>
          <el-button type="primary" :disabled="enablePushOrg==false" @click="handleConfirmPushOrg">
           确认推送 
          </el-button>
        </div>
      </template>
    </el-dialog>
    
  </div>
</template>

<script setup name="Role">
import {getToken} from "@/utils/auth";
import { ElLoading } from 'element-plus'

import {
  listRole,
  delRole,
  findOrg,
  getRole,
  updateRole,
  catalogTreeSelect,
  addRole,
  confirmPushOrg,
  changStatusField,
  changeSaleField,
  recommended
} from "@/api/smart/assistant/role";

import KnowledgeDataset from '@/views/smart/assistant/role/knowledge/parseDataset'
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
const imageUrl = ref([])

const knowledgeDatasetRef = ref(null)
const knowDrawerDialog = ref(false)
const knowTitle = ref("")
const knowRoleAvatar = ref("")

const pushOrgDialogFormVisible = ref(false)
const pushOrgId = ref(null)
const pushRoleInfo = ref({})
const enablePushOrg = ref(false)
const pushOrgInfo = ref({
  doorplateNumber: "" ,
  id: "",
  orgName: "" , 
  remark: ""
})

const chainOpen = ref(false);
const promptOpen = ref(false);

const deptName = ref("");
const deptOptions = ref(undefined);

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
  url: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/importData",
  // 显示地址
  display: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" 
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

const roleTypes = ref([
  { key: 'single_role', name: '单角色', description: '自己单独完成一个聊天，可流式输出或者同步输出' },
  { key: 'collaborative_role', name: '协作角色', description: '与其它角色协作才可以完成一个工作，可流式输出或者同步输出' },
  { key: 'scenario_role', name: '场景角色', description: '与其它角色协作才可以完成一个工作，只能同步输出' },
  { key: 'combined_role', name: '组合角色', description: '结合了协作与场景角色的特点，既可以在协作中发挥作用，也可以适应特定场景，支持流式输出和同步输出' }
]);

const data = reactive({
  form: {
    roleAvatar: null,
    scriptType: 'script'
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
    roleType: [{required: true, message: "应用类型不能为空", trigger: "blur"}],
    scriptType: [{required: true, message: "脚本类型不能为空", trigger: "blur"}],
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
    industryCatalog: [{required: true, message: "角色类型不能为空", trigger: "blur"}],
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

/** 查询组织信息 */
function handleFindOrg(){
  if(!pushOrgId.value){
    proxy.$modal.msgError("请输入组织号");
    return ;
  }
  findOrg(pushOrgId.value).then(res => {
    console.log('res = ' + res) ;
    if(data){
      pushOrgInfo.value = res.data ;
      enablePushOrg.value = true ;
    }else{
      proxy.$modal.msgError("查询组织信息失败，请确认组织号是否正确.");
    }
  });
}

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
  imageUrl.value = response.data ? response.data.split(',').map(url =>{return { url:upload.display + url }}):[];
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

/** 推荐组织Hero角色 */
function handleRecommended(roleId){
  recommended(roleId).then(res => {
    proxy.$modal.msgSuccess("推荐成功");
    getList();
  })
}

/** 推送到指定的组织 */
function pushOrg(row){
  pushOrgDialogFormVisible.value = true;
  pushRoleInfo.value = row ;
  pushOrgInfo.value = {id:null}
}

/** 确认推送到指定的组织 */
function handleConfirmPushOrg(){
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })

  let data = {
    roleId: pushRoleInfo.value.id ,
    orgId: pushOrgId.value,
  }

  confirmPushOrg(data).then(res => {
    console.log('res = ' + res) ;
    loading.close();
    proxy.$modal.msgSuccess("推送成功");
  }).catch(() => {
    loading.close();
  })
}

/** 修改状态 */
const handleChangeSaleField= async(field , value , id) => {
   // 判断tags值 这样就不会进页面时调用了
     const res = await changeSaleField({
        field: field,
        value: value?1:0,
        id: id
     }).catch(() => { })
     if (res && res.code == 200) {
        // 刷新表格
        getList()
     }
}

// /** 修改状态 */
// const handleChangStatusField = async(field , value , id) => {
//    // 判断tags值 这样就不会进页面时调用了
//      const res = await changStatusField({
//         field: field,
//         value: value?1:0,
//         id: id
//      }).catch(() => { })
//      if (res && res.code == 200) {
//         // 刷新表格
//         getList()
//      }
// }

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

/** 配置执行脚本 */
function configExecuteScript(row){

  console.log('scriptType = ' + row.scriptType)

  if(row.scriptType && row.scriptType == 'flow'){  // 流程
    router.push({path:"/smart/assistant/role/createDefinition" , query: {roleId: row.id}});
  }else if(row.scriptType && row.scriptType == 'react'){  // 推荐
    router.push({path:"/expert/smart/assistant/role/react" , query: {roleId: row.id}});
  }else {  // 脚本
    router.push({path:"/expert/smart/assistant/role/script" , query: {roleId: row.id}});
  }

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
      width:56px;
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