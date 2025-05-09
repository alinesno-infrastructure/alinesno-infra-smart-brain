package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent;

import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 任务输出结果处理
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Component
public class OutputHandler extends BaseHandler {

    /**
     * 处理任务输出结果
     * @param answerOutput
     * @param deepSearchOutput
     */
    public void handleOutput(StringBuilder answerOutput, DeepSearchFlow.Output deepSearchOutput) {
        getDeepSearchFlow().setOutput(deepSearchOutput);
        getStepEventUtil().eventStepMessage(getDeepSearchFlow(), getRole() , getTaskInfo());
    }
}
