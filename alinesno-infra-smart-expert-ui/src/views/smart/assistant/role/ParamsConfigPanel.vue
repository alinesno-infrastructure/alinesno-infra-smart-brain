<template>
    <div>
        <!-- AI配置界面 -->
        <el-card class="box-card" shadow="never"  :style="'height:calc(100vh - ' + props.diffHeight + 'px)'">

            <el-form ref="agentModelConfigFormRef" :model="agentModelConfigForm" :rules="agentModelConfigRules">

                <div class="clearfix">
                    <span class="box-card-title">AI配置</span>
                </div>

                <!-- <el-row class="nav-row">
                    <el-col :span="24">
                        <div class="ai-config-section-title">
                            <i class="fa-solid fa-comment-slash"></i> 对话开场白
                        </div>
                    </el-col>
                    <el-col :span="24">
                        <div class="input-wrapper">
                            <el-form-item prop="greeting" class="form-item-wrapper">
                                <el-input maxlength="100" v-model="agentModelConfigForm.greeting" show-word-limit
                                    type="textarea" resize="none" :rows="2"
                                    placeholder="每次对话开始前，发送一个初始内容。支持标准 Markdown 语法，可使用的额外标记：[快捷按键]：用户点击后可以直接发送该问题"></el-input>
                            </el-form-item>
                        </div>
                    </el-col>
                </el-row> -->

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
                            <el-button v-if="voiceInputStatus" type="primary" text bg
                                @click="toggleVoiceInputStatusPanel">参数</el-button>
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

                <!-- <el-row class="nav-row">
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
                            <el-button v-if="guessWhatYouAskStatus" type="primary" text bg
                                @click="toggleGuessWhatYouAskStatusPanel">参数</el-button>
                        </div>
                    </el-col>
                </el-row> -->
            </el-form>

        </el-card>

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
            <el-dialog title="开场白预置问题" v-model="openingPhraseStatusVisible" width="500px">
                <OpeningPhraseStatusPanel @handleOpeningPhraseStatusPanelClose="handleOpeningPhraseStatusPanelClose" ref="openingPhraseStatusPanelRef" />
            </el-dialog>

    </div>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { ElMessage } from 'element-plus'

import {
    getRole
} from "@/api/smart/assistant/role";

import {
    listAllLlmModel
} from "@/api/smart/assistant/llmModel"

const emit = defineEmits(['submitModelConfig'])

const props = defineProps({
    diffHeight: {
        type: Number,
        default: 295
    }
})

// import DatasetChoicePanel from '@/views/base/search/vectorData/datasetChoicePanel'
// import DatasetParamsChoicePanel from '@/views/base/search/vectorData/datasetParamsChoicePanel'
// import ToolsChoicePanel from '@/views/smart/assistant/plugin/toolsChoicePanel'
// import VoiceChoicePanel from '@/views/smart/assistant/llmModel/choiceVoicePanel'
// import LllModelConfigPanel from '@/views/smart/assistant/llmModel/lllModelConfigPanel'
// import guessWhatYouAskPanel from '@/views/smart/assistant/llmModel/guessWhatYouAskPanel'
// import promptEditorPanel from '@/views/smart/assistant/llmModel/promptEditorPanel'
// import VoiceInputStatusPanel from '@/views/smart/assistant/llmModel/voiceInputStatusPanel'

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


// AI大模型配置窗口相关
const agentModelConfigFormRef = ref(null)

// 已选择的数据集数据
const selectionDatasetData = ref([]);

const router = useRouter();
const { proxy } = getCurrentInstance();
const currentRole = ref({
    roleName: ''
});

// const modelConfigDialogVisible = ref(false);
// const datasetConfigDialogVisible = ref(false)
// const datasetParamsConfigDialogVisible = ref(false)
// const toolsConfigDialogVisible = ref(false)
// const voiceConfigDialogVisible = ref(false)
// const promptDialogVisible = ref(false)
// const voiceInputStatusVisible = ref(false)
// const guessWhatYouAskStatusVisible = ref(false)

// const llmModelConfigPanelRef = ref(null)
// const datasetChoicePanelRef = ref(null)
// const toolsChoicePanelRef = ref(null)
// const roleChatPanelRef = ref(null)
// const voiceChoicePanelRef = ref(null)
// const guessWhatYouAskRef = ref(null)
// const promptEditorPanelRef = ref(null)
// const voiceInputStatusPanelRef = ref(null)

// const modelOptions = ref([])
// const llmModelOptions = ref([])  // 大模型
// const voiceModelOptions = ref([])  // 语音播放模型
// const voiceRecoModelOptions = ref([])  // 语音播放模型

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

const agentModelConfigForm = ref({
    roleId: 0,
    // modelId: 0,
    // promptContent: '',
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

// 表单校验规则
// const agentModelConfigRules = ref({
//     // modelId: [
//     //     { required: true, message: '请确认是否选择模型', trigger: 'bulr' }
//     // ],
//     greeting: [
//         { required: true, message: '请确认是否填写欢迎语', trigger: 'bulr' }
//     ]
// });

// 长期记忆开关
// const longTermMemoryEnabled = ref(false);
// // 语音播放开关
// const voicePlayStatus = ref(false);
// // 语音输入开关
// const voiceInputStatus = ref(false);
// // 用户问题建议开关
// const guessWhatYouAskStatus = ref(false);

// 长期记忆开关
const longTermMemoryEnabled = ref(false);
// 语音播放开关
const voicePlayStatus = ref(false);
// 语音输入开关
const voiceInputStatus = ref(false);
// 用户问题建议开关
const guessWhatYouAskStatus = ref(false);

// 已选择的数据集数据
// const selectionDatasetData = ref([]);

// 已选择的工具数据
const selectionToolsData = ref([])

// 打开AI大模型配置窗口
// function openModelConfigDialog() {
//     modelConfigDialogVisible.value = true;

//     nextTick(() => {
//         llmModelConfigPanelRef.value.setLlmModelOptions(llmModelOptions.value);
//         llmModelConfigPanelRef.value.setAgentModelParams(agentModelConfigForm.value.modelConfig)
//     });
// }

// 切换语音输入状态
function toggleVoiceInput() {

    console.log('voiceRecoModelOptions.value.length  = ' + voiceRecoModelOptions.value.length)

    if(voiceRecoModelOptions.value.length == 0){
        ElMessage.error('请先配置语音识别模型');
        return ;
    }

    voiceInputStatus.value = !voiceInputStatus.value;
    agentModelConfigForm.value.voiceInputStatus = voiceInputStatus.value;

    // 更新角色内容
    submitModelConfig();
}

// 配置语音输入参数 
// function toggleVoiceInputStatusPanel() {
//     voiceInputStatusVisible.value = !voiceInputStatusVisible.value;
//     voiceInputStatusVisible.value && nextTick(() => {
//         console.log(voiceChoicePanelRef.value)
//         voiceInputStatusPanelRef.value.setVoiceModelOptions(voiceRecoModelOptions.value);

//         if(agentModelConfigForm.value.voiceInputData){
//             voiceInputStatusPanelRef.value.setConfigParams(agentModelConfigForm.value.voiceInputData);
//         }
//     });
// }

// 切换长期记忆状态
function toggleLongTermMemory() {
    longTermMemoryEnabled.value = !longTermMemoryEnabled.value;
    agentModelConfigForm.value.longTermMemoryEnabled = longTermMemoryEnabled.value;

    // 更新角色内容
    submitModelConfig();
}

// 打开语音播放窗口
function toggleVoicePlayStatus() {

    if(voiceModelOptions.value.length == 0){
        ElMessage.error('请先配置语音模型');
        return;
    }

    voicePlayStatus.value = !voicePlayStatus.value;
    agentModelConfigForm.value.voicePlayStatus = voicePlayStatus.value;

    // 更新角色内容
    submitModelConfig();
}

// 设置语音播放窗口参数
function handleVoiceConfigParams(formData) {
    agentModelConfigForm.value.voicePlayData = formData;
    voiceConfigDialogVisible.value = false;

    // 更新角色内容
    submitModelConfig();
}

// 切换语音播放状态
function toggleVoicePlayback() {

    if (voiceModelOptions.value.length == 0) {
        ElMessage.error('请先配置语音模型');
        return;
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


// 切换用户问题建议状态
function toggleGuessWhatYouAsk() {
    guessWhatYouAskStatus.value = !guessWhatYouAskStatus.value;
    agentModelConfigForm.value.guessWhatYouAskStatus = guessWhatYouAskStatus.value;

    // 更新角色内容
    submitModelConfig();
}

// 用户问题建议窗口配置
// function toggleGuessWhatYouAskStatusPanel() {
//     guessWhatYouAskStatusVisible.value = true;
//     nextTick(() => {
//         console.log(voiceChoicePanelRef.value)
//         guessWhatYouAskRef.value.setLlmModelOptions(llmModelOptions.value);
//         console.log('--->>> agentModelConfigForm.value.guessWhatYouAskData = ' + JSON.stringify(agentModelConfigForm.value.guessWhatYouAskData))
//         if(agentModelConfigForm.value.guessWhatYouAskData){
//             guessWhatYouAskRef.value.setConfigParams(agentModelConfigForm.value.guessWhatYouAskData);
//         }
//     });
// }

// 用户问题建议窗口相关
// function handleGuessWhatYouAskStatusPanelClose(formData) {
//     if (guessWhatYouAskRef.value) {
//         guessWhatYouAskStatusVisible.value = false ;

//         // const formData = guessWhatYouAskRef.value.getFormData();
//         console.log('handleGuessWhatYouAskStatusPanelClose formData = ' + JSON.stringify(formData))
//         guessWhatYouAskStatus.value = formData.enable; 
//         agentModelConfigForm.value.guessWhatYouAskData = formData;
//     }
// }

/** 获取角色信息 */
function getRoleInfo() {
    currentRoleId.value = router.currentRoute.value.query.roleId;

    getRole(currentRoleId.value).then(response => {
        currentRole.value = response.data;
        // roleChatPanelRef.value.setRoleInfo(currentRole.value)
        displayRoleInfoBack(currentRole.value);
    });

}

/** 回显角色信息 */
const displayRoleInfoBack = (currentRole) =>{

    // 回显当前角色信息
    agentModelConfigForm.value.roleId = currentRole.id ;
    // agentModelConfigForm.value.modelId = currentRole.modelId;
    // agentModelConfigForm.value.promptContent = currentRole.promptContent;
    agentModelConfigForm.value.greeting = currentRole.greeting;

    // 长期记忆
    longTermMemoryEnabled.value = currentRole.longTermMemoryEnabled ;
    agentModelConfigForm.value.longTermMemoryEnabled = currentRole.longTermMemoryEnabled ;

    if(currentRole.knowledgeBaseIds){
        let datasetParsedArray = null ; 
        if(typeof currentRole.knowledgeBaseIds === 'object'){
            datasetParsedArray = currentRole.knowledgeBaseIds;
        }else{
            datasetParsedArray = JSON.parse(currentRole.knowledgeBaseIds);
        }
        selectionDatasetData.value = datasetParsedArray;
        agentModelConfigForm.value.knowledgeBaseIds = datasetParsedArray ;
    }

    if(currentRole.selectionToolsData){
        const toolsParsedArray = JSON.parse(currentRole.selectionToolsData);
        selectionToolsData.value = toolsParsedArray;
        agentModelConfigForm.value.selectionToolsData = toolsParsedArray ; 
    }

    // 语音输入
    voiceInputStatus.value = currentRole.voiceInputStatus ;
    agentModelConfigForm.value.voiceInputStatus = currentRole.voiceInputStatus ;
    if(currentRole.voiceInputData){
        // agentModelConfigForm.value.voiceInputData = JSON.parse(currentRole.voiceInputData);
        if(typeof currentRole.voiceInputData === 'object'){
            agentModelConfigForm.value.voiceInputData = currentRole.voiceInputData;
        }else{
            agentModelConfigForm.value.voiceInputData = JSON.parse(currentRole.voiceInputData);
        }
    }

    // 语音播放
    voicePlayStatus.value = currentRole.voicePlayStatus ;
    agentModelConfigForm.value.voicePlayStatus = currentRole.voicePlayStatus ;
    if(currentRole.voicePlayData){
        if(typeof currentRole.voicePlayData === 'object'){
            agentModelConfigForm.value.voicePlayData = currentRole.voicePlayData;
        }else{
            agentModelConfigForm.value.voicePlayData = JSON.parse(currentRole.voicePlayData);
        }
    }

    // 用户问题建议
    guessWhatYouAskStatus.value = currentRole.guessWhatYouAskStatus ;
    agentModelConfigForm.value.guessWhatYouAskStatus = currentRole.guessWhatYouAskStatus ;
    if(currentRole.guessWhatYouAskData){
        console.log('currentRole.guessWhatYouAskData = ' + currentRole.guessWhatYouAskData)
        // 判断currentRole.guessWhatYouAskData是否为object对象
        if(typeof currentRole.guessWhatYouAskData === 'object'){
            agentModelConfigForm.value.guessWhatYouAskData = currentRole.guessWhatYouAskData;
        }else{
            agentModelConfigForm.value.guessWhatYouAskData = JSON.parse(currentRole.guessWhatYouAskData);
        }
    }

    // 模型配置
    if(currentRole.modelConfig){
        agentModelConfigForm.value.modelConfig = JSON.parse(currentRole.modelConfig);
    }

    // 附件上传
    if(currentRole.uploadStatus){
        uploadStatus.value = currentRole.uploadStatus ;
        agentModelConfigForm.value.uploadStatus = currentRole.uploadStatus ;
        if(typeof currentRole.uploadData === 'object'){
            agentModelConfigForm.value.uploadData = currentRole.uploadData;
        }else {
            agentModelConfigForm.value.uploadData = JSON.parse(currentRole.uploadData);
        }
    }

    //  对话开场白
    if(currentRole.greetingQuestion){
        agentModelConfigForm.value.greetingQuestion = currentRole.greetingQuestion ;
    }

    console.log('agentModelConfigForm = ' + JSON.stringify(agentModelConfigForm.value));
}

/** 列出所有模型列表 */
const handleListAllLlmModel = async () => {
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

        voiceInputStatus.value = voiceInputData.enable; // !voiceInputStatus.value;
        agentModelConfigForm.value.voiceInputStatus = voiceInputData.enable;// voiceInputStatus.value;

        voiceInputStatusVisible.value = false ;
        agentModelConfigForm.value.voiceInputData = voiceInputData;

        // 更新配置
        submitModelConfig();
    }
}

/** 列出所有模型列表 */
// const handleListAllLlmModel = async() => {
//     listAllLlmModel().then(res => {
//         modelOptions.value = res.data;

//         llmModelOptions.value = res.data.filter(item => item.modelType === 'large_language_model');
//         voiceModelOptions.value = res.data.filter(item => item.modelType === 'speech_synthesis');
//         voiceRecoModelOptions.value = res.data.filter(item => item.modelType === 'speech_recognition');
//         multiModelOptions.value = res.data.filter(item => item.modelType === 'vision_model');
//         ocrModelOptions.value = res.data.filter(item => item.modelType === 'ocr_model');

//         console.log('voiceModelOptions = ' + JSON.stringify(voiceModelOptions.value))

//     });
// }

/** 打开提示词 */
function openPromptDialog() {
    promptDialogVisible.value = true;
    console.log('打开提示词:' + agentModelConfigForm.value.promptContent)
    nextTick(() => {
        promptEditorPanelRef.value.setPromptContent(agentModelConfigForm.value.promptContent);
    });
}

// 附件上传_start 
// function toggleUploadStatus() {
//     uploadStatus.value = !uploadStatus.value;
//     agentModelConfigForm.value.uploadStatus = uploadStatus.value;

//     // submitModelConfig();
// }

/** 同步提示词 */
function syncPromptContent(content) {
    agentModelConfigForm.value.promptContent = content;
    promptDialogVisible.value = false;
}

// 更新配置
function submitModelConfig() {
    console.log('submitModelConfig agentModelConfigForm.value = ' + JSON.stringify(agentModelConfigForm.value))
    emit('submitModelConfig') ; 
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

nextTick(() => {
    initData();
})

const initData = async () => {
    try {
        await handleListAllLlmModel();
        getRoleInfo();
    } catch (error) {
        console.error('handleListAllLlmModel 请求出错:', error);
    }
}

// 定义表单验证方法
const validateForm = () => {
    return new Promise((resolve) => {
    // 调用表单的 validate 方法进行验证
    agentModelConfigFormRef.value.validate((valid, fields) => {
        if (valid) {
        // 验证成功，打印日志并解析包含验证成功和表单数据的对象
        console.log('--> agentModelConfigForm = ' + JSON.stringify(agentModelConfigFormRef.value));
        resolve({ valid: true, formData: agentModelConfigFormRef.value });
        } else {
        // 验证失败，打印日志并解析包含验证失败和 null 表单数据的对象
        console.log('--> error agentModelConfigForm = ' + JSON.stringify(agentModelConfigFormRef.value));
        resolve({ valid: false, formData: null });
        }
    });
    });
};

const getAgentConfigParams = () => {
    return agentModelConfigForm.value ; 
}

defineExpose({
    getAgentConfigParams,
    validateForm,
})

</script>

<style lang="scss" scoped>
.nav-row {
    width: 100%;

    .form-item-wrapper {
        width: 100%;
    }
}
</style>
