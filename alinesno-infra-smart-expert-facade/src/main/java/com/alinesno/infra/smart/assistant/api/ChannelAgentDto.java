package com.alinesno.infra.smart.assistant.api;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ChannelAgentDto {

    private long channelId ;
    private List<Long> rolesId ;

}
