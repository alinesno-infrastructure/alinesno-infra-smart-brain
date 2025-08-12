package com.alinesno.infra.smart.assistant.point.service;

import com.alinesno.infra.smart.assistant.point.config.PointProperties;
import com.alinesno.infra.smart.point.service.IAccountPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 账户积分服务实现类
 */
@Slf4j
@Service
public class AccountPointServiceImpl implements IAccountPointService {

    @Autowired
    private PointProperties pointProperties; // 积分配置

    @Override
    public boolean isOpenPoint() {
        return pointProperties.isEnabled() ;
    }

}
