<template>
  <!-- 模板部分保持不变 -->
  <div class="editor-function-panel">
    <!-- 头部/保存 -->
    <div class="article-edit-header">
      <el-button type="primary" text bg size="large" @click="handleSaveArticle">
        <i class="fa-solid fa-floppy-disk"></i> &nbsp; 保存
      </el-button>
      <el-button type="danger" text bg size="large" @click="handleExportArticle">
        <i class="fa-solid fa-file-powerpoint"></i> &nbsp; 导出word
      </el-button>
    </div>

    <!-- 标题 -->
    <div class="article-edit-title">
      <span class="text">
        <i class="fa-solid fa-spray-can-sparkles"></i> 整体润色 
        <i class="icon icon-edit" />
      </span>
      <span class="desc">
        你可以选择以下选项来重新润色这篇文章。
      </span>
    </div>

    <!-- 配置内容 -->
    <div class="article-edit-content">
      <!-- 文章风格 -->
      <div class="item">
        <div class="title">文章风格</div>
        <div class="content">
          <div v-for="style in templateResultConfig?.styles" :key="style.key"
            :class="{ 'active': articleStyleData.articleStyle === style.key }" @click="handleStyleChange(style.key)"
            class="option-item">
            {{ style.name }}
          </div>
        </div>
      </div>

      <!-- 应用场景 -->
      <div class="item">
        <div class="title">应用场景</div>
        <div class="content">
          <div v-for="hook in templateResultConfig?.hooks" :key="hook.key"
            :class="{ 'active': articleStyleData.articleScene === hook.key }" @click="handleSceneChange(hook.key)"
            class="option-item">
            {{ hook.name }}
          </div>
        </div>
      </div>

      <!-- 字数要求 -->
      <div class="item">
        <div class="title">字数</div>
        <div class="content">
          <div v-for="num in templateResultConfig?.time" :key="num.key"
            :class="{ 'active': articleStyleData.articleNum === num.key }" @click="handleNumChange(num.key)"
            class="option-item">
            {{ num.name }}
          </div>
        </div>
      </div>
    </div>

    <div class="article-edit-footer">
      <el-button type="primary" style="width:100%" text bg size="large" @click="handleGenerate">
        <i class="fa-solid fa-spray-can-sparkles"></i> 重新生成
      </el-button>
    </div>

    <!-- 快捷键说明 -->
    <!-- 
    <div class="article-edit-shortcut">
      <div class="title">快捷键</div>
      <div class="content">
        <div class="item">
          <span class="key">Ctrl + S</span>
          <span class="desc">保存</span>
        </div>
        <div class="item">
          <span class="key">Esc</span>
          <span class="desc">退出AI编辑</span>
        </div>
        <div class="item">
          <span class="key">Ctrl + E</span>
          <span class="desc">导出word</span>
        </div>
      </div>
    </div> 
    -->

  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';

const emit = defineEmits(['config-change' , 'handleSaveArticle' , 'handleExportArticle']);

import {
  getTemplateDetail
} from '@/api/base/im/scene/articleWriting';

const props = defineProps({
  articleData: {
    type: Object,
    required: false
  }, 
  resultConfig: {
    type: Object,
    required: false
  },
  promptText: {
    type: String,
    required: false
  }
});

const templateResultConfig = ref({
  styles: [],
  time: [],
  hooks: []
});

const articleStyleData = ref({
  articleStyle: '',
  articleScene: '',
  articleNum: ''
});

// 计算属性，生成promptText
const promptText = computed(() => {
  const style = templateResultConfig.value.styles.find(item => item.key === articleStyleData.value.articleStyle)?.name || '';
  const scene = templateResultConfig.value.hooks.find(item => item.key === articleStyleData.value.articleScene)?.name || '';
  const num = templateResultConfig.value.time.find(item => item.key === articleStyleData.value.articleNum)?.name || '';
  
  return `文章风格:${style}，应用场景：${scene}，字数：${num}`;
});

// 设置默认选中项
const setDefaultSelections = () => {
  if (templateResultConfig.value.styles.length > 0 && !articleStyleData.value.articleStyle) {
    articleStyleData.value.articleStyle = templateResultConfig.value.styles[0].key;
  }
  if (templateResultConfig.value.hooks.length > 0 && !articleStyleData.value.articleScene) {
    articleStyleData.value.articleScene = templateResultConfig.value.hooks[0].key;
  }
  if (templateResultConfig.value.time.length > 0 && !articleStyleData.value.articleNum) {
    articleStyleData.value.articleNum = templateResultConfig.value.time[0].key;
  }
};

const handleStyleChange = (value) => {
  articleStyleData.value.articleStyle = value;
};

const handleSceneChange = (value) => {
  articleStyleData.value.articleScene = value;
};

const handleNumChange = (value) => {
  articleStyleData.value.articleNum = value;
};

// 处理生成按钮点击
const handleGenerate = () => {
  emitConfigChange();
};

const emitConfigChange = () => {
  emit('config-change', {
    style: articleStyleData.value.articleStyle,
    scene: articleStyleData.value.articleScene,
    num: articleStyleData.value.articleNum,
    promptText: promptText.value // 添加promptText到emit数据中
  });
};

const handleSaveArticle = () => {
  emit('handleSaveArticle');
};

// 文章导出
const handleExportArticle= () => {
  emit('handleExportArticle');
};

// watch articleData的变动，然后根据templateId获取到最新的template信息
watch(() => props.articleData, (newValue) => {
  if (newValue?.templateId) {
    getTemplateDetail(newValue.templateId).then((res) => {
      if(res.data?.resultConfig){
        templateResultConfig.value = JSON.parse(res.data.resultConfig);
        setDefaultSelections(); // 数据加载后设置默认选中项
      }
    });
  }
}, { immediate: true });
</script>

<style lang="scss" scoped>
/* 样式保持不变 */
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
        background: #fafafa;
        border: 0px;
        font-size: 14px;

        &.active {
          background-color: #409eff;
          color: white;
        }

        &:hover {
          background-color: #409eff;
          color: white;
        }
      }
    }
  }
}

.article-edit-shortcut {
    position: absolute;
    bottom: 0px;
    font-size: 14px;
    line-height: 2rem;

    .title {
      font-weight: bold;
      font-size: 14px;
    }

    .content {
      line-height: 1.5rem;
      color: rgb(132, 145, 165);
    }
}

.article-edit-footer {
  margin-top: 30px;
}

.article-edit-header {
  margin-bottom: 30px;
}
</style>