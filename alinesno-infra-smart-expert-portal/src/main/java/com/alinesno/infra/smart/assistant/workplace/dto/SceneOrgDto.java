package com.alinesno.infra.smart.assistant.workplace.dto;

import com.alinesno.infra.smart.scene.entity.SceneEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SceneOrgDto类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SceneOrgDto extends SceneEntity {
    private String orgName;
}
