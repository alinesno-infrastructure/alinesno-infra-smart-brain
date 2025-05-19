<template>
  <div class="app-container">
    <div class="box-card" shadow="never">

        <div class="label-title">
          <div class="tip">MCP服务配置</div>
          <div class="sub-tip">针对企业和团队在运营管理、业务拓展等方面的差异化需求，打造契合自身发展的专属平台环境。</div>
        </div>

      <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" size="large" status-icon style="margin-top:20px;">
        <el-form-item label="MCP名称" prop="mcpName">
          <el-input v-model="formData.mcpName" placeholder="请输入MCP名称" />
        </el-form-item>

        <el-form-item label="MCP地址" prop="mcpUrl">
            <el-input v-model="formData.mcpUrl" placeholder="请输入MCP地址" style="width:100%" />
        </el-form-item>

        <el-form-item label="MCP密钥" prop="mcpToken">
          <el-input v-model="formData.mcpToken" placeholder="请输入MCP密钥" show-password />
        </el-form-item>

        <el-form-item>
          <el-button @click="resetForm" type="primary" style="margin-right: 10px">
            重置
          </el-button>
          <el-button @click="handleSubmit" type="primary">
            保存配置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import mcpApi from '@/api/smart/assistant/mcpConfig';
import { ElMessage, ElMessageBox } from 'element-plus';

// 表单数据
const formData = ref({
  id: null,
  mcpName: '',
  mcpUrl: '',
  mcpToken: ''
});

// 表单引用
const formRef = ref(null);

// 地址验证状态
const urlValidating = ref(false);
const urlValidateResult = ref(null);

// 表单验证规则
const rules = ref({
  mcpName: [
    { required: true, message: '请输入MCP名称', trigger: 'blur' },
  ],
  mcpUrl: [
    { required: true, message: '请输入MCP地址', trigger: 'blur' },
  ],
  mcpToken: [
    { required: true, message: '请输入MCP密钥', trigger: 'blur' },
    { min: 8, max: 32, message: '长度在8到32个字符之间', trigger: 'blur' }
  ]
});

// 加载配置
const loadConfig = async () => {
    mcpApi.getByOrgId(formData.value.orgId).then(res => {
      formData.value = { ...formData.value, ...res.data };
    });
};

// 保存配置（使用表单验证）
const handleSubmit = async () => {
  await formRef.value.validate((valid) => {
    if (!valid) return;

    mcpApi.saveOrUpdate(formData.value)
      .then((res) => {
        ElMessage.success('配置保存成功');
        loadConfig();
      })
      .catch((error) => {
        ElMessage.error('保存失败: ' + error.message);
      });
  });
};

// 重置表单
const resetForm = () => {
  formRef.value.resetFields();
  urlValidateResult.value = null;
  urlValidating.value = false;
};

// 页面加载时初始化
onMounted(() => {
  loadConfig();
});
</script>

<style lang="scss" scoped>
.box-card {
  width: 700px;
  margin: auto;

 .label-title {
    text-align: center;
    max-width: 960px;
    margin: 10px auto 0;

   .tip {
      padding-bottom: 10px;
      font-size: 26px;
      font-weight: bold;
    }

   .sub-tip {
      font-size: 13px;
      text-align: center;
      padding: 10px;
    }
  }

  .url-input-group{
    display: flex;
    flex-direction: row;
  }
}
</style>