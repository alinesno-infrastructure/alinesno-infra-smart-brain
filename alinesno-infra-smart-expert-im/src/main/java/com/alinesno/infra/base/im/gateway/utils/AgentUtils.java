package com.alinesno.infra.base.im.gateway.utils;

import cn.hutool.core.date.DateUtil;
import com.alinesno.infra.base.im.dto.ChatMessageDto;
import com.alinesno.infra.base.im.dto.WebMessageDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;

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

    /**
     * 生成默认回复内容
     * @param dtoList
     * @param roleDto
     * @param businessId
     * @param text
     * @return
     */
    public static ChatMessageDto getChatMessageDto(List<WebMessageDto> dtoList, IndustryRoleEntity roleDto , long businessId , String text) {

        // 发送消息给前端
        ChatMessageDto personDto = new ChatMessageDto() ;
        personDto.setChatText("收到，任务我已经在处理，请稍等1-2分钟 :-)");

        personDto.setIcon(roleDto.getRoleAvatar()) ;
        personDto.setName(roleDto.getRoleName());

        personDto.setRoleType("agent");
        personDto.setReaderType("html");
        personDto.setBusinessId(businessId);

        personDto.setLoading(true);
        personDto.setDateTime(DateUtil.formatDateTime(new Date()));

        return personDto;
    }

    /**
     * 获取上一个节点的业务ID
     * @param dtoList
     * @return
     */
    public static String getPreBusinessId(List<WebMessageDto> dtoList) {
        WebMessageDto dto = getTypeText(dtoList , "business");
        return dto.getBusinessId() ;
    }

    private static WebMessageDto getTypeText(List<WebMessageDto> dtoList , String type){
        for(WebMessageDto dto : dtoList){
            if(dto.getType().equals(type)){
                return dto ;
            }
        }
        return new WebMessageDto();
    }
}
