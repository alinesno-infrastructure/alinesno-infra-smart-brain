package com.alinesno.infra.smart.assistant.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.agentsflex.core.document.Document;
import com.agentsflex.core.image.*;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.ocr.OcrConfig;
import com.agentsflex.core.ocr.OcrModel;
import com.agentsflex.core.ocr.OcrRequest;
import com.agentsflex.core.ocr.OcrResponse;
import com.agentsflex.core.prompt.ImagePrompt;
import com.agentsflex.core.reranker.ReRanker;
import com.agentsflex.core.reranker.ReRankerConfig;
import com.agentsflex.core.reranker.ReRankerRequest;
import com.agentsflex.core.reranker.ReRankerResponse;
import com.agentsflex.core.speech.*;
import com.agentsflex.core.store.VectorData;
import com.agentsflex.core.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.smart.assistant.adapter.enums.LlmModelProviderEnums;
import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.api.TestLlmModelDto;
import com.alinesno.infra.smart.assistant.api.config.VoiceInputData;
import com.alinesno.infra.smart.assistant.api.config.VoicePlayData;
import com.alinesno.infra.smart.assistant.constants.PublishConst;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.enums.ModelDataScopeOptions;
import com.alinesno.infra.smart.assistant.enums.ModelTypeEnums;
import com.alinesno.infra.smart.assistant.mapper.LlmModelMapper;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

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
        taskInfo.setChannelStreamId(String.valueOf(dto.getTestChannelId()));
        long workflowId = IdUtil.getSnowflakeNextId() ;

        IndustryRoleEntity role = new IndustryRoleEntity();
        role.setRoleAvatar(PublishConst.EMPTY);
        role.setRoleName(PublishConst.ANONYMOUS_USERNAME);
        role.setId(PublishConst.ANONYMOUS_USER);

        if(modelType.equals(ModelTypeEnums.LARGE_LANGUAGE_MODEL.getCode())){  // 大语言模型

            validateLargeLanguageModel(url, apiKey, secretKey , modelName, modelProvider, taskInfo, role, workflowId);

        } else if(modelType.equals(ModelTypeEnums.VECTOR_MODEL.getCode())){  // 向量模型

            validateVectorModel(url, apiKey, modelName, modelProvider, taskInfo, role, workflowId);

        } else if(modelType.equals(ModelTypeEnums.RE_RANKING_MODEL.getCode())){  // 重排模型

            validateRankingModel(url, apiKey, modelName, modelProvider, taskInfo, role, workflowId);

        }else if(modelType.equals(ModelTypeEnums.SPEECH_RECOGNITION.getCode())){  // 语音识别

            return validateSpeechRecognition(url, apiKey, modelName, modelProvider, taskInfo, role, dto);

        }else if(modelType.equals(ModelTypeEnums.SPEECH_SYNTHESIS.getCode())){  // 语音合成

          return validateSpeechSynthesis(url, apiKey, modelName, modelProvider, taskInfo, role, workflowId , dto.getVoice());

        }else if(modelType.equals(ModelTypeEnums.IMAGE_GENERATION.getCode())){ // 图片生成

           return validateImageGeneration(url, apiKey, secretKey , modelName, modelProvider, taskInfo, role, workflowId);

        }else if(modelType.equals(ModelTypeEnums.VISION_MODEL.getCode())){  // 视觉模型

            validateVisionModel(url, apiKey, modelName, modelProvider, taskInfo, role, workflowId);

        }else if(modelType.equals(ModelTypeEnums.OCR_MODEL.getCode())){  // OCR模型识别
            validateOCRModel(url, apiKey, modelName, modelProvider, taskInfo, role, workflowId);
        }

        return null;
    }

    /**
     * 测试OCR模型识别
     * @param url
     * @param apiKey
     * @param modelName
     * @param modelProvider
     * @param taskInfo
     * @param role
     * @param workflowId
     */
    private void validateOCRModel(String url,
                                  String apiKey,
                                  String modelName,
                                  String modelProvider,
                                  MessageTaskInfo taskInfo,
                                  IndustryRoleEntity role,
                                  long workflowId) {


        OcrConfig ocrConfig = new OcrConfig();
        ocrConfig.setEndpoint(url);
        ocrConfig.setApiKey(apiKey) ;

        OcrModel ocrModel = llmAdapterService.ocrModel(modelProvider,ocrConfig) ;

        OcrRequest ocrRequest = new OcrRequest();

        String downloadUrl = "http://data.linesno.com/OIP-C.jpg" ;

        // HTTPUtil从网络下载到临时文件夹

        File targetFile = new File(System.getProperty("java.io.tmpdir") + File.separator + IdUtil.getSnowflakeNextIdStr() + ".jpg");
        HttpUtil.downloadFile(downloadUrl ,  targetFile.getAbsoluteFile()) ;

        ocrRequest.setImage(targetFile) ;
        OcrResponse response = ocrModel.recognize(ocrRequest);

        log.debug("OCR识别结果:{}" , response.getResults());

        streamMessagePublisher.doStuffAndPublishAnEvent(response.getResults(), role, taskInfo, IdUtil.getSnowflakeNextId()) ;
        streamMessagePublisher.doStuffAndPublishAnEvent("[DONE]", role, taskInfo, IdUtil.getSnowflakeNextId()) ;
    }

    /**
     * 测试识别内容返回文字
     * @param url
     * @param apiKey
     * @param modelName
     * @param modelProvider
     * @param taskInfo
     * @param role
     * @param dto
     * @return
     */
    private String validateSpeechRecognition(String url,
                                             String apiKey,
                                             String modelName,
                                             String modelProvider,
                                             MessageTaskInfo taskInfo,
                                             IndustryRoleEntity role,
                                             TestLlmModelDto dto) {

        SpeechConfig config = new SpeechConfig();
        config.setEndpoint(url);
        config.setApiKey(apiKey) ;
        config.setModel(modelName);

        SpeechModel speechModel = llmAdapterService.speechModel(modelProvider,config) ;

        RecognizeSpeechRequest request = new RecognizeSpeechRequest();
        request.setAudioList(dto.getAudioList()) ;

        List<RecognizeSpeechResponse> generate = speechModel.recognize(request);
        String output = null ;
        if(generate != null){
            for(RecognizeSpeechResponse speechResponse : generate){
                output = speechResponse.getTranscripts().get(0).getText() ;
                log.debug("speechResponse = {}" , output) ;
            }
        }else{
            output = "识别失败" ;
        }

        return output ;
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

        LlmConfig llmConfig = new LlmConfig();
        llmConfig.setEndpoint(url);
        llmConfig.setApiKey(apiKey) ;
        llmConfig.setModel(modelName);

        Llm llm = llmAdapterService.visionModel(modelProvider, llmConfig);
        ImagePrompt imagePrompt = new ImagePrompt("这是什么" , "https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg");

        llm.chatStream(imagePrompt , new StreamResponseListener(){

            @Override
            public void onMessage(ChatContext context , AiMessageResponse response) {

                AiMessage message = response.getMessage();
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

    /**
     * 测试图片生成
     *
     * @param url
     * @param apiKey
     * @param modelName
     * @param modelProvider
     * @param taskInfo
     * @param role
     * @param workflowId
     * @return
     */
    private String validateImageGeneration(String url,
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
        request.setN(1);
        request.setSize(512,512);

        ImageResponse generate = imageModel.generate(request) ;
        if (generate != null && generate.getImages() != null){
            int index = 0;
            for (Image image : generate.getImages()) {

                String tempDir = System.getProperty("java.io.tmpdir");
                String fileName = "Image" + UUID.randomUUID() + ".png"; // 生成唯一的文件名
                File file = new File(tempDir, fileName); // 构建完整的文件路径

                image.writeToFile(file) ;

                return file.getAbsolutePath() ;
            }
        }

        log.debug("generate={}" , generate);
        return null ;
    }

    /**
     * 测试语音合成
     *
     * @param url
     * @param apiKey
     * @param modelName
     * @param modelProvider
     * @param taskInfo
     * @param role
     * @param workflowId
     * @param voice
     */
    private String validateSpeechSynthesis(String url,
                                           String apiKey,
                                           String modelName,
                                           String modelProvider,
                                           MessageTaskInfo taskInfo,
                                           IndustryRoleEntity role,
                                           long workflowId,
                                           String voice) {

        SpeechConfig config = new SpeechConfig();
        config.setEndpoint(url);
        config.setApiKey(apiKey) ;
        config.setModel(modelName);

        SpeechModel speechModel = llmAdapterService.speechModel(modelProvider,config) ;

        SynthesizeSpeechRequest request = new SynthesizeSpeechRequest();
        request.setVoice(voice);
        request.setText("一间有着精致窗户的花店，漂亮的木质门，摆放着花朵");

        SpeechResponse generate = speechModel.synthesize(request);
        if (generate != null && generate.getSpeechMp3() != null){

            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = "Speech_" + UUID.randomUUID() + ".mp3"; // 生成唯一的文件名
            File file = new File(tempDir, fileName); // 构建完整的文件路径

            generate.getSpeechMp3().writeToFile(file) ;

            return file.getAbsolutePath() ;
        }

        log.debug("generate = {}" , generate);

        return null ;
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

        ReRankerConfig config = new ReRankerConfig() ;
        config.setEndpoint(url);
        config.setApiKey(apiKey) ;
        config.setModel(modelName);

        ReRanker reRanker = llmAdapterService.reranker(modelProvider , config) ;


        // 创建测试用的 ReRankerRequest 实例
        ReRankerRequest request = new ReRankerRequest();
        request.setTop_n(2);
        request.setReturn_documents(true);
        request.setQuery("什么是文本排序模型");
        List<String> documents = Arrays.asList(
                "文本排序模型广泛用于搜索引擎和推荐系统中，它们根据文本相关性对候选文本进行排序",
                "量子计算是计算科学的一个前沿领域",
                "预训练语言模型的发展给文本排序模型带来了新的进展"
        );
        request.setDocuments(documents);

        // 调用 reranker 方法获取响应
        ReRankerResponse response = reRanker.reranker(request);

        // 使用 Fastjson 格式化打印响应
        String formattedJson = JSON.toJSONString(response, true);
        log.info("ReRanker Response: {}", formattedJson);
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
                                            String secretKey,
                                            String modelName,
                                            String modelProvider,
                                            MessageTaskInfo taskInfo,
                                            IndustryRoleEntity role,
                                            long workflowId) {
        LlmConfig llmConfig = new LlmConfig();
        llmConfig.setEndpoint(url);
        llmConfig.setApiKey(apiKey) ;
        llmConfig.setApiSecret(secretKey);
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

    /**
     * 获取声音模型
     *
     * @param modelId
     * @return
     */
    @Override
    public Map<String, Object> getVoiceModelSpeech(String modelId) {
        LlmModelEntity modelEntity = getById(modelId) ;

        String providerCode = modelEntity.getProviderCode() ;  // 模型提供商
        String model = modelEntity.getModel() ;  // 获取模型

        // 获取语音模型
        Map<String, Object> modelInfo = LlmModelProviderEnums.getSpeechModelInfoByTypeAndModelName(providerCode , model) ;

        log.debug(">>>> " + JSONObject.toJSONString(modelInfo));
        return modelInfo ;
    }

    @Override
    public String speechSynthesisFile(IndustryRoleEntity role , ChatMessageDto dto) {

        IndustryRoleDto roleDto = IndustryRoleDto.fromEntity(role) ;
        VoicePlayData voicePlayData = roleDto.getVoicePlayData() ;

        Assert.isTrue(roleDto.isVoicePlayStatus() , "语音播放未开启") ;
        Assert.notNull(voicePlayData, "语音模型未配置");

        LlmModelEntity modelEntity = getById(voicePlayData.getVoiceModel()) ;

        SpeechConfig config = new SpeechConfig();
        config.setEndpoint(modelEntity.getApiUrl());
        config.setApiKey(modelEntity.getApiKey()) ;

        config.setModel(modelEntity.getModel());

        SpeechModel speechModel = llmAdapterService.speechModel(modelEntity.getProviderCode(),config) ;

        SynthesizeSpeechRequest request = new SynthesizeSpeechRequest();
        request.setVoice(voicePlayData.getVoiceSpeechModel());
        request.setText(dto.getChatText()+"");

        SpeechResponse generate = speechModel.synthesize(request);
        if (generate != null && generate.getSpeechMp3() != null){

            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = "Speech_" + UUID.randomUUID() + ".mp3"; // 生成唯一的文件名
            File file = new File(tempDir, fileName); // 构建完整的文件路径

            generate.getSpeechMp3().writeToFile(file) ;

            return file.getAbsolutePath() ;
        }

        log.debug("generate = {}" , generate);
        return null;
    }

    @Override
    public String speechRecognitionFile(IndustryRoleEntity role, String data) {

        IndustryRoleDto roleDto = IndustryRoleDto.fromEntity(role) ;
        VoiceInputData voiceRecognitionData = roleDto.getVoiceInputData() ;
        Assert.notNull(voiceRecognitionData , "语音识别未配置，请先配置") ;

        LlmModelEntity modelEntity = getById(voiceRecognitionData.getVoiceModel()) ;

        Assert.notNull(modelEntity, "语音识别模型未配置");

        SpeechConfig config = new SpeechConfig();
        config.setEndpoint(modelEntity.getApiUrl());
        config.setApiKey(modelEntity.getApiKey()) ;
        config.setModel(modelEntity.getModel());

        SpeechModel speechModel = llmAdapterService.speechModel(modelEntity.getProviderCode(),config) ;

        RecognizeSpeechRequest request = new RecognizeSpeechRequest();
        request.setAudioList(List.of(data)) ;

        List<RecognizeSpeechResponse> generate = speechModel.recognize(request);
        String output = null ;
        if(generate != null){
            for(RecognizeSpeechResponse speechResponse : generate){
                output = speechResponse.getTranscripts().get(0).getText() ;
                log.debug("speechResponse = {}" , output) ;
            }
        }

        return output ;
    }

    @Override
    public List<LlmModelEntity> listLlmMode(PermissionQuery query, String modelType) {
        LambdaQueryWrapper<LlmModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntityClass(LlmModelEntity.class);

        Long orgId = query.getOrgId();
        Long operatorId = query.getOperatorId();

        // 组合条件：公共模型 或 组织模型（按 orgId 过滤） 或 个人模型（按 operatorId 过滤）
        queryWrapper.and(wrapper ->
                wrapper
                        // 无条件查询 PUBLIC 模型
                        .eq(LlmModelEntity::getModelPermission, ModelDataScopeOptions.PUBLIC.getValue())
                        .or()
                        // 查询 ORG 模型，并限制 orgId 匹配
                        .nested(q -> {
                            q.eq(LlmModelEntity::getModelPermission, ModelDataScopeOptions.ORG.getValue())
                                    .eq(LlmModelEntity::getOrgId, orgId);  // 直接按 orgId 过滤
                        })
                        .or()
                        // 查询 PERSON 模型，并限制 operatorId 匹配
                        .nested(q -> {
                            q.eq(LlmModelEntity::getModelPermission, ModelDataScopeOptions.PERSON.getValue())
                                    .eq(LlmModelEntity::getOperatorId, operatorId);  // 直接按 operatorId 过滤
                        })
        );

        // 按模型类型筛选（如果有）
        if (StringUtils.isNotEmpty(modelType)) {
            queryWrapper.eq(LlmModelEntity::getModelType, modelType);
        }

        return list(queryWrapper);
    }

}