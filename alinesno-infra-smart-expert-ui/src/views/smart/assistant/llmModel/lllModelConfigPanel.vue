<template>
    <div>
        <el-form :model="modelConfig" :rules="rules" label-width="100" :label-Position="'left'" style="padding:20px"
            ref="formRef">
            <el-form-item label="AI模型" prop="aiModel">
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
            <el-form-item label="记忆轮数" prop="memoryRounds">
                <el-slider size="large" show-input v-model="modelConfig.memoryRounds" :step="1" :max="30" />
            </el-form-item>
            <el-form-item label="回复上限" prop="replyLimit">
                <el-slider size="large" show-input v-model="modelConfig.replyLimit" :step="1" :max="4000" />
            </el-form-item>
            <el-form-item label="温度" prop="temperature">
                <el-slider size="large" show-input v-model="modelConfig.temperature" :step="1" :max="10" />
            </el-form-item>
            <el-form-item label="Top_p" prop="topP">
                <el-slider size="large" show-input v-model="modelConfig.topP" :step="0.1" :max="1" />
            </el-form-item>
            <el-form-item label="回复格式" prop="replayFormat">
                <el-row style="width:100%">
                    <el-col :span="8">
                        <el-checkbox v-model="replyFormatEnabled">回复格式设置</el-checkbox>
                    </el-col>
                    <el-col :span="16">
                        <el-radio-group size="large" v-model="modelConfig.replayFormat" v-if="replyFormatEnabled">
                            <el-radio value="json">JSON</el-radio>
                            <el-radio value="text">TEXT</el-radio>
                        </el-radio-group>
                    </el-col>
                </el-row>
            </el-form-item>
            <el-form-item label="停止序列" prop="stopSequences">
                <el-row style="width:100%">
                    <el-col :span="8">
                        <el-checkbox v-model="stopSequencesEnabled">停止序列设置</el-checkbox>
                    </el-col>
                    <el-col :span="16">
                        <el-input v-model="modelConfig.stopSequences" size="large"
                            placeholder="多个序列号通过 | 隔开，例如：finalAnswer|stop" v-if="stopSequencesEnabled" />
                    </el-col>
                </el-row>
            </el-form-item>
            <el-form-item style="margin-top: 10px;">
                <div style="display: flex;justify-content: flex-end;width: 100%;">
                    <el-button text bg size="large" @click="modelConfigDialogVisible = false">关闭</el-button>
                    <el-button type="primary" @click="handleSubmit" size="large" text bg>确认</el-button>
                </div>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

const emit = defineEmits(['setAgentModelConfig'])

const modelConfig = ref({
    aiModel: '',
    memoryRounds: '',
    replyLimit: '',
    temperature: '',
    topP: '',
    replayFormat: '',
    stopSequences: ''
});

const llmModelOptions = ref([]);
const replyFormatEnabled = ref(false);
const stopSequencesEnabled = ref(false);
const modelConfigDialogVisible = ref(false);
const formRef = ref();

const setLlmModelOptions = (options) => {
    llmModelOptions.value = options;
};

const validateCallback = (field) => {
    return (rule, value, callback) => {
        if (field === 'memoryRounds' && value > 30) {
            return callback(new Error('记忆轮数不能超过30'));
        }
        if (field === 'replyLimit' && value > 4000) {
            return callback(new Error('回复上限不能超过4000'));
        }
        if (field === 'temperature' && value > 10) {
            return callback(new Error('温度不能超过10'));
        }
        if (field === 'topP' && value > 1) {
            return callback(new Error('Top_p不能超过1'));
        }
        if (field === 'replayFormat' && !value && replyFormatEnabled.value) {
            return callback(new Error('请选择回复格式'));
        }
        if (field === 'stopSequences' && !value && stopSequencesEnabled.value) {
            return callback(new Error('请输入停止序列'));
        }
        callback();
    };
};

const rules = {
    aiModel: [
        { required: true, message: '请选择AI模型', trigger: 'change' }
    ],
    memoryRounds: [
        { validator: validateCallback('memoryRounds'), trigger: 'change' }
    ],
    replyLimit: [
        { validator: validateCallback('replyLimit'), trigger: 'change' }
    ],
    temperature: [
        { validator: validateCallback('temperature'), trigger: 'change' }
    ],
    topP: [
        { validator: validateCallback('topP'), trigger: 'change' }
    ],
    replayFormat: [
        { validator: validateCallback('replayFormat'), trigger: 'change' }
    ],
    stopSequences: [
        { validator: validateCallback('stopSequences'), trigger: 'change' }
    ]
};

const handleSubmit = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            console.log('modelConfig = ' + JSON.stringify(modelConfig.value));
            emit("setAgentModelConfig" , modelConfig.value);
            ElMessage.success('提交成功');
        } else {
            ElMessage.error('表单验证失败，请检查输入');
        }
    });
};

defineExpose({ 
    setLlmModelOptions 
});

</script>