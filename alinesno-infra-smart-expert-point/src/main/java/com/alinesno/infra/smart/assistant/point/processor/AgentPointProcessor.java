package com.alinesno.infra.smart.assistant.point.processor;

import com.alinesno.infra.smart.assistant.point.PointProcessorParent;
import com.alinesno.infra.smart.point.annotation.AgentPointAnnotation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
public class AgentPointProcessor extends PointProcessorParent {

    private final AgentPointAnnotation agentPointAnnotation;

    public AgentPointProcessor(AgentPointAnnotation agentPointAnnotation) {
        this.agentPointAnnotation = agentPointAnnotation;
    }

    @Override
    public void process(HttpServletRequest request, Long userId, Long orgId) {

        String roleId = getRoleIdFromRequest(request, agentPointAnnotation.paramName()) ;
        long chatCount = accountPointService.getOrgSessionCount(orgId) ;

        int agentMaxConcurrent = getIntegral(orgId).getAgentMaxConcurrency() ;

        log.debug("处理AgentPoint注解 - userId: {} , roleId = {} , chatCount = {}", userId , roleId , chatCount);
        Assert.isTrue(chatCount < agentMaxConcurrent , "当前并发会话数已超组织限制,会话并发数:" + agentMaxConcurrent);
    }
}