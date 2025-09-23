package com.alinesno.infra.smart.assistant.point;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.log.utils.SpringUtils;
import com.alinesno.infra.smart.assistant.adapter.AipPointConsumer;
import com.alinesno.infra.smart.assistant.adapter.dto.OrgPointDto;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.point.service.IAccountPointService;
import com.alinesno.infra.smart.scene.service.ISceneService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import javax.lang.exception.RpcServiceRuntimeException;
import java.io.BufferedReader;

@Slf4j
public abstract class PointProcessorParent implements PointAnnotationProcessor {

    protected IAccountPointService accountPointService = SpringUtils.getBean(IAccountPointService.class) ;
    protected ISceneService sceneService = SpringUtils.getBean(ISceneService.class) ;
    protected IIndustryRoleService roleService = SpringUtils.getBean(IIndustryRoleService.class) ;
    protected AipPointConsumer aipPointConsumer = SpringUtils.getBean(AipPointConsumer.class) ;

    /**
     * 获取当前用户最大并发数
     * @return
     */
    protected OrgPointDto getIntegral(long orgId) {
        R<OrgPointDto> dtoData = aipPointConsumer.getIntegral(orgId) ;

        log.debug("获取到组织积分:{}" , dtoData.getData());
        OrgPointDto dto = R.isSuccess(dtoData)? dtoData.getData() : OrgPointDto.empty() ;

        if(dto.getState().equals("FAIL")) {  // 积分异常
            throw new RpcServiceRuntimeException(dto.getFailMessage()) ;
        }

        return dto ;
    }

    /**
     * 从请求中获取roleId（自动检测参数来源）
     * @param request HTTP请求对象
     * @param paramName 参数名称
     * @return 参数值，如果找不到则返回null
     */
    public String getRoleIdFromRequest(HttpServletRequest request, String paramName) {
        if (request == null || paramName == null) {
            log.warn("参数不能为空: request={}, paramName={}", request, paramName);
            return null;
        }

        try {
            // 1. 首先检查URL查询参数
            String value = request.getParameter(paramName);
            if (value != null) {
                log.debug("从URL参数中获取到 {}: {}", paramName, value);
                return value;
            }

            // 2. 检查请求体（JSON或表单）
            String contentType = request.getContentType();
            if (contentType != null) {
                if (contentType.contains("application/json")) {
                    value = getFromJsonBody(request, paramName);
                    if (value != null) {
                        log.debug("从JSON请求体中获取到 {}: {}", paramName, value);
                        return value;
                    }
                } else if (contentType.contains("application/x-www-form-urlencoded") ||
                        contentType.contains("multipart/form-data")) {
                    value = request.getParameter(paramName);
                    if (value != null) {
                        log.debug("从表单数据中获取到 {}: {}", paramName, value);
                        return value;
                    }
                }
            }

            // 3. 检查请求头
            value = request.getHeader(paramName);
            if (value != null) {
                log.debug("从请求头中获取到 {}: {}", paramName, value);
                return value;
            }

            log.warn("未找到参数: {}", paramName);
            return null;

        } catch (Exception e) {
            log.error("从请求中获取参数失败: paramName={}", paramName, e);
            return null;
        }
    }

    /**
     * 从JSON请求体中获取参数
     */
    private String getFromJsonBody(HttpServletRequest request, String paramName) {
        try {
            String body = getRequestBody(request);
            if (body != null && !body.trim().isEmpty()) {
                JSONObject jsonObject = JSONObject.parseObject(body);
                return jsonObject.getString(paramName);
            }
        } catch (Exception e) {
            log.warn("解析JSON请求体失败", e);
        }
        return null;
    }

    /**
     * 获取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            log.warn("读取请求体失败", e);
            return null;
        }
    }
}