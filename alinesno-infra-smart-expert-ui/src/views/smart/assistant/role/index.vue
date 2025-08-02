<template>
  <div class="app-container">
    <el-row :gutter="20">

      <!--类型数据-->
      <el-col :span="4" :xs="24">
        <div class="head-container">
          <el-input v-model="deptName" placeholder="请输入类型名称" clearable prefix-icon="Search"
            style="margin-bottom: 20px" />
        </div>
        <div class="head-container">
          <el-tree :data="deptOptions" :props="{ label: 'label', children: 'children' }" :expand-on-click-node="false"
            :filter-node-method="filterNode" ref="deptTreeRef" node-key="id" default-expand-all highlight-current
            @node-click="handleNodeClick" />
        </div>
      </el-col>

      <!--角色数据-->
      <el-col :span="20" :xs="20">
        <el-form :model="queryParams" ref="queryRef"  :inline="true" v-show="showSearch" label-width="68px">

          <div style="display: flex;align-items: flex-start;justify-content: space-between;width: 100%;">

            <div>
              <el-form-item label="角色名称" prop="roleName">
                <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable
                  style="width: 240px" @keyup.enter="handleQuery" />
              </el-form-item>
              <el-form-item label="角色描述" prop="responsibilities" label-width="100px">
                <el-input v-model="queryParams.responsibilities" placeholder="请输入角色描述" clearable
                  style="width: 240px" @keyup.enter="handleQuery" />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              </el-form-item>
            </div>

            <el-row :gutter="10" class="mb8">
              <el-col :span="1.5">
                <el-button type="primary" plain icon="Plus" @click="handleAdd">新建
                </el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button type="warning" plain icon="Plus" @click="handleAddFromTemplate">从模型创建
                </el-button>
              </el-col>
            </el-row>
          </div>
        </el-form>


        <div class="role-table-container" v-loading="loading">

          <el-empty description="你还没有智能体角色，请点击新增加你的智能体." v-if="RoleList.length == 0 && !loading" style="margin-top:50px">
            <el-button @click="handleAdd" icon="Plus" type="primary" size="large" text bg>新增加角色</el-button>
          </el-empty>

          <el-row>
            <el-col class="role-item-column" :span="8" v-for="(item, index) in RoleList" :key="index">
              <div class="role-item-panel" @click="handleClick(item)">
                <div class="role-info">
                  <div class="role-icon">
                    <img :src="imagePath(item.roleAvatar)" />
                  </div>
                  <div class="role-details">
                    <router-link :to="'/base/search/memoryData/index?roleId=' + item.id">
                      <span class="role-name">
                        {{ item.roleName }}
                        <span class="chat-count">会话: {{ item.chatCount }}</span>
                      </span>
                    </router-link>
                    <span class="role-responsibilities">{{ item.responsibilities }}</span>
                  </div>

                  <div style="position: absolute;top: 0px;z-index: 100;top: 10px;right: 10px;">
                    <el-tooltip content="发布渠道" placement="top">
                        <el-button 
                          link 
                          type="warning" 
                          @click="deployChannel(item)"
                          icon="Position" 
                          v-hasPermi="['system:Role:edit']"></el-button>
                    </el-tooltip>
                    <el-tooltip content="更新" placement="top">
                      <el-button 
                          link 
                          type="primary" 
                          icon="EditPen" 
                          @click="handleUpdate(item)"
                          v-hasPermi="['system:Role:edit']"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                      <el-button 
                          link 
                          type="info" 
                          icon="Delete" 
                          @click="handleDelete(item)"
                        v-hasPermi="['system:Role:remove']"></el-button>
                    </el-tooltip>
                  </div>
                </div>
                <div class="role-actions">
                  <div class="action-buttons">
                    <el-button type="success" v-if="item.scriptType === 'flow'" text size="small" @click="configExecuteScript(item)">
                      <i class="fa-solid fa-sailboat"></i>流程
                    </el-button>
                    <el-button type="danger" v-if="item.scriptType === 'react'" text size="small" @click="configExecuteScript(item)">
                      <i class="fas fa-file-signature"></i>推理
                    </el-button>
                    <el-button type="primary" v-if="item.scriptType === 'script'" text size="small" @click="configExecuteScript(item)">
                      <i class="fa-solid fa-code"></i> 脚本 
                    </el-button>
                    <!-- deepsearch-->
                     <el-button type="primary" v-if="item.scriptType === 'deepsearch'" text size="small" @click="configExecuteScript(item)">
                      <i class="fa-solid fa-magnifying-glass"></i> 深度搜索
                    </el-button>

                  </div>
                  <div class="switch-buttons">
                    <!-- 
                    <el-switch size="small" v-model="item.hasSale" :active-value="1" :inactive-value="0" active-text="分享" :disabled="item.saleFromRoleId" @change="handleChangeSaleField('hasSale', item.hasSale, item.id)"></el-switch>
                    <el-switch size="small" v-model="item.hasRecommended" :active-value="true" :inactive-value="false" active-text="推荐" @change="handleRecommended(item.id)"></el-switch> 
                    -->

                    <el-button type="primary" size="small" :disabled="item.saleFromRoleId" text bg @click="pushOrg(item)">
                      <i class="fa-solid fa-truck-fast"></i> 
                    </el-button>

                    <el-button type="primary" text bg size="small" @click="editWelcomePage(item)">
                      <i class="fa-solid fa-computer"></i> 
                    </el-button>

                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>

        <pagination style="margin-top:0px;" v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-col>
    </el-row>

    <!-- 添加或修改角色配置对话框 -->
    <el-dialog :title="title" v-model="open" style="width:80%;max-width:1400px" append-to-body>

    <el-row>
  <el-col :span="16">
      <el-form :model="form" :label-position="'right'" :rules="rules" size="large" ref="RoleRef" label-width="80px">
        <el-row>
          <el-col :span="24" class="editor-after-div">
            <el-form-item label="头像" prop="roleAvatar">

              <el-upload 
                :file-list="imageUrl" 
                :action="upload.url + '?type=img&updateSupport=' + upload.updateSupport"
                list-type="picture-card" 
                :auto-upload="true" 
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload" 
                :headers="upload.headers" 
                :disabled="upload.isUploading"
                :on-progress="handleFileUploadProgress">
                <el-icon class="avatar-uploader-icon">
                  <Plus />
                </el-icon>
                <template #tip>
                  <div class="el-upload__tip" style="text-align: left;"> 
                  </div>
                </template>
              </el-upload>

            </el-form-item>
          </el-col>
        </el-row>
<el-row>


  <el-col :span="24">
    <el-form-item label="执行类型" prop="scriptType">
      <el-radio-group v-model="form.scriptType" :disabled="form.id != null">
        <!-- 自定义每个radio的内容，包含图标、标题和描述 -->
        <el-radio 
          v-for="item in executeTypes" 
          :key="item.code" 
          :value="item.code"
          size="large" 
          border
          class="custom-radio"
        >
          <template #default>
            <div class="radio-content"> 
              <div class="radio-info">
                <div class="radio-title"><i :class="item.icon" class="radio-icon"></i> {{ item.title }}</div>
                <div class="radio-desc">{{ item.desc }}</div>
              </div>
            </div>
          </template>
        </el-radio>
      </el-radio-group>
    </el-form-item>
  </el-col>
</el-row> 
        
        <el-row>
        <!--
        <el-col :span="12">
          <el-form-item label="角色类型" prop="roleType">
            <el-radio-group v-model="form.roleType">
              <el-radio v-for="item in roleTypes" :key="item.key" :label="item.key" size="large" border>{{ item.name
                }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        -->
          <el-col :span="12">
            <el-form-item style="width: 100%;" label="业务分类" prop="industryCatalog">
              <el-tree-select v-model="form.industryCatalog" :data="deptOptions"
                :props="{ value: 'id', label: 'label', children: 'children' }" value-key="id" placeholder="请选择归属类型"
                check-strictly />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="23">
            <el-form-item label="名称" prop="roleName">
              <el-input v-model="form.roleName" placeholder="请输入角色名称" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="23">
            <el-form-item label="描述" prop="responsibilities">
              <el-input type="textarea" :rows="2" resize="none" v-model="form.responsibilities" placeholder="请输入角色描述"
                maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>

        

      </el-form>
      
        <div class="dialog-footer">
          <el-button type="primary" size="large" @click="submitForm">确 定</el-button>
        </div>
      
        </el-col>

<el-col :span="8" class="agent-siderbar">
  <div class="sidebar-content">
    <div class="selected-type-info" v-if="form.scriptType">
      <h3> <i :class="selectedTypeInfo.icon" class="type-icon"></i> {{ selectedTypeInfo.title }}</h3>
      <p class="type-description">{{ selectedTypeInfo.desc }}</p>
    </div>
    <div class="sidebar-image">
      <img :src="getAgentIconPath(selectedTypeInfo.code)"/>
    </div>
  </div>
</el-col>

</el-row>
    </el-dialog>

    <el-dialog v-model="pushOrgDialogFormVisible" title="请输入推送组织号" width="600px">
      <div
        style="padding: 10px 20px;display: flex;align-items: center; margin-bottom:20px; line-height: 1.1rem; gap: 10px;margin-top: 20px;">
        <div>
          <img :src="imagePath(pushRoleInfo.roleAvatar)" style="width:70px;height:70px;border-radius:50%;" />
        </div>
        <div style="display: flex;flex-direction: column;gap: 5px;">
          <span style="font-size:16px;font-weight: 500;">
            <b>角色</b>:{{ pushRoleInfo.roleName }}
          </span>
          <span style="font-size: 13px;color: #a5a5a5;">
            {{ pushRoleInfo.responsibilities }}
          </span>
        </div>
      </div>

      <el-form :model="form" size="large" label-width="100px" width="600" label-position="top">

        <el-form-item label="推送模式">
          <el-radio-group v-model="pushType">
            <el-radio
              v-for="option in pushOptions"
              :key="option.value"
              :value="option.value"
              border
              style="width: 100%; margin-bottom: 10px; margin-right: 0px;">
              <template #default>
                <div style="display: flex; gap: 10px;">
                  <span>{{ option.label }}</span>
                  <div style="color: #999; font-size: 12px;">{{ option.description }}</div>
                </div>
              </template>
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="推送组织ID">
          <el-input v-model="pushOrgId" autocomplete="off" placeholder="请输入推送组织号" style="margin-right:30px;">
            <template #append>
              <el-button @click="handleFindOrg" icon="Search" />
            </template>
          </el-input>
        </el-form-item>
      </el-form>

      <div style="padding:10px 20px;" v-if="pushOrgInfo.id">
        <div>
          <p><i class="fa-solid fa-masks-theater"></i> 组织ID: {{ pushOrgInfo.id }}</p>
          <p><i class="fa-brands fa-slack"></i> 组织名称: {{ pushOrgInfo.orgName }}</p>
          <p><i class="fa-solid fa-file-word"></i> 备注: {{ pushOrgInfo.remark }}</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer" style="margin-bottom: 20px;">
          <el-button size="large" :disabled="pushLoading" @click="pushOrgDialogFormVisible = false">取消</el-button>
          <el-button size="large" :loading="pushLoading" type="primary" :disabled="enablePushOrg == false" @click="handleConfirmPushOrg">
            确认推送
          </el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup name="Role">
import { getToken } from "@/utils/auth";
import { getAgentIconPath } from "@/utils/llmicons";
import { ElLoading } from 'element-plus'

import {
  listRole,
  delRole,
  findOrg,
  getRole,
  modifyInfo,
  updateRole,
  catalogTreeSelect,
  addRole,
  confirmPushOrg,
  changStatusField,
  changeSaleField,
  recommended
} from "@/api/smart/assistant/role";

import { ElMessage } from 'element-plus'
import { reactive } from "vue";

const router = useRouter();
const { proxy } = getCurrentInstance();

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

const pushOrgDialogFormVisible = ref(false)

const pushLoading = ref(false)
const pushOrgId = ref(null)
const pushType = ref('shop')
const pushRoleInfo = ref({})
const enablePushOrg = ref(false)
const pushOrgInfo = ref({
  doorplateNumber: "",
  id: "",
  orgName: "",
  remark: ""
})

const pushOptions = ref([
  { value: 'shop', label: '推送商店', description: '角色只可被组织引用不可修改' },
  { value: 'market', label: '推送市场', description: '角色能复制修改并单独定义' }
])

const chainOpen = ref(false);
const promptOpen = ref(false);

const deptName = ref("");
const deptOptions = ref(undefined);

/*** 角色导入参数 */
const upload = reactive({
  // 是否显示弹出层（角色导入）
  open: false,
  // 弹出层标题（角色导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的角色数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/importData",
  // 显示地址
  display: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/"
});
// 列显隐信息
const columns = ref([
  { key: 0, label: `图标`, visible: true },
  { key: 1, label: `角色名称`, visible: true },
  { key: 2, label: `角色描述`, visible: true },
  { key: 3, label: `所属领域`, visible: true },
  { key: 4, label: `角色级别`, visible: true },
  { key: 5, label: `安全存储路径`, visible: true },
  { key: 6, label: `角色目标`, visible: true },
  { key: 7, label: `创建时间`, visible: true },
  { key: 8, label: `编辑`, visible: true },

]);

const executeTypes = ref([
  {
    icon: 'fa-solid fa-robot',
    code: 'react',
    title: 'Agent推理角色',
    desc: '通过界面填写表单形式创建的AI角色，有知识库和工具，适合新人角色及简单RAG场景使用'
  },
  {
    icon: 'fa-solid fa-code',
    code: 'script',
    title: '自定义脚本角色',
    desc: '通过代码编辑方式实现角色复杂逻辑，结合内容接口及复杂业务场景，建议高级人员使用'
  },
  {
    icon: 'fa-solid fa-stream',
    code: 'flow',
    title: '工作流角色',
    desc: '通过低代码流程化实现复杂逻辑，针对于较为通用和复杂场景，建议高级人员使用'
  },
  {
    icon: 'fa-solid fa-search', // 这里假设的图标，你可以根据实际需求修改
    code: 'deepsearch',
    title: 'DeepSearch角色',
    desc: '用于深度搜索的角色，能够处理复杂的搜索任务，适合专业搜索场景使用'
  }
]);

const roleTypes = ref([
  { key: 'single_role', name: '单角色', description: '自己单独完成一个聊天，可流式输出或者同步输出' },
  { key: 'collaborative_role', name: '协作角色', description: '与其它角色协作才可以完成一个工作，可流式输出或者同步输出' },
  { key: 'scenario_role', name: '场景角色', description: '与其它角色协作才可以完成一个工作，只能同步输出' },
]);

const data = reactive({
  form: {
    roleAvatar: null,
    scriptType: 'script',
    roleType: 'single_role',
  },
  queryParams: {
    pageNum: 1,
    pageSize: 12,
    roleName: undefined,
    roleName: undefined,
    responsibilities: undefined,
    status: undefined,
    industryCatalog: undefined
  },
  rules: {
    roleId: [{ required: true, message: "角色编号不能为空", trigger: "blur" }],
    // roleType: [{ required: true, message: "角色类型不能为空", trigger: "blur" }],
    scriptType: [{ required: true, message: "脚本类型不能为空", trigger: "blur" }],
    roleName: [{ required: true, message: "角色名称不能为空", trigger: "blur" }, {
      min: 2,
      max: 20,
      message: "角色名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    responsibilities: [{ required: true, message: "角色描述不能为空", trigger: "blur" }],
    roleAvatar: [{ required: true, message: "头像不能为空", trigger: "blur" }],
    roleLevel: [{ required: true, message: "角色级别不能为空", trigger: "blur" }],
    storagePath: [{ required: true, message: "安全存储路径不能为空", trigger: "blur" }],
    industryCatalog: [{ required: true, message: "角色类型不能为空", trigger: "blur" }],
  },
  chainForm: {
    roleId: undefined,
  },
  chainRules: {
    chainName: [{ required: true, message: "链路名称不能为空", trigger: "blur" }],
    elData: [{ required: true, message: "链路流程不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules, chainForm, chainRules } = toRefs(data);

/** 查询角色列表 */
function getList() {
  loading.value = true;
  listRole(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    RoleList.value = res.rows;
    total.value = res.total;
  });
};

/** 查询组织信息 */
function handleFindOrg() {
  if (!pushOrgId.value) {
    proxy.$modal.msgError("请输入组织号");
    return;
  }
  findOrg(pushOrgId.value).then(res => {
    console.log('res = ' + res);
    if (data) {
      pushOrgInfo.value = res.data;
      enablePushOrg.value = true;
    } else {
      proxy.$modal.msgError("查询组织信息失败，请确认组织号是否正确.");
    }
  });
}

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
  imageUrl.value = response.data ? response.data.split(',').map(url => { return { url: upload.display + url } }) : [];
  form.value.roleAvatar = response.data;
  // console.log('form.roleAvatar = ' + form.roleAvatar);
};

/** 图片上传之前 */
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!');
    return false;
  }
  return true;
};

// 替换原来的三个方法，只需一个计算属性
const selectedTypeInfo = computed(() => {
  return executeTypes.value.find(type => type.code === form.value.scriptType) || {
    title: '请选择执行类型',
    desc: '智能体类型',
    icon: 'fa-solid fa-question'
  }
})

/** 推荐组织Hero角色 */
function handleRecommended(roleId) {
  recommended(roleId).then(res => {
    proxy.$modal.msgSuccess("推荐成功");
    getList();
  })
}

/** 发布渠道 */
function deployChannel(item){
  router.push({path: '/expert/smart/assistant/role/publishChannel', query: { roleId: item.id }})
}

/** 配置欢迎页 */
function editWelcomePage(item){
  router.push({path: '/expert/smart/assistant/role/welcomePage', query: { roleId: item.id }})
}

/** 点击配置 */
function handleClick(item){

}

/** 推送到指定的组织 */
function pushOrg(row) {
  pushOrgDialogFormVisible.value = true;
  pushRoleInfo.value = row;
  pushOrgInfo.value = { id: null }
}

/** 确认推送到指定的组织 */
function handleConfirmPushOrg() {
  // const loading = ElLoading.service({
  //   lock: true,
  //   text: 'Loading',
  //   background: 'rgba(0, 0, 0, 0.7)',
  // })

  pushLoading.value = true;

  let data = {
    pushType: pushType.value ,
    roleId: pushRoleInfo.value.id,
    orgId: pushOrgId.value,
  }

  confirmPushOrg(data).then(res => {
    console.log('res = ' + res);
    // loading.close();
    pushLoading.value = false;
    proxy.$modal.msgSuccess("推送成功");
  }).catch(() => {
    // loading.close();
    pushLoading.value = false;
  })
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

  proxy.$modal.confirm('是否确认删除角色编号为"' + roleIds + '"的数据项？').then(function () {
    return delRole(roleIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
};

/** 配置角色脚本 */
function configExecuteScript(row) {

  console.log('scriptType = ' + row.scriptType)

  if (row.scriptType && row.scriptType == 'flow') {  // 流程
    router.push({ path: '/expert/smart/assistant/role/workflowAgent', query: { roleId: row.id } });
  } else if (row.scriptType && row.scriptType == 'react') {  // 推荐
    router.push({ path: '/expert/smart/assistant/role/agentInference', query: { roleId: row.id } });
  // 深度搜索
  } else if (row.scriptType && row.scriptType == 'deepsearch') {  // 深度搜索
    router.push({ path: '/expert/smart/assistant/role/deepSearch', query: { roleId: row.id } });
  } else {  // 脚本
    router.push({ path: '/expert/smart/assistant/role/constomScript', query: { roleId: row.id } });
  }

}

/** 选择条数  */
// function handleSelectionChange(selection) {
//   ids.value = selection.map(item => item.id);
//   single.value = selection.length != 1;
//   multiple.value = !selection.length;
// };

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

/** 角色类型 */
// function handleRoleTypeClick(roleType) {
//   const roleCode = roleType.code

//   console.log('roleCode = ' + roleCode)

//   if (roleCode === 'agent-inference') {
//     router.push({ path: '/expert/smart/assistant/role/agentInference' });
//   } else if (roleCode === 'workflow') {
//     router.push({ path: '/expert/smart/assistant/role/workflowAgent' });
//   } else if (roleCode === 'custom-script') {
//     router.push({ path: '/expert/smart/assistant/role/constomScript' });
//   }

// }

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加角色";
  imageUrl.value = []; // 清空数组
};

function handleAddFromTemplate() {
  router.push({ path: '/dashboard/smart/assistant/roleTemplate/index' });
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const roleId = row.id || ids.value;
  getRole(roleId).then(response => {
    form.value = response.data;
    form.value.roleId = roleId;
    open.value = true;
    title.value = "修改角色";

    const item = {
      url: upload.display + response.data.roleAvatar,
      uid: roleId // 可以在这里初始化属性
    }
    imageUrl.value = []; // 清空数组
    imageUrl.value.push(item) ; 

  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["RoleRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        form.value.roleType = 'single_role' ; 
        modifyInfo(form.value).then(response => {
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
    width: 55px;
    height: 55px;
    border-radius: 8px;
  }
}

.editor-after-div {
  .el-upload {
    width: 56px;
    height: 56px;
    text-align: center;
    line-height: 56px;
  }

  .el-upload-list__item-thumbnail {
    width: 56px;
    height: 56px;
  }

  .el-upload-list__item {
    width: 56px;
    height: 56px;
  }
}

$radio-primary-color: #409eff; // 主色调变量
$radio-margin-bottom: 12px;
$radio-padding: 5px;
$title-font-size: 14px;
$desc-font-size: 12px;
$desc-color: #606266;

.custom-radio {
  display: block;
  margin-bottom: $radio-margin-bottom;
  padding: $radio-padding !important;
  height: auto;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  margin-right: 10px !important;
  &:hover {
    background-color: rgba($radio-primary-color, 0.05);
  }
  
  .radio-content {
    display: flex; 
    align-items: center;
    width:230px;
    line-height: 1.5rem;
    
    .radio-icon {
      font-size: 20px; 
      color: $radio-primary-color;
      vertical-align: middle;
      flex-shrink: 0; 
    }
    
    .radio-info {
      flex-grow: 1;
      min-width: 0;
      
      .radio-title {
        font-weight: 500;
        margin-bottom: 4px;
        font-size: $title-font-size;
        display: flex;
        align-items: center;
        gap: 10px;
      }
      
      .radio-desc {
        color: $desc-color;
        font-size: $desc-font-size;
        line-height: 1.5;
        max-width: 800px;
        white-space: normal;
        
        // 关键样式：限制最多2行并显示省略号
        display: -webkit-box;
        -webkit-line-clamp: 2; // 最多显示2行
        -webkit-box-orient: vertical;
        overflow: hidden; // 超出部分隐藏
        text-overflow: ellipsis; // 显示省略号
      }
    }
  }
  
  &.is-checked {
    .radio-title {
      color: $radio-primary-color;
      font-weight: 600;
    }
  }

}

.agent-siderbar {
  border-radius: 8px;
  background-color: #fafafa; 
  height: 100%;
  margin-top:20px;
  display: flex;
  flex-direction: column;
  padding-bottom: 30px;
  
  .sidebar-content {
    display: flex;
    flex-direction: column;
    height: 100%;
    
    .selected-type-info { 
      padding:10px 20px;
      font-size:14px;
      
      h3 {
        color: #333;
        font-size: 15px;
        margin-bottom: 10px;
      }
      
      .type-description {
        color: #666;
        font-size: 14px;
        line-height: 1.5;
      }
      
      .type-icon { 
        font-size: 20px;
        color: var(--el-color-primary);
      }
    }
    
    .sidebar-image {
      margin-top: auto;
      
      img {
        width: 100%;
        height: auto;
        border-radius: 8px;
      }
    }
  }
}


  .dialog-footer {
    text-align: right;
    margin: 20px;
}

</style>