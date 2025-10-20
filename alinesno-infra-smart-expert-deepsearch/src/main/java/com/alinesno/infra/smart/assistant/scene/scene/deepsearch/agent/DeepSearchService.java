package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.config.ContextEngineeringData;
import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.ReActExpertService;
import com.alinesno.infra.smart.assistant.role.tools.ReActServiceTool;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean.DeepTaskBean;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events.record.DeepSearchTaskRecordManager;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.DeepsearchSummaryMessageTool;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.StepEventUtil;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.scene.entity.DeepSearchTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    private DeepSearchFlow deepSearchFlow ;

    @Autowired
    private StepEventUtil stepEventUtil ;

    @Autowired
    private PlannerHandler plannerHandler ;

    @Autowired
    private WorkerHandler workerHandler ;

    @Autowired
    private SummaryHandler deepSearchOutputHandler ;

    @Autowired
    private ReActServiceTool reActServiceTool  ;

    @Autowired
    private IDeepSearchTaskService taskService ;

    @Autowired
    private DeepsearchSummaryMessageTool summaryMessageTool ;

    @Autowired
    private DeepSearchTaskRecordManager recordManager;

    @Autowired
    @Qualifier("chatThreadPool")
    protected ThreadPoolTaskExecutor chatThreadPool;

    @Override
    protected CompletableFuture<String> handleRole(IndustryRoleEntity role,
                                                   MessageEntity workflowMessage,
                                                   MessageTaskInfo taskInfo) {

        String goal = clearMessage(taskInfo.getText()); // 目标

        deepSearchFlow = new DeepSearchFlow(IdUtil.getSnowflakeNextIdStr()) ;  // 执行步骤统计
        Long sceneTaskId = taskInfo.getSceneTaskId() ;  // 当前任务id
        Long sceneId = taskInfo.getSceneId() ;  // 当前场景id

        deepSearchFlow.setSceneId(sceneId);
        deepSearchFlow.setTaskId(sceneTaskId);

        // 深度任务
        DeepSearchTaskEntity deepSearchTask = taskService.getById(sceneTaskId) ;

        // 1) 创建 TASK 元记录
        recordManager.createTaskMeta(sceneTaskId, sceneId, goal, deepSearchFlow.getFlowId());

        // 1. 解析公共知识库（同步操作，假设耗时短；若耗时长可改为异步）
        List<DocumentVectorBean> datasetKnowledgeDocumentList = searchChannelKnowledgeBase(goal, role.getKnowledgeBaseIds());
        reActServiceTool.handleReferenceArticle(taskInfo, datasetKnowledgeDocumentList);

        IndustryRoleDto industryRoleDto = IndustryRoleDto.fromEntity(getRole()) ;

        // 上传配置
        UploadData uploadData = industryRoleDto.getUploadData() ;
        reActServiceTool.setUploadData(uploadData);

        // 上下文工程
        boolean contextEngineeringDataEnable = industryRoleDto.isContextEngineeringEnable() ;
        ContextEngineeringData contextEngineeringData = industryRoleDto.getContextEngineeringData() ;

        if(contextEngineeringDataEnable){
            reActServiceTool.setContextEngineeringData(contextEngineeringData);
        }

        HistoriesPrompt historyPrompt = new HistoriesPrompt();
        historyPrompt.setMaxAttachedMessageCount(maxHistory);
        historyPrompt.setHistoryMessageTruncateEnable(false);

        String oneChatId = IdUtil.getSnowflakeNextIdStr();
        String datasetKnowledgeDocument = reActServiceTool.getDatasetKnowledgeDocument(goal,
                workflowMessage,
                taskInfo,
                datasetKnowledgeDocumentList,
                oneChatId,
                historyPrompt);

        // 初始化 WorkerHandler 参数
        initWorkerHandlerParams(role.getOrgId(),
                datasetKnowledgeDocument,
                goal ,
                reActServiceTool ,
                summaryMessageTool ,
                uploadData ,
                contextEngineeringData ,
                deepSearchTask ,
                recordManager);

        Llm llm = getLlm(role);
        DeepSearchFlow.Plan plan = new DeepSearchFlow.Plan("执行规划思考");
        deepSearchFlow.setPlan(plan);
        stepEventUtil.eventStepMessage(deepSearchFlow, getRole(), getTaskInfo());

        // 添加任务计划
        recordManager.addTaskPlan(sceneTaskId, sceneId, goal, deepSearchFlow.getFlowId() , plan);

        // 2. 异步链式调用：规划生成 -> 任务解析 -> 顺序执行每个任务 -> 汇总输出
        return plannerHandler.getAiChatResultAsync(llm, historyPrompt, taskInfo, oneChatId, goal)
                .thenCompose(planningOutput -> {
                    log.debug("planning prompt output = {}", planningOutput);

                    return plannerHandler.getAiChatPlanAsync(llm, historyPrompt, taskInfo, oneChatId, planningOutput);
                })
                .thenCompose(taskBeans -> {

                    // 保存plan的任务
                    recordManager.addTaskPlanStep(sceneTaskId, sceneId, goal, deepSearchFlow.getFlowId() , plan);

                    workerHandler.setTaskStepMap(taskBeans);
                    deepSearchFlow.setSteps(new ArrayList<>()); // 初始化步骤列表

                    // 更新任务计划
                    deepSearchTask.setTaskPlanJson(JSONObject.toJSONString(taskBeans));
                    taskService.updateById(deepSearchTask);

                    // 初始一个已完成的 CompletableFuture<StringBuilder>
                    CompletableFuture<StringBuilder> seq = CompletableFuture.completedFuture(new StringBuilder());

                    // 串行地将每个任务链到 seq 上：上一个完成后再执行下一个
                    for (DeepTaskBean task : taskBeans) {
                        seq = seq.thenCompose(sb ->
                                CompletableFuture.supplyAsync(() -> {
                                    // 在线程池中同步执行单个任务
                                    log.debug("执行任务: {}", task);
                                    DeepSearchFlow.Step step = new DeepSearchFlow.Step();
                                    step.setName(task.getTaskName());
                                    step.setDescription(task.getTaskDesc());
                                    step.setId(task.getId());

                                    recordManager.addTaskWorkerStep(sceneTaskId, sceneId, goal, deepSearchFlow.getFlowId() , step);

                                    workerHandler.setLlm(llm);
                                    workerHandler.setStep(step);
                                    String taskOutput = workerHandler.executeTask(task); // 同步执行，运行在线程池中

                                    recordManager.addTaskWorkerStepActionAsync(sceneTaskId, sceneId, goal, deepSearchFlow.getFlowId() , step);

                                    // 合并结果
                                    sb.append(taskOutput);
                                    return sb;
                                }, chatThreadPool)
                        );
                    }

                    // 最终返回一个包含所有任务输出的 CompletableFuture<StringBuilder>
                    return seq;
                })
                .thenCompose(answerOutput -> {
                    // 5. 所有任务执行完成后处理最终输出（在当前线程或可改为线程池）
                    // 异步处理最终输出（调用改造后的 handleOutputAsync）
                    DeepSearchFlow.Output deepSearchOutput = new DeepSearchFlow.Output();

                    recordManager.addTaskOutput(sceneTaskId, sceneId, goal, deepSearchFlow.getFlowId() , deepSearchOutput);

                    return deepSearchOutputHandler.handleOutputAsync(llm, answerOutput, deepSearchOutput, historyPrompt, goal)
                            .thenApply(v -> {

                                // 将 final output 放入 flow 并持久化
                                deepSearchFlow.setOutput(deepSearchOutput);
                                stepEventUtil.eventStepMessage(deepSearchFlow, getRole(), getTaskInfo());

                                // 成功完成，更新 TASK 元记录状态
                                recordManager.updateTaskStatus(sceneTaskId, "COMPLETED");

                                recordManager.addTaskOutputStep(sceneTaskId, sceneId, goal, deepSearchFlow.getFlowId() , deepSearchOutput);

                                // 更新 progress 为 100
                                recordManager.updateTaskProgress(sceneTaskId, 100);
                                return AgentConstants.ChatText.CHAT_FINISH;
                            }); // 输出处理完成后返回结束标识
                })
                .exceptionally(ex -> {
                    log.error("DeepSearch 全流程处理失败", ex);
                    recordManager.updateTaskStatus(sceneTaskId, "FAILED");
                    throw new RuntimeException("DeepSearch 全流程处理失败", ex);
                });
    }

    /**
     * 初始化参数
     */
    private void initWorkerHandlerParams(Long orgId,
                                         String datasetKnowledgeDocument,
                                         String goal,
                                         ReActServiceTool reActServiceTool,
                                         DeepsearchSummaryMessageTool summaryMessageTool,
                                         UploadData uploadData,
                                         ContextEngineeringData contextEngineeringData,
                                         DeepSearchTaskEntity deepSearchTask,
                                         DeepSearchTaskRecordManager statusManager) {

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
        plannerHandler.setReActServiceTool(reActServiceTool) ;
        plannerHandler.setSummaryMessageTool(summaryMessageTool) ;
        plannerHandler.setUploadData(uploadData) ;
        plannerHandler.setContextEngineeringData(contextEngineeringData) ;
        plannerHandler.setDeepSearchTask(deepSearchTask) ;
        plannerHandler.setStatusManager(statusManager) ;

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
        workerHandler.setReActServiceTool(reActServiceTool) ;
        workerHandler.setSummaryMessageTool(summaryMessageTool) ;
        workerHandler.setUploadData(uploadData) ;
        workerHandler.setContextEngineeringData(contextEngineeringData) ;
        workerHandler.setDeepSearchTask(deepSearchTask) ;
        workerHandler.setStatusManager(statusManager) ;

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
        deepSearchOutputHandler.setReActServiceTool(reActServiceTool) ;
        deepSearchOutputHandler.setSummaryMessageTool(summaryMessageTool) ;
        deepSearchOutputHandler.setUploadData(uploadData) ;
        deepSearchOutputHandler.setContextEngineeringData(contextEngineeringData) ;
        deepSearchOutputHandler.setDeepSearchTask(deepSearchTask) ;
        deepSearchOutputHandler.setStatusManager(statusManager) ;

    }

}

