<template>
  <div class="app-container">


    <div class="page-header-container">
      <el-page-header @back="goBack">
        <template #content>
          <div class="header-content">
            <span class="role-info">
              <img :src="imagePath(currentRole.roleAvatar)" class="role-avatar" />
              {{ currentRole.roleName }} 欢迎界面编辑器
            </span>
            <el-button type="primary" text bg @click="saveConfig" class="save-btn">
              <i class="fa-solid fa-save"></i> 保存配置
            </el-button>
          </div>
        </template>
      </el-page-header>
    </div>

    <el-row :gutter="20">
      <!-- 左侧编辑区 -->
      <el-col :span="14">
        <div class="editor-card">
          <h2 class="editor-title">
            <i class="fa-solid fa-computer"></i> 界面元素
          </h2>

          <el-form label-width="100px" :label-position="'top'" class="welcome-editor-form">

            <!-- 新增：欢迎界面开关 -->
            <el-form-item label="是否开启欢迎界面" class="enable-switch">
              <el-radio-group v-model="welcomeEnabled">
                <el-radio :label="true" size="large">开启</el-radio>
                <el-radio :label="false" size="large">关闭</el-radio>
              </el-radio-group>
            </el-form-item>

            <!-- 核心能力编辑 -->
            <el-form-item label="核心能力">
              <div class="feature-editor">
                <div v-for="(feature, index) in features" :key="index" class="feature-item-editor">
                  <el-input v-model="feature.title" size="large" placeholder="能力标题" class="feature-input" />
                  <el-input v-model="feature.description" placeholder="能力描述" size="large" class="feature-desc" />
                  <el-select size="large" v-model="feature.icon" placeholder="选择图标" class="icon-selector">
                    <el-option v-for="icon in iconOptions" :key="icon.value" :label="icon.label" :value="icon.value">
                      <i :class="icon.value"></i>
                      <span>{{ icon.label }}</span>
                    </el-option>
                  </el-select>
                  <el-button type="danger" icon="Delete" text bg @click="removeFeature(index)" />
                </div>
                <el-button type="primary" icon="Plus" size="large" text bg @click="addNewFeature" class="add-btn">
                  添加能力
                </el-button>
              </div>
            </el-form-item>

            <!-- 使用示例编辑 -->
            <el-form-item label="使用示例">
              <div class="example-editor">
                <div v-for="(example, index) in examples" :key="index" class="example-item-editor">
                  <el-upload class="image-uploader" action="" :show-file-list="false"
                    :before-upload="(file) => uploadExampleImage(file, index)">
                    <img v-if="example.image" :src="example.image" class="example-image">
                    <div v-else class="uploader-placeholder">
                      <i class="fas fa-image"></i>
                      <!-- <span>上传图片</span> -->
                    </div>
                  </el-upload>
                  <el-input v-model="example.label" size="large" placeholder="示例描述" class="example-input" />
                  <el-button type="danger" icon="Delete" text bg @click="removeExample(index)" />
                </div>
                <el-button type="primary" size="large" icon="Plus" text bg @click="addNewExample" class="add-btn">
                  添加示例
                </el-button>
              </div>
            </el-form-item>

            <!-- 使用提示编辑 -->
            <el-form-item label="使用提示">
              <div class="tip-editor">
                <div v-for="(tip, index) in tips" :key="index" class="tip-item-editor">
                  <el-input v-model="tips[index]" size="large" placeholder="输入提示内容" class="tip-input" />
                  <el-button type="danger" icon="Delete" text bg @click="removeTip(index)" />
                </div>
                <el-button type="primary" icon="Plus" size="large" text bg @click="addNewTip" class="add-btn">
                  添加提示
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </el-col>

      <!-- 右侧预览区 -->
      <el-col :span="10">
        <div class="preview-card">
          <h2 class="preview-title">
            <i class="fas fa-eye"></i> 实时预览
          </h2>
          <welcome-preview :features="features" :examples="examples" :currentRole="currentRole" :tips="tips"
            class="preview-content" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import WelcomePreview from './WelcomePreview.vue'

import { getRole, changeSaleField } from "@/api/smart/assistant/role";

const router = useRouter();
const route = useRoute();
const { proxy } = getCurrentInstance();

const currentRoleId = ref(route.query.roleId);
const currentRole = ref({
  roleName: ''
});

// 新增：欢迎界面开关状态
const welcomeEnabled = ref(true);

// 核心能力数据
const features = ref([
  { icon: 'fas fa-database', title: '数据智能处理', description: '支持Excel/CSV等多种格式，自动清洗与预处理' },
  { icon: 'fas fa-chart-bar', title: '可视化分析', description: '自动生成最佳图表，直观呈现数据洞察' },
  { icon: 'fas fa-project-diagram', title: '趋势预测', description: '基于机器学习算法预测未来趋势' },
  { icon: 'fas fa-file-export', title: '报告生成', description: '一键生成专业分析报告，支持PDF/PPT导出' }
])

// 使用示例数据
const examples = ref([
  {
    image: '',
    label: '分析过去6个月的销售额趋势并预测未来3个月'
  },
  {
    image: '',
    label: '客户群体细分与特征分析'
  }
])

// 使用提示数据
const tips = ref([
  "提示：使用自然语言描述你的分析需求，我会自动选择最合适的分析方法",
  "技巧：上传数据后，可以问我'这些数据有什么值得关注的趋势吗？'"
])

// 图标选项
const iconOptions = ref([
  { value: 'fas fa-database', label: '数据库' },
  { value: 'fas fa-chart-line', label: '图表' },
  { value: 'fas fa-arrow-trend-up', label: '趋势' },
  { value: 'fas fa-file-export', label: '导出' },
  { value: 'fas fa-gear', label: '设置' },
  { value: 'fas fa-magnifying-glass', label: '搜索' },
  { value: 'fas fa-robot', label: 'AI' },
  { value: 'fas fa-brain', label: '智能' },
  { value: 'fas fa-table', label: '表格' },
  { value: 'fas fa-cloud-upload-alt', label: '上传' }
])

// 添加新能力
const addNewFeature = () => {
  features.value.push({
    icon: 'fas fa-cog',
    title: '',
    description: ''
  })
}

// 删除能力
const removeFeature = (index) => {
  if (features.value.length > 1) {
    features.value.splice(index, 1)
  }
}

// 添加新示例
const addNewExample = () => {
  examples.value.push({
    image: '',
    label: ''
  })
}

// 删除示例
const removeExample = (index) => {
  if (examples.value.length > 1) {
    examples.value.splice(index, 1)
  }
}

// 添加新提示
const addNewTip = () => {
  tips.value.push('')
}

// 删除提示
const removeTip = (index) => {
  if (tips.value.length > 1) {
    tips.value.splice(index, 1)
  }
}

// 上传示例图片
const uploadExampleImage = (file, index) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    examples.value[index].image = e.target.result
  }
  reader.readAsDataURL(file)
  return false
}

/** 返回 */
function goBack() {
  router.go(-1);
}

/** 获取角色信息 */
function getRoleInfo() {
  getRole(currentRoleId.value).then(response => {
    currentRole.value = response.data;
  });
}

// 保存配置
const saveConfig = async () => {
  try {
    const configData = {
      welcomeEnabled: welcomeEnabled.value,
      features: features.value,
      examples: examples.value,
      tips: tips.value
    };
    
    // 调用API保存配置
    await changeSaleField({
      roleId: currentRoleId.value,
      field: 'welcomeConfig',
      value: JSON.stringify(configData)
    });
    
    proxy.$modal.msgSuccess("配置保存成功");
  } catch (error) {
    proxy.$modal.msgError("保存失败: " + error.message);
  }
};

onMounted(() => {
  getRoleInfo();
});

</script>

<style lang="scss" scoped>
.editor-card,
.preview-card {
  background-color: #fff;
  border-radius: 8px;
  height: calc(100vh - 120px);
  overflow-y: auto;
}

.editor-title,
.preview-title {
  font-size: 18px;
  color: #333;
  margin-top: 20px;
  margin-bottom: 0px;
  // margin-left: 20px;
  padding-bottom: 10px;
  display: flex;
  align-items: center;

  i {
    margin-right: 10px;
    color: #409EFF;
  }
}

.welcome-editor-form {
  margin-top: 10px;
  width: 100%;
}

.feature-item-editor,
.example-item-editor,
.tip-item-editor {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 10px;

  .el-button {
    flex-shrink: 0;
  }
}

.feature-input {
  width: 150px;
}

.feature-desc {
  width: 250px;
}

.icon-selector {
  width: 120px;

  i {
    margin-right: 8px;
    width: 16px;
    text-align: center;
  }
}

.example-input {
  width: 300px;
}

.image-uploader {
  width: 40px;
  height: 40px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
  background-color: #f5f7fa;

  &:hover {
    border-color: #409EFF;
  }

  .uploader-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    color: #909399;

    i {
      font-size: 20px;
    }

    span {
      font-size: 12px;
    }
  }

  .example-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.tip-input {
  width: 100%;
  min-width: 600px;
}

.add-btn {
  margin-top: 10px;
}

.preview-content {
  // border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 15px;
  padding-left: 5px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.enable-switch {
  margin-bottom: 20px;

  :deep(.el-form-item__label) {
    font-weight: bold;
  }
}

.role-info {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-right: 15px;

  .role-avatar {
    width: 25px;
    height: 25px;
    border-radius: 5px;
  }
}
</style>