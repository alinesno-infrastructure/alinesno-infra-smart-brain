package com.alinesno.infra.smart.assistant.screen.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.adapter.CloudStorageConsumer;
import com.alinesno.infra.smart.assistant.screen.entity.ScreenEntity;
import com.alinesno.infra.smart.assistant.screen.mapper.ScreenMapper;
import com.alinesno.infra.smart.assistant.screen.service.IScreenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScreenServiceImpl extends IBaseServiceImpl<ScreenEntity, ScreenMapper> implements IScreenService {

    @Autowired
    private CloudStorageConsumer storageConsumer ;

}
