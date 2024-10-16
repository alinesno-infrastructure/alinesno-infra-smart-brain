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

                <el-form :model="form" :rules="rules" label-width="120px" style="max-width: 980px" ref="ruleForm"  >
                    <el-form-item label="节点名称" prop="name">
                        <el-input v-model="form.name" :value="node.name" disabled="disabled" placeholder="请输入节点名称" />
                    </el-form-item>
                    <el-form-item label="请求方式" prop="imType">
                        <el-radio-group v-model="form.imType">
                            <el-radio v-for="option in imOptions" :key="option.label" :label="option.value">
                                {{ option.label }}
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-row  v-if="form.imType === 'wechat_work'">
                        <el-col :span="24">
                            <el-form-item label="机器人Key" prop="wechatKey">
                                <el-input v-model="form.wechatKey" placeholder="请输入企业微信机器人Key" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row v-if="form.imType === 'email'">
                        <el-col :span="24">
                            <el-form-item label="通知邮件" prop="email">
                                <el-input v-model="form.email" placeholder="请输入接收邮箱，以英文逗号分隔" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-form-item label="通知内容">
                        <CodeEditor ref="codeEditorRef" :lang="'markdown'" />
                    </el-form-item>
                </el-form>

                <div class="flow-setting-footer">
                    <el-button type="primary" bg  @click="submitForm('ruleForm')">确认保存</el-button>
                    <el-button @click="onClose" size="large" text bg>取消</el-button>
                </div>
            </div>
        </div>
        <FlowDrawerFooter @close="onClose" @save="onSave" />
    </a-drawer>
</template>

<script setup>

import flowNodeStore from '@/store/modules/flowNode'
import { branchIcon2 } from '@/utils/flowMixin';
import CodeEditor from '../../CodeEditor.vue';

const { proxy } = getCurrentInstance();

const codeEditorRef = ref(null)
const node = ref({})
const visible = ref(false)
const headerStyle = ref({
    'background-image': 'linear-gradient(90deg, #268bfb -16.37%, #33e1ae 137.34%), linear-gradient(#0084FF, #3b5998)',
    'border-radius': '0px 0px 0 0',
})

const imOptions = ref([
    { label: '企业微信', value: 'wechat_work' },
    { label: '邮件', value: 'email' },
])

const data = reactive({
    form: {
        name: '',
        imType: 'wechat_work',
        wechatKey: '',
        email: '',
        noticeContent: '',
    },
    rules: {
        imType: [
            { required: true, message: '请选择通知方式', trigger: 'blur' }
        ],
        wechatKey: [
            { required: true, message: '请输入企业微信机器人Key', trigger: 'blur' }
        ],
        email: [
            { required: true, message: '请输入接收邮箱，以英文逗号分隔', trigger: 'blur' }
        ],
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

            form.value.noticeContent = codeEditorRef.value.getRawScript() 

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
 * 打开侧边栏 
 * @param {*} node 
 * @param {*} routeNode 
 */
function showDrawer(_node) {
    console.log('node = ' + _node.name)

    visible.value = true;
    node.value = _node;
    form.value.name = _node.name;
    
}

/**
 * 关闭侧边栏
 */
function onClose() {
    visible.value = false;
}

defineExpose({ showDrawer })

</script>
