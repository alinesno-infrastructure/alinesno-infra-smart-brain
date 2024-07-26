package com.alinesno.infra.smart.assistant.im.dingtalk.event;

import com.alinesno.infra.smart.assistant.im.dingtalk.dto.DingtalkRobotMessageDto;
import com.alinesno.infra.smart.assistant.im.dto.NoticeDto;
import com.alinesno.infra.smart.assistant.im.utils.DingtalkSignUtils;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DingtalkMsgDispatcher {

    @Value("${alinesno.infra.smart.dingtalk.robot.webhook:}")
    private String robotWebhook;

    @Value("${alinesno.infra.smart.dingtalk.robot.secret:}")
    private String robotSecret;

    /**
     * 发送消息通知
     *
     * @param dto
     * @param noticeDto
     * @throws ApiException
     */
    public void sendMessageWebhook(DingtalkRobotMessageDto dto, NoticeDto noticeDto) throws ApiException {

        dto.setWebhook(robotWebhook);
        dto.setSecret(robotSecret);
        dto.setAtUser(noticeDto.getSender());

        String signWebhook = DingtalkSignUtils.signWebHook(dto.getSecret() , dto.getWebhook()) ;
        DingTalkClient client = new DefaultDingTalkClient(signWebhook);

        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype(dto.getMessageType());

        if(dto.getMessageType().equals("text")){
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(dto.getMessageContent());
            request.setText(text);
        }else if(dto.getMessageType().equals("link")){
            OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
            link.setMessageUrl("https://www.dingtalk.com/");
            link.setPicUrl("https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png");
            link.setTitle("时代的火车向前开");
            link.setText("这个即将发布的新版本，创始人xx称它为红树林。而在此之前，每当面临重大升级，产品经理们都会取一个应景的代号，这一次，为什么是红树林");
            request.setLink(link);
        }else if(dto.getMessageType().equals("markdown")){
            OapiRobotSendRequest.Markdown markdown = getMarkdown(noticeDto);
            request.setMarkdown(markdown);
        }else if(dto.getMessageType().equals("actionCard")){
            OapiRobotSendRequest.Actioncard actioncard = new OapiRobotSendRequest.Actioncard() ;
            // TODO
            request.setActionCard(actioncard);
        }

        log.debug("dto userId = {}" , dto.getAtUser());

        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        if(dto.getAtUser() != null){
            at.setAtUserIds(List.of(dto.getAtUser()));
        }

        at.setIsAtAll(dto.isAtAll());
        request.setAt(at);

        OapiRobotSendResponse response = client.execute(request);
        System.out.println(response.getBody());
    }

    @NotNull
    private static OapiRobotSendRequest.Markdown getMarkdown(NoticeDto noticeDto) {
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("## 任务执行结果通知");

        String taskName = noticeDto.getTaskName();
        String usageTime = noticeDto.getUsageTime();
        String finishTime = noticeDto.getFinishTime();
        String applyLink = noticeDto.getApplyLink();
        String taskStatus = noticeDto.getTaskStatus();
        String businessId = noticeDto.getBusinessId();

        String markdownText = "## "+noticeDto.getSenderNick()+"的任务已经处理:\n" +
                "- <font color=\"#3b5998\">" +taskName+ "</font>\n" +
                "---\n" +
                "- 业务标识: "+businessId+"\n" +
                "- 持续时间: "+usageTime+"\n" +
                "- 环境: [测试环境](#)\n" +
                "- 内容: [查看生成结果]("+applyLink+")\n" +
                "- 状态: " +taskStatus+ "\n" +
                "- 完成时间: "+finishTime+"\n" +
                "---\n" +
                "- 执行人：培训题设计Agent" ;

        System.out.println("markdownText = " + markdownText);

        markdown.setText(markdownText) ;
        return markdown;
    }

}
