package com.alinesno.infra.smart.assistant.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.core.utils.StringUtils;
import com.alinesno.infra.common.core.utils.ip.AddressUtils;
import com.alinesno.infra.common.web.log.bo.SysOperLogBo;
import com.alinesno.infra.common.web.log.event.OperLogEvent;
import com.alinesno.infra.smart.assistant.entity.ApiRecordEntity;
import com.alinesno.infra.smart.assistant.mapper.ApiRecordMapper;
import com.alinesno.infra.smart.assistant.service.IApiRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author LuoXiaoDong
 * @version 1.0.0
 */
@Slf4j
@Service
public class ApiRecordServiceImpl extends IBaseServiceImpl<ApiRecordEntity, ApiRecordMapper> implements IApiRecordService {

    /**
     * 操作日志记录
     *
     * @param operLogEvent 操作日志事件
     */
    @Async
    @EventListener
    public void recordOper(OperLogEvent operLogEvent) {

        log.debug("--->> operLogEvent = {}" , operLogEvent);

//        SysOperLogBo operLog = MapstructUtils.convert(operLogEvent, SysOperLogBo.class);

        SysOperLogBo operLog = new SysOperLogBo() ;
        BeanUtils.copyProperties(operLogEvent , operLog);

        log.debug("--->> operLog = {}" , operLog);

        // 远程查询操作地点
        operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));

        insertOperlog(operLog);
    }

    @Override
    public void insertOperlog(SysOperLogBo bo) {

        ApiRecordEntity entity = new ApiRecordEntity() ;
        BeanUtils.copyProperties(bo , entity);

        entity.setOperTime(new Date());
        entity.setOperId(IdWorker.getId());

        save(entity) ;
    }

    @Override
    public List<ApiRecordEntity> selectOperLogList(SysOperLogBo operLog) {
        Map<String, Object> params = operLog.getParams();
        return mapper.selectList(new LambdaQueryWrapper<ApiRecordEntity>()
                .like(StringUtils.isNotBlank(operLog.getOperIp()), ApiRecordEntity::getOperIp, operLog.getOperIp())
                .like(StringUtils.isNotBlank(operLog.getTitle()), ApiRecordEntity::getTitle, operLog.getTitle())
                .eq(operLog.getBusinessType() != null && operLog.getBusinessType() > 0,
                        ApiRecordEntity::getBusinessType, operLog.getBusinessType())
                .func(f -> {
                    if (ArrayUtil.isNotEmpty(operLog.getBusinessTypes())) {
                        f.in(ApiRecordEntity::getBusinessType, Arrays.asList(operLog.getBusinessTypes()));
                    }
                })
                .eq(operLog.getStatus() != null && operLog.getStatus() > 0,
                        ApiRecordEntity::getStatus, operLog.getStatus())
                .like(StringUtils.isNotBlank(operLog.getOperName()), ApiRecordEntity::getOperName, operLog.getOperName())
                .between(params.get("beginTime") != null && params.get("endTime") != null,
                        ApiRecordEntity::getOperTime, params.get("beginTime"), params.get("endTime"))
                .orderByDesc(ApiRecordEntity::getOperId));
    }

    @Override
    public boolean deleteOperLogByIds(Long[] operIds) {
        return removeBatchByIds(Arrays.asList(operIds));
    }

    @Override
    public ApiRecordEntity selectOperLogById(Long operId) {
        return getById(operId) ;
    }

    @Override
    public void cleanOperLog() {
        remove(new LambdaQueryWrapper<>());
    }
}
