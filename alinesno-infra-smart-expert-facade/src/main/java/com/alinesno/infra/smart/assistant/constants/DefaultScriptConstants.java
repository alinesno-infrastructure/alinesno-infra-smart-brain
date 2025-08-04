package com.alinesno.infra.smart.assistant.constants;

/**
 * 默认脚本常量类
 */
public interface DefaultScriptConstants {

    /**
     * 默认Groovy脚本示例
     */
    String DEFAULT_GROOVY_SCRIPT_DEMO = """
            import com.alinesno.infra.smart.assistant.api.ModelNodeDto
            import com.agentsflex.core.llm.Llm
            import com.agentsflex.llm.deepseek.DeepseekLlm
            import com.agentsflex.llm.deepseek.DeepseekLlmConfig
            import java.util.concurrent.CompletableFuture
                        
            // 创建对象
            def modelNode = new ModelNodeDto(UUID.randomUUID().toString(), false)
            modelAdapterLLM.setNode(modelNode)
                        
            def config = new DeepseekLlmConfig(
                endpoint: "https://dashscope.aliyuncs.com/compatible-mode/v1",  // 更换成对应的API接口地址
                apiKey: secretKey?.qwen_key, // 请在全局配置，密钥配置中设置token apiKey
                model: "qwen-turbo"  // 指定的模型名称
            )
                        
            Llm llm = new DeepseekLlm(config)
                        
            def prompt = expertService.clearMessage(datasetKnowledgeDocument + taskInfo.getText())
                        
            // 使用异步方法并返回CompletableFuture
            CompletableFuture<String> future = modelAdapterLLM.processStreamSingleAsync(llm, role, prompt, taskInfo)
                        
            // 当处理完成时设置完整内容
            future.thenAccept { output ->
                taskInfo.setFullContent(output)
            }
                        
            return future
            """ ;

}
