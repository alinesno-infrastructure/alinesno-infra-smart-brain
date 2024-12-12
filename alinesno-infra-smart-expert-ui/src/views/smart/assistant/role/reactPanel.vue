<template>
    <div class="app-container">
        <el-page-header @back="goBack" :content="'[' + currentRole.roleName + ']角色推理配置'"></el-page-header>

        <div class="form-container">
            <el-row>
                <el-col :span="12" v-loading="loading" element-loading-text="任务正在生成中，请勿刷新 ..."
                    :element-loading-spinner="svg">

                    <div
                        style="margin-top: 20px;margin-bottom: 20px;display: inline-flex;width: 100%;flex-direction: column;gap: 8px;">

                        <div class="flow-setting-footer" style="margin: 0px;display: flex;gap: 10px;">
                            <el-input v-model="chatMessage" size="large" style="width: 100%;" placeholder="输入需求描述信息." />

                            <el-button type="primary" text bg @click="handleValidateTask()" size="large">
                                <i class="fa-solid fa-truck-fast"></i>&nbsp;验证任务
                            </el-button>
                            <el-button type="danger" bg text @click="submitForm(ruleFormRef)" size="large">
                                <i class="fa-solid fa-paper-plane"></i>&nbsp;提交保存
                            </el-button>
                        </div>

                        <div class="form-panel">
                            <el-form ref="ruleFormRef" :label-position="'top'" :model="ruleForm" :rules="rules"
                                label-width="auto" class="demo-ruleForm" :size="formSize" status-icon>
                                <el-form-item label="角色背景" prop="backstory">
                                    <el-input v-model="ruleForm.backstory" :rows="10" resize="none" type="textarea" />
                                </el-form-item>
                                <el-form-item label="工具选择" prop="tools">
                                    <el-checkbox-group v-model="ruleForm.tools" size="large">
                                        <el-checkbox 
                                            style="margin-bottom:10px"
                                            v-for="item in toolOptions"
                                            :label="item.id" 
                                            :value="item.id"
                                            :key="item.id"
                                            border>
                                            {{ item.name }}
                                        </el-checkbox>
                                    </el-checkbox-group>
                                </el-form-item>
                                <el-form-item label="开场白" prop="greeting">
                                    <el-input v-model="ruleForm.greeting" size="large"
                                        :placeholder="'你好，我是' + currentRole.roleName + '🎉 '" />
                                </el-form-item>
                                <el-form-item label="人类咨询">
                                    <el-checkbox v-model="ruleForm.askHumanHelp" label="人类咨询" :value="'true'" size="large" border /> 
                                </el-form-item>
                            </el-form>
                        </div>

                    </div>

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

import {
    getRoleWithTool,
    saveRoleWithTool,
    validateReActRole
} from "@/api/smart/assistant/role";

import {
    getAllTool
} from "@/api/smart/assistant/tool";

import { ElMessage } from 'element-plus';

import MarkdownIt from 'markdown-it';
const markdown = new MarkdownIt()

const router = useRouter();
const { proxy } = getCurrentInstance();

const genContent = ref(null)
const loading = ref(false)

const currentRole = ref({
    roleName: ''
});
const currentRoleId = ref(null)
const chatMessage = ref("")

const ruleFormRef = ref(null);


const data = reactive({
    ruleForm: {
        backstory: `## 背景
拥有多年的工作经验，是一名高级工程师，非常擅长做相关领域的事项。

## 限制
- 只专注于职责范围内工作相关的事务，拒绝处理与工作安排无关的问题。
- 所输出的内容必须按照给定的格式进行组织，不能偏离框架要求。
- 回复示例中的内容要根据实际情况进行具体填写。`,
        greeting: '',
        tools: []
    },
    rules: {
        roleName: [
            { required: true, message: '请输入角色名称', trigger: 'blur' },
        ],
        backstory: [
            { required: true, message: '请输入角色背景', trigger: 'blur' },
        ],
        responsibilities: [
            { required: true, message: '请输入角色职责', trigger: 'blur' },
        ],
        greeting: [
            { required: true, message: '请输入开场白', trigger: 'blur' },
        ],
        tools: [
            { required: true, message: '请选择至少一个工具', trigger: 'change' },
        ]
    }
});

const { ruleForm, rules } = toRefs(data);

const toolOptions = ref([]);


/** 获取角色信息 */
function getRoleInfo() {

    getRoleWithTool(currentRoleId.value).then(response => {
        currentRole.value.roleName = response.data.roleName ;

        if(response.tools){
            currentRole.value = response.data;
            ruleForm.value = currentRole.value;
            ruleForm.value.tools = response.tools.map(item => item.id);
        }

        // 获取到所有工具类
        handleAllTools();
    });
}

/** 获取到所有工具类 */
function handleAllTools() {
    getAllTool().then(response => {
        if (response.data) {
            toolOptions.value = response.data;
        }
    });
}

/** 验证脚本任务 */
const handleValidateTask = () => {
    const roleId = currentRoleId.value;
    loading.value = true
    
    validateReActRole({
        'roleId': roleId,
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

/** 提交表单 */
function submitForm(formEl) {
    if (!formEl) return;
    formEl.validate((valid) => {
        if (valid) {
            console.log('ruleForm.value= ' + ruleForm.value);

            ruleForm.value.roleId = currentRoleId.value;

            saveRoleWithTool(ruleForm.value).then(res => {
                // 提交表单逻辑
                ElMessage({
                    message: '提交成功',
                    type: 'success',
                });
            })
        }
    });
}


/** 返回 */
function goBack() {
    router.push({ path: '/expert/smart/assistant/role/index' });
}

nextTick(() => {
    currentRoleId.value = router.currentRoute.value.query.roleId;

    getRoleInfo();
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