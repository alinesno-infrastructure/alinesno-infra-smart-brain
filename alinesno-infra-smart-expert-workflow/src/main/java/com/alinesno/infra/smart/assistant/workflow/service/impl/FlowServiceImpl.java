package com.alinesno.infra.smart.assistant.workflow.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.alinesno.infra.common.core.service.impl.IBaseServiceImpl;
import com.alinesno.infra.smart.assistant.workflow.dto.FlowDto;
import com.alinesno.infra.smart.assistant.workflow.dto.FlowNodeDto;
import com.alinesno.infra.smart.assistant.workflow.dto.WorkflowRequestDto;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowEntity;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowExecutionEntity;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowNodeEntity;
import com.alinesno.infra.smart.assistant.workflow.entity.FlowNodeExecutionEntity;
import com.alinesno.infra.smart.assistant.workflow.enums.FlowExecutionStatus;
import com.alinesno.infra.smart.assistant.workflow.enums.PublishStatus;
import com.alinesno.infra.smart.assistant.workflow.mapper.FlowMapper;
import com.alinesno.infra.smart.assistant.workflow.nodes.AbstractFlowNode;
import com.alinesno.infra.smart.assistant.workflow.parse.LogicFlowParser;
import com.alinesno.infra.smart.assistant.workflow.parse.NodePrinter;
import com.alinesno.infra.smart.assistant.workflow.service.IFlowExecutionService;
import com.alinesno.infra.smart.assistant.workflow.service.IFlowNodeExecutionService;
import com.alinesno.infra.smart.assistant.workflow.service.IFlowNodeService;
import com.alinesno.infra.smart.assistant.workflow.service.IFlowService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.alinesno.infra.smart.assistant.workflow.constants.FlowConst.FLOW_STEP_NODE;

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
    private IFlowNodeService flowNodeService;  // 流程定义

    @Autowired
    private IFlowExecutionService flowExecutionService ;  // 流程实例

    @Autowired
    private IFlowNodeExecutionService flowNodeExecutionService ; // 流程节点实例

    /**
     * 运行指定角色的流程 <br/>
     * 1. 获取到角色当前最新的已经发布的流程 <br/>
     * 2. 建立流程实例并启动流程实例，流程实例在运行中 <br/>
     * 3. 获取到流程当前的所有节点信息 <br/>
     * 4. 按链表顺序遍历节点信息，依次执行节点的逻辑 <br/>
     * 5. 每个流程节点起一个节点实例，节点实例运行中 <br/>
     * 6. 所有节点实例执行完毕后，流程实例结束 <br/>
     * @param roleId
     */
    @Override
    public void runRoleFlow(Long roleId) {

        FlowEntity flowEntity = getLatestPublishedFlowByRoleId(roleId);

        FlowExecutionEntity flowExecutionEntity = new FlowExecutionEntity();
        flowExecutionEntity.setFlowId(flowEntity.getId()) ;
        flowExecutionEntity.setRoleId(roleId) ;

        flowExecutionEntity.setExecutionStatus(FlowExecutionStatus.EXECUTING.getCode());
        flowExecutionEntity.setExecuteTime(new Date());

        flowExecutionService.save(flowExecutionEntity);

        // 执行节点实例
        WorkflowRequestDto dto = JSONObject.parseObject(flowEntity.getFlowGraphJson() , WorkflowRequestDto.class) ;

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("nodes", dto.getNodes());
        dataMap.put("edges", dto.getEdges());
        List<FlowNodeDto> parseNodes = LogicFlowParser.parseNodes(dataMap) ;

        // 跟数据库中的节点信息进行匹配，构建节点之间的关系
        List<FlowNodeEntity> flowNodes = flowNodeService.list(Wrappers.lambdaQuery(FlowNodeEntity.class).eq(FlowNodeEntity::getFlowId , flowEntity.getId())) ;
        for (FlowNodeDto flowNode : parseNodes) {
            FlowNodeEntity flowNodeEntity = flowNodes.stream().filter(e -> e.getStepId().equals(flowNode.getId())).findFirst().orElse(null);
            assert flowNodeEntity != null;
            flowNode.setNodeId(flowNodeEntity.getId());
        }

        Map<String , Object> output = new HashMap<>();  // 流程全局参数

        log.debug("执行任务.");
        executeNodesAsTopologicalSort(parseNodes , flowExecutionEntity , output) ;
        log.debug("执行完毕.");

        flowExecutionEntity.setExecutionStatus(FlowExecutionStatus.COMPLETED.getCode());
        flowExecutionEntity.setFinishTime(new Date());
        flowExecutionService.update(flowExecutionEntity);
    }

    /**
     * 执行节点实例
     */
    private void executeFlowNode(FlowNodeDto node,
                                 FlowExecutionEntity flowExecutionEntity,
                                 Map<String, Object> output ,
                                 int count ,
                                 int level){

        // 流程节点实例
        FlowNodeExecutionEntity flowNodeExecutionEntity = new FlowNodeExecutionEntity();

        flowNodeExecutionEntity.setFlowExecutionId(flowExecutionEntity.getId()) ;
        flowNodeExecutionEntity.setExecutionStatus(FlowExecutionStatus.EXECUTING.getCode());
        flowNodeExecutionEntity.setExecuteTime(new Date());

        flowNodeExecutionEntity.setNodeId(node.getNodeId()) ;  // 与数据库存储节点的ID
        flowNodeExecutionEntity.setStepId(node.getId());  // 与前端图形界面关联的ID

        flowNodeExecutionEntity.setExecutionOrder(count);
        flowNodeExecutionEntity.setExecutionDepth(level);

        flowNodeExecutionEntity.setNodeType(node.getType());
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            flowNodeExecutionEntity.setProperties(objectMapper.writeValueAsString(node.getProperties()));
        } catch (JsonProcessingException e) {
            log.error("Error converting properties to JSON: " + e.getMessage());
            flowNodeExecutionEntity.setProperties(null);
        }

        flowNodeExecutionService.save(flowNodeExecutionEntity);

        // 执行节点_开始
        AbstractFlowNode abstractFlowNode = SpringUtil.getBean(FLOW_STEP_NODE + node.getType());
        abstractFlowNode.executeNode(node , flowExecutionEntity , flowNodeExecutionEntity , output) ;
        // 执行节点_结束

        flowNodeExecutionEntity.setExecutionStatus(FlowExecutionStatus.COMPLETED.getCode());
        flowNodeExecutionEntity.setFinishTime(new Date());
        flowNodeExecutionService.update(flowNodeExecutionEntity);

    }

    /**
     * 使用拓扑排序执行节点
     *
     * @param nodes               包含 FlowNodeDto 节点的列表
     * @param flowExecutionEntity
     * @param output
     */
    public void executeNodesAsTopologicalSort(List<FlowNodeDto> nodes, FlowExecutionEntity flowExecutionEntity, Map<String, Object> output) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        // 计算每个节点的入度
        Map<FlowNodeDto, Integer> inDegree = new HashMap<>();
        for (FlowNodeDto node : nodes) {
            inDegree.put(node, node.getPrevNodes().size());
        }

        // 初始化队列，将入度为 0 的节点加入队列
        Queue<FlowNodeDto> queue = new LinkedList<>();
        for (FlowNodeDto node : nodes) {
            if (inDegree.get(node) == 0) {
                queue.offer(node);
            }
        }

        int count = 1;
        int level = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                FlowNodeDto currentNode = queue.poll();
                log.debug("node--{}--level--{}--->>>>>>>>start", count, level);
                log.debug("Processing node stepName = {} , type = {} , id = {}", Objects.requireNonNull(currentNode).getStepName(), currentNode.getType(), currentNode.getId());
                log.debug("node--{}--level--{}--->>>>>>>>end", count, level);

                executeFlowNode(currentNode, flowExecutionEntity, output, count, level);
                count++;

                // 减少后续节点的入度
                for (FlowNodeDto nextNode : currentNode.getNextNodes()) {
                    int newInDegree = inDegree.get(nextNode) - 1;
                    inDegree.put(nextNode, newInDegree);
                    if (newInDegree == 0) {
                        queue.offer(nextNode);
                    }
                }
            }
            level++;
        }
    }

    /**
     * 从类型为 "start" 的节点开始，按层级顺序执行 FlowNodeDto 节点及其后续节点信息
     *
     * @param nodes               包含 FlowNodeDto 节点的列表
     * @param flowExecutionEntity
     * @param output
     */
    public void executeNodesAsLinkedList(List<FlowNodeDto> nodes, FlowExecutionEntity flowExecutionEntity, Map<String, Object> output) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        // 找到类型为 "start" 的起始节点
        FlowNodeDto startNode = null;
        for (FlowNodeDto node : nodes) {
            if ("start".equals(node.getType())) {
                startNode = node;
                break;
            }
        }

        int count = 1;
        int level = 1;

        if (startNode != null) {
            Queue<FlowNodeDto> queue = new LinkedList<>();
            queue.offer(startNode);
            Set<String> visited = new HashSet<>();
            visited.add(startNode.getId());

            while (!queue.isEmpty()) {
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    FlowNodeDto currentNode = queue.poll();
                    log.debug("node--{}--level--{}--->>>>>>>>start", count, level);
                    log.debug("Processing node stepName = {} , type = {} , id = {}", Objects.requireNonNull(currentNode).getStepName(), currentNode.getType(), currentNode.getId());
                    log.debug("node--{}--level--{}--->>>>>>>>end", count, level);

                    executeFlowNode(currentNode, flowExecutionEntity, output, count, level);
                    count++;

                    for (FlowNodeDto nextNode : currentNode.getNextNodes()) {
                        if (!visited.contains(nextNode.getId())) {
                            queue.offer(nextNode);
                            visited.add(nextNode.getId());
                        }
                    }
                }
                level++;
            }
        }
    }

//    /**
//     * 从类型为 "start" 的节点开始，按链表形式打印 FlowNodeDto 节点及其后续节点信息，并输出层级
//     *
//     * @param nodes               包含 FlowNodeDto 节点的列表
//     * @param flowExecutionEntity
//     * @param output
//     */
//    public void executeNodesAsLinkedList(List<FlowNodeDto> nodes, FlowExecutionEntity flowExecutionEntity, Map<String, Object> output) {
//        if (nodes == null || nodes.isEmpty()) {
//            return;
//        }
//
//        // 找到类型为 "start" 的起始节点
//        FlowNodeDto startNode = null;
//        for (FlowNodeDto node : nodes) {
//            if ("start".equals(node.getType())) {
//                startNode = node;
//                break;
//            }
//        }
//
//        int count = 1 ;
//        int level = 1 ;
//
//        if (startNode != null) {
//            Set<String> visited = new HashSet<>();
//            dfsExecuteNodes(startNode, flowExecutionEntity, output , count , level, visited);
//        }
//    }

    /**
     * 使用深度优先搜索执行节点及其后续节点信息，并输出层级
     *
     * @param currentNode         当前节点
     * @param flowExecutionEntity
     * @param output
     * @param count               节点执行顺序
     * @param level               节点层级
     * @param visited             已访问节点集合
     */
    private void dfsExecuteNodes(FlowNodeDto currentNode, FlowExecutionEntity flowExecutionEntity, Map<String, Object> output, int count, int level, Set<String> visited) {
        if (visited.contains(currentNode.getId())) {
            log.debug("Node {} already visited, skipping.", currentNode.getId());
            return;
        }
        visited.add(currentNode.getId());

        log.debug("node--{}--level--{}--->>>>>>>>start", count, level);
        log.debug("Processing node stepName = {} , type = {} , id = {}", currentNode.getStepName(), currentNode.getType(), currentNode.getId());
        log.debug("node--{}--level--{}--->>>>>>>>end", count, level);

        executeFlowNode(currentNode, flowExecutionEntity, output, count, level);

        for (FlowNodeDto nextNode : currentNode.getNextNodes()) {
            dfsExecuteNodes(nextNode, flowExecutionEntity, output, count + 1, level + 1, visited);
        }
    }

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
            flowEntity.setLockVersion(0);
            flowEntity.setPublishStatus(PublishStatus.UNPUBLISHED.getCode());
            flowEntity.setUpdateTime(new Date());
        }

        flowEntity.setFlowGraphJson(JSONObject.toJSONString(flowDto)); // 假设 WorkflowRequestDto 有 toJson 方法

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
     * @param flowId
     */
    @Override
    @Transactional
    public void publishFlow(Long flowId) {
        // 查询角色的流程
        LambdaQueryWrapper<FlowEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlowEntity::getId, flowId);
        FlowEntity flowEntity = getOne(queryWrapper);

        if (Objects.nonNull(flowEntity)) {
            // 版本号加1
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