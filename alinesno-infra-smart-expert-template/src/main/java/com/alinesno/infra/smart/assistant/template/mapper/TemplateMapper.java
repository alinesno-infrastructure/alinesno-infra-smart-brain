package com.alinesno.infra.smart.assistant.template.mapper;

import com.alinesno.infra.common.facade.mapper.repository.IBaseMapper;
import com.alinesno.infra.smart.assistant.template.entity.TemplateEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TemplateMapper extends IBaseMapper<TemplateEntity> {
}
