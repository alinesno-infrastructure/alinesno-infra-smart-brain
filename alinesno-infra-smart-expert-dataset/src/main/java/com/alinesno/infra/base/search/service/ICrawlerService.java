package com.alinesno.infra.base.search.service;

import com.alinesno.infra.base.search.api.CrawlerDto;
import com.alinesno.infra.common.facade.datascope.PermissionQuery;

/**
 * ICrawlerService接口
 */
public interface ICrawlerService {

    /**
     * 解析网站
     * @param dto
     * @return
     */
    void parseWebsite(CrawlerDto dto , PermissionQuery query);

}
