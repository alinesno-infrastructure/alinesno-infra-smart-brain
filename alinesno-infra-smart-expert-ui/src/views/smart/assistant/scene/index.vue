<template>
  <div class="app-container">
    <el-row :gutter="20">
       <!--应用数据-->
      <el-col :span="3" :xs="24">
        <SceneSider :scenes="supportSceneList" @scene-selected="handleSceneSelected"  />
      </el-col>
      <el-col :span="21" :xs="24">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">

          <el-form-item label="场景名称" prop="name">
            <el-input v-model="queryParams.sceneName" placeholder="请输入场景名称" clearable
                      style="width: 240px" @keyup.enter="handleQuery"/>
          </el-form-item>

          <el-form-item label="数据范围" prop="sceneScope" label-width="100px">
            <el-radio-group v-model="queryParams.sceneScope" label="数据范围" label-width="100px" @change="handleQuery">
              <el-radio v-for="item in sceneScopeList" 
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
                v-hasPermi="['system:scene:edit']"
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
                v-hasPermi="['system:scene:remove']"
            >删除
            </el-button>
          </el-col>

          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="SceneList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column label="图标" align="center" width="70" prop="sceneBanner" v-if="columns[0].visible">
            <template #default="scope">
              <div class="scene-icon">
                <img :src="imagePath(scope.row.sceneBanner)" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="场景名称" align="left" key="name" prop="name" v-if="columns[1].visible">
            <template #default="scope">
              <div style="font-size: 15px;font-weight: 500;color: #1d75b0;">
               {{ scope.row.sceneName }}
              </div>
              <div style="font-size: 13px;color: #a5a5a5;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                {{ scope.row.sceneDesc }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="数据范围" align="left" key="description"  width="180" prop="description" v-if="columns[5].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <i :class="getSceneScopeByType(scope.row.sceneScope)?.icon"></i>
              {{  getSceneScopeByType(scope.row.sceneScope)?.label }}
            </template>
          </el-table-column> 
          <el-table-column label="场景类型" align="left" key="sceneType" width="220" prop="sceneType" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
             
              <i :class="getSceneTypeByType(scope.row.sceneType)?.icon" />
              {{ getSceneTypeByType(scope.row.sceneType)?.sceneName }} &nbsp;
               <el-button type="primary" text bg @click="configScene(scope.row)">
                配置
              </el-button>
            </template>
          </el-table-column>

          <el-table-column label="智能体" align="center" key="sceneType" width="320" prop="sceneType" v-if="columns[2].visible" :show-overflow-tooltip="true">
            <template #default="scope">
              <el-button text bg type="warning" @click="openGreetingQuestionPanel(scope.row)">
                <i class="fa-solid fa-envelope-open-text"></i> &nbsp;开场白
              </el-button>
              <el-button text bg type="primary" @click="configAgent(scope.row)">
                <i class="fa-solid fa-user-shield"></i>&nbsp;智能体
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
                           v-hasPermi="['system:scene:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top" v-if="scope.row.id !== 1">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                           v-hasPermi="['system:scene:remove']"></el-button>
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
      <el-form :model="form" :rules="rules" size="large" ref="SceneRef" label-width="80px">
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
            <el-form-item label="场景类型" prop="sceneType">
              <el-radio-group v-model="form.sceneType">
                <el-radio v-for="item in supportSceneList" 
                  :key="item.code" 
                  :value="item.code"
                  :label="item.code" size="large">
                  <i :class="item.icon" /> {{ item.sceneName }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="频道类型" prop="sceneScope">

              <el-alert title="频道类型提交确认后不可修改" type="warning" :closable="false" show-icon style="margin-bottom: 10px;" />

              <el-radio-group v-model="form.sceneScope" :disabled="form.id">
                <el-radio v-for="item in sceneScopeList" 
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
            <el-form-item  label="场景名称" prop="sceneName">
              <el-input v-model="form.sceneName" placeholder="请输入场景名称" maxlength="30"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="场景描述" prop="sceneDesc">
              <el-input v-model="form.sceneDesc" placeholder="请输入场景类型" maxlength="350"/>
            </el-form-item>
          </el-col>
        </el-row>

        
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" size="large" @click="submitForm">确 定</el-button>
          <el-button @click="cancel" size="large">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="configAgentDialogVisible" :title="sceneAgentConfigTitle" width="960px">
      <!-- 配置场景智能体角色 -->
     <ConfigSceneAgent ref="configSceneAgentRef" />
    </el-dialog>

    <!-- 开场白预置问题 -->
    <el-dialog title="开场白预置问题" v-model="openingPhraseStatusVisible" width="700px">
        <OpeningPhraseStatusPanel @handleOpeningPhraseStatusPanelClose="handleOpeningPhraseStatusPanelClose" ref="openingPhraseStatusPanelRef" />
    </el-dialog>

    <!-- 场景配置项 -->
    <el-dialog :title="sceneConfigPanelTitle" v-model="openingSceneConfigPanelVisible" width="1300px">
        <!-- 通用场景 -->
        <GeneratorAgentConfigPanel 
            ref="generatorAgentConfigPanelRef" 
            v-if="sceneConfigType === 'generalAgent'" 
            :initial-selected-templates="selectedConfigData"
            :sceneData="sceneItemData" />
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" size="large" @click="submitSceneConfig">确 定</el-button>
            <el-button @click="openingSceneConfigPanelVisible = false" size="large">取 消</el-button>
          </div>
        </template>
    </el-dialog>

  </div>
</template>

<script setup name="scene">
import {getToken} from "@/utils/auth";
import {
  listScene,
  delScene,
  getScene,
  updateScene,
  getSceneScope,
  addScene,
  updateSceneAgent,
  updateGreetingQuestion,
  supportAllScene , 
  updateSceneConfigData
} from "@/api/smart/assistant/scene";

import {
  listAllRole
} from '@/api/smart/assistant/role';

import {nextTick, reactive} from "vue";

import SceneSider from "./sceneSider.vue"
import ConfigSceneAgent from "./configSceneAgent.vue"
import OpeningPhraseStatusPanel from './openingPhraseStatusPanel'

// 场景配置面板
import GeneratorAgentConfigPanel from './config/generalAgentConfigPanel.vue'

const generatorAgentConfigPanelRef = ref(null)

const router = useRouter();
const {proxy} = getCurrentInstance();

const configSceneAgentRef = ref(null)

// 开场白预置问题
const openingPhraseStatusVisible = ref(false)
const openingPhraseStatusPanelRef  = ref(null)

// 场景配置项
const sceneConfigPanelTitle = ref(null)
const openingSceneConfigPanelVisible = ref(false)
const sceneConfigType = ref(null)
const sceneConfigTypeSceneId = ref(null)
const sceneItemData = ref(null)
const selectedConfigData = ref(null)

const SceneList = ref([]);
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

const sceneTypesArr = [];

const sceneScopeList = ref([])

const agentList = ref([])
const configAgentDialogVisible = ref(false)
const sceneAgentConfigTitle = ref("")
const sceneAgentList = ref([])
const currentSceneId = ref(0) ;
const supportSceneList = ref([])

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
    SceneName: undefined,
    name: undefined,
    toolType: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    sceneName: [{required: true, message: "场景名称不能为空", trigger: "blur"}, {min: 2, max: 20, message: "场景名称长度必须介于 2 和 20 之间", trigger: "blur" }],
    sceneDesc: [{required: true, message: "场景类型不能为空", trigger: "blur"}],
    sceneType: [{required: true, message: "场景类型不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules} = toRefs(data);


/** 查询应用列表 */
function getList() {
  loading.value = true;
  listScene(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    SceneList.value = res.rows;
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
  form.value.sceneBanner = response.data ;
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
    return delScene(applicationIds);
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
  proxy.resetForm("SceneRef");
};

/** 打开开场白角色配置 */
const openGreetingQuestionPanel = (item) => {
    openingPhraseStatusVisible.value = true;
    currentSceneId.value = item.id ;

    console.log('greetingQuestion = ' + item.greetingQuestion)

    nextTick(() => {
       openingPhraseStatusPanelRef.value.setOpeningQuestions(JSON.parse(item.greetingQuestion)) 
    });
}

const handleOpeningPhraseStatusPanelClose = (formData) => {
    console.log('handleOpeningPhraseStatusPanelClose formData = ' + JSON.stringify(formData))
    openingPhraseStatusVisible.value = false ;
    if(formData){
        // agentModelConfigForm.value.greetingQuestion = formData ;
        updateGreetingQuestion(currentSceneId.value, formData).then(response => {
            getList() ;
        })
    }
}

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
  getScene(id).then(response => {
    form.value = response.data;
    form.value.id = id;
    open.value = true;
    title.value = "修改应用";

    const item = {
      url: upload.display + response.data.sceneBanner ,
      uid: id // 可以在这里初始化属性
    }
    imageUrl.value = []; // 清空数组
    imageUrl.value.push(item) ; 

  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["SceneRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateScene(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addScene(form.value).then(response => {
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
    sceneAgentConfigTitle.value = row.sceneName + "配置" ; 

    nextTick(() => {
      configSceneAgentRef.value.configSceneAgent(row , supportSceneList.value);
    })
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

  updateSceneAgent(currentSceneId.value , sceneAgentList.value).then(res => {
    proxy.$modal.msgSuccess("更新成功");
    getList() ;
  })
  
}

/** 搜索过滤方法 */
const filterAgentMethod = (query, item) => {
  // return item.initial.includes(query)
  return item ;
}

/** 通过场景代码获取场景信息 */
const getSceneTypeByType = (type) => {
  for (let i = 0; i < supportSceneList.value.length; i++) {
    const item = supportSceneList.value[i];
    if(item.code == type){
      return item ;
    }
  }
}

/** 场景数据权限 */
const getSceneScopeByType = (type) => {
  for (let i = 0; i < sceneScopeList.value.length; i++) {
    const item = sceneScopeList.value[i];
    if(item.value == type){
      return item ;
    }
  }
}

/** 支持的场景列表 */
const handleSupportScene = () => {
  supportAllScene().then(res => {
    supportSceneList.value = res.data ;
  })
}

/** 场景数据权限 */
const handleGetSceneScope = () => {
  getSceneScope().then(res => {
    sceneScopeList.value = res.data ;
  })
}

// 场景配置
const configScene = (row) => {
  openingSceneConfigPanelVisible.value = true ;
  sceneConfigPanelTitle.value = "配置 " + row.sceneName ;
  sceneConfigType.value = row.sceneType ;
  sceneConfigTypeSceneId.value = row.id ;
  selectedConfigData.value = JSON.parse(row.configData)  ;
}

const handleSceneSelected = (sceneId) => {
  console.log('选中的场景ID:', sceneId);
  // 在这里处理选中的场景ID
  queryParams.value.sceneType = sceneId;
  getList();
};

// 提交场景配置
const submitSceneConfig = () => {
  let sceneConfigData = null ; 

  if(sceneConfigType.value === 'generalAgent') {
    sceneConfigData = generatorAgentConfigPanelRef.value.getConfigData() ; 
  }

  console.log('sceneConfigType = ' + sceneConfigType.value + ' ,  sceneConfigData = ' + sceneConfigData)

  updateSceneConfigData({sceneId: sceneConfigTypeSceneId.value , config: sceneConfigData}).then(res => {
    proxy.$modal.msgSuccess("更新成功");
    openingSceneConfigPanelVisible.value = false ; 
    getList();
  })
}

onMounted(async () => {
  await getList();
  await handleListAllRole();
  await handleSupportScene();
  await handleGetSceneScope();
})



</script>

<style lang="scss" scoped>
.scene-icon{
  img {
    width:auto;
    height:45px;
    border-radius: 4px;
  }
}
</style>