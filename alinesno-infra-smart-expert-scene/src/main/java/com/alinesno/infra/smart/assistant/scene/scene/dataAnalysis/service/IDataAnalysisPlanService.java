package com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.DataAnalysisPlanEntity;

import java.util.List;

/**
 * 数据分析规划服务接口
 */
public interface IDataAnalysisPlanService extends IBaseService<DataAnalysisPlanEntity> {

    /**
     * 获取章节树
     * @param sceneId
     * @param analysisSceneId
     * @return
     */
    List<TreeNodeDto> getPlanTree(Long sceneId , Long analysisSceneId);
}