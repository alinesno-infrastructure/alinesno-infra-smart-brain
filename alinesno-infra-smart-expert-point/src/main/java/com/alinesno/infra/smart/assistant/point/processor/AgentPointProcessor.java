package com.alinesno.infra.smart.assistant.point.processor;

import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.point.PointProcessorParent;
import com.alinesno.infra.smart.point.annotation.AgentPointAnnotation;
import com.alinesno.infra.smart.point.enums.ParamSource;
import com.alinesno.infra.smart.point.service.IAccountPointService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
public class AgentPointProcessor extends PointProcessorParent {

    private final AgentPointAnnotation agentPointAnnotation;

    public AgentPointProcessor(AgentPointAnnotation agentPointAnnotation) {
        this.agentPointAnnotation = agentPointAnnotation;
    }

    @Override
    public void process(HttpServletRequest request, Long userId, Long orgId) {
        IAccountPointService accountPointService = SpringUtils.getBean(IAccountPointService.class) ;

        String roleId = getRoleIdFromRequest(request, agentPointAnnotation.paramName() , ParamSource.PARAMS) ;
        long chatCount = accountPointService.getOrgSessionCount(orgId) ;

        log.debug("处理AgentPoint注解 - userId: {} , roleId = {} , chatCount = {}", userId , roleId , chatCount);
        Assert.isTrue(chatCount < 3 , "当前并发会话数已超组织限制.");
    }
}