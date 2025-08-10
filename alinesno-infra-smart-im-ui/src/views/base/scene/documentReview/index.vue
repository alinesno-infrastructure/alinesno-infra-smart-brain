<template>

    <DocumentReviewContainer>

                <div class="document-review-container">
                    <el-row>
                        <el-col :span="24">
                            <RoleSelectPanel :currentSeneInfo="currentSceneInfo" @openExecuteHandle="openExecuteHandle"
                                ref="roleSelectPanelRef" />
                            <div class="review-main-content">


                                <div class="review-header-content">
                                    <div class="review-title">
                                        <i class="fa-brands fa-phoenix-framework" style="color:#409EFF"></i>
                                        文档审查，一键开启审查
                                    </div>
                                    <div class="review-description">快速识别文档潜在风险，提供专业的风险评估和修改建议</div>
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

                    </el-row>

                    <!-- 角色选择面板 -->
                    <ExecuteHandle ref="executeHandleRef" @openChatBox="openChatBox" @handleGetScene="handleGetScene" />

                </div> 

                <!-- AI生成状态 -->
                <AIGeneratingStatus 
                    ref="generatingStatusRef" 
                    :closeEnable="false"
                    :showActions="false"
                    @takeOver="handleTakeOver"
                /> 
    </DocumentReviewContainer>
</template>

<script setup>
import { ref } from 'vue';
import { ElLoading, ElMessage } from 'element-plus'
import { getRoleBySceneIdAndAgentType } from '@/api/base/im/scene';
import { getToken } from "@/utils/auth";

import DocumentReviewContainer from "./DocumentReviewContainer"
import AIGeneratingStatus from '@/components/GeneratingStatus/index.vue'

import ExecuteHandle from './executeHandle'
import FunctionList from './functionList'
import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'

import { getScene } from '@/api/base/im/scene/documentReview';

import {
  getReviewTask 
} from '@/api/base/im/scene/documentReviewTask';

import SnowflakeId from "snowflake-id";

const snowflake = new SnowflakeId();

// 设置角色
const roleSelectPanelRef = ref(null)
const generatingStatusRef = ref(null)

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
const taskId = ref(null)
const channelStreamId = ref(null)

const pollingTimer = ref(null); // 用于存储轮询定时器

const historyRecords = [
    { sceneId: '1', title: '大罗市应急管理局首都核心区加油站智能视频监控和物联监测系统项目_招投标文件' },
    { sceneId: '3', title: '广西人工智能产业发展白皮书（2024）' },
    { sceneId: '4', title: '大罗市应急管理局首都核心区加油站智能视频监控和物联监测系统项目_招投标文件' },
    { sceneId: '3', title: '广西人工智能产业发展白皮书（2024）' }
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
    if (currentSceneInfo.value.analysisAgentEngineer == 0 || currentSceneInfo.value.logicReviewerEngineer == 0) {
        roleSelectPanelRef.value.configAgent();
    }
};

const openExecuteHandle = () => {
    executeHandleRef.value.handleOpen(currentSceneInfo.value, analysisAgentEngineers.value, logicReviewerEngineers.value);
}

/**  文件上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
    imageUrl.value = response.data ? response.data.split(',').map(url => { return { url: upload.display + url } }) : [];
    uploadLoading.value.close();

    taskId.value = response.taskId ;
    channelStreamId.value = response.channelStreamId ;
    console.log('taskId = ' + taskId) ;

    // 上传成功后，开始每10秒轮询任务状态
    startPollingTaskStatus();
};

// 开始轮询任务状态
const startPollingTaskStatus = () => {
    // 先立即查询一次
    handleGetReviewTask();
    
    // 然后设置定时器每10秒查询一次
    pollingTimer.value = setInterval(() => {
        handleGetReviewTask();
    }, 10000); // 10秒间隔
};

// 停止轮询
const stopPolling = () => {
    if (pollingTimer.value) {
        clearInterval(pollingTimer.value);
        pollingTimer.value = null;
    }
};

// 获取当前任务
const handleGetReviewTask = () => {
    getReviewTask(taskId.value).then(res => {

        if(res.data.documentParseStatus == 'success'){  // 文档解析成功 
            generatingStatusRef.value?.close();
            router.push({
                path: '/scene/documentReview/documentParser',
                query: {
                    sceneId: sceneId.value,
                    taskId: taskId.value ,
                    channelStreamId: channelStreamId.value
                }
            })
        }else if(res.data.documentParseStatus == 'generating'){  // 文档解析中
            generatingStatusRef.value?.loading();
            generatingStatusRef.value?.setText(res.data.taskName + " 文档解析中");

        }

    }).catch(err => {
        console.error('查询任务状态失败:', err);
        // 可以选择重试或停止轮询
        generatingStatusRef.value?.close();
        stopPolling();
    });
}

/**  文件上传之前 */
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

// 组件卸载时清理定时器
onUnmounted(() => {
    stopPolling();
});

</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-review.scss'; 

.review-upload-container{
    box-shadow: 0 12px 24px -16px #3636490f,0 12px 40px #4a50601f,0 0 1px #2c2c3605;

}

.review-main-content{
   height: calc(80vh - 150px) !important;
}
</style>