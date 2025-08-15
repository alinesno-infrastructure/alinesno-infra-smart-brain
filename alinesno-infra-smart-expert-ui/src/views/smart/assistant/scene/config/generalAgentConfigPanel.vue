<template>
  <div class="agent-scene-container"> 
    <main class="main-content">
      <div class="content-wrapper">
        <!-- 场景分类侧边栏 -->
        <CategorySidebar 
          ref="categorySidebarRef" 
          v-model:activeCategory="activeTab" 
          v-model:categories="templateTypeOptions"
          :selected-templates="selectedTemplates"
        />

        <!-- 右侧内容区域保持不变 -->
        <div class="right-content">
          <!-- 搜索和添加按钮 -->
          <div class="search-add-container">
            <el-input v-model="searchQuery" size="large" placeholder="搜索场景模板..." class="search-input"
              :prefix-icon="Search" />

              <div v-if="selectedTemplates.length > 0" class="selection-status">
              已选择 {{ selectedTemplates.length }} 个模板
              <el-button type="primary" text bg size="small" @click="clearSelection" style="margin-left: 10px;">
                清除选择
              </el-button>
            </div>

            <div>
  
            </div>
          </div>

          <!-- 场景模板网格 -->
          <div class="template-grid">
  
            <!-- 模板卡片 -->
            <div v-for="template in filteredTemplates " :key="template.id" class="template-card"
            :class="{ 'selected': isTemplateSelected(template.id) }"
              @click="toggleTemplateSelection(template)"
             shadow="hover">
              <div class="card-image-container">
                <img :src="imagePath(template.icon)" :alt="template.name + '图标'" class="card-image" @error="handleImageError" /> 

                <div class="selection-checkbox" v-if="isTemplateSelected(template.id)">
                  <i class="fa-solid fa-check"></i>
                </div>
              </div>
              <div class="card-content">
                <div class="card-header">
                  <h3 class="card-title">{{ template.name }}</h3> 
                </div>
                <p class="card-description">{{ template.description }}</p>
                <div class="card-footer">
                  <span class="category-name"> 
                      <i :class="getCategoryName(template.templateGroupId)?.icon" />
                     {{ getCategoryName(template.templateGroupId)?.name }}
                  </span> 
                </div>
              </div>
            </div> 

          </div>
        </div>
      </div>
    </main> 
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

import { ElMessage, ElMessageBox } from 'element-plus'

import {
  listTemplate , 
  getTemplate   
} from "@/api/smart/scene/generalAgentTemplate"

import CategorySidebar from './generalAgentSiderbar.vue'
import defaultImage from '@/assets/icons/outline/example_banner.png';
import { getToken } from "@/utils/auth";

const { proxy } = getCurrentInstance();
const router = useRouter()

const props = defineProps({
  initialSelectedTemplates: {
    type: Array,
    default: () => []
  }
})
 
// 模板配置
const showConfigModal = ref(false)
const eiditConfigTemplate = ref('')
const templateConfigForm = ref({ 
  resultConfig: {   // 结果配置
    enable: true , 
    config: null
  }, 
  config: {         // 结果配置
    planTemplate: null ,   // 规划配置
    contentTemplate: null , // 内容配置
  }, 
})
const currentEditTemplateId = ref(null)
const templateConfigFormRef = ref(null)

// 标签页数据
const templateTypeOptions = ref([])

const loading = ref(true);
const activeTab = ref('')
const searchQuery = ref('')
const categorySidebarRef = ref(null)
const imageUrl = ref([])
const dateRange = ref([]);
const total = ref(0);
 

// 模板数据
const templates = ref([])

const queryParams = ref({
    pageNum: 1,
    pageSize: 15,
    roleName: undefined,
    roleName: undefined,
    responsibilities: undefined,
    status: undefined,
    industryCatalog: undefined
})
 
// 新增的选择相关状态
const selectedTemplates = ref([])

// 切换模板选择状态
const toggleTemplateSelection = (template) => {
  const index = selectedTemplates.value.findIndex(t => t.id === template.id)
  if (index > -1) {
    selectedTemplates.value.splice(index, 1)
  } else {
    selectedTemplates.value.push({...template})
  }
}

// 检查模板是否被选中
const isTemplateSelected = (templateId) => {
  return selectedTemplates.value.some(t => t.id === templateId)
}

// 清除所有选择
const clearSelection = () => {
  selectedTemplates.value = []
}

// 过滤模板（根据搜索条件）
const filteredTemplates = computed(() => {
  let result = templates.value
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(t => 
      t.name.toLowerCase().includes(query) || 
      t.description.toLowerCase().includes(query)
    )
  }
  return result
})
  
const handleImageError = (event) => {
  event.target.src = defaultImage
  event.target.classList.add('error-image') // 可选：添加错误样式
}

// 添加获取分类名称的方法
const getCategoryName = (templateGroupId) => {
  const category = templateTypeOptions.value.find(item => item.id === templateGroupId);
  return category ; // ? category.name : '未分类';
};

 
// 在获取模板列表后，确保选中的模板与初始值匹配
function getList() {
  loading.value = true;
  listTemplate(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false;
    templates.value = res.rows;
    total.value = res.total;
    
    // 确保初始选中的模板在列表中
    if (props.initialSelectedTemplates.length > 0) {
      selectedTemplates.value = props.initialSelectedTemplates.filter(template => 
        res.rows.some(t => t.id === template.id)
      )
    }
  });
};

const handleConfigSubmit = () => {
  templateConfigFormRef.value.validate((valid) => {
    if (valid) {
      templateConfigForm.value.templateId = currentEditTemplateId ;

      updateTemplateConfig(templateConfigForm.value).then(res => {
        ElMessage.success('配置更新成功');
        getList();
      }) 
    } else {
      ElMessage.warning('请填写完整的配置信息');
      return false;
    }
  });
}


getList();

// 添加 watch 监听 activeTab 变化
watch(activeTab, (newVal, oldVal) => { 
  if (newVal !== oldVal) {
    queryParams.value.templateGroupId = newVal ; 
    getList()
  }
})

const getConfigData = () => {
    // 处理 selectedTemplates，确保转换成数组形式（支持数组或逗号分隔的字符串）
    const selectedTemplateIds = Array.isArray(selectedTemplates.value) 
        ? selectedTemplates.value
        : selectedTemplates.value.split(',');

    // 遍历模板数据，仅保留需要的字段（icon、name、description）
    const simplifiedTemplates = selectedTemplateIds.map(template => ({
        id: template.id,        // 模板id
        icon: template.icon,        // 模板图标
        name: template.name,        // 模板名称
        description: template.description  // 模板描述
    }));

    return {
        selectedTemplateIds: simplifiedTemplates  // 返回简化后的模板数据
    };
};

// 监听初始值变化并设置选中状态
watch(() => props.initialSelectedTemplates, (newVal) => {
  // 确保newVal是数组，如果不是则转为空数组
  console.log('--->>> newVal = ', newVal)
  
  if (Array.isArray(newVal)) {
    // 如果直接传递的是模板ID数组
    selectedTemplates.value = newVal
  } else if (newVal && Array.isArray(newVal.selectedTemplateIds)) {
    // 如果传递的是包含selectedTemplateIds属性的对象
    selectedTemplates.value = newVal.selectedTemplateIds
  } else {
    // 其他情况设为空数组
    selectedTemplates.value = []
  }
}, { immediate: true, deep: true })

defineExpose({ 
  getConfigData
})

</script>

<style lang="scss" scoped>
.agent-scene-container {
  .page-header {
    background-color: var(--el-bg-color);

    .header-title {
      font-size: 18px;
      font-weight: 500;
      color: var(--el-text-color-primary);
    }
  }


  .selection-status {
    display: flex;
    align-items: center;
    font-size: 14px;
    color: var(--el-color-primary);
    margin-left: 16px;
  }

  .template-card {
    position: relative;
    cursor: pointer;
    
    &.selected {
      border: 2px solid var(--el-color-primary);
      box-shadow: 0 0 8px rgba(64, 158, 255, 0.3);
    }
    
    .selection-checkbox {
      position: absolute;
      top: 8px;
      right: 8px;
      width: 20px;
      height: 20px;
      background-color: var(--el-color-primary);
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
    }
  }

  .main-content {
    margin-top: 10px;

    .content-wrapper {
      display: flex;
      gap: 24px;

      .right-content {
        flex: 1;

        .search-add-container {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 24px;
          gap: 16px;

          .search-input {
            flex: 1;
            max-width: 400px;
          }
        }

        .template-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
          gap: 20px;

          .template-card {
            height: 100%;
            transition: all 0.3s var(--el-transition-function-ease-in-out-bezier);
            border: 1px solid #e4e7ed;
            border-radius: 8px;

            &:hover { 
              border: 1px solid #409EFF; 
              box-shadow: 0px 0px 6px rgba(0, 0, 0, .12);
            }

            .card-image-container {
              position: relative;
              height: 70px;
              overflow: hidden;

              .card-image {
                width: 100%;
                height: 100%;
                object-fit: cover;
                border-radius: 8px 8px 0px 0px;
              }

              .card-actions {
                position: absolute;
                top: 12px;
                right: 12px;
                display: flex;
                gap: 8px;

                .action-button {
                  background-color: rgba(255, 255, 255, 0.8);
                  backdrop-filter: blur(4px);

                  &:hover {
                    background-color: var(--el-bg-color);
                  }
                }
              }
            }

            .card-content {
              padding: 8px;
              padding-top: 10px;

              .card-header {
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                margin-bottom: 5px;

                .card-title {
          font-size: 15px;
          font-weight: 500;
          color: var(--el-text-color-primary);
          margin: 0;
          flex: 1;
          white-space: nowrap; /* 禁止换行 */
          overflow: hidden;    /* 隐藏溢出内容 */
          text-overflow: ellipsis; /* 显示省略号 */
          min-height: 20px;
          line-height: 20px;
        }
              }

              .card-description {
                font-size: 14px;
                color: var(--el-text-color-secondary);
                margin-bottom: 10px;
                display: -webkit-box;
                -webkit-line-clamp: 2; /* 限制两行 */
                -webkit-box-orient: vertical;
                overflow: hidden;
                text-overflow: ellipsis;
                min-height: 40px; /* 保持两行高度一致 */
                line-height: 20px; /* 行高设置为字体大小的1.4倍左右 */
              }

              .card-footer {
                display: flex;
                justify-content: space-between;
                align-items: center;

                .category-name {
                  font-size: 12px;
                  color: var(--el-text-color-placeholder);
                }

                .update-time {
                  font-size: 12px;
                  color: var(--el-text-color-placeholder);
                }

                .config-button {
                  padding: 0;
                }
              }
            }
          }

          .add-template-card {
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100%;
            cursor: pointer;
            border: 2px dashed var(--el-border-color);
            transition: all 0.3s var(--el-transition-function-ease-in-out-bezier);

            &:hover {
              border-color: var(--el-color-primary);
            }

            .add-card-content {
              text-align: center;
              padding: 20px;

              .add-icon-container {
                width: 64px;
                height: 64px;
                margin: 0 auto 16px;
                display: flex;
                align-items: center;
                justify-content: center;
                background-color: rgba(22, 93, 255, 0.1);
                border-radius: 50%;

                .add-icon {
                  font-size: 24px;
                  color: var(--el-color-primary);
                }
              }

              .add-card-title {
                font-size: 16px;
                font-weight: 500;
                color: var(--el-text-color-primary);
                margin-bottom: 8px;
              }

              .add-card-description {
                font-size: 14px;
                color: var(--el-text-color-secondary);
                margin: 0;
              }
            }
          }
        }
      }
    }
  }

  .template-dialog {
    :deep(.el-upload--picture-card) {
      width: 120px;
      height: 120px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}
</style>