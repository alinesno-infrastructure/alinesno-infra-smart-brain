package com.alinesno.infra.smart.assistant.role;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.LlmConfig;
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
import com.alinesno.infra.smart.assistant.role.context.AgentConstants;
import com.alinesno.infra.smart.assistant.role.context.WorkerResponseJson;
import com.alinesno.infra.smart.assistant.role.prompt.Prompt;
import com.alinesno.infra.smart.assistant.role.tools.AskHumanHelpTool;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.assistant.service.IToolService;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageReferenceDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import lombok.extern.slf4j.Slf4j;
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
    private ILlmModelService llmModelService ;

    @Override
    protected String handleRole(IndustryRoleEntity role,
                                MessageEntity workflowExecution,
                                MessageTaskInfo taskInfo) {

        String goal = clearMessage(taskInfo.getText()) ; // 目标

        List<ToolDto> tools = toolService.getByToolIds(role.getSelectionToolsData()) ;
        if(tools == null || tools.isEmpty()){
            return "没有可用的工具，请检查工具是否正常。";
        }

        List<DocumentVectorBean> datasetKnowledgeDocumentList = searchChannelKnowledgeBase(goal , role.getKnowledgeBaseIds()) ;
        handleReferenceArticle(taskInfo , datasetKnowledgeDocumentList) ;
        String datasetKnowledgeDocument = handleDocumentContent(datasetKnowledgeDocumentList) ;

        boolean isCompleted = false ;  // 是否已经完成
        int loop = 0 ;

        String answer = null; // 回答
        StringBuilder thought = new StringBuilder(); // 思考
        StringBuilder askHumanHelpThought = new StringBuilder(); // 交流过程

        do {

            String oneChatId = IdUtil.getSnowflakeNextIdStr() ;
            eventStepMessage(loop == 0?"开始思考问题.":"第"+loop+"次思考", AgentConstants.STEP_START , oneChatId , taskInfo) ;
            taskInfo.setReasoningText(null);
            loop++;

            String prompt = Prompt.buildPrompt(role , tools , thought , goal , datasetKnowledgeDocument) ;
            prompt = Prompt.buildHumanHelpPrompt(prompt , askHumanHelpThought) ;

            eventStepMessage("开始思考中..." , AgentConstants.STEP_START , oneChatId , taskInfo) ;

            // 历史对话
            HistoriesPrompt historyPrompt = new HistoriesPrompt();
            handleHistoryUserMessage(historyPrompt , taskInfo.getChannelId()) ;
            historyPrompt.addMessage(new HumanMessage(prompt));

            Llm llm = getLlm(role) ;
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
                                    IdUtil.getSnowflakeNextId());

                            isCompleted = true ;   // 结束对话，等待人类回复
                            continue;
                        }

                        log.debug("正在执行工具名称：{}" , toolFullName);
                        ToolEntity toolEntity = toolService.getToolScript(toolFullName , role.getSelectionToolsData()) ;

                        Map<String, Object> argsList = tool.getArgsList();

                        try {
                            ToolResult  toolResult = ToolExecutor.executeGroovyScript(toolEntity.getGroovyScript(), argsList , getSecretKey());
                            Object executeToolOutput = toolResult.getOutput() ;

                            if(executeToolOutput != null && StringUtils.hasLength(executeToolOutput+"")){

                                String toolAndObservable = String.format("%s\r\n" , executeToolOutput) ;
                                thought.append(toolAndObservable);

                                taskInfo.setReasoningText(observation);
                                streamStoreMessagePublisher.doStuffAndPublishAnEvent(String.valueOf(executeToolOutput),
                                        role,
                                        taskInfo,
                                        taskInfo.getTraceBusId()) ;
                                answer = String.valueOf(executeToolOutput) ;

                            }
                            log.debug("工具执行结果：{}", executeToolOutput);

                            if(toolResult.isFinished()){
                                answer = "表达结束" ;
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
            }

            if(loop >= maxLoop){
                isCompleted = true ;
            }
        } while (!isCompleted);

        return StringUtils.hasLength(answer)? answer : "我尝试找了很多次，但是未找到答案";
    }

    /**
     * 获取到指定的模型
     * @param role
     * @return
     */
    private Llm getLlm(IndustryRoleEntity role) {
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
     * @param datasetKnowledgeDocumentList
     * @return
     */
    private String handleDocumentContent(List<DocumentVectorBean> datasetKnowledgeDocumentList) {

        StringBuilder sb = new StringBuilder();

        if(!CollectionUtils.isEmpty(datasetKnowledgeDocumentList)){
            for(DocumentVectorBean bean : datasetKnowledgeDocumentList){
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
                                      MessageEntity workflowExecution,
                                      List<CodeContent> codeContentList,
                                      MessageTaskInfo taskInfo) {

        return null;
    }

    @Override
    protected String handleFunctionCall(IndustryRoleEntity role,
                                        MessageEntity workflowExecution,
                                        List<CodeContent> codeContentList,
                                        MessageTaskInfo taskInfo) {

        return null;
    }

    protected CompletableFuture<String> getAiChatResultAsync(Llm llm,  HistoriesPrompt historyPrompt  , MessageTaskInfo taskInfo , String oneChatId) {
        CompletableFuture<String> future = new CompletableFuture<>();
        AtomicReference<String> outputStr = new AtomicReference<>("");

        // 创建一个 final 局部变量来持有 taskInfo 的引用
        final MessageTaskInfo localTaskInfo = taskInfo;

        try {
            llm.chatStream(historyPrompt, (context, response) -> {

                AiMessage message = response.getMessage();

                System.out.println(">>>> " + message);

                FlowStepStatusDto stepDto = new FlowStepStatusDto();
                stepDto.setMessage("任务进行中...");
                stepDto.setStepId(oneChatId);
                stepDto.setStatus(AgentConstants.STEP_PROCESS);

                if(com.alinesno.infra.common.core.utils.StringUtils.isNotBlank(message.getContent())) {
                    stepDto.setFlowChatText(message.getContent());
                }

                if(com.alinesno.infra.common.core.utils.StringUtils.isNotBlank(message.getReasoningContent())){
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
                        future.complete(outputStr.get());
                    }
                } catch (Exception e) {
                    // 处理发布事件时的异常
                    log.error(e.getMessage());
                    future.completeExceptionally(e);
                }
            });
        } catch (Exception e) {
            // 处理 chatStream 方法的异常
            log.error(e.getMessage());
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * 流程节点消息
     * @param stepMessage
     * @param status
     */
    public void eventStepMessage(String stepMessage, String status, String stepId , MessageTaskInfo taskInfo) {

        FlowStepStatusDto stepDto = new FlowStepStatusDto() ;
        stepDto.setMessage(stepMessage) ;
        stepDto.setStepId(stepId) ;
        stepDto.setStatus(status);
        stepDto.setPrint(false);

        taskInfo.setFlowStep(stepDto);

        streamMessagePublisher.doStuffAndPublishAnEvent(null,
                getRole(),
                getTaskInfo(),
                taskInfo.getTraceBusId()
        );

    }

}
