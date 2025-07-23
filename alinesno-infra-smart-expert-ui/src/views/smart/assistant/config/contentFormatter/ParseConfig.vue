<template>
  <div class="parse-config">
    <div class="header">
      <h2>文档解析服务配置</h2>
      <p class="description">配置系统使用的文档解析服务地址及相关参数</p>
    </div>
    
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" size="large" v-loading="loading">
      <el-form-item label="解析服务名称" prop="toolName">
        <el-input v-model="formData.toolName" placeholder="请输入解析服务名称" />
      </el-form-item>
      <el-form-item label="解析服务地址" prop="toolPath">
        <el-input v-model="formData.toolPath" placeholder="例如：http://localhost:8080/office" />
      </el-form-item>
      <el-form-item label="请求Token" prop="requestToken">
        <el-input v-model="formData.requestToken" placeholder="如需认证请填写Token" />
      </el-form-item>
      <el-form-item label="服务范围" prop="dataScope">
        <el-radio-group v-model="formData.dataScope">
          <el-radio 
            v-for="item in dataScopes" 
            :key="item.id" 
            :value="item.id" 
            :label="item.id"
            style="margin-top: 10px;align-items: center; margin-bottom: 10px; width: 100%; height: auto;"
          >
            <div class="scope-option">
              <span class="scope-title">
                <i :class="item.icon"></i> {{ item.name }}
              </span>
              <span class="scope-desc">
                {{ item.desc }}
              </span>
            </div>
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">保存配置</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  getContentFormatterOfficeConfig,
  addContentFormatterOfficeConfig,
  updateContentFormatterOfficeConfig
} from '@/api/smart/scene/contentFormatterOfficeConfig'

// 服务范围选项
const dataScopes = [
  { id: "public", name: "公开", icon: "fa-solid fa-globe", desc: "审核清单对外所有人可用" },
  { id: "org", name: "组织", icon: "fa-solid fa-truck-plane", desc: "只对组织内的成员可用" }
]

const loading = ref(false)
const formRef = ref()
const formData = reactive({
  id: null,
  toolName: '',
  toolPath: '',
  requestToken: '',
  dataScope: 'public' // 默认值设为第一个选项
})

const rules = reactive({
  toolName: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  toolPath: [
    { required: true, message: '请输入服务地址', trigger: 'blur' },
    { pattern: /^https?:\/\/.+/i, message: '请输入有效的URL地址', trigger: 'blur' }
  ],
  dataScope: [{ required: true, message: '请选择服务范围', trigger: 'change' }]
})

// 获取配置
const fetchConfig = async () => {
  try {
    loading.value = true
    const { data } = await getContentFormatterOfficeConfig()
    if (data) {
      Object.assign(formData, data)
    }
  } catch (error) {
    console.error('获取配置失败:', error)
    ElMessage.error('获取配置失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 提交表单
const submitForm = async () => {
  try {
    const result = await formRef.value.validate()
    if (!result) {
      return
    }

    loading.value = true
    
    if (formData.id) {
      await updateContentFormatterOfficeConfig(formData)
      ElMessage.success('配置更新成功')
    } else {
      const { data } = await addContentFormatterOfficeConfig(formData)
      formData.id = data.id
      ElMessage.success('配置保存成功')
    }
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (formData.id) {
    fetchConfig()
  } else {
    formRef.value.resetFields()
    formData.dataScope = 'public' // 重置时恢复默认值
  }
  ElMessage.info('配置已重置')
}

// 组件挂载时获取配置
onMounted(() => {
  fetchConfig()
})
</script>

<style lang="scss" scoped>
.parse-config {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  
  .header {
    margin-bottom: 30px;
    text-align: center;
    
    h2 {
      font-size: 24px;
      color: #333;
      margin-bottom: 8px;
    }
    
    .description {
      font-size: 14px;
      color: #666;
    }
  }
  
  .el-form {
    background: #fff;
    padding: 30px;
    border-radius: 8px;
  }
  
  .scope-option {
    padding: 10px;
    display: flex;
    flex-direction: column;
    line-height: 1.5rem;
    width: 100%;
    
    .scope-title {
      font-size: 15px;
      font-weight: bold;
      
      i {
        margin-right: 8px;
      }
    }
    
    .scope-desc {
      color: #a5a5a5;
      font-size: 13px;
    }
  }
  
  // 调整单选按钮样式
  :deep(.el-radio) {
    align-items: flex-start;
    margin-right: 0;
    
    .el-radio__label {
      padding-left: 8px;
    }
  }
}
</style>