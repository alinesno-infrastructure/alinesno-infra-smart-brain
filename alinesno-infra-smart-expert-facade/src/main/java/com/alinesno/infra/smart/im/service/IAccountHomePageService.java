package com.alinesno.infra.smart.im.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.im.entity.AccountHomePageEntity;

/**
 * 账户首页服务接口
 */
public interface IAccountHomePageService extends IBaseService<AccountHomePageEntity> {

    boolean setHomePage(Long accountId, String homePage, String type);

    AccountHomePageEntity getByAccountId(Long accountId);
}