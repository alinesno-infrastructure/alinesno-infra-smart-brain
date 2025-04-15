package com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.mapper.DataAnalysisPlanMapper;
import com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.service.IDataAnalysisPlanService;
import com.alinesno.infra.smart.scene.dto.TreeNodeDto;
import com.alinesno.infra.smart.scene.entity.DataAnalysisPlanEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据分析规划服务实现类
 */
@Slf4j
@Service
public class DataAnalysisPlanServiceImpl extends IBaseServiceImpl<DataAnalysisPlanEntity, DataAnalysisPlanMapper> implements IDataAnalysisPlanService {
    @Override
    public List<TreeNodeDto> getPlanTree(Long sceneId, Long analysisSceneId) {
        return null;
    }
}