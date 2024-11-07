package com.alinesno.infra.smart.assistant.screen.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.facade.response.R;
import com.alinesno.infra.smart.assistant.adapter.BaseSearchConsumer;
import com.alinesno.infra.smart.assistant.screen.dto.ScreenDto;
import com.alinesno.infra.smart.assistant.screen.entity.ScreenEntity;
import com.alinesno.infra.smart.assistant.screen.mapper.ScreenMapper;
import com.alinesno.infra.smart.assistant.screen.service.IScreenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScreenServiceImpl extends IBaseServiceImpl<ScreenEntity, ScreenMapper> implements IScreenService {

    @Autowired
    private BaseSearchConsumer baseSearchConsumer; ;

    @Override
    public ScreenEntity saveScreen(ScreenDto screenDto) {

        ScreenEntity screenEntity = new ScreenEntity();
        BeanUtils.copyProperties(screenDto, screenEntity);

        // 创建频道知识库
        R<String> createDataset = baseSearchConsumer.datasetCreate(
                screenEntity.getScreenDesc() ,
                screenEntity.getScreenName()) ;

        screenEntity.setKnowledgeId(createDataset.getData());
        save(screenEntity);

        return screenEntity ;
    }
}
