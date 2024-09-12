package com.alinesno.infra.base.im.service.impl;

import com.alinesno.infra.base.im.mapper.AccountChannelMapper;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.im.entity.AccountChannelEntity;
import com.alinesno.infra.smart.im.service.IAccountChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountChannelServiceImpl extends IBaseServiceImpl<AccountChannelEntity, AccountChannelMapper> implements IAccountChannelService {
}
