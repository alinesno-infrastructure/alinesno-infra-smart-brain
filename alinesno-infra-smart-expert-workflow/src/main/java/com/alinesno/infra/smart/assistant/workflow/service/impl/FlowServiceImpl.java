package com.alinesno.infra.smart.assistant.workflow.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.workflow.dto.FlowDto;
import com.alinesno.infra.smart.assistant.workflow.dto.FlowNodeDto;
import com.alinesno.infra.smart.assistant.workflow.dto.WorkflowRequestDto;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowEntity;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowNodeEntity;
import com.alinesno.infra.smart.assistant.workflow.enums.PublishStatus;
import com.alinesno.infra.smart.assistant.workflow.mapper.FlowMapper;
import com.alinesno.infra.smart.assistant.workflow.parse.LogicFlowParser;
import com.alinesno.infra.smart.assistant.workflow.parse.NodePrinter;
import com.alinesno.infra.smart.assistant.workflow.service.IFlowNodeService;
import com.alinesno.infra.smart.assistant.workflow.service.IFlowService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 工作流服务接口的实现类，负责处理工作流基础信息和元数据信息的具体业务逻辑。
 * 继承自 IBaseServiceImpl 类，借助该类提供的通用方法，可完成对工作流数据的基本操作。
 *
 * @author luoxiaodong
 * @version 1.0.0
 */
@Slf4j
@Service
public class FlowServiceImpl extends IBaseServiceImpl<FlowEntity, FlowMapper> implements IFlowService {

    @Autowired
    private IFlowNodeService flowNodeService;

    /**
     * 每次保存之后，版本号加1
     * @param roleId
     * @param flowDto
     */
    @Override
    @Transactional
    public void saveRoleFlow(Long roleId, WorkflowRequestDto flowDto) {
        // 查询角色的流程
        LambdaQueryWrapper<FlowEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowEntity::getRoleId, roleId);
        FlowEntity flowEntity = getOne(queryWrapper);

        if (flowEntity == null) {
            flowEntity = new FlowEntity();
            flowEntity.setRoleId(roleId);
            flowEntity.setFlowVersion(0);
            flowEntity.setLockVersion(0);
            flowEntity.setPublishStatus(PublishStatus.UNPUBLISHED.getCode());
        }

        flowEntity.setFlowGraphJson(JSONObject.toJSONString(flowDto)); // 假设 WorkflowRequestDto 有 toJson 方法

        flowEntity.setFlowVersion(flowEntity.getFlowVersion() + 1);
        saveOrUpdate(flowEntity);

        // 保存节点信息
        Map<String, Object> data = new HashMap<>();
        data.put("nodes", flowDto.getNodes());
        data.put("edges", flowDto.getEdges());

        List<FlowNodeDto> nodes = LogicFlowParser.parseNodes(data);

        log.debug("链表打印.");
        NodePrinter.printNodesAsLinkedList(nodes);

        // 转换成Entity保存到数据库中
        Long flowId = flowEntity.getId();

        List<FlowNodeEntity> nodeEntities = nodes.stream()
                .map(nodeDto -> {
                    FlowNodeEntity nodeEntity = nodeDto.toEntity();
                    nodeEntity.setFlowId(flowId); // 设置所属工作流的 ID
                    return nodeEntity;
                })
                .toList();

        // 先删除该工作流下的所有旧节点信息
        LambdaQueryWrapper<FlowNodeEntity> deleteWrapper = Wrappers.lambdaQuery();
        deleteWrapper.eq(FlowNodeEntity::getFlowId, flowId);
        flowNodeService.remove(deleteWrapper);

        // 保存节点信息
        flowNodeService.saveBatch(nodeEntities);
    }

    /**
     * 发布工作流
     * @param roleId
     */
    @Override
    @Transactional
    public void publishFlow(Long roleId) {
        // 查询角色的流程
        LambdaQueryWrapper<FlowEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowEntity::getRoleId, roleId);
        FlowEntity flowEntity = getOne(queryWrapper);

        if (Objects.nonNull(flowEntity)) {
            // 版本号加1
            flowEntity.setFlowVersion(flowEntity.getFlowVersion() + 1);
            flowEntity.setPublishStatus(PublishStatus.PUBLISHED.getCode());

            update(flowEntity);
        }
    }

    /**
     * 获取指定角色最新版本的已发布流程
     * @param roleId 角色ID
     * @return 最新版本的已发布流程实体，如果不存在则返回 null
     */
    @Override
    public FlowEntity getLatestPublishedFlowByRoleId(Long roleId) {
        LambdaQueryWrapper<FlowEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowEntity::getRoleId, roleId)
                .eq(FlowEntity::getPublishStatus, PublishStatus.PUBLISHED.getCode())
                .orderByDesc(FlowEntity::getFlowVersion)
                .last("LIMIT 1");
        return getOne(queryWrapper);
    }

    /**
     * 获取指定角色的未发布流程
     * @param roleId 角色ID
     * @return 未发布流程实体，如果不存在则返回 null
     */
    @Override
    public FlowEntity getUnpublishedFlowByRoleId(Long roleId) {
        LambdaQueryWrapper<FlowEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowEntity::getRoleId, roleId)
                .eq(FlowEntity::getPublishStatus, PublishStatus.UNPUBLISHED.getCode());
        return getOne(queryWrapper);
    }

    /**
     * 获取指定角色最新版本的流程
     * @param roleId 角色ID
     * @return 最新版本的已发布流程实体，如果不存在则返回 null
     */
    @Override
    public FlowDto getLatestFlowByRoleId(Long roleId) {
        LambdaQueryWrapper<FlowEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(FlowEntity::getRoleId, roleId)
                .orderByDesc(FlowEntity::getFlowVersion)
                .last("LIMIT 1");
        FlowEntity flowEntity = getOne(queryWrapper);

        if(flowEntity == null){
            return FlowDto.empty();
        }

        WorkflowRequestDto dto = JSONObject.parseObject(flowEntity.getFlowGraphJson() , WorkflowRequestDto.class) ;

        FlowDto flowDto = new FlowDto() ;
        BeanUtils.copyProperties(flowEntity , flowDto);
        flowDto.setFlowGraphJson(dto);

        List<FlowNodeDto> flowNodeDtos = getFlowNodeDtosByFlowId(flowEntity.getId()) ;
        flowDto.setNodes(flowNodeDtos);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("nodes", dto.getNodes());
        dataMap.put("edges", dto.getEdges());
        List<FlowNodeDto> parseNodes = LogicFlowParser.parseNodes(dataMap) ;

        log.debug("链表打印.");
        NodePrinter.printNodesAsLinkedList(parseNodes);

        return flowDto ;
    }

    /**
     * 查询指定工作流 ID 下的所有节点实体，并将其转换为 DTO 列表
     * @param flowId 工作流 ID
     * @return 转换后的 FlowNodeDto 列表
     */
    public List<FlowNodeDto> getFlowNodeDtosByFlowId(Long flowId) {
        // 查询指定工作流 ID 下的所有节点实体
        LambdaQueryWrapper<FlowNodeEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(FlowNodeEntity::getFlowId, flowId);
        List<FlowNodeEntity> nodeEntities = flowNodeService.list(queryWrapper);

        // 将实体列表转换为 DTO 列表
        return nodeEntities.stream()
                .map(FlowNodeDto::fromEntity)
                .toList();
    }

}