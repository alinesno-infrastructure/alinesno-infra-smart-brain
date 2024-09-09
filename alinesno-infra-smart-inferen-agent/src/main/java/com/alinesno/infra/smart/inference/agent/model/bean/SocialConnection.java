package com.alinesno.infra.smart.inference.agent.model.bean;

import cn.hutool.core.util.IdUtil;
import com.alinesno.infra.smart.inference.agent.model.base.Agent;
import com.alinesno.infra.smart.inference.agent.model.base.LifeAgent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SocialConnection {

    private Long id;
    private Agent agent;
    private LocalDateTime time;
    private String location;
    private int depth;
    private SocialConnection next;

    public SocialConnection(LifeAgent agent, LocalDateTime time, String location, int depth) {
        this.id = IdUtil.getSnowflakeNextId();
        this.agent = agent;
        this.time = time;
        this.location = location;
        this.depth = depth;
        this.next = null;
    }

}