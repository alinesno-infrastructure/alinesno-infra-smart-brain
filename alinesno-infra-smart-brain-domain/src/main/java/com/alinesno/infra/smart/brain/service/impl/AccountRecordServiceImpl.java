package com.alinesno.infra.smart.brain.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.brain.entity.AccountRecordEntity;
import com.alinesno.infra.smart.brain.service.IAccountRecordService;
import com.alinesno.infra.smart.brain.mapper.AccountRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
//@Service
public class AccountRecordServiceImpl extends IBaseServiceImpl<AccountRecordEntity, AccountRecordMapper> implements IAccountRecordService {
    //日志记录
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(AccountRecordServiceImpl.class);
}
