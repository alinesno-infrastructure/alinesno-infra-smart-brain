# alinesno-infra-smart-brain

**alinesno-infra-smart-brain** 是一个用于进行自然语言生成和推理的服务，基于预训练模型，旨在提供强大的平台大脑服务。

## alinesno-infra-smart-brain-inference

推理模块旨在针对多种场景进行多样化推理，采用流程化的方式进行推理。每个推理场景下都包含多个推理过程。当前项目主要集中在软件开发场景下的推理。

## 服务交互

服务提供异步与 GPT 进行交互的方式。Brain 生成结果，并根据要求解析后保存到数据库。其中，同步返回结果为流式输出，而异步方式则由 Client 获取并保存结果。

```
Client 对话方  ---获取流式-->|
                         | Brain 服务 --> 生成 Brain 本地结果内容（保存一段时间后清理）
Client 调用方  <--获取结果-->|
```

## API

### 对话接口

- `POST /chat/fastchat`: 直接与 LLM 模型进行对话
- `POST /chat/chat`: 通过 LLMChain 与 LLM 模型对话
- `POST /chat/knowledge_base_chat`: 与知识库进行对话
- `POST /chat/search_engine_chat`: 与搜索引擎进行对话

### 知识库管理

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
