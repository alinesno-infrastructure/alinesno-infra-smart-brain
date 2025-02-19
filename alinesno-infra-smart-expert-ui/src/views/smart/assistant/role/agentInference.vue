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
            <el-button type="primary" bg @click="handleInference">保存配置</el-button>
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
                                        <img :src="imagePath(currentRole.roleAvatar)" style="width:30px; height:30px; border-radius:5px;" />
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
                        <div slot="header" class="clearfix">
                            <span class="box-card-title">AI配置</span>
                        </div>
                        <el-row class="nav-row">
                            <el-col :span="6">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-brain"></i> AI模型
                                </div>
                            </el-col>
                            <el-col :span="18">
                                <div class="select-wrapper">
                                    <el-select v-model="value" placeholder="Select" size="large" style="width:100%">
                                        <el-option v-for="item in options" :key="item.value" :label="item.label"
                                            :value="item.value" />
                                    </el-select>
                                    <el-button type="primary" text bg @click="openModelConfigDialog">
                                        <i class="fa-solid fa-screwdriver-wrench"></i>
                                    </el-button>
                                </div>
                            </el-col>
                        </el-row>
                        <el-row class="nav-row">
                            <el-col :span="24">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-pen-to-square"></i> 提示词
                                </div>
                            </el-col>
                            <el-col :span="24">
                                <div class="input-wrapper">
                                    <el-input type="textarea" resize="none" :rows="4"
                                        placeholder="模型固定的引导词，通过调整该内容，可以引导模型聊天方向。该内容会被固定在上下文的开头。可通过输入 / 插入选择变量如果关联了知识库，你还可以通过适当的描述，来引导模型何时去调用知识库搜索。例如：你是电影《星际穿越》的助手，当用户询问与《星际穿越》相关的内容时，请搜索知识库并结合搜索结果进行回答。"></el-input>
                                </div>
                            </el-col>
                        </el-row>
                        <el-row class="nav-row">
                            <el-col :span="12">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-database"></i> <span>关联知识库</span>
                                </div>
                            </el-col>
                            <el-col :span="12">
                                <div class="button-group">
                                    <el-button type="primary" text bg @click="openKnowledgeBaseSelection">+ 选择</el-button>
                                    <el-button type="primary" text bg>参数</el-button>
                                </div>
                            </el-col>
                        </el-row>
                        <el-row class="nav-row">
                            <el-col :span="12">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-wrench"></i> <span>工具调用</span>
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
                                        {{ longTermMemoryEnabled? '关闭' : '开启' }}
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
                                    <el-input type="textarea" resize="none" :rows="2"
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
                                    <el-button type="primary" text bg @click="toggleVoicePlayback">+ 浏览器自带（免费）</el-button>
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
                                        {{ voiceInputStatus? '关闭' : '开启' }}
                                    </el-button>
                                    <el-button v-if="voiceInputStatus" type="primary" text bg>参数</el-button>
                                </div>
                            </el-col>
                        </el-row>
                        <el-row class="nav-row">
                            <el-col :span="12">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-lightbulb"></i> <span>猜你想问</span>
                                </div>
                            </el-col>
                            <el-col :span="12">
                                <div class="button-group">
                                    <el-button type="primary" text bg @click="toggleGuessWhatYouAsk">
                                        {{ guessWhatYouAskStatus? '关闭' : '开启' }}
                                    </el-button>
                                    <el-button v-if="guessWhatYouAskStatus" type="primary" text bg>参数</el-button>
                                </div>
                            </el-col>
                        </el-row>
                    </el-card>
                </el-scrollbar>
            </el-col>
            <!--类型数据-->
            <el-col :span="14" :xs="24">
                <el-card shadow="never" class="agent-chat-box">
                    <RoleChatPanel />
                </el-card>
            </el-col>
        </el-row>
    </div>

    <!-- 弹出的AI大模型配置窗口 -->
    <el-dialog title="AI大模型配置" v-model="modelConfigDialogVisible" width="500px"  >
        <el-form :model="modelConfig" label-width="80">
            <el-form-item label="AI模型">
                <el-select v-model="modelConfig.aiModel" placeholder="Select" size="large" style="width:100%">
                    <el-option v-for="item in options" :key="item.value" :label="item.label"
                        :value="item.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="记忆轮数">
                <el-input v-model="modelConfig.memoryRounds" />
            </el-form-item>
            <el-form-item label="回复上限">
                <el-input v-model="modelConfig.replyLimit" />
            </el-form-item>
            <el-form-item label="温度">
                <el-input v-model="modelConfig.temperature" />
            </el-form-item>
            <el-form-item label="Top_p">
                <el-input v-model="modelConfig.topP" />
            </el-form-item>
            <el-form-item label="停止序列">
                <el-input v-model="modelConfig.stopSequences" placeholder="多个序列号通过 | 隔开，例如：aaa|stop" />
            </el-form-item>
            <el-form-item label="回复格式">
                <el-input v-model="modelConfig.replyFormat" />
            </el-form-item>
            <el-form-item style="margin-top: 30px;display: flex;justify-content: flex-end;">
                <el-button>关闭</el-button>
                <el-button type="primary" @click="onSubmit">确认</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>


    <el-dialog title="选择知识库" v-model="datasetConfigDialogVisible" width="1024px"  >
        <div style="margin-bottom:30px">
            <DatasetChocePanel />
        </div>
    </el-dialog>

    <el-dialog title="选择工具" v-model="toolsConfigDialogVisible" width="1024px"  >
        <div style="margin-bottom:30px">
            <ToolsChocePanel />
        </div>
    </el-dialog>

    <el-dialog title="选择语音" v-model="voiceConfigDialogVisible" width="500px"  >
        <div style="margin-bottom:30px">
            <VioceChocePanel />
        </div>
    </el-dialog>
    
</template>
<script setup>
import {
    getRole
} from "@/api/smart/assistant/role";
import { ref } from 'vue';

import DatasetChocePanel from '@/views/base/search/vectorData/datasetChoicePanel'
import ToolsChocePanel from '@/views/smart/assistant/plugin/toolsChoicePanel'
import VioceChocePanel from '@/views/smart/assistant/llmModel/choiceViocePanel'

import RoleChatPanel from '@/views/smart/assistant/role/chat/index';

const router = useRouter();
const { proxy } = getCurrentInstance();
const currentRole = ref({
    roleName: ''
});
const currentRoleId = ref(null)

const datasetConfigDialogVisible = ref(false)

const toolsConfigDialogVisible = ref(false)

const voiceConfigDialogVisible = ref(false)

// AI大模型配置窗口相关
const modelConfigDialogVisible = ref(false);
const modelConfig = ref({
    memoryRounds: '',
    replyLimit: '',
    temperature: '',
    topP: '',
    stopSequences: ''
});
// 关联知识库选择相关
const knowledgeBaseIds = ref([]);
// 关闭工具相关
const closedToolIds = ref([]);
// 长期记忆开关
const longTermMemoryEnabled = ref(false);
// 语音播放开关
const voicePlaybackEnabled = ref(false);
// 语音输入开关
const voiceInputStatus = ref(false);
// 猜你想问开关
const guessWhatYouAskStatus = ref(false);
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
    });
}

nextTick(() => {
    getRoleInfo();
})

// 打开AI大模型配置窗口
function openModelConfigDialog() {
    modelConfigDialogVisible.value = true;
}
// 打开关联知识库选择窗口
function openKnowledgeBaseSelection() {
    console.log('打开关联知识库选择窗口')
    // 这里可以添加打开选择窗口的逻辑，选择后更新knowledgeBaseIds
    datasetConfigDialogVisible.value = true;
}
// 打开工具选择窗口
function openToolSelection() {
    // 这里可以添加打开选择窗口的逻辑，选择后更新closedToolIds
    toolsConfigDialogVisible.value = true ;
}
// 切换长期记忆状态
function toggleLongTermMemory() {
    longTermMemoryEnabled.value =!longTermMemoryEnabled.value;
}
// 切换语音播放状态
function toggleVoicePlayback() {
    voicePlaybackEnabled.value =!voicePlaybackEnabled.value;
    voiceConfigDialogVisible.value = true ;
}
// 切换语音输入状态
function toggleVoiceInput() {
    voiceInputStatus.value =!voiceInputStatus.value;
}
// 切换猜你想问状态
function toggleGuessWhatYouAsk() {
    guessWhatYouAskStatus.value =!guessWhatYouAskStatus.value;
}
// 保存配置
function handleInference() {
    // 这里将配置信息提交到后台，包含knowledgeBaseIds, closedToolIds, modelConfig等
    console.log('提交配置：', {
        knowledgeBaseIds: knowledgeBaseIds.value,
        closedToolIds: closedToolIds.value,
        modelConfig: modelConfig.value,
        longTermMemoryEnabled: longTermMemoryEnabled.value,
        voicePlaybackEnabled: voicePlaybackEnabled.value,
        voiceInputStatus: voiceInputStatus.value,
        guessWhatYouAskStatus: guessWhatYouAskStatus.value
    });

    modelConfigDialogVisible.value = true ;
}
defineExpose({
    setCurrentRoleId
})
</script>
<style lang="scss" scoped>
</style>