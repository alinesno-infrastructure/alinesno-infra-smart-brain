<template>
  <div class="category-sidebar">

    <div class="category-header" style="">  
        <span>
          模板分类
        </span>
        <el-tag round effect="dark" size="small"  v-if="totalSelectedCount > 0" class="total-selected-count">
          已选 {{ totalSelectedCount }} 个
        </el-tag>  
    </div>

    <!-- 分类列表 -->
    <div  
      class="category-item" 
      :class="{ 'active': activeCategory === '' }"
      @click="selectCategory('')">
      <i class="fa-solid fa-house"></i> &nbsp; 所有场景分类  
    </div>
    <div 
      v-for="category in categories" 
      :key="category.id"
      class="category-item"
      :class="{ 'active': activeCategory === category.id }"
      @click="selectCategory(category.id)">
      <i :class="category.icon"></i> &nbsp;{{ category.name }} 
      <el-tag round effect="dark" size="small" v-if="getSelectedCount(category.id) > 0" class="selected-count">
        {{ getSelectedCount(category.id) }}
      </el-tag>
    </div>

    <div v-if="categories.length == 0">
      <el-empty  
         image-size="100"
         description="当前并没有集成分类，请点击右上角添加分类。" />
    </div>
     
  </div>
</template>

<script setup>
import { ref, defineProps, defineExpose , defineEmits } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

import {  
  getAllTemplateGroup , 
} from "@/api/smart/scene/generalAgentTemplate"

const emit = defineEmits(['update:activeCategory', 'update:categories'])

const props = defineProps({
  categories: {
    type: Object,
    required: true 
  },
  selectedTemplates: {
    type: Array,
    default: () => []
  },
});

// 表单引用
const formRef = ref(null)
const categories = ref([])
const activeCategory = ref(null)

// 分类表单
const showAddModal = ref(false)
const editingCategory = ref(null)
const categoryForm = ref({
  id: '',
  name: '',
  icon: ''
})
  
// 图标选项
const iconOptions = ref([
  { value: 'fa-solid fa-layer-group', label: '分组' },
  { value: 'fa-solid fa-chart-line', label: '图表' },
  { value: 'fa-solid fa-pen-fancy', label: '编辑' },
  { value: 'fa-solid fa-headset', label: '客服' },
  { value: 'fa-solid fa-robot', label: '机器人' },
  { value: 'fa-solid fa-gear', label: '设置' },
  { value: 'fa-solid fa-database', label: '数据库' },
  { value: 'fa-solid fa-users', label: '用户' }
])

// 选择分类
const selectCategory = (id) => {
  emit('update:activeCategory', id)
  activeCategory.value = id ;
}

// 计算总选择数量
const totalSelectedCount = computed(() => props.selectedTemplates.length)

// 获取指定分类下的选择数量
const getSelectedCount = (categoryId) => {
  if (!categoryId) return props.selectedTemplates.length
  
  return props.selectedTemplates.filter(template => 
    template.templateGroupId === categoryId
  ).length
}
 
const handleGetAllTemplateGroup = () => {
  getAllTemplateGroup().then(res => {
    console.log('res = ' + res);
    categories.value = res.data ;
    emit('update:categories' , res.data)
  })
}

// 获取到大模型能力信息
const getAllTemplate = () => {
  return categories.value; 
}

handleGetAllTemplateGroup();

defineExpose({ 
  getAllTemplate
})

</script>

<style lang="scss" scoped>
.category-sidebar {
  width: 220px;
  flex-shrink: 0;
  background-color: var(--el-bg-color);
  border-radius: var(--el-border-radius-base);
  padding: 8px 0;
  padding-top: 0px;

  .category-item {
    position: relative;
    padding: 12px 16px;
    cursor: pointer;
    color: var(--el-text-color-regular);
    font-size: 14px;
    display: flex;
    align-items: center;
    background-color: #fafafa;
    margin-bottom: 6px;
    gap: 5px;
    border-left: 3px solid #fafafa;
    line-height: 17px;

    &:hover {
      background-color: #f5f5f5;
      color: var(--el-color-primary);
      
      .category-actions {
        display: flex;
      }
    }

    &.active {
      font-weight: 500;
      background-color: #f5f5f5;
      border-radius: 5px;
      color: var(--el-color-primary);
      border-left: 3px solid #1d75b0;
    }

    .category-actions {
      display: none;
      position: absolute;
      right: 8px;
      gap: 4px;

      .action-button {
        padding: 4px;
        background-color: rgba(255, 255, 255, 0.8);
        backdrop-filter: blur(4px);

        &:hover {
          background-color: var(--el-bg-color);
        }
      }
    }
  }


  .add-category {
    padding: 12px 16px;
    cursor: pointer;
    color: var(--el-color-primary);
    font-size: 14px; 
    display: flex;
    align-items: center;
    gap: 8px;

    &:hover {
      background-color: #f5f5f5;
    }
  }

.category-header{
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
    align-items: center;
    font-size: 14px;
}

}
</style>