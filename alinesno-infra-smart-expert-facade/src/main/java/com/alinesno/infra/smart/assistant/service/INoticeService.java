package com.alinesno.infra.smart.assistant.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.entity.NoticeEntity;
import com.alinesno.infra.smart.assistant.im.dto.NoticeDto;

/**
 * 应用构建Service接口
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
public interface INoticeService extends IBaseService<NoticeEntity> {

    /**
     * 保存消息实体信息
     * @param noticeDto
     */
    void saveNoticeDto(NoticeDto noticeDto);

    /**
     * 通过消息获取到实体信息
     * @param businessId
     * @return
     */
    NoticeEntity getByBusinessId(String businessId);
}