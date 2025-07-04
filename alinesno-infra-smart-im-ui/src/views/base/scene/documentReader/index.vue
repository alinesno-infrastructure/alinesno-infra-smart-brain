<template>

    <div class="ppt-pager-container">

        <el-container style="height:calc(100vh - 40px );background-color: #fff;">

            <el-aside width="80px" class="ppt-pager-aside">
                <FunctionList />
            </el-aside>

            <el-main class="ppt-pager-main">

                <div class="document-review-container">
                    <el-row>
                        <el-col :span="24">
                            <div class="review-header-title">
                                <!-- {{ currentSceneInfo.sceneName }}  -->
                                <RoleSelectPanel :currentSeneInfo="currentSceneInfo"
                                    @openExecuteHandle="openExecuteHandle" ref="roleSelectPanelRef" />
                            </div>
                            <div class="review-main-content">


                                <div class="review-header-content">
                                    <div class="review-title">
                                        <!-- <img class="review-agent" :src="imagePathByPath('1866758271038771201')" /> -->
                                        <i class="fa-solid fa-book-open-reader" style="color:#409EFF"></i>
                                        企业文档，一键上传分析阅读
                                    </div>
                                    <div class="review-description">可以辅助您阅读起标书、答辩状、笔录等各类型的文档</div>
                                </div>
                                <div class="review-upload-container">
                                    <el-upload class="upload-demo" drag :file-list="imageUrl"
                                        :action="upload.url + '?sceneId=' + currentSceneInfo.id" :auto-upload="true"
                                        accept=".doc,.docx" :on-success="handleAvatarSuccess"
                                        :before-upload="beforeAvatarUpload" :headers="upload.headers" multiple>

                                        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                                        <div class="el-upload__text">
                                            <em>点击</em>或将文档拖拽到这里上传
                                        </div>

                                        <template #tip>
                                            <div class="el-upload__tip">
                                                单个合同文件的字数不超过10万字，格式支持：doc/docx/pdf/excel等
                                            </div>
                                        </template>

                                    </el-upload>
                                </div>

                                <!--
                    <div class="review-knowledge-base" @click="handleOpenDataset()">
                        <span>
                            <i class="fa-solid fa-file-signature"></i> 文档审查知识库
                        </span>
                        <i class="fa-solid fa-right-long"></i>
                    </div>
                    -->

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
                    <ExecuteHandle ref="executeHandleRef" @openChatBox="openChatBox" @handleGetScene="handleGetScene" />

                </div>
            </el-main>
        </el-container>

        <!-- 运行抽屉 -->
        <div class="aip-flow-drawer flow-control-panel">
            <el-drawer v-model="showDebugRunDialog" :modal="false" size="40%" style="max-width: 700px;" title="预览与调试"
                :with-header="true">
                <div style="margin-top: 0px;">
                    <RoleChatPanel ref="roleChatPanelRef" />
                </div>
            </el-drawer>
        </div>

    </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElLoading, ElMessage } from 'element-plus'
import { getRoleBySceneIdAndAgentType } from '@/api/base/im/scene';
import { getToken } from "@/utils/auth";

import ExecuteHandle from './executeHandle'
import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'
import RoleChatPanel from '@/views/base/scene/common/chatPanel';
import FunctionList from './functionList'

// 设置角色
const roleSelectPanelRef = ref(null)

const uploadLoading = ref(null);

// 角色信息
const summaryAgentEngineers = ref([]);
const caseQueryAgentEngineers = ref([]);

const currentSceneInfo = ref({});

const route = useRoute();
const router = useRouter();
const { proxy } = getCurrentInstance();

import { getScene } from '@/api/base/im/scene/documentReader';

const imageUrl = ref([])

const executeHandleRef = ref(null)
const sceneId = ref(route.query.sceneId)

// const historyRecords = [
//     { sceneId: '1' , title: '大罗市应急管理局首都核心区加油站智能视频监控和物联监测系统项目_招投标文件' },
//     { sceneId: '3' , title: '广西人工智能产业发展白皮书（2024）' },
//     { sceneId: '4' , title: '大罗市应急管理局首都核心区加油站智能视频监控和物联监测系统项目_招投标文件' },
//     { sceneId: '3' , title: '广西人工智能产业发展白皮书（2024）' }
// ];

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
    url: import.meta.env.VITE_APP_BASE_API + "/api/infra/smart/assistant/scene/documentReader/importData",
    // 显示地址
    display: import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/"
});

// historyRecords.forEach(record => {
//     record.hover = false;
// });

/** 根据场景id和类型获取到角色信息 */
const handleRoleBySceneIdAndAgentType = async () => {
    console.log('sceneId = ' + currentSceneInfo.value.sceneId + ' , agents = ' + currentSceneInfo.value.agents);

    if (currentSceneInfo.value.sceneId && currentSceneInfo.value.agents) {
        for (let i = 0; i < currentSceneInfo.value.agents.length; i++) {
            let item = currentSceneInfo.value.agents[i];

            if (item.code === 'summaryAgent') {
                const response = await getRoleBySceneIdAndAgentType(currentSceneInfo.value.sceneId, item.id);
                summaryAgentEngineers.value = response.data;
            } else if (item.code === 'caseQueryAgent') {
                const response = await getRoleBySceneIdAndAgentType(currentSceneInfo.value.sceneId, item.id);
                caseQueryAgentEngineers.value = response.data;
            }

        }
    }

    if (currentSceneInfo.value.analysisAgentEngineer == 0 || currentSceneInfo.value.logicReviewerEngineer == 0) {
        openExecuteHandle();
    }

};

const openExecuteHandle = () => {
    executeHandleRef.value.handleOpen(currentSceneInfo.value, summaryAgentEngineers.value, caseQueryAgentEngineers.value);
}

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
    // imageUrl.value = URL.createObjectURL(uploadFile.raw);
    imageUrl.value = response.data ? response.data.split(',').map(url => { return { url: upload.display + url } }) : [];

    //   form.value.sceneBanner = response.data;
    //   console.log('form.icon = ' + form.value.sceneBanner);

    uploadLoading.value.close();

    // 上传成功，进入分析界面
    router.push({
        path: '/scene/documentReader/documentParser',
        query: {
            sceneId: sceneId.value
        }
    })

};

/** 图片上传之前 */
const beforeAvatarUpload = (rawFile) => {
    if (rawFile.size / 1024 / 1024 > 2) {
        ElMessage.error('Avatar picture size can not exceed 2MB!');
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
    proxy.$router.push({
        path: '/scene/documentReview/documentDataset',
        query: {
            sceneId: sceneId.value
        }
    })
}

const handleGetScene = () => {
    getScene(sceneId.value).then(res => {
        currentSceneInfo.value = res.data;
        handleRoleBySceneIdAndAgentType();
    })
}

onMounted(() => {
    handleGetScene();
})

</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss';

.ppt-pager-container {
  background: #fff;
  height: calc(100vh - 60px);

  .review-footer {
    padding: 10px;
    font-size: 14px;
    background: #fafafa;
    border-radius: 8px;
    text-align: left;
    color: #555;
    margin-top: 10px;
}

  .main-content {
    display: flex;
    flex-direction: column;
    padding-top: calc(1vh);
    margin: auto;
    padding-left: 20px;
    padding-right: 20px;

    .example-result-section {
      padding: 12px;
      border-radius: 10px;
      font-size: 14px;
      text-align: left;
      color: #585a73;
      display: flex;
      flex-direction: row;
      width: 100%;
      align-items: center;
      gap: 10px;
    }

    .title-section {
      display: flex;
      flex-direction: column;
      text-align: center;
      align-items: flex-start;

      .title {
        color: #2C2C36;
        font-weight: 600;
        font-size: 28px;
        margin-bottom: 10px;
        line-height: 40px;
      }

      .description {
        margin-top: 10px;
        color: #8F91A8;
        font-weight: 400;
        font-size: 16px;
        line-height: 24px;
      }
    }

    .input-button-section {
      display: flex;
      gap: 10px;
      position: relative;
      box-sizing: border-box;
      width: 100%;
      border-radius: 15px;
      transition: 0.3s;
      background: rgb(255, 255, 255);
      padding: 10px !important;
      border: 1px solid rgb(232, 234, 242);
      margin-top: 30px;
      margin-bottom: 10px;
      align-items: flex-start;
      flex-direction: column;

      .input-box {
        width: 100%;
        height: 50px;
        border: 0px !important;
        margin-bottom: 0px;
      }
    }

  }

  .review-footer-message {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 10px;
    height: 36px;
    padding: 12px 0px;
    text-align: center;

    .footer-message {
      margin-bottom: 4px;
      color: #C8CAD9;
      font-size: 12px;
      line-height: 12px;
    }
  }

  .review-question-preview-title {
    padding: 10px;
    text-align: left;
    font-weight: bold;
    margin-left: 20px;
    margin-right: 10px;
    margin-bottom: 10px;
    border-radius: 10px;
    background: #fafafa;
    color: #444;
    font-size: 15px;
    display: flex;
    align-content: center;
    align-items: center;
    justify-content: space-between;
  }

  .review-question-preview {
    height: calc(100vh - 170px);
    margin-left: 20px;
    border-radius: 8px;
    background: #fafafa;
    border: 1px solid #e8eaf2;
    overflow: hidden;
  }

}

.ppt-pager-aside{
  padding: 0px;
  border-right: 1px solid #f2f3f7;
  background: #fff;
  margin-bottom: 0px;
}

.ppt-pager-main{
  padding: 0px !important;
}

.pager-gen-result-panel{
  margin-bottom:20px;
  text-align: left;

  .pager-container {
    background-color: #fafafa;
    margin: 10px 10px;
    margin-right: 0px;
    border-radius: 8px;
    height: calc(100vh - 190px);
    padding: 10px;
    padding-left: 10px;
    margin-left: 20px;
    margin-bottom: 0px;
  }
}
</style>


<style>
.ppt-pager-container .input-button-section .el-input__wrapper {
  box-shadow: none !important;
  padding: 5px;
  margin-left: 0px;
}
</style>