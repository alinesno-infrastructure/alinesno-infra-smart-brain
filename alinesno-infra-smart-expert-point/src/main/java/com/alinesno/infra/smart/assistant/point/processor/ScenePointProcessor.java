package com.alinesno.infra.smart.assistant.point.processor;

import com.alinesno.infra.smart.assistant.point.PointProcessorParent;
import com.alinesno.infra.smart.point.annotation.ScenePointAnnotation;
import com.alinesno.infra.smart.point.enums.ParamSource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class ScenePointProcessor extends PointProcessorParent {

    private final ScenePointAnnotation scenePointAnnotation ;

    public ScenePointProcessor(ScenePointAnnotation scenePointAnnotation) {
        this.scenePointAnnotation = scenePointAnnotation ;
    }

    @Override
    public void process(HttpServletRequest request, Long userId, Long orgId) {
        String roleId = getRoleIdFromRequest(request, scenePointAnnotation.paramName() , ParamSource.PARAMS) ;
        log.debug("处理ScenePoint注解:{}" , orgId);
    }
}