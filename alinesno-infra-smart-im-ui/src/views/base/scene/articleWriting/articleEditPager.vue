<template>
  <ArticleWriterContainer class="article-edit-container edit-display-container">
    <el-container style="height:calc(100vh - 40px );background-color: #fff;"> 
      <el-main class="article-edit-main">

        <div class="article-edit-content">
          <!-- 标题内容 -->
          <EditableTitle 
            v-model:title="articleData.title" 
            class="article-edit-title" 
          />

          <!-- 编辑内容 -->
          <div class="article-edit-content-display">
            <AgentContentDisplay v-model:articleData="articleData" @content-change="handleContentChange"
              ref="contentEditor" />
          </div>
        </div>
      </el-main>

      <el-aside width="300px" class="article-edit-right-aside">
        <EditorFunctionPanel @handleSaveArticle="handleSaveArticle" :articleData="articleData"
          :resultConfig="articleConfig" :promptText="promptText" @handleExportArticle="handleExportArticle"
          @config-change="handleConfigChange" />
      </el-aside>

    </el-container>

    <!-- 运行抽屉 -->
    <div class="aip-flow-drawer flow-control-panel">
      <el-drawer v-model="showDebugRunDialog" :modal="false" size="40%" style="max-width: 700px;" title="预览与调试"
        :with-header="true">
        <div style="margin-top: 0px;">
          <RoleChatPanel ref="roleChatPanelRef" :showDebugRunDialog="showDebugRunDialog" />
        </div>
      </el-drawer>
    </div>

    <!-- 添加预览组件 -->
    <MarkdownPreview 
      ref="previewDialogRef" 
      :content="previewContent" 
      @confirm="handlePreviewConfirm"
      @regenerate="handlePreviewRegenerate" />

                <!-- AI生成状态 -->
        <AIGeneratingStatus 
            ref="generatingStatusRef"  
            :close-enable="false" 
        />

  </ArticleWriterContainer>
</template>

<script setup>

import { onMounted, ref } from 'vue';

import {
  getArticleById,
  reChatPromptContent,
  updateArticle,
  getScene
} from '@/api/base/im/scene/articleWriting';

import ArticleWriterContainer from "./articleWriterContainer"
import EditableTitle from './components/EditableTitle.vue'
import MarkdownPreview from './components/MarkdownPreview.vue';
import RoleChatPanel from '@/views/base/scene/common/chatPanel';
import FunctionList from './functionList'
import AgentContentDisplay from './agentContentDisplay'
import EditorFunctionPanel from './components/editorFunctionPanel'
import AIGeneratingStatus from '@/components/GeneratingStatus/index.vue'

import { ElMessage, ElLoading, ElMessageBox } from 'element-plus';

const { proxy } = getCurrentInstance();

// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)
const streamLoading = ref(null)
const promptText = ref('')

const route = useRoute();
const sceneId = ref(route.query.sceneId)
const channelStreamId = ref(route.query.channelStreamId)
const articleId = ref(route.query.articleId)
const contentEditor = ref(null);

const generatingStatusRef = ref(null)

const currentSceneInfo = ref({
  sceneName: '通用智能体服务',
});

const articleData = ref({
  title: '默认标题',
  content: '',
  description: ''
});

const articleConfig = ref({
  "styles": [],
  "hooks": [],
  "time": []
});

// 添加防抖标志
const isSaving = ref(false);

// 原有响应式变量保持不变
const previewDialogRef = ref(null);
const previewContent = ref('');
const currentPromptText = ref('');

// 定义用于展示的响应式变量
const currentStyle = ref('默认');
const currentScene = ref('默认');
const currentNum = ref('300字');

// 接收子组件传递的配置变更事件
const handleConfigChange = (config) => {
  // 更新父组件的状态
  currentStyle.value = articleConfig.value.styles.find(
    style => style.key === config.style
  )?.name || '默认';
  currentScene.value = articleConfig.value.hooks.find(
    hook => hook.key === config.scene
  )?.name || '默认';
  currentNum.value = articleConfig.value.time.find(
    num => num.key === config.num
  )?.name || '300字';

  // 可在此处触发后续逻辑（如生成文章、保存配置等）
  console.log('最新配置：', config);
  generaterText(config.promptText);
};

// 处理内容变化
const handleContentChange = (content) => {
  console.log('内容已更新:', content);
  // 可以在这里添加自动保存逻辑
  articleData.value.content = content;
};

// 修改保存方法，返回Promise
const handleSaveArticle = () => {
  return new Promise((resolve, reject) => {
    // 发起保存请求
    updateArticle(articleData.value).then(res => {
      console.log('保存成功:', res);
      ElMessage.success('保存成功');
      resolve();
    }).catch(err => {
      console.error('保存失败:', err);
      reject(err);
    });
  });
};

// 导出文章
const handleExportArticle = () => {

  // 如果长度过长，则截取前10个字符
  const downloadTitle = articleData.value.title.length > 10 ? articleData.value.title.substring(0, 10) : articleData.value.title;

  proxy.download('/api/infra/smart/assistant/scene/articleGenerate/export/' + articleId.value, {
  }, `${downloadTitle}_${new Date().getTime()}.docx`)
}

// 重构后的 generaterText 方法
const generaterText = async (promptText) => {
  currentPromptText.value = promptText;
  
  // 打开流容器
  showDebugRunDialog.value = true;
  await nextTick(() => {
    roleChatPanelRef.value.openChatBoxWithRole(currentSceneInfo.value.articleWriterEngineer);
  });

 
  generatingStatusRef.value?.loading()
  generatingStatusRef.value?.setText("正在重新生成中...") ;
  
  try {
    const articleData = {
      sceneId: currentSceneInfo.value.id,
      articleId: articleId.value,
      channelStreamId: channelStreamId.value,
      promptText: promptText
    };

    const res = await reChatPromptContent(articleData);
    previewContent.value = res.data;
    
    // 关闭加载状态
    generatingStatusRef.value?.close();
    showDebugRunDialog.value = false;
    
    // 显示预览 - 现在使用正确的引用和方法
    previewDialogRef.value?.open();
    
  } catch (err) {
    console.error('内容处理失败:', err);
    ElMessage.error('内容生成失败');
  } finally {
    generatingStatusRef.value?.close();
    showDebugRunDialog.value = false;
  }
};

// 预览确认回调
const handlePreviewConfirm = (content) => {
  handleContentChange(content);
  ElMessage.success('内容已替换');
};

// 预览重新生成回调
const handlePreviewRegenerate = async () => {
  ElMessage.info('重新生成内容');
  await generaterText(currentPromptText.value);
};


const handleGetScene = () => {
  getScene(sceneId.value).then(res => {
    currentSceneInfo.value = res.data;
  })
}

// 键盘事件处理（添加防抖逻辑）
const handleKeyDown = (e) => {
  // 检测是否按下 Ctrl+S (Windows/Linux) 或 Cmd+S (Mac)
  if ((e.ctrlKey || e.metaKey) && e.key === 's') {
    e.preventDefault(); // 阻止浏览器默认的保存对话框
    
    // 如果正在保存中，则不再重复执行
    if (isSaving.value) return;
    
    // 设置保存状态为true
    isSaving.value = true;
    
    // 调用保存方法
    handleSaveArticle().finally(() => {
      // 保存完成后重置状态
      isSaving.value = false;
    });
  }
};

onMounted(() => {
  // 组件挂载时添加键盘事件监听
  window.addEventListener('keydown', handleKeyDown);

  handleGetScene();
  // 初始化时，根据文章ID获取文章内容
  getArticleById(articleId.value).then(res => {
    console.log('res = ' + res);
    articleData.value = res.data;
  }).catch(err => {
    ElMessage.error('获取文章失败:', err);
  })
});

onBeforeUnmount(() => {
  // 组件卸载时移除键盘事件监听，防止内存泄漏
  window.removeEventListener('keydown', handleKeyDown);
});

</script>

<style lang="scss" scoped>
.article-edit-container {
  .article-edit-aside {
    padding: 0px;
    border-right: 1px solid #f2f3f7;
    background: #fff;
    margin-bottom: 0px;
  }

  .article-edit-header {
    text-align: right;
    margin-right: 20px;
    margin-top: 20px;
  }

  .article-edit-right-aside {
    padding: 20px;
    margin-bottom: 0px;
    display: flex;
    flex-direction: column;
    height: calc(100vh - 40px);
    border-left: 1px solid #f2f3f7;
    background-color: #fff;
  }

  .article-edit-main {
    padding: 0px !important;
  }

  .article-edit-content {
    width: calc(100% - 30px);
    margin: auto;
    margin-top: 20px;
    height: calc(100vh - 70px);
    margin-bottom: 10px;

    .article-edit-title {
      margin: 10px 0px;
    }

  }

}

.markdown-preview-dialog {
  .el-dialog__body {
    padding: 0;
  }
  
  .markdown-body {
    pre {
      background-color: #f6f8fa;
      border-radius: 6px;
      padding: 16px;
    }
    
    table {
      border-collapse: collapse;
      width: 100%;
      
      th, td {
        padding: 6px 13px;
        border: 1px solid #dfe2e5;
      }
    }
  }
}
</style>