package com.alinesno.infra.base.search.api;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 段落更新数据传输对象
 */
@Data
public class SegmentUpdateDto {

    @NotNull(message = "内容不能为空")
    private String content ;

    @NotNull(message = "id不能为空")
    private String id ;
}
