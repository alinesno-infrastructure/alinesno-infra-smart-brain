package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.api.TestLlmModelDto;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;

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

}