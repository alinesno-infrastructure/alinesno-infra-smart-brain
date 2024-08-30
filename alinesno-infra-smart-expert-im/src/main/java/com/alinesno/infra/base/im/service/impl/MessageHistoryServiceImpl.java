package com.alinesno.infra.base.im.service.impl;

import com.alinesno.infra.base.im.entity.MessageHistoryEntity;
import com.alinesno.infra.base.im.mapper.MessageHistoryMapper;
import com.alinesno.infra.base.im.service.IMessageHistoryService;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageHistoryServiceImpl extends IBaseServiceImpl<MessageHistoryEntity, MessageHistoryMapper> implements IMessageHistoryService {

}
