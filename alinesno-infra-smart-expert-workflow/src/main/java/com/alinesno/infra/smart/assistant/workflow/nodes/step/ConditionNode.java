// ConditionNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.step.compare.*;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.ConditionNodeData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 该类表示判断器节点，继承自 AbstractFlowNode 类。
 * 其功能是根据不同的条件执行不同的节点，实现工作流的分支逻辑。
 * 在工作流中，当需要根据某些条件来决定后续执行的节点时，会使用该节点。
 */
@Slf4j
@Data
@Scope("prototype")
@Service(FlowConst.FLOW_STEP_NODE + "condition")
@EqualsAndHashCode(callSuper = true)
public class ConditionNode extends AbstractFlowNode {

    static List<ConditionVerifier> conditionVerifiers = new ArrayList<>();

    static {
        conditionVerifiers.add(new GEConditionVerifier());
        conditionVerifiers.add(new GTConditionVerifier());
        conditionVerifiers.add(new ContainConditionVerifier());
        conditionVerifiers.add(new EqualConditionVerifier());
        conditionVerifiers.add(new LTConditionVerifier());
        conditionVerifiers.add(new LEConditionVerifier());
        conditionVerifiers.add(new LengthLEConditionVerifier());
        conditionVerifiers.add(new LengthLTConditionVerifier());
        conditionVerifiers.add(new LengthEqualConditionVerifier());
        conditionVerifiers.add(new LengthGEConditionVerifier());
        conditionVerifiers.add(new LengthGTConditionVerifier());
        conditionVerifiers.add(new IsNullConditionVerifier());
        conditionVerifiers.add(new IsNotNullConditionVerifier());
        conditionVerifiers.add(new NotContainConditionVerifier());
    }

    /**
     * 构造函数，初始化节点类型为 "condition"。
     */
    public ConditionNode() {
        setType("condition");
    }

    @Override
    protected CompletableFuture<Void> handleNode() {
        log.debug("ConditionNode:{}" , node.toString());
        ConditionNodeData nodeData = JSON.parseObject(JSON.toJSONString(node.getProperties()), ConditionNodeData.class);
        log.debug("node type = {} output = {}" , node.getType() , JSONUtil.toJsonPrettyStr(nodeData));

        Map<String , String> branchMap = new HashMap<>() ;

        boolean isHasBranch = false ;
        for (ConditionNodeData.BranchCondition branchCondition : nodeData.getBranchConditionList()) {

            List<ConditionNodeData.Condition> conditions = branchCondition.getConditions() ;

            if(!CollectionUtils.isEmpty(conditions)){
                ConditionNodeData.Condition conditionField = branchCondition.getConditions().get(0) ;
                List<Object> field = conditionField.getField() ;
                String nodeId = String.valueOf(field.get(0));
                String keyName = String.valueOf(field.get(1));

                Object fieldValue = workflowManage.getReferenceField(nodeId , keyName) ;
                log.debug("fieldValue = {}" , fieldValue);

                for (ConditionVerifier conditionVerifier : conditionVerifiers) {
                    if(conditionVerifier.isSupported(conditionField.getCompare())){
                        if(conditionVerifier.verify(fieldValue , conditionField.getValue())){
                            branchMap.put("branch_id" , branchCondition.getId()) ;
                            branchMap.put("branch_name" , branchCondition.getType()) ;
                            isHasBranch = true ;
                            break ;
                        }
                    }
                }

            }

            if(!isHasBranch){
                branchMap.put("branch_id" , branchCondition.getId()) ;
                branchMap.put("branch_name" , "ELSE") ;
            }
        }

        output.put(node.getStepName()+".branch_map" , branchMap);
        output.put(node.getStepName()+".branch_name" , branchMap.get("branch_name"));

        return CompletableFuture.completedFuture(null);
    }
}