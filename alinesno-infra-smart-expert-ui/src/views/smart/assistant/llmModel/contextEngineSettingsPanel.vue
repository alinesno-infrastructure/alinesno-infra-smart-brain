<template>
  <div class="row" style="padding: 20px">
    <el-form :model="formData" :rules="rules" ref="formRef" label-width="90px" size="large" :label-position="'top'">

      <el-form-item label="是否启用" prop="enable">
        <el-radio-group size="large" v-model="formData.enable">
          <el-radio :value="true">启用</el-radio>
          <el-radio :value="false">不启用</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 处理策略 -->
      <el-form-item label="处理策略" prop="strategy">
        <el-radio-group size="large" v-model="formData.strategy">
          <el-radio v-for="item in strategyType" :value="item.value" :key="item.key" :label="item.label"></el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 大模型选择项 -->
      <el-form-item label="大模型" prop="llmModel" v-if="formData.strategy === 'llm_summary'">
        <LLMSelector :modelType="'large_language_model'" :size="'large'" v-model="formData.llmModel" />
      </el-form-item>

      <el-form-item label="上下文长度限制" prop="maxContextLength">
        <div class="context-length-selector">
          <el-radio-group v-model="selectedLengthOption" @change="handleLengthOptionChange">
            <el-radio-button v-for="option in lengthOptions" :key="option.value" :label="option.value">
              {{ option.label }}
            </el-radio-button>
          </el-radio-group>
          
          <el-input-number 
            v-if="selectedLengthOption === 'custom'" 
            v-model="formData.maxContextLength" 
            :min="100" 
            :max="300000" 
            :step="100"
            size="large"
            style="margin-top: 10px; width: 200px;"
          />
        </div>
      </el-form-item>

      <el-form-item label="上下文缓存过期时间" prop="cacheExpireMinutes">
          <el-slider size="large" show-input v-model="formData.cacheExpireMinutes" :step="1" :max="60" />
      </el-form-item>

      <el-form-item label="概要提示词" prop="summaryPrompt" v-if="formData.strategy === 'llm_summary'">
         <div style="width:100%">
            <el-input 
                v-model="formData.summaryPrompt" 
                type="textarea" 
                :rows="5" 
                :maxlength="5000"
                show-word-limit
                resize="none" 
                placeholder="请输入内容">
            </el-input>
         </div>
      </el-form-item>

      <el-form-item style="margin-top: 20px;">
          <div style="display: flex;justify-content: flex-end;width: 100%;">
              <el-button type="primary" @click="handleSubmit" size="large" text bg>确认</el-button>
          </div>
      </el-form-item> 

    </el-form>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';

const emit = defineEmits(['handleContextEngineSettingsPanelClose'])
import LLMSelector from '@/views/smart/assistant/workflow/components/LLMSelector'

// // 初始化表单数据
// const formData = ref({
//   enable: true ,
//   strategy: 'llm_summary', // 默认选择大模型总结
//   llmModel: '',
//   summaryPrompt: '- 请总结对话上下文的核心内容和关键信息\n- 保留重要的细节和数据，忽略无关内容\n- 总结应简洁明了，便于快速理解对话要点\n- 确保总结能够支持后续相关问题的回答\n- 保持客观中立，不添加额外解读',
//   maxContextLength: 4096,
//   cacheExpireMinutes: 1,
// });

// 初始化表单数据
const formData = ref({
  enable: true,
  strategy: 'llm_summary', // 默认选择大模型总结
  llmModel: '',
  summaryPrompt: `# 你是一个负责将内部工具调用情况和知识库针对于目标进行的对话总结。            
当对话工作调用过长和知识库变得过长时，你将被动用，将整个历史提炼成简洁、结构化的快照结构。此快照至关重要，因为它将成为智能体*唯一*的过去记忆。智能体将仅基于此快照恢复工作。所有关键细节、计划、错误和用户指示必须被保留。你将在私有的中通盘思考整个历史。回顾用户的总体目标、智能体的操作、工具输出、文件修改以及任何未解决的问题。识别出对未来操作至关重要的每一条信息。
在你的推理完成后，生成最终的对象。信息要极其紧凑。省略任何不相关的对话填充内容。
                        
# 结构必须如下：
状态快照
### 关键知识
- [基于对话历史和与用户的互动，智能体必须记住的关键事实、约定和约束。使用项目符号。]
                            
## 最近调用工具情况
- [对最近几个重要的智能体操作及其结果的总结。专注于事实。]

# 对话目标={{goal}}
# 下面是工具返回的内容={{toolObs}}
# 下面是知识库内容={{knowledge}}`,
  maxContextLength: 4096,
  cacheExpireMinutes: 1,
});

// 上下文长度选项
const lengthOptions = [
  { label: '4k', value: 4096 },
  { label: '8k', value: 8192 },
  { label: '14k', value: 14336 },
  { label: '32k', value: 32768 },
  { label: '128k', value: 131072 },
  { label: '256k', value: 262144 },
  { label: '自定义', value: 'custom' }
];

const selectedLengthOption = ref(32768); // 默认选择32k

// 处理策略类型
const strategyType = ref([
  // {"key": "split", "label": "切割", "value": "split"},
  // {"key": "rag", "label": "RAG知识库", "value": "rag"},
  {"key": "llm_summary", "label": "大模型总结", "value": "llm_summary"},
])

// 定义可用的语音模型数组
const llmModelOptions = ref([]);

// 表单验证规则
const rules = ref({
  enable: [
    { required: true, message: '请确认是否启用', trigger: 'change' }
  ],
  strategy: [
    { required: true, message: '请选择处理策略', trigger: 'change' }
  ],
  llmModel: [
    { required: true, message: '请选择大模型', trigger: 'change' }
  ],
  maxContextLength: [
    { required: true, message: '请设置上下文长度', trigger: 'change' }
  ],
  summaryPrompt: [
    { required: true, message: '请输入概要提示词', trigger: 'blur' },
  ]
});

// 获取表单实例
const formRef = ref(null);

// 处理上下文长度选择变化
const handleLengthOptionChange = (value) => {
  if (value !== 'custom') {
    formData.value.maxContextLength = value;
  }
};

// 监听自定义输入的变化
watch(
  () => formData.value.maxContextLength,
  (newVal) => {
    // 检查新值是否匹配预设选项
    const matchedOption = lengthOptions.find(option => option.value === newVal);
    if (matchedOption) {
      selectedLengthOption.value = newVal;
    } else if (newVal !== undefined && newVal !== null) {
      selectedLengthOption.value = 'custom';
    }
  }
);

const setLlmModelOptions = (models) => {
  llmModelOptions.value = models;
}

const getFormData = () => {
  return formData.value
}

const setConfigParams = (params) => {
  console.log('params 999999>>> = ' + JSON.stringify(params));
  formData.value = params ;
  
  // 同步长度选择器
  const matchedOption = lengthOptions.find(option => option.value === params.maxContextLength);
  selectedLengthOption.value = matchedOption ? params.maxContextLength : 'custom';
}

const handleSubmit = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            console.log('handleContextEngineSettingsPanelClose params = ' + JSON.stringify(formData.value));
            emit("handleContextEngineSettingsPanelClose" , formData.value);
            ElMessage.success('提交成功');
        } else {
            ElMessage.error('表单验证失败，请检查输入');
        }
    });
};

onMounted(() => {
  formData.value.isEnable = true ;
})

defineExpose({
  setLlmModelOptions , 
  getFormData ,
  setConfigParams
})

</script>

<style scoped lang="scss">
.context-length-selector {
  .el-radio-group {
    display: flex;
    flex-wrap: wrap;
    // gap: 10px;
    margin-bottom: 10px;
  }
}
</style>
