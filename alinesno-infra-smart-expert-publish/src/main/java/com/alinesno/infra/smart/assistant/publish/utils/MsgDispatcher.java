package com.alinesno.infra.smart.assistant.publish.utils;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.smart.assistant.api.WorkflowExecutionDto;
import com.alinesno.infra.smart.assistant.api.request.*;
import com.alinesno.infra.smart.assistant.api.response.TextMessageRes;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.screen.utils.RoleUtils;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信消息业务处理分发器，负责根据不同类型的微信消息进行相应的处理和分发。
 * 该类接收一个包含微信消息信息的 Map，根据消息类型进行分类处理，并返回相应的处理结果。
 */
@Slf4j
public class MsgDispatcher {

    /**
     * 处理微信消息的核心方法。
     * 从传入的消息 Map 中提取关键信息，如用户 openid 和公众号原始 ID，
     * 并根据消息类型进行不同的处理。对于文本消息，会返回一个测试回复；
     * 对于其他类型的消息，仅进行对象解析，暂不做进一步处理。
     *
     * @param map 包含微信消息信息的 Map，键为消息字段名，值为对应字段的值。
     * @return 处理后的消息 XML 字符串，如果没有匹配的消息类型则返回空字符串。
     */
    public static String processMessage(Map<String, String> map , String appId , String appSecret , String accessTokenKey) {
        // 获取用户 openid
        String openid = map.get("FromUserName");

        // 获取公众号原始 ID
        String mpid = map.get("ToUserName");

        // 初始化文本消息响应对象
        TextMessageRes textMessageRes = new TextMessageRes();
        textMessageRes.setToUserName(openid);
        textMessageRes.setFromUserName(mpid);
        textMessageRes.setCreateTime(new Date().getTime());
        textMessageRes.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);

        if (map.get("MsgType").equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
            // 记录文本消息处理日志
            log.info("接收到文本消息");
            String jsonString = JSONObject.toJSONString(map);
            TextMessageReq textMessage = JSONObject.parseObject(jsonString, TextMessageReq.class);

            log.debug("textMessage = {}" , textMessage);

//          // TODO 待处理企业微信号客服返回的能力
//            ThreadUtil.execute(() -> {
//                WechatService wechatService = SpringUtil.getBean(WechatService.class);
//                wechatService.setAppId(appId);
//                wechatService.setAppSecret(appSecret);
//                wechatService.setAccessTokenKey(accessTokenKey);
//                String result =  wechatService.sendTextMessageToUser(openid, "你好，这里是客服测试回复");
//                log.info("发送结果 = {}" , result);
//            });

            IIndustryRoleService roleService = SpringUtil.getBean(IIndustryRoleService.class);
            MessageTaskInfo taskInfo = new MessageTaskInfo() ;

//            taskInfo.setRoleId(leaderRole.getId());
//            taskInfo.setChannelId(dto.getScreenId());
//            taskInfo.setScreenId(dto.getScreenId());
//            taskInfo.setText(textMessage.getContent());
//
//            Map<String, Object> params = new HashMap<>();
//            taskInfo.setParams(params);
//
//            WorkflowExecutionDto genContent  = roleService.runRoleAgent(taskInfo) ;

//            textMessageRes.setContent(genContent.getGenContent());

            textMessageRes.setContent("你好，这里是客服测试回复");
            return MessageUtil.textMessageToXml(textMessageRes);
        }

        if (map.get("MsgType").equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
            // 记录图片消息处理日志
            log.info("接收到图片消息");
            String jsonString = JSONObject.toJSONString(map);
            ImageMessageReq imageMessage = JSONObject.parseObject(jsonString, ImageMessageReq.class);
        }

        if (map.get("MsgType").equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
            // 记录链接消息处理日志
            log.info("接收到链接消息");
            String jsonString = JSONObject.toJSONString(map);
            LinkMessageReq linkMessage = JSONObject.parseObject(jsonString, LinkMessageReq.class);
        }

        if (map.get("MsgType").equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
            // 记录位置消息处理日志
            log.info("接收到位置消息");
            String jsonString = JSONObject.toJSONString(map);
            LocationMessageReq locationMessage = JSONObject.parseObject(jsonString, LocationMessageReq.class);
        }

        if (map.get("MsgType").equals(MessageUtil.REQ_MESSAGE_TYPE_VIDEO)) {
            // 记录视频/小视频消息处理日志
            log.info("接收到视频消息");
            String jsonString = JSONObject.toJSONString(map);
            VideoMessageReq videoMessage = JSONObject.parseObject(jsonString, VideoMessageReq.class);
        }

        if (map.get("MsgType").equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
            // 记录语音消息处理日志
            log.info("接收到语音消息");
            String jsonString = JSONObject.toJSONString(map);
            VoiceMessageReq voiceMessage = JSONObject.parseObject(jsonString, VoiceMessageReq.class);
        }

        return "";
    }
}