package com.alinesno.infra.smart.assistant.adapter.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 积分明细分页查询参数DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PointsDetailQueryDto extends BaseDto {

    /**
     * 组织ID
     */
    private Long orgId;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 页码，默认第1页
     */
    private Integer pageNum = 1;

    /**
     * 每页条数，默认10条
     */
    private Integer pageSize = 10;
}
    