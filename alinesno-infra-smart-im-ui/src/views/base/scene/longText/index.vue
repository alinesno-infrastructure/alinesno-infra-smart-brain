<template>
    <div class="long-text-container">

                <RoleSelectPanel 
                    :currentSeneInfo="currentSceneInfo"
                    @openExecuteHandle="openExecuteHandle"
                    ref="roleSelectPanelRef" />

        <div class="main-content">
            <div class="title-section">
                <span class="title">文档生成，一键生成企业文档</span>
                <span class="description">借助AI智能体，实现企业文档一键生成。输入信息后，瞬间生成结构清晰、内容详实的优质文档</span>
            </div>
            <div class="input-button-section">
                <el-input v-model="promptText" class="input-box" size="large" placeholder="请输入您的指令生成对应的文案内容"
                    :prefix-icon="Search" />
                <el-button type="primary" size="large" @click="generaterText()" class="send-button">
                    <i class="fa-solid fa-paper-plane" style="font-size:22px"></i>
                </el-button>
            </div>
            <div class="example-section">
                <div class="example-title">你可以这样说</div>
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
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import RoleSelectPanel from '@/views/base/scene/common/roleSelectPanel'

import { getScene , updateChapterPromptContent } from '@/api/base/im/scene/longText';
import SnowflakeId from "snowflake-id";

const roleSelectPanelRef = ref(null)
const snowflake = new SnowflakeId();
const route = useRoute();
const router = useRouter();

const sceneId = ref(route.query.sceneId)
const promptText = ref('');

const greetingQuestionList = ref([
  { text: "编写一篇校园爱情小说" },
  { text: "撰写一份项目管理文档" },
  { text: "起草一份劳动仲裁申请书" },
  { text: "制定一份技术标书文案" }
]);

const currentSceneInfo = ref({
    sceneName: '文书生成',
});

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

        if(!currentSceneInfo.value.chapterEditor || !currentSceneInfo.value.contentEditor){ // 选择配置角色
            roleSelectPanelRef.value.configAgent();
            return ;
        }

        if(res.data.genStatus == 1){
            router.push({
                path: '/scene/longText/textParser',
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
    ElMessage.success('生成成功');

    updateChapterPromptContent({
        sceneId: sceneId.value,
        promptContent: promptText.value
    }).then(res => {
        router.push({
            path: '/scene/longText/textParser',
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
    handleGetScene();
})

</script>

<style lang="scss" scoped>
.long-text-container {
    background: #fff;
    height: calc(100vh - 50px);

   .main-content {
        display: flex;
        flex-direction: column;
        padding-top: calc(20vh - 56px);
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

           .input-box {
                width: 100%;
                height: 50px;
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
.long-text-container .el-input__wrapper {
    box-shadow: none !important;
}
</style>