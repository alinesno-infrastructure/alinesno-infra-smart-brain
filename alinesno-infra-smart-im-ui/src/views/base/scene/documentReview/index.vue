<template>
    <div class="document-review-container">
        <el-row>
            <el-col :span="24">
                <!-- <div class="review-header-title">
                    {{ currentSceneInfo.sceneName }} 
                </div> -->
                <RoleSelectPanel 
                    :currentSeneInfo="currentSceneInfo"
                    @openExecuteHandle="openExecuteHandle"
                    ref="roleSelectPanelRef" />
                <div class="review-main-content">


                    <div class="review-header-content">
                        <div class="review-title">
                            <!-- <img class="review-agent" :src="imagePathByPath('1866758271038771201')" /> -->
                            <i class="fa-brands fa-phoenix-framework" style="color:#409EFF"></i>
                            文档审查，一键开启审查
                        </div>
                        <div class="review-description">快速识别文档潜在风险，提供专业的风险评估和修改建议</div>
                    </div>
                    <div class="review-upload-container">
                        <el-upload 
                            class="upload-demo" 
                            drag
                            :file-list="imageUrl"
                            :action="upload.url + '?sceneId=' + currentSceneInfo.id" 
                            :auto-upload="true" 
                            accept=".doc,.docx,.pdf" 
                            :on-success="handleAvatarSuccess" 
                            :before-upload="beforeAvatarUpload"
                            :headers="upload.headers" 
                            multiple>

                            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                            <div class="el-upload__text">
                                <em>点击</em>或将文档拖拽到这里上传
                            </div>

                            <template #tip>
                                <div class="el-upload__tip">
                                    单个合同文件的字数不超过10万字，格式支持：pdf/doc/docx
                                </div>
                            </template>

                        </el-upload>
                    </div>
                    <div class="review-knowledge-base" @click="handleOpenDataset()">
                        <span>
                            <i class="fa-solid fa-file-signature"></i> 文档审查知识库
                        </span>
                        <span>
                           提供自定义审核规则 <i class="fa-solid fa-right-long"></i>
                        </span>
                    </div>
                </div>
                <div class="review-footer-message">
                    服务生成的所有内容均由人工智能模型生成，其生成内容的准确性和完整性无法保证，不能代表我们的态度和观点。
                </div>
            </el-col>

            <!--
            <el-col :span="4">
                <div class="review-history-record-title">
                    <el-icon><Histogram /></el-icon>历史记录
                </div>
                <div class="review-history-record-list">
                    <div v-for="(record, index) in historyRecords" :key="index" class="review-history-record-item"
                         @mouseenter="record.hover = true" @mouseleave="record.hover = false">
                        <el-icon><Document /></el-icon>
                        <router-link :to="'/scene/documentReview/documentParser?sceneId=' + record.sceneId">
                            {{ record.title }}
                        </router-link>
                    </div>
                </div>
            </el-col>
            -->

        </el-row>

        <!-- 角色选择面板 -->
        <ExecuteHandle ref="executeHandleRef" 
            @openChatBox="openChatBox" 
            @handleGetScene="handleGetScene" />

    </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElLoading , ElMessage } from 'element-plus'
import { getRoleBySceneIdAndAgentType } from '@/api/base/im/scene';
import { getToken } from "@/utils/auth";

import ExecuteHandle from './executeHandle'
// import RoleSelectPanel from './roleSelectPanel'
import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'

import { getScene } from '@/api/base/im/scene/documentReview';
import SnowflakeId from "snowflake-id";

const snowflake = new SnowflakeId();

// 设置角色
const roleSelectPanelRef = ref(null)

const uploadLoading = ref(null);

// 角色信息
const analysisAgentEngineers = ref([]);
const logicReviewerEngineers = ref([]);

const currentSceneInfo = ref({});

const router = useRouter();
const { proxy } = getCurrentInstance();
const route = useRoute();
const isBack = ref(route.query.back || false)

const imageUrl = ref([])

const executeHandleRef = ref(null)
const sceneId = ref(route.query.sceneId)

const historyRecords = [
    { sceneId: '1' , title: '大罗市应急管理局首都核心区加油站智能视频监控和物联监测系统项目_招投标文件' },
    { sceneId: '3' , title: '广西人工智能产业发展白皮书（2024）' },
    { sceneId: '4' , title: '大罗市应急管理局首都核心区加油站智能视频监控和物联监测系统项目_招投标文件' },
    { sceneId: '3' , title: '广西人工智能产业发展白皮书（2024）' }
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
  url: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/scene/documentReview/importData",
  // 显示地址
  display: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/"
});

historyRecords.forEach(record => {
    record.hover = false;
});

/** 根据场景id和类型获取到角色信息 */
const handleRoleBySceneIdAndAgentType = async () => {
  if(currentSceneInfo.value.analysisAgentEngineer == 0 || currentSceneInfo.value.logicReviewerEngineer == 0){
    // openExecuteHandle();
    roleSelectPanelRef.value.configAgent();
  }
};

const openExecuteHandle = () => {
    executeHandleRef.value.handleOpen(currentSceneInfo.value , analysisAgentEngineers.value , logicReviewerEngineers.value);
}

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
  imageUrl.value = response.data ? response.data.split(',').map(url => { return { url: upload.display + url } }) : [];
  uploadLoading.value.close();

  // 上传成功，进入分析界面
  router.push({
      path: '/scene/documentReview/documentParser',
      query: {
          sceneId: sceneId.value , 
          channelStreamId: snowflake.generate()
      }
  })

};

/** 图片上传之前 */
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.size / 1024 / 1024 > 20) {
    ElMessage.error('Avatar picture size can not exceed 20MB!');
    return false;
  }

  uploadLoading.value = ElLoading.service({
      lock: true,
      text: '文件上传中 ...',
      background: 'rgba(0, 0, 0, 0.7)'
  });

  return true;
};

const handleOpenDataset = () => {
    ElMessage.warning('请从智能体平台后台，全局配置自定义审核清单.');
    /*
    proxy.$router.push({
        path: '/scene/documentReview/documentDataset',
        query: {
            sceneId: sceneId.value,
            channelStreamId: snowflake.generate() 
        }
    })
    */
}

const handleGetScene = () => {
    getScene(sceneId.value).then(res => {
        currentSceneInfo.value = res.data;

        if(currentSceneInfo.value.genStatus == 1 && !isBack.value){
            router.push({
                path: '/scene/documentReview/documentParser',
                query: {
                    sceneId: sceneId.value , 
                    channelStreamId: snowflake.generate()
                }
            })
            return ;
        }

        handleRoleBySceneIdAndAgentType();
    })
}

onMounted(() => {
    handleGetScene();
})

</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss';
</style>    