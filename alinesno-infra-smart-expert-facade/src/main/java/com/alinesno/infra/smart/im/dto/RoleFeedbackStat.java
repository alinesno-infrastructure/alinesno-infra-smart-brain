package com.alinesno.infra.smart.im.dto;

import lombok.Data;

/**
 * RoleFeedbackStatDTO
 */
@Data
public class RoleFeedbackStat {
    private Long roleId;
    private Integer likeCount;
    private Integer dislikeCount;
}