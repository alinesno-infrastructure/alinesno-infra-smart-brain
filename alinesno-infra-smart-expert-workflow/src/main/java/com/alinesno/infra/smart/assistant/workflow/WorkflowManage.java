package com.alinesno.infra.smart.assistant.workflow;

import com.alinesno.infra.smart.assistant.workflow.dto.FlowNodeDto;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * WorkflowManage类
 */
@NoArgsConstructor
public class WorkflowManage {

    private FlowNodeDto flowNode ;

    private List<FlowNodeDto> flowNodes ;

    @Setter
    private Map<String, Object> output ;

    public WorkflowManage(FlowNodeDto flowNode, List<FlowNodeDto> flowNodes) {
        this.flowNode = flowNode;
        this.flowNodes = flowNodes;
    }

    /**
     * 获取引用字段
     * @return
     */
    public Object getReferenceField(String nodeId, String keyName){
        FlowNodeDto flowNode = getByNodeId(nodeId);

        if(flowNode != null){
            String key = flowNode.getStepName() + "." + keyName ;
            return output.get(key);
        }

        return null ;
    }

    private FlowNodeDto getByNodeId(String nodeId) {

        for (FlowNodeDto flowNodeDto : flowNodes) {
            if(flowNodeDto.getId().equals(nodeId)){
                return flowNodeDto ;
            }
        }

        return null;
    }

}
