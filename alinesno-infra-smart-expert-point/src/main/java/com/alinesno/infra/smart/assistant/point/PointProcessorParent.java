package com.alinesno.infra.smart.assistant.point;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.adapter.AipPointConsumer;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.point.enums.ParamSource;
import com.alinesno.infra.smart.point.service.IAccountPointService;
import com.alinesno.infra.smart.scene.service.ISceneService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.stream.Collectors;

@Slf4j
public abstract class PointProcessorParent implements PointAnnotationProcessor {

    protected IAccountPointService accountPointService = SpringUtils.getBean(IAccountPointService.class) ;
    protected ISceneService sceneService = SpringUtils.getBean(ISceneService.class) ;
    protected IIndustryRoleService roleService = SpringUtils.getBean(IIndustryRoleService.class) ;
    protected AipPointConsumer aipPointConsumer = SpringUtils.getBean(AipPointConsumer.class) ;

    /**
     * 从请求中获取roleId
     * @param request
     * @param paramName
     * @param paramSource
     * @return
     */
    public String getRoleIdFromRequest(HttpServletRequest request , String paramName , ParamSource paramSource) {

        if (paramSource == ParamSource.PARAMS) {
            return request.getParameter(paramName);
        } else {
            try {
                String body = request.getReader().lines().collect(Collectors.joining());
                JSONObject jsonObject = JSONObject.parseObject(body);
                return jsonObject.getString(paramName);
            } catch (Exception e) {
                log.error("从请求体获取roleId失败", e);
                throw new RpcServiceRuntimeException("获取roleId参数失败");
            }
        }
    }

}