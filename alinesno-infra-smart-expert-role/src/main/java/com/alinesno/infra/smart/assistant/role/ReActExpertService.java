package com.alinesno.infra.smart.assistant.role;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.service.ILLmAdapterService;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.LlmModelEntity;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolResult;
import com.alinesno.infra.smart.assistant.role.context.WorkerResponseJson;
import com.alinesno.infra.smart.assistant.role.prompt.Prompt;
import com.alinesno.infra.smart.assistant.role.tools.AskHumanHelpTool;
import com.alinesno.infra.smart.assistant.role.utils.AttachmentReaderUtils;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.assistant.service.IToolService;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.*;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Agent推理模式
 */
@Scope("prototype")
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_REACT)
public class ReActExpertService extends ExpertService {

    @Value("${alinesno.infra.smart.assistant.maxLoop:10}")
    private int maxLoop ;

    @Autowired
    private IToolService toolService ;

    @Autowired
    private ILLmAdapterService llmAdapter ;

    @Autowired
    private AttachmentReaderUtils  attachmentReaderUtils; ;

    @Autowired
    private ILlmModelService llmModelService ;

    private final MessageUsageDto usage = new MessageUsageDto();  // 消耗统计

    @Override
    protected String handleRole(IndustryRoleEntity role,
                                MessageEntity workflowMessage,
                                MessageTaskInfo taskInfo) {

        String goal = clearMessage(taskInfo.getText()) ; // 目标

        List<ToolDto> tools = toolService.getByToolIds(role.getSelectionToolsData()) ;

        HistoriesPrompt historyPrompt = new HistoriesPrompt();
        historyPrompt.setMaxAttachedMessageCount(maxHistory);
        historyPrompt.setHistoryMessageTruncateEnable(false);

        List<DocumentVectorBean> datasetKnowledgeDocumentList = searchChannelKnowledgeBase(goal , role.getKnowledgeBaseIds()) ;
        handleReferenceArticle(taskInfo , datasetKnowledgeDocumentList) ;

        String oneChatId = IdUtil.getSnowflakeNextIdStr() ;
        String datasetKnowledgeDocument = getDatasetKnowledgeDocument(workflowMessage, taskInfo, datasetKnowledgeDocumentList, oneChatId, historyPrompt);

        boolean isCompleted = false ;  // 是否已经完成
        boolean isToolCompleted = false ;  // 是否已经完成
        int loop = 0 ;

        String answer = "" ; // 回答
        StringBuilder toolOutput = new StringBuilder(); // 工具执行结果

        Llm llm = getLlm(role) ;

        do {

            oneChatId = IdUtil.getSnowflakeNextIdStr() ;
            eventStepMessage(loop == 0?"开始思考问题.":"第"+loop+"次思考", AgentConstants.STEP_START , oneChatId , taskInfo) ;
            taskInfo.setReasoningText(null);
            loop++;

            String prompt = Prompt.buildPrompt(role , tools , toolOutput , goal , datasetKnowledgeDocument) ;

            eventStepMessage("开始思考中..." , AgentConstants.STEP_START , oneChatId , taskInfo) ;

            // 历史对话
            handleHistoryUserMessage(historyPrompt , taskInfo.getChannelId()) ;
            historyPrompt.addMessage(new HumanMessage(prompt));

            CompletableFuture<String> future = getAiChatResultAsync(llm, historyPrompt , taskInfo , oneChatId) ; // replacePlaceholders(nodeData.getPrompt()));

            // 等待异步任务完成并获取结果
            try {

                String output = future.get();
                log.debug("output = {}" , output);

                eventStepMessage("思考结束" , AgentConstants.STEP_FINISH, oneChatId  , taskInfo) ;

                List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(output);
                CodeContent codeContent = codeContentList.get(0);

                WorkerResponseJson reactResponse = JSON.parseObject(codeContent.getContent(), WorkerResponseJson.class);
                log.debug("reactResponse = {}" , reactResponse);

                if (reactResponse.getTools() != null && !reactResponse.getTools().isEmpty()) {
                    String observation = "" ;
                    for(WorkerResponseJson.Tool tool : reactResponse.getTools()){

                        observation = reactResponse.getThought() ;

                        String toolFullName = tool.getName() ;

                        // 如果是咨询人类的
                        if(toolFullName.equals(AskHumanHelpTool.class.getSimpleName())){
                            String question = tool.getArgsList().get("question")+"" ;

                            streamMessagePublisher.doStuffAndPublishAnEvent(question ,
                                    role,
                                    taskInfo,
                                    taskInfo.getTraceBusId());

                            isToolCompleted = true ;
                            isCompleted = true ;   // 结束对话，等待人类回复
                            continue;
                        }

                        log.debug("正在执行工具名称：{}" , toolFullName);
                        ToolEntity toolEntity = toolService.getToolScript(toolFullName , role.getSelectionToolsData()) ;

                        Map<String, Object> argsList = tool.getArgsList();

                        try {
                            ToolResult toolResult = ToolExecutor.executeGroovyScript(toolEntity.getGroovyScript(), argsList , getSecretKey());
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

                                String toolAndObservable = "\r\n" + executeToolOutput ;
                                toolOutput.append(toolAndObservable);

                                taskInfo.setReasoningText("<p>" + observation + "</p>");
                            }

                            log.debug("工具执行结果：{}", executeToolOutput);

                            if(toolResult.isFinished()){  // 设置工具执行结果即是答案
                                answer = String.valueOf(executeToolOutput) ;
                                isToolCompleted = true ;
                                isCompleted = true ;   // 结束对话
                            }

                        } catch (Exception e) {
                            log.error("工具执行失败:{}", e.getMessage());
                            streamMessagePublisher.doStuffAndPublishAnEvent("工具执行失败:" + e.getMessage(),
                                    role,
                                    taskInfo,
                                    taskInfo.getTraceBusId()) ;
                        }
                    }

                }

                if(StringUtils.hasLength(reactResponse.getFinalAnswer())){  // 有了最终的答案
                    answer = reactResponse.getFinalAnswer();
                    isCompleted = true;
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                streamMessagePublisher.doStuffAndPublishAnEvent("调用失败:" + e.getMessage(),
                        role,
                        taskInfo,
                        taskInfo.getTraceBusId()) ;
                answer = "角色调用失败，请根据异常处理" ;
                break ; // 跳出循环
            }

            if(loop >= maxLoop){
                isCompleted = true ;
            }
        } while (!isCompleted);

        streamStoreMessagePublisher.doStuffAndPublishAnEvent(answer == null ? "" : answer,
                role,
                taskInfo,
                taskInfo.getTraceBusId()) ;

        return StringUtils.hasLength(answer)? AgentConstants.ChatText.CHAT_FINISH : AgentConstants.ChatText.CHAT_NO_ANSWER;
    }

    /**
     * 获取知识库
     * @param workflowMessage
     * @param taskInfo
     * @param datasetKnowledgeDocumentList
     * @param oneChatId
     * @param historyPrompt
     * @return
     */
    @NotNull
    private String getDatasetKnowledgeDocument(MessageEntity workflowMessage, MessageTaskInfo taskInfo, List<DocumentVectorBean> datasetKnowledgeDocumentList, String oneChatId, HistoriesPrompt historyPrompt) {
        String datasetKnowledgeDocument = "" ;
        if(!CollectionUtils.isEmpty(datasetKnowledgeDocumentList) || !CollectionUtils.isEmpty(taskInfo.getAttachments()) || workflowMessage != null){

            String preKnowledgeProcess = "解析知识库" + (taskInfo.getAttachments() == null ? "" : "和" + taskInfo.getAttachments().size() + "个附件")  ;

            if(workflowMessage != null){
                preKnowledgeProcess += "和" + workflowMessage.getId() + "消息" ;
            }

            eventStepMessage(preKnowledgeProcess, AgentConstants.STEP_START , oneChatId, taskInfo) ;
            datasetKnowledgeDocument = handleDocumentContent(datasetKnowledgeDocumentList, workflowMessage, taskInfo.getAttachments() , historyPrompt) ;
            eventStepMessage(preKnowledgeProcess , AgentConstants.STEP_FINISH, oneChatId, taskInfo, datasetKnowledgeDocument) ;
        }
        return datasetKnowledgeDocument;
    }

    /**
     * 获取到指定的模型
     * @param role
     * @return
     */
    private Llm getLlm(IndustryRoleEntity role) {

        Assert.notNull(role.getModelId(), "角色模型未配置.");
        long modelId = role.getModelId(); ; // 模型ID

        LlmModelEntity llmModel = llmModelService.getById(modelId) ;
        Assert.notNull(llmModel, "模型未配置或者不存在.");

        LlmConfig config = new LlmConfig() ;

        config.setEndpoint(llmModel.getApiUrl());
        config.setApiKey(llmModel.getApiKey()) ;
        config.setModel(llmModel.getModel()) ;

        return llmAdapter.getLlm(llmModel.getProviderCode(), config);
    }


    /**
     * 处理并合并文档内容
     *
     * @param datasetKnowledgeDocumentList
     * @param workflowMessage
     * @param attachments
     * @param historyPrompt
     * @return
     */
    private String handleDocumentContent(List<DocumentVectorBean> datasetKnowledgeDocumentList, MessageEntity workflowMessage, List<FileAttachmentDto> attachments, HistoriesPrompt historyPrompt) {

        StringBuilder sb = new StringBuilder();

        // 如果上一个节点有内容，则自动的获取到上一个节点的结果做为知识库内容的一部分
        if(workflowMessage != null && StringUtils.hasLength(workflowMessage.getContent())){
            sb.append(AgentConstants.Slices.PRE_CONTENT);
            sb.append(workflowMessage.getContent()).append("\n");
        }

        // 添加附件解析 attachments(比如文件或者图片之类的)
        if(!CollectionUtils.isEmpty(attachments)){
            List<FileAttachmentDto> newAttachments = attachmentReaderUtils.readAttachmentList(attachments) ;
            for(FileAttachmentDto fileAttachmentDto : newAttachments){
                sb.append(AgentConstants.Slices.REFERENCE_CONTENT);


                StringBuilder treatmentSb = new StringBuilder();
                treatmentSb.append("文件名称:").append(fileAttachmentDto.getFileName()).append("\n");
                treatmentSb.append("文件类型:").append(fileAttachmentDto.getFileType()).append("\n");
                treatmentSb.append("文件大小:").append(fileAttachmentDto.getLength()).append("\n");
                treatmentSb.append("文件内容:").append(fileAttachmentDto.getFileContent()).append("\n");

                // 将附件内容同步放到用户消息历史中，以确保之前的消息包含逻辑
                historyPrompt.addMessage(new HumanMessage(treatmentSb.toString()));

                sb.append(treatmentSb);
            }
        }

        // 添加知识库内容
        if(!CollectionUtils.isEmpty(datasetKnowledgeDocumentList)){
            for(DocumentVectorBean bean : datasetKnowledgeDocumentList){
                sb.append(AgentConstants.Slices.DATASET_CONTENT);
                sb.append(bean.getDocument_content()).append("\n");
            }
        }

        return sb.toString() ;
    }

    /**
     * 处理知识库引用的问题
     * @param taskInfo
     * @param datasetKnowledgeDocument
     */
    private void handleReferenceArticle(MessageTaskInfo taskInfo, List<DocumentVectorBean> datasetKnowledgeDocument) {
        if(datasetKnowledgeDocument != null && !datasetKnowledgeDocument.isEmpty()){
            List<MessageReferenceDto> contentReferenceArticle = new ArrayList<>();

            for (DocumentVectorBean documentVectorBean : datasetKnowledgeDocument) {
                MessageReferenceDto messageReferenceDto = new MessageReferenceDto();

                messageReferenceDto.setId(documentVectorBean.getId()+"");
                messageReferenceDto.setDocumentName(documentVectorBean.getDocument_title());
                messageReferenceDto.setDocumentUrl(documentVectorBean.getSourceUrl());
                contentReferenceArticle.add(messageReferenceDto) ;
            }

            taskInfo.setContentReferenceArticle(contentReferenceArticle);
        }
    }

    @Override
    protected String handleModifyCall(IndustryRoleEntity role,
                                      MessageEntity workflowMessage,
                                      List<CodeContent> codeContentList,
                                      MessageTaskInfo taskInfo) {

        return null;
    }

    @Override
    protected String handleFunctionCall(IndustryRoleEntity role,
                                        MessageEntity workflowMessage,
                                        List<CodeContent> codeContentList,
                                        MessageTaskInfo taskInfo) {

        return null;
    }

    protected CompletableFuture<String> getAiChatResultAsync(Llm llm,  HistoriesPrompt historyPrompt  , MessageTaskInfo taskInfo , String oneChatId) {
        CompletableFuture<String> future = new CompletableFuture<>();
        AtomicReference<String> outputStr = new AtomicReference<>("");

        // 创建一个 final 局部变量来持有 taskInfo 的引用
        final MessageTaskInfo localTaskInfo = taskInfo;
        long startTime = System.currentTimeMillis();

//        try {
            llm.chatStream(historyPrompt, new StreamResponseListener() {
                @Override
                public void onMessage(ChatContext context, AiMessageResponse response) {

                        AiMessage message = response.getMessage();

                        System.out.println(">>>> " + message);

                        FlowStepStatusDto stepDto = new FlowStepStatusDto();
                        stepDto.setMessage("任务进行中...");
                        stepDto.setStepId(oneChatId);
                        stepDto.setStatus(AgentConstants.STEP_PROCESS);

                        if(StringUtils.hasLength(message.getContent())) {
                            stepDto.setFlowChatText(message.getContent());
                        }

                        if(StringUtils.hasLength(message.getReasoningContent())){
                            stepDto.setFlowReasoningText(message.getReasoningContent());
                        }

                        stepDto.setPrint(true);

                        synchronized (localTaskInfo) {
                            localTaskInfo.setFlowStep(stepDto);
                        }

                        try {
                            boolean isEnd = false;
                            synchronized (localTaskInfo) {
                                if (message.getStatus() == MessageStatus.END) {
                                    outputStr.set(message.getFullContent());
                                    stepDto.setStatus(AgentConstants.STEP_FINISH);
                                    isEnd = true;
                                }
                            }

                            streamMessagePublisher.doStuffAndPublishAnEvent(null, getRole(), localTaskInfo, localTaskInfo.getTraceBusId());

                            if (isEnd) {
                                long endTime = System.currentTimeMillis();
                                int totalToken = getTotalToken(message) ;

                                taskInfo.setUsage(usage);

                                usage.setTime(endTime - startTime);
                                usage.setToken(totalToken);
                                taskInfo.setUsage(usage);

                                future.complete(outputStr.get());
                            }
                        } catch (Exception e) {
                            // 处理发布事件时的异常
                            log.error(e.getMessage());
                            future.completeExceptionally(e);
                        }
                }

                @Override
                public void onFailure(ChatContext context, Throwable throwable) {
                    log.error("消息处理失败" , throwable);
                    eventStepMessage("消息处理失败", AgentConstants.STEP_FINISH , oneChatId , taskInfo) ;
                    future.completeExceptionally(throwable);
                }

            }) ;

//        } catch (Exception e) {
//            // 处理 chatStream 方法的异常
//            log.error(e.getMessage());
//            future.completeExceptionally(e);
//        }

        return future;
    }

    /**
     * 获取总token
     * @param aiMessage
     * @return
     */
    private int getTotalToken(AiMessage aiMessage) {
        try{
            return aiMessage.getTotalTokens();
        }catch(Exception e){
            return 0 ;
        }
    }

    /**
     * 流程节点消息
     * @param stepMessage
     * @param status
     */
    public void eventStepMessage(String stepMessage, String status, String stepId , MessageTaskInfo taskInfo , String stepContent) {

        FlowStepStatusDto stepDto = new FlowStepStatusDto() ;
        stepDto.setMessage(stepMessage) ;
        stepDto.setStepId(stepId) ;
        stepDto.setStatus(status);
        stepDto.setPrint(false);

        if(StringUtils.hasLength(stepContent)){
            // 如果stepContent的内容超过2048个字符，则进行截取
            if(stepContent.length() > 2048){
                stepContent = stepContent.substring(0, 2048);
            }
            stepDto.setFlowChatText(stepContent);
        }

        taskInfo.setFlowStep(stepDto);

        streamMessagePublisher.doStuffAndPublishAnEvent(null,
                getRole(),
                getTaskInfo(),
                taskInfo.getTraceBusId()
        );

    }

    public void eventStepMessage(String stepMessage, String status, String stepId , MessageTaskInfo taskInfo) {
        eventStepMessage(stepMessage, status, stepId, taskInfo, null);
    }
}
