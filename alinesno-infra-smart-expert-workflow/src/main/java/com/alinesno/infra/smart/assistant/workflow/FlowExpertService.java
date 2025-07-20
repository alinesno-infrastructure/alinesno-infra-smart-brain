package com.alinesno.infra.smart.assistant.workflow;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.util.StringUtil;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.enums.WorkflowStatusEnum;
import com.alinesno.infra.smart.assistant.role.ExpertService;
import com.alinesno.infra.smart.assistant.role.llm.adapter.MessageManager;
import com.alinesno.infra.smart.assistant.role.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.workflow.dto.FlowNodeDto;
import com.alinesno.infra.smart.assistant.workflow.service.IFlowService;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.alinesno.infra.smart.im.constants.ImConstants.TYPE_ROLE;

/**
 * 流程节点编排引擎，执行多个流程节点并返回结果任务
 */
@Scope("prototype")
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_FLOW)
public class FlowExpertService extends ExpertService {

    @Autowired
    private IFlowService flowService;

    @Setter
    private FlowNodeDto node;

    @Setter
    private StringBuilder outputContent;

    @Override
    protected CompletableFuture<String> handleRole(IndustryRoleEntity role,
                                                   MessageEntity workflowExecution,
                                                   MessageTaskInfo taskInfo) {

        String result = flowService.runRoleFlow(taskInfo, role, workflowExecution, this);
        log.debug("handleRole result : {}", result);

        return null ; //   AgentConstants.ChatText.CHAT_FINISH ;
    }

    @Override
    public CompletableFuture<WorkflowExecutionDto> runRoleAgent(IndustryRoleEntity role, MessageEntity workflowExecution, MessageTaskInfo taskInfo) {

        WorkflowExecutionDto record = new WorkflowExecutionDto();

        // 设置业务跟踪
        long traceBusId = IdUtil.getSnowflakeNextId();
        long flowChatId = IdUtil.getSnowflakeNextId();

        record.setTraceBusId(traceBusId);

        taskInfo.setTraceBusId(traceBusId);
        taskInfo.setFlowChatId(flowChatId);

        // 任务开始记录
        record.setRoleId(role.getId());
        record.setChannelId(taskInfo.getChannelId());

        record.setBuildNumber(1);
        record.setStartTime(System.currentTimeMillis());

        record.setStatus(WorkflowStatusEnum.IN_PROGRESS.getStatus());

        this.setRole(role);
        this.setTaskInfo(taskInfo);
        this.setMsgUuid(IdUtil.getSnowflakeNextId());
        this.setSecretKey(secretService.getByOrgId(role.getOrgId()));

        record.setChatType(TYPE_ROLE);

        try {
            // 处理业务
            CompletableFuture<String> gentContent = handleRole(role, workflowExecution, taskInfo);
            log.debug("handleRole result : {}", gentContent);// 解析出生成的内容
//            record.setGenContent(gentContent);
//            List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(gentContent);
//            record.setCodeContent(codeContentList);
        } catch (Exception e) {
            log.error("解析代码块异常:{}", e.getMessage());

            streamMessagePublisher.doStuffAndPublishAnEvent(e.getMessage() , role, taskInfo, IdUtil.getSnowflakeNextId());
            streamMessagePublisher.doStuffAndPublishAnEvent("[DONE]" , role, taskInfo, IdUtil.getSnowflakeNextId());
        }

        // 处理完成之后记录更新
        record.setStatus(WorkflowStatusEnum.COMPLETED.getStatus());
        record.setEndTime(System.currentTimeMillis());
        record.setUsageTimeSeconds(RoleUtils.formatTime(record.getStartTime(), record.getEndTime()));

//        return  record;

        return null ;

    }

    @Override
    protected CompletableFuture<String> handleModifyCall(IndustryRoleEntity role,
                                      MessageEntity workflowExecution,
                                      List<CodeContent> codeContentList,
                                      MessageTaskInfo taskInfo) {

        return null;
    }

    @Override
    protected CompletableFuture<String> handleFunctionCall(IndustryRoleEntity role,
                                                           MessageEntity workflowExecution,
                                                           List<CodeContent> codeContentList,
                                                           MessageTaskInfo taskInfo) {

        return null;
    }

    @Override
    protected void processStreamCallback(IndustryRoleEntity role, MessageTaskInfo taskInfo, MessageManager msgManager) throws InterruptedException {
//        Semaphore semaphore = new Semaphore(0);
//        StringBuilder fullContent = new StringBuilder();
//        long traceBusId = taskInfo.getTraceBusId(); // IdUtil.getSnowflakeNextId() ; // taskInfo.getWorkflowRecordId() ;
//
//        msgManager.setTraceBusId(taskInfo.getTraceBusId());
//        msgManager.setWorkflowId(traceBusId);
//        msgManager.setChannelId(taskInfo.getChannelId());
//
//        qianWenLLM.getGeneration(msgManager, new ResultCallback<>() {
//            @SneakyThrows
//            @Override
//            public void onEvent(GenerationResult message) {
//
//                String msg = message.getOutput().getChoices().get(0).getMessage().getContent();
//                String finishReason = message.getOutput().getChoices().get(0).getFinishReason();
//
//                log.info("Received message: {}", JsonUtils.toJson(message));
//
//                if (finishReason != null && finishReason.equals("stop")) {
//
//
//                    taskInfo.setFlowStepContent(fullContent.toString());
//                    msg = "[DONE]";
//                    semaphore.release();
//                } else {
//                    fullContent.append(msg);
//                }
//
//                FlowStepStatusDto stepDto = new FlowStepStatusDto();
//
//                stepDto.setMessage("任务进行中...");
//                stepDto.setStepId(node.getId());
//                stepDto.setStatus(AgentConstants.STEP_PROCESS);
//                stepDto.setFlowChatText(msg);
//                stepDto.setPrint(node.isPrint());
//
//                taskInfo.setFlowStep(stepDto);
//
//                streamMessagePublisher.doStuffAndPublishAnEvent(null, //  msg.substring(preMsg.toString().length()),
//                        role,
//                        taskInfo,
//                        taskInfo.getFlowChatId());
//
//            }
//
//            @Override
//            public void onError(Exception err) {
//                log.error("Exception occurred: {}", err.getMessage());
//                semaphore.release();
//            }
//
//            @Override
//            public void onComplete() {
//                log.info("Completed");
//                semaphore.release();
//                log.info("Full content: \n{}", fullContent);
//
//                if(node.isPrint()){
//                    outputContent.append(fullContent) ;   // 将内容输出到最终结果中
//                }
//
//            }
//        });
//
//        semaphore.acquire();
    }

    @Override
    public void notifyCallback(String msg) {
        eventStepMessage("流程:" +node.getStepName() + ":" + msg,  AgentConstants.STEP_FINISH , IdUtil.getSnowflakeNextIdStr());
    }

    /**
     * 流式任务
     *
     * @param role
     * @param prompt
     * @param taskInfo
     */
    @Override
    public void processStream(Llm llm, IndustryRoleEntity role, String prompt, MessageTaskInfo taskInfo) {

        long workflowId =  IdUtil.getSnowflakeNextId();
        long traceBusId = taskInfo.getTraceBusId() ;// IdUtil.getSnowflakeNextId();

        llm.chatStream(prompt, (context, response) -> {
            AiMessage message = response.getMessage();

            if (StringUtil.hasText(message.getReasoningContent())) {
                FlowStepStatusDto stepDto = new FlowStepStatusDto();

                stepDto.setMessage("任务进行中...");
                stepDto.setStepId(node.getId());
                stepDto.setStatus(AgentConstants.STEP_PROCESS);
                stepDto.setFlowChatText(null);
                stepDto.setPrint(node.isPrint());
                stepDto.setFlowReasoningText(message.getReasoningContent());

                taskInfo.setFlowStep(stepDto);
                streamMessagePublisher.doStuffAndPublishAnEvent(null, role, taskInfo, taskInfo.getFlowChatId());

//                eventStepMessage("任务进行中...", AgentConstants.STEP_PROCESS , taskInfo.getFlowChatId()+"") ;
            }

            if (StringUtil.hasText(message.getContent())) {
                taskInfo.setReasoningText(null);

                FlowStepStatusDto stepDto = new FlowStepStatusDto();

                stepDto.setMessage("任务进行中...");
                stepDto.setStepId(node.getId());
                stepDto.setStatus(AgentConstants.STEP_PROCESS);
                stepDto.setFlowChatText(message.getContent());
                stepDto.setPrint(node.isPrint());
                stepDto.setFlowReasoningText(null);

                taskInfo.setFlowStep(stepDto);
                streamMessagePublisher.doStuffAndPublishAnEvent(null, role, taskInfo, taskInfo.getFlowChatId());

//                eventStepMessage("任务进行中...", AgentConstants.STEP_PROCESS , taskInfo.getFlowChatId()+"") ;
            }

            MessageStatus status = message.getStatus();

            if (status == MessageStatus.END) {  // 结束

                if(node.isPrint()){
                    outputContent.append(message.getFullContent()) ;   // 将内容输出到最终结果中
                }

//                MessageEntity entity = new MessageEntity();
//
//                entity.setTraceBusId(taskInfo.getTraceBusId());
//                entity.setId(workflowId);
//                entity.setContent(message.getFullContent());
//                entity.setReasoningContent(message.getFullReasoningContent());
//                entity.setFormatContent(message.getFullContent());
//                entity.setName(role.getRoleName());
//
//                entity.setRoleType("agent");
//                entity.setReaderType("html");
//
//                entity.setAddTime(new Date());
//                entity.setIcon(role.getRoleAvatar());
//
//                entity.setChannelId(taskInfo.getChannelId());
//                entity.setRoleId(role.getId());
//
//                messageService.save(entity);
            }

        });

    }

    /**
     * 流程节点消息
     *
     * @param stepMessage
     * @param status
     */
    public void eventStepMessage(String stepMessage, String status, String stepId) {

        FlowStepStatusDto stepDto = new FlowStepStatusDto();
        stepDto.setMessage(stepMessage);
        stepDto.setStepId(stepId);
        stepDto.setStatus(status);
        stepDto.setPrint(node.isPrint());

        getTaskInfo().setFlowStep(stepDto);

        streamMessagePublisher.doStuffAndPublishAnEvent(null,
                getRole(),
                getTaskInfo(),
                getTaskInfo().getFlowChatId()
        );

    }
}
