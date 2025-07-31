<template>
  <div class="app-container">
    <el-row :gutter="20">
       <!--应用数据-->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">

          <el-form-item label="工作台名称" prop="name">
            <el-input v-model="queryParams.name" placeholder="请输入工作台名称" clearable
                      style="width: 240px" @keyup.enter="handleQuery"/>
          </el-form-item>

          <!-- 
          <el-form-item label="数据范围" prop="sceneScope" label-width="100px">
            <el-radio-group v-model="queryParams.workplaceType" label="数据范围" label-width="100px" @change="handleQuery">
              <el-radio v-for="item in workplaceTypesArr" 
                :key="item.id" 
                :label="item.id">
                {{ item.name }}
              </el-radio>
            </el-radio-group>
          </el-form-item>  
          -->
          
          <!-- 
          <el-form-item label="工作台类型" prop="toolType" label-width="100px">
            <el-input v-model="queryParams.toolType" placeholder="请输入工作台类型" clearable
                      style="width: 240px" @keyup.enter="handleQuery"/>
          </el-form-item> 
          -->

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
                v-hasPermi="['system:Workplace:edit']"
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
                v-hasPermi="['system:Workplace:remove']"
            >删除
            </el-button>
          </el-col>

          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="WorkplaceList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <!-- <el-table-column label="图标" align="center" width="130px" prop="icon" v-if="columns[0].visible">
            <template #default="scope">
              <div v-if="scope.row.backgroundImage != null" class="role-icon">
                <img :src="imagePath(scope.row.backgroundImage)" />
              </div>
            </template>
          </el-table-column>  -->
          <el-table-column label="工作台名称" align="left" key="name" prop="name" v-if="columns[1].visible">
            <template #default="scope">
                <div style="font-size: 15px;font-weight: 500;color: #1d75b0;">
                {{ scope.row.name }}
                </div>
                <div style="font-size: 13px;color: #a5a5a5;">
                  {{ scope.row.description }}
                </div>
            </template>
          </el-table-column>
          <!-- 
          <el-table-column label="工作台描述" align="left" key="description" prop="description" v-if="columns[5].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                {{ scope.row.description }}
              </div>
            </template>
          </el-table-column> 
          -->
          <!-- <el-table-column label="数据范围" align="center" key="workplaceType" width="180" prop="workplaceType" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
                <i :class="getWorkplaceNameByType(scope.row.workplaceType)?.icon" ></i>
                {{ getWorkplaceNameByType(scope.row.workplaceType)?.name }}
            </template>
          </el-table-column>  -->
          <el-table-column label="智能体" align="center" key="workplaceType" width="180" prop="workplaceType" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button text bg type="primary" @click="configAgent(scope.row)">
                <i class="fa-solid fa-user-shield"></i>&nbsp;智能体配置({{ scope.row.agentsList == null ? 0 : scope.row.agentsList.length}})
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="频道库" align="center" key="workplaceType" width="180" prop="workplaceType" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button text bg type="primary" @click="configChannel(scope.row)">
                <i class="fa-solid fa-user-shield"></i>&nbsp;频道配置({{ scope.row.channelsList == null? 0 : scope.row.channelsList.length }})
              </el-button>
            </template>
          </el-table-column>
          <el-table-column label="应用场景" align="center" key="workplaceType" width="180" prop="workplaceType" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button text bg type="primary" @click="configScene(scope.row)">
                <i class="fa-solid fa-user-shield"></i>&nbsp;场景配置({{ scope.row.scenesList == null? 0 : scope.row.scenesList.length }})
              </el-button>
            </template>
          </el-table-column>
          <!-- 
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
          -->
          <el-table-column label="更新时间" align="center" prop="addTime" v-if="columns[7].visible" width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.addTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="100" class-name="small-padding fixed-width" v-if="columns[8].visible">
            <template #default="scope">
              <el-tooltip content="修改" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                           v-hasPermi="['system:Workplace:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                           v-hasPermi="['system:Workplace:remove']"></el-button>
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
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <el-form :model="form" :rules="rules" ref="WorkplaceRef" size="large" label-width="80px">
          <el-row>
            <el-col :span="24" class="editor-after-div">
              <el-form-item
                  label="头像"
                  props="roleAvatar"
                  :rules="[{ required: true, message: '请上传头像', trigger: 'blur',},]">
                  <el-upload
                    :file-list="imageUrl"
                    :action="upload.url + '?type=img&updateSupport=' + upload.updateSupport"
                    list-type="picture-card"
                    :auto-upload="true"
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
            <el-form-item label="类型" prop="workplaceType">
              <el-radio-group v-model="form.workplaceType" :disabled="form.id">
                <el-radio v-for="item in workplaceTypesArr" 
                  style="margin-top: 10px;margin-bottom: 10px;" 
                  :key="item.id" 
                  :value="item.id"
                  :label="item.id" size="large">
                  <div style="padding:10px; display: flex;flex-direction: column;line-height: 1.5rem;">
                    <span style="font-size:15px;font-weight: bold;">
                      <i :class="item.icon"></i> {{ item.name }}
                    </span>
                    <span style="color:#a5a5a5">
                      {{ item.desc }}
                    </span>
                  </div>
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row> 
        <el-row>
          <el-col :span="24">
            <el-form-item  label="名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入工作台名称" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input type="textarea" :rows="3" resize="none" v-model="form.description" placeholder="请输入工作台类型" />
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

    
    <!-- 配置智能体对话框 -->
    <ConfigAgentPanel ref="configAgentPanelRef" @getList="getList" />

    <!-- 工作台频道库配置对话框 -->
    <ConfigChannelPanel ref="configChannelPanelRef" @getList="getList" />

    <!-- 工作台场景配置对话框架 -->
    <ConfigScenePanel ref="configScenePanelRef" @getList="getList" />

  </div>
</template>

<script setup name="Workplace">

import ConfigAgentPanel from './configAgentPanel.vue'
import ConfigChannelPanel from './configChannelPanel.vue'
import ConfigScenePanel from './configScenePanel.vue'

import {getToken} from "@/utils/auth";
import {
  listWorkplace,
  delWorkplace,
  getWorkplace,
  updateWorkplace,
  addWorkplace,
  // updateWorkplaceAgent
} from "@/api/smart/assistant/workplace";

// import {
//   listAllRole
// } from '@/api/smart/assistant/role';

import {reactive} from "vue";

const router = useRouter();
const {proxy} = getCurrentInstance();
// const { sys_normal_disable, sys_Workplace_sex } = proxy.useDict("sys_normal_disable", "sys_Workplace_sex");

const WorkplaceList = ref([]);
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

const configAgentPanelRef = ref(null);
const configChannelPanelRef = ref(null);
const configScenePanelRef = ref(null);

// const deptName = ref("");
// const deptOptions = ref(undefined);
// const initPassword = ref(undefined);
// const postOptions = ref([]);
// const roleOptions = ref([]);

// const workplaceTypesArr = [
//   { "id": "public", "name": "公开工作台" },  // 公开工作台
//   { "id": "org", "name": "组织工作台" },  // 公共工作台
// ];

const workplaceTypesArr = [
    { "id": "public", "name": "公开工作台", "icon": "fa-solid fa-globe" , "desc":"公开工作台只能选择公共智能体、公开频道、公开场景" },
    { "id": "org", "name": "组织工作台", "icon": "fa-solid fa-truck-plane" , "desc":"组织工作台可以选择公共和组织内的智能体、频道和场景" }
];

// const agentList = ref([])
// const configAgentDialogVisible = ref(false)
// const workplaceAgentConfigTitle = ref("")
// const workplaceAgentList = ref([])
const currentWorkplaceId = ref(0) ;

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
  {key: 1, label: `工作台名称`, visible: true},
  {key: 2, label: `工作台类型`, visible: true},
  {key: 3, label: `使用场景`, visible: true},
  {key: 4, label: `状态`, visible: true},
  {key: 5, label: `工作台描述`, visible: true},
  {key: 6, label: `应用目标`, visible: true},
  {key: 7, label: `创建时间`, visible: true},
  {key: 8, label: `编辑`, visible: true},

]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    WorkplaceName: undefined,
    name: undefined,
    toolType: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    roleAvatar: [{required: true, message: "头像不能为空", trigger: "blur"}],
    name: [{required: true, message: "工作台名称不能为空", trigger: "blur"}, {
      min: 2,
      max: 20,
      message: "工作台名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    description: [{required: true, message: "工作台类型不能为空", trigger: "blur"}],
    scene: [{required: true, message: "使用场景不能为空", trigger: "blur"}],
    workplaceType: [{required: true, message: "工作台类型不能为空", trigger: "blur"}],
    hasStatus: [{required: true, message: "状态不能为空", trigger: "blur"}],
    description: [{required: true, message: "工作台描述不能为空", trigger: "blur"}],
    target: [{required: true, message: "应用目标不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules} = toRefs(data);

const getWorkplaceNameByType = (type) => {
  for (let i = 0; i < workplaceTypesArr.length; i++) {
    if (workplaceTypesArr[i].id == type) {
      return workplaceTypesArr[i];
    }
  }
  return null ;
};

/** 查询应用列表 */
function getList() {
  loading.value = true;
  listWorkplace(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    WorkplaceList.value = res.rows;
    total.value = res.total;
  });
};


/** 搜索按钮操作 */
function handleQuery() {
  console.log(queryParams);
  queryParams.value.pageNum = 1;
  getList();
};

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
  // imageUrl.value = URL.createObjectURL(uploadFile.raw);
  imageUrl.value = response.data ? response.data.split(',').map(url => { return { url: upload.display + url } }) : [];
  form.value.backgroundImage = response.data ;
};

/** 图片上传之前 */
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!');
    return false;
  }
  return true;
};

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  queryParams.value.name = undefined;
  queryParams.value.workplaceType = undefined;
  // proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
  const applicationIds = row.id || ids.value;

  proxy.$modal.confirm('是否确认删除应用编号为"' + applicationIds + '"的数据项？').then(function () {
    return delWorkplace(applicationIds);
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
    id: undefined,
    name: undefined,
    toolType: undefined,
    scene: undefined,
    hasStatus: undefined,
    description: undefined,
    target: undefined,
  };
  proxy.resetForm("WorkplaceRef");
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
  title.value = "添加业务工作台";
  imageUrl.value = []; // 清空数组
};

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getWorkplace(id).then(response => {
    form.value = response.data;
    form.value.id = id;
    open.value = true;
    title.value = "修改应用";

    const item = {
      url: upload.display + response.data.backgroundImage,
      uid: id // 可以在这里初始化属性
    }
    imageUrl.value = []; // 清空数组
    imageUrl.value.push(item) ; 

  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["WorkplaceRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateWorkplace(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addWorkplace(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
};

/** 配置成员 */
const configAgent = async(row) =>{
  console.log('configAgent = ' + configAgentPanelRef.value)
  configAgentPanelRef.value.configAgent(row)
}

/** 配置频道 */
const configChannel = async(row) =>{
  console.log('configChannel = ' + configChannelPanelRef.value)
  configChannelPanelRef.value.configChannel(row)
}

/** 配置场景 */
const configScene = async(row) =>{
  console.log('configScene = ' + configScenePanelRef.value)
  configScenePanelRef.value.configScene(row)
}

getList();
// handleListAllRole();

</script>

<style lang="scss" scoped>
.role-icon {
  img {
    width:100px;
    border-radius: 5px;
  }
}
</style>