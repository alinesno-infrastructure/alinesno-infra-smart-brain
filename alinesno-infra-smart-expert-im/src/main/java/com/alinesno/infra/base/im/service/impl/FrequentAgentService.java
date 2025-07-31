package com.alinesno.infra.base.im.service.impl;

import com.alinesno.infra.base.im.mapper.FrequentAgentMapper;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.api.IndustryFrequentRoleDto;
import com.alinesno.infra.smart.assistant.entity.IndustryRoleEntity;
import com.alinesno.infra.smart.assistant.service.IIndustryRoleService;
import com.alinesno.infra.smart.im.entity.ChannelEntity;
import com.alinesno.infra.smart.im.entity.FrequentAgentEntity;
import com.alinesno.infra.smart.im.enums.FrequentTypeEnums;
import com.alinesno.infra.smart.im.service.IChannelService;
import com.alinesno.infra.smart.im.service.IFrequentAgentService;
import com.alinesno.infra.smart.scene.entity.SceneEntity;
import com.alinesno.infra.smart.scene.service.ISceneService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 常用Agent记录服务实现类
 */
@Slf4j
@Service
public class FrequentAgentService extends IBaseServiceImpl<FrequentAgentEntity, FrequentAgentMapper> implements IFrequentAgentService {

    @Autowired
    private IIndustryRoleService roleService;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private IChannelService channelService ;

    @Override
    public boolean updateAgentClickCount(Long accountId, Long agentId, FrequentTypeEnums type) {
        LambdaQueryWrapper<FrequentAgentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FrequentAgentEntity::getAccountId, accountId)
                .eq(FrequentAgentEntity::getAgentId, agentId)
                .eq(FrequentAgentEntity::getType, type);

        FrequentAgentEntity frequentAgent = this.getOne(queryWrapper);
        if (frequentAgent != null) {
            frequentAgent.setClickCount(frequentAgent.getClickCount() + 1);
            frequentAgent.setUpdateTime(new Date());
            return this.updateById(frequentAgent);
        } else {
            frequentAgent = new FrequentAgentEntity();
            frequentAgent.setAccountId(accountId);
            frequentAgent.setAgentId(agentId);
            frequentAgent.setType(type.getType());
            frequentAgent.setClickCount(1);
            frequentAgent.setUpdateTime(new Date());
            return this.save(frequentAgent);
        }
    }

    @Override
    public boolean addFrequentAgent(Long accountId, Long agentId, FrequentTypeEnums type) {
       return updateAgentClickCount(accountId, agentId, type) ;
    }

    @Override
    public boolean deleteFrequentAgent(Long accountId, Long agentId, FrequentTypeEnums type) {
        LambdaQueryWrapper<FrequentAgentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FrequentAgentEntity::getAccountId, accountId)
                .eq(FrequentAgentEntity::getAgentId, agentId)
                .eq(FrequentAgentEntity::getType, type);
        return this.remove(queryWrapper);
    }

    @Override
    public List<IndustryFrequentRoleDto> getFrequentAgentList(Long accountId, int count) {
        // 1. 查询常用列表
        LambdaQueryWrapper<FrequentAgentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FrequentAgentEntity::getAccountId, accountId)
                .orderByDesc(FrequentAgentEntity::getUpdateTime)
                .last("limit " + count);

        List<FrequentAgentEntity> frequentAgents = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(frequentAgents)) {
            return Collections.emptyList();
        }

        // 2. 按类型分组收集ID
        Map<String, List<Long>> typeToIdsMap = new HashMap<>();
        for (FrequentAgentEntity agent : frequentAgents) {
            typeToIdsMap.computeIfAbsent(agent.getType(), k -> new ArrayList<>())
                    .add(agent.getAgentId());
        }

        // 3. 批量查询各类型数据
        Map<Long, IndustryRoleEntity> agentMap = new HashMap<>();
        Map<Long, SceneEntity> sceneMap = new HashMap<>();
        Map<Long, ChannelEntity> channelMap = new HashMap<>();

        if (typeToIdsMap.containsKey(FrequentTypeEnums.AGENT.getType())) {
            agentMap = roleService.listByIds(typeToIdsMap.get(FrequentTypeEnums.AGENT.getType()))
                    .stream().collect(Collectors.toMap(IndustryRoleEntity::getId, Function.identity()));
        }

        if (typeToIdsMap.containsKey(FrequentTypeEnums.SCENE.getType())) {
            sceneMap = sceneService.listByIds(typeToIdsMap.get(FrequentTypeEnums.SCENE.getType()))
                    .stream().collect(Collectors.toMap(SceneEntity::getId, Function.identity()));
        }

        if (typeToIdsMap.containsKey(FrequentTypeEnums.CHANNEL.getType())) {
            channelMap = channelService.listByIds(typeToIdsMap.get(FrequentTypeEnums.CHANNEL.getType()))
                    .stream().collect(Collectors.toMap(ChannelEntity::getId, Function.identity()));
        }

        // 4. 组装结果
        List<IndustryFrequentRoleDto> dtos = new ArrayList<>();
        for (FrequentAgentEntity frequentAgent : frequentAgents) {
            IndustryFrequentRoleDto dto = new IndustryFrequentRoleDto();
            String type = frequentAgent.getType();
            Long agentId = frequentAgent.getAgentId();

            if (Objects.equals(type, FrequentTypeEnums.AGENT.getType())) {
                IndustryRoleEntity entity = agentMap.get(agentId);
                if (entity != null) {
                    fillDtoFromAgent(dto, entity, frequentAgent);
                }
            } else if (Objects.equals(type, FrequentTypeEnums.SCENE.getType())) {
                SceneEntity scene = sceneMap.get(agentId);
                if (scene != null) {
                    fillDtoFromScene(dto, scene, frequentAgent);
                }
            } else if (Objects.equals(type, FrequentTypeEnums.CHANNEL.getType())) {
                ChannelEntity channel = channelMap.get(agentId);
                if (channel != null) {
                    fillDtoFromChannel(dto, channel, frequentAgent);
                }
            }

            if (dto.getId() != null) {
                dtos.add(dto);
            }
        }

        return dtos;
    }

    /**
     * Dto转换成智能体工具
     * @param dto
     * @param entity
     * @param frequentAgent
     */
    private void fillDtoFromAgent(IndustryFrequentRoleDto dto, IndustryRoleEntity entity, FrequentAgentEntity frequentAgent) {
        dto.setId(entity.getId());
        dto.setName(entity.getRoleName());
        dto.setAvator(entity.getRoleAvatar());
        dto.setType(FrequentTypeEnums.AGENT.getType());
        dto.setClickCount(frequentAgent.getClickCount());
    }

    /**
     * Dto转换成场景工具
     * @param dto
     * @param entity
     * @param frequentAgent
     */
    private void fillDtoFromScene(IndustryFrequentRoleDto dto, SceneEntity entity, FrequentAgentEntity frequentAgent) {
        dto.setId(entity.getId());
        dto.setName(entity.getSceneName());
        dto.setAvator(entity.getSceneBanner());
        dto.setSceneType(entity.getSceneType());
        dto.setType(FrequentTypeEnums.SCENE.getType());
        dto.setClickCount(frequentAgent.getClickCount());
    }

    /**
     * Dto转换成频道工具
     * @param dto
     * @param entity
     * @param frequentAgent
     */
    private void fillDtoFromChannel(IndustryFrequentRoleDto dto, ChannelEntity entity, FrequentAgentEntity frequentAgent) {
        dto.setId(entity.getId());
        dto.setName(entity.getChannelName());
        dto.setAvator(entity.getIcon());
        dto.setType(FrequentTypeEnums.CHANNEL.getType());
        dto.setClickCount(frequentAgent.getClickCount());
    }

}