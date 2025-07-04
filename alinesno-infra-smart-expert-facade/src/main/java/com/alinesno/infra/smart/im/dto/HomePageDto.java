package com.alinesno.infra.smart.im.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 首页数据传输对象
 */
@Data
public class HomePageDto {

    @NotBlank(message = "首页数据不能为空")
    private String homePage ;

    @NotBlank(message = "设置类型不能为空")
    private String type ; // 设置类型(默认default|自定义settings)

}
