package com.alinesno.infra.smart.assistant.plugin.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.ToolTypeEntity;
import com.alinesno.infra.smart.assistant.plugin.mapper.ToolTypeMapper;
import com.alinesno.infra.smart.assistant.service.IToolTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用构建Service业务层处理
 * 
 * @version 1.0.0
 * @author luoxiaodong
 */
@Slf4j
@Service
public class ToolTypeServiceImpl extends IBaseServiceImpl<ToolTypeEntity, ToolTypeMapper> implements IToolTypeService {

    @Override
    public void initToolType(Long orgId) {
        LambdaQueryWrapper<ToolTypeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ToolTypeEntity::getOrgId, orgId);
        if(count(wrapper) == 0){
            // 创建 ToolTypeEntity 列表
            List<ToolTypeEntity> toolTypeEntities = new ArrayList<>();

            // 添加工具类型信息
            toolTypeEntities.add(createToolTypeEntity(orgId, "fa-solid fa-newspaper", "新闻阅读"));
            toolTypeEntities.add(createToolTypeEntity(orgId, "fa-solid fa-image", "图像"));
            toolTypeEntities.add(createToolTypeEntity(orgId, "fa-solid fa-screwdriver-wrench", "实用工具"));
            toolTypeEntities.add(createToolTypeEntity(orgId, "fa-solid fa-house-chimney", "便利生活"));
            toolTypeEntities.add(createToolTypeEntity(orgId, "fa-solid fa-magnifying-glass", "网页搜索"));
            toolTypeEntities.add(createToolTypeEntity(orgId, "fa-solid fa-graduation-cap", "科学与教育"));
            toolTypeEntities.add(createToolTypeEntity(orgId, "fa-solid fa-users", "社交"));
            toolTypeEntities.add(createToolTypeEntity(orgId, "fa-solid fa-gamepad", "游戏与娱乐"));
            toolTypeEntities.add(createToolTypeEntity(orgId, "fa-solid fa-money-bill-trend-up", "金融与商业"));

            // 批量插入工具类型记录
            saveBatch(toolTypeEntities);
        }
    }

    /**
     * 创建 ToolTypeEntity
     * @param orgId
     * @param icon
     * @param name
     * @return
     */
    private ToolTypeEntity createToolTypeEntity(Long orgId, String icon, String name) {
        ToolTypeEntity entity = new ToolTypeEntity();
        entity.setOrgId(orgId);
        entity.setIcon(icon);
        entity.setName(name);
        return entity;
    }
}