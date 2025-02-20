<template>
  <div class="row" style="padding: 20px">
    <el-form :model="formData" :rules="rules" ref="formRef" label-width="90px" :label-position="'left'">

      <el-form-item label="是否启用" prop="isEnable">
        <el-radio-group size="large" v-model="formData.isEnable">
          <el-radio value="true">启用</el-radio>
          <el-radio value="false">不启用</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 语音模型选择项 -->
      <el-form-item label="语音模型" prop="voiceModel">
        <el-select v-model="formData.voiceModel" placeholder="请选择大模型" size="large" style="width:100%">
          <el-option v-for="item in voiceModelOptions" :key="item.id" :label="item.modelName" :value="item.id">
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

      <el-form-item label="语速配置" prop="speechRate">
         <div style="width:100%">
          <el-slider size="large" show-input  v-model="formData.speechRate" :step="1" />
         </div>
      </el-form-item>

      <!-- 提交按钮 -->
      <el-form-item>
        <div style="width: 100%;text-align: right;margin-top:10px">

          <img v-if="isSpeaking" :src="speakingIcon" style="width:35px" />

          <el-button v-if="isSpeaking" type="danger" text bg size="large" @click="listenPlayVoiceOption()"> 
            <i class="fa-regular fa-circle-stop"></i> &nbsp;&nbsp; 停止 
          </el-button>

          <el-button v-if="!isSpeaking" type="primary" text bg size="large" @click="listenPlayVoiceOption()"> 
            <i class="fa-solid fa-headphones-simple"></i> &nbsp;&nbsp; 试听
          </el-button>

        </div>
      </el-form-item>

    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue';

import speakingIcon from '@/assets/icons/speaking.gif';

// 初始化表单数据
const formData = ref({
  voiceModel: '',
  speechRate: 1 // 默认语速为 1
});

// 定义可用的语音模型数组
const voiceModelOptions = ref([]);

const isSpeaking = ref(false)

// 表单验证规则
const rules = ref({
  isEnable: [
    { required: true, message: '请确认是否选择模型', trigger: 'bulr' }
  ],
  voiceModel: [
    { required: true, message: '请选择语音模型', trigger: 'change' }
  ],
  speechRate: [
    { required: true, message: '请输入语速', trigger: 'blur' },
    { type: 'number', message: '语速必须为数字', trigger: 'blur' },
    { min: 1, max: 100, message: '语速范围在 1 - 10 之间', trigger: 'blur' }
  ]
});

// 获取表单实例
const formRef = ref(null);

// 提交表单的方法
const submitForm = async () => {
  const isValid = await formRef.validate();
  if (isValid) {
    console.log('提交的表单数据：', formData.value);
    // 这里可以添加实际的提交逻辑，比如发送请求到后端等
  }
};

const setVoiceModelOptions = (models) => {
  voiceModelOptions.value = models;
}

/** 是否在播放 */
const listenPlayVoiceOption = () => {
  isSpeaking.value = !isSpeaking.value
}

// 重置表单的方法
const resetForm = () => {
  formRef.resetFields();
};

defineExpose({
  setVoiceModelOptions
})

</script>

<style scoped lang="scss"></style>