<template>

  <el-dialog v-model="centerDialogVisible" title="创建频道" width="30%" :append-to-body="true" align-center>

    <!--
    <div class="channel-box-panel" :class="showCreateBox?'show-box':'hidden-box'">

      <ul style="margin: 0;padding: 0px;list-style: none;">
        <li>
          <div class="channel-item">
            <img class="channel-image" src="http://data.linesno.com/icons/sepcialist/dataset_55.png" />
            <div class="channel-text">
              创建自己的频道
            </div>
            <el-button type="danger" style="position: absolute;right: 10px;" text bg @click="showCreateBox = false">
              <i class="fa-solid fa-feather icon-btn"></i> 创建 
            </el-button>
          </div>
        </li>
      </ul>

      <div style="font-size: 13px;margin-bottom: 10px;width: 100%;">
        进入推荐频道 
      </div>
      
      <el-scrollbar height="400px">
        <ul style="margin: 0;padding: 0px;list-style: none;">
          <li v-for="(item, index) in chatChannelTemplate" :key="index">
            <div class="channel-item">

              <img class="channel-image" :src="imagePath(item)" />

              <div class="channel-text">
                #{{ item.channelName }}
              </div>
              <div class="channel-desc">
                {{ truncateString(item.channelDesc,15) }}
              </div>
              <el-button type="primary" @click="handleSetChannelId(item.id)" style="position: absolute;right: 10px;" text bg>
                <i class="fa-solid fa-hammer icon-btn"></i> 进入
              </el-button>
            </div>
          </li>
        </ul>
      </el-scrollbar>
    </div>
    -->

    <div style="height:481px;padding-top:20px;">
        <!-- <el-page-header title="" style="margin-bottom: 30px;">
          <template #content>
            <span class="text-large font-600 mr-3"> 填写频道信息</span>
          </template>
        </el-page-header> -->
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
              <el-col :span="12">
                <el-form-item label="频道类型" prop="channelType">
                  <el-select size="large" v-model="form.channelType" placeholder="选择频道类型">
                    <el-option label="公共频道" value="9" />
                    <el-option label="个人频道" value="2" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="1">
              </el-col>
              <el-col :span="11" v-if="form.channelType === '9'">
                <el-form-item label="进入密钥" prop="channelKey">
                    <el-input size="large" v-model="form.channelKey" placeholder="请输入进入密钥" maxlength="30" />
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
    channelType: '2'
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
//       background: 'rgba(0, 0, 0, 0.7)',
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