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
                :src="getIconSvg(category.image)" 
                :alt="category.name" 
                @error="handleImageError"
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
            :src="getIconSvg(sub.image)" 
            :alt="sub.name"
            @error="handleImageError"
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
import { ref, computed } from 'vue';

// 在状态管理部分添加 hoveredSubcategory
const hoveredSubcategory = ref(null);

// 模板分类数据
const templateCategories = ref([
  { 
    id: 'thesis', 
    name: '学位论文', 
    desc: '收录多所高校论文格式',
    hasChildren: true,
    image: 'documents/thesis.png'
  },
  { 
    id: 'official', 
    name: '党政公文', 
    desc: '适合党政风格通知和公告',
    hasChildren: true,
    image: 'documents/official.png'
  },
  { 
    id: 'contract', 
    name: '合同协议', 
    desc: '适合各种法律协议、合同',
    hasChildren: false,
    image: 'documents/contract.png'
  },
  { 
    id: 'bidding', 
    name: '招投标文书', 
    desc: '适合各类型图标文件',
    hasChildren: false,
    image: 'documents/bidding.png'
  },
  { 
    id: 'general', 
    name: '通用文档', 
    desc: '适合大部分文档',
    hasChildren: false,
    image: 'documents/general.png'
  }
]);

// 子类数据
const subcategories = {
  thesis: [
    { id: 1, name: '中山大学', image: 'office/sysu.png' },
    { id: 2, name: '广东工业大学', image: 'office/gdut.png' },
    { id: 3, name: '广西大学', image: 'office/gxu.png' },
    { id: 6, name: '广西中医药大学', image: 'office/gxzyydx.png' }, // 修正id和图片路径
    { id: 4, name: '广西桂林电子科技大学', image: 'office/guet.png' },
    { id: 5, name: '广西民族大学', image: 'office/gxun.png' }
  ],
  official: [
    { id: 1, name: '公文主体', image: 'office/01.png' },
    { id: 2, name: '意见（短署名）', image: 'office/02.png' },
    { id: 3, name: '报告（短署名）', image: 'office/03.png' },
    { id: 4, name: '报告（长署名）', image: 'office/04.png' },
    { id: 5, name: '请示', image: 'office/05.png' },
    { id: 6, name: '请求（公章）', image: 'office/06.png' },
    { id: 7, name: '通知', image: 'office/07.png' },
    { id: 8, name: '通告（长署名）', image: 'office/08.png' },
    { id: 9, name: '通报', image: 'office/09.png' },
    { id: 10, name: '纪要', image: 'office/10.png' },
    { id: 11, name: '信涵', image: 'office/11.png' },
    { id: 12, name: '决定（公章）', image: 'office/12.png' },
    { id: 13, name: '决议（短书名）', image: 'office/13.png' },
    { id: 14, name: '命令', image: 'office/14.png' },
    { id: 15, name: '批复（短署名）', image: 'office/15.png' }
  ]
};

// 状态管理
const hoveredCategory = ref(null);
const selectedCategory = ref(null);

// 计算当前子分类
const currentSubcategories = computed(() => {
  if (!selectedCategory.value) return [];
  return subcategories[selectedCategory.value.id] || [];
});

// 处理分类点击
const handleCategoryClick = (category) => {
  if (category.hasChildren) {
    selectedCategory.value = category;
  } else {
    startFormatting(category);
  }
};

// 开始排版
const startFormatting = (template) => {
  console.log('开始排版:', template);
  // 这里可以触发排版逻辑或跳转到排版页面
  // emit('select', template);
};

// 处理图片加载错误
const handleImageError = (event) => {
  // event.target.src = '/images/default-image.jpg';
};

const getIconSvg = (fileName) => {
  return new URL(`/src/assets/icons/${fileName}`, import.meta.url).href;
};

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