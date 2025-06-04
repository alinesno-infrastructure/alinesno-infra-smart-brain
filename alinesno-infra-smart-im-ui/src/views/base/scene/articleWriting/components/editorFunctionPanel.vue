<template>
  <div class="editor-function-panel">
    <!-- 标题 -->
    <div class="article-edit-title">
      <span class="text">
       <i class="fa-solid fa-spray-can-sparkles"></i>  持续创作
        <i class="icon icon-edit" /> <!-- 示例图标位置 -->
      </span>
      <span class="desc">
        根据当前的文章，你还可以进一步调整
      </span>
    </div>

    <!-- 配置内容 -->
    <div class="article-edit-content">
      <!-- 文章风格 -->
      <div class="item">
        <div class="title">文章风格</div>
        <div class="content">
          <div 
            v-for="style in resultConfig?.styles" 
            :key="style.key"
            :class="{'active': articleStyleData.articleStyle === style.key}"
            @click="handleStyleChange(style.key)"
            class="option-item"
          >
            {{ style.name }}
          </div>
        </div>
      </div>

      <!-- 应用场景 -->
      <div class="item">
        <div class="title">应用场景</div>
        <div class="content">
          <div 
            v-for="hook in resultConfig?.hooks" 
            :key="hook.key"
            :class="{'active': articleStyleData.articleScene === hook.key}"
            @click="handleSceneChange(hook.key)"
            class="option-item"
          >
            {{ hook.name }}
          </div>
        </div>
      </div>

      <!-- 字数要求 -->
      <div class="item">
        <div class="title">字数</div>
        <div class="content">
          <div 
            v-for="num in resultConfig?.time" 
            :key="num.key"
            :class="{'active': articleStyleData.articleNum === num.key}"
            @click="handleNumChange(num.key)"
            class="option-item"
          >
            {{ num.name }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const emit = defineEmits(['config-change']);

const props = defineProps({
  resultConfig: {
    type: Object,
    required: true
  }
});

const articleStyleData = ref({
  articleStyle: '', 
  articleScene: '', 
  articleNum: '' 
});

const handleStyleChange = (value) => {
  articleStyleData.value.articleStyle = value;
  emitConfigChange();
};

const handleSceneChange = (value) => {
  articleStyleData.value.articleScene = value;
  emitConfigChange();
};

const handleNumChange = (value) => {
  articleStyleData.value.articleNum = value;
  emitConfigChange();
};

const emitConfigChange = () => {
  emit('config-change', {
    style: articleStyleData.value.articleStyle,
    scene: articleStyleData.value.articleScene,
    num: articleStyleData.value.articleNum
  });
};
</script>

<style lang="scss" scoped>
.article-edit-title {
  .text {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #505355;
    font-size: 16px;
    font-weight: 600;
    line-height: 24px;
    margin-bottom: 8px;
    
    .icon-edit {
      font-size: 14px;
      color: #999;
    }
  }
  
  .desc {
    font-size: 14px;
    color: #888;
  }
}

.article-edit-content {
  .item {
    margin-bottom: 16px;
    
    .title {
        display: inline-block;
        width: 80px;
        margin-bottom: 10px;
        color: #333;
        font-size: 14px;
        font-weight: bold; 
    }
    
    .content {
      display: inline-block;
      display: flex;
      gap: 8px; 
      flex-wrap: wrap; 
      
      .option-item {
        padding: 0px 12px;
        cursor: pointer;
        transition: all 0.2s;
        border-radius: 8px;
        background: #fff;
        border: 0px;
        font-size: 14px; 
        
        &.active {
          background-color: #409eff;
          color: white;
          border-color: #409eff;
        }
        
        &:hover {
          background-color: #f4f6fa;
        }
      }
    }

  }
}
</style>