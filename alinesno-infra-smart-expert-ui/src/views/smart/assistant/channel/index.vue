<template>
  <div class="app-container">
    <el-row :gutter="20">
       <!--应用数据-->
      <el-col :span="24" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">

          <el-form-item label="频道名称" prop="name">
            <el-input v-model="queryParams.channelName" placeholder="请输入频道名称" clearable
                      style="width: 240px" @keyup.enter="handleQuery"/>
          </el-form-item>

          <el-form-item label="数据范围" prop="sceneScope" label-width="100px">
            <el-radio-group v-model="queryParams.channelType" label="数据范围" label-width="100px" @change="handleQuery">
              <el-radio v-for="item in channelTypesArr" 
                :key="item.value" 
                :label="item.value">

                {{ item.label }}

              </el-radio>
            </el-radio-group>
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
                v-hasPermi="['system:Channel:edit']"
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
                v-hasPermi="['system:Channel:remove']"
            >删除
            </el-button>
          </el-col>

          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="ChannelList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column label="图标" align="center" width="60px" prop="icon" v-if="columns[0].visible">
            <template #default="scope">
              <div class="role-icon">
                <img :src="imagePath(scope.row.icon)" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="频道名称" align="left" key="name" prop="name" v-if="columns[1].visible" >
            <template #default="scope">
              <div style="font-size: 15px;font-weight: 500;color: #1d75b0;">
               {{ scope.row.channelName }}
              </div>
              <div style="font-size: 13px;color: #a5a5a5;">
                {{ truncateString(scope.row.channelDesc , 50) }}
              </div>
            </template>
          </el-table-column>
          <!-- 
          <el-table-column label="频道描述" align="left" key="description" prop="description" v-if="columns[5].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <div>
                {{ scope.row.channelDesc }}
              </div>
            </template>
          </el-table-column> 
          -->
          <el-table-column label="数据范围" align="center" key="channelType" width="180" prop="channelType" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <i :class="getChannelTypeByType(scope.row.channelType)?.icon"></i>
              {{  getChannelTypeByType(scope.row.channelType)?.label }}
            </template>
          </el-table-column>
          <el-table-column label="成员" align="center" key="channelType" width="180" prop="channelType" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button text bg type="primary" @click="configAgent(scope.row)">
                <i class="fa-solid fa-user-shield"></i>&nbsp;成员管理({{ scope.row.roles.length }})
              </el-button>
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
          <el-table-column label="更新时间" align="center" prop="addTime" v-if="columns[7].visible" width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.addTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="100" class-name="small-padding fixed-width" v-if="columns[8].visible">
            <template #default="scope">
              <el-tooltip content="修改" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                           v-hasPermi="['system:Channel:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                           v-hasPermi="['system:Channel:remove']"></el-button>
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
      <el-form :model="form" :rules="rules" size="large" ref="ChannelRef" label-width="80px">
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
            <el-form-item label="频道类型" prop="channelType">

              <el-alert title="频道类型提交确认后不可修改" type="warning" :closable="false" show-icon style="margin-bottom: 10px;" />

              <el-radio-group v-model="form.channelType" :disabled="form.id">
                <el-radio v-for="item in channelTypesArr" 
                  style="margin-top: 10px;margin-bottom: 10px;" 
                  :key="item.value" 
                  :value="item.value"
                  :label="item.value" 
                  size="large">

                  <div style="padding:10px; display: flex;flex-direction: column;line-height: 1.5rem;">
                    <span style="font-size:15px;font-weight: bold;">
                      <i :class="item.icon"></i> {{ item.label }}
                    </span>
                    <span style="color:#a5a5a5">
                      {{  item.desc }}
                    </span>
                  </div>

                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item  label="频道名称" prop="channelName">
              <el-input v-model="form.channelName" placeholder="请输入频道名称" maxlength="30"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="频道描述" prop="channelDesc">
              <el-input v-model="form.channelDesc" placeholder="请输入频道类型" maxlength="350"/>
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
    <!-- 
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
    -->

    <el-dialog v-model="configAgentDialogVisible" :title="channelAgentConfigTitle" width="50%">
        <div style="text-align: center">
          <el-transfer 
              v-model="channelAgentList" 
              filterable
              style="text-align: left; width:100%; display: inline-block"
              :titles="['源角色', '已选择']"
              :format="{
                noChecked: '${total}',
                hasChecked: '${checked}/${total}',
              }"
              :filter-method="filterAgentMethod"
              filter-placeholder="搜索角色" 
            :data="agentList" >
                <!-- 自定义源列表项 -->
                <template #default="{ option }">
                    <div class="aip-el-transfer-panel__item">
                        <img :src="imagePath(option.avatar)" /> {{ option.label }}
                    </div>
                </template>
          </el-transfer>
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

<script setup name="Channel">
import {getToken} from "@/utils/auth";
import {
  listChannel,
  delChannel,
  getChannel,
  listAllChannelType , 
  updateChannel,
  addChannel,
  updateChannelAgent
} from "@/api/smart/assistant/channel";

import {
  listAllRole , 
  listPublicRole
} from '@/api/smart/assistant/role';

import {reactive} from "vue";

const router = useRouter();
const {proxy} = getCurrentInstance();
// const { sys_normal_disable, sys_Channel_sex } = proxy.useDict("sys_normal_disable", "sys_Channel_sex");

const ChannelList = ref([]);
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

// const deptName = ref("");
// const deptOptions = ref(undefined);
// const initPassword = ref(undefined);
// const postOptions = ref([]);
// const roleOptions = ref([]);

const channelTypesArr = ref([]);

const agentList = ref([])
const configAgentDialogVisible = ref(false)
const channelAgentConfigTitle = ref("")
const channelAgentList = ref([])
const currentChannelId = ref(0) ;

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
  {key: 1, label: `频道名称`, visible: true},
  {key: 2, label: `频道类型`, visible: true},
  {key: 3, label: `使用场景`, visible: true},
  {key: 4, label: `状态`, visible: true},
  {key: 5, label: `频道描述`, visible: true},
  {key: 6, label: `应用目标`, visible: true},
  {key: 7, label: `创建时间`, visible: true},
  {key: 8, label: `编辑`, visible: true},

]);

const data = reactive({
  form: {
    channelType: 'org'
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    ChannelName: undefined,
    name: undefined,
    toolType: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    channelName: [{required: true, message: "频道名称不能为空", trigger: "blur"}, {
      min: 2,
      max: 20,
      message: "频道名称长度必须介于 2 和 20 之间",
      trigger: "blur"
    }],
    channelType: [{required: true, message: "频道类型不能为空", trigger: "blur"}],
    channelDesc: [{required: true, message: "频道类型不能为空", trigger: "blur"}],
    scene: [{required: true, message: "使用场景不能为空", trigger: "blur"}],
    hasStatus: [{required: true, message: "状态不能为空", trigger: "blur"}],
    description: [{required: true, message: "频道描述不能为空", trigger: "blur"}],
    target: [{required: true, message: "应用目标不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules} = toRefs(data);


/** 查询应用列表 */
function getList() {
  loading.value = true;
  listChannel(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    ChannelList.value = res.rows;
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
  form.value.icon = response.data ;
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
    return delChannel(applicationIds);
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
  proxy.resetForm("ChannelRef");
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
  imageUrl.value = []; // 清空数组
};

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getChannel(id).then(response => {
    form.value = response.data;
    form.value.id = id;
    open.value = true;
    title.value = "修改应用";

    const item = {
      url: upload.display + response.data.icon ,
      uid: id // 可以在这里初始化属性
    }
    imageUrl.value = []; // 清空数组
    imageUrl.value.push(item) ; 

  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ChannelRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateChannel(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addChannel(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
};

/** 配置成员 */
const configAgent = async(row) => {

    const channelType = row.channelType ;
    if(channelType == 'org' || channelType == 'private'){
      await handleListAllRole() ;
    }else if(channelType == 'public'){
      await handleListPublicRole();
    }

    configAgentDialogVisible.value = true ;
    channelAgentConfigTitle.value = row.channelName + "成员" ; 
    currentChannelId.value = row.id ;
    channelAgentList.value = row.roles ;

}

/** 获取到所有的角色包括公共角色信息 */
const handleListAllRole = async() => {

  const res = await listAllRole() ; // .then(res => {

  for (let i = 0; i < res.data.length ; i++) {
      let item = res.data[i]

      agentList.value.push({
        key: item.id ,
        avatar: item.roleAvatar,
        label: item.roleName , 
        disabled: false ,
    })
  }

  // })
}

/** 获取到所有的公共角色(即在商店里面的角色) */
const handleListPublicRole = async() => {

  const res = await listPublicRole() ; // .then(res => {

  for (let i = 0; i < res.data.length ; i++) {
      let item = res.data[i]

      agentList.value.push({
        key: item.id ,
        avatar: item.roleAvatar,
        label: item.roleName, 
      })
  }

}

/** 关闭弹窗 */
function handleCloseAgentConfig(){
  configAgentDialogVisible.value = false ;

  updateChannelAgent(currentChannelId.value , channelAgentList.value).then(res => {
    proxy.$modal.msgSuccess("更新成功");
    getList() ;
  })
  
}

/** 搜索过滤方法 */
const filterAgentMethod = (query, item) => {
  // return item.initial.includes(query)
  return item ;
}

const handleListAllChannelType = () => {
  listAllChannelType().then(res => {
    channelTypesArr.value = res.data
  })
}

const getChannelTypeByType = (type) => {
  for (let i = 0; i < channelTypesArr.value.length; i++) {
    if (channelTypesArr.value[i].value == type) {
      return channelTypesArr.value[i] ;
    }
  }
}

getList();
// handleListAllRole();
handleListAllChannelType() ;

</script>

<style lang="scss" scoped>
.role-icon {
  img {
    width:45px;
    height:45px;
    border-radius: 50%;
  }
}
</style>