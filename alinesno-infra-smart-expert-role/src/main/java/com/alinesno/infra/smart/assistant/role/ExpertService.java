package com.alinesno.infra.smart.assistant.role;

import cn.hutool.core.util.IdUtil;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.utils.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.alinesno.infra.base.search.service.IVectorDatasetService;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.adapter.dto.DocumentVectorBean;
import com.alinesno.infra.smart.assistant.adapter.dto.VectorSearchDto;
import com.alinesno.infra.smart.assistant.adapter.service.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.adapter.service.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.api.CodeContent;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.api.prompt.PromptMessage;
import com.alinesno.infra.smart.assistant.chain.IBaseExpertService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.enums.WorkflowStatusEnum;
import com.alinesno.infra.smart.assistant.role.event.StreamMessagePublisher;
import com.alinesno.infra.smart.assistant.role.event.StreamStoreMessagePublisher;
import com.alinesno.infra.smart.assistant.role.llm.AgentFlexLLM;
import com.alinesno.infra.smart.assistant.role.llm.QianWenAuditLLM;
import com.alinesno.infra.smart.assistant.role.llm.QianWenLLM;
import com.alinesno.infra.smart.assistant.role.llm.adapter.MessageManager;
import com.alinesno.infra.smart.assistant.role.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.role.utils.TemplateParser;
import com.alinesno.infra.smart.assistant.screen.entity.ScreenEntity;
import com.alinesno.infra.smart.assistant.screen.service.IScreenService;
import com.alinesno.infra.smart.assistant.service.ISecretService;
import com.alinesno.infra.smart.assistant.template.service.ITemplateService;
import com.alinesno.infra.smart.brain.api.dto.PromptMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.entity.ChannelEntity;
import com.alinesno.infra.smart.im.entity.MessageEntity;
import com.alinesno.infra.smart.im.service.IChannelService;
import com.alinesno.infra.smart.im.service.IMessageService;
import com.alinesno.infra.smart.im.service.ITaskService;
import com.alinesno.infra.smart.utils.CodeBlockParser;
import com.plexpt.chatgpt.entity.chat.Message;
import io.jsonwebtoken.lang.Assert;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.Semaphore;

import static com.alinesno.infra.smart.im.constants.ImConstants.*;

// 创建父类 ITExpert 并声明为抽象类
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public abstract class ExpertService extends ExpertToolsService implements IBaseExpertService {

    private static final String TASK_SYNC = "sync";

    @Value("${alinesno.file.local.path:${java.io.tmpdir}}")
    private String localPath;

    // 设置当前执行任务的信息
    private IndustryRoleEntity role ;

    private MessageTaskInfo taskInfo ;

    private long msgUuid;

    private Map<String , String> secretKey ; // 组织配置密钥

    @Autowired
    private IChannelService channelService ;

    @Autowired
    private IScreenService screenService ;

    @Autowired
    private ISecretService secretService ;

    @Autowired
    protected StreamMessagePublisher streamMessagePublisher ;  // 不保存入库的消息

    @Autowired
    protected StreamStoreMessagePublisher streamStoreMessagePublisher; // 保存入库的消息

    @Autowired
    protected BaseSearchConsumer searchConsumer;

    @Autowired
    protected CloudStorageConsumer cloudStorageConsumer;

    @Autowired
    protected IVectorDatasetService vectorDatasetService;

    @Autowired
    protected QianWenLLM qianWenLLM;

    @Autowired
    protected AgentFlexLLM agentFlexLLM ;

    @Autowired
    protected QianWenAuditLLM qianWenAuditLLM;

    @Autowired
    protected IMessageService messageService;

//    @Autowired
//    private IWorkflowExecutionService workflowExecutionService;

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
    public WorkflowExecutionDto runRoleAgent(IndustryRoleEntity role, MessageEntity workflowExecution, MessageTaskInfo taskInfo) {

        WorkflowExecutionDto record = new WorkflowExecutionDto();

        // 设置业务跟踪
        long traceBusId = IdUtil.getSnowflakeNextId();
        taskInfo.setTraceBusId(traceBusId);
        record.setTraceBusId(traceBusId);

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

        if (taskInfo.isFunctionCall()) {  // 执行方法

            record.setChatType(TYPE_FUNCTION);
            Assert.isTrue(StringUtils.isNotEmpty(role.getFunctionCallbackScript()) , role.getRoleName()+" 未配置执行能力。");

            String result = null;
            if (workflowExecution == null) {
                result = "请选择操作业务.";
                record.setGenContent(result);
            } else {
                // 执行任务并记录
                String gentContent = workflowExecution.getContent();
                List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(gentContent);

                result = handleFunctionCall(role, workflowExecution, codeContentList, taskInfo);
                record.setGenContent(result);
            }

        } else if (taskInfo.isModify()) {

            record.setChatType(TYPE_MODIFY);
            Assert.isTrue(StringUtils.isNotEmpty(role.getAuditScript()) , role.getRoleName()+" 未配置审核修改能力。");

            String result = null;

            if(!taskInfo.isModifyPreBusinessId()){
                result = handleModifyCall(role, workflowExecution, null , taskInfo);
            }else{
                if (workflowExecution == null) {
                    result = "请选择操作业务.";
                } else {
                    // 执行任务并记录
                    String gentContent = workflowExecution.getContent();
                    List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(gentContent);

                    result = handleModifyCall(role, workflowExecution, codeContentList, taskInfo);
                }
            }

            record.setGenContent(result);
        } else {

            record.setChatType(TYPE_ROLE);

            // 处理业务
            String gentContent = handleRole(role, workflowExecution, taskInfo);

            // 解析出生成的内容
            record.setGenContent(gentContent);

            try {
                List<CodeContent> codeContentList = CodeBlockParser.parseCodeBlocks(gentContent);

                record.setCodeContent(codeContentList);
            } catch (Exception e) {
                log.error("解析代码块异常:{}", e.getMessage());
            }
        }

        // 处理完成之后记录更新
        record.setStatus(WorkflowStatusEnum.COMPLETED.getStatus());
        record.setEndTime(System.currentTimeMillis());
        record.setUsageTimeSeconds(RoleUtils.formatTime(record.getStartTime(), record.getEndTime()));

//        // 如果是异步的，则为插入不为更新
//        MessageEntity e = messageService.getById(record.getId());
//        streamMessagePublisher.doStuffAndPublishAnEvent(e != null && TASK_SYNC.equals(e.getFieldProp())?"同步任务完成.":"异步任务完成.",
//                getRole() ,
//                getTaskInfo() ,
//                IdUtil.getSnowflakeNextId()) ;

        return record;
    }

    /**
     * 通过channelId获取渠道信息
     * @param channelId
     * @return
     */
    protected ChannelEntity getChannelInfo(long channelId) {
        if (channelId > 0){
            return channelService.getById(channelId) ;
        }
        return null ;
    }

    /**
     * 场景唯一标识
     * @param channelId
     * @return
     */
    protected ScreenEntity getScreenInfo(long channelId) {
        if (channelId > 0){
            return screenService.getById(channelId) ;
        }
        return null ;
    }


    /**
     * 获取到任务执行详情
     * @param workflowId
     * @return
     */
    protected MessageEntity getWorkflow(long workflowId) {
        if (workflowId > 0){
            MessageEntity record = messageService.getById(workflowId) ;
            log.debug("record:{}", record);
            return  record ;
        }
        return null ;
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
                                      MessageEntity workflowExecution,
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
                                        MessageEntity workflowExecution,
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
    protected abstract String handleRole(IndustryRoleEntity role, MessageEntity workflowExecution, MessageTaskInfo taskInfo);

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

        R<String> r = cloudStorageConsumer.upload(file) ;

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

        // 异步任务，则更新任务状态
//        MessageEntity record = .getById(taskInfo.getWorkflowRecordId());
//        if (record != null) {
//            record.setFieldProp(TASK_SYNC);
//            messageService.update(record);
//        }

        com.alibaba.dashscope.common.Message promptMsg = com.alibaba.dashscope.common.Message.builder()
                .role("user")
                .content(prompt)
                .build();

        MessageManager msgManager = new MessageManager(10);
        msgManager.add(promptMsg);

        processStreamCallback(role, taskInfo , msgManager);
    }

    /**
     * 流式任务
     * @param role
     * @param messages
     * @param taskInfo
     */
    @SneakyThrows
    public void processStream(IndustryRoleEntity role, List<PromptMessage> messages, MessageTaskInfo taskInfo) {
        // 异步任务，则更新任务状态
//        MessageEntity record = workflowExecutionService.getById(taskInfo.getWorkflowRecordId());
//        if (record != null) {
//            record.setFieldProp(TASK_SYNC);
//            workflowExecutionService.update(record);
//        }

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

    /**
     * 同步任务完成之后回调
     * @param role 角色
     * @param taskInfo 任务信息
     * @param content 回调内容
     */
    public void processSyncCallback(IndustryRoleEntity role, MessageTaskInfo taskInfo , String content){

        MessageEntity entity = new MessageEntity();

        entity.setTraceBusId(taskInfo.getTraceBusId());

        entity.setContent(content) ;
        entity.setFormatContent(content);
        entity.setName(role.getRoleName());

        entity.setRoleType("agent");
        entity.setReaderType("html");

        entity.setAddTime(new Date());
        entity.setIcon(role.getRoleAvatar());

        entity.setChannelId(taskInfo.isScreen()?taskInfo.getScreenId():taskInfo.getChannelId());
        entity.setRoleId(role.getId()) ;

        messageService.save(entity);
        taskInfo.setBusinessId(entity.getId()+"");

        streamMessagePublisher.doStuffAndPublishAnEvent("同步任务完成.",
                getRole() ,
                getTaskInfo(),
                IdUtil.getSnowflakeNextId()) ;
    }

    private void processStreamCallback(IndustryRoleEntity role, MessageTaskInfo taskInfo, MessageManager msgManager) throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        StringBuilder fullContent = new StringBuilder();
        long workflowId = IdUtil.getSnowflakeNextId() ; // taskInfo.getWorkflowRecordId() ;

        msgManager.setTraceBusId(taskInfo.getTraceBusId());
        msgManager.setWorkflowId(workflowId);
        msgManager.setChannelId(taskInfo.getChannelId());

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

                streamMessagePublisher.doStuffAndPublishAnEvent(msg , role, taskInfo, workflowId);
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
                entity.setId(msgManager.getWorkflowId());
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

                streamMessagePublisher.doStuffAndPublishAnEvent("流式任务完成.",
                        getRole() ,
                        getTaskInfo() ,
                        IdUtil.getSnowflakeNextId()) ;
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

        MessageEntity message =  messageService.saveMessage(role, taskInfo, msg) ;

        streamMessagePublisher.doStuffAndPublishAnEvent(msg ,
                getRole() ,
                getTaskInfo() ,
                message.getId()) ;
    }

    /**
     * 频道知识库搜索
     */
    public String searchChannelKnowledgeBase(String content , String datasetId , int topK){

        VectorSearchDto dto = new VectorSearchDto() ;
        dto.setDatasetId(Long.parseLong(datasetId)) ;
        dto.setSearchText(content) ;
        dto.setTopK(topK) ;

        List<DocumentVectorBean> result = vectorDatasetService.search(dto) ;

        StringBuilder sb = new StringBuilder();

        if(!CollectionUtils.isEmpty(result)){
            for(DocumentVectorBean bean : result){
                sb.append(bean.getDocument_content()).append("\n");
            }
        }

        return sb.toString() ;
    }



}