<template>
  <div class="deepsearch-text-container">

    <RoleSelectPanel :currentSeneInfo="currentSceneInfo" @openExecuteHandle="openExecuteHandle"
      ref="roleSelectPanelRef" />

    <div class="main-content">
      <div class="title-section">
        <span class="title">
          <i class="fa-solid fa-poo-storm"></i> 深度DeepResearch服务
        </span>
        <span class="description">依托强大的通用智能体，轻松应对各类任务。输入需求，快速获得精准、专业的解决方案</span>
      </div>
      <div class="input-button-section">

        <!-- 附件内容-->
        <AttachmentSetionPanel @upload-success="handleUploadSuccess" ref="attachmentPanelRef"
          style="padding: 0px 0px;margin-bottom:0px;" />

        <el-input v-model="promptText" class="input-box" size="large" placeholder="请输入您的需求，获取智能体服务"
          :prefix-icon="Search" />

        <!-- 上传附件按键 -->
        <div class="upload-button-section">
          <div>
            <el-button type="primary" size="large" class="upload-button" @click="handleUpload" text bg>
              <i class="fa-solid fa-paperclip"></i> 上传附件
            </el-button>
          </div>

          <el-button type="primary" size="large" @click="generaterText()" class="send-button">
            <i class="fa-solid fa-paper-plane" style="font-size:22px"></i>
          </el-button>
        </div>
      </div>
      <div class="example-section">
        <div class="example-title">你可以这样提问</div>
        <div class="example-list">
          <div v-for="(item, index) in greetingQuestionList" :key="index" class="example-item"
            @click="handleExampleClick(item)">
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
    <el-dialog v-model="isShowSaveSceneDialog" title="保存场景" width="600px" :before-close="handleCloseSaveSceneDialog">
      <div class="save-scene-dialog-content">
        <el-form ref="saveSceneFormRef" :label-position="'top'" :model="saveSceneForm" label-width="80px" size="large"
          :rules="rules">
          <el-form-item label="场景横幅" prop="sceneBanner">
            <el-scrollbar height="140px">
              <el-radio-group style="width:100%" v-model="saveSceneForm.sceneBanner">

                <el-radio v-for="(image, index) in bannerImages" style="height:70px" :key="index" :label="image">
                  <img :src="'http://data.linesno.com/' + image" alt="banner" width="100" style="border-radius: 3px;" />
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
import { nextTick, ref } from 'vue';
import { ElMessage } from 'element-plus';

import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'
import AttachmentSetionPanel from '@/views/base/scene/articleWriting/common/attachmentSection'

import {
  getScene,
  createScene,
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
const attachments = ref([]);

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
  'scene-bg/10.jpg',
  'scene-bg/11.png',
  'scene-bg/12.png',
  'scene-bg/13.png',
  'scene-bg/14.png',
  'scene-bg/15.png',
  'scene-bg/16.png',
  'scene-bg/17.jpg',
  'scene-bg/18.png',
  'scene-bg/1.jpg',
  'scene-bg/19.png',
  'scene-bg/2.png',
  'scene-bg/20.png',
  'scene-bg/21.jpg',
  'scene-bg/22.png',
  'scene-bg/23.png',
  'scene-bg/24.png',
  'scene-bg/3.png',
  'scene-bg/4.jpg',
  'scene-bg/5.jpg',
  'scene-bg/6.png',
  'scene-bg/7.png',
  'scene-bg/8.jpg',
  'scene-bg/9.jpg'
]);

const greetingQuestionList = ref([
  { text: "制定一份市场营销策划方案" },
  { text: "分析当前热门短视频运营策略" },
  { text: "规划一场线上产品发布会流程" },
  { text: "设计一套员工培训课程体系" },
  { text: "制定一份销售策略" },
  { text: "撰写一份品牌推广活动执行方案" },
  { text: "策划一场客户答谢会活动方案" },
  { text: "制定新媒体平台内容运营计划" }
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

      saveSceneForm.value.sceneScope = 'org';
      saveSceneForm.value.sceneType = 'deepSearch';

      console.log('表单数据：', saveSceneForm.value);

      createScene(saveSceneForm.value).then(res => {
        ElMessage.success('保存成功');
        isShowSaveSceneDialog.value = false;
      })

    } else {
      return false;
    }
  });
};


// 添加attachmentPanelRef引用
const attachmentPanelRef = ref(null);

// 处理上传成功
const handleUploadSuccess = (fileInfo) => {
  console.log('文件上传成功:', fileInfo);
  // 这里可以保存storageId到表单数据中
  // 例如: formData.value.attachments.push(fileInfo.storageId);
  attachments.value.push(fileInfo.storageId);
};

// 修改handleUpload方法，直接调用子组件的上传
const handleUpload = () => {
  if (attachmentPanelRef.value) {
    attachmentPanelRef.value.$el.querySelector('.el-upload__input').click();
  }
};

const handleGetScene = () => {

  getScene(sceneId.value).then(res => {
    currentSceneInfo.value = res.data;
    // handleRoleBySceneIdAndAgentType();

    if (res.data.greetingQuestion && res.data.greetingQuestion.length > 0) {
      greetingQuestionList.value = [];
      res.data.greetingQuestion.forEach(item => {
        greetingQuestionList.value.push({
          text: item
        });
      });
    }

    if (!currentSceneInfo.value.searchPlannerEngineer) { // 选择配置角色
      nextTick(() => {
        // roleSelectPanelRef.value.configAgent();
      });
      return;
    }

    if (res.data.genStatus == 1 && !isBack.value) {
      router.push({
        path: '/scene/deepsearch/index',
        query: {
          sceneId: sceneId.value,
          genStatus: true,
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
    channelStreamId: snowflake.generate(),
    attachments: attachments.value.join(','),
    promptContent: promptText.value
  }).then(res => {

    const taskId = res.data ;

    router.push({
      path: '/scene/deepsearch/taskPanel',
      query: {
        sceneId: sceneId.value,
        taskId: taskId,
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

  // if(!sceneId.value){
  //   // 配置深度搜索场景
  //   isShowSaveSceneDialog.value = true;
  // }else{
  //   handleGetScene();
  // }

  handleGetScene();
})

</script>

<style lang="scss" scoped>
.deepsearch-text-container {
  background: #fff;
  height: calc(100vh - 70px);

  .main-content {
    display: flex;
    flex-direction: column;
    padding-top: calc(15vh - 56px);
    text-align: center;
    max-width: 860px;
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
      border-radius: 15px;
      transition: 0.3s;
      background: rgb(255, 255, 255);
      padding: 10px !important;
      border: 1px solid rgb(232, 234, 242);
      margin-top: 30px;
      margin-bottom: 30px;
      align-items: flex-start;
      flex-direction: column;

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
        justify-content: center;

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
          width: auto; // calc(50% - 10px);
          box-sizing: border-box;
          padding: 10px;
          margin: 5px 5px 8px 5px;

          &:hover {
            background: rgb(232 233 235);

            .example-icon {
              visibility: visible;
            }
          }

          .example-icon {
            visibility: hidden;
            color: #585a73;
            font-size: 12px;
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