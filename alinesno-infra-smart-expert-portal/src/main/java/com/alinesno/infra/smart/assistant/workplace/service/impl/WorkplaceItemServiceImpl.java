package com.alinesno.infra.smart.assistant.workplace.service.impl;

import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.common.web.adapter.base.consumer.IBaseOrganizationConsumer;
import com.alinesno.infra.common.web.adapter.base.dto.OrganizationDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.assistant.workplace.dto.*;
import com.alinesno.infra.smart.assistant.workplace.entity.WorkplaceItemEntity;
import com.alinesno.infra.smart.assistant.workplace.enums.WorkplaceItemTypeEnums;
import com.alinesno.infra.smart.assistant.workplace.mapper.WorkplaceItemMapper;
import com.alinesno.infra.smart.assistant.workplace.service.IWorkplaceItemService;
import com.alinesno.infra.smart.assistant.workplace.tools.DateFormatterUtils;
import com.alinesno.infra.smart.im.dto.CollectItemDto;
import com.alinesno.infra.smart.im.dto.CollectItemObjectDto;
import com.alinesno.infra.smart.im.entity.ChannelEntity;
import com.alinesno.infra.smart.im.service.IChannelService;
import com.alinesno.infra.smart.scene.dto.SceneInfoDto;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.enums.SceneEnum;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.lang.exception.RpcServiceRuntimeException;
import java.util.*;

@Slf4j
@Service
public class WorkplaceItemServiceImpl extends IBaseServiceImpl<WorkplaceItemEntity, WorkplaceItemMapper> implements IWorkplaceItemService {

    @Autowired
    private IChannelService channelService ;

    @Autowired
    private IIndustryRoleService industryRoleService ;

    @Autowired
    private ISceneService sceneService ;

    @Autowired
    private IBaseOrganizationConsumer organizationConsumer ;

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

    @Override
    public boolean updateWorkplaceItem(WorkplaceItemDto dto) {

        // 删除掉之前的item
        LambdaUpdateWrapper<WorkplaceItemEntity> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(WorkplaceItemEntity::getItemType, dto.getItemType());
        lambdaUpdateWrapper.eq(WorkplaceItemEntity::getWorkplaceId, dto.getWorkplaceId());
        remove(lambdaUpdateWrapper);

        List<String> itemIds = dto.getItemIds();
        if(itemIds == null || itemIds.isEmpty()){
            return false ;
        }

        List<WorkplaceItemEntity> entities = new ArrayList<>();
        for (String itemId : itemIds) {
            WorkplaceItemEntity entity = new WorkplaceItemEntity();
            entity.setWorkplaceId(dto.getWorkplaceId());
            entity.setItemId(Long.parseLong(itemId));
            entity.setItemType(dto.getItemType());

            entities.add(entity);
        }

        saveBatch(entities) ;

        return true ;
    }

    @Override
    public List<CollectItemObjectDto> getWorkplaceItemByType(Long workplaceId, String type) {
        List<CollectItemObjectDto> collectItemObjectDtos = new ArrayList<>();

        LambdaQueryWrapper<WorkplaceItemEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WorkplaceItemEntity::getWorkplaceId, workplaceId);

        if(StringUtils.hasText(type)){
            lambdaQueryWrapper.eq(WorkplaceItemEntity::getItemType, type);
        }

        List<WorkplaceItemEntity> list = list(lambdaQueryWrapper);

        if(list.isEmpty()){
            return collectItemObjectDtos ;
        }

        try {
            // 预收集所有需要的orgId
            Set<Long> orgIds = new HashSet<>();

            List<Long> agentItemIds = filterAndMapItemIds(list, WorkplaceItemTypeEnums.AGENT.getCode());
            if(!agentItemIds.isEmpty()) {
                List<IndustryRoleEntity> agentsList = industryRoleService.listByIds(agentItemIds);
                agentsList.forEach(agent -> orgIds.add(agent.getOrgId()));
            }

            List<Long> channelItemIds = filterAndMapItemIds(list, WorkplaceItemTypeEnums.CHANNEL.getCode());
            if(!channelItemIds.isEmpty()) {
                List<ChannelEntity> channelList = channelService.listByIds(channelItemIds);
                channelList.forEach(channel -> orgIds.add(channel.getOrgId()));
            }

            List<Long> sceneItemIds = filterAndMapItemIds(list, WorkplaceItemTypeEnums.SCENE.getCode());
            if(!sceneItemIds.isEmpty()) {
                List<SceneEntity> sceneList = sceneService.listByIds(sceneItemIds);
                sceneList.forEach(scene -> orgIds.add(scene.getOrgId()));
            }

            // 批量获取所有组织信息
            Map<Long, OrganizationDto> orgMap = new HashMap<>();
            if(!orgIds.isEmpty()) {
                List<OrganizationDto> orgList = organizationConsumer.findOrgByIds(new ArrayList<>(orgIds)).getData();
                orgList.forEach(org -> orgMap.put(org.getId(), org));
            }

            // 获取智能体信息
            List<Long> agentIds = filterAndMapItemIds(list, WorkplaceItemTypeEnums.AGENT.getCode());
            List<IndustryRoleEntity> agentsList = agentIds.isEmpty() ? Collections.emptyList() : industryRoleService.listByIds(agentIds);

            List<CollectItemObjectDto> agentsOrgList = new ArrayList<>();
            for (IndustryRoleEntity industryRoleEntity : agentsList) {
                CollectItemObjectDto dto1 = new CollectItemObjectDto();
                BeanUtils.copyProperties(industryRoleEntity, dto1);
                dto1.setOrgName(orgMap.getOrDefault(industryRoleEntity.getOrgId(), new OrganizationDto()).getOrgName());
                dto1.setType(WorkplaceItemTypeEnums.AGENT.getCode());

                dto1.setId(industryRoleEntity.getId());
                dto1.setItemId(industryRoleEntity.getId());
                dto1.setTitle(industryRoleEntity.getRoleName());
                dto1.setAvatar(industryRoleEntity.getRoleAvatar());
                dto1.setDescription(industryRoleEntity.getResponsibilities());
                dto1.setEditInfo(orgMap.getOrDefault(industryRoleEntity.getOrgId(), new OrganizationDto()).getOrgName());
                dto1.setEditTime(DateFormatterUtils.getRelativeTime(industryRoleEntity.getAddTime()));

                agentsOrgList.add(dto1);
            }
            collectItemObjectDtos.addAll(agentsOrgList);

            // 获取频道信息
            List<Long> channelIds = filterAndMapItemIds(list, WorkplaceItemTypeEnums.CHANNEL.getCode());
            List<ChannelEntity> channelList = channelIds.isEmpty() ? Collections.emptyList() : channelService.listByIds(channelIds);

            List<CollectItemObjectDto> channelOrgList = new ArrayList<>();
            for (ChannelEntity channelEntity : channelList) {
                CollectItemObjectDto dto1 = new CollectItemObjectDto();
                BeanUtils.copyProperties(channelEntity, dto1);
                dto1.setType(WorkplaceItemTypeEnums.CHANNEL.getCode());

                dto1.setId(channelEntity.getId());
                dto1.setItemId(channelEntity.getId());
                dto1.setOrgName(orgMap.getOrDefault(channelEntity.getOrgId(), new OrganizationDto()).getOrgName());
                dto1.setTitle(channelEntity.getChannelName());
                dto1.setAvatar(channelEntity.getIcon());
                dto1.setDescription(channelEntity.getChannelDesc());
                dto1.setEditInfo(orgMap.getOrDefault(channelEntity.getOrgId(), new OrganizationDto()).getOrgName());
                dto1.setEditTime(DateFormatterUtils.getRelativeTime(channelEntity.getAddTime()));

                channelOrgList.add(dto1);
            }
            collectItemObjectDtos.addAll(channelOrgList);

            // 获取场景信息
            List<Long> sceneIds = filterAndMapItemIds(list, WorkplaceItemTypeEnums.SCENE.getCode());
            List<SceneEntity> sceneList = sceneIds.isEmpty() ? Collections.emptyList() : sceneService.listByIds(sceneIds);

            List<CollectItemObjectDto> sceneOrgList = new ArrayList<>();
            for (SceneEntity sceneEntity : sceneList) {
                CollectItemObjectDto dto1 = new CollectItemObjectDto();
                BeanUtils.copyProperties(sceneEntity, dto1);
                dto1.setType(WorkplaceItemTypeEnums.SCENE.getCode());

                dto1.setOrgName(orgMap.getOrDefault(sceneEntity.getOrgId(), new OrganizationDto()).getOrgName());

                dto1.setId(sceneEntity.getId());
                dto1.setItemId(sceneEntity.getId());
                dto1.setTitle(sceneEntity.getSceneName());
                dto1.setAvatar(sceneEntity.getSceneBanner());
                dto1.setSceneType(sceneEntity.getSceneType());

                SceneInfoDto sceneInfo = SceneEnum.getSceneInfoByCode(sceneEntity.getSceneType()) ;
                if(sceneEntity.getSceneType() != null && sceneInfo != null){
                    dto1.setSceneTypeName(sceneInfo.getSceneName());
                    dto1.setSceneIcon(sceneInfo.getIcon());
                }
                dto1.setDescription(sceneEntity.getSceneDesc());
                dto1.setEditInfo(orgMap.getOrDefault(sceneEntity.getOrgId(), new OrganizationDto()).getOrgName());
                dto1.setEditTime(DateFormatterUtils.getRelativeTime(sceneEntity.getAddTime()));

                sceneOrgList.add(dto1);
            }
            collectItemObjectDtos.addAll(sceneOrgList);
        } catch (Exception e) {
            log.error("Error occurred while processing item lists", e);
            throw new RpcServiceRuntimeException("查询列表数据异常");
        }

        return collectItemObjectDtos;
    }

    @Override
    public void addCollectItem(CollectItemDto dto, Long workplaceId) {

        String type = dto.getType();

        LambdaQueryWrapper<WorkplaceItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WorkplaceItemEntity::getWorkplaceId, workplaceId)
                .eq(WorkplaceItemEntity::getItemId, dto.getItemId())
                .eq(WorkplaceItemEntity::getItemType, type);

        // 如果已存在，则不处理
        if (count(queryWrapper) > 0) {
            throw new RpcServiceRuntimeException("收藏已存在，如果 ");
        }

        WorkplaceItemEntity entity = new WorkplaceItemEntity();
        entity.setWorkplaceId(workplaceId);
        entity.setItemId(dto.getItemId());
        entity.setItemType(type);

        save(entity);
    }

    @Override
    public void removeCollectItem(CollectItemDto dto, Long workplaceId) {

        LambdaUpdateWrapper<WorkplaceItemEntity> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(WorkplaceItemEntity::getWorkplaceId, workplaceId)
                .eq(WorkplaceItemEntity::getItemId, dto.getItemId())
                .eq(WorkplaceItemEntity::getItemType, dto.getType());

        remove(queryWrapper);
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
