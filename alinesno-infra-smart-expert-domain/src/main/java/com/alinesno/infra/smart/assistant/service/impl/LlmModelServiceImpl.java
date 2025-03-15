package com.alinesno.infra.smart.assistant.service.impl;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
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
        String modelName = dto.getModel() ;

        MessageTaskInfo taskInfo = new MessageTaskInfo();
        taskInfo.setChannelId(dto.getTestChannelId());
        long workflowId = IdUtil.getSnowflakeNextId() ;

        IndustryRoleEntity role = new IndustryRoleEntity();
        role.setRoleAvatar(PublishConst.EMPTY);
        role.setRoleName(PublishConst.ANONYMOUS_USERNAME);
        role.setId(PublishConst.ANONYMOUS_USER);

        if(modelType.equals(ModelTypeEnums.LARGE_LANGUAGE_MODEL.getCode())){  // 大语言模型

            LlmConfig llmConfig = new LlmConfig();
            llmConfig.setEndpoint(url);
            llmConfig.setApiKey(apiKey) ;
            llmConfig.setModel(modelName);

            Llm llm = llmAdapterService.getLlm(modelProvider , llmConfig);
            String prompt = "你是谁." ;

            llm.chatStream(prompt, (context, response) -> {
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
                            role ,
                            taskInfo ,
                            IdUtil.getSnowflakeNextId()) ;
                }

            });

        } else if(modelType.equals(ModelTypeEnums.RE_RANKING_MODEL.getCode())){  // 重排模型

        }else if(modelType.equals(ModelTypeEnums.SPEECH_RECOGNITION.getCode())){  // 语音识别

        }else if(modelType.equals(ModelTypeEnums.SPEECH_SYNTHESIS.getCode())){  // 语音合成

        }else if(modelType.equals(ModelTypeEnums.IMAGE_GENERATION.getCode())){ // 图片生成

        }

        return null;
    }
}