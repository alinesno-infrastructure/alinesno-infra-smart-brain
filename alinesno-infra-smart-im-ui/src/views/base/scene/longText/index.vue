<template>

  <div class="ppt-pager-container">

    <el-container style="height:calc(100vh - 40px );background-color: #fff;">

      <el-aside width="80px" class="ppt-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="ppt-pager-main">


            <div class="long-text-container">

              <RoleSelectPanel :currentSeneInfo="currentSceneInfo" @openExecuteHandle="openExecuteHandle"
                ref="roleSelectPanelRef" />

        <el-row>
          <el-col :span="13">
              <div class="main-content">


                <div class="title-section">
                  <span class="title">
                    <i class="fa-solid fa-file-word"></i> 文档生成，一键生成企业文档
                  </span>
                  <span class="description">借助AI智能体，实现企业文档一键生成。输入信息后，瞬间生成结构清晰、内容详实的优质文档</span>
                </div>

                <div class="input-button-section">
                  <div style="width:100%">
                    <!-- 附件内容-->
                    <AttachmentSetionPanel @upload-success="handleUploadSuccess" ref="attachmentPanelRef"
                      style="padding: 0px 0px;margin-bottom:0px;" />

                    <!-- 文本内容 -->
                    <el-input type="textarea" :rows="3" resize="none" v-model="formData.promptText" class="input-box"
                      size="large" placeholder="请输入您的需求，获取智能体服务" :prefix-icon="Search" />

                  </div>

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
                  <div class="example-title">你可以这样说</div>
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

          </el-col>
          <el-col :span="11" style="padding-right: 20px;">
            <div class="review-question-preview-title">
                <span>
                  <i class="fa-solid fa-file-pdf"></i> 文档模板 
                </span>
              </div>

              <!-- 文章模板 -->
              <div class="pager-gen-result-panel">

                <el-scrollbar class="pager-container" ref="scrollbarRef"> 
                  <div ref="innerRef">


                <DatasetTemplatePanel v-model="formData.selectedTemplateId" />

                  </div>
                </el-scrollbar>
              </div>
          </el-col>
        </el-row>
            </div>


      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'
import FunctionList from './functionList'
import AttachmentSetionPanel from '@/views/base/scene/articleWriting/common/attachmentSection'
import DatasetTemplatePanel from './articleTemplate'

import {
  getScene,
} from '@/api/base/im/scene/longText';
import {
  addTask,
} from '@/api/base/im/scene/longTextTask';

import SnowflakeId from "snowflake-id";

const roleSelectPanelRef = ref(null)
const snowflake = new SnowflakeId();
const route = useRoute();
const router = useRouter();

const sceneId = ref(route.query.sceneId)
const channelStreamId = ref(route.query.channelStreamId || snowflake.generate())
const promptText = ref('');

const greetingQuestionList = ref([
  { text: "区块链跨境支付结算系统架构技术方案" },   
  { text: "实时个性化推荐系统性能优化解决方案" },  
  { text: "医疗影像AI辅助诊断模型优化方案" },  
  { text: "工业物联网维护技术方案" },  
  { text: "交通大数据调度平台建设方案" },  
  { text: "新能源电网智能调度与储能优化方案" },  
  { text: "大模型驱动个性化在线学习系统解决方案" },  
  { text: "无人机精准农业测技术方案" }, 
]);

// 场景表单信息
const formData = ref({
  pagerType: 'pager',
  sceneId: sceneId.value,
  difficultyLevel: 'easy',
  channelStreamId: channelStreamId.value,
  attachments: [],
})

const currentSceneInfo = ref({
  sceneName: '文书生成',
});

// 添加attachmentPanelRef引用
const attachmentPanelRef = ref(null);

// 处理上传成功
const handleUploadSuccess = (fileInfo) => {
  console.log('文件上传成功:', fileInfo);
  // 这里可以保存storageId到表单数据中
  // 例如: formData.value.attachments.push(fileInfo.storageId);
  formData.value.attachments.push(fileInfo.storageId);
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

    if (!currentSceneInfo.value.chapterEditor || !currentSceneInfo.value.contentEditor) { // 选择配置角色
      roleSelectPanelRef.value.configAgent();
      return;
    }

    // if (res.data.genStatus == 1) {
    //     router.push({
    //         path: '/scene/longText/textParser',
    //         query: {
    //             sceneId: sceneId.value,
    //             genStatus: true,
    //             channelStreamId: snowflake.generate()
    //         }
    //     })
    // }

  })
}

const generaterText = () => {
  if (!formData.value.promptText) {
    ElMessage.error('请输入内容');
    return;
  } 

  addTask(formData.value).then(res => {
    const taskId = res.data;
    router.push({
      path: '/scene/longText/textParser',
      query: {
        sceneId: sceneId.value,
        genStatus: true,
        taskId: taskId,
        channelStreamId: snowflake.generate()
      }
    })
  })

}

const handleExampleClick = (item) => {
  // promptText.value = item.text;
  // generaterText();
  formData.value.promptText = item.text;
}

onMounted(() => {
  handleGetScene();
})

</script>

<style lang="scss" scoped>
.ppt-pager-container {
  background: #fff;

  .ppt-pager-aside {
    padding: 0px;
    border-right: 1px solid #f2f3f7;
    background: #fff;
    margin-bottom: 0px;
  }

  .ppt-pager-main {
    padding: 0px !important;
  }

  .long-text-container {
    background: #fff;
    height: calc(100vh - 50px);

    .main-content {
      display: flex;
      flex-direction: column;
      padding-top: calc(1vh);
      padding-left: 20px;
      padding-right: 20px;
      text-align: center;
      max-width: 960px;
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
          margin-bottom: 10px;
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
        margin-top: 10px;
        margin-bottom: 10px;
        align-items: flex-start;
        flex-direction: column;

        .input-box {
          width: 100%;
          border: 0px !important;
          height: auto;
          margin-bottom: 0px;
        }

        .upload-button-section {
          display: flex;
          width: 100%;
          justify-content: space-between;
          align-items: center;
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
            // width: calc(50% - 10px);
            box-sizing: border-box;
            padding: 10px;
            margin: 5px 5px 8px 5px;

            &:hover {
              background: rgb(232 233 235);

              .example-icon {
                // display: block;
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

  .review-footer {
    padding: 10px;
    font-size: 14px;
    background: #fafafa;
    border-radius: 8px;
    text-align: left;
    color: #555;
    margin-top: 10px;
  }

  .review-question-preview-title {
    padding: 10px;
    text-align: left;
    font-weight: bold;
    margin-left: 20px;
    margin-bottom: 10px;
    margin-top: 10px;
    border-radius: 10px;
    background: #fafafa;
    color: #444;
    font-size: 15px;
    display: flex;
    align-content: center;
    align-items: center;
    justify-content: space-between;
  }

  .review-question-preview {
    height: calc(100vh - 170px);
    margin-left: 20px;
    border-radius: 8px;
    background: #fafafa;
    border: 1px solid #e8eaf2;
    overflow: hidden;
  }

  .pager-gen-result-panel {
    margin-bottom: 20px;
    margin-left: 20px;
    text-align: left;

    .pager-container {
      background-color: #fafafa;
      // margin: 10px 10px;
      margin-right: 0px;
      border-radius: 8px;
      height: calc(100vh - 190px);
      padding: 10px;
      // padding-left: 10px;
      // margin-left: 20px;
      margin-bottom: 0px;
    }
  }
}
</style>


<style>
.long-text-container .el-textarea__inner {
  box-shadow: none !important;
}
</style>