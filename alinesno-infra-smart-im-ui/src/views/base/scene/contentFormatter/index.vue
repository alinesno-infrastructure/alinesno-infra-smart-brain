<template>
  <div class="content-formatter-container">

              <RoleSelectPanel 
                  :currentSeneInfo="currentSceneInfo"
                  @openExecuteHandle="openExecuteHandle"
                  ref="roleSelectPanelRef" />

      <div class="main-content">
          <div class="title-section">
              <span class="title">内容排版，一键按模板格式</span>
              <span class="description">运用先进算法，高效进行内容排版。输入文本后，即刻为您调整段落结构、字体格式等，根据模板进行排版，打造专业美观的内容呈现</span>
          </div>
          <div class="input-button-section">
              <el-input type="textarea" 
                  :rows="14" 
                  v-model="promptText" 
                  class="input-box" 
                  size="large" 
                  resize="none"
                  placeholder="请输入您的数据及分析需求，生成对应的分析报告或进行数据处理"
                  :prefix-icon="Search" />
              <el-button type="primary" size="large" @click="generaterText()" class="send-button">
                内容重新排版 &nbsp; <i class="fa-solid fa-paper-plane" style="font-size:15px"></i> 
              </el-button>
          </div>
      </div>
      <div class="review-footer-message">
          <div class="footer-message">服务生成的所有内容均由人工智能模型生成，其生成内容的准确性和完整性无法保证，不能代表我们的态度和观点。</div>
      </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'

import { getScene , updateChapterPromptContent } from '@/api/base/im/scene/contentFormatter';
import SnowflakeId from "snowflake-id";

const roleSelectPanelRef = ref(null)
const snowflake = new SnowflakeId();
const route = useRoute();
const router = useRouter();

const sceneId = ref(route.query.sceneId)
const promptText = ref('');

const list = [
  { text: "分析某电商平台近三个月的销售数据并生成趋势报告" },
  { text: "对公司员工的绩效数据进行清洗和统计分析" },
  { text: "根据市场调研数据，撰写一份竞品分析报告" },
  { text: "对某APP的用户行为数据进行聚类分析并给出优化建议" }
];

const currentSceneInfo = ref({
    sceneName: '文书生成',
});

const handleGetScene = () => {
    getScene(sceneId.value).then(res => {
        currentSceneInfo.value = res.data;
        // handleRoleBySceneIdAndAgentType();

        if(!currentSceneInfo.value.templateExtractorEngineer){ // 选择配置角色
            roleSelectPanelRef.value.configAgent();
            return ;
        }

        if(res.data.genStatus == 1){
            jumpDataParser(); 
        }
    })
}

const generaterText = () => {
    if (!promptText.value) {
        ElMessage.error('请输入内容');
        return;
    }
    ElMessage.success('生成成功');

    updateChapterPromptContent({
        sceneId: sceneId.value,
        promptContent: promptText.value
    }).then(res => {
        jumpDataParser(); 
    })

}

// 跳转路径
const jumpDataParser = () => {
    router.push({
        path: '/scene/contentFormatter/contentParser',
        query: {
            sceneId: sceneId.value,
            genStatus: false ,
            channelStreamId: snowflake.generate() 
        }
    })
}

const handleExampleClick = (item) => {
    promptText.value = item.text;
    // generaterText();
}

onMounted(() => {
    handleGetScene();
})

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
        padding-top: calc(10vh - 56px);
        text-align: center;
        max-width: 1024px;
        margin: auto;

       .title-section {
            display: flex;
            flex-direction: column;
            text-align: center;

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
            flex-direction: column;

           .input-box {
                width: 100%;
                height: auto;
                border: 0px !important;
                margin-bottom: 0px;
            }
        }

       .example-section {
            padding: 0px 20px;

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
.content-formatter-container .el-textarea__inner{
    box-shadow: none !important;
}
</style>