package com.alinesno.infra.smart.assistant.adapter;

import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.dto.PointSettingDto;
import com.alinesno.infra.smart.assistant.adapter.dto.UserPointDto;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;

import java.util.List;

/**
 * AipPoint积分系统消费端，通过消费端获取到积分的管理和统一配置
 */
@BaseRequest(
        baseURL = "#{alinesno.infra.gateway.point-server}" ,
        connectTimeout = 30000,
        readTimeout = 60000)
public interface AipPointConsumer {

    /**
     * 获取到所有的拦截连接地址与匹配的积分配置
     */
    @Get(url = "/getAllPointSetting" , contentType = "application/json")
    R<List<PointSettingDto>> getAllPointSetting();

    /**
     * 获取当前用户积分
     */
    @Get(url = "/getUserPoint" , contentType = "application/json")
    R<UserPointDto> getUserPoint(@Var("accountId") Long accountId);

    /**
     * 减积分
     */
    @Get(url = "/reducePoint" , contentType = "application/json")
    R<Boolean> reducePoint(@Var("accountId") Long accountId, @Var("point") Integer point);

}
