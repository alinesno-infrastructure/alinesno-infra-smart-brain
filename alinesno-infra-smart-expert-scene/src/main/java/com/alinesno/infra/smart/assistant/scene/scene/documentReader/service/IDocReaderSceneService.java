package com.alinesno.infra.smart.assistant.scene.scene.documentReader.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.scene.documentReader.dto.DocReaderInitDto;
import com.alinesno.infra.smart.scene.entity.DocReaderSceneEntity;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IDocReaderSceneService extends IBaseService<DocReaderSceneEntity> {

    /**
     * 根据场景ID获取场景信息
     * @param sceneId
     * @return
     */
    DocReaderSceneEntity getBySceneId(long sceneId);

    /**
     * 初始化场景代理
     * @param dto
     */
    void initAgents(DocReaderInitDto dto);

}
