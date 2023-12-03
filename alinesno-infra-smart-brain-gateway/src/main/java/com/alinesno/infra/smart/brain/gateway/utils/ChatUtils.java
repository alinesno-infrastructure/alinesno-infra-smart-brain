package com.alinesno.infra.smart.brain.gateway.utils;

import com.alinesno.infra.smart.brain.api.dto.ChatRequestDto;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import com.plexpt.chatgpt.entity.chat.Message;

import java.util.List;

/**
 * 聊天工具类
 */
public class ChatUtils {

    /**
     * 转换类型
     * @param chatRequestDto
     * @return
     */
    public static ChatCompletion convert(ChatRequestDto chatRequestDto){

        Message systemMessage = Message
                .builder()
                .role(Message.Role.SYSTEM.getValue())
                .content(chatRequestDto.getSystemMessage())
                .build();

        Message message = Message
                .builder()
                .role(Message.Role.USER.getValue())
                .content(chatRequestDto.getPrompt())
                .build();

        return ChatCompletion
                .builder()
                .messages(List.of(systemMessage , message))
                .temperature(chatRequestDto.getTemperature())
                .maxTokens(chatRequestDto.getTokens())
                .topP(chatRequestDto.getTop_p())
                .build();
    }

}
