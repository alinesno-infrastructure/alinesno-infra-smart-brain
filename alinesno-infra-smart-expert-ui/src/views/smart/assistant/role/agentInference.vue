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
            <el-button type="primary" bg @click="submitModelConfig()">保存配置</el-button>
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
                                    <div class="text item">
                                        <el-button type="primary" text bg @click="handleInference">编辑</el-button>
                                    </div>
                                </div>
                                <div class="text-role-item-desc">
                                    {{ currentRole.responsibilities }}
                                </div>
                            </el-col>
                        </el-row>
                    </el-card>
                    <!-- AI配置界面 -->
                    <el-card class="box-card" shadow="never">
                        <div class="clearfix">
                            <span class="box-card-title">AI配置</span>
                        </div>
                        <el-row class="nav-row">
                            <el-col :span="6">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-masks-theater"></i> AI模型
                                </div>
                            </el-col>
                            <el-col :span="18">
                                <div class="select-wrapper">
                                    <el-select v-model="agentModelConfig.modelName" placeholder="请选择大模型" size="large"
                                        style="width:100%">
                                        <el-option v-for="item in llmModelOptions" :key="item.id"
                                            :label="item.modelName" :value="item.id">
                                            <template #default>
                                                <div>
                                                    <img :src="'http://data.linesno.com/icons/llm/' + item.providerCode + '.png'"
                                                        alt="图标" style="width: 25px; height: 25px; border-radius: 50%;">
                                                    {{ item.modelName }}
                                                </div>
                                            </template>
                                        </el-option>
                                    </el-select>
                                    <el-button type="primary" size="large" text bg @click="openModelConfigDialog">
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
                                    <el-input 
                                        maxlength="5000" 
                                        show-word-limit
                                        v-model="agentModelConfig.promptContent" 
                                        type="textarea" 
                                        resize="none" 
                                        :rows="4"
                                        placeholder="模型固定的引导词，通过调整该内容，可以引导模型聊天方向。该内容会被固定在上下文的开头。可通过输入 / 插入选择变量如果关联了知识库，你还可以通过适当的描述，来引导模型何时去调用知识库搜索。例如：你是电影《星际穿越》的助手，当用户询问与《星际穿越》相关的内容时，请搜索知识库并结合搜索结果进行回答。"></el-input>
                                        <div class="function-CodemirrorEditor__footer">
                                            <el-button text @click="openPromptDialog" style="background-color: transparent !important;" class="magnify">
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
                                    <el-tag style="cursor: pointer;" effect="dark">{{ selectionDatasetData.length }}</el-tag>
                                </div>
                            </el-col>
                            <el-col :span="12">
                                <div class="button-group">
                                    <el-button type="primary" text bg @click="openKnowledgeBaseSelection">+
                                        选择</el-button>
                                    <el-button type="primary" text bg @click="handleOpenDatasetConfigPanel" >参数</el-button>
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
                            <el-col :span="24">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-comment-slash"></i> 对话开场白
                                </div>
                            </el-col>
                            <el-col :span="24">
                                <div class="input-wrapper">
                                    <el-input 
                                        maxlength="100" 
                                        v-model="agentModelConfig.greeting"
                                        show-word-limit
                                        type="textarea" 
                                        resize="none" 
                                        :rows="2"
                                        placeholder="每次对话开始前，发送一个初始内容。支持标准 Markdown 语法，可使用的额外标记：[快捷按键]：用户点击后可以直接发送该问题"></el-input>
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
                                    <el-button type="primary" text bg @click="toggleVoicePlayback">+
                                        浏览器自带（免费）</el-button>
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
                                </div>
                            </el-col>
                        </el-row>
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

    <el-dialog title="选择知识库" v-model="datasetConfigDialogVisible" width="1024px" :before-close="handleSelectDatasetConfigClose">
        <div style="margin-bottom:30px">
            <DatasetChoicePanel ref="datasetChoicePanelRef" />
        </div>
    </el-dialog>

    <el-dialog title="知识库配置" v-model="datasetParamsConfigDialogVisible" width="600px" :before-close="handleSelectDatasetParamsConfigClose">
        <div style="margin-bottom:30px">
            <DatasetParamsChoicePanel ref="datasetParamsChoicePanelRef" />
        </div>
    </el-dialog>

    <el-dialog title="选择工具" v-model="toolsConfigDialogVisible" width="1024px" :before-close="handleSelectToolsConfigClose()">
        <div style="margin-bottom:30px">
            <ToolsChoicePanel ref="toolsChoicePanelRef" />
        </div>
    </el-dialog>

    <el-dialog title="选择语音" v-model="voiceConfigDialogVisible" width="500px">
        <VoiceChoicePanel ref="voiceChoicePanelRef" />
    </el-dialog>

    <!-- 用户问题建议 -->
    <el-dialog title="用户问题建议参数" v-model="guessWhatYouAskStatus" width="900px">
        <guessWhatYouAskPanel ref="guessWhatYouAskRef" />
    </el-dialog>

    <!-- 提示词 -->
     <el-dialog title="提示词" v-model="promptDialogVisible" width="800px">
        <promptEditorPanel @syncPromptContent="syncPromptContent" ref="promptEditorPanelRef" />
     </el-dialog>

    <!-- 语音输入参数 -->
     <el-dialog title="语音输入" v-model="voiceInputStatusVisible" width="500px">
        <VoiceInputStatusPanel ref="voiceInputStatusPanelRef" />
     </el-dialog>

</template>
<script setup>
import { nextTick, ref } from 'vue';

import {
    getRole
} from "@/api/smart/assistant/role";

import {
    listAllLlmModel
} from "@/api/smart/assistant/llmModel"


import DatasetChoicePanel from '@/views/base/search/vectorData/datasetChoicePanel'
import DatasetParamsChoicePanel from '@/views/base/search/vectorData/datasetParamsChoicePanel'
import ToolsChoicePanel from '@/views/smart/assistant/plugin/toolsChoicePanel'
import VoiceChoicePanel from '@/views/smart/assistant/llmModel/choiceVoicePanel'
import LllModelConfigPanel from '@/views/smart/assistant/llmModel/lllModelConfigPanel'
import guessWhatYouAskPanel from '@/views/smart/assistant/llmModel/guessWhatYouAskPanel'
import promptEditorPanel from '@/views/smart/assistant/llmModel/promptEditorPanel'
import VoiceInputStatusPanel from '@/views/smart/assistant/llmModel/voiceInputStatusPanel'

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

const llmModelConfigPanelRef = ref(null)
const datasetChoicePanelRef = ref(null)
const toolsChoicePanelRef = ref(null)
const roleChatPanelRef = ref(null)
const voiceChoicePanelRef = ref(null)
const guessWhatYouAskRef = ref(null)
const promptEditorPanelRef = ref(null)
const voiceInputStatusPanelRef = ref(null)

const modelOptions = ref([])
const llmModelOptions = ref([])  // 大模型
const voiceModelOptions = ref([])  // 语音播放模型
const voiceRecoModelOptions = ref([])  // 语音播放模型

// AI大模型配置窗口相关
const agentModelConfig = ref({
    modelName: '',
    promptContent: '' ,
    // memoryRounds: '',
    // replyLimit: '',
    // temperature: '',
    // topP: '',
    // stopSequences: ''
});

// 关联知识库选择相关
// const knowledgeBaseIds = ref([]);
// 关闭工具相关
// const closedToolIds = ref([]);

// 长期记忆开关
const longTermMemoryEnabled = ref(false);
// 语音播放开关
const voicePlaybackEnabled = ref(false);
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
    });
}

nextTick(() => {
    getRoleInfo();
})

// 打开AI大模型配置窗口
function openModelConfigDialog() {
    modelConfigDialogVisible.value = true;

    nextTick(() => {
        llmModelConfigPanelRef.value.setLlmModelOptions(llmModelOptions.value);
    });
}

/** 设置AI大模型配置 */
function setAgentModelConfig(modelConfig){
    console.log('agentModelConfig modelConfig = ' + modelConfig);
    modelConfigDialogVisible.value = false;
    agentModelConfig.value.modelConfig = modelConfig;
}

// 打开关联知识库选择窗口
function openKnowledgeBaseSelection() {
    console.log('打开关联知识库选择窗口')
    // 这里可以添加打开选择窗口的逻辑，选择后更新knowledgeBaseIds
    datasetConfigDialogVisible.value = true;
}

// 打开数据集配置窗口
function handleOpenDatasetConfigPanel(){
    datasetParamsConfigDialogVisible.value = true;
}

// 关闭数据集配置窗口
function handleSelectDatasetConfigClose(){
    if(datasetConfigDialogVisible.value){
        datasetConfigDialogVisible.value = false;
        selectionDatasetData.value = datasetChoicePanelRef.value.getSelectItemList();
        agentModelConfig.value.knowledgeBaseIds = selectionDatasetData.value;
    }
}

// 关闭数据集配置窗口
function handleSelectDatasetParamsConfigClose(){
    if(datasetParamsConfigDialogVisible.value){
        datasetParamsConfigDialogVisible.value = false;
        // selectionDatasetParamsData.value = datasetParamsChoicePanelRef.value.getSelectItemList();
    }
}

// 打开工具选择窗口
function openToolSelection() {
    // 这里可以添加打开选择窗口的逻辑，选择后更新closedToolIds
    toolsConfigDialogVisible.value = true;
}

// 关闭工具选择窗口之前，获取到工具
function handleSelectToolsConfigClose(){
    if(toolsChoicePanelRef.value){
        selectionToolsData.value = toolsChoicePanelRef.value.getSelectItemList() ; 
        console.log('selectionToolsData.value = ' + JSON.stringify(selectionToolsData.value)) ;
        agentModelConfig.value.selectionToolsData = selectionToolsData.value;
    }
}

// 切换长期记忆状态
function toggleLongTermMemory() {
    longTermMemoryEnabled.value = !longTermMemoryEnabled.value;
}
// 切换语音播放状态
function toggleVoicePlayback() {
    voicePlaybackEnabled.value = !voicePlaybackEnabled.value;
    voiceConfigDialogVisible.value = true;

    nextTick(() => {
        console.log(voiceChoicePanelRef.value)
        voiceChoicePanelRef.value.setVoiceModelOptions(voiceModelOptions.value);
    });

}
// 切换语音输入状态
function toggleVoiceInput() {
    voiceInputStatus.value = !voiceInputStatus.value;
}
// 切换用户问题建议状态
function toggleGuessWhatYouAsk() {
    guessWhatYouAskStatus.value = !guessWhatYouAskStatus.value;

    nextTick(() => {
        console.log(voiceChoicePanelRef.value)
        guessWhatYouAskRef.value.setLlmModelOptions(llmModelOptions.value);
    });
}

// 关联知识库选择窗口相关
// function handleSelectionDatasetData(data) {
//     console.log('关联知识库选择窗口相关', data)
//     selectionDatasetData.value = data;
// }

// 配置语音输入参数 
function toggleVoiceInputStatusPanel(){
    voiceInputStatusVisible.value = !voiceInputStatusVisible.value;
    voiceInputStatusVisible.value && nextTick(() => {
        console.log(voiceChoicePanelRef.value)
        voiceInputStatusPanelRef.value.setVoiceModelOptions(voiceRecoModelOptions.value);
    });
}

// 保存配置
function handleInference() {
    // 这里将配置信息提交到后台
    modelConfigDialogVisible.value = true;
}

/** 列出所有模型列表 */
function handleListAllLlmModel() {
    listAllLlmModel().then(res => {
        modelOptions.value = res.data;

        // 过滤出下面的参数
        // llmModelOptions   // 大模型(large_language_model)
        // voiceModelOptions  // 语音播放模型(speech_synthesis)

        llmModelOptions.value = res.data.filter(item => item.modelType === 'large_language_model');
        voiceModelOptions.value = res.data.filter(item => item.modelType === 'speech_synthesis');
        voiceRecoModelOptions.value = res.data.filter(item => item.modelType === 'speech_recognition');

        console.log('voiceModelOptions = ' + JSON.stringify(voiceModelOptions.value))

    });
}

/** 打开提示词 */
function openPromptDialog(){
    promptDialogVisible.value = true ;
    console.log('打开提示词:' + agentModelConfig.value.promptContent)
    nextTick(() => {
        promptEditorPanelRef.value.setPromptContent(agentModelConfig.value.promptContent);
    });
}

/** 同步提示词 */
function syncPromptContent(content){
    agentModelConfig.value.promptContent = content ;
    promptDialogVisible.value = false ;
}

/** 保存模型配置 */
function submitModelConfig(){
    console.log('保存模型配置'+ JSON.stringify(agentModelConfig.value))
}

// defineEmits(['sysncPromptContent'])

handleListAllLlmModel();

defineExpose({
    setCurrentRoleId
})



</script>
<style lang="scss" scoped>
.function-CodemirrorEditor__footer{
  position: absolute;
  bottom: 5px;
  right: 0px;
}
</style>