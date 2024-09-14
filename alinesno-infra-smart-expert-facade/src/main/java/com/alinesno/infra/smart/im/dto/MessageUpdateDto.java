package com.alinesno.infra.smart.im.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 聊天消息更新DTO
 */
@ToString
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageUpdateDto extends BaseDto {

    private Object content ; // 消息内容
    private String code; // 是否为代码
    private long businessId ; // 业务ID

}

