<template>
    <div class="node-components border-r-4" v-show="show">

        <div class="node-components-title"><i class="fa-solid fa-masks-theater" ></i> 任务节点</div>

        <el-scrollbar height="400px">
            <div class="node-components-content" v-for="item in workflowNodes" :key="item.name" @click="clickNode(item)">
                <el-avatar :size="30" :src="circleUrl" class="avatar" />
                <div class="node-info">
                    <span>{{ item.label }}</span>
                    <desc class="description">{{ item.description }}</desc>
                </div>
            </div>
        </el-scrollbar>
    </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue';

const emit = defineEmits(['update:show']);
const props = defineProps({
    show: {
        type: Boolean,
        default: false
    },
    id: {
        type: String,
        default: ''
    },
    workflowRef: Object
});

const circleUrl = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png');

const workflowNodes = ref([
    { id: '1001', name: "AI_CONVERSATION", label: "AI 对话", description: "与 AI 大模型进行对话", type: "ai_conversation", properties: {} },
    { id: '1002', name: "IMAGE_UNDERSTANDING", label: "图片理解", description: "识别出图片中的对象、场景等信息回答用户问题", type: "image_understanding", properties: {} },
    { id: '1003', name: "IMAGE_GENERATION", label: "图片生成", description: "根据提供的文本内容生成图片", type: "image_generation", properties: {} },
    { id: '1004', name: "KNOWLEDGE_BASE_RETRIEVAL", label: "知识库检索", description: "关联知识库，查找与问题相关的分段", type: "knowledge_base_retrieval", properties: {} },
    { id: '1005', name: "MULTIWAY_RECALL", label: "多路召回", description: "使用重排模型对多个知识库的检索结果进行二次召回", type: "multiway_recall", properties: {} },
    { id: '1006', name: "JUDGER", label: "判断器", description: "根据不同条件执行不同的节点", type: "judger", properties: {} },
    { id: '1007', name: "SPECIFIED_REPLY", label: "指定回复", description: "指定回复内容，引用变量会转换为字符串进行输出", type: "specified_reply", properties: {} },
    { id: '1008', name: "FORM_COLLECTION", label: "表单收集", description: "在问答过程中用于收集用户信息，可以根据收集到表单数据执行后续流程", type: "form_collection", properties: {} },
    { id: '1009', name: "QUESTION_OPTIMIZATION", label: "问题优化", description: "根据历史聊天记录优化完善当前问题，更利于匹配知识库分段", type: "question_optimization", properties: {} },
    { id: '1010', name: "DOCUMENT_CONTENT_EXTRACTION", label: "文档内容提取", description: "提取文档中的内容", type: "document_content_extraction", properties: {} },
    { id: '1011', name: "SPEECH_TO_TEXT", label: "语音转文本", description: "将音频通过语音识别模型转换为文本", type: "speech_to_text", properties: {} },
    {
        id: '1012',
        name: "TEXT_TO_SPEECH",
        label: "文本转语音",
        description: "将文本通过语音合成模型转换为音频",
        type: "text_to_speech",
        properties: {}
    }
]);

const clickNode = (item) => {
    // const { id, workflowRef } = props;
    // 点击之后，show 设置为 false
    props.show = false;
    emit('update:show', false);

    props.workflowRef?.clickNode(item);
};
</script>

<style lang="scss" scoped>
.node-components {
    position: absolute;
    top: 49px;
    right: 120px;
    z-index: 100000;
    width: 268px;
    background: #ffffff;
    border: 1px solid #e5e5e5;
    border-radius: 2px;
    box-shadow: var(--el-box-shadow-light);

    .node-components-title {
        font-size: 14px;
        padding: 15px;
        background: #f5f7fa;
        margin-bottom: 5px;
    }

    .node-components-content {
        display: flex;
        line-height:17px;
        padding: 8px 10px;
        gap: 10px;
        cursor: pointer;
        align-items: center;
        justify-content: flex-start;

        &:hover {
            background: #f5f5f5;
        }
    }

    .avatar {
        width: 30px;
    }

    .node-info {
        display: flex;
        width: calc(100% - 50px);
        flex-direction: column;
        gap: 5px;
        font-size: 13px;
    }

    .description {
        color: #8f959e;
    }
}
</style>