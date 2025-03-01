package com.alinesno.infra.smart.assistant.workflow.dto;

import com.alinesno.infra.common.facade.base.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class FlowDto extends BaseDto {
    private Long roleId;
    private WorkflowRequestDto flowGraphJson; // 工作流图JSON数据
    private int flowVersion; // 工作流版本(使用最新的版本号，当其它版本状态为不可用)
    private int lockVersion; // 乐观锁版本号
    private String publishStatus; // 发布状态

    private List<FlowNodeDto> nodes;

    /**
     * 创建一个空的FlowDto对象
     * @return
     */
    public static FlowDto empty() {
        return new FlowDto();
    }
}
