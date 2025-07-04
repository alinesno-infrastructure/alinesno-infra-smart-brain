<template>
  <div class="app-container">
    <div class="label-title">
      <div class="tip">智能体平台全局配置</div>
      <div class="sub-tip">针对企业和团队在运营管理、业务拓展等方面的差异化需求，打造契合自身发展的专属平台环境。</div>
    </div>
    <div class="form-container">
      <el-form
        :model="form"
        :rules="rules"
        v-loading="loading"
        ref="formRef"
        size="large"
        label-width="180px"
        class="demo-form">
        <el-form-item label="产品名称" prop="productName">
          <el-row style="width:100%">
            <el-col :span="20">
              <el-input
                type="input"
                maxlength="50"
                size="large"
                show-word-limit
                v-model="form.productName"
                placeholder="请输入产品名称"
              ></el-input>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="界面主题选择" prop="themeStyle">
          <el-space wrap>
            <el-card
              v-for="(option, index) in themeStyleOptions"
              style="width:270px"
              :key="index"
              :offset="index > 0? 1 : 0"
              :body-style="{ padding: '0px !important' }"
              :class="form.themeStyle === option.code? 'select-card' : ''"
              shadow="never">
              <img :src="getAipStyle(option.code)" class="image">
              <div style="padding: 14px;line-height: 1.2rem;">
                <span>{{ option.desc }}</span>
                <div class="bottom clearfix">
                  <el-button @click="selectStyle(option)" type="text" size="default" class="button">选择</el-button>
                </div>
              </div>
            </el-card>
          </el-space>
        </el-form-item>
        <el-row>
          <el-col :span="24" class="editor-after-div">
            <el-form-item label="团队logo" prop="logoImg">
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
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="是否显示服务" prop="displayService">
          <el-radio-group v-model="form.displayService">
            <el-radio value="none">不显示</el-radio>
            <el-radio value="display">显示</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="工作台地址" prop="studioUrl">
          <el-row style="width:100%">
            <el-col :span="20">
              <el-input
                type="input"
                maxlength="250"
                size="large"
                show-word-limit
                v-model="form.studioUrl"
                placeholder="请输入产品名称"
              ></el-input>
            </el-col>
          </el-row>
        </el-form-item>

        <br />
        <el-form-item>
          <el-button type="primary" @click="submitForm('form')">
            保存
          </el-button>
          <el-button @click="resetForm">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 配置默认大模型Prompt的对话框 -->
    <el-dialog
      v-model="largeModelDialogVisible"
      title="配置内容生成指令"
      width="600"
      :before-close="handleLargeModelDialogClose">
      <el-input
        v-model="largeModelDefaultPrompt"
        type="textarea"
        :rows="15"
        placeholder="请输入Prompt内容"></el-input>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="largeModelDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleLargeModelDialogConfirm">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 配置默认图片模型Prompt的对话框 -->
    <el-dialog
      v-model="imageModelDialogVisible"
      title="配置图片生成指令"
      width="600"
      :before-close="handleImageModelDialogClose">
      <el-input
        v-model="imageModelDefaultPrompt"
        type="textarea"
        :rows="15"
        placeholder="请输入Prompt内容"></el-input>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="imageModelDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleImageModelDialogConfirm">
           确认 
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { getToken } from "@/utils/auth";
import { ElMessage, ElDialog } from 'element-plus';

import { getAipStyle } from "@/utils/llmIcons"
import { listAllLlmModel } from "@/api/smart/assistant/llmModel";
import { getByOrg , addGlobalConfig } from "@/api/smart/assistant/globalConfig";

// 定义默认的大模型Prompt
const DEFAULT_LARGE_MODEL_PROMPT = `请根据用户输入的主题，
生成一段详细且有价值的文本内容，
内容应包含相关的背景知识和实用建议。`;
// 定义默认的图片模型Prompt
const DEFAULT_IMAGE_MODEL_PROMPT = `根据用户输入的描述，
生成一张清晰、美观且符合主题的图片，
图片应包含关键元素和细节。`;

const imageUrl = ref([]);

const themeStyleOptions = [
  {
    code: 'dark',
    icon: 'asserts/images/dark-style.png',
    desc: '深色主题，提供更具沉浸感的视觉体验'
  },
  {
    code: 'light',
    icon: 'asserts/images/light-style.png',
    desc: '浅色主题，带来清晰明亮的界面风格'
  }
];

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
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/importData",
  // 显示地址
  display: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/"
});

const form = ref({
  themeStyle: 'light',
  logoImg: '',
  productName: '',
  defaultLargeModel: '',
  defaultImageModel: '',
  maxChannelPeople: 0,
  displayService: 'none' , 
  studioUrl: 'http://alinesno-infra-plat-console-ui.beta.base.infra.linesno.com'
});

const rules = ref({
  productName: [
    { required: true, message: '请输入产品名称', trigger: 'blur' },
    { min: 1, max: 500, message: '产品名称长度在1到500之间', trigger: 'blur' }
  ],
  logoImg: [
    { required: true, message: '请输入团队logo', trigger: 'blur' },
    { min: 1, max: 500, message: '团队logo长度在1到500之间', trigger: 'blur' }
  ],
  defaultLargeModel: [
    { required: true, message: '请选择默认大模型', trigger: 'blur' }
  ],
  defaultImageModel: [
    { required: true, message: '请选择默认图片模型', trigger: 'blur' }
  ],
  studioUrl: [
    { required: true, message: '请输入工作台地址', trigger: 'blur' },
    { min: 1, max: 500, message: '工作台地址长度在1到500之间', trigger: 'blur' }
  ],
  maxChannelPeople: [
    { required: true, message: '请输入频道最多人数', trigger: 'blur' },
    { type: 'number', message: '请输入正确的数字', trigger: 'blur' },
  ]
});

// const currentSiteId = ref(null);

const loading = ref(false);
const formRef = ref(null);
const llmModelOptions = ref([]);
const imageModelOptions = ref([]);

/** 列出所有模型列表 */
const handleListAllLlmModel = async () => {
  try {
    const res = await listAllLlmModel();
    llmModelOptions.value = res.data.filter(item => item.modelType === 'large_language_model');
    imageModelOptions.value = res.data.filter(item => item.modelType === 'image_generation');

    // 获取组织信息
    handleGetByOrg();
   
  } catch (error) {
    console.error('获取模型列表失败', error);
  }
};

const handleGetByOrg = async () => {
  // 获取到配置
  const configRes = await getByOrg();
  if (configRes.data) {
    form.value = configRes.data;

    // 如果存在logoImg，而且判断是否以http开头
    if (configRes.data.logoImg && configRes.data.logoImg.startsWith('http')) {
      imageUrl.value = configRes.data.logoImg? [{ url : configRes.data.logoImg }] : [];
    }else{
      imageUrl.value = configRes.data.logoImg? [{ url : upload.display + configRes.data.logoImg }] : [];
    }

  }
};

onMounted(() => {
  handleListAllLlmModel();
});

const selectStyle = (option) => {
  form.value.themeStyle = option.code;
};

const submitForm = (formName) => {
  formRef.value.validate((valid) => {
    if (valid) {
      loading.value = true;

      form.value.llmPrompt = DEFAULT_LARGE_MODEL_PROMPT
      form.value.imagePrompt = DEFAULT_IMAGE_MODEL_PROMPT

      console.log(JSON.stringify(form.value))

      addGlobalConfig(form.value).then(response => {
        console.log('response = ' + response);
        ElMessage.success('保存成功');
        // 获取组织信息
        handleGetByOrg();
      });

   
      // 假设这里有保存逻辑
      loading.value = false;
    }
  });
};

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
  imageUrl.value = response.data? response.data.split(',').map(url => { return { url: upload.display + url } }) : [];
  form.value.logoImg = response.data;
  console.log('form.logoImg ='+ form.value.logoImg);
};

/** 图片上传之前 */
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!');
    return false;
  }
  return true;
};

const resetForm = () => {
  formRef.value.resetFields();
  // 假设这里有重新获取设置的逻辑
};

// 控制配置默认大模型Prompt对话框的显示状态
const largeModelDialogVisible = ref(false);
const largeModelDefaultPrompt = ref(DEFAULT_LARGE_MODEL_PROMPT);

const handleLargeModelDialogClose = () => {
  largeModelDialogVisible.value = false;
};
// 配置默认大模型Prompt对话框确定按钮的处理函数
const handleLargeModelDialogConfirm = () => {
  // 这里可以添加点击确定按钮后的逻辑，比如保存Prompt
  console.log('大模型的Prompt内容:', largeModelDefaultPrompt.value);
  largeModelDialogVisible.value = false;
};
// 配置默认大模型的Prompt
const selectDefaultLargeModel = () => {
  largeModelDialogVisible.value = true;
};

// 控制配置默认图片模型Prompt对话框的显示状态
const imageModelDialogVisible = ref(false);
// 默认图片模型的Prompt内容，使用定义好的常量初始化
const imageModelDefaultPrompt = ref(DEFAULT_IMAGE_MODEL_PROMPT);
// 关闭配置默认图片模型Prompt对话框的处理函数
const handleImageModelDialogClose = () => {
  // 这里可以添加关闭对话框时的额外逻辑
  imageModelDialogVisible.value = false;
};
// 配置默认图片模型Prompt对话框确定按钮的处理函数
const handleImageModelDialogConfirm = () => {
  // 这里可以添加点击确定按钮后的逻辑，比如保存Prompt
  console.log('图片模型的Prompt内容:', imageModelDefaultPrompt.value);
  imageModelDialogVisible.value = false;
};
// 配置默认图片模型的Prompt
const selectDefaultImageModel = () => {
  imageModelDialogVisible.value = true;
};
</script>

<style scoped lang="scss">
.app-container {

 .label-title {
    text-align: center;
    max-width: 960px;
    margin: 10px auto 0;

   .tip {
      padding-bottom: 10px;
      font-size: 26px;
      font-weight: bold;
    }

   .sub-tip {
      font-size: 13px;
      text-align: center;
      padding: 10px;
    }
  }

 .form-container {
    max-width: 960px;
    margin: 20px auto 0;

   .demo-form {
     .image {
        width: 100%;
        height: 130px;
      }

     .select-card {
        border: 1px solid rgb(0, 91, 212);
      }
    }

   .default-model-config {
      margin-top: 20px;
    }
  }
}
</style>