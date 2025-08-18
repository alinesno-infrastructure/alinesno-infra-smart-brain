package com.alinesno.infra.smart.assistant.point.processor;

import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.point.PointProcessorParent;
import com.alinesno.infra.smart.point.annotation.ScenePointAnnotation;
import com.alinesno.infra.smart.point.enums.ParamSource;
import com.alinesno.infra.smart.point.service.IAccountPointService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
public class ScenePointProcessor extends PointProcessorParent {

    private final ScenePointAnnotation scenePointAnnotation ;

    @Setter
    private int sceneMaxConcurrent;

    public ScenePointProcessor(ScenePointAnnotation scenePointAnnotation) {
        this.scenePointAnnotation = scenePointAnnotation ;
    }

    @Override
    public void process(HttpServletRequest request, Long userId, Long orgId) {

        String taskId = getRoleIdFromRequest(request, scenePointAnnotation.paramName() , ParamSource.PARAMS) ;
        long sceneCount = accountPointService.getOrgSceneTaskCount(orgId)  ;

        log.debug("处理ScenePoint注解 - userId: {} , taskId = {} , sceneCount = {}", userId , taskId , sceneCount);
        Assert.isTrue(sceneCount < sceneMaxConcurrent , "当前场景任务数已超过组织限制,组织并发数:" + sceneMaxConcurrent);
    }
}