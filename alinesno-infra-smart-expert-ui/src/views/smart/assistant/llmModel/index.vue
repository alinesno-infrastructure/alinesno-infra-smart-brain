<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--应用数据-->
      <el-col :span="24" :xs="24">
        <div style="display: flex;align-items: center;justify-content: space-between;">

          <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
            <el-form-item label="模型名称" prop="modelName">
              <el-input v-model="queryParams['condition[modelName|like]']" placeholder="请输入大模型名称" clearable
                style="width: 240px" @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="模型类型" prop="modelType">
              <el-input v-model="queryParams['condition[providerId|like]']" placeholder="请输入所属提供商名称" clearable
                style="width: 240px" @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button type="primary" plain icon="Plus" @click="handleAdd">
                新增模型
              </el-button>
            </el-col>

          </el-row>
        </div>


        <div class="gen-template-box" v-if="LlmModelList.size == 0">
          <el-col :sm="24">
            <el-empty description="还没有创建模型,可以根据提示链接创建自己的工程模型">
              <el-link type="primary" icon="el-icon-link">如何创建工程模板?</el-link>
            </el-empty>
          </el-col>
        </div>

        <!-- 模板内容 -->
        <div class="vc-div div_l14lqa1k tpl-container" v-loading="loading || loadingFilter">

          <div class="vc-div div_l14lqa1j tpl-item" v-for="(item, index) in LlmModelList" :key="index">
            <div class="vc-div div_l14lqa1i">
              <div class="vc-div div_l14lqa1c tpl-item-title">
                <div class="vc-text text_l14lqa1a"
                  style="display: -webkit-box; -webkit-box-orient: vertical; overflow: hidden; -webkit-line-clamp: 1;">
                  <img :src="'http://data.linesno.com/icons/llm/' + item.providerCode + '.png'" alt="图标" style="width: 45px; height: 45px; border-radius: 50%;">
                   {{ item.modelName }}
                </div>
                <div>

                  <el-tooltip content="查看" placement="top" v-if="item.id !== 1">
                    <el-button link type="primary" icon="Edit" @click="handleUpdate(item)"
                      v-hasPermi="['system:LlmModel:edit']"></el-button>
                  </el-tooltip>
                  <el-tooltip content="删除" placement="top" v-if="item.id !== 1">
                    <el-button link type="primary" icon="Delete" @click="handleDelete(item)"
                      v-hasPermi="['system:LlmModel:remove']"></el-button>
                  </el-tooltip>
                </div>
              </div>
              <div class="vc-div tpl-item-description">
                <div class="vc-text text_l14lqa1d">
                  名称: {{ item.model }}
                </div>
                <div class="vc-text text_l14lqa1d">
                  地址: {{ item.apiUrl }}
                </div>
              </div>
              <div class="vc-div tpl-item-footer">
                <div class="vc-text text_l14lqa1g" :title="item.tempTeam">
                  <el-button text bg type="primary">
                    {{ item.modelPermission == 'org' ? '组织' : '私有' }}
                  </el-button>
                </div>

                <div class="vc-text" title="">
                    <el-button type="primary" text bg v-if="item.modelType === 'large_language_model'">
                        <i class="fas fa-file-signature"></i> {{ item.fieldProp }}
                    </el-button>
                    <el-button type="danger" text bg v-if="item.modelType ==='speech_synthesis'">
                        <i class="fa-solid fa-volume-up"></i> {{ item.fieldProp }}
                    </el-button>
                    <el-button type="success" text bg v-if="item.modelType ==='vector_model'">
                        <i class="fa-solid fa-arrows-alt-v"></i> {{ item.fieldProp }} <!-- 这里的图标是示意，可按需更换 -->
                    </el-button>
                    <el-button type="warning" text bg v-if="item.modelType ==='re_ranking_model'">
                        <i class="fa-solid fa-sync-alt"></i> {{ item.fieldProp }} <!-- 这里的图标是示意，可按需更换 -->
                    </el-button>
                    <el-button type="info" text bg v-if="item.modelType ==='speech_recognition'">
                        <i class="fa-solid fa-microphone-alt"></i> {{ item.fieldProp }} <!-- 这里的图标是示意，可按需更换 -->
                    </el-button>
                    <el-button type="danger" text bg v-if="item.modelType === 'vision_model'">
                        <i class="fa-solid fa-eye"></i> {{ item.fieldProp }} <!-- 这里的图标是示意，可按需更换 -->
                    </el-button>
                    <el-button type="primary" text bg v-if="item.modelType === 'image_generation'">
                        <i class="fa-solid fa-palette"></i> {{ item.fieldProp }} <!-- 这里的图标是示意，可按需更换 -->
                    </el-button>
                </div>

              </div>
            </div>
          </div>

        </div>

        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-col>
    </el-row>

    <!-- 添加或修改应用配置对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>

      <el-form :model="form" :rules="rules" ref="LlmModelRef" label-width="80px" style="padding:20px;">
        <el-row>
          <el-col :span="24">
            <el-form-item label="模型名称" prop="modelName">
              <el-input v-model="form.modelName" placeholder="请输入大模型名称" maxlength="255" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="模型厂商" prop="providerCode">
              <el-radio-group v-model="form.providerCode" @change="selectLLMProvider">
                <el-radio v-for="item in providerOptions" :value="item.code" :label="item.code" :key="item.code">
                  {{ item.displayName }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="模型类型" prop="modelType">
              <el-select v-model="form.modelType" placeholder="请选择模型类型">
                <el-option v-for="item in modelTypeOptions" :label="item.displayName" :value="item.code"
                  :key="item.code">
                  {{ item.displayName }}
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">模型配置</el-divider>

        <el-row>
          <el-col :span="24">
            <el-form-item label="权限" prop="modelPermission">
              <el-radio-group v-model="form.modelPermission">
                <el-radio :value="'person'" :label="'person'">私有</el-radio>
                <el-radio :value="'org'" :label="'org'">组织</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="模型地址" prop="apiUrl">
              <el-input v-model="form.apiUrl" placeholder="请输入 API 地址" maxlength="255" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="ApiKey" prop="apiKey">
              <el-input type="password" v-model="form.apiKey" placeholder="请输入 API 密钥" maxlength="255" show-password="true" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="基础模型" prop="model">
              <el-select v-model="form.model" allow-create filterable default-first-option placeholder="请选择基础模型">

                <el-option v-for="item in baseModelOptions" :label="item.modelName" :value="item.modelName"
                  :key="item.modelName">
                  {{ item.modelName }}
                </el-option>

              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">测试结果</el-divider>

          <el-skeleton :rows="1" v-if="chatLoading" />

        <div v-if="!chatLoading"  >

          <el-alert title="Success alert" type="success" />
          <div style="margin-top:10px; line-height: 23px;padding: 10px;background: #f5f5f5;border-radius: 2px;">
              {{ testLlmModelReponse }}
          </div> 
        </div>

      </el-form>
      <template #footer>
        <div class="dialog-footer">

          <!-- 语音识别模型 -->
           <div v-if="form.modelType === 'speech_recognition'" >
              <img v-if="isSpeaking" :src="speakingIcon" style="width:35px" />

              <el-button v-if="isSpeaking" type="danger" text bg size="large" @click="listenPlayVoiceOption()"> 
                <i class="fa-regular fa-circle-stop"></i> &nbsp;&nbsp; 停止 
              </el-button>
              <el-button v-if="!isSpeaking" type="primary" text bg size="large" @click="listenPlayVoiceOption()"> 
                <i class="fa-solid fa-microphone-lines"></i> 试讲 
              </el-button>
           </div>

          <!-- 语音合成模型测试 -->
          <div v-if="form.modelType === 'speech_synthesis'" >
            <img v-if="isSpeaking" :src="speakingIcon" style="width:35px" />
            <el-button v-if="isSpeaking" type="danger" text bg size="large" @click="listenPlayVoiceOption()"> 
              <i class="fa-regular fa-circle-stop"></i> &nbsp;&nbsp; 停止 
            </el-button>
            <el-button v-if="!isSpeaking" type="primary" text bg size="large" @click="listenPlayVoiceOption()"> 
              <i class="fa-solid fa-headphones-simple"></i> &nbsp;&nbsp; 试听
            </el-button>
          </div>

          <!-- 大模型测试-->
          <el-button type="primary" 
              v-if="form.modelType === 'large_language_model' || form.modelType === 'image_generation' || form.modelType === 'vector_model' || form.modelType === 're_ranking_model' " 
              @click="handleTestLlmModel" 
              text 
              bg 
              size="large">测试</el-button>

          <el-button type="primary" @click="submitForm" text bg size="large">保存</el-button>
          <el-button @click="cancel" text bg size="large">取 消</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup name="LlmModel">
// import { getToken } from "@/utils/auth";

import {
  listLlmModel,
  delLlmModel,
  getLlmModel,
  updateLlmModel,
  addLlmModel,
  testLlmModel,
  getAllModelProvidersInfo,
  getAllModelTypesInfo,
} from "@/api/smart/assistant/llmModel";

import { reactive, ref } from "vue";
import speakingIcon from '@/assets/icons/speaking.gif';

// import {useRouter, getCurrentInstance} from "vue-router";

const router = useRouter();
const { proxy } = getCurrentInstance();

const LlmModelList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);

// const single = ref(true);
// const multiple = ref(true);

const total = ref(0);
const title = ref("");
const dateRange = ref([]);

// 模型测试
const chatLoading = ref(true);
const isSpeaking = ref(false)

const providerOptions = ref(undefined)
const modelTypeOptions = ref(undefined)
const baseModelOptions = ref(undefined)

const testLlmModelReponse = ref('')

// const deptName = ref("");
// const deptOptions = ref(undefined);
// const initPassword = ref(undefined);
// const postOptions = ref([]);
// const roleOptions = ref([]);
// /*** 应用导入参数 */
// const upload = reactive({
//   // 是否显示弹出层（应用导入）
//   open: false,
//   // 弹出层标题（应用导入）
//   title: "",
//   // 是否禁用上传
//   isUploading: false,
//   // 是否更新已经存在的应用数据
//   updateSupport: 0,
//   // 设置上传的请求头部
//   headers: { Authorization: "Bearer " + getToken() },
//   // 上传的地址
//   url: import.meta.env.VITE_APP_BASE_API + "/system/LlmModel/importData"
// });

// 列显隐信息
// const columns = ref([
//   { key: 0, label: `图标`, visible: true },
//   { key: 1, label: `大模型名称`, visible: true },
//   { key: 2, label: `大模型描述`, visible: true },
//   { key: 3, label: `所属提供商名称`, visible: true },
//   { key: 4, label: `API 密钥`, visible: true },
//   { key: 5, label: `API 地址`, visible: true },
//   { key: 6, label: `模型名称`, visible: true },
//   { key: 7, label: `编辑`, visible: true },
// ]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    modelName: undefined,
    providerId: undefined,
    status: undefined,
    deptId: undefined
  },
  rules: {
    modelName: [{ required: true, message: "大模型名称不能为空", trigger: "blur" }, {
      min: 2,
      max: 255,
      message: "大模型名称长度必须介于 2 和 255 之间",
      trigger: "blur"
    }],
    providerCode: [{ required: true, message: "所属提供商名称不能为空", trigger: "blur" }],
    modelType: [{ required: true, message: "所属模型类型不能为空", trigger: "blur" }],
    apiKey: [{ required: true, message: "API 密钥不能为空", trigger: "blur" }],
    apiUrl: [{ required: true, message: "API 地址不能为空", trigger: "blur" }],
    model: [{ required: true, message: "模型名称不能为空", trigger: "blur" }],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询应用列表 */
function getList() {
  loading.value = true;
  listLlmModel(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    LlmModelList.value = res.rows;
    total.value = res.total;
  });
};

/** 搜索按钮操作 */
function handleQuery() {
  console.log(queryParams);
  queryParams.value.pageNum = 1;
  getList();
};

/** 模型标识 */
// function getByModelType(modelCode){
//   let currentType = null 

//   for(var i = 0 ; i < modelTypeOptions.length ; i ++){
//     if(modelTypeOptions.value[i].code == modelCode){
//       currentType = modelTypeOptions.value[i]
//       break 
//     }
//   }

//   if(currentType){
//     return currentType.displayName
//   }else{
//     return "未识别模型" 
//   }

// }

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  queryParams.value.modelName = undefined;
  queryParams.value.providerId = undefined;
  proxy.$refs.deptTreeRef.setCurrentKey(null);
  handleQuery();
};

/** 删除按钮操作 */
function handleDelete(row) {
  const ids = row.id || ids.value;

  proxy.$modal.confirm('是否确认删除大模型编号为"' + ids + '"的数据项？').then(function () {
    return delLlmModel(ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
};

/** 选择条数  */
// function handleSelectionChange(selection) {
//   ids.value = selection.map(item => item.id);
//   single.value = selection.length != 1;
//   multiple.value = !selection.length;
// };

/** 重置操作表单 */
function reset() {
  form.value = {
    id: undefined,
    icon: undefined,
    modelName: undefined,
    description: undefined,
    providerId: undefined,
    apiKey: undefined,
    apiUrl: undefined,
    model: undefined,
  };
  proxy.resetForm("LlmModelRef");
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
  title.value = "添加大模型";
};

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getLlmModel(id).then(response => {
    form.value = response.data;
    form.value.id = id;
    open.value = true;
    title.value = "修改大模型";
  });
};

/** 提交按钮 */
function submitForm() {
  proxy.$refs["LlmModelRef"].validate(valid => {
    if (valid) {
      if (form.value.id != undefined) {
        updateLlmModel(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          // open.value = false;
          getList();
        });
      } else {
        addLlmModel(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          // open.value = false;
          getList();
        });
      }
    }
  });
};

/** 测试提交 */
function handleTestLlmModel(){
  proxy.$refs["LlmModelRef"].validate(valid => {
    if (valid) {
      testLlmModel(form.value).then(response => {
        proxy.$modal.msgSuccess("测试成功");
        testLlmModelReponse.value = response.data;
        chatLoading.value = false ;
      });
    }
  });
}

/** 获取模型供应商 */
function handleAllModelProvidersInfo() {
  getAllModelProvidersInfo().then(response => {
    providerOptions.value = response.data;
  });

  getAllModelTypesInfo().then(response => {
    modelTypeOptions.value = response.data;
  });
}

/** 选中模型供应商 */
function selectLLMProvider(value) {
  console.log('value = ' + value);

  let selectedItem = null;

  for (let i = 0; i < providerOptions.value.length; i++) {
    const item = providerOptions.value[i];
    if (item.code === value) {
      // 这里可以对选中的项进行处理，比如打印信息
      console.log('找到匹配项:', item);
      // 如果想提前结束查找，可添加 break 语句
      selectedItem = item;
      break;
    }

  }

  if (selectedItem) {  // 查找到角色

    form.value.apiUrl = selectedItem.url;
    baseModelOptions.value = selectedItem.modelList;

    console.log('form.apiUrl = ' + form.apiUrl + 'baseModelOptions = ' + baseModelOptions);
  }

}

// >>>>>>>>>>>>>>>>>> 模型测试
/** 是否在播放 */
const listenPlayVoiceOption = () => {
  isSpeaking.value = !isSpeaking.value
}

getList();
handleAllModelProvidersInfo();

</script>

<style lang="scss" scoped>

.role-icon {
  img {
    width: 45px;
    height: 45px;
    border-radius: 50%;
  }
}

.text_l14lqa1d{
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 13px;
  color: #444;
  line-height: 20px;
}

.tpl-item-footer{
  padding-top:10px;
}

.dialog-footer{
  display: flex;
  gap: 10px;
  flex-direction: row;
  justify-content: flex-end;
}

</style>