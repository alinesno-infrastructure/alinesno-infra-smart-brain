<template>
  <div class="row">
    <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
      <!-- 语音模型选择项 -->
      <el-form-item label="语音模型" prop="voiceModel">
        <el-select style="width:100%" v-model="formData.voiceModel" placeholder="请选择语音模型">
          <el-option
            v-for="model in voiceModels"
            :key="model.id"
            :label="model.label"
            :value="model.id">
          </el-option>
        </el-select>
      </el-form-item>
      <!-- 语速配置项 -->
      <el-form-item label="语速配置" prop="speechRate">
        <el-input-number
          style="width:100%"
          v-model="formData.speechRate"
          :min="0.1"
          :max="3"
          :step="0.1"
          placeholder="请输入语速 (0.1 - 3)">
        </el-input-number>
      </el-form-item>
      <!-- 提交按钮 -->
      <el-form-item>
        <el-button type="primary" bg text @click="submitForm">提交</el-button>
        <el-button text bg @click="resetForm">试听</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 初始化表单数据
const formData = ref({
  voiceModel: '',
  speechRate: 1 // 默认语速为 1
});

// 定义可用的语音模型数组
const voiceModels = ref([
  { code: 'vm1', id: '1', label: '标准男声' },
  { code: 'vm2', id: '2', label: '温柔女声' },
  { code: 'vm3', id: '3', label: '可爱童声' },
  { code: 'vm4', id: '4', label: '磁性男声' }
]);

// 表单验证规则
const rules = ref({
  voiceModel: [
    { required: true, message: '请选择语音模型', trigger: 'change' }
  ],
  speechRate: [
    { required: true, message: '请输入语速', trigger: 'blur' },
    { type: 'number', message: '语速必须为数字', trigger: 'blur' },
    { min: 0.1, max: 3, message: '语速范围在 0.1 - 3 之间', trigger: 'blur' }
  ]
});

// 获取表单实例
const formRef = ref(null) ; 

// 提交表单的方法
const submitForm = async () => {
  const isValid = await formRef.validate();
  if (isValid) {
    console.log('提交的表单数据：', formData.value);
    // 这里可以添加实际的提交逻辑，比如发送请求到后端等
  }
};

// 重置表单的方法
const resetForm = () => {
  formRef.resetFields();
};
</script>

<style scoped lang="scss">
</style>