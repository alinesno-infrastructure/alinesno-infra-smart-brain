package com.alinesno.infra.smart.assistant.role;

import cn.hutool.core.util.IdUtil;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.utils.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.adapter.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.adapter.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.api.prompt.PromptMessage;
import com.alinesno.infra.smart.assistant.chain.IBaseExpertService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.WorkflowExecutionEntity;
import com.alinesno.infra.smart.assistant.enums.WorkflowStatusEnum;
import com.alinesno.infra.smart.assistant.role.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.role.llm.QianWenAuditLLM;
import com.alinesno.infra.smart.assistant.role.llm.QianWenLLM;
import com.alinesno.infra.smart.assistant.role.llm.adapter.MessageManager;
import com.alinesno.infra.smart.assistant.role.utils.CodeBlockParser;
import com.alinesno.infra.smart.assistant.role.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.role.utils.TemplateParser;
import com.alinesno.infra.smart.assistant.service.IWorkflowExecutionService;
import com.alinesno.infra.smart.assistant.template.service.ITemplateService;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.im.service.ITaskService;
import com.plexpt.chatgpt.entity.chat.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import static com.alinesno.infra.smart.im.constants.ImConstants.*;

// 创建父类 ITExpert 并声明为抽象类
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public abstract class ExpertService extends ExpertToolsService implements IBaseExpertService {

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath;

    // 设置当前执行任务的信息
    private IndustryRoleEntity role ;

    private MessageTaskInfo taskInfo ;

    private long msgUuid;

    @Autowired
    private StreamMessagePublisher streamMessagePublisher ;

    @Autowired
    protected BaseSearchConsumer searchConsumer;

    @Autowired
    protected CloudStorageConsumer cloudStorageConsumer;

    @Autowired
    protected QianWenLLM qianWenLLM;

    @Autowired
    protected QianWenAuditLLM qianWenAuditLLM;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IWorkflowExecutionService workflowExecutionService;

    @Autowired
    private ITemplateService templateService ;

    /**
     * 执行角色
     *
     * @param role              角色信息
     * @param workflowExecution 工作流执行实体
     * @param taskInfo          消息任务信息
     * @return
     */
    @Override
    public WorkflowExecutionDto runRoleAgent(IndustryRoleEntity role, WorkflowExecutionEntity workflowExecution, MessageTaskInfo taskInfo) {

        WorkflowExecutionDto recordDto = new WorkflowExecutionDto();

        // 任务开始记录
        WorkflowExecutionEntity record = new WorkflowExecutionEntity();
        record.setRoleId(role.getId());
        record.setChannelId(taskInfo.getChannelId());
        record.setBuildNumber(1);
        record.setStartTime(System.currentTimeMillis());
        record.setStatus(WorkflowStatusEnum.IN_PROGRESS.getStatus());

        workflowExecutionService.saveRecord(record);

        log.debug("--->>>> record.getId() = {}" , record.getId());

        // 设置workflowRecordId用于异步流场景使用
        taskInfo.setWorkflowRecordId(record.getId());

        this.setRole(role);
        this.setTaskInfo(taskInfo);
        this.setMsgUuid(IdUtil.getSnowflakeNextId());

        if (taskInfo.isFunctionCall()) {  // 执行方法

            record.setFieldProp(TYPE_FUNCTION);

            String result = null;
            if (workflowExecution == null) {
                result = "请选择操作业务.";
            } else {
                // 执行任务并记录
                String gentContent = workflowExecution.getGenContent();
                List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(gentContent);

                result = handleFunctionCall(role, workflowExecution, codeContentList, taskInfo);

                BeanUtils.copyProperties(record, recordDto);
                recordDto.setGenContent(result);
            }

        } else if (taskInfo.isModify()) {

            record.setFieldProp(TYPE_MODIFY);

            String result = null;
            if (workflowExecution == null) {
                result = "请选择操作业务.";
            } else {
                // 执行任务并记录
                String gentContent = workflowExecution.getGenContent();
                List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(gentContent);

                result = handleModifyCall(role, workflowExecution, codeContentList, taskInfo);
            }

            BeanUtils.copyProperties(record, recordDto);
            recordDto.setGenContent(result);
        } else {

            record.setFieldProp(TYPE_ROLE);

            // 处理业务
            String gentContent = handleRole(role, workflowExecution, taskInfo);

            // 解析出生成的内容
            record.setGenContent(gentContent);

            try {
                List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(gentContent);

                BeanUtils.copyProperties(record, recordDto);
                recordDto.setCodeContent(codeContentList);
            } catch (Exception e) {
                log.error("解析代码块异常:{}", e.getMessage());
            }
        }

        // 处理完成之后记录更新
        record.setStatus(WorkflowStatusEnum.COMPLETED.getStatus());
        record.setEndTime(System.currentTimeMillis());
        record.setUsageTimeSeconds(RoleUtils.formatTime(record.getStartTime(), record.getEndTime()));

        workflowExecutionService.update(record);

        return recordDto;
    }

    /**
     * 内容修改调用
     *
     * @param role
     * @param workflowExecution
     * @param codeContentList
     * @param taskInfo
     * @return
     */
    protected String handleModifyCall(IndustryRoleEntity role,
                                      WorkflowExecutionEntity workflowExecution,
                                      List<CodeContent> codeContentList,
                                      MessageTaskInfo taskInfo) {
        log.debug("handleModifyCall:{}", taskInfo);
        return "已接收到处理消息";
    }

    /**
     * 处理函数调用
     *
     * @param role
     * @param workflowExecution
     * @param codeContentList
     * @param taskInfo
     * @return
     */
    protected String handleFunctionCall(IndustryRoleEntity role,
                                        WorkflowExecutionEntity workflowExecution,
                                        List<CodeContent> codeContentList,
                                        MessageTaskInfo taskInfo) {
        log.debug("handleFunctionCall:{}", taskInfo);
        return "已接收到任务执行消息";
    }

    /**
     * 处理业务流
     *
     * @param role
     * @param workflowExecution
     * @param taskInfo
     */
    protected abstract String handleRole(IndustryRoleEntity role, WorkflowExecutionEntity workflowExecution, MessageTaskInfo taskInfo);

    /**
     * 查询历史消息
     *
     * @param taskInfo
     * @return
     */
    protected List<PromptMessageDto> queryChannelLastMessage(MessageTaskInfo taskInfo) {

        long channel = taskInfo.getChannelId();
        long accountId = taskInfo.getAccountId();
        long roleId = taskInfo.getRoleId();
        int siz = 10;

        return messageService.queryChannelLastMessage(channel, accountId, roleId, siz);
    }

    /**
     * 解析消息
     *
     * @param promptContent
     * @param params
     * @return
     */
    protected List<PromptMessage> parseMessage(String promptContent, String params) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("label1", params);
        return getPromptMessages(promptContent, params, paramMap);
    }

    /**
     * 解析消息
     *
     * @param promptContent
     * @param params
     * @return
     */
    protected List<PromptMessage> parseMessage(String promptContent, String params, String promptDoc) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("label1", params);
        paramMap.put("prompt_doc", promptDoc);
        return getPromptMessages(promptContent, params, paramMap);
    }

    /**
     * 获取到PromptMessage信息列表
     *
     * @param askInfo 用户咨询信息
     * @return
     */
    public List<PromptMessage> promptMessages(String askInfo, IndustryRoleEntity role) {
        return parseMessage(role.getPromptContent(), askInfo);
    }

    /**
     * 获取提示消息
     *
     * @param promptContent
     * @param params
     * @param paramMap
     * @return
     */
    @NotNull
    private static List<PromptMessage> getPromptMessages(String promptContent, String params, Map<String, Object> paramMap) {
        List<PromptMessageDto> promptMessageList = JSONArray.parseArray(promptContent, PromptMessageDto.class);
        List<PromptMessage> messages = new ArrayList<>();

        for (PromptMessageDto msg : promptMessageList) {
            PromptMessage message = null;
            // 模板解析处理
            String contentTemplate = msg.getContent().trim();

            if (params != null) {
                contentTemplate = TemplateParser.parserTemplate(contentTemplate, paramMap);
            }
            if (Message.Role.SYSTEM.getValue().equals(msg.getRole())) {
                message = PromptMessage.ofSystem(contentTemplate);
            } else if (Message.Role.ASSISTANT.getValue().equals(msg.getRole())) {
                message = PromptMessage.ofAssistant(contentTemplate);
            } else if (Message.Role.USER.getValue().equals(msg.getRole())) {
                message = PromptMessage.of(contentTemplate);
            }

            if (message != null) {
                messages.add(message);
            }
        }

        return messages;
    }

    public static String clearMessage(String message) {
        String[] words = message.split(" ");

        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.startsWith("@") && !word.startsWith("#")) {
                result.append(word);
            }
        }

        return result.toString();
    }

    /**
     * 生成语音mp3并上传到oss
     */
    public String generateAudio(String voice, String text) {

        File file = qianWenAuditLLM.generateAudit(voice, text);

        R<String> r = cloudStorageConsumer.upload(file, "qiniu-kodo", progress -> {
            System.out.println("total bytes: " + progress.getTotalBytes());
            System.out.println("current bytes: " + progress.getCurrentBytes());
            System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
        });

        return r.getData();
    }

    /**
     * 上传文件到存储平台
     */
    public String uploadFile(String fileAbcPath) {
        File f = new File(fileAbcPath);

        log.info("uploadFile:{}", f.getAbsolutePath());

        if(!f.exists()){
            return "文件"+fileAbcPath+"不存在.";
        }

        R<String> r = cloudStorageConsumer.uploadCallbackUrl(f, "qiniu-kodo-pub");
        return r.getData();
    }

    @SneakyThrows
    public void processStream(IndustryRoleEntity role, String prompt, MessageTaskInfo taskInfo) {
        com.alibaba.dashscope.common.Message promptMsg = com.alibaba.dashscope.common.Message.builder()
                .role("user")
                .content(prompt)
                .build();

        MessageManager msgManager = new MessageManager(10);
        msgManager.add(promptMsg);

        processStreamCallback(role, taskInfo , msgManager);
    }

    @SneakyThrows
    public void processStream(IndustryRoleEntity role, MessageManager messages, MessageTaskInfo taskInfo) {

    }

    /**
     * 流式任务
     * @param role
     * @param messages
     * @param taskInfo
     */
    @SneakyThrows
    public void processStream(IndustryRoleEntity role, List<PromptMessage> messages, MessageTaskInfo taskInfo) {

        MessageManager msgManager = new MessageManager(10);

        for(PromptMessage m : messages) {
            com.alibaba.dashscope.common.Message msg = com.alibaba.dashscope.common.Message.builder()
                    .role(m.getRole())
                    .content(m.getContent())
                    .build();
            msgManager.add(msg);
        }

        processStreamCallback(role, taskInfo, msgManager);
    }

    private void processStreamCallback(IndustryRoleEntity role, MessageTaskInfo taskInfo, MessageManager msgManager) throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        StringBuilder fullContent = new StringBuilder();
        long workflowId = taskInfo.getWorkflowRecordId() ;
        long bId = workflowId ; // IdUtil.getSnowflakeNextId() ;

        log.debug("--->>>> record.getId() = {}" , taskInfo.getWorkflowRecordId());
        msgManager.setWorkflowId(workflowId);

        qianWenLLM.getGeneration(msgManager, new ResultCallback<>() {
            @SneakyThrows
            @Override
            public void onEvent(GenerationResult message) {

                String msg = message.getOutput().getChoices().get(0).getMessage().getContent();
                String finishReason = message.getOutput().getChoices().get(0).getFinishReason() ;

                log.info("Received message: {}", JsonUtils.toJson(message));

                if (finishReason != null && finishReason.equals("stop")) {
                    msg = "[DONE]" ;
                    semaphore.release();
                }else{
                    fullContent.append(msg);
                }

                streamMessagePublisher.doStuffAndPublishAnEvent(msg , role, taskInfo, bId);
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

                log.debug("--->>>> record.getId() = {}" , taskInfo.getWorkflowRecordId());

                // 记录执行内容信息
                WorkflowExecutionDto recordDto = new WorkflowExecutionDto() ;
                WorkflowExecutionEntity record = workflowExecutionService.getById(msgManager.getWorkflowId());
                log.debug("record:{}", record);

                BeanUtils.copyProperties(record, recordDto);
                recordDto.setGenContent(fullContent.toString()) ; // "任务处理完成: <span class='mention-business'>#"+msgManager.getWorkflowId()+"</span>");

                // 处理完成之后记录更新
                record.setStatus(WorkflowStatusEnum.COMPLETED.getStatus());
                record.setEndTime(System.currentTimeMillis());
                record.setUsageTimeSeconds(RoleUtils.formatTime(record.getStartTime(), record.getEndTime()));
                record.setGenContent(fullContent.toString());

                workflowExecutionService.update(record);

                // 更新消息并记录消息运行情况
                ITaskService taskService = SpringUtils.getBean(ITaskService.class);
                taskService.handleWorkflowMessageWithoutMessage(taskInfo, recordDto);

            }
        }) ;

        semaphore.acquire();
    }

    /**
     * 运行回调通知到界面
     * 通知内容:msg
     */
    public void notifyCallback(String msg) {
        log.info("notifyCallback:{}", msg);
        streamMessagePublisher.doStuffAndPublishAnEvent(msg ,
                getRole() ,
                getTaskInfo() ,
                IdUtil.getSnowflakeNextId()) ;

        WorkflowExecutionDto recordDto = new WorkflowExecutionDto() ;

        recordDto.setGenContent(msg) ;
        recordDto.setRoleId(role.getId());
        recordDto.setChannelId(taskInfo.getChannelId());
        recordDto.setBuildNumber(1);
        recordDto.setStartTime(System.currentTimeMillis());
        recordDto.setStatus(WorkflowStatusEnum.COMPLETED.getStatus());

        // 更新消息并记录消息运行情况
        ITaskService taskService = SpringUtils.getBean(ITaskService.class);
        taskService.handleWorkflowMessageWithoutMessage(taskInfo, recordDto);

        messageService.saveMessage(role, taskInfo, msg) ;
    }

}