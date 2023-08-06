package com.alineson.infra.smart.brain.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alineson.infra.smart.brain.entity.AppStatisticsEntity;
import com.alineson.infra.smart.brain.mapper.AppStatisticsMapper;
import com.alineson.infra.smart.brain.service.IAppStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * 数据资产统计Service业务层处理
 *

 * @author luoxiaodong
 * @version 1.0.0
 */
@Service
public class AppStatisticsServiceImpl extends IBaseServiceImpl<AppStatisticsEntity, AppStatisticsMapper> implements IAppStatisticsService {
    //日志记录
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(AppStatisticsServiceImpl.class);
}
