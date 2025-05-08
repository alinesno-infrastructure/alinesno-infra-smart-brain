<template>
  <div class="deepsearch-text-container">

    <RoleSelectPanel 
      :currentSeneInfo="currentSceneInfo"
      @openExecuteHandle="openExecuteHandle"
      ref="roleSelectPanelRef" />

    <div class="main-content">
      <div class="title-section">
        <span class="title">
          <i class="fa-solid fa-poo-storm"></i> 深度DeepResearch服务
        </span>
        <span class="description">依托强大的通用智能体，轻松应对各类任务。输入需求，快速获得精准、专业的解决方案</span>
      </div>
      <div class="input-button-section">
        <el-input v-model="promptText" class="input-box" size="large" placeholder="请输入您的需求，获取智能体服务" :prefix-icon="Search" />
        <el-button type="primary" size="large" @click="generaterText()" class="send-button">
          <i class="fa-solid fa-paper-plane" style="font-size:22px"></i>
        </el-button>
      </div>
      <div class="example-section">
        <div class="example-title">你可以这样提问</div>
        <div class="example-list">
          <div v-for="(item, index) in greetingQuestionList" :key="index" class="example-item" @click="handleExampleClick(item)">
            <span class="example-text">{{ item.text }}</span>
            <i class="fa-solid fa-paper-plane example-icon"></i>
          </div> 
        </div>
      </div>
    </div>
    <div class="review-footer-message">
      <div class="footer-message">服务生成的所有内容均由人工智能模型生成，其生成内容的准确性和完整性无法保证，不能代表我们的态度和观点。</div>
    </div>

    <!-- 弹出窗口，用于保存场景信息 -->
    <el-dialog v-model="isShowSaveSceneDialog" 
      title="保存场景" 
      width="600px" 
      :before-close="handleCloseSaveSceneDialog">
      <div class="save-scene-dialog-content">
        <el-form
          ref="saveSceneFormRef"
          :label-position="'top'"
          :model="saveSceneForm"
          label-width="80px"
          size="large"
          :rules="rules"
        >
          <el-form-item label="场景横幅" prop="sceneBanner">
              <el-scrollbar height="110px">
            <el-radio-group style="width:100%" v-model="saveSceneForm.sceneBanner">

              <el-radio
                v-for="(image, index) in bannerImages"
                style="height:60px"
                :key="index"
                :label="image"
              >
                <img :src="image" alt="banner" width="90" style="border-radius: 3px;" />
              </el-radio>
            </el-radio-group>
              </el-scrollbar>
          </el-form-item>
          <el-form-item label="场景名称" prop="sceneName">
            <el-input v-model="saveSceneForm.sceneName" placeholder="比如法律知识库深度搜索" />
          </el-form-item>

          <!-- 场景描述 -->
          <el-form-item label="场景描述" prop="sceneDesc">
            <el-input v-model="saveSceneForm.sceneDesc" placeholder="比如基于法律知识库深度搜索，重点在于案例和咨询" />
          </el-form-item>

          <el-form-item style="text-align: right;">
            <div style="text-align: right;width:100%;margin-top:20px;margin-bottom:0px;">
              <el-button @click="isShowSaveSceneDialog = false">取消</el-button>
              <el-button type="primary" @click="submitForm">保存</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'

import { 
  getScene, 
  updateChapterPromptContent 
} from '@/api/base/im/scene/deepSearch';
import SnowflakeId from "snowflake-id";

const roleSelectPanelRef = ref(null)
const snowflake = new SnowflakeId();
const route = useRoute();
const router = useRouter();

const sceneId = ref(route.query.sceneId)
const isBack = ref(route.query.back || false)
const promptText = ref('');

// 弹出场景信息保存对话框
const isShowSaveSceneDialog = ref(false);
const saveSceneForm = ref({
  sceneName: '',
  sceneDesc: ''
});
const saveSceneFormRef = ref(null);

// 表单校验规则
const rules = ref({
  sceneName: [
    { required: true, message: '请输入场景名称', trigger: 'blur' },
    { min: 2, max: 20, message: '场景名称长度在 2 到 20 个字符之间', trigger: 'blur' }
  ],
  sceneDesc: [
    { required: true, message: '请输入场景描述', trigger: 'blur' },
    { min: 5, max: 200, message: '场景描述长度在 5 到 200 个字符之间', trigger: 'blur' }
  ]
});

// 横幅图片列表
const bannerImages = ref([
  'http://data.linesno.com/scene-bg/10.jpg',
  'http://data.linesno.com/scene-bg/11.png',
  'http://data.linesno.com/scene-bg/12.png',
  'http://data.linesno.com/scene-bg/13.png',
  'http://data.linesno.com/scene-bg/14.png',
  'http://data.linesno.com/scene-bg/15.png',
  'http://data.linesno.com/scene-bg/16.png',
  'http://data.linesno.com/scene-bg/17.jpg',
  'http://data.linesno.com/scene-bg/18.png',
  'http://data.linesno.com/scene-bg/1.jpg',
  'http://data.linesno.com/scene-bg/19.png',
  'http://data.linesno.com/scene-bg/2.png',
  'http://data.linesno.com/scene-bg/20.png',
  'http://data.linesno.com/scene-bg/21.jpg',
  'http://data.linesno.com/scene-bg/22.png',
  'http://data.linesno.com/scene-bg/23.png',
  'http://data.linesno.com/scene-bg/24.png',
  'http://data.linesno.com/scene-bg/3.png',
  'http://data.linesno.com/scene-bg/4.jpg',
  'http://data.linesno.com/scene-bg/5.jpg',
  'http://data.linesno.com/scene-bg/6.png',
  'http://data.linesno.com/scene-bg/7.png',
  'http://data.linesno.com/scene-bg/8.jpg',
  'http://data.linesno.com/scene-bg/9.jpg'
]);

const greetingQuestionList = ref([
  { text: "制定一份市场营销策划方案" },
  { text: "分析当前热门短视频运营策略" },
  { text: "规划一场线上产品发布会流程" },
  { text: "设计一套员工培训课程体系" }
]);

const currentSceneInfo = ref({
  sceneName: '通用智能体服务',
});

// 关闭提示，在没有保存之前不允许关闭
const handleCloseSaveSceneDialog = () => {
  if (!sceneId.value) {
    ElMessage.error('请先保存场景信息');
    return false;
  }
};

// 表单提交方法
const submitForm = () => {
  saveSceneFormRef.value.validate((valid) => {
    if (valid) {
      // 这里可以添加保存数据的逻辑，例如调用 API
      console.log('表单数据：', saveSceneForm.value);
      ElMessage.success('保存成功');
      isShowSaveSceneDialog.value = false;
    } else {
      return false;
    }
  });
};

const handleGetScene = () => {

  getScene(sceneId.value).then(res => {
    currentSceneInfo.value = res.data;
    // handleRoleBySceneIdAndAgentType();

    if(res.data.greetingQuestion && res.data.greetingQuestion.length > 0){
      greetingQuestionList.value = [] ; 
      res.data.greetingQuestion.forEach(item => {
        greetingQuestionList.value.push({
          text: item
        });
      });
    }

    if(!currentSceneInfo.value.businessProcessorEngineer || !currentSceneInfo.value.dataViewerEngineer){ // 选择配置角色
      roleSelectPanelRef.value.configAgent();
      return ;
    }

    if(res.data.genStatus == 1 && !isBack.value){
      router.push({
        path: '/scene/generalAgent/agentParser',
        query: {
          sceneId: sceneId.value,
          genStatus: true ,
          channelStreamId: snowflake.generate() 
        }
      })
    }
  })
}

const generaterText = () => {
  if (!promptText.value) {
    ElMessage.error('请输入内容');
    return;
  }
  ElMessage.success('处理完成');

  updateChapterPromptContent({
    sceneId: sceneId.value,
    promptContent: promptText.value
  }).then(res => {
    router.push({
      path: '/scene/generalAgent/agentParser',
      query: {
        sceneId: sceneId.value,
        genStatus: true ,
        channelStreamId: snowflake.generate() 
      }
    })
  })

}

const handleExampleClick = (item) => {
  promptText.value = item.text;
  // generaterText();
}

onMounted(() => {

  if(!sceneId.value){
    // 配置深度搜索场景
    isShowSaveSceneDialog.value = true;
  }else{
    handleGetScene();
  }
})

</script>

<style lang="scss" scoped>
.deepsearch-text-container {
  background: #fff;
  height: calc(100vh - 70px);

 .main-content {
    display: flex;
    flex-direction: column;
    padding-top: calc(22vh - 56px);
    text-align: center;
    max-width: 1024px;
    margin: auto;

   .title-section {
      display: flex;
      flex-direction: column;
      text-align: left;

     .title {
        color: #2C2C36;
        font-weight: 600;
        font-size: 28px;
        margin-bottom: 10px;
        line-height: 40px;
      }

     .description {
        margin-top: 10px;
        color: #8F91A8;
        font-weight: 400;
        font-size: 16px;
        line-height: 24px;
      }
    }

   .input-button-section {
      display: flex;
      gap: 10px;
      position: relative;
      box-sizing: border-box;
      width: 100%;
      border-radius: 8px;
      box-shadow: rgba(54, 54, 73, 0.06) 0px 12px 24px -16px, rgba(74, 80, 96, 0.12) 0px 12px 40px, rgba(44, 44, 54, 0.02) 0px 0px 1px;
      transition: 0.3s;
      background: rgb(255, 255, 255);
      padding: 10px !important;
      border: 1px solid rgb(232, 234, 242);
      margin-top: 30px;
      margin-bottom: 30px;
      align-items: center;

     .input-box {
        width: 100%;
        height: 50px;
        border: 0px !important;
        margin-bottom: 0px;
      }
    }

   .example-section {
      padding: 0px 0px;

     .example-title {
        color: rgb(44, 44, 54);
        font-size: 14px;
        text-align: left;
        margin-left: 5px;
        margin-top: 15px;
        margin-bottom: 15px;
      }

     .example-list {
        display: flex;
        flex-wrap: wrap;

       .example-item {
          position: relative;
          display: flex;
          gap: 8px;
          align-items: center;
          height: 40px;
          background: rgb(242, 243, 247);
          border-radius: 8px;
          cursor: pointer;
          transition: 0.25s cubic-bezier(0.4, 0, 0.2, 1);
          will-change: opacity, transform;
          width: calc(50% - 10px);
          box-sizing: border-box;
          padding: 10px;
          margin: 5px 5px 8px 5px;

          &:hover {
            background: rgb(232 233 235);
           .example-icon {
              display: block;
            }
          }

          .example-icon {
            display: none;
            color: #585a73;
            font-size:12px;
          }

         .example-text {
            flex: 1 1;
            overflow: hidden;
            color: #585a73;
            font-size: 14px;
            white-space: nowrap;
            text-align: left;
            text-overflow: ellipsis;
            transition: padding-right .2s ease-out;
          }
        }
      }
    }
  }

 .review-footer-message {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 50px;
    height: 36px;
    padding: 12px 0px;
    text-align: center;

   .footer-message {
      margin-bottom: 4px;
      color: #C8CAD9;
      font-size: 12px;
      line-height: 12px;
    }
  }
}

</style>


<style>
.main-content .el-input__wrapper {
  box-shadow: none !important;
}
</style>