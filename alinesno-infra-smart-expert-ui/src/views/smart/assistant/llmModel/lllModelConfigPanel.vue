<template>
    <div>
        <el-form :model="modelConfig" :rules="rules" label-width="100" :label-Position="'left'" style="padding:20px" ref="formRef">

            <el-form-item label="是否启用" prop="enabled">
                <el-radio-group size="large" v-model="modelConfig.enabled">
                <el-radio :value="true">启用</el-radio>
                <el-radio :value="false">不启用</el-radio>
                </el-radio-group>
            </el-form-item>

            <el-form-item label="记忆轮数" prop="memoryRounds">
                <el-slider size="large" show-input v-model="modelConfig.memoryRounds" :step="1" :max="30" :input-width="180"  />
            </el-form-item>
            <el-form-item label="回复上限" prop="replyLimit">
                <el-slider size="large" show-input v-model="modelConfig.replyLimit" :step="1" :max="8192" :input-width="180" />
            </el-form-item>
            <el-form-item label="温度" prop="temperature">
                <el-slider size="large" show-input v-model="modelConfig.temperature" :step="0.1" :max="1" :input-width="180" />
            </el-form-item>
            <el-form-item label="Top_p" prop="topP">
                <el-slider size="large" show-input v-model="modelConfig.topP" :step="0.1" :max="1" :input-width="180" />
            </el-form-item>
            <el-form-item label="执行循环数" prop="memoryRounds">
                <el-slider size="large" show-input v-model="modelConfig.maxLoop" :step="1" :max="10" :input-width="180"  />
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
    enabled: false ,
    memoryRounds: 3,
    replyLimit: 2048,
    temperature: 0.7,
    topP: 0.9,
    maxLoop: 5
});

const llmModelOptions = ref([]);
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
        if (field === 'replyLimit' && value > 1280000) {
            return callback(new Error('回复上限不能超过4000'));
        }
        if (field === 'temperature' && value > 10) {
            return callback(new Error('温度不能超过10'));
        }
        if (field === 'topP' && value > 1) {
            return callback(new Error('Top_p不能超过1'));
        }
        if (field === 'replayFormat' && !value && modelConfig.replyFormatEnabled.value) {
            return callback(new Error('请选择回复格式'));
        }
        if (field === 'stopSequences' && !value && modelConfig.stopSequencesEnabled.value) {
            return callback(new Error('请输入停止序列'));
        }
        callback();
    };
};

const rules = {
    enable: [
        { required: true, message: '请选择执行场景', trigger: 'change' }
    ],
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

const setAgentModelParams = (modelParams) => {
    if(modelParams){
        console.log('setAgentModelParams modelParams = ' + JSON.stringify(modelParams));
        modelConfig.value = modelParams
    }
}

defineExpose({ 
    setLlmModelOptions,
    setAgentModelParams
});

</script>