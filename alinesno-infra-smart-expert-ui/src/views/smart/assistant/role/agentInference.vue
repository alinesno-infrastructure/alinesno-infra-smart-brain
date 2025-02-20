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
                                    <el-select v-model="modelConfig.modelName" placeholder="请选择大模型" size="large"
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
                                    <i class="fa-solid fa-pen-to-square"></i> 提示词
                                </div>
                            </el-col>
                            <el-col :span="24">
                                <div class="input-wrapper">
                                    <el-input v-model="modelConfig.promptContent" type="textarea" resize="none" :rows="4"
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

                                    <el-popover placement="top-start" title="已选择知识库" :width="600" trigger="hover"
                                        content="this is content, this is content, this is content">
                                        <template #reference>
                                            <el-tag style="cursor: pointer;" effect="dark">6</el-tag>
                                        </template>

                                        <div style="padding:10px 0px">
                                            <el-table :data="selectionDatasetData">
                                                <el-table-column type="index" label="序号" width="50" align="center" />
                                                <el-table-column property="name" label="工具名称">
                                                    <template #default="scope">
                                                        <div style="display: flex; gap:10px;align-items:center">
                                                            <i :class="scope.row.icon"></i> {{ scope.row.name }}
                                                        </div>
                                                    </template>
                                                </el-table-column>
                                                <el-table-column width="100" property="address" label="操作">
                                                    <template #default="scope">
                                                        <el-button type="danger" bg text @click="handleDeleteSelectionDataset(scope.$index, scope.row)">
                                                            删除
                                                        </el-button>
                                                </template>
                                                </el-table-column>
                                            </el-table>
                                        </div>

                                    </el-popover>

                                </div>
                            </el-col>
                            <el-col :span="12">
                                <div class="button-group">
                                    <el-button type="primary" text bg @click="openKnowledgeBaseSelection">+
                                        选择</el-button>
                                    <el-button type="primary" text bg>参数</el-button>
                                </div>
                            </el-col>
                        </el-row>
                        <el-row class="nav-row">
                            <el-col :span="12">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-wrench"></i> <span>工具调用</span>
                                    <el-popover placement="top-start" title="已选择工具" :width="600" trigger="hover"
                                        content="this is content, this is content, this is content">
                                        <template #reference>
                                            <el-tag style="cursor: pointer;" effect="dark">6</el-tag>
                                        </template>

                                        <div style="padding:10px 0px">
                                            <el-table :data="selectionToolsData">
                                                <el-table-column type="index" label="序号" width="50" align="center" />
                                                <el-table-column property="name" label="工具名称">
                                                    <template #default="scope">
                                                        <div style="display: flex;gap:10px;">
                                                            <img :src="imagePath(scope.row.icon)" style="width:25px;height:25px;border-radius: 5px;" />
                                                            {{ scope.row.name }}
                                                        </div>
                                                    </template>
                                                </el-table-column>
                                                <el-table-column width="100" property="address" label="操作">
                                                    <template #default="scope">
                                                        <el-button type="danger" bg text @click="handleDeleteSelectionTool(scope.$index, scope.row)">
                                                            删除
                                                        </el-button>
                                                </template>
                                                </el-table-column>
                                            </el-table>
                                        </div>

                                    </el-popover>

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
                                    <el-button v-if="voiceInputStatus" type="primary" text bg>参数</el-button>
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
                                    <!-- <el-button v-if="guessWhatYouAskStatus" type="primary" text bg>参数</el-button> -->
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
    <el-dialog title="AI大模型配置" v-model="modelConfigDialogVisible" width="700px">
        <el-form :model="modelConfig" label-width="80" :label-Position="'left'" style="padding:20px;">
            <el-form-item label="AI模型">
                <!-- <el-select v-model="modelConfig.aiModel" placeholder="Select" size="large" style="width:100%">
                    <el-option v-for="item in options" :key="item.value" :label="item.label"
                        :value="item.value" />
                </el-select> -->
                <el-select v-model="modelConfig.aiModel" placeholder="请选择大模型" size="large" style="width:100%">
                    <el-option v-for="item in llmModelOptions" :key="item.id" :label="item.modelName" :value="item.id">
                        <template #default>
                            <div>
                                <img :src="'http://data.linesno.com/icons/llm/' + item.providerCode + '.png'" alt="图标"
                                    style="width: 25px; height: 25px; border-radius: 50%;">
                                {{ item.modelName }}
                            </div>
                        </template>
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="记忆轮数">
                <!-- <el-input v-model="modelConfig.memoryRounds" /> -->
                <el-slider size="large" show-input v-model="modelConfig.memoryRounds" :step="1" />
            </el-form-item>
            <el-form-item label="回复上限">
                <!-- <el-input v-model="modelConfig.replyLimit" /> -->
                <el-slider size="large" show-input v-model="modelConfig.replyLimit" :step="1" />
            </el-form-item>
            <el-form-item label="温度">
                <!-- <el-input v-model="modelConfig.temperature" /> -->
                <el-slider size="large" show-input v-model="modelConfig.temperature" :step="1" />
            </el-form-item>
            <el-form-item label="Top_p">
                <!-- <el-input v-model="modelConfig.topP" /> -->
                <el-slider size="large" show-input v-model="modelConfig.topP" :step="1" />
            </el-form-item>
            <el-form-item label="回复格式">
                <!-- <el-input v-model="modelConfig.replyFormat" size="large" /> -->
                <el-radio-group size="large" v-model="modelConfig.replayFormat">
                    <el-radio value="json">JSON</el-radio>
                    <el-radio value="text">TEXT</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="停止序列">
                <el-input v-model="modelConfig.stopSequences" size="large"
                    placeholder="多个序列号通过 | 隔开，例如：finalAnswer|stop" />
            </el-form-item>
            <el-form-item style="margin-top: 30px;">
                <div style="display: flex;justify-content: flex-end;width: 100%;">
                    <el-button text bg size="large" @click="modelConfigDialogVisible = false">关闭</el-button>
                    <el-button type="primary" @click="onSubmit" size="large" text bg>确认</el-button>
                </div>
            </el-form-item>
        </el-form>
    </el-dialog>


    <el-dialog title="选择知识库" v-model="datasetConfigDialogVisible" width="1024px">
        <div style="margin-bottom:30px">
            <DatasetChocePanel />
        </div>
    </el-dialog>

    <el-dialog title="选择工具" v-model="toolsConfigDialogVisible" width="1024px">
        <div style="margin-bottom:30px">
            <ToolsChocePanel />
        </div>
    </el-dialog>

    <el-dialog title="选择语音" v-model="voiceConfigDialogVisible" width="500px">
        <VoiceChocePanel ref="voiceChocePanelRef" />
    </el-dialog>

    <!-- 用户问题建议 -->
    <el-dialog title="用户问题建议参数" v-model="guessWhatYouAskStatus" width="900px">
        <guessWhatYouAskPanel ref="guessWhatYouAskRef" />
    </el-dialog>

    <!-- 提示词 -->
     <el-dialog title="提示词" v-model="promptDialogVisible" width="800px">
        <promptEditorPanel @syncPromptContent="syncPromptContent" ref="promptEditorPanelRef" />
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


import DatasetChocePanel from '@/views/base/search/vectorData/datasetChoicePanel'
import ToolsChocePanel from '@/views/smart/assistant/plugin/toolsChoicePanel'
import VoiceChocePanel from '@/views/smart/assistant/llmModel/choiceVoicePanel'
import guessWhatYouAskPanel from '@/views/smart/assistant/llmModel/guessWhatYouAskPanel'
import promptEditorPanel from '@/views/smart/assistant/llmModel/promptEditorPanel'

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
const promptDialogVisible = ref(false)

const roleChatPanelRef = ref(null)
const voiceChocePanelRef = ref(null)
const guessWhatYouAskRef = ref(null)
const promptEditorPanelRef = ref(null)

const modelOptions = ref([])
const llmModelOptions = ref([])  // 大模型
const voiceModelOptions = ref([])  // 语音播放模型

// AI大模型配置窗口相关
const modelConfigDialogVisible = ref(false);
const modelConfig = ref({
    modelName: '',
    promptContent: '' ,
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
// 用户问题建议开关
const guessWhatYouAskStatus = ref(false);

// 已选择的数据集数据
const selectionDatasetData = ref([
    { id: 1, icon: 'fa-solid fa-users', name: '客户数据' },
    { id: 2, icon: 'fa-solid fa-chart-line', name: '销售数据' },
    { id: 3, icon: 'fa-solid fa-money-bill-trend-up', name: '财务数据' },
    { id: 4, icon: 'fa-solid fa-users-gear', name: '人事数据' }
]);

// 已选择的工具数据
const selectionToolsData = ref([
    { id: 1, icon: '1882209997121572866', name: '获取客户演示时间' },
    { id: 2, icon: '1882209997121572866', name: '生成客户报告' },
    { id: 3, icon: '1882209997121572866', name: '分析客户反馈' },
    { id: 4, icon: '1882209997121572866', name: '管理客户联系人' },
    { id: 5, icon: '1882209997121572866', name: '安排客户会议' },
    { id: 6, icon: '1882209997121572866', name: '发送客户提醒' },
    { id: 7, icon: '1882209997121572866', name: '评估客户满意度' }
])

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
    toolsConfigDialogVisible.value = true;
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
        console.log(voiceChocePanelRef.value)
        voiceChocePanelRef.value.setVoiceModelOptions(voiceModelOptions.value);
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
        console.log(voiceChocePanelRef.value)
        guessWhatYouAskRef.value.setLlmModelOptions(llmModelOptions.value);
    });
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

        console.log('voiceModelOptions = ' + JSON.stringify(voiceModelOptions.value))

    });
}

/** 打开提示词 */
function openPromptDialog(){
    promptDialogVisible.value = true ;
    console.log('打开提示词:' + modelConfig.value.promptContent)
    nextTick(() => {
        promptEditorPanelRef.value.setPromptContent(modelConfig.value.promptContent);
    });
}

/** 同步提示词 */
function syncPromptContent(content){
    modelConfig.value.promptContent = content ;
    promptDialogVisible.value = false ;
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