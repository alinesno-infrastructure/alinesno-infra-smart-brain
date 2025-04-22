<template>
  <div class="app-container">
    <el-page-header @back="goBack" class="review-page-header">
        <template #content>
            <span class="text-large font-600 mr-3"> 新增模板</span>
        </template>
    </el-page-header>

    <div style="max-width:90%; margin:auto; margin-top: 20px;">
        <el-row :gutter="20">
            <el-col :span="24">
      <el-form ref="databaseRef" :model="form" :rules="rules" :label-position="'top'" label-width="80px">
          <el-row>
            <el-col :span="14">
                <el-form-item label="模板名称" prop="templateName">
                    <el-input type="input" v-model="form.templateName" size="large" placeholder="请输入模板名称" />
                </el-form-item>
            </el-col>
            <el-col :span="10">
                <el-form-item style="float: right;" label="解析引擎" prop="templateEngine">
                    <el-radio-group v-model="form.templateEngine" size="large">
                    <el-radio v-for="(item, index) in templateEngineOptions" 
                        :key="index"
                        :value="item.key"
                        :label="item.key">
                        {{ item.label }}
                    </el-radio>
                    </el-radio-group>
                </el-form-item>
                </el-col>
            </el-row>

         <el-form-item label="模板类型" prop="templateType">
            <el-radio-group v-model="form.templateType" size="large">
                  <el-radio v-for="item in templateTypeOptions" 
                     style="margin-top: 10px;margin-bottom: 10px;" 
                     :key="item.id" 
                     :value="item.id"
                     :label="item.id" size="large">
                     <div style="padding:10px; display: flex;flex-direction: column;line-height: 1.5rem;">
                     <span style="font-size:15px;font-weight: bold;">
                        <i :class="item.icon"></i> {{ item.name }}
                     </span>
                     <span style="color:#a5a5a5">
                        {{ truncateString(item.desc , 10) }}
                     </span>
                     </div>
                  </el-radio>
            </el-radio-group>
         </el-form-item>

          <el-row>
            <el-col :span="24">
               <el-form-item label="数据范围" prop="templateDataScope">
               <el-radio-group v-model="form.templateDataScope" :disabled="form.id">
                  <el-radio v-for="item in templateDataScopesArr" 
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

         <el-form-item label="模板内容" prop="templateContent">
            <el-upload v-model="form.templateContent" ref="uploadRef" 
               :limit="1" 
               accept=".ppt,.docx,.doc,.xml" 
               style="width:100%" 
               :headers="upload.headers"
               :action="upload.url + '?templateName=' + form.templateName + '&templateDataScope=' + form.templateDataScope + '&sceneId=' + sceneId + '&templateEngine=' + form.templateEngine"
               :disabled="upload.isUploading" 
               :on-progress="handleFileUploadProgress" 
               :on-success="handleFileSuccess"
               :auto-upload="false" 
               drag>
               <el-icon class="el-icon--upload">
                  <upload-filled />
               </el-icon>
               <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
               <template #tip>
                  <div class="el-upload__tip text-center">
                     <span>支持 .xml, .doc, .docx 文件，导入文件不能超过10M</span>
                  </div>
               </template>
            </el-upload>
         </el-form-item>



      </el-form>
            <template #footer>
            <div class="dialog-footer">
               <el-button type="primary" :loading="upload.isUploading" size="large" @click="submitFileForm">上 传</el-button>
               <el-button size="large" @click="upload.open = false">取 消</el-button>
            </div>
            </template>
            </el-col>
        </el-row>
    </div>

  </div>
</template>

<script setup name="Index">

import {
   listTemplate,
   delTemplate,
   getTemplate,
   updateTemplate,
   catalogTreeSelect,
   getTemplateType,
   addTemplate
} from "@/api/smart/assistant/template";

import { copyClick } from '@/utils/clipboard.js'
import { getToken } from "@/utils/auth";

// import TemplateCardPanel from './templateCardPanel.vue'
// import ConfigParamFormatPanel from './configParamFormat.vue'
import { nextTick } from "vue";

const router = useRouter();
const { proxy } = getCurrentInstance();

const templateDataScopesArr = [
    { "id": "public", "name": "公开工作台", "icon": "fa-solid fa-globe" , "desc":"模板对外所有人可用" },
    { "id": "org", "name": "组织工作台", "icon": "fa-solid fa-truck-plane" , "desc":"只对组织内的成员可用" }
];

// 解析方式(简单/复杂)
const templateEngineOptions = [
   { key: "simple", label: "简单" },
   { key: "complex", label: "复杂" }
];

const templateTypeOptions = ref([]);

const databaseRef = ref(null);

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
  url:
    import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/template/importData",
})

const data = reactive({
   form: {
      templateDataScope: 'org' ,
      templateEngine: 'simple'
   },
   queryParams: {
      pageNum: 1,
      pageSize: 10,
      templateName: undefined,
      promptDesc: undefined,
      catalogId: undefined
   },
   rules: {
      templateName: [{ required: true, message: "名称不能为空", trigger: "blur" }],
      templateEngine: [{ required: true, message: "解析方式不能为空", trigger: "blur" }]
   }
});

const { queryParams, form, rules } = toRefs(data);

const handleGetTemplateType = () => {
   getTemplateType().then(response => {
      templateTypeOptions.value = response.data;
   });
};

handleGetTemplateType();

</script>

<style lang="scss" scoped>
// 自定义新式 
</style>