package com.alinesno.infra.smart.assistant.api;

import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class IndustryRoleDto {

    private Long id ;

    private String roleName; // 角色名称

    private String roleAvatar ; // 角色头像

    private Long industryCatalog ; // 所属行业

    private String responsibilities; // 角色职责描述

    private String skills; // 所需技能描述

    private String roleLevel; // 角色等级

    // 假设存在一个静态方法 fromEntity，用于从 IndustryRoleEntity 转换为 IndustryRoleDto
    public static IndustryRoleDto fromEntity(IndustryRoleEntity entity) {
        // 实现将 IndustryRoleEntity 转换为 IndustryRoleDto 的逻辑
        // 例如，如果存在相应的构造函数或转换方法，则在此处使用它们

        // 返回 IndustryRoleDto 对象
        IndustryRoleDto dto = new IndustryRoleDto();
        BeanUtils.copyProperties(entity , dto);

        return dto ;
    }
}