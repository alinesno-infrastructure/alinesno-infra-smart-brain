<template>
    <div class="app-container agent-inference-container">

        <div class="page-header-container">
            <el-page-header @back="goBack">
                <template #content>
                    <div style="display: flex;gap: 10px;align-items: center;">
                        <span style="color: #aaaaaa;font-size: 14px;">更新时间：2025-02-14 23:50:44</span>
                    </div>
                </template>
            </el-page-header>
            <div>
                <!-- <el-button type="warning" bg @click="validateRoleScript" text size="large">验证脚本</el-button> -->
                <!-- <el-button type="primary" bg @click="submitModelConfig" text size="large">保存配置</el-button> -->
                <el-button type="primary" icon="Setting" text bg size="large" @click="submitModelConfig()">保存配置</el-button>
                <el-button type="danger" icon="Position" text bg size="large" @click="publishRoleConfig()">发布角色</el-button>
            </div>
        </div>

        <el-row :gutter="20">
            <!--类型数据-->
            <el-col :span="12" :xs="24">
                <el-scrollbar class="scrollbar-style">
                    <el-card class="box-card" shadow="never">
                        <el-row>
                            <el-col :span="24">
                                <div style="display: flex;align-items: center;gap: 8px;margin-bottom: 10px;">
                                    <span>
                                        <img :src="imagePath(currentRole.roleAvatar)" style="width:30px;height:30px;border-radius:5px;" />
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

                        <el-tabs :tab-position="tabPosition" v-model="scriptType" @tab-click="handleTabClick" class="constom-script-tabs">

                            <el-tab-pane name="params" label="参数配置">
                                <ParamsConfigPanel ref="paramsConfigRef" @submitModelConfig="submitModelConfig" />
                            </el-tab-pane>

                            <el-tab-pane name="execute" label="角色脚本">
                                <ScriptEditorPanel ref="executeEditorRef" :lang="'java'" />
                            </el-tab-pane>

                            <el-tab-pane name="audit" label="意见审核">
                                <ScriptEditorPanel ref="auditEditorRef" :lang="'java'" />
                            </el-tab-pane>

                            <el-tab-pane name="functionCall" label="角色执行">
                                <ScriptEditorPanel ref="functionCallEditorRef" :lang="'java'" />
                            </el-tab-pane>

                        </el-tabs>


                    </el-card>
                </el-scrollbar>
            </el-col>

            <!--类型数据-->
            <el-col :span="12" :xs="24">
                <el-card shadow="never" class="agent-chat-box">
                    <RoleChatPanel ref="roleChatPanelRef" />
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>

import { 
    validateRoleScript, 
    updateRoleScript,
} from '@/api/smart/assistant/role'

import ScriptEditorPanel from './ScriptEditor.vue';
import ParamsConfigPanel from './ParamsConfigPanel.vue';

import RoleChatPanel from '@/views/smart/assistant/role/chat/index';

import {
    getRole
} from "@/api/smart/assistant/role";

/** 返回 */
function goBack() {
    router.back();
}

import MarkdownIt from 'markdown-it';
const markdown = new MarkdownIt()

const router = useRouter();
const { proxy } = getCurrentInstance();
const tabPosition = ref('left')

const loading = ref(false)

const executeEditorRef = ref(null)
const auditEditorRef = ref(null)
const paramsConfigRef = ref(null)
const functionCallEditorRef = ref(null)
const roleChatPanelRef = ref(null)

const scriptType = ref("params")
const genContent = ref(null)

const currentRole = ref({
    roleName: ''
});
const currentRoleId = ref(null)
const chatMessage = ref("")

/** 获取代码 */
const getCode = () => {
    return {
        execute: executeEditorRef.value.getRawScript(),
        audit: auditEditorRef.value.getRawScript(),
        functionCall: functionCallEditorRef.value.getRawScript()
    }
}

/** 设置当前角色 */
function setCurrentRoleId(id) {
    currentRoleId.value = id;
}

/** 提交脚本任务 */
const submitModelConfig = async() => {

    const { valid, formData } = await paramsConfigRef.value.validateForm();
    if (!valid) {
        console.log('formData = ' + JSON.stringify(formData));
        return 
    }

    const type = scriptType.value && scriptType.value !== 'params'?scriptType.value:'execute' ;
    console.log('type = ' + type);

    const scriptCode = getCode()[type];
    const roleId = currentRoleId.value;

    loading.value = true

    const agentConfigParams = paramsConfigRef.value.getAgentConfigParams();
    console.log('agentConfigParmas = ' + JSON.stringify(agentConfigParams));

    updateRoleScript({
        'agentConfigParams': agentConfigParams,
        'script': scriptCode,
        'roleId': roleId,
        'type': type,
        'message': 'save'
    }).then(res => {
        console.log('res = ' + res);
        loading.value = false
        proxy.$modal.msgSuccess("更新成功");
        getRoleInfo();
    }).catch(err => {
        console.log('err = ' + err);
        loading.value = false
    })

}

/** 返回 */
// function goBack() {
//     router.push({ path: '/expert/smart/assistant/role/index' });
// }

/** 获取角色信息 */
function getRoleInfo() {
    currentRoleId.value = router.currentRoute.value.query.roleId;
    getRole(currentRoleId.value).then(response => {
        currentRole.value = response.data;

        roleChatPanelRef.value.setRoleInfo(currentRole.value)

        // 设置脚本
        executeEditorRef.value.setRawScript(currentRole.value.executeScript);
        auditEditorRef.value.setRawScript(currentRole.value.auditScript);
        functionCallEditorRef.value.setRawScript(currentRole.value.functionCallbackScript);
    });
}

/** 切换tab */
function handleTabClick(tab, event) {
    console.log('tab = ' + JSON.stringify(tab))
    scriptType.value = tab.props.name
    console.log('type = ' + scriptType.value)
}

nextTick(() => {
    getRoleInfo();
})

defineExpose({
    setCurrentRoleId
})

</script>

<style scoped lang="scss">
.flow-setting-footer {
    float: right;
    margin-bottom: 20px;
}

.output-result-box {
    padding: 20px;
    margin-left: 20px;

    .card-header {
        padding: 10px;
    }
}

</style>

<style lang="css">
.constom-script-tabs .el-tabs__nav{
    height: auto !important ; 
}
</style>