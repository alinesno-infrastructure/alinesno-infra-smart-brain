package com.alinesno.infra.smart.deepsearch.dto;

import com.alinesno.infra.smart.deepsearch.enums.StepActionEnums;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DeepSearch返回流程节点
 */
@NoArgsConstructor
@Data
public class DeepSearchFlow implements Serializable {

    private String flowId ; // 流程ID
    private Plan plan; // 规划
    private List<Step> steps; // 规划步骤列表
    private Output output ; // 输出

    public DeepSearchFlow(String flowId) {
        this.flowId = flowId;
    }

    /**
     * 规划
     */
    @Data
    @NoArgsConstructor
    public static class Plan {
        private String name ; // 规划步骤名称
        private String description ;  // 规划步骤描述
        private List<StepAction> actions = new ArrayList<>();  // 规划步骤动作列表

        public Plan(String name) {
            this.name = name;
        }

        public void addStepAction(StepAction stepActionDto) {
            // 通过ActionId查询到该动作，如果是同一个则删除当前的actions，然后添加新的action
            for (StepAction action : actions) {
                if (action.getActionId().equals(stepActionDto.getActionId())) {
                    actions.remove(action);
                    break;
                }
            }
            actions.add(stepActionDto);
        }
    }

    /**
     * 规划步骤
     */
    @Data
    @NoArgsConstructor
    public static class Step {
        private String id ;
        private String name ; // 规划步骤名称
        private String description ;  // 规划步骤描述
        private List<StepAction> actions = new ArrayList<>() ;  // 规划步骤动作列表

        public void addAction(StepAction stepActionDto) {
            // 通过ActionId查询到该动作，如果是同一个则删除当前的actions，然后添加新的action
            for (StepAction action : actions) {
                if (action.getActionId().equals(stepActionDto.getActionId())) {
                    actions.remove(action);
                    break;
                }
            }
            actions.add(stepActionDto);
        }
    }

    /**
     * 输出
     */
    @Data
    @NoArgsConstructor
    public static class Output {
        private String name ; // 规划步骤名称
        private String description ; //  规划步骤描述
        private List<StepAction> actions ; // 规划步骤动作列表
        private List<FileAttachmentDto> attachments ; // 附件列表
    }

    /**
     * 附件
     */
    @Data
    @NoArgsConstructor
    public static class FileAttachmentDto {
        private String id; // 文件ID
        private String name; // 文件名
        private String desc ; // 文件描述
        private String storageId ;  // 存储ID
        private String type ; // 文件类型
        private String icon ; // font-awesome图标
    }

    /**
     * 步骤动作
     */
    @Data
    @NoArgsConstructor
    public static class StepAction {
        private String actionId ; // 动作ID
        private String icon ; // 动作图标
        private String actionType ; // 动作类型
        private String actionName ; // 动作名称
        private String think ; // 思考
        private String result ; // 结果
        private boolean status; // 状态(done成功|fail失败|doing进行中)
        private String errorMsg ; // 错误信息

        public void setActionType(String actionType) {
            StepActionEnums step = StepActionEnums.getByActionType(actionType);

            this.actionType = actionType;
            this.actionName = step.getDescription();
            this.icon = step.getFontAwesomeIcon();
        }
    }

}
