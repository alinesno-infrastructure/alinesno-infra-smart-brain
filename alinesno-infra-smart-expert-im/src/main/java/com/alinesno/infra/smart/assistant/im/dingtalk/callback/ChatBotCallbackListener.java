package com.alinesno.infra.smart.assistant.im.dingtalk.callback;//package com.alinesno.infra.smart.assistant.im.dingtalk.callback;
//
//import cn.hutool.core.util.IdUtil;
//import com.alibaba.fastjson.JSONObject;
//import com.alinesno.infra.smart.assistant.im.dingtalk.service.RobotGroupMessagesService;
//import com.alinesno.infra.smart.assistant.im.dto.NoticeDto;
//import com.alinesno.infra.smart.assistant.im.service.IDingtalkNoticeService;
//import com.alinesno.infra.smart.assistant.service.IChannelService;
//import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
//import com.alinesno.infra.smart.assistant.service.INoticeService;
//import com.dingtalk.open.app.api.callback.OpenDingTalkCallbackListener;
//import com.dingtalk.open.app.api.models.bot.ChatbotMessage;
//import com.dingtalk.open.app.api.models.bot.MessageContent;
//import lombok.extern.slf4j.Slf4j;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 机器人消息回调
// *
// * @author zeymo
// */
//@Slf4j
//@Component
//public class ChatBotCallbackListener implements OpenDingTalkCallbackListener<ChatbotMessage, JSONObject> {
//
//    @Autowired
//    private RobotGroupMessagesService robotGroupMessagesService;
//
//    @Autowired
//    private IIndustryRoleService industryRoleService ;
//
//    @Autowired
//    private IChannelService channelService ;
//
//    @Autowired
//    private INoticeService noticeService ;
//
//    @Autowired
//    private IDingtalkNoticeService dingtalkNoticeService ;
//
//    /**
//     * <a href="https://open.dingtalk.com/document/orgapp/the-application-robot-in-the-enterprise-sends-group-chat-messages">...</a>
//     *
//     * @param message
//     * @return
//     */
//    @Override
//    public JSONObject execute(ChatbotMessage message) {
//        try {
//            MessageContent text = message.getText();
//            if (text != null) {
//
//                String msg = text.getContent();
//
//                NoticeDto noticeDto = getNoticeDto(message , msg);
//
//                // 保存消息实体信息
//                noticeService.saveNoticeDto(noticeDto) ;
//
//                // 获取到执行角色的ID
//                String roleId = channelService.getRoleIdByRobotKey(noticeDto.getChatbotUserId())  ;
//
//                log.info("receive bot message from user={}, msg={}", message.getSenderId(), msg);
//                String openConversationId = message.getConversationId();
//                try {
//
//                    // 发送培训相关的要求到任务中
//                    Map<String , Object> params = new HashMap<>() ;
//                    params.put("label1" , msg) ;
//
//                    industryRoleService.runRoleChainByRoleId(params , roleId , noticeDto);
//
//                    //发送机器人消息
//                    String result = robotGroupMessagesService.send(openConversationId, "收到，"+noticeDto.getSenderNick()+"的任务我已经在处理，请稍等1-2分钟 :-)");
//                    log.debug("result = {}" , result);
//                } catch (Exception e) {
//                    log.error("send group message by robot error:" + e.getMessage(), e);
//                }
//            }
//        } catch (Exception e) {
//            log.error("receive group message by robot error:" + e.getMessage(), e);
//        }
//        return new JSONObject();
//    }
//
//    @NotNull
//    private static NoticeDto getNoticeDto(ChatbotMessage message, String msg) {
//
//        String senderId = message.getSenderId() ;
//        String senderNick = message.getSenderNick() ;
//        String chatbotUserId = message.getChatbotUserId() ;
//        String senderStaffId = message.getSenderStaffId() ;
//
//        log.debug("@机器人ID senderId = {}" , senderStaffId);
//        log.debug("发送者ID senderId = {}" , senderId);
//        log.debug("发送者名称 senderNick = {}" , senderNick);
//        log.debug("机器人Id chatbotUserId = {}" , chatbotUserId);
//
//        NoticeDto noticeDto = new NoticeDto() ;
//
//        noticeDto.setSenderNick(senderNick);
//        noticeDto.setBusinessId(IdUtil.getSnowflakeNextId()+"");
//        noticeDto.setTaskName(msg);
//        noticeDto.setChatbotUserId(chatbotUserId);
//        noticeDto.setApplyLink("http://portal.infra.linesno.com");
//        noticeDto.setEnv("测试环境");
//        noticeDto.setUsageTime("1分23秒231");
//        noticeDto.setFinishTime("2023-12-2 01:12:32");
//        noticeDto.setTaskStatus("完成");
//
//        noticeDto.setSender(senderStaffId);
//
//        return noticeDto;
//    }
//}