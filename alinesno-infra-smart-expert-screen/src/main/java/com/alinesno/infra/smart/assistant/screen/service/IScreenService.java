package com.alinesno.infra.smart.assistant.screen.service;

import com.alinesno.infra.common.facade.services.IBaseService;
import com.alinesno.infra.smart.assistant.screen.dto.ScreenDto;
import com.alinesno.infra.smart.assistant.screen.entity.ScreenEntity;

/**
 *
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
public interface IScreenService extends IBaseService<ScreenEntity> {

    /**
     * 保存一个ScreenEntity对象
     * @param screenDto
     * @return
     */
    ScreenEntity saveScreen(ScreenDto screenDto);

    /**
     * 生成markdown内容
     * @param screenId
     * @return
     */
    String genMarkdownContent(long screenId);

}
