package com.alinesno.infra.smart.deepsearch.dto;

import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.scene.dto.SceneDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 深度搜索场景数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeepSearchSceneDto extends SceneDto {

    /**
     * 场景ID
     * 用于唯一标识一个场景
     */
    private long sceneId;

    /**
     * 搜索规划者ID
     */
    private Long searchPlannerEngineer ;

    /**
     * 搜索规划者ID列表
     */
    private List<IndustryRoleDto> searchPlannerEngineers ;

    /**
     * 执行规划者ID
     */
    private Long searchExecutorEngineer ;

    /**
     * 执行规划者ID列表
     */
    private List<IndustryRoleDto> searchExecutorEngineers ;

}
