package com.alinesno.infra.smart.im.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 收藏常用应用类型
 */
@Data
public class CollectItemDto {

    /**
     *  类型
     */
    @NotNull(message = "类型不能为空")
    private String type ;

    /**
     *  ID
     */
    @NotNull(message = "ID不能为空")
    private Long itemId ;

}
