<template>

  <el-dialog v-model="centerDialogVisible" title="创建频道" width="30%" :append-to-body="true" align-center>

    <div style="height:481px;padding-top:20px;">
        <el-form :model="form" :rules="rules" label-position="top" ref="userRef" label-width="80px">
          <el-row>
            <el-col :span="24" class="editor-after-div">
              <el-form-item label="头像" prop="channelImage">
                  <el-upload
                    :file-list="form.channelImage"
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
          <el-row>
              <el-col :span="24">
                <el-form-item label="频道名称" prop="channelName">
                    <el-input size="large" v-model="form.channelName" placeholder="请输入频道名称" maxlength="30" />
                </el-form-item>
              </el-col>
          </el-row>
          <el-row>
              <el-col :span="24">
                <el-form-item label="备注" prop="channelDesc">
                    <el-input size="large" v-model="form.channelDesc" placeholder="请输入备注"></el-input>
                </el-form-item>
              </el-col>
          </el-row>

          <el-row>
              <el-col :span="24">
                <el-form-item label="频道类型" prop="channelType">

                  <el-radio-group v-model="form.channelType" size="large">
                    <el-radio label="org">组织频道</el-radio>
                    <el-radio label="private">个人频道</el-radio>
                  </el-radio-group>

                </el-form-item>
              </el-col>
          </el-row>

        </el-form> 
        <div class="dialog-footer">
            <el-button type="primary" size="large" bg text @click="submitChannelForm" style="float:right;margin-top:20px" icon="Link">创建频道</el-button>
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
  createChannel , 
} from '@/api/base/im/channel'

  // getRecommendChannel,
  // getDefaultChannelId

import { reactive, ref , onMounted, nextTick } from 'vue'
// import { getParam } from '@/utils/ruoyi'
import { ElLoading } from 'element-plus'

// const showCreateBox = ref(false) ; 
const centerDialogVisible = ref(false)
const router = useRouter();

const data = reactive({
  form: {
    channelType: 'org'
  },
  rules: {
    // channelImage: [{ required: true, message: "频道头像不能为空", trigger: "blur" }],
    channelName: [{ required: true, message: "频道名称不能为空", trigger: "blur" }],
    channelDesc: [{ required: true, message: "频道描述不能为空", trigger: "blur" }],
    channelType: [{ required: true, message: "频道类型不能为空", trigger: "blur" }],
  }
});

const { form, rules } = toRefs(data);

// const chatChannelTemplate = ref([]);

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
  url: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/importData" 
});

// const goBack = () => {
//   showCreateBox.value = true ;
// }

const reset = () => {
  form.value = {
    icon: null , 
    channelId: undefined,
    channelName: undefined,
    channelDesc: undefined,
  };
  proxy.resetForm("userRef");
}

/** 设置ChannelId */
function handleSetChannelId(channelId){
  router.push({
      path: '/chat',
      query: { 'channel': channelId }
  })
}

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
  // imageUrl.value = URL.createObjectURL(uploadFile.raw);
  form.value.icon = response.data ;
  console.log('form.icon = ' + form.icon);
};

/** 图片上传之前 */
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!');
    return false;
  }
  return true;
};

/** 显示图片 */
// function imagePath(row){
//   let roleAvatar = '1746435800232665090' ; 
//   if(row.icon){
//     roleAvatar = row.icon ; 
//   }
//   return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
// }

/** 获取到推荐频道列表 */
// function handleRecommendChannel(){
//   getRecommendChannel().then(response => {
//     chatChannelTemplate.value = response.data ;
//   })
// }

/** 创建频道 */
function submitChannelForm(){
  proxy.$refs["userRef"].validate(valid => {
    if (valid) {
      const loading = ElLoading.service({
        lock: true,
        text: 'Loading',
      })
      createChannel(form.value).then(response => {
        proxy.$modal.msgSuccess("新增成功");
        // loading.close();
        centerDialogVisible.value = false ;
        loading.close() ;

        reset() ;

        handleSetChannelId(response.data) ;
      });
    }
  });
}

/** 初始化 */
// onMounted(() => {

//   const channelId = getParam("channel");

//   if(channelId == undefined) {

//     const loading = ElLoading.service({
//       lock: true,
//       text: 'Loading',
//       background: 'rgba(0, 0, 0, 0.2)',
//     })

//     getDefaultChannelId().then(response => {
//       loading.close() ;
//       handleSetChannelId(response.data) ;
//     })
//   }

// })

function handleOpenChannel(val){
    centerDialogVisible.value = val;
    // nextTick(() => {
    //   handleRecommendChannel() ;
    // })
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