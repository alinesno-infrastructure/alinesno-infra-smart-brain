package com.alinesno.infra.smart.assistant.scene.scene.examPaper.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考生添加DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExamineeAddDTO extends BaseDto  {

    @NotBlank(message = "考生编号不能为空")
    private String examineeId;
    
    @NotBlank(message = "考生姓名不能为空")
    private String name;

    @NotNull(message = "所属场景不能为空")
    private Long sceneId;

    @NotNull(message = "分组ID不能为空")
    private Long groupId ;
    
    private Integer gender = 0;
    
    private String idCardNumber;
    
    private String phone;
    
    private String email;
    
    private String photoUrl;
    
    private String organization;
    
    private String remark;
}