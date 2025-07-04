<template>
  <div class="app-container agent-scene-container">
    <!-- 顶部导航栏 -->
    <el-page-header @back="goBack" class="page-header">
      <template #content>
        <span class="header-title">智能体场景配置</span>
      </template>
    </el-page-header>

    <main class="main-content">
      <div class="content-wrapper">
        <!-- 场景分类侧边栏 -->
        <CategorySidebar ref="categorySidebarRef" v-model:activeCategory="activeTab" v-model:categories="tabs" />

        <!-- 右侧内容区域保持不变 -->
        <div class="right-content">
          <!-- 搜索和添加按钮 -->
          <div class="search-add-container">
            <el-input v-model="searchQuery" size="large" placeholder="搜索场景模板..." class="search-input"
              :prefix-icon="Search" />
            <div>

              <el-button type="primary" size="large" text bg class="add-button" @click="showAddModal = true">
                <el-icon class="el-icon--left">
                  <Plus />
                </el-icon>
                添加新模板
              </el-button>
              <!-- 添加分类按钮 -->
              <el-button class="add-category" type="primary" text bg size="large" @click="addCategory()">
                <el-icon>
                  <Plus />
                </el-icon> 添加分类
              </el-button>

            </div>
          </div>

          <!-- 场景模板网格 -->
          <div class="template-grid">
            <!-- 模板卡片 -->
            <div v-for="template in filteredTemplates" :key="template.id" class="template-card" shadow="hover">
              <div class="card-image-container">
                <img :src="template.image" :alt="template.name + '图标'" class="card-image" @error="handleImageError" />
                <div class="card-actions">
                  <el-button circle class="action-button" @click.stop="editTemplate(template)">
                    <el-icon>
                      <Edit />
                    </el-icon>
                  </el-button>
                  <el-button circle class="action-button" @click.stop="deleteTemplate(template.id)">
                    <el-icon>
                      <Delete />
                    </el-icon>
                  </el-button>
                </div>
              </div>
              <div class="card-content">
                <div class="card-header">
                  <h3 class="card-title">{{ template.name }}</h3>
                  <el-tag type="info" size="small">{{ template.category }}</el-tag>
                </div>
                <p class="card-description">{{ template.description }}</p>
                <div class="card-footer">
                  <span class="update-time">更新于 {{ template.updatedAt }}</span>
                  <el-button link type="primary" class="config-button" @click="configureTemplate(template.id)">
                    配置 <el-icon>
                      <ArrowRight />
                    </el-icon>
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 添加新模板卡片 -->
            <el-card class="add-template-card" shadow="hover" @click="showAddModal = true">
              <div class="add-card-content">
                <div class="add-icon-container">
                  <el-icon class="add-icon">
                    <Plus />
                  </el-icon>
                </div>
                <h3 class="add-card-title">添加新模板</h3>
                <p class="add-card-description">点击创建新的场景模板，上传自定义图标</p>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </main>

    <!-- 添加/编辑模板模态框 -->
    <el-dialog v-model="showAddModal" :title="editingTemplate ? '编辑模板' : '添加新模板'" width="600px" class="template-dialog">
      <el-form :model="templateForm" size="large" label-width="100px">
        <el-form-item label="上传图标">
          <el-upload action="https://jsonplaceholder.typicode.com/posts/" list-type="picture-card"
            :on-preview="handlePreview" :on-remove="handleRemove" :on-success="handleUploadSuccess">
            <el-icon>
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="模板名称">
          <el-input v-model="templateForm.name" placeholder="请输入模板名称"></el-input>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="templateForm.category" placeholder="请选择分类">
            <el-option v-for="tab in tabs" :key="tab.id" :label="tab.name" :value="tab.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="templateForm.description" type="textarea" :rows="3" placeholder="请输入模板描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button size="large" @click="showAddModal = false">取消</el-button>
          <el-button size="large" type="primary" @click="submitTemplate">
            {{ editingTemplate ? '更新' : '确认' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import CategorySidebar from './generalAgentSiderbar.vue'
import defaultImage from '@/assets/icons/outline/example_banner.png';

const router = useRouter()

// 返回
const goBack = () => {
  router.push({ path: "/global/config" })
}

// 标签页数据
const tabs = ref([
  { id: 'all', name: '全部场景', icon: 'fa-solid fa-layer-group' },
  { id: 'data', name: '数据分析', icon: 'fa-solid fa-chart-line' },
  { id: 'content', name: '内容生成', icon: 'fa-solid fa-pen-fancy' },
  { id: 'service', name: '客户服务', icon: 'fa-solid fa-headset' },
  { id: 'automation', name: '自动化流程', icon: 'fa-solid fa-robot' },
  { id: 'system', name: '系统管理', icon: 'fa-solid fa-gear' },
])

const activeTab = ref('all')
const searchQuery = ref('')
const categorySidebarRef = ref(null)

// 模板数据
const templates = ref([
  {
    id: 1,
    name: '数据可视化分析',
    category: '数据分析',
    description: '基于历史数据生成多种可视化图表，帮助用户直观理解数据趋势和模式。',
    updatedAt: '2025-06-15',
    image: 'xxx'
  }
])

// 过滤模板
const filteredTemplates = computed(() => {
  return templates.value.filter(template => {
    // 按标签过滤
    const tabMatch = activeTab.value === 'all' || template.category === tabs.value.find(t => t.id === activeTab.value)?.name

    // 按搜索查询过滤
    const searchMatch = template.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      template.description.toLowerCase().includes(searchQuery.value.toLowerCase())

    return tabMatch && searchMatch
  })
})

// 模板表单
const showAddModal = ref(false)
const editingTemplate = ref(null)
const templateForm = ref({
  name: '',
  category: '',
  description: '',
  image: ''
})

// 编辑模板
const editTemplate = (template) => {
  editingTemplate.value = template.id
  templateForm.value = { ...template }
  showAddModal.value = true
}

// 添加分类
const addCategory = () => { 
  categorySidebarRef.value.addCategory()
}

// 删除模板
const deleteTemplate = (id) => {
  ElMessageBox.confirm('确定要删除此模板吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    templates.value = templates.value.filter(t => t.id !== id)
    ElMessage.success('删除成功')
  }).catch(() => {
    // 取消操作
  })
}

// 配置模板
const configureTemplate = (id) => {
  ElMessage.info(`配置模板 ${id}`)
  // 实际项目中这里可以跳转到配置页面
}

// 上传图片处理
const handleRemove = () => {
  templateForm.value.image = ''
}

const handlePreview = (file) => {
  // 预览图片逻辑
}

const handleUploadSuccess = (response, file) => {
  templateForm.value.image = URL.createObjectURL(file.raw)
}

// 提交表单
const submitTemplate = () => {
  if (!templateForm.value.name || !templateForm.value.category) {
    ElMessage.warning('请填写完整信息')
    return
  }

  if (editingTemplate.value) {
    // 更新模板
    const index = templates.value.findIndex(t => t.id === editingTemplate.value)
    if (index !== -1) {
      templates.value[index] = { ...templateForm.value, id: editingTemplate.value }
    }
    ElMessage.success('模板更新成功')
  } else {
    // 添加新模板
    const newTemplate = {
      ...templateForm.value,
      id: templates.value.length + 1,
      updatedAt: new Date().toISOString().split('T')[0]
    }
    templates.value.unshift(newTemplate)
    ElMessage.success('模板添加成功')
  }

  showAddModal.value = false
  resetForm()
}

const handleImageError = (event) => {
  event.target.src = defaultImage
  event.target.classList.add('error-image') // 可选：添加错误样式
}

// 重置表单
const resetForm = () => {
  templateForm.value = {
    name: '',
    category: '',
    description: '',
    image: ''
  }
  editingTemplate.value = null
}
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

  .main-content {
    margin-top: 20px;

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
          grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
          gap: 20px;

          .template-card {
            height: 100%;
            transition: all 0.3s var(--el-transition-function-ease-in-out-bezier);
            border: 1px solid #f5f5f5;
            border-radius: 8px;

            .card-image-container {
              position: relative;
              height: 100px;
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
              padding: 16px;

              .card-header {
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                margin-bottom: 12px;

                .card-title {
                  font-size: 16px;
                  font-weight: 500;
                  color: var(--el-text-color-primary);
                  margin: 0;
                  flex: 1;
                }
              }

              .card-description {
                font-size: 14px;
                color: var(--el-text-color-secondary);
                margin-bottom: 16px;
                display: -webkit-box;
                -webkit-box-orient: vertical;
                overflow: hidden;
              }

              .card-footer {
                display: flex;
                justify-content: space-between;
                align-items: center;

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