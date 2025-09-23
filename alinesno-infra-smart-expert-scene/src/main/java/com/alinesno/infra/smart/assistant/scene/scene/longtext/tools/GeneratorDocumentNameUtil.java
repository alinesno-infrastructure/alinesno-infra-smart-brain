package com.alinesno.infra.smart.assistant.scene.scene.longtext.tools;

import cn.hutool.core.lang.Assert;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.prompt.TextPrompt;
import com.agentsflex.core.prompt.template.TextPromptTemplate;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.scene.entity.LongTextTaskEntity;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.service.ISceneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * GeneratorDocumentNameUtil
 * 使用大模型生成文档名称和描述
 */
@Slf4j
@Component
public class GeneratorDocumentNameUtil {

    /**
     * 生成标题和描述的提示词
     */
    private static final String titleGeneratorPrompt = """
        请从以下描述中提取核心信息，生成一个准确概括内容的标题：
        {{content}}
        
        要求：
        1. 标题需精准反映描述的核心主题，不添加额外信息
        2. 标题长度控制在10-30字之间，简洁精炼
        3. 必须以JSON格式返回结果，仅包含"title"字段
        4. 确保JSON格式严格正确，无任何多余字符或注释
        
        示例：
        当描述为"这是一款搭载最新处理器的智能手机，拥有6.7英寸超清屏幕，支持5G网络和无线充电功能，电池容量为5000mAh"时，返回：
        {"title":"新款5G智能手机"}
        """;

    private static final int MAX_RETRY_COUNT = 2;
    private static final long RETRY_DELAY_MS = 1000;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private ILLmAdapterService llmAdapter ;

    @Autowired
    private ILlmModelService llmModelService ;

    @Autowired
    @Qualifier("chatThreadPool")
    private ThreadPoolTaskExecutor chatThreadPool;

    @Autowired
    private com.alinesno.infra.smart.assistant.scene.scene.longtext.service.ILongTextTaskService longTextTaskService;

    /**
     * 异步执行文档名称生成
     * 使用@Async注解配合指定的线程池实现异步处理
     *
     * @param taskEntity 长文本任务实体
     * @param promptText 用于生成标题的提示文本
     */
    @Async("chatThreadPool")
    public void generatorDocumentName(LongTextTaskEntity taskEntity, String promptText) {
        // 记录任务开始日志
        log.info("开始异步生成文档名称，任务ID: {}", taskEntity.getId());

        try {
            Long sceneId = taskEntity.getSceneId();
            Assert.notNull(sceneId, "场景ID不能为空，任务ID: " + taskEntity.getId());

            SceneEntity sceneEntity = sceneService.getById(sceneId);
            Assert.notNull(sceneEntity, "场景不存在，场景ID: " + sceneId + "，任务ID: " + taskEntity.getId());

            if (sceneEntity.getModelId() != null) {
                generateAndUpdateTitle(taskEntity, promptText, sceneEntity.getModelId());
            } else {
                log.warn("场景未配置模型，不生成文档名称，任务ID: {}", taskEntity.getId());
            }
        } catch (Exception e) {
            log.error("生成文档名称时发生异常，任务ID: {}", taskEntity.getId(), e);
            // 可以在这里添加失败处理逻辑，如设置任务状态等
        }
    }

    /**
     * 生成并更新标题，包含重试机制
     */
    private void generateAndUpdateTitle(LongTextTaskEntity taskEntity, String promptText, String modelId) {
        int retryCount = 0;
        boolean success = false;

        while (retryCount <= MAX_RETRY_COUNT && !success) {
            try {
                TextPromptTemplate template = TextPromptTemplate.of(titleGeneratorPrompt);
                TextPrompt prompt = template.format(Map.of("content", promptText));

                Llm llm = getLlm(modelId);
                String jsonTitle = llm.chat(prompt.getContent());

                // 清理可能的多余字符
                String cleanedJson = cleanJsonResponse(jsonTitle);

                // 解析JSON并提取标题
                JSONObject jsonData = JSONObject.parseObject(cleanedJson);
                String title = jsonData.getString("title");

                // 验证标题
                if (isValidTitle(title)) {
                    taskEntity.setTaskName(title);
                    longTextTaskService.updateById(taskEntity);
                    log.info("成功生成并更新文档标题，任务ID: {}", taskEntity.getId());
                    success = true;
                } else {
                    throw new IllegalArgumentException("生成的标题不符合要求: " + title);
                }
            } catch (JSONException e) {
                retryCount++;
                log.error("JSON解析异常，将进行第{}次重试，任务ID: {}", retryCount, taskEntity.getId(), e);
                if (retryCount <= MAX_RETRY_COUNT) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    log.error("达到最大重试次数，JSON解析失败，任务ID: {}", taskEntity.getId());
                    handleGenerationFailure(taskEntity);
                }
            } catch (Exception e) {
                log.error("生成标题失败，不进行重试，任务ID: {}", taskEntity.getId(), e);
                handleGenerationFailure(taskEntity);
                break;
            }
        }
    }

    /**
     * 清理LLM返回的可能包含多余字符的JSON响应
     */
    private String cleanJsonResponse(String response) {
        // 移除可能的前后引号和空白字符
        String cleaned = response.trim();
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        // 移除可能的代码块标记
        cleaned = cleaned.replace("```json", "").replace("```", "").trim();
        return cleaned;
    }

    /**
     * 验证标题是否符合要求
     */
    private boolean isValidTitle(String title) {
        return title != null && title.length() >= 5 && title.length() <= 20;
    }

    /**
     * 处理生成失败的情况
     */
    private void handleGenerationFailure(LongTextTaskEntity taskEntity) {
        // 可以设置默认标题或更新任务状态
        if (taskEntity.getTaskName() == null || taskEntity.getTaskName().isEmpty()) {
            taskEntity.setTaskName("未命名任务_" + System.currentTimeMillis());
            longTextTaskService.updateById(taskEntity);
            log.info("生成标题失败，已设置默认标题，任务ID: {}", taskEntity.getId());
        }
    }

    /**
     * 获取到指定的模型
     *
     * @return LLM模型实例
     */
    protected Llm getLlm(String modelId) {
        LlmModelEntity llmModel = llmModelService.getById(modelId);
        Assert.notNull(llmModel, "模型未配置或者不存在，模型ID: " + modelId);

        LlmConfig config = new LlmConfig();
        config.setEndpoint(llmModel.getApiUrl());
        config.setApiKey(llmModel.getApiKey());
        config.setModel(llmModel.getModel());

        return llmAdapter.getLlm(llmModel.getProviderCode(), config);
    }
}
