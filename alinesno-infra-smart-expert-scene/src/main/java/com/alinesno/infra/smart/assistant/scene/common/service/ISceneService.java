package com.alinesno.infra.smart.assistant.scene.common.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.scene.core.entity.SceneEntity;
import com.alinesno.infra.smart.scene.dto.SceneDto;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface ISceneService extends IBaseService<SceneEntity> {

    /**
     * 保存一个SceneEntity对象
     * @param sceneDto
     * @return
     */
    SceneEntity saveScene(SceneDto sceneDto);

    /**
     * 生成markdown内容
     * @param sceneId
     * @return
     */
    String genMarkdownContent(long sceneId);

}
