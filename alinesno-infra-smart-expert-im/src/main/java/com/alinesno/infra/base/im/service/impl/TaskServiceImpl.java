package com.alinesno.infra.base.im.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.base.im.dto.ChatMessageDto;
import com.alinesno.infra.base.im.dto.TableItem;
import com.alinesno.infra.base.im.entity.MessageEntity;
import com.alinesno.infra.base.im.service.IMessageService;
import com.alinesno.infra.base.im.service.ITaskService;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.entity.MessageQueueEntity;
import com.alinesno.infra.smart.assistant.service.IMessageQueueService;
import com.alinesno.infra.smart.im.enums.TaskStatusEnums;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class TaskServiceImpl implements ITaskService {

    private static final Map<Long , TaskInfo> taskBox = new HashMap<>() ;

    @Autowired
    private IMessageService messageService ;

    @Autowired
    private IMessageQueueService messageQueueService;

//    @Autowired
//    private SmartAssistantConsumer assistantConsumer ;

    @Override
    public List<TableItem> getFlowTaskNotice() {

        List<TableItem> taskFlowItem = new ArrayList<>() ;

        if(!flowTaskBox.isEmpty()){

            Iterator<Map.Entry<Long, TableItem>> iterator = flowTaskBox.entrySet().iterator();
            while (iterator.hasNext()) {

                Map.Entry<Long, TableItem> entry = iterator.next();
                taskFlowItem.add(entry.getValue()) ;

                iterator.remove(); // 使用Iterator的remove方法来删除元素
            }
        }

        return taskFlowItem ;
    }

    @Override
    public void addTask(long channelId, long businessId , long roleId, String text, String preBusinessId , IndustryRoleEntity roleDto ) {
        log.debug("添加任务实例:messageId = {} , businessId = {}" , channelId , businessId);

        TaskInfo taskInfo = new TaskInfo() ;

        taskInfo.setText(text);
        taskInfo.setBusinessId(businessId) ;
        taskInfo.setChannelId(channelId);
        taskInfo.setPreBusinessId(preBusinessId);
        taskInfo.setRoleDto(roleDto);

        taskBox.put(businessId , taskInfo) ;

        // 获取到上一条消息的业务ID
        String assistantContent = null ;

        if(StringUtils.isNotBlank(preBusinessId)){
            MessageQueueEntity messageQueueDto = messageQueueService.queryMessage(preBusinessId) ;
            assistantContent = messageQueueDto.getAssistantContent() ;
        }

        Map<String , Object> params = new HashMap<>() ;
        params.put("label1" , assistantContent == null ? text : assistantContent) ;
        params.put("require" , text) ;  // 个人所提要求

        MessageQueueEntity dto = new MessageQueueEntity() ;

        dto.setAgentId(roleId);
        dto.setChannelId(channelId);
        dto.setBusinessId(businessId+"");
        dto.setContent(JSONObject.toJSONString(params));
        dto.setAssistantContent(assistantContent);

        messageQueueService.addMessage(dto);

        // assistantConsumer.runChainAgent(dto) ;
    }

    @Override
    public List<ChatMessageDto> getTaskMessage() {

        log.debug("查询任务实例信息.");

        List<ChatMessageDto> messageList = new ArrayList<>() ;
        List<MessageEntity> messageEntities = new ArrayList<>() ;

        if(!taskBox.isEmpty()){

            Iterator<Map.Entry<Long, TaskInfo>> iterator = taskBox.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, TaskInfo> entry = iterator.next();

                long businessId = entry.getKey() ;
                TaskInfo taskInfo = entry.getValue() ;

                long channelId = taskInfo.getChannelId() ;
                String agentName = taskInfo.getRoleDto().getRoleName() ;
                String agentIcon = taskInfo.getRoleDto().getRoleAvatar() ;

                MessageQueueEntity messageQueueDto = messageQueueService.queryMessage(businessId+"") ;
                if(messageQueueDto != null){

                    log.debug("messageQueueDto = {}" , messageQueueDto);

                    // 发送到构建查询动态
                    pushMessageInfo(taskInfo , messageQueueDto) ;

                    if(messageQueueDto.getStatus().equals("success")){  // 构建成功.

                        ChatMessageDto chatMessageDto = new ChatMessageDto() ;
                        MessageEntity entity = new MessageEntity() ;

                        String linkInfo = getLinkInfo(messageQueueDto , agentName , businessId) ;

                        chatMessageDto.setChatText(linkInfo);

                        chatMessageDto.setName(agentName);
                        chatMessageDto.setIcon(agentIcon) ;

                        chatMessageDto.setChannelId(channelId);
                        chatMessageDto.setRoleType("agent");
                        chatMessageDto.setBusinessId(businessId);
                        chatMessageDto.setDateTime(DateUtil.formatDateTime(new Date()));
                        chatMessageDto.setClassName("result-tip");

                        messageList.add(chatMessageDto) ; // 返回前端消息列表
                        iterator.remove(); // 使用Iterator的remove方法来删除元素

                        // 持久化消息
                        extractedSaveDb(entity, chatMessageDto, taskInfo , messageEntities , businessId);
                    }
                }

            }
        }

        return messageList ;

    }

    /**
     * 推送到频道消息界面
     * @param taskInfo
     * @param messageQueueDto
     */
    private void pushMessageInfo(TaskInfo taskInfo, MessageQueueEntity messageQueueDto) {

        long channelId = taskInfo.getChannelId() ;
        String agentName = taskInfo.getRoleDto().getRoleName() ;

        TableItem tableItem = new TableItem() ;

        tableItem.setTaskStatus(TaskStatusEnums.PROCESSING.getValue());
        tableItem.setBusinessId(taskInfo.getBusinessId()+"");
        tableItem.setChannel(channelId+"");
        tableItem.setTaskType("message");
        tableItem.setTaskName(agentName + "构建任务["+ taskInfo.businessId+"]");
        tableItem.setAssistantContent(messageQueueDto.getAssistantContent());
        tableItem.setUsageTime("13毫秒");

        long id = IdUtil.getSnowflakeNextId() ;
        log.debug("添加item:{} , 值:{}" , id , tableItem);

        ITaskService.flowTaskBox.put(id , tableItem) ;
    }

    /**
     * 将消息保存到数据库中，进行执久化保存
     * @param entity
     * @param chatMessageDto
     * @param taskInfo
     * @param messageEntities
     * @param businessId
     */
    private void extractedSaveDb(MessageEntity entity,
                                 ChatMessageDto chatMessageDto,
                                 TaskInfo taskInfo,
                                 List<MessageEntity> messageEntities ,
                                 long businessId) {

        long channelId = taskInfo.getChannelId() ;
        long agentId = taskInfo.getRoleDto().getId() ;
        String agentName = taskInfo.getRoleDto().getRoleName() ;
        String agentIcon = taskInfo.getRoleDto().getRoleAvatar() ;

        // 消息保存到数据库
        entity.setContent(chatMessageDto.getChatText().toString()) ;

        entity.setName(agentName);
        entity.setIcon(agentIcon) ;

        entity.setRoleType(chatMessageDto.getRoleType());
        entity.setReaderType(chatMessageDto.getReaderType());
        entity.setBusinessId(businessId);
        entity.setAddTime(new Date()) ;
        entity.setMessageId(IdUtil.getSnowflakeNextId());
        entity.setClassName("result-tip");

        entity.setChannelId(channelId);
        entity.setSenderId(agentId) ;
        entity.setReceiverId(IdUtil.getSnowflakeNextIdStr());

        messageEntities.add(entity) ;

        log.debug("完成任务实例:businessId = {}" , businessId);

        if(!messageEntities.isEmpty()){
            messageService.saveBatch(messageEntities) ;
        }
    }

    /**
     * 得到连接类型
     * @param messageQueueDto
     * @return
     */
    private String getLinkInfo(MessageQueueEntity messageQueueDto, String agentName, long businessId) {

        String linkType = messageQueueDto.getLinkType() ;
        String linkPath = messageQueueDto.getLinkPath() ;

        if(StringUtils.isBlank(linkPath)){
            linkPath = "#" ;
        }

        return "### <i class='fa-brands fa-redhat'></i> 任务已经处理\n" +
                "- 任务: 执行简单业务服务任务类型 \n" +
                "- 业务标识: " + businessId + "\n" +
                "- 持续时间: 46秒503\n" +
                "- <i class='fa-solid fa-file-pdf'></i> 内容: <a href='" + linkPath + "'>查看生成结果</a>\n" +
                "- <i class='fa-solid fa-vial-circle-check'></i> 状态: 完成\n" +
                "- <i class='fa-solid fa-hourglass-end'></i> 完成时间: " + DateUtil.now() + "\n" +
                "- <i class='fa-solid fa-user-secret'></i> 执行人:" + agentName +
                "\n";
    }

    @Data
    private static class TaskInfo {
        private long channelId ;
        private long businessId ;
        private long roleId ;
        private String text ;
        private String preBusinessId ;
        private IndustryRoleEntity roleDto  ;
    }
}
