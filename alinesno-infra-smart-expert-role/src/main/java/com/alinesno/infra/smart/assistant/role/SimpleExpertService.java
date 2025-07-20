package com.alinesno.infra.smart.assistant.role;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.alibaba.fastjson.JSON;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.IndustryRoleDto;
import com.alinesno.infra.smart.assistant.api.ToolDto;
import com.alinesno.infra.smart.assistant.api.config.ModelConfig;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.ToolEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.plugin.tool.ToolExecutor;
import com.alinesno.infra.smart.assistant.api.ToolResult;
import com.alinesno.infra.smart.assistant.role.context.WorkerResponseJson;
import com.alinesno.infra.smart.assistant.role.prompt.Prompt;
import com.alinesno.infra.smart.assistant.role.tools.ReActServiceTool;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 简单的LLM大模型专家服务，只会调用一次工具还有查询一次知识库，如果查询不到，结果直接返回
 */
@Scope("prototype")
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_SIMPLE)
public class SimpleExpertService extends ReActExpertService {


    @Autowired
    private ReActServiceTool reActServiceTool  ;

    @Override
    protected CompletableFuture<String> handleRole(IndustryRoleEntity role,
                                                   MessageEntity workflowMessage,
                                                   MessageTaskInfo taskInfo) {


        reActServiceTool.setRole(getRole());
        reActServiceTool.setTaskInfo(getTaskInfo());

        String goal = clearMessage(taskInfo.getText()) ; // 目标

        IndustryRoleDto industryRoleDto = IndustryRoleDto.fromEntity(getRole()) ;
        ModelConfig modelConfig = industryRoleDto.getModelConfig() ;

        List<ToolDto> tools = getToolService().getByToolIds(role.getSelectionToolsData() , role.getOrgId()) ;

        HistoriesPrompt historyPrompt = new HistoriesPrompt();
        historyPrompt.setMaxAttachedMessageCount(maxHistory);
        historyPrompt.setHistoryMessageTruncateEnable(false);

        List<DocumentVectorBean> datasetKnowledgeDocumentList = searchChannelKnowledgeBase(goal , role.getKnowledgeBaseIds()) ;
        reActServiceTool.handleReferenceArticle(taskInfo , datasetKnowledgeDocumentList) ;

        String oneChatId = IdUtil.getSnowflakeNextIdStr() ;
        String datasetKnowledgeDocument = reActServiceTool.getDatasetKnowledgeDocument(goal , workflowMessage, taskInfo, datasetKnowledgeDocumentList, oneChatId, historyPrompt);

        boolean hasOutsideKnowledge = StringUtils.hasLength(taskInfo.getCollectionIndexName()) ;
        String answer = "" ; // 回答
        StringBuilder toolOutput = new StringBuilder(); // 工具执行结果

        Llm llm = getLlm(role) ;

            oneChatId = IdUtil.getSnowflakeNextIdStr() ;
            reActServiceTool.eventStepMessage("开始思考问题.", AgentConstants.STEP_START , oneChatId , taskInfo) ;
            taskInfo.setReasoningText(null);

            String prompt = Prompt.buildPrompt(role , tools , toolOutput , goal , datasetKnowledgeDocument, getMaxLoop() , hasOutsideKnowledge) ;

            reActServiceTool.eventStepMessage("开始思考中..." , AgentConstants.STEP_START , oneChatId , taskInfo) ;

            // 历史对话
            handleHistoryUserMessage(historyPrompt , taskInfo.getChannelId()) ;
            historyPrompt.addMessage(new HumanMessage(prompt));

            CompletableFuture<String> future = getAiChatResultAsync(llm, historyPrompt , taskInfo , oneChatId, modelConfig) ; // replacePlaceholders(nodeData.getPrompt()));

            // 等待异步任务完成并获取结果
            try {

                String output = future.get();
                log.debug("output = {}" , output);

                reActServiceTool.eventStepMessage("思考结束" , AgentConstants.STEP_FINISH, oneChatId  , taskInfo) ;

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
//                        if(toolFullName.equals(AskHumanHelpTool.class.getSimpleName())){
//                            String question = tool.getArgsList().get("question")+"" ;
//
//                            streamMessagePublisher.doStuffAndPublishAnEvent(question ,
//                                    role,
//                                    taskInfo,
//                                    taskInfo.getTraceBusId());
//
//                            isToolCompleted = true ;
//                            isCompleted = true ;   // 结束对话，等待人类回复
//                            continue;
//                        }

                        log.debug("正在执行工具名称：{}" , toolFullName);
                        ToolEntity toolEntity = getToolService().getToolScript(toolFullName , role.getSelectionToolsData()) ;

                        Map<String, String> argsList = tool.getArgsList();

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
//                                isToolCompleted = true ;
//                                isCompleted = true ;   // 结束对话
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
//                    isCompleted = true;
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
//                streamMessagePublisher.doStuffAndPublishAnEvent("调用失败:" + e.getMessage(),
//                        role,
//                        taskInfo,
//                        taskInfo.getTraceBusId()) ;
                log.error("调用失败" , e) ;
                if(!StringUtils.hasText(answer)){
                    answer = "角色调用失败，请根据异常处理" ;
                }
//                break ; // 跳出循环
            }

//            if(loop >= maxLoop){
//                isCompleted = true ;
//                answer = AgentConstants.ChatText.CHAT_NO_ANSWER ; // 没有找到答案
//            }
//        } while (!isCompleted);

        taskInfo.setFullContent(answer);

        streamStoreMessagePublisher.doStuffAndPublishAnEvent(answer == null ? "" : answer,
                role,
                taskInfo,
                taskInfo.getTraceBusId()) ;

//        return StringUtils.hasLength(answer)? AgentConstants.ChatText.CHAT_FINISH : AgentConstants.ChatText.CHAT_NO_ANSWER;

        return null ;
    }

}
