package com.alinesno.infra.base.search.service;

import com.alinesno.infra.base.search.api.SearchConfigDto;
import com.alinesno.infra.base.search.entity.SearchConfigEntity;
import com.alinesno.infra.common.facade.services.IBaseService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author LuoXiaoDong
 * @version 1.0.0
 */

public interface ISearchConfigService extends IBaseService<SearchConfigEntity> {

    /**
     * 保存配置
     * @param searchConfigDTO
     */
    void saveConfiguration(SearchConfigDto searchConfigDTO);

    /**
     * 查找配置
     * @param orgId
     * @return
     */
    SearchConfigDto findConfiguration(Long orgId);

}
