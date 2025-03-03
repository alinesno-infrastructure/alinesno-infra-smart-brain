// 引入 WorkflowType
import { WorkflowType } from '@/utils/workflow';

// 开始节点配置
const startNode = {
    id: WorkflowType.Start,
    type: WorkflowType.Start,
    x: 180,
    y: 720,
    properties: {
        height: 200,
        stepName: '开始节点',
        config: {
            fields: [
                {
                    label: '用户消息',
                    value: 'message'
                },
                {
                    label: '文档',
                    value: 'document'
                }
            ],
            globalFields: [
                {
                    value: 'time',
                    label: '当前时间'
                },
                {
                    value: 'pre_content',
                    label: '上节点内容'
                },
                {
                    value: 'channelId',
                    label: '频道号'
                },
                {
                    value: 'history_content',
                    label: '历史对话'
                }
            ]
        }
    }
};

// 基础节点配置
const baseNode = {
    id: WorkflowType.Base,
    type: WorkflowType.Base,
    x: 200,
    y: 270,
    text: '',
    properties: {
        width: 420,
        height: 200,
        stepName: '基础节点',
        input_field_list: [],
        node_data: {
            name: '',
            desc: '',
            prologue: '默认前言'
        },
        config: {}
    }
};

// 基础节点列表
const baseNodes = [baseNode, startNode];

// AI对话节点配置
const aiChatNode = {
    type: WorkflowType.AiChat,
    text: 'AI对话节点',
    label: 'AI对话节点',
    height: 340,
    properties: {
        stepName: 'AI对话节点',
        config: {
            fields: [
                {
                    label: '回答',
                    value: 'answer'
                },
                {
                    label: '思考内容',
                    value: 'reasoning_content'
                }
            ]
        }
    }
};

// 知识库检索节点配置
const searchDatasetNode = {
    type: WorkflowType.SearchDataset,
    text: '知识库检索节点',
    label: '知识库检索节点',
    height: 355,
    properties: {
        stepName: '知识库检索节点',
        config: {
            fields: [
                {
                    label: '段落列表',
                    value: 'paragraph_list'
                },
                {
                    label: '命中处理方法列表',
                    value: 'is_hit_handling_method_list'
                },
                {
                    label: '结果',
                    value: 'data'
                },
                {
                    label: '直接返回',
                    value: 'directly_return'
                }
            ]
        }
    }
};

// 问题节点配置
const questionNode = {
    type: WorkflowType.Question,
    text: '问题节点',
    label: '问题节点',
    height: 345,
    properties: {
        stepName: '问题节点',
        config: {
            fields: [
                {
                    label: '结果',
                    value: 'answer'
                }
            ]
        }
    }
};

// 条件节点配置
const conditionNode = {
    type: WorkflowType.Condition,
    text: '条件节点',
    label: '条件节点',
    height: 175,
    properties: {
        width: 600,
        stepName: '条件节点',
        config: {
            fields: [
                {
                    label: '分支名称',
                    value: 'branch_name'
                }
            ]
        }
    }
};

// 回复节点配置
const replyNode = {
    type: WorkflowType.Reply,
    text: '回复节点',
    label: '回复节点',
    height: 210,
    properties: {
        stepName: '回复节点',
        config: {
            fields: [
                {
                    label: '内容',
                    value: 'answer'
                }
            ]
        }
    }
};

// 接口调用配置 
const httpApiNode = {
    type: WorkflowType.HttpApi ,
    text: '接口调用',
    label: 'urkk调用',
    height: 510,
    properties: {
        stepName: '调用接口',
        config: {
            fields: [
                {
                    label: '返回',
                    value: 'response'
                }
            ]
        }
    }
};

// 重排器节点配置
const rerankerNode = {
    type: WorkflowType.RrerankerNode,
    text: '重排器节点',
    label: '重排器节点',
    height: 252,
    properties: {
        stepName: '重排器节点',
        config: {
            fields: [
                {
                    label: '结果列表',
                    value: 'result_list'
                },
                {
                    label: '结果',
                    value: 'result'
                }
            ]
        }
    }
};

// 表单节点配置
const formNode = {
    type: WorkflowType.FormNode,
    text: '表单节点',
    label: '表单节点',
    height: 252,
    properties: {
        width: 600,
        stepName: '表单节点',
        node_data: {
            is_result: true,
            form_field_list: [],
            form_content_format: `表单内容格式 1
{{form}}
表单内容格式 2`
        },
        config: {
            fields: [
                {
                    label: '表单数据',
                    value: 'form_data'
                }
            ]
        }
    }
};

// 文档提取节点配置
const documentExtractNode = {
    type: WorkflowType.DocumentExtractNode,
    text: '文档提取节点',
    label: '文档提取节点',
    height: 252,
    properties: {
        stepName: '文档提取节点',
        config: {
            fields: [
                {
                    label: '内容',
                    value: 'content'
                }
            ]
        }
    }
};

// 图片理解节点配置
const imageUnderstandNode = {
    type: WorkflowType.ImageUnderstandNode,
    text: '图片理解节点',
    label: '图片理解节点',
    height: 252,
    properties: {
        stepName: '图片理解节点',
        config: {
            fields: [
                {
                    label: '回答',
                    value: 'answer'
                }
            ]
        }
    }
};

// 图片生成节点配置
const imageGenerateNode = {
    type: WorkflowType.ImageGenerateNode,
    text: '图片生成节点',
    label: '图片生成节点',
    height: 252,
    properties: {
        stepName: '图片生成节点',
        config: {
            fields: [
                {
                    label: '回答',
                    value: 'answer'
                },
                {
                    label: '图片',
                    value: 'image'
                }
            ]
        }
    }
};

// 语音转文本节点配置
const speechToTextNode = {
    type: WorkflowType.SpeechToTextNode,
    text: '语音转文本节点',
    label: '语音转文本节点',
    height: 252,
    properties: {
        stepName: '语音转文本节点',
        config: {
            fields: [
                {
                    label: '结果',
                    value: 'result'
                }
            ]
        }
    }
};

// 文本转语音节点配置
const textToSpeechNode = {
    type: WorkflowType.TextToSpeechNode,
    text: '文本转语音节点',
    label: '文本转语音节点',
    height: 252,
    properties: {
        stepName: '文本转语音节点',
        config: {
            fields: [
                {
                    label: '结果',
                    value: 'result'
                }
            ]
        }
    }
};

// 菜单节点列表
const menuNodes = [
    aiChatNode,
    imageUnderstandNode,
    imageGenerateNode,
    searchDatasetNode,
    rerankerNode,
    conditionNode,
    replyNode,
    httpApiNode,
    formNode,
    questionNode,
    documentExtractNode,
    speechToTextNode,
    textToSpeechNode
];

// 自定义函数节点配置
const functionNode = {
    type: WorkflowType.FunctionLibCustom,
    text: '自定义函数节点',
    label: '自定义函数节点',
    height: 260,
    properties: {
        stepName: '自定义函数节点',
        config: {
            fields: [
                {
                    label: '结果',
                    value: 'result'
                }
            ]
        }
    }
};

// 函数库节点配置
const functionLibNode = {
    type: WorkflowType.FunctionLib,
    text: '函数库节点',
    label: '函数库节点',
    height: 170,
    properties: {
        stepName: '函数库节点',
        config: {
            fields: [
                {
                    label: '结果',
                    value: 'result'
                }
            ]
        }
    }
};

// 应用节点配置
const applicationNode = {
    type: WorkflowType.Application,
    text: '应用节点',
    label: '应用节点',
    height: 260,
    properties: {
        stepName: '应用节点',
        config: {
            fields: [
                {
                    label: '结果',
                    value: 'result'
                }
            ]
        }
    }
};

// 比较列表
const compareList = [
    { value: 'is_null', label: '为空' },
    { value: 'is_not_null', label: '不为空' },
    { value: 'contain', label: '包含' },
    { value: 'not_contain', label: '不包含' },
    { value: 'eq', label: '等于' },
    { value: 'ge', label: '大于等于' },
    { value: 'gt', label: '大于' },
    { value: 'le', label: '小于等于' },
    { value: 'lt', label: '小于' },
    { value: 'len_eq', label: '长度等于' },
    { value: 'len_ge', label: '长度大于等于' },
    { value: 'len_gt', label: '长度大于' },
    { value: 'len_le', label: '长度小于等于' },
    { value: 'len_lt', label: '长度小于' }
];

// 节点字典
const nodeDict = {
    [WorkflowType.AiChat]: aiChatNode,
    [WorkflowType.SearchDataset]: searchDatasetNode,
    [WorkflowType.Question]: questionNode,
    [WorkflowType.Condition]: conditionNode,
    [WorkflowType.Base]: baseNode,
    [WorkflowType.Start]: startNode,
    [WorkflowType.Reply]: replyNode,
    [WorkflowType.HttpApi]: httpApiNode,
    [WorkflowType.FunctionLib]: functionLibNode,
    [WorkflowType.FunctionLibCustom]: functionNode,
    [WorkflowType.RrerankerNode]: rerankerNode,
    [WorkflowType.FormNode]: formNode,
    [WorkflowType.Application]: applicationNode,
    [WorkflowType.DocumentExtractNode]: documentExtractNode,
    [WorkflowType.ImageUnderstandNode]: imageUnderstandNode,
    [WorkflowType.TextToSpeechNode]: textToSpeechNode,
    [WorkflowType.SpeechToTextNode]: speechToTextNode,
    [WorkflowType.ImageGenerateNode]: imageGenerateNode
};

// 判断是否为工作流类型
function isWorkFlow(type) {
    return type === 'WORK_FLOW';
}

// 判断是否为最后一个节点
function isLastNode(nodeModel) {
    const incoming = nodeModel.graphModel.getNodeIncomingNode(nodeModel.id);
    const outcomming = nodeModel.graphModel.getNodeOutgoingNode(nodeModel.id);
    if (incoming.length > 0 && outcomming.length === 0) {
        return true;
    } else {
        return false;
    }
}

// 导出所有配置和函数
export {
    startNode,
    baseNode,
    baseNodes,
    aiChatNode,
    searchDatasetNode,
    questionNode,
    conditionNode,
    replyNode,
    httpApiNode,
    rerankerNode,
    formNode,
    documentExtractNode,
    imageUnderstandNode,
    imageGenerateNode,
    speechToTextNode,
    textToSpeechNode,
    menuNodes,
    functionNode,
    functionLibNode,
    applicationNode,
    compareList,
    nodeDict,
    isWorkFlow,
    isLastNode
};