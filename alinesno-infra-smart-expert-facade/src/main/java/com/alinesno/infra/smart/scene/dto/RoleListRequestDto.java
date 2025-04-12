package com.alinesno.infra.smart.scene.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RoleListRequestDto类用于封装角色列表请求相关的数据
 * 它包含了智能助手类型ID、场景ID、场景类型代码和智能助手类型代码等信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RoleListRequestDto extends BaseDto {

    /**
     * 场景ID，用于标识特定的使用场景
     */
    private Long sceneId ;

    /**
     * 场景类型代码，用于表示场景的类型
     */
    private String sceneTypeCode ;

    /**
     * 智能助手类型代码，用于表示智能助手的类型
     */
    private String agentTypeCode ;

    /**
     * 智能助手类型ID，用于标识不同的智能助手类型
     */
    private Long agentTypeId ;
}