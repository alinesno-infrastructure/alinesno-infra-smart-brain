package com.alinesno.infra.smart.assistant.scene.scene.deepsearch.agent;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.config.ContextEngineeringData;
import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.ReActExpertService;
import com.alinesno.infra.smart.assistant.role.tools.ReActServiceTool;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean.DeepSearchContext;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.bean.DeepTaskBean;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.events.record.DeepSearchTaskRecordManager;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.service.IDeepSearchTaskService;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.DeepsearchSummaryMessageTool;
import com.alinesno.infra.smart.assistant.scene.scene.deepsearch.utils.StepEventUtil;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.deepsearch.dto.DeepSearchFlow;
import com.alinesno.infra.smart.deepsearch.enums.StepActionStatusEnums;
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
    private ILLmAdapterService llmAdapter;

    @Autowired
    private ILlmModelService llmModelService;

    @Autowired
    private CloudStorageConsumer cloudStorageConsumer;

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

        // 构造 DeepSearchContext（替代原来的 initWorkerHandlerParams 填充 handler fields）
        DeepSearchContext context = initWorkerHandlerParams(role.getOrgId(),
                datasetKnowledgeDocument,
                goal ,
                reActServiceTool ,
                summaryMessageTool ,
                uploadData ,
                contextEngineeringData ,
                deepSearchTask ,
                recordManager,
                deepSearchFlow,
                oneChatId);

        Llm llm = getLlm(role);
        DeepSearchFlow.Plan plan = new DeepSearchFlow.Plan("执行深度搜索规划");
        deepSearchFlow.setPlan(plan);
        stepEventUtil.eventStepMessage(deepSearchFlow, getRole(), getTaskInfo());

        // 添加任务计划
        recordManager.addTaskPlan(sceneTaskId, sceneId, goal, deepSearchFlow.getFlowId() , plan);

        // 2. 异步链式调用：规划生成 -> 任务解析 -> 顺序执行每个任务 -> 汇总输出
        return plannerHandler.getAiChatResultAsync(llm, historyPrompt, taskInfo, oneChatId, goal, context)
                .thenCompose(planningOutput -> {
                    log.debug("planning prompt output = {}", planningOutput);

                    return plannerHandler.getAiChatPlanAsync(llm, historyPrompt, taskInfo, oneChatId, planningOutput, context);
                })
                .thenCompose(taskBeans -> {

                    // 标识规划任务已完成
                    recordManager.markTaskPlan(plan , StepActionStatusEnums.DONE.getKey());

                    // 之前将步骤 map 注入到 workerHandler，现改为显式通过 context 传递，故不再 setTaskStepMap 等
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

                                    // 由 workerHandler.executeTask 接收 context 并在内部处理 step/addAction/record 等
                                    String taskOutput = workerHandler.executeTask(task, llm, taskBeans, context); // 同步执行，运行在线程池中

                                    recordManager.markTaskWorker(step , StepActionStatusEnums.DONE.getKey());

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

                    // 异步处理最终输出（调用改造后的 handleOutputAsync，显式传 context）
                    DeepSearchFlow.Output deepSearchOutput = new DeepSearchFlow.Output();
                    deepSearchOutput.setId(IdUtil.getSnowflakeNextIdStr());
                    deepSearchOutput.setName("内容总结");
                    deepSearchOutput.setDescription("将根据目标生成多个目标结构.");

                    // 添加任务输出
                    recordManager.addTaskOutput(sceneTaskId, sceneId, goal, deepSearchFlow.getFlowId() , deepSearchOutput);

                    return deepSearchOutputHandler.handleOutputAsync(llm, answerOutput, deepSearchOutput, historyPrompt, goal, context)
                            .thenApply(v -> {

                                // 将 final output 放入 flow 并持久化
                                deepSearchFlow.setOutput(deepSearchOutput);
                                stepEventUtil.eventStepMessage(deepSearchFlow, getRole(), getTaskInfo());

                                // 添加任务输出
                                recordManager.markTaskOutput(deepSearchOutput , StepActionStatusEnums.DONE.getKey());

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
     * 初始化并返回 DeepSearchContext（不再对 handler 进行 setXXX）
     */
    private DeepSearchContext initWorkerHandlerParams(Long orgId,
                                                      String datasetKnowledgeDocument,
                                                      String goal,
                                                      ReActServiceTool reActServiceTool,
                                                      DeepsearchSummaryMessageTool summaryMessageTool,
                                                      UploadData uploadData,
                                                      ContextEngineeringData contextEngineeringData,
                                                      DeepSearchTaskEntity deepSearchTask,
                                                      DeepSearchTaskRecordManager statusManager,
                                                      DeepSearchFlow deepSearchFlow,
                                                      String oneChatId) {

        List<ToolDto> tools = getToolService().getByToolIds(getRole().getSelectionToolsData() , orgId) ;

        // 构造不可变/显式传参上下文 DeepSearchContext

        return DeepSearchContext.builder()
                .orgId(orgId)
                .goal(goal)
                .datasetKnowledgeDocument(datasetKnowledgeDocument)
                .historyPrompt(new HistoriesPrompt()) // 若需要传入上面已构造的 historyPrompt，可改为传入变量
                .oneChatId(oneChatId)
                .maxLoopCount(getMaxLoop())
                .role(getRole())
                .deepSearchTask(deepSearchTask)
                .uploadData(uploadData)
                .contextEngineeringData(contextEngineeringData)
                .secretKey(getSecretKey())
                .reActServiceTool(reActServiceTool)
                .summaryMessageTool(summaryMessageTool)
                .toolService(getToolService())
                .tools(tools)
                .deepSearchFlow(deepSearchFlow)
                .stepEventUtil(stepEventUtil)
                .recordManager(statusManager)
                .taskInfo(getTaskInfo())
                // 以下外部服务若在父类或容器中可获取则传入，否则可留 null 并在 handler 使用时考虑空判断
                .llmAdapter(llmAdapter)
                .llmModelService(llmModelService)
                .cloudStorageConsumer(cloudStorageConsumer)
                .build();
    }

}