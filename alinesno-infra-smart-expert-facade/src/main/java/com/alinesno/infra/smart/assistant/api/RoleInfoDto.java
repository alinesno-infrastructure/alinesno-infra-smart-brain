package com.alinesno.infra.smart.assistant.api;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色基础信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RoleInfoDto extends BaseDto {

    @NotBlank(message = "角色头像不能为空")
    private String roleAvatar; // 角色头像

    @NotBlank(message = "角色类型不能为空")
    private String roleType;

    @Min(value = 1, message = "行业分类不能为空")
    private Long industryCatalog;

    @NotBlank(message = "角色名称不能为空")
    private String roleName; // 角色名称

    @NotBlank(message = "角色背景不能为空")
    private String responsibilities; // 角色背景

    @NotBlank(message = "执行类型不能为空")
    private String scriptType ;

    @NotBlank(message = "角色等级不能为空")
    private String roleLevel;

}
