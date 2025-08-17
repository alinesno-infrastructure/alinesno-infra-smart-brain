package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.tools;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.image.*;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.message.AiMessage;
import com.alinesno.infra.common.core.cache.RedisUtils;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.role.llm.ModelAdapterLLM;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ChatEditorDto;
import com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto.ImageGeneratorDTO;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.AgentSceneEntity;
import com.alinesno.infra.smart.im.service.IAgentSceneService;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 文章生成工具类
 */
@Slf4j
@Component
public class ArticleChatRoleUtil {


    @Autowired
    private IIndustryRoleService roleService ;

    @Autowired
    private ILLmAdapterService llmAdapterService ;

    @Autowired
    private ILlmModelService llmModelService ;

    @Autowired
    private ModelAdapterLLM modelAdapterLLM ;

    @Autowired
    private IAgentSceneService agentSceneService ;

    @Autowired
    private CloudStorageConsumer storageConsumer ;

    private static final String PROMPT_TEXT = """
            ##角色=你是一名文章修改专员，有资深的修改经验，现在根据用户需求修改文章内容或者某个片段
            ##原整篇文章=%s
            ##要修改的内容=%s
            ##修改要求=%s
            ##限制
            - 只返回修改的结果，其它不相关内容不要返回
            """;

    /**
     * 生成图片
     * 用途	推荐比例	示例分辨率	适用场景
     * 文章配图	16:9	1920×1080	博客、新闻、网页文章
     * 图文混排	4:3	1200×900	技术文档、电子书、PPT
     * 社交媒体	1:1	1080×1080	Instagram、微博、小红书
     * 封面/横幅	2:1 或 3:1	1200×600	文章头图、网页Banner
     * 手机适配	9:16	1080×1920	短视频、竖版文章（如微信公众号）
     * @param dto
     * @param query
     * @return
     */
    public List<String> generateImages(ImageGeneratorDTO dto, Long roleId, PermissionQuery query) {
        // 1. 参数校验
        if (dto == null || dto.getCount() <= 0) {
            throw new IllegalArgumentException("Invalid image generation parameters");
        }

        // 2. 获取模型配置
        AgentSceneEntity agentSceneEntity = agentSceneService.getByRoleAndScene(roleId, SceneEnum.ARTICLE_WRITING.getSceneInfo().getId());
        if (agentSceneEntity == null) {
            throw new IllegalStateException("Scene configuration not found for role: " + roleId);
        }

        LlmModelEntity modelEntity = llmModelService.getById(agentSceneEntity.getImageModelId());
        if (modelEntity == null) {
            throw new IllegalStateException("未配置图片生成能力.") ;
        }

        // 3. 初始化图片模型
        ImageConfig llmConfig = new ImageConfig();
        llmConfig.setEndpoint(modelEntity.getApiUrl());
        llmConfig.setApiKey(modelEntity.getApiKey());
        llmConfig.setSecretKey(modelEntity.getSecretKey());
        llmConfig.setModel(modelEntity.getModel());

        String modelProvider = modelEntity.getProviderCode();
        ImageModel imageModel = llmAdapterService.getImageModel(modelProvider, llmConfig);

        // 4. 准备图片生成请求
        GenerateImageRequest request = new GenerateImageRequest();
        request.setPrompt("画面左侧是一个人皱着眉头，有些生气的表情，旁边气泡中文字：'有人说我说话直别介意怎么办？'，字体为黑色描边的白色字体。这个人站在一个普通的室内场景中，身后有简单的沙发和茶几。 画面右侧是另一个人双手抱在胸前，一脸不屑，旁边气泡中文字：'我怼人你也别介意。'，字体为红色立体的抖音风格字体。这个人站在同样的室内场景中，但位置与左侧的人稍有间隔，身后也有简单的沙发和茶几。整个场景色调较为明亮，但两个人的情绪让画面稍显紧张。");
        request.setNegativePrompt("人物");
        request.setN(dto.getCount());
        request.setSize(1920, 1080);

        // 5. 生成图片并保存
        ImageResponse generate = imageModel.generate(request);
        if (generate == null || generate.getImages() == null || generate.getImages().isEmpty()) {
            log.warn("No images generated for request: {}", request);
            return Collections.emptyList();
        }

        // 6. 保存图片并收集路径
        List<String> imagePaths = new ArrayList<>(generate.getImages().size());
        String tempDir = System.getProperty("java.io.tmpdir");

        for (Image image : generate.getImages()) {
            try {
                // 使用线程安全的文件名生成方式
                String fileName = "Image_" + UUID.randomUUID() + "_" + Thread.currentThread().getId() + ".png";
                File file = new File(tempDir, fileName);

                // 保存图片文件
                image.writeToFile(file);

                // 获取绝对路径并添加到结果列表
                String filePath = file.getAbsolutePath();

                // 上传并获取到图片请求
                String uploadUrl = storageConsumer.uploadCallbackUrl(file , "qiniu-kodo-pub").getData() ;

                imagePaths.add(uploadUrl);

                log.debug("Image generated and saved to: {}", filePath);
            } catch (Exception e) {
                log.error("Failed to save generated image", e);
                // 可以选择继续处理其他图片或抛出异常
            }
        }

        log.debug("Generated {} images", imagePaths.size());
        return imagePaths;
    }

    @SneakyThrows
    public CompletableFuture<AiMessage> chat(IndustryRoleDto roleDto, ChatEditorDto dto, PermissionQuery query) {

        AgentSceneEntity agentSceneEntity =  agentSceneService.getByRoleAndScene(roleDto.getId() , SceneEnum.ARTICLE_WRITING.getSceneInfo().getId()) ;
        LlmModelEntity modelEntity = llmModelService.getById(agentSceneEntity.getLlmModelId()) ;

        LlmConfig llmConfig = new LlmConfig();
        llmConfig.setEndpoint(modelEntity.getApiUrl());
        llmConfig.setApiKey(modelEntity.getApiKey()) ;
        llmConfig.setApiSecret(modelEntity.getSecretKey());
        llmConfig.setModel(modelEntity.getModel());

        String modelProvider = modelEntity.getProviderCode() ;

        Llm llm = llmAdapterService.getLlm(modelProvider, llmConfig);

        IndustryRoleEntity role = roleService.getById(roleDto.getId());
        long messageId = IdUtil.getSnowflakeNextId() ;

        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setChannelStreamId(String.valueOf(dto.getChannelStreamId()));
        taskInfo.setRoleId(roleDto.getId());
        taskInfo.setChannelId(dto.getSceneId());
        taskInfo.setSceneId(dto.getSceneId());

        String prompt = String.format(PROMPT_TEXT , dto.getArticleContent() , dto.getModifyText() ,  dto.getMessage()) ;

        CompletableFuture<AiMessage> future = modelAdapterLLM.getSingleAiChatResultAsync(llm, role, prompt , taskInfo , messageId) ;

//        AiMessage message = future.get();
//        log.debug("output = {}" , message.getFullContent());

        return future ;
    }

}
