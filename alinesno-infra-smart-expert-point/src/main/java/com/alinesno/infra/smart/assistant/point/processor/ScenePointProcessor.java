package com.alinesno.infra.smart.assistant.point.processor;

import com.alinesno.infra.smart.assistant.point.PointProcessorParent;
import com.alinesno.infra.smart.point.annotation.ScenePointAnnotation;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
public class ScenePointProcessor extends PointProcessorParent {

    private final ScenePointAnnotation scenePointAnnotation ;

    public ScenePointProcessor(ScenePointAnnotation scenePointAnnotation) {
        this.scenePointAnnotation = scenePointAnnotation ;
    }

    @Override
    public void process(HttpServletRequest request, Long userId, Long orgId) {

        String sceneId = getRoleIdFromRequest(request, scenePointAnnotation.paramName()) ;
        SceneEnum sceneType = scenePointAnnotation.sceneCode() ;
        long sceneCount = accountPointService.getOrgSceneTaskCount(orgId)  ;

        int sceneMaxConcurrent = getIntegral(orgId).getSceneMaxConcurrency() ;

        log.debug("处理ScenePoint注解 - userId: {} , taskId = {} , sceneCount = {} , sceneType = {}", userId , sceneId , sceneCount , sceneType.name());
        Assert.isTrue(sceneCount < sceneMaxConcurrent  , "当前场景任务数已超过组织限制,组织并发数:" + sceneMaxConcurrent);
    }
}