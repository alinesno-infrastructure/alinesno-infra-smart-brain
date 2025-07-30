/**
 * 内容生成引用实现，以提高内容的可信程度，开发内容参考CSDN文章: https://blog.csdn.net/eahba/article/details/145713543 <br/>
 * 1. 使用工具调用标注文档ID
 * 2. 使用工具调用标注文档ID及文本片段
 * 3. 直接提示模型生成引用
 * 4. 检索后处理（如对检索到的上下文进行压缩以提高其相关性）
 * 5. 生成后处理（通过第二次调用LLM对答案注释引用）
 */
package com.alinesno.infra.smart.assistant.role.quote;