package com.alinesno.infra.smart.assistant.workflow;

import cn.hutool.core.util.IdUtil;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.utils.JsonUtils;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.enums.AssistantConstants;
import com.alinesno.infra.smart.assistant.role.ExpertService;
import com.alinesno.infra.smart.assistant.role.context.AgentConstants;
import com.alinesno.infra.smart.assistant.role.llm.adapter.MessageManager;
import com.alinesno.infra.smart.assistant.workflow.dto.FlowNodeDto;
import com.alinesno.infra.smart.assistant.workflow.service.IFlowService;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 流程节点编排引擎，执行多个流程节点并返回结果任务
 */
@Scope("prototype")
@Slf4j
@Service(AssistantConstants.PREFIX_ASSISTANT_FLOW)
public class FlowExpertService extends ExpertService {

    @Autowired
    private IFlowService flowService ;

    @Setter
    private FlowNodeDto node ;

    @Override
    protected String handleRole(IndustryRoleEntity role,
                                MessageEntity workflowExecution,
                                MessageTaskInfo taskInfo) {

        String result = flowService.runRoleFlow(taskInfo , role , workflowExecution , this);
        log.debug("handleRole result : {}" , result);

        return "执行结束";
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

    @Override
    protected void processStreamCallback(IndustryRoleEntity role, MessageTaskInfo taskInfo, MessageManager msgManager) throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        StringBuilder fullContent = new StringBuilder();
        long traceBusId = taskInfo.getTraceBusId() ; // IdUtil.getSnowflakeNextId() ; // taskInfo.getWorkflowRecordId() ;

        msgManager.setTraceBusId(taskInfo.getTraceBusId());
        msgManager.setWorkflowId(traceBusId);
        msgManager.setChannelId(taskInfo.getChannelId());

        qianWenLLM.getGeneration(msgManager, new ResultCallback<>() {
            @SneakyThrows
            @Override
            public void onEvent(GenerationResult message) {

                String msg = message.getOutput().getChoices().get(0).getMessage().getContent();
                String finishReason = message.getOutput().getChoices().get(0).getFinishReason() ;

                log.info("Received message: {}", JsonUtils.toJson(message));

                if (finishReason != null && finishReason.equals("stop")) {
                    taskInfo.setFlowStepContent(fullContent.toString());
                    msg = "[DONE]" ;
                    semaphore.release();
                }else{
                    fullContent.append(msg);
                }

//                streamMessagePublisher.doStuffAndPublishAnEvent(msg , role, taskInfo, traceBusId);

                FlowStepStatusDto stepDto = new FlowStepStatusDto() ;
                stepDto.setMessage("任务进行中...") ;
                stepDto.setStepId(node.getId()) ;
                stepDto.setStatus(AgentConstants.STEP_PROCESS);
                stepDto.setFlowChatText(msg) ;

                taskInfo.setFlowStep(stepDto);

                streamMessagePublisher.doStuffAndPublishAnEvent(null , //  msg.substring(preMsg.toString().length()),
                        role,
                        taskInfo,
                        taskInfo.getTraceBusId());

            }

            @Override
            public void onError(Exception err) {
                log.error("Exception occurred: {}", err.getMessage());
                semaphore.release();
            }

            @Override
            public void onComplete() {
                log.info("Completed");
                semaphore.release();
                log.info("Full content: \n{}", fullContent);

                MessageEntity entity = new MessageEntity();

                entity.setTraceBusId(taskInfo.getTraceBusId());
                entity.setId(IdUtil.getSnowflakeNextId()) ; // msgManager.getWorkflowId());
                entity.setContent(fullContent.toString()) ;
                entity.setFormatContent(fullContent.toString());
                entity.setName(role.getRoleName());

                entity.setRoleType("agent");
                entity.setReaderType("html");

                entity.setAddTime(new Date());
                entity.setIcon(role.getRoleAvatar());

                entity.setChannelId(msgManager.getChannelId());
                entity.setRoleId(role.getId()) ;

                messageService.save(entity);


//                streamMessagePublisher.doStuffAndPublishAnEvent("流式任务完成.",
//                        getRole() ,
//                        getTaskInfo() ,
//                        traceBusId) ;

            }
        }) ;

        semaphore.acquire();
    }

}
