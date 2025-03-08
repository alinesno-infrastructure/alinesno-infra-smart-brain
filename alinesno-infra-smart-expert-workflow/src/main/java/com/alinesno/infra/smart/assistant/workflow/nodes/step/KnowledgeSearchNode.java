// KnowledgeSearchNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alinesno.infra.smart.assistant.role.context.AgentConstants;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 该类表示知识库检索节点，继承自 AbstractFlowNode 类。
 * 主要用于关联知识库，查找与用户提出的问题相关的分段内容。
 * 在工作流中，当需要从知识库中获取相关信息时，会使用该节点。
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "knowledge_search")
@EqualsAndHashCode(callSuper = true)
public class KnowledgeSearchNode extends AbstractFlowNode {

    /**
     * 构造函数，初始化节点类型为 "knowledge_search"。
     */
    public KnowledgeSearchNode() {
        setType("knowledge_search");
    }

    @SneakyThrows
    @Override
    protected void handleNode() {

        eventStepMessage("开始节点检索知识库" , AgentConstants.STEP_START) ;
//        List<DocumentVectorBean> content =  this.flowExpertService.searchChannelKnowledgeBase("Java测试技术" , getDatasetIds()) ;
        String content =  this.flowExpertService.searchKnowledgeContent("Java测试技术" , getDatasetIds()) ;

        FlowStepStatusDto stepDto = new FlowStepStatusDto();

        stepDto.setMessage("任务进行中...");
        stepDto.setStepId(node.getId());
        stepDto.setStatus(AgentConstants.STEP_PROCESS);
        stepDto.setFlowChatText(content);
        stepDto.setPrint(node.isPrint());

        taskInfo.setFlowStep(stepDto);

        streamMessagePublisher.doStuffAndPublishAnEvent(null,
                role,
                taskInfo,
                taskInfo.getTraceBusId());

        eventStepMessage("节点检索知识库" , AgentConstants.STEP_FINISH) ;

    }

    private String getDatasetIds(){
        log.debug("getDatasetIds = {}" , node.getProperties());
        return "" ;
    }
}