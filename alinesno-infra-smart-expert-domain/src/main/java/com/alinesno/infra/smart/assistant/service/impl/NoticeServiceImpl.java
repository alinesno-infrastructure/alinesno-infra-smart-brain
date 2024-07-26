package com.alinesno.infra.smart.assistant.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.NoticeEntity;
import com.alinesno.infra.smart.assistant.im.dto.NoticeDto;
import com.alinesno.infra.smart.assistant.mapper.NoticeMapper;
import com.alinesno.infra.smart.assistant.service.INoticeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class NoticeServiceImpl extends IBaseServiceImpl<NoticeEntity, NoticeMapper> implements INoticeService {

    @Override
    public void saveNoticeDto(NoticeDto noticeDto) {
        NoticeEntity e = new NoticeEntity()  ;
        BeanUtils.copyProperties(noticeDto , e);
        save(e) ;
    }

    @Override
    public NoticeEntity getByBusinessId(String businessId) {

        LambdaQueryWrapper<NoticeEntity> wrapper = new LambdaQueryWrapper<>() ;
        wrapper.eq(NoticeEntity::getBusinessId , businessId)
                .orderByDesc(NoticeEntity::getAddTime);

        List<NoticeEntity> es = list(wrapper) ;

        return es.isEmpty()?null:es.get(0);
    }
}