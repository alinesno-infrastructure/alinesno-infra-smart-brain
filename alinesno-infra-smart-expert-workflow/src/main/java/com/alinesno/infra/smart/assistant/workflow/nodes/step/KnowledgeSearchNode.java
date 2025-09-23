package com.alinesno.infra.smart.assistant.workflow.nodes.step;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.workflow.constants.FlowConst;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.step.KnowledgeSearchNodeData;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.ParserDataBean;
import com.alinesno.infra.smart.im.enums.FileType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Asserts;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    @Override
    protected CompletableFuture<Void> handleNode() {
        try {
            eventStepMessage("开始节点检索知识库", AgentConstants.STEP_START);

            KnowledgeSearchNodeData nodeData = getKnowledgeSearchNodeData() ;
            Asserts.notNull(nodeData , "知识库配置为空不能为空");

            String nodeId = String.valueOf(nodeData.getQuestionReference().get(0));
            String keyName = String.valueOf(nodeData.getQuestionReference().get(1));

            Object fieldValue = workflowManage.getReferenceField(nodeId , keyName) ;
            log.debug("fieldValue = {}" , fieldValue);

            List<Long> datasetList =  nodeData.getDatasetIdList() ;

            String query = fieldValue.toString();

            log.debug("KnowledgeSearchNode query = {}, datasetIds = {}", query, datasetList);


//            public List<DocumentVectorBean> searchWorkflowKnowledgeContent(String query,
//                    List<Long> datasetList ,
//            int quoteLimit ,
//            String searchType ,
//            int topK ,
//            String minScore) {
//
//            }

            // 执行检索
            KnowledgeSearchNodeData.DatasetSetting datasetSetting = nodeData.getDatasetSetting() ;
            List<DocumentVectorBean> paragraphList = this.flowExpertService.searchWorkflowKnowledgeContent(query,
                    datasetList ,
                    datasetSetting.getQuoteLimit() ,
                    datasetSetting.getSearchType() ,
                    datasetSetting.getTopK() ,
                    datasetSetting.getMinRelevance()
            );

            StringBuilder content = new StringBuilder();
            if(!CollectionUtils.isEmpty(paragraphList)){
                List<ParserDataBean> datasetMap = new ArrayList<>();

                for(DocumentVectorBean bean : paragraphList){
                    content.append(bean.getDocument_content()).append("\n");
                    datasetMap.add(new ParserDataBean(
                            bean.getDocument_title() + "#" +  bean.getId() ,
                            datasetMap.size() + 1 ,
                            FileType.DOCX.getCode() ,
                            bean.getDocument_content()
                    )) ;
                }

                taskInfo.setDatasetMap(datasetMap);
            }


            // 构造节点状态并发布
            FlowStepStatusDto stepDto = new FlowStepStatusDto();
            stepDto.setMessage("任务进行中...");
            stepDto.setStepId(node.getId());
            stepDto.setStatus(AgentConstants.STEP_PROCESS);
            stepDto.setFlowChatText(content.toString());
            stepDto.setPrint(node.isPrint());

            taskInfo.setFlowStep(stepDto);

            streamMessagePublisher.doStuffAndPublishAnEvent(
                    null,
                    role,
                    taskInfo,
                    taskInfo.getFlowChatId()
            );

            eventStepMessage("节点检索知识库", AgentConstants.STEP_FINISH);

            // 将检索结果写入输出上下文，保留原有几个键以兼容下游处理
            output.put(node.getStepName() + ".paragraph_list", paragraphList);
            output.put(node.getStepName() + ".data", content);

//            output.put(node.getStepName() + ".is_hit_handling_method_list", content);
//            output.put(node.getStepName() + ".directly_return", content);

            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            log.error("KnowledgeSearchNode 执行异常: {}", ex.getMessage(), ex);
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(ex);
            return failed;
        }
    }

//    /**
//     * 从节点属性中获取检索使用的 dataset ids（逗号分隔），若不存在则返回空字符串。
//     */
//    private String getDatasetIds() {
//        try {
//            Object v = node.getProperties().get("dataset_ids");
//            String ids = v == null ? "" : String.valueOf(v);
//            log.debug("getDatasetIds = {}", ids);
//            return ids;
//        } catch (Exception ex) {
//            log.warn("getDatasetIds 解析异常，返回空: {}", ex.getMessage());
//            return "";
//        }
//    }

//    /**
//     * 从节点属性中获取检索使用的 query（优先级：node 属性中的 query -> node 名称 -> 默认值）。
//     */
//    private String getQuery() {
//        try {
//            Object v = node.getProperties().get("query");
//            if (v != null && !String.valueOf(v).trim().isEmpty()) {
//                return String.valueOf(v);
//            }
//            // 如果没有在属性中指定 query，尝试使用节点 stepName 或回退到默认值
//            String stepName = node.getStepName();
//            if (stepName != null && !stepName.trim().isEmpty()) {
//                return stepName;
//            }
//        } catch (Exception ex) {
//            log.warn("getQuery 解析异常，使用默认值: {}", ex.getMessage());
//        }
//        return "Java测试技术";
//    }

    private KnowledgeSearchNodeData getKnowledgeSearchNodeData(){
        String nodeDataJson =  node.getProperties().get("node_data")+"" ;
        return StringUtils.isNotEmpty(nodeDataJson)? JSONObject.parseObject(nodeDataJson , KnowledgeSearchNodeData.class):null;
    }
}