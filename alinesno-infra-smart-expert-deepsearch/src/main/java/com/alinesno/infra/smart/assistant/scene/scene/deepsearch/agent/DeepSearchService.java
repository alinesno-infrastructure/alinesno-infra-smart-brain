package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.ReActExpertService;
import com.alinesno.infra.smart.assistant.role.tools.ReActServiceTool;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean.DeepTaskBean;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.StepEventUtil;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 深度搜索服务，执行DeepSearch任务 <br/>
 * 1. 获取到规划列表并临时保存到任务清单里面
 * 2. 根据任务清单并行往下执行
 * 3. 执行任务
 * 4. 更新任务清单，再往下执行
 * 5. 循环执行
 * 6. 输出总结（文档格式、文档格式、链接格式）
 */
@Scope("prototype")
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_DEEP_SEARCH)
public class DeepSearchService extends ReActExpertService {

    private final DeepSearchFlow deepSearchFlow = new DeepSearchFlow(IdUtil.getSnowflakeNextIdStr()) ;  // 执行步骤统计

    @Autowired
    private StepEventUtil stepEventUtil ;

    @Autowired
    private PlannerHandler plannerHandler ;

    @Autowired
    private WorkerHandler workerHandler ;

    @Autowired
    private OutputHandler deepSearchOutputHandler ;

    @Autowired
    private ReActServiceTool reActServiceTool  ;

    @Override
    protected String handleRole(IndustryRoleEntity role, MessageEntity workflowMessage, MessageTaskInfo taskInfo) {

        String goal = clearMessage(taskInfo.getText()) ; // 目标

        List<DeepTaskBean> taskBeans ;

        // 解析出公共的知识库
        List<DocumentVectorBean> datasetKnowledgeDocumentList = searchChannelKnowledgeBase(goal , role.getKnowledgeBaseIds()) ;
        reActServiceTool.handleReferenceArticle(taskInfo , datasetKnowledgeDocumentList) ;

        HistoriesPrompt historyPrompt = new HistoriesPrompt();
        historyPrompt.setMaxAttachedMessageCount(maxHistory);
        historyPrompt.setHistoryMessageTruncateEnable(false);

        String oneChatId = IdUtil.getSnowflakeNextIdStr() ;
        String datasetKnowledgeDocument = reActServiceTool.getDatasetKnowledgeDocument(goal , workflowMessage, taskInfo, datasetKnowledgeDocumentList, oneChatId, historyPrompt);

        initWorkerHandlerParams(role.getOrgId() , datasetKnowledgeDocument , goal) ;

        Llm llm = getLlm(role) ;
        StringBuilder answerOutput = new StringBuilder(); // 工具执行结果

        DeepSearchFlow.Plan plan = new DeepSearchFlow.Plan("执行规划思考") ;
        deepSearchFlow.setPlan(plan);
        stepEventUtil.eventStepMessage(deepSearchFlow , getRole() , getTaskInfo());

        CompletableFuture<String> future = plannerHandler.getAiChatResultAsync(llm, historyPrompt , taskInfo , oneChatId , goal) ;

        String output = null;
        try {
            output = future.get();

            taskBeans = plannerHandler.getAiChatPlanAsync(llm, historyPrompt , taskInfo , oneChatId , output) ;
            workerHandler.setTaskStepMap(taskBeans);

            List<DeepSearchFlow.Step> steps = new ArrayList<>();
            deepSearchFlow.setSteps(steps);

            for(DeepTaskBean task : taskBeans){
                log.debug("task = {}" , task);

                DeepSearchFlow.Step step = new DeepSearchFlow.Step() ;
                step.setName(task.getTaskName());
                step.setDescription(task.getTaskDesc());
                step.setId(task.getId());

                workerHandler.setLlm(llm);
                workerHandler.setStep(step);
                String taskOutput =  workerHandler.executeTask(task) ; // 执行任务步骤

                answerOutput.append(taskOutput) ;
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        log.debug("output = {}" , output);

        // 处理完成之后，进行output输出处理
        DeepSearchFlow.Output deepSearchOutput = new DeepSearchFlow.Output() ;
        deepSearchOutputHandler.handleOutput(llm , answerOutput , deepSearchOutput , historyPrompt , goal) ;

        return StringUtils.hasLength(answerOutput.toString())? AgentConstants.ChatText.CHAT_FINISH : AgentConstants.ChatText.CHAT_NO_ANSWER;
    }

    /**
     * 初始化参数
     */
    private void initWorkerHandlerParams(Long orgId, String datasetKnowledgeDocument, String goal) {

        List<ToolDto> tools = getToolService().getByToolIds(getRole().getSelectionToolsData() , orgId) ;

        // 初始化规划服务参数
        plannerHandler.setRole(getRole()) ;
        plannerHandler.setStreamMessagePublisher(getStreamMessagePublisher()) ;
        plannerHandler.setTaskInfo(getTaskInfo()) ;
        plannerHandler.setStepEventUtil(stepEventUtil) ;
        plannerHandler.setDeepSearchFlow(deepSearchFlow) ;
        plannerHandler.setToolService(getToolService()) ;
        plannerHandler.setSecretKey(getSecretKey()) ;
        plannerHandler.setTools(tools) ;
        plannerHandler.setDatasetKnowledgeDocument(datasetKnowledgeDocument) ;
        plannerHandler.setGoal(goal) ;

        // 初始化执行服务参数
        workerHandler.setRole(getRole()) ;
        workerHandler.setStreamMessagePublisher(getStreamMessagePublisher()) ;
        workerHandler.setTaskInfo(getTaskInfo()) ;
        workerHandler.setStepEventUtil(stepEventUtil) ;
        workerHandler.setDeepSearchFlow(deepSearchFlow) ;
        workerHandler.setToolService(getToolService()) ;
        workerHandler.setSecretKey(getSecretKey()) ;
        workerHandler.setMaxLoopCount(getMaxLoop());
        workerHandler.setTools(tools) ;
        workerHandler.setDatasetKnowledgeDocument(datasetKnowledgeDocument) ;
        workerHandler.setGoal(goal) ;

        // outputHandler
        deepSearchOutputHandler.setRole(getRole()) ;
        deepSearchOutputHandler.setStreamMessagePublisher(getStreamMessagePublisher()) ;
        deepSearchOutputHandler.setTaskInfo(getTaskInfo()) ;
        deepSearchOutputHandler.setStepEventUtil(stepEventUtil) ;
        deepSearchOutputHandler.setDeepSearchFlow(deepSearchFlow) ;
        deepSearchOutputHandler.setToolService(getToolService()) ;
        deepSearchOutputHandler.setSecretKey(getSecretKey()) ;
        deepSearchOutputHandler.setTools(tools) ;
        deepSearchOutputHandler.setDatasetKnowledgeDocument(datasetKnowledgeDocument) ;
        deepSearchOutputHandler.setGoal(goal) ;

    }

}

