package com.alinesno.infra.smart.assistant.scene.common.utils;

import cn.hutool.core.util.StrUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.llm.embedding.EmbeddingOptions;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

/**
 * LLM工具类
 */
@Component
public class KnowledgeUtils {

    @Value("${alinesno.file.allowed-ext:.xlsx,.xls,.csv,.txt,.pdf,.doc,.docx,.ppt,.pptx,.md}") // 允许扩展名
    private String allowedExtsConfig;

    @Autowired
    private ILLmAdapterService llmAdapter ;

    @Autowired
    private ILlmModelService llmModelService ;

    /**
     * 获取到指定的模型
     *
     * @param modelId
     * @param embeddingOptions
     * @return
     */
    public Llm getLlm(Long modelId, EmbeddingOptions embeddingOptions) {

        LlmModelEntity llmModel = llmModelService.getById(modelId) ;
        cn.hutool.core.lang.Assert.notNull(llmModel, "模型未配置或者不存在.");

        LlmConfig config = new LlmConfig() ;

        config.setEndpoint(llmModel.getApiUrl());
        config.setApiKey(llmModel.getApiKey()) ;
        config.setModel(llmModel.getModel()) ;

        embeddingOptions.setModel(llmModel.getModel());

        return llmAdapter.getLlm(llmModel.getProviderCode(), config);
    }

    public String computeMd5Hex(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public boolean isAllowedExtension(String ext) {
        if (StrUtil.isBlank(ext)) {
            return false;
        }

        String normalized = ext.toLowerCase();
        List<String> allowed = Arrays.stream(allowedExtsConfig.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
        return allowed.contains("."+normalized);
    }

    public String sanitizeFileName(String original) {
        if (original == null) return "unknown";
        // 去掉路径部分（防目录穿越）
        String name = Paths.get(original).getFileName().toString();
        // 进一步移除非法字符
        name = name.replaceAll("[\\\\/:*?\"<>|\\s]+", "_");
        // 禁止 .. 等
        if (name.contains("..")) {
            name = name.replace("..", "_");
        }
        return name;
    }

}
