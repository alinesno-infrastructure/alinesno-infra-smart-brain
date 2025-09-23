<template>
  <ContentFormatterContainer>

    <div class="ai-document-container">
      <!-- Top Navigation Bar -->
      <header class="header">
        <div class="container">
          <!-- Logo and Title -->
          <div class="logo">
            <div class="logo-title">
              <i class="fa-solid fa-file-lines"></i>
              <!-- 标题内容 -->
              <EditableDocTitle 
                v-model:title="currentDocumentInfo.documentName" 
                @update:title="handleSaveDocument"
                class="document-edit-title" />
            </div> 

            <el-divider direction="vertical" />
           
            <el-tooltip class="box-item" effect="dark" content="转换成PDF" placement="top">
              <button class="btn-icon">
                 <i class="fa-solid fa-file-pdf"></i>
              </button>
            </el-tooltip>

            <el-tooltip class="box-item" effect="dark" content="打印" placement="top">
              <button class="btn-icon">
                <i class="fa-solid fa-print"></i>
              </button>
            </el-tooltip> 

          </div>

          <!-- AI Function Buttons -->
          <!-- AI功能按钮区域 -->
          <div class="ai-functions">
            <!-- 使用v-for循环渲染AI功能按钮 -->
            <el-button v-for="btn in aiFunctionButtons" :key="btn.id" class="btn"
              :class="{ 'btn-primary': btn.isPrimary }" @click="handleAiFunctionClick(btn)">
              <!-- 按钮图标 -->
              <i :class="btn.icon"></i>
              <!-- 按钮文字 -->
              <span>{{ btn.label }}</span>
            </el-button>
          </div>

          <!-- Utility Buttons -->
          <div class="utility-buttons">
            <!-- 自动保存 -->
            <el-tooltip class="box-item" effect="dark" content="自动保存" placement="top">
              <button class="btn-icon">
                <i class="fa-solid fa-clock-rotate-left"></i>
              </button>
            </el-tooltip>
            <!-- 保存按钮 -->
            <el-tooltip class="box-item" effect="dark" content="保存" placement="top">
              <button class="btn-icon" @click="handleSaveDocument">
                <i class="fa-solid fa-cloud-arrow-up"></i>
              </button>
            </el-tooltip>
            <!--  导出按钮  -->
            <el-tooltip class="box-item" effect="dark" content="导出Docx" placement="top">
              <button class="btn-icon" @click="exportDocument">
                <i class="fa-solid fa-file-word"></i>
              </button>
            </el-tooltip>
          </div>
          
        </div>

      </header>

      <!-- Main Content Area -->
      <main class="main-content">
        <!-- Left Sidebar - Document Outline -->
        <aside class="sidebar left-sidebar">
          <ChatcherSider />
        </aside>

        <!-- Main Document Content -->
        <section class="document-content">
          <el-scrollbar class="document-paper-scroller" style="height:calc(100vh - 170px)">

            <div class="document-paper">
                <TinyMCEEditor 
                  :minHeight="940" 
                  @setHtml="handleSetHtml"
                  ref="tinyMCEEditorRef"
                  v-model="customEditorContent"
                  :contentMargins="currentMargins"
                  />
            </div>

          </el-scrollbar>

          <!-- 控制document-paper -->

        </section>

        <!-- Right Sidebar - Templates -->
        <aside class="sidebar right-sidebar">
         <!-- AI重写 -->
         <AIRewriteSider v-if="currentFunctionId == 'rewrite'" />
         <!-- AI校对  -->
         <AiProofreadSider v-if="currentFunctionId == 'proofread'" />
          <!-- AI排版 -->
          <TemplateSider v-if="currentFunctionId == 'format'" @select="handleTemplateSelect" />
        </aside>
      </main>

      <!--   排版过后，用户确认是否要替换当前的html内容 -->
      <div class="confirmation-banner" v-if="confirmReplaceDialog">
        <span><i class="fa-solid fa-circle-question"></i> 是否应用当排版</span>
        <span>
          <el-button type="danger" size="large" @click="handleTemplateConfirm(false)">弃用</el-button>
          <el-button type="primary" size="large" @click="handleTemplateConfirm(true)">应用当前模板</el-button>
        </span>
      </div>

        <!-- 运行抽屉 -->
        <div class="aip-flow-drawer flow-control-panel">
            <el-drawer v-model="showDebugRunDialog" :modal="false" size="40%" style="max-width: 700px;" title="预览与调试"
                :with-header="true">
                <div style="margin-top: 0px;">
                    <RoleChatPanel ref="roleChatPanelRef" />
                </div>
            </el-drawer>
        </div>
 
<!-- 边距设置弹窗 -->
  <el-dialog v-model="showMarginDialog" title="边距设置" width="700px">
    <div class="margin-settings">
      <div class="presets">
        <h4>预设边距</h4>
        <div class="preset-buttons">
          <el-button 
            v-for="preset in marginPresets" 
            :key="preset.value"
            @click="selectPreset(preset)"
            :type="JSON.stringify(currentMargins) === JSON.stringify(preset.margins) ? 'primary' : ''">
            {{ preset.label }}
          </el-button>
        </div>
      </div>
      
      <div class="custom-margins">
        <h4>自定义边距 (单位: 厘米)</h4>
        <div class="margin-controls">
          <div class="margin-control">
            <label>上边距</label>
            <el-input-number v-model="currentMargins.top" :min="0" :max="10" :step="0.1" :precision="1" />
          </div>
          <div class="margin-control">
            <label>下边距</label>
            <el-input-number v-model="currentMargins.bottom" :min="0" :max="10" :step="0.1" :precision="1" />
          </div>
          <div class="margin-control">
            <label>左边距</label>
            <el-input-number v-model="currentMargins.left" :min="0" :max="10" :step="0.1" :precision="1" />
          </div>
          <div class="margin-control">
            <label>右边距</label>
            <el-input-number v-model="currentMargins.right" :min="0" :max="10" :step="0.1" :precision="1" />
          </div>
        </div>
      </div>
      
      <div class="margin-preview">
        <h4>边距预览</h4>
        <div class="preview-box">
          <div class="margin-visualization top">
            <span class="margin-label top-label">{{ currentMargins.top }} cm</span>
          </div>
          <div class="margin-visualization right">
            <span class="margin-label right-label">{{ currentMargins.right }} cm</span>
          </div>
          <div class="margin-visualization bottom">
            <span class="margin-label bottom-label">{{ currentMargins.bottom }} cm</span>
          </div>
          <div class="margin-visualization left">
            <span class="margin-label left-label">{{ currentMargins.left }} cm</span>
          </div>
          <div class="content-area">
            内容区域
          </div>
        </div>
      </div>
    </div>
    
    <template #footer>
      <el-button type="danger" @click="showMarginDialog = false">取消</el-button>
      <el-button type="primary" @click="applyMargins">应用</el-button>
    </template>
  </el-dialog>
        

    </div>
  </ContentFormatterContainer>
</template>

<script setup>

import { onBeforeUnmount, ref, shallowRef, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElLoading , ElMessage  } from 'element-plus'
import SnowflakeId from "snowflake-id";

const { proxy } = getCurrentInstance();
const snowflake = new SnowflakeId();
const router = useRouter()
const route = useRoute()

import ContentFormatterContainer from './common/ContentFormatterContainer';
import RoleChatPanel from '@/views/base/scene/common/chatPanel';
import EditableDocTitle from './components/EditableDocTitle'

import TinyMCEEditor from './components/TinyMCEEditor';
import ChatcherSider from './components/ChatcherSider';

import AIRewriteSider from './components/AIRewriteSider';
import AiProofreadSider from './components/AiProofreadSider';
import TemplateSider from './components/TemplateSider';

import { getScene } from '@/api/base/im/scene/contentFormatter';
import { 
  formatContent , 
  exportDocx
} from '@/api/base/im/scene/contentFormatterLayout';
import { 
  createNewDocument ,
  saveDocument ,
  detail as documentDetail,
} from '@/api/base/im/scene/contentFormatterDocument';

const tinyMCEEditorRef = ref(null);

const currentFunctionId = ref('format')
const confirmReplaceDialog = ref(false);

// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)

const sceneId = ref(route.query.sceneId) 
const documentId = ref(route.query.documentId)
const channelStreamId = ref(route.query.channelStreamId);
const currentDocumentInfo = ref({
  documentName: '文档标题' 
});
const currentSceneInfo = ref({
  sceneName: '政务公文内容排版 业务开发平台'
})

const showMarginDialog = ref(false);
// 修改当前边距初始值为Word默认值
const currentMargins = ref({ top: 1.9, bottom: 1.9, left: 2.5, right: 2.5 }); 

// 修改边距预设值为Word常用值（单位：厘米）
const marginPresets = ref([
  { label: '普通', value: 'normal', margins: { top: 2.54, bottom: 2.54, left: 3.17, right: 3.17 } }, // Word默认值
  { label: '窄', value: 'narrow', margins: { top: 1.27, bottom: 1.27, left: 1.27, right: 1.27 } },
  { label: '适中', value: 'medium', margins: { top: 2.54, bottom: 2.54, left: 1.91, right: 1.91 } },
  { label: '宽', value: 'wide', margins: { top: 5.08, bottom: 5.08, left: 5.08, right: 5.08 } },
  { label: '公文标准', value: 'official', margins: { top: 3.7, bottom: 3.5, left: 2.8, right: 2.6 } } // 中国公文标准
]);

// AI功能按钮配置数组
const aiFunctionButtons = ref([
  { id: 'margin', icon: 'fa-solid fa-ruler', label: '边距设置' },                // 新增边距设置
  { id: 'rewrite', icon: 'fa-solid fa-rotate', label: 'AI重写' },              // AI重写
  { id: 'format', icon: 'fa-solid fa-align-left', label: 'AI排版' },            // AI排版
  { id: 'proofread', icon: 'fa-solid fa-spell-check', label: 'AI校对' },        // 新增：AI校对
  { id: 'chat', icon: 'fa-solid fa-comments' , label: 'AI咨询' },
]);

// 处理AI功能按钮点击事件
const handleAiFunctionClick = (functionBtn) => {

  const functionId = functionBtn.id;
  console.log(`点击了AI功能: ${functionId}`);

  // 当前点击的functionBtn设置为isPrimary=true，其它的设置为isPrimary=false
  aiFunctionButtons.value = aiFunctionButtons.value.map(btn => {
    btn.isPrimary = btn.id === functionId;
    return btn;
  });


  // 根据不同的功能ID执行不同的操作
  switch (functionId) {
    case 'polish':
      // 执行AI润色逻辑
      break;
    case 'continue':
      // 执行AI续写逻辑
      break;
    case 'expand':
      // 执行AI扩写逻辑
      break;
    case 'rewrite':
      // 执行AI重写逻辑
      currentFunctionId.value = functionId ;
      break;
    case 'format':
      // 执行AI排版逻辑
      currentFunctionId.value = functionId ;
      break;
    case 'proofread':
      // 执行AI校对逻辑
      currentFunctionId.value = functionId ;
      break;
    case 'summarize':
      // 执行AI总结逻辑
      break;
    case 'chat':
      // 执行AI问答逻辑
      openChatBox('1920860135564115969');
      break;
    case 'margin':
      showMarginDialog.value = true;
      break;
    default:
      console.warn('未知的AI功能ID:', functionId);
      break;
  }
};

const preCustomEditorContent = ref(``) ;  // 未排版前的html内容
const customEditorContent = ref(``) // 正式的html内容

const toolbarConfig = {}
const editorConfig = { placeholder: '请输入内容...' }
const mode = 'default' // 或 'simple'

const openChatBox = (roleId) => {
  if (showDebugRunDialog.value) {
    return;
  }
  showDebugRunDialog.value = true;
  nextTick(() => {
    roleChatPanelRef.value.openChatBoxWithRole(roleId);
  })
}
 
// 修改应用边距方法，将厘米转换为像素（1cm ≈ 37.8px）
const applyMargins = () => {
  const editor = tinyMCEEditorRef.value;
  if (editor && editor.updateMargins) {
     editor.updateMargins(currentMargins.value);
  }
  showMarginDialog.value = false;
};

// 选择预设边距
const selectPreset = (preset) => {
  currentMargins.value = { ...preset.margins };
  applyMargins();
};


// 保存文档
const handleSaveDocument = () => {

  const data = {
    ... currentDocumentInfo.value ,
    documentId: documentId.value ,
    sceneId: sceneId.value , 
    documentContent: customEditorContent.value  ,
  }

  saveDocument(data).then(res => {
    console.log('res = ' + res) ;
    ElMessage.success('文档保存成功');
  })

}


const handleSetHtml = (textContent, htmlContent) => {
  console.log("纯文本内容:", textContent);
  console.log("HTML 内容:", htmlContent);
  
  // 可以存储到 data 中
  // someTextData.value = textContent;
  // someHtmlData.value = htmlContent;
};

const handleTemplateSelect = (template) => {
  console.log('选择的模板:', template.id);
  console.log('文档内容', customEditorContent.value);
  // 处理模板选择逻辑
  const data = {
    documentId: documentId.value ,
    templateId: template.id,
    content:  customEditorContent.value ,
  }

  preCustomEditorContent.value = customEditorContent.value // 临时保存，用户确认之前替换

  formatContent(data).then(res => {
    console.log('res = ' + res)
    customEditorContent.value = res.data;
    confirmReplaceDialog.value = true;
  })
};

const handleGetScene = () => {
  getScene(sceneId.value).then(res => {
    currentSceneInfo.value = res.data;
    // chapterEditorRef.value.setData(currentSceneInfo.value.contentPromptContent);
  })
}

// 确认是否使用当前模板
const handleTemplateConfirm = (type) => { 
  if(!type){
    customEditorContent.value = preCustomEditorContent.value;
  }else{
    handleSaveDocument();
  }
  confirmReplaceDialog.value = false;
};

// 导出Document
const exportDocument = () => { 
   const loading = ElLoading.service({
    lock: true,
    text: '正在导出 ...',
    background: 'rgba(0, 0, 0, 0.7)',
  })

  const data = {
    content: customEditorContent.value
  }

  // 导出html
  exportDocx(data).then(res => { 
    loading.close();
    
    // 处理响应，创建下载链接
    const blob = new Blob([res], { 
      type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' 
    });
    
    // 创建a标签用于下载
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.href = url;
    
    // 设置下载文件名
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const timestamp = now.getTime();

    // 格式：文件名_YYYYMMDD_时间戳.docx
    link.download = `${currentDocumentInfo.value.documentName}_${year}${month}${day}_${timestamp}.docx`; 
    
    // 触发点击事件开始下载
    document.body.appendChild(link);
    link.click();
    
    // 清理资源
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  }).catch(error => {
    loading.close();
    ElMessage.error('导出失败：' + (error.message || '未知错误'));
  });

};

watch(currentMargins, (newVal) => {
  if (tinyMCEEditorRef.value && tinyMCEEditorRef.value.updateMargins) {
    tinyMCEEditorRef.value.updateMargins(newVal);
  }
}, { deep: true });

const handleGetDetail = () => {
  documentDetail(documentId.value).then(res => {
    ElMessage.success('文档详情获取成功');  
    currentDocumentInfo.value = res.data ;
    customEditorContent.value = res.data.documentContent || '' ;
  })
}

// 添加键盘事件处理函数
const handleKeyDown = (event) => {
  // 检查是否按下了 Ctrl + S
  if ((event.ctrlKey || event.metaKey) && event.key === 's') {
    event.preventDefault(); // 阻止默认保存行为
    handleSaveDocument(); // 调用你的保存方法
  }
};

onMounted(() => {
  window.addEventListener('keydown', handleKeyDown);
  handleGetDetail();
})

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeyDown);
})

</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-formatter.scss';

.confirmation-banner {
    position: fixed;
    z-index: 1000;
    bottom: 20px;
    left: 50%;
    transform: translateX(-55%);
    background: #fff;
    padding: 10px 20px;
    border-radius: 10px;
    display: flex;
    gap: 20px;
    font-size: 15px;
    align-items: center;
    color: #777;
    width: 500px;
    justify-content: space-between;
    box-shadow: 0 0 12px rgba(0, 0, 0, .12);
    border: 1px solid #e4e7ed;
}

.margin-settings {
  padding: 10px;
  
  .presets {
    margin-bottom: 20px;
    
    h4 {
      margin-bottom: 10px;
      color: #666;
      font-size: 14px;
      font-weight: 500;
    }
    
    .preset-buttons {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
      
      .el-button {
        flex: 1;
        min-width: 80px;
      }
    }
  }
  
  .custom-margins {
    margin-top: 25px;
    
    h4 {
      margin-bottom: 15px;
      color: #666;
      font-size: 14px;
      font-weight: 500;
    }
    
    .margin-controls {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
      
      .margin-control {
        display: flex;
        align-items: center;
        
        label {
          width: 60px;
          font-size: 13px;
          color: #606266;
          margin-right: 10px;
        }
        
        .el-input-number {
          flex: 1;
          
          :deep(.el-input__inner) {
            text-align: left;
            padding-left: 8px;
            padding-right: 30px;
          }
        }
      }
    }
  }
  
  .margin-preview {
    margin-top: 25px;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    padding: 15px;
    background-color: #f8f9fa;
    
    h4 {
      margin-bottom: 10px;
      color: #666;
      font-size: 14px;
      font-weight: 500;
    }
    
    .preview-box {
      position: relative;
      height: 200px;
      background-color: #fff;
      border: 1px dashed #dcdfe6;
      
      .margin-visualization {
        position: absolute;
        background-color: rgba(64, 158, 255, 0.1);
        border: 1px dashed #409eff;
        
        &.top {
          top: 0;
          left: 0;
          right: 0;
          height: calc(v-bind('currentMargins.top') * 20px); /* 缩放比例显示 */
        }
        
        &.right {
          top: 0;
          right: 0;
          bottom: 0;
          width: calc(v-bind('currentMargins.right') * 20px); /* 缩放比例显示 */
        }
        
        &.bottom {
          left: 0;
          right: 0;
          bottom: 0;
          height: calc(v-bind('currentMargins.bottom') * 20px); /* 缩放比例显示 */
        }
        
        &.left {
          top: 0;
          left: 0;
          bottom: 0;
          width: calc(v-bind('currentMargins.left') * 20px); /* 缩放比例显示 */
        }
        
        .margin-label {
          position: absolute;
          color: #409eff;
          font-size: 12px;
          
          &.top-label, &.bottom-label {
            left: 50%;
            transform: translateX(-50%);
          }
          
          &.left-label, &.right-label {
            top: 50%;
            transform: translateY(-50%);
          }
          
          &.top-label {
            top: 50%;
          }
          
          &.right-label {
            right: 5px;
          }
          
          &.bottom-label {
            bottom: 50%;
          }
          
          &.left-label {
            left: 5px;
          }
        }
      }
      
      .content-area {
        position: absolute;
        top: calc(v-bind('currentMargins.top') * 20px);
        right: calc(v-bind('currentMargins.right') * 20px);
        bottom: calc(v-bind('currentMargins.bottom') * 20px);
        left: calc(v-bind('currentMargins.left') * 20px);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #909399;
        font-size: 14px;
      }
    }
  }
}

</style>

<style>
.flow-control-panel .el-card__body {
  padding: 13px !important
}

.aip-flow-drawer .el-drawer.ltr,
.aip-flow-drawer .el-drawer.rtl {
  height: 93%;
  bottom: 10px;
  top: auto;
  right: 10px;
  border-radius: 8px;
  position: absolute;
}

.aip-flow-drawer .el-drawer__header {
  margin-bottom: 0px;
}

</style>
