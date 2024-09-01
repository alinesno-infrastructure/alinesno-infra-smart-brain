<template>

  <el-dialog v-model="centerDialogVisible" title="创建频道" width="30%" align-center>

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
        从模板开始进入
      </div>
      
      <el-scrollbar height="400px">
        <ul style="margin: 0;padding: 0px;list-style: none;">
          <li v-for="(item, index) in chatChannelTemplate" :key="index">
            <div class="channel-item">

              <img class="channel-image" :src="'http://data.linesno.com/icons/sepcialist/dataset_' + (index + 5) + '.png'" />
              <!-- <img class="channel-image" :src="imagePath(item)" /> -->

              <div class="channel-text">
                #{{ item.name }}
              </div>
              <div class="channel-desc">
                {{ item.desc }}
              </div>
              <el-button type="primary" @click="handleSetChannelId(item.id)" style="position: absolute;right: 10px;" text bg>
                <i class="fa-solid fa-hammer icon-btn"></i> 创建 
              </el-button>
            </div>
          </li>
        </ul>
      </el-scrollbar>
    </div>

    <div :class="showCreateBox?'hidden-box':'show-box'" style="height:481px">
        <el-page-header @back="goBack" title="返回" style="margin-bottom: 30px;">
          <template #content>
            <span class="text-large font-600 mr-3"> 填写频道信息</span>
          </template>
        </el-page-header>
        <el-form :model="form" :rules="rules" label-position="top" ref="userRef" label-width="80px">
          <!-- <el-row>
              <el-col :span="24">
                <el-form-item label="频道图标" prop="icon">
                  <el-avatar :size="size" src="http://data.linesno.com/icons/sepcialist/dataset_55.png" />
                </el-form-item>
              </el-col>
          </el-row> -->
          <el-row>
            <el-col :span="24" class="editor-after-div">
              <el-form-item
                  label="头像"
                  :rules="[{
                      required: true,
                      message: '请上传运行效果',
                      trigger: 'blur',
                    },]"
                >
                  <el-upload
                    :file-list="fileList"
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
              <el-col :span="12" v-if="form.channelType === '9'">
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
        <!-- <el-button type="primary">加入频道</el-button> -->
      </span>
    </template>

  </el-dialog>

</template>

<script setup>

let { proxy } = getCurrentInstance();
import {getToken} from "@/utils/auth";
import Cookies from 'js-cookie'

import {
  createChannel , 
  getDefaultChannelId
} from '@/api/base/im/channel'

import { reactive, ref , onMounted } from 'vue'
import { getParam } from '@/utils/ruoyi'
import { ElLoading } from 'element-plus'

const showCreateBox = ref(true) ; 
const centerDialogVisible = ref(false)
const router = useRouter();

const data = reactive({
  form: {
    channelType: '2'
  },
  rules: {
    channelName: [{ required: true, message: "频道名称不能为空", trigger: "blur" }],
    channelDesc: [{ required: true, message: "频道描述不能为空", trigger: "blur" }],
    channelType: [{ required: true, message: "频道类型不能为空", trigger: "blur" }],
  }
});

const { form, rules } = toRefs(data);

const chatChannelTemplate = ref([
  { id: '1', name: '公共频道', desc: '这是公共讨论服务频道', icon: '' },
  { id: '2', name: '数据库设计频道', desc: '关于数据库设计的讨论', icon: '' },
  { id: '3', name: '代码模块生成频道', desc: '讨论代码模块生成相关话题', icon: '' },
  { id: '4', name: '数据分析频道', desc: '数据分析技术和方法的讨论', icon: '' },
  { id: '5', name: '考核题目设计频道', desc: '考核题目设计相关讨论', icon: '' },
  { id: '6', name: '文档生成频道', desc: '讨论文档生成工具和最佳实践', icon: '' },
]);

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

const goBack = () => {
  showCreateBox.value = true ;
}

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
  // window.location.reload();
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
function imagePath(row){
  let roleAvatar = '1746435800232665090' ; 
  if(row.icon){
    roleAvatar = row.icon ; 
  }
  return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
}

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
onMounted(() => {

  const channelId = getParam("channel");

  if(channelId == undefined) {
    // centerDialogVisible.value = true;

    const loading = ElLoading.service({
      lock: true,
      text: 'Loading',
      background: 'rgba(0, 0, 0, 0.7)',
    })

    getDefaultChannelId().then(response => {
      loading.close() ;
      handleSetChannelId(response.data) ;
    })
  }

})

function handleOpenChannel(val){
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