<template>
  <ContentFormatterContainer>

    <div class="ai-document-container">
      <!-- Top Navigation Bar -->
      <header class="header">
        <div class="container">
          <!-- Logo and Title -->
          <div class="logo">
            <i class="fa-solid fa-file-lines"></i>
            <h1>AI智能文档</h1>
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
            <button class="btn-icon">
              <i class="fa-solid fa-clock-rotate-left"></i>
            </button>
            <!-- 保存按钮 -->
            <button class="btn-icon" @click="saveDocument">
              <i class="fa-solid fa-cloud-arrow-up"></i>
            </button>
            <button class="btn-icon">
              <i class="fa-solid fa-gear"></i>
            </button>
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
          <el-scrollbar class="document-paper-scroller" style="height:calc(100vh - 175px)">

            <div class="document-paper">
                <TinyMCEEditor 
                  :minHeight="940" 
                  @setHtml="handleSetHtml"
                  v-model="customEditorContent"
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

        <!-- 运行抽屉 -->
        <div class="aip-flow-drawer flow-control-panel">
            <el-drawer v-model="showDebugRunDialog" :modal="false" size="40%" style="max-width: 700px;" title="预览与调试"
                :with-header="true">
                <div style="margin-top: 0px;">
                    <RoleChatPanel ref="roleChatPanelRef" />
                </div>
            </el-drawer>
        </div>

    </div>
  </ContentFormatterContainer>
</template>

<script setup>

import { onBeforeUnmount, ref, shallowRef, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import SnowflakeId from "snowflake-id";

const snowflake = new SnowflakeId();
const router = useRouter()
const route = useRoute()

import ContentFormatterContainer from './common/ContentFormatterContainer';
import RoleChatPanel from '@/views/base/scene/common/chatPanel';

import TinyMCEEditor from './components/TinyMCEEditor';
import ChatcherSider from './components/ChatcherSider';

import AIRewriteSider from './components/AIRewriteSider';
import AiProofreadSider from './components/AiProofreadSider';
import TemplateSider from './components/TemplateSider';

import { getScene } from '@/api/base/im/scene/contentFormatter';
import { 
  formatContent 
} from '@/api/base/im/scene/contentFormatterLayout';

const currentFunctionId = ref('format')


// 执行面板
const showDebugRunDialog = ref(false);
const roleChatPanelRef = ref(null)

const sceneId = ref(route.query.sceneId)
const channelStreamId = ref(route.query.channelStreamId);

const currentSceneInfo = ref({
  sceneName: '政务公文内容排版 业务开发平台'
})

// AI功能按钮配置数组
const aiFunctionButtons = ref([
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
    default:
      console.warn('未知的AI功能ID:', functionId);
      break;
  }
};

const customEditorContent = ref(`
<html>
	<body>
		<div>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:16pt">
				<span style="font-family:宋体; font-weight:bold; color:#4f81bd">关于</span><span style="font-family:宋体; font-weight:bold; color:#4f81bd">高效阅卷，AIP智能体平台让教学反馈更及时</span><span style="font-family:宋体; font-weight:bold; color:#4f81bd">通知</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
				<span style="font-family:宋体>在教育教学过程中，考试是检验学生学习成果和教师教学效果的重要手段。而阅卷作为考试流程中的关键环节，其效率和准确性直接影响到教学反馈的及时性和有效性。传统的人工阅卷方式不仅耗时费力，而且容易出现误差，给教师和学生都带来了一定的困扰。AIP智能体平台凭借其高效的阅卷功能和及时的结果反馈，为教育教学带来了全新的变革。</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:14pt">
				<span style="font-family:宋体; font-weight:bold; color:#4f81bd">快速阅卷</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
				<span style="font-family:宋体">AIP智能体平台的阅卷功能具有诸多亮点。首先，平台支持在线阅卷，教师无需再面对堆积如山的纸质试卷，只需在电脑前轻轻点击，即可完成阅卷工作。这大大节省了教师的时间和精力，让他们能够将更多的时间投入到教学研究和学生辅导中。</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
				<span style="font-family:宋体">其次，平台的阅卷系统具有高度的准确性和客观性。对于客观题，系统能够自动识别答案并进行评分，避免了人工阅卷可能出现的误差和主观因素的影响。对于主观题，平台也提供了详细的评分参考和标准，帮助教师更加准确地进行评分。</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
				<span style="font-family:宋体">再者，平台能够快速统计学生的考试成绩，并生成详细的成绩分析报告。教师可以通过报告清晰地了解每个学生的得分情况、优势领域和薄弱环节，为后续的教学调整提供有力的依据。同时，平台还能够自动分析学生的错题情况，找出学生在知识掌握方面存在的问题，为教师的教学提供针对性的建议。就像图中展示的那样，平台生成的成绩分析报告清晰展示了考试的总体得分、优势领域、薄弱点以及具体的改进建议等内容，让教师能够一目了然地了解学生的学习情况。</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:14pt">
				<span style="font-family:宋体; font-weight:bold; color:#4f81bd">结果反馈</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
				<span style="font-family:宋体">AIP智能体平台的高效阅卷和结果反馈功能为教育教学带来了多方面的价值。对于教师来说，最大的价值在于提高了教学反馈的效率。传统的人工阅卷方式需要花费大量的时间和精力，往往导致教学反馈的滞后，影响了教学效果。而使用AIP智能体平台，教师可以在考试结束后迅速完成阅卷和成绩统计工作，及时将学生的学习情况反馈给他们，让学生能够尽快了解自己的学习成果，调整学习策略。</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
				<span style="font-family:宋体">同时，平台的成绩分析和错题分析功能为教师提供了丰富的教学参考。教师可以根据学生的考试情况，有针对性地调整教学内容和教学方法，加强对学生薄弱环节的辅导，提高教学质量。例如，如果发现学生在一次函数图像性质方面存在普遍的问题，教师可以在后续的教学中加强这部分内容的讲解和练习。</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
				<span style="font-family:宋体">对于学生来说，平台的及时反馈功能让他们能够尽快了解自己的学习情况，发现自己的优势和不足。通过分析错题，学生可以明确自己在知识掌握方面存在的问题，有针对性地进行复习和巩固。同时，平台提供的学习建议（也为学生的学习提供了明确的方向，帮助他们制定更加科学合理的学习计划，提高学习效果。</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:14pt">
				<span style="font-family:宋体; font-weight:bold; color:#4f81bd">满足不同需求</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
				<span style="font-family:宋体">AIP智能体平台的高效阅卷和结果反馈功能适用于广泛的受众人群。首先是学校教师，无论是小学、中学还是大学的教师，都可以利用平台的这一功能，快速完成阅卷工作，及时反馈学生的学习情况，提高教学效率和质量。</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
				<span style="font-family:宋体">其次是培训机构，在竞争激烈的培训市场中，培训机构需要不断提高教学质量和服务水平，以吸引更多的学员。AIP智能体平台的高效阅卷和结果反馈功能能够帮助培训机构快速为学员提供详细的学习反馈和建议（如图，从专项练习到巩固各类知识都有清晰指引），提高学员的学习效果和满意度，增强自身的市场竞争力。</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
				<span style="font-family:宋体">此外，企业内部培训也可以借助AIP智能体平台的这一功能。企业可以通过平台对员工进行培训考核，及时了解员工的学习情况和技能掌握程度，为员工的职业发展提供参考。同时，平台的数据分析功能还能为企业的培训决策提供支持，帮助企业优化培训资源，提高培训效益。</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:14pt">
				<span style="font-family:宋体; font-weight:bold; color:#4f81bd">总结 </span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
				<span style="font-family:宋体">AIP智能体平台的高效阅卷和结果反馈功能，以其便捷高效、精准分析的特点，为教育教学带来了全新的变革。它不仅解决了传统阅卷方式中的诸多难题，还为教师和学生提供了更加及时、准确的教学反馈，提高了教学质量和学习效果。</span>
			</p>
			<p style="margin-top:0pt; margin-bottom:8pt; line-height:150%; widows:0; orphans:0; font-size:12pt">
				<span style="font-family:宋体">在未来，随着教育信息化的不断深入，AIP智能体平台的高效阅卷和结果反馈功能将会不断完善和升级，为教育事业的发展做出更大的贡献。让我们一起携手，利用这一强大的工具，开启教学反馈的新时代，为每一个学生提供更加优质的教育，助力他们在学习的道路上不断进步，实现自己的人生价值。</span>
			</p>
		</div>
	</body>
</html>
`)

const toolbarConfig = {}
const editorConfig = { placeholder: '请输入内容...' }
const mode = 'default' // 或 'simple'

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  // const editor = editorRef.value
  // if (editor == null) return
  // editor.destroy()
})

const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}

const openChatBox = (roleId) => {
  if (showDebugRunDialog.value) {
    return;
  }
  showDebugRunDialog.value = true;
  nextTick(() => {
    roleChatPanelRef.value.openChatBoxWithRole(roleId);
  })
}


const saveDocument = () => {
  const editor = editorRef.value
  console.log('editor.getHtml()');
  console.log('saveDocument = ', editor.getHtml())
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
    templateId: template.id,
    content:  customEditorContent.value ,
  }
  formatContent(data).then(res => {
    console.log('res = ' + res)
  })
};

const handleGetScene = () => {
  getScene(sceneId.value).then(res => {
    currentSceneInfo.value = res.data;
    // chapterEditorRef.value.setData(currentSceneInfo.value.contentPromptContent);
  })
}

onMounted(() => {
  // console.log('chapterEditorRef = ', chapterEditorRef.value)
  // handleGetScene() ;

  if (!route.query.channelStreamId) {
    router.replace({
      query: {
        ...route.query,
        channelStreamId: snowflake.generate()
      }
    });
  }
})

</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-formatter.scss';
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
}

.aip-flow-drawer .el-drawer__header {
  margin-bottom: 0px;
}

</style>
