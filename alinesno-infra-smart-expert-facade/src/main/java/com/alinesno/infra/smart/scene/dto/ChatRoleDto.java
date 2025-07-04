package com.alinesno.infra.smart.scene.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;

/**
 * 与角色交互的数据传输对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChatRoleDto extends BaseDto {

    @NotNull(message = "sceneId不能为空")
    private Long sceneId ;  // 场景ID

    @NotNull(message = "任务Id不能为空")
    private Long taskId ;

    @NotBlank(message = "channelStreamId不能为空")
    private String channelStreamId ;

    @NotBlank(message = "message不能为空")
    private String message ; // 消息内容

    @Min(value = 1, message = "channelId不能为空")
    private long roleId ; // 角色ID

    public MessageTaskInfo toPowerMessageTaskInfo() {
        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setOrgId(this.getOrgId());
        taskInfo.setDepartmentId(this.getDepartmentId());
        taskInfo.setOperatorId(this.getOperatorId());

        return taskInfo ;
    }
}
