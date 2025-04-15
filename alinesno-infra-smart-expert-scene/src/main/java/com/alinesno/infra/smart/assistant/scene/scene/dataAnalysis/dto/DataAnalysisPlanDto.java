package com.alinesno.infra.smart.assistant.scene.scene.dataAnalysis.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.scene.entity.DataAnalysisPlanEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 数据分析规划 DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DataAnalysisPlanDto extends BaseDto  {

    private String planName;
    private Integer planLevel;
    private Integer planSort;
    private String planRequire;
    private String planAdditionalRequire;
    private Long parentChapterId;
    private Boolean isLeaf;
    private Long sceneId;
    private Long dataAnalysisSceneId;
    private Long dataMinerEngineerId;
    private String content;
    private List<DataAnalysisPlanDto> subtitles;
    private static DataAnalysisPlanEntity convertToEntity(DataAnalysisPlanDto dto) {
        DataAnalysisPlanEntity entity = new DataAnalysisPlanEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}    