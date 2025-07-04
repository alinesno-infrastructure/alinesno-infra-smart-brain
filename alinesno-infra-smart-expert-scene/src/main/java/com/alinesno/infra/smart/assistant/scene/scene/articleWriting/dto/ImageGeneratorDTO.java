package com.alinesno.infra.smart.assistant.scene.scene.articleWriting.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 重新写文章
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageGeneratorDTO extends BaseDto {

    @NotNull(message = "场景ID不能为空")
    private Long sceneId; // 场景ID

    @NotNull(message = "文章ID不能为空")
    private Long articleId; // 文章ID

    private Integer count = 2; // 图片生成数量

    @NotBlank(message = "消息内容不能为空")
    private String prompt ; // 消息

    public MessageTaskInfo toPowerMessageTaskInfo() {
        MessageTaskInfo taskInfo = new MessageTaskInfo() ;

        taskInfo.setOrgId(this.getOrgId());
        taskInfo.setDepartmentId(this.getDepartmentId());
        taskInfo.setOperatorId(this.getOperatorId());

        return taskInfo ;
    }
}
