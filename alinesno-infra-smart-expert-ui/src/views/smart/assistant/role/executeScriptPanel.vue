<template>
    <div class="app-container">
        <el-page-header @back="goBack" :content="'['+ currentRole.roleName +']角色脚本配置'">
        </el-page-header>

        <div class="form-container">
            <el-row>
                <el-col :span="12" 
                    v-loading="loading"
                    element-loading-text="任务正在生成中，请勿刷新 ..."
                    :element-loading-spinner="svg"
                >

                    <div style="margin-top: 20px;margin-bottom: 20px;display: flex;width: 100%;gap:10px;">
                        <el-input v-model="chatMessage" size="large" style="height:40px; width: 100%;" placeholder="输入需求描述信息." />

                        <div class="flow-setting-footer" style="margin: 0px;display: flex;">
                            <el-button type="primary" text bg @click="handleValidateTask()" size="large">
                                <i class="fa-solid fa-truck-fast"></i>&nbsp;验证任务
                            </el-button>
                            <el-button type="danger" bg text @click="submitForm()" size="large">
                                <i class="fa-solid fa-paper-plane"></i>&nbsp;提交保存
                            </el-button>
                        </div>

                    </div>

                    <el-tabs :tab-position="tabPosition" v-model="scriptType" @tab-click="handleTabClick" class="demo-tabs">

                        <el-tab-pane name="execute" label="执行脚本">
                            <ScriptEditorPanel ref="executeEditorRef" :lang="'java'" />
                        </el-tab-pane>
                        <el-tab-pane name="audit" label="意见审核">
                            <ScriptEditorPanel ref="auditEditorRef" :lang="'java'" />
                        </el-tab-pane>
                        <el-tab-pane name="functionCall" label="方法回调">
                            <ScriptEditorPanel ref="functionCallEditorRef" :lang="'java'" />
                        </el-tab-pane>
                        <el-tab-pane name="agentParams" label="参数配置">
                            <ParamsConfigPanel ref="paramsConfigRef" />
                        </el-tab-pane>
                    </el-tabs>

                </el-col>
                <el-col :span="12">
                    <div class="output-result-box">
                        <el-card shadow="never" style="height:calc(100vh - 220px);padding:0px !important">
                            <template #header>
                                <div class="card-header">
                                    <span>执行结果</span>
                                </div>
                            </template>
                            
                            <el-skeleton v-if="!genContent" :rows="10" />
                            <el-scrollbar v-else style="height: calc(-320px + 100vh); padding: 20px;">
                                <div v-html="markdown.render(genContent)"></div>
                            </el-scrollbar>

                        </el-card>
                    </div>
                </el-col>
            </el-row>
        </div>

    </div>
</template>

<script setup>

import { validateRoleScript, updateRoleScript } from '@/api/smart/assistant/role'
import ScriptEditorPanel from './ScriptEditor.vue';
import ParamsConfigPanel from './ParamsConfigPanel.vue';
import {
  getRole
} from "@/api/smart/assistant/role";
// import { getParam } from '@/utils/ruoyi'

import MarkdownIt from 'markdown-it';
const markdown = new MarkdownIt()

const router = useRouter();
const { proxy } = getCurrentInstance();
const tabPosition = ref('right')

const loading = ref(false)

const executeEditorRef = ref(null)
const auditEditorRef = ref(null)
const functionCallEditorRef = ref(null)
const paramsConfigRef = ref(null)

const scriptType = ref("execute")
const genContent = ref(null)

const currentRole = ref({
    roleName:''
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

/** 验证脚本任务 */
const handleValidateTask = () => {
    let type = scriptType.value;
    const code = getCode()[type];
    const roleId = currentRoleId.value;

    loading.value = true
    
    validateRoleScript({
        'script': code,
        'roleId': roleId,
        'type': type,
        'message': chatMessage.value
    }).then(res => {
        console.log('res = ' + res);
        loading.value = false 
        genContent.value = res.data.genContent 
        proxy.$modal.msgSuccess("验证成功");
    }).catch(err => {
        console.log('err = ' + err);
        loading.value = false 
    })
}

/** 提交脚本任务 */
const submitForm = () => {
    let type = scriptType.value;
    const code = getCode()[type];
    const roleId = currentRoleId.value;

    loading.value = true
    
    updateRoleScript({
        'script': code,
        'roleId': roleId,
        'type': type,
        'message': 'save'
    }).then(res => {
        console.log('res = ' + res);
        loading.value = false 
        proxy.$modal.msgSuccess("更新成功");
    }).catch(err => {
        console.log('err = ' + err);
        loading.value = false 
    })
}

/** 返回 */
function goBack() {
    router.push({ path: '/expert/smart/assistant/role/index' });
}

/** 获取角色信息 */
function getRoleInfo() {
    currentRoleId.value = router.currentRoute.value.query.roleId; 
    getRole(currentRoleId.value).then(response => {
        currentRole.value = response.data;

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