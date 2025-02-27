<template>
    <div class="node-components border-r-4" v-show="show">

        <div class="node-components-title"><i class="fa-solid fa-masks-theater"></i> 任务节点</div>
        <el-scrollbar height="400px">
            <div class="node-components-content">
                <el-input v-model="searchNodeComponent" :prefix-icon="Search" clearable style="width: 100%"
                    placeholder="搜索" />
            </div>
            <div class="node-components-content" v-for="item in workflowNodes" :key="item.name"
                @click="clickNode(item)">
                <div class="node-components-icon">
                    <i :class="item.properties.icon" />
                </div>
                <div class="node-info">
                    <span>{{ item.label }}</span>
                    <desc class="description">{{ item.description }}</desc>
                </div>
            </div>
        </el-scrollbar>
    </div>
</template>

<script setup>
import { ref, defineProps } from 'vue';

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

const searchNodeComponent = ref('')

const workflowNodes = [
    {
        id: '1001',
        name: "AI_CHAT",
        label: "AI 对话",
        description: "与 AI 大模型进行对话",
        type: "ai_chat",
        properties: {
            icon: 'fa-solid fa-comments',
            color: "#2962FF",
            stepName: 'AI 对话',
            showNode: true,
            x: 440,
            y: 340,
            height: 682,
            width: 280
        }
    },
    {
        id: '1002',
        name: "IMAGE_UNDERSTAND",
        label: "图片理解",
        description: "识别出图片中的对象、场景等信息回答用户问题",
        type: "image_understand",
        properties: {
            icon: 'fa-solid fa-image',
            color: "#388E3C",
            stepName: '图片理解',
            showNode: true,
            x: 440,
            y: 340,
            height: 630,
            width: 280
        }
    },
    {
        id: '1003',
        name: "IMAGE_GENERATE",
        label: "图片生成",
        description: "根据提供的文本内容生成图片",
        type: "image_generate",
        properties: {
            icon: 'fa-solid fa-palette',
            color: "#F57C00",
            stepName: '图片理解',
            showNode: true,
            x: 440,
            y: 340,
            height: 570,
            width: 280
        }
    },
    {
        id: '1004',
        name: "KNOWLEDGE_SEARCH",
        label: "知识库检索",
        description: "关联知识库，查找与问题相关的分段",
        type: "knowledge_search",
        properties: {
            icon: 'fa-solid fa-book',
            color: "#D32F2F",
            stepName: '图片理解',
            showNode: true,
            x: 440,
            y: 340,
            height: 740,
            width: 280
        }
    },
    {
        id: '1005',
        name: "RERANKER",
        label: "多路召回",
        description: "使用重排模型对多个知识库的检索结果进行二次召回",
        type: "reranker",
        properties: {
            icon: 'fa-solid fa-magnifying-glass-chart',
            color: "#616161",
            stepName: '多路召回',
            showNode: true,
            x: 440,
            y: 340,
            height: 570,
            width: 280
        }
    },
    {
        id: '1006',
        name: "CONDITION",
        label: "判断器",
        description: "根据不同条件执行不同的节点",
        type: "condition",
        properties: {
            icon: 'fa-solid fa-question',
            color: "#303F9F",
            stepName: '判断器',
            showNode: true,
            x: 440,
            y: 340,
            height: 330,
            width: 540,
            branch_condition_list: [
                {
                    "index": 0,
                    "height": 129,
                    "id": "1009"
                },
                {
                    "index": 2,
                    "height": 44 ,
                    "id": "161"
                }
            ]
        }
    },
    {
        id: '1007',
        name: "REPLAY",
        label: "指定回复",
        description: "指定回复内容，引用变量会转换为字符串进行输出",
        type: "reply",
        properties: {
            icon: 'fa-solid fa-reply',
            color: "#FF9800",
            stepName: '指定回复',
            showNode: true,
            x: 440,
            y: 340,
            height: 400,
            width: 280
        }
    },
    {
        id: '1009',
        name: "QUESTION",
        label: "问题优化",
        description: "根据历史聊天记录优化完善当前问题，更利于匹配知识库分段",
        type: "question",
        properties: {
            icon: 'fa-solid fa-pencil',
            color: "#F4511E",
            stepName: '问题优化',
            showNode: true,
            x: 440,
            y: 340,
            height: 570,
            width: 280
        }
    },
    {
        id: '1010',
        name: "DOCUMENT_EXTRACT",
        label: "文档内容提取",
        description: "提取文档中的内容",
        type: "document_extract",
        properties: {
            icon: 'fa-solid fa-file-lines',
            color: "#8E24AA",
            stepName: '文档内容提取',
            showNode: true,
            x: 440,
            y: 340,
            height: 320,
            width: 280
        }
    },
    {
        id: '1011',
        name: "SPEECH_TO_TEXT",
        label: "语音转文本",
        description: "将音频通过语音识别模型转换为文本",
        type: "speech_to_text",
        properties: {
            icon: 'fa-solid fa-masks-theater',
            color: "#00ACC1",
            stepName: '语音转文本',
            showNode: true,
            x: 440,
            y: 340,
            height: 400,
            width: 280
        }
    },
    {
        id: '1012',
        name: "TEXT_TO_SPEECH",
        label: "文本转语音",
        description: "将文本通过语音合成模型转换为音频",
        type: "text_to_speech",
        properties: {
            icon: 'fa-solid fa-volume-high',
            color: "#FFB300",
            stepName: '文本转语音',
            showNode: true,
            x: 440,
            y: 340,
            height: 390,
            width: 280
        }
    },
    {
        id: '1013',
        name: "function",
        label: "脚本功能",
        description: "使用Groovy脚本进行编辑开发",
        type: "function",
        properties: {
            icon: 'fas fa-file-signature',
            color: "#424242",
            stepName: '脚本功能',
            showNode: true,
            x: 440,
            y: 340,
            height: 510,
            width: 280
        }
    }
];

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
    // border: 1px solid #e5e5e5;
    padding-bottom: 20px;
    border-radius: 5px;
    box-shadow: var(--el-box-shadow-light);

    .node-components-title {
        font-size: 14px;
        padding: 15px;
        background: #f5f7fa;
        margin-bottom: 5px;
    }

    .node-components-icon {
        width: 35px;
        height: 35px;
        background: #3b5998;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        color: rgb(255, 255, 255);
        border-radius: 5px;
    }

    .node-components-content {
        display: flex;
        line-height: 17px;
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