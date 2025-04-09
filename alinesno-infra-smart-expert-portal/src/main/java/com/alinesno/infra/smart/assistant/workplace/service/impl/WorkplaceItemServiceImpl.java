package com.alinesno.infra.smart.assistant.workplace.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.workplace.dto.WorkplaceResponseDto;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceItemEntity;
import com.alinesno.infra.smart.assistant.workplace.enums.WorkplaceItemTypeEnums;
import com.alinesno.infra.smart.assistant.workplace.mapper.WorkplaceItemMapper;
import com.alinesno.infra.smart.assistant.workplace.service.IWorkplaceItemService;
import com.alinesno.infra.smart.im.entity.ChannelEntity;
import com.alinesno.infra.smart.im.service.IChannelService;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class WorkplaceItemServiceImpl extends IBaseServiceImpl<WorkplaceItemEntity, WorkplaceItemMapper> implements IWorkplaceItemService {

    @Autowired
    private IChannelService channelService ;

    @Autowired
    private IIndustryRoleService industryRoleService ;

    @Autowired
    private ISceneService sceneService ;

    @Override
    public WorkplaceResponseDto getWorkplaceItem(Long id) {

        WorkplaceResponseDto dto =  new WorkplaceResponseDto();

        LambdaQueryWrapper<WorkplaceItemEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WorkplaceItemEntity::getWorkplaceId, id);

        List<WorkplaceItemEntity> list = list(lambdaQueryWrapper);
        if(list.isEmpty()){
            return dto ;
        }

        // 主逻辑
        try {
            // 使用公共方法提取 agentIds、channelIds 和 sceneIds
            List<Long> agentIds = filterAndMapItemIds(list, WorkplaceItemTypeEnums.AGENT.getCode());
            List<Long> channelIds = filterAndMapItemIds(list, WorkplaceItemTypeEnums.CHANNEL.getCode());
            List<Long> sceneIds = filterAndMapItemIds(list, WorkplaceItemTypeEnums.SCENE.getCode());

            // 调用服务方法前检查 ID 列表是否为空
            List<IndustryRoleEntity> agentsList = agentIds.isEmpty() ? Collections.emptyList() : industryRoleService.listByIds(agentIds);
            List<ChannelEntity> channelList = channelIds.isEmpty() ? Collections.emptyList() : channelService.listByIds(channelIds);
            List<SceneEntity> sceneList = sceneIds.isEmpty() ? Collections.emptyList() : sceneService.listByIds(sceneIds);

            // 设置 DTO 属性
            dto.setAgentsList(agentsList);
            dto.setChannelsList(channelList);
            dto.setScenesList(sceneList);
        } catch (Exception e) {
            // 捕获异常并记录日志（具体日志实现可根据项目需求调整）
            log.error("Error occurred while processing item lists" , e);
            throw new RpcServiceRuntimeException("查询列表数据异常"); // 根据需求决定是否重新抛出异常
        }

        return dto ;
    }

    // 提取公共方法，用于根据 itemType 筛选并映射 itemId
    private List<Long> filterAndMapItemIds(List<WorkplaceItemEntity> list, String itemTypeCode) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList(); // 显式返回空列表
        }
        return list.stream()
                .filter(item -> itemTypeCode.equals(item.getItemType()))
                .map(WorkplaceItemEntity::getItemId)
                .toList();
    }

}
