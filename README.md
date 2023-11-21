# alinesno-infra-smart-brain
用于GPT推理的服务，实现基于预训练模型的自然语言生成和推理，提供平台大脑服务

## alinesno-infra-smart-brain-inference

推理模块主要的作用是针对不同的场景进行多种推理结果，使用流程的方式进行推理，每个推理涵盖一个场景，即一个场景下面有多个推理，当前做的推理是针对于软件开发的。

## 服务交互

异步与GPT交互过程，由Brain生成结果，并根据要求解析生成保存到库中，一种是异步一种是同步，同步返回流式，异步则由Client获取并保存

Client对话方  ---获取流式-->| 
                         |Brain服务 --> Brain本地结果生成内容(保存一段时间则清理)
Client调用方  <--获取结果-->|  

## API

/chat/fastchat 与llm模型对话(直接与api对话)POST

POST/chat/chat 与llm模型对话(通过LLMChain)
POST/chat/knowledge_base_chat 与知识库对话
POST/chat/search_engine_chat 与搜索引擎对话

POST根据content中文档重建向量库，流式输出处理进度
GET/knowledge_base/list_knowledge_bases 获取知识库列表
POST/knowledge_base/create_knowledge_base 创建知识库
POST/knowledge base/delete knowledge_base 删除知识库
/knowledge_base/list_docs 获取知识库内的文件列表GET
POST/knowledge base/upload doc 上传文件到知识库
POST/knowledge_base/delete_doc 删除知识库内指定文件
POST/knowledge base/update_doc 更新现有文件到知识库

## 鸣谢

- interpreter部分集成参考[open-interpreter]()代码和思路
- 项目此处集成[chatgpt-web](https://github.com/Chanzhaoyu/chatgpt-web)前端
