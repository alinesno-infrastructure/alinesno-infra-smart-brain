package com.alinesno.infra.base.im.service.impl;

import com.alinesno.infra.base.im.mapper.AccountHomePageMapper;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.im.entity.AccountHomePageEntity;
import com.alinesno.infra.smart.im.service.IAccountHomePageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountHomePageServiceImpl extends IBaseServiceImpl<AccountHomePageEntity, AccountHomePageMapper> implements IAccountHomePageService {

    @Override
    @Transactional
    public boolean setHomePage(Long accountId, String homePage, String type) {
        AccountHomePageEntity accountHomePage = this.getByAccountId(accountId);
        
        if (accountHomePage == null) {
            // 新增记录
            accountHomePage = new AccountHomePageEntity();
            accountHomePage.setOperatorId(accountId);
            accountHomePage.setHomePage(homePage);
            accountHomePage.setType(type);

            return this.save(accountHomePage);
        } else {
            // 更新记录
            accountHomePage.setHomePage(homePage);
            accountHomePage.setType(type);

            return this.updateById(accountHomePage);
        }
    }

    @Override
    public AccountHomePageEntity getByAccountId(Long accountId) {
        LambdaQueryWrapper<AccountHomePageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountHomePageEntity::getOperatorId, accountId);
        return this.getOne(queryWrapper);
    }
}