package com.alinesno.infra.common.web.adapter.base.consumer;

import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.common.web.adapter.base.dto.ManagerCodeDto;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;

import java.util.List;

/**
 * 平台配置服务
 * @author luoxiaodong
 * @version 1.0.0
 */
@BaseRequest(baseURL = "#{alinesno.infra.gateway.host}" , connectTimeout = 30*1000)
public interface IBaseConfigConsumer {

    /**
     * 通过代码类型查询代码
     * @param codeType 代码类型
     * @param projectCode 应用ID
     * @return 代码列表
     */
    @Get("/v1/api/base/authority/dict/codeListByType")
    R<List<ManagerCodeDto>> codeListByType(@Query("codeType") String codeType,
                                           @Query("projectCode")  String projectCode);

}
