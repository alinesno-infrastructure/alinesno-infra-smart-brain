# alinesno-infra-smart-brain

**alinesno-infra-smart-brain** 是一个用于进行自然语言生成和推理的服务，基于预训练模型，旨在提供强大的平台大脑服务。

## alinesno-infra-smart-brain-inference

推理模块旨在针对多种场景进行多样化推理，采用流程化的方式进行推理。每个推理场景下都包含多个推理过程。当前项目主要集中在软件开发场景下的推理。

## 服务交互

服务提供异步与 GPT 进行交互的方式。Brain 生成结果，并根据要求解析后保存到数据库。其中，同步返回结果为流式输出，而异步方式则由 Client 获取并保存结果。

<img src="images/brain-service-design.png"  alt="服务交互设计规划"/>

说明:

- [重点]服务之前交互以自定义的yaml格式来进行交互，以达到更高的准确率，数据安全问题，节约Token成本，响应时间等问题
- 每个专家可以执行多个Prompt，assistant使用多任务多线程方式调用返回结果(是否引用工作流？待考虑)
- 执行服务每个服务在`adapter`里面进一步添加模板解析的能力来调用本服务，来进行多方的适配管理(DDD的概念)
- 处理完成之后，数据还有其它统一进入到数据仓库里面，为后期的专家服务提供知识来源，同时也是向量仓数据来源之一(注意是之一)

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
