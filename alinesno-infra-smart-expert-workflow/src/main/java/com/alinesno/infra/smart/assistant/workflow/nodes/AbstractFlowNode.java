// AbstractWorkflowNode.java
package com.alinesno.infra.smart.assistant.workflow.nodes;

import cn.hutool.core.util.IdUtil;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.MessageStatus;
import com.agentsflex.core.util.StringUtil;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.assistant.adapter.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.adapter.event.StreamStoreMessagePublisher;
import com.alinesno.infra.smart.assistant.role.llm.QianWenNewApiLLM;
import com.alinesno.infra.smart.assistant.service.ILlmModelService;
import com.alinesno.infra.smart.assistant.workflow.FlowExpertService;
import com.alinesno.infra.smart.assistant.workflow.dto.FlowNodeDto;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowExecutionEntity;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowNodeExecutionEntity;
import com.alinesno.infra.smart.assistant.workflow.nodes.variable.GlobalVariables;
import com.alinesno.infra.smart.assistant.workflow.parse.TextReplacer;
import com.alinesno.infra.smart.im.dto.FlowStepStatusDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.reactivex.Flowable;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 这是一个抽象父类，继承自 WorkflowNode 接口。
 * 它作为所有具体工作流节点类的基类，包含了所有工作流节点可能通用的属性和方法。
 * 由于它是抽象类，不能直接实例化，具体的工作流节点类需要继承该类并实现必要的逻辑。
 * 使用 Lombok 的 @Data 注解自动生成 getter、setter、toString、equals 和 hashCode 方法。
 */
@Slf4j
@Setter
public abstract class AbstractFlowNode implements FlowNode {

    @Autowired
    protected StreamMessagePublisher streamMessagePublisher ;  // 不保存入库的消息

    @Autowired
    protected StreamStoreMessagePublisher streamStoreMessagePublisher; // 保存入库的消息

    @Autowired
    protected QianWenNewApiLLM qianWenNewApiLLM ;

    @Autowired
    protected ILlmModelService llmModelService; ;

    /**
     * 流程节点DTO，用于存储流程节点的相关信息
     */
    protected FlowNodeDto node;

    /**
     * 最后输出的内容
     */
    private StringBuilder outputContent ;

    /**
     * 流程执行实体，代表整个流程的执行实例
     */
    protected FlowExecutionEntity flowExecution;

    /**
     * 流程节点执行实体，记录流程中每个节点的执行情况
     */
    protected FlowNodeExecutionEntity flowNodeExecution;

    /**
     * 输出参数的集合，用于存储流程节点执行后的输出数据
     */
    protected Map<String, Object> output;

    /**
     * 消息任务信息，包含与消息任务相关的信息和操作
     */
    protected MessageTaskInfo taskInfo;

    /**
     * 行业角色实体，定义在流程中所涉及的行业角色信息
     */
    protected IndustryRoleEntity role;

    /**
     * 工作流执行实体，记录工作流执行过程中的状态和数据变化
     */
    protected MessageEntity workflowExecution;

    /**
     * 流程专家服务，提供流程相关的业务逻辑处理服务
     */
    protected FlowExpertService flowExpertService;

    /**
     * 节点类型
     */
    private String type ;

    /**
     * 全局变量
     */
    private GlobalVariables globalVariables;

    /**
     * 执行节点任务
     *
     * @param node
     * @param flowExecution
     * @param flowNodeExecution
     * @param output
     */
    @SneakyThrows
    public void executeNode(FlowNodeDto node,
                            FlowExecutionEntity flowExecution ,
                            FlowNodeExecutionEntity flowNodeExecution,
                            Map<String, Object> output,
                            StringBuilder outputContent ,
                            MessageTaskInfo taskInfo,
                            IndustryRoleEntity role,
                            MessageEntity workflowExecution,
                            FlowExpertService flowExpertService) {

        boolean isPrintContent = isPrintContent(node) ;
        log.debug("isPrintContent(node) = {}" , isPrintContent);

        node.setPrint(isPrintContent);
        flowExpertService.setNode(node);
        flowExpertService.setOutputContent(outputContent);

        // 设置运行参数变量
        this.setNode(node);
        this.outputContent = outputContent ;
        this.setFlowExecution(flowExecution);
        this.setFlowNodeExecution(flowNodeExecution);
        this.setOutput(output);
        this.setTaskInfo(taskInfo);
        this.setRole(role);
        this.setWorkflowExecution(workflowExecution);
        this.setFlowExpertService(flowExpertService);

        log.debug("执行节点任务:{} , node:{}", node.getType(), node.getProperties());
        eventStepMessage(AgentConstants.STEP_START) ;

        // 如果是开始节点，则配置全局变量
        if ("start".equals(node.getType())) {

            List<String> messages = new ArrayList<>();
            handleHistoryMessage(messages , taskInfo.getChannelId());

            String preContent = workflowExecution==null?"" : workflowExecution.getContent() ;

            GlobalVariables globalVariables = new GlobalVariables(preContent , taskInfo.getChannelId() , messages) ;
            this.setGlobalVariables(globalVariables);

            String historyContentAsString = String.join(",", globalVariables.getHistoryContent());

            // 设置全局变量
            output.put("global.time", globalVariables.getTime()) ;
            output.put("global.pre_content", globalVariables.getPreContent()) ;
            output.put("global.channelId", globalVariables.getChannelId()) ;
            output.put("global.history_content", historyContentAsString) ;
        }

        handleNode();
        Thread.sleep(500); // TODO fix:处理节点状态的问题
        log.debug("output = {}" , output);

        // 结束流程节点
        eventStepMessage(AgentConstants.STEP_FINISH) ;
    }

    /**
     * 流程节点消息
     * @param status
     */
    protected void eventStepMessage(String status) {
        eventStepMessage("流程:" +node.getStepName(),  status);
    }

    /**
     * 流程节点消息
     * @param message
     * @param status
     */
    protected void eventStepMessage(String message , String status) {
        eventStepMessage(message ,  status , node.getId());
    }

    public boolean isPrintContent(FlowNodeDto dto) {
        // 设置isPrint
        Object nodeData = dto.getProperties().get("node_data") ;
        if(nodeData != null){
            JSONObject nodeDataJson = JSONObject.parseObject(nodeData.toString()) ;
            if (Objects.nonNull(nodeDataJson)) {
                try {
                    return nodeDataJson.getBoolean("isPrint");
                }catch (Exception e){
                    log.error("判断是否打印输出异常:{}" , e.getMessage());
                }
            }
        }
        return false ;
    }


    /**
     * 流程节点消息
     * @param message
     * @param status
     * @param stepId
     */
    protected void eventStepMessage(String message , String status , String stepId) {
        flowExpertService.eventStepMessage(message ,  status , stepId);
    }

    /**
     * 运行事件消息
     * @param msg
     */
    protected void eventMessage(String msg) {
        streamMessagePublisher.doStuffAndPublishAnEvent(msg ,
                role,
                taskInfo,
                taskInfo.getFlowChatId()
        );

    }


    /**
     * 保存所有消息
     * @param msg
     */
    protected void eventMessageCallbackMessage(String msg) {

        outputContent.append(msg) ;  // 每个节点的内容都添加到outputContent中
        outputContent.append("\n\n"); // 添加换行

        // 如果是最后一个节点，则将所有内容拼接保存到数据库中
        if (node.isLastNode()) {

            MessageEntity entity = new MessageEntity();

            entity.setTraceBusId(taskInfo.getTraceBusId());
            entity.setId(taskInfo.getFlowChatId()) ; // 消息保存的业务Id
            entity.setContent(outputContent.toString());
            entity.setFormatContent(outputContent.toString());
            entity.setName(role.getRoleName());

            entity.setRoleType("agent");
            entity.setReaderType("html");

            entity.setAddTime(new Date());
            entity.setIcon(role.getRoleAvatar());

            entity.setChannelId(taskInfo.getChannelId());
            entity.setRoleId(role.getId());

            flowExpertService.getMessageService().save(entity);

            streamMessagePublisher.doStuffAndPublishAnEvent(outputContent.toString() ,
                    role,
                    taskInfo,
                    taskInfo.getFlowChatId()
            );
        }

    }

    /**
     * 处理本次会话的历史记录
     * @param messages
     * @param channelId
     */
    private void handleHistoryMessage(List<String> messages, long channelId) {

        // 假设这是你想要设置的最大字符数
        int maxLength = 131072; // 或者其他你认为合适的最大值

        log.debug("历史记录：");

        LambdaQueryWrapper<MessageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageEntity::getChannelId, channelId)
                .orderByDesc(MessageEntity::getAddTime)
                .last("limit 500");;
        List<MessageEntity> chatMessageDtoList = flowExpertService.getMessageService().list(wrapper) ;
        StringBuilder allMessagesText = new StringBuilder();

        int tokenLength;

        for (MessageEntity dto : chatMessageDtoList) {
            String chatText = !StringUtils.hasLength(dto.getFormatContent()) ? dto.getContent() : dto.getFormatContent();
            tokenLength = chatText.length();

            // 如果是 "agent" 或 "person" 角色的消息，并且总长度加上新消息长度不超过最大长度，则添加消息
            if (("agent".equals(dto.getRoleType()) || "person".equals(dto.getRoleType()))  && (allMessagesText.length() + tokenLength <= maxLength)) {

                messages.add(chatText) ;

                // 更新所有消息文本的总长度
                allMessagesText.append(chatText);
            }
        }

        log.debug("历史记录处理完毕，总消息长度为: {}", allMessagesText.length());

    }

    /**
     * 处理节点任务
     */
    protected abstract void handleNode() ;

    /**
     * 获取字符串的CompletableFuture
     * @param prompt
     * @return
     */
    @NotNull
    protected CompletableFuture<String> getStringCompletableFuture(String prompt) {
        return CompletableFuture.supplyAsync(() -> {

            StringBuilder outputStr = new StringBuilder();

            List<Message> messages = new ArrayList<>();
            messages.add(qianWenNewApiLLM.createMessage(Role.USER, prompt));

            Flowable<GenerationResult> result = qianWenNewApiLLM.streamReasoningCall(messages) ; // "qwen-max-2025-01-25"

            long tmpMsgId = taskInfo.getFlowChatId() ;

            StringBuilder preMsg = new StringBuilder() ;

            result.blockingForEach(message -> {

                String msg = message.getOutput().getChoices().get(0).getMessage().getContent();
                String finishReason = message.getOutput().getChoices().get(0).getFinishReason() ;

                log.debug(msg);

                String newMsg = msg.substring(preMsg.toString().length()) ;

                eventNodeMessage(newMsg);

                preMsg.setLength(0);
                preMsg.append(msg) ;

                if(finishReason != null && finishReason.equals("stop")){
                    outputStr.append(msg);
                }
            });

            eventStepMessage("思考结束" , AgentConstants.STEP_FINISH, String.valueOf(tmpMsgId)) ;

            // 生成任务结果
            return outputStr.toString() ;
        });
    }

    /**
     * 流式任务
     * @param role
     * @param prompt
     * @param taskInfo
     */
    @SneakyThrows
    protected CompletableFuture<String> processStream(Llm llm , IndustryRoleEntity role, String prompt, MessageTaskInfo taskInfo) {

        return CompletableFuture.supplyAsync(() -> {

            StringBuilder outputStr = new StringBuilder();
            long workflowId = IdUtil.getSnowflakeNextId() ;

            llm.chatStream(prompt, (context, response) -> {
                AiMessage message = response.getMessage();

//            // TODO 流程节点输出思考内容
//            if(StringUtil.hasText(message.getReasoningContent())){
//                taskInfo.setReasoningText(message.getReasoningContent());
//                streamMessagePublisher.doStuffAndPublishAnEvent(null , role, taskInfo, workflowId);
//            }

                if(StringUtil.hasText(message.getContent())){
                    taskInfo.setReasoningText(null);
                    eventNodeMessage(message.getContent() );
                }

                MessageStatus status =  message.getStatus() ;
                if(status == MessageStatus.END){  // 结束

                    MessageEntity entity = new MessageEntity();

                    entity.setTraceBusId(taskInfo.getTraceBusId());
                    entity.setId(workflowId) ;
                    entity.setContent(message.getFullContent()) ;
                    entity.setReasoningContent(message.getFullReasoningContent());
                    entity.setFormatContent(message.getFullContent());
                    entity.setName(role.getRoleName());

                    entity.setRoleType("agent");
                    entity.setReaderType("html");

                    entity.setAddTime(new Date());
                    entity.setIcon(role.getRoleAvatar());

                    entity.setChannelId(taskInfo.getChannelId()) ;
                    entity.setRoleId(role.getId()) ;

                    flowExpertService.getMessageService().save(entity);
                    outputStr.append(message.getFullContent()) ;
                }

            });

            return outputStr.toString() ;
        });

    }

    protected void eventNodeMessage(String newMsg) {

        if(newMsg == null || newMsg.isEmpty()){
            return ;
        }

        FlowStepStatusDto stepDto = new FlowStepStatusDto() ;
        stepDto.setMessage("任务进行中...") ;
        stepDto.setStepId(node.getId()) ;
        stepDto.setStatus(AgentConstants.STEP_PROCESS);
        stepDto.setFlowChatText(newMsg) ;
        stepDto.setPrint(node.isPrint());

        taskInfo.setFlowStep(stepDto);

        streamMessagePublisher.doStuffAndPublishAnEvent(null , //  msg.substring(preMsg.toString().length()),
                role,
                taskInfo,
                taskInfo.getFlowChatId());
    }


    protected CompletableFuture<String> getAiChatResultAsync(Llm llm, String prompt) {
        CompletableFuture<String> future = new CompletableFuture<>();
        AtomicReference<String> outputStr = new AtomicReference<>("");

        // 创建一个 final 局部变量来持有 taskInfo 的引用
        final MessageTaskInfo localTaskInfo = taskInfo;

        try {
            llm.chatStream(prompt, (context, response) -> {
                AiMessage message = response.getMessage();
                System.out.println(">>>> " + message);

                FlowStepStatusDto stepDto = new FlowStepStatusDto();
                stepDto.setMessage("任务进行中...");
                stepDto.setStepId(node.getId());
                stepDto.setStatus(AgentConstants.STEP_PROCESS);
                stepDto.setFlowChatText(message.getContent());
                stepDto.setPrint(node.isPrint());

                synchronized (localTaskInfo) {
                    localTaskInfo.setFlowStep(stepDto);
                }

                try {
                    synchronized (localTaskInfo) {
                        if (message.getStatus() == MessageStatus.END) {
                            outputStr.set(message.getFullContent());
                            stepDto.setStatus(AgentConstants.STEP_FINISH);
                            streamMessagePublisher.doStuffAndPublishAnEvent(null, role, localTaskInfo, localTaskInfo.getFlowChatId());
                            future.complete(outputStr.get());
                        } else {
                            streamMessagePublisher.doStuffAndPublishAnEvent(null, role, localTaskInfo, localTaskInfo.getFlowChatId());
                        }
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
     * 替换占位符
     * @return
     */
    protected String replacePlaceholders(String text){
       return TextReplacer.replacePlaceholders(text, output);
    }

}