<template>
  <div class="content-formatter-container exam-pagercontainer">


    <el-container style="height:calc(100vh - 40px );background-color: #fff;">

      <el-aside width="280px" class="exam-pager-aside">
        <FunctionList
          :title="props.title" />
      </el-aside>

      <el-main class="exam-pager-main">

        <!-- 自定义内容 -->
        <slot>

        </slot>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import FunctionList from './functionPanel'

// import { getScene, updateChapterPromptContent } from '@/api/base/im/scene/contentFormatter';
import SnowflakeId from "snowflake-id";
import { processParagraph } from '@/api/base/im/scene/longText';

// import {
//   getTemplates,
// } from '@/api/base/im/scene/contentFormatter';

const ruleFormRef = ref(null)
const roleSelectPanelRef = ref(null)
const snowflake = new SnowflakeId();
const route = useRoute();
const router = useRouter();
const templateList = ref([]);

const props = defineProps({
  title: {
    type: String,
    default: ''
  }
})

// 定义表单数据
const ruleForm = reactive({
  promptText: '',
  documentType: [],
  businessScenario: [],
  templateId: ''
});

const sceneId = ref(route.query.sceneId)
// const promptText = ref('');

// 定义表单验证规则
// const rules = reactive({
//   promptText: [
//     { required: true, message: '请输入需要整理的文稿类型', trigger: 'blur' }
//   ],
//   documentType: [
//     { required: true, message: '请选择文稿类型', trigger: 'change' }
//   ],
//   businessScenario: [
//     { required: true, message: '请选择业务场景', trigger: 'change' }
//   ],
//   templateId: [
//     { required: true, message: '请选择排版模板', trigger: 'change' }
//   ]
// });

// const topics = [
//   { text: "大学生职业生涯规划" },
//   { text: "房地产行业调控后市场走向" },
//   { text: "生态保护与企业发展兼容方案" },
//   { text: "健康生活方式的趋势" },
//   { text: "虚拟货币市场的发展历程与未来展望" },
//   { text: "新能源汽车市场现状与发展预测" },
//   { text: "人工智能的发展趋势与未来展望" },
//   { text: "公益事业与企业社会责任的结合" },
//   { text: "2025在线教育的发展与挑战" },
//   { text: "企业数字化执行阻力突破" },
//   { text: "年度工作回顾与未来规划" }
// ];

// const businessOptions = ref([
//   { value: 'government', label: '政务' },
//   { value: 'commerce', label: '商务' },
//   { value: 'ecommerce', label: '电商' },
//   { value: 'campus', label: '校园' },
//   { value: 'medical', label: '医疗' },
//   { value: 'education', label: '教育' },
//   { value: 'finance', label: '金融' },
//   { value: 'manufacturing', label: '制造' },
//   { value: 'media', label: '媒体' },
//   { value: 'entertainment', label: '娱乐' },
//   { value: 'transportation', label: '交通' },
//   { value: 'agriculture', label: '农业' },
//   { value: 'construction', label: '建筑' },
//   { value: 'energy', label: '能源' },
//   { value: 'tourism', label: '旅游' },
//   { value: 'service', label: '服务' }
// ]);

// // 文书类型
// const documentOptions = ref([
//   { value: 'notice', label: '通知' },
//   { value: 'government_letter', label: '政务函' },
//   { value: 'red_head_document', label: '红头文件' },
//   { value: 'report', label: '报告' },
//   { value: 'memo', label: '备忘录' },
//   { value: 'contract', label: '合同' },
//   { value: 'agreement', label: '协议' },
//   { value: 'proposal', label: '提案' },
//   { value: 'announcement', label: '公告' },
//   { value: 'circular', label: '通报' },
//   { value: 'invitation', label: '邀请函' },
//   { value: 'declaration', label: '声明' },
//   { value: 'application', label: '申请书' },
//   { value: 'resume', label: '简历' }
// ]);

// const list = [
//     { text: "分析某电商平台近三个月的销售数据并生成趋势报告" },
//     { text: "对公司员工的绩效数据进行清洗和统计分析" },
//     { text: "根据市场调研数据，撰写一份竞品分析报告" },
//     { text: "对某APP的用户行为数据进行聚类分析并给出优化建议" }
// ];

const channelStreamId = ref(route.query.channelStreamId || snowflake.generate())

// 添加attachmentPanelRef引用
// const attachmentPanelRef = ref(null);

// // 处理上传成功
// const handleUploadSuccess = (fileInfo) => {
//   console.log('文件上传成功:', fileInfo);
//   // 这里可以保存storageId到表单数据中
//   // 例如: formData.value.attachments.push(fileInfo.storageId);
//   formData.value.attachments.push(fileInfo.storageId);
// };

// // 修改handleUpload方法，直接调用子组件的上传
// const handleUpload = () => {
//   if (attachmentPanelRef.value) {
//     attachmentPanelRef.value.$el.querySelector('.el-upload__input').click();
//   }
// };

const currentSceneInfo = ref({
  sceneName: '文书生成',
});

const handleGetTemplates = () => {
  getTemplates().then(res => {
    // contractTypeList.value = res.data;
    templateList.value = res.data;
  })
}

const handleGetScene = () => {
  getScene(sceneId.value).then(res => {
    currentSceneInfo.value = res.data;
    // handleRoleBySceneIdAndAgentType();

    if (!currentSceneInfo.value.templateExtractorEngineer || !currentSceneInfo.value.contentReviewerEngineer) { // 选择配置角色
      roleSelectPanelRef.value.configAgent();
      return;
    }

    if (res.data.genStatus == 1) {
      jumpDataParser();
    }

  })
}

const generaterText = () => {

  const formEl = ruleFormRef.value;
  if (!formEl) {
    return;
  }


  formEl.validate((valid) => {
    if (valid) {
      if (!ruleForm.promptText) {
        ElMessage.error('请输入内容');
        return;
      }
      ElMessage.success('生成成功');

      updateChapterPromptContent({
        sceneId: sceneId.value,
        promptContent: ruleForm.promptText
      }).then(res => {
        jumpDataParser();
      })
    }
  });


}

// 跳转路径
const jumpDataParser = () => {
  router.push({
    path: '/scene/contentFormatter/contentParser',
    query: {
      sceneId: sceneId.value,
      genStatus: false,
      channelStreamId: channelStreamId.value
    }
  })
}

const handleExampleClick = (item) => {
  form.promptText.value = item.text;
  // generaterText();
}

// 存储表单数据到本地缓存
const saveFormDataToLocalStorage = () => {
  localStorage.setItem('ruleForm', JSON.stringify({
    documentType: ruleForm.documentType,
    businessScenario: ruleForm.businessScenario,
    templateId: ruleForm.templateId
  }));
};

watch([() => ruleForm.documentType, () => ruleForm.businessScenario, () => ruleForm.templateId], () => {
  saveFormDataToLocalStorage();
});

onMounted(() => {
  handleGetScene();
  handleGetTemplates();

})

// route改变了
// watch(() => route.query.channelStreamId, (newValue, oldValue) => {
//     console.log('channelStreamId changed from ' + oldValue + ' to ' + newValue);
//     channelStreamId.value = newValue;

//     if (!route.query.channelStreamId) {
//         console.log('channelStreamId = ' + channelStreamId.value);
//         router.replace({
//             query: {
//                 ...route.query,
//                 channelStreamId: channelStreamId.value
//             }
//         });
//     }
// });

</script>

<style lang="scss" scoped>
.content-formatter-container {
  background: #fff;
  height: calc(100vh - 50px);

  .send-button {
    width: 100%;
  }

  .main-content {
    display: flex;
    flex-direction: column;
    padding-top: calc(3vh - 56px);
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
      // gap: 10px;
      position: relative;
      box-sizing: border-box;
      width: 100%;
      border-radius: 8px;
      // box-shadow: rgba(54, 54, 73, 0.06) 0px 12px 24px -16px, rgba(74, 80, 96, 0.12) 0px 12px 40px, rgba(44, 44, 54, 0.02) 0px 0px 1px;
      transition: 0.3s;
      background: rgb(255, 255, 255);
      padding: 10px !important;
      border: 1px solid rgb(232, 234, 242);
      margin-top: 30px;
      margin-bottom: 20px;
      align-items: flex-start;
      flex-direction: column;

      .input-box {
        width: 100%;
        height: auto;
        border: 0px !important;
        margin-bottom: 0px;
      }
    }

    // .example-section {
    //     padding: 0px 20px;

    //     .example-title {
    //         color: rgb(44, 44, 54);
    //         font-size: 14px;
    //         text-align: left;
    //         margin-left: 5px;
    //         margin-top: 15px;
    //         margin-bottom: 15px;
    //     }

    //     .example-list {
    //         display: flex;
    //         flex-wrap: wrap;

    //         .example-item {
    //             position: relative;
    //             display: flex;
    //             gap: 8px;
    //             align-items: center;
    //             height: 40px;
    //             background: rgb(242, 243, 247);
    //             border-radius: 8px;
    //             cursor: pointer;
    //             transition: 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    //             will-change: opacity, transform;
    //             width: calc(50% - 10px);
    //             box-sizing: border-box;
    //             padding: 10px;
    //             margin: 5px 5px 8px 5px;

    //             &:hover {
    //                 background: rgb(232 233 235);

    //                 .example-icon {
    //                     display: block;
    //                 }
    //             }

    //             .example-icon {
    //                 display: none;
    //                 color: #585a73;
    //                 font-size: 12px;
    //             }

    //             .example-text {
    //                 flex: 1 1;
    //                 overflow: hidden;
    //                 color: #585a73;
    //                 font-size: 14px;
    //                 white-space: nowrap;
    //                 text-align: left;
    //                 text-overflow: ellipsis;
    //                 transition: padding-right .2s ease-out;
    //             }
    //         }
    //     }
    // }
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
.content-formatter-container .el-textarea__inner {
  box-shadow: none !important;
}
</style>