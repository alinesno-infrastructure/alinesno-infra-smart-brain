package com.alinesno.infra.smart.assistant.point.service;

import com.alinesno.infra.smart.point.service.IAccountPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 账户积分服务实现类
 */
@Slf4j
@Service
public class AccountPointServiceImpl implements IAccountPointService {

    @Value("${alinesno.point.enable:false}")
    private boolean pointEnable ;

    @Override
    public boolean isOpenPoint() {
        return pointEnable ;
    }

}
