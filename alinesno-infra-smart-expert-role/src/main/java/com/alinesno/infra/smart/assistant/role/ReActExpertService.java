package com.alinesno.infra.smart.assistant.role;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.*;
import com.agentsflex.core.llm.embedding.EmbeddingOptions;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.message.SystemMessage;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.ToolResult;
import com.alinesno.infra.smart.assistant.api.config.ContextEngineeringData;
import com.alinesno.infra.smart.assistant.api.config.ModelConfig;
import com.alinesno.infra.smart.assistant.api.config.UploadData;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import com.alinesno.infra.smart.assistant.role.context.ContextEngineerUtils;
import com.alinesno.infra.smart.assistant.role.context.WorkerResponseJson;
import com.alinesno.infra.smart.assistant.role.prompt.PromptHandle;
import com.alinesno.infra.smart.assistant.role.prompt.PromptTemplate;
import com.alinesno.infra.smart.assistant.role.tools.*;
import com.alinesno.infra.smart.assistant.role.utils.ParserReActJsonUtil;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.assistant.service.IToolService;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.dto.MessageUsageDto;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.enums.TaskResultTypeEnums;
import com.alinesno.infra.smart.scene.entity.ProjectKnowledgeGroupEntity;
import com.alinesno.infra.smart.scene.service.IProjectKnowledgeGroupService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.alinesno.infra.smart.utils.CodeBlockParser.parseJSONObjectCodeBlocks;

/**
 * Agent推理模式
 */
@Getter
@Scope("prototype")
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_REACT)
public class ReActExpertService extends ExpertService {

    @Value("${alinesno.infra.smart.assistant.maxLoop:6}")
    private int maxLoop ;

    @Autowired
    private IToolService toolService ;

    @Autowired
    private ILLmAdapterService llmAdapter ;

    @Autowired
    private ReActServiceTool reActServiceTool  ;

    @Autowired
    private ILlmModelService llmModelService ;

    @Autowired
    private IProjectKnowledgeGroupService projectKnowledgeGroupService ;

    @Autowired
    private ContextEngineerUtils contextEngineerUtils ;

    @Autowired
    private SummaryMessageTool summaryMessageTool ;

    private final MessageUsageDto usage = new MessageUsageDto();  // 消耗统计

    @Override
    protected CompletableFuture<String> handleRole(IndustryRoleEntity role,
                                                   MessageEntity workflowMessage,
                                                   MessageTaskInfo taskInfo) {

        String goal = clearMessage(taskInfo.getText()) ; // 目标
        String queryText = StringUtils.hasLength(taskInfo.getQueryText()) ? taskInfo.getQueryText() : goal ;

        reActServiceTool.setRole(getRole());
        reActServiceTool.setTaskInfo(getTaskInfo());

        List<ToolDto> tools = toolService.getByToolIds(role.getSelectionToolsData() , role.getOrgId()) ;

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

        ModelConfig modelConfig = industryRoleDto.getModelConfig() ;
        int roleMaxHistory = maxHistory ;
        if(modelConfig != null && modelConfig.isEnabled()){
            roleMaxHistory = modelConfig.getMemoryRounds() ;
            maxLoop = modelConfig.getMaxLoop() ;
        }

        HistoriesPrompt historyPrompt = new HistoriesPrompt();
        historyPrompt.setMaxAttachedMessageCount(roleMaxHistory);
        historyPrompt.setHistoryMessageTruncateEnable(false);
        historyPrompt.addMessage(new SystemMessage(PromptTemplate.REACT_PROMPT_SYSTEM_TEMPLATE));

        // 智能体本身的静态知识库
        List<DocumentVectorBean> datasetKnowledgeDocumentList = searchChannelKnowledgeBase(queryText , role.getKnowledgeBaseIds()) ;
        reActServiceTool.handleReferenceArticle(taskInfo , datasetKnowledgeDocumentList) ;

        // 动态知识库处理
        String oneChatId = IdUtil.getSnowflakeNextIdStr() ;
        String datasetKnowledgeDocument = reActServiceTool.getDatasetKnowledgeDocument(queryText ,
                workflowMessage,
                taskInfo,
                datasetKnowledgeDocumentList,
                oneChatId,
                historyPrompt);

        // 历史对话
        handleHistoryUserMessage(historyPrompt , taskInfo.getChannelId()) ;

        boolean hasOutsideKnowledge = StringUtils.hasLength(taskInfo.getOutsideCollectionIndexName()) ;
        boolean hasUploadKnowledge = StringUtils.hasLength(taskInfo.getUploadCollectionIndexName()) ;
        boolean isCompleted = false ;  // 是否已经完成
        boolean isToolCompleted = false ;  // 是否已经完成
        int loop = 0 ;

        String answer = "" ; // 回答
        String useToolName = "" ;  // 调用工具名称
        StringBuilder toolOutput = new StringBuilder(); // 工具执行结果
        List<WorkerResponseJson> workerResponseJsons = new ArrayList<>();

        Llm llm = getLlm(role) ;

        do {
            oneChatId = IdUtil.getSnowflakeNextIdStr() ;

            reActServiceTool.eventStepMessage(getStepMessage(loop , goal , useToolName), AgentConstants.STEP_START , oneChatId , taskInfo) ;
            taskInfo.setReasoningText(null);
            loop++;
            toolOutput.append(String.format(AgentConstants.Slices.LOOP_COUNT , loop)) ;

            // 构建prompt时使用优化后的上下文
            String prompt = PromptHandle.buildPrompt(role , tools , toolOutput , goal , datasetKnowledgeDocument , maxLoop , hasOutsideKnowledge , hasUploadKnowledge) ;

            if(contextEngineeringDataEnable){
                // 通过上下文处理长度的问题
                CompletableFuture<String> compressPromptFuture = contextEngineerUtils.tryCompressChat(
                        taskInfo ,
                        goal,
                        historyPrompt ,
                        toolOutput ,
                        datasetKnowledgeDocument ,
                        roleMaxHistory ,
                        contextEngineeringData) ;
                try {
                    String compressDatasetKnowledgeDocument = compressPromptFuture.get() ;
                    prompt = PromptHandle.buildPrompt(role , tools , toolOutput , goal , compressDatasetKnowledgeDocument , maxLoop , hasOutsideKnowledge , hasUploadKnowledge) ;

                    log.debug("compressDatasetKnowledgeDocument prompt = {}" , prompt);

                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            reActServiceTool.eventStepMessage("开始思考中..." , AgentConstants.STEP_START , oneChatId , taskInfo) ;
            historyPrompt.addMessage(new HumanMessage(prompt));

            CompletableFuture<String> future = getAiChatResultAsync(llm, historyPrompt , taskInfo , oneChatId , modelConfig) ;

            // 等待异步任务完成并获取结果
            try {

                String output = future.get();
                log.debug("output = {}" , output);

                reActServiceTool.eventStepMessage("思考结束" , AgentConstants.STEP_FINISH, oneChatId  , taskInfo) ;

                List<CodeContent> codeContentList;
                WorkerResponseJson reactResponse;

                try{
                    codeContentList = parseJSONObjectCodeBlocks(output);
                    CodeContent codeContent = codeContentList.get(0);
                    reactResponse = ParserReActJsonUtil.parseJSON(codeContent.getContent()) ;
                    log.debug("reactResponse = {}" , reactResponse);

                    if (reactResponse.getTools() != null && !reactResponse.getTools().isEmpty()) {
                        String observation;

                        // 获取到工具名称，拼接名称起来
                        useToolName = reactResponse.getTools().stream()
                                .map(tool -> tool.getName() + "(" + tool.getType() + ")")
                                .collect(Collectors.joining(","));

                        for(WorkerResponseJson.Tool tool : reactResponse.getTools()){

                            observation = reactResponse.getThought() ;

                            String toolFullName = tool.getName() ;

                            // 如果是咨询人类的
                            if(toolFullName.equals(AskHumanHelpTool.class.getSimpleName())){
                                String question = tool.getArgsList().get("question");

                                streamMessagePublisher.doStuffAndPublishAnEvent(question ,
                                        role,
                                        taskInfo,
                                        taskInfo.getTraceBusId());

                                isToolCompleted = true ;
                                isCompleted = true ;   // 结束对话，等待人类回复
                                continue;
                            }

                            if(toolFullName.equals(UploadRagTool.class.getSimpleName())){
                                String toolAndObservable = reActServiceTool.executeUploadRagTool(tool, reactResponse.getThought(), taskInfo, oneChatId);
                                if(StringUtils.hasLength(toolAndObservable)){
                                    toolOutput.append(toolAndObservable);
                                }
                                continue;
                            }

                            // 如果是OutsideRagTool工具类，使用外挂知识库
                            if(toolFullName.equals(OutsideRagTool.class.getSimpleName())){
                                String toolAndObservable = reActServiceTool.executeOutsideRagTool(tool, reactResponse.getThought(), taskInfo, oneChatId);
                                if(StringUtils.hasLength(toolAndObservable)){
                                    toolOutput.append(toolAndObservable);
                                }
                                continue;
                            }

                            Map<String, String> argsList = tool.getArgsList();

                            // 放置工具用户和组织信息
                            argsList.put("accountId" , String.valueOf(taskInfo.getAccountId())) ;
                            argsList.put("accountOrgId" , String.valueOf(taskInfo.getAccountOrgId())) ;
                            argsList.put("datasetIds" , String.valueOf(role.getKnowledgeBaseIds())) ;

                            try {

                                log.debug("正在执行工具名称：{}" , toolFullName);

                                ToolResult toolResult = null ;

                                if(tool.getType().equals("stdio")){
                                    ToolEntity toolEntity = toolService.getToolScript(toolFullName , role.getSelectionToolsData()) ;
                                    toolResult = ToolExecutor.executeGroovyScript(toolEntity.getGroovyScript(), argsList , getSecretKey());
                                }else if(tool.getType().equals("mcp")){
                                    toolResult = toolService.executeMcpTool(tool.getId() , argsList , role.getOrgId()) ;
                                }else{
                                    continue;
                                }

                                Object executeToolOutput = toolResult.getOutput() ;

                                if(executeToolOutput != null && StringUtils.hasLength(executeToolOutput+"")){

                                    FlowStepStatusDto stepDto = new FlowStepStatusDto();
                                    stepDto.setMessage("工具执行完成.");
                                    stepDto.setStepId(oneChatId);
                                    stepDto.setStatus(AgentConstants.STEP_FINISH);
                                    stepDto.setFlowChatText("\r\n" + executeToolOutput);
                                    stepDto.setPrint(false);
                                    taskInfo.setFlowStep(stepDto);
                                    streamMessagePublisher.doStuffAndPublishAnEvent(null, getRole(), taskInfo, taskInfo.getTraceBusId());

                                    String toolAndObservable = "\r\n" + reactResponse.getThought() + ":" + executeToolOutput ;
                                    toolOutput.append(toolAndObservable);

                                    taskInfo.setReasoningText("<p>" + observation + "</p>");
                                }

                                log.debug("工具执行结果：{}", executeToolOutput);
                                reactResponse.setExecuteToolOutput(executeToolOutput + "\r\n" + reactResponse.getExecuteToolOutput());

                                if(toolResult.isFinished()){  // 设置工具执行结果即是答案
                                    answer = String.valueOf(executeToolOutput) ;
                                    isToolCompleted = true ;
                                    isCompleted = true ;   // 结束对话
                                }

                            } catch (Exception e) {
                                log.error("工具执行失败:{}", e.getMessage());

                                String errorMsg = "工具执行失败:" + e.getMessage();
                                toolOutput.append("\r\n").append(errorMsg);

                                streamMessagePublisher.doStuffAndPublishAnEvent("工具执行失败:" + e.getMessage(),
                                        role,
                                        taskInfo,
                                        taskInfo.getTraceBusId()) ;
                            }
                        }

                        workerResponseJsons.add(reactResponse);
                    }else if(StringUtils.hasLength(reactResponse.getFinalAnswer())){  // 有了最终的答案
                        answer = reactResponse.getFinalAnswer();
                        taskInfo.setFullContent(answer);
                        isCompleted = true;
                    }

                }catch(Exception e){
                    String errorMsg = String.format(PromptTemplate.SELF_CORRECTION_TEMPLATE , e.getMessage() ,  output) ;
                    toolOutput.append(errorMsg) ;
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                log.error("调用失败" , e) ;
                if(!StringUtils.hasText(answer)){
                  answer = AgentConstants.EMPTY_RESULT ;
                }
                break ; // 跳出循环
            }

            if(loop >= maxLoop){
                isCompleted = true ;

                // 健壮性处理(如果是循环结束而且没有答案)
                if(!StringUtils.hasText(answer)){
                    String summaryChatId = IdUtil.getSnowflakeNextIdStr() ;
                    reActServiceTool.eventStepMessage("内容总结输出开始" , AgentConstants.STEP_START , summaryChatId , taskInfo) ;

                    taskInfo.setReasoningText(null);
                    taskInfo.setText(null);

                    String summaryPrompt = PromptHandle.buildSummaryPrompt(goal ,
                            datasetKnowledgeDocument,
                            workerResponseJsons) ;
                    answer = summaryMessageTool.getSummaryAnswer(llm ,
                            summaryPrompt ,
                            summaryChatId ,
                            taskInfo ,
                            role ,
                            getStreamMessagePublisher()) ;

                    taskInfo.setFullContent(answer);
                    taskInfo.setResultType(TaskResultTypeEnums.SUMMARY.getCode());

                    reActServiceTool.eventStepMessage("内容总结输出完成" , AgentConstants.STEP_FINISH , summaryChatId , taskInfo) ;
                }

                answer = StringUtils.hasText(answer) ? answer : AgentConstants.ChatText.CHAT_NO_ANSWER ; // 没有找到答案
            }
        } while (!isCompleted);

        // 返回消息的ID
        String messageId = streamStoreMessagePublisher.doStuffAndPublishAnEvent(answer == null ? "" : answer,
                role,
                taskInfo,
                taskInfo.getTraceBusId()) ;

        // 保存内容引用
        messageReferenceService.saveMessageReference(taskInfo.getDatasetMap() , messageId);

        return CompletableFuture.completedFuture(StringUtils.hasLength(answer)?
                AgentConstants.ChatText.CHAT_FINISH :
                AgentConstants.ChatText.CHAT_NO_ANSWER);
    }

    @NotNull
    private static String getStepMessage(int loop, String goal , String useToolName) {
        if(StringUtils.hasLength(useToolName)){
            return "使用工具 | " + useToolName + " (第"+loop+"次)" ;
        }
        return loop == 0 ? "开始分析"+goal : "第" + loop + "次思考";
    }


    /**
     * 获取到指定的模型
     *
     * @param role
     * @return
     */
    protected Llm getLlm(IndustryRoleEntity role) {

        Assert.notNull(role.getModelId(), "角色模型未配置.");
        long modelId = role.getModelId(); // 模型ID

        LlmModelEntity llmModel = llmModelService.getById(modelId) ;
        Assert.notNull(llmModel, "模型未配置或者不存在.");

        LlmConfig config = new LlmConfig() ;

        config.setEndpoint(llmModel.getApiUrl());
        config.setApiKey(llmModel.getApiKey()) ;
        config.setModel(llmModel.getModel()) ;

        return llmAdapter.getLlm(llmModel.getProviderCode(), config);
    }


    @Override
    protected CompletableFuture<String> handleModifyCall(IndustryRoleEntity role,
                                      MessageEntity workflowMessage,
                                      List<CodeContent> codeContentList,
                                      MessageTaskInfo taskInfo) {

        return null;
    }

    @Override
    protected CompletableFuture<String> handleFunctionCall(IndustryRoleEntity role,
                                                           MessageEntity workflowMessage,
                                                           List<CodeContent> codeContentList,
                                                           MessageTaskInfo taskInfo) {

        return null;
    }

    protected CompletableFuture<String> getAiChatResultAsync(Llm llm,
                                                             HistoriesPrompt historyPrompt,
                                                             MessageTaskInfo taskInfo,
                                                             String oneChatId,
                                                             ModelConfig modelConfig) {

        CompletableFuture<String> future = new CompletableFuture<>();
        AtomicReference<String> outputStr = new AtomicReference<>("");
        long startTime = System.currentTimeMillis();

        ChatOptions chatOptions = new ChatOptions();
        if (modelConfig != null && modelConfig.isEnabled()) {
            chatOptions.setMaxTokens(modelConfig.getReplyLimit());
            chatOptions.setTemperature(Float.parseFloat(modelConfig.getTemperature()));
            chatOptions.setTopP(Float.parseFloat(modelConfig.getTopP()));
        }

        llm.chatStream(historyPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {
                AiMessage message = response.getMessage();

                FlowStepStatusDto stepDto = new FlowStepStatusDto();
                stepDto.setMessage("任务进行中...");
                stepDto.setStepId(oneChatId);
                stepDto.setStatus(AgentConstants.STEP_PROCESS);
                stepDto.setFlowChatText(message.getContent());
                stepDto.setFlowReasoningText(message.getReasoningContent());
                stepDto.setPrint(true);

                // 移除了 synchronized 块
                taskInfo.setFlowStep(stepDto);

                try {
                    streamMessagePublisher.doStuffAndPublishAnEvent(null, getRole(), taskInfo, taskInfo.getTraceBusId());

                    if (message.getStatus() == MessageStatus.END) {
                        outputStr.set(message.getFullContent());
                        stepDto.setStatus(AgentConstants.STEP_FINISH);

                        long endTime = System.currentTimeMillis();
                        int totalToken = reActServiceTool.getTotalToken(message);

                        usage.setTime(endTime - startTime);
                        usage.setToken(totalToken);
                        taskInfo.setUsage(usage);

                        future.complete(outputStr.get());
                    }
                } catch (Exception e) {
                    log.error("消息处理失败", e);
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                log.error("消息处理失败", throwable);
                reActServiceTool.eventStepMessage("消息处理失败", AgentConstants.STEP_FINISH, oneChatId, taskInfo);
                future.completeExceptionally(throwable);
            }
        }, chatOptions);

        return future;
    }

}
