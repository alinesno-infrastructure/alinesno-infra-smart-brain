<template>
  <div class="template-selector">
    <!-- 主分类列表 -->
    <div v-if="!selectedCategory" class="category-list">
      <h2 class="title">
        <i class="fa-solid fa-table-cells"></i>
        <span>文档排版模板</span>
      </h2>
      
      <div class="categories">
        <div 
          v-for="category in templateCategories" 
          :key="category.id" 
          class="category-card"
          @mouseenter="hoveredCategory = category.id"
          @mouseleave="hoveredCategory = null"
        >
          <div class="card-content">
            <div class="text-content">
              <h3>{{ category.name }}</h3>
              <p>{{ category.desc }}</p>
            </div>
            <div class="image-wrapper">

              <img 
                :src="imagePathByPath(category.image)" 
                :alt="category.name" 
              />
            </div>
          </div>
          
          <div 
            v-if="!category.hasChildren" 
            class="action-button"
            :class="{ 'visible': hoveredCategory === category.id }">
            <el-button type="primary" text bg @click.stop="startFormatting(category)">
              开始排版
            </el-button>
          </div>
          <div 
            v-else
            class="action-button"
            :class="{ 'visible': hoveredCategory === category.id }">
            <el-button type="primary" text bg @click.stop="handleCategoryClick(category)">
              选择类型 
            </el-button>
          </div>

        </div>
      </div>
    </div>
    
    <!-- 子分类列表 -->
  <div v-else class="subcategory-list">
     <div class="header">
        <button class="back-button" @click="selectedCategory = null">
          <i class="fa-solid fa-arrow-left"></i>
          <span>返回</span>
        </button>
        <div style="font-weight: bold;">{{ selectedCategory.name }}</div>
      </div>
    
    <div class="subcategories">
      <div 
        v-for="sub in currentSubcategories" 
        :key="sub.id" 
        class="subcategory-card"
        @mouseenter="hoveredSubcategory = sub.id"
        @mouseleave="hoveredSubcategory = null"
      >
        <div class="image-container">
          <img 
            :src="imagePathByPath(sub.image)" 
            :alt="sub.name"
          />
        </div>
        <div class="name">{{ sub.name }}</div>
        
        <!-- 添加开始排版按钮 -->
        <div 
          class="subcategory-action"
          :class="{ 'visible': hoveredSubcategory === sub.id }"
        >
          <el-button type="primary" text bg @click.stop="startFormatting(sub)">
            开始排版
          </el-button>
        </div>
      </div>
    </div>
  </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { selectOrgLayout } from '@/api/base/im/scene/contentFormatterLayout';

// 在状态管理部分添加 hoveredSubcategory
const hoveredSubcategory = ref(null);

// 模板分类数据 - 初始为空，将从后端加载
const templateCategories = ref([]);

// 子类数据 - 初始为空，将从后端加载
const subcategories = ref({});

// 状态管理
const hoveredCategory = ref(null);
const selectedCategory = ref(null);

// 计算当前子分类
const currentSubcategories = computed(() => {
  if (!selectedCategory.value) return [];
  return subcategories.value[selectedCategory.value.id] || [];
});

// 加载模板数据
const loadTemplateData = async () => {
  try {
    const res = await selectOrgLayout();
    if (res.code === 200) {
      // 假设后端返回的数据结构为 { categories: [], subcategories: {} }
      templateCategories.value = res.data.categories || [];
      subcategories.value = res.data.subcategories || {};
      
      // 如果没有数据，使用默认数据
      if (templateCategories.value.length === 0) {
        templateCategories.value = getDefaultCategories();
        subcategories.value = getDefaultSubcategories();
      }
    }
  } catch (error) {
    console.error('加载模板数据失败:', error);
    // 使用默认数据作为回退
    templateCategories.value = getDefaultCategories();
    subcategories.value = getDefaultSubcategories();
  }
};

// 默认分类数据
const getDefaultCategories = () => [];

// 默认子分类数据
const getDefaultSubcategories = () => ({});

// 处理分类点击
const handleCategoryClick = (category) => {
  if (category.hasChildren) {
    selectedCategory.value = category;
  } else {
    startFormatting(category);
  }
};

// 开始排版
const startFormatting = async (template) => {
  console.log('开始排版:', template);
  
  try {
    // 这里可以调用后端API获取模板详情
    // const res = await getLayoutTemplate(template.id);
    // if (res.code === 200) {
    //   // 处理模板详情
    //   const templateDetail = res.data;
    //   emit('select', templateDetail);
    // }
    
    // 暂时直接触发事件
    emit('select', template);
  } catch (error) {
    console.error('获取模板详情失败:', error);
    // 可以显示错误提示
  }
};

// // 处理图片加载错误
// const handleImageError = (event) => {
//   event.target.src = '/images/default-document.png';
// };

// const getIconSvg = (fileName) => {
//   return new URL(`/src/assets/icons/${fileName}`, import.meta.url).href;
// };

// 定义emit事件
const emit = defineEmits(['select']);

onMounted(() => {
  console.log('加载文档模板...');
  loadTemplateData();
});
</script>

<style lang="scss" scoped>
.template-selector {
  color: #333;
  max-width: 800px;
  margin: 20px ;

  // 标题样式
  .title {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 18px;
    margin-bottom: 20px;
    color: #2c3e50;

    i {
      color: #409eff;
    }
  }

  // 分类卡片列表
  .categories {
    display: grid;
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .category-card {
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    overflow: hidden;
    transition: all 0.3s ease;
    position: relative;
    background-color: #fff;

    .card-content {
      display: flex;
      padding: 15px;
      align-items: center;

      .text-content {
        flex: 1;

        h3 {
          font-size: 16px;
          margin: 0 0 8px 0;
          font-weight: 600;
        }

        p {
          font-size: 14px;
          color: #666;
          margin: 0;
        }
      }

      .image-wrapper {
        max-width: 30%;
        height: 50px;
        border-radius: 4px;
        overflow: hidden;
        margin-left: 15px;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
    }

    // 操作按钮
    .action-button {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: rgba(255, 255, 255, 0.9);
      opacity: 0;
      transition: opacity 0.3s ease;

      &.visible {
        opacity: 1;
      }
    }
  }

  // 子分类列表样式
  .subcategory-list {
    animation: fadeIn 0.3s ease;
  }

  @keyframes fadeIn {
    from { 
      opacity: 0; 
      transform: translateY(10px); 
    }
    to { 
      opacity: 1; 
      transform: translateY(0); 
    }
  }

  .header {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-bottom: 20px;
  }

  .back-button {
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 6px 12px;
    background-color: #f5f7fa;
    border: none;
    border-radius: 4px;
    color: #606266;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background-color: #e4e7ed;
      color: #409eff;
    }
  }

  .subcategories {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 15px;
  }

}

.subcategory-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  position: relative; // 添加相对定位

  .image-container {
    height: 70px;
    overflow: hidden;
    border-bottom: 1px solid #e4e7ed;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      object-position: top;
    }
  }

  .name {
    padding: 8px;
    line-height: 1.5rem;
    text-align: center;
    font-size: 14px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    font-weight: bold ;
    color: #444;
  }

  // 子分类操作按钮
  .subcategory-action {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: rgba(255, 255, 255, 0.9);
    opacity: 0;
    transition: opacity 0.3s ease;

    &.visible {
      opacity: 1;
    }
  }
}
</style>