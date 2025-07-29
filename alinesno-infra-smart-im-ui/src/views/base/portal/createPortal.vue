<template>
  <el-dialog
    v-model="dialogVisible"
    title=" 编辑AI工作台"
    width="580"
    :before-close="handleClose"
  >
    <div 
      v-loading="loading"
      element-loading-text="Loading..."
      :element-loading-spinner="svg"
      element-loading-svg-view-box="-10, -10, 50, 50"
      element-loading-background="rgba(122, 122, 122, 0.8)"
    >

      <div style="padding: 5px;margin-bottom: 10px;">
        选择不同的工作台会形成不同的<b>AI智能体</b>工作组合
      </div>

      <el-form
        ref="ruleFormRef"
        :model="ruleForm"
        :rules="rules"
        label-width="auto"
        class="demo-ruleForm"
        :label-position="'top'"
        :size="formSize"
        status-icon>
        <el-form-item label="名称" prop="name">
          <el-input v-model="ruleForm.name" placeholder="请输入工作台名称"  />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input v-model="ruleForm.description" placeholder="请输入工作台描述"  />
        </el-form-item>

      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button v-loading="submitLoading" type="primary" size="large" @click="handleSubmit">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import {
  listOrgWorkplace, 
  customWorkplace,
} from "@/api/base/im/workplace"

const emit = defineEmits(['handleHasWorkplace']);

const dialogVisible = ref(false);
const loading = ref(false);
const submitLoading = ref(false);
const ruleFormRef = ref(null);
const ruleForm = ref({
  name: '',
  type: 2,
  template: ''
});
const formSize = ref('large');
const workbenches = ref([]);    

const rules = ref({
  name: [
    { required: true, message: '请输入工作台名称', trigger: 'blur' },
    { min: 2, max: 20, message: '工作台名称长度应在2到20个字符之间', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请填写工作台描述.', trigger: 'change' }
  ] , 
});
const handleClose = (done) => {
  dialogVisible.value = false;
};

const handleSubmit = () => {
  ruleFormRef.value.validate((valid) => {
    if (valid) {
      // 这里可以添加提交表单数据的逻辑，比如发送请求到后端
      submitLoading.value = true;

      customWorkplace(ruleForm.value).then(res => {
          ElMessage.success('表单提交成功');
          submitLoading.value = false;
          dialogVisible.value = false;

          emit("handleHasWorkplace")
          
        }).catch(() => {
          submitLoading.value = false;
        })

    } else {
      return false;
    }
  });
}

const handleListOrgWorkplace = () => {
  loading.value = true;
  listOrgWorkplace().then(res => {
    workbenches.value = res.data;
    loading.value = false;
  })
}

const openSelectWorkplace = (workplace) => {
  dialogVisible.value = true;
  ruleForm.value.name = workplace.name;
  ruleForm.value.description = workplace.description;
}

// 提供给父类使用
defineExpose({
  openSelectWorkplace
})

</script>

<style lang="scss" scoped>
</style>    