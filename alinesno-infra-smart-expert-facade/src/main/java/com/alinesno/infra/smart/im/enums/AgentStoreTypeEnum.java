package com.alinesno.infra.smart.im.enums;

import com.alinesno.infra.smart.im.dto.AgentTypeDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum AgentStoreTypeEnum {

    LATEST(new AgentTypeDto(1L, "latest", "最新", "fa-solid fa-clock")),
    OFFICE_COPYWRITING(new AgentTypeDto(2L, "office_copywriting", "办公文案", "fa-solid fa-file-word")),
    BUSINESS_SERVICE(new AgentTypeDto(3L, "business_service", "商业服务", "fa-solid fa-handshake")),
    TEXT_CREATION(new AgentTypeDto(4L, "text_creation", "文本创作", "fa-solid fa-pen")),
    LEARNING_EDUCATION(new AgentTypeDto(5L, "learning_education", "学习教育", "fa-solid fa-book")),
    IMAGE_AUDIO(new AgentTypeDto(6L, "image_audio", "图片与音频", "fa-solid fa-image")),
    LIFESTYLE(new AgentTypeDto(7L, "lifestyle", "生活方式", "fa-solid fa-house")),
    PROJECT_OPERATION(new AgentTypeDto(9L, "project_operation", "项目运维", "fa-solid fa-cogs"));

    private final AgentTypeDto agentTypeDto;

    AgentStoreTypeEnum(AgentTypeDto agentTypeDto) {
        this.agentTypeDto = agentTypeDto;
    }

    /**
     * 通过id获取到对应的AgentTypeEnum
     */
    public static AgentStoreTypeEnum getById(Long id) {
        for (AgentStoreTypeEnum agentTypeEnum : AgentStoreTypeEnum.values()) {
            if (agentTypeEnum.getAgentTypeDto().getId().equals(id)) {
                return agentTypeEnum;
            }
        }
        return null;
    }

    /**
     * 通过code获取到对应的AgentTypeEnum
     */
    public static AgentStoreTypeEnum getByCode(String code) {
        for (AgentStoreTypeEnum agentTypeEnum : AgentStoreTypeEnum.values()) {
            if (agentTypeEnum.getAgentTypeDto().getCode().equals(code)) {
                return agentTypeEnum;
            }
        }
        return null;
    }

    /**
     * 获取到列表，并返回List<AgentTypeDto>列表，不是数组
     */
    public static List<AgentTypeDto> getList() {
        List<AgentTypeDto> dtoList = new ArrayList<>();
        for (AgentStoreTypeEnum agentTypeEnum : AgentStoreTypeEnum.values()) {
            dtoList.add(agentTypeEnum.getAgentTypeDto());
        }
        return dtoList;
    }

}    