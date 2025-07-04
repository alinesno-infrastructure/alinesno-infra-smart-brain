package com.alinesno.infra.smart.im.dto;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 行业角色组织数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IndustryRoleOrgDto extends IndustryRoleEntity {

    private String orgName ;

}
