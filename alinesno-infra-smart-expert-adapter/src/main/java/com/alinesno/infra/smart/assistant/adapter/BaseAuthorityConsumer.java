package com.alinesno.infra.smart.assistant.adapter;

import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.dto.OrganizationDto;
import com.dtflys.forest.annotation.*;

/**
 * 权限服务消费者基类
 */
@BaseRequest(
        baseURL = "#{alinesno.infra.gateway.host}/base-authority" ,
        connectTimeout = 30*1000
)
public interface BaseAuthorityConsumer {

    /**
     * 查询组织信息
     *
     * @param id 组织ID
     * @return 组织信息
     */
    @Get("/v1/api/base/authority/organize/findOrg")
    R<OrganizationDto> findOrg(@Query("id") long id);

    /**
     * 更新组织信息
     *
     * @param organize 组织实体
     * @return 更新结果
     */
    @Post("/v1/api/base/authority/organize/updateOrg")
    R<Boolean> updateOrg(@JSONBody OrganizationDto organize);

    /**
     * 用户创建或者加入组织
     * @param dto
     */
    @Post("/v1/api/base/authority/organize/createOrJoinOrg")
    void createOrJoinOrg(@JSONBody OrganizationDto dto , @Query("userId") long userId);

}