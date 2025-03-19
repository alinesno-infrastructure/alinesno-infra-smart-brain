package com.alinesno.infra.smart.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.im.constants.AgentConstants;
import com.alinesno.infra.smart.im.dto.ChatMessageDto;
import com.alinesno.infra.smart.im.dto.MessageTaskInfo;
import com.alinesno.infra.smart.im.dto.WebMessageDto;
import com.alinesno.infra.smart.im.enums.DocumentTypeEnum;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class AgentUtils {

    /**
     * 获取到请求角色的ID
     * @param dtoList
     * @return
     */
    public static long getRoleId(List<WebMessageDto> dtoList) {
        WebMessageDto mention = getTypeText(dtoList , "mention");
        return Long.parseLong(mention.getId()) ;
    }

    /**
     * 获取到生成的内容要求
     * @param dtoList
     * @return
     */
    public static String getText(List<WebMessageDto> dtoList) {
        WebMessageDto dto = getTypeText(dtoList , "text");
        return dto.getText() ;
    }

    @NotNull
    public static ChatMessageDto getUploadChatMessageDto(String fileName, String ossPath , String fileType , String status) {
        ChatMessageDto personDto = new ChatMessageDto() ;

        String icon = DocumentTypeEnum.getIconByFileType(fileType);
        String escapedFileName = StringEscapeUtils.escapeHtml4(fileName);

        String chatText = "<div style='color: #409EFF; padding: 3px;'>" +
                "<i class='" + icon + " aip-icon-word'></i>" +
                escapedFileName +
                " </div>" +
                " 已上传文件"+(status.equals("SUCCESS")?"成功.":"失败.")+".<a href='"+ossPath+"'>下载</a>";

        personDto.setChatText(chatText);

        personDto.setName("AIP助理");
        personDto.setRoleType("agent");
        personDto.setReaderType("html");
        personDto.setBusinessId(IdUtil.getSnowflakeNextId());

        personDto.setIcon("1830185154541305857") ;

        personDto.setLoading(false);
        personDto.setDateTime(DateUtil.formatDateTime(new Date()));

        return personDto;
    }

    /**
     * 生成默认回复内容
     * @return
     */
    public static ChatMessageDto getChatMessageDto(IndustryRoleEntity roleDto , long businessId) {

        // 发送消息给前端
        ChatMessageDto personDto = new ChatMessageDto() ;
        personDto.setChatText(AgentConstants.ChatText.CHAT_WAITING_NOT_TIME);

        personDto.setRoleId(roleDto.getId());
        personDto.setName(roleDto.getRoleName());
        personDto.setIcon(roleDto.getRoleAvatar()) ;

        personDto.setRoleType("agent");
        personDto.setReaderType("html");
        personDto.setBusinessId(businessId);

        personDto.setLoading(true);
        personDto.setDateTime(DateUtil.formatDateTime(new Date()));

        return personDto;
    }

    /**
     * 生成默认回复内容
     * @return
     */
    public static ChatMessageDto genSSEChatMessageDto() {

        // 发送消息给前端
        ChatMessageDto personDto = new ChatMessageDto() ;
        personDto.setChatText("恭喜你，已连接成功SSE:-)");

        personDto.setName("Agent小助理");
        personDto.setIcon("1830185154541305857") ;

        personDto.setRoleType("agent");
        personDto.setReaderType("html");
        personDto.setBusinessId(IdUtil.getSnowflakeNextId());

        personDto.setLoading(false);
        personDto.setDateTime(DateUtil.formatDateTime(new Date()));

        return personDto;
    }

    private static WebMessageDto getTypeText(List<WebMessageDto> dtoList , String type){
        for(WebMessageDto dto : dtoList){
            if(dto.getType().equals(type)){
                return dto ;
            }
        }
        return new WebMessageDto();
    }

    /**
     * 完成任务调试
     * @return
     */
    public static ChatMessageDto genTaskStatusMessageDto(MessageTaskInfo taskInfo , String usageTime) {

        String agentName = taskInfo.getRoleDto().getRoleName() ;
        String icon = taskInfo.getRoleDto().getRoleAvatar() ;

        String getLinkInfo = getLinkInfo(usageTime);

        // 发送消息给前端
        ChatMessageDto personDto = new ChatMessageDto() ;
        personDto.setChatText(getLinkInfo);

        personDto.setName(agentName);
        personDto.setIcon(icon) ;

        personDto.setRoleType("agent");
        personDto.setReaderType("html");
        personDto.setBusinessId(IdUtil.getSnowflakeNextId());

        personDto.setLoading(false);
        personDto.setDateTime(DateUtil.formatDateTime(new Date()));

        return personDto;

    }

    /**
     * 得到连接类型
     * @return
     */
    private static String getLinkInfo(String usageTime) {

        String linkPath = "#";

        return "### <i class='fa-brands fa-redhat'></i> 任务已经处理\n" +
                "- 任务: 执行简单业务服务任务类型 \n" +
                "- 持续时间: "+usageTime+" \n" +
                "- <i class='fa-solid fa-file-pdf'></i> 内容: <a href='" + linkPath + "'>查看生成结果</a>\n" +
                "- <i class='fa-solid fa-hourglass-end'></i> 完成时间: " + DateUtil.now() + "\n" +
                "\n";
    }
}
