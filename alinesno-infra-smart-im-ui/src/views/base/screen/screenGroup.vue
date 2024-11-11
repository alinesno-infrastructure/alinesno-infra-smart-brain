<template>

  <el-dialog v-model="centerDialogVisible" title="创建场景" width="30%" :append-to-body="true" align-center>

    <div style="height:420px;padding-top:20px;">
        <el-form :model="form" :rules="rules" label-position="top" ref="userRef" label-width="80px">
          <el-row>
            <el-col :span="24" class="editor-after-div">
              <el-form-item label="封面" prop="screenBanner">
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
                    <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                  </el-upload>
                </el-form-item>
            </el-col>
          </el-row>
          <el-col :span="24">
            <el-form-item label="场景类型" prop="screenType">
              <el-radio-group v-model="form.screenType">
                <el-radio v-for="item in screenTypes" :key="item.key" :label="item.key">{{ item.name }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-row>
              <el-col :span="24">
                <el-form-item label="场景名称" prop="screenName">
                    <el-input size="large" v-model="form.screenName" placeholder="请输入场景名称" maxlength="30" />
                </el-form-item>
              </el-col>
          </el-row>
          <el-row>
              <el-col :span="24">
                <el-form-item label="备注" prop="screenDesc">
                    <el-input size="large" v-model="form.screenDesc" placeholder="请输入备注"></el-input>
                </el-form-item>
              </el-col>
          </el-row>

        </el-form> 
        <div class="dialog-footer">
            <el-button type="primary" size="large" bg text @click="submitScreenForm" style="float:right;margin-top:20px" icon="Link">创建场景</el-button>
        </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
      </span>
    </template>

  </el-dialog>

</template>

<script setup>

let { proxy } = getCurrentInstance();
import {getToken} from "@/utils/auth";

import {
  createScreen, 
} from '@/api/base/im/screen'

import { reactive, ref , onMounted, nextTick } from 'vue'
import { ElLoading } from 'element-plus'

const centerDialogVisible = ref(false)
const router = useRouter();

const imageUrl = ref([])

const screenTypes = ref([
  { key: 'large_text', name: '大文本' },
  { key: 'leader_model', name: '管理者' },
]);

const data = reactive({
  form: {
    channelType: '2'
  },
  rules: {
    screenName: [{ required: true, message: "场景名称不能为空", trigger: "blur" }],
    screenDesc: [{ required: true, message: "场景描述不能为空", trigger: "blur" }],
    screenType: [{ required: true, message: "场景类型不能为空", trigger: "blur" }],
  }
});

const { form, rules } = toRefs(data);

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

const reset = () => {
  form.value = {
    screenBanner: null , 
    channelId: undefined,
    screenName: undefined,
    screenType: undefined,
    screenDesc: undefined,
  };
  proxy.resetForm("userRef");
}

/** 设置ChannelId */
function handleSetScreenId(screenId){

  if(form.value.screenType === 'leader_model'){  // 领导模型
    router.push({
        path: '/screen/leaderModel',
        query: { 'screenId': screenId}
    })
  }else{  // 默认长文本模型
    router.push({
        path: '/screen/longText',
        query: { 'screenId': screenId}
    })
  }
}

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
  // imageUrl.value = URL.createObjectURL(uploadFile.raw);
  imageUrl.value = response.data ? response.data.split(',').map(url =>{return { url:upload.display + url }}):[];
  form.value.screenBanner = response.data ;
  console.log('form.icon = ' + form.value.screenBanner);
};

/** 图片上传之前 */
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!');
    return false;
  }
  return true;
};

/** 创建场景 */
function submitScreenForm(){
  proxy.$refs["userRef"].validate(valid => {
    if (valid) {
      const loading = ElLoading.service({
        lock: true,
        text: 'Loading',
      })
      createScreen(form.value).then(response => {
        proxy.$modal.msgSuccess("新增成功");
        // loading.close();
        centerDialogVisible.value = false ;
        loading.close() ;

        handleSetScreenId(response.data) ;
      });
    }
  });
}


function handleOpenChannel(val){
  imageUrl.value = [];
  reset();
  centerDialogVisible.value = val;
}

defineExpose({
  handleOpenChannel,
})

</script>

<style lang="scss" scoped>
.channel-box-panel {

  .channel-item {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    padding: 10px;
    position: relative;
    background: #fafafa;
    border-radius: 5px;
  }

  .channel-image {
    width: 35px;
    height: 35px;
    border-radius: 50%;
    margin-right: 10px;
  }

  .channel-text {
    font-size: 14px;
    font-weight: bold;
    color: #333;
  }

  .channel-desc {
    margin-left: 10px;
    font-size: 13px;
  }

  .icon-btn{
    margin-right: 10px;
  }

}

.hidden-box {
  display: none;
}

.show-box {
  display: block;
}

</style>