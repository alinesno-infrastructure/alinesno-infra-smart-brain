package com.alinesno.infra.smart.assistant.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.image.*;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.store.VectorData;
import com.agentsflex.core.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.TestLlmModelDto;
import com.alinesno.infra.smart.assistant.constants.PublishConst;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.enums.ModelTypeEnums;
import com.alinesno.infra.smart.assistant.mapper.LlmModelMapper;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class LlmModelServiceImpl extends IBaseServiceImpl<LlmModelEntity, LlmModelMapper> implements ILlmModelService {

    @Autowired
    private ILLmAdapterService llmAdapterService ;

    @Autowired
    protected StreamMessagePublisher streamMessagePublisher ;  // 不保存入库的消息

    @Override
    public String testLlmModel(TestLlmModelDto dto) {

        String modelType = dto.getModelType() ;
        String modelProvider = dto.getProviderCode() ;
        String url = dto.getApiUrl() ;
        String apiKey = dto.getApiKey() ;
        String secretKey = dto.getSecretKey() ;
        String modelName = dto.getModel() ;

        MessageTaskInfo taskInfo = new MessageTaskInfo();
        taskInfo.setChannelId(dto.getTestChannelId());
        long workflowId = IdUtil.getSnowflakeNextId() ;

        IndustryRoleEntity role = new IndustryRoleEntity();
        role.setRoleAvatar(PublishConst.EMPTY);
        role.setRoleName(PublishConst.ANONYMOUS_USERNAME);
        role.setId(PublishConst.ANONYMOUS_USER);

        if(modelType.equals(ModelTypeEnums.LARGE_LANGUAGE_MODEL.getCode())){  // 大语言模型

            validateLargeLanguageModel(url, apiKey, modelName, modelProvider, taskInfo, role, workflowId);

        } else if(modelType.equals(ModelTypeEnums.VECTOR_MODEL.getCode())){  // 向量模型

            validateVectorModel(url, apiKey, modelName, modelProvider, taskInfo, role, workflowId);

        } else if(modelType.equals(ModelTypeEnums.RE_RANKING_MODEL.getCode())){  // 重排模型

            validateRankingModel(url, apiKey, modelName, modelProvider, taskInfo, role, workflowId);

        }else if(modelType.equals(ModelTypeEnums.SPEECH_RECOGNITION.getCode())){  // 语音识别

            log.debug("语音识别（FormData）请求已收到");

        }else if(modelType.equals(ModelTypeEnums.SPEECH_SYNTHESIS.getCode())){  // 语音合成

            validateSpeechSynthesis(url, apiKey, modelName, modelProvider, taskInfo, role, workflowId);

        }else if(modelType.equals(ModelTypeEnums.IMAGE_GENERATION.getCode())){ // 图片生成

            validateImageGeneration(url, apiKey, secretKey , modelName, modelProvider, taskInfo, role, workflowId);

        }else if(modelType.equals(ModelTypeEnums.VISION_MODEL.getCode())){  // 视觉模型

            validateVisionModel(url, apiKey, modelName, modelProvider, taskInfo, role, workflowId);

        }

        return null;
    }

    /**
     * 测试视觉模型
     * @param url
     * @param apiKey
     * @param modelName
     * @param modelProvider
     * @param taskInfo
     * @param role
     * @param workflowId
     */
    private void validateVisionModel(String url,
                                     String apiKey,
                                     String modelName,
                                     String modelProvider,
                                     MessageTaskInfo taskInfo,
                                     IndustryRoleEntity role,
                                     long workflowId) {

    }

    /**
     * 测试图片生成
     * @param url
     * @param apiKey
     * @param modelName
     * @param modelProvider
     * @param taskInfo
     * @param role
     * @param workflowId
     */
    private void validateImageGeneration(String url,
                                         String apiKey,
                                         String secretKey,
                                         String modelName,
                                         String modelProvider,
                                         MessageTaskInfo taskInfo,
                                         IndustryRoleEntity role,
                                         long workflowId) {
        ImageConfig config = new ImageConfig();

        config.setApiKey(apiKey);
        config.setEndpoint(url);
        config.setModel(modelName);
        config.setSecretKey(secretKey);

        ImageModel imageModel = llmAdapterService.getImageModel(modelProvider,config);

        GenerateImageRequest request = new GenerateImageRequest();
        request.setPrompt("画面左侧是一个人皱着眉头，有些生气的表情，旁边气泡中文字：“有人说我说话直别介意怎么办？”，字体为黑色描边的白色字体。这个人站在一个普通的室内场景中，身后有简单的沙发和茶几。 画面右侧是另一个人双手抱在胸前，一脸不屑，旁边气泡中文字：“我怼人你也别介意。”，字体为红色立体的抖音风格字体。这个人站在同样的室内场景中，但位置与左侧的人稍有间隔，身后也有简单的沙发和茶几。整个场景色调较为明亮，但两个人的情绪让画面稍显紧张。") ;
        request.setNegativePrompt("人物");
        request.setN(3);
        request.setSize(512,512);

        ImageResponse generate = imageModel.generate(request) ;
        if (generate != null && generate.getImages() != null){
            int index = 0;
            for (Image image : generate.getImages()) {
                image.writeToFile(new File("E:\\tmp\\mp3\\image_" + UUID.randomUUID() + "_" +(index++)+".jpg"));
            }
        }

        log.debug("generate={}" , generate);
    }

    /**
     * 测试语音合成
     * @param url
     * @param apiKey
     * @param modelName
     * @param modelProvider
     * @param taskInfo
     * @param role
     * @param workflowId
     */
    private void validateSpeechSynthesis(String url,
                                         String apiKey,
                                         String modelName,
                                         String modelProvider,
                                         MessageTaskInfo taskInfo,
                                         IndustryRoleEntity role,
                                         long workflowId) {

    }

    /**
     * 测试重排模型
     * @param url
     * @param apiKey
     * @param modelName
     * @param modelProvider
     * @param taskInfo
     * @param role
     * @param workflowId
     */
    private void validateRankingModel(String url,
                                      String apiKey,
                                      String modelName,
                                      String modelProvider,
                                      MessageTaskInfo taskInfo,
                                      IndustryRoleEntity role,
                                      long workflowId) {

    }

    /**
     * 测试向量模型
     * @param url
     * @param apiKey
     * @param modelName
     * @param modelProvider
     * @param taskInfo
     * @param role
     * @param workflowId
     */
    private void validateVectorModel(String url,
                                     String apiKey,
                                     String modelName,
                                     String modelProvider,
                                     MessageTaskInfo taskInfo,
                                     IndustryRoleEntity role,
                                     long workflowId) {

        LlmConfig llmConfig = new LlmConfig();
        llmConfig.setEndpoint(url);
        llmConfig.setApiKey(apiKey) ;
        llmConfig.setModel(modelName);

        Llm llm = llmAdapterService.getLlm(modelProvider, llmConfig);

        try{
            VectorData embeddings = llm.embed(Document.of("我是向量化."));

            Assert.notNull(embeddings.getVector() , "向量化失败");
            log.debug("向量化的数据:{}"  , Arrays.toString(embeddings.getVector()));
        }catch(Exception e){
            log.error("向量化失败:{}" , e.getMessage());
            throw new RuntimeException("向量化失败:" + e.getMessage()) ;
        }

    }

    /**
     * 测试大语言模型
     * @param url
     * @param apiKey
     * @param modelName
     * @param modelProvider
     * @param taskInfo
     * @param role
     * @param workflowId
     */
    private void validateLargeLanguageModel(String url,
                                            String apiKey,
                                            String modelName,
                                            String modelProvider,
                                            MessageTaskInfo taskInfo,
                                            IndustryRoleEntity role,
                                            long workflowId) {
        LlmConfig llmConfig = new LlmConfig();
        llmConfig.setEndpoint(url);
        llmConfig.setApiKey(apiKey) ;
        llmConfig.setModel(modelName);

        Llm llm = llmAdapterService.getLlm(modelProvider, llmConfig);
        String prompt = "你是谁." ;

        llm.chatStream(prompt , new StreamResponseListener(){

            @Override
            public void onMessage(ChatContext context , AiMessageResponse response) {

                log.debug(">>>> " + JSONObject.toJSONString(response));

                AiMessage message = response.getMessage();

                log.debug(">>>> " + JSONObject.toJSONString(message));

                if(StringUtil.hasText(message.getReasoningContent())){
                    taskInfo.setReasoningText(message.getReasoningContent());
                    streamMessagePublisher.doStuffAndPublishAnEvent(null , role, taskInfo, workflowId);
                }

                if(StringUtil.hasText(message.getContent())){
                    taskInfo.setReasoningText(null);
                    streamMessagePublisher.doStuffAndPublishAnEvent(message.getContent() , role, taskInfo, workflowId);
                }

                MessageStatus status =  message.getStatus() ;
                if(status == MessageStatus.END){  // 结束
                    streamMessagePublisher.doStuffAndPublishAnEvent("流式任务完成.",
                            role,
                            taskInfo,
                            IdUtil.getSnowflakeNextId()) ;
                }
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                log.error(">>>> " + throwable.getMessage());

                // 标识为异常信息
                taskInfo.setHasError(true);
                taskInfo.setErrorMessage(throwable.getMessage());

                streamMessagePublisher.doStuffAndPublishAnEvent(throwable.getMessage(), role, taskInfo, IdUtil.getSnowflakeNextId()) ;
                streamMessagePublisher.doStuffAndPublishAnEvent("[DONE]", role, taskInfo, IdUtil.getSnowflakeNextId()) ;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}