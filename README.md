## 概述

运营人员提供一个强大的助手工具，帮助他们监控系统运维状态，并提供状态通报、分析和通知等功能。
Brain服务提供了一组接口用于生成和处理自然语言内容，基于OpenAI GPT-3.5模型，以解决业务场景中的自然语言处理需求。这些接口包括流式内容响应、实时内容响应和离线内容生成接口。

## 服务交互

服务提供异步与 GPT 进行交互的方式。Brain 生成结果，并根据要求解析后保存到数据库。其中，同步返回结果为流式输出，而异步方式则由 Client 获取并保存结果。

<img src="images/brain-service-design.png"  alt="服务交互设计规划"/>

说明:

- [重点]服务之前交互以自定义的yaml格式来进行交互，以达到更高的准确率，数据安全问题，节约Token成本，响应时间等问题
- 每个专家可以执行多个Prompt，assistant使用多任务多线程方式调用返回结果(是否引用工作流？待考虑)
- 执行服务每个服务在`adapter`里面进一步添加模板解析的能力来调用本服务，来进行多方的适配管理(DDD的概念)
- 处理完成之后，数据还有其它统一进入到数据仓库里面，为后期的专家服务提供知识来源，同时也是向量仓数据来源之一(注意是之一)

## 智能助手的菜单管理：
- 角色管理 --> 角色规则 + 知识库（文档/表格/数据库/对话记录/数据标记) + 向量化(数据集) + 技能管理(召回设置/技能组合)
- 渠道管理 --> IM系统（钉钉/企业微信/飞书) + 接入角色配置
- 数据管理 --> 数据源 + 文档 + 知识库 + API接口 + 定时任务（同步或者异步）
- 密钥管理 --> 提供出Token和第三方交互密钥和方式
- 调用日志 --> 接口调用日志

## 推理接口使用

以下是使用Java进行接口调用的示例：

1. 添加maven依赖：

```xml
<dependency>
    <groupId>com.alinesno.infra.smart</groupId>
    <artifactId>alinesno-infra-smart-brain-client</artifactId>
    <version>${revision}</version>
</dependency>
```

配置网关接口:

```yaml
alinesno:
    gateway:
        smart.brain.gateway: http://xxxxx
```

2. 调用接口：

```java
// 获取流式接口
GptChatResponse gptChatProcess;

// 获取实时接口
GptChatResponse gptChatCompletions;

// 生成任务接口
GptChatResponse gptChatTask;

// 查询任务接口
GptChatContent gptChatContent;
```

### 获取流式内容响应接口(`/api/gpt/chat-process`)

场景：用于前端或业务内容，支持交互式对话

请求内容：

```json
{
    "prompt": "测试发送",
    "options": {
        "conversationId": "",
        "parentMessageId": "as-k0ixmi02q0"
    },
    "systemMessage": "You are ChatGPT, a large language model trained by OpenAI. Follow the user's instructions carefully. Respond using markdown.",
    "temperature": 0.8,
    "top_p": 1,
    "tokens": 1000
}
```

返回内容：

```json
{
    "role": "assistant",
    "parentMessageId": "as-k0ixmi02q0",
    "id": "chatcmpl-8RVK6Zn1xAsFDzDTgqvipVZeIRkzH",
    "text": "",
    "detail": {
        "created": 1701565954,
        "model": "gpt-3.5-turbo-0613",
        "id": "chatcmpl-8RVK6Zn1xAsFDzDTgqvipVZeIRkzH",
        "choices": [
            {
                "delta": {
                    "role": "assistant",
                    "content": ""
                },
                "index": 0
            }
        ],
        "object": "chat.completion.chunk"
    }
}
```

### 获取实时内容响应接口(`/api/gpt/chat-completions`)

场景：生成内容较少，实时返回的内容

请求内容：

```json
{
    "businessId": 65730,
    "systemContent": "",
    "promptId": "789",
    "userContent": ""
}
```

返回内容：

```json
{
    "choices": [
        {
            "finish_reason": "stop",
            "index": 0,
            "message": {
                "content": "The 2020 World Series was played in Texas at Globe Life Field in Arlington.",
                "role": "assistant"
            }
        }
    ],
    "created": 1677664795,
    "id": "chatcmpl-7QyqpwdfhqwajicIEznoc6Q47XAyW",
    "model": "gpt-3.5-turbo-0613",
    "object": "chat.completion",
    "usage": {
        "completion_tokens": 17,
        "prompt_tokens": 57,
        "total_tokens": 74
    }
}
```

### 获取离线内容生成接口(`/api/gpt/chat-task`)

场景：用于处理生成内容过多、时间过长的问题

请求内容：

```json
{
    "businessId": 65730,
    "systemContent": "",
    "promptId": "789",
    "userContent": ""
}
```

返回内容：

```json
{
    "businessId": 67423,
    "gen_content": "", 
    "code_content": [
        {
            "language": "",
            "content": ""
        },
        {
            "language": "",
            "content": ""
        }
    ]
}
```

这些接口允许对自然语言模型进行交互式或批量操作，并提供实时或离线的内容生成能力，以满足不同场景下的需求。

### 知识库管理

> 待完善

- `POST /knowledge_base/rebuild_vectors`: 根据文档内容重新构建向量库，并以流式输出处理进度
- `GET /knowledge_base/list_knowledge_bases`: 获取知识库列表
- `POST /knowledge_base/create_knowledge_base`: 创建知识库
- `POST /knowledge_base/delete_knowledge_base`: 删除知识库
- `GET /knowledge_base/list_docs`: 获取知识库内文件列表
- `POST /knowledge_base/upload_doc`: 将文件上传至知识库
- `POST /knowledge_base/delete_doc`: 删除知识库内指定文件
- `POST /knowledge_base/update_doc`: 更新现有文件至知识库


## 鸣谢

- interpreter 部分参考了 [open-interpreter]() 项目的代码和思路。
- 前端部分集成了 [chatgpt-web](https://github.com/Chanzhaoyu/chatgpt-web)。
- 头像地址从[userpics](https://userpics.craftwork.design/) , 注:此不可商用
- 部分设计参考[FastGPT](https://github.com/labring/FastGPT)
