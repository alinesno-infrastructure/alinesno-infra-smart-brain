<template>
    <div class="app-container document-review-container">
        <el-row>
            <el-col :span="20">
                <div class="review-header-title">
                    {{ currentSceneInfo.sceneName }} 
                    <RoleSelectPanel 
                        :currentSeneInfo="currentSceneInfo"
                        @openExecuteHandle="openExecuteHandle"
                        ref="roleSelectPanelRef" />
                </div>
                <div class="review-main-content">


                    <div class="review-header-content">
                        <div class="review-title">
                            <!-- <img class="review-agent" :src="imagePathByPath('1866758271038771201')" /> -->
                            <i class="fa-brands fa-phoenix-framework" style="color:#409EFF"></i>
                            合同审查，一键开启审查
                        </div>
                        <div class="review-description">快速识别合同潜在风险，提供专业的风险评估和修改建议</div>
                    </div>
                    <div class="review-upload-container">
                        <el-upload class="upload-demo" drag
                            action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15" multiple>
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
                        <i class="fa-solid fa-right-long"></i>
                    </div>
                </div>
                <div class="review-footer-message">
                    服务生成的所有内容均由人工智能模型生成，其生成内容的准确性和完整性无法保证，不能代表我们的态度和观点。
                </div>
            </el-col>
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
        </el-row>

        <!-- 角色选择面板 -->
        <ExecuteHandle ref="executeHandleRef" 
            @openChatBox="openChatBox" 
            @handleGetScene="handleGetScene" />

    </div>
</template>

<script setup>
import { ref } from 'vue';
import { getRoleBySceneIdAndAgentType } from '@/api/base/im/scene';

import ExecuteHandle from './executeHandle'
import RoleSelectPanel from './roleSelectPanel'

// 设置角色
const roleSelectPanelRef = ref(null)

// 角色信息
const analysisAgentEngineers = ref([]);
const logicReviewerEngineers = ref([]);

const currentSceneInfo = ref({});

const route = useRoute();
const { proxy } = getCurrentInstance();

import { getScene } from '@/api/base/im/scene/documentReview';

const executeHandleRef = ref(null)
const sceneId = ref(route.query.sceneId)

const historyRecords = [
    { sceneId: '1' , title: '大罗市应急管理局首都核心区加油站智能视频监控和物联监测系统项目_招投标文件' },
    { sceneId: '3' , title: '广西人工智能产业发展白皮书（2024）' },
    { sceneId: '4' , title: '大罗市应急管理局首都核心区加油站智能视频监控和物联监测系统项目_招投标文件' },
    { sceneId: '3' , title: '广西人工智能产业发展白皮书（2024）' }
];

historyRecords.forEach(record => {
    record.hover = false;
});

/** 根据场景id和类型获取到角色信息 */
const handleRoleBySceneIdAndAgentType = async () => {
  console.log('sceneId = ' + currentSceneInfo.value.sceneId + ' , agents = ' + currentSceneInfo.value.agents);

  if (currentSceneInfo.value.sceneId && currentSceneInfo.value.agents) {
    for (let i = 0; i < currentSceneInfo.value.agents.length; i++) {
      let item = currentSceneInfo.value.agents[i];

      if (item.code === 'analysisAgent') {
        const response = await getRoleBySceneIdAndAgentType(currentSceneInfo.value.sceneId, item.id);
        analysisAgentEngineers.value = response.data;
      } else if (item.code === 'logicReviewer') {
        const response = await getRoleBySceneIdAndAgentType(currentSceneInfo.value.sceneId, item.id);
        logicReviewerEngineers.value = response.data;
      }

    }
  }

  if(currentSceneInfo.value.analysisAgentEngineer == 0 || currentSceneInfo.value.logicReviewerEngineer == 0){
    openExecuteHandle();
  }

};

const openExecuteHandle = () => {
    executeHandleRef.value.handleOpen(currentSceneInfo.value , analysisAgentEngineers.value , logicReviewerEngineers.value);
}


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
</style>    