package com.alinesno.infra.base.im.service.impl;

import com.alinesno.infra.base.im.entity.UserEntity;
import com.alinesno.infra.base.im.mapper.UserMapper;
import com.alinesno.infra.base.im.service.IUserService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl extends IBaseServiceImpl<UserEntity, UserMapper> implements IUserService  {
}
