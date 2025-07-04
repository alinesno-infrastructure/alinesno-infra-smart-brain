<template>
    <div class="app-container agent-inference-container">
        <div class="page-header-container">
            <el-page-header @back="goBack">
                <template #content>
                    <div style="display: flex; gap: 10px; align-items: center;">
                        <span style="color: #aaaaaa; font-size: 14px;">更新时间：2025-02-14 23:50:44</span>
                    </div>
                </template>
            </el-page-header>
            <div>
                <el-button type="primary" icon="Setting" text bg size="large" @click="submitModelConfig()">保存配置</el-button>
                <el-button type="danger" icon="Position" text bg size="large" @click="publishRoleConfig()">发布角色</el-button>
            </div>
        </div>
        <el-row :gutter="20">
            <!--类型数据-->
            <el-col :span="10" :xs="24">
                <el-scrollbar class="scrollbar-style">
                    <el-card class="box-card" shadow="never">
                        <el-row>
                            <el-col :span="24">
                                <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 10px;">
                                    <span>
                                        <img :src="imagePath(currentRole.roleAvatar)"
                                            style="width:30px; height:30px; border-radius:5px;" />
                                    </span>
                                    <span style="font-weight: bold;">
                                        {{ currentRole.roleName }}
                                    </span>
                                </div>
                                <div class="text-role-item-desc">
                                    {{ currentRole.responsibilities }}
                                </div>
                            </el-col>
                        </el-row>
                    </el-card>
                    <!-- AI配置界面 -->
                    <el-card class="box-card" shadow="never">
                        <el-form ref="agentModelConfigFormRef" size="large" :model="agentModelConfigForm"
                            :rules="agentModelConfigRules">


                            <div class="clearfix">
                                <span class="box-card-title">AI配置</span>
                            </div>

                            <el-row class="nav-row" style="align-items: flex-start;">
                                <el-col :span="6">
                                    <div class="ai-config-section-title">
                                        <i class="fa-solid fa-masks-theater"></i> AI模型
                                    </div>
                                </el-col>
                                <el-col :span="18">
                                    <div class="select-wrapper" style="align-items: flex-start;">
                                        <el-form-item prop="modelName" class="form-item-wrapper">
                                            <LLMSelector :modelType="'large_language_model'" v-model="agentModelConfigForm.modelId" />
                                        </el-form-item>
                                        <el-button type="primary" size="large" text bg @click="openModelConfigDialog" style="margin-top:2px;">
                                            <i class="fa-solid fa-screwdriver-wrench"></i>
                                        </el-button>
                                    </div>
                                </el-col>
                            </el-row>
                            <el-row class="nav-row">
                                <el-col :span="24">
                                    <div class="ai-config-section-title">
                                        <i class="fa-solid fa-file-signature"></i> 提示词
                                    </div>
                                </el-col>
                                <el-col :span="24">
                                    <div class="input-wrapper">
                                        <el-form-item prop="promptContent" class="form-item-wrapper">
                                            <el-input maxlength="5000" show-word-limit
                                                v-model="agentModelConfigForm.promptContent" type="textarea"
                                                resize="none" :rows="4"
                                                placeholder="模型固定的引导词，通过调整该内容，可以引导模型聊天方向。该内容会被固定在上下文的开头。可通过输入 / 插入选择变量如果关联了知识库，你还可以通过适当的描述，来引导模型何时去调用知识库搜索。例如：你是电影《星际穿越》的助手，当用户询问与《星际穿越》相关的内容时，请搜索知识库并结合搜索结果进行回答。"></el-input>
                                        </el-form-item>
                                        <div class="function-CodemirrorEditor__footer">
                                            <el-button text @click="openPromptDialog"
                                                style="background-color: transparent !important;" class="magnify">
                                                <i class="fa-solid fa-up-right-and-down-left-from-center"></i>
                                            </el-button>
                                        </div>
                                    </div>
                                </el-col>
                            </el-row>
                            <el-row class="nav-row">
                                <el-col :span="12">
                                    <div class="ai-config-section-title">
                                        <i class="fa-solid fa-database"></i> <span>关联知识库</span>
                                        <el-tag style="cursor: pointer;" effect="dark">{{ selectionDatasetData.length
                                        }}</el-tag>
                                    </div>
                                </el-col>
                                <el-col :span="12">
                                    <div class="button-group">
                                        <el-button type="primary" text bg @click="openKnowledgeBaseSelection">+选择</el-button>
                                        <el-button type="primary" text bg @click="handleOpenDatasetConfigPanel">参数</el-button>
                                    </div>
                                </el-col>
                            </el-row>
                            <el-row class="nav-row">
                                <el-col :span="12">
                                    <div class="ai-config-section-title">
                                        <i class="fa-solid fa-wrench"></i> <span>工具调用</span>
                                        <el-tag style="cursor: pointer;" effect="dark">{{ selectionToolsData.length }}</el-tag>
                                    </div>
                                </el-col>
                                <el-col :span="12">
                                    <div class="button-group">
                                        <el-button type="primary" text bg @click="openToolSelection">+ 选择</el-button>
                                    </div>
                                </el-col>
                            </el-row>
                            <el-row class="nav-row">
                                <el-col :span="12">
                                    <div class="ai-config-section-title">
                                        <i class="fa-solid fa-upload"></i> <span>长期记忆</span>
                                    </div>
                                </el-col>
                                <el-col :span="12">
                                    <div class="button-group">
                                        <el-button type="primary" text bg @click="toggleLongTermMemory">
                                            {{ longTermMemoryEnabled ? '关闭' : '开启' }}
                                        </el-button>
                                    </div>
                                </el-col>
                            </el-row>
                            <el-row class="nav-row">
                                <el-col :span="12">
                                    <div class="ai-config-section-title">
                                        <i class="fa-solid fa-comment-slash"></i> 对话开场白
                                    </div>
                                </el-col>
                                <el-col :span="12">
                                    <div class="button-group">
                                        <el-button type="primary" text @click="openGreetingQuestionPanel">
                                            开场白预置问题
                                        </el-button>
                                    </div>
                                </el-col>
                                <el-col :span="24">
                                    <div class="input-wrapper">
                                        <el-form-item prop="greeting" class="form-item-wrapper">
                                            <el-input maxlength="100" v-model="agentModelConfigForm.greeting"
                                                show-word-limit type="textarea" resize="none" :rows="2"
                                                placeholder="每次对话开始前，发送一个初始内容。支持标准 Markdown 语法，可使用的额外标记：[快捷按键]：用户点击后可以直接发送该问题">
                                            </el-input>
                                        </el-form-item>
                                    </div>
                                </el-col>
                            </el-row>
                            <el-row class="nav-row">
                                <el-col :span="12">
                                    <div class="ai-config-section-title">
                                        <i class="fa-solid fa-volume-up"></i> <span>语音播放</span>
                                    </div>
                                </el-col>
                                <el-col :span="12">
                                    <div class="button-group">
                                        <el-button type="primary" text bg @click="toggleVoicePlayStatus">
                                            {{ voicePlayStatus ? '关闭' : '开启' }}
                                        </el-button>
                                        <el-button v-if="voicePlayStatus" type="primary" text bg
                                            @click="toggleVoicePlayback">参数</el-button>
                                    </div>
                                </el-col>
                            </el-row>
                            <el-row class="nav-row">
                                <el-col :span="12">
                                    <div class="ai-config-section-title">
                                        <i class="fa-solid fa-microphone"></i> <span>语音输入</span>
                                    </div>
                                </el-col>
                                <el-col :span="12">
                                    <div class="button-group">
                                        <el-button type="primary" text bg @click="toggleVoiceInput">
                                            {{ voiceInputStatus ? '关闭' : '开启' }}
                                        </el-button>
                                        <el-button v-if="voiceInputStatus" type="primary" text bg @click="toggleVoiceInputStatusPanel">参数</el-button>
                                    </div>
                                </el-col>
                            </el-row>

                            <!-- 附件上传_start -->
                            <el-row class="nav-row">
                                <el-col :span="12">
                                    <div class="ai-config-section-title">
                                        <i class="fa-solid fa-file-word"></i> <span>附件上传</span>
                                    </div>
                                </el-col>
                                <el-col :span="12">
                                    <div class="button-group">
                                        <el-button type="primary" text bg @click="toggleUploadStatus">
                                            {{ uploadStatus ? '关闭' : '开启' }}
                                        </el-button>
                                        <el-button v-if="uploadStatus" type="primary" text bg @click="toggleUploadSettingsPanel">参数</el-button>
                                    </div>
                                </el-col>
                            </el-row>
                            <!-- 附件上传_end -->

                            <el-row class="nav-row">
                                <el-col :span="12">
                                    <div class="ai-config-section-title">
                                        <i class="fa-solid fa-lightbulb"></i> <span>用户问题建议</span>
                                    </div>
                                </el-col>
                                <el-col :span="12">
                                    <div class="button-group">
                                        <el-button type="primary" text bg @click="toggleGuessWhatYouAsk">
                                            {{ guessWhatYouAskStatus ? '关闭' : '开启' }}
                                        </el-button>
                                        <el-button v-if="guessWhatYouAskStatus" type="primary" text bg @click="toggleGuessWhatYouAskStatusPanel">参数</el-button>
                                    </div>
                                </el-col>
                            </el-row>

                        </el-form>
                    </el-card>
                </el-scrollbar>
            </el-col>
            <!--类型数据-->
            <el-col :span="14" :xs="24">
                <el-card shadow="never" class="agent-chat-box">
                    <RoleChatPanel ref="roleChatPanelRef" />
                </el-card>
            </el-col>
        </el-row>
    </div>

    <!-- 弹出的AI大模型配置窗口 -->
    <el-dialog title="AI大模型配置" v-model="modelConfigDialogVisible" width="600px">
        <div style="margin-bottom:0px">
            <LllModelConfigPanel @setAgentModelConfig="setAgentModelConfig" ref="llmModelConfigPanelRef" />
        </div>
    </el-dialog>

    <el-dialog title="选择知识库" v-model="datasetConfigDialogVisible" width="1024px">
        <div style="margin-bottom:30px">
            <DatasetChoicePanel @handleSelectDatasetConfigClose="handleSelectDatasetConfigClose" ref="datasetChoicePanelRef" />
        </div>
    </el-dialog>

    <el-dialog title="知识库配置" v-model="datasetParamsConfigDialogVisible" width="600px">
        <div style="margin-bottom:30px">
            <DatasetParamsChoicePanel @handleSelectDatasetParamsConfigClose="handleSelectDatasetParamsConfigClose" ref="datasetParamsChoicePanelRef" />
        </div>
    </el-dialog>

    <el-dialog title="选择工具" v-model="toolsConfigDialogVisible" width="1024px">
        <div style="margin-bottom:30px">
            <ToolsChoicePanel @handleSelectToolsConfigClose="handleSelectToolsConfigClose" ref="toolsChoicePanelRef" />
        </div>
    </el-dialog>

    <el-dialog title="选择语音" v-model="voiceConfigDialogVisible" width="500px">
        <VoiceChoicePanel @handleVoiceConfigParams="handleVoiceConfigParams" ref="voiceChoicePanelRef" />
    </el-dialog>

    <!-- 用户问题建议 -->
    <el-dialog title="用户问题建议参数" v-model="guessWhatYouAskStatusVisible" width="900px">
        <guessWhatYouAskPanel @handleGuessWhatYouAskStatusPanelClose="handleGuessWhatYouAskStatusPanelClose" ref="guessWhatYouAskRef" />
    </el-dialog>

    <!-- 提示词 -->
    <el-dialog title="提示词" v-model="promptDialogVisible" width="900px">
        <promptEditorPanel @syncPromptContent="syncPromptContent" ref="promptEditorPanelRef" />
    </el-dialog>

    <!-- 语音输入参数 -->
    <el-dialog title="语音输入" v-model="voiceInputStatusVisible" width="500px">
        <VoiceInputStatusPanel @handleVoiceInputStatusPanelClose="handleVoiceInputStatusPanelClose" ref="voiceInputStatusPanelRef" />
    </el-dialog>

    <!-- 附件上传 -->
     <el-dialog title="附件上传" v-model="uploadStatusVisible" width="500px">
        <AttachmentUploadStatusPanel @handleAttachmentUploadStatusPanelClose="handleAttachmentUploadStatusPanelClose" ref="uploadStatusVisiblePanelRef" />
    </el-dialog>

    <!-- 开场白预置问题 -->
     <el-dialog title="开场白预置问题" v-model="openingPhraseStatusVisible" width="700px">
        <OpeningPhraseStatusPanel @handleOpeningPhraseStatusPanelClose="handleOpeningPhraseStatusPanelClose" ref="openingPhraseStatusPanelRef" />
    </el-dialog>

</template>
<script setup>
import { nextTick, ref } from 'vue';
import { ElLoading , ElMessage } from 'element-plus'

import {
    getRole , 
    saveRoleWithReActConfig , 
    publishRole
} from "@/api/smart/assistant/role";

import {
    listAllLlmModel
} from "@/api/smart/assistant/llmModel"

import LLMSelector from '@/views/smart/assistant/workflow/components/LLMSelector'

// import DatasetChoicePanel from '@/views/base/search/vectorData/datasetChoicePanel'
import DatasetChoicePanel from '@/views/base/search/vectorData/datasetChoiceTransferPanel'
import DatasetParamsChoicePanel from '@/views/base/search/vectorData/datasetParamsChoicePanel'
// import ToolsChoicePanel from '@/views/smart/assistant/plugin/toolsChoicePanel'
import ToolsChoicePanel from '@/views/smart/assistant/plugin/toolsChoiceTransferPanel'
import VoiceChoicePanel from '@/views/smart/assistant/llmModel/choiceVoicePanel'
import LllModelConfigPanel from '@/views/smart/assistant/llmModel/lllModelConfigPanel'
import guessWhatYouAskPanel from '@/views/smart/assistant/llmModel/guessWhatYouAskPanel'
import promptEditorPanel from '@/views/smart/assistant/llmModel/promptEditorPanel'
import VoiceInputStatusPanel from '@/views/smart/assistant/llmModel/voiceInputStatusPanel'
import AttachmentUploadStatusPanel from '@/views/smart/assistant/llmModel/attachmentUploadStatusPanel'
import OpeningPhraseStatusPanel from '@/views/smart/assistant/llmModel/openingPhraseStatusPanel'

import RoleChatPanel from '@/views/smart/assistant/role/chat/index';

const router = useRouter();
const { proxy } = getCurrentInstance();
const currentRole = ref({
    roleName: ''
});
const currentRoleId = ref(null)

const modelConfigDialogVisible = ref(false);
const datasetConfigDialogVisible = ref(false)
const datasetParamsConfigDialogVisible = ref(false)
const toolsConfigDialogVisible = ref(false)
const voiceConfigDialogVisible = ref(false)
const promptDialogVisible = ref(false)
const voiceInputStatusVisible = ref(false)
const guessWhatYouAskStatusVisible = ref(false)
const uploadStatusVisible = ref(false)
const openingPhraseStatusVisible = ref(false)

const llmModelConfigPanelRef = ref(null)
const datasetParamsChoicePanelRef= ref(null)
const datasetChoicePanelRef = ref(null)
const toolsChoicePanelRef = ref(null)
const roleChatPanelRef = ref(null)
const voiceChoicePanelRef = ref(null)
const guessWhatYouAskRef = ref(null)
const promptEditorPanelRef = ref(null)
const voiceInputStatusPanelRef = ref(null)
const uploadStatusVisiblePanelRef = ref(null)
const openingPhraseStatusPanelRef  = ref(null)

const modelOptions = ref([])
const llmModelOptions = ref([])  // 大模型
const voiceModelOptions = ref([])  // 语音播放模型
const voiceRecoModelOptions = ref([])  // 语音播放模型
const multiModelOptions = ref([])  // 多模态模型 
const ocrModelOptions = ref([])  // ocr模型

// 附件上传
const uploadStatus = ref(false)

// AI大模型配置窗口相关
const agentModelConfigFormRef = ref(null)

const agentModelConfigForm = ref({
    roleId: 0, 
    modelId: 0,
    promptContent: '',
    greeting: '',
});

// 表单校验规则
const agentModelConfigRules = ref({
    modelId: [
        { required: true, message: '请确认是否选择模型', trigger: 'bulr' }
    ],
    promptContent: [
        { required: true, message: '请确认是否填写提示语', trigger: 'bulr' }
    ],
    greeting: [
        { required: true, message: '请确认是否填写欢迎语', trigger: 'bulr' }
    ]   
});

// 关联知识库选择相关
// const knowledgeBaseIds = ref([]);
// 关闭工具相关
// const closedToolIds = ref([]);

// 长期记忆开关
const longTermMemoryEnabled = ref(false);
// 语音播放开关
const voicePlayStatus = ref(false);
// 语音输入开关
const voiceInputStatus = ref(false);
// 用户问题建议开关
const guessWhatYouAskStatus = ref(false);

// 已选择的数据集数据
const selectionDatasetData = ref([]);

// 已选择的工具数据
const selectionToolsData = ref([])

/** 返回 */
function goBack() {
    router.back();
}
/** 设置当前角色 */
function setCurrentRoleId(id) {
    currentRoleId.value = id;
}
/** 获取角色信息 */
function getRoleInfo() {
    currentRoleId.value = router.currentRoute.value.query.roleId;

    getRole(currentRoleId.value).then(response => {
        currentRole.value = response.data;
        roleChatPanelRef.value.setRoleInfo(currentRole.value)
        displayRoleInfoBack(currentRole.value);
    });

}

/** 回显角色信息 */
const displayRoleInfoBack = (currentRole) =>{
    // 回显当前角色信息
    agentModelConfigForm.value.roleId = currentRole.id ;
    agentModelConfigForm.value.modelId = currentRole.modelId;
    agentModelConfigForm.value.promptContent = currentRole.promptContent;
    agentModelConfigForm.value.greeting = currentRole.greeting;
    // 长期记忆
    longTermMemoryEnabled.value = currentRole.longTermMemoryEnabled ;
    agentModelConfigForm.value.longTermMemoryEnabled = currentRole.longTermMemoryEnabled ;

    if(currentRole.knowledgeBaseIds){
        const datasetParsedArray = currentRole.knowledgeBaseIds ; // JSON.parse(currentRole.knowledgeBaseIds);
        selectionDatasetData.value = datasetParsedArray;
        agentModelConfigForm.value.knowledgeBaseIds = datasetParsedArray ;
    }

    if(currentRole.selectionToolsData){
        const toolsParsedArray = currentRole.selectionToolsData ; // JSON.parse(currentRole.selectionToolsData);
        selectionToolsData.value = toolsParsedArray;
        agentModelConfigForm.value.selectionToolsData = toolsParsedArray ; 
    }

    // 语音输入
    voiceInputStatus.value = currentRole.voiceInputStatus ;
    agentModelConfigForm.value.voiceInputStatus = currentRole.voiceInputStatus ;
    if(currentRole.voiceInputData){
        agentModelConfigForm.value.voiceInputData = currentRole.voiceInputData ; // JSON.parse(currentRole.voiceInputData);
    }

    // 语音播放
    voicePlayStatus.value = currentRole.voicePlayStatus ;

    agentModelConfigForm.value.voicePlayStatus = currentRole.voicePlayStatus ;
    if(currentRole.voicePlayData){
        agentModelConfigForm.value.voicePlayData = currentRole.voicePlayData ; // JSON.parse(currentRole.voicePlayData);
    }

    // 用户问题建议
    guessWhatYouAskStatus.value = currentRole.guessWhatYouAskStatus ;
    agentModelConfigForm.value.guessWhatYouAskStatus = currentRole.guessWhatYouAskStatus ;
    if(currentRole.guessWhatYouAskData){
        agentModelConfigForm.value.guessWhatYouAskData = currentRole.guessWhatYouAskData ; // JSON.parse(currentRole.guessWhatYouAskData);
    }

    // 模型配置
    if(currentRole.modelConfig){
        agentModelConfigForm.value.modelConfig = currentRole.modelConfig ; // JSON.parse(currentRole.modelConfig);
    }

    // 搜索配置
    if(currentRole.datasetSearchConfig){
        agentModelConfigForm.value.datasetSearchConfig = currentRole.datasetSearchConfig ; // JSON.parse(currentRole.searchConfig);
    } 

    // 附件上传
    if(currentRole.uploadStatus){
        uploadStatus.value = currentRole.uploadStatus ;
        agentModelConfigForm.value.uploadStatus = currentRole.uploadStatus ;
        agentModelConfigForm.value.uploadData = currentRole.uploadData;
    }

    //  对话开场白
    if(currentRole.greetingQuestion){
        agentModelConfigForm.value.greetingQuestion = currentRole.greetingQuestion ;
    }

    console.log('agentModelConfigForm = ' + JSON.stringify(agentModelConfigForm.value));
}

// 打开AI大模型配置窗口
function openModelConfigDialog() {
    modelConfigDialogVisible.value = true;

    nextTick(() => {
        llmModelConfigPanelRef.value.setLlmModelOptions(llmModelOptions.value);
        llmModelConfigPanelRef.value.setAgentModelParams(agentModelConfigForm.value.modelConfig)
    });
}

/** 设置AI大模型配置 */
function setAgentModelConfig(modelConfig) {
    console.log('agentModelConfigForm modelConfig = ' + modelConfig);
    modelConfigDialogVisible.value = false;
    agentModelConfigForm.value.modelConfig = modelConfig;

    // 更新角色内容
    submitModelConfig();
}

// 打开关联知识库选择窗口
function openKnowledgeBaseSelection() {
    console.log('打开关联知识库选择窗口')
    // 这里可以添加打开选择窗口的逻辑，选择后更新knowledgeBaseIds
    datasetConfigDialogVisible.value = true;

    // 设置知识库值
    nextTick(() => {
        datasetChoicePanelRef.value.setSelectItemList(selectionDatasetData.value);
    });

}

// 打开数据集配置窗口
function handleOpenDatasetConfigPanel() {
    datasetParamsConfigDialogVisible.value = true;

    nextTick(() => {
        datasetParamsChoicePanelRef.value.setDataset(agentModelConfigForm.value.datasetSearchConfig);
    });
}

// 关闭数据集配置窗口
function handleSelectDatasetConfigClose(selectItem) {
    console.log('关闭数据集配置窗口 = ' + selectItem)
    if (datasetConfigDialogVisible.value) {
        datasetConfigDialogVisible.value = false;
        selectionDatasetData.value = selectItem ; // datasetChoicePanelRef.value.getSelectItemList();
        agentModelConfigForm.value.knowledgeBaseIds = selectionDatasetData.value;

        // 更新配置
        submitModelConfig();
    }
}

// 关闭数据集配置窗口
function handleSelectDatasetParamsConfigClose(formData) {

    console.log('handleSelectDatasetParamsConfigClose = ' + JSON.stringify(formData));

    if (datasetParamsConfigDialogVisible.value) {
        datasetParamsConfigDialogVisible.value = false;
        agentModelConfigForm.value.datasetSearchConfig = formData;

        // 更新配置
        submitModelConfig();
    }
}

// 打开工具选择窗口
function openToolSelection() {
    // 这里可以添加打开选择窗口的逻辑，选择后更新closedToolIds
    toolsConfigDialogVisible.value = true;

    // 设置知识库值
    nextTick(() => {
        toolsChoicePanelRef.value.setSelectItemList(selectionToolsData.value);
    });
}

// 关闭工具选择窗口之前，获取到工具
function handleSelectToolsConfigClose(selectItem) {
    toolsConfigDialogVisible.value = false;

    if (toolsChoicePanelRef.value) {
        selectionToolsData.value = selectItem ; 
        agentModelConfigForm.value.selectionToolsData = selectionToolsData.value;

        // 更新配置
        submitModelConfig();
    }
}

// 切换长期记忆状态
function toggleLongTermMemory() {
    longTermMemoryEnabled.value = !longTermMemoryEnabled.value;
    agentModelConfigForm.value.longTermMemoryEnabled = longTermMemoryEnabled.value;
}

// 打开语音播放窗口
function toggleVoicePlayStatus() {
    voicePlayStatus.value = !voicePlayStatus.value;
    agentModelConfigForm.value.voicePlayStatus = voicePlayStatus.value;

    submitModelConfig();
}

// 设置语音播放窗口参数
function handleVoiceConfigParams(formData){
    agentModelConfigForm.value.voicePlayData = formData;
    voiceConfigDialogVisible.value = false;

    // 更新角色内容
    submitModelConfig();
}

// 切换语音播放状态
function toggleVoicePlayback() {

    if(voiceModelOptions.value.length == 0){
        ElMessage.error('请先配置语音模型');
        return ;
    }

    voiceConfigDialogVisible.value = true;

    nextTick(() => {
        console.log(voiceChoicePanelRef.value)
        voiceChoicePanelRef.value.setVoiceModelOptions(voiceModelOptions.value);
        if(agentModelConfigForm.value.voicePlayData){
            voiceChoicePanelRef.value.setVoiceModelParams(agentModelConfigForm.value.voicePlayData)
        }
    });
}

// 附件上传_start 
function toggleUploadStatus() {
    uploadStatus.value = !uploadStatus.value;
    agentModelConfigForm.value.uploadStatus = uploadStatus.value;

    submitModelConfig();
}


// 配置语音输入参数 
function toggleUploadSettingsPanel() {
    uploadStatusVisible.value = !uploadStatusVisible.value;
    uploadStatusVisible.value && nextTick(() => {
        console.log(uploadStatusVisiblePanelRef.value)
        uploadStatusVisiblePanelRef.value.setMultiModalOptions(multiModelOptions.value);
        uploadStatusVisiblePanelRef.value.setOcrModalOptions(ocrModelOptions.value);

        nextTick(() => {
            if(agentModelConfigForm.value.uploadData){
                uploadStatusVisiblePanelRef.value.setConfigParams(agentModelConfigForm.value.uploadData);
            }
        });
    });
}

// 附件上传关闭
function handleAttachmentUploadStatusPanelClose(uploadData) {
    console.log('uploadData = ' + JSON.stringify(uploadData))

    uploadStatusVisible.value = false ;
    uploadStatus.value = uploadData.enable; 
    agentModelConfigForm.value.uploadData = uploadData;

    // 更新角色内容
    submitModelConfig();
}

// 切换语音输入状态
function toggleVoiceInput() {
    voiceInputStatus.value = !voiceInputStatus.value;
    agentModelConfigForm.value.voiceInputStatus = voiceInputStatus.value;

    // 更新角色内容
    submitModelConfig();
}
// 切换用户问题建议状态
function toggleGuessWhatYouAsk() {
    guessWhatYouAskStatus.value = !guessWhatYouAskStatus.value;
    agentModelConfigForm.value.guessWhatYouAskStatus = guessWhatYouAskStatus.value;

    // 更新角色内容
    submitModelConfig();
}

// 用户问题建议窗口配置
function toggleGuessWhatYouAskStatusPanel() {
    guessWhatYouAskStatusVisible.value = true;
    nextTick(() => {
        console.log(voiceChoicePanelRef.value)
        guessWhatYouAskRef.value.setLlmModelOptions(llmModelOptions.value);

        console.log('--->>> agentModelConfigForm.value.guessWhatYouAskData = ' + JSON.stringify(agentModelConfigForm.value.guessWhatYouAskData))

        if(agentModelConfigForm.value.guessWhatYouAskData){
            guessWhatYouAskRef.value.setConfigParams(agentModelConfigForm.value.guessWhatYouAskData);
        }
    });
}

// 用户问题建议窗口相关
function handleGuessWhatYouAskStatusPanelClose(formData) {
    if (guessWhatYouAskRef.value) {
        guessWhatYouAskStatusVisible.value = false ;

        // const formData = guessWhatYouAskRef.value.getFormData();
        console.log('handleGuessWhatYouAskStatusPanelClose formData = ' + JSON.stringify(formData))
        guessWhatYouAskStatus.value = formData.enable; 
        agentModelConfigForm.value.guessWhatYouAskData = formData;

        // 更新角色内容
        submitModelConfig();
    }
}

// 配置语音输入参数 
function toggleVoiceInputStatusPanel() {
    voiceInputStatusVisible.value = !voiceInputStatusVisible.value;
    voiceInputStatusVisible.value && nextTick(() => {
        console.log(voiceChoicePanelRef.value)
        voiceInputStatusPanelRef.value.setVoiceModelOptions(voiceRecoModelOptions.value);

        if(agentModelConfigForm.value.voiceInputData){
            voiceInputStatusPanelRef.value.setConfigParams(agentModelConfigForm.value.voiceInputData);
        }
    });
}

function handleVoiceInputStatusPanelClose(voiceInputData) {
    if (voiceInputStatusPanelRef.value) {
        console.log('voiceInputData = ' + JSON.stringify(voiceInputData))

        voiceInputStatus.value = voiceInputData.enable; // !voiceInputStatus.value;
        agentModelConfigForm.value.voiceInputStatus = voiceInputData.enable;// voiceInputStatus.value;

        voiceInputStatusVisible.value = false ;
        agentModelConfigForm.value.voiceInputData = voiceInputData;

        // 更新配置
        submitModelConfig();
    }
}

/** 列出所有模型列表 */
const handleListAllLlmModel = async() => {
    listAllLlmModel().then(res => {
        modelOptions.value = res.data;

        llmModelOptions.value = res.data.filter(item => item.modelType === 'large_language_model');
        voiceModelOptions.value = res.data.filter(item => item.modelType === 'speech_synthesis');
        voiceRecoModelOptions.value = res.data.filter(item => item.modelType === 'speech_recognition');
        multiModelOptions.value = res.data.filter(item => item.modelType === 'vision_model');
        ocrModelOptions.value = res.data.filter(item => item.modelType === 'ocr_model');

        console.log('voiceModelOptions = ' + JSON.stringify(voiceModelOptions.value))

    });
}

/** 打开提示词 */
function openPromptDialog() {
    promptDialogVisible.value = true;
    console.log('打开提示词:' + agentModelConfigForm.value.promptContent)
    nextTick(() => {
        promptEditorPanelRef.value.setPromptContent(agentModelConfigForm.value.promptContent);
    });
}

/** 同步提示词 */
function syncPromptContent(content) {
    agentModelConfigForm.value.promptContent = content;
    promptDialogVisible.value = false;
}

/** 打开开场白角色配置 */
const openGreetingQuestionPanel = () => {
    openingPhraseStatusVisible.value = true;

    nextTick(() => {
       openingPhraseStatusPanelRef.value.setOpeningQuestions(agentModelConfigForm.value.greetingQuestion) 
    });
}

/** 开场白角色配置 */
const handleOpeningPhraseStatusPanelClose = (formData) => {
    console.log('handleOpeningPhraseStatusPanelClose formData = ' + JSON.stringify(formData))
    openingPhraseStatusVisible.value = false ;
    if(formData){
        agentModelConfigForm.value.greetingQuestion = formData ;

        // 更新配置
        submitModelConfig();
    }
}

const submitModelConfig = async () => {

    await agentModelConfigFormRef.value.validate((valid, fields) => {
        if (valid) {

            const loading = ElLoading.service({
                lock: true,
                text: '角色配置保存中...',
                background: 'rgba(0, 0, 0, 0.7)',
            })

            agentModelConfigForm.value.roleId = currentRoleId.value

            console.log('保存模型配置') ; 
            console.log(JSON.stringify(agentModelConfigForm.value))

            saveRoleWithReActConfig(agentModelConfigForm.value).then(res => {
                console.log('res = ' + res);
                proxy.$modal.msgSuccess("角色信息保存成功");
                loading.close();
                getRoleInfo();
            }).catch(() => {
                loading.close();
            })

        } else {
            console.log('error submit!', fields);
        }
    });
};

/**  发布角色 */
const publishRoleConfig = () => {
    publishRole(currentRoleId.id).then(response => {
        ElMessage.success('发布成功');
    })
};


nextTick(() => {
    initData() ; 
})

const initData = async () => {
  try {
    // 等待 handleListAllLlmModel 请求完成
    await handleListAllLlmModel();
    // 执行 getRoleInfo
    getRoleInfo();
  } catch (error) {
    console.error('handleListAllLlmModel 请求出错:', error);
  }
}

defineExpose({
    setCurrentRoleId
})

</script>
<style lang="scss" scoped>
.function-CodemirrorEditor__footer {
    position: absolute;
    bottom: 25px;
    right: 0px;
}

.nav-row {
    width: 100%;
    .form-item-wrapper{
        width:100%;
    }
}
</style>