<template>

    <ContentFormatterContainer>

        <div class="document-review-container">
            <RoleSelectPanel :currentSeneInfo="currentSceneInfo" @openExecuteHandle="openExecuteHandle"
                ref="roleSelectPanelRef" />

            <div style="padding: 20px;">

                <el-row>
                    <el-col :span="12">
                        <div class="review-main-content">


                            <div class="review-header-content">
                                <div class="review-title">
                                    <i class="fa-solid fa-file-contract"></i> AI智能文档，提升文件处理效率
                                </div>
                                <div class="review-description">
                                    提升文件处理效率，助力高效办公，让每一份文件都成为决策加速器，赋能智慧政务与企业管理
                                </div>
                            </div>

                            <div class="review-upload-container">
                                <el-upload class="upload-demo" drag :file-list="imageUrl"
                                    :action="upload.url + '?sceneId=' + currentSceneInfo.id" :auto-upload="true"
                                    accept=".doc,.docx,.pdf" :on-success="handleAvatarSuccess"
                                    :before-upload="beforeAvatarUpload" :headers="upload.headers" multiple>

                                    <div class="imgWrapper">
                                        <span class="file-icon file-icon--pdf"><i
                                                class="fa-solid fa-file-powerpoint"></i></span>
                                        <span class="file-icon file-icon--word"><i
                                                class="fa-solid fa-file-word"></i></span>
                                        <span class="file-icon file-icon--file"><i
                                                class="fa-solid fa-file-contract"></i></span>
                                    </div>

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
                    <el-col :span="12">
                        <!-- 最近文件 -->
                         <RecentFile />
                    </el-col>

                </el-row>
            </div>

            <!-- 角色选择面板 -->
            <!-- <ExecuteHandle ref="executeHandleRef" @openChatBox="openChatBox" @handleGetScene="handleGetScene" /> -->

        </div>
    </ContentFormatterContainer>
</template>

<script setup>
import { ref } from 'vue';
import { ElLoading, ElMessage } from 'element-plus'
import { getRoleBySceneIdAndAgentType } from '@/api/base/im/scene';
import { getToken } from "@/utils/auth";

import ContentFormatterContainer from './common/ContentFormatterContainer';
import RecentFile from './components/RecentFile'
import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'

import { getScene, updateChapterPromptContent } from '@/api/base/im/scene/contentFormatter';
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
        // openExecuteHandle();
        roleSelectPanelRef.value.configAgent();
    }
};

const openExecuteHandle = () => {
    executeHandleRef.value.handleOpen(currentSceneInfo.value, analysisAgentEngineers.value, logicReviewerEngineers.value);
}

/** 图片上传成功 */
const handleAvatarSuccess = (response, uploadFile) => {
    imageUrl.value = response.data ? response.data.split(',').map(url => { return { url: upload.display + url } }) : [];
    uploadLoading.value.close();

    const taskId = response.taskId;
    console.log('taskId = ' + taskId);

    // 上传成功，进入分析界面
    router.push({
        path: '/scene/documentReview/documentParser',
        query: {
            sceneId: sceneId.value,
            taskId: taskId,
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
}

const handleGetScene = () => {
    getScene(sceneId.value).then(res => {
        currentSceneInfo.value = res.data;

        if (currentSceneInfo.value.genStatus == 1 && !isBack.value) {
            // router.push({
            //     path: '/scene/documentReview/documentParser',
            //     query: {
            //         sceneId: sceneId.value , 
            //         channelStreamId: snowflake.generate()
            //     }
            // })
            return;
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

.review-title {
    color: #2C2C36;
}

.review-header-content {
    align-items: flex-start !important;
    padding: 20px 0px !important;
}

.review-main-content {
    justify-content: flex-start !important;
}

.el-upload__text {
    font-weight: bold;
    font-size: 16px !important;
}

.imgWrapper {
    margin: 20px;
    display: flex;
    gap: 20px;
    align-items: center;
    justify-content: center;

    .file-icon {
        align-items: center;
        justify-content: center;

        i {
            font-size: 35px;
        }

        &--pdf {
            color: #1d75b0; // PDF icon color (red)
        }

        &--word {
            color: #F56C6C; // Word icon color (green)
        }

        &--file {
            color: #E6A23C; // File icon color (gray)
        }
    }
}
</style>