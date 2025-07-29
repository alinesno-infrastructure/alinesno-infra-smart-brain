package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.im.entity.AccountHomePageEntity;

/**
 * 账户首页服务接口
 */
public interface IAccountHomePageService extends IBaseService<AccountHomePageEntity> {

    /**
     * 设置账户首页
     * @param accountId
     * @param homePage
     * @param type
     * @return
     */
    boolean setHomePage(Long accountId, String homePage, String type);

    /**
     * 获取账户首页
     * @param accountId
     * @return
     */
    AccountHomePageEntity getByAccountId(Long accountId);
}