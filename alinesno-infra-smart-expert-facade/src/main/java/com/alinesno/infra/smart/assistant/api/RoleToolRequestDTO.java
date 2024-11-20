package com.alinesno.infra.smart.assistant.api;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleToolRequestDTO extends BaseDto {

    @Min(value = 1, message = "角色ID不能为空")
    private long roleId;

    @NotBlank(message = "角色背景不能为空")
    private String backstory;

    @NotBlank(message = "欢迎语不能为空")
    private String greeting;

    @Size(max = 10 , min=1, message = "工具列表不能为空")
    @NotNull(message = "工具列表不能为空")
    private List<Long> tools;

}