<template>
  <div class="app-container">
    <el-row :gutter="20">
       <!--应用数据-->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">

          <el-form-item label="场景名称" prop="name">
            <el-input v-model="queryParams.screenName" placeholder="请输入场景名称" clearable
                      style="width: 240px" @keyup.enter="handleQuery"/>
          </el-form-item>
          <!-- 
          <el-form-item label="场景类型" prop="toolType" label-width="100px">
            <el-input v-model="queryParams['condition[toolType|like]']" placeholder="请输入场景类型" clearable
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
                v-hasPermi="['system:Screen:edit']"
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
                v-hasPermi="['system:Screen:remove']"
            >删除
            </el-button>
          </el-col>

          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="ScreenList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column label="图标" align="center" width="100" prop="screenBanner" v-if="columns[0].visible">
            <template #default="scope">
              <div class="screen-icon">
                <img :src="imagePath(scope.row.screenBanner)" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="场景名称" align="left" width="250" key="name" prop="name" v-if="columns[1].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div style="font-size: 15px;font-weight: 500;color: #3b5998;">
               {{ scope.row.screenName }}
              </div>
              <div style="font-size: 13px;color: #a5a5a5;">
                Key: {{ scope.row.id }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="场景描述" align="left" key="description" prop="description" v-if="columns[5].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                {{ scope.row.screenDesc }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="场景类型" align="center" key="screenType" width="180" prop="screenType" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button v-if="scope.row.screenType == 1" text bg type="primary"><i class="fa-solid fa-user-shield"></i>&nbsp;个人场景</el-button>
              <el-button v-if="scope.row.screenType == 2" text bg type="danger"><i class="fa-solid fa-user-ninja"></i>&nbsp;私人场景</el-button>
              <el-button v-if="scope.row.screenType == 3" text bg type="success"><i class="fa-solid fa-user-ninja"></i>&nbsp;推荐场景</el-button>
              <el-button v-if="scope.row.screenType == 9" text bg type="primary"><i class="fa-solid fa-users-gear"></i>&nbsp;公共场景</el-button>
            </template>
          </el-table-column>

          <!--
          <el-table-column label="成员" align="center" key="screenType" width="180" prop="screenType" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button text bg type="primary" @click="configAgent(scope.row)">
                <i class="fa-solid fa-user-shield"></i>&nbsp;成员管理({{ scope.row.roles.length }})
              </el-button>
            </template>
          </el-table-column>
          -->

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
          <el-table-column label="更新时间" align="center" prop="addTime" v-if="columns[7].visible" width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.addTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="100" class-name="small-padding fixed-width" v-if="columns[8].visible">
            <template #default="scope">
              <el-tooltip content="修改" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                           v-hasPermi="['system:Screen:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                           v-hasPermi="['system:Screen:remove']"></el-button>
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
      <el-form :model="form" :rules="rules" ref="ScreenRef" label-width="80px">
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
                    <!-- <img v-if="form.roleAvatar" style="width:100%;height:100%" :src="imagePath(form.roleAvatar)" /> -->
                    <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                  </el-upload>
                </el-form-item>
            </el-col>
          </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="场景类型" prop="screenType">
              <!-- <el-input v-model="form.screenType" placeholder="请输入机器人Key" maxlength="50"/> -->
              <el-radio-group v-model="form.screenType">
                <el-radio v-for="item in screenTypesArr" 
                  :key="item.id" 
                  :value="item.id"
                  :label="item.id" size="large">{{ item.name }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item  label="场景名称" prop="screenName">
              <el-input v-model="form.screenName" placeholder="请输入场景名称" maxlength="30"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="场景描述" prop="screenDesc">
              <el-input v-model="form.screenDesc" placeholder="请输入场景类型" maxlength="350"/>
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

    <el-dialog v-model="configAgentDialogVisible" :title="screenAgentConfigTitle" width="50%">
        <div style="text-align: center">
          <el-transfer 
              v-model="screenAgentList" 
              filterable
              style="text-align: left; width:100%; display: inline-block"
              :titles="['源角色', '已选择']"
              :format="{
                noChecked: '${total}',
                hasChecked: '${checked}/${total}',
              }"
              :filter-method="filterAgentMethod"
              filter-placeholder="搜索角色" 
            :data="agentList" />
        </div>

        <template #footer>
          <div class="dialog-footer">
            <el-button @click="configAgentDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="handleCloseAgentConfig">确认</el-button>
          </div>
        </template>
    </el-dialog>

  </div>
</template>

<script setup name="Screen">
import {getToken} from "@/utils/auth";
import {
  listScreen,
  delScreen,
  getScreen,
  updateScreen,
  addScreen,
  updateScreenAgent
} from "@/api/smart/assistant/screen";

import {
  listAllRole
} from '@/api/smart/assistant/role';

import {reactive} from "vue";

const router = useRouter();
const {proxy} = getCurrentInstance();
// const { sys_normal_disable, sys_Screen_sex } = proxy.useDict("sys_normal_disable", "sys_Screen_sex");

const ScreenList = ref([]);
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

// const deptName = ref("");
// const deptOptions = ref(undefined);
// const initPassword = ref(undefined);
// const postOptions = ref([]);
// const roleOptions = ref([]);

const screenTypesArr = [
  { "id": "long_text", "name": "大文本" },  // 对外公开场景
  { "id": "leader_model", "name": "管理者" },  // 个人公共场景
];

const agentList = ref([])
const configAgentDialogVisible = ref(false)
const screenAgentConfigTitle = ref("")
const screenAgentList = ref([])
const currentScreenId = ref(0) ;

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
  {key: 1, label: `场景名称`, visible: true},
  {key: 2, label: `场景类型`, visible: true},
  {key: 3, label: `使用场景`, visible: true},
  {key: 4, label: `状态`, visible: true},
  {key: 5, label: `场景描述`, visible: true},
  {key: 6, label: `应用目标`, visible: true},
  {key: 7, label: `创建时间`, visible: true},
  {key: 8, label: `编辑`, visible: true},

]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    ScreenName: undefined,
    name: undefined,
    toolType: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    screenName: [{required: true, message: "场景名称不能为空", trigger: "blur"}, {
      min: 2,
      max: 20,
      message: "场景名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    screenDesc: [{required: true, message: "场景类型不能为空", trigger: "blur"}],
    screen: [{required: true, message: "使用场景不能为空", trigger: "blur"}],
    hasStatus: [{required: true, message: "状态不能为空", trigger: "blur"}],
    description: [{required: true, message: "场景描述不能为空", trigger: "blur"}],
    target: [{required: true, message: "应用目标不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules} = toRefs(data);


/** 查询应用列表 */
function getList() {
  loading.value = true;
  listScreen(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    ScreenList.value = res.rows;
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
  imageUrl.value = URL.createObjectURL(uploadFile.raw);
  form.value.screenBanner = response.data ;
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
  queryParams.value.toolType = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
  const applicationIds = row.id || ids.value;

  proxy.$modal.confirm('是否确认删除应用编号为"' + applicationIds + '"的数据项？').then(function () {
    return delScreen(applicationIds);
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
    screen: undefined,
    hasStatus: undefined,
    description: undefined,
    target: undefined,
  };
  proxy.resetForm("ScreenRef");
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
  title.value = "添加应用";
};

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getScreen(id).then(response => {
    form.value = response.data;
    form.value.id = id;
    open.value = true;
    title.value = "修改应用";

    const item = {
      url: upload.display + response.data.screenBanner ,
      uid: id // 可以在这里初始化属性
    }
    imageUrl.value = []; // 清空数组
    imageUrl.value.push(item) ; 

  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ScreenRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateScreen(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addScreen(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
};

/** 配置成员 */
function configAgent(row){

    configAgentDialogVisible.value = true ;
    screenAgentConfigTitle.value = row.screenName + "成员" ; 
    currentScreenId.value = row.id ;
    screenAgentList.value = row.roles ;
}

/** 获取到所有的角色信息 */
function handleListAllRole(){

  listAllRole().then(res => {

    for (let i = 0; i < res.data.length ; i++) {
        let item = res.data[i]

        agentList.value.push({
          key: item.id ,
          label: item.roleName , 
          disabled: false ,
      })
    }
  })
}

/** 关闭弹窗 */
function handleCloseAgentConfig(){
  configAgentDialogVisible.value = false ;

  updateScreenAgent(currentScreenId.value , screenAgentList.value).then(res => {
    proxy.$modal.msgSuccess("更新成功");
    getList() ;
  })
  
}

/** 搜索过滤方法 */
const filterAgentMethod = (query, item) => {
  // return item.initial.includes(query)
  return item ;
}

getList();
handleListAllRole();

</script>

<style lang="scss" scoped>
.screen-icon{
  img {
    width:auto;
    height:45px;
    border-radius: 4px;
  }
}
</style>