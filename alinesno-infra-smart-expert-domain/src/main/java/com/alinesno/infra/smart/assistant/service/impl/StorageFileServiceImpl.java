package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.StorageFileEntity;
import com.alinesno.infra.smart.assistant.mapper.StorageFileMapper;
import com.alinesno.infra.smart.assistant.service.IStorageFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StorageFileServiceImpl extends IBaseServiceImpl<StorageFileEntity, StorageFileMapper> implements IStorageFileService {

}
