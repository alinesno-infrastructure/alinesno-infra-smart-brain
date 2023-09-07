package com.alinesno.infra.smart.brain.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.brain.entity.LoginRecordEntity;
import com.alinesno.infra.smart.brain.mapper.LoginRecordMapper;
import com.alinesno.infra.smart.brain.service.ILoginRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
//@Service
public class LoginRecordServiceImpl extends IBaseServiceImpl<LoginRecordEntity, LoginRecordMapper> implements ILoginRecordService {
    //日志记录
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(LoginRecordServiceImpl.class);
}
