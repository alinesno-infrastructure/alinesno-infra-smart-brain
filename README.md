# alinesno-infra-smart-brain
用于GPT推理的服务，实现基于预训练模型的自然语言生成和推理，提供平台大脑服务

## alinesno-infra-smart-brain-inference

推理模块主要的作用是针对不同的场景进行多种推理结果，使用流程的方式进行推理，每个推理涵盖一个场景，即一个场景下面有多个推理，当前做的推理是针对于软件开发的。

## 服务交互

异步与GPT交互过程，由Brain生成结果，并根据要求解析生成保存到库中，一种是异步一种是同步，同步返回流式，异步则由Client获取并保存

Client对话方  ---获取流式-->|
<<<<<<< HEAD
|Brain服务 --> Brain本地结果生成内容(保存一段时间则清理)
Client调用方  <--获取结果-->|
=======
                         |Brain服务 --> Brain本地结果生成内容(保存一段时间则清理)
Client调用方  <--获取结果-->| 
>>>>>>> 0a3dce5668702df0466ad025d18bd313e6cce399

## 鸣谢

- 项目此处集成[chatgpt-web](https://github.com/Chanzhaoyu/chatgpt-web)前端
