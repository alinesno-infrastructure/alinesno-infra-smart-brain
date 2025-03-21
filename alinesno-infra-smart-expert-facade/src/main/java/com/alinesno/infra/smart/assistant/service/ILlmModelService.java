package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.api.TestLlmModelDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;

import java.util.Map;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface ILlmModelService extends IBaseService<LlmModelEntity> {

    /**
     * 模型测试结果
     * @param dto
     */
    String testLlmModel(TestLlmModelDto dto);

    /**
     * 获取语音模型声音
     *
     * @param modelId
     * @return
     */
    Map<String, Object> getVoiceModelSpeech(String modelId);

    /**
     * 语音合成，并返回生成的文件
     * @param dto
     * @return
     */
    String speechSynthesisFile(IndustryRoleEntity role , ChatMessageDto dto);

    /**
     * 语音识别
     * @param role
     * @param data
     * @return 识别的内容
     */
    String speechRecognitionFile(IndustryRoleEntity role, String data);
}