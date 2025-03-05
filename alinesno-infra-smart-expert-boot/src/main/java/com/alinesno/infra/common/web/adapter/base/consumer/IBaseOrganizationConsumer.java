package com.alinesno.infra.common.web.adapter.base.consumer;

import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.base.dto.OrganizationDto;
import com.dtflys.forest.annotation.*;

/**
 * 组织信息接口
 * @author luoxiaodong
 * @version 1.0.0
 */
@BaseRequest(baseURL = "#{alinesno.infra.gateway.host}" , connectTimeout = 30*1000)
public interface IBaseOrganizationConsumer {

    /**
     * 通过ID查询组织号
     * @param orgId 代码类型
     * @return 代码列表
     */
    @Get("/v1/api/base/authority/organize/findOrg")
    R<OrganizationDto> findOrg(@Query("id") long orgId) ;

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
