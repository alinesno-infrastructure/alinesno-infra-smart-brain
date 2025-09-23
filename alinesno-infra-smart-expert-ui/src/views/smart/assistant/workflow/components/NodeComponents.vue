<template>
    <div class="node-components border-r-4" v-show="show">

        <div class="node-components-title">
            <span>
                <i class="fa-solid fa-masks-theater"></i> 任务节点
            </span>
            <!-- 
            <span style="cursor: pointer;">
                <i class="fa-solid fa-xmark"></i>
            </span> 
            -->
        </div>
        <el-scrollbar height="600px" style="padding:10px;">
            <el-row>
                <el-col :span="24">
                    <div class="node-components-content">
                        <el-input v-model="searchNodeComponent" :prefix-icon="Search" clearable style="width: 100%"
                            placeholder="搜索" />
                    </div>
                </el-col>

                <el-col :span="12" v-for="item in filteredWorkflowNodes" :key="item.name" >
                    <div class="node-components-content" @click="clickNode(item)">
                        <div class="node-components-icon">
                            <i :class="item.properties.icon" />
                        </div>
                        <div class="node-info">
                            <span>{{ item.label }}</span>
                            <desc class="description">{{ item.description }}</desc>
                        </div>
                    </div>
                </el-col>

                 <el-col :span="24" v-if="filteredWorkflowNodes.length === 0">
                    <div class="node-components-content no-results">
                        <el-empty description="没有搜索结果，请重新再搜索." image-size="100" />
                    </div>
                </el-col>

            </el-row>
        </el-scrollbar>
    </div>
</template>

<script setup>
import { ref } from 'vue';

const emit = defineEmits(['clickNode']);
const props = defineProps({
    show: {
        type: Boolean,
        default: false
    },
    id: {
        type: String,
        default: ''
    },
    // workflowRef: Object
});

const searchNodeComponent = ref('')

const workflowNodes = [
    {
        id: '1001',
        name: "AI_CHAT",
        label: "AI对话",
        description: "与 AI 大模型进行对话",
        type: "ai_chat",
        properties: {
            icon: 'fa-solid fa-comments',
            color: "#2962FF",
            stepName: 'AI对话',
            showNode: true,
            height: 622,
            width: 330
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
            height: 584,
            width: 330
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
            stepName: '图片生成',
            showNode: true,
            height: 584,
            width: 330
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
            stepName: '知识库检索',
            showNode: true,
            height: 740,
            width: 330
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
            height: 570,
            width: 330
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
            height: 352,
            width: 540,
            branch_condition_list: [
                {
                    "index": 0,
                    "height": 129,
                    "id": "1009"
                },
                {
                    "index": 2,
                    "height": 44,
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
            height: 400,
            width: 330
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
            height: 570,
            width: 330
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
            height: 320,
            width: 330
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
            height: 400,
            width: 330
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
            height: 390,
            width: 330
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
            height: 540,
            width: 330
        }
    },
    {
        id: '1014',
        name: "HTTP_API",
        label: "Http调用",
        description: "开发来实现Http接口的调用。",
        type: "http_api",
        properties: {
            icon: 'fa-solid fa-feather',
            color: "#424242",
            stepName: 'Http调用',
            showNode: true,
            height: 540,
            width: 330
        }
    },
    {
        // 结束节点
        id: '1015',
        name: "END",
        label: "结束节点",
        description: "结束流程",
        type: "end",
        properties: {
            icon: 'fa-solid fa-flag-checkered',
            color: "#424242",
            stepName: '结束节点',
            showNode: true,
            height: 330,
            width: 330
        }
    }
];

// 将搜索拆分为关键字数组（去除空项），匹配任一字段包含所有关键字才保留
const filteredWorkflowNodes = computed(() => {
    const q = (searchNodeComponent.value || '').trim().toLowerCase();
    if (!q) {
        return workflowNodes;
    }
    const terms = q.split(/\s+/).filter(Boolean);
    return workflowNodes.filter(node => {
        const hay = `${node.label} ${node.name} ${node.description} ${node.type}`.toLowerCase();
        // 所有 terms 必须都出现在 hay 中
        return terms.every(t => hay.indexOf(t) !== -1);
    });
});

// 辅助：安全地构造正则（转义特殊字符）
function escapeRegExp(string = '') {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

// 高亮 label（返回 HTML）
const highlightLabel = (item) => {
    const q = (searchNodeComponent.value || '').trim();
    if (!q) return escapeHtml(item.label);
    return highlightText(item.label, q);
}

// 高亮 description（返回 HTML）
const highlightDesc = (item) => {
    const q = (searchNodeComponent.value || '').trim();
    if (!q) return escapeHtml(item.description);
    return highlightText(item.description, q);
}

// 将 text 中匹配的关键字用 <mark> 包裹（支持多关键字，忽略大小写）
function highlightText(text = '', query = '') {
    const terms = query.split(/\s+/).filter(Boolean);
    if (terms.length === 0) return escapeHtml(text);

    // 依次替换每个关键字（注意逐一替换以避免影响后续关键字）
    let result = escapeHtml(text);
    terms.forEach(term => {
        const re = new RegExp(escapeRegExp(term), 'ig');
        result = result.replace(re, match => `<mark class="node-highlight">${match}</mark>`);
    });
    return result;
}

// 简单 HTML 转义，防止注入
function escapeHtml(str = '') {
    return str
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

const clickNode = (item) => {
    // const { id, workflowRef } = props;
    // 点击之后，show 设置为 false
    // props.show = false;
    console.log('item = ' + item);
    emit('clickNode', item);

    // props.workflowRef?.clickNode(item);

    // return item ;
};

defineExpose({ clickNode });

</script>

<style lang="scss" scoped>
.node-components {
    position: fixed;
    right: 120px;
    z-index: 100000;
    width: 600px;
    background: #ffffff;
    padding-bottom: 20px;
    border-radius: 5px;
    box-shadow: var(--el-box-shadow-light);
    bottom: 90px;
    left: 250px;

    .node-components-title {
        font-size: 14px;
        padding: 15px;
        background: #f5f7fa;
        margin-bottom: 5px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .node-components-icon {
        width: 35px;
        height: 35px;
        background: #1d75b0;
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
        margin-top:5px;
        margin-bottom: 5px;
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
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .no-results {
        justify-content: center;
    }
}
</style>