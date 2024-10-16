<template>
    <a-drawer :headerStyle="headerStyle" :bodyStyle="bodyStyle" :closable="true" :visible="visible"
        :after-visible-change="afterVisibleChange" width="50%" placement="right" @close="onClose">
        <template #title>
            <img :src="branchIcon2" class="anticon" />
            <span class="flow-ant-drawer-title">
                {{ node.name }}
            </span>
        </template>
        <div class="flow-setting-module">
            <div class="flow-setting-content">

                <el-form :model="form" :rules="rules" label-width="auto" style="max-width: 980px" ref="ruleForm">
                    <el-form-item label="节点名称" prop="name">
                        <el-input v-model="form.name" disabled="disabled" placeholder="请输入节点名称" />
                    </el-form-item>
                    <!-- <el-form-item label="环境名称" prop="env">
                        <el-select v-model="form.env" placeholder="选择执行环境" style="width:100%">
                            <el-option
                                v-for="item in envData"
                                :key="item.id"
                                :label="'(' + item.systemEnv + ')' + item.name"
                                :value="item.id"
                                :systemEnv="item.systemEnv"
                            />
                        </el-select>
                    </el-form-item> -->
                    <el-form-item label="脚本">
                        <CodeEditor ref="codeEditorRef" :lang="python" />
                    </el-form-item>
                    <el-form-item label="资源" prop="resourceId">
                        <!-- <el-input v-model="form.resourceId" placeholder="请选择资源" /> -->
                        <el-tree-select v-model="form.resourceId" :data="resourceData" multiple placeholder="请选择资源"
                            :render-after-expand="false" style="width: 500px" />
                    </el-form-item>
                    <el-form-item label="自定义参数">
                        <el-button type="primary" bg text @click="paramsDialog = true">
                            <i class="fa-solid fa-screwdriver-wrench"></i>&nbsp;配置任务参数
                        </el-button>
                    </el-form-item>
                </el-form>

                <div class="flow-setting-footer">
                    <el-button type="primary" text bg @click="handleValidateTask()"><i class="fa-solid fa-truck-fast"></i>&nbsp;验证任务</el-button>
                    <el-button type="primary" bg @click="submitForm('ruleForm')"><i class="fa-solid fa-paper-plane"></i>&nbsp;确认保存</el-button>
                    <el-button @click="onClose" size="large" text bg>取消</el-button>
                </div>
            </div>
        </div>

        <el-dialog title="任务环境变量" v-model="paramsDialog" append-to-body destroy-on-close class="scrollbar">
            <ContextParam ref="taskParamRef" :context="form.context" />
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="paramsDialog = false">取消</el-button>
                    <el-button type="primary" @click="callTaskParamRef()">
                        确认
                    </el-button>
                </div>
            </template>
        </el-dialog>

        <FlowDrawerFooter @close="onClose" @save="onSave" />
    </a-drawer>
</template>

<script setup>

import { ElLoading } from 'element-plus'
import flowNodeStore from '@/store/modules/flowNode'

// import { getAllResource } from '@/api/data/scheduler/resource'
import { validateTask } from '@/api/smart/assistant/processDefinition'
import { branchIcon2 } from '@/utils/flowMixin';
import CodeEditor from '../../CodeEditor.vue';
// import ContextParam from "../../../params/contextParam.vue";

const { proxy } = getCurrentInstance();

const codeEditorRef = ref(null)
const node = ref({})
const visible = ref(false)
const headerStyle = ref({
    'background-image': 'linear-gradient(90deg, #ff6a00, #f78b3e), linear-gradient(#ff6a00, #ff6a00)',
    'border-radius': '0px 0px 0 0',
})

const paramsDialog = ref(false)
const taskParamRef = ref(null)
const resourceData = ref([])
// const envData = ref([])

const data = reactive({
    form: {
        name: '',
        desc: '',
        delivery: false,
        retryCount: 0,
        env: '',
        rawScript: '',
        resourceId: [],
        customParams: {}
    },
    rules: {
        name: [
            { required: true, message: '请输入节点名称', trigger: 'blur' },
            { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
        ],
        desc: [
            { required: true, message: '请输入节点描述', trigger: 'blur' },
            { min: 10, max: 200, message: '长度在 10 到 200 个字符', trigger: 'blur' }
        ],
        delivery: [
            { required: true, message: '请选择超时告警', trigger: 'change' }
        ],
        retryCount: [
            { required: true, message: '请选择失败重试次数', trigger: 'change' }
        ],
        env: [
            { required: true, message: '请选择环境名称', trigger: 'change' }
        ],
        // resourceId: [
        //     { required: true, message: '请选择资源', trigger: 'blur' }
        // ],
        customParams: [
            { required: true, message: '请输入自定义参数', trigger: 'blur' }
        ]
    }
});

const { form, rules } = toRefs(data);

/**
 * 提交表单
 * @param {*} formName
 */
const submitForm = (formName) => {
    const formInstance = proxy.$refs[formName];
    formInstance.validate((valid) => {
        if (valid) {

            form.value.rawScript = codeEditorRef.value.getRawScript()

            // 更新节点信息
            node.value.params = form.value;
            flowNodeStore().setNode(node.value);
            onClose();
        } else {
            console.log('验证失败!');
            return false;
        }
    });
};

/**
 * 获取到环境变量值
 */
function callTaskParamRef() {
    let contextParam = taskParamRef.value.getEnvVarsAsJson();
    form.value.customParams = contextParam;
    paramsDialog.value = false;

    console.log(JSON.stringify(contextParam, null, 2));
}

/** 验证脚本任务 */
function handleValidateTask() {

    const loading = ElLoading.service({
        lock: true,
        text: 'Loading',
        background: 'rgba(0, 0, 0, 0.7)',
    })

    const formDataStr = localStorage.getItem('processDefinitionFormData');
    const formData = JSON.parse(formDataStr);
    form.value.rawScript = codeEditorRef.value.getRawScript()
    let data = {
        taskParams: form.value,
        taskType: node.value.type,
        context: formData
    }

    // 提交流程信息
    validateTask(data).then(response => {
        console.log(response);
        proxy.$modal.msgSuccess("任务执行成功,无异常.");
        loading.close();
    }).catch(error => {
        loading.close();
    })
}

/**
 * 打开侧边栏
 * @param {*} node
 * @param {*} routeNode
 */
function showDrawer(_node) {
    console.log('node = ' + JSON.stringify(_node))

    visible.value = true;
    node.value = _node;
    form.value.name = _node.name;

    nextTick(() => {
        // 获取所有环境
        // getAllEnvironment().then(res => {
        //     envData.value = res.data
        //     // nextTick(() => {
        //     //     form.value.env = res.defaultEnvId
        //     // })
        // })
        // 获取所有资源
        getAllResource().then(res => {
            resourceData.value = res.data
        })
    })
}

/**
 * 关闭侧边栏
 */
function onClose() {
    visible.value = false;
}

defineExpose({ showDrawer })

</script>
