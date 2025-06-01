package com.alinesno.infra.smart.im.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatSendMessageDto extends BaseDto {

    private String message;
    private List<Long> users = new ArrayList<>() ; // Agent用户ID列表
    private List<Long> businessIds = new ArrayList<>() ;  // 业务ID列表
    private List<Long> fileIds = new ArrayList<>(); // 文件ID列表
    private Long channelId;
    private String channelStreamId ;  // 频道流ID

    private Long accountId ; // 操作账号ID
    private Long accountOrgId ; // 操作账户所属组织Id
    private String accountName; // 操作账号名称
    private String accountIcon ; // 操作账号头像
    private String accountEmail ; // 操作账号邮箱

    private String type;

    /**
     * 转换为权限实体
     * @return
     */
    public MessageEntity toPowerMessageEntity() {
        MessageEntity messageEntity = new MessageEntity();

        messageEntity.setOrgId(this.getOrgId());
        messageEntity.setDepartmentId(this.getDepartmentId());
        messageEntity.setOperatorId(this.getOperatorId());

        return messageEntity ;
    }
}
