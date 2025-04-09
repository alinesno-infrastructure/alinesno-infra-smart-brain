package com.alinesno.infra.smart.scene.service;

import com.alinesno.infra.common.facade.datascope.PermissionQuery;
import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.dto.SceneDto;
import com.alinesno.infra.smart.scene.dto.SceneResponseDto;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

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

    /**
     * 分页查询场景列表
     * @param query
     * @param pageNow
     * @param pageSize
     * @return
     */
    IPage<SceneResponseDto> sceneListByPage(PermissionQuery query, int pageNow, int pageSize);
}
