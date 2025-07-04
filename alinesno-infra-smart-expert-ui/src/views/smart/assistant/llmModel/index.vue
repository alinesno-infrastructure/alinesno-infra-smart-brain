<template>
  <div class="app-container">
    <el-row :gutter="20">

      <!-- 
      <el-col :span="4" :xs="24">
        <ModelTypeSelectPanel />
      </el-col> 
      -->

      <!--应用数据-->
      <el-col :span="24" :xs="24">
        <div style="display: flex;align-items: center;justify-content: space-between;">

          <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
            <el-form-item label="模型名称" prop="modelName">
              <el-input v-model="modelName" placeholder="请输入大模型名称" clearable
                style="width: 240px" @keyup.enter="handleQuery" />
            </el-form-item>

            <!-- 
            <el-form-item label="模型类型" prop="modelType">
              <el-input v-model="providerId" placeholder="请输入所属提供商名称" clearable
                style="width: 240px" @keyup.enter="handleQuery" />
            </el-form-item> 
            -->

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

      <el-row :gutter="10" class="mb8">
         <el-col :span="24">
            <div class="aip-template-type" style="padding:0px" >
               <span class="template-type-title" >类型</span>
               <div class="template-type-list" >
                  <span v-for="(item , index) in modelTypeOptions" 
                    :key="index" 
                    class="template-type-item" 
                    :class="queryParams.modelType === item.code ? 'active' : ''"
                    @click="handleTypeChange(item)">
                     {{ item.displayName }}
                  </span>
               </div>
            </div>
         </el-col>
      </el-row>

        <div class="gen-template-box" v-if="LlmModelList.length == 0">
          <el-col :sm="24">
            <el-empty description="还没有创建模型,可以根据提示链接创建链接自己的模型">
              <el-button type="primary" bg icon="Link" @click="handleAdd">新增加模型</el-button>
            </el-empty>
          </el-col>
        </div>


        <!-- 模板内容 -->
        <div class="vc-div div_l14lqa1k tpl-container" v-loading="loading || loadingFilter">


          <div class="vc-div div_l14lqa1j tpl-item" v-for="(item, index) in LlmModelList" :key="index">
            <div class="vc-div div_l14lqa1i">
              <div class="vc-div div_l14lqa1c tpl-item-title">
                <div class="vc-text text_l14lqa1a" style="display: -webkit-box; -webkit-box-orient: vertical; overflow: hidden; -webkit-line-clamp: 1;font-weight: bold">
                  <img :src="getLlmIconPath(item.providerCode)" alt="图标" style="width: 30px; height: 30px; border-radius: 50%;">
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
                    <span v-if="item.modelPermission == 'person'">私有</span>
                    <span v-if="item.modelPermission == 'org'">组织</span>
                    <span v-if="item.modelPermission == 'public'">公开</span>
                  </el-button>
                </div>

                <div class="vc-text" title="">
                    <el-button type="info" text bg v-if="item.modelType === 'large_language_model'">
                        <i class="fas fa-file-signature"></i> {{ item.fieldProp }}
                    </el-button>
                    <el-button type="info" text bg v-if="item.modelType ==='speech_synthesis'">
                        <i class="fa-solid fa-volume-up"></i> {{ item.fieldProp }}
                    </el-button>
                    <el-button type="info" text bg v-if="item.modelType ==='vector_model'">
                        <i class="fa-solid fa-arrows-alt-v"></i> {{ item.fieldProp }} <!-- 这里的图标是示意，可按需更换 -->
                    </el-button>
                    <el-button type="info" text bg v-if="item.modelType ==='re_ranking_model'">
                        <i class="fa-solid fa-sync-alt"></i> {{ item.fieldProp }} <!-- 这里的图标是示意，可按需更换 -->
                    </el-button>
                    <el-button type="info" text bg v-if="item.modelType ==='speech_recognition'">
                        <i class="fa-solid fa-microphone-alt"></i> {{ item.fieldProp }} <!-- 这里的图标是示意，可按需更换 -->
                    </el-button>
                    <el-button type="info" text bg v-if="item.modelType === 'vision_model'">
                        <i class="fa-solid fa-eye"></i> {{ item.fieldProp }} <!-- 这里的图标是示意，可按需更换 -->
                    </el-button>
                    <el-button type="info" text bg v-if="item.modelType === 'image_generation'">
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
    <el-dialog :title="title" v-model="open" width="800px" append-to-body :before-close="handleClose">

      <el-form :model="form" :rules="rules" ref="LlmModelRef" label-width="80px" style="padding:20px;" size="large">
        <el-row>
          <el-col :span="24">
            <el-form-item label="模型名称" prop="modelName">
              <el-input v-model="form.modelName" placeholder="请输入大模型名称" maxlength="255" size="large" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="模型厂商" prop="providerCode">
              <el-radio-group v-model="form.providerCode">
                <el-radio v-for="item in providerOptions" size="large" :value="item.code" :label="item.code" :key="item.code" @change="handleSelectLLMProvider(item)">
                  <!-- 添加图标 -->
                  <img :src="getLlmIconPath(item.code)" alt="图标" style="width: 30px; height: 30px; border-radius: 50%;">

                  {{ item.displayName }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="模型类型" prop="modelType">

              <el-radio-group v-model="form.modelType" size="large">
                <el-radio v-for="item in filteredOptions" :value="item.code" :label="item.code" :key="item.code" @change="handleSelectModelType(item)">
                  {{ item.displayName }}
                </el-radio>
              </el-radio-group>

            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">模型配置</el-divider>

        <el-row>
          <el-col :span="24">
            <el-form-item label="权限" prop="modelPermission">
              <!--
              <el-radio-group v-model="form.modelPermission" size="large">
                <el-radio :value="'person'" :label="'person'">私有</el-radio>
                <el-radio :value="'org'" :label="'org'">组织</el-radio>
              </el-radio-group>
              -->

              <el-radio-group v-model="form.modelPermission" size="large">
                <el-radio v-for="option in dataScopeOptions" :key="option.value" :value="option.value" :label="option.label">
                    <el-tooltip class="box-item" effect="dark" :content="option.desc" placement="top-start">
                  <div style="display: flex;align-items: center;gap: 6px;">
                      {{ option.text }} <el-icon><QuestionFilled /></el-icon>
                  </div>
                    </el-tooltip>
                </el-radio>
              </el-radio-group>

            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="模型地址" prop="apiUrl">
              <el-input v-model="form.apiUrl" size="large" placeholder="请输入 API 地址" maxlength="255" autocomplete="off" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="ApiKey" prop="apiKey">
              <el-input type="password" v-model="form.apiKey" size="large" placeholder="请输入 API 密钥" maxlength="255" show-password="true" autocomplete="off" />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.providerCode === 'doubao' && form.modelType === 'image_generation'" prop="secretKey">
            <el-form-item label="SecretKey" prop="secretKey">
              <el-input type="password" size="large" v-model="form.secretKey" placeholder="请输入 secretKey 密钥" maxlength="255" show-password="true" autocomplete="off" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="基础模型" prop="model" v-if="form.modelType !== 'speech_synthesis'">
              <el-select
                  v-model="form.model"
                  filterable
                  allow-create
                  default-first-option
                  :reserve-keyword="false"
                  placeholder="自定义基础输入模型后回车即可"
                  size="large">
                  <el-option
                    v-for="item in currentLlmModelList"
                    :key="item.modelName"
                    :label="item.modelName"
                    :value="item.modelName"
                  >
                    {{ item.modelName }}
                  </el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="基础模型" prop="model" v-if="form.modelType === 'speech_synthesis'">
              <el-select
                  v-model="form.model"
                  filterable
                  allow-create
                  default-first-option
                  :reserve-keyword="false"
                  placeholder="自定义基础输入模型后回车即可"
                  @change="handleSelectSpeechModel"
                  size="large">
                  <el-option
                    v-for="speechItem in currentSpeechModelList"
                    :key="speechItem.modelName"
                    :label="speechItem.modelName"
                    :value="speechItem.modelName"
                  >
                    {{ speechItem.modelName }}
                  </el-option>
                </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="发音人" prop="voice" v-if="form.modelType === 'speech_synthesis'">
              <!-- <el-input type="text" v-model="form.voice" placeholder="请输入对应模型的发音人" maxlength="255" /> -->
              <el-select
                  v-model="form.voice"
                  filterable
                  allow-create
                  default-first-option
                  :reserve-keyword="false"
                  placeholder="自定义基础输入模型后回车即可"
                  size="large">
                  <el-option
                    v-for="item in currentSpeechVoiceModelList"
                    :key="item.voice"
                    :label="item.voice"
                    :value="item.voice"
                  >
                    {{ item.voice }}({{ item.description }})
                  </el-option>
                </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <div v-if="(form.modelType === 'large_language_model' || form.modelType === 'vision_model' || form.modelType === 'ocr_model') && (testLlmModelReponse || testLlmModelReasoingReponse)">
          <el-divider content-position="left">测试结果</el-divider>

          <div class="reasoning-chat-content" v-if="testLlmModelReasoingReponse">
              {{ testLlmModelReasoingReponse }}
          </div> 
          <div class="text-chat-content">
              {{ testLlmModelReponse }}
          </div> 
        </div>

        <div v-if="form.modelType === 'speech_recognition' && speechRecognitionResult">
          <el-divider content-position="left">识别结果</el-divider>

          <div class="text-chat-content">
              {{ speechRecognitionResult }}
          </div> 
        </div>


        <el-row v-if="form.modelType === 'image_generation' && generatedImageUrl">
          <el-divider content-position="left"> 图片生成</el-divider>
          <div>
            <el-image
              style="width:200px;border-radius: 5px;"
              :src="generatedImageUrl"
              fit="contain"
              :preview-src-list="[generatedImageUrl]"
              @error="handleImageError">
              <template #loading>
                <el-spinner></el-spinner>
              </template>
            </el-image>
          </div>
        </el-row>

      </el-form>
      <template #footer>
        <div class="dialog-footer">

          <!-- 语音识别模型 -->
           <div v-if="form.modelType === 'speech_recognition'" >
              <img v-if="isRecording" :src="speakingIcon" style="width:35px" />

              <el-button v-if="isRecording" type="danger" text bg size="large" @click="stopRecording()"> 
                <i class="fa-regular fa-circle-stop"></i> &nbsp;&nbsp; 停止
              </el-button>
              <el-button v-if="!isRecording" type="primary" text bg size="large" @click="listenPlayVoiceOption()" :loading="chatLoading"> 
                <i class="fa-solid fa-microphone-lines"></i> 录音试讲 
              </el-button>
           </div>

          <!-- 大模型测试-->
          <el-button type="primary" 
              v-if="form.modelType === 'large_language_model'" 
              @click="handleTestLlmModel" 
              text bg size="large" :loading="chatLoading">模型测试</el-button>
            
          <!-- 向量测试-->
          <el-button type="primary" 
              v-if="form.modelType === 'vector_model'" 
              @click="handleTestVectorModel" 
              text bg size="large" :loading="chatLoading">向量测试</el-button>

          <!-- 重排测试-->
          <el-button type="primary" 
              v-if="form.modelType === 're_ranking_model'" 
              @click="handleTestVectorModel" 
              text bg size="large" :loading="chatLoading">重排测试</el-button>

          <!-- 图片生成测试-->
          <el-button type="primary" 
              v-if="form.modelType === 'image_generation'" 
              @click="handleTestGenerateImageModel" 
              text bg size="large" :loading="chatLoading">生成图片</el-button>

          <!-- 语音合成 -->
           <!-- 语音合成模型测试 -->
          <div v-if="form.modelType === 'speech_synthesis'" >
            <img v-if="isSpeaking" :src="speakingIcon" style="width:35px" />
          </div>
          <el-button type="primary" 
              v-if="form.modelType === 'speech_synthesis'" 
              @click="handleTestSpeechModel" 
              text bg size="large" :loading="chatLoading">语音合成</el-button>

          <!-- 多模态测试 -->
          <el-button type="primary" 
              v-if="form.modelType === 'vision_model'" 
              @click="handleTestLlmModel" 
              text bg size="large" :loading="chatLoading">识别图片</el-button>

          <!-- OCR识别 -->
          <el-button type="primary" 
              v-if="form.modelType === 'ocr_model'" 
              @click="handleTestLlmModel" 
              text bg size="large" :loading="chatLoading">测试识别</el-button>

          <el-tooltip content="请先测试通过，确认模型可用." placement="top">
            <el-button type="primary" @click="submitForm" text bg size="large" :disabled="isValidatedStatus">保存</el-button>
          </el-tooltip>

          <el-button @click="cancel" text bg size="large">取 消</el-button>
        </div>
        <br/>
      </template>
    </el-dialog>

  </div>
</template>

<script setup name="LlmModel">

import {
  listLlmModel,
  delLlmModel,
  getLlmModel,
  updateLlmModel,
  addLlmModel,
  testLlmModel,
  testRecognition ,
  getAllModelProvidersInfo,
  getAllModelTypesInfo,
  getGenerateImage,
  getSpeech
} from "@/api/smart/assistant/llmModel";

import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import speakingIcon from '@/assets/icons/speaking.gif';
import Recorder from 'js-audio-recorder';

import { getLlmIconPath } from '@/utils/llmIcons';
import { openSseConnect } from "@/api/smart/assistant/chatsse";

import SnowflakeId from "snowflake-id";
const snowflake = new SnowflakeId();

const router = useRouter();
const { proxy } = getCurrentInstance();

const LlmModelList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);

const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const channelId = ref(null);

// 模型测试
const isValidatedStatus = ref(true)  // 验证状态(false通过|true不通过)
const chatLoading = ref(false);
const isSpeaking = ref(false)
const streamLoading = ref(null)

const providerOptions = ref(undefined)
const modelTypeOptions = ref(undefined)
const filteredOptions = ref([])
const generatedImageUrl = ref('');

// 当前模型提供商
const currentModelProvider = ref(undefined)
const currentLlmModelList = ref([])
const currentSpeechModelList = ref([])
const currentSpeechVoiceModelList = ref([])

const testLlmModelReasoingReponse = ref(undefined)
const testLlmModelReponse = ref(undefined)

// 语音识别
const isRecording = ref(false);
const speechRecognitionResult = ref('')

// 组织信息
const dataScopeOptions = ref([
  { value: 'person', label: 'person', text: '私有', desc: '此数据仅你个人可见和使用' },
  { value: 'org', label: 'org', text: '组织', desc: '此数据可在组织内部共享和使用' },
  { value: 'public', label: 'public', text: '公开', desc: '此数据可被所有人访问和查看' }
]);

const recorder = new Recorder({
  sampleBits: 16, // 采样位数，支持 8 或 16，默认是16
  sampleRate: 16000, // 采样率，支持 11025、16000、22050、24000、44100、48000，根据浏览器默认值，我的chrome是48000
  numChannels: 1 // 声道，支持 1 或 2， 默认是1
});

// 监听录音状态
const stat = ref({ duration: 0, fileSize: 0, vol: 0 });
const st = ref({ start: 0, isGo: false });
recorder.onprogress = (params) => {
  stat.value = { duration: params.duration, fileSize: params.fileSize, vol: params.vol };
};

const data = reactive({
  form: {
    modelPermission: 'person'
  },
  queryParams: {
    pageNum: 1,
    pageSize: 12,
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
    modelPermission: [{ required: true, message: "模型权限不能为空", trigger: "blur" }],
    providerCode: [{ required: true, message: "所属提供商名称不能为空", trigger: "blur" }],
    modelType: [{ required: true, message: "所属模型类型不能为空", trigger: "blur" }],
    apiKey: [{ required: true, message: "API 密钥不能为空", trigger: "blur" }],
    secretKey: [{ required: true, message: "SecretKey 密钥不能为空", trigger: "blur" }],
    apiUrl: [{ required: true, message: "API 地址不能为空", trigger: "blur" }],
    model: [{ required: true, message: "模型名称不能为空", trigger: "blur" }],
    voice: [{ required: true, message: "发音人模型不能为空", trigger: "blur" }],
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

  testLlmModelReasoingReponse.value = '' ; 
  testLlmModelReponse.value = '' ;

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

  isValidatedStatus.value = true 
  generatedImageUrl.value = null;
  
  open.value = true;
  title.value = "添加大模型";
};

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();

  isValidatedStatus.value = true;
  generatedImageUrl.value = null;

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
          getList();
          open.value = false;
        });
      } else {
        addLlmModel(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          getList();
          open.value = false;
        });
      }
    }
  });
};

/** 测试大模型 */
function handleTestLlmModel(){
  proxy.$refs["LlmModelRef"].validate(valid => {

    if (valid) {

      chatLoading.value = true ;
      form.value.testChannelId = channelId.value ; 
      testLlmModelReasoingReponse.value = '' ; 
      testLlmModelReponse.value = '' ;

      testLlmModel(form.value).then(response => {
      })
    }

  });
}

/** 测试向量化 */
function handleTestVectorModel(){
  proxy.$refs["LlmModelRef"].validate(valid => {

    if (valid) {

      chatLoading.value = true ;
      form.value.testChannelId = channelId.value ; 
      testLlmModelReasoingReponse.value = '' ; 
      testLlmModelReponse.value = '' ;

      testLlmModel(form.value).then(response => {
        chatLoading.value = false ;
        isValidatedStatus.value = false;
        proxy.$modal.msgSuccess("验证通过");
      }).catch(error => {
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

    filteredOptions.value = modelTypeOptions.value ; 
  });

  form.value.modelType = null;
}

// >>>>>>>>>>>>>>>>>> 模型测试
/** 是否在播放 */
// const listenPlayVoiceOption = () => {
//   isSpeaking.value = !isSpeaking.value
// }

// 关闭事件
const handleClose = (done) => {
  chatLoading.value = false
  open.value = false 
}

// 模型管理
const handleSelectLLMProvider = (item) => {
  console.log('item = ' + JSON.stringify(item));
  currentModelProvider.value = item ;
  filteredOptions.value = item.supportedModels

  form.value.modelType = null ; 
  form.value.apiUrl = null ;
  form.value.apiKey = null ;
  form.value.secretKey = null ;
  form.value.model = null ;
}

// 模型类型选择
const handleSelectModelType = (item) => {
  // 先通过选择的厂家过滤出厂家的的信息
  console.log('item = ' + JSON.stringify(item))
  form.value.apiUrl = item.url ;

  form.value.apiKey = null ;
  form.value.secretKey = null ;
  form.value.model = null ;

  currentLlmModelList.value = item.modelList ;
  currentSpeechModelList.value = item.speechModelList ;
}

// 把表单验证包装成返回 Promise 的函数
const validateForm = () => {
    return new Promise((resolve, reject) => {
        proxy.$refs["LlmModelRef"].validate((valid) => {
            if (valid) {
                resolve();
            } else {
                reject(new Error('表单验证未通过'));
            }
        });
    });
};

// 验证语音播放
const handleTestSpeechModel = async () => {

  // 等待表单验证通过
  await validateForm();

  // 表单验证通过后，发出语音请求
  chatLoading.value = true ;
  form.value.testChannelId = channelId.value ; 

  getSpeech(form.value).then(res => {
    const audioBlob = new Blob([res], { type: 'audio/wav' }) // 这按照自己的数据类型来写type的内容
    const audioUrl = URL.createObjectURL(audioBlob) // 生成url
    const audio = new Audio(audioUrl);

    audio.addEventListener('ended', () => {
      console.log('音频播放完成');

      chatLoading.value = false;
      isValidatedStatus.value = false
      isSpeaking.value = false 
    });

    isSpeaking.value = true 
    audio.play();

  }).catch(error => {
    chatLoading.value = false;
    console.error('操作失败:', error.message);
  }); 
};

/** 测试生成图片 */
const handleTestGenerateImageModel = async () => {

  // 等待表单验证通过
  await validateForm();

  // 表单验证通过后，发出语音请求
  chatLoading.value = true ;
  form.value.testChannelId = channelId.value ; 

  getGenerateImage(form.value).then(res => {
    const url = URL.createObjectURL(res);
    generatedImageUrl.value = url;

    chatLoading.value = false; 
    isValidatedStatus.value = false 

  }).catch(error => {
    chatLoading.value = false;
    console.error('操作失败:', error.message);
  }); 
}

// 选择发音人
const handleSelectSpeechModel = (value) => {
  console.log('item = ' + JSON.stringify(value))
  form.value.voice = null;

  const selectedSpeechItem = currentSpeechModelList.value.find(item => item.modelName === value);
  if (selectedSpeechItem) {
    console.log('选中的 speechItem 是:', selectedSpeechItem.voiceInfos);
    currentSpeechVoiceModelList.value = selectedSpeechItem.voiceInfos;
  }
}

const handleImageError = () => {
  generatedImageUrl.value = '';
  ElMessage.warning('图片加载失败，请重试');
};

/** 连接sse */
function handleSseConnect(channelId) {
  nextTick(() => {
    if (channelId) {

      let sseSource = openSseConnect(channelId);
      // 接收到数据
      sseSource.onmessage = function (event) {

        if (!event.data.includes('[DONE]')) {
          let resData = event.data;
          if (resData != 'ping') {  // 非心跳消息
            const data = JSON.parse(resData);
            pushResponseMessageList(data);

            chatLoading.value = false
            isValidatedStatus.value = false 

            if (data.hasError) {
              isValidatedStatus.value = true 
              proxy.$modal.msgError("模型测试失败，请检查模型配置是否正确");
            }

          }
        } else {
          console.log('消息接收结束.')
          if (streamLoading.value) {
            streamLoading.value.close();
          }
        }

      }
    }
  })
}

/** 消息输出 */
function pushResponseMessageList(data){

  if(data.reasoningText){
    testLlmModelReasoingReponse.value += data.reasoningText ;
    console.log('reasoningText = ' + data.reasoningText + ', testLlmModelReasoingReponse = ' + testLlmModelReasoingReponse.value)
  }

  if(data.chatText){
    testLlmModelReponse.value += data.chatText ;
    console.log('chatText = ' + data.chatText + ' , testLlmModelReponse = ' + testLlmModelReponse.value)
  }
}

// 录音识别
// 开始录音函数
const listenPlayVoiceOption = async () => {

  // 等待表单验证通过
  await validateForm();

  speechRecognitionResult.value = '' ;

  isRecording.value = !isRecording.value;
  recorder.start().then(() => {
    st.value.start = 1;
    st.value.isGo = true;
  }).catch((e) => {
    console.log('录音错误' + e);
    ElMessage.error('录音失败');
    // emit('cancel');
  });
};

// 停止录音函数
const stopRecording = () => {
  isRecording.value = false;
  st.value.start = 0;
  recorder.stop();
  recorder.stopPlay();
  send();
};

const send = async () => {
  console.log('recorder.getWAVBlob() = ' + recorder.getWAVBlob());
  console.log('stat.value = ' + stat.value);

  const blob = recorder.getWAVBlob();
  if (!blob) {
    console.error('获取的音频数据为空');
    return;
  }

  const du = "whisper.wav";
  const file = new File([blob], du, { type: "audio/wav" });

  var newbolb = new Blob([blob], { type: 'audio/wav' });
  var fileOfBlob = new File([newbolb], new Date().getTime() + '.wav');

  const micData = {
    act: "gpt.whisper",
    actData: { file: fileOfBlob, prompt: "whisper", duration: stat.value?.duration },
  };

  var formData = new FormData();

  formData.append('modelType', form.value.modelType);
  formData.append('providerCode', form.value.providerCode) ; 
  formData.append('apiUrl', form.value.apiUrl) ; 
  formData.append('apiKey', form.value.apiKey) ; 
  formData.append('model', form.value.model) ; 

  formData.append('act', "gpt.whisper");
  formData.append('prompt', "whisper");
  formData.append('duration', stat.value?.duration);
  formData.append('file', fileOfBlob);

  sendAudioFileToBackend(formData);

  stat.value = { duration: 0, fileSize: 0, vol: 0 };
};

// 发送音频文件到后端
const sendAudioFileToBackend = (audioFormData) => {
  chatLoading.value = true;
  // const response = await testRecognition(audioFormData);
  testRecognition(audioFormData).then(response => {
    chatLoading.value = false ;
    isValidatedStatus.value = false
    console.log('response = ' + JSON.stringify(response));
    speechRecognitionResult.value = response.data ;
  }).catch(error => {
    chatLoading.value = false;
    isValidatedStatus.value = true 
  }) ;
  // emits("sendAudioToBackend" , response);
};

// 模型类型改变
const handleTypeChange = (item) => {
  queryParams.value.modelType = item.code ;
  getList() ;
}

onMounted(() => {
  getList();
  handleAllModelProvidersInfo();

  // 监听sse
  channelId.value = snowflake.generate()
  handleSseConnect(channelId.value);
})


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

.dialog-footer{
  display: flex;
  gap: 10px;
  flex-direction: row;
  justify-content: flex-end;
}

.reasoning-chat-content{
  margin-top: 10px;
  line-height: 23px;
  padding: 10px;
  background: rgb(245, 245, 245);
  border-left: 2px solid #a5a5a5;
  border-radius: 5px;
  color: #a5a5a5;
}

.text-chat-content{
  margin-top:10px; 
  line-height: 23px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 2px;
}

.image-container {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 16px;
}

.image-item {
  width: 100%;
  max-height: 400px;
}

</style>