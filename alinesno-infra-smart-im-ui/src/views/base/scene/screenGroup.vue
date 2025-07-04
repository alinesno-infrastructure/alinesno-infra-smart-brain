<template>
  <!-- 创建多个企业场景 -->
  <el-dialog v-model="centerDialogVisible" title="选择场景" width="1200" :append-to-body="true" align-center>
    <div class="dialog-content">

      <el-collapse-transition>

        <div v-if="currentStep == 1">

          <div class="dialog-tip">
            请选择合适的场景，结合多智能体(Agent)能力创建场景。
          </div>
          <div class="scene-list">
            <div v-for="item in sceneList" :key="item.id"
              :class="{ 'box-card': true, 'selected': selectedId === item.id }" @mouseenter="handleMouseEnter(item.id)"
              @mouseleave="handleMouseLeave(item.id)" @click="handleItemClick(item)">

              <div class="scene-item">
                <div class="scene-banner">
                  <i :class="item.icon" />
                </div>
                <div class="scene-info">
                  <div class="scene-title">
                    <span>{{ item.sceneName }}</span>
                    <span v-if="item.isMature === '0'" style="margin-left:10px;">
                      <el-tag type="info">测试中</el-tag>
                    </span>
                  </div>
                  <div class="scene-description">
                    {{ item.sceneDescription }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div style="width: 700px;margin: auto;" v-if="currentStep == 2">

          <div class="dialog-tip">
            请给场景确认名称，便于后续使用。
          </div>
          <br/>

          <el-form :model="form" :rules="rules" label-position="top" ref="userRef" label-width="80px">
            <el-row>
              <el-col :span="24" class="editor-after-div">
                <el-form-item label="封面" prop="sceneBanner">
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
            <!-- 
            <el-col :span="24">
              <el-form-item label="场景类型" prop="sceneType">
                <el-radio-group v-model="form.sceneType">
                  <el-radio v-for="item in sceneTypes" :disabled="!item.isAvailable" :key="item.key" :label="item.key">{{ item.name }}</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col> 
            -->
            <el-row>
              <el-col :span="24">
                <el-form-item label="场景名称" prop="sceneName">
                  <el-input size="large" v-model="form.sceneName" placeholder="请输入场景名称" maxlength="30" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="备注" prop="sceneDesc">
                  <el-input size="large" v-model="form.sceneDesc" placeholder="请输入备注"></el-input>
                </el-form-item>
              </el-col>
            </el-row>

          </el-form>
        </div>

      </el-collapse-transition>

      <div class="dialog-footer">
        <el-button type="primary" size="large" v-if="currentStep == 1" bg text @click="nextStep(2)" icon="Right">下一步</el-button>
        <el-button type="primary" size="large" v-if="currentStep == 2" bg text @click="nextStep(1)" icon="Back">上一步</el-button>

        <el-button :disabled="currentStep == 1" type="primary" size="large" bg text @click="submitSceneForm" icon="Position">创建场景</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { getToken } from "@/utils/auth";

import {
  createScene,
  supportAllScene
} from '@/api/base/im/scene'

import { reactive, ref, onMounted, nextTick } from 'vue'
import { ElLoading } from 'element-plus'

const { proxy } = getCurrentInstance();

const centerDialogVisible = ref(false);
const router = useRouter();
const selectedId = ref(null);
const selectedCode = ref(null);
const hoveredId = ref(null);

const sceneList = ref([]);
const imageUrl = ref([])

const currentStep = ref(1)

const data = reactive({
  form: {
    channelType: '2'
  },
  rules: {
    sceneName: [{ required: true, message: "场景名称不能为空", trigger: "blur" }],
    sceneDesc: [{ required: true, message: "场景描述不能为空", trigger: "blur" }],
    sceneType: [{ required: true, message: "场景类型不能为空", trigger: "blur" }],
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
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/importData",
  // 显示地址
  display: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/"
});

const handleOpenChannel = (val) => {
  centerDialogVisible.value = val;

  reset() ; 

  supportAllScene().then(res => {
    console.log('res = ' + res);
    sceneList.value = res.data;
  })
};

const reset = () => {
  imageUrl.value = [];
  currentStep.value = 1;
  selectedId.value = null ;
  form.value = {
    channelType: '2'
  };
};

const handleMouseEnter = (id) => {
  hoveredId.value = id;
};

const nextStep = (val) => {
  currentStep.value = val;
};

const handleMouseLeave = () => {
  hoveredId.value = null;
};

const handleItemClick = (item) => {

  if(item.isMature === '0'){
    proxy.$modal.msgError("该场景为还在开发测试中，暂不支持使用");
    return;
  }

  selectedId.value = item.id;
  selectedCode.value = item.code;
};

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
  // imageUrl.value = URL.createObjectURL(uploadFile.raw);
  imageUrl.value = response.data ? response.data.split(',').map(url => { return { url: upload.display + url } }) : [];
  form.value.sceneBanner = response.data;
  console.log('form.icon = ' + form.value.sceneBanner);
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
function submitSceneForm() {
  proxy.$refs["userRef"].validate(valid => {
    if (valid) {
      const loading = ElLoading.service({
        lock: true,
        text: 'Loading',
        background: 'rgba(0, 0, 0, 0.1)',
      })

      form.value.sceneType = selectedCode.value;
      form.value.sceneScope = 'org';

      createScene(form.value).then(response => {
        proxy.$modal.msgSuccess("新增成功");
        centerDialogVisible.value = false;
        loading.close();

        handleSetSceneId(response.data);
      }).catch(error => {
        loading.close();
      }) ;
    }
  });
}

/** 设置ChannelId */
function handleSetSceneId(sceneId){
  console.log('sceneId = ' + sceneId)
  const pathScene = '/scene/'+selectedCode.value+'/index';
  console.log('path = ' + pathScene)

  router.push({
      path: pathScene , 
      query: { 'sceneId': sceneId}
  })
}

defineExpose({
  handleOpenChannel,
  submitSceneForm
});
</script>

<style lang="scss" scoped>
.dialog-content {
  .dialog-tip {
    margin-bottom: 10px;
    font-size: 16px;
  }

  .scene-list {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 10px;
    width: 100%;
    margin-top: 20px;
    margin-bottom: 20px;

    .box-card {
      width: calc(33% - 10px);
      border-radius: 10px;
      transition: box-shadow 0.3s ease;
      border: 2px solid #f5f5f5;

      &:hover {
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
      }

      &.selected {
        border: 2px solid #409eff;
      }

      .scene-item {
        padding: 15px;
        background: #f5f5f5;
        border-radius: 5px;
        gap: 10px;
        text-align: left;
        display: flex;
        align-items: center;
        cursor: pointer;
        flex-direction: row;
        justify-content: start;
        border-radius: 10px;

        .scene-banner {
          i {
            font-size: 30px;
            color: #d33233;
          }
        }

        .scene-info {
          display: flex;
          flex-direction: column;
          gap: 8px;

          .scene-title {
            font-size: 18px;
            font-weight: bold;
            color: #333 ;
          }

          .scene-description {
            height: 40px;
          }
        }
      }
    }
  }

  .dialog-footer {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    margin-bottom: 20px;
    margin-right: 25px;
    margin-top: 30px;
  }
}
</style>